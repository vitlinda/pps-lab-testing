package testLecture.code.e3propertyBased

import org.junit.runner.RunWith
import org.scalacheck.Prop.{exists, forAll}
import org.scalacheck.{Arbitrary, Gen, Prop, Properties}
import testLecture.ScalaCheckJUnitRunner

// https://github.com/oscarrenalias/scalacheck-cookbook/blob/master/markdown/scalacheck-integration.md
@RunWith(classOf[ScalaCheckJUnitRunner])
class TestOnNumbers extends Properties("Numbers") {
  property("Sum is associative") = forAll{ (a: Int, b: Int, c: Int) =>
    (a+b)+c == a+(b+c)
  }
  property("Sum is commutative") = forAll { (a: Int, b: Int) =>
    a+b == b+a
  }
  property("Sum has zero as identity") = forAll { (a: Int) =>
    a + 0 == a && 0 + a == a
  }
  property("Diff is not associative") = forAll{ (a: Int, b: Int) =>
    exists{ c:Int => (a-b)-c != a-(b-c) }
  }
}

@RunWith(classOf[ScalaCheckJUnitRunner])
class TestOnLists extends Properties("Seqs") {
  def genericSizeProp[A:Arbitrary]: Prop = forAll{ (l1: Seq[A], l2: Seq[A]) =>
    (l1++l2).size == l1.size + l2.size
  }
  property("Size of concatenation") = Prop.all(
    genericSizeProp[Int], genericSizeProp[String], genericSizeProp[(Boolean,Double)])

  property("Reverse") = forAll { (l1: Seq[Int]) =>
    l1.reverse.reverse == l1
  }
}

@RunWith(classOf[ScalaCheckJUnitRunner])
class TestPersons extends Properties("Persons") {
  case class Person(name: String, age: Int) {
    def adult: Boolean = age >= 18
  }

  val adultPersonGen: Gen[Person] = for(name <- Gen.const("Bob");
                                        age <- Gen.choose(18,100))
    yield Person(name, age)

  property("Adultness") = forAll(adultPersonGen) { (p: Person) =>
    p.adult
  }

  implicit val arbitraryPerson: Arbitrary[Person] = Arbitrary(
    for(name <- Gen.oneOf("Bob","Rick");
        age <- Gen.chooseNum(0,100).suchThat(x => x >= 18))
      yield Person(name, age)
  )

  // Using an Arbitrary[Person] (a kind of default generator for a type)
  property("Adultness 2") = forAll { (p: Person) =>
    Prop.collect(p){ p.adult }
  }

  property("Elder") = exists { (p: Person) => p.age > 200 }
}