package com.prometteur.sathiya.model;

public abstract class ListObject {
        public static final int TYPE_DATE = 0;
        public static final int TYPE_GENERAL = 1;
        public static final int VIEW_TYPE_LOADING = 2;

        abstract public int getType();
    }