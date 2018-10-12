/**
 * This class contains class (static) methods
 * that will help you test the Picture class 
 * methods.  Uncomment the methods and the code
 * in the main to test.
 * 
 * @author Barbara Ericson 
 */
public class PictureTester
{
  /** Method to test zeroBlue */
  public static void testZeroBlue()
  {
    Picture beach = new Picture("beach.jpg");
    beach.explore();
    beach.zeroBlue();
    beach.explore();
  }
  
  public static void testKeepOnlyBlue()
  {
    Picture beach = new Picture("beach.jpg");
    beach.explore();
    beach.keepOnlyBlue();
    beach.explore();
  }
  
  public static void testNegate()
  {
    Picture beach = new Picture("beach.jpg");
    beach.explore();
    beach.negate();
    beach.explore();
  }
  
  public static void testGrayscale()
  {
    Picture beach = new Picture("beach.jpg");
    beach.explore();
    beach.grayscale();
    beach.explore();
  }
  
  public static void testFixUnderwater()
  {
    Picture water = new Picture("water.jpg");
    water.explore();
    water.fixUnderwater();
    water.explore();
  }
  
  
  /** Method to test mirrorVertical */
  public static void testMirrorVertical()
  {
    Picture caterpillar = new Picture("caterpillar.jpg");
    caterpillar.explore();
    caterpillar.mirrorVertical();
    caterpillar.explore();
  }
  
  public static void testMirrorVerticalRightToLeft()
  {
    Picture caterpillar = new Picture("caterpillar.jpg");
    caterpillar.explore();
    caterpillar.mirrorVerticalRightToLeft();
    caterpillar.explore();
  }
  
  public static void testMirrorHorizontal()
  {
    Picture motorcycle = new Picture("redMotorcycle.jpg");
    motorcycle.explore();
    motorcycle.mirrorHorizontal();
    motorcycle.explore();
  }
  
  public static void testMirrorHorizontalBotToTop()
  {
    Picture motorcycle = new Picture("redMotorcycle.jpg");
    motorcycle.explore();
    motorcycle.mirrorHorizontalBotToTop();
    motorcycle.explore();
  }
  
  /** Method to test mirrorTemple */
  public static void testMirrorTemple()
  {
    Picture temple = new Picture("temple.jpg");
    temple.explore();
    temple.mirrorTemple();
    temple.explore();
  }
  
  public static void testCopy()
  {
    Picture canvas = new Picture("640x480.jpg");
    Picture water = new Picture("water.jpg");
    water.explore();
    canvas.copy(water,100,50);
    canvas.explore();
  }
  
  /** Method to test the collage method */
  public static void testCollage()
  {
    Picture canvas = new Picture("640x480.jpg");
    canvas.createCollage();
    canvas.explore();
  }
  
  public static void testMyCollage()
  {
    Picture canvas = new Picture("640x480.jpg");
    canvas.myCollage();
    canvas.explore();
  }
  
  /** Method to test edgeDetection */
  public static void testEdgeDetection()
  {
    Picture swan = new Picture("beach.jpg");
    swan.explore();
    swan.edgeDetection(20);
    swan.explore();
  }
  
  public static void testPixelate()
  {
    Picture swan = new Picture("beach.jpg");
    swan.explore();
    swan.pixelate(4);
    swan.explore();
  }
  
  public static void testBlur()
  {
    Picture swan = new Picture("test.jpg");
    swan.explore();
    swan.blur(12);
    swan.explore();
  }
  
  public static void testCrossOutPicture()
  {
      Picture f = new Picture("flower1.jpg");
      f.explore();
      f.crossOutPicture();
      f.explore();
  }
  
  /** Main method for testing.  Every class can have a main
    * method in Java */
  public static void main(String[] args)
  {
    // uncomment a call here to run a test
    // and comment out the ones you don't want
    // to run
    testZeroBlue();
    testKeepOnlyBlue();
    //testKeepOnlyRed();
    //testKeepOnlyGreen();
    testNegate();
    testGrayscale();
    testFixUnderwater();
    testMirrorVertical();
    testMirrorVerticalRightToLeft();
    testMirrorHorizontal();
    testMirrorHorizontalBotToTop();
    //testMirrorTemple();
    //testMirrorArms();
    //testMirrorGull();
    //testMirrorDiagonal();
    //testCollage();
    //testCopy();
    //testEdgeDetection();
    //testEdgeDetection2();
    //testChromakey();
    //testEncodeAndDecode();
    //testGetCountRedOverValue(250);
    //testSetRedToHalfValueInTopHalf();
    //testClearBlueOverValue(200);
    //testGetAverageForColumn(0);
  }
}