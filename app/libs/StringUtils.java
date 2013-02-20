package libs;

import java.io.FileInputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class StringUtils extends org.apache.commons.lang.StringUtils {
	/**
	 * decode string.
	 * 
	 * @param str
	 *            string
	 * @return decoded string
	 */
	public static String decode(final String str) {
		try {
			return URLDecoder.decode(str, "UTF-8");
		} catch (Exception e) {
			return str;
		}
	}
	
	public static String encode(final String str) {
		try {
			return URLEncoder.encode(str, "UTF-8");
		} catch (Exception e) {
			return str;
		}
	}

    public static String defaultIfEmpty(Object obj, String defaultValue) {
        return obj == null ? defaultValue : obj.toString();
    }

	/**
	 *  <p>只允许字母和数字</p>
	 *  <p>String regEx = "[^a-zA-Z0-9]"</p>
	 *  <p>清除掉所有特殊字符.</p>
	 * @param str 传入的字符串
	 * @return 将过滤好的字符串返回
	 * @throws java.util.regex.PatternSyntaxException
	 */
	public static String stringFilter(final String str)
			throws PatternSyntaxException {
		String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

    /**
     * <p>使用给定的数据<code>data</code>，逐个替换一个文件中的含有data.keySet的内容</p>
     * <p>比如：一个文件的内容是：hello:${pkg}</p>
     * <p>data={pkg:"abc"}</p>
     * <p>那么parseFile("文件名路径", data)==hello:abc</p>
     *
     * @param file 文件名
     * @param data 要替换的数据
     * @return 替换后的内容
     */
    public static String parseFile(String file, Map<String, String> data) {
        FileInputStream fis = null;
        String str = null;
        try {
            fis = new FileInputStream(file);
            str = IOUtils.toString(fis);
            for (String key : data.keySet()) {
                str = str.replace("${" + key + "}", data.get(key));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(fis);
        }
        return str;
    }
}
