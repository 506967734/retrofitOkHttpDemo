package com.zd.retrofitlibrary.utils;

import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by zhudi on 2017/8/16.
 * json解析类
 */

public class JsonUtil {

    /**
     * model解析
     *
     * @param jsonStr
     * @param t
     * @return
     */
    public static Object jsonAnalysisModel(String jsonStr, Class t) {
        return new Gson().fromJson(jsonStr, t);
    }

    /**
     * List解析
     *
     * @param jsonStr
     * @param t
     * @return
     */
    public static Object jsonAnalysisList(String jsonStr, Class t) {
        return new Gson().fromJson(jsonStr, type(t));
    }

    private static ParameterizedType type(final Type... args) {
        return new ParameterizedType() {
            public Type getRawType() {
                return List.class;
            }

            public Type[] getActualTypeArguments() {
                return args;
            }

            public Type getOwnerType() {
                return null;
            }
        };
    }
}
