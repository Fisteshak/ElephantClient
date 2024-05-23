package com.elephant.client.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FolderStructure {
    Folder parentFolder;
    List<Folder> subfolders;
    List<File> files;


}
