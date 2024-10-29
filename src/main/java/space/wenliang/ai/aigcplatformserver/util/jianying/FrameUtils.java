package space.wenliang.ai.aigcplatformserver.util.jianying;

import lombok.Data;

/**
 * @author Administrator
 * date   2024/10/28
 */

@Data
public class FrameUtils {

    private int fps = 30;

    public FrameUtils() {
    }

    public FrameUtils(int fps) {
        this.fps = fps;
    }

    public static FrameUtils of() {
        return new FrameUtils();
    }

    public static FrameUtils of(int fps) {
        return new FrameUtils(fps);
    }

    public long framePerMs(String ms) {
        return framePerMs(Long.parseLong(ms));
    }

    public long framePerMs(long ms) {
        return ms * 1000;
    }

    public static void main(String[] args) {
        FrameUtils frameUtils = FrameUtils.of(30);
        System.out.println((2496*30)/(1000.0f/30));
    }
}
