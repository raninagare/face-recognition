package com.rani.demo;


import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;

import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;


public class SearchFaceMatchingImageCollection {
    public static final String collectionId = "MyCollection";
    public static final String bucket = "facerecograni";
    public static final String photo = "rani.png";

    public static void main(String[] args) throws Exception {

        AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

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
        List < FaceMatch > faceImageMatches = searchFacesByImageResult.getFaceMatches();
        for (FaceMatch face: faceImageMatches) {
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(face));
            System.out.println();
        }
    }
     public void createCollection(){
        // AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();
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

         String collectionId = "MyCollection";
         System.out.println("Creating collection: " +
                 collectionId );

         CreateCollectionRequest request = new CreateCollectionRequest()
                 .withCollectionId(collectionId);

         CreateCollectionResult createCollectionResult = rekognitionClient.createCollection(request);
         System.out.println("CollectionArn : " +
                 createCollectionResult.getCollectionArn());
         System.out.println("Status code : " +
                 createCollectionResult.getStatusCode().toString());

     }

     public void addToCollection(){
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

         Image image = new Image()
                 .withS3Object(new S3Object()
                         .withBucket(bucket)
                         .withName(photo));

         IndexFacesRequest indexFacesRequest = new IndexFacesRequest()
                 .withImage(image)
                 .withQualityFilter(QualityFilter.AUTO)
                 .withMaxFaces(1)
                 .withCollectionId(collectionId)
                 .withExternalImageId(photo)
                 .withDetectionAttributes("DEFAULT");

         IndexFacesResult indexFacesResult = rekognitionClient.indexFaces(indexFacesRequest);

         System.out.println("Results for " + photo);
         System.out.println("Faces indexed:");
         List<FaceRecord> faceRecords = indexFacesResult.getFaceRecords();
         for (FaceRecord faceRecord : faceRecords) {
             System.out.println("  Face ID: " + faceRecord.getFace().getFaceId());
             System.out.println("  Location:" + faceRecord.getFaceDetail().getBoundingBox().toString());
         }

         List<UnindexedFace> unindexedFaces = indexFacesResult.getUnindexedFaces();
         System.out.println("Faces not indexed:");
         for (UnindexedFace unindexedFace : unindexedFaces) {
             System.out.println("  Location:" + unindexedFace.getFaceDetail().getBoundingBox().toString());
             System.out.println("  Reasons:");
             for (String reason : unindexedFace.getReasons()) {
                 System.out.println("   " + reason);
             }
         }
     }
}

