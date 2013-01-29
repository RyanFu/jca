package jobs;

import libs.EasyMap;
import libs.Objects;
import libs.WS;
import models.Episode;
import models.Movie;
import models.MovieItem;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import play.Logger;
import play.jobs.Job;
import play.jobs.On;
import play.jobs.OnApplicationStart;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author wujinliang
 * @since 1/29/13
 */
//@OnApplicationStart
@On("0 0 1 * * ?")
public class MovieSpider extends Job {
    private static ObjectMapper mapper = new ObjectMapper();
    private static int page = 1;
    private static int count = 1;
    private static final String url_tpl = "http://video.baidu.com/tvplay/?area=%C3%C0%B9%FA&actor=&type=&start=&order=hot&pn=";

    @Override
    public void doJob() throws Exception {
        Logger.info("开始处理电影抓取Job");
        Movie.deleteAll();
        crawl();
    }

    private static void crawl() {
        String url = url_tpl + (page++);
        Logger.info("正在抓取：%s", url);
        if (StringUtils.isBlank(url)) return;
        sleep();
        Document doc = Jsoup.parse(WS.url(url).get().body, url);
        Elements elements = doc.select(".video-item");
        if (elements.isEmpty()) return;
        for (Element element : elements) {
            try {
                Element link = element.select(">a").first();
                String cover = link.select("img").first().absUrl("src");
                String coverTitle = link.select(".v-update").first().html();
                String detailUrl = link.absUrl("href");
                String name = element.select(".v-desc .v-title a").first().html();
                Logger.info("正在抓取名称：%s", name);
                Movie movie = new Movie();
                movie.name = name;
                movie.cover = cover;
                movie.cover_title = coverTitle;
                movie.id = (count++) + "";
                List<MovieItem> details = getDetails(movie, "http://video.baidu.com/v?word=" + URLEncoder.encode("美剧 " + name, "GBK"));
                movie.details = details;
                movie.save();
            } catch (Exception e) {
                Logger.error(e.getMessage(), e);
            }
        }
        crawl();
    }

    private static List<MovieItem> getDetails(Movie movie, String url) {
        sleep();
        List<MovieItem> result = new ArrayList<MovieItem>();
        Logger.info("正在抓取Detail界面：%s", url);
        String body = WS.url(url).get().body;
        String rating = null;
        String[] blocks = StringUtils.substringsBetween(body, "T.object.extend(", ", {\"alias\":\"");
        if (blocks == null){
            System.out.println(url);
            System.out.println(body);
            System.exit(-1);
            return result;
        }
        for (String block : blocks) {
            try {
                Map map = mapper.readValue(block, Map.class);
                Object id = map.get("id");
                List<Map> sites = (List<Map>) map.get("sites");
                Map site = sites.get(0);
                Object from = site.get("site_name");
                List<Map> es = (List<Map>) site.get("episode");
                if ("tudou.com".equals(site.get("site_url")) && sites.size() > 1) { // 土豆不支持html5
                    site = sites.get(1);
                    from = site.get("site_name");
                    String b = WS.url("http://video.baidu.com/htvplaysingles/?id=" + id + "&site=" + site.get("site_url")).get().body;
                    es = (List<Map>)mapper.readValue(b, Map.class).get("videos");
                }
                if ("tudou.com".equals(site.get("site_url"))) {
                    return result;
                }
                List<Episode> episodes = new ArrayList<Episode>();
                for (Map e : es) {
                    String webUrl = e.get("url").toString();
                    if (webUrl.contains("baidu.com")) {
                        continue;
                    }
                    Episode episode = new Episode();
                    episode.e = NumberUtils.toInt(e.get("episode").toString());
                    episode.url = webUrl;
                    episode.v = "";
                    episodes.add(episode);
                }
                Collections.sort(episodes, new Comparator<Episode>() {
                    public int compare(Episode o1, Episode o2) {
                        return o1.e < o2.e ? -1 : 1;
                    }
                });
                if (rating == null && map.get("rating") != null && StringUtils.isNotBlank(map.get("rating").toString())) {
                    rating = map.get("rating").toString();
                }
                Object season = map.get("season");
                if (season == null || "0".equals(season)) season = "1";
                MovieItem item = new MovieItem();
                item.brief = Objects.de4(map.get("brief"), "");
                item.from = Objects.de4(from, "");
                item.season = Objects.de4(season, "");
                item.actors = (List<String>) map.get("actor");
                item.episodes = episodes;
                result.add(item);
            } catch (IOException e) {
                Logger.error(e.getMessage(), e);
            }
        }
        if (rating == null) rating = (8 + Math.floor(RandomUtils.nextFloat() * 10) / 10) + "";
        movie.rate = rating;
        Collections.sort(result, new Comparator<MovieItem>() {
            public int compare(MovieItem o1, MovieItem o2) {
                return NumberUtils.toInt(o1.season) < NumberUtils.toInt(o2.season) ? 1 : -1;
            }
        });
        return result;
    }

    private static void sleep() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
