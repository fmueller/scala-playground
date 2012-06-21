package de.cupofjava.wordindexer

/**
 * This is a small indexer for retrieving the line number and column of each word in a text.
 * The goal is to achieve this functionality by the use of recursion and functional paradigms.
 *
 * @author Felix Müller
 */
object WordIndexer {

  type Line = Seq[(Char, Int)]

  def index(text: String) : Index = text.lines.toList.par.zipWithIndex.map(line => {
    positions(Index(), line._1.zipWithIndex, line._2)
  }).foldLeft(Index())(_ + _)

  @annotation.tailrec
  private def positions(index: Index, line: Line, numberOfLine: Int) : Index = {
    if (line.isEmpty) {
      index
    } else {
      val word = nextWord(line)
      if (word._2.isEmpty) {
        positions(index, skipNonWordCharacters(line), numberOfLine)
      } else {
        positions(index + (word._2, Set(Position(numberOfLine + 1, word._3 + 1))), word._1, numberOfLine)
      }
    }
  }

  @annotation.tailrec
  private def nextWord(line: Line, word: String = "", startOfWord: Int = -1) : (Line, String, Int) = {
    if (line.isEmpty) {
      (line, word, startOfWord)
    } else {
      val first = line.head._1
      if (isStartOfWord(first) || (!word.isEmpty && first.isDigit)) {
        if (startOfWord == -1) {
          nextWord(line.tail, word + first, line.head._2)
        } else {
          nextWord(line.tail, word + first, startOfWord)
        }
      } else {
        (line, word, startOfWord)
      }
    }
  }

  @annotation.tailrec
  private def skipNonWordCharacters(line: Line, nonWord: String = "") : Line = {
    if (line.isEmpty || isStartOfWord(line.head._1)) {
      line
    } else {
      skipNonWordCharacters(line.tail, nonWord + line.head)
    }
  }

  private def isStartOfWord(c: Char) = c.isLetter || c == '_'
}
