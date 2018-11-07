package Controller;

import Model.DisJointSet;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.scene.control.Label;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

import javafx.scene.image.ImageView;

import java.io.IOException;

public class Controller {

    @FXML private FileChooser fc;
    @FXML private File filePath;
    @FXML private Image image;
    @FXML private ImageView imageCall, grayImage;
    @FXML private BufferedImage bufferedImage, result;
    @FXML private Slider slider, clusterSize;
    @FXML private Label count;


    private static final int White = 255;
    private static final int Black = 0;

    @FXML
    private int getThreshold() {
        return (int) slider.getValue();
    }

    @FXML
    private int getClusterSize() {
        return (int) clusterSize.getValue();
    }


    @FXML
    protected void handleOpenAction() {
        new File(System.getProperty("user.home"));
        fc = new FileChooser();
        filePath = fc.showOpenDialog(null);
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images",
                        "*.jpg", "*.jpeg", "*.png", "*.bmp", "*.gif"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("JPEG", "*.jpeg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("BMP", "*.bmp"),
                new FileChooser.ExtensionFilter("GIF", "*.gif")
        );
        try {
            bufferedImage = ImageIO.read(filePath);
            image = SwingFXUtils.toFXImage(bufferedImage, null);
            imageCall.setImage(image);
            grayImage.setImage(SwingFXUtils.toFXImage(makeGray(bufferedImage), null));
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private BufferedImage makeGray(BufferedImage image) {
        result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        int[][] blackAndWhite = new int[result.getWidth()][result.getHeight()];
        int threshold = getThreshold();


        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 1; y < height; y++) {
            for (int x = 1; x < width; x++) {
                int pixel = image.getRGB(x, y);

                int a = ((pixel >> 24) & 0xff);
                int r = ((pixel >> 16) & 0xff);
                int g = ((pixel >> 8) & 0xff);
                int b = (pixel & 0xff);

                //calculate average
                int avg = (r + g + b) / 3;

                //replace RGB value with black or white
                int color = avg < threshold ? Black : White;
                pixel = (a << 24) | (color << 16) | (color << 8) | color;

                result.setRGB(x, y, pixel);
            }
        }
        return result;
    }

    @FXML
    private void grayScale() {

        makeGray(bufferedImage);
    }



    @FXML
    private int preCountSheep(BufferedImage bufferedImage) {

        int[][] colorArray = getPixelArray(bufferedImage);
        int[][] blackAndWhite = getNullColorArray(colorArray);

        count.setText("There is " + countSheep(blackAndWhite));
        return countSheep(blackAndWhite);
    }

    private int countSheep(int blackAndWhite[][]) {
        int rows = blackAndWhite.length;
        int columns = blackAndWhite[0].length;

        DisJointSet dus = new DisJointSet(rows * columns);


        int clusterSize = getClusterSize();

        for (int j = 0; j < rows; j++) {
            for (int k = 0; k < columns; k++) {
                /* If cell is 0, no
                 thing to do*/
                if (blackAndWhite[j][k] == 0)
                    continue;

                /* Check all 8 neighbours and do a union
                with neighbour's set if neighbour is
                also 255*/
                if (j + 1 < rows && blackAndWhite[j + 1][k] == 255) {
                    dus.union(j * (columns) + k, (j + 1) * (columns) + k);
                }
                if (j - 1 >= 0 && blackAndWhite[j - 1][k] == 255) {
                    dus.union(j * (columns) + k, (j - 1) * (columns) + k);
                }
                if (k + 1 < columns && blackAndWhite[j][k + 1] == 255) {
                    dus.union(j * (columns) + k, (j) * (columns) + k + 1);
                }
                if (k - 1 >= 0 && blackAndWhite[j][k - 1] == 255) {
                    dus.union(j * (columns) + k, (j) * (columns) + k - 1);
                }
                if (j + 1 < rows && k + 1 < columns && blackAndWhite[j + 1][k + 1] == 255) {
                    dus.union(j * (columns) + k, (j + 1) * (columns) + k + 1);
                }
                if (j + 1 < rows && k - 1 >= 0 && blackAndWhite[j + 1][k - 1] == 255) {
                    dus.union(j * columns + k, (j + 1) * (columns) + k - 1);
                }
                if (j - 1 >= 0 && k + 1 < columns && blackAndWhite[j - 1][k + 1] == 255) {
                    dus.union(j * columns + k, (j - 1) * columns + k + 1);
                }
                if (j - 1 >= 0 && k - 1 >= 0 && blackAndWhite[j - 1][k - 1] == 255) {
                    dus.union(j * columns + k, (j - 1) * columns + k - 1);
                }
            }
        }
        int[] c = new int[rows * columns];
        int numberOfSheep = 0;
        for (int j = 0; j < rows; j++) {
            for (int k = 0; k < columns; k++) {
                if (blackAndWhite[j][k] == 255) {

                    int x = dus.find(j * columns + k);

                    /* If frequency of set is 0,
                     increment numberOfSheep*/
                    if (c[x] == clusterSize) {
                        numberOfSheep++;
                    }{
                        c[x]++;
                    }
                }
            }
        }
        return numberOfSheep;
    }


    @FXML
    private void counter() {
        preCountSheep(result);
    }

    private int[][] getNullColorArray(int[][] colorArray) {

        int threshold = getThreshold();
        int rows = colorArray.length;
        int columns = colorArray[0].length;
        int[][] blackAndWhite2 = new int[rows][columns];

        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < columns; ++j) {
                blackAndWhite2[i][j] = colorArray[i][j] > threshold ? 255 : 0; //This is the more efficient machine
            }
        }
        return blackAndWhite2;
    }

    private int[][] getPixelArray(BufferedImage bufferedImage) {

        int rows = bufferedImage.getWidth();
        int columns = bufferedImage.getHeight();
        int[][] pixelArray = new int[rows][columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int pixel = bufferedImage.getRGB(i, j);

                int red = ((pixel >> 16) & 0xff);
                int green = ((pixel >> 8) & 0xff);
                int blue = (pixel & 0xff);

                int avg = (red + green + blue) / 3;
                pixelArray[i][j] = avg;
            }
        }
        return pixelArray;
    }
}