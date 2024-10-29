package space.wenliang.ai.aigcplatformserver.service.business;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import space.wenliang.ai.aigcplatformserver.ai.chat.AiService;
import space.wenliang.ai.aigcplatformserver.bean.AiResult;
import space.wenliang.ai.aigcplatformserver.bean.DramaAdd;
import space.wenliang.ai.aigcplatformserver.bean.DramaExportParam;
import space.wenliang.ai.aigcplatformserver.bean.DramaSummary;
import space.wenliang.ai.aigcplatformserver.bean.ImagePromptInferenceParam;
import space.wenliang.ai.aigcplatformserver.bean.ImageRoleInferenceData;
import space.wenliang.ai.aigcplatformserver.bean.ImageRoleInferenceParam;
import space.wenliang.ai.aigcplatformserver.bean.ProjectQuery;
import space.wenliang.ai.aigcplatformserver.bean.TextRoleChange;
import space.wenliang.ai.aigcplatformserver.bean.UpdateModelInfo;
import space.wenliang.ai.aigcplatformserver.common.AudioTaskStateConstants;
import space.wenliang.ai.aigcplatformserver.common.Page;
import space.wenliang.ai.aigcplatformserver.config.EnvConfig;
import space.wenliang.ai.aigcplatformserver.entity.AmModelConfigEntity;
import space.wenliang.ai.aigcplatformserver.entity.AmModelFileEntity;
import space.wenliang.ai.aigcplatformserver.entity.AmPromptAudioEntity;
import space.wenliang.ai.aigcplatformserver.entity.DramaInfoEntity;
import space.wenliang.ai.aigcplatformserver.entity.ImageCommonRoleEntity;
import space.wenliang.ai.aigcplatformserver.entity.ImageDramaEntity;
import space.wenliang.ai.aigcplatformserver.entity.ImageProjectEntity;
import space.wenliang.ai.aigcplatformserver.entity.ImageRoleEntity;
import space.wenliang.ai.aigcplatformserver.entity.ImageRoleInferenceEntity;
import space.wenliang.ai.aigcplatformserver.exception.BizException;
import space.wenliang.ai.aigcplatformserver.service.AmModelConfigService;
import space.wenliang.ai.aigcplatformserver.service.AmModelFileService;
import space.wenliang.ai.aigcplatformserver.service.AmPromptAudioService;
import space.wenliang.ai.aigcplatformserver.service.DramaInfoInferenceService;
import space.wenliang.ai.aigcplatformserver.service.DramaInfoService;
import space.wenliang.ai.aigcplatformserver.service.ImageCommonRoleService;
import space.wenliang.ai.aigcplatformserver.service.ImageDramaService;
import space.wenliang.ai.aigcplatformserver.service.ImageProjectService;
import space.wenliang.ai.aigcplatformserver.service.ImageRoleInferenceService;
import space.wenliang.ai.aigcplatformserver.service.ImageRoleService;
import space.wenliang.ai.aigcplatformserver.service.cache.GlobalSettingService;
import space.wenliang.ai.aigcplatformserver.socket.GlobalWebSocketHandler;
import space.wenliang.ai.aigcplatformserver.util.FileUtils;
import space.wenliang.ai.aigcplatformserver.util.IdUtils;
import space.wenliang.ai.aigcplatformserver.util.jianying.Draft;
import space.wenliang.ai.aigcplatformserver.util.jianying.FrameUtils;
import space.wenliang.ai.aigcplatformserver.util.jianying.materials.AudioMaterial;
import space.wenliang.ai.aigcplatformserver.util.jianying.materials.TextMaterial;
import space.wenliang.ai.aigcplatformserver.util.jianying.materials.VideoMaterial;
import space.wenliang.ai.aigcplatformserver.util.jianying.tracks.AudioTrack;
import space.wenliang.ai.aigcplatformserver.util.jianying.tracks.TextTrack;
import space.wenliang.ai.aigcplatformserver.util.jianying.tracks.VideoTrack;
import space.wenliang.ai.aigcplatformserver.util.srt.SRT;
import space.wenliang.ai.aigcplatformserver.util.srt.SrtUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BImageDramaService {

    private final EnvConfig envConfig;
    private final AiService aiService;
    private final ImageRoleService imageRoleService;
    private final ImageDramaService imageDramaService;
    private final DramaInfoService dramaInfoService;
    private final DramaInfoInferenceService dramaInfoInferenceService;
    private final ImageProjectService imageProjectService;
    private final ImageCommonRoleService imageCommonRoleService;
    private final ImageRoleInferenceService imageRoleInferenceService;
    private final AmModelFileService amModelFileService;
    private final AmModelConfigService amModelConfigService;
    private final AmPromptAudioService amPromptAudioService;
    private final GlobalWebSocketHandler globalWebSocketHandler;
    private final GlobalSettingService globalSettingService;


    public Page<ImageDramaEntity> pageChapters(ProjectQuery projectQuery) {
        Page<ImageDramaEntity> page = imageDramaService.page(
                Page.of(projectQuery.getCurrent(), projectQuery.getPageSize()),
                new LambdaQueryWrapper<ImageDramaEntity>()
                        .eq(ImageDramaEntity::getProjectId, projectQuery.getProjectId())
                        .orderByAsc(ImageDramaEntity::getSortOrder, ImageDramaEntity::getId));

        if (!CollectionUtils.isEmpty(page.getRecords())) {

            Map<String, DramaSummary> chapterSummaryMap = dramaInfoService.chapterSummaryMap();

            Map<String, Integer> chapterRoleCount = imageRoleService.chapterRoleGroupCount();

            List<ImageDramaEntity> list = page.getRecords().stream()
                    .peek(t -> {
                        DramaSummary chapterSummary = chapterSummaryMap.get(t.getChapterId());
                        if (Objects.nonNull(chapterSummary)) {
//                            t.setWordNum(chapterSummary.getWordCount());
//                            t.setTextNum(chapterSummary.getTextCount());
//                            t.setDialogueNum(chapterSummary.getDialogueCount());
//                            t.setAudioTaskState(chapterSummary.getMaxTaskState());
                        }
//                        t.setRoleNum(chapterRoleCount.get(t.getChapterId()));
                    }).toList();

            page.setRecords(list);
        }
        return page;
    }


    public List<ImageDramaEntity> chapters4Sort(String projectId) {
        return imageDramaService.list(new LambdaQueryWrapper<ImageDramaEntity>()
                        .select(ImageDramaEntity.class, entityClass -> !entityClass.getColumn().equals("content"))
                        .eq(ImageDramaEntity::getProjectId, projectId)
                        .orderByAsc(ImageDramaEntity::getSortOrder, ImageDramaEntity::getId))
                .stream()
                .toList();
    }


    public void deleteChapter(ImageDramaEntity textChapter) throws IOException {
        ImageProjectEntity project = imageProjectService.getByProjectId(textChapter.getProjectId());

        dramaInfoService.deleteByChapterId(textChapter.getChapterId());
        imageDramaService.deleteByChapterId(textChapter.getChapterId());

        imageRoleService.deleteByChapterId(textChapter.getChapterId());
        imageRoleInferenceService.deleteByChapterId(textChapter.getChapterId());

        FileUtils.deleteDirectoryAll(Path.of(
                envConfig.getProjectDir(),
                "text",
                FileUtils.fileNameFormat(project.getProjectName()),
                FileUtils.fileNameFormat(textChapter.getChapterName())
        ));
    }


    public void chapterEdit(ImageDramaEntity textChapter) {
        String chapterId = textChapter.getChapterId();

        ImageDramaEntity entity = imageDramaService.getByChapterId(chapterId);

        if (Objects.nonNull(entity) && StringUtils.isNotBlank(textChapter.getContent())) {

            saveDramaInfoEntities(textChapter);

            entity.setChapterName(textChapter.getChapterName());
            entity.setContent(textChapter.getContent());
            imageDramaService.updateById(entity);
        }
    }


    public void chapterAdd(DramaAdd chapterAdd) {
        ImageDramaEntity textChapter = chapterAdd.getImageDrama();
        List<ImageDramaEntity> sortChapters = chapterAdd.getSortDramas();

        String projectId = textChapter.getProjectId();
        String chapterId = IdUtils.uuid();

        textChapter.setChapterId(chapterId);

        if (StringUtils.isNotBlank(textChapter.getContent())) {

            saveDramaInfoEntities(textChapter);

            ImageDramaEntity save = new ImageDramaEntity();
            save.setProjectId(projectId);
            save.setChapterId(chapterId);
            save.setChapterName(textChapter.getChapterName());
            save.setContent(textChapter.getContent());
            save.setSortOrder(Optional.ofNullable(textChapter.getSortOrder()).orElse(0));

            imageDramaService.save(save);

            if (!CollectionUtils.isEmpty(sortChapters)) {
                List<ImageDramaEntity> saveList = sortChapters.stream()
                        .map(t -> {
                            ImageDramaEntity textChapterEntity = new ImageDramaEntity();
                            textChapterEntity.setId(t.getId());
                            textChapterEntity.setSortOrder(t.getSortOrder());
                            return textChapterEntity;
                        }).toList();
                imageDramaService.updateBatchById(saveList);
            }
        }
    }


    public void chapterSort(List<ImageDramaEntity> sortChapters) {
        if (!CollectionUtils.isEmpty(sortChapters)) {
            List<ImageDramaEntity> saveList = sortChapters.stream().map(t -> {
                ImageDramaEntity textChapterEntity = new ImageDramaEntity();
                textChapterEntity.setId(t.getId());
                textChapterEntity.setSortOrder(t.getSortOrder());
                return textChapterEntity;
            }).toList();
            imageDramaService.updateBatchById(saveList);
        }
    }


    public List<ImageRoleEntity> roles(String chapterId) {
        List<ImageRoleEntity> roleEntities = imageRoleService.getByChapterId(chapterId);

        if (!CollectionUtils.isEmpty(roleEntities)) {
            Map<String, Long> roleCountMap = dramaInfoService.getByChapterId(chapterId)
                    .stream()
                    .collect(Collectors.groupingBy(DramaInfoEntity::getRole, Collectors.counting()));

            List<ImageRoleEntity> deleteList = new ArrayList<>();

            roleEntities = roleEntities.stream()
                    .peek(r -> r.setRoleCount(Optional.ofNullable(roleCountMap.get(r.getRole())).orElse(0L)))
                    .filter(r -> {
                        if (r.getRoleCount() > 0) {
                            return true;
                        } else {
                            deleteList.add(r);
                            return false;
                        }
                    })
                    .toList();

            if (!CollectionUtils.isEmpty(deleteList)) {
                CompletableFuture.runAsync(() -> imageRoleService
                        .removeByIds(deleteList.stream().map(ImageRoleEntity::getId).toList()));
            }
        }

        return roleEntities;
    }


    public void updateRole(ImageRoleEntity textRoleEntity) {
        ImageRoleEntity cache = imageRoleService.getById(textRoleEntity.getId());

        imageRoleService.update(new LambdaUpdateWrapper<ImageRoleEntity>()
                .eq(ImageRoleEntity::getId, textRoleEntity.getId())
                .set(ImageRoleEntity::getRole, textRoleEntity.getRole()));

        dramaInfoService.update(new LambdaUpdateWrapper<DramaInfoEntity>()
                .set(DramaInfoEntity::getRole, textRoleEntity.getRole())
                .eq(DramaInfoEntity::getProjectId, textRoleEntity.getProjectId())
                .eq(DramaInfoEntity::getChapterId, textRoleEntity.getChapterId())
                .eq(DramaInfoEntity::getRole, cache.getRole()));
    }


    public void updateRoleModel(UpdateModelInfo updateModelInfo) {

        for (Integer id : updateModelInfo.getIds()) {
            ImageRoleEntity cache = imageRoleService.getById(id);

            AmModelFileEntity modelFile = amModelFileService.getByMfId(updateModelInfo.getAmMfId());
            AmModelConfigEntity modelConfig = amModelConfigService.getByMcId(updateModelInfo.getAmMcId());
            AmPromptAudioEntity promptAudio = amPromptAudioService.getByPaId(updateModelInfo.getAmPaId());

            List<DramaInfoEntity> updateList = dramaInfoService.list(new LambdaQueryWrapper<DramaInfoEntity>()
                    .eq(DramaInfoEntity::getProjectId, updateModelInfo.getProjectId())
                    .eq(DramaInfoEntity::getChapterId, updateModelInfo.getChapterId())
                    .eq(DramaInfoEntity::getRole, cache.getRole()));

            for (DramaInfoEntity chapterInfo : updateList) {
            }

            dramaInfoService.updateBatchById(updateList);

            ImageRoleEntity update = new ImageRoleEntity();
            update.setId(id);

            imageRoleService.updateById(update);
        }
    }


    public void roleCombine(String projectId, String chapterId, String fromRoleName, String toRoleName) {
        List<DramaInfoEntity> chapterInfoEntities = dramaInfoService.getByChapterId(chapterId)
                .stream()
                .filter(c -> StringUtils.equals(c.getRole(), fromRoleName))
                .toList();

        ImageRoleEntity toRole = imageRoleService.getOne(new LambdaQueryWrapper<ImageRoleEntity>()
                .eq(ImageRoleEntity::getProjectId, projectId)
                .eq(ImageRoleEntity::getChapterId, chapterId)
                .eq(ImageRoleEntity::getRole, toRoleName));


        if (!CollectionUtils.isEmpty(chapterInfoEntities) && Objects.nonNull(toRole)) {
            chapterInfoEntities = chapterInfoEntities.stream()
                    .peek(c -> {
//                        c.setAudioRoleInfo(toRole);
//                        c.setAudioModelInfo(toRole);
                    }).toList();

            dramaInfoService.saveOrUpdateBatch(chapterInfoEntities);
        }
    }


    public void textRoleChange(TextRoleChange textRoleChange) {
        List<DramaInfoEntity> chapterInfoEntities = dramaInfoService.listByIds(textRoleChange.getChapterInfoIds());

        ImageRoleEntity textRoleEntity = imageRoleService.getOne(new LambdaQueryWrapper<ImageRoleEntity>()
                .eq(ImageRoleEntity::getProjectId, textRoleChange.getProjectId())
                .eq(ImageRoleEntity::getChapterId, textRoleChange.getChapterId())
                .eq(ImageRoleEntity::getRole, textRoleChange.getFormRoleName()));

        for (DramaInfoEntity chapterInfoEntity : chapterInfoEntities) {
            chapterInfoEntity.setRole(textRoleChange.getFormRoleName());
            chapterInfoEntity.setImageTaskState(AudioTaskStateConstants.modified);


            if (StringUtils.equals(textRoleChange.getFromRoleType(), "role")) {

                if (Objects.nonNull(textRoleEntity) && Objects.equals(textRoleChange.getChangeModel(), Boolean.TRUE)) {
                }

            }

            if (StringUtils.equals(textRoleChange.getFromRoleType(), "commonRole")) {
                ImageCommonRoleEntity textCommonRoleEntity = imageCommonRoleService.getOne(
                        new LambdaQueryWrapper<ImageCommonRoleEntity>()
                                .eq(ImageCommonRoleEntity::getProjectId, textRoleChange.getProjectId())
                                .eq(ImageCommonRoleEntity::getRole, textRoleChange.getFormRoleName()));

                if (Objects.nonNull(textCommonRoleEntity) && Objects.equals(textRoleChange.getChangeModel(), Boolean.TRUE)) {
                }
            }
        }

        dramaInfoService.updateBatchById(chapterInfoEntities);

        if (Objects.isNull(textRoleEntity)) {
            ImageRoleEntity saveRole = new ImageRoleEntity();
            saveRole.setProjectId(textRoleChange.getProjectId());
            saveRole.setChapterId(textRoleChange.getChapterId());
            saveRole.setRole(textRoleChange.getFormRoleName());
            imageRoleService.save(saveRole);
        }
    }


    public Boolean saveToCommonRole(ImageRoleEntity textRoleEntity) {
//        List<ImageCommonRoleEntity> commonRoleEntities = textCommonRoleService.list(
//                new LambdaQueryWrapper<ImageCommonRoleEntity>()
//                        .eq(ImageCommonRoleEntity::getRole, textRoleEntity.getRole()));
//
//        if (!Objects.equals(textRoleEntity.getCoverCommonRole(), Boolean.TRUE)
//                && !CollectionUtils.isEmpty(commonRoleEntities)) {
//            return false;
//        }
//
//        if (!CollectionUtils.isEmpty(commonRoleEntities)) {
//            textCommonRoleService.removeByIds(commonRoleEntities.stream().map(ImageCommonRoleEntity::getId).toList());
//        }
//
//        ImageCommonRoleEntity textCommonRoleEntity = new ImageCommonRoleEntity();
//        textCommonRoleEntity.setProjectId(textRoleEntity.getProjectId());
//        textCommonRoleEntity.setAudioRoleInfo(textRoleEntity);
//        textCommonRoleEntity.setAudioModelInfo(textRoleEntity);
//        textCommonRoleService.save(textCommonRoleEntity);

        return true;
    }


    public List<ImageCommonRoleEntity> commonRoles(String projectId) {
        return imageCommonRoleService.getByProjectId(projectId);
    }


    public void createCommonRole(ImageCommonRoleEntity textCommonRoleEntity) {

        imageCommonRoleService.getByProjectId(textCommonRoleEntity.getProjectId())
                .stream()
                .filter(r -> StringUtils.equals(r.getRole(), textCommonRoleEntity.getRole()))
                .findAny()
                .ifPresent(r -> {
                    throw new BizException("预置角色名称[" + r.getRole() + "]已存在");
                });

        imageCommonRoleService.save(textCommonRoleEntity);
    }


    public void updateCommonRole(ImageCommonRoleEntity updateModelInfo) {
        imageCommonRoleService.updateById(updateModelInfo);
    }


    public void deleteCommonRole(ImageCommonRoleEntity textCommonRoleEntity) {
        imageCommonRoleService.removeById(textCommonRoleEntity);
    }

    public void deleteRole(ImageRoleEntity imageRole) {
        imageRoleService.removeById(imageRole.getId());
    }


    public ImageRoleInferenceData queryRoleInferenceCache(String projectId, String chapterId) {
        List<DramaInfoEntity> chapterInfos = dramaInfoService.getByChapterId(chapterId);

        List<String> linesList = new ArrayList<>();
//        chapterInfos.forEach(lineInfo -> {
//            if (Objects.equals(lineInfo.getDialogueFlag(), Boolean.TRUE)) {
//                linesList.add(lineInfo.getIndex() + ": " + lineInfo.getText());
//            }
//        });

        StringBuilder content = new StringBuilder();

        chapterInfos.stream()
                .collect(Collectors.groupingBy(DramaInfoEntity::getParaIndex, TreeMap::new, Collectors.toList()))
                .values()
                .forEach(val -> {
                    val.stream().sorted(Comparator.comparingInt(DramaInfoEntity::getSentIndex))
                            .map(DramaInfoEntity::getText)
                            .forEach(content::append);
                    content.append("\n");
                });

        ImageRoleInferenceData roleInferenceData = new ImageRoleInferenceData();
        roleInferenceData.setContent(content.toString());
        roleInferenceData.setLines(String.join("\n", linesList));

        List<ImageRoleInferenceEntity> textRoleInferences = imageRoleInferenceService.getByChapterId(chapterId);
        if (!CollectionUtils.isEmpty(textRoleInferences)) {
            roleInferenceData.setImageRoleInferences(textRoleInferences);
        }

        return roleInferenceData;
    }


    public void loadRoleInference(String projectId, String chapterId) {
        List<ImageRoleInferenceEntity> roleInferenceEntities = imageRoleInferenceService.getByChapterId(chapterId);

        if (!CollectionUtils.isEmpty(roleInferenceEntities)) {
            List<ImageCommonRoleEntity> commonRoles = imageCommonRoleService.list();
            Map<String, ImageCommonRoleEntity> commonRoleMap = commonRoles.
                    stream()
                    .collect(Collectors.toMap(ImageCommonRoleEntity::getRole, Function.identity(), (a, b) -> a));

            List<ImageRoleEntity> textRoleEntities = roleInferenceEntities.stream()
                    .collect(Collectors.toMap(ImageRoleInferenceEntity::getRole, Function.identity(), (v1, b) -> v1))
                    .values()
                    .stream().map(roleInferenceEntity -> {
                        ImageRoleEntity textRoleEntity = new ImageRoleEntity();
                        textRoleEntity.setProjectId(projectId);
                        textRoleEntity.setChapterId(chapterId);
                        textRoleEntity.setRole(roleInferenceEntity.getRole());
                        ImageCommonRoleEntity commonRole = commonRoleMap.get(roleInferenceEntity.getRole());
                        if (Objects.nonNull(commonRole)) {
                        }
                        return textRoleEntity;
                    }).toList();

            Map<String, ImageRoleInferenceEntity> roleInferenceEntityMap = roleInferenceEntities.stream()
                    .collect(Collectors.toMap(ImageRoleInferenceEntity::getImageIndex, Function.identity(), (a, b) -> a));

            List<DramaInfoEntity> chapterInfoEntities = dramaInfoService.getByChapterId(chapterId);

            List<Integer> audioModelResetIds = new ArrayList<>();

            String asideRole = "旁白";

            Optional<ImageRoleEntity> hasAsideRole = textRoleEntities
                    .stream()
                    .filter(c -> StringUtils.equals(c.getRole(), asideRole))
                    .findFirst();

            Optional<DramaInfoEntity> hasAsideText = chapterInfoEntities
                    .stream()
                    .filter(c -> StringUtils.equals(c.getRole(), asideRole))
                    .findFirst();

            List<DramaInfoEntity> saveInfos = chapterInfoEntities.stream()
                    .filter(c -> roleInferenceEntityMap.containsKey(c.getIndex()))
                    .peek(c -> {
                        ImageRoleInferenceEntity roleInferenceEntity = roleInferenceEntityMap.get(c.getIndex());
                        c.setRole(roleInferenceEntity.getRole());

                        if (commonRoleMap.containsKey(roleInferenceEntity.getRole())) {
                            ImageCommonRoleEntity commonRole = commonRoleMap.get(roleInferenceEntity.getRole());


                            c.setRole(roleInferenceEntity.getRole());
                        } else {
                            audioModelResetIds.add(c.getId());
                        }
                    }).toList();

            dramaInfoService.updateBatchById(saveInfos);
            dramaInfoService.audioModelReset(audioModelResetIds);

            ArrayList<ImageRoleEntity> saveTextRoles = new ArrayList<>(textRoleEntities);

            if (hasAsideRole.isEmpty() && hasAsideText.isPresent()) {

                ImageRoleEntity textRoleEntity = new ImageRoleEntity();
                textRoleEntity.setProjectId(projectId);
                textRoleEntity.setChapterId(chapterId);
                textRoleEntity.setRole(asideRole);
                ImageCommonRoleEntity commonRole = commonRoleMap.get(asideRole);
                if (Objects.nonNull(commonRole)) {
                }
                saveTextRoles.add(textRoleEntity);
            }

            imageRoleService.deleteByChapterId(chapterId);
            imageRoleService.saveBatch(saveTextRoles);
        }
    }


//    public void chapterExpose(ChapterExpose chapterExpose) throws Exception {
//        if (CollectionUtils.isEmpty(chapterExpose.getChapterInfoIds())) {
//            return;
//        }
//
//        String projectId = chapterExpose.getProjectId();
//        String chapterId = chapterExpose.getChapterId();
//        Boolean combineAudio = chapterExpose.getCombineAudio();
//        Boolean subtitle = chapterExpose.getSubtitle();
//
//        ImageProjectEntity textProject = imageProjectService.getByProjectId(projectId);
//        ImageDramaEntity textChapter = imageDramaService.getByChapterId(chapterId);
//
//        List<DramaInfoEntity> chapterInfos = dramaInfoService.getByChapterId(chapterId);
//
//        Boolean subtitleOptimize = globalSettingService.getGlobalSetting().getSubtitleOptimize();
//
//        List<AudioSegment> audioSegments = chapterInfos.stream()
//                .filter(c -> chapterExpose.getChapterInfoIds().contains(c.getId()))
//                .sorted(Comparator.comparingInt((DramaInfoEntity entity) -> Optional.ofNullable(entity.getTextSort()).orElse(0))
//                        .thenComparingInt(DramaInfoEntity::getId))
//                .map(c -> {
//
//                    List<String> subtitles = SubtitleUtils.subtitleSplit(c.getText(), subtitleOptimize);
//
//                    String[] audioNames = c.getAudioFiles().split(",");
//
//                    if (CollectionUtils.isEmpty(subtitles) || subtitles.size() != audioNames.length) {
//                        return null;
//                    }
//
//                    List<AudioSegment> subAudioSegments = new ArrayList<>();
//
//                    for (int i = 0; i < audioNames.length; i++) {
//
//                        AudioSegment subAudioSegment = new AudioSegment();
//                        subAudioSegment.setId(c.getId());
//                        subAudioSegment.setPart(i);
//                        subAudioSegment.setAudioName(audioNames[i]);
//                        subAudioSegment.setText(subtitles.get(i));
//                        subAudioSegment.setAudioVolume(c.getAudioVolume());
//                        subAudioSegment.setAudioSpeed(c.getAudioSpeed());
//
//                        if (i == audioNames.length - 1) {
//                            subAudioSegment.setAudioInterval(c.getAudioInterval());
//                        } else {
//                            subAudioSegment.setAudioInterval(globalSettingService.getGlobalSetting().getSubAudioInterval());
//                        }
//
//                        Path subPath = envConfig.buildProjectPath(
//                                "text",
//                                FileUtils.fileNameFormat(textProject.getProjectName()),
//                                FileUtils.fileNameFormat(textChapter.getChapterName()),
//                                "audio",
//                                audioNames[i]);
//
//                        subAudioSegment.setAudioPath(subPath.toAbsolutePath().toString());
//
//                        subAudioSegments.add(subAudioSegment);
//                    }
//
//                    return subAudioSegments;
//                }).filter(Objects::nonNull).flatMap(Collection::stream).toList();
//
//        if (Objects.equals(combineAudio, Boolean.TRUE)) {
//            Path outputWavPath = envConfig.buildProjectPath(
//                    "text",
//                    FileUtils.fileNameFormat(textProject.getProjectName()),
//                    FileUtils.fileNameFormat(textChapter.getChapterName()),
//                    "output.wav");
//
//            if (Files.notExists(outputWavPath.getParent())) {
//                Files.createDirectories(outputWavPath.getParent());
//            }
//
//            AudioUtils.mergeAudioFiles(audioSegments, outputWavPath.toAbsolutePath().toString());
//
//            List<DramaInfoEntity> updateList = audioSegments.stream()
//                    .map(a -> {
//                        DramaInfoEntity chapterInfoEntity = new DramaInfoEntity();
//                        chapterInfoEntity.setId(a.getId());
//                        chapterInfoEntity.setAudioLength(a.getAudioLength());
//                        chapterInfoEntity.setAudioTaskState(AudioTaskStateConstants.combined);
//                        return chapterInfoEntity;
//                    }).toList();
//
//            dramaInfoService.updateBatchById(updateList);
//
//            Path archiveWavPath = envConfig.buildProjectPath(
//                    "text",
//                    FileUtils.fileNameFormat(textProject.getProjectName()),
//                    "output",
//                    FileUtils.fileNameFormat(textChapter.getChapterName()) + ".wav");
//            if (Files.notExists(archiveWavPath.getParent())) {
//                Files.createDirectories(archiveWavPath.getParent());
//            }
//            if (Files.exists(archiveWavPath)) {
//                Files.delete(archiveWavPath);
//            }
//            Files.copy(outputWavPath, archiveWavPath);
//        }
//
//        if (Objects.equals(subtitle, Boolean.TRUE)) {
//            Path outputSrtPath = envConfig.buildProjectPath(
//                    "text",
//                    FileUtils.fileNameFormat(textProject.getProjectName()),
//                    FileUtils.fileNameFormat(textChapter.getChapterName()),
//                    "output.srt");
//
//            SubtitleUtils.srtFile(audioSegments, outputSrtPath);
//
//            Path archiveSrtPath = envConfig.buildProjectPath(
//                    "text",
//                    FileUtils.fileNameFormat(textProject.getProjectName()),
//                    "output",
//                    FileUtils.fileNameFormat(textChapter.getChapterName()) + ".srt");
//            if (Files.notExists(archiveSrtPath.getParent())) {
//                Files.createDirectories(archiveSrtPath.getParent());
//            }
//            if (Files.exists(archiveSrtPath)) {
//                Files.delete(archiveSrtPath);
//            }
//            Files.copy(outputSrtPath, archiveSrtPath);
//        }
//    }


    public Flux<String> roleInference(ImageRoleInferenceParam roleInferenceParam) {
        String projectId = roleInferenceParam.getProjectId();
        String chapterId = roleInferenceParam.getChapterId();
        if (StringUtils.equals(roleInferenceParam.getInferenceType(), "online")) {
            return onlineRoleInference(roleInferenceParam);
        }
        if (StringUtils.equals(roleInferenceParam.getInferenceType(), "last")) {
            loadRoleInference(projectId, roleInferenceParam.getChapterId());
        }
        if (StringUtils.equals(roleInferenceParam.getInferenceType(), "input")) {
//            if (StringUtils.isNotBlank(roleInferenceParam.getInferenceResult())) {
//                List<DramaInfoEntity> chapterInfos = dramaInfoService.getByChapterId(chapterId);
//                mergeAiResultInfo(projectId, chapterId, roleInferenceParam.getInferenceResult(), chapterInfos);
//            }
        }
        return Flux.empty();
    }

    public Flux<String> imagePromptInference(ImagePromptInferenceParam param) {
        String projectId = param.getProjectId();
        String chapterId = param.getChapterId();
        if (StringUtils.equals(param.getInferenceType(), "online")) {
            return onlinePromptInference(param);
        }
        if (StringUtils.equals(param.getInferenceType(), "last")) {
            loadRoleInference(projectId, param.getChapterId());
        }
        if (StringUtils.equals(param.getInferenceType(), "input")) {
//            if (StringUtils.isNotBlank(param.getInferenceResult())) {
//                List<DramaInfoEntity> chapterInfos = dramaInfoService.getByChapterId(chapterId);
//                mergeAiResultInfo(projectId, chapterId, param.getInferenceResult(), chapterInfos);
//            }
        }
        return Flux.empty();
    }

    public Flux<String> onlineRoleInference(ImageRoleInferenceParam roleInferenceParam) {
        String projectId = roleInferenceParam.getProjectId();
        String chapterId = roleInferenceParam.getChapterId();

        List<DramaInfoEntity> dramaInfos = dramaInfoService.getByChapterId(chapterId);

        StringBuilder content = new StringBuilder();

        dramaInfos.forEach(val -> {
            content.append(val.getIndex()).append("：");
            val.getText().forEach(content::append);
            content.append("\n");
        });

        String systemMessage = roleInferenceParam.getSystemPrompt();
        Integer tmServerId = roleInferenceParam.getTmServerId();

        String userMessage = roleInferenceParam.getUserPrompt()
                .replace("@{小说内容}", content.toString());

        log.info("\n提示词, systemMessage: {}", systemMessage);
        log.info("\n提示词, userMessage: {}", userMessage);

        StringBuilder aiResultStr = new StringBuilder();
        AtomicBoolean isMapping = new AtomicBoolean(false);
        StringBuilder sbStr = new StringBuilder();

        return aiService.stream(tmServerId, systemMessage, userMessage)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(v -> {
                    System.out.println(v);
                    aiResultStr.append(v);

                    try {
                        sbStr.append(v);
                        while (true) {
                            int newlineIndex = sbStr.indexOf("\n");
                            if (newlineIndex == -1) {
                                break;
                            }
                            String line = sbStr.substring(0, newlineIndex + 1).trim();
                            sbStr.delete(0, newlineIndex + 1);

                            if (StringUtils.isNotBlank(line)) {
                                if (StringUtils.equals("角色分析:", line)) {
                                    isMapping.set(false);
                                    continue;
                                }
                                if (StringUtils.equals("出场分析:", line)) {
                                    isMapping.set(true);
                                    continue;
                                }
                                if (!isMapping.get()) {
                                    globalWebSocketHandler.sendSuccessMessage("角色分析", line);
                                }
                                if (isMapping.get()) {
                                    globalWebSocketHandler.sendSuccessMessage("出场分析", line);
                                }
                            }

                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                })
                .onErrorResume(e -> {
                    String errorMessage;
                    if (e instanceof WebClientResponseException.Unauthorized) {
                        errorMessage = "未经授权的访问，请检查 apiKey 等相关配置";
                    } else {
                        errorMessage = "错误请求：" + e.getMessage();
                    }
                    globalWebSocketHandler.sendErrorMessage("文本大模型请求异常", errorMessage);
                    return Flux.error(e);
                })
                .doOnComplete(() -> mergeAiResultInfo(projectId, chapterId, aiResultStr.toString(), dramaInfos));
    }

    public Flux<String> onlinePromptInference(ImagePromptInferenceParam param) {
        String projectId = param.getProjectId();
        String chapterId = param.getChapterId();

        List<DramaInfoEntity> dramaInfos = dramaInfoService.getByChapterId(chapterId);

        StringBuilder content = new StringBuilder();

        dramaInfos.forEach(val -> {
            content.append(val.getIndex()).append("：");
            val.getText().forEach(content::append);
            content.append("\n");
        });

        String systemMessage = param.getSystemPrompt();
        Integer tmServerId = param.getTmServerId();

        String userMessage = param.getUserPrompt()
                .replace("@{小说内容}", content.toString());

        log.info("\n提示词, systemMessage: {}", systemMessage);
        log.info("\n提示词, userMessage: {}", userMessage);

        StringBuilder aiResultStr = new StringBuilder();
        AtomicBoolean isMapping = new AtomicBoolean(false);
        StringBuilder sbStr = new StringBuilder();

        return aiService.stream(tmServerId, systemMessage, userMessage)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(v -> {
                    System.out.println(v);
                    aiResultStr.append(v);

                    try {
                        sbStr.append(v);
                        while (true) {
                            int newlineIndex = sbStr.indexOf("\n");
                            if (newlineIndex == -1) {
                                break;
                            }
                            String line = sbStr.substring(0, newlineIndex + 1).trim();
                            sbStr.delete(0, newlineIndex + 1);

                            if (StringUtils.isNotBlank(line)) {
                                if (StringUtils.equals("画面分析:", line)) {
                                    isMapping.set(true);
                                    continue;
                                }
                                if (isMapping.get()) {
                                    globalWebSocketHandler.sendSuccessMessage("画面分析", line);
                                }
                            }

                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                })
                .onErrorResume(e -> {
                    String errorMessage;
                    if (e instanceof WebClientResponseException.Unauthorized) {
                        errorMessage = "未经授权的访问，请检查 apiKey 等相关配置";
                    } else {
                        errorMessage = "错误请求：" + e.getMessage();
                    }
                    globalWebSocketHandler.sendErrorMessage("文本大模型请求异常", errorMessage);
                    return Flux.error(e);
                })
                .doOnComplete(() -> mergePromptResultInfo(projectId, chapterId, aiResultStr.toString(), dramaInfos));
    }

    public void saveDramaInfoEntities(ImageDramaEntity imageDrama) {

        String projectId = imageDrama.getProjectId();
        String chapterId = imageDrama.getChapterId();

        List<DramaInfoEntity> dramaInfoEntities = dramaInfoService.buildDramaInfos(imageDrama);

        dramaInfoService.deleteByChapterId(chapterId);
        dramaInfoInferenceService.deleteByChapterId(chapterId);
        imageRoleService.deleteByChapterId(chapterId);

        if (!CollectionUtils.isEmpty(dramaInfoEntities)) {

            List<ImageCommonRoleEntity> commonRoleEntities = imageCommonRoleService.getByProjectId(projectId);
            Optional<ImageCommonRoleEntity> asideRoleOptional = commonRoleEntities.stream()
                    .filter(r -> StringUtils.equals(r.getRole(), "未知"))
                    .findAny();

            ImageRoleEntity textRoleEntity = new ImageRoleEntity();
            textRoleEntity.setProjectId(projectId);
            textRoleEntity.setChapterId(chapterId);
            textRoleEntity.setRole("未知");

            if (asideRoleOptional.isPresent()) {
                ImageCommonRoleEntity textCommonRoleEntity = asideRoleOptional.get();

            }

            for (DramaInfoEntity dramaInfoEntity : dramaInfoEntities) {
                dramaInfoService.save(dramaInfoEntity);
                dramaInfoEntity.getInferences().forEach(o -> o.setDramaInfoId(dramaInfoEntity.getId()));
                dramaInfoInferenceService.saveBatch(dramaInfoEntity.getInferences());
            }
            imageRoleService.save(textRoleEntity);

        }
    }

    public void mergeAiResultInfo(String projectId, String chapterId, String aiResultStr, List<DramaInfoEntity> chapterInfos) {

        System.out.println("=========================文本大模型返回结果=========================");
        System.out.println(aiResultStr);
        System.out.println("=========================文本大模型返回结果=========================");

        try {

            AiResult aiResult = formatAiResult(aiResultStr);

            if (Objects.isNull(aiResult)) {
                globalWebSocketHandler.sendErrorMessage("文本大模型请求异常", "没有接收到文本大模型的消息！");
            }

            List<AiResult.DramaRole> roles = aiResult.getDramaRoles();

            Map<String, AiResult.DramaRole> aiResultRoleMap = aiResult.getDramaRoles()
                    .stream()
                    .collect(Collectors.toMap(AiResult.DramaRole::getRole, Function.identity(), (a, b) -> a));
            Map<String, AiResult.DramaLinesRole> linesMappingMap = aiResult.getDramaLinesRoles()
                    .stream()
                    .collect(Collectors.toMap(AiResult.DramaLinesRole::getLinesIndex, Function.identity(), (a, b) -> a));

            List<ImageCommonRoleEntity> commonRoles = imageCommonRoleService.list();
            Map<String, ImageCommonRoleEntity> commonRoleMap = commonRoles.
                    stream()
                    .collect(Collectors.toMap(ImageCommonRoleEntity::getRole, Function.identity(), (a, b) -> a));

            boolean hasAside = false;
            for (DramaInfoEntity chapterInfo : chapterInfos) {
                String key = chapterInfo.getIndex();
                String role = "未知";
                if (linesMappingMap.containsKey(key)) {
                    AiResult.DramaLinesRole linesMapping = linesMappingMap.get(key);
                    role = linesMapping.getRole();
                } else {
                    hasAside = true;
                }

                chapterInfo.setRole(role);
                if (aiResultRoleMap.containsKey(role)) {
                }

                if (commonRoleMap.containsKey(role)) {
                    ImageCommonRoleEntity commonRole = commonRoleMap.get(role);


                } else {
                }
            }

            if (hasAside) {
                String role = "未知";
                roles.add(new AiResult.DramaRole(role));
            }

            List<ImageRoleEntity> textRoleEntities = roles.stream()
                    .map(role -> {
                        ImageRoleEntity textRoleEntity = new ImageRoleEntity();
                        textRoleEntity.setProjectId(projectId);
                        textRoleEntity.setChapterId(chapterId);
                        textRoleEntity.setRole(role.getRole());
                        textRoleEntity.setImagePrompt(role.getImagePrompt());

                        ImageCommonRoleEntity commonRole = commonRoleMap.get(role.getRole());
                        if (Objects.nonNull(commonRole)) {
                        }

                        return textRoleEntity;
                    }).toList();


            List<ImageRoleInferenceEntity> roleInferenceEntities = aiResult.getDramaLinesRoles().stream()
                    .map(linesMapping -> {
                        ImageRoleInferenceEntity roleInferenceEntity = new ImageRoleInferenceEntity();
                        roleInferenceEntity.setProjectId(projectId);
                        roleInferenceEntity.setChapterId(chapterId);
                        roleInferenceEntity.setImageIndex(linesMapping.getLinesIndex());
                        roleInferenceEntity.setRole(linesMapping.getRole());
                        return roleInferenceEntity;
                    }).toList();

            dramaInfoService.updateBatchById(chapterInfos);

            imageRoleService.deleteByChapterId(chapterId);
            imageRoleService.saveBatch(textRoleEntities);

            imageRoleInferenceService.deleteByChapterId(chapterId);
            imageRoleInferenceService.saveBatch(roleInferenceEntities);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void mergePromptResultInfo(String projectId, String chapterId, String aiResultStr, List<DramaInfoEntity> dramaInfos) {

        System.out.println("=========================文本大模型返回结果=========================");
        System.out.println(aiResultStr);
        System.out.println("=========================文本大模型返回结果=========================");
        if (Objects.isNull(aiResultStr)) {
            globalWebSocketHandler.sendErrorMessage("文本大模型请求异常", "没有接收到文本大模型的消息！");
            return;
        }
        Map<String, String> promptMap = new HashMap<>();
        try {
            for (String line : aiResultStr.split("\n")) {
                if (StringUtils.equals("画面分析:", line.trim()) || StringUtils.isEmpty(line)) {
                    continue;
                }
                String[] split = line.split("#");
                promptMap.put(split[0], split[1]);
            }
            for (DramaInfoEntity dramaInfo : dramaInfos) {
                String index = dramaInfo.getIndex();
                if (promptMap.containsKey(index)) {
                    dramaInfo.setImagePrompt(promptMap.get(index));
                }
            }

            dramaInfoService.updateBatchById(dramaInfos);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public AiResult formatAiResult(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }

        boolean isMapping = false;
        List<AiResult.DramaRole> roles = new ArrayList<>();
        List<AiResult.DramaLinesRole> linesMappings = new ArrayList<>();
        for (String line : text.split("\n")) {
            if (StringUtils.equals("角色分析:", line.trim())) {
                isMapping = false;
                continue;
            }
            if (StringUtils.equals("出场分析:", line.trim())) {
                isMapping = true;
                continue;
            }
            if (!isMapping) {
                String[] split = line.trim().split("#");
                if (split.length == 2) {
                    roles.add(new AiResult.DramaRole(split[0].trim(), split[1].trim()));
                }
            }
            if (isMapping) {
                String[] split = line.trim().split("#");
                if (split.length == 2) {
                    linesMappings.add(new AiResult.DramaLinesRole(split[0].trim(), split[1].trim()));
                }
            }
        }

        AiResult aiResult = new AiResult();
        aiResult.setDramaRoles(roles);
        aiResult.setDramaLinesRoles(linesMappings);
        return aiResult;
    }

    @SneakyThrows
    public void dramaExpose(DramaExportParam param, MultipartFile audio) {
        String projectId = param.getProjectId();
        String chapterId = param.getChapterId();
        ImageProjectEntity imageProject = imageProjectService.getByProjectId(projectId);
        ImageDramaEntity imageDrama = imageDramaService.getImageDramaAndContent(projectId, chapterId);
        List<DramaInfoEntity> dramaInfoList = dramaInfoService.getByChapterId(chapterId);

        Draft draft = new Draft();
        TextTrack textTrack = new TextTrack();
        VideoTrack videoTrack = new VideoTrack();
        TreeMap<Integer, SRT> srt = SrtUtils.parseSrt(imageDrama.getContent());
        FrameUtils frameUtils = FrameUtils.of();
        srt.forEach((integer, row) -> {
            TextMaterial material = new TextMaterial(row.getSrtBody());
            draft.addMaterial(material);
            textTrack.addText(material.getId(), frameUtils.framePerMs(row.getBeginTime()), frameUtils.framePerMs(row.getEndTime()));
        });
        draft.addTrack(textTrack);
        for (int i = 0; i < dramaInfoList.size(); i++) {
            DramaInfoEntity dramaInfo = dramaInfoList.get(i);
            String imageFiles = dramaInfo.getFinalImageFiles();
            String path = envConfig.buildProjectPath("image", imageProject.getProjectName(), imageDrama.getChapterName(), imageFiles).toString();
            VideoMaterial material = new VideoMaterial(path, imageFiles);
            draft.addMaterial(material);
            videoTrack.addImg(material.getId(), frameUtils.framePerMs(dramaInfo.getTimeStart()),
                    i != dramaInfoList.size() - 1 ? frameUtils.framePerMs(dramaInfoList.get(i + 1).getTimeStart()) : frameUtils.framePerMs(dramaInfo.getTimeEnd()));
        }
        draft.addTrack(videoTrack);
        if (audio != null) {
            Path path = envConfig.buildProjectPath("image", imageProject.getProjectName(), imageDrama.getChapterName(), "output.wav");
            audio.transferTo(path);
            FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(path.toString());
            grabber.start();
            long lengthInTime = grabber.getLengthInTime();
            grabber.close();
            AudioMaterial material = new AudioMaterial(path.toString(), "output.wav", frameUtils.framePerMs(lengthInTime / 1000));
            draft.addMaterial(material);
            draft.addTrack(new AudioTrack().addAudio(material.getId(), 0, frameUtils.framePerMs(lengthInTime / 1000)));
        }
        draft.export(envConfig.buildProjectPath("image", imageProject.getProjectName(), imageDrama.getChapterName(),
                imageProject.getProjectName() + "-" + imageDrama.getChapterName()).toString());
    }
}
