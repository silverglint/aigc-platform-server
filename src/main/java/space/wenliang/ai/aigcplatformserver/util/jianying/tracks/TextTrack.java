package space.wenliang.ai.aigcplatformserver.util.jianying.tracks;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import lombok.Data;

/**
 * @author Administrator
 * date   2024/10/28
 */
@Data
public class TextTrack {

    private final String template = """
                    {
                      "attribute": 0,
                      "flag": 1,
                      "id": "6E070484-81BE-4270-92D1-FA53B54478D7",
                      "is_default_name": true,
                      "name": "",
                      "segments": [],
                      "type": "text"
                    }
            """;

    private final String textSegmentTemplate = """
                {
                          "caption_info": null,
                          "cartoon": false,
                          "clip": {
                            "alpha": 1.0,
                            "flip": {
                              "horizontal": false,
                              "vertical": false
                            },
                            "rotation": 0.0,
                            "scale": {
                              "x": 1.0,
                              "y": 1.0
                            },
                            "transform": {
                              "x": 0.0,
                              "y": -0.73
                            }
                          },
                          "common_keyframes": [],
                          "enable_adjust": false,
                          "enable_color_curves": true,
                          "enable_color_match_adjust": false,
                          "enable_color_wheels": true,
                          "enable_lut": false,
                          "enable_smart_color_adjust": false,
                          "extra_material_refs": [],
                          "group_id": "",
                          "hdr_settings": null,
                          "id": "FF2CFACC-657E-4dad-B894-5E2CC42728F9",
                          "intensifies_audio": false,
                          "is_placeholder": false,
                          "is_tone_modify": false,
                          "keyframe_refs": [],
                          "last_nonzero_volume": 1.0,
                          "material_id": "7C0435AF-8766-462e-A1EE-1DC25A78938B",
                          "render_index": 14000,
                          "responsive_layout": {
                            "enable": false,
                            "horizontal_pos_layout": 0,
                            "size_layout": 0,
                            "target_follow": "",
                            "vertical_pos_layout": 0
                          },
                          "reverse": false,
                          "source_timerange": null,
                          "speed": 1.0,
                          "target_timerange": {
                            "duration": 3433334,
                            "start": 166666
                          },
                          "template_id": "",
                          "template_scene": "default",
                          "track_attribute": 0,
                          "track_render_index": 1,
                          "uniform_scale": {
                            "on": true,
                            "value": 1.0
                          },
                          "visible": true,
                          "volume": 1.0
                        }
            """;

    private long endTime;
    private JSONObject jsonObject;

    public TextTrack() {
        jsonObject = new JSONObject(template);
        jsonObject.set("id", IdUtil.fastUUID());
    }

    public TextTrack addText(String materialId, long start, long end) {
        JSONObject textSegment = new JSONObject(textSegmentTemplate);
        textSegment.set("id", IdUtil.fastUUID());
        textSegment.set("material_id", materialId);
        long duration = end - start;
        JSONObject targetTimerange = textSegment.getJSONObject("target_timerange");
        targetTimerange.set("duration", duration);
        targetTimerange.set("start", start);
        jsonObject.getJSONArray("segments").add(textSegment);
        endTime = Math.max(endTime, end);
        return this;
    }
}
