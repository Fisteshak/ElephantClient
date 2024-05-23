package com.elephant.client.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Folder implements FsObject {

    private Integer id;

    private String name;

    private Folder parent;

    @Override
    public int getType() {
        return FsObject.TYPE_FOLDER;
    }

    @Override
    public String toString() {
        return "Folder{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parent=" + parent +
                '}';
    }

    public Folder(String name, Folder parent) {
        this.name = name;
        this.parent = parent;
    }
}