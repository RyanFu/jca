package services.serializers;

import models.Movie;
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
public class MovieSerializer extends JsonSerializer<Movie> {

    @Override
    public void serialize(Movie m, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        Map<String, Object> map = toMap(m);

        jgen.writeObject(map);
    }

    public Map<String, Object> toMap(Movie m) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", m.id);
        map.put("name", m.name);
        map.put("no", m.no);
        map.put("cover", m.cover);
        map.put("cover_title", m.cover_title);
        map.put("rate", m.rate);
        return map;
    }
}
