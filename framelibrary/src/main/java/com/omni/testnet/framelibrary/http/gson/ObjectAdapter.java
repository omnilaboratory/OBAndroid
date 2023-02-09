package com.omni.testnet.framelibrary.http.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.omni.testnet.baselibrary.utils.LogUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ObjectAdapter extends TypeAdapter<Object> {
    private static final String TAG = ObjectAdapter.class.getSimpleName();
    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
        @SuppressWarnings("unchecked")
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (type.getRawType() == Object.class) {
                return (TypeAdapter<T>) new ObjectAdapter(gson);
            }
            return null;
        }
    };

    private final Gson gson;

    public ObjectAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public Object read(JsonReader in) throws IOException {
        JsonToken token = in.peek();
        switch (token) {
            case BEGIN_ARRAY:
                List<Object> list = new ArrayList<>();
                in.beginArray();
                while (in.hasNext()) {
                    list.add(read(in));
                }
                in.endArray();
                return list;

            case BEGIN_OBJECT:
                Map<String, Object> map = new LinkedTreeMap<>();
                in.beginObject();
                while (in.hasNext()) {
                    map.put(in.nextName(), read(in));
                }
                in.endObject();
                return map;

            case STRING:
                return in.nextString();

            case NUMBER:
                LogUtils.e(TAG, "======NUMBER======>");
                try {
                    // 将源数据先作为一个字符串读取出来
                    String numberStr = in.nextString(); //返回的numberStr不会为null
                    if (numberStr.contains(".") || numberStr.contains("e") || numberStr.contains("E")) {
                        LogUtils.e(TAG, "======NUMBER======>" + numberStr);
                        return Double.parseDouble(numberStr);
                    }
                    long longValue = Long.parseLong(numberStr);
                    if (longValue <= Integer.MAX_VALUE && longValue >= Integer.MIN_VALUE) {
                        return Integer.parseInt(numberStr);
                    } else {
                        return Long.parseLong(numberStr);
                    }
                } catch (Exception e) {
                    LogUtils.e(TAG, "Gson解析报错：" + e.getMessage());
                    e.printStackTrace();
                }
                return in.nextDouble();

            case BOOLEAN:
                return in.nextBoolean();

            case NULL:
                in.nextNull();
                return null;

            default:
                throw new IllegalStateException();
        }
    }


    @Override
    public void write(JsonWriter out, Object value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.beginObject();
        out.endObject();
    }
}
