package model;

import datasets.BinaryQuestionnaire;
import datasets.Dataset;
import datasets.FeatureBasedDataset;
import view.View;
import smile.validation.metric.NormalizedMutualInformation;
import java.util.Date;

public class Model {
    //Names of supported clustering algorithms.
    public static final String tangleName = "Tangle";
    public static final String kMeansName = "K-Means";

    private TangleClusterer tangleClusterer = new TangleClusterer();
    private Dataset dataset;
    private long clusteringTime = -1;
    private double[][] softClustering;
    private int[] hardClustering;
    private double NMIScore = -1;

    public Model() {

    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public void generateClusters(int a, int psi) {
        long time = new Date().getTime();
        tangleClusterer.generateClusters(dataset, a, psi);
        softClustering = tangleClusterer.getSoftClustering();
        hardClustering = tangleClusterer.getHardClustering();
        updateNMIScore();
        clusteringTime = new Date().getTime() - time;
    }

    public void generateClustersKMeans(int k) {
        long time = new Date().getTime();
        softClustering = null;
        hardClustering = dataset.kMeans(k);
        updateNMIScore();
        clusteringTime = new Date().getTime() - time;
    }

    private void updateNMIScore() {
        NMIScore = -1;
        if (getHardClustering() != null && dataset.getGroundTruth() != null) {
            NMIScore = NormalizedMutualInformation.joint(getHardClustering(), dataset.getGroundTruth());
        }
    }

    public double[][] getSoftClustering() {
        return softClustering;
    }

    public int[] getHardClustering() {
        return hardClustering;
    }

    public double getNMIScore() {
        return NMIScore;
    }

    public long getClusteringTime() {
        return clusteringTime;
    }

}
