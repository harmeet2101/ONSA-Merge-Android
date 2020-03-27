package co.uk.depotnet.onsa.utils;

import androidx.annotation.NonNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ListOfJson<T> implements ParameterizedType {
        private Class<?> wrapped;


        public ListOfJson(Class<T> wrapper) {
            this.wrapped = wrapper;
        }

        @NonNull
        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{wrapped};
        }

        @NonNull
        @Override
        public Type getRawType() {
            return ArrayList.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }