package libs;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.utils.LongIdEntity.StoredId;
import play.modules.morphia.Model;

public class DBCounter {

    /**
     * 生成递升的计数
     *
     * @param clazz 类
     * @return Long
     */
    public static <T> Long generateUniqueCounter(Class<T> clazz) {
        Datastore ds = Model.ds();
        String collName = ds.getCollection(clazz).getName();
        StoredId newId = ds.findAndModify(ds.find(StoredId.class, "_id", collName), ds.createUpdateOperations(StoredId.class).inc("value"));
        if (newId == null) {
            synchronized (DBCounter.class) {
                newId = new StoredId(collName);
                ds.save(newId);
            }
        }
        return newId.getValue();
    }


}
