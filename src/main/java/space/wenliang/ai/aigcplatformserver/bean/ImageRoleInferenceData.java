package space.wenliang.ai.aigcplatformserver.bean;

import lombok.Data;
import space.wenliang.ai.aigcplatformserver.entity.ImageRoleInferenceEntity;

import java.util.ArrayList;
import java.util.List;

@Data
public class ImageRoleInferenceData {
    private String content;
    private String lines;
    private List<ImageRoleInferenceEntity> imageRoleInferences = new ArrayList<>();
}
