/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seamcarver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author Uzair
 */
public class SeamCarver {

    private Picture picture;
    boolean verticalSeamFound;
    double energyMatrix[][];
    int[][] vpaths;
    
    public SeamCarver(Picture picture)
    {
        this.picture = picture;
    }
    
    public Picture picture()
    {
        return this.picture;
    }
    
    public int width()
    {
        return picture.width();
    }
    
    public int height()
    {
        return picture.height();
    }
    
    public void energyMatrix()
    {
        energyMatrix = new double[picture.width()][picture.height()];
        for(int x = 0; x < picture.width(); x++)
            for(int y = 0; y < picture.height();y++)
            {
                double x_gradient = Math.pow(picture.get((x+1)%picture.width(), y).getRed()-picture.get(Math.floorMod(x-1, picture.width()), y).getRed(),2) +
                Math.pow(picture.get((x+1)%picture.width(), y).getBlue()-picture.get(Math.floorMod(x-1, picture.width()), y).getBlue(),2) +
                Math.pow(picture.get((x+1)%picture.width(), y).getGreen()-picture.get(Math.floorMod(x-1, picture.width()), y).getGreen(),2);
        
                double y_gradient = Math.pow(picture.get(x, (y+1)%picture.height()).getRed()-picture.get(x, Math.floorMod(y-1, picture.height())).getRed(),2) +
                Math.pow(picture.get(x, (y+1)%picture.height()).getBlue()-picture.get(x, Math.floorMod(y-1, picture.height())).getBlue(),2) +
                Math.pow(picture.get(x, (y+1)%picture.height()).getGreen()-picture.get(x, Math.floorMod(y-1, picture.height())).getGreen(),2);
        
                //System.out.println(x_gradient + " " + y_gradient + " " + (x_gradient+y_gradient));
                energyMatrix[x][y] = Math.sqrt(y_gradient+x_gradient);
            }
    }
    
    public double energy(int x, int y)
    {
        return energyMatrix[x][y];
    }
    
    public int[] findHorizontalSeam()
    {
        this.picture = this.transpose();
        //this.energyMatrix();
        int[] seam = findVerticalSeam();
        picture = this.transpose();
        return seam;
    }
    
   
    
    public int[] findVerticalSeam()
    {
        energyMatrix();
        this.dp();
        double min = Double.MAX_VALUE;
        int minIndex = 0;
        for(int i = 0;i  < picture.width(); i++)
        {
            if(energyMatrix[i][0] < min)
            {
                minIndex = i;
                min = energyMatrix[i][0];
            }
        }
        
        int[] seam = new int[picture.height()];
       
        seam[0] = minIndex;
        for(int i = 1; i < picture.height(); i++)
        {
            seam[i] = vpaths[seam[i-1]][i-1];
        }
        // System.out.println(Arrays.toString(seam)+ " " + energyMatrix[minIndex][0]);
        //verticalSeamFound = true;
        
        return seam;

        
    }
    
   public void dp()
   {
       vpaths = new int[picture.width()][picture.height()];
       for(int y = picture.height()-2; y >= 0; y--)
           for(int x = 0; x < picture.width();x++)
           {
               int index = min(x,y);
               vpaths[x][y] = index;
               energyMatrix[x][y] += energyMatrix[index][y+1]; 
           }
   }
   
   public int min(int x, int y)
   {
       double left = x == 0? Double.MAX_VALUE:energyMatrix[x-1][y+1];
       double mid = energyMatrix[x][y+1];
       double right = x == picture.width()-1?Double.MAX_VALUE:energyMatrix[x+1][y+1];
       if(left < mid)
           if(left < right)
               return x-1;
           else
               return x+1;
       else if(mid < right)
           return x;
       else
           return x+1;
   }
   
    public void removeHorizontalSeam(int[] seam)
    {
        picture = this.transpose();
        this.energyMatrix();
        this.picture = new Picture(picture,seam);
        picture = this.transpose();
    }
    public Picture transpose()
    {
        Picture pic = new Picture(picture.height(),picture.width());
        for (int col = 0; col < pic.width(); col++)
            for (int row = 0; row < pic.height(); row++)
                pic.image.setRGB(col, row, picture.get(row, col).getRGB());
        //pic.show();
        return pic;
    }
    public void removeVerticalSeam(int[] seam)
    {   
        this.picture = new Picture(picture,seam);
    }
    
    public static void main(String[] args) {
        Picture picture = new Picture("HJoceanSmall.png");
        System.out.printf("%d-by-%d\n", picture.width(), picture.height());
        //Scanner scan = new Scanner(System.in);
        SeamCarver obj = new SeamCarver(picture);
        
        
        //TESTING
        int VERTICAL_SEAM_REMOVE_COUNT = 200;
        int HORIZONTAL_SEAM_REMOVE_COUNT = 150;
        
        obj.picture.show();
        int i = 0;
        while(i <VERTICAL_SEAM_REMOVE_COUNT || i < HORIZONTAL_SEAM_REMOVE_COUNT)
        {
            
        //System.out.println(obj.energy(0, 0));
        
        //picture.show();
            if(i < VERTICAL_SEAM_REMOVE_COUNT)
            {
                int[] vseam = obj.findVerticalSeam();
                
                //System.out.println(obj.energyMatrix[vseam[0]][0] + " " + Arrays.toString(vseam));
                obj.removeVerticalSeam(vseam);
            }
            
            if(i < HORIZONTAL_SEAM_REMOVE_COUNT)
            {
                int[] hseam = obj.findHorizontalSeam();
                obj.removeHorizontalSeam(hseam);
            }
            obj.picture.show();
            i++;
        }
    }
    
}
