package com.tvl.trainningapp.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tvl.trainningapp.R;
import com.tvl.trainningapp.activity.EntryActivity;
import com.tvl.trainningapp.datasource.DBHelperLocal;
import com.tvl.trainningapp.model.Note;

import java.util.ArrayList;
import java.util.Locale;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.MyViewHolder> {
    private ArrayList<Note> list;
    private Activity activity;
    private Locale locale;

    String successMessage = "";

    DBHelperLocal dbhelper;

    ProgressDialog prgDialog;

    public NoteListAdapter(ArrayList<Note> Data, Activity activity) {
        list = Data;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cap_list_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        dbhelper = new DBHelperLocal(activity);

        prgDialog = new ProgressDialog(activity);
        prgDialog.setMessage("Syncing With Server. Please wait...");
        prgDialog.setCancelable(false);


        holder.txtNote.setText(list.get(position).getNote());
        holder.txtName.setText(list.get(position).getName());

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertbox = new AlertDialog.Builder(v.getRootView().getContext());
                alertbox.setMessage("Are you sure you want to delete the selected item?");
                alertbox.setTitle("Warning");

                alertbox.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0,
                                                int arg1) {

                                dbhelper.deleteNoteById(String.valueOf(list.get(position).getId()));
                                Toast.makeText(activity, "Note has been deleted successfully.", Toast.LENGTH_LONG).show();
                                list.remove(position);
                                notifyDataSetChanged();

                            }
                        });
                alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0,
                                        int arg1) {

                    }
                });
                alertbox.show();
            }
        });

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, EntryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("mode","Edit");
                intent.putExtra("noteId",list.get(position).getId());
                activity.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView txtNote, txtName;
        public Button btnDelete, btnEdit, btnSync;

        public MyViewHolder(View v) {
            super(v);
            txtNote = (TextView) v.findViewById(R.id.txtNote);
            txtName = (TextView) v.findViewById(R.id.txtName);

            btnDelete = (Button) v.findViewById(R.id.btnDelete);
            btnEdit = (Button) v.findViewById(R.id.btnEdit);
            btnSync = (Button) v.findViewById(R.id.btnSync);

        }
    }

    public boolean isNetworkOnline() {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null
                    && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(1);
                if (netInfo != null
                        && netInfo.getState() == NetworkInfo.State.CONNECTED)
                    status = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return status;

    }
}
