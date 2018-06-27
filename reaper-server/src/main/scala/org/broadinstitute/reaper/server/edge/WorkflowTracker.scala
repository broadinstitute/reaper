package org.broadinstitute.reaper.server.edge

import akka.NotUsed
import akka.stream.scaladsl.Source
import cromwell.api.model.WorkflowId

import scala.concurrent.Future

trait WorkflowTracker {

  def pullWorkflowIds(): Source[WorkflowId, NotUsed]

  def markWorkflowReaped(id: WorkflowId): Future[Unit]

  def markWorkflowReapingFailed(id: WorkflowId, e: Throwable): Future[Unit]
}
