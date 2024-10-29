package space.wenliang.ai.aigcplatformserver.util.jianying.tracks;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import lombok.Data;

/**
 * @author Administrator
 * date   2024/10/27
 */
@Data
public class VideoTrack {

    private final String template = """
                    {
                      "attribute": 0,
                      "flag": 0,
                      "id": "B01A0FF5-6F5B-4760-AF0F-9CEB6F1C4131",
                      "is_default_name": true,
                      "name": "",
                      "segments": [],
                      "type": "video"
                    }
            """;

    private final String imageSegmentTemplate = """
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
                                       "y": 0.0
                                     }
                                   },
                                   "common_keyframes": [],
                                   "enable_adjust": true,
                                   "enable_color_curves": true,
                                   "enable_color_match_adjust": false,
                                   "enable_color_wheels": true,
                                   "enable_lut": true,
                                   "enable_smart_color_adjust": false,
                                   "extra_material_refs": [],
                                   "group_id": "",
                                   "hdr_settings": {
                                     "intensity": 1.0,
                                     "mode": 1,
                                     "nits": 1000
                                   },
                                   "id": "9EA21DC7-B72B-4a7c-AABD-E3C0C1E99C32",
                                   "intensifies_audio": false,
                                   "is_placeholder": false,
                                   "is_tone_modify": false,
                                   "keyframe_refs": [],
                                   "last_nonzero_volume": 1.0,
                                   "material_id": "C5C25736-94C9-4c3b-AA0D-3AC44C8D652B",
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
                                     "duration": 6366666,
                                     "start": 0
                                   },
                                   "speed": 1.0,
                                   "target_timerange": {
                                     "duration": 6366666,
                                     "start": 0
                                   },
                                   "template_id": "",
                                   "template_scene": "default",
                                   "track_attribute": 0,
                                   "track_render_index": 0,
                                   "uniform_scale": {
                                     "on": true,
                                     "value": 1.0
                                   },
                                   "visible": true,
                                   "volume": 1.0
                               }
            """;

    private final String keyFramsTemplate = """
            [
                           {
                             "id": "2FE4E40B-6F1D-4a39-8E01-566B797DFF94",
                             "keyframe_list": [
                               {
                                 "curveType": "Line",
                                 "graphID": "",
                                 "id": "E88FBD90-0E28-4298-9E5B-2EA5719A8D88",
                                 "left_control": {
                                   "x": 0.0,
                                   "y": 0.0
                                 },
                                 "right_control": {
                                   "x": 0.0,
                                   "y": 0.0
                                 },
                                 "time_offset": 0,
                                 "values": [
                                   -0.1
                                 ]
                               },
                               {
                                 "curveType": "Line",
                                 "graphID": "",
                                 "id": "CAD3D147-7E8B-4585-9FB7-58304A7C29F3",
                                 "left_control": {
                                   "x": 0.0,
                                   "y": 0.0
                                 },
                                 "right_control": {
                                   "x": 0.0,
                                   "y": 0.0
                                 },
                                 "time_offset": 866666,
                                 "values": [
                                   0.1
                                 ]
                               }
                             ],
                             "material_id": "",
                             "property_type": "KFTypePositionX"
                           },
                           {
                             "id": "C94C678E-0B08-4a8d-AF4D-AEA6C2C980A8",
                             "keyframe_list": [
                               {
                                 "curveType": "Line",
                                 "graphID": "",
                                 "id": "41A21F70-5A88-484f-B820-5F255A33628C",
                                 "left_control": {
                                   "x": 0.0,
                                   "y": 0.0
                                 },
                                 "right_control": {
                                   "x": 0.0,
                                   "y": 0.0
                                 },
                                 "time_offset": 0,
                                 "values": [
                                   0.0
                                 ]
                               },
                               {
                                 "curveType": "Line",
                                 "graphID": "",
                                 "id": "CD4EAA0C-E548-4599-A43D-BEAB22C4BFFA",
                                 "left_control": {
                                   "x": 0.0,
                                   "y": 0.0
                                 },
                                 "right_control": {
                                   "x": 0.0,
                                   "y": 0.0
                                 },
                                 "time_offset": 866666,
                                 "values": [
                                   0.0
                                 ]
                               }
                             ],
                             "material_id": "",
                             "property_type": "KFTypePositionY"
                           },
                           {
                             "id": "7CD3F5DF-5997-4f64-8DD8-F749FE5DD844",
                             "keyframe_list": [
                               {
                                 "curveType": "Line",
                                 "graphID": "",
                                 "id": "B87FE42A-8CAE-4462-B910-D65BE8CF924C",
                                 "left_control": {
                                   "x": 0.0,
                                   "y": 0.0
                                 },
                                 "right_control": {
                                   "x": 0.0,
                                   "y": 0.0
                                 },
                                 "time_offset": 0,
                                 "values": [
                                   1.2
                                 ]
                               },
                               {
                                 "curveType": "Line",
                                 "graphID": "",
                                 "id": "44822368-BF36-4522-81D7-93ED94A6628A",
                                 "left_control": {
                                   "x": 0.0,
                                   "y": 0.0
                                 },
                                 "right_control": {
                                   "x": 0.0,
                                   "y": 0.0
                                 },
                                 "time_offset": 866666,
                                 "values": [
                                   1.2
                                 ]
                               }
                             ],
                             "material_id": "",
                             "property_type": "KFTypeScaleX"
                           }
                         ]
            """;

    private long endTime;
    private JSONObject jsonObject;

    public VideoTrack() {
        jsonObject = new JSONObject(template);
        jsonObject.set("id", IdUtil.fastUUID());
    }

    public VideoTrack addImg(String materialId, long start, long end) {
        JSONObject img = new JSONObject(imageSegmentTemplate);
        img.set("id", IdUtil.fastUUID());
        img.set("material_id", materialId);
        long duration = end - start;
        JSONObject sourceTimerange = img.getJSONObject("source_timerange");
        sourceTimerange.set("duration", duration);
        sourceTimerange.set("start", start);
        img.set("source_timerange", sourceTimerange);
        JSONObject targetTimerange = img.getJSONObject("target_timerange");
        targetTimerange.set("duration", duration);
        targetTimerange.set("start", start);
        img.set("target_timerange", targetTimerange);
        jsonObject.getJSONArray("segments").add(img);
        endTime = Math.max(endTime, end);
        return this;
    }
}
