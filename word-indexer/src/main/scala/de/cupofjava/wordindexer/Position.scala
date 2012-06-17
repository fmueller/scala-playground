package de.cupofjava.wordindexer

/**
 * @author Felix Müller
 */
case class Position(row: Int, column: Int) {

  override def toString() = "(" + row + "," + column + ")"
}
