package top.vmctcn.vmtu.core.util;

import java.util.ArrayList;
import java.util.stream.Stream;

public class ArrayUtil {
    public static <T> ArrayList<T> asArrayList(Stream<T> stream) {
        return stream.collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
}
