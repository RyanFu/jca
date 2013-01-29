package models;

import com.google.code.morphia.annotations.Embedded;

/**
 * @author wujinliang
 * @since 1/29/13
 */
@Embedded
public class Episode {
    /** 第几集 */
    public int e;

    /** 视频地址 */
    public String url;

    /** 视频m3u8地址 */
    public String v;
}
