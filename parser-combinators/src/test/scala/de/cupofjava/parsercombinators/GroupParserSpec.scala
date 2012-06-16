package de.cupofjava.parsercombinators

import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers

class GroupParserSpec extends WordSpec with ShouldMatchers {

  "A group parser" should parse {

    "one group" in {
      val groups = GroupParser.parse(
        """[groups]
          |admins = chef, techlead
        """.stripMargin)
      groups should be (List(Group("admins", Seq("chef", "techlead"))))
    }

    "many groups" in {
      val groups = GroupParser.parse(
        """[groups]
          |admins = chef, techlead
          |devs = max, tim, paul
          |users = jonny, tom
        """.stripMargin)
      groups should be (List(
        Group("admins", Seq("chef", "techlead")),
        Group("devs",   Seq("max", "tim", "paul")),
        Group("users",  Seq("jonny", "tom"))))
    }

    "no groups if input is not valid" in {
      val groups = GroupParser.parse(
        """[groups]
          |admins = chef techlead
        """.stripMargin)
      groups should be ('empty)
    }
  }

  def parse = afterWord("parse")
}
