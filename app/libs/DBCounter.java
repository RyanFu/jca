package libs;

//import com.google.code.morphia.Datastore;
//import com.google.code.morphia.utils.LongIdEntity.StoredId;
//import play.modules.morphia.Model;

import models.Counter;
import play.db.jpa.JPA;

public class DBCounter {

    /**
     * 生成递升的计数
     *
     * @param clazz 类
     * @return Long
     */
    public static <T> Long generateUniqueCounter(Class<T> clazz) {
//        Datastore ds = Model.ds();
//        String collName = ds.getCollection(clazz).getName();
//        StoredId newId = ds.findAndModify(ds.find(StoredId.class, "_id", collName), ds.createUpdateOperations(StoredId.class).inc("value"));
//        if (newId == null) {
//            synchronized (DBCounter.class) {
//                newId = new StoredId(collName);
//                ds.save(newId);
//            }
//        }
//        return newId.getValue();
        return 0L;
    }

    public static <T> long generateMySQLCounter(Class<T> clazz) {
        if (!JPA.em().getTransaction().isActive()) JPA.em().getTransaction().begin();
        String key = clazz.getSimpleName();
        Counter mCounter = Counter.find("byName", key).first();
        if (mCounter == null) {
            mCounter = new Counter();
            mCounter.count = 1;
            mCounter.name = key;
            mCounter.save();
        } else {
            mCounter.count = mCounter.count + 1;
            mCounter.save();
        }
        JPA.em().flush();
        JPA.em().getTransaction().commit();
        return mCounter.count;
    }

}
