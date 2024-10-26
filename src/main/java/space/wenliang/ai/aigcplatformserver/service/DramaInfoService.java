package space.wenliang.ai.aigcplatformserver.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.bean.DramaSummary;
import space.wenliang.ai.aigcplatformserver.entity.DramaInfoEntity;
import space.wenliang.ai.aigcplatformserver.entity.DramaInfoInferenceEntity;
import space.wenliang.ai.aigcplatformserver.entity.ImageDramaEntity;
import space.wenliang.ai.aigcplatformserver.mapper.DramaInfoMapper;
import space.wenliang.ai.aigcplatformserver.util.srt.SRT;
import space.wenliang.ai.aigcplatformserver.util.srt.SrtUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
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
        return dramaInfoMapper.getByChapterId(chapterId);
    }


    public List<DramaInfoEntity> buildDramaInfos(ImageDramaEntity imageDramaEntity) {
        if (Objects.isNull(imageDramaEntity) || StringUtils.isBlank(imageDramaEntity.getContent())) {
            return new ArrayList<>();
        }

        List<DramaInfoEntity> chapterInfoEntities = new ArrayList<>();

        TreeMap<Integer, SRT> srt = SrtUtils.parseSrt(imageDramaEntity.getContent());

        srt.forEach((integer, srt1) -> {
            DramaInfoEntity dramaInfoEntity = new DramaInfoEntity();
            DramaInfoInferenceEntity inference = new DramaInfoInferenceEntity();
            dramaInfoEntity.setProjectId(imageDramaEntity.getProjectId());
            dramaInfoEntity.setChapterId(imageDramaEntity.getChapterId());
            dramaInfoEntity.setTextSort(integer);
            inference.setProjectId(imageDramaEntity.getProjectId());
            inference.setChapterId(imageDramaEntity.getChapterId());
            inference.setDramaInfoId(dramaInfoEntity.getId());
            inference.setTextId(srt1.getId());
            inference.setText(srt1.getSrtBody());
            inference.setTimeStart(srt1.getBeginTime());
            inference.setTimeEnd(srt1.getEndTime());

            dramaInfoEntity.setInferences(List.of(inference));

            dramaInfoEntity.setRole("未知");

            chapterInfoEntities.add(dramaInfoEntity);
        });

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
