/**
 * Mithila Wanasinghe
 * Keele University
 * 2012
 */
package smilepanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


public class SmilePanel extends JFrame {


    private final int PICTURE_PANEL_WIDTH = 460;
    private final int PICTURE_PANEL_HEIGHT = 340;
    private final int FACES_PANEL_WIDTH = 370;
    private final int FACE_PANEL_HEIGHT = 180;
    private final int FACE_SCAN_WINDOW = 30;
    
    final ImageIcon icon = new ImageIcon("wait.gif");

    //Displays the photo used
    class PhotoPanel extends JPanel{
        BufferedImage image;// = FacePixelTrace.readFile(fileName);
        @Override
        public void paintComponent(Graphics g){
            g.drawImage(image, 4, 15, 445, 315, null, null);
            
        }//end g
    }//end jpanel
    
    
    //Displays the faces cropped
    class FacePanel extends JPanel{
        
        BufferedImage face = null;
        BufferedImage [] faces = new BufferedImage[1];
        
        @Override
        public void paintComponent(Graphics g){
            
            g.setColor(Color.WHITE);
            g.fillRect(8, 15, (FACES_PANEL_WIDTH*4)-10, 336);
           
            // THIS WAS TO SHOW FACES IN 40x40 TO SHOW 16 FACES
            int x,y;
            x=10; y=15;
            
           for(int i=0;i<faces.length;i++){
                
                    face = faces[i];
                    g.drawImage(face, x, y, null);
                    x=x+170;
                    
            }//end i
            
        }//end g  
        
    }//end jpanel
    
    
    //Displaying the selected smiling faces
    class SmilingFacePanel extends JPanel{
           
        BufferedImage face = null;
        BufferedImage [] smilingFaces = new BufferedImage[1];
        
        @Override
        public void paintComponent(Graphics g){
           
            // THIS WAS TO SHOW FACES IN 40x40 TO SHOW 16 FACES
            int x,y,f;
            x=10; y=15; f=0;
            
            for(int i=0 ; i<smilingFaces.length ; i++){
                
                    face = smilingFaces[f++];
                   // g.drawImage(face, x, y, null);
                    g.drawImage(face, x, y, 50, 50, null, null);
                    x=x+70;
                
            }//end i
                  
        }//end g  
        
    }//end jpanel
    
     
 private JPanel upperBox;
 private JPanel middileBox;
 private JPanel lowerBox;
 
