package space.wenliang.ai.aigcplatformserver.bean;

import lombok.Data;
import space.wenliang.ai.aigcplatformserver.entity.ImageDramaEntity;
import space.wenliang.ai.aigcplatformserver.entity.TextChapterEntity;

import java.util.List;

@Data
public class DramaAdd {
    private ImageDramaEntity imageDrama;
    private List<ImageDramaEntity> sortDramas;
}
