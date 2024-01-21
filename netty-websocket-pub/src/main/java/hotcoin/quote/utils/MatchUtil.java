package hotcoin.quote.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.utils
 * @ClassName: MatchUtil
 * @Author: hf
 * @Description:
 * @Date: 2019/6/20 14:59
 * @Version: 1.0
 */
public class MatchUtil {
    /**
     * /截取数字
     * @param content
     * @return
     */
    public static String getNumbers(String content) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }
}
