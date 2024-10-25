package space.wenliang.ai.aigcplatformserver.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.entity.ImageProjectEntity;
import space.wenliang.ai.aigcplatformserver.mapper.ImageProjectMapper;

/**
 * @author Administrator
 * date   2024/10/23
 */
@RequiredArgsConstructor
@Service
public class ImageProjectService extends ServiceImpl<ImageProjectMapper, ImageProjectEntity> {


    public ImageProjectEntity getByProjectName(String projectName) {
        return this.getOne(new LambdaQueryWrapper<ImageProjectEntity>()
                .select(ImageProjectEntity.class, entityClass -> !entityClass.getColumn().equals("content"))
                .eq(ImageProjectEntity::getProjectName, projectName));
    }

    public void deleteByProjectId(String projectId) {
        this.remove(new LambdaQueryWrapper<ImageProjectEntity>()
                .eq(ImageProjectEntity::getProjectId, projectId));
    }

    public ImageProjectEntity getAndContentByProjectId(String projectId) {
        return this.getOne(new LambdaQueryWrapper<ImageProjectEntity>()
                .eq(ImageProjectEntity::getProjectId, projectId));
    }

    public ImageProjectEntity getByProjectId(String projectId) {
        return this.getOne(new LambdaQueryWrapper<ImageProjectEntity>()
                .select(ImageProjectEntity.class, entityClass -> !entityClass.getColumn().equals("content"))
                .eq(ImageProjectEntity::getProjectId, projectId));
    }
}
