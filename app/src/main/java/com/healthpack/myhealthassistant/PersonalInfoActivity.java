package com.healthpack.myhealthassistant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PersonalInfoActivity extends AppCompatActivity {

    Button btnSavePersonalInfo;
    EditText etName, etHeight;
    DatePicker dpBirthday;
    RadioGroup rgGender;
    RadioButton rbtnMale, rbtnFemale;
    TextView tvHeight;

    DataManager dataManager = new DataManager();

    PersonalInfo personalInfo = new PersonalInfo();
    Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        btnSavePersonalInfo = findViewById(R.id.btnSavePersonalInfo);
        tvHeight = findViewById(R.id.tvHeight);
        etName = findViewById(R.id.etName);
        etHeight = findViewById(R.id.etHeight);
        dpBirthday = findViewById(R.id.dpBirthday);
        rgGender = findViewById(R.id.rgGender);
        rbtnMale = findViewById(R.id.rbtnMale);
        rbtnFemale = findViewById(R.id.rbtnFemale);

        //Pull existing data from firebase
        personalInfo = Singleton.getInstance().getDataManager().getPersonalInfo();
        settings = Singleton.getInstance().getDataManager().pullSettings();

        if(settings.isMetric())
        {
            tvHeight.setText("Height (m)");
        }
        else
        {
            tvHeight.setText("Height (in)");
        }

        if(personalInfo != null) {

            double heightValue = 0.0;

            if(settings.isMetric()) {
                heightValue = personalInfo.getHeight();
            }else{
                heightValue = Singleton.getInstance().conversionManager.MetresToInches(personalInfo.getHeight());
            }

            etName.setText(personalInfo.getName());
            etHeight.setText(heightValue + "");

            String []split = personalInfo.getBirthday().split("-");
            dpBirthday.init(Integer.parseInt(split[2]), Integer.parseInt(split[1]), Integer.parseInt(split[0]), null);

            if (personalInfo.isMale()) { //set the user's gender
                rbtnMale.setChecked(true);
            } else {
                rbtnFemale.setChecked(true);
            }
        }

        //Push data
        btnSavePersonalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = etName.getText().toString().trim();
                double height = Double.parseDouble(etHeight.getText().toString().trim());
                String birthday = "";
                boolean isMale, flag = false;

                /*
                 * CODE ATTRIBUTION
                 * SOURCE: https://stackoverflow.com/questions/14851285/how-to-get-datepicker-value-in-date-format
                 * AUTHOR: zdesam & abhishek (Stackoverflow usernames)
                 * DESCRIPTION: Get day, month and year from the date picker which is
                 *              in spinner mode and convert it into type calendar so that
                 *              it can be parsed into simple date format
                 */

                int   day  = dpBirthday.getDayOfMonth();
                int   month= dpBirthday.getMonth() - 1;
                int   year = dpBirthday.getYear();
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                birthday = sdf.format(calendar.getTime());

                //END CODE ATTRIBUTION

                //check that the user has filled in all necessary values
                if(!(name.isEmpty() || height <= 0.0 || birthday.isEmpty())) {

                    /*
                    Check if the user has entered their height in inches, if they have,
                    then convert it to metres to be stored in Firebase*/
                    if(!settings.isMetric())
                    {
                        height = Singleton.getInstance().getConversionManager().InchesToMetres(height);
                    }

                    personalInfo = new PersonalInfo();

                    /*personalInfo.setName(etName.getText().toString().trim());
                    personalInfo.setHeight(Double.parseDouble(etHeight.getText().toString().trim()));*/
                    personalInfo.setName(name);
                    personalInfo.setHeight(height);
                    personalInfo.setBirthday(birthday);

                    if (rbtnMale.isChecked()) {
                        personalInfo.setMale(true);
                        flag = true;
                    } else {
                        if (rbtnFemale.isChecked()) {
                            personalInfo.setMale(false);
                            flag = true;
                        } else {
                            flag = false;
                            Toast.makeText(PersonalInfoActivity.this, "You need to select a gender", Toast.LENGTH_SHORT).show();
                        }
                    }

                    if ((name != null) && (height > 0.0) && (birthday != null) && (flag == true))
                    {
                        dataManager.pushPersonalInfo(personalInfo);
                        Toast.makeText(PersonalInfoActivity.this, "Your personal info has been updated", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(PersonalInfoActivity.this, "Check that you filled in all the fields", Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    Toast.makeText(PersonalInfoActivity.this, "Check that you filled in all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}