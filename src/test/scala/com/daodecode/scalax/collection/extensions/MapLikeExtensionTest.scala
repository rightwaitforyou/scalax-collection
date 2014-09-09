package com.daodecode.scalax.collection.extensions

import org.scalatest.{Matchers, FlatSpec}

import scala.collection._

class MapLikeExtensionTest extends FlatSpec with Matchers {

  "mergeWith" should "merge empty maps" in {
    Map.empty[String, Int].mergeBy(Map.empty[String, Int])(_ + _) should be(Map.empty[String, Int])
    Map("1" -> 1, "2" -> 2).mergeBy(Map.empty[String, Int])(_ + _) should be(Map("1" -> 1, "2" -> 2))
    Map.empty[String, Int].mergeBy(Map("1" -> 1, "2" -> 2))(_ + _) should be(Map("1" -> 1, "2" -> 2))
  }

  it should "merge non-empty maps" in {
    Map("1" -> 1, "2" -> 2).mergeBy(Map("1" -> 1, "2" -> 2))(_ + _) should be(Map("1" -> 2, "2" -> 4))
    Map("1" -> 1, "2" -> 2).mergeBy(Map("1" -> 1, "2" -> 2))(_ - _) should be(Map("1" -> 0, "2" -> 0))
  }

  it should "keep type of source map" in {
    val mutableMap = mutable.Map("1" -> 1, "2" -> 2)
    val immutableMap = immutable.Map("1" -> 1, "2" -> 2)

    immutableMap.mergeBy(mutableMap)(_ + _) should be(an[immutable.Map[String, Int]])
    mutableMap.mergeBy(immutableMap)(_ + _) should be(a[mutable.Map[String, Int]])

    mutableMap should be(Map("1" -> 1, "2" -> 2))

    immutableMap.mergeBy(mutableMap)(_ + _)
  }

  it should "not modify original map if it's mutable" in {
    val mutableMap = mutable.Map("1" -> 1, "2" -> 2)

    val resultMap = mutableMap.mergeBy(Map("1" -> 1, "2" -> 2))(_ + _)

    mutableMap should be(Map("1" -> 1, "2" -> 2))
    resultMap should be(Map("1" -> 2, "2" -> 4))
  }

  it should "not link original map with result map if original is mutable" in {
    val mutableMap = mutable.Map("1" -> 1, "2" -> 2)
    val resultMap = mutableMap.mergeBy(Map("1" -> 1, "2" -> 2))(_ + _)

    mutableMap += "3" -> 3

    mutableMap should be(Map("1" -> 1, "2" -> 2, "3" -> 3))
    resultMap should be(Map("1" -> 2, "2" -> 4))

    resultMap += "4" -> 2

    mutableMap should be(Map("1" -> 1, "2" -> 2, "3" -> 3))
    resultMap should be(Map("1" -> 2, "2" -> 4, "4" -> 2))
  }

  it should "pass value from original map as a first argument to merge function" in {
    Map("1" -> 1, "2" -> 2).mergeBy(Map("1" -> 2, "2" -> 1))((a, b) => a) should be(
      Map("1" -> 1, "2" -> 2))
  }

}
