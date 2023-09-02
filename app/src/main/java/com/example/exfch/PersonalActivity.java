package com.example.exfch;

import static androidx.constraintlayout.widget.StateSet.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Locale;

public class PersonalActivity extends AppCompatActivity {
    private static final int GALLERY_IMAGE_REQUEST_CODE = 1;
    Classifier cls;

    CheckInf checkInf = new CheckInf();

    ImageButton upload;
    private ImageView image;
    TextView check;

    private final DatabaseReference root = FirebaseDatabase.getInstance().getReference("image");
    private final StorageReference reference = FirebaseStorage.getInstance().getReference();

    private FirebaseDatabase database = FirebaseDatabase.getInstance(); //파이어베이스 데이터베이스 연동
    DatabaseReference databaseReference = database.getReference();
    DatabaseReference conditionRef = databaseReference.child("User");
    DatabaseReference usercount = databaseReference.child("User Count");

    private Uri imageUri;
    Uri selectedImage;
    ActivityResult result = null;

    int num;
    String user = "User";
    //String result = null;

    public static final int PERSONAL_IMAGE_REQUEST_CODE = 1;

    private static final int FROM_ALBUM = 1;    // onActivityResult 식별자
    private static final int FROM_CAMERA = 2;   // 카메라는 사용 안함

    private static final int INPUT_SIZE = 128; // 입력 이미지 크기
    private static final int NUM_CLASSES = 3; // 클래스 수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        upload = (ImageButton)findViewById(R.id.uploadimage);
        check = (TextView)findViewById(R.id.textView7);
        image = (ImageView)findViewById(R.id.imageView5);

        Intent intent2 = getIntent();
        num = intent2.getIntExtra("사용자번호", 0);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        upload.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

           }
        });

        cls = new Classifier(this);
        try {
            cls.init();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(intent, GALLERY_IMAGE_REQUEST_CODE);
            }
        });
    }

    private void getImageFromGallery(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
        startActivityForResult(intent, GALLERY_IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //uploadToFirebase(imageUri, num);
        if (resultCode == Activity.RESULT_OK && requestCode == GALLERY_IMAGE_REQUEST_CODE) {
            if (data == null) { return; }

            selectedImage = data.getData();
            activityResult.launch(new Intent(Intent.ACTION_VIEW, selectedImage));

            Bitmap bitmap = null;
            uploadToFirebase(selectedImage, num);

            try {
                if(Build.VERSION.SDK_INT >= 29) {
                    ImageDecoder.Source src = ImageDecoder.createSource(getContentResolver(), selectedImage);
                    bitmap = ImageDecoder.decodeBitmap(src);
                } else {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                }
            } catch (IOException ioe) {
                Log.e(TAG, "Failed to read Image", ioe);
            }

            if(bitmap != null) {
                Pair<String, Float> output = cls.classify(bitmap);
                String resultStr = String.format(Locale.ENGLISH,
                        "Color : %s" + "\n" + "정확도 : %.2f%%",
                        output.first, output.second * 100);

                String color = null;
                if(resultStr.contains("spring")) {
                    color = "spring";
                    checkInf.setUserColor(color);
                } else if (resultStr.contains("summer")) {
                    color = "summer";
                    checkInf.setUserColor(color);
                } else if (resultStr.contains("fall")) {
                    color = "fall";
                    checkInf.setUserColor(color);
                } else if (resultStr.contains("winter")) {
                    color = "winter";
                    checkInf.setUserColor(color);
                }

                if(num >= 1) {
                    conditionRef.child(user+num).child("userColor").setValue(resultStr);
                }

                ArrayList<String> result = new ArrayList<>();
                result.add(resultStr);
                result.add(Integer.toString(num));

                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                intent.putExtra("사용자정보", result);
                startActivity(intent);
                finish();
            }

        }
    }

    //사진 가져오기 -> imageview에 출력
    ActivityResultLauncher<Intent> activityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK && result.getData() != null){
                        imageUri = result.getData().getData();
                        image.setImageURI(imageUri);
                    }
                }
            }
    );

    //firebase image upload
    private void uploadToFirebase(Uri uri, int num) {
        StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //이미지 모델에 담기
                        Model model = new Model(uri.toString());

                        //키로 아이디 생성
                        String modelid = root.push().getKey();


                        Toast.makeText(getApplicationContext(), "업로드 성공", Toast.LENGTH_SHORT).show();
                        image.setImageResource(R.drawable.imageup);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "업로드 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //파일타입 가져오기
    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    //뒤로가기
    @Override
    public  void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        cls.finish();
        super.onDestroy();
    }
}



















