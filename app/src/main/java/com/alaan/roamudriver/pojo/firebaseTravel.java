package com.alaan.roamudriver.pojo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class firebaseTravel {

    public String driver_id;
    public Map<String, String> Clients;
    public firebaseTravelCounters Counters;


    public firebaseTravel() {
        this.driver_id = "";
        this.Clients = new Map<String, String>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean containsKey(@Nullable Object o) {
                return false;
            }

            @Override
            public boolean containsValue(@Nullable Object o) {
                return false;
            }

            @Nullable
            @Override
            public String get(@Nullable Object o) {
                return null;
            }

            @Nullable
            @Override
            public String put(String s, String s2) {
                return null;
            }

            @Nullable
            @Override
            public String remove(@Nullable Object o) {
                return null;
            }

            @Override
            public void putAll(@NonNull Map<? extends String, ? extends String> map) {

            }

            @Override
            public void clear() {

            }

            @NonNull
            @Override
            public Set<String> keySet() {
                return null;
            }

            @NonNull
            @Override
            public Collection<String> values() {
                return null;
            }

            @NonNull
            @Override
            public Set<Entry<String, String>> entrySet() {
                return null;
            }
        };
        this.Counters = new firebaseTravelCounters();
    }

    public firebaseTravel(String driver_id, Map<String, String> Clients) {
        this.driver_id = driver_id;
        this.Clients = Clients;
    }
}
