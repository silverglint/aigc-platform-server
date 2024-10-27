package space.wenliang.ai.aigcplatformserver.service.business;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import space.wenliang.ai.aigcplatformserver.bean.ControlsUpdate;
import space.wenliang.ai.aigcplatformserver.bean.UpdateModelInfo;
import space.wenliang.ai.aigcplatformserver.bean.comfyui.FluxBaseParam;
import space.wenliang.ai.aigcplatformserver.common.AudioTaskStateConstants;
import space.wenliang.ai.aigcplatformserver.config.EnvConfig;
import space.wenliang.ai.aigcplatformserver.entity.DramaInfoEntity;
import space.wenliang.ai.aigcplatformserver.entity.DramaInfoInferenceEntity;
import space.wenliang.ai.aigcplatformserver.entity.ImageCommonRoleEntity;
import space.wenliang.ai.aigcplatformserver.entity.ImageDramaEntity;
import space.wenliang.ai.aigcplatformserver.entity.ImageProjectEntity;
import space.wenliang.ai.aigcplatformserver.entity.ImageRoleEntity;
import space.wenliang.ai.aigcplatformserver.service.DramaInfoInferenceService;
import space.wenliang.ai.aigcplatformserver.service.DramaInfoService;
import space.wenliang.ai.aigcplatformserver.service.ImageCommonRoleService;
import space.wenliang.ai.aigcplatformserver.service.ImageDramaService;
import space.wenliang.ai.aigcplatformserver.service.ImageProjectService;
import space.wenliang.ai.aigcplatformserver.service.ImageRoleService;
import space.wenliang.ai.aigcplatformserver.service.cache.GlobalSettingService;
import space.wenliang.ai.aigcplatformserver.service.comfyui.ComfyuiTaskService;
import space.wenliang.ai.aigcplatformserver.socket.GlobalWebSocketHandler;
import space.wenliang.ai.aigcplatformserver.socket.TextProjectWebSocketHandler;
import space.wenliang.ai.aigcplatformserver.util.FileUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BDramaInfoService {

    public static final LinkedBlockingDeque<DramaInfoEntity> audioCreateTaskQueue = new LinkedBlockingDeque<>();

    private final EnvConfig envConfig;
    private final ComfyuiTaskService comfyuiTaskService;

    private final ImageRoleService imageRoleService;
    private final ImageProjectService imageProjectService;
    private final ImageDramaService imageDramaService;
    private final DramaInfoService dramaInfoService;
    private final DramaInfoInferenceService dramaInfoInferenceService;
    private final ImageCommonRoleService imageCommonRoleService;

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

                List<ImageCommonRoleEntity> commonRoleEntities = imageCommonRoleService.getByProjectId(projectId);
                Optional<ImageCommonRoleEntity> asideRoleOptional = commonRoleEntities.stream()
                        .filter(r -> StringUtils.equals(r.getRole(), "未知"))
                        .findAny();

                ImageRoleEntity imageRoleEntity = new ImageRoleEntity();
                imageRoleEntity.setProjectId(projectId);
                imageRoleEntity.setChapterId(chapterId);
                imageRoleEntity.setRole("未知");

                if (asideRoleOptional.isPresent()) {
                    ImageCommonRoleEntity textCommonRoleEntity = asideRoleOptional.get();


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

    public DramaInfoEntity queryDramaInfo(int dramaInfoId) {
        return dramaInfoService.queryDramaInfo(dramaInfoId);
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

    public void addImageCreateTask(DramaInfoEntity dramaInfo) {
        ImageProjectEntity imageProject = imageProjectService.getByProjectId(dramaInfo.getProjectId());
        ImageDramaEntity imageDrama = imageDramaService.getImageDramaAndContent(dramaInfo.getProjectId(), dramaInfo.getChapterId());
        DramaInfoEntity entity = dramaInfoService.getById(dramaInfo.getId());
        comfyuiTaskService.submitFluxBaseTask(FluxBaseParam.builder()
                .latentBatchSize(2).imgId(entity.getIndex())
                .prompt(entity.getImagePrompt())
                .outputPath(envConfig.buildProjectPath(
                        "image",
                        FileUtils.fileNameFormat(imageProject.getProjectName()),
                        FileUtils.fileNameFormat(imageDrama.getChapterName())).toString())
                .build());
//        sendAudioGenerateSummaryMsg(dramaInfo.getProjectId(), dramaInfo.getChapterId());
    }


    public void startCreateImage(String projectId, String chapterId, String actionType, List<Integer> chapterInfoIds) {
        ImageProjectEntity imageProject = imageProjectService.getByProjectId(projectId);
        ImageDramaEntity imageDrama = imageDramaService.getImageDramaAndContent(projectId, chapterId);
        List<ImageRoleEntity> roles = imageRoleService.getByChapterId(chapterId);
        List<ImageCommonRoleEntity> commonRoles = imageCommonRoleService.getByProjectId(projectId);
        Map<String, String> promptMap = commonRoles.stream().collect(Collectors.toMap(ImageCommonRoleEntity::getRole, ImageCommonRoleEntity::getImagePrompt));
        roles.forEach(role -> promptMap.put(role.getRole(), role.getImagePrompt()));

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
                }).toList();
        for (DramaInfoEntity entity : entities) {
            Path path = envConfig.buildProjectPath(
                    "image",
                    FileUtils.fileNameFormat(imageProject.getProjectName()),
                    FileUtils.fileNameFormat(imageDrama.getChapterName()),
                    "previewImages");
            int latentBatchSize = 2;
            StringBuilder rolePrompt = new StringBuilder();
            if (StringUtils.isNotEmpty(entity.getRole())) {
                for (String role : entity.getRole().split(",")) {
                    rolePrompt.append(promptMap.get(role)).append(",");
                }
            }
            FluxBaseParam param = FluxBaseParam.builder()
                    .latentBatchSize(latentBatchSize).imgId(entity.getIndex()).projectId(projectId).chapterId(chapterId)
                    .prompt(rolePrompt + entity.getImagePrompt())
                    .outputPath(path.toString())
                    .build();

            if (FileUtil.isDirectory(path)) {
                List<String> imageNames = FileUtil.listFileNames(path.toString());
                imageNames.stream().filter(name -> name.startsWith(entity.getIndex())).forEach(name -> FileUtil.del(path.resolve(name)));
            } else {
                FileUtil.mkdir(path);
            }
            StringBuilder imgFileNames = new StringBuilder();
            for (int i = 0; i < latentBatchSize; i++) {
                imgFileNames.append(entity.getIndex()).append("_").append(StringUtils.leftPad(String.valueOf(i + 1), 2, "0")).append(".png").append(";");
            }
            imgFileNames.deleteCharAt(imgFileNames.length() - 1);
            entity.setPreviewImageFiles(imgFileNames.toString());
            comfyuiTaskService.submitFluxBaseTask(param);
        }
        dramaInfoService.updateBatchById(entities);
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

    public void imageSave(String projectId, String chapterId, String imageId) {

    }
}
