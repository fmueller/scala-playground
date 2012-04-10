package app.persistence

import app.model.Person

object PersonDao extends Dao[Person] {

  def findAll() = Nil
  def save(person: Person) = person
  def update(person: Person) = person
  def delete(person: Person) = person
}