package jp.co.leadinge.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.co.leadinge.chat.entity.Member;

public class MemberlistActivity
        extends AppCompatActivity
        implements AdapterView.OnItemClickListener{

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Member> memberlist = new ArrayList<>();
    private String myId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memberlist);

        myId = getMyId();
        getMemberlist();

    }

    private List<Member> getMemberlist(){

        db.collection("members")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Map<String, Object> user = document.getData();
                                Member member = new Member();
                                member.setUsername(user.get("name").toString());
                                member.setPassword(user.get("password").toString());
                                member.setLoginid(user.get("loginid").toString());
                                member.setId(document.getId());

                                if(myId.equals(member.getId())){
                                    Log.d("TEST123", document.getId() + " => " + document.getData()
                                        + " is ME.");
                                    continue;
                                }

                                memberlist.add(member);
                                Log.d("TEST123", document.getId() + " => " + document.getData());
                            }
                            List<String> names = new ArrayList<>();
                            for (Member member : memberlist) {
                                names.add(member.getUsername());
                            }

                            ArrayAdapter<String> adapter =
                                    new ArrayAdapter<String>(
                                            MemberlistActivity.this,
                                            android.R.layout.simple_list_item_1);

                            adapter.addAll(names);
                            ListView listView = (ListView)findViewById(R.id.list_view);
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener((AdapterView.OnItemClickListener) MemberlistActivity.this);
                        } else {
                            Log.w("TEST123", "Error getting documents.", task.getException());
                        }
                    }
                });

        return memberlist;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Member member = memberlist.get(position);
        setSharedPreferences(member.getId(),member.getUsername());

        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }

    private void setSharedPreferences(String id,String username){
        SharedPreferences dataStore = getSharedPreferences("DataStore", MODE_PRIVATE);
        SharedPreferences.Editor editor = dataStore.edit();
        editor.putString("otherid", id);
        editor.putString("otherusername", username);
        editor.commit();
    }

    private String getMyId() {
        SharedPreferences dataStore = getSharedPreferences("DataStore", MODE_PRIVATE);
        return dataStore.getString("id", null);
    }
}
