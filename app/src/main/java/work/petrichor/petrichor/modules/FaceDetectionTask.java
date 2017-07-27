package work.petrichor.petrichor.modules;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import work.petrichor.seeta.Detector;
import work.petrichor.seeta.FaceInfo;
import work.petrichor.seeta.ImageData;

/**
 * @author Mitscherlich.
 * @date 2017/7/26.
 **/

public class FaceDetectionTask extends AsyncTask<ImageData, Void, List<FaceInfo>> {

    private String modelPath;

    public FaceDetectionTask(String path) {
        modelPath = path;
    }

    @Override
    protected List<FaceInfo> doInBackground(ImageData[] frames) {
        long now = System.currentTimeMillis();

        Detector detector = new Detector(modelPath);

        List<FaceInfo> faces = detector.detect(frames[0]);

        long ms = System.currentTimeMillis() - now;
        if (faces == null || faces.isEmpty()) {
            android.util.Log.i("FaceDetectionTask", "None face detected in " + ms + "ms!");
            return new ArrayList<>();
        } else {
            android.util.Log.i("FaceDetectionTask", "Detected " + faces.size() +  " face(s) in " + ms + "ms!");
            return faces;
        }
    }
}
