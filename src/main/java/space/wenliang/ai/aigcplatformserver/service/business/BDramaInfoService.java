package space.wenliang.ai.aigcplatformserver.service.business;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import space.wenliang.ai.aigcplatformserver.ai.audio.AudioCreator;
import space.wenliang.ai.aigcplatformserver.bean.ControlsUpdate;
import space.wenliang.ai.aigcplatformserver.bean.UpdateModelInfo;
import space.wenliang.ai.aigcplatformserver.common.AudioTaskStateConstants;
import space.wenliang.ai.aigcplatformserver.config.EnvConfig;
import space.wenliang.ai.aigcplatformserver.entity.DramaInfoEntity;
import space.wenliang.ai.aigcplatformserver.entity.DramaInfoInferenceEntity;
import space.wenliang.ai.aigcplatformserver.entity.ImageDramaEntity;
import space.wenliang.ai.aigcplatformserver.entity.ImageRoleEntity;
import space.wenliang.ai.aigcplatformserver.entity.TextCommonRoleEntity;
import space.wenliang.ai.aigcplatformserver.service.DramaInfoInferenceService;
import space.wenliang.ai.aigcplatformserver.service.DramaInfoService;
import space.wenliang.ai.aigcplatformserver.service.ImageDramaService;
import space.wenliang.ai.aigcplatformserver.service.ImageProjectService;
import space.wenliang.ai.aigcplatformserver.service.ImageRoleService;
import space.wenliang.ai.aigcplatformserver.service.TextCommonRoleService;
import space.wenliang.ai.aigcplatformserver.service.cache.GlobalSettingService;
import space.wenliang.ai.aigcplatformserver.socket.GlobalWebSocketHandler;
import space.wenliang.ai.aigcplatformserver.socket.TextProjectWebSocketHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingDeque;

@Slf4j
@Service
@RequiredArgsConstructor
public class BDramaInfoService {

    public static final LinkedBlockingDeque<DramaInfoEntity> audioCreateTaskQueue = new LinkedBlockingDeque<>();

    private final EnvConfig envConfig;
    private final AudioCreator audioCreator;

    private final ImageRoleService imageRoleService;
    private final ImageProjectService imageProjectService;
    private final ImageDramaService imageDramaService;
    private final DramaInfoService dramaInfoService;
    private final DramaInfoInferenceService dramaInfoInferenceService;
    private final TextCommonRoleService textCommonRoleService;

    private final GlobalWebSocketHandler globalWebSocketHandler;
    private final TextProjectWebSocketHandler textProjectWebSocketHandler;
    private final GlobalSettingService globalSettingService;


    public List<DramaInfoEntity> chapterInfos(String projectId, String chapterId) {

        List<DramaInfoEntity> dramaInfos = dramaInfoService.getByChapterId(chapterId);

        if (CollectionUtils.isEmpty(dramaInfos)) {
            ImageDramaEntity imageDramaEntity = imageDramaService.getByChapterId(chapterId);

            List<DramaInfoEntity> dramaInfoEntities = dramaInfoService.buildDramaInfos(imageDramaEntity);

            dramaInfoService.deleteByChapterId(chapterId);
            dramaInfoInferenceService.deleteByChapterId(chapterId);
            imageRoleService.deleteByChapterId(chapterId);

            if (!CollectionUtils.isEmpty(dramaInfoEntities)) {

                List<TextCommonRoleEntity> commonRoleEntities = textCommonRoleService.getByProjectId(projectId);
                Optional<TextCommonRoleEntity> asideRoleOptional = commonRoleEntities.stream()
                        .filter(r -> StringUtils.equals(r.getRole(), "未知"))
                        .findAny();

                ImageRoleEntity imageRoleEntity = new ImageRoleEntity();
                imageRoleEntity.setProjectId(projectId);
                imageRoleEntity.setChapterId(chapterId);
                imageRoleEntity.setRole("未知");

                if (asideRoleOptional.isPresent()) {
                    TextCommonRoleEntity textCommonRoleEntity = asideRoleOptional.get();


                    dramaInfoEntities = dramaInfoEntities.stream()
                            .peek(c -> {
                            }).toList();
                }

                for (DramaInfoEntity dramaInfoEntity : dramaInfoEntities) {
                    dramaInfoService.save(dramaInfoEntity);
                    dramaInfoEntity.getInferences().forEach(o -> o.setDramaInfoId(dramaInfoEntity.getId()));
                    dramaInfoInferenceService.saveBatch(dramaInfoEntity.getInferences());
                }
                imageRoleService.save(imageRoleEntity);

                JSONObject j1 = new JSONObject();
                j1.put("type", "chapter_title_refresh,chapter_role_refresh");
                j1.put("state", "success");
                j1.put("projectId", projectId);
                j1.put("chapterId", chapterId);

                textProjectWebSocketHandler.sendMessageToProject(projectId, JSON.toJSONString(j1));
            } else {
                return new ArrayList<>();
            }

            dramaInfos = dramaInfoEntities;
        }

        return dramaInfos;
    }


