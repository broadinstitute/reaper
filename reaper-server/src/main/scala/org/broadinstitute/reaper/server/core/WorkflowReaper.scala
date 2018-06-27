package org.broadinstitute.reaper.server.core

import akka.NotUsed
import akka.stream.scaladsl.Source
import cromwell.api.model.{WorkflowId, WorkflowMetadata}
import org.broadinstitute.clio.transfer.model.ClioIndex
import org.broadinstitute.reaper.server.db.MetricsExtractor
import org.broadinstitute.reaper.server.edge.WorkflowTracker

import scala.concurrent.{ExecutionContext, Future}

abstract class WorkflowReaper[CI <: ClioIndex](protected val clioIndex: CI)(
  implicit ec: ExecutionContext
) {
  import clioIndex.{KeyType => K, MetadataType => M}

  def processWorkflows(
    tracker: WorkflowTracker,
    metricsProcessor: MetricsExtractor[K, M]
  ): Source[Unit, NotUsed] = {
    // TODO configurable parallelism etc.
    tracker
      .pullWorkflowIds()
      .flatMapMerge(1, processWorkflow(metricsProcessor))
      // TODO divert errors to the tracker's handler method.
      .foldAsync(())((_, id) => tracker.markWorkflowReaped(id))
  }

  private def processWorkflow(
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
      .map(_ => id)
  }

  // TODO some of these will shake out to be Sources, not Futures.

  protected def getWorkflowMetadata(id: WorkflowId): Future[WorkflowMetadata]

  protected def extractClioInfo(metadata: WorkflowMetadata): (K, M)

  protected def getExistingClioMetadata(key: K): Future[Option[M]]

  // TODO we should make this a standard field on Clio metadata.
  protected def getWorkflowId(metadata: M): WorkflowId

  // TODO improve the Clio MoveExecutor API to make it easier to use generically here.
  protected def moveFiles(key: K, metadata: M): Future[Unit]
}
