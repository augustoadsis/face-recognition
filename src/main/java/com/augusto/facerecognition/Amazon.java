package com.augusto.facerecognition;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class Amazon {

    public static final String IMAGE1 = "https://statig0.akamaized.net/bancodeimagens/8v/t3/in/8vt3incliwwx1k9q80hfj1p88.jpg";
    public static final String IMAGE2 = "https://www.jornalcontabil.com.br/wp-content/uploads/2018/02/silvio.jpg";

    public static void main(String[] args) {
        compareTwoFaces();
    }

    public static void compareTwoFaces() {

        try {

            Float similarityThreshold = 70F;

            Region region = Region.US_EAST_2;
            RekognitionClient rekClient = RekognitionClient.builder()
                    .region(region)
                    .build();

            InputStream sourceStream = new URL(IMAGE1).openStream();
            InputStream targetStream = new URL(IMAGE2).openStream();

            SdkBytes sourceBytes = SdkBytes.fromInputStream(sourceStream);
            SdkBytes targetBytes = SdkBytes.fromInputStream(targetStream);

            // Create an Image object for the source image
            Image souImage = Image.builder()
                    .bytes(sourceBytes)
                    .build();

            Image tarImage = Image.builder()
                    .bytes(targetBytes)
                    .build();

            CompareFacesRequest facesRequest = CompareFacesRequest.builder()
                    .sourceImage(souImage)
                    .targetImage(tarImage)
                    .similarityThreshold(similarityThreshold)
                    .build();

            // Compare the two images
            CompareFacesResponse compareFacesResult = rekClient.compareFaces(facesRequest);

            // Display results
            List<CompareFacesMatch> faceDetails = compareFacesResult.faceMatches();
            for (CompareFacesMatch match : faceDetails) {
                ComparedFace face = match.face();
                BoundingBox position = face.boundingBox();
                System.out.println("Face at " + position.left().toString()
                        + " " + position.top()
                        + " matches with " + face.confidence().toString()
                        + "% confidence.");

            }
            List<ComparedFace> uncompared = compareFacesResult.unmatchedFaces();

            System.out.println("There was " + uncompared.size()
                    + " face(s) that did not match");
            System.out.println("Source image rotation: " + compareFacesResult.sourceImageOrientationCorrection());
            System.out.println("target image rotation: " + compareFacesResult.targetImageOrientationCorrection());

        } catch (RekognitionException | FileNotFoundException | MalformedURLException e) {
            System.out.println("Failed to load source image " + IMAGE1);
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Failed to load source image " + IMAGE2);
            System.exit(1);
        }
    }
}
