package space.wenliang.ai.aigcplatformserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * date   2024/10/24
 */
@Data
@TableName(value = "drama_info")
public class DramaInfoEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "project_id")
    private String projectId;

    @TableField(value = "chapter_id")
    private String chapterId;

    @TableField(value = "text_sort")
    private Integer textSort;

    @TableField(value = "\"role\"")
    private String role;

    @TableField(value = "image_prompt")
    private String imagePrompt;

    @TableField(value = "preview_image_files")
    private String previewImageFiles;

    @TableField(value = "final_image_files")
    private String finalImageFiles;

    @TableField("image_task_state")
    private int imageTaskState;

    @TableField(exist = false)
    private Integer paraIndex;

    @TableField(exist = false)
    private Integer sentIndex;

    @TableField(exist = false)
    private List<String> text;

    @TableField(exist = false)
    private String timeStart;

    @TableField(exist = false)
    private String timeEnd;

    @TableField(exist = false)
    private List<DramaInfoInferenceEntity> inferences;

    public String getIndex() {
        if (Objects.nonNull(getParaIndex()) && Objects.nonNull(getSentIndex())) {
            return getParaIndex() + "-" + getSentIndex();
        }
        return null;
    }

    public Integer getParaIndex() {
        return CollectionUtils.isEmpty(inferences) ? 0 : inferences.stream().mapToInt(DramaInfoInferenceEntity::getTextId).min().getAsInt();
    }

    public Integer getSentIndex() {
        return CollectionUtils.isEmpty(inferences) ? 0 : inferences.stream().mapToInt(DramaInfoInferenceEntity::getTextId).max().getAsInt();
    }

    public List<String> getText() {
        return CollectionUtils.isEmpty(inferences) ? new ArrayList<>() : inferences.stream().map(DramaInfoInferenceEntity::getText).collect(Collectors.toList());
    }

    public String getTimeStart() {
        return String.valueOf(CollectionUtils.isEmpty(inferences) ? 0 : inferences.stream().mapToInt(o -> Integer.parseInt(o.getTimeStart())).min().getAsInt());
    }

    public String getTimeEnd() {
        return String.valueOf(CollectionUtils.isEmpty(inferences) ? 0 : inferences.stream().mapToInt(o -> Integer.parseInt(o.getTimeEnd())).max().getAsInt());
    }
}
