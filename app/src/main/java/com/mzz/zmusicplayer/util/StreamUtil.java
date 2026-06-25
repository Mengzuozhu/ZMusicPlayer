package com.mzz.zmusicplayer.util;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The type Stream util.
 */
public class StreamUtil {

    /**
     * To list list.
     *
     * @param <T>    the type parameter
     * @param <R>    the type parameter
     * @param rows   the rows
     * @param mapper the mapper
     * @return the list
     */
    public static <T, R> List<R> toList(List<T> rows, Function<? super T, ? extends R> mapper) {
        return streamOrEmpty(rows).map(mapper).collect(Collectors.toList());
    }

    /**
     * Stream or empty stream.
     *
     * @param <T>        the type parameter
     * @param collection the collection
     * @return the stream
     */
    public static <T> Stream<T> streamOrEmpty(Collection<T> collection) {
        return collection == null ? Stream.empty() : collection.stream();
    }
}
