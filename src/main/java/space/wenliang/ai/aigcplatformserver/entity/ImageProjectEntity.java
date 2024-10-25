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
@TableName(value = "image_project")
public class ImageProjectEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "project_id")
    private String projectId;

    @TableField(value = "project_name")
    private String projectName;

    @TableField(value = "project_type")
    private String projectType;

    @TableField(value = "content")
    private String content;
}
