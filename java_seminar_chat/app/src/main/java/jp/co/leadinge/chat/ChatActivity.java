package jp.co.leadinge.chat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import jp.co.leadinge.chat.entity.Member;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private Button mButtonSend;

    private ImageView mImageView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ChatMessageAdapter mAdapter;
    @BindView(R.id.et_message) EditText mEditTextMessage;
    private String myId;
    private String otherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mButtonSend = (Button) findViewById(R.id.btn_send);
        mRecyclerView = findViewById(R.id.recyclerView);
        mEditTextMessage = findViewById(R.id.et_message);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new ChatMessageAdapter(this, new ArrayList<ChatMessage>());
        mRecyclerView.setAdapter(mAdapter);

        myId = getMyId();
        otherId = getOtherId();

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditTextMessage.getText().toString();
                if (TextUtils.isEmpty(message)) {
                    return;
                }

                writeMessage(message);
                mEditTextMessage.setText("");
            }
        });

        //リスナー
        db.collection("message")
                .whereEqualTo("groupid", "1")
                .orderBy("date")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot values,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            e.printStackTrace();
                            return;
                        }

                        mAdapter.clear();
                        for (DocumentSnapshot doc : values) {

                            //sendMessage(doc.get("message").toString());

                            if (doc.get("senderid") != null && doc.get("senderid").equals(myId)) {
                                ChatMessage chatMessage = new ChatMessage(doc.get("message").toString(), true, false);
                                mAdapter.add(chatMessage);
                            } else {
                                //相手側のメッセージ
                                ChatMessage chatMessage = new ChatMessage(doc.get("message").toString(), false, false);
                                mAdapter.add(chatMessage);
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                    }
                });

    }

    private void writeMessage(String message){

        Map<String, Object> member = new HashMap<>();
        member.put("receiverid", otherId);
        member.put("groupid", "1");
        member.put("senderid", myId);
        member.put("message", message);
        member.put("date",new Date());

        db.collection("message")
           .add(member)
           .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("CHAT-LOG","onSuccess");

            }
        })
           .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("CHAT-LOG",e.getMessage());
                }
        });

    }

    private void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, true, false);
        mAdapter.add(chatMessage);

        //テスト用にエコー
        //mimicOtherMessage(message);
    }

    private void mimicOtherMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, false, false);
        mAdapter.add(chatMessage);

        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    private void sendMessage() {
        ChatMessage chatMessage = new ChatMessage(null, true, true);
        mAdapter.add(chatMessage);
    }

    private void mimicOtherMessage() {
        ChatMessage chatMessage = new ChatMessage(null, false, true);
        mAdapter.add(chatMessage);

        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }


    private String getMyId() {
        SharedPreferences dataStore = getSharedPreferences("DataStore", MODE_PRIVATE);
        return dataStore.getString("id", null);
    }
    private String getOtherId() {
        SharedPreferences dataStore = getSharedPreferences("DataStore", MODE_PRIVATE);
        return dataStore.getString("otherid", null);
    }

}
