package space.wenliang.ai.aigcplatformserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
  * @author Administrator
  * date   2024/10/23
  */
@Data
@TableName(value = "image_drama")
public class ImageDramaEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "chapter_id")
    private String chapterId;

    @TableField(value = "project_id")
    private String projectId;

    @TableField(value = "chapter_name")
    private String chapterName;

    @TableField(value = "content")
    private String content;

    @TableField(value = "sort_order")
    private Integer sortOrder;

    @TableField(exist = false)
    private Integer wordNum;

    @TableField(exist = false)
    private Integer textNum;

    @TableField(exist = false)
    private Integer dialogueNum;

    @TableField(exist = false)
    private Integer roleNum;

    @TableField(exist = false)
    private Integer audioTaskState;
}
