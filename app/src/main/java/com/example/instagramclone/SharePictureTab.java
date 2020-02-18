package com.example.instagramclone;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * A simple {@link Fragment} subclass.
// * Use the {@link SharePictureTab#newInstance} factory method to
// * create an instance of this fragment.
 */
public class SharePictureTab extends Fragment implements View.OnClickListener{

    private static final String TAG = "SharePictureTab";
    private ImageView imgShare;
    private EditText edtImageDescription;
    private Button btnShareImage;
    Bitmap receivedImageBitmap;

    public SharePictureTab() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_share_picture_tab, container, false);

        imgShare = view.findViewById(R.id.imgShare);
        edtImageDescription = view.findViewById(R.id.edtImageDescription);
        btnShareImage = view.findViewById(R.id.btnShareImage);

        imgShare.setOnClickListener(SharePictureTab.this);


        btnShareImage.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.imgShare:

                if (Build.VERSION.SDK_INT >= 23 &&
                        ActivityCompat.checkSelfPermission(getContext(),              //ActivityCompat coz we are in fragment
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1000); //requestcode is unique to our fragment
                } else {

                    getChosenImage();
                }

                break;

            case R.id.btnShareImage:
                    if (receivedImageBitmap != null){
                        if (edtImageDescription.getText().toString().equals("")){
                            FancyToast.makeText(getContext(),
                                    " Error: you must specify a description ",
                                    FancyToast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                        } else {
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); //have to convert image to array of bytes
                            receivedImageBitmap.compress(Bitmap.CompressFormat.PNG, 100,byteArrayOutputStream);
                            byte[] bytes = byteArrayOutputStream.toByteArray();
                            ParseFile parseFile = new ParseFile("pic.png",bytes);
                            ParseObject parseObject = new ParseObject("Photo");
                            parseObject.put("picture", parseFile);
                            parseObject.put("img_desc",edtImageDescription.getText().toString());
                            parseObject.put("username", ParseUser.getCurrentUser().getUsername());
                            final ProgressDialog progressDialog = new ProgressDialog(getContext());
                            progressDialog.setMessage("Loading");
                            progressDialog.show();

                            parseObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        FancyToast.makeText(getContext(),
                                                " Done!! ",
                                                FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
                                    } else {
                                        FancyToast.makeText(getContext(),
                                                " Unknown Error: ",
                                                FancyToast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                                    }
                                    progressDialog.dismiss();
                                }

                            });



                        }

                    } else {
                        FancyToast.makeText(getContext(),
                                " Error: you must select an image ", FancyToast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                    }

                break;
        }

    }

    private void getChosenImage() {

        FancyToast.makeText(getContext(),
                " Now we can access the images", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();

        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, 2000); //unique req code for accessing the image
        }
    }

    //Pass request code 1000
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000 ){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){ //user has given the permission
                    getChosenImage();
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.d(TAG,"data : "+data;
        if (requestCode == 2000) {
            if (resultCode == Activity.RESULT_OK){

                //Do something with the captured image.
                try{
                    //Access image and get the image as bitmap
                    Uri selectedImage = data.getData();
                    Log.d(TAG,"selectedImage : "+selectedImage);
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getActivity().getContentResolver().query(selectedImage,filePathColumn,null,null,null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    Log.d(TAG,"picturePath : "+picturePath);
                    cursor.close();
                    BitmapFactory.Options options = new BitmapFactory.Options ();
                    options.inSampleSize = 4;

                    while (receivedImageBitmap == null) // makes screen blank coz of higher resolution image.
                        //returns null and throws file not found exception and as image size is big or  image that is not supported
//                   receivedImageBitmap = BitmapFactory.decodeFile(picturePath);
                    receivedImageBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImage);

                    //rotating the image to its actual position without the below code the imageView is rotated.
                    Matrix matrix = new Matrix();
                    Bitmap scaledBitmap;
                    if (receivedImageBitmap.getWidth() >= receivedImageBitmap.getHeight()){
                        matrix.setRectToRect(new RectF(0, 0, receivedImageBitmap.getWidth(), receivedImageBitmap.getHeight()), new RectF(0, 0, 400, 300), Matrix.ScaleToFit.CENTER);
                        scaledBitmap = Bitmap.createBitmap(receivedImageBitmap, 0, 0, receivedImageBitmap.getWidth(), receivedImageBitmap.getHeight(), matrix, true);
                    } else{
                        matrix.setRectToRect(new RectF(0, 0, receivedImageBitmap.getWidth(), receivedImageBitmap.getHeight()), new RectF(0, 0, 300, 400), Matrix.ScaleToFit.CENTER);
                        scaledBitmap = Bitmap.createBitmap(receivedImageBitmap, 0, 0, receivedImageBitmap.getWidth(), receivedImageBitmap.getHeight(), matrix, true);
                    }

                    File file = new File(getContext().getExternalCacheDir(), "image.jpg");
                    try {
                        FileOutputStream out = new FileOutputStream(file);
                        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();
                    } catch (Exception e) {
                        Log.e("Image", "Convert");
                    }



                    Log.d(TAG,"receivedImageBitmap : "+scaledBitmap);
                   imgShare.setImageBitmap(scaledBitmap);
//                    imgShare.setImageBitmap(receivedImageBitmap);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
