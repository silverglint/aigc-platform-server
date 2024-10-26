package space.wenliang.ai.aigcplatformserver.util.srt;

import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.TreeMap;

/**
 * @author zhanglianyong
 * 2022/12/23 10:46
 */
public class SrtUtils {

    @SneakyThrows
    public static TreeMap<Integer, SRT> parseSrt(String content) {
        return parseSrt(new ByteArrayInputStream(content.getBytes()));
    }

    /**
     * 解析SRT字幕文件
     *
     * @param is 流
     * @return
     */
    @SneakyThrows
    public static TreeMap<Integer, SRT> parseSrt(InputStream is) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        TreeMap<Integer, SRT> srtMap = new TreeMap<>();
        StringBuilder sb = new StringBuilder();
        int key = 0;
        String s = "";
        while ((line = br.readLine()) != null || sb.length() > 0) {
            if (!"".equals(line) && line != null) {
                sb.append(line).append("@");
                continue;
            }
            String[] parseStrs = sb.toString().split("@");
            // 该if为了适应一开始就有空行以及其他不符格式的空行情况
            if (parseStrs.length < 3) {
                continue;
            }
            SRT srt = new SRT();
            StringBuilder srtBody = new StringBuilder();
            TimeToken timeToken = new TimeToken(srt);
            // 可能1句字幕，也可能2句及以上。
            srt.setId(Integer.valueOf(parseStrs[0].trim()));
            timeToken.parseTime(parseStrs[1]);
            // 删除最后一个"\n"
            for (int i = 2; i < parseStrs.length; i++) {
                srtBody.append(parseStrs[i], 0, parseStrs[i].length() - 1);
            }
            srt.setSrtBody(new String(srtBody.toString().getBytes(), StandardCharsets.UTF_8));
            srtMap.put(key, srt);
            key++;
            // 清空，否则影响下一个字幕元素的解析
            sb.delete(0, sb.length());
        }
        return srtMap;
    }

}
