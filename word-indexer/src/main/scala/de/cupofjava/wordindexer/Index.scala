package de.cupofjava.wordindexer

/**
 * @author Felix Müller
 */
case class Index(wordPositions: Map[String, Set[Position]] = Map.empty) {

  def +(index: Index) : Index = {
    var tempMap = collection.mutable.Map[String, Set[Position]]()
    tempMap ++= wordPositions
    index.wordPositions foreach { entry =>
      if (tempMap.keySet.exists(_ == entry._1)) {
        tempMap(entry._1) ++= entry._2
      } else {
        tempMap += entry
      }
    }
    Index(tempMap.toMap)
  }

  def +(wordPosition : (String, Set[Position])) : Index = {
    this + Index(Map(wordPosition))
  }

  override def toString() = {
    val sortedWordsOfIndex = wordPositions.keys.toList.sortWith(_.compareToIgnoreCase(_) < 0)
    sortedWordsOfIndex.map(word => {
      val str = wordPositions(word).map(" " + _).foldLeft(word)(_ + _ + ",")
      str.substring(0, str.length - ",".length)
    }).foldLeft("")(_ + "\n" +  _).trim
  }
}
