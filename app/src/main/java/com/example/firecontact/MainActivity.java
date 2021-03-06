package com.example.firecontact;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    /**
     * Attributs globaux
     **/
    ProgressBar progressBar, progressBarprogressBar1;
    RecyclerView recyclerView;
    /**
     * Cr??ation d'une variable pour savoir si l'on a choisi un avatar
     **/
    private static final int PICK_IMAGE_REQUEST = 1;
    Context context;

    /**
     * Attribut pour l'Uri de l'image
     **/
    private Uri imageUri;

    /**
     * Attributs globaux de la connexion ?? Firebase
     **/
    private FirebaseFirestore db;
    private FireBaseAdapter adapter;
    private StorageReference storageReference;

    /**
     * Attribut pour la gestion du linearlayout du recyclerview
     **/
    LinearLayoutManager linearLayoutManager;

    /**
     * M??thode pour l'initialisation de l'interface utilisateur
     **/
    private void initUI() {
        progressBar = (ProgressBar) findViewById(R.id.pb_loading);
        progressBarprogressBar1 = (ProgressBar) findViewById(R.id.progess_bar_alert);
        recyclerView = (RecyclerView) findViewById(R.id.rv_listeContact);
        context = getApplicationContext();
    }

    /**
     * M??thode init pour initailiser diff??rents composants
     **/
    private void init() {
        // initialisation du linear layout qui va contenir le recycler view
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false);
        // Initialisation du recyclerView avec le linearLayout
        recyclerView.setLayoutManager(linearLayoutManager);
        // Initialisation de la base de donn??e firestore
        db = FirebaseFirestore.getInstance();
        // Initialisation du storage avec le nom du dossier de stockage
        storageReference = FirebaseStorage.getInstance().getReference("contacts");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /** Appel de nos m??thodes **/
        initUI();
        init();
        getDataFromFirestore();

        /** Ajout du fab bouton pour ajouter un contact **/
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addContact();
            }
        });
    }

    /**
     * M??thode pour r??cup??rer les donn??es de la base dans le recycler view
     **/
    private void getDataFromFirestore() {
        // On cr??er une query sur la collection que l'on veut appeler
        Query query = db.collection("contacts");

        // On utilise le model pour faire l'acquisition des donn??es dans Firestore
        FirestoreRecyclerOptions<ContactModel> contacts =
                new FirestoreRecyclerOptions.Builder<ContactModel>()
                        .setQuery(query, ContactModel.class)
                        .build();

        adapter = new FireBaseAdapter(contacts);
//        progressBar.setVisibility(View.GONE);

//        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private void actionDuClic() {
//        Snackbar.make(recyclerView,
//                model.getNom()
//                        + ", "
//                        + model.getMail()
//                        + ", "
//                        + model.getTel()
//                , Snackbar.LENGTH_LONG)
//                .setAction("Action", null)
//                .show();
    }


    // M??thode onStart pour lancer le listener d??s le d??marrage de l'activit??
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    // M??thode onStop pour stoper le listener pour ??viter d'avoir un thread qui cours lorsque l'on quitte l'activit??
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    /************** M??THODE POUR AJOUTER UN CONTACT ************/
    /**
     * M??thode 1 : ajouter une image ?? partir des photos pr??santes dans le terminal
     **/
    private void addContact() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View subView = inflater.inflate(R.layout.add_contact_layout, null);

        final EditText nomContact = (EditText) subView.findViewById(R.id.add_nom);
        final EditText mailContact = (EditText) subView.findViewById(R.id.add_mail);
        final EditText telContact = (EditText) subView.findViewById(R.id.add_tel);
        final Button addAvatar = (Button) subView.findViewById(R.id.btn_choose_img);
        final ImageView iv_avatarChoisi = (ImageView) subView.findViewById(R.id.iv_avatarChoisi);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        /** Gestion du bouton r??ponse ok **/
        builder.setPositiveButton("VALIDER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final String nom = nomContact.getText().toString();
                final String mail = mailContact.getText().toString();
                final String tel = telContact.getText().toString();

                /** Ici ce ne sont que des exemples de v??rifiaction, pour vous donner quelques pistes  **/
//                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
//                String telPattern = "[0-9]";
                Glide.with(getApplicationContext()).load(imageUri).into(iv_avatarChoisi);

                if ((TextUtils.isEmpty(nom)) || (TextUtils.isEmpty(mail)) || (TextUtils.isEmpty(tel))) {
                    Toast.makeText(MainActivity.this, "Il manque une information", Toast.LENGTH_LONG).show();
//                } else if(TextUtils.isEmpty(mail)){
//                    Toast.makeText(MainActivity.this, "Pas de mail pour le contact", Toast.LENGTH_LONG).show();
//                } else if(!mail.trim().matches(emailPattern)){
//                    Toast.makeText(MainActivity.this, "Le mail n'est pas valide", Toast.LENGTH_LONG).show();
//                } else if(TextUtils.isEmpty(tel)){
//                    Toast.makeText(MainActivity.this, "Pas de num??ro de t??l??phone pour le contact", Toast.LENGTH_LONG).show();
//                } else if(mail.trim().matches(telPattern)){
//                    Toast.makeText(MainActivity.this, "Le num??ro de t??l??phone ne doit comporter que des chiffres", Toast.LENGTH_LONG).show();
                } else {
                    /** Envoi des donn??es dans FireStore **/
                    /** Upload de la photo dans le storage **/
                    if (imageUri != null) {
                        // on utilise le temps en milliseconde pour avoir un id unique pour chaque image
                        StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                                + "." + getFileExtension(imageUri));

                        fileReference.putFile(imageUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // On reset la progress bar ?? z??ro avec un handler
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressBar.setProgress(0);
                                            }
                                        }, 500);
                                        // On affiche un toast de succes et on ajoute la m??thode upload
                                        Toast.makeText(MainActivity.this, "Upload r??ussi", Toast.LENGTH_SHORT).show();
                                        // R??cup??ration de l'URL de l'avatar pour le passer ?? Frestore
                                        fileReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                String urlAvatar = task.getResult().toString();

                                                /******/
                                                ContactModel newContact = new ContactModel(nom, mail, tel, urlAvatar);
                                                Log.i(TAG, nom + " " + mail + " " + tel + " " + urlAvatar);
                                                // Add a new document with a generated ID
                                                db.collection("contacts")
                                                        .add(newContact)
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {
                                                                Log.i("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                                                                Log.i("TAG", "onSuccess: " + nom + " " + mail + " " + tel + " " + urlAvatar);
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.e("TAG", "Error adding document", e);
                                                            }
                                                        });
                                                /***/
                                                // Pour info l'URL
                                                Log.i(TAG, "URL : " + urlAvatar);
                                                // L'adresse de URI dans le terminal
                                                Log.i(TAG, "URI : " + imageUri.toString());
                                            }
                                        });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                        // Affichage du nombre de bytes transf??r??s par rapport au total de bytes
                                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                                        // On passe la donn??e progress pour l'affichage de la progressBar
                                        progressBar.setProgress((int) progress);
                                    }
                                });
                    } else {
                        Toast.makeText(getApplicationContext(), "Aucune image de s??lectionn??e", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        /** Gestion du bouton r??ponse n??gative **/
        builder.setNegativeButton("ANNULER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Op??ration annul??e", Toast.LENGTH_LONG).show();
            }
        });

        final AlertDialog dialog = builder.create();
        builder.setTitle("AJOUTER UN CONTACT");
        builder.setView(subView);
        builder.create();

        /** Listener sur le bouton **/
        addAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        builder.show();
    }

    /************** CHOIX DE L'IMAGE ************/
    /**
     * M??thode 1 : ajouter une image ?? partir des photos pr??santes dans le terminal
     **/
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
        }
    }

    /**----------- La gestion de l'exetension du fichier image -----------*/
    /**
     * Il faut r??cup??rer l'extension des images pour les stocker dans firebase storage
     **/
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    /************** END CHOIX DE L'IMAGE ************/


    /******************** GESTION DU MENU ***************/
    /**
     * ----------- L'affichage -----------
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return MainActivity.super.onCreateOptionsMenu(menu);
    }

    /**----------- La fonction de recherche -----------*/
//    private void search(SearchView searchView) {
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
////                if (adapter!=null)
////                    adapter.getFilter().filter(newText);
//                return true;
//            }
//        });
//    }
    /******************** END GESTION DU MENU ***************/

}