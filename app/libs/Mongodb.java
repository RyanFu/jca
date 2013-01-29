package libs;

import com.mongodb.*;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import org.apache.commons.lang.math.NumberUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * mongodb 工具类
 * usage:
 *  Mongodb.save("users", datas);
 *
 * Date: 2010-7-8 Time: 22:10:37
 * Author: wujinliang
 */
public class Mongodb {
    private static final Logger logger = LoggerFactory.getLogger(Mongodb.class);
    private static com.mongodb.DB db;
    private static final Map dbConfig = Config.getMap("db", Collections.emptyMap());
    private static final String SERVER = dbConfig.get("host").toString();
    private static final String DB = dbConfig.get("name").toString();
    private static final String USER = Objects.de4(dbConfig.get("user"), null);
    private static final String PASSWD = Objects.de4(dbConfig.get("pwd"), null);
    private static final int DEFAULT_PORT = 27017;

    /**
	 * Obtain a reference to the mongo database.
	 *
	 * @return - a reference to the Mongo database
	 */
	public static com.mongodb.DB db() {
		if (db == null){
            MongoOptions options = new MongoOptions();
            options.slaveOk = true;
            Mongo mongo = null;
            try {
                List<ServerAddress> replicaSets = new ArrayList<ServerAddress>();
                String[] servers = SERVER.split(",");
                if (servers.length == 0) {
                    throw new Error("mongodb never config, please check");
                }
                for (String server : servers) {
                    String[] addrPort = server.split(":");
                    replicaSets.add(new ServerAddress(addrPort[0], addrPort.length > 1 ? NumberUtils.toInt(addrPort[1], DEFAULT_PORT) : DEFAULT_PORT));
                }
                if (replicaSets.size() == 1) mongo = new Mongo(replicaSets.get(0), options);
                else mongo = new Mongo(replicaSets, options);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
            db = mongo.getDB(DB);
            if (USER != null && PASSWD != null) db.authenticate(USER, PASSWD.toCharArray());
            logger.info("mongodb server:{}, user:{}, database:{}", new Object[]{SERVER, USER, DB});
        }
		return db;
	}

    /**
     * 保存一组数据
     *
     * @param collectionName collection名称
     * @param datas 要保存的数据
     */
    public static void save(String collectionName, Map... datas) {
        List<DBObject> trunks = new ArrayList<DBObject>();
        for (Map data : datas) {
            Object id = data.get("_id");
            if (id == null) id = ObjectId.get();
            data.put("_id", id);
            BasicDBObject dbObject = new BasicDBObject(data);
            trunks.add(dbObject);
        }
        db().getCollection(collectionName).insert(trunks);
    }

    /**
     * 保存或更新数据，根据_id来决定
     * @param collectionName collection名称
     * @param data 数据
     */
    public static void saveOrUpdate(String collectionName, Map data) {
        Object id = data.get("_id");
        if (id == null) id = ObjectId.get();
        data.put("_id", id);
        BasicDBObject dbObject = new BasicDBObject(data);
        db().getCollection(collectionName).save(dbObject);
    }

    public static void set(String tableName, Map condition, Map data, boolean upsert) {
        if (condition != null) condition.put("$atomic", 1);// 原子操作
        if (data.containsKey("_id")) data.remove("_id");
        Mongodb.db().getCollection(tableName).update(Mongodb.getDBObject(condition), new BasicDBObject("$set", Mongodb. getDBObject(data)), upsert, true);
    }

    /**
     * 返回满足condition条件的数据，并修改为指定的{@param options}
     *
     * e.g:
     *      condition={_id:1}, data={$set:{name:abc}} 表示把_id为1的那条记录设置更新为：{_id:1,name:abc}
     * @param tableName 表名
     * @param query 条件
     * @param sort sort
     * @param update options
     * @return map
     */
    public static Map findAndModify(String tableName, Map query, Map sort, Map update) {
        DBObject object = Mongodb.db().getCollection(tableName).findAndModify(Mongodb.getDBObject(query), Mongodb.getDBObject(sort), Mongodb.getDBObject(update));
        if (object != null) return object.toMap();
        return null;
    }

    /**
     * 查找记录并删除和返回其中一条，这个操作是原子性操作
     * @param tableName 表名
     * @param query 查询条件
     * @return map
     */
    public static Map findAndRemove(String tableName, Map query) {
        DBObject object = Mongodb.db().getCollection(tableName).findAndModify(Mongodb.getDBObject(query), null, null, true, null, false, false);
        if (object != null) return object.toMap();
        return null;
    }

    /**
     * 返回指定collection的所有数据
     * @param collectionName collection 名称
     * @return 所有数据
     */
    public static List<Map> findAll(String collectionName) {
        return find(collectionName, Collections.emptyMap());
    }

    public static List<Map> find(String collectionName, Map query) {
        return find(collectionName, query, -1, -1);
    }

    public static List<Map> find(String collectionName, Map query, int start, int limit) {
        DBObject queryDBObject = getDBObject(query);
        DBCursor cursor = null;
        if (start < 0) start = 0;
        cursor = db().getCollection(collectionName).find(queryDBObject);
        if (limit > 0) cursor = cursor.skip(start).limit(limit);
        List<Map> result = new ArrayList<Map>();
        try {
            while (cursor.hasNext()) {
                DBObject data = cursor.next();
                if (data != null) result.add(data.toMap());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * @see Mongodb#sort(String, java.util.Map, java.util.Map, int, int)
     */
    public static List<Map> sort(String collectionName, Map query, Map sort) {
        return sort(collectionName, query, sort, -1, -1);
    }

    /**
     * 按某个顺序排列返回
     * @param collectionName collection名称
     * @param query 查询条件
     * @param sort 排序对象，e.g: {version:-1, name:1}就表示按version降序排并按name升序排
     * @param start 起始
     * @param limit 返回的个数
     * @return 排列后的有序内容
     */
    public static List<Map> sort(String collectionName, Map query, Map sort, int start, int limit) {
        return sort(collectionName, query, sort, null, start, limit);
    }
    public static List<Map> sort(String collectionName, Map query, Map sort, Map field, int start, int limit) {
        if (sort == null) sort = Collections.emptyMap();
        DBObject queryDBObject = getDBObject(query);
        DBCursor cursor = null;
        if (start < 0) start = 0;
        if (field == null) cursor = db().getCollection(collectionName).find(queryDBObject).sort(new BasicDBObject(sort));
        else cursor = db().getCollection(collectionName).find(queryDBObject, getDBObject(field)).sort(new BasicDBObject(sort));
        if (limit > 0) cursor = cursor.skip(start).limit(limit);
        List<Map> result = new ArrayList<Map>();
        try {
            while (cursor.hasNext()) {
                DBObject data = cursor.next();
                if (data != null) result.add(data.toMap());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static long count(String collectionName) {
        return count(collectionName, Collections.emptyMap());
    }

    public static long count(String collectionName, Map query) {
        DBObject queryDBObject = getDBObject(query);
        return db().getCollection(collectionName).count(queryDBObject);
    }

    public static void clear(String collectionName) {
        remove(collectionName, Collections.emptyMap());
    }

    public static void remove(String collectionName, Map data) {
        DBObject dbObject = getDBObject(data);
        db().getCollection(collectionName).remove(dbObject);
    }

    public static void drop(String collectionName) {
        db().getCollection(collectionName).drop();
    }

    /**
     * 保存文件
     * @param file 文件
     * @param props 文件属性
     */
    public static void saveFile(File file, Map<String, Object> props) throws IOException {
        GridFSInputFile gif = new GridFS(db()).createFile(file);
        for (Map.Entry<String, Object> entry : props.entrySet()) {
            gif.put(entry.getKey(), entry.getValue());
        }
        gif.save();
    }

    /**
     * 保存文件
     * @param is 文件流
     * @param props 文件属性
     */
    public static void saveFile(InputStream is, Map<String, Object> props) {
        GridFSInputFile gif = new GridFS(db()).createFile(is);
        for (Map.Entry<String, Object> entry : props.entrySet()) {
            gif.put(entry.getKey(), entry.getValue());
        }
        gif.save();
    }

    /**
     * 找出一堆文件
     * @param condition 条件
     * @return 符号条件的文件
     */
    public static List<GridFSDBFile> findFiles(Map condition) {
        return new GridFS(db()).find(new BasicDBObject(condition));
    }

    public static BasicDBObject getDBObject(Map query) {
        if (query == null) query = Collections.emptyMap();
        if (query instanceof BasicDBObject) return (BasicDBObject) query;
        BasicDBObject dbObj = new BasicDBObject();
        for (Object key : query.keySet()) {
            if (key != null) {
                Object value = query.get(key);
                if (value != null && value instanceof Map) dbObj.put(key.toString(), getDBObject((Map) value));
                else dbObj.append(key.toString(), value);
            }
        }
        return dbObj;
    }
}