    public void chapterInfoSort(List<DramaInfoEntity> chapterInfoEntities) {
        List<DramaInfoEntity> updateList = chapterInfoEntities.stream().map(c -> {
            DramaInfoEntity chapterInfoEntity = new DramaInfoEntity();
            chapterInfoEntity.setId(c.getId());
            chapterInfoEntity.setTextSort(c.getTextSort());
            return chapterInfoEntity;
        }).toList();
        dramaInfoService.updateBatchById(updateList);
    }


    public void audioModelChange(UpdateModelInfo updateModelInfo) {

        List<DramaInfoEntity> updateList = updateModelInfo.getIds().stream().map(id -> {
            DramaInfoEntity chapterInfoEntity = new DramaInfoEntity();
            chapterInfoEntity.setId(id);
            chapterInfoEntity.setImageTaskState(AudioTaskStateConstants.modified);


            if (StringUtils.isNotBlank(updateModelInfo.getAmMcParamsJson())) {
            }

            return chapterInfoEntity;
        }).toList();

        dramaInfoService.updateBatchById(updateList);
    }


    public void updateVolume(DramaInfoEntity chapterInfoEntity) {
        dramaInfoService.update(new LambdaUpdateWrapper<DramaInfoEntity>()
                .set(DramaInfoEntity::getImageTaskState, AudioTaskStateConstants.modified)
                .eq(DramaInfoEntity::getId, chapterInfoEntity.getId()));
    }


    public void updateSpeed(DramaInfoEntity chapterInfoEntity) {
        dramaInfoService.update(new LambdaUpdateWrapper<DramaInfoEntity>()
                .set(DramaInfoEntity::getImageTaskState, AudioTaskStateConstants.modified)
                .eq(DramaInfoEntity::getId, chapterInfoEntity.getId()));
    }


    public void updateInterval(DramaInfoEntity chapterInfoEntity) {
        dramaInfoService.update(new LambdaUpdateWrapper<DramaInfoEntity>()
                .set(DramaInfoEntity::getImageTaskState, AudioTaskStateConstants.modified)
                .eq(DramaInfoEntity::getId, chapterInfoEntity.getId()));
    }


    public void updateControls(ControlsUpdate controlsUpdate) {
        if (!CollectionUtils.isEmpty(controlsUpdate.getChapterInfoIds())) {
            List<DramaInfoEntity> updateList = controlsUpdate.getChapterInfoIds().stream()
                    .map(item -> {
                        DramaInfoEntity update = new DramaInfoEntity();
                        update.setId(item);
                        if (Objects.equals(controlsUpdate.getEnableVolume(), Boolean.TRUE)) {
                        }
                        if (Objects.equals(controlsUpdate.getEnableSpeed(), Boolean.TRUE)) {
                        }
                        if (Objects.equals(controlsUpdate.getEnableInterval(), Boolean.TRUE)) {
                        }
                        return update;
                    }).toList();
            dramaInfoService.updateBatchById(updateList);
        }
    }


    public void deleteChapterInfo(DramaInfoEntity chapterInfoEntity) {
        dramaInfoService.removeById(chapterInfoEntity.getId());
    }

    public void updateDramaInfo(DramaInfoEntity dramaInfoEntity) {
        dramaInfoService.updateById(dramaInfoEntity);
    }

