package app.persistence

import app.model.Person

class PersonDao extends Dao[Person] {

  def findAll() : Seq[Person] = Nil
  def save(person: Person) = person
  def update(person: Person) = person
  def delete(person: Person) = person
}