package org.broadinstitute.reaper.server.edge

import akka.NotUsed
import akka.stream.scaladsl.Source
import cromwell.api.model.WorkflowId

import scala.concurrent.Future

/**
  * Component responsible for:
  * <ol>
  *   <li>Finding workflows in Cromwell ready for reaping
  *   <li>Marking successfully-processed workflows as reaped to ensure they aren't pulled twice
  *   <li>Marking workflows which fail to be reaped so that persistent problems can be detected
  * </ol>
  */
trait WorkflowTracker {

  /**
    * Generate a stream of workflow IDs which are ready for reaping.
    *
    * Might be an infinite stream.
    */
  def pullWorkflowIds(): Source[WorkflowId, NotUsed]

  /**
    * Mark that a workflow has been successfully reaped, and shouldn't be processed again.
    */
  def markWorkflowReaped(id: WorkflowId): Future[Unit]

  /**
    * Mark that reaping a workflow resulted in an error.
    */
  def markWorkflowReapingFailed(id: WorkflowId, e: Throwable): Future[Unit]
}
