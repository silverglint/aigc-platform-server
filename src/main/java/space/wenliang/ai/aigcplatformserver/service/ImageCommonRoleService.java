package space.wenliang.ai.aigcplatformserver.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.entity.ImageCommonRoleEntity;
import space.wenliang.ai.aigcplatformserver.mapper.ImageCommonRoleMapper;

import java.util.List;

/**
 * @author Administrator
 * date   2024/10/26
 */
@Service
public class ImageCommonRoleService extends ServiceImpl<ImageCommonRoleMapper, ImageCommonRoleEntity> {

    public List<ImageCommonRoleEntity> getByProjectId(String projectId) {
        return super.list(new LambdaQueryWrapper<ImageCommonRoleEntity>()
                .eq(ImageCommonRoleEntity::getProjectId, projectId));
    }

    public void deleteByProjectId(String projectId) {
        this.remove(new LambdaQueryWrapper<ImageCommonRoleEntity>()
                .eq(ImageCommonRoleEntity::getProjectId, projectId));
    }
}
