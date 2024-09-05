package ru.tusur.ShaurmaWebSiteProject.backend.model;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Setter
@Getter
public class AvatarImage {

    private byte[] image;
    private String name;
    private String mime;

    public void saveAsStaticResource(){
        File newFile = new File("/images/**" + name);

        try {
            if(!newFile.exists()){
                newFile.createNewFile();
            }

            new FileOutputStream(newFile).write(image);

        } catch (IOException e){
            e.printStackTrace();
        }

    }

}

