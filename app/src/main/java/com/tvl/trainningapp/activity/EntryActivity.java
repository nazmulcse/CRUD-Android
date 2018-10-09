package com.tvl.trainningapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tvl.trainningapp.R;
import com.tvl.trainningapp.datasource.DBHelperLocal;
import com.tvl.trainningapp.model.DivisionItem;

import java.sql.Timestamp;
import java.util.ArrayList;

public class EntryActivity extends AppCompatActivity {

    EditText txtNote, txtName, txtPhone, txtEmail, txtAdress;
    Spinner cboDivision;

    CheckBox chkIsActive;

    Button btnSave;

    ArrayList<DivisionItem> divisionList = new ArrayList<>();

    DBHelperLocal dbhelper;
    String divisionId = "";

    String formMode = "";
    String noteID = "";

    AlertDialog.Builder builder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            formMode = extras.getString("mode");
            noteID = String.valueOf(extras.getInt("noteId"));
        }

        dbhelper = new DBHelperLocal(this);
        builder = new AlertDialog.Builder(this);

        txtNote = (EditText) findViewById(R.id.txtNote);
        txtName = (EditText) findViewById(R.id.txtName);
        txtPhone = (EditText) findViewById(R.id.txtPhone);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtAdress = (EditText) findViewById(R.id.txtAdress);
        chkIsActive = (CheckBox) findViewById(R.id.chkIsActive);

        cboDivision = (Spinner) findViewById(R.id.cboDivision);
        btnSave = (Button) findViewById(R.id.btnSave);


        if (formMode.equalsIgnoreCase("Edit")) {
            String divName = "";
            Cursor c = dbhelper.getNoteById(noteID);
            if (c.getCount() > 0) {
                if (c.moveToFirst()) {
                    do {
                        txtNote.setText(c.getString(1));
                        txtName.setText(c.getString(2));
                        txtPhone.setText(c.getString(3));
                        txtEmail.setText(c.getString(4));
                        txtAdress.setText(c.getString(5));

                        if(c.getString(9) != null) {
                            if (c.getString(9).equalsIgnoreCase("1")) {
                                chkIsActive.setChecked(true);
                            } else {
                                chkIsActive.setChecked(false);
                            }
                        }

                        divisionId = c.getString(7);

                        divName = c.getString(8);

                    } while (c.moveToNext());
                }

                loadDropDown();

                cboDivision.setSelection(getIndex(cboDivision, divName));
            }


            btnSave.setText("Update");

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    builder.setMessage("Are you sure that you want to update this note?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    if(txtNote.getText().toString().trim().equalsIgnoreCase(""))
                                    {
                                        txtNote.setHint("Please Enter Note");
                                        txtNote.setError("Required");
                                        txtNote.requestFocus();
                                    } else {

                                        String note = txtNote.getText().toString().trim();
                                        String name = txtName.getText().toString().trim();
                                        String phone = txtPhone.getText().toString().trim();
                                        String email = txtEmail.getText().toString().trim();
                                        String address = txtAdress.getText().toString().trim();

                                        Boolean isActive = false;
                                        if(chkIsActive.isChecked()) {
                                            isActive = true;
                                        } else
                                        {
                                            isActive = false;
                                        }

                                        dbhelper.updateNoteById(noteID, note, name, phone, email, address, divisionId,isActive);
                                    }

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.setTitle("Confirmation");
                    alert.show();

                }
            });


        } else {
            btnSave.setText("Save");
            loadDropDown();

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    builder.setMessage("Are you sure that you want to save this note?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    if(txtNote.getText().toString().trim().equalsIgnoreCase(""))
                                    {
                                        txtNote.setHint("Please Enter Note");
                                        txtNote.setError("Required");
                                        txtNote.requestFocus();
                                    } else {

                                        String note = txtNote.getText().toString().trim();
                                        String name = txtName.getText().toString().trim();
                                        String phone = txtPhone.getText().toString().trim();
                                        String email = txtEmail.getText().toString().trim();
                                        String address = txtAdress.getText().toString().trim();

                                        Boolean isActive = false;
                                        if(chkIsActive.isChecked()) {
                                            isActive = true;
                                        } else
                                        {
                                            isActive = false;
                                        }

                                        dbhelper.saveNote(note, name, phone, email, address, divisionId,isActive);
                                    }

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.setTitle("Confirmation");
                    alert.show();
                }
            });
        }

    }

    private void loadDropDown() {
        Cursor division = dbhelper.getDivisionList();

        divisionList.clear();
        DivisionItem licenseItem = new DivisionItem();
        licenseItem.setId("");
        licenseItem.setName("-Select-");
        licenseItem.setName_bn("");

        divisionList.add(licenseItem);
        if (division.getCount() > 0) {
            if (division.moveToFirst()) {
                do {

                    DivisionItem item = new DivisionItem();
                    item.setId(division.getString(0));
                    item.setName(division.getString(1));
                    item.setName_bn(division.getString(2));

                    divisionList.add(item);

                    ArrayAdapter<DivisionItem> adapter = new ArrayAdapter<DivisionItem>(getApplicationContext(), R.layout.custom_spinner_item, divisionList);
                    cboDivision.setAdapter(adapter);

                    cboDivision.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            DivisionItem item = (DivisionItem) parent.getSelectedItem();
                            divisionId = item.getId();

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                } while (division.moveToNext());
            }
        }
    }

    private int getIndex(Spinner spinner, String myString) {

        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(myString)) {
                index = i;
            }
        }
        return index;
    }

    @Override
    public void onBackPressed() {
        // It's expensive, if running turn it off.
        finish();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
