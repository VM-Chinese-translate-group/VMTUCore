package top.vmctcn.vmtu.core.pack;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PackSupportedFormatsAdapter extends TypeAdapter<Object> {
    @Override
    public void write(JsonWriter out, Object value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        if (value instanceof Number) {
            out.value(((Number) value).intValue());
        } else if (value instanceof int[]) {
            out.beginArray();
            for (int item : (int[]) value) {
                out.value(item);
            }
            out.endArray();
        } else if (value instanceof List) {
            out.beginArray();
            Gson gson = new Gson();
            for (Object item : (List<?>) value) {
                gson.toJson(item, Object.class, out);
            }
            out.endArray();
        } else {
            // 对于其他类型，使用默认序列化
            new Gson().toJson(value, Object.class, out);
        }
    }

    @Override
    public Object read(JsonReader in) throws IOException {
        JsonToken token = in.peek();

        switch (token) {
            case NUMBER:
                return in.nextInt();
            case BEGIN_ARRAY:
                in.beginArray();
                List<Object> list = new ArrayList<>();
                while (in.hasNext()) {
                    JsonToken arrayToken = in.peek();
                    switch (arrayToken) {
                        case NUMBER:
                            list.add(in.nextInt());
                            break;
                        case BEGIN_OBJECT:
                        case BEGIN_ARRAY:
                            list.add(new Gson().fromJson(in, Object.class));
                            break;
                        default:
                            // 对于其他类型，跳过
                            in.skipValue();
                            break;
                    }
                }
                in.endArray();
                return list;
            case BEGIN_OBJECT:
                return new Gson().fromJson(in, Object.class);
            case NULL:
                in.nextNull();
                return null;
            default:
                // 对于其他类型，使用默认反序列化
                return new Gson().fromJson(in, Object.class);
        }
    }
}