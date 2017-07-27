package work.petrichor.seeta;

/**
 * @author Mitscherlich.
 * @date 2017/7/25.
 **/

public class ImageData {

    public byte[] frame;
    public int width;
    public int heigth;
    public int channels;

    public ImageData() { throw new RuntimeException("Stub!"); }

    public ImageData(byte[] data, int w, int h, int ch) {
        frame = data;
        width = w;
        heigth = h;
        channels = ch;
    }
}
