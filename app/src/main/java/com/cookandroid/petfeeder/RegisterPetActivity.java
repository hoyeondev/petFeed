package com.cookandroid.petfeeder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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
            // 입력값 검증
            String petName = etPetName.getText().toString().trim();
            String petAge = etPetAge.getText().toString().trim();
            String petWeight = etPetWeight.getText().toString().trim();

            if (petName.isEmpty()) {
                Toast.makeText(this, "반려동물 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (petAge.isEmpty()) {
                Toast.makeText(this, "나이를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (petWeight.isEmpty()) {
                Toast.makeText(this, "몸무게를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 이미지 선택 확인
            if (imgPetPhoto.getDrawable() == null) {
                Toast.makeText(this, "사진을 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 숫자 변환 (예외 처리 포함)
            int ageVal;
            double weightVal;

            try {
                ageVal = Integer.parseInt(petAge);
                if (ageVal <= 0) {
                    Toast.makeText(this, "올바른 나이를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "나이는 숫자로 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                weightVal = Double.parseDouble(petWeight);
                if (weightVal <= 0) {
                    Toast.makeText(this, "올바른 몸무게를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "몸무게는 숫자로 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 이미지 저장
            Bitmap bitmap = ((BitmapDrawable) imgPetPhoto.getDrawable()).getBitmap();
            File file = new File(getFilesDir(), petName + ".png");

            try (FileOutputStream fos = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();

                String photoPath = file.getAbsolutePath();
                String selectedPetVal = tvSelectedPet.getText().toString();
                String autoFeedVal = tvAutoFeed.getText().toString();

                // DB에 INSERT (Toast는 여기서만 처리)
                boolean success = savePetToDB(selectedPetVal, autoFeedVal, petName, ageVal, weightVal, photoPath);

                if (success) {
                    Toast.makeText(this, "등록이 완료되었습니다!", Toast.LENGTH_SHORT).show();
                    finish(); // 성공 시에만 Activity 종료
                }

            } catch (IOException e) {
                Log.e("PetRegister", "이미지 저장 실패", e);
                Toast.makeText(this, "이미지 저장 실패", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e("PetRegister", "저장 중 오류 발생", e);
            Toast.makeText(this, "저장 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // DB 저장 메서드도 수정 (boolean 반환)
    private boolean savePetToDB(String selectedPet, String autoFeed, String petName,
                                int petAge, double petWeight, String photoPath) {

        myDBHelper dbHelper = null;
        SQLiteDatabase db = null;

        try {
            dbHelper = new myDBHelper(this);
            db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("selected_pet", selectedPet);
            values.put("auto_feed", autoFeed);
            values.put("pet_name", petName);
            values.put("pet_age", petAge);
            values.put("pet_weight", petWeight);
            values.put("pet_photo", photoPath);

            long result = db.insert("pet_register", null, values);

            if (result == -1) {
                Toast.makeText(this, "데이터 저장 실패", Toast.LENGTH_SHORT).show();
                return false;
            }

            return true;

        } catch (Exception e) {
            Log.e("PetRegister", "DB 저장 오류", e);
            Toast.makeText(this, "데이터베이스 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    public class myDBHelper extends SQLiteOpenHelper {
        public myDBHelper(Context context) {
            super(context, "pet_register", null, 1);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE pet_register (\n" +
                    " id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    " selected_pet TEXT NOT NULL,\n" +
                    " auto_feed TEXT NOT NULL,\n" +
                    " pet_name TEXT NOT NULL,\n" +
                    " pet_age INTEGER NOT NULL,\n"+
                    " pet_weight REAL NOT NULL,\n" +
                    " pet_photo TEXT,\n" +
                    " created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n" +
                    ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            db.execSQL("DROP TABLE IF EXISTS pet_register");
            onCreate(db);
        }
    }


}
