import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.text.*;
import java.util.*;
import java.util.List; // resolves problem with java.awt.List and java.util.List
import java.awt.Color;
import java.util.stream.*;

import java.io.FileWriter;
import java.io.IOException;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;  


import georegression.fitting.curves.FitEllipseAlgebraic_F32;
import georegression.struct.point.Point2D_F32;
import georegression.struct.curve.EllipseQuadratic_F32;


import org.apache.commons.math3.optimization.fitting.HarmonicFitter;
import org.apache.commons.math3.optimization.general.*;
import org.apache.commons.math3.fitting.*;

class Drop
{
    int row;
    int col;
    int z;
    int x;
    int area;
    EllipseQuadratic_F32 ellipse;
    
    Drop(int row, int col, int z, int x, int area, EllipseQuadratic_F32 ellipse)
    {
        this.row = row;
        this.col = col;
        this.z = z;
        this.x = x;
        this.area = area;
        this.ellipse = ellipse;
    }
    
    public DropInfo getDropInfo()
    {
        return new DropInfo(this.calculateRmax(), this.calculateRmin(), this.calculateZ(), this.calculateX(), 
                            this.calculateAngle(), this.calculateArea(), this.calculateVolume());
    }
    
    public double calculateQ()
    {
        double foo = 4*this.ellipse.A*this.ellipse.C - this.ellipse.B*this.ellipse.B;
        
        return 64*(this.ellipse.F * foo - this.ellipse.A*this.ellipse.E*this.ellipse.E + this.ellipse.B*this.ellipse.D*this.ellipse.E 
        - this.ellipse.C*this.ellipse.D*this.ellipse.D) / (foo*foo);
    }
    
    public double calculateS()
    {
        double q = this.calculateQ();
        
        return .25*Math.sqrt(Math.abs(q) * Math.sqrt(this.ellipse.B*this.ellipse.B + Math.pow(this.ellipse.A-this.ellipse.C, 2)));
    }
    
    public double calculateRmax()
    {
        double q = this.calculateQ();
        
        return .125*Math.sqrt(2 * Math.abs(q) * Math.sqrt(this.ellipse.B*this.ellipse.B + Math.pow(this.ellipse.A-this.ellipse.C, 2)) - 2*q*(this.ellipse.A + this.ellipse.C));
    }
    
    public double calculateRmin()
    {
        double s = this.calculateS();
        
        return Math.sqrt(Math.pow(this.calculateRmax(), 2) - s*s);        
    }
    
    public double calculateZ()
    {
        return (this.ellipse.B*this.ellipse.E - 2*this.ellipse.C*this.ellipse.D) / (4*this.ellipse.A*this.ellipse.C - this.ellipse.B*this.ellipse.B);
    }
    
    public double calculateX()
    {
        return (this.ellipse.B*this.ellipse.D - 2*this.ellipse.A*this.ellipse.E) / (4*this.ellipse.A*this.ellipse.C - this.ellipse.B*this.ellipse.B);
    }
    
    public double calculateAngle()
    {
        double q = this.calculateQ();
        double qac = q*(this.ellipse.A - this.ellipse.C);
        
        if(qac == 0)
        {
            double qb = q*this.ellipse.B;
            
            if(qb == 0)
            {return 0.0;}
            else if(qb > 0)
            {return .25*Math.PI;}
            else
            {return .75*Math.PI;}  
        }
        else if (qac > 0)
        {
            double qb = q*this.ellipse.B;
            
            if(qb >= 0)
            {return .5*Math.atan(this.ellipse.B / (this.ellipse.A - this.ellipse.C));}
            else
            {return .5*Math.atan(this.ellipse.B / (this.ellipse.A - this.ellipse.C)) + Math.PI;}
        }
        else
        {
            return .5*Math.atan(this.ellipse.B / (this.ellipse.A - this.ellipse.C)) + .5*Math.PI;
        }
    }
    
    public double calculateVolume()
    {
        return calculateVolume(this.calculateRmax(), this.calculateRmin());
    }
    
    public double calculateVolume(double rmax, double rmin)
    {        
        return (4.0/3)*Math.PI*rmax*rmin*rmin;
    }
    
    public double calculateArea()
    {
        return calculateArea(this.calculateRmax(), this.calculateRmin());
    }
    
    public double calculateArea(double rmax, double rmin)
    {
        return Math.PI*rmax*rmin;
    }
}

class DropInfo
{
    double rmax;
    double rmin;
    double z;
    double x;
    double angle;
    double area;
    double volume;
    
    DropInfo(double rmax, double rmin, double z, double x, double angle, double area, double volume)
    {
        this.rmax = rmax;
        this.rmin = rmin;
        this.z = z;
        this.x = x;
        this.angle = angle;
        this.area = area;
        this.volume = volume;
    }
}    


/**
 * A class that represents a picture.  This class inherits from
 * Simplepicture and allows the student to add functionality to
 * the picture class.
 *
 * @author Barbara Ericson ericson@cc.gatech.edu
 */
public class Picture extends SimplePicture
{
  ///////////////////// constructors //////////////////////////////////

  /**
   * Constructor that takes no arguments
   */
  public Picture ()
  {
    /* not needed but use it to show students the implicit call to super()
     * child constructors always call a parent constructor
     */
    super();
  }

  /**
   * Constructor that takes a file name and creates the picture
   * @param fileName the name of the file to create the picture from
   */
  public Picture(String fileName)
  {
    // let the parent class handle this fileName
    super(fileName);
  }

  /**
   * Constructor that takes the width and height
   * @param height the height of the desired picture
   * @param width the width of the desired picture
   */
  public Picture(int height, int width)
  {
    // let the parent class handle this width and height
    super(width,height);
  }

