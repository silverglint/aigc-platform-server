package space.wenliang.ai.aigcplatformserver.util.jianying.tracks;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;

/**
 * @author Administrator
 * date   2024/10/28
 */
@Data
public class AudioTrack {

    private final String template = """
                   {
                           "attribute": 0,
                           "flag": 0,
                           "id": "187146D4-0921-4d14-8D40-BC008A6765CC",
                           "is_default_name": true,
                           "name": "",
                           "segments": [],
                           "type": "audio"
                         }
            """;

    private final String audioSegmentTemplate = """
                         {
                                          "caption_info": null,
                                          "cartoon": false,
                                          "clip": null,
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
                                          "id": "83CF00DA-A2FB-42ec-9077-D47C6157CE9C",
                                          "intensifies_audio": false,
                                          "is_placeholder": false,
                                          "is_tone_modify": false,
                                          "keyframe_refs": [],
                                          "last_nonzero_volume": 1.0,
                                          "material_id": "D8D0C965-E79F-46d2-BEBF-64DF871E73F9",
                                          "render_index": 0,
                                          "responsive_layout": {
                                            "enable": false,
                                            "horizontal_pos_layout": 0,
                                            "size_layout": 0,
                                            "target_follow": "",
                                            "vertical_pos_layout": 0
                                          },
                                          "reverse": false,
                                          "source_timerange": {
                                            "duration": 12300000,
                                            "start": 0
                                          },
                                          "speed": 1.0,
                                          "target_timerange": {
                                            "duration": 12300000,
                                            "start": 0
                                          },
                                          "template_id": "",
                                          "template_scene": "default",
                                          "track_attribute": 0,
                                          "track_render_index": 0,
                                          "uniform_scale": null,
                                          "visible": true,
                                          "volume": 1.0
                                        }
            """;

    private long endTime;
    private JSONObject jsonObject;

    public AudioTrack() {
        jsonObject = new JSONObject(template);
        jsonObject.set("id", IdUtil.fastUUID());
    }

    public AudioTrack addAudio(String materialId, long start, long end) {
        JSONObject audioSegment = new JSONObject(audioSegmentTemplate);
        audioSegment.set("id", IdUtil.fastUUID());
        audioSegment.set("material_id", materialId);
        long duration = end - start;
        JSONObject sourceTimerange = audioSegment.getJSONObject("source_timerange");
        sourceTimerange.set("duration", duration);
        sourceTimerange.set("start", start);
        audioSegment.set("source_timerange", sourceTimerange);
        JSONObject targetTimerange = audioSegment.getJSONObject("target_timerange");
        targetTimerange.set("duration", duration);
        targetTimerange.set("start", start);
        audioSegment.set("target_timerange", targetTimerange);
        jsonObject.getJSONArray("segments").add(audioSegment);
        endTime = Math.max(endTime, end);
        return this;
    }
}
