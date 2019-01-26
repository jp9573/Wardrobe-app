package in.co.jaypatel.wardrobe;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

import static android.app.Activity.RESULT_OK;

public class TopClothFragment extends Fragment implements Observer {

    View rootView;
    ImageButton topImageButton;
    Dress dress;
    int slideNo;
    private static final int PICK_IMAGE = 511;
    private static final int CAPTURE_IMAGE = 512;

    public TopClothFragment() {
        // Required empty public constructor
    }

    public static TopClothFragment newInstance(int sectionNumber) {
        TopClothFragment fragment = new TopClothFragment();
        Bundle args = new Bundle();
        args.putInt("slide_number", sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_top_cloth, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dress = new Dress();
        topImageButton = rootView.findViewById(R.id.imageButtonTop);
        slideNo = getArguments().getInt("slide_number");

        if(MainActivity.topDressMap.containsKey(slideNo)) {
            loadData();
        }

        if(slideNo != 1 && !MainActivity.topDressMap.containsKey(slideNo)) {
            selectImage();
        }

        topImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    public void setImage(String dress) {
        File file = new File(dress);
        if(topImageButton != null)
            Picasso.get().load(file).placeholder(R.drawable.top_cloth).into(topImageButton);
    }

    void selectImage() {
        ChooseImageDialog dialog = new ChooseImageDialog(getActivity(), TopClothFragment.this);
        dialog.show();
    }

    void loadData() {
        dress = MainActivity.topDressMap.get(slideNo);
        String top = dress.getCloth();

        if (top != null) {
            File file = new File(top);
            Picasso.get().load(file).placeholder(R.drawable.top_cloth).into(topImageButton);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PICK_IMAGE: //when gallery image selected
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();

                    if(selectedImage.toString().startsWith("content://com.google.android.apps")) {
                        Toast.makeText(getContext(), "Please select image from local storage!\nYou need to be premium user to use this functionality!",Toast.LENGTH_LONG).show();
                    }else {
                        String path = getPath(getContext(), selectedImage);
                        if (path != null) {

                            File file = new File(path);
                            Picasso.get().load(file).placeholder(R.drawable.top_cloth).into(topImageButton);
                            dress.setCloth(path);

                            updateDressMap();
                        }
                    }
                }
                break;
            case CAPTURE_IMAGE: // when 'take a photo' is pressed
                if (resultCode == RESULT_OK) {
                    String path = ChooseImageDialog.imageFilePath;
                    if (path != null) {
                        File file = new File(path);
                        Picasso.get().load(file).placeholder(R.drawable.top_cloth).into(topImageButton);
                        dress.setCloth(path);

                        updateDressMap();
                    }

                }
                break;
        }
    }

    void updateDressMap() {
        if(MainActivity.topDressMap.containsKey(slideNo)) {
            MainActivity.topDressMap.remove(slideNo);
            MainActivity.topDressMap.put(slideNo, dress);
        }else {
            MainActivity.topDressMap.put(slideNo, dress);
        }
    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    @Override
    public void update(Observable o, Object arg) {
        loadData();
    }
}
