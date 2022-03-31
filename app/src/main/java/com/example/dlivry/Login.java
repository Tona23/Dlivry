package com.example.dlivry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dlivry.Registro.Registro;
import com.example.dlivry.Registro.RegistroConstructor;
import com.example.dlivry.Registro.RegistroFoto;
import com.example.dlivry.utils.InputValidation;
import com.example.dlivry.utils.MemoriaPreferences;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    private EditText et_usuario, et_contrasena;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private FirebaseAuth mAuth,nAuth;
    private GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 1;
    String TAG = "GoogleSignIn";
    DatabaseReference nDatabase;
    String key =null;
    String reg = "0";
    int c = 0;
    String email;
    boolean g=false;
    String password;
    String nombre="";
    String apellido="";
    String fecha="";
    String numero="";
    String ruta="";
    String licencia="";
    WifiManager wm;
    AlertDialog ad=null;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et_usuario = (EditText)findViewById(R.id.txt_usr);
        et_contrasena = (EditText)findViewById(R.id.txt_pass);
        btn=(Button)findViewById(R.id.btnSignIn);

        // Configurar Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("428129381351-lj80hvnuct0i9rjiulat4tlejrgm1lgo.apps.googleusercontent.com")
                .requestEmail()
                .build();
        // Crear un GoogleSignInClient con las opciones especificadas por gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();
