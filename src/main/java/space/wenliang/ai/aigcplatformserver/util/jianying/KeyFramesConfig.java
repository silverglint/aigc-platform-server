package space.wenliang.ai.aigcplatformserver.util.jianying;

import lombok.Data;

/**
 * @author Administrator
 * date   2024/10/28
 */
@Data
public class KeyFramesConfig {

    private boolean addKeyFrames;
    private boolean random;
    //1 放大,2 缩小,3 左移,4 右移,5 上移,6 下移,
    private String[] loop;
    private float scale;
    private float left;
    private float top;
    private float right;
    private float bottom;
}
