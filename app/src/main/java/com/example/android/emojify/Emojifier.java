package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

// DONE (1): Create a Java class called Emojifier
// DONE (2): Create a static method in the Emojifier class called detectFaces() which detects and logs the number of faces in a given bitmap.
public class Emojifier {

    private static final String LOG_TAG = Emojifier.class.getSimpleName();

    public static void detectFaces(Context context, Bitmap bitmap) {


        /*
        See: https://developers.google.com/vision/android/detect-faces-tutorial

        Setting “tracking enabled” to false is recommended for detection for unrelated individual images (as opposed to video or a series of consecutively captured still images),
        since this will give a more accurate result. But for detection on consecutive images (e.g., live video), having tracking enabled gives a more accurate and faster result.

        Given a bitmap, we can create Frame instance from the bitmap to supply to the detector:
         */

        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();


        // The detector can be called synchronously with a frame to detect faces:
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();

        // The result returned includes a collection of Face instances. We can iterate over the collection of faces, the collection of landmarks for each face, and draw the result based on each landmark position:
        SparseArray<Face> faces = detector.detect(frame);

        Log.d(LOG_TAG, "Detected " + faces.size() + " faces");

        detector.release();
    }
}
