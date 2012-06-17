package de.cupofjava.wordindexer

/**
 * This is a small indexer for retrieving the line number and column of each word in a text.
 * The goal is to achieve this functionality by the use of recursion and functional paradigms.
 *
 * @author Felix Müller
 */
object WordIndexer {

  def index(text: String) : Index = text.lines.zipWithIndex.map(line => {
    positions(Index(), line._1, line._2)
  }).foldLeft(Index())(_ + _)

  @annotation.tailrec
  private def positions(index: Index, line: String, numberOfLine: Int, position: Int = 0) : Index = {
    if (line.isEmpty) {
      index
    } else {
      val word = nextWord(line)
      if (word._2.isEmpty) {
        val nonWord = nonWordCharacters(line)
        positions(index, nonWord._1, numberOfLine, position + nonWord._2.length)
      } else {
        positions(index + (word._2, Set(Position(numberOfLine + 1, position + 1))),
                  word._1, numberOfLine, position + word._2.length)
      }
    }
  }

  @annotation.tailrec
  private def nextWord(line: String, word: String = "") : (String, String) = {
    if (line.isEmpty) {
      (line, word)
    } else {
      val first = line.head
      if (isStartOfWord(first) || (!word.isEmpty && first.isDigit)) {
        nextWord(line.tail, word + first)
      } else {
        (line, word)
      }
    }
  }

  @annotation.tailrec
  private def nonWordCharacters(line: String, nonWord: String = "") : (String, String) = {
    if (line.isEmpty || isStartOfWord(line.head)) {
      (line, nonWord)
    } else {
      nonWordCharacters(line.tail, nonWord + line.head)
    }
  }

  private def isStartOfWord(c: Char) = c.isLetter || c == '_'
}
