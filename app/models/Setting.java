package models;


import org.codehaus.jackson.map.annotate.JsonSerialize;
import play.db.jpa.Model;
import services.serializers.SettingSerializer;

import javax.persistence.Entity;

/**
 * @author wujinliang
 * @since 1/30/13
 */
@Entity
@JsonSerialize(using = SettingSerializer.class)
public class Setting extends Model {
    /** 标题 */
    public String title;

    /** 类型 */
    public String type;

    /** 值 */
    public String value;
}
