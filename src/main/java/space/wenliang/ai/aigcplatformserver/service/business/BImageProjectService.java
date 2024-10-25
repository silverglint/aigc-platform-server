package space.wenliang.ai.aigcplatformserver.service.business;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.bean.ImageProject;
import space.wenliang.ai.aigcplatformserver.entity.ImageDramaEntity;
import space.wenliang.ai.aigcplatformserver.entity.ImageProjectEntity;
import space.wenliang.ai.aigcplatformserver.exception.BizException;
import space.wenliang.ai.aigcplatformserver.service.ImageDramaService;
import space.wenliang.ai.aigcplatformserver.service.ImageProjectService;
import space.wenliang.ai.aigcplatformserver.util.IdUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Administrator
 * date   2024/10/23
 */
@Service
@RequiredArgsConstructor
public class BImageProjectService {

    private final ImageDramaService imageDramaService;
    private final ImageProjectService imageProjectService;

    public List<ImageProject> projectList() {
        List<ImageProjectEntity> textProjectEntities = imageProjectService.list(new LambdaQueryWrapper<ImageProjectEntity>()
                .select(ImageProjectEntity.class, entityClass -> !entityClass.getColumn().equals("content")));

        Map<String, Integer> countMap = imageDramaService.chapterCount();

        return textProjectEntities.stream().map(e -> {
            ImageProject textProject = new ImageProject();
            BeanUtils.copyProperties(e, textProject);
            textProject.setChapterCount(countMap.get(e.getProjectId()));
            return textProject;
        }).toList();
    }


    public ImageProjectEntity getByProjectId(String projectId) {
        return imageProjectService.getOne(new LambdaQueryWrapper<ImageProjectEntity>()
                .select(ImageProjectEntity.class, entityClass -> !entityClass.getColumn().equals("content"))
                .eq(ImageProjectEntity::getProjectId, projectId));
    }


    public void createProject(String project, String projectType, String content) {
        ImageProjectEntity entity = imageProjectService.getByProjectName(project);
        if (Objects.nonNull(entity)) {
            throw new BizException("已存在[" + project + "]项目");
        }

        ImageProjectEntity save = new ImageProjectEntity();
        String projectId = IdUtils.uuid();
        save.setProjectId(projectId);
        save.setProjectName(project);
        save.setProjectType(projectType);
        save.setContent(content);
        imageProjectService.save(save);

        if (StringUtils.equals(projectType, "short_text")) {
            ImageDramaEntity imageDramaEntity = new ImageDramaEntity();
            imageDramaEntity.setProjectId(projectId);
            imageDramaEntity.setChapterId(IdUtils.uuid());
            imageDramaEntity.setChapterName("单章节");
            imageDramaEntity.setContent(content);

            imageDramaService.save(imageDramaEntity);
        }
    }


    public void updateProject(ImageProjectEntity imageProjectEntity) {

    }


    public void deleteProject(ImageProjectEntity imageProjectEntity) throws IOException {

    }


    public List<String> tmpChapterSplit(String projectId, String chapterPattern, String dialoguePattern) {
        return null;
    }


    public void chapterSplit(String projectId, String chapterPattern, String dialoguePattern) {

    }
}
