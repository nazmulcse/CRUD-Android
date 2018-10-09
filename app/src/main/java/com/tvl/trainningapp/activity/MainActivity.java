package com.tvl.trainningapp.activity;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.tvl.trainningapp.R;
import com.tvl.trainningapp.adapter.NoteListAdapter;
import com.tvl.trainningapp.datasource.DBHelperLocal;
import com.tvl.trainningapp.model.Note;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    DBHelperLocal dbhelper;

    ArrayList<Note> dataLists;

    NoteListAdapter adapter;

    RecyclerView listView;

    Button btnAddNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbhelper = new DBHelperLocal(this);

        dataLists = new ArrayList<>();
        listView = (RecyclerView) findViewById(R.id.listView);

        btnAddNew = (Button) findViewById(R.id.btnAddNew) ;

        Cursor c = dbhelper.getDataFromLocal();

        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {

                    Note item = new Note();
                    item.setId(c.getInt(0));
                    item.setNote(c.getString(1));
                    item.setName(c.getString(2));
                    item.setPhone(c.getString(3));
                    item.setEmail(c.getString(4));
                    item.setAddress(c.getString(5));
                    item.setDivision_id(c.getString(7));
                    item.setIsActive(c.getString(9));

                    dataLists.add(item);

                } while (c.moveToNext());
            }

            adapter = new NoteListAdapter(dataLists, this);

            listView.setHasFixedSize(true);
            LinearLayoutManager MyLayoutManager = new LinearLayoutManager(this);
            MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

            listView.setAdapter(adapter);

            listView.setLayoutManager(MyLayoutManager);
        }

        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),EntryActivity.class);
                startActivity(intent);
            }
        });


    }
}
