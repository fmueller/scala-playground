package de.cupofjava.wordindexer

import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers

class WordIndexerSpec extends WordSpec with ShouldMatchers {

  "A word indexer" should index {

    "an empty text" in {
      indexWithSuccess("", withOutput(""))
    }

    "one word" in {
      indexWithSuccess("Test", withOutput("Test (1,1)"))
    }

    "one word with leading non word characters" in {
      indexWithSuccess("    ,:.+*Test", withOutput("Test (1,10)"))
    }

    "two words" in {
      indexWithSuccess("Hello World", withOutput("Hello (1,1)\nWorld (1,7)"))
    }

    "two words in two lines" in {
      indexWithSuccess("Hello\nWorld", withOutput("Hello (1,1)\nWorld (2,1)"))
    }

    "one word that is starting in line two" in {
      indexWithSuccess("\nWorld", withOutput("World (2,1)"))
    }

    "one word that occures twice" in {
      indexWithSuccess("Hello Hello", withOutput("Hello (1,1), (1,7)"))
    }

    "two words that occure multiple times" in {
      indexWithSuccess("Hello World Hello Hello World",
        withOutput("Hello (1,1), (1,13), (1,19)\n"
          + "World (1,7), (1,25)"))
    }

    "case sensitive" in {
      indexWithSuccess("hElLo HeLlO", withOutput("hElLo (1,1)\nHeLlO (1,7)"))
    }

    "case sensitive but print out properly" in {
      indexWithSuccess("Der Die Das der", withOutput("Das (1,9)\nder (1,13)\nDer (1,1)\nDie (1,5)"))
    }

    "one word when input ends with non-word characters" in {
      indexWithSuccess("Hello 123", withOutput("Hello (1,1)"))
    }

    "two words when input ends with non-word characters" in {
      indexWithSuccess("Hello Hello  \t123", withOutput("Hello (1,1), (1,7)"))
    }
  }

  def index = afterWord("index")

  def indexWithSuccess(text: String, output: String) {
    val result = WordIndexer.index(text).toString
    result should be (output)
  }

  def withOutput(output: String) = output
}