  /**
   * Constructor that takes a picture and creates a
   * copy of that picture
   * @param copypicture the picture to copy
   */
  public Picture(Picture copyPicture)
  {
    // let the parent class do the copy
    super(copyPicture);
  }

  /**
   * Constructor that takes a buffered image
   * @param image the buffered image to use
   */
  public Picture(BufferedImage image)
  {
    super(image);
  }

  ////////////////////// methods ///////////////////////////////////////

  /**
   * Method to return a string with information about this picture.
   * @return a string with information about the picture such as fileName,
   * height and width.
   */
  public String toString()
  {
    String output = "picture, filename " + getFileName() +
      " height " + getHeight()
      + " width " + getWidth();
    return output;

  }
  
  public static void writeCsv(String[][] csvMatrix, String filename)
  {
    ICsvListWriter csvWriter = null;
    try 
    {
        csvWriter = new CsvListWriter(new FileWriter(filename), CsvPreference.STANDARD_PREFERENCE);
    
        for (int i = 0; i < csvMatrix.length; i++) 
        {
            csvWriter.write(csvMatrix[i]);
        }
    
    } 
    catch (IOException e) 
    {
        e.printStackTrace(); // TODO handle exception properly
    } 
    finally 
    {
        try 
        {
            csvWriter.close();
        } 
    catch (IOException e) {}
    }
  }

  /** Method to set the blue to 0 */
  public void zeroBlue()
  {

    Pixel[][] Pixels = this.getPixels2D();
    for (Pixel[] rowArray : Pixels)
    {
      int i = 0;
      for (Pixel PixelObj : rowArray)
      {
        if(i%2 == 0)
        {
            PixelObj.setBlue(0);
        }
        i++;
      }
    }
  }

  /** Method that mirrors the picture around a
    * vertical mirror in the center of the picture
    * from left to right */
  public void mirrorVertical()
  {
    Pixel[][] Pixels = this.getPixels2D();
    Pixel leftPixel = null;
    Pixel rightPixel = null;
    int width = Pixels[0].length;
    for (int row = 0; row < Pixels.length; row++)
    {
      for (int col = 0; col < width / 2; col++)
      {
        leftPixel = Pixels[row][col];
        rightPixel = Pixels[row][width - 1 - col];
        rightPixel.setColor(leftPixel.getColor());
      }
    }
  }

  public void mirrorVerticalRightToLeft()
  {
    Pixel[][] Pixels = this.getPixels2D();
    Pixel leftPixel = null;
    Pixel rightPixel = null;
    int width = Pixels[0].length;
    for (int row = 0; row < Pixels.length; row++)
    {
      for (int col = 0; col < width / 2; col++)
      {
        leftPixel = Pixels[row][col];
        rightPixel = Pixels[row][width - 1 - col];
        leftPixel.setColor(rightPixel.getColor());
      }
    }
  }

  public void mirrorHorizontal()
  {
    Pixel[][] Pixels = this.getPixels2D();
    Pixel topPixel = null;
    Pixel botPixel = null;
    int height = Pixels.length;
    for (int row = 0; row < height/2; row++)
    {
      for (int col = 0; col < Pixels[0].length; col++)
      {
        topPixel = Pixels[row][col];
        botPixel = Pixels[height - 1 - row][col];
        botPixel.setColor(topPixel.getColor());
      }
    }
  }

  public void mirrorHorizontalBotToTop()
  {
    Pixel[][] Pixels = this.getPixels2D();
    Pixel topPixel = null;
    Pixel botPixel = null;
    int height = Pixels.length;
    for (int row = 0; row < height/2; row++)
    {
      for (int col = 0; col < Pixels[0].length; col++)
      {
        topPixel = Pixels[row][col];
        botPixel = Pixels[height - 1 - row][col];
        topPixel.setColor(botPixel.getColor());
      }
    }
  }


  /** Mirror just part of a picture of a temple */
  public void mirrorTemple()
  {
    int mirrorPoint = 276;
    Pixel leftPixel = null;
    Pixel rightPixel = null;
    int count = 0;
    Pixel[][] Pixels = this.getPixels2D();

    // loop through the rows
    for (int row = 27; row < 97; row++)
    {
      // loop from 13 to just before the mirror point
      for (int col = 13; col < mirrorPoint; col++)
      {

        leftPixel = Pixels[row][col];
        rightPixel = Pixels[row][mirrorPoint - col + mirrorPoint];
        rightPixel.setColor(leftPixel.getColor());
      }
    }
  }

  /** copy from the passed fromPic to the
    * specified startRow and startCol in the
    * current picture
    * @param fromPic the picture to copy from
    * @param startRow the start row to copy to
    * @param startCol the start col to copy to
    */
  public void copy(Picture fromPic,
                 int startRow, int startCol)
  {
    Pixel fromPixel = null;
    Pixel toPixel = null;
    Pixel[][] toPixels = this.getPixels2D();
    Pixel[][] fromPixels = fromPic.getPixels2D();
    for (int fromRow = 0, toRow = startRow;
         fromRow < fromPixels.length &&
         toRow < toPixels.length;
         fromRow++, toRow++)
    {
      for (int fromCol = 0, toCol = startCol;
           fromCol < fromPixels[0].length &&
           toCol < toPixels[0].length;
           fromCol++, toCol++)
      {
        fromPixel = fromPixels[fromRow][fromCol];
        toPixel = toPixels[toRow][toCol];
        toPixel.setColor(fromPixel.getColor());
      }
    }
  }

