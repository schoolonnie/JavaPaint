package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SaveManager {
	// Opens a file selector for output path, then saves canvas as an image in the output path
	// Takes parentCanvas for displaying dialog onto and imageToSave as the image to save
	public static void saveImageAtPath(Canvas parentCanvas, BufferedImage imageToSave) {
		if (imageToSave == null) {
            System.err.println("No canvas image data found to save.");
            return;
        }
		
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Save Canvas Image");
		
		// Limit the user to common image types
        FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("PNG Image (*.png)", "png");
        FileNameExtensionFilter jpgFilter = new FileNameExtensionFilter("JPEG Image (*.jpg, *.jpeg)", "jpg", "jpeg");
        
        fileChooser.addChoosableFileFilter(pngFilter);
        fileChooser.addChoosableFileFilter(jpgFilter);
        fileChooser.setFileFilter(pngFilter); // Set PNG as default choice
        fileChooser.setAcceptAllFileFilterUsed(false); // Disable "All Files" to prevent errors
        
        // Show the save dialog
        int userSelection = fileChooser.showSaveDialog(parentCanvas);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
        	// Get active file and extension
        	File fileToSave = fileChooser.getSelectedFile();
        	FileNameExtensionFilter activeFilter = (FileNameExtensionFilter) fileChooser.getFileFilter();
        	
        	// Set format based on data from above
        	String format = activeFilter.getExtensions()[0];
        	String filePath = fileToSave.getAbsolutePath();
        	
        	// Append extension if user omitted it
        	if (!filePath.toLowerCase().endsWith("." + format)) {
        		if (format.equals("jpg") && filePath.toLowerCase().endsWith(".jpeg")) {
                    // Do nothing, safe variation
                } else {
                    fileToSave = new File(filePath + "." + format);
                }
        	}
        	
        	// Save the file
        	try {
                BufferedImage imageToWrite = imageToSave;
                String writerFormat = format;
                // Special handling for JPEG to avoid alpha channel issues
                if (format.equals("jpg")) {
                    imageToWrite = new BufferedImage(imageToSave.getWidth(), imageToSave.getHeight(), BufferedImage.TYPE_INT_RGB);
                    Graphics2D graphics = imageToWrite.createGraphics();
                    graphics.setColor(Color.WHITE);
                    graphics.fillRect(0, 0, imageToWrite.getWidth(), imageToWrite.getHeight());
                    graphics.drawImage(imageToSave, 0, 0, null);
                    graphics.dispose();
                    writerFormat = "jpeg";
                }
                boolean success = ImageIO.write(imageToWrite, writerFormat, fileToSave);
        		if (success) {
                    System.out.println("Saved canvas to: " + fileToSave.getAbsolutePath());
                } else {
                    System.err.println("Failed to export image format.");
                }
            } catch (IOException e) {
                System.err.println("Error saving image: " + e.getMessage());
        	}
        }
	}
	// Opens a file selector to select a file to load as the canvas image
	// Takes parentCanvas for displaying dialog onto and a cached image to revert to in case the load fails
	public static BufferedImage loadImageFromPath(Canvas parentCanvas, BufferedImage cachedImage) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Load Canvas Image");

        // Limit the user to common image types
        FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("PNG Image (*.png)", "png");
        FileNameExtensionFilter jpgFilter = new FileNameExtensionFilter("JPEG Image (*.jpg, *.jpeg)", "jpg", "jpeg");
        
        fileChooser.addChoosableFileFilter(pngFilter);
        fileChooser.addChoosableFileFilter(jpgFilter);
        fileChooser.setFileFilter(pngFilter); // Set PNG as default choice
        fileChooser.setAcceptAllFileFilterUsed(false); // Disable "All Files" to prevent errors
        
        // Show the open dialog
        int userSelection = fileChooser.showOpenDialog(parentCanvas);
        	
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            
            // Get active file
            File fileToLoad = fileChooser.getSelectedFile();
            
            // Load the file
            try {
                BufferedImage image = ImageIO.read(fileToLoad);
                
                if (image == null) {
                    System.err.println("Unsupported image format or corrupted file.");
                    return cachedImage; // Return original if format is unreadable
                }
                return image;
                
            } catch (IOException e) {
                System.err.println("Error reading the image file: " + e.getMessage());
                return cachedImage;
            }
        }
        
        // If the user clicked Cancel, safely return the unchanged cached image
        return cachedImage;
	}
}