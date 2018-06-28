package wilber.journalapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import wilber.journalapp.adapters.DataProvider;

public class LoginPage extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener{
    private SignInButton googleSignInButton;
    private Button loginButton, createAccountButton;
    private GoogleApiClient googleApiClient;
    private static final int REQ_CODE=667;
    private EditText usernameView,passwordView;
    SQLiteDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        googleSignInButton=(SignInButton)findViewById(R.id.myGoogleLoginButton);
        loginButton=(Button)findViewById(R.id.loginButton);
        createAccountButton=(Button)findViewById(R.id.new_account_button);
        usernameView=(EditText) findViewById(R.id.login_username);
        passwordView=(EditText) findViewById(R.id.login_password);
        databaseConnections();
        if(checkUser("","",false)==true){
            createAccountButton.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
        }else{
            createAccountButton.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.GONE);
        }
        googleSignInButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        createAccountButton.setOnClickListener(this);
        GoogleSignInOptions signInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient=new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();
    }

    @Override
    public void onClick(View view) {
        String username=usernameView.getText().toString();
        String password=passwordView.getText().toString();
        switch (view.getId()){
            case R.id.myGoogleLoginButton:
                signIn();
                break;
            case R.id.loginButton:
                if(username.equals("")||password.equals("")){
                    Toast.makeText(LoginPage.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                }else {
                    if(checkUser(username,password,true)){
                        startActivity(new Intent(getApplicationContext(), HomePage.class));
                    }else{
                        Toast.makeText(LoginPage.this, "Wrong username or password", Toast.LENGTH_SHORT).show();
                    }
                }
                emptyFields();
                break;
            case R.id.new_account_button:
                if(username.equals("")||password.equals("")){
                    Toast.makeText(LoginPage.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                }else{
                    databaseConnections();
                    database.execSQL("insert into users_tb (username,password) values('" + username+"','"+password+"')");
                    createAccountButton.setVisibility(View.GONE);
                    loginButton.setVisibility(View.VISIBLE);
                    emptyFields();
                }
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private void signIn(){
        Intent intent=Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,REQ_CODE);
    }
    private void signOut(){
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {

            }
        });
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==REQ_CODE){
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResults(result);
        }
    }

    private void handleResults(GoogleSignInResult result){
        if(result.isSuccess()){
            startActivity(new Intent(getApplicationContext(),HomePage.class));
//            GoogleSignInAccount account=result.getSignInAccount();
//            String name=account.getDisplayName();
//            String email=account.getEmail();
//            Toast.makeText(LoginPage.this, "Name="+name+" and Email="+email, Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(LoginPage.this, "Error: "+result.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        signOut();
        emptyFields();
        super.onBackPressed();
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
    public boolean checkUser(String username,String password,boolean checkType){
        boolean existance=false;
        try {
            //pick from sqlite
            databaseConnections();
            String query=(checkType)?"SELECT * FROM users_tb WHERE username='"+username+"' AND password='"+password+"'":"SELECT * FROM users_tb";
            Cursor c=database.rawQuery(query,null);
            c.moveToFirst();
            if (c.getCount()>0){
                do{
                    String user_id,user_name;
                    user_id=c.getString(0);
                    user_name=c.getString(1);
                }while (c.moveToNext());
                existance=true;
            }
        }
        catch (Exception ex){
            Toast.makeText(LoginPage.this, ""+ex, Toast.LENGTH_SHORT).show();
            existance=false;
        }
        return existance;
    }
    private  void  emptyFields(){
        usernameView.setText("");
        passwordView.setText("");
    }
}
