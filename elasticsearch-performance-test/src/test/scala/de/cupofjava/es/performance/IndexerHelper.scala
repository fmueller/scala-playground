package de.cupofjava.es.performance

import org.scalatest.{Suite, BeforeAndAfter}
import com.traackr.scalastic.elasticsearch.Indexer

trait IndexerHelper extends BeforeAndAfter {

  self: Suite =>

  var indexer: Indexer = _

  before({
    indexer = Indexer.local.start()
    indexer.deleteIndex()
  })

  after({
    indexer.stop
  })
}
