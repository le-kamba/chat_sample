package jp.co.leadinge.chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import jp.co.leadinge.chat.entity.Member;

public class ResistActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @BindView(R.id.LoginidEditText) EditText loginidEdit;
    @BindView(R.id.PasswordEditText) EditText passwordEdit;
    @BindView(R.id.UsernameEditText) EditText usernameEdit;
    @BindView(R.id.TelEditText) EditText telEdit;
    @BindView(R.id.resistButton) Button resistButton;
    private Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resist);
        mUnbinder = ButterKnife.bind(this);

    }

    @OnClick(R.id.resistButton)
    public void submitResistButton() {
        Log.d("TEST","setOnClickListener");

        String loginid = loginidEdit.getText().toString();
        if( loginid.length() < 5 && loginid.length() > 16){
            return;
        }

        Map<String, Object> member = new HashMap<>();
        member.put("loginid", loginidEdit.getText().toString());
        member.put("password", passwordEdit.getText().toString());
        member.put("name", usernameEdit.getText().toString());
        member.put("tel", telEdit.getText().toString());

        db.collection("members").document(loginidEdit.getText().toString())
                .set(member)
                .addOnSuccessListener(new OnSuccessListener<Void>() {

                    @Override
                    public void onSuccess(Void a) {
                        Log.d("TEST", "onSuccess");
                        setSharedPreferences(loginidEdit.getText().toString(), usernameEdit.getText().toString());
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TEST",e.getMessage());
                    }
                });
    }

    private void setSharedPreferences(String id,String username){
        SharedPreferences dataStore = getSharedPreferences("DataStore", MODE_PRIVATE);
        SharedPreferences.Editor editor = dataStore.edit();
        editor.putString("id", id);
        editor.putString("username", username);
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }
}