  public void copy(Picture fromPic,
                 int startRow, int startCol, int startFromRow, int endFromRow, int startFromCol, int endFromCol)
  {
    Pixel fromPixel = null;
    Pixel toPixel = null;
    Pixel[][] toPixels = this.getPixels2D();
    Pixel[][] fromPixels = fromPic.getPixels2D();
    for (int fromRow = startFromRow, toRow = startRow;
         fromRow < endFromRow &&
         toRow < toPixels.length;
         fromRow++, toRow++)
    {
      for (int fromCol = startFromCol, toCol = startCol;
           fromCol < endFromCol &&
           toCol < toPixels[0].length;
           fromCol++, toCol++)
      {
        fromPixel = fromPixels[fromRow][fromCol];
        toPixel = toPixels[toRow][toCol];
        toPixel.setColor(fromPixel.getColor());
      }
    }
  }

  /** Method to create a collage of several pictures */
  public void createCollage()
  {
    Picture flower1 = new Picture("flower1.jpg");
    Picture flower2 = new Picture("flower2.jpg");
    this.copy(flower1,0,0);
    this.copy(flower2,100,0);
    this.copy(flower1,200,0);
    Picture flowerNoBlue = new Picture(flower2);
    flowerNoBlue.zeroBlue();
    this.copy(flowerNoBlue,300,0);
    this.copy(flower1,400,0);
    this.copy(flower2,500,0);
    this.mirrorVertical();
    this.write("collage.jpg");
  }

  public void myCollage()
  {
    Picture pic1 = new Picture("caterPIllar.jpg");
    Picture pic2 = new Picture("swan.jpg");
    Picture pic3 = new Picture("robot.jpg");
    this.copy(pic1,0,0,0,50,0,150);
    this.copy(pic2,100,0,30,130,300,480);
    this.copy(pic3,200,0,0,20,0,40);
    Picture pic2noBlue = new Picture(pic2);
    pic2noBlue.zeroBlue();
    this.copy(pic2noBlue,300,0,30,130,300,480);
    this.copy(pic1,400,0,0,50,0,150);
    this.copy(pic2,500,0,30,130,300,480);
    this.mirrorVertical();
    //this.write("collage.jpg");
  }


  /** Method to show large changes in color
    * @param edgeDist the distance for finding edges
    */
  public void edgeDetection(int edgeDist)
  {
    Pixel leftPixel = null;
    Pixel rightPixel = null;
    Pixel botPixel = null;
    Color botColor = null;
    Pixel[][] Pixels = this.getPixels2D();
    Color rightColor = null;
    for (int row = 0; row < Pixels.length; row++)
    {
      for (int col = 0;
           col < Pixels[0].length-1; col++)
      {
        leftPixel = Pixels[row][col];
        rightPixel = Pixels[row][col+1];
        rightColor = rightPixel.getColor();
        if(row < Pixels.length-1) //check with the Pixel below
        {
            botPixel = Pixels[row+1][col];
            botColor = botPixel.getColor();
            if (leftPixel.colorDistance(rightColor) >
                edgeDist || leftPixel.colorDistance(botColor) > edgeDist)
              leftPixel.setColor(Color.BLACK);
            else
              leftPixel.setColor(Color.WHITE);
        }
        else
        {
            if (leftPixel.colorDistance(rightColor) >
                edgeDist)
              leftPixel.setColor(Color.BLACK);
            else
              leftPixel.setColor(Color.WHITE);
        }
      }
    }
  }

  public void pixelate(int size)
  {
      Pixel[][] Pixels = this.getPixels2D();
      int redAvg = 0;
      int greenAvg = 0;
      int blueAvg = 0;
      int total = 0;
      int loops = 0;
      for(int i = 0; i <= Pixels.length/size; i++)
      {
          for(int j = 0; j <= Pixels[0].length/size; j++)
          {
              total = 0;
              redAvg = 0;
              greenAvg = 0;
              blueAvg = 0;
              for(int x = 0; x < size && x+i*size < Pixels.length; x++)
              {
                  for(int y = 0; y < size && y+j*size < Pixels[0].length; y++)
                  {
                      redAvg += Pixels[x+i*size][y+j*size].getRed();
                      greenAvg += Pixels[x+i*size][y+j*size].getGreen();
                      blueAvg += Pixels[x+i*size][y+j*size].getBlue();
                      //System.out.println(Pixels[x+i*size][y+j*size].getBlue());
                      total++;
                  }
              }
              if (total != 0)
              {
                  redAvg /= total;
                  blueAvg /= total;
                  greenAvg /= total;
                  //System.out.println(total);
                  //System.out.println(loops);
                  Color col = new Color(redAvg,greenAvg,blueAvg);
                  for(int x = 0; x < size && x+i*size < Pixels.length; x++)
                  {
                      for(int y = 0; y < size && y+j*size < Pixels[0].length; y++)
                      {
                          Pixels[x+i*size][y+j*size].setColor(col);
                      }
                  }
              }
              loops++;
          }
      }

  }

  public void blur(int size)
  {
      Pixel[][] Pixels = this.getPixels2D();
      Pixel[][] newPic = this.getPixels2D();
      int redAvg = 0;
      int greenAvg = 0;
      int blueAvg = 0;
      int total = 0;
      int loops = 0;
      for(int i = 0; i < Pixels.length; i++)
      {
          for(int j = 0; j < Pixels[0].length; j++)
          {
              total = 0;
              redAvg = 0;
              greenAvg = 0;
              blueAvg = 0;
              //find avg of surrounding Pixels
              for(int x = i-size/2; x < i+size/2; x++)
              {
                  for(int y = j-size/2; y < j+size/2; y++)
                  {
                      if(x>=0 && x<Pixels.length && y>=0 && y<Pixels[0].length)
                      {
                          redAvg += Pixels[x][y].getRed();
                          greenAvg += Pixels[x][y].getGreen();
                          blueAvg += Pixels[x][y].getBlue();
                          total++;
                      }
                  }
              }
              redAvg /= total;
              blueAvg /= total;
              greenAvg /= total;
              //System.out.println(redAvg + " " + greenAvg + " " + blueAvg);
              Color col = new Color(redAvg,greenAvg,blueAvg);
              //System.out.println(loops);
              //copy avg to new array
              newPic[i][j].setColor(col);
              loops++;
          }
      }

      //copy newPic to original
      for(int i = 0; i < Pixels.length; i++)
      {
          for(int j = 0; j < Pixels[0].length; j++)
          {
              Pixels[i][j] = newPic[i][j];
            }
        }
  }

