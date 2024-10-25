package space.wenliang.ai.aigcplatformserver.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.entity.ImageRoleInferenceEntity;
import space.wenliang.ai.aigcplatformserver.entity.ImageRoleInferenceEntity;
import space.wenliang.ai.aigcplatformserver.mapper.ImageRoleInferenceMapper;

import java.util.List;

/**
 * @author Administrator
 * date   2024/10/24
 */
@Service
public class ImageRoleInferenceService extends ServiceImpl<ImageRoleInferenceMapper, ImageRoleInferenceEntity> {
    public void deleteByChapterId(String chapterId) {
        this.remove(new LambdaQueryWrapper<ImageRoleInferenceEntity>()
                .eq(ImageRoleInferenceEntity::getChapterId, chapterId));
    }

    public void deleteByProjectId(String projectId) {
        this.remove(new LambdaQueryWrapper<ImageRoleInferenceEntity>()
                .eq(ImageRoleInferenceEntity::getProjectId, projectId));
    }

    public List<ImageRoleInferenceEntity> getByChapterId(String chapterId) {
        return this.list(new LambdaQueryWrapper<ImageRoleInferenceEntity>()
                .eq(ImageRoleInferenceEntity::getChapterId, chapterId));
    }
}
