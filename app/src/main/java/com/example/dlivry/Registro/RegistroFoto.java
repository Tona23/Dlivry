package com.example.dlivry.Registro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dlivry.ConfirmarCuenta;
import com.example.dlivry.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import kotlin.jvm.internal.Intrinsics;

public class RegistroFoto extends AppCompatActivity {

    private ImageView img_control;
    CardView cv_Camara,cv_Subir;
    int foto = 0,n=0;
    StorageReference nStorage;
    int INTENT = 0;
    int CAMARA_INTENT=1;
    int GALLERY_INTENT=2;
    Uri uri;
    Button btn_registrar;
    private Uri photo;
    Intent var1;
    String fileName;
    FirebaseAuth mAuth;
    String correo ;
    private String modelName;
    private Bitmap bitmap;
    int PCAMARA =100;
    FirebaseUser currentUser ;
    Uri pickedImgUri ;
    boolean reg=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_foto);
        img_control = (ImageView) findViewById(R.id.img_perfil);
        cv_Camara = findViewById(R.id.cv_c_c);
        cv_Subir = findViewById(R.id.cv_c_s);
        btn_registrar = findViewById(R.id.btn_c_registrar);

        cv_Camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkPermissionCamera();

            }

        });

        cv_Subir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirAlbum();
            }
        });
        img_control.setImageResource(R.drawable.dlivry);
        correo = getIntent().getStringExtra("EmailTo");

        btn_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foto == 1) {
                    //createUser();
                    Toast.makeText(getApplicationContext(), "Subiendo Foto", Toast.LENGTH_SHORT).show();
                    reg=false;
                    Intent aceptar = new Intent(getApplicationContext(), ConfirmarCuenta.class);

                    aceptar.putExtra("EmailTo",correo);

                    StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("Perfil");
                    final StorageReference imageFilePath = mStorage.child(modelName);
                    imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // image uploaded succesfully
                            // now we can get our image url

                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri urii) {

                                    // uri contain user image url
                                    currentUser = mAuth.getCurrentUser();

                                    UserProfileChangeRequest profleUpdate = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(currentUser.getDisplayName())
                                            .setPhotoUri(urii)
                                            .build();

                                    currentUser.updateProfile(profleUpdate)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()&&reg==false) {
                                                        // user info updated successfully
                                                        Toast.makeText(getApplicationContext(), ""+urii, Toast.LENGTH_SHORT).show();
                                                        try{

                                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                                                            databaseReference.child("RegistroControl").addListenerForSingleValueEvent(new ValueEventListener() {

                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    RegistroConstructor emp = new RegistroConstructor( "nombre", "apellido", "fecha", "numero", correo, "contrasena", "ruta","true");

                                                                    if (snapshot.exists()&&reg==false){

                                                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                                            String mail= dataSnapshot.child("correo").getValue().toString();
                                                                            String key = dataSnapshot.getKey();
                                                                            emp.setKey(key);
                                                                            key = emp.getKey();
                                                                            if(correo.equals(mail)){

                                                                                HashMap<String, Object> hashMap = new HashMap<>();

                                                                                hashMap.put("confirmado", "false");
                                                                                databaseReference.child("RegistroControl").child(key).updateChildren(hashMap).addOnSuccessListener(suc ->
                                                                                {
                                                                                    Toast.makeText(getApplicationContext(), "Record is updated", Toast.LENGTH_SHORT).show();
                                                                                }).addOnFailureListener(er ->
                                                                                {
                                                                                    Toast.makeText(getApplicationContext(), "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                });

                                                                                startActivity(aceptar);
                                                                                finish();
                                                                            }

                                                                        }
                                                                        reg=true;
                                                                    }
                                                                }
                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });

                                                        }catch (Exception eo){
                                                            Toast.makeText(getApplicationContext(), "e"+eo, Toast.LENGTH_SHORT).show();
                                                        }

                                                    }

                                                }
                                            });

                                }
                            });





                        }
                    });




                } else {
                    Toast.makeText(getApplicationContext(), "Debes tomarte una foto", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PCAMARA);
        }
        nStorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


    }
    private final void checkPermissionCamera() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission((Context) this, "android.permission.CAMERA") != 0) {
                    ActivityCompat.requestPermissions((Activity) this, new String[]{"android.permission.CAMERA"}, 100);
                } else {
                    abrirCamara();
                }
            } else {
                abrirCamara();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error: "+e, Toast.LENGTH_SHORT).show();
        }
    }
    private void abrirCamara(){
        try {
            var1 = new Intent("android.media.action.IMAGE_CAPTURE");
            photo = takeAndSavePicture();

            if (var1.resolveActivity(this.getPackageManager()) != null) {
                var1.putExtra("output", (Parcelable) photo);


                this.startActivityForResult(var1, CAMARA_INTENT);
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error abrirCamara: "+e, Toast.LENGTH_SHORT).show();
        }
    }

    private void abrirAlbum(){
        try {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_INTENT);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error: "+e, Toast.LENGTH_SHORT).show();
        }
    }

    private final Uri takeAndSavePicture() {

        OutputStream fos = null;
        File file = null;
        Uri uri = null;

        if (Build.VERSION.SDK_INT >= 29) {
            ContentResolver resolver = this.getContentResolver();
            fileName = "Image_Profile" + System.currentTimeMillis() + ".jpg";
            ContentValues var7 = new ContentValues();
            var7.put("_display_name", fileName);
            var7.put("mime_type", "image/jpeg");
            var7.put("relative_path", "Pictures/");
            var7.put("is_pending", 1);
            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, var7);

            try {
                OutputStream var10000;
                if (uri != null) {
                    var10000 = resolver.openOutputStream(uri);
                } else {
                    var10000 = null;
                }

                fos = var10000;
            } catch (FileNotFoundException var14) {
                var14.printStackTrace();
            }

            var7.clear();
            var7.put("is_pending", 0);
            if (uri != null) {
                resolver.update(uri, var7, (String) null, (String[]) null);
            }
        } else {
            String imageDir = String.valueOf(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            fileName = System.currentTimeMillis() + ".jpg";
            file = new File(imageDir, fileName);

            try {
                fos = (OutputStream) (new FileOutputStream(file));
            } catch (FileNotFoundException var13) {
                var13.printStackTrace();
            }
        }

        boolean var19;
        label60:
        {
            Bitmap var18 = this.bitmap;
            if (var18 != null) {
                if (var18.compress(Bitmap.CompressFormat.JPEG, 100, fos)) {
                    var19 = true;
                    break label60;
                }
            }
            var19 = false;
        }

        boolean save = var19;
        if (save) {
            Toast.makeText(getApplicationContext(), "Picture save successfully", Toast.LENGTH_SHORT).show();
        }

        if (fos != null) {
            try {
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (fos != null) {
            try {
                fos.flush();
                fos.close();
            } catch (IOException var12) {
                var12.printStackTrace();
            }
        }

        if (file != null) {
            MediaScannerConnection.scanFile((Context) this, new String[]{file.toString()}, (String[]) null, (MediaScannerConnection.OnScanCompletedListener) null);
        }
        if(uri==null){
            takeAndSavePicture();
        }


        return uri;
    }

    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        try{
            Intrinsics.checkNotNullParameter(permissions, "permissions");
            Intrinsics.checkNotNullParameter(grantResults, "grantResults");
            if (requestCode == 100) {
                if (grantResults.length != 0 && grantResults[0] == 0) {
                    //this.abrirCamara();
                } else {
                    Toast.makeText(getApplicationContext(), "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();            }
            }

            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error: "+e, Toast.LENGTH_SHORT).show();

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if ((requestCode == CAMARA_INTENT && resultCode == RESULT_OK)) {
                // if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                //Bundle extras = data.getExtras();
                // imgBitmap = (Bitmap) extras.get("data") ;

                /*try {
                    filePath = nStorage.child("Perfil").child(fileName);

                    filePath.putFile(photo).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            SUBIDAS[n] = fileName;
                            Toast.makeText(getApplicationContext(), "Imagen Subida " + SUBIDAS[n] + "", Toast.LENGTH_SHORT).show();
                            foto = 1;
                        }
                    });

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                    //Conexion con Firebase Storage
                    FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();

                        storageRef = mFirebaseStorage.getReference("Perfil/" + fileName + "");
                        File localfile = File.createTempFile("tempfile", ".jpg");
                        storageRef.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                                //binding.imgLpWallpaper.setImageBitmap(bitmap);
                                img_control.setImageBitmap(bitmap);
                            }
                        });

                    n++;
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error insertar: "+e, Toast.LENGTH_SHORT).show();
                  //  abrirAlbum();

                }*/
                //INTENT=CAMARA_INTENT;

                // }else {

                // } Uri
                abrirAlbum();
            }
            if ((requestCode == GALLERY_INTENT && resultCode == RESULT_OK)) {
                uri = data.getData();
                pickedImgUri = data.getData() ;
                Toast.makeText(getApplicationContext(), ""+uri, Toast.LENGTH_SHORT).show();
                modelName = "" + correo + ".jpg";
                img_control.setImageURI(pickedImgUri);
                n++;
                INTENT = GALLERY_INTENT;
                foto=1;
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error Activity: "+e, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed(){

    }


}