package com.fixer.dmapper;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UserEmailReceiveActivity extends AppCompatActivity {

    TextInputLayout textInputLayout;
    TextInputEditText email_edit;
    Button submit;
    String user_email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_email_receive);
        init_bindview();

    }

    @Override
    protected void onResume() {
        super.onResume();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user_email = email_edit.getText().toString();
                Intent intent = new Intent(UserEmailReceiveActivity.this, MainActivity.class);
                intent.putExtra("myid",MainActivity.M_user_id);
                intent.putExtra("myname",MainActivity.M_user_name);
                intent.putExtra("myemail",user_email);
                startActivity(intent);
            }
        });
    }

    public void init_bindview(){
        textInputLayout = (TextInputLayout)findViewById(R.id.textinputlayout5);
        email_edit = (TextInputEditText)findViewById(R.id.user_email_receive_edittext);
        submit = (Button)findViewById(R.id.submit_button);
    }
}
