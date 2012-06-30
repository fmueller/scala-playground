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
    wordsWithPositions(Index(), line._1.zipWithIndex, line._2)
  }).foldLeft(Index())(_ + _)

  @annotation.tailrec
  private def wordsWithPositions(index: Index, line: Line, numberOfLine: Int) : Index = {
    if (line.isEmpty) {
      index
    } else {
      val wordTriple = nextWord(line)
      val word = wordTriple._2
      if (word.isEmpty) {
        wordsWithPositions(index, skipNonWordCharacters(line), numberOfLine)
      } else {
        val restOfLine = wordTriple._1
        val startOfWord = wordTriple._3
        wordsWithPositions(index + (word, Set(Position(numberOfLine + 1, startOfWord + 1))), restOfLine, numberOfLine)
      }
    }
  }

  @annotation.tailrec
  private def nextWord(line: Line, word: String = "", startOfWord: Int = -1) : (Line, String, Int) = {
    if (line.isEmpty) {
      (line, word, startOfWord)
    } else {
      val firstCharacter = line.head._1
      if (isStartOfWord(firstCharacter) || (!word.isEmpty && firstCharacter.isDigit)) {
        if (startOfWord == -1) {
          nextWord(line.tail, word + firstCharacter, line.head._2)
        } else {
          nextWord(line.tail, word + firstCharacter, startOfWord)
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
