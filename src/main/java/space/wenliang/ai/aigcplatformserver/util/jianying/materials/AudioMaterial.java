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
public class AudioMaterial {

    private final String template = """
            {
                    "app_id": 0,
                    "category_id": "",
                    "category_name": "local",
                    "check_flag": 1,
                    "copyright_limit_type": "none",
                    "duration": 12300000,
                    "effect_id": "",
                    "formula_id": "",
                    "id": "D8D0C965-E79F-46d2-BEBF-64DF871E73F9",
                    "intensifies_path": "",
                    "is_ai_clone_tone": false,
                    "is_text_edit_overdub": false,
                    "is_ugc": false,
                    "local_material_id": "8329fd4b-9f39-4bf2-8f6b-4a0009d8175e",
                    "music_id": "7cba278c-1dbd-4b1a-8d21-1bef4adb814b",
                    "name": "3-0-0.wav",
                    "path": "E:/software/aigc-server-2.6.3/project/text/62岁老房东/1/audio/3-0-0.wav",
                    "query": "",
                    "request_id": "",
                    "resource_id": "",
                    "search_id": "",
                    "source_from": "",
                    "source_platform": 0,
                    "team_id": "",
                    "text_id": "",
                    "tone_category_id": "",
                    "tone_category_name": "",
                    "tone_effect_id": "",
                    "tone_effect_name": "",
                    "tone_platform": "",
                    "tone_second_category_id": "",
                    "tone_second_category_name": "",
                    "tone_speaker": "",
                    "tone_type": "",
                    "type": "extract_music",
                    "video_id": "",
                    "wave_points": []
                  }
            """;

    private String id;

    private JSONObject jsonObject;

    public AudioMaterial(String path, String name, long duration) {
        JSONObject jsonObject = JSONUtil.parseObj(template);
        jsonObject.set("path", path);
        jsonObject.set("name", name);
        this.id = IdUtil.fastUUID();
        jsonObject.set("id", id);
        jsonObject.set("local_material_id", IdUtil.fastUUID());
        jsonObject.set("music_id", IdUtil.fastUUID());
        jsonObject.set("duration", duration);
        this.jsonObject = jsonObject;
    }
}
