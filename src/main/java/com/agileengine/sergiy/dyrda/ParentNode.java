package com.agileengine.sergiy.dyrda;

/**
 * Created by Sergiy Dyrda on 22.06.2018
 */

class ParentNode {
    private String tag, clazz, id;

    ParentNode(String tag, String clazz, String id) {
        this.tag = tag;
        this.clazz = clazz;
        this.id = id;
    }

    String toStringDelimited(String clazzPointer, String idPointer) {
        StringBuilder builder = new StringBuilder(tag);
        if (!clazz.isEmpty()) {
            String[] classes = clazz.split(" ");
            for (String cc : classes) {
                builder.append(String.format("%s%s", clazzPointer, cc));
            }
        }
        if (!id.isEmpty()) builder.append(String.format("%s%s", idPointer, id));
        return builder.toString();
    }

    public String getTag() {
        return tag;
    }

    public String getClazz() {
        return clazz;
    }

    public String getId() {
        return id;
    }
}

