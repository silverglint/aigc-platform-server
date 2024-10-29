package space.wenliang.ai.aigcplatformserver.util.jianying.materials;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;

/**
 * @author Administrator
 * date   2024/10/27
 */
@Data
public class VideoMaterial {

    private final String template = """
            {
                    "aigc_type": "none",
                    "audio_fade": null,
                    "cartoon_path": "",
                    "category_id": "",
                    "category_name": "local",
                    "check_flag": 63487,
                    "crop": {
                      "lower_left_x": 0.0,
                      "lower_left_y": 1.0,
                      "lower_right_x": 1.0,
                      "lower_right_y": 1.0,
                      "upper_left_x": 0.0,
                      "upper_left_y": 0.0,
                      "upper_right_x": 1.0,
                      "upper_right_y": 0.0
                    },
                    "crop_ratio": "free",
                    "crop_scale": 1.0,
                    "duration": 10800000000,
                    "extra_type_option": 0,
                    "formula_id": "",
                    "freeze": null,
                    "has_audio": false,
                    "height": 1536,
                    "id": "F7BA2CE7-5608-450b-9B9C-57FECF22D9D5",
                    "intensifies_audio_path": "",
                    "intensifies_path": "",
                    "is_ai_generate_content": false,
                    "is_copyright": false,
                    "is_text_edit_overdub": false,
                    "is_unified_beauty_mode": false,
                    "local_id": "",
                    "local_material_id": "",
                    "material_id": "",
                    "material_name": "0019.png",
                    "material_url": "",
                    "matting": {
                      "flag": 0,
                      "has_use_quick_brush": false,
                      "has_use_quick_eraser": false,
                      "interactiveTime": [],
                      "path": "",
                      "strokes": []
                    },
                    "media_path": "",
                    "object_locked": null,
                    "origin_material_id": "",
                    "path": "E:/work/小说/我是62岁男房东/1/afterimg/0019.png",
                    "picture_from": "none",
                    "picture_set_category_id": "",
                    "picture_set_category_name": "",
                    "request_id": "",
                    "reverse_intensifies_path": "",
                    "reverse_path": "",
                    "smart_motion": null,
                    "source": 0,
                    "source_platform": 0,
                    "stable": {
                      "matrix_path": "",
                      "stable_level": 0,
                      "time_range": {
                        "duration": 0,
                        "start": 0
                      }
                    },
                    "team_id": "",
                    "type": "photo",
                    "video_algorithm": {
                      "algorithms": [],
                      "complement_frame_config": null,
                      "deflicker": null,
                      "gameplay_configs": [],
                      "motion_blur_config": null,
                      "noise_reduction": null,
                      "path": "",
                      "quality_enhance": null,
                      "time_range": null
                    },
                    "width": 2048
                  }
            """;

    private String id;
    private JSONObject jsonObject;

    public VideoMaterial(String path, String name) {
        jsonObject = JSONUtil.parseObj(template);
        jsonObject.set("path", path);
        jsonObject.set("material_name", name);
        id = IdUtil.fastUUID();
        jsonObject.set("id", id);
    }
}
