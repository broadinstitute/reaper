package org.broadinstitute.reaper.server.db

import org.broadinstitute.clio.transfer.model.{IndexKey, Metadata}

import scala.concurrent.Future

/**
  * Component responsible for:
  * <ol>
  *   <li>Determining which files tracked by Clio point to metrics
  *   <li>Extracting metrics values from those files
  *   <li>Writing extracted metrics to the cloud database
  * </ol>
  */
trait MetricsExtractor[K <: IndexKey, M <: Metadata[M]] {

  /**
    * Locate, extract, and persist metrics from a Clio record.
    */
  def extractMetrics(key: K, metadata: M): Future[Unit]
}
