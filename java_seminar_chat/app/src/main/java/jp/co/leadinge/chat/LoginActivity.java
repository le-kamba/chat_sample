package jp.co.leadinge.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import jp.co.leadinge.chat.entity.Member;

public class LoginActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView newResistText;
    private Button loginButton;
    private Button telButton;
    EditText loginIdEdit;
    EditText passwordEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton = (Button) findViewById(R.id.loginButton);
        newResistText = (TextView) findViewById(R.id.newResistText);
        telButton = (Button) findViewById(R.id.telButton);
        loginIdEdit = findViewById(R.id.editText);
        passwordEdit = findViewById(R.id.editText3);

        getSupportActionBar().setTitle("ログイン");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitLogin(loginIdEdit.getText().toString(), passwordEdit.getText().toString());
            }
        });

        telButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, PhoneNumberAuthentication.class);
                startActivity(intent);
            }
        });

        newResistText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitNewResistText();
            }
        });
    }


    public void submitLogin(final String loginid, final String password) {
        final DocumentReference docRef = db.collection("members").document(loginid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        Log.d("TEST", "DocumentSnapshot data: " + document.getData());

                        if (password.equals(document.getString("password"))) {
                            setSharedPreferences(loginid, document.getString("name"));
                            Intent intent = new Intent(
                                    getApplication(), MemberlistActivity.class);
                            startActivity(intent);
                        }else{
                            Log.d("TEST", "INVALID PASSWORD");
                        }
                    } else {
                        Log.d("TEST", "No such document");
                    }
                } else {
                    Log.d("TEST", "get failed with ", task.getException());
                }
            }
        });


    }

    public void submitNewResistText() {
        Log.d("!!!!!", "!!!No such document");
        Intent intent = new Intent(LoginActivity.this, ResistActivity.class);
        startActivity(intent);
    }

    private void setSharedPreferences(String id,String username){
        SharedPreferences dataStore = getSharedPreferences("DataStore", MODE_PRIVATE);
        SharedPreferences.Editor editor = dataStore.edit();
        editor.putString("id", id);
        editor.putString("username", username);
        editor.commit();
    }
}
