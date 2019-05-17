package com.example.cristofer.fileexplorerexample;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cristofer.fileexplorerexample.Utillities.FileUtillities;

import java.io.File;
import java.util.ArrayList;

import ir.mahdi.mzip.rar.RarArchive;
import ir.mahdi.mzip.zip.ZipArchive;
import uk.co.senab.photoview.PhotoViewAttacher;

public class MainActivity extends AppCompatActivity {


    private File imageFiles[];
    private Toolbar toolbar;
    private ImageView imageFile;
    private Button openDialog;
    private Button buttonUp;
    private TextView textPage;
    private TextView finalPage;
    private TextView textFolder;
    private ListView dialogListView;
    private FileUtillities fileUtillities;
    private PhotoViewAttacher photoViewAttacher;

    private ArrayList<String> nameOfFolders;

    private int actualPage = 2;
    private int endPage = 0;

    public static final int REQUEST_CODE = 123;
    private static final int SWIPE_THRESHOLD = 100;
    private static final int CUSTOM_DIALOG_ID = 0;
    private static final int CUSTOM_DIALOG_ID2 = 1;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
    }


    @Override
    protected void onDestroy() {
        fileUtillities.deleteTmpFolder();
        super.onDestroy();
    }

    private void checkPermissions() {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission_group.STORAGE);
        switch (permissionCheck) {
            case PackageManager.PERMISSION_GRANTED:
                showDialog(CUSTOM_DIALOG_ID);
                break;
            case PackageManager.PERMISSION_DENIED:
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission_group.STORAGE},
                        REQUEST_CODE);
                break;
        }
    }

    private void initComponents() {

        toolbar = (Toolbar) findViewById(R.id.toolbarVisor);
        textPage = (EditText) findViewById(R.id.editTextPage);
        finalPage = (TextView) findViewById(R.id.textViewPage);
        imageFile = (ImageView) findViewById(R.id.imageView1);

        photoViewAttacher = new PhotoViewAttacher(imageFile);
        photoViewAttacher.setOnSingleFlingListener(createListenerForFling());
        photoViewAttacher.setScaleType(ImageView.ScaleType.FIT_XY);

        openDialog = (Button) findViewById(R.id.opener);
        openDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    //checkPermissions();
                    showDialog(CUSTOM_DIALOG_ID);
                else
                    showDialog(CUSTOM_DIALOG_ID);
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle("");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fileUtillities = new FileUtillities(this, true);
        fileUtillities.initFolders();
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        switch (id) {
            case CUSTOM_DIALOG_ID:
                dialogListView.setAdapter(fileUtillities.ListDir(
                        fileUtillities.getCurFolder(),
                        buttonUp,
                        textFolder
                        )
                );
                break;
            case CUSTOM_DIALOG_ID2:
                dialogListView.setAdapter(
                        new AdapterDialogVol(
                                this,
                                nameOfFolders)
                );
                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
            case CUSTOM_DIALOG_ID:
                dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.file_explorer_dialog);
                dialog.setTitle("Custom Dialog");
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                textFolder = (TextView) dialog.findViewById(R.id.folder);
                buttonUp = (Button) dialog.findViewById(R.id.up);
                buttonUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogListView.setAdapter(fileUtillities.ListDir(
                                fileUtillities.getCurFolder().getParentFile(),
                                buttonUp,
                                textFolder
                                )
                        );
                    }
                });

                dialogListView = (ListView) dialog.findViewById(R.id.folderList);
                dialogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        File selected = new File((fileUtillities.getFileList()).get(position));
                        if (selected.isDirectory()) {
                            dialogListView.setAdapter(fileUtillities.ListDir(
                                    selected,
                                    buttonUp,
                                    textFolder
                                    )
                            );
                        } else {
                            if (selected.toString().contains(".zip") || selected.toString().contains(".cbz"))
                                unPackFile(selected.toString(), 1);
                            else if (selected.toString().contains(".rar") || selected.toString().contains(".cbr"))
                                unPackFile(selected.toString(), 2);
                            dismissDialog(CUSTOM_DIALOG_ID);
                        }
                    }
                });
                break;
            case CUSTOM_DIALOG_ID2:
                dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.comic_explorer_dialog);
                dialog.setTitle("¿Que volumen quieres?");
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);

                dialogListView = (ListView) dialog.findViewById(R.id.listVol);
                dialogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        File selected = new File(fileUtillities.getCurFolder().listFiles()[position].getAbsolutePath());
                        fileUtillities.setCurFolder(selected);
                        if (selected.isDirectory()) {
                            getFileImages();
                        }
                    }
                });
                break;
        }
        return dialog;
    }

    private void unPackFile(String file, int opc) {
        fileUtillities.createTmpFolder();
        switch (opc) {
            case 1:
                ZipArchive zipFile = new ZipArchive();
                zipFile.unzip(
                        file,
                        fileUtillities.getCurFolder().getPath(),
                        ""
                );
                break;
            case 2:
                Toast.makeText(this, file, Toast.LENGTH_LONG).show();
                RarArchive rarFile = new RarArchive();
                rarFile.extractArchive(
                        file,
                        fileUtillities.getCurFolder().getPath()
                );
                break;
        }
        getFileImages();
    }

    private void getFileImages() {
        openDialog.setVisibility(View.GONE);
        imageFiles = fileUtillities.getCurFolder().listFiles();
       if (fileUtillities.verifyFiles(imageFiles)) {
            endPage = imageFiles.length;
            finalPage.setText(String.valueOf(endPage));
            setPage();
            disableZoom();
            setImage();
            ableZoom();
        } else {
            nameOfFolders = fileUtillities.getNameOfFolders(imageFiles);
            showDialog(CUSTOM_DIALOG_ID2);
        }
    }

    private void setImage() {
        Bitmap myBitmap = BitmapFactory.decodeFile(
                imageFiles[actualPage].getAbsolutePath()
        );
        imageFile.setImageBitmap(myBitmap);
    }

    private void setPage() {
        String page = " ";
        textPage.setText("");
        page = String.valueOf(actualPage);
        textPage.setText(page);

    }

    private PhotoViewAttacher.OnSingleFlingListener createListenerForFling() {
        PhotoViewAttacher.OnSingleFlingListener listener = new PhotoViewAttacher.OnSingleFlingListener() {
            @Override
            public boolean onFling(MotionEvent beginTouch, MotionEvent endTouch, float v, float v1) {
                boolean result = false;
                float diffX = endTouch.getX() - beginTouch.getX();
                float diffY = endTouch.getY() - beginTouch.getY();

                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(diffX) > SWIPE_THRESHOLD_VELOCITY) {
                        if (diffX > 0)
                            swipeRight();
                        else
                            swipeLeft();
                        result = true;
                    }
                } else {
                    if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(diffY) > SWIPE_THRESHOLD_VELOCITY) {
                        /*if (diffY > 0)
                            swipeUp();
                        else
                            swipeDown();
                        result = true;
                        */
                        result = true;
                    }
                }
                return result;
            }
        };
        return listener;
    }

    private void swipeLeft() {
        if (actualPage > 1) {
            actualPage--;
            setPage();
            disableZoom();
            setImage();
            ableZoom();
        }
    }

    private void swipeRight() {
        if (actualPage < endPage) {
            actualPage++;
            setPage();
            disableZoom();
            setImage();
            ableZoom();
        }
    }

    private void disableZoom() {
        photoViewAttacher.setZoomable(false);
    }

    private void ableZoom() {
        photoViewAttacher.setZoomable(true);
    }
}

