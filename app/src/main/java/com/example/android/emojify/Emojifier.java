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
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.DrawableRes;
import android.util.Log;
import android.util.SparseArray;

import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

// DONE (1): Create a Java class called Emojifier
// DONE (2): Create a static method in the Emojifier class called detectFaces() which detects and logs the number of faces in a given bitmap.
public class Emojifier {

    // TODO (3): Change all Log statements to Timber logs and remove the LOG_TAG variable
    private static final String LOG_TAG = Emojifier.class.getSimpleName();

    // UDACITY SOLUTION - COMMENTED OUT
    // private static final float EMOJI_SCALE_FACTOR = .9f;
    // private static final double SMILING_PROB_THRESHOLD = .15;
    // private static final double EYE_OPEN_PROB_THRESHOLD = .5;

    private static final double SMILING_PROB_THRESHOLD = .5;
    private static final double EYE_OPEN_PROB_THRESHOLD = .4;

    /**
     * Method for detecting faces in a bitmap, and drawing emoji depending on the facial
     * expression.
     *
     * @param context The application context.
     * @param bitmap The picture in which to detect the faces.
     */
    public static Bitmap detectFacesAndOverlayEmoji(Context context, Bitmap bitmap) {
        // DONE (3): Change the name of the detectFaces() method to detectFacesAndOverlayEmoji() and the return type from void to Bitmap

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

        // DONE (7): Create a variable called resultBitmap and initialize it to the original picture bitmap passed into the detectFacesAndOverlayEmoji() method
        // Initialize result bitmap to original picture
        Bitmap resultBitmap = bitmap;

        // If there are no faces detected, show a Toast message
        if (faces.size() == 0) {
            Toast.makeText(context, R.string.no_faces_message, Toast.LENGTH_SHORT).show();
        } else {

            // DONE (2): Iterate through the faces, calling getClassifications() for each face.
            // Iterate through the faces
            for (int i = 0; i < faces.size(); ++i) {
                Face face = faces.valueAt(i);
                // DONE (6): Change the call to getClassifications to whichEmoji() to log the appropriate emoji for the facial expression.
                // Log the classification probabilities for each face.
                Emoji emoji = whichEmoji(face);

                // DONE (4): Create a variable called emojiBitmap to hold the appropriate Emoji bitmap and remove the call to whichEmoji()
                // DONE (5): Create a switch statement on the result of the whichEmoji() call, and assign the proper emoji bitmap to the variable you created

                @DrawableRes int emojiDrawable = R.drawable.smile;
                switch (emoji) {
                    case SMILING:
                        emojiDrawable = R.drawable.smile;
                        break;
                    case FROWNING:
                        emojiDrawable = R.drawable.frown;
                        break;
                    case LEFT_WINK:
                        emojiDrawable = R.drawable.leftwink;
                        break;
                    case RIGHT_WINK:
                        emojiDrawable = R.drawable.rightwink;
                        break;
                    case LEFT_WINK_FROWNING:
                        emojiDrawable = R.drawable.leftwinkfrown;
                        break;
                    case RIGHT_WINK_FROWNING:
                        emojiDrawable = R.drawable.rightwinkfrown;
                        break;
                    case CLOSED_EYES_SMILING:
                        emojiDrawable = R.drawable.closed_smile;
                        break;
                    case CLOSED_EYES_FROWNING:
                        emojiDrawable = R.drawable.closed_frown;
                        break;
                }

                Bitmap emojiBitmap = BitmapFactory.decodeResource(context.getResources(), emojiDrawable);

                // DONE (8): Call addBitmapToFace(), passing in the resultBitmap, the emojiBitmap and the Face  object, and assigning the result to resultBitmap
                resultBitmap = addBitmapToFace(resultBitmap, emojiBitmap, face);
            }
        }

        // Release the detector
        detector.release();

        // DONE (9): Return the resultBitmap
        return resultBitmap;
    }

