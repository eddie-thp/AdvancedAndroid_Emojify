/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;

import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

// DONE (1): Create a Java class called Emojifier
// DONE (2): Create a static method in the Emojifier class called detectFaces() which detects and logs the number of faces in a given bitmap.
public class Emojifier {

    private static final String LOG_TAG = Emojifier.class.getSimpleName();

    /**
     * Method for detecting faces in a bitmap.
     *
     * @param context The application context.
     * @param bitmap The picture in which to detect the faces.
     */
    public static void detectFaces(Context context, Bitmap bitmap) {


        /*
        See: https://developers.google.com/vision/android/detect-faces-tutorial

        Setting “tracking enabled” to false is recommended for detection for unrelated individual images (as opposed to video or a series of consecutively captured still images),
        since this will give a more accurate result. But for detection on consecutive images (e.g., live video), having tracking enabled gives a more accurate and faster result.

        Given a bitmap, we can create Frame instance from the bitmap to supply to the detector:
         */
        // Create the face detector, disable tracking and enable classifications
        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        // Build the frame
        // The detector can be called synchronously with a frame to detect faces:
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();

        // Detect the faces
        // The result returned includes a collection of Face instances. We can iterate over the collection of faces, the collection of landmarks for each face, and draw the result based on each landmark position:
        SparseArray<Face> faces = detector.detect(frame);

        // Log the number of faces
        Log.d(LOG_TAG, "detectFaces: number of faces = " + faces.size());

        // If there are no faces detected, show a Toast message
        if(faces.size() == 0){
            Toast.makeText(context, R.string.no_faces_message, Toast.LENGTH_SHORT).show();
        } else {

            // DONE (2): Iterate through the faces, calling getClassifications() for each face.
            for (int i = 0; i < faces.size(); ++i) {
                Face face = faces.valueAt(i);

                // Log the classification probabilities for each face.
                whichEmoji(face);
                // TODO (6): Change the call to getClassifications to whichEmoji() to log the appropriate emoji for the facial expression.
            }

        }

        // Release the detector
        detector.release();
    }

    // DONE (1): Create a static method called getClassifications() which logs the probability of each eye being open and that the person is smiling.
    /**
     * Method for logging the classification probabilities.
     *
     * @param face The face to get the classification probabilities.
     */
    private static void whichEmoji(Face face) {
        // DONE (2): Change the name of the getClassifications() method to whichEmoji() (also change the log statements)
        float smilingProbability = face.getIsSmilingProbability();
        float rightEyeOpenProbability = face.getIsSmilingProbability();
        float leftEyeOpenProbability = face.getIsSmilingProbability();
        Log.d(LOG_TAG,"Classification - Smiling ? " + smilingProbability + " Right Eye Open ? " + rightEyeOpenProbability + "Left Eye Open ?" + leftEyeOpenProbability);

        // DONE (3): Create threshold constants for a person smiling, and and eye being open by taking pictures of yourself and your friends and noting the logs.
        // DONE (4): Create 3 boolean variables to track the state of the facial expression based on the thresholds you set in the previous step: smiling, left eye closed, right eye closed.
        boolean smiling = smilingProbability > 0.5;
        boolean rightEyeOpen = rightEyeOpenProbability > 0.4;
        boolean leftEyeOpen = leftEyeOpenProbability > 0.4;

        // DONE (5): Create an if/else system that selects the appropriate emoji based on the above booleans and log the result.
        Emoji emoji = Emoji.SMILING;
        if (!smiling && rightEyeOpen && leftEyeOpen) {
            emoji = Emoji.FROWNING;
        } else if (smiling && rightEyeOpen && !leftEyeOpen) {
            emoji = Emoji.LEFT_WINK;
        } else if (smiling && !rightEyeOpen && leftEyeOpen) {
            emoji = Emoji.RIGHT_WINK;
        } else if (!smiling && rightEyeOpen && !leftEyeOpen) {
            emoji = Emoji.LEFT_WINK_FROWNING;
        } else if (!smiling && !rightEyeOpen && leftEyeOpen) {
            emoji = Emoji.RIGHT_WINK_FROWNING;
        } else if (smiling && !rightEyeOpen && !leftEyeOpen) {
            emoji = Emoji.CLOSED_EYES_SMILING;
        } else if (!smiling && !rightEyeOpen && !leftEyeOpen) {
            emoji = Emoji.CLOSED_EYES_FROWNING;
        }

        Log.d(LOG_TAG, "Resulting emoji: " + emoji);
    }

    // DONE (1): Create an enum class called Emoji that contains all the possible emoji you can make (smiling, frowning, left wink, right wink, left wink frowning, right wink frowning, closed eye smiling, close eye frowning).
    enum Emoji {
        SMILING,
        FROWNING,
        LEFT_WINK,
        RIGHT_WINK,
        LEFT_WINK_FROWNING,
        RIGHT_WINK_FROWNING,
        CLOSED_EYES_SMILING,
        CLOSED_EYES_FROWNING
    }
}
