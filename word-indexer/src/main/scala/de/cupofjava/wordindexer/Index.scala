package de.cupofjava.wordindexer

import collection.mutable.Map

/**
 * @author Felix Müller
 */
case class Index(wordPositions: Map[String, Set[Position]] = Map.empty) {

  def +(index: Index) : Index = {
    index.wordPositions foreach { entry =>
      if (this.wordPositions.keySet.exists(_ == entry._1)) {
        this.wordPositions(entry._1) ++= entry._2
      } else {
        this.wordPositions += entry
      }
    }
    Index(this.wordPositions)
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
