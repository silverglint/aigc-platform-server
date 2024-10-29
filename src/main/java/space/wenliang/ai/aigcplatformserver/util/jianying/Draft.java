package space.wenliang.ai.aigcplatformserver.util.jianying;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import space.wenliang.ai.aigcplatformserver.util.jianying.materials.AudioMaterial;
import space.wenliang.ai.aigcplatformserver.util.jianying.materials.TextMaterial;
import space.wenliang.ai.aigcplatformserver.util.jianying.materials.VideoMaterial;
import space.wenliang.ai.aigcplatformserver.util.jianying.tracks.AudioTrack;
import space.wenliang.ai.aigcplatformserver.util.jianying.tracks.TextTrack;
import space.wenliang.ai.aigcplatformserver.util.jianying.tracks.VideoTrack;

/**
 * @author Administrator
 * date   2024/10/27
 */
@Data
public class Draft {
    private static final String draft_meta_info = """
                        {
                                "cloud_package_completed_time": "",
                                "draft_cloud_capcut_purchase_info": "",
                                "draft_cloud_last_action_download": false,
                                "draft_cloud_materials": [],
                                "draft_cloud_purchase_info": "",
                                "draft_cloud_template_id": "",
                                "draft_cloud_tutorial_info": "",
                                "draft_cloud_videocut_purchase_info": "",
                                "draft_cover": "draft_cover.jpg",
                                "draft_deeplink_url": "",
                                "draft_enterprise_info": {
                                  "draft_enterprise_extra": "",
                                  "draft_enterprise_id": "",
                                  "draft_enterprise_name": "",
                                  "enterprise_material": []
                                },
                                "draft_fold_path": "",
                                "draft_id": "8BE7B07E-2F3B-4c1a-86F3-C55C56AFEA2A",
                                "draft_is_ai_packaging_used": false,
                                "draft_is_ai_shorts": false,
                                "draft_is_ai_translate": false,
                                "draft_is_article_video_draft": false,
                                "draft_is_from_deeplink": "false",
                                "draft_is_invisible": false,
                                "draft_materials": [],
                                "draft_materials_copied_info": [],
                                "draft_name": "test",
                                "draft_new_version": "",
                                "draft_removable_storage_device": "",
                                "draft_root_path": "",
                                "draft_segment_extra_info": [],
                                "draft_timeline_materials_size_": 0,
                                "draft_type": "",
                                "tm_draft_cloud_completed": "",
                                "tm_draft_cloud_modified": 0,
                                "tm_draft_create": 0,
                                "tm_draft_modified": 0,
                                "tm_draft_removed": 0,
                                "tm_duration": 0
                        }
            """;

