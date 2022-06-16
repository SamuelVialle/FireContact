# FireContact
Liste de contact avec ajout, modification et suppression le tout synchronisé avec Firestore et Fire Storage.

# Informations de base sur l'ajout d'image depuis notre terminal

1 - Ajouter les dépendences de Storage en cliquant sur Tools > Firebase > Storage et cliquer sur add Cloud Storage to your app

2 - Les ajouts dans le gradle sont : 
        /** Connaitre le nom exact d'une librairie **/
        /** https://developer.android.com/jetpack/androidx/migrate/artifact-mappings **/
        /** Support **/
        implementation 'androidx.appcompat:appcompat:1.2.0'
        /** Glide **/
        implementation 'com.github.bumptech.glide:glide:4.11.0'
        annotationProcessor 'com.github.bumptech.glide:compiler:4.11.0'
        /** Picasso **/
        implementation 'com.squareup.picasso:picasso:2.71828'
        /** Firebase -- Firestore **/
        implementation 'com.google.firebase:firebase-firestore:22.0.0'
        implementation 'com.firebaseui:firebase-ui-firestore:7.1.0'
        implementation platform('com.google.firebase:firebase-bom:26.1.0')
        implementation 'com.google.firebase:firebase-analytics'
        /** Firebase -- Storage **/
        implementation 'com.google.firebase:firebase-storage:19.2.0'
        /** Firebase Auth **/
        implementation 'com.google.firebase:firebase-auth:20.0.1'
        /** RecyclerView **/
        implementation 'androidx.recyclerview:recyclerview:1.1.0'
        implementation 'androidx.recyclerview:recyclerview-selection:1.1.0-rc03'
        implementation 'androidx.cardview:cardview:1.0.0'
        
3 - Il faut ajouter l'accés à internet dans le fichier AndroidManifest
    <uses-permission android:name="android.permission.INTERNET"/>

4 - 

