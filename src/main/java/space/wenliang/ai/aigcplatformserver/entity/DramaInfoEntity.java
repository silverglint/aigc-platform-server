package space.wenliang.ai.aigcplatformserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Objects;

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

    @TableField(value = "para_index")
    private Integer paraIndex;

    @TableField(value = "sent_index")
    private Integer sentIndex;

    @TableField(value = "text_id")
    private String textId;

    @TableField(value = "\"text\"")
    private String text;

    @TableField(value = "text_lang")
    private String textLang;

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

    @TableField(value = "time_start")
    private String timeStart;

    @TableField(value = "time_end")
    private String timeEnd;

    @TableField("image_task_state")
    private int imageTaskState;

    public String getIndex() {
        if (Objects.nonNull(paraIndex) && Objects.nonNull(sentIndex)) {
            return paraIndex + "-" + sentIndex;
        }
        return null;
    }
}
