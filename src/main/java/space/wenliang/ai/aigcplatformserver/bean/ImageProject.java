package space.wenliang.ai.aigcplatformserver.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;
import space.wenliang.ai.aigcplatformserver.entity.ImageProjectEntity;

/**
 * @author Administrator
 * date   2024/10/23
 */
@Getter
@Setter
public class ImageProject extends ImageProjectEntity {

    @TableField(exist = false)
    private Integer chapterCount;
}
