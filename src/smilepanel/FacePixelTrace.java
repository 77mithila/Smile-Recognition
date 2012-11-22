
package smilepanel;

import de.offis.faint.detection.plugins.opencv.OpenCVDetection;
import de.offis.faint.model.Region;
import foobar.App;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.neuroph.core.NeuralNetwork;

/**
 *
 * @author Dell
 */
public class FacePixelTrace {
    
    private static final int FACE_WIDTH = 160;
    private static final int FACE_HEIGHT = 160;
    public static final int QUDRANT_WIDTH = 40;
    public static final int QUDRANT_HEIGHT = 40;
    public static final int HUE_LOWER_LIMIT = 6;
    public static final int HUE_UPPER_LIMIT = 38;
           
    public static BufferedImage readFile(String fileName){
        
        BufferedImage snapShot = null;
        
        try {
            snapShot = ImageIO.read(new File(fileName));
        } catch (IOException ex) {
            Logger.getLogger(FacePixelTrace.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        return snapShot;
        
    }//end read picture
    
    
    public static BufferedImage [] cropFaces(String file, int size){
        
        BufferedImage face = null;
        
        Region[] ocvFaces = new OpenCVDetection().detectFaces(file, size);
        
        int arrayLength=1;
        
        try{
            arrayLength = ocvFaces.length;
        }catch(NullPointerException e){
           //return null;
        }
        
        BufferedImage [] faces = new BufferedImage[arrayLength];
        
        try{
           for(int i=0; i<ocvFaces.length; i++){

                    face = ocvFaces[i].toThumbnail(FACE_WIDTH, FACE_HEIGHT);
                    
                    faces[i] = face;
  
            }//end for
        }catch(NullPointerException e){
            //does nothing (i.e. no faces detected)
        }
        
        return faces;
        
    }//end crop faces
        
    
    public static BufferedImage [] getHSVPreview(BufferedImage[] rgbFaces){

       BufferedImage [] hsvFaces =  rgbFaces;//new BufferedImage[rgbFaces.length];
       
       int red, green, blue, hue, stat, val;
       
       int [] rgbs = new int [FACE_WIDTH*FACE_HEIGHT];
       int [] rgbHSV = new int [FACE_WIDTH*FACE_HEIGHT];
       float [] hsvVal = new float [3];
       
       for(int i=0; i<rgbFaces.length; i++){
           
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

            }//end inner for
            
            hsvFaces[i].setRGB(0, 0, FACE_WIDTH, FACE_HEIGHT, rgbHSV, 0, FACE_WIDTH);
           
       }//end outer for
        
       return hsvFaces;
        
    }//end hsv face
    
    
    public static BufferedImage [] getHPreview(BufferedImage[] rgbFaces){

       BufferedImage [] hFaces =  rgbFaces;//new BufferedImage[rgbFaces.length];
       
       int red, green, blue, hue;
       
       int [] rgbs = new int [FACE_WIDTH*FACE_HEIGHT];
       int [] rgbH = new int [FACE_WIDTH*FACE_HEIGHT];
       float [] hsvVal = new float [3];
       
       for(int i=0; i<rgbFaces.length; i++){
           
            rgbFaces[i].getRGB(0, 0, FACE_WIDTH, FACE_HEIGHT, rgbs, 0, FACE_WIDTH);
       
            for(int j=0; j<rgbs.length; j++){
            
                red   = (rgbs[j] & 0x00ff0000) >> 16;
                green = (rgbs[j] & 0x0000ff00) >> 8;
                blue  =  rgbs[j] & 0x000000ff; 

                Color.RGBtoHSB(red, green, blue, hsvVal);

                hue = (int)(hsvVal[0]*255);

                rgbH[j]=(hue<<16);

            }//end inner for
            
            hFaces[i].setRGB(0, 0, FACE_WIDTH, FACE_HEIGHT, rgbH, 0, FACE_WIDTH);
           
       }//end outer for
        
       return hFaces;
        
    }//end hue face
    
    
    public static BufferedImage [] getSkinPreview(BufferedImage[] rgbFaces){

       BufferedImage [] skinFaces =  rgbFaces;//new BufferedImage[rgbFaces.length];
       
       int red, green, blue, hue;
       
       int [] rgbs = new int [FACE_WIDTH*FACE_HEIGHT];
       int [] rgbH = new int [FACE_WIDTH*FACE_HEIGHT];
       int [] rgbBW = new int [FACE_WIDTH*FACE_HEIGHT];
       float [] hsvVal = new float [3];
       
       for(int i=0; i<rgbFaces.length; i++){
           
            rgbFaces[i].getRGB(0, 0, FACE_WIDTH, FACE_HEIGHT, rgbs, 0, FACE_WIDTH);
       
            for(int j=0; j<rgbs.length; j++){
            
                red   = (rgbs[j] & 0x00ff0000) >> 16;
                green = (rgbs[j] & 0x0000ff00) >> 8;
                blue  =  rgbs[j] & 0x000000ff; 

                Color.RGBtoHSB(red, green, blue, hsvVal);

                hue = (int)(hsvVal[0]*255);

                rgbH[j]=(hue<<16);
                
                if (hue>HUE_LOWER_LIMIT&&hue<HUE_UPPER_LIMIT){ rgbBW[j]=(250<<16)|(250<<8)|250;}else{rgbBW[j]=0;}

            }//end inner for
            
            skinFaces[i].setRGB(0, 0, FACE_WIDTH, FACE_HEIGHT, rgbBW, 0, FACE_WIDTH);
           
       }//end outer for
        
       return skinFaces;
        
    }//end skin face
    
    
    public static BufferedImage [] getQuadrants(BufferedImage[] rgbFaces){

       BufferedImage [] skinFaces =  rgbFaces;//new BufferedImage[rgbFaces.length];
       
       int red, green, blue, hue, qCount=0;
       
       int [] rgbs = new int [FACE_WIDTH*FACE_HEIGHT];
       int [] rgbH = new int [FACE_WIDTH*FACE_HEIGHT];
       int [] rgbBW = new int [FACE_WIDTH*FACE_HEIGHT];
       float [] hsvVal = new float [3];
       
       for(int i=0; i<rgbFaces.length; i++){
           
            rgbFaces[i].getRGB(0, 0, FACE_WIDTH, FACE_HEIGHT, rgbs, 0, FACE_WIDTH);
       
            for(int j=0; j<rgbs.length; j++){
            
                red   = (rgbs[j] & 0x00ff0000) >> 16;
                green = (rgbs[j] & 0x0000ff00) >> 8;
                blue  =  rgbs[j] & 0x000000ff; 

                Color.RGBtoHSB(red, green, blue, hsvVal);

                hue = (int)(hsvVal[0]*255);

                rgbH[j]=(hue<<16);
                
                if (hue>HUE_LOWER_LIMIT&&hue<HUE_UPPER_LIMIT){ rgbBW[j]=(250<<16)|(250<<8)|250;}else{rgbBW[j]=0;}

            }//end inner for
            
            skinFaces[i].setRGB(0, 0, FACE_WIDTH, FACE_HEIGHT, rgbBW, 0, FACE_WIDTH);
            
            Graphics2D g2d = skinFaces[i].createGraphics();
            g2d.setColor(Color.red);
            
            for (int y=0; y<=FACE_HEIGHT; y=y+QUDRANT_HEIGHT){  

                g2d.fill3DRect(0, y, FACE_WIDTH, 2, false);

            }
            for (int x=0; x<=FACE_WIDTH; x=x+QUDRANT_WIDTH){

                g2d.fill3DRect(x, 0, 2, FACE_HEIGHT, false);

            }
            
            
       }//end outer for
        
       return skinFaces;
        
    }//end face quadrants
    
    
  public static String [] getNNValues(String file, int size){
        
        double [][] openImajFaces = App.faceKeypoints(file);
        
        BufferedImage face = null;
        
        String smiling;
        
        String annPath = "7_4_1.nnet";
        
        NeuralNetwork nn = NeuralNetwork.load(annPath);
       
        Region[] ocvFaces = new OpenCVDetection().detectFaces(file, size);
        
        BufferedImage [] faces = new BufferedImage[ocvFaces.length];
        
        String [] nnVals = new String[ocvFaces.length];
        
        try{
           for(int i=0; i<ocvFaces.length; i++){

                    face = ocvFaces[i].toThumbnail(FACE_WIDTH, FACE_HEIGHT);
                    
                    faces[i] = face;
  
            }//end for
        }catch(NullPointerException e){
            //does nothing (i.e. no faces detected)
        }//end try-catch

       BufferedImage [] skinFaces =  faces;//new BufferedImage[faces.length];
                     
       BufferedImage quadrant = new BufferedImage(QUDRANT_WIDTH, QUDRANT_HEIGHT, BufferedImage.TYPE_INT_RGB);
       
       float [] quadrantValues = new float [(faces.length)*16];
              
       double[] networkOutput = new double[1];
       
       int red, green, blue, hue, qCount, blackCount, smileCount=0;
       
       double  inputA=0, inputB=0, inputC=0, inputD=0, inputE=0, inputF=0, inputG=0;
       
       int [] rgbs = new int [FACE_WIDTH*FACE_HEIGHT];
       int [] quadrantRGB = new int [QUDRANT_WIDTH*QUDRANT_HEIGHT];
       int [] rgbH = new int [FACE_WIDTH*FACE_HEIGHT];
       int [] rgbBW = new int [FACE_WIDTH*FACE_HEIGHT];
       float [] hsvVal = new float [3];
       
       for(int i=0; i<faces.length; i++){
            smiling = "Not Smiling";
            faces[i].getRGB(0, 0, FACE_WIDTH, FACE_HEIGHT, rgbs, 0, FACE_WIDTH);
       
            for(int j=0; j<rgbs.length; j++){
            
                red   = (rgbs[j] & 0x00ff0000) >> 16;
                green = (rgbs[j] & 0x0000ff00) >> 8;
                blue  =  rgbs[j] & 0x000000ff; 

                Color.RGBtoHSB(red, green, blue, hsvVal);

                hue = (int)(hsvVal[0]*255);

                rgbH[j]=(hue<<16);
                
                if (hue>HUE_LOWER_LIMIT&&hue<HUE_UPPER_LIMIT){ rgbBW[j]=(250<<16)|(250<<8)|250;}else{rgbBW[j]=0;}

            }//end one face for
            
            skinFaces[i].setRGB(0, 0, FACE_WIDTH, FACE_HEIGHT, rgbBW, 0, FACE_WIDTH);
            
            qCount=0;
            
            for (int y=0; y<FACE_HEIGHT; y=y+QUDRANT_HEIGHT){  
                
                for (int x=0; x<FACE_WIDTH; x=x+QUDRANT_WIDTH){

                    quadrant = skinFaces[i].getSubimage(x, y, QUDRANT_WIDTH, QUDRANT_HEIGHT);
                    
                    quadrant.getRGB(0, 0, QUDRANT_WIDTH, QUDRANT_HEIGHT, quadrantRGB, 0, QUDRANT_WIDTH);
                    
                    blackCount=0;
                    
                    for (int k = 0; k<quadrantRGB.length; k++){
                                                
                        red   = (quadrantRGB[k] & 0x00ff0000) >> 16;
                        
                        if (red==0) blackCount++;
                        
                    }//end for
                    
                    quadrantValues[qCount++]=(float)((QUDRANT_WIDTH*QUDRANT_HEIGHT)-blackCount)/(float)(QUDRANT_WIDTH*QUDRANT_HEIGHT);
                                                      
                }//end sub image colomn

            }//end sub image row
            
            nn.reset();
            try{
                inputA = quadrantValues[9];
                inputB = quadrantValues[10];
                inputC = quadrantValues[13];
                inputD = quadrantValues[14];
                inputE = openImajFaces[i][1]/openImajFaces[i][0];
                inputF = openImajFaces[i][2]/openImajFaces[i][0];
                inputG = openImajFaces[i][3]/openImajFaces[i][0];
            }catch(ArrayIndexOutOfBoundsException e){
                
            }
            
            nn.setInput(inputA, inputB, inputC, inputD, inputE, inputF, inputG);
            
            nn.calculate();
            
            networkOutput = nn.getOutput();
            
            if(0.55<networkOutput[0]&&1>networkOutput[0]) smiling = "smiling";
            
            nnVals [i] = " Neural Network inputs: "+inputA+", "+inputB+", "+inputC+", "+inputD+" AND "+inputE+", "+inputF+", "+inputG+"\n Neural Network output: "+networkOutput[0]+"\n This face is "+smiling;
            
       }//end faces array for
        
       return nnVals;
        
    }//end qudrant values
    
    
    //==========================================================================
    //THE MAIN SEQUENCE OF PROCESS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    
    public static BufferedImage[] getSmilingFace(String file, int size){
        
        double [][] openImajFaces = App.faceKeypoints(file);
        
        BufferedImage face = null;
        
        NeuralNetwork nn = NeuralNetwork.load("7_4_1.nnet");
       
        Region[] ocvFaces = new OpenCVDetection().detectFaces(file, size);
        
        BufferedImage [] faces = new BufferedImage[ocvFaces.length];
        
        try{
           for(int i=0; i<ocvFaces.length; i++){

                    face = ocvFaces[i].toThumbnail(FACE_WIDTH, FACE_HEIGHT);
                    
                    faces[i] = face;
  
            }//end for
        }catch(NullPointerException e){
            //does nothing (i.e. no faces detected)
        }

       BufferedImage [] skinFaces =  faces;//new BufferedImage[faces.length];
       
       BufferedImage [] outputFace =  faces;
       
       BufferedImage [] smilingFaces = new BufferedImage[faces.length];
              
       BufferedImage quadrant = new BufferedImage(QUDRANT_WIDTH, QUDRANT_HEIGHT, BufferedImage.TYPE_INT_RGB);
       
       float [] quadrantValues = new float [(faces.length)*16];
              
       double[] networkOutput = new double[1];
       
       int red, green, blue, hue, qCount, blackCount, smileCount=0;
       
       double  inputA=0, inputB=0, inputC=0, inputD=0, inputE=0, inputF=0, inputG=0;
       
       int [] rgbs = new int [FACE_WIDTH*FACE_HEIGHT];
       int [] quadrantRGB = new int [QUDRANT_WIDTH*QUDRANT_HEIGHT];
       int [] rgbBW = new int [FACE_WIDTH*FACE_HEIGHT];
       float [] hsvVal = new float [3];
       
       for(int i=0; i<faces.length; i++){
           
            faces[i].getRGB(0, 0, FACE_WIDTH, FACE_HEIGHT, rgbs, 0, FACE_WIDTH);
       
            for(int j=0; j<rgbs.length; j++){
            
                red   = (rgbs[j] & 0x00ff0000) >> 16;
                green = (rgbs[j] & 0x0000ff00) >> 8;
                blue  =  rgbs[j] & 0x000000ff; 

                Color.RGBtoHSB(red, green, blue, hsvVal);

                hue = (int)(hsvVal[0]*255);
     
                if (hue>HUE_LOWER_LIMIT&&hue<HUE_UPPER_LIMIT){rgbBW[j]=(250<<16)|(250<<8)|250;}else{rgbBW[j]=0;}

            }//end one face for
            
            skinFaces[i].setRGB(0, 0, FACE_WIDTH, FACE_HEIGHT, rgbBW, 0, FACE_WIDTH);
            
            qCount=0;
            
            for (int y=0; y<FACE_HEIGHT; y=y+QUDRANT_HEIGHT){  
                
                for (int x=0; x<FACE_WIDTH; x=x+QUDRANT_WIDTH){

                    quadrant = skinFaces[i].getSubimage(x, y, QUDRANT_WIDTH, QUDRANT_HEIGHT);
                    
                    quadrant.getRGB(0, 0, QUDRANT_WIDTH, QUDRANT_HEIGHT, quadrantRGB, 0, QUDRANT_WIDTH);
                    
                    blackCount=0;
                    
                    for (int k = 0; k<quadrantRGB.length; k++){
                                                
                        red   = (quadrantRGB[k] & 0x00ff0000) >> 16;
                        
                        if (red==0) blackCount++;
                        
                    }//end for
                    
                    quadrantValues[qCount++]=(float)((QUDRANT_WIDTH*QUDRANT_HEIGHT)-blackCount)/(float)(QUDRANT_WIDTH*QUDRANT_HEIGHT);
                                                      
                }//end sub image colomn

            }//end sub image row
            
            faces[i].setRGB(0, 0, FACE_WIDTH, FACE_HEIGHT, rgbs, 0, FACE_WIDTH);
            
            nn.reset();
            try{
                inputA = quadrantValues[9];
                inputB = quadrantValues[10];
                inputC = quadrantValues[13];
                inputD = quadrantValues[14];
                inputE = openImajFaces[i][1]/openImajFaces[i][0];
                inputF = openImajFaces[i][2]/openImajFaces[i][0];
                inputG = openImajFaces[i][3]/openImajFaces[i][0];
            }catch(ArrayIndexOutOfBoundsException e){
                
            }
            
            nn.setInput(inputA, inputB, inputC, inputD, inputE, inputF, inputG);
            
            nn.calculate();
            
            networkOutput = nn.getOutput();
            
            if(0.55<networkOutput[0]&&1>networkOutput[0]) smilingFaces[smileCount++] = outputFace[i];
            
            System.out.println("Neural Network inputs: "+inputA+", "+inputB+", "+inputC+", "+inputD+", "+inputE+", "+inputF+", "+inputG);
            System.out.println("Neural Network output: "+networkOutput[0]);
            
       }//end faces array for
               
       return smilingFaces;
        
    }//end smiling faces
    
    
}//end class

