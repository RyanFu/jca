package jobs;

import libs.DBCounter;
import libs.EasyMap;
import models.Movie;
import models.MovieItem;
import models.Setting;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.codehaus.jackson.map.ObjectMapper;
import play.Logger;
import play.Play;
import play.jobs.Job;
import play.jobs.On;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author wujinliang
 * @since 1/29/13
 */
//@OnApplicationStart
//@On("0 0 5 * * ?")
public class MovieGenerator extends Job {
    private static String DEST_FOLDER = Play.configuration.getProperty("movie.dest.folder");
    private static ObjectMapper mapper = new ObjectMapper();

    @Override
    public void doJob() throws Exception {
        Logger.info("开始生成电影静态文件");
        List<Movie> movies = Movie.find("order by no asc").fetch();
        List<Map> data = new ArrayList<Map>();
        for (Movie movie : movies) {
            Set details = mapper.readValue(movie.details, Set.class);
            if (!details.isEmpty()) {
                double rate = movie.rate;
                if (rate == 0) {
                    rate = (8 + Math.floor(RandomUtils.nextFloat() * 10) / 10);
                }
                EasyMap<String, Object> map = new EasyMap<String, Object>("id", movie.bid).easyPut("rate", rate)
                        .easyPut("name", movie.name).easyPut("cover", movie.cover).easyPut("cover_title", movie.cover_title);
                data.add(map);

                // 生成单个电影文件
                for (Object obj : details) {
                    MovieItem item = (MovieItem)obj;
                    String season = item.season;
                    if (StringUtils.isBlank(season)) item.season = "1";
                }
                Logger.info("正在生成文件:" + movie.name + ", 大小为：" + (details).size());
                IOUtils.write(mapper.writeValueAsString(new EasyMap<String, Object>().easyPutAll(map).easyPut("details", details)), new FileOutputStream(new File(DEST_FOLDER, movie.bid + ".html")), "UTF-8");
            }
        }
        List<Setting> settings = Setting.findAll();
        List<Map> settingData = new ArrayList<Map>();
        for (Setting setting : settings) {
            settingData.add(new EasyMap<String, Object>("title", setting.title).easyPut("type", setting.type).easyPut("value", setting.value));
        }

        Long count = DBCounter.generateMySQLCounter(Setting.class);
        String map = mapper.writeValueAsString(new EasyMap<String, Object>("checksum", count + "").easyPut("data", data).easyPut("setting", settingData));
        Logger.info("正在上传文件:" + movies.size());
        IOUtils.write(map, new FileOutputStream(new File(DEST_FOLDER, "list.html")), "UTF-8");

        // 修改check_update
        IOUtils.write("{\"code\":\"nok\"}", new FileOutputStream(new File(DEST_FOLDER, "check_update_crc="+(count - 1)+".html")), "UTF-8");
        IOUtils.write("{\"code\":\"ok\"}", new FileOutputStream(new File(DEST_FOLDER, "check_update_crc="+(count)+".html")), "UTF-8");

        Logger.info("完成静态文件的生成");
    }
}
