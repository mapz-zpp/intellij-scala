package org.jetbrains.plugins.scala.lang.resolve2


/**
 * Pavel.Fatin, 02.02.2010
 */

class FunctionCurryTest extends ResolveTestBase {
  override def getTestDataPath: String = {
    super.getTestDataPath + "function/curry/"
  }

  def testCurryiedToCurryied = doTest
  //TODO
//  def testCurryiedToNormal = doTest
  //TODO
//  def testNormalToCurryied = doTest
}