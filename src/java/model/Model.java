package model;

import datasets.Dataset;
import datasets.FeatureBasedDataset;
import smile.validation.metric.NormalizedMutualInformation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

public class Model {

    //This is the model for the Model-View-Controller design pattern.

    //Names of supported clustering algorithms.
    public static final String tangleName = "Tangle";
    public static final String kMeansName = "K-Means";
    public static final String spectralClusteringName = "Spectral";
    public static final String linkageName = "Linkage";

    private final TangleClusterer tangleClusterer = new TangleClusterer();
    private Dataset dataset;
    private long clusteringTime = -1;
    private double[][] softClustering;
    private int[] hardClustering;
    private double NMIScore = -1;

    public Model() {

    }

    //Sets the current dataset for the model.
    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    //Returns the current dataset.
    public Dataset getDataset() {
        return dataset;
    }

    //Generates a hard clustering using tangles without updating the model.
    public int[] generateClusters(Dataset dataset, int a, int psi, String initialCutGenerator, String costFunctionName) {
        TangleClusterer clusterer = new TangleClusterer();
        clusterer.generateClusters(dataset, a, psi, initialCutGenerator, costFunctionName);
        return clusterer.getHardClustering();
    }

    //Generates a clustering using tangles and updates the model.
    public void generateClusters(int a, int psi, String initialCutGenerator, String costFunctionName) {
        long time = new Date().getTime();
        tangleClusterer.generateClusters(dataset, a, psi, initialCutGenerator, costFunctionName);
        softClustering = tangleClusterer.getSoftClustering();
        hardClustering = tangleClusterer.getHardClustering();
        clusteringTime = new Date().getTime() - time;
        updateNMIScore();
    }

    //Generates a clustering using K-Means and updates the model.
    public void generateClustersKMeans(int k) {
        long time = new Date().getTime();
        softClustering = null;
        hardClustering = dataset.kMeans(k);
        clusteringTime = new Date().getTime() - time;
        updateNMIScore();
    }

    //Generates a clustering using spectral clustering and updates the model.
    public void generateClustersSpectral(int k, double sigma) {
        long time = new Date().getTime();
        softClustering = null;
        hardClustering = dataset.spectralClustering(k, sigma);
        clusteringTime = new Date().getTime() - time;
        updateNMIScore();
    }

    //Generates a clustering using linkage clustering and updates the model.
    public void generateClustersLinkage(int k) {
        long time = new Date().getTime();
        softClustering = null;
        hardClustering = dataset.hierarchicalClustering(k);
        clusteringTime = new Date().getTime() - time;
        updateNMIScore();
    }

    //Updates the NMI score using the last generated hard clustering and the ground truth for the current dataset.
    private void updateNMIScore() {
        NMIScore = -1;
        if (getHardClustering() != null && dataset.getGroundTruth() != null) {
            NMIScore = NormalizedMutualInformation.joint(getHardClustering(), dataset.getGroundTruth());
        }
    }

    //Returns the last generated soft clustering.
    public double[][] getSoftClustering() {
        return softClustering;
    }

    //Returns the last generated hard clustering.
    public int[] getHardClustering() {
        return hardClustering;
    }

    //Returns the last calculated NMI score.
    public double getNMIScore() {
        return NMIScore;
    }

    //Returns the last measured clustering time.
    public long getClusteringTime() {
        return clusteringTime;
    }

    public void clusterImage(String inputFilePath, String outputFilePath) {
        try {
            Color[] colors = new Color[] { Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN, Color.ORANGE, Color.PINK, Color.GRAY};
            BufferedImage image = ImageIO.read(new FileInputStream(new File(inputFilePath)));
            double[][] dataPoints = new double[image.getHeight()*image.getWidth()][3];
            int index = 0;
            for (int i = 0; i < image.getWidth(); i++) {
                for (int j = 0; j < image.getHeight(); j++) {
                    Color color = new Color(image.getRGB(i, j));
                    double x = (i/(double)image.getWidth())*255;
                    double y = (j/(double)image.getHeight())*255;
                    dataPoints[index++] = new double[] {color.getRed(), color.getGreen(), color.getBlue()};
                }
            }
            FeatureBasedDataset imageDataset = new FeatureBasedDataset(dataPoints);
            TangleClusterer clusterer = new TangleClusterer();
            clusterer.generateClusters(imageDataset, (int)(dataPoints.length/5.0*(2.0/3.0)), -1, null, null);
            int[] hardClustering = clusterer.getHardClustering();

            colors = new Color[Arrays.stream(hardClustering).max().getAsInt()+1];
            for (int i = 0; i < colors.length; i++) {
                int r = 0;
                int g = 0;
                int b = 0;
                int n = 0;
                for (int j = 0; j < hardClustering.length; j++) {
                    if (hardClustering[j] == i) {
                        r += dataPoints[j][0];
                        g += dataPoints[j][1];
                        b += dataPoints[j][2];
                        n++;
                    }
                }
                colors[i] = new Color(r/n, g/n, b/n);
            }

            BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            index = 0;
            for (int i = 0; i < newImage.getWidth(); i++) {
                for (int j = 0; j < newImage.getHeight(); j++) {
                    newImage.setRGB(i, j, colors[hardClustering[index++]].getRGB());
                }
            }
            File outputfile = new File(outputFilePath);
            ImageIO.write(newImage, "jpg", outputfile);
        } catch (IOException e) {}
    }

}
