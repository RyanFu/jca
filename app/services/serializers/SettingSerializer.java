package services.serializers;

import models.Setting;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wujinliang
 * @since 2013-02-05
 */
public class SettingSerializer extends JsonSerializer<Setting> {

    @Override
    public void serialize(Setting m, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("title", m.title);
        map.put("type", m.type);
        map.put("value", m.value);

        jgen.writeObject(map);
    }
}