  public void keepOnlyBlue()
  {
    Pixel[][] Pixels = this.getPixels2D();
    for (Pixel[] rowArray : Pixels)
    {
      for (Pixel PixelObj : rowArray)
      {
        PixelObj.setRed(0);
        PixelObj.setGreen(0);
      }
    }
  }

  public void negate()
  {
    Pixel[][] Pixels = this.getPixels2D();
    for (Pixel[] rowArray : Pixels)
    {
      for (Pixel PixelObj : rowArray)
      {
        PixelObj.setRed(255-PixelObj.getRed());
        PixelObj.setGreen(255-PixelObj.getGreen());
        PixelObj.setBlue(255-PixelObj.getBlue());
      }
    }
  }

  public void grayscale()
  {
    Pixel[][] Pixels = this.getPixels2D();
    int avg = 0;
    for (Pixel[] rowArray : Pixels)
    {
      for (Pixel PixelObj : rowArray)
      {
        avg = (PixelObj.getRed()+PixelObj.getGreen()+PixelObj.getBlue())/3;
        Color gray = new Color(avg, avg, avg);
        PixelObj.setColor(gray);
      }
    }
  }

  public void fixUnderwater()
  {
    Pixel[][] Pixels = this.getPixels2D();
    for (Pixel[] rowArray : Pixels)
    {
      for (Pixel PixelObj : rowArray)
      {
        //int newRed = (255+PixelObj.getRed())/2;
        //PixelObj.setRed(newRed);
        PixelObj.setRed(PixelObj.getRed()*3);
      }
    }
  }

  public void crossOutPicture()
  {
      Color col = new Color(0,0,0);
      Pixel[][] Pixels = this.getPixels2D();
      for(int i = 0; i < Pixels.length; i++)
      {
          for(int j = 0; j < Pixels[i].length; j++)
          {
              if(i == j || i+j == Pixels.length)
              {
                  Pixels[i][j].setColor(col);
              }
          }
      }
  }

  public void subtract(Picture p)
  {
      Pixel[][] pic = this.getPixels2D();
      Pixel[][] sub = p.getPixels2D();

      for(int i = 0; i < pic.length; i++)
      {
          for(int j = 0; j < pic[i].length; j++)
          {
              int r = 255-Math.abs(pic[i][j].getRed()-sub[i][j].getRed());
              int g = 255-Math.abs(pic[i][j].getGreen()-sub[i][j].getGreen());
              int b = 255-Math.abs(pic[i][j].getBlue()-sub[i][j].getBlue());

              if(r > 255) {r = 255;}
              if(g > 255) {g = 255;}
              if(b > 255) {b = 255;}

              pic[i][j].setColor(new Color(r,g,b));
          }
      }
  }

  public int sumRow(int r)
  {
      //System.out.println("sumCol(" + r + ")");
      Pixel[][] pic = this.getPixels2D();
      return this.sumRow(r, 0, pic[0].length);
  }

  public int sumRow(int r, int minCol, int maxCol)
  {
      //System.out.println("sumCol(" + r + ", " + minCol + ", " + maxCol + ")");
      Pixel[][] pic = this.getPixels2D();
      int sum = 0;
      for(int c = minCol; c < maxCol-1; c++)
      {
          //System.out.println(r + ", " + c);
          if(pic[r][c].getRed() == 0)
          {
              sum++;
          }
      }
      return sum;
  }

  public int sumCol(int c)
  {
      //System.out.println("sumCol(" + c + ")");
      Pixel[][] pic = this.getPixels2D();
      return this.sumCol(c, 0, pic.length);
      
  }

  public int sumCol(int c, int minRow, int maxRow)
  {
      //System.out.println("sumCol(" + c + ", " + minRow + ", " + maxRow + ")");
      Pixel[][] pic = this.getPixels2D();
      int sum = 0;
      for(int r = minRow; r < maxRow-1; r++)
      {
          
          //System.out.println(r + ", " + c);
          if(pic[r][c].getRed() == 0)
          {
              sum++;
          }
      }
      //System.out.println(sum);
      return sum;
  }
  
  public int sumColor(int c, int minRow, int maxRow)
  {
      //System.out.println("sumCol(" + c + ", " + minRow + ", " + maxRow + ")");
      Pixel[][] pic = this.getPixels2D();
      
      int sum = 0;
      for(int r = minRow; r < maxRow-1; r++)
      {
          //System.out.println(pic.length + "," + pic[r].length);
          //System.out.println(r + ", " + c);
          sum += (255-pic[r][c].getRed());
      }
      //System.out.println(sum);
      return sum;
  }
  
  public boolean isChange(){return true;}
  
  public Drop drawRectAroundDrop(int threshold, int minSize)
  {
      Pixel[][] pic = this.getPixels2D();

      return drawRectAroundDrop(0, pic[0].length-1, threshold, minSize);
  }
  
  private void cleanDrop(int minRow, int maxRow, int minCol, int maxCol)
  {
      Pixel[][] pic = this.getPixels2D();
      
      for(int j = minCol+1; j < maxCol; j++)
      {
          int firstPixel = -1;
          
          for(int i = minRow+1; i < maxRow; i++)
          {
              if(pic[i][j].getRed() != 255)
              {
                  if(firstPixel == -1)
                  {
                      firstPixel = i;
                  }
                  else
                  {
                      for(int k = firstPixel+1; k < i; k++)
                      {
                          pic[k][j].setColor(Color.WHITE);
                      }
                  }
              }
          }
      }
      
  }
  
