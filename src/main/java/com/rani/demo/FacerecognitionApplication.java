package com.rani.demo;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rani.demo.utilty.CompareFaces;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.List;

@SpringBootApplication
public class FacerecognitionApplication {

    public static final String collectionId = "MyCollection";
    public static final String bucket = "facerecograni";
    public static final String photo = "rani.png";

    public static void main(String[] args) throws Exception {

        SpringApplication.run( FacerecognitionApplication.class, args );
        SearchFaceMatchingImageCollection img =new SearchFaceMatchingImageCollection();
        img.addToCollection();


        AWSCredentials credentials;
        try {
            credentials = new ProfileCredentialsProvider().getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
                    + "Please make sure that your credentials file is at the correct "
                    + "location (/Users/userid/.aws/credentials), and is in valid format.", e);
        }
        AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder
                .standard()
                .withRegion( Regions.US_EAST_2)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
//

        ObjectMapper objectMapper = new ObjectMapper();

        // Get an image object from S3 bucket.
        Image image=new Image()
                .withS3Object(new S3Object()
                        .withBucket(bucket)
                        .withName(photo));

        // Search collection for faces similar to the largest face in the image.
        SearchFacesByImageRequest searchFacesByImageRequest = new SearchFacesByImageRequest()
                .withCollectionId(collectionId)
                .withImage(image)
                .withFaceMatchThreshold(70F)
                .withMaxFaces(2);

        SearchFacesByImageResult searchFacesByImageResult =
                rekognitionClient.searchFacesByImage(searchFacesByImageRequest);

        System.out.println("Faces matching largest face in image from" + photo);
        List<FaceMatch> faceImageMatches = searchFacesByImageResult.getFaceMatches();
        for (FaceMatch face: faceImageMatches) {
            if(face.getSimilarity()>90){
                System.out.println( "User is Authenticated :" );
            }
        }
    }
}
