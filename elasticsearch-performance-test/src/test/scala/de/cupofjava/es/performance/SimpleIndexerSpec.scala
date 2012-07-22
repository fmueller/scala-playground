package de.cupofjava.es.performance

import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers

class SimpleIndexerSpec extends WordSpec with ShouldMatchers with IndexerHelper {

  val indexName = "myindex"
  val documentType = "document"

  "A simple indexer" should {

    "index one document" in {
      indexer.index(indexName, documentType, "1", """ { "key": "value" } """)
      assertDocumentWasIndexedAndCanBeFullyRetrieved("1", "key", "value")
    }

    "index many documents and generate id automatically" in {
      indexer.index(indexName, documentType, "1", """ { "key1": "value1" } """)
      indexer.index(indexName, documentType, "2", """ { "key2": "value2" } """)
      indexer.index(indexName, documentType, "3", """ { "key3": "value3" } """)
      assertDocumentWasIndexedAndCanBeFullyRetrieved("1", "key1", "value1")
      assertDocumentWasIndexedAndCanBeFullyRetrieved("2", "key2", "value2")
      assertDocumentWasIndexedAndCanBeFullyRetrieved("3", "key3", "value3")
    }
  }

  private def assertDocumentWasIndexedAndCanBeFullyRetrieved(id: String, key: String, field: String) = {
    val response = indexer.get(indexName, documentType, id)
    response.exists should be (true)
    response.sourceAsMap().get(key) should be (field)
  }
}
