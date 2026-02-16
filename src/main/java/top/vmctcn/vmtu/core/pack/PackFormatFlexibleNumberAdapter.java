package top.vmctcn.vmtu.core.pack;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PackFormatFlexibleNumberAdapter extends TypeAdapter<Object> {
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
                List<Integer> list = new ArrayList<>();
                while (in.hasNext()) {
                    if (in.peek() == JsonToken.NUMBER) {
                        list.add(in.nextInt());
                    } else {
                        // 对于非数字类型，跳过
                        in.skipValue();
                    }
                }
                in.endArray();
                // 将 List 转换为 int[]
                int[] result = new int[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    result[i] = list.get(i);
                }
                return result;
            case NULL:
                in.nextNull();
                return null;
            default:
                throw new JsonParseException("Expected integer or array of integers");
        }
    }
}