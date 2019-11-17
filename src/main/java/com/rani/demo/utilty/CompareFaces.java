package com.rani.demo.utilty;


import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.BoundingBox;
import com.amazonaws.services.rekognition.model.CompareFacesMatch;
import com.amazonaws.services.rekognition.model.CompareFacesRequest;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.ComparedFace;

import java.io.IOException;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import com.amazonaws.util.IOUtils;
    public class CompareFaces {
        public  void compareFace(AmazonRekognition rekognitionClient, String photo1, String photo2) throws IOException {
            Image source = getImageUtil(photo1);
            Image target = getImageUtil(photo2);
            Float similarityThreshold = 70F;
            CompareFacesResult compareFacesResult = callCompareFaces(source,
                    target,
                    similarityThreshold,
                    rekognitionClient);

            List<CompareFacesMatch> faceDetails = compareFacesResult.getFaceMatches();
            if (faceDetails.size() > 0) {
                System.out.println("Face [" + photo1 + "] matches with [" + photo2 + "]");
            } else {
                System.out.println("Face [" + photo1 + "] doesn't matches with [" + photo2 + "]\n");
            }
            for (CompareFacesMatch match: faceDetails){
                ComparedFace face= match.getFace();
                BoundingBox position = face.getBoundingBox();
                System.out.println("Face at " + position.getLeft().toString()
                        + " " + position.getTop()
                        + " matches with " + face.getConfidence().toString()
                        + "% confidence.\n");
            }
        }

        private static CompareFacesResult callCompareFaces(Image sourceImage, Image targetImage,
                                                           Float similarityThreshold, AmazonRekognition amazonRekognition) {

            CompareFacesRequest compareFacesRequest = new CompareFacesRequest()
                    .withSourceImage(sourceImage)
                    .withTargetImage(targetImage)
                    .withSimilarityThreshold(similarityThreshold);
            return amazonRekognition.compareFaces(compareFacesRequest);
        }

        private static Image getImageUtil(String key) throws IOException {
            ByteBuffer imageBytes;
            ClassLoader classLoader = new CompareFaces().getClass().getClassLoader();
            try (InputStream inputStream = classLoader.getResourceAsStream(key)) {
                imageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
            }
            return new Image().withBytes(imageBytes);
        }
    }



