package main;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        BinaryQuestionnaire questionnaire = new BinaryQuestionnaire();
        questionnaire.loadAnswersFromFile("NPI.csv", 1, -1, 1, 40);
        TangleClusterer.generateClusters(questionnaire, 1500);
        System.out.println("\nkMeans:");
        int[] kMeansResult = questionnaire.kMeans(BinaryQuestionnaire.NIP_SCORE_LOOKUP);
        System.out.println("Resulting clustering for first 50 participants: ");
        for (int i = 0; i < 50; i++) {
            System.out.print(kMeansResult[i] + " ");
        }
    }
}