  private EllipseQuadratic_F32 toOffsetEllipse(int minRow, int maxRow, int minCol, int maxCol, int z, int x)
  {
      Pixel[][] pic = this.getPixels2D();
      List<Point2D_F32> points = new ArrayList<Point2D_F32>();
      
      for(int j = minCol+1; j < maxCol; j++)
      {
          for(int i = minRow+1; i < maxRow; i++)
          {
              if(pic[i][j].getRed() != 255)
              {
                  Point2D_F32 p = new Point2D_F32(j-z, i-x);
                  //Point2D_F32 p = new Point2D_F32(j, i);
                  points.add(p);
              }
          }
      }
      
      FitEllipseAlgebraic_F32 estimator = new FitEllipseAlgebraic_F32();
      EllipseQuadratic_F32 ellipse;
        
      if(!estimator.process(points))
      {
          float temp = (float) 0.0;
          ellipse = new EllipseQuadratic_F32(temp, temp, temp, temp, temp, temp);
      }
      else
      {
          ellipse = estimator.getEllipse();
      }
      
      //System.out.println(ellipse.A + ", " + ellipse.B + ", " + ellipse.C + ", " + ellipse.D + ", " + ellipse.E + ", " + ellipse.F);
      return ellipse;
  }
  

  private Drop drawRectAroundDrop(int startRow, int startCol, int threshold, int minSize)
  {
      Pixel[][] pic = this.getPixels2D();
      int minRow = startRow;
      int maxRow = pic.length-1;
      int minCol = 0;
      int maxCol = startCol;
      int consecutive = 0;

      for(int i = maxCol; this.sumCol(i, minRow, maxRow) == 0 && i > 0; i--)
      {
          maxCol = i;
          //System.out.println("col:
      }
      
      consecutive = 0;
      for(int i = maxCol-1; consecutive < threshold && i > 0; i--)
      {
          if(this.sumCol(i, minRow, maxRow) == 0) {consecutive++;}
          minCol = i;
      }
      minCol += (threshold-1);
 
      for(int i = minRow; this.sumRow(i, minCol, maxCol) == 0 && i < pic.length-1; i++)
      {
          minRow = i;
      }
      
      consecutive = 0;
      for(int i = minRow+1; consecutive < threshold && i < pic.length-1; i++)
      {
          if(this.sumRow(i, minCol, maxCol) == 0) {consecutive++;}
          maxRow = i;
      }
      maxRow -= (threshold-1);
      
      System.out.println("rows: " + minRow + ", " +  maxRow + " cols: "  + minCol + ", " + maxCol);

      /*for(int i = minRow; i <= maxRow; i++)
      {
          for(int j = minCol; j <= maxCol; j++)
          {
              if(i == minRow || i == maxRow || j == minCol || j == maxCol)
              {
                  pic[i][j].setColor(Color.RED);
              }
          }
      }
      
      this.cleanDrop(minRow, maxRow, minCol, maxCol);
      
      int[] rowCol = {minRow, maxCol};
      return rowCol;*/
      
      if(maxCol-minCol < minSize)
      {
          return this.cleanRectangle(0, pic.length-1, 0, minCol, threshold, minSize);
      }
      
      return this.cleanRectangle(minRow, maxRow, minCol, maxCol, threshold, minSize);
  }
  
  private Drop cleanRectangle(int minRow, int maxRow, int minCol, int maxCol, int threshold, int minSize)
  {
      Pixel[][] pic = this.getPixels2D();
      int consecutive = 0;

      for(int i = maxCol; this.sumCol(i, minRow, maxRow) == 0 && i > 0; i--)
      {
          maxCol = i;
      }
      
      consecutive = 0;
      for(int i = maxCol-1; consecutive < threshold && i > 0; i--)
      {
          if(this.sumCol(i, minRow, maxRow) == 0) {consecutive++;}
          minCol = i;
      }
      minCol += (threshold-1);
 
      for(int i = minRow; this.sumRow(i, minCol, maxCol) == 0 && i < pic.length-1; i++)
      {
          minRow = i;
      }
      
      consecutive = 0;
      for(int i = minRow+1; consecutive < threshold && i < pic.length-1; i++)
      {
          if(this.sumRow(i, minCol, maxCol) == 0) {consecutive++;}
          maxRow = i;
      }
      maxRow -= (threshold-1);
      
      System.out.println("rows: " + minRow + ", " +  maxRow + " cols: "  + minCol + ", " + maxCol);
      
      
      if(maxCol-minCol < minSize)
      {
          return this.cleanRectangle(0, pic.length-1, 0, minCol, threshold, minSize);
      }
      
      for(int i = minRow; i <= maxRow; i++)
      {
          for(int j = minCol; j <= maxCol; j++)
          {
              if(i == minRow || i == maxRow || j == minCol || j == maxCol)
              {
                  pic[i][j].setColor(Color.RED);
              }
          }
      }
      
      this.cleanDrop(minRow, maxRow, minCol, maxCol);
      EllipseQuadratic_F32 ellipse = this.toOffsetEllipse(minRow, maxRow, minCol, maxCol, (minCol+maxCol)/2, (minRow+maxRow)/2);
      
      /*
      int[] rowCol = {minRow, maxCol};
      int[] zx = {(minCol+maxCol)/2, (minRow+maxRow)/2};
      int[] area = {(maxCol-minCol)*(maxRow-minRow)};
      
      int[][] rowColzxArea = {rowCol, zx, area};*/
      //System.out.println(ellipse);
      return new Drop(minRow, maxCol, (minCol+maxCol)/2, (minRow+maxRow)/2, (maxCol-minCol)*(maxRow-minRow), ellipse);
  }
  
