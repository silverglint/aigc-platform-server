package space.wenliang.ai.aigcplatformserver.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import space.wenliang.ai.aigcplatformserver.entity.DramaInfoInferenceEntity;
import space.wenliang.ai.aigcplatformserver.mapper.DramaInfoInferenceMapper;
/**
  * @author Administrator
  * date   2024/10/26
  */
@Service
public class DramaInfoInferenceService extends ServiceImpl<DramaInfoInferenceMapper, DramaInfoInferenceEntity> {

    public void deleteByDramaInfoId(Integer dramaInfoId) {
        this.remove(new LambdaQueryWrapper<DramaInfoInferenceEntity>()
                .eq(DramaInfoInferenceEntity::getDramaInfoId, dramaInfoId));
    }

    public void deleteByChapterId(String chapterId) {
        this.remove(new LambdaQueryWrapper<DramaInfoInferenceEntity>()
                .eq(DramaInfoInferenceEntity::getChapterId, chapterId));
    }
}
