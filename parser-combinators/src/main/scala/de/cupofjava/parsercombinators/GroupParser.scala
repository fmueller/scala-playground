package de.cupofjava.parsercombinators

import util.parsing.combinator._

/**
 * This parser parses group definitions which have the following form:
 * [groups]
 * group1 = user1, user2...
 *
 * The parsed result is transformed into a list of group objects.
 *
 * @author Felix Müller
 */
object GroupParser extends RegexParsers {

  def parse(str: String) : List[Group] = parseAll(groups, str) match {
    case Success(groups, _) => groups
    case Failure(groups, _) => Nil
  }

  def groups : Parser[List[Group]] = "[groups]" ~> rep(group) ^^ ( List() ++ _ )

  def group : Parser[Group] = name ~ "=" ~ users ^^ { case name ~ "=" ~ users => Group(name, users) }

  def users : Parser[List[String]] = repsep(name, ",")

  def name = """[a-zA-Z_]*""".r
}
