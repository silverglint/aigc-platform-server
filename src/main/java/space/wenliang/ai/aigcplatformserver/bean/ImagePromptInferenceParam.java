package space.wenliang.ai.aigcplatformserver.bean;

import lombok.Data;

@Data
public class ImagePromptInferenceParam {
    private String projectId;
    private String chapterId;
    private String inferenceType;
    private Integer tmServerId;
    private String systemPrompt;
    private String userPrompt;
}
