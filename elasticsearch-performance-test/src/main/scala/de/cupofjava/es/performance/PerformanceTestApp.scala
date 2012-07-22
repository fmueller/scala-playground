package de.cupofjava.es.performance

import java.text.DecimalFormat
import org.apache.commons.lang.RandomStringUtils
import com.traackr.scalastic.elasticsearch.Indexer
import org.elasticsearch.action.index.IndexRequest
import org.apache.commons.lang.math.RandomUtils
import org.elasticsearch.index.query.QueryBuilders._
import org.elasticsearch.search.facet.FacetBuilders._
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.elasticsearch.common.settings.ImmutableSettings
import org.apache.commons.io.FileUtils
import java.io.File
import collection.JavaConversions._

/**
 * This is a small app for testing the performance of a given elasticsearch
 * server and its configuration. For that, an index named myindex will be created.
 * After this a certain number of documents will be indexed. At the end some
 * queries are run against the search server. The duration of all action is
 * printed out. You can change the settings and mapping of myindex via the files under
 * src/main/resources.
 *
 * @author Felix Müller
 */
object PerformanceTestApp {

  type Document = (String, String)
  type Documents = Seq[Document]

  def main(args: Array[String]) {
    print("Run elasticsearch in embedded mode: ")
    val isEmbedded = readBoolean()
    val host = readStringWithLabelIf(!isEmbedded, "Host: ")
    val port = readStringWithLabelIf(!isEmbedded, "Port: ")
    val clusterName = readStringWithLabelIf(!isEmbedded, "Cluster name: ")
    val numberOfDocuments = readIntWithLabel("Number of documents: ")
    val bulkSize = readIntWithLabel("Size of bulk insert: ")

    val documents = generateDocuments(numberOfDocuments)
    printf("Generated %s documents (size: %s mb)%n", numberOfDocuments,
      new DecimalFormat("#.##").format(sizeOf(documents)))

    println("Initializing elasticsearch...")
    val indexer = {
      if (isEmbedded) {
        Indexer.local.start()
      } else {
        Indexer.transport(host = host, ports = Seq(port.toInt), settings = Map(("cluster.name", clusterName)))
      }
    }
    println("Connection established!")
    println("Start indexing...")

    indexer.createIndex("myindex",
      settings = ImmutableSettings.settingsBuilder().loadFromClasspath("myindex-settings.yml").internalMap().toMap,
      mappings = Map(("document", FileUtils.readFileToString(
        new File(Thread.currentThread().getContextClassLoader().getResource("myindex-mappings.json").toURI)))))
    indexer.waitTillActive()

    indexAllDocuments(indexer, documents, bulkSize)
    runAllQueries(indexer)

    println("Delete all indices...")
    indexer.deleteIndex()
    if (isEmbedded) {
      println("Stopping elasticsearch...")
      indexer.stop
    }
    println("Shutdown.")
  }

  private def indexAllDocuments(indexer: Indexer, documents: Documents, bulkSize: Int) {
    measureExecutionTime("Importing and indexing lasts") {
      documents.sliding(bulkSize, bulkSize).zipWithIndex foreach { (nextDocumentsWithIndex) =>
        printf("%s: Indexing next %s documents...%n", nextDocumentsWithIndex._2 + 1, nextDocumentsWithIndex._1.length)
        val insertRequests = nextDocumentsWithIndex._1 map { d =>
          new IndexRequest("myindex", "document", d._1).source(d._2)
        }
        measureExecutionTime("Bulk insert lasts")(indexer.bulk(insertRequests))
      }
    }
    measureExecutionTime("Refreshing of index lasts")(indexer.refresh(Seq("myindex")))
  }

