package de.cupofjava.dereferenceoperator

import dummydomain.{Name, PersonWithNameObject, Person}
import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers

class DereferenceOperatorSpec extends WordSpec with ShouldMatchers {

  import DereferenceOperator._

  "A dereference operator" should {

    "return the expected return value if it is not null" in {
      val joe = Person("joe")
      joe ? (_.name) should be ("joe")
    }

    "return null if the expected return value is null" in {
      val joe = Person(null)
      joe ? (_.name) should be (null)
    }

    "be chainable and return the value of the last called function if there is no null" in {
      val joe = PersonWithNameObject(Name("joe", "maier"))
      joe ? (_.name) ? (_.lastName) should be ("maier")
    }

    "return null if one value is null in a chained call" in {
      val joe = PersonWithNameObject(null)
      joe ? (_.name) ? (_.lastName) should be (null)
    }
  }
}