 private PhotoPanel colourImage;
 private FacePanel faceImage;
 private FacePanel hsvImage;
 private FacePanel hImage;
 private FacePanel skinImage;
 private FacePanel faceQuadrants;
 private SmilingFacePanel smilingFacesChosen;
 private JTextArea messageArea;
 private JButton runButton;
 
 
    public SmilePanel(){
        
        setTitle("Smile Recognition Control Panel - Mithila Wanasinghe - Keele University");
        setLayout(new BorderLayout());
        
        upperBox = new JPanel();
        upperBox.setPreferredSize(new Dimension(1300, 340));
        upperBox.setLayout(new BorderLayout());
        upperBox.setBorder(new TitledBorder(new EtchedBorder(), ""));
        add(upperBox, BorderLayout.NORTH);
        
        middileBox = new JPanel();
        middileBox.setPreferredSize(new Dimension(1300, 200));
        middileBox.setLayout(new BorderLayout());
        add(middileBox, BorderLayout.CENTER);
        
        lowerBox = new JPanel();
        lowerBox.setPreferredSize(new Dimension(1300, 150));
        lowerBox.setLayout(new BorderLayout());
        add(lowerBox, BorderLayout.PAGE_END);
                
        colourImage=new PhotoPanel();
        colourImage.setBorder(new TitledBorder(new EtchedBorder(), "RGB Image"));
        colourImage.setPreferredSize(new Dimension(PICTURE_PANEL_WIDTH, PICTURE_PANEL_HEIGHT));
        upperBox.add(colourImage, BorderLayout.WEST);
        
        faceImage = new FacePanel();
        faceImage.setBorder(new TitledBorder(new EtchedBorder(), "Detected Faces"));
        faceImage.setPreferredSize(new Dimension(FACES_PANEL_WIDTH*4, FACE_PANEL_HEIGHT));
        JScrollPane facesScrollPane = new JScrollPane(faceImage);
        facesScrollPane.setPreferredSize(new Dimension(FACES_PANEL_WIDTH+20, PICTURE_PANEL_HEIGHT));
        upperBox.add(facesScrollPane, BorderLayout.CENTER);
        
        hsvImage = new FacePanel();
        hsvImage.setBorder(new TitledBorder(new EtchedBorder(), "HSV Preview"));
        hsvImage.setPreferredSize(new Dimension(FACES_PANEL_WIDTH*4, FACE_PANEL_HEIGHT));
        JScrollPane hsvFacesScrollPane = new JScrollPane(hsvImage);
        hsvFacesScrollPane.setPreferredSize(new Dimension(FACES_PANEL_WIDTH+20, PICTURE_PANEL_HEIGHT));
        upperBox.add(hsvFacesScrollPane, BorderLayout.EAST);
        
        hImage = new FacePanel();
        hImage.setBorder(new TitledBorder(new EtchedBorder(), "Hue Values Preview"));
        hImage.setPreferredSize(new Dimension(FACES_PANEL_WIDTH*4, FACE_PANEL_HEIGHT));
        JScrollPane hFacesScrollPane = new JScrollPane(hImage);
        hFacesScrollPane.setPreferredSize(new Dimension(FACES_PANEL_WIDTH+20, FACE_PANEL_HEIGHT));
        middileBox.add(hFacesScrollPane, BorderLayout.EAST);
        
        skinImage = new FacePanel();
        skinImage.setBorder(new TitledBorder(new EtchedBorder(), "Skin Pixels Contrasted"));
        skinImage.setPreferredSize(new Dimension(FACES_PANEL_WIDTH*4, FACE_PANEL_HEIGHT));
        JScrollPane skinFacesScrollPane = new JScrollPane(skinImage);
        skinFacesScrollPane.setPreferredSize(new Dimension(FACES_PANEL_WIDTH+20, FACE_PANEL_HEIGHT));
        middileBox.add(skinFacesScrollPane, BorderLayout.CENTER);
        
        faceQuadrants = new FacePanel();
        faceQuadrants.setBorder(new TitledBorder(new EtchedBorder(), "Made Ready For ANN"));
        faceQuadrants.setPreferredSize(new Dimension(FACES_PANEL_WIDTH*4, FACE_PANEL_HEIGHT));
        JScrollPane quadrantFacesScrollPane = new JScrollPane(faceQuadrants);
        quadrantFacesScrollPane.setPreferredSize(new Dimension(PICTURE_PANEL_WIDTH, FACE_PANEL_HEIGHT));
        middileBox.add(quadrantFacesScrollPane, BorderLayout.WEST);
        
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setBackground(Color.white);
        JScrollPane textAreaScrollPane = new JScrollPane(messageArea);
        textAreaScrollPane.setBorder(new TitledBorder(new EtchedBorder(), "Inputs to Neural Network"));
        textAreaScrollPane.setPreferredSize(new Dimension(950, 200));
        lowerBox.add(textAreaScrollPane, BorderLayout.WEST);
        
        smilingFacesChosen = new SmilingFacePanel();
        smilingFacesChosen.setBorder(new TitledBorder(new EtchedBorder(), "Selected Smiling Faces"));
        smilingFacesChosen.setPreferredSize(new Dimension(70*3, 100));
        JScrollPane smileFacesScrollPane = new JScrollPane(smilingFacesChosen);
        smileFacesScrollPane.setPreferredSize(new Dimension(300, 200));
        lowerBox.add(smileFacesScrollPane, BorderLayout.CENTER);
        
        runButton = new JButton("RUN");
        runButton.setPreferredSize(new Dimension(100, 200));
        runButton.addActionListener(new RunButtonListener());
        lowerBox.add(runButton, BorderLayout.EAST);
        
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        pack();
        setVisible(true);
        
    }//end constructer
    
