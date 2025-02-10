import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class ImageIconConverter {
    private JFrame frame;
    private JTextField imagePathField;
    private JLabel imageLabel;
    private JButton convertButton;
    private JButton saveButton;
    private ImageIcon imageIcon;
    private BufferedImage processedImage;  // Store the processed image

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ImageIconConverter().createAndShowGUI();
        });
    }

    public void createAndShowGUI() {
        frame = new JFrame("Image Icon Converter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 510);  // Increase frame height to fit new label size
        frame.setLayout(null);  // Set null layout to manually adjust positions

        // Input field for the image path
        imagePathField = new JTextField(30);
        imagePathField.setText("C:\\Users\\pawan\\OneDrive\\Desktop\\cartoon.jpg");  // Default path
        imagePathField.setBounds(150, 20, 300, 30);  // Set position for input field
        frame.add(imagePathField);

        // Button to convert image to ImageIcon
        convertButton = new JButton("Convert to Icon");
        convertButton.setBounds(150, 60, 200, 30);  // Set position for button
        frame.add(convertButton);

        // Button to save the image
        saveButton = new JButton("Save Image");
        saveButton.setBounds(150, 100, 200, 30);  // Set position for Save button
        saveButton.setEnabled(false);  // Disable the save button initially
        frame.add(saveButton);

        // Label to display the converted image
        imageLabel = new JLabel("Image will appear here");
        imageLabel.setPreferredSize(new Dimension(300, 310)); // Set size for label
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);  // Center image horizontally
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);    // Center image vertically
        imageLabel.setBounds(150, 200, 300, 310); // Set bounds of label
        frame.add(imageLabel);

        // Action listener for the Convert Button
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String imagePath = imagePathField.getText();
                
                // Normalize the path (use forward slashes)
                imagePath = imagePath.replace("\\", "/");

                // Check if the file exists
                File imageFile = new File(imagePath);
                if (imageFile.exists() && imageFile.isFile()) {
                    try {
                        // Load the image file as a BufferedImage
                        BufferedImage originalImage = ImageIO.read(imageFile);

                        // Remove the white background (make white pixels transparent)
                        processedImage = removeWhiteBackground(originalImage);

                        // Resize the image to fit the label's dimensions
                        Image scaledImage = processedImage.getScaledInstance(imageLabel.getWidth(), imageLabel.getHeight(), Image.SCALE_SMOOTH);

                        // Create an ImageIcon from the resized image
                        imageIcon = new ImageIcon(scaledImage);
                        imageLabel.setIcon(imageIcon);  // Set the resized image to the label

                        // Enable the Save button after processing
                        saveButton.setEnabled(true);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "Error loading image: " + ex.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid file path. Please enter a valid image path.");
                }
            }
        });

        // Action listener for the Save Button
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Use JFileChooser to select where to save the file
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save Image");
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG Image", "png"));
                
                // Show Save dialog
                int userSelection = fileChooser.showSaveDialog(frame);
                
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    String filePath = fileToSave.getAbsolutePath();
                    
                    // If the user didn't add a file extension, add ".png" by default
                    if (!filePath.endsWith(".png")) {
                        filePath += ".png";
                    }
                    
                    try {
                        // Save the processed image as a PNG file
                        ImageIO.write(processedImage, "PNG", new File(filePath));
                        JOptionPane.showMessageDialog(frame, "Image saved successfully at " + filePath);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(frame, "Error saving image: " + ex.getMessage());
                    }
                }
            }
        });

        // Center the window on the screen
        frame.setLocationRelativeTo(null);

        // Make the window visible
        frame.setVisible(true);
    }

    // Method to remove the white background and make it transparent
    private BufferedImage removeWhiteBackground(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Create a new image with transparency (RGBA)
        BufferedImage transparentImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // Loop through each pixel and set white pixels to transparent
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color pixelColor = new Color(image.getRGB(x, y));

                // Check if the pixel is white or close to white
                if (isWhite(pixelColor)) {
                    transparentImage.setRGB(x, y, new Color(0, 0, 0, 0).getRGB()); // Set transparent
                } else {
                    transparentImage.setRGB(x, y, pixelColor.getRGB()); // Keep the original color
                }
            }
        }

        return transparentImage;
    }

    // Helper method to determine if a color is white or close to white
    private boolean isWhite(Color color) {
        int threshold = 200;  // Adjust this value if needed
        return color.getRed() > threshold && color.getGreen() > threshold && color.getBlue() > threshold;
    }
}
