package blxt.qandroid.base;

import java.util.HashMap;
import java.util.Map;

public class DataPool {

    static DataPool dataPool = null;

    static {
        dataPool = new DataPool();
    }

    Map<String, Object> datas = new HashMap<>();

    DataPool(){

    }

    public static DataPool getInstance(){
        return dataPool;
    }

    public void put(String key, Object data){
        datas.put(key, data);
    }

    public Object get(String key){
        return datas.get(key);
    }

    public Object poll(String key){
        Object object = datas.get(key);
        datas.remove(key);
        return object;
    }

}
