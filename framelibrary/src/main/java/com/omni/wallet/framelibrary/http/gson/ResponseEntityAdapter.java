package com.omni.wallet.framelibrary.http.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.omni.wallet.framelibrary.entity.HttpResponseEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ResponseEntityAdapter extends TypeAdapter<HttpResponseEntity> {
    private static final String TAG = ResponseEntityAdapter.class.getSimpleName();
    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
        @SuppressWarnings("unchecked")
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            // 只有当泛型类型是HttpResponseEntity时才用这个Adapter去解析
            if (type.getRawType() == HttpResponseEntity.class) {
                return (TypeAdapter<T>) new ResponseEntityAdapter(gson);
            }
            return null;
        }
    };

    private final Gson gson;

    public ResponseEntityAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    @SuppressWarnings("unchecked")
    public HttpResponseEntity read(JsonReader in) throws IOException {
        // 把整体的Json数据解析成一个Map，然后手动赋值到HttpResponseEntity实体中
        HttpResponseEntity data = new HttpResponseEntity();
        try {
            Map<String, Object> dataMap = (Map<String, Object>) readInternal(in);
            if (dataMap != null) {
                if (dataMap.get("info") != null) {
                    data.setInfo(dataMap.get("info").toString());
                }
                data.setData(dataMap.get("data"));
                if (dataMap.get("code") != null) {
                    data.setCode(dataMap.get("code").toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }


    /**
     * Json数据解析，整个Json数据的解析都会用这个方法完成
     */
    private Object readInternal(JsonReader in) throws IOException {
        JsonToken token = in.peek();
        switch (token) {
            case BEGIN_ARRAY:
                List<Object> list = new ArrayList<>();
                in.beginArray();
                while (in.hasNext()) {
                    list.add(readInternal(in));
                }
                in.endArray();
                return list;

            case BEGIN_OBJECT:
                Map<String, Object> map = new LinkedTreeMap<>();
                in.beginObject();
                while (in.hasNext()) {
                    map.put(in.nextName(), readInternal(in));
                }
                in.endObject();
                return map;

            case STRING:
                return in.nextString();

            case NUMBER:
                try {
                    // 将源数据先作为一个字符串读取出来
                    String numberStr = in.nextString(); //返回的numberStr不会为null
//                    LogUtils.e(TAG, "==========原数===========>" + Double.parseDouble(numberStr));
                    if (numberStr.contains(".") || numberStr.contains("e") || numberStr.contains("E")) {
//                        LogUtils.e(TAG, "=========转换后============>" + Double.parseDouble(numberStr));
                        return Double.parseDouble(numberStr);
                    }
                    long longValue = Long.parseLong(numberStr);
                    if (longValue <= Integer.MAX_VALUE && longValue >= Integer.MIN_VALUE) {
                        return Integer.parseInt(numberStr);
                    } else {
                        return Long.parseLong(numberStr);
                    }
                } catch (Exception e) {
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
    public void write(JsonWriter out, HttpResponseEntity value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.beginObject();
        out.name("info");
        gson.getAdapter(Object.class).write(out, value.getInfo());
        out.name("data");
        gson.getAdapter(Object.class).write(out, value.getData());
        out.name("code");
        gson.getAdapter(String.class).write(out, value.getCode());
        out.endObject();
    }
}
