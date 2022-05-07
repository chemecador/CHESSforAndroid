package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText oldPass;
    private EditText newPass;
    private Button ok;
    private Cliente c;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        Intent i = getIntent();
        user = i.getStringExtra("user");
        oldPass = findViewById(R.id.txtOldPass);
        newPass = findViewById(R.id.txtNewPass);
        ok = findViewById(R.id.bOk);

        oldPass.setOnClickListener(this);
        newPass.setOnClickListener(this);
        ok.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.bOk){
            c = new Cliente();
            if (c.isConectado()) {
                c.cambiarPass(this, user, oldPass.getText().toString(), newPass.getText().toString());
            } else {
                Toast.makeText(this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        }
    }
}