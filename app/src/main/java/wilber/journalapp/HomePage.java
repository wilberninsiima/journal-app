package wilber.journalapp;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import wilber.journalapp.adapters.DataProvider;
import wilber.journalapp.adapters.ListDataAdapter;

public class HomePage extends AppCompatActivity {
    SQLiteDatabase database;
    ListDataAdapter listdataAdapter;
    ListView results_list_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        results_list_view=(ListView)findViewById(R.id.diary_list_view);
        listdataAdapter=new ListDataAdapter(getApplicationContext(),R.layout.diary_list_layout);
        results_list_view.setAdapter(listdataAdapter);
        databaseConnections();
        loadDiary();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDiaryModel();
            }
        });
        results_list_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                long id=listdataAdapter.getItemId(i);
                Toast.makeText(HomePage.this, "ID="+id, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }
    public void addDiaryModel(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_diary_model_layout);
        dialog.setCancelable(true);
        dialog.show();
        final EditText titleView=(EditText)dialog.findViewById(R.id.add_title);
        final EditText bodyView=(EditText)dialog.findViewById(R.id.add_body);
        TextView btn_cancel = (TextView) dialog.findViewById(R.id.btn_cancel);
        TextView btn_submit_diary = (TextView) dialog.findViewById(R.id.btn_submit_new);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        btn_submit_diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String diary_title=titleView.getText().toString();
                final String diary_body=bodyView.getText().toString();
                if(diary_body.equals("")||diary_title.equals("")){
                    Toast.makeText(getApplicationContext(), "Cannot upload empty diary", Toast.LENGTH_SHORT).show();
                }
                else{
                    database=openOrCreateDatabase("journal_app_db", MODE_PRIVATE, null);
                    database.execSQL("insert into diary_tb (diary_title,diary_body) values('" + diary_title+"','"+diary_body+"')");
                    Toast.makeText(getApplicationContext(), "New diary submitted successfully", Toast.LENGTH_SHORT).show();
                    loadDiary();
                }
                dialog.cancel();
            }
        });
    }
    public void databaseConnections() {
        try {
            database = openOrCreateDatabase("journal_app_db", MODE_PRIVATE, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS diary_tb (diary_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,diary_time DATETIME  NOT NULL DEFAULT (DATETIME('now','localtime')),last_edited DATE NULL, diary_title VARCHAR(64) NOT NULL,diary_body TEXT NOT NULL)");
            database.execSQL("CREATE TABLE IF NOT EXISTS users_tb (user_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,username VARCHAR(64) NOT NULL,password VARCHAR(64) NOT NULL)");
        } catch (Exception ex) {
            Toast.makeText(this, "" + ex, Toast.LENGTH_LONG).show();
        }
    }
    public void loadDiary(){
        listdataAdapter.clear();
        try {
            //pick from sqlite
            databaseConnections();
            Cursor c=database.rawQuery("SELECT * FROM diary_tb ORDER BY diary_time DESC",null);
            c.moveToFirst();
            if (c.getCount()>0){
                do{
                    String time,title,body,last_edited;
                    time=c.getString(1).substring(0,10);
                    last_edited="edited on "+c.getString(2);
                    title=c.getString(3);
                    body=c.getString(4);
                    DataProvider dataProvider=new DataProvider(c.getString(0),time,title,body,last_edited);
                    listdataAdapter.add(dataProvider);
                }while (c.moveToNext());
            }
            listdataAdapter.notifyDataSetChanged();
        }
        catch (Exception ex){
            Toast.makeText(this, "No diary data uploaded", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.delete_all) {
            databaseConnections();
            database.execSQL("DELETE FROM diary_tb");
            listdataAdapter.clear();
            results_list_view.setAdapter(null);
            results_list_view.setAdapter(listdataAdapter);
        }else if (id == R.id.logout) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
