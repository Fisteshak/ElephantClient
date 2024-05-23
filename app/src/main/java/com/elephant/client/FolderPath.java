package com.elephant.client;

import com.elephant.client.models.Folder;

import java.util.Stack;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FolderPath {
    private Stack<Folder> path = new Stack<>();

    private Folder rootFolder = new Folder(1, "root", null);

    public FolderPath() {
        path.add(rootFolder);
    }

    public void addFolder(Folder folder) {
        for (Folder f : path) {
            if (f.getId().equals(folder.getId())) {
                //erase all folders after this folder
                while (path.peek() != f) {
                    path.pop();
                }
                return;
            }

        }
        path.add(folder);
    }

    public Folder getTopFolder() {
        return path.peek();
    }

    public void deleteFolder() {
        if (path.size() > 1)
            path.pop();
    }

    public void getSize() {
        path.size();
    }

    public String getFullPath() {
        StringBuilder fullPath = new StringBuilder();
        for (Folder folder : path) {
            fullPath.append(folder.getName());
            fullPath.append("/");
        }
        return fullPath.toString();
    }

}
