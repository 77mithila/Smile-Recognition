/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smilepanel;

import de.offis.faint.detection.plugins.opencv.OpenCVDetection;
import de.offis.faint.model.Region;
import foobar.App;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.neuroph.core.NeuralNetwork;

/**
 *
 * @author Mithila
 */
public class FaceProcess {
    
    private static final int FACE_WIDTH = 160;
    private static final int FACE_HEIGHT = 160;
    public static final int QUDRANT_WIDTH = 40;
    public static final int QUDRANT_HEIGHT = 40;
    
    BufferedImage snapShot = null;

    BufferedImage face = null;

    BufferedImage [] rgbFaces = new BufferedImage[5];

    BufferedImage [] hsvFaces =  rgbFaces;//new BufferedImage[rgbFaces.length];

    BufferedImage [] hFaces =  rgbFaces;

    BufferedImage [] skinFaces =  rgbFaces;

    BufferedImage [] skinQuadrants =  new BufferedImage[(rgbFaces.length)*16];

    BufferedImage [] outputFace =  rgbFaces;

    BufferedImage [] smilingFaces = new BufferedImage[rgbFaces.length];

    double  inputA=0, inputB=0, inputC=0, inputD=0, inputE=0, inputF=0, inputG=0;

    double[] networkOutput = new double[1];

    float [] quadrantValues = new float [(rgbFaces.length)*16];

    int [] rgbs = new int [FACE_WIDTH*FACE_HEIGHT];

    int [] rgbHSV = new int [FACE_WIDTH*FACE_HEIGHT];

    int [] rgbH = new int [FACE_WIDTH*FACE_HEIGHT];

    float [] hsvVal = new float [3];

    int [] rgbBW = new int [FACE_WIDTH*FACE_HEIGHT];

    int [] quadrantRGB = new int [QUDRANT_WIDTH*QUDRANT_HEIGHT];

