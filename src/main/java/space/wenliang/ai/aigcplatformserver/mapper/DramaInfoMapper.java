package space.wenliang.ai.aigcplatformserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import space.wenliang.ai.aigcplatformserver.bean.DramaSummary;
import space.wenliang.ai.aigcplatformserver.entity.DramaInfoEntity;

import java.util.List;

/**
  * @author Administrator
  * date   2024/10/24
  */
public interface DramaInfoMapper extends BaseMapper<DramaInfoEntity> {


    @Select("""
            select chapter_id                                         as chapter_id,
                   sum(char_length(text))                                  as word_count,
                   count(*)                                           as text_count,
                   max(audio_task_state)                              as max_task_state
            from drama_info
            group by chapter_id
            """)
    List<DramaSummary> dramaSummary4MySQL();

    List<DramaSummary> dramaSummary4SQLite();

    List<DramaInfoEntity> getByChapterId(@Param("chapterId") String chapterId);

    DramaInfoEntity queryDramaInfo(@Param("dramaInfoId") int dramaInfoId);
}
