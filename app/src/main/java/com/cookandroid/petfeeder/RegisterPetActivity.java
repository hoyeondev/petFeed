package com.cookandroid.petfeeder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.Bitmap;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class RegisterPetActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private TextView tvSelectedPet, tvAutoFeed;
    private EditText etPetName, etPetAge, etPetWeight;
    private ImageView imgPetPhoto;
    private Button btnSelectPhoto, btnRegisterPet;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pet_register);

        // 뷰 연결
        tvSelectedPet = findViewById(R.id.tvSelectedPet);
        tvAutoFeed = findViewById(R.id.tvAutoFeed);
        etPetName = findViewById(R.id.etPetName);
        etPetAge = findViewById(R.id.etPetAge);
        etPetWeight = findViewById(R.id.etPetWeight);
        imgPetPhoto = findViewById(R.id.imgPetPhoto);
        btnSelectPhoto = findViewById(R.id.btnSelectPhoto);
        btnRegisterPet = findViewById(R.id.btnRegisterPet);

        // 뒤로가기 버튼
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish(); // 현재 Activity 종료 → 이전 Activity로 돌아감
        });

        // MainActivity에서 전달된 값 받기
        Intent intent = getIntent();
        String selectedPet = intent.getStringExtra("selectedPet");
        Log.d("PetRegister", "선택된 반려동물: " + selectedPet);
        String autoFeed = intent.getStringExtra("autoFeed");

        tvSelectedPet.setText(selectedPet);
        tvAutoFeed.setText(autoFeed);

        // 사진 선택 버튼
        //btnSelectPhoto.setOnClickListener(v -> openGallery());
        btnSelectPhoto.setOnClickListener(v -> {
            // drawable 리소스 배열
            final int[] images = {R.drawable.dog1, R.drawable.cat1, R.drawable.fox, R.drawable.horse};

            String[] items = {"강아지", "고양이", "여우", "말"};

            new AlertDialog.Builder(RegisterPetActivity.this)
                    .setTitle("사진 선택")
                    .setItems(items, (dialog, which) -> {
                        // 선택한 이미지 ImageView에 설정
                        imgPetPhoto.setImageResource(images[which]);
                    })
                    .show();
        });

        // 등록 완료 버튼
        btnRegisterPet.setOnClickListener(v -> savePetData());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imgPetPhoto.setImageURI(selectedImageUri);
        }
    }

    private void savePetData() {
        try {
            String petName = etPetName.getText().toString();
            String petAge = etPetAge.getText().toString();
            String petWeight = etPetWeight.getText().toString();


            // ImageView에서 Bitmap 가져오기
            imgPetPhoto.setDrawingCacheEnabled(true);
            Bitmap bitmap = ((BitmapDrawable) imgPetPhoto.getDrawable()).getBitmap();
            imgPetPhoto.setDrawingCacheEnabled(false);

            // 내부 저장소에 저장
            File file = new File(getFilesDir(), petName + ".png");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "이미지 저장 실패", Toast.LENGTH_SHORT).show();
                return;
            }

            // Toast 메시지
            Toast.makeText(this, "등록이 완료되었습니다!", Toast.LENGTH_SHORT).show();

            // 등록 후 Activity 종료
            finish();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "저장 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
