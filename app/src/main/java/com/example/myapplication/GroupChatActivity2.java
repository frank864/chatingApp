package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity2 extends AppCompatActivity {
    private Toolbar mToolbar;
    private ImageButton sendMessageButton;
    private EditText userMessageInput;
    private ScrollView mscrollview;
    private TextView displayTextMessage;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef,groupNameRef,GroupMessageKeyRef;

    private String currentGroupName,currentUserID,currentUserName,currentDate,currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat2);


        currentGroupName= getIntent().getExtras().get("groupName").toString();
        Toast.makeText(GroupChatActivity2.this, currentGroupName, Toast.LENGTH_SHORT).show();


        mAuth=FirebaseAuth.getInstance();
        currentUserID= mAuth.getCurrentUser().getUid();
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        groupNameRef=FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);


       initializemethod();


       GetUserInfo();
       sendMessageButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               saveMessageInfoToDatabase();

               userMessageInput.setText("");
               mscrollview.fullScroll(ScrollView.FOCUS_DOWN);
           }
       });


    }

    @Override
    protected void onStart() {
        super.onStart();
        groupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot datasnapshot, @Nullable String previousChildName) {
                if (datasnapshot.exists()){
                    DisplayMessages(datasnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot datasnapshot, @Nullable String previousChildName) {
                if (datasnapshot.exists()){
                    DisplayMessages(datasnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot datasnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot daatasnapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void DisplayMessages(DataSnapshot dataSnapshot) {

        Iterator iterator= dataSnapshot.getChildren().iterator();

        while (iterator.hasNext()){

            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();

            displayTextMessage.append(chatName +":\n" + chatMessage +"\n" + chatTime + "  " +chatDate + "\n\n\n");

            mscrollview.fullScroll(ScrollView.FOCUS_DOWN);
        }

    }

    private void saveMessageInfoToDatabase() {
        String message= userMessageInput.getText().toString();
        String messageKEY = groupNameRef.push().getKey();

        if (TextUtils.isEmpty(message)){
            Toast.makeText(this, "please write message...", Toast.LENGTH_SHORT).show();
        }else{
            Calendar ccalForDate =Calendar.getInstance();
            SimpleDateFormat currentDateFormat= new SimpleDateFormat("MMM dd,yyyy");
            currentDate= currentDateFormat.format(ccalForDate.getTime());


            Calendar ccalForTime =Calendar.getInstance();
            SimpleDateFormat currentTimeFormat= new SimpleDateFormat("hh:mm:ss a");
            currentTime= currentTimeFormat.format(ccalForTime.getTime());


            HashMap<String, Object> groupMessageKey = new HashMap<>();
            groupNameRef.updateChildren(groupMessageKey);
            GroupMessageKeyRef=groupNameRef.child(messageKEY);

            HashMap<String, Object> messageInfoMap= new HashMap<>();
            messageInfoMap.put("name",currentUserName);
            messageInfoMap.put("message",message);
            messageInfoMap.put("date",currentDate);
            messageInfoMap.put("time",currentTime);

            GroupMessageKeyRef.updateChildren(messageInfoMap);




        }
    }

    private void GetUserInfo() {
        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if (datasnapshot.exists()){
                    currentUserName= datasnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void initializemethod() {




        getSupportActionBar().setTitle(currentGroupName);

        sendMessageButton= findViewById(R.id.send_message_button);
        userMessageInput= findViewById(R.id.input_group_message);
        displayTextMessage= findViewById(R.id.group_chat_text_display);
        mscrollview= findViewById(R.id.my_scroll_view);
    }


}