  public static double[] fitParabola(int[] p)
  {
      PolynomialCurveFitter curve = PolynomialCurveFitter.create(2);
      
      List<WeightedObservedPoint> points = new ArrayList();
      
      for(int i = 0; i < p.length; i++)
      {
          points.add(new WeightedObservedPoint(1, i, (double)p[i]));
      }
      
      return curve.fit(points);
  }
  
  public static double[] fitSinusoid(double[] points)
  {
      HarmonicFitter h = new HarmonicFitter(new LevenbergMarquardtOptimizer());
      
      for(int i = 0; i < points.length; i++)
      {
          h.addObservedPoint(i, points[i]);
      }
      
      //System.out.println(h.fit());
      return h.fit();
  }
  
  private static double[][] fixAxisSwitching(double[] rmax, double[] rmin, double[] angle)
  {
      for(int i = 1; i < angle.length; i++)
      {
          if(angle[i] - angle[i-1] > 0.9)
          {
              angle[i] -= Math.PI/2;
              
              double temp = rmax[i];
              rmax[i] = rmin[i];
              rmin[i] = temp;
          }
          else if(angle[i] - angle[i-1] < -0.9)
          {
              angle[i] += Math.PI/2;
              
              double temp = rmax[i];
              rmax[i] = rmin[i];
              rmin[i] = temp;
          }
      }
      double[][] result = {rmax, rmin, angle};
      return result;
  }
    

