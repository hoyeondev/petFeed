package com.cookandroid.petfeeder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // 변수 선언
    TextView text1, text2;
    CheckBox ChkAM, ChkPM;
    RadioGroup rGroup1;
    RadioButton rdoDog, rdoCat, rdoFox, rdoHorse;
    Button btnOK;
    ImageView imgPet;

    private String selectedPet;
    private String autoFeed;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("반려동물 자동 급식");
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


        rGroup1 = (RadioGroup) findViewById(R.id.Rgroup1);

        imgPet = (ImageView) findViewById(R.id.ImgPet);

        imgPet.setImageResource(R.drawable.dog3);

        // 라디오 버튼 체크 이벤트
        RadioGroup rgPets = findViewById(R.id.Rgroup1);

        rgPets.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.RdoDog) {
                    selectedPet = "강아지";
                    imgPet.setImageDrawable(null);
                    imgPet.setImageResource(R.drawable.dog3);
                } else if (checkedId == R.id.RdoCat) {
                    selectedPet = "고양이";
                    imgPet.setImageResource(R.drawable.cat1);
                } else if (checkedId == R.id.RdoFox) {
                    selectedPet = "여우";
                    imgPet.setImageResource(R.drawable.fox);
                } else if (checkedId == R.id.RdoHorse) {
                    selectedPet = "말";
                    imgPet.setImageResource(R.drawable.horse);
                } else {
                    // 선택 해제된 경우 등
                    selectedPet = null;
                    imgPet.setImageDrawable(null);
                }
            }
        });

        // 자동급식 설정

        ChkAM = findViewById(R.id.ChkAM);
        ChkPM = findViewById(R.id.ChkPM);


        // 반려동물 등록 버튼 이벤트
        Button btnRegister = findViewById(R.id.BtnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String autoFeed = "";

                if (ChkAM.isChecked()) autoFeed = "자동 급식 : 아침";
                if (ChkPM.isChecked()) autoFeed = "자동 급식 : 저녁";

                Intent intent = new Intent(MainActivity.this, RegisterPetActivity.class);
                intent.putExtra("selectedPet", selectedPet);
                intent.putExtra("autoFeed", autoFeed);

                startActivity(intent);
            }
        });







    }
}