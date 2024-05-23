package com.elephant.client.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class File implements FsObject {
    private Integer id;
    private String name;


    @Override
    public int getType() {
        return FsObject.TYPE_FILE;
    }
}
