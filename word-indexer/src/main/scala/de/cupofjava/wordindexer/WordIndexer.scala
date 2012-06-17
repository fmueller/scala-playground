package de.cupofjava.wordindexer

/**
 * This is a small indexer for retrieving the line number and column of each word in a text.
 * The goal is to achieve this functionality by the use of recursion and functional paradigms.
 *
 * @author Felix Müller
 */
object WordIndexer {

  def index(text: String) : Index = positions(Index(), text.lines.toList)

  @annotation.tailrec
  private def positions(index: Index, lines: List[String], line: Int = 1, position: Int = 0) : Index = {
    if (lines.isEmpty) {
      index
    } else if (lines.head.isEmpty) {
        positions(index, lines.tail, line + 1)
    } else {
      val word = nextWord(lines.head)
      if (word._2.isEmpty) {
        val nonWord = nonWordCharacters(lines.head)
        positions(index, List(nonWord._1) ++ lines.tail, line, position + nonWord._2.length)
      } else {
        positions(index + (word._2, Set(Position(line, position + 1))),
                  List(word._1) ++ lines.tail, line, position + word._2.length)
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
