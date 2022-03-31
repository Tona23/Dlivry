package com.example.dlivry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dlivry.Registro.RegistroFoto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Splash extends AppCompatActivity {

    ImageView IMG_1;
    String email="";
    boolean str=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

    getSupportActionBar().hide();
    IMG_1=(ImageView)findViewById(R.id.img_logo);
    AlphaAnimation animation = new AlphaAnimation(0.2f, 1.0f);
        animation.setDuration(1000);
        animation.setStartOffset(100);
        animation.setFillAfter(true);
        IMG_1.startAnimation(animation);
        new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();

            FirebaseUser user = mAuth.getCurrentUser();

            if(user != null) {
                email=user.getEmail();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                databaseReference.child("RegistroUsuario").addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()&&str==false){
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                                String mail= dataSnapshot.child("correo").getValue().toString();
                                if(email.equals(mail)){

                                    String confirmado=dataSnapshot.child("confirmado").getValue().toString();
                                    if(confirmado.equals("true")){
                                        Intent correo = new Intent(getApplicationContext(), MainActivity.class);
                                        correo.putExtra("EmailTo",email);
                                        startActivity(correo);

                                    } else if(confirmado.equals("null")){
                                        Intent correo = new Intent(getApplicationContext(), RegistroFoto.class);
                                        correo.putExtra("EmailTo",email);
                                        startActivity(correo);

                                    }
                                    else{
                                        Intent correos = new Intent(getApplicationContext(), ConfirmarCuenta.class);
                                        correos.putExtra("EmailTo",email);
                                        startActivity(correos);
                                    }
                                }

                            }
                            str=true;
                            finish();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Toast.makeText(getApplicationContext(), "User logged in successfully", Toast.LENGTH_SHORT).show();
                //user is already connected  so we need to redirect him to home page
            }else {
                Intent login = new Intent(getApplicationContext(), Login.class);
                startActivity(login);
            }

        }
    },2000);
}
    @Override
    public void onBackPressed(){

    }
}