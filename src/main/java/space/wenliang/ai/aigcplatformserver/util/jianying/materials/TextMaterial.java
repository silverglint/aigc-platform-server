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
public class TextMaterial {

    private final String template = """
                        {
                                "add_type": 1,
                                "alignment": 1,
                                "background_alpha": 1.0,
                                "background_color": "",
                                "background_height": 0.14,
                                "background_horizontal_offset": 0.0,
                                "background_round_radius": 0.0,
                                "background_style": 0,
                                "background_vertical_offset": 0.0,
                                "background_width": 0.14,
                                "base_content": "",
                                "bold_width": 0.0,
                                "border_alpha": 1.0,
                                "border_color": "",
                                "border_width": 0.08,
                                "caption_template_info": {
                                  "category_id": "",
                                  "category_name": "",
                                  "effect_id": "",
                                  "is_new": false,
                                  "path": "",
                                  "request_id": "",
                                  "resource_id": "",
                                  "resource_name": "",
                                  "source_platform": 0
                                },
                                "check_flag": 7,
                                "combo_info": {
                                  "text_templates": []
                                },
                                "content": "{\\"text\\":\\"叮系统激活宿主吴老六作为房东\\",\\"styles\\":[{\\"fill\\":{\\"content\\":{\\"solid\\":{\\"color\\":[1,1,1]}}},\\"font\\":{\\"path\\":\\"E:/software/jianying/jianying5.8.0.11586/5.8.0.11586/Resources/Font/SystemFont/zh-hans.ttf\\",\\"id\\":\\"\\"},\\"size\\":8,\\"effectStyle\\":{\\"path\\":\\"C:/Users/Administrator/AppData/Local/JianyingPro/User Data/Cache/artistEffect/6896137793022463239/7a57ca827553e6caa71427fe2c5a3af4\\",\\"id\\":\\"6896137793022463239\\"},\\"range\\":[0,99]}]}",
                                "fixed_height": -1.0,
                                "fixed_width": -1.0,
                                "font_category_id": "",
                                "font_category_name": "",
                                "font_id": "",
                                "font_name": "",
                                "font_path": "E:/software/jianying/jianying5.8.0.11586/5.8.0.11586/Resources/Font/SystemFont/zh-hans.ttf",
                                "font_resource_id": "",
                                "font_size": 8.0,
                                "font_source_platform": 0,
                                "font_team_id": "",
                                "font_title": "none",
                                "font_url": "",
                                "fonts": [],
                                "force_apply_line_max_width": false,
                                "global_alpha": 1.0,
                                "group_id": "Auto_1730031418510",
                                "has_shadow": false,
                                "id": "7C0435AF-8766-462e-A1EE-1DC25A78938B",
                                "initial_scale": 1.0,
                                "inner_padding": -1.0,
                                "is_rich_text": false,
                                "italic_degree": 0,
                                "ktv_color": "",
                                "language": "",
                                "layer_weight": 1,
                                "letter_spacing": 0.0,
                                "line_feed": 1,
                                "line_max_width": 0.82,
                                "line_spacing": 0.02,
                                "multi_language_current": "none",
                                "name": "",
                                "original_size": [],
                                "preset_category": "",
                                "preset_category_id": "",
                                "preset_has_set_alignment": false,
                                "preset_id": "",
                                "preset_index": 0,
                                "preset_name": "",
                                "recognize_task_id": "287d4bef-827f-4bab-b296-2b800d2a6515",
                                "recognize_type": 0,
                                "relevance_segment": [],
                                "shadow_alpha": 0.9,
                                "shadow_angle": -45.0,
                                "shadow_color": "",
                                "shadow_distance": 5.0,
                                "shadow_point": {
                                  "x": 0.6363961030678928,
                                  "y": -0.6363961030678928
                                },
                                "shadow_smoothing": 0.45,
                                "shape_clip_x": false,
                                "shape_clip_y": false,
                                "source_from": "",
                                "style_name": "",
                                "sub_type": 0,
                                "subtitle_keywords": null,
                                "subtitle_template_original_fontsize": 0.0,
                                "text_alpha": 1.0,
                                "text_color": "#ffffff",
                                "text_curve": null,
                                "text_preset_resource_id": "",
                                "text_size": 30,
                                "text_to_audio_ids": [],
                                "tts_auto_update": false,
                                "type": "subtitle",
                                "typesetting": 0,
                                "underline": false,
                                "underline_offset": 0.22,
                                "underline_width": 0.05,
                                "use_effect_default_color": true,
                                "words": {
                                  "end_time": [],
                                  "start_time": [],
                                  "text": []
                                }
                              }
            """;

    private String id;
    private JSONObject jsonObject;

    public TextMaterial(String text) {
        jsonObject = JSONUtil.parseObj(template);
        JSONObject content = JSONUtil.parseObj(jsonObject.getStr("content"));
        content.set("text", text);
        jsonObject.set("content", JSONUtil.toJsonStr(content));
        id = IdUtil.fastUUID();
        jsonObject.set("id", id);
    }
}
