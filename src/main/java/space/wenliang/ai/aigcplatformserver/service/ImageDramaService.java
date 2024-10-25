package space.wenliang.ai.aigcplatformserver.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.bean.GroupCount;
import space.wenliang.ai.aigcplatformserver.entity.ImageDramaEntity;
import space.wenliang.ai.aigcplatformserver.entity.ImageDramaEntity;
import space.wenliang.ai.aigcplatformserver.mapper.ImageChapterMapper;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * date   2024/10/23
 */
@Service
public class ImageDramaService extends ServiceImpl<ImageChapterMapper, ImageDramaEntity> {

    public Map<String, Integer> chapterCount() {
        return baseMapper.projectGroupCount().stream()
                .collect(Collectors.toMap(GroupCount::getGroup1, GroupCount::getCount1));
    }

    public void deleteByChapterId(String chapterId) {
        this.remove(new LambdaQueryWrapper<ImageDramaEntity>()
                .eq(ImageDramaEntity::getChapterId, chapterId));
    }

    public ImageDramaEntity getImageDramaAndContent(String projectId, String chapterId) {
        return this.getOne(new LambdaQueryWrapper<ImageDramaEntity>()
                .eq(ImageDramaEntity::getProjectId, projectId)
                .eq(ImageDramaEntity::getChapterId, chapterId));
    }

    public ImageDramaEntity getByChapterId(String chapterId) {
        return this.getOne(new LambdaQueryWrapper<ImageDramaEntity>()
                .eq(ImageDramaEntity::getChapterId, chapterId));
    }

    public void deleteByProjectId(String projectId) {
        this.remove(new LambdaQueryWrapper<ImageDramaEntity>()
                .eq(ImageDramaEntity::getProjectId, projectId));
    }
}
