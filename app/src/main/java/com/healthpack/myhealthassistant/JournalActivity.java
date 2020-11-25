package com.healthpack.myhealthassistant;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class JournalActivity extends AppCompatActivity {

    EditText etActivity;
    TextView tvResults;
    Button btnSave, btnSearch;
    DatePicker dpSearchDate;
    Journal journal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        etActivity = findViewById(R.id.etActivity);
        tvResults = findViewById(R.id.tvResult);
        dpSearchDate = findViewById(R.id.dpSearchDate);
        btnSave = findViewById(R.id.btnSaveActivity);
        btnSearch = findViewById(R.id.btnSearch);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SaveActivity();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SearchActivity();
            }
        });
    }

    public void SaveActivity(){

        Journal journal = new Journal();

        journal.setNote(etActivity.getText().toString());
        journal.setDate(Singleton.getInstance().getDataManager().getToday());

        Singleton.getInstance().getDataManager().pushJournal(journal);
    }

    public void SearchActivity(){ //find the activity journal entry for the date the user has specified

        String searchDate;

        int day  = dpSearchDate.getDayOfMonth();
        int month= dpSearchDate.getMonth();
        int year = dpSearchDate.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        searchDate = sdf.format(calendar.getTime());

        Journal journal = Singleton.getInstance().getDataManager().pullJournal(searchDate);

        if(journal != null) //an entry was found
        {
            tvResults.setText(journal.getNote());
        }
        else //no entry was found
        {
            tvResults.setText("There is no journal entry for " + searchDate);
        }
    }
}