    public void saveDramaInfoInference(DramaInfoInferenceEntity dramaInfoInferenceEntity) {
        dramaInfoInferenceService.saveOrUpdate(dramaInfoInferenceEntity);
    }

    public void deleteDramaInfoInference(DramaInfoInferenceEntity dramaInfoInferenceEntity) {
        dramaInfoInferenceService.removeById(dramaInfoInferenceEntity.getId());
    }

    public void addAudioCreateTask(DramaInfoEntity chapterInfoEntity) {
        DramaInfoEntity chapterInfo = dramaInfoService.getById(chapterInfoEntity.getId());
        if (Objects.nonNull(chapterInfo)) {
            audioCreateTaskQueue.add(chapterInfo);
        }

        sendAudioGenerateSummaryMsg(chapterInfoEntity.getProjectId(), chapterInfoEntity.getChapterId());
    }


    public void startCreateImage(String projectId, String chapterId, String actionType, List<Integer> chapterInfoIds) {
        List<DramaInfoEntity> entities = dramaInfoService.getByChapterId(chapterId)
                .stream()
                .filter(c -> {
                    if (StringUtils.equals(actionType, "all")) {
                        return true;
                    } else if (StringUtils.equals(actionType, "modified")) {
                        return !List.of(AudioTaskStateConstants.created, AudioTaskStateConstants.combined).contains(c.getImageTaskState());
                    } else {
                        return chapterInfoIds.contains(c.getId());
                    }
                })
                .toList();
        audioCreateTaskQueue.addAll(entities);

        sendAudioGenerateSummaryMsg(projectId, chapterId);
    }


    public void stopCreateAudio() {
        audioCreateTaskQueue.clear();
    }


    public List<DramaInfoEntity> chapterCondition(String projectId, String chapterId) {
        return dramaInfoService.getByChapterId(chapterId);
    }


    public DramaInfoEntity addChapterInfo(DramaInfoEntity dramaInfo) {
        DramaInfoEntity dramaInfoEntity = new DramaInfoEntity();
        List<DramaInfoInferenceEntity> inferences = dramaInfo.getInferences();
        dramaInfoEntity.setProjectId(dramaInfo.getProjectId());
        dramaInfoEntity.setChapterId(dramaInfo.getChapterId());
        dramaInfoEntity.setTextSort(dramaInfo.getTextSort());
        dramaInfoEntity.setImageTaskState(AudioTaskStateConstants.init);
        String aside = "未知";
        dramaInfoEntity.setRole(aside);

        List<DramaInfoEntity> dramaInfoEntityList = dramaInfoService.getByChapterId(dramaInfo.getChapterId());
        int i = 0;
        List<DramaInfoEntity> updateList = new ArrayList<>();
        for (DramaInfoEntity infoEntity : dramaInfoEntityList) {
            DramaInfoEntity save = new DramaInfoEntity();
            save.setId(infoEntity.getId());
            if (i >= Optional.ofNullable(dramaInfo.getTextSort()).orElse(0)) {
                save.setTextSort(i + 1);
            } else {
                save.setTextSort(i);
            }
            i++;

            updateList.add(save);

        }

        dramaInfoService.updateBatchById(updateList);
        dramaInfoService.save(dramaInfoEntity);
        inferences.forEach(o -> o.setDramaInfoId(dramaInfoEntity.getId()));
        dramaInfoInferenceService.saveBatch(inferences);

        return dramaInfoEntity;
    }


    private void sendAudioGenerateSummaryMsg(String projectId, String chapterId) {
        JSONObject j1 = new JSONObject();
        j1.put("type", "audio_generate_summary");
        j1.put("state", "success");
        j1.put("projectId", projectId);
        j1.put("chapterId", chapterId);
        j1.put("taskNum", audioCreateTaskQueue.size());

        List<String> creatingIds = new ArrayList<>();
        audioCreateTaskQueue.forEach(t -> creatingIds.add(t.getIndex()));
        j1.put("creatingIds", creatingIds);
        textProjectWebSocketHandler.sendMessageToProject(projectId, JSON.toJSONString(j1));
    }
}