    public FaceProcess(String fileName){
        
        Region[] ocvFaces = new OpenCVDetection().detectFaces(fileName, 50);
        
        int red, green, blue, hue, stat, val, qFaceCount=0, qValCount=0, blackCount, smileCount=0;
        
        double [][] openImajfaces = App.faceKeypoints(fileName);
        
        NeuralNetwork nn = NeuralNetwork.load("7_4_1.nnet");
        
        try {
            snapShot = ImageIO.read(new File(fileName));
        } catch (IOException ex) {
            Logger.getLogger(FaceProcess.class.getName()).log(Level.SEVERE, null, ex);
        }

        try{
           for(int i=0; i<ocvFaces.length; i++){

                    face = ocvFaces[i].toThumbnail(FACE_WIDTH, FACE_HEIGHT);

                    rgbFaces[i] = face;

            }//end for
        }catch(NullPointerException e){
            //does nothing (i.e. no faces detected)
        }
        
        for(int i=0; i<ocvFaces.length; i++){
           
            rgbFaces[i].getRGB(0, 0, FACE_WIDTH, FACE_HEIGHT, rgbs, 0, FACE_WIDTH);
       
            for(int j=0; j<rgbs.length; j++){
            
                red   = (rgbs[j] & 0x00ff0000) >> 16;
                green = (rgbs[j] & 0x0000ff00) >> 8;
                blue  =  rgbs[j] & 0x000000ff; 

                Color.RGBtoHSB(red, green, blue, hsvVal);

                hue = (int)(hsvVal[0]*255);
                stat = (int)(hsvVal[1]*255);
                val = (int)(hsvVal[2]*255);

                rgbHSV[j]=(hue<<16)|(stat<<8)|val;
                
                rgbH[j]=(hue<<16);
                
                if (hue>6&&hue<30){ rgbBW[j]=(250<<16)|(250<<8)|250;}else{rgbBW[j]=0;}

            }//end inner for
            
            System.out.print("rgbHSV: ");
            for(int k=0 ; k<rgbHSV.length ; k++){System.out.print(rgbHSV[k]+", ");}
            System.out.println(": end");
            System.out.print("rgbH: ");
            for(int k=0 ; k<rgbH.length ; k++){System.out.print(rgbH[k]+", ");}
            System.out.println(": end");
            System.out.print("rgbBW: ");
            for(int k=0 ; k<rgbBW.length ; k++){System.out.print(rgbBW[k]+", ");}
            System.out.println(": end");
            
            hsvFaces[i].setRGB(0, 0, FACE_WIDTH, FACE_HEIGHT, rgbHSV, 0, FACE_WIDTH);
                    
            hFaces[i].setRGB(0, 0, FACE_WIDTH, FACE_HEIGHT, rgbH, 0, FACE_WIDTH);
                        
            skinFaces[i].setRGB(0, 0, FACE_WIDTH, FACE_HEIGHT, rgbBW, 0, FACE_WIDTH);
            
            
            
            for (int y=0; y<FACE_HEIGHT; y=y+40){  
                
                for (int x=0; x<FACE_WIDTH; x=x+40){
                    
                    skinQuadrants [qFaceCount] = skinFaces[i].getSubimage(x, y, QUDRANT_WIDTH, QUDRANT_HEIGHT);
                    
                    skinQuadrants [qFaceCount++].getRGB(0, 0, QUDRANT_WIDTH, QUDRANT_HEIGHT, quadrantRGB, 0, QUDRANT_WIDTH);
                    
                    blackCount=0;
                    
                    for (int k = 0; k<quadrantRGB.length; k++){
                                                
                        red   = (quadrantRGB[k] & 0x00ff0000) >> 16;
                        
                        if (red==0) blackCount++;
                        
                    }//end for
                    
                    quadrantValues[qValCount++]=(float)((QUDRANT_WIDTH*QUDRANT_HEIGHT)-blackCount)/(float)(QUDRANT_WIDTH*QUDRANT_HEIGHT);
                                             
                }//end sub image row

            }//end sub image column
             
            nn.reset();
            try{
                inputA = quadrantValues[9];
                inputB = quadrantValues[10];
                inputC = quadrantValues[13];
                inputD = quadrantValues[14];
                inputE = openImajfaces[i][1]/openImajfaces[i][0];
                inputF = openImajfaces[i][2]/openImajfaces[i][0];
                inputG = openImajfaces[i][3]/openImajfaces[i][0];
            }catch(ArrayIndexOutOfBoundsException e){
                
            }
            
            nn.setInput(inputA, inputB, inputC, inputD, inputE, inputF, inputG);
            
            nn.calculate();
            
            networkOutput = nn.getOutput();
            
            if(0.5<networkOutput[0]&&1>networkOutput[0]) smilingFaces[smileCount++] = outputFace[i];
            
            System.out.println("Neural Network imputs: "+inputA+", "+inputB+", "+inputC+", "+inputD+", "+inputE+", "+inputF+", "+inputG);
            System.out.println("Neural Network output: "+networkOutput[0]);
                         
       }//end outer for
        
        
    }
    
     public BufferedImage readFile(){
         
         return snapShot;
         
     }
     
     public BufferedImage [] cropFaces(){
         
         return rgbFaces;
         
     }
     
     public BufferedImage [] getHSVPreview(){
         
         return hsvFaces;
         
     }
     
     public BufferedImage [] getHPreview(){
         
         return hFaces;
         
     }
     
     public BufferedImage [] getSkinPreview(){
         
         return skinFaces;
         
     }
     
     public BufferedImage [] getQuadrants(){
         
         return skinQuadrants;
         
     }
     
      public float [] getQuadrantValues(){
          
          return quadrantValues;
          
      }
      
       public BufferedImage[] getSmilingFace(){
           
           return smilingFaces;
           
       }
     
    
    public static void main (String [] args){
        
        FaceProcess faces = new FaceProcess("test.jpg");
        
    }
    
}
