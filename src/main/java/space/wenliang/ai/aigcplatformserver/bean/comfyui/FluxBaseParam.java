package space.wenliang.ai.aigcplatformserver.bean.comfyui;

import lombok.Builder;
import lombok.Data;

import java.util.Random;

/**
 * @author Administrator
 * date   2024/10/27
 */
@Data
@Builder
public class FluxBaseParam {

    private String taskId;
    private String projectId;
    private String chapterId;
    private String imgId;
    private String prompt;
    private Integer seed;
    private String outputPath;
    private Integer latentBatchSize;

    public String getTaskId() {
        return projectId + "_" + chapterId + "_" + imgId;
    }

    public Integer getSeed() {
        return seed == null ? Math.abs(new Random().nextInt()) : seed;
    }
}
