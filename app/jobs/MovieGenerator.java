package jobs;

import libs.EasyMap;
import models.Movie;
import models.MovieItem;
import models.Setting;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.codehaus.jackson.map.ObjectMapper;
import play.Logger;
import play.Play;
import play.jobs.Job;
import play.jobs.On;
import play.jobs.OnApplicationStart;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wujinliang
 * @since 1/29/13
 */
//@OnApplicationStart
//@On("0 0 5 * * ?")
public class MovieGenerator {
    private static String DEST_FOLDER = Play.configuration.getProperty("movie.dest.folder");
    private static ObjectMapper mapper = new ObjectMapper();

    public void doJob() throws Exception {
        Logger.info("开始生成电影静态文件");
        FileUtils.deleteDirectory(new File(DEST_FOLDER));
        FileUtils.forceMkdir(new File(DEST_FOLDER));
        List<Movie> movies = Movie.find("order by no asc").fetch();
        List<Map> data = new ArrayList<Map>();
        for (Movie movie : movies) {
            if (StringUtils.isNotBlank(movie.details)) {
                String rate = movie.rate;
                if (StringUtils.isBlank(rate)) {
                    rate = (8 + Math.floor(RandomUtils.nextFloat() * 10) / 10) + "";
                }
                EasyMap<String, Object> map = new EasyMap<String, Object>("id", movie.id + "").easyPut("rate", NumberUtils.toFloat(rate))
                        .easyPut("name", movie.name).easyPut("no", movie.no).easyPut("cover", movie.cover).easyPut("cover_title", movie.cover_title);

                // 生成单个电影文件
                List<Map> details = mapper.readValue(movie.details, List.class);
                for (Map item : details) {
                    String season = item.get("season").toString();
                    if (StringUtils.isBlank(season)) item.put("season", "1");
                }
                Logger.info("正在生成文件:" + movie.name + ", 大小为：" + details.size());
                if (!details.isEmpty()) {
                    data.add(map);
                    File file = new File(DEST_FOLDER, movie.id + ".html");
                    if (!file.exists()) file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    try {
                        IOUtils.write(mapper.writeValueAsString(new EasyMap<String, Object>().easyPutAll(map).easyPut("details", details)), fos, "UTF-8");
                    } finally {
                        IOUtils.closeQuietly(fos);
                    }
                }
            }
        }
        List<Setting> settings = Setting.findAll();
        List<Map> settingData = new ArrayList<Map>();
        for (Setting setting : settings) {
            settingData.add(new EasyMap<String, Object>("title", setting.title).easyPut("type", setting.type).easyPut("value", setting.value));
        }

        Long count = 1L;//DBCounter.generateUniqueCounter(Setting.class);
        String map = mapper.writeValueAsString(new EasyMap<String, Object>("checksum", count + "").easyPut("data", data).easyPut("setting", settingData));
        Logger.info("正在上传文件:" + movies.size());
        File file = new File(DEST_FOLDER, "list.html");
        if (!file.exists()) file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        try {
            IOUtils.write(map, fos, "UTF-8");
        } finally {
            IOUtils.closeQuietly(fos);
        }

        // 修改check_update
        IOUtils.write("{\"code\":\"nok\"}", new FileOutputStream(new File(DEST_FOLDER, "check_update_crc="+(count - 1)+".html")), "UTF-8");
        IOUtils.write("{\"code\":\"ok\"}", new FileOutputStream(new File(DEST_FOLDER, "check_update_crc="+(count)+".html")), "UTF-8");

        Logger.info("完成静态文件的生成");
    }
}
