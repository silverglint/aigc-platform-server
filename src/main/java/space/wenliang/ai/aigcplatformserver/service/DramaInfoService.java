package space.wenliang.ai.aigcplatformserver.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.vavr.Tuple2;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import space.wenliang.ai.aigcplatformserver.bean.DramaSummary;
import space.wenliang.ai.aigcplatformserver.entity.DramaInfoEntity;
import space.wenliang.ai.aigcplatformserver.entity.TextChapterEntity;
import space.wenliang.ai.aigcplatformserver.mapper.DramaInfoMapper;
import space.wenliang.ai.aigcplatformserver.util.ChapterUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * date   2024/10/24
 */
@Service
@RequiredArgsConstructor
public class DramaInfoService extends ServiceImpl<DramaInfoMapper, DramaInfoEntity> {
    private final DramaInfoMapper dramaInfoMapper;
    @Value("${spring.profiles.active:}")
    private String activeProfiles;


    public Map<String, DramaSummary> chapterSummaryMap() {
        List<DramaSummary> dramaSummaries = new ArrayList<>();

        if (activeProfiles.contains("mysql")) {
            dramaSummaries = dramaInfoMapper.dramaSummary4MySQL();
        }
        if (activeProfiles.contains("sqlite")) {
            dramaSummaries = dramaInfoMapper.dramaSummary4SQLite();
        }

        return dramaSummaries
                .stream()
                .collect(Collectors.toMap(DramaSummary::getChapterId, Function.identity()));
    }


    public void deleteByChapterId(String chapterId) {
        this.remove(new LambdaQueryWrapper<DramaInfoEntity>()
                .eq(DramaInfoEntity::getChapterId, chapterId));
    }


    public List<DramaInfoEntity> getByChapterId(String chapterId) {
        return this.list(new LambdaQueryWrapper<DramaInfoEntity>()
                .eq(DramaInfoEntity::getChapterId, chapterId)
                .orderByAsc(DramaInfoEntity::getTextSort)
                .orderByAsc(DramaInfoEntity::getId));
    }


    public List<DramaInfoEntity> buildChapterInfos(TextChapterEntity textChapterEntity) {
        if (Objects.isNull(textChapterEntity) || StringUtils.isBlank(textChapterEntity.getContent())) {
            return new ArrayList<>();
        }

        List<String> dialoguePatterns = StringUtils.isBlank(textChapterEntity.getDialoguePattern())
                ? List.of()
                : List.of(textChapterEntity.getDialoguePattern());

        List<DramaInfoEntity> chapterInfoEntities = new ArrayList<>();

        int paraIndex = 0;

        for (String line : textChapterEntity.getContent().split("\n")) {
            int sentIndex = 0;

            List<Tuple2<Boolean, String>> chapterInfoTuple2s = ChapterUtils.dialogueSplit(line, dialoguePatterns);
            if (CollectionUtils.isEmpty(chapterInfoTuple2s)) {
                continue;
            }

            for (Tuple2<Boolean, String> chapterInfoTuple2 : chapterInfoTuple2s) {

                DramaInfoEntity chapterInfoEntity = new DramaInfoEntity();
                chapterInfoEntity.setProjectId(textChapterEntity.getProjectId());
                chapterInfoEntity.setChapterId(textChapterEntity.getChapterId());
                chapterInfoEntity.setParaIndex(paraIndex);
                chapterInfoEntity.setSentIndex(sentIndex);
                chapterInfoEntity.setText(chapterInfoTuple2._2);

                chapterInfoEntity.setRole("旁白");

                chapterInfoEntities.add(chapterInfoEntity);

                sentIndex++;
            }

            paraIndex++;
        }

        return chapterInfoEntities;
    }


    public void deleteByProjectId(String projectId) {
        this.remove(new LambdaQueryWrapper<DramaInfoEntity>()
                .eq(DramaInfoEntity::getProjectId, projectId));
    }


    public void audioModelReset(List<Integer> ids) {
        this.update(new LambdaUpdateWrapper<DramaInfoEntity>()
                .in(DramaInfoEntity::getId, ids));
    }
}
