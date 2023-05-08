package cn.bob.idea.plugin.common.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * ui²ÎÊımap
 *
 * @author guojianbo
 * @date 2023/5/4 18:41
 */
public class ParamsMap {
    private static ParamsMap instance = new ParamsMap();

    public static ParamsMap getInstance() {
        return instance;
    }

    private Map<String,String> params = new HashMap<>();

    public String get(String key){
        return params.get(key);
    }

    public String put(String key,String values){
        return params.put(key,values);
    }
}
