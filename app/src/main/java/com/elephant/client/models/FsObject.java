package com.elephant.client.models;

public interface FsObject {
    int TYPE_FILE = 101;
    int TYPE_FOLDER = 102;

    int getType();
}
