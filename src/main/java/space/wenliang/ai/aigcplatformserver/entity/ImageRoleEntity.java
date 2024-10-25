package space.wenliang.ai.aigcplatformserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
  * @author Administrator
  * date   2024/10/24
  */
@Data
@TableName(value = "image_role")
public class ImageRoleEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "project_id")
    private String projectId;

    @TableField(value = "chapter_id")
    private String chapterId;

    @TableField(value = "\"role\"")
    private String role;

    @TableField(value = "image_prompt")
    private String imagePrompt;

    @TableField(exist = false)
    private Long roleCount;

    @TableField(exist = false)
    private Boolean coverCommonRole;
}
