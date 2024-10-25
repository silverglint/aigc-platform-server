package space.wenliang.ai.aigcplatformserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import space.wenliang.ai.aigcplatformserver.bean.GroupCount;
import space.wenliang.ai.aigcplatformserver.entity.ImageDramaEntity;

import java.util.List;

/**
  * @author Administrator
  * date   2024/10/23
  */
public interface ImageChapterMapper extends BaseMapper<ImageDramaEntity> {

    @Select("select project_id as group1, count(*) as count1 from image_drama group by project_id")
    List<GroupCount> projectGroupCount();
}
