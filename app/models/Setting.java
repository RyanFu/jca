package models;


import play.db.jpa.Model;

import javax.persistence.Entity;

/**
 * @author wujinliang
 * @since 1/30/13
 */
@Entity
public class Setting extends Model {
    /** 标题 */
    public String title;

    /** 类型 */
    public String type;

    /** 值 */
    public String value;
}