//        FacebookSdk.sdkInitialize(getApplicationContext());
        //      AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email","public_profile");

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookLogin(loginResult);
            }

            @Override
            public void onCancel() {
                Toast toast1 =
                        Toast.makeText(getApplicationContext(),
                                "Cancelado", Toast.LENGTH_SHORT);

                toast1.show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast toast1 =
                        Toast.makeText(getApplicationContext(),
                                "Error : "+exception, Toast.LENGTH_SHORT);

                toast1.show();
                Log.d("debug ", "Error : "+exception);
            }
        });

        nAuth = FirebaseAuth.getInstance();

        nAuth.setLanguageCode("es");
        mAuth.setLanguageCode("es");
        loginButton.setLogoutText((String) loginButton.getText());

        et_contrasena.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    ingresar();
                }

                return false;
            }
        });

        wm= (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if(Build.VERSION.SDK_INT <= 29 && wm.isWifiEnabled()==false){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Necesita estar conectado a Internet para poder usar correctamenta le App ¿Desea activar el WIFI").setCancelable(false).
                    setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            wm.setWifiEnabled(true);
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            ad=builder.create();
            ad.show();
        }else{

            String memcache = MemoriaPreferences.getDefaultsPreference("Wifi",this);
            if(memcache==null){
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Necesita estar conectado a una red Internet para poder utilizar correctamenta la App ").setCancelable(false).
                        setPositiveButton("Activar WIFI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));

                            }
                        }).setNegativeButton("Activar Datos", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
                    }
                }).setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                MemoriaPreferences.setDefaultsPreference("Wifi","true",getApplicationContext());
                ad=builder.create();
                ad.show();
            }

        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log=true;
                if(log==true) {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    //Intent i = new Intent(mGoogleSignInClient.getApplicationContext(), Registro_conductor.class);
                    //startActivity(i);
                    //finish();
                    startActivityForResult(signInIntent, RC_SIGN_IN);

                }
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Resultado devuelto al iniciar el Intent de GoogleSignInApi.getSignInIntent (...);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN )   {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if(task.isSuccessful()&&log==true){
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    // Google Sign In fallido, actualizar GUI
                    Log.w(TAG, "Google sign in failed", e);
                }
                log=false;
            }else{
                Log.d(TAG, "Error, login no exitoso:" + task.getException().toString());
                Toast.makeText(this, "Ocurrio un error. "+task.getException().toString(),
                        Toast.LENGTH_LONG).show();
            }

        } else  {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
    boolean cont = false,rg=false;

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            log();
//Iniciar DASHBOARD u otra actividad luego del SigIn Exitoso
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }
                    }
                });
    }

    private void log(){
        mAuth = FirebaseAuth.getInstance();
        Toast.makeText(getApplicationContext(), ""+mAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
        RegistroConstructor emp = new RegistroConstructor( nombre, apellido, "fecha1", numero, "correo", "contrasena", ruta,"false");
        try {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("RegistroControl").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()&&rg==false) {

                        if(cont==false) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                key = "" + (Integer.parseInt(dataSnapshot.getKey()) + 1);

                            }
                        }
                        Query databaseReferences = FirebaseDatabase.getInstance().getReference("RegistroControl").orderByChild("correo").equalTo(mAuth.getCurrentUser().getEmail());
                        databaseReferences.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()==false&&rg==false){

                                    emp.setKey(key);
                                    key = emp.getKey();
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("nombre", mAuth.getCurrentUser().getDisplayName());
                                    hashMap.put("apellido", " ");
                                    hashMap.put("numero"," "+ mAuth.getCurrentUser().getPhoneNumber());
                                    hashMap.put("correo", mAuth.getCurrentUser().getEmail());
                                    hashMap.put("confirmado", "true");
                                    hashMap.put("ruta", "27");
                                    databaseReference.child("RegistroControl").child(key).updateChildren(hashMap).addOnSuccessListener(suc ->
                                    {

                                        cont = true;
                                        Toast.makeText(getApplicationContext(), "Record is updated", Toast.LENGTH_SHORT).show();
                                        DatabaseReference databaseReferenceE = FirebaseDatabase.getInstance().getReference();
                                        databaseReferenceE.child("ControlEstado").addListenerForSingleValueEvent(new ValueEventListener() {

                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()&&rg==false){
                                                    int cont =0;
                                                    int a=0;
                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                                                        String mail= dataSnapshot.child("correo").getValue().toString();
                                                        if(mail==mAuth.getCurrentUser().getEmail()){
                                                            a=1;
                                                        }
                                                    }
                                                    if(a==0){
                                                        Toast.makeText(getApplicationContext(), "" + key, Toast.LENGTH_LONG).show();

                                                        HashMap<String, Object> hashMap = new HashMap<>();
                                                        hashMap.put("correo", mAuth.getCurrentUser().getEmail());
                                                        hashMap.put("ruta", "27");

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
                                                    }
                                                    rg=true;
                                                }else  if (!snapshot.exists()&&rg==false){
                                                    key="0";
                                                    emp.setKey(key);
                                                    key = emp.getKey();
                                                    HashMap<String, Object> hashMap = new HashMap<>();
                                                    hashMap.put("correo", mAuth.getCurrentUser().getEmail());
                                                    hashMap.put("ruta", "27");

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
                                                    rg=true;
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
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getApplicationContext(), "No hay registro", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else if (!snapshot.exists()&&rg==false){
                        int cont = 0;
                        key="0";
                        Query databaseReferences = FirebaseDatabase.getInstance().getReference("RegistroControl").orderByChild("correo").equalTo(mAuth.getCurrentUser().getEmail());
                        databaseReferences.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()==false&&rg==false){

                                    emp.setKey(key);
                                    key = emp.getKey();
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("nombre", mAuth.getCurrentUser().getDisplayName());
                                    hashMap.put("apellido", " ");
                                    hashMap.put("numero"," "+ mAuth.getCurrentUser().getPhoneNumber());
                                    hashMap.put("correo", mAuth.getCurrentUser().getEmail());
                                    hashMap.put("confirmado", "true");
                                    hashMap.put("ruta", "27");
                                    databaseReference.child("RegistroControl").child(key).updateChildren(hashMap).addOnSuccessListener(suc ->
                                    {
                                        Toast.makeText(getApplicationContext(), "Record is updated", Toast.LENGTH_SHORT).show();
                                        DatabaseReference databaseReferenceE = FirebaseDatabase.getInstance().getReference();
                                        databaseReferenceE.child("ControlEstado").addListenerForSingleValueEvent(new ValueEventListener() {

                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()&&rg==false){
                                                    int cont =0;
                                                    int a=0;
                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                                                        String mail= dataSnapshot.child("correo").getValue().toString();
                                                        if(mail==mAuth.getCurrentUser().getEmail()){
                                                            a=1;
                                                        }
                                                    }
                                                    if(a==0){
                                                        Toast.makeText(getApplicationContext(), "" + key, Toast.LENGTH_LONG).show();
                                                        emp.setKey(key);
                                                        key = emp.getKey();
                                                        HashMap<String, Object> hashMap = new HashMap<>();
                                                        hashMap.put("correo", mAuth.getCurrentUser().getEmail());
                                                        hashMap.put("ruta", "27");

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
                                                    }
                                                    rg=true;
                                                }else if (!snapshot.exists()&&rg==false){
                                                    key="0";
                                                    emp.setKey(key);
                                                    key = emp.getKey();
                                                    HashMap<String, Object> hashMap = new HashMap<>();
                                                    hashMap.put("correo", mAuth.getCurrentUser().getEmail());
                                                    hashMap.put("ruta", "27");

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
                                                    rg=true;
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
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getApplicationContext(), "No hay registro", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }catch (Exception e){
            Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_SHORT).show();
        }

        // Sign in success, update UI with the signed-in user's information
        Log.d(TAG, "signInWithCredential:success");
        //FirebaseUser user = mAuth.getCurrentUser();
        Intent a = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(a);
        finish();
    }

    boolean log=false;

    public void registro(View view){
        Intent i = new Intent(Login.this, Registro.class);
        startActivity(i);
        finish();
    }
    public void recuperar_contra(View view){
        Intent i = new Intent(Login.this, RecuperarContra.class);
        startActivity(i);
        finish();
    }
    public void ingresar(){

        String usuario = et_usuario.getText().toString();
        boolean usuario_b = InputValidation.isValidEditText(et_usuario, getString(R.string.field_is_required));
        String contrasena = et_contrasena.getText().toString();
        boolean contrasena_b = InputValidation.isValidEditText(et_contrasena, getString(R.string.field_is_required));

        email = et_usuario.getText().toString();
        password = et_contrasena.getText().toString();

        if (TextUtils.isEmpty(email)) {
            et_usuario.setError("Email cannot be empty");
            et_usuario.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            et_contrasena.setError("Password cannot be empty");
            et_contrasena.requestFocus();
        } else {
            nAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                        databaseReference.child("RegistroControl").addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
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
                                                datos();
                                            }
                                        }

                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Toast.makeText(getApplicationContext(), "User logged in successfully", Toast.LENGTH_SHORT).show();



                    } else {
                        Toast.makeText(getApplicationContext(), "Log in Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void datos(){
        try {
            Intent correos = new Intent(getApplicationContext(), ConfirmarCuenta.class);
            correos.putExtra("EmailTo",email);
            startActivity(correos);} catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error: "+e, Toast.LENGTH_SHORT).show();
        }

    }

    private void handleFacebookLogin(LoginResult loginResult){
        AuthCredential credential= FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                           log();
//Iniciar DASHBOARD u otra actividad luego del SigIn Exitoso
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }
                    }
                });
    }

    @Override
    public void onBackPressed(){

    }
}