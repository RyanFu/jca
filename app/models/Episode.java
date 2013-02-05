package models;

import play.db.jpa.Model;

import javax.persistence.Entity;

/**
 * @author wujinliang
 * @since 1/29/13
 */
@Entity
public class Episode extends Model {
    /** 第几集 */
    public int e;

    /** 视频地址 */
    public String url;

    /** 视频m3u8地址 */
    public String v;
}
