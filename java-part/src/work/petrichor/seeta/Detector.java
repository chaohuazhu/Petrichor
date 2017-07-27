package work.petrichor.seeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mitscherlich.
 * @date 2017/7/25.
 **/

public class Detector {

    private String modelPath = "";
    private long address;

    public Detector(String path) {
        if ("".equals(path)) throw new RuntimeException("Uninitialized parameter!");

        File model = new File(path);
        if (!model.exists()) throw new RuntimeException("Invalid model path!");

        address = getAddress(path);
    }

    public List<FaceInfo> detect(ImageData frame) {
        String faceInfo = detect(address, frame.frame, frame.width, frame.heigth);

        if (faceInfo.isEmpty() || "".equals(faceInfo)) return null;

        String[] facesStr = faceInfo.split(";");
        ArrayList<FaceInfo> faces = new ArrayList<FaceInfo>();
        
        for (String str : facesStr) {
            String[] box = str.split(",");
            faces.add(new FaceInfo(Integer.parseInt(box[0]), Integer.parseInt(box[1]), Integer.parseInt(box[2]), Integer.parseInt(box[3])));
        }

        return faces;
    }

    private native String detect(long address, byte[] frame, int width, int height);

    private native long getAddress(String modelPath);

    static { System.loadLibrary("detector-lib"); }
}
