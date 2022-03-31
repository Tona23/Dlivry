package com.example.dlivry.Registro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.dlivry.Login;
import com.example.dlivry.R;
import com.example.dlivry.utils.InputValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class Registro extends AppCompatActivity {
//Crear variables

    private EditText et_nombre, et_apellido, et_fecha1,et_fecha2,et_fecha3,et_numero , et_correo, et_contrasena, et_confirmar, et_ruta;
    private Button btn_aceptar;

    private RadioButton rb_terminos;

    String key = "0";
    FirebaseAuth mAuth;
    String error="";
    String nombre ;
    String apellido;
    String fecha1;
    String fecha2;
    String fecha3;
    String numero ;
    String correo ;
    String contrasena ;
    String ruta ;
    boolean reg=false;
    RegistroConstructor emp = new RegistroConstructor( nombre, apellido, fecha1, numero, correo, contrasena, ruta,"false");
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_registro);

            et_nombre = (EditText) findViewById(R.id.txt_c_nombre);
            et_apellido = (EditText) findViewById(R.id.txt_c_apellido);
            et_fecha1 = (EditText) findViewById(R.id.txt_c_fecha1);
            et_fecha2 = (EditText) findViewById(R.id.txt_c_fecha2);
            et_fecha3 = (EditText) findViewById(R.id.txt_c_fecha3);
            et_numero = (EditText) findViewById(R.id.txt_c_numero);
            et_correo = (EditText) findViewById(R.id.txt_c_correo);
            et_contrasena = (EditText) findViewById(R.id.txt_c_contrasena);
            et_confirmar = (EditText) findViewById(R.id.txt_c_confirmar);
            et_ruta = (EditText) findViewById(R.id.txt_c_ruta);

            btn_aceptar = (Button) findViewById(R.id.btn_c_aceptar);
            rb_terminos = (RadioButton) findViewById(R.id.rb_c_terminos);

            btn_aceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                   /*FirebaseDatabase database = FirebaseDatabase.getInstance();
                   DatabaseReference ref = database.getReference("Registro");
                   ref.removeValue(); */
                    nombre = et_nombre.getText().toString().toLowerCase();
                    boolean nombre_b = InputValidation.isValidEditText(et_nombre, "Campo requerido");
                    apellido = et_apellido.getText().toString().toLowerCase();
                    boolean apellido_b = InputValidation.isValidEditText(et_apellido, "Campo requerido");
                    fecha1 = et_fecha1.getText().toString();
                    boolean fecha1_b = InputValidation.isValidEditText(et_fecha1, "Campo requerido");
                    fecha2 = et_fecha2.getText().toString();
                    boolean fecha2_b = InputValidation.isValidEditText(et_fecha2, "Campo requerido");
                    fecha3 = et_fecha3.getText().toString();
                    boolean fecha3_b = InputValidation.isValidEditText(et_fecha3, "Campo requerido");
                    numero = et_numero.getText().toString();
                    boolean numero_b = InputValidation.isValidEditText(et_numero, "Campo requerido");
                    correo = et_correo.getText().toString();
                    boolean correo_b = InputValidation.isValidEditText(et_correo, "Campo requerido");
                    contrasena = et_contrasena.getText().toString();
                    boolean contrasena_b = InputValidation.isValidEditText(et_contrasena, "Campo requerido");
                    String confirmar = et_confirmar.getText().toString();
                    boolean confirmar_b = InputValidation.isValidEditText(et_confirmar, "Campo requerido");
                    ruta = et_ruta.getText().toString();
                    boolean ruta_b = InputValidation.isValidEditText(et_ruta, "Campo requerido");
                    boolean terminos = rb_terminos.isChecked();
                    for(int i = 0;i<=nombre.length()-1;i++){
                        if(nombre.charAt(i)!='a'&&nombre.charAt(i)!='b'&&nombre.charAt(i)!='c'&&nombre.charAt(i)!='d'&&nombre.charAt(i)!='e'
                                &&nombre.charAt(i)!='f'&&nombre.charAt(i)!='g'&&nombre.charAt(i)!='h'&&nombre.charAt(i)!='i'&&nombre.charAt(i)!='j'
                                &&nombre.charAt(i)!='k'&&nombre.charAt(i)!='l'&&nombre.charAt(i)!='m'&&nombre.charAt(i)!='n'&&nombre.charAt(i)!='ñ'
                                &&nombre.charAt(i)!='o'&&nombre.charAt(i)!='p'&&nombre.charAt(i)!='q'&&nombre.charAt(i)!='r'&&nombre.charAt(i)!='s'
                                &&nombre.charAt(i)!='t'&&nombre.charAt(i)!='u'&&nombre.charAt(i)!='v'&&nombre.charAt(i)!='w'&&nombre.charAt(i)!='x'
                                &&nombre.charAt(i)!='y'&&nombre.charAt(i)!='z')
                        {
                            et_nombre.setError("Campo invalido");
                            nombre_b=false;
                        }
                    }
                    for(int i = 0;i<=apellido.length()-1;i++){
                        if(apellido.charAt(i)!='a'&&apellido.charAt(i)!='b'&&apellido.charAt(i)!='c'&&apellido.charAt(i)!='d'&&apellido.charAt(i)!='e'
                                &&apellido.charAt(i)!='f'&&apellido.charAt(i)!='g'&&apellido.charAt(i)!='h'&&apellido.charAt(i)!='i'&&apellido.charAt(i)!='j'
                                &&apellido.charAt(i)!='k'&&apellido.charAt(i)!='l'&&apellido.charAt(i)!='m'&&apellido.charAt(i)!='n'&&apellido.charAt(i)!='ñ'
                                &&apellido.charAt(i)!='o'&&apellido.charAt(i)!='p'&&apellido.charAt(i)!='q'&&apellido.charAt(i)!='r'&&apellido.charAt(i)!='s'
                                &&apellido.charAt(i)!='t'&&apellido.charAt(i)!='u'&&apellido.charAt(i)!='v'&&apellido.charAt(i)!='w'&&apellido.charAt(i)!='x'
                                &&apellido.charAt(i)!='y'&&apellido.charAt(i)!='z')
                        {
                            et_apellido.setError("Campo invalido");
                            apellido_b=false;
                        }
                    }
                    if(Integer.parseInt(fecha2)>12||Integer.parseInt(fecha2)==0){
                        et_fecha2.setError("Campo invalido");
                        fecha2_b=false;
                    }
                    if(Integer.parseInt(fecha3)>31||Integer.parseInt(fecha3)==0){
                        et_fecha3.setError("Campo invalido");
                        fecha3_b=false;
                    }

                    if (nombre_b && apellido_b && fecha1_b&& fecha2_b&& fecha3_b && numero_b && correo_b && contrasena_b && confirmar_b && ruta_b) {
                        if (contrasena.equals(confirmar)) {
                            if (contrasena.length() >= 6) {
                                if (terminos == true) {

                                    //createUser();
                                    String email = et_correo.getText().toString();
                                    String password = et_contrasena.getText().toString();
                                    reg=false;

                                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()&&reg==false) {

                                                UserProfileChangeRequest profleUpdate = new UserProfileChangeRequest.Builder()
                                                        .setDisplayName(nombre+" "+apellido)
                                                        .build();

                                                FirebaseUser currentUser = mAuth.getCurrentUser();

                                                currentUser.updateProfile(profleUpdate)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful()&&reg==false) {
                                                                    // user info updated successfully
                                                                    Toast.makeText(getApplicationContext(), "Current", Toast.LENGTH_SHORT).show();
                                                                    Toast.makeText(getApplicationContext(), "Registro Exitoso", Toast.LENGTH_SHORT).show();


                                                                    databaseReference.child("RegistroControl").addListenerForSingleValueEvent(new ValueEventListener() {

                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                            if (snapshot.exists()&&reg==false){
                                                                                int cont =0;
                                                                                int a=0;
                                                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                                                    cont++;
                                                                                    key = ""+cont;
                                                                                    String mail= dataSnapshot.child("correo").getValue().toString();
                                                                                    if(mail==correo){
                                                                                        a=1;
                                                                                    }
                                                                                }
                                                                                if(a==0){
                                                                                    registrar();
                                                                                }
                                                                            }else if (!snapshot.exists()&&reg==false) {
                                                                                key="0";
                                                                                registrar();
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                                        }
                                                                    });

                                                                }

                                                            }
                                                        });



                                            } else {
                                                et_correo.setText("");
                                                et_correo.setError("Campo invalido");
                                                error = "" + task.getException().getMessage();
                                                Toast.makeText(getApplicationContext(), "Registration Error:-" + error + "-", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                                    correo_b = InputValidation.isValidEditText(et_correo, "!");
                                } else {
                                    Toast.makeText(getApplicationContext(), "Debes aceptar los terminos y condiciones", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                et_contrasena.setText("");
                                contrasena_b = InputValidation.isValidEditText(et_contrasena, "!");
                                et_confirmar.setText("");
                                confirmar_b = InputValidation.isValidEditText(et_confirmar, "!");
                                Toast.makeText(getApplicationContext(), "La contrseña debe tener 6 o mas caracteres", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            et_contrasena.setText("");
                            contrasena_b = InputValidation.isValidEditText(et_contrasena, "!");
                            et_confirmar.setText("");
                            confirmar_b = InputValidation.isValidEditText(et_confirmar, "!");
                            Toast.makeText(getApplicationContext(), "Contraseñas incorrectas", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Debes llenar todos los campos", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            mAuth = FirebaseAuth.getInstance();

            mAuth.setLanguageCode("es");
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error: "+e, Toast.LENGTH_SHORT).show();
        }
    }

    private void datos(){
        try {
            Intent correos = new Intent(getApplicationContext(), RegistroFoto.class);
            correos.putExtra("EmailTo",et_correo.getText().toString());
            correos.putExtra("correo",correo);
            correos.putExtra("contrasena",contrasena);

            startActivity(correos);} catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error: "+e, Toast.LENGTH_SHORT).show();
        }

    }

    private void registrar(){
        Toast.makeText(getApplicationContext(), "" + key, Toast.LENGTH_LONG).show();
        emp.setKey(key);
        key = emp.getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("nombre", nombre);
        hashMap.put("apellido", apellido);
        hashMap.put("fecha", ""+fecha3+"/"+fecha2+"/"+fecha1);
        hashMap.put("numero", numero);
        hashMap.put("correo", correo);
        hashMap.put("contrasena", contrasena);
        hashMap.put("ruta", ruta);
        hashMap.put("confirmado", "null");
        databaseReference.child("RegistroControl").child(key).updateChildren(hashMap).addOnSuccessListener(suc ->
        {
            Toast.makeText(getApplicationContext(), "Record is updated", Toast.LENGTH_SHORT).show();
            DatabaseReference databaseReferenceE = FirebaseDatabase.getInstance().getReference();
            databaseReferenceE.child("ControlEstado").addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()&&reg==false){
                        int cont =0;
                        int a=0;
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            cont++;
                            key = ""+cont;
                            String mail= dataSnapshot.child("correo").getValue().toString();
                            if(mail==correo){
                                a=1;
                            }
                        }
                        if(a==0){
                          registrarEstado();
                        }
                        reg=true;
                    }else if (!snapshot.exists()&&reg==false){
                        key="0";
                        registrarEstado();
                        reg=true;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }).addOnFailureListener(er ->
        {
            Toast.makeText(getApplicationContext(), "" + er.getMessage(), Toast.LENGTH_SHORT).show();
        });

    }

    private void registrarEstado(){
        Toast.makeText(getApplicationContext(), "" + key, Toast.LENGTH_LONG).show();
        emp.setKey(key);
        key = emp.getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("correo", correo);
        hashMap.put("ruta", ruta);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference1E;
        databaseReference1E = db.getReference("ControlEstado");
        databaseReference1E.child(key).updateChildren(hashMap).addOnSuccessListener(suc ->
        {

            Toast.makeText(getApplicationContext(), "Record is updated", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(er ->
        {
            Toast.makeText(getApplicationContext(), "" + er.getMessage(), Toast.LENGTH_SHORT).show();
        });
        datos();
    }

    @Override
    public void onBackPressed(){
        Intent r = new Intent(getApplicationContext(), Login.class);
        startActivity(r);
        finish();
    }
}