package com.example.dlivry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dlivry.utils.InputValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.Properties;

public class RecuperarContra extends AppCompatActivity {

    private EditText et_correo;
    private Button btn_enviar;
    private FirebaseAuth auth;
    private boolean e=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_recuperar_contra);

            et_correo = (EditText) findViewById(R.id.txt_c_correoContra);
            btn_enviar = (Button) findViewById(R.id.btn_c_enviar);

            Intent aceptar = new Intent(this, Login.class);
            auth = FirebaseAuth.getInstance();

            btn_enviar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (InputValidation.isValidEditText(et_correo, getString(R.string.field_is_required))) {

                        String correo = et_correo.getText().toString();
                        auth.setLanguageCode("es");
                        auth.sendPasswordResetEmail(correo).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(), "Correo enviado", Toast.LENGTH_SHORT).show();
                                    startActivity(aceptar);
                                }else{
                                    Toast.makeText(getApplicationContext(), "Correo no registrado", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error: "+e, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed(){
        Intent r = new Intent(getApplicationContext(), Login.class);
        startActivity(r);
        finish();
    }

}