    private final String draft_content = """
                        {
                                      "canvas_config": {
                                        "height": 1440,
                                        "ratio": "4:3",
                                        "width": 1920
                                      },
                                      "color_space": 0,
                                      "config": {
                                        "adjust_max_index": 1,
                                        "attachment_info": [],
                                        "combination_max_index": 1,
                                        "export_range": null,
                                        "extract_audio_last_index": 1,
                                        "lyrics_recognition_id": "",
                                        "lyrics_sync": true,
                                        "lyrics_taskinfo": [],
                                        "maintrack_adsorb": true,
                                        "material_save_mode": 0,
                                        "multi_language_current": "none",
                                        "multi_language_list": [],
                                        "multi_language_main": "none",
                                        "multi_language_mode": "none",
                                        "original_sound_last_index": 1,
                                        "record_audio_last_index": 1,
                                        "sticker_max_index": 1,
                                        "subtitle_keywords_config": null,
                                        "subtitle_recognition_id": "",
                                        "subtitle_sync": true,
                                        "subtitle_taskinfo": [],
                                        "system_font_list": [],
                                        "video_mute": false,
                                        "zoom_info_params": null
                                      },
                                      "cover": null,
                                      "create_time": 0,
                                      "duration": 0,
                                      "extra_info": null,
                                      "fps": 30.0,
                                      "free_render_index_mode_on": false,
                                      "group_container": null,
                                      "id": "A411120C-AF68-45e5-8333-89B8EDA8165A",
                                      "keyframe_graph_list": [],
                                      "keyframes": {
                                        "adjusts": [],
                                        "audios": [],
                                        "effects": [],
                                        "filters": [],
                                        "handwrites": [],
                                        "stickers": [],
                                        "texts": [],
                                        "videos": []
                                      },
                                      "last_modified_platform": {},
                                      "materials": {
                                        "ai_translates": [],
                                        "audio_balances": [],
                                        "audio_effects": [],
                                        "audio_fades": [],
                                        "audio_track_indexes": [],
                                        "audios": [],
                                        "beats": [],
                                        "canvases": [],
                                        "chromas": [],
                                        "color_curves": [],
                                        "digital_humans": [],
                                        "drafts": [],
                                        "effects": [],
                                        "flowers": [],
                                        "green_screens": [],
                                        "handwrites": [],
                                        "hsl": [],
                                        "images": [],
                                        "log_color_wheels": [],
                                        "loudnesses": [],
                                        "manual_deformations": [],
                                        "masks": [],
                                        "material_animations": [],
                                        "material_colors": [],
                                        "multi_language_refs": [],
                                        "placeholders": [],
                                        "plugin_effects": [],
                                        "primary_color_wheels": [],
                                        "realtime_denoises": [],
                                        "shapes": [],
                                        "smart_crops": [],
                                        "smart_relights": [],
                                        "sound_channel_mappings": [],
                                        "speeds": [],
                                        "stickers": [],
                                        "tail_leaders": [],
                                        "text_templates": [],
                                        "texts": [],
                                        "time_marks": [],
                                        "transitions": [],
                                        "video_effects": [],
                                        "video_trackings": [],
                                        "videos": [],
                                        "vocal_beautifys": [],
                                        "vocal_separations": []
                                      },
                                      "mutable_config": null,
                                      "name": "",
                                      "new_version": "109.0.0",
                                      "platform": {},
                                      "relationships": [],
                                      "render_index_track_mode_on": true,
                                      "retouch_cover": null,
                                      "source": "default",
                                      "static_cover_image_path": "",
                                      "time_marks": null,
                                      "tracks": [],
                                      "update_time": 0,
                                      "version": 360000
                                    }
            """;

    private JSONObject draftContent;
    private JSONObject draftMetaInfo;
    private long endTime;

    public Draft() {
        draftContent = new JSONObject(draft_content);
        draftContent.set("id", IdUtil.fastUUID());
        draftMetaInfo = new JSONObject(draft_meta_info);
        draftMetaInfo.set("draft_id", IdUtil.fastUUID());
    }

    public Draft addMaterial(AudioMaterial audio) {
        draftContent.getJSONObject("materials").getJSONArray("audios").add(audio.getJsonObject());
        return this;
    }

    public Draft addMaterial(VideoMaterial video) {
        draftContent.getJSONObject("materials").getJSONArray("videos").add(video.getJsonObject());
        return this;
    }

    public Draft addMaterial(TextMaterial text) {
        draftContent.getJSONObject("materials").getJSONArray("texts").add(text.getJsonObject());
        return this;
    }

    public Draft addTrack(VideoTrack videoTrack) {
        endTime = Math.max(endTime, videoTrack.getEndTime());
        draftContent.getJSONArray("tracks").add(videoTrack.getJsonObject());
        return this;
    }

    public Draft addTrack(AudioTrack audioTrack) {
        endTime = Math.max(endTime, audioTrack.getEndTime());
        draftContent.getJSONArray("tracks").add(audioTrack.getJsonObject());
        return this;
    }

    public Draft addTrack(TextTrack textTrack) {
        endTime = Math.max(endTime, textTrack.getEndTime());
        draftContent.getJSONArray("tracks").add(textTrack.getJsonObject());
        return this;
    }

    public void export(String path) {
        draftContent.set("duration", endTime + 1000000);
        if (FileUtil.isDirectory(path)) {
            FileUtil.del(path);
        }
        FileUtil.mkdir(path);
        FileUtil.writeString(JSONUtil.toJsonStr(draftContent), path + "/draft_content.json", CharsetUtil.UTF_8);
        FileUtil.writeString(JSONUtil.toJsonStr(draftMetaInfo), path + "/draft_meta_info.json", CharsetUtil.UTF_8);
    }
}
