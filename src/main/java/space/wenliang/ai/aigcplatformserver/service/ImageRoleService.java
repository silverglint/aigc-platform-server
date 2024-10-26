package space.wenliang.ai.aigcplatformserver.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.bean.GroupCount;
import space.wenliang.ai.aigcplatformserver.entity.ImageRoleEntity;
import space.wenliang.ai.aigcplatformserver.entity.ImageRoleEntity;
import space.wenliang.ai.aigcplatformserver.mapper.ImageRoleMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * date   2024/10/24
 */
@Service
public class ImageRoleService extends ServiceImpl<ImageRoleMapper, ImageRoleEntity> {

    public Map<String, Integer> chapterRoleGroupCount() {
        return baseMapper.chapterRoleGroupCount().stream()
                .collect(Collectors.toMap(GroupCount::getGroup1, GroupCount::getCount1));
    }

    public void deleteByChapterId(String chapterId) {
        this.remove(new LambdaQueryWrapper<ImageRoleEntity>()
                .eq(ImageRoleEntity::getChapterId, chapterId));
    }

    public List<ImageRoleEntity> getByChapterId(String chapterId) {
        return this.list(new LambdaQueryWrapper<ImageRoleEntity>()
                .eq(ImageRoleEntity::getChapterId, chapterId));
    }

    public void deleteByProjectId(String projectId) {
        this.remove(new LambdaQueryWrapper<ImageRoleEntity>()
                .eq(ImageRoleEntity::getProjectId, projectId));
    }

}