  /* Main method for testing - each class in Java can have a main
   * method
   */
  public static void main(String[] args)
  {
    String location = "/Users/ChristopherWang/Desktop/CPS Comp Sci/Karel Stuff/KJRFiles/Unit 3/PictureLab/research/";
    String directory = "535/run5";
    Picture sub = new Picture(directory + "/frame000.jpg");
    Picture pic;
    
    int lastRow = sub.getPixels2D().length/3;
    int lastCol = sub.getPixels2D()[0].length-1;
    int[][] startFrames = {{0, 0, 0, 0, 0}, 
                           {0, 0, 0, 84, 0},
                           {65, 79, 72, 79, 69},
                           {66, 55, 43, 33, 67},
                           {62, 53, 45, 71, 73},
                           {69, 41, 36, 42, 50},
                           {42, 40, 31, 46, 35},
                           {54, 45, 66, 42, 67},
                           {52, 0, 69, 78, 70}};
    int[][] endFrames = {{0, 0, 0, 0, 0},
                         {0, 0, 0, 88, 0},
                         {77, 93, 84, 96, 75},
                         {80, 67, 67, 57, 97},
                         {86, 90, 65, 108, 113},
                         {114, 74, 74, 92, 76},
                         {78, 103, 87, 110, 91},
                         {76, 107, 137, 92, 131},
                         {68, 0, 109, 129, 146}};
                         
    int run = Integer.parseInt(directory.substring(7))-1;
    int distance = (Integer.parseInt(directory.substring(0, 3)) - 415) / 15;
    
    /*System.out.println(Integer.parseInt(directory.substring(0, 3)) - 415);
    System.out.println("run: " + run + " distance: " + distance);*/
    
    int edgeDetectionThreshold = 20;
    int actionThreshold = 70000;
    int gapThreshold = 5;
    int sizeThreshold = 7;
    int rowBacktrack = 10;
    int colBacktrack = 100;
    
    double pixelsPerCentimeter = 915.0/11;
    double pixelsAtZero = 223-pixelsPerCentimeter*4;
    double gConstant = 9.81;
    double dilationFactor = 90.0/95;
    
    
    String[][] csvMatrix = new String[201][36];
    csvMatrix[0][0] = "frame";
    csvMatrix[0][1] = "z pos (pixels)";
    csvMatrix[0][2] = "x pos (pixels)";
    csvMatrix[0][3] = "z velocity (pix/frame)";
    csvMatrix[0][4] = "x velocity (pix/frame)";
    csvMatrix[0][5] = "z accel (pix/frame^2)";
    csvMatrix[0][6] = "x accel (pix/frame^2)";
    csvMatrix[0][7] = "area of rectangle";
    csvMatrix[0][8] = "(blank space)";
    csvMatrix[0][9] = "rmax";
    csvMatrix[0][10] = "rmin";
    csvMatrix[0][11] = "z";
    csvMatrix[0][12] = "x";
    csvMatrix[0][13] = "theta";
    csvMatrix[0][14] = "fixed rmax";
    csvMatrix[0][15] = "fixed rmin";//"ellipse area";
    csvMatrix[0][16] = "fixed theta";
    csvMatrix[0][17] = "shifted rmax";
    csvMatrix[0][18] = "shifted rmin";
    csvMatrix[0][19] = "(blank space)";
    csvMatrix[0][20] = "time (s)";
    csvMatrix[0][21] = "z pos (m)";
    csvMatrix[0][22] = "x pos (m)";
    csvMatrix[0][23] = "z velocity (m/s)";
    csvMatrix[0][24] = "x velocity (m/s)";
    csvMatrix[0][25] = "z accel (m/s^2)";
    csvMatrix[0][26] = "x accel (m/s^2)";
    csvMatrix[0][27] = "ellipsoid volume (cm^3)";
    csvMatrix[0][28] = "(blank space)";
    csvMatrix[0][29] = "z pos (m, dilation)";
    csvMatrix[0][30] = "x pos (m, dilation)";
    csvMatrix[0][31] = "z velocity (m/s, dilation)";
    csvMatrix[0][32] = "x velocity (m/s, dilation)";
    csvMatrix[0][33] = "z accel (m/s^2, dilation)";
    csvMatrix[0][34] = "x accel (m/s^2, dilation)";
    csvMatrix[0][35] = "ellipsoid volume (cm^3, dilation)";
    
    
    double[] pointsMajor = new double[endFrames[distance][run] - startFrames[distance][run] + 1];
    double[] pointsMinor = new double[endFrames[distance][run] - startFrames[distance][run] + 1];
    double[] angle = new double[endFrames[distance][run] - startFrames[distance][run] + 1];
    
    int[] zCoords = new int[endFrames[distance][run] - startFrames[distance][run] + 1];

    for(int i = 1; i <= 200; i++)
    {
        if(i < 10)
        {
            pic = new Picture(directory + "/frame00" + i + ".jpg");
        }
        else if (i < 100)
        {
            pic = new Picture(directory + "/frame0" + i + ".jpg");
        }
        else
        {
            pic = new Picture(directory + "/frame" + i + ".jpg");
        }
        
        pic.subtract(sub);
        int action = 0;
        
        for(int c = 50; c < 100; c++)
        {action += pic.sumColor(c, 150, 350);}
        System.out.println("frame: " + i + ", action: " + action);
        
        if(action > actionThreshold || (i >= startFrames[distance][run]-3 && i <= endFrames[distance][run]+3 && endFrames[distance][run] != 0))
        {
            pic.edgeDetection(edgeDetectionThreshold);
            //pic.explore();
            
            if(i >= startFrames[distance][run] && i <= endFrames[distance][run])
            {
                lastRow = Math.max(lastRow-rowBacktrack, 0);
                lastCol = Math.min(lastCol+colBacktrack, sub.getPixels2D()[0].length-1);
                
                Drop d = pic.drawRectAroundDrop(lastRow, lastCol, gapThreshold, sizeThreshold);
                DropInfo info = d.getDropInfo();
                //System.out.println(foo.ellipse);
                
                lastRow = d.row;
                lastCol = d.col;
                //pic.explore();
                
                csvMatrix[i][0] = Integer.toString(i);
                csvMatrix[i][1] = Integer.toString(d.z);
                csvMatrix[i][2] = Integer.toString(d.x);
                csvMatrix[i][7] = Integer.toString(d.area);
                //csvMatrix[i][8] = "0" + d.ellipse.A + "*x^2 + 2*" + d.ellipse.B + "*x*y + " + d.ellipse.C + "*y^2 + 2*" + 
                                  //d.ellipse.D + "*x + 2*" + d.ellipse.E + "*y + " + d.ellipse.F + " = 0";
                csvMatrix[i][8] = "";
                csvMatrix[i][9] = Double.toString(info.rmax);
                csvMatrix[i][10] = Double.toString(info.rmin);
                csvMatrix[i][11] = Double.toString(info.z);
                csvMatrix[i][12] = Double.toString(info.x);
                csvMatrix[i][13] = Double.toString(info.angle);
                //csvMatrix[i][15] = Double.toString(info.area);
                csvMatrix[i][27] = Double.toString(info.volume / Math.pow(pixelsPerCentimeter, 3));
                
                pointsMajor[i-startFrames[distance][run]] = info.rmax;
                pointsMinor[i-startFrames[distance][run]] = info.rmin;
                angle[i-startFrames[distance][run]] = info.angle;
                zCoords[i-startFrames[distance][run]] = d.z;
               
                if(csvMatrix[i-1][1] != "N/A")
                {
                    csvMatrix[i][3] = Integer.toString(d.z - Integer.parseInt(csvMatrix[i-1][1]));
                    csvMatrix[i][4] = Integer.toString(d.x - Integer.parseInt(csvMatrix[i-1][2]));
                }
                else
                {   
                    csvMatrix[i][3] = "N/A";
                    csvMatrix[i][4] = "N/A";
                }
                if(csvMatrix[i-1][3] != "N/A")
                {
                    csvMatrix[i][5] = Integer.toString(Integer.parseInt(csvMatrix[i][3]) - Integer.parseInt(csvMatrix[i-1][3]));
                    csvMatrix[i][6] = Integer.toString(Integer.parseInt(csvMatrix[i][4]) - Integer.parseInt(csvMatrix[i-1][4]));
                }
                else
                {
                    csvMatrix[i][5] = "N/A";
                    csvMatrix[i][6] = "N/A";
                }
            }
            else
            {
                csvMatrix[i][0] = Integer.toString(i);
                csvMatrix[i][1] = "N/A";
                csvMatrix[i][2] = "N/A";
                csvMatrix[i][3] = "N/A";
                csvMatrix[i][4] = "N/A";
                csvMatrix[i][5] = "N/A";
                csvMatrix[i][6] = "N/A";
                csvMatrix[i][7] = "N/A";
            }
            if(i < 10)
            {pic.write(location + directory + "_processed/frame00" + i + ".jpg");}
            else if (i < 100)
            {pic.write(location + directory + "_processed/frame0" + i + ".jpg");}
            else
            {pic.write(location + directory + "_processed/frame" + i + ".jpg");}
        }
        else
        {
            csvMatrix[i][0] = Integer.toString(i);
            csvMatrix[i][1] = "N/A";
            csvMatrix[i][2] = "N/A";
            csvMatrix[i][3] = "N/A";
            csvMatrix[i][4] = "N/A";
            csvMatrix[i][5] = "N/A";
            csvMatrix[i][6] = "N/A";
            csvMatrix[i][7] = "N/A";
        }
    }
    
    if(endFrames[distance][run] - startFrames[distance][run] != 0)
    {
        double[][] fixedAxes = fixAxisSwitching(pointsMajor, pointsMinor, angle);
        for(int i = 0; i < fixedAxes.length; i++)
        {
            for(int j = 0; j < fixedAxes[i].length; j++)
            {
                csvMatrix[j+startFrames[distance][run]][14+i] = Double.toString(fixedAxes[i][j]);
            }
        }
        
        double[] shiftedMajorPoints = new double[fixedAxes[0].length]; 
        double[] shiftedMinorPoints = new double[fixedAxes[1].length];
        
        double majorAvg = DoubleStream.of(fixedAxes[0]).sum() / fixedAxes[0].length;
        double minorAvg = DoubleStream.of(fixedAxes[1]).sum() / fixedAxes[1].length;
        
        for(int i = 0; i < shiftedMajorPoints.length; i++)
        {
            shiftedMajorPoints[i] = fixedAxes[0][i] - majorAvg;
            shiftedMinorPoints[i] = fixedAxes[1][i] - minorAvg;
            
            csvMatrix[i+startFrames[distance][run]][17] = Double.toString(shiftedMajorPoints[i]);
            csvMatrix[i+startFrames[distance][run]][18] = Double.toString(shiftedMinorPoints[i]);
        }
        
        /*double[] infoMajor = fitSinusoid(shiftedMajorPoints);
        double[] infoMinor = fitSinusoid(shiftedMinorPoints);
        
        csvMatrix[endFrames[distance][run]+3][9] = "major axis:";
        csvMatrix[endFrames[distance][run]+4][9] = "minor axis:";
        csvMatrix[endFrames[distance][run]+2][10] = "amplitude";
        csvMatrix[endFrames[distance][run]+2][11] = "angular freq";
        csvMatrix[endFrames[distance][run]+2][12] = "phase";
        
        csvMatrix[endFrames[distance][run]+3][10] = Double.toString(infoMajor[0]);
        csvMatrix[endFrames[distance][run]+3][11] = Double.toString(infoMajor[1]);
        csvMatrix[endFrames[distance][run]+3][12] = Double.toString(infoMajor[2]);
        
        csvMatrix[endFrames[distance][run]+4][10] = Double.toString(infoMinor[0]);
        csvMatrix[endFrames[distance][run]+4][11] = Double.toString(infoMinor[1]);
        csvMatrix[endFrames[distance][run]+4][12] = Double.toString(infoMinor[2]);*/
        
        double[] parabola = fitParabola(zCoords);
        double fps = Math.sqrt(gConstant * 0.5 / Math.abs(parabola[2]) * pixelsPerCentimeter * 100);
        System.out.println("eq: " + parabola[0] + ", " + parabola[1] + ", " + parabola[2] + " fps: " + fps);
        
        csvMatrix[endFrames[distance][run]+2][14] = "estimated framerate:";
        csvMatrix[endFrames[distance][run]+2][15] = Double.toString(fps);
        
        double avgMass = 0.0;
        
        for(int i = startFrames[distance][run]; i <= endFrames[distance][run]; i++)
        {
            if(csvMatrix[i][1] != "N/A")
            {
                csvMatrix[i][21] = Double.toString((Double.parseDouble(csvMatrix[i][1]) - pixelsAtZero)/pixelsPerCentimeter/100 + pixelsPerCentimeter);
            }
            if(csvMatrix[i][2] != "N/A")
            {
                csvMatrix[i][22] = Double.toString(Double.parseDouble(csvMatrix[i][2])/pixelsPerCentimeter/100);
            }
                csvMatrix[i][20] = Double.toString(i / fps);
            if(csvMatrix[i][3] != "N/A")
            {
                csvMatrix[i][23] = Double.toString(Double.parseDouble(csvMatrix[i][3])/pixelsPerCentimeter/100 * fps);
                csvMatrix[i][24] = Double.toString(Double.parseDouble(csvMatrix[i][4])/pixelsPerCentimeter/100 * fps);
            }
            if(csvMatrix[i][5] != "N/A")
            {
                csvMatrix[i][25] = Double.toString(Double.parseDouble(csvMatrix[i][5])/pixelsPerCentimeter/100 * fps*fps);
                csvMatrix[i][26] = Double.toString(Double.parseDouble(csvMatrix[i][6])/pixelsPerCentimeter/100 * fps*fps);
            }
            if(csvMatrix[i][27] != "N/A")
            {
                avgMass += Double.parseDouble(csvMatrix[i][27]);
            }
        }
        
        for(int i = startFrames[distance][run]; i <= endFrames[distance][run]; i++)
        {
            for(int j = 29; j <= 35; j++)
            {
                //System.out.println("i: " + i + "j: " + j);
                if(csvMatrix[i][j-8] != null)
                {
                    //System.out.println(csvMatrix[i][j-8]);
                    csvMatrix[i][j] = Double.toString(Double.parseDouble(csvMatrix[i][j-8]) * dilationFactor);
                }
            }
        }
        
        
        
        avgMass /= endFrames[distance][run] - startFrames[distance][run] + 1;
        
        csvMatrix[endFrames[distance][run]+2][21] = "final KE:";
        csvMatrix[endFrames[distance][run]+2][22] =
            Double.toString(Math.pow(Double.parseDouble(csvMatrix[endFrames[distance][run]][22]), 2) + 
            Math.pow(Double.parseDouble(csvMatrix[endFrames[distance][run]][23]), 2) * 0.5 * avgMass / 1000);
            
        csvMatrix[endFrames[distance][run]+2][29] = "final KE (dilation):";
        csvMatrix[endFrames[distance][run]+2][30] =
            Double.toString(Math.pow(Double.parseDouble(csvMatrix[endFrames[distance][run]][22]), 2) + 
            Math.pow(Double.parseDouble(csvMatrix[endFrames[distance][run]][23]), 2) * 0.5 * avgMass / 1000 * Math.pow(dilationFactor, 3)); //volume is cubed
    }
    
    
    
    writeCsv(csvMatrix, "../csv/" + directory + ".csv");
  }

}
