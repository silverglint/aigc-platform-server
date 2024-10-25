package space.wenliang.ai.aigcplatformserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import space.wenliang.ai.aigcplatformserver.bean.GroupCount;
import space.wenliang.ai.aigcplatformserver.entity.ImageRoleEntity;

import java.util.List;

/**
  * @author Administrator
  * date   2024/10/24
  */
public interface ImageRoleMapper extends BaseMapper<ImageRoleEntity> {

    @Select("""
            select chapter_id as group1, count(*) as count1
            from image_role
            group by chapter_id
            """)
    List<GroupCount> chapterRoleGroupCount();
}