  private def runAllQueries(indexer: Indexer) {
    measureQueryExecutionTime(indexer, wildcardQuery("author", "a*"), "author a*")
    measureQueryExecutionTime(indexer, wildcardQuery("author", "*aaaa"), "author *aaaa")
    measureQueryExecutionTime(indexer, wildcardQuery("content", "abc*"), "content abc*")
    measureQueryExecutionTime(indexer, wildcardQuery("content", "*abc*"), "content *abc*")
    measureQueryExecutionTime(indexer, wildcardQuery("content", "*a*"), "content *a*")
    measureQueryExecutionTime(indexer, wildcardQuery("content", "*a"), "content *a")

    measureQueryExecutionTime(indexer,
      boolQuery()
        .must(wildcardQuery("content", "*a*"))
        .must(wildcardQuery("keywords", "*b")),
      "content *a* and keywords *b")

    measureQueryExecutionTime(indexer,
      boolQuery()
        .must(wildcardQuery("content", "*a*"))
        .must(wildcardQuery("keywords", "*b*")),
      "content *a* and keywords *b*")

    measureQueryExecutionTime(indexer,
      boolQuery()
        .should(wildcardQuery("content", "*a*"))
        .should(wildcardQuery("keywords", "*b*")),
      "content *a* or keywords *b*")

    measureQueryExecutionTime(indexer,
      boolQuery()
        .must(wildcardQuery("content", "*a*"))
        .must(wildcardQuery("keywords", "*b*"))
        .must(wildcardQuery("title", "*c*")),
      "content *a* and keywords *b* and title *c*")

    measureQueryExecutionTime(indexer,
      spanNearQuery().slop(10).clause(spanTermQuery("content", "abc")).clause(spanTermQuery("content", "aaa")),
      "content abc and aaa with slop 10")

    measureExecutionTime("Query for content *a* with facetting by pages lasts") {
      indexer.search(indices = Seq("myindex"),
        internalBuilder = Some(new SearchSourceBuilder()
          .query(wildcardQuery("content", "*a*"))
          .facet(termsFacet("pages").field("pages"))))
    }
  }

  private def generateDocuments(numberOfDocuments: Int) : Documents = {
    val docTemplate = """
                        |{
                        |  "title": "#title",
                        |  "author": "#firstname #lastname",
                        |  "keywords": "#keywords",
                        |  "content": "#content",
                        |  "pages": "#pages"
                        |}
                      """.stripMargin

    (1 to numberOfDocuments) map { id =>
      val titleWordCount = 2 + RandomUtils.nextInt(9)
      val keywordsWordCount = 2 + RandomUtils.nextInt(19)
      val contentWordCount = 2 + RandomUtils.nextInt(999)

      val title = for (i <- 1 to titleWordCount) yield RandomStringUtils.randomAlphabetic(1 + RandomUtils.nextInt(64))
      val firstname = RandomStringUtils.randomAlphabetic(1 + RandomUtils.nextInt(32))
      val lastname = RandomStringUtils.randomAlphabetic(1 + RandomUtils.nextInt(32))
      val keywords = for (i <- 1 to keywordsWordCount) yield RandomStringUtils.randomAlphabetic(1 + RandomUtils.nextInt(64))
      val content = for (i <- 1 to contentWordCount) yield RandomStringUtils.randomAlphabetic(1 + RandomUtils.nextInt(64))

      (id.toString, docTemplate
        .replaceAll("#title", title.mkString(" "))
        .replaceAll("#firstname", firstname)
        .replaceAll("#lastname", lastname)
        .replaceAll("#keywords", keywords.mkString(" "))
        .replaceAll("#content", content.mkString(" "))
        .replaceAll("#pages", (1 + RandomUtils.nextInt(2000)).toString))
    }
  }

  private def sizeOf(documents: Documents) : Double = {
    val bytes = documents.foldLeft(0)((summed: Int, document: Document) => summed + document._2.getBytes.length)
    bytes  / 1024.0 / 1024.0
  }

  private def measureQueryExecutionTime(indexer: Indexer, q: QueryBuilder, text: String) {
    measureExecutionTime("Query for " + text + " lasts") {
      indexer.search(indices = Seq("myindex"), query = q)
    }
  }

  private def measureExecutionTime[A](text: String)(f: => A) {
    val start = System.currentTimeMillis
    f
    val elapsed = System.currentTimeMillis - start
    val formattedTimeElapsed = new DecimalFormat("#.##").format(elapsed / 1000.0)
    printf("%s %s seconds%n", text, formattedTimeElapsed)
  }

  private def readIntWithLabel(label: String) : Int = {
    print(label)
    readInt()
  }

  private def readStringWithLabelIf(shouldRead: Boolean, label: String) : String = {
    if (shouldRead) {
      print(label)
      readLine()
    } else {
      ""
    }
  }
}