    // DONE (1): Create a static method called getClassifications() which logs the probability of each eye being open and that the person is smiling.
    /**
     * Determines the closest emoji to the expression on the face, based on the
     * odds that the person is smiling and has each eye open.
     *
     * @param face The face for which you pick an emoji.
     */
    private static Emoji whichEmoji(Face face) {
        // DONE (2): Change the name of the getClassifications() method to whichEmoji() (also change the log statements)
        // DONE (1): Change the return type of the whichEmoji() method from void to Emoji.
        float smilingProbability = face.getIsSmilingProbability();
        float rightEyeOpenProbability = face.getIsRightEyeOpenProbability();
        float leftEyeOpenProbability = face.getIsLeftEyeOpenProbability();
        Log.d(LOG_TAG,"Classification - Smiling ? " + smilingProbability + " Right Eye Open ? " + rightEyeOpenProbability + "Left Eye Open ?" + leftEyeOpenProbability);

        // DONE (3): Create threshold constants for a person smiling, and and eye being open by taking pictures of yourself and your friends and noting the logs.
        // DONE (4): Create 3 boolean variables to track the state of the facial expression based on the thresholds you set in the previous step: smiling, left eye closed, right eye closed.
        boolean smiling = smilingProbability > SMILING_PROB_THRESHOLD;
        boolean rightEyeOpen = rightEyeOpenProbability > EYE_OPEN_PROB_THRESHOLD;
        boolean leftEyeOpen = leftEyeOpenProbability > EYE_OPEN_PROB_THRESHOLD;

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

        // Log the chosen Emoji
        Log.d(LOG_TAG, "Resulting emoji: " + emoji);

        // DONE (2): Have the method return the selected Emoji type.
        return emoji;
    }
    

    // DONE (1): Create an enum class called Emoji that contains all the possible emoji you can make (smiling, frowning, left wink, right wink, left wink frowning, right wink frowning, closed eye smiling, close eye frowning).
    // Enum for all possible Emojis
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

    // DONE (6) Create a method called addBitmapToFace() which takes the background bitmap, the Emoji bitmap, and a Face object as arguments and returns the combined bitmap with the Emoji over the face.
    /**
     * Combines the original picture with the emoji bitmaps
     *
     * @param backgroundBitmap The original picture
     * @param emojiBitmap      The chosen emoji
     * @param face             The detected face
     * @return The final bitmap, including the emojis over the faces
     */
    public static Bitmap addBitmapToFace(Bitmap facesBitmap, Bitmap emojiBitmap, Face face) {

        Bitmap resultBitmap = facesBitmap.copy(facesBitmap.getConfig(), true);

        PointF facePosition = face.getPosition();
        float faceWidth = face.getWidth();
        float faceHeight = face.getHeight();

        RectF faceDest = new RectF(facePosition.x, facePosition.y, (facePosition.x + faceWidth), (facePosition.y + faceHeight));

        resultBitmap.prepareToDraw();
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(emojiBitmap, null, faceDest, null);

        return resultBitmap;

// UDACITY IMPLEMENTATION - COMMENTED OUT
//        // Initialize the results bitmap to be a mutable copy of the original image
//        Bitmap resultBitmap = Bitmap.createBitmap(backgroundBitmap.getWidth(),
//                backgroundBitmap.getHeight(), backgroundBitmap.getConfig());
//
//        // Scale the emoji so it looks better on the face
//        float scaleFactor = EMOJI_SCALE_FACTOR;
//
//        // Determine the size of the emoji to match the width of the face and preserve aspect ratio
//        int newEmojiWidth = (int) (face.getWidth() * scaleFactor);
//        int newEmojiHeight = (int) (emojiBitmap.getHeight() *
//                newEmojiWidth / emojiBitmap.getWidth() * scaleFactor);
//
//
//        // Scale the emoji
//        emojiBitmap = Bitmap.createScaledBitmap(emojiBitmap, newEmojiWidth, newEmojiHeight, false);
//
//        // Determine the emoji position so it best lines up with the face
//        float emojiPositionX =
//                (face.getPosition().x + face.getWidth() / 2) - emojiBitmap.getWidth() / 2;
//        float emojiPositionY =
//                (face.getPosition().y + face.getHeight() / 2) - emojiBitmap.getHeight() / 3;
//
//        // Create the canvas and draw the bitmaps to it
//        Canvas canvas = new Canvas(resultBitmap);
//        canvas.drawBitmap(backgroundBitmap, 0, 0, null);
//        canvas.drawBitmap(emojiBitmap, emojiPositionX, emojiPositionY, null);
//
//        return resultBitmap;

    }
}
