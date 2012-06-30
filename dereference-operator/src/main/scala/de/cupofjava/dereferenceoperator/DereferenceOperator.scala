package de.cupofjava.dereferenceoperator

/**
 * This is an example how one could implement a dereference operator in Scala. The idea is copied from:
 * http://stackoverflow.com/questions/1163393/best-scala-imitation-of-groovys-safe-dereference-operator
 *
 * @author Felix Müller
 */
object DereferenceOperator {

  class SafeDereference[A](obj: A) {

    def ?[B >: Null](function: A => B): B = if (obj == null) null else function(obj)
  }

  implicit def safeDereference[A](obj: A) = new SafeDereference(obj)
}
