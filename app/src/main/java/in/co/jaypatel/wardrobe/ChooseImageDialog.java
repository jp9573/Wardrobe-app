package in.co.jaypatel.wardrobe;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jay on 14/04/18.
 */

public class ChooseImageDialog extends Dialog {

    private Activity mActivity;
    private Fragment mFragment;
    private Context mContext;
    public static String imageFilePath;
    private static int PICK_IMAGE = 511;
    private static int CAMERA_REQUEST = 512;

    public ChooseImageDialog(Context context, Fragment fragment) {
        super(context);
        mFragment = fragment;
        mActivity = fragment.getActivity();
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_image_dialog);

        Button mChooseGalleryImageButton = findViewById(R.id.button_choose_image_from_gallery);
        Button mTakeCameraImageButton = findViewById(R.id.button_take_camera_image);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
        }

        mChooseGalleryImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                mFragment.startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

                dismiss();
            }
        });


        mTakeCameraImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pictureIntent = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                if(pictureIntent.resolveActivity(getContext().getPackageManager()) != null){
                    //Create a file to store the image
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(getContext(),"in.co.jaypatel.wardrobe.ImageFileProvider", photoFile);
                        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        mFragment.startActivityForResult(pictureIntent,CAMERA_REQUEST);
                    }
                }
                dismiss();
            }
        });

    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

}
