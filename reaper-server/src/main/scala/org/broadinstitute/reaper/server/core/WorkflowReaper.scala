package org.broadinstitute.reaper.server.core

import akka.NotUsed
import akka.stream.scaladsl.Source
import cromwell.api.model.{WorkflowId, WorkflowMetadata}
import org.broadinstitute.clio.transfer.model.ClioIndex
import org.broadinstitute.reaper.server.db.MetricsExtractor
import org.broadinstitute.reaper.server.edge.WorkflowTracker

import scala.concurrent.{ExecutionContext, Future}

/**
  * Main controller for post-processing cloud workflows.
  *
  * Knows how to process a single type of Cromwell workflow into
  * records in a single Clio index.
  *
  * TODO should probably work on multiple indexes to handle i.e. SSWF generating both cram and gvcf.
  */
abstract class WorkflowReaper[CI <: ClioIndex](
  protected val clioIndex: CI
)(implicit ec: ExecutionContext) {
  import clioIndex.{KeyType => K, MetadataType => M}

  /**
    * Build a stream which will pull Cromwell workflows and process their outputs.
    *
    * Might be an infinite stream, depending on the stream of workflow IDs.
    *
    * @param tracker component responsible for providing workflow IDs to process
    *                and marking processed workflows as "done" / "errored"
    * @param metricsProcessor component responsible for extracting and persisting
    *                         metrics values from workflow outputs
    */
  def reapWorkflows(
    tracker: WorkflowTracker,
    metricsProcessor: MetricsExtractor[K, M]
  ): Source[Unit, NotUsed] = {
    // TODO configurable parallelism etc.
    tracker
      .pullWorkflowIds()
      .flatMapMerge(1, reapWorkflow(metricsProcessor))
      // TODO divert errors to the tracker's handler method.
      .foldAsync(())((_, id) => tracker.markWorkflowReaped(id))
  }

  /** Build a stream which will process the outputs of a single Cromwell workflow. */
  private def reapWorkflow(
    metricsProcessor: MetricsExtractor[K, M]
  )(id: WorkflowId): Source[WorkflowId, NotUsed] = {
    Source
      .fromFuture(getWorkflowMetadata(id))
      .map(extractClioInfo)
      .mapAsync(1) {
        case (key, metadata) =>
          // TODO these should probably be in separate stream stages,
          // not a mega mapAsync.
          getExistingClioMetadata(key).map {
            case None => metadata
            case Some(existing) =>
              if (getWorkflowId(existing) != getWorkflowId(metadata)) {
                throw new RuntimeException(
                  "Don't let us overwrite non-deleted records in Clio"
                )
              } else {
                existing
              }
          }.flatMap { currentMetadata =>
            metricsProcessor.extractMetrics(key, currentMetadata).flatMap { _ =>
              moveFiles(key, currentMetadata)
            }
          }
      }
      .mapAsync(1)(_ => cleanExecutionDirectory(id))
      .map(_ => id)
  }

  // TODO some of these will shake out to be Sources, not Futures.

  /** Get the metadata for a Cromwell workflow. */
  protected def getWorkflowMetadata(id: WorkflowId): Future[WorkflowMetadata]

  /** Extract a Clio key-metadata pair from a blob of Cromwell metadata. */
  protected def extractClioInfo(metadata: WorkflowMetadata): (K, M)

  /** Check Clio for existing metadata associated with a key. */
  protected def getExistingClioMetadata(key: K): Future[Option[M]]

  // TODO we should make this a standard field on Clio metadata.
  /** Extract the Cromwell workflow ID stored within a blob of Clio metadata. */
  protected def getWorkflowId(metadata: M): Option[WorkflowId]

  // TODO improve the Clio MoveExecutor API to make it easier to use generically here.
  /**
    * Move Cromwell outputs to long-term storage.
    *
    * Moves via Clio, for tracking.
    */
  protected def moveFiles(key: K, metadata: M): Future[Unit]

  /** Delete the execution directory of a Cromwell workflow. */
  protected def cleanExecutionDirectory(id: WorkflowId): Future[Unit]
}