    public class RunButtonListener implements ActionListener{
        
        public void actionPerformed (ActionEvent e){
            
            faceImage.faces = new BufferedImage[1];
            hsvImage.faces = new BufferedImage[1];
            hImage.faces = new BufferedImage[1];
            skinImage.faces = new BufferedImage[1];
            faceQuadrants.faces = new BufferedImage[1];
            smilingFacesChosen.smilingFaces = new BufferedImage[1];
                        
            String fileName, file=null, files;
            
            String path = "C:/Users/Mithila/Pictures";
            File folder = new File (path);
            File [] listOfFiles = folder.listFiles();
            
            long time = listOfFiles[0].lastModified();
            
            for (int i=0; i<listOfFiles.length; i++){
                
                files = listOfFiles[i].getName();
                
                if(time<listOfFiles[i].lastModified()&&(files.endsWith(".jpg") || files.endsWith(".JPG"))){
                    file=files;
                    time = listOfFiles[i].lastModified();
                }
                
            }
            
            fileName = path+"/"+file;
          
            colourImage.image = FacePixelTrace.readFile(fileName);
            
            upperBox.repaint();
            middileBox.repaint();
            lowerBox.repaint();
            
            JOptionPane.showMessageDialog(rootPane, "The system is processing the file: "+file, "Please Wait", JOptionPane.INFORMATION_MESSAGE, icon);
            
            faceImage.faces = FacePixelTrace.cropFaces(fileName, FACE_SCAN_WINDOW);
            
            hsvImage.faces = FacePixelTrace.getHSVPreview(FacePixelTrace.cropFaces(fileName, FACE_SCAN_WINDOW));
            
            hImage.faces = FacePixelTrace.getHPreview(FacePixelTrace.cropFaces(fileName, FACE_SCAN_WINDOW));
            
            skinImage.faces = FacePixelTrace.getSkinPreview(FacePixelTrace.cropFaces(fileName, FACE_SCAN_WINDOW));
            
            faceQuadrants.faces = FacePixelTrace.getQuadrants(FacePixelTrace.cropFaces(fileName, FACE_SCAN_WINDOW));
            
            smilingFacesChosen.smilingFaces = FacePixelTrace.getSmilingFace(fileName, FACE_SCAN_WINDOW);
            
            String [] nnVals = FacePixelTrace.getNNValues(fileName, FACE_SCAN_WINDOW);
       /*      
            FaceProcess machine = new FaceProcess(fileName);
            
            colourImage.image = machine.readFile();
            
            faceImage.faces = machine.cropFaces();
            
            hsvImage.faces = machine.getHSVPreview();
            
            hImage.faces = machine.getHPreview();
            
            skinImage.faces = machine.getSkinPreview();
            
            faceQuadrants.faces = machine.getQuadrants();
            
            smilingFacesChosen.smilingFaces = machine.getSmilingFace();
            
            //float [] quadrantVals = machine.getQuadrantValues();
         */ 
            
            //Writing the walues in the test area
            messageArea.setText("Values of picture \""+fileName+"\"\n");
            
            for (int i=0; i<nnVals.length; i++){
                
                messageArea.append("\n"+"Values of face "+(i+1)+":  \n");
                messageArea.append(nnVals[i]+", ");
                
            }//end printing the values
            
            upperBox.repaint();
            middileBox.repaint();
            lowerBox.repaint();
            
        }//end method
        
    }//end listener

        
    public static void main(String[] args) {
       SmilePanel smileRecognitionPanel = new SmilePanel();
    }//end main
    
}//end class
