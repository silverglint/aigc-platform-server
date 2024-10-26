package space.wenliang.ai.aigcplatformserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
  * @author Administrator
  * date   2024/10/26
  */
@Data
@TableName(value = "drama_info_inference")
public class DramaInfoInferenceEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "project_id")
    private String projectId;

    @TableField(value = "chapter_id")
    private String chapterId;

    @TableField(value = "drama_info_id")
    private Integer dramaInfoId;

    @TableField(value = "\"text\"")
    private String text;

    @TableField(value = "text_id")
    private Integer textId;

    @TableField(value = "time_start")
    private String timeStart;

    @TableField(value = "time_end")
    private String timeEnd;
}
