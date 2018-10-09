package com.tvl.trainningapp.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.tvl.trainningapp.model.Note;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DBHelperLocal extends SQLiteOpenHelper {

    private static String DB_NAME = "notes_db.db";
    private SQLiteDatabase db;
    private final Context context;
    private String DB_PATH;

    private HashMap hp;

    public DBHelperLocal(Context context) {
        super(context, DB_NAME, null, 3);
        this.context = context;
        DB_PATH = "/data/data/" + context.getPackageName() + "/" + "databases/";
    }

    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();
        if (dbExist) {

        } else {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    private void copyDataBase() throws IOException {

        InputStream myInput = context.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Note.TABLE_NAME + " ORDER BY " +
                Note.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)));
                note.setNote(cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)));

                notes.add(note);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notes;
    }

    public long insertNote(String note) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Note.COLUMN_NOTE, note);

        // insert row
        long id = db.insert(Note.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public Note getNote(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Note.TABLE_NAME,
                new String[]{Note.COLUMN_ID, Note.COLUMN_NOTE, Note.COLUMN_TIMESTAMP},
                Note.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        Note note = new Note(
                cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)));

        // close the db connection
        cursor.close();

        return note;
    }

    public int updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Note.COLUMN_NOTE, note.getNote());

        // updating row
        return db.update(Note.TABLE_NAME, values, Note.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
    }

    public void deleteNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Note.TABLE_NAME, Note.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
        db.close();
    }

    public int getNotesCount() {
        String countQuery = "SELECT  * FROM " + Note.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }


    public Cursor getDataFromLocal() {
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READONLY);
        Cursor c = db.rawQuery("select notes.id,\n" +
                "notes.note,\n" +
                "notes.name,\n" +
                "notes.phone,\n" +
                "notes.email,\n" +
                "notes.address,\n" +
                "notes.timestamp,\n" +
                "notes.division_id,\n" +
                "divisions.name,\n" +
                "notes.IsActive\n" +
                "from notes\n" +
                "left join divisions on divisions.id = notes.division_id", null);
        return c;
    }

    public Cursor getDivisionList() {
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READONLY);
        Cursor c = db.rawQuery("select divisions.id,\n" +
                "divisions.name,\n" +
                "divisions.name_bn,\n" +
                "divisions.geo_code,\n" +
                "divisions.created_by,\n" +
                "divisions.created_at,\n" +
                "divisions.updated_by,\n" +
                "divisions.updated_at\n" +
                "from divisions ", null);
        return c;
    }

    public void saveNote(String note, String name, String phone, String email, String address, String divisionId, Boolean isActive) throws SQLiteException {

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        ContentValues newValues = new ContentValues();

        newValues.put("note", note);
        newValues.put("name", name);
        newValues.put("phone", phone);
        newValues.put("email", email);
        newValues.put("address", address);
        newValues.put("division_id", divisionId);
        newValues.put("timestamp", "");
        newValues.put("IsActive", isActive);

        long rowInserted = db.insert("notes", null, newValues);

        if (rowInserted != -1) {
            Toast.makeText(context, "Note Saved Successfully.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Note Save Failed.", Toast.LENGTH_SHORT).show();
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public void deleteNoteById(String observationId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM notes WHERE id = " + observationId);
        db.close();
    }

    public Cursor getNoteById(String noteId) {
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READONLY);
        Cursor c = db.rawQuery("select notes.id,\n" +
                "notes.note,\n" +
                "notes.name,\n" +
                "notes.phone,\n" +
                "notes.email,\n" +
                "notes.address,\n" +
                "notes.timestamp,\n" +
                "notes.division_id,\n" +
                "divisions.name,\n" +
                "notes.IsActive\n" +
                "from notes\n" +
                "left join divisions on divisions.id = notes.division_id\n" +
                "where notes.id = " + noteId, null);
        return c;
    }

    public void updateNoteById(String noteId, String note, String name, String phone, String email, String address, String divisionId, Boolean isActive) throws SQLiteException {

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        ContentValues newValues = new ContentValues();

        newValues.put("note", note);
        newValues.put("name", name);
        newValues.put("phone", phone);
        newValues.put("email", email);
        newValues.put("address", address);
        newValues.put("division_id", divisionId);
        newValues.put("IsActive", isActive);

        String where = "id = " + noteId;;
        long rowInserted = db.update("notes", newValues, where, null);

        if(rowInserted != -1) {
            Toast.makeText(context, "Note Updated Successfully.", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context, "Note Update failed", Toast.LENGTH_SHORT).show();
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

//        if (oldVersion < 3) {
//            db.execSQL("ALTER TABLE bookmarks ADD COLUMN base_url varchar(200) DEFAULT NULL;");
//            db.execSQL("ALTER TABLE bookmarks ADD COLUMN created_at DATETIME DEFAULT NULL;");
//        }

    }

}