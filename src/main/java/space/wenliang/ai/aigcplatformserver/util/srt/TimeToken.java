package space.wenliang.ai.aigcplatformserver.util.srt;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhanglianyong
 * 2022/12/23 11:25
 */
public class TimeToken {

    private final SRT srt;

    public TimeToken(SRT srt) {
        this.srt = srt;
    }

    /**
     * startTime   -->     endTime
     * 解析时间
     *
     * @param timeToken 时间的句子
     */
    public void parseTime(String timeToken) {
        StringBuilder sb = new StringBuilder();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < timeToken.length(); i++) {
            if (isToken(timeToken.charAt(i))) {
                if (sb.length() == 0 || sb.length() < 12) {
                    continue;
                }
                list.add(sb.toString());
                sb.delete(0, sb.length());
            } else {
                if (timeToken.charAt(i) == ' ' || timeToken.charAt(i) == '\n') {
                    continue;
                }
                sb.append(timeToken.charAt(i));
            }
        }
        if (sb.length() >= 12) {
            list.add(sb.toString());
        }
        setStartTimeAndEndTime(list);
    }

    private boolean isToken(char ch) {
        return "-->".contains(String.valueOf(ch));
    }

    private void setStartTimeAndEndTime(List<String> time) {
        if (time.size() < 2) {
            throw new RuntimeException("字幕文件不合法！！！");
        }
        String startTime = time.get(0);
        String endTime = time.get(1);
        int lastIndexOfStartTime = startTime.lastIndexOf(',');
        int lastIndexOfEndTime = endTime.lastIndexOf(',');
        String[] startArray = startTime.substring(0, lastIndexOfStartTime).split(":");
        String[] endArray = endTime.substring(0, lastIndexOfEndTime).split(":");
        if (startArray.length < 3 || endArray.length < 3) {
            throw new RuntimeException("字幕文件不合法！！！");
        }
        int beginHour = Integer.parseInt(startArray[0]);
        int beginMintue = Integer.parseInt(startArray[1]);
        int beginSecond = Integer.parseInt(startArray[2]);
        int beginMilli = Integer.parseInt(startTime.substring(lastIndexOfStartTime + 1, startTime.length()));
        int beginTime = (beginHour * 3600 + beginMintue * 60 + beginSecond)
                * 1000 + beginMilli;
        srt.setBeginTime(beginTime);

        int endHour = Integer.parseInt(endArray[0]);
        int endMintue = Integer.parseInt(endArray[1]);
        int endSecond = Integer.parseInt(endArray[2]);
        // 校验时间格式
        if (!isValid(endMintue, endSecond, beginMintue, beginSecond)) {
            throw new RuntimeException("时间格式不合法！！！");
        }
        int endMilli = Integer.parseInt(endTime.substring(lastIndexOfStartTime + 1, endTime.length()));
        int endTimeInt = (endHour * 3600 + endMintue * 60 + endSecond) * 1000 + endMilli;
        srt.setEndTime(endTimeInt);
    }

    /**
     * 判断实践格式
     *
     * @return
     */
    private boolean isValid(int... value) {
        int len = value.length;
        for (int val : value) {
            if (val < 0 || val >= 60) {
                return false;
            }
        }
        return true;
    }

}
