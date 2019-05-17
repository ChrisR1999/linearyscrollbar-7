package com.example.cristofer.fileexplorerexample.Utillities;

import android.content.Context;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class FileUtillities {

    private File root;
    private File curFolder;
    private Context context;
    private ArrayList<String> fileList = new ArrayList<>();
    private final boolean DEBUG;

    public FileUtillities(Context context, boolean DEBUG) {
        this.context = context;
        this.DEBUG = DEBUG;
        setRoot();
    }

    public FileUtillities(boolean DEBUG) {
        this.DEBUG = DEBUG;
    }

    private void setRoot() {
        this.root = new File(
                Environment
                        .getExternalStorageDirectory()
                        .getAbsolutePath()
        );
        this.curFolder = root;
    }

    public void initFolders() {
        boolean success = false;
        File coverFolder = new File(root + File.separator + "allMight");
        if (!coverFolder.exists()) {
            success = coverFolder.mkdir();
            if (success)
                if (DEBUG)
                    Toast.makeText(context, "Directorio de portadas creado con exito.", Toast.LENGTH_SHORT).show();
                else if (DEBUG)
                    Toast.makeText(context, "Directorio de portadas no pudo ser creado.", Toast.LENGTH_SHORT).show();
        } else {
            if (DEBUG)
                Toast.makeText(context, "Directorio ya existente", Toast.LENGTH_SHORT).show();
        }
    }


    public void createTmpFolder() {
        File tmp = new File(root + File.separator + "tmpMight");
        boolean success;
        if (!tmp.exists()) {
            success = tmp.mkdir();
            if (success) {
                curFolder = tmp;
                if (DEBUG)
                    Toast.makeText(
                            context,
                            "Carpeta temporal creada con exito",
                            Toast.LENGTH_SHORT
                    ).show();
            } else {
                if (DEBUG)
                    Toast.makeText(
                            context,
                            "Error al crear carpeta temporal",
                            Toast.LENGTH_SHORT
                    ).show();
            }
        } else {
            curFolder = tmp;
        }
    }

    public void deleteTmpFolder() {
        File tmp = new File(root + File.separator + "tmpMight");
        File[] tmpFiles = tmp.listFiles();
        if (tmpFiles != null)
            for (File files : tmpFiles) {
                if (files.delete())
                    if (DEBUG)
                        Toast.makeText(
                                context,
                                "Archivo borrado con exito",
                                Toast.LENGTH_SHORT
                        ).show();
                    else if (DEBUG)
                        Toast.makeText(
                                context,
                                "Error al borrar archivo",
                                Toast.LENGTH_SHORT
                        ).show();
            }
        if (tmp.delete())
            if (DEBUG)
                Toast.makeText(
                        context,
                        "Exito al borrar el directorio",
                        Toast.LENGTH_SHORT
                ).show();
            else if (DEBUG)
                Toast.makeText(
                        context,
                        "Error al borrar el directorio",
                        Toast.LENGTH_SHORT
                ).show();
    }


    public ArrayAdapter<String> ListDir(File f, Button buttonUp, TextView textFolder) {
        ArrayAdapter<String> directoryList;

        if (f.equals(root)) {
            buttonUp.setEnabled(false);
        } else {
            buttonUp.setEnabled(true);
        }

        curFolder = f;
        textFolder.setText(f.getPath());

        File[] files = f.listFiles();
        fileList.clear();

        for (File file : files) {
            if (file.toString().contains(".zip") || file.toString().contains(".rar") ||
                    file.toString().contains(".cbz") || file.toString().contains(".cbr") ||
                    file.isDirectory())
                fileList.add(file.getPath());
        }

        directoryList = new ArrayAdapter<String>(
                context,
                android.R.layout.simple_expandable_list_item_1,
                fileList
        );

        return directoryList;
    }



    public boolean verifyFiles(File[] files) {
        for (File file : files) {
            if (file.isDirectory()) {
                curFolder = new File(file.getParent());
                return false;
            }
        }
        return true;
    }

    public ArrayList<String> getNameOfFolders(File[] files) {
        ArrayList<String> list = new ArrayList<String>();
        for (File file : files) {
            if (file.isDirectory()){
                list.add(file.getName());
            }
        }
        return list;
    }


    public File getCurFolder() {
        return curFolder;
    }
    public void setCurFolder(File file){this.curFolder = file;}

    public ArrayList<String> getFileList() {
        return fileList;
    }

}


