package space.wenliang.ai.aigcplatformserver.bean;

import lombok.Data;
import space.wenliang.ai.aigcplatformserver.entity.ImageRoleInferenceEntity;

import java.util.List;

@Data
public class ImageRoleInferenceParam {
    private String projectId;
    private String chapterId;
    private String inferenceType;
    private Integer tmServerId;
    private String systemPrompt;
    private String userPrompt;
    private List<ImageRoleInferenceEntity> imageRoleInferences;
}
