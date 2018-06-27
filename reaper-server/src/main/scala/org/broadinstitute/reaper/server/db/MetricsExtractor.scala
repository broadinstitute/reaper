package org.broadinstitute.reaper.server.db

import org.broadinstitute.clio.transfer.model.{IndexKey, Metadata}

import scala.concurrent.Future

trait MetricsExtractor[K <: IndexKey, M <: Metadata[M]] {

  def extractMetrics(key: K, metadata: M): Future[Unit]
}
