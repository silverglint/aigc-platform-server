package space.wenliang.ai.aigcplatformserver.bean;

import lombok.Data;
import space.wenliang.ai.aigcplatformserver.util.jianying.KeyFramesConfig;

/**
 * @author Administrator
 * date   2024/10/28
 */
@Data
public class DramaExportParam {

    private String projectId;
    private String chapterId;

    private KeyFramesConfig keyFramesConfig;
}
