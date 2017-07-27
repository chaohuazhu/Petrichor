package work.petrichor.seeta;

/**
 * @author Mitscherlich.
 * @date 2017/7/25.
 **/

public class FaceInfo {

    public int x;
    public int y;
    public int width;
    public int height;

    public FaceInfo() { throw new RuntimeException("Stub!"); }

    public FaceInfo(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        width = w;
        height = h;
    }
}
