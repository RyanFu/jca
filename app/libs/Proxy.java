package libs;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.math.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wujinliang
 * @since 2012-02-13
 */
public class Proxy {
    private static List<ProxyEntry> entries = new ArrayList<ProxyEntry>();

    public static ProxyEntry getRandomProxy() {
        if (entries.size() < 10) {
//            List<Map> datas = Mongodb.find("proxy", new EasyMap("status", 1));
//            for (Map data : datas) {
//                ProxyEntry entry = new ProxyEntry();
//                entry.host = data.get("ip").toString();
//                entry.port = NumberUtils.toInt(data.get("port").toString());
//                entries.add(entry);
//            }
        }
        return entries.isEmpty() ? null : entries.get(RandomUtils.nextInt(entries.size()));
    }

    public static void removeProxy(ProxyEntry entry) {
        entries.remove(entry);
    }

    public static class ProxyEntry {
        public String host;
        public int port;
    }
}
