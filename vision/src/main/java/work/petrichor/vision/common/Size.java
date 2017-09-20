package work.petrichor.vision.common;

/**
 * @author  Mitscherlich
 * @date    2017/9/1
 * **/
public final class Size {

    private final int width;
    private final int height;

    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean equals(Object another) {
        if (another == null)
            return false;
        else if (this == another)
            return true;
        else if (!(another instanceof Size))
            return false;
        else {
            Size anSize = (Size) another;
            return this.height == anSize.height && this.width == anSize.width;
        }
    }

    public String toString() {
        int width = this.width;
        int height = this.height;
        return (new StringBuffer(23)).append(width).append("x").append(height).toString();
    }

    private static NumberFormatException numberFormatException(String size) {
        throw new NumberFormatException((new StringBuffer(16 + String.valueOf(size).length()))
                .append("Invalid Size: \"")
                .append(size).append("\"")
                .toString());
    }

    public static Size parserSize(String string) throws NumberFormatException {
        if (string == null)
            throw new IllegalArgumentException("string must not be null");
        else {
            int pos = string.indexOf(42);
            if (pos < 0)
                throw numberFormatException(string);
            else {
                try {
                    return new Size(Integer.parseInt(string.substring(0, pos)), Integer.parseInt(string.substring(pos + 1)));
                } catch (NumberFormatException e) {
                    throw numberFormatException(string);
                }
            }
        }
    }

    public int hashCode() {
        return this.height ^ (this.width << 16 | this.width >>> 16);
    }
}
