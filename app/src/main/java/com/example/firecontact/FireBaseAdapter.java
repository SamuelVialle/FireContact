package com.example.firecontact;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class FireBaseAdapter extends FirestoreRecyclerAdapter<ContactModel, FireBaseAdapter.MyViewHolder> {

    /**
     * Variable Globales
     */
    Context context;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    // FirebaseRecyclerAdapter is a class provided by
    // FirebaseUI. it provides functions to bind, adapt and show
    // database contents in a Recycler View
    public FireBaseAdapter(@NonNull FirestoreRecyclerOptions<ContactModel> options) {
        super(options);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nom, mail, tel;
        ImageView avatar;

        public MyViewHolder(@NonNull View itemView) {
            /** Dans ce constructeur on fait le lien vers le design du layout d'une ligne du recycler **/
            super(itemView);
            avatar = itemView.findViewById(R.id.iv_avatar);
            nom = itemView.findViewById(R.id.tv_nom);
            mail = itemView.findViewById(R.id.tv_mail);
            tel = itemView.findViewById(R.id.tv_tel);
        }
    }


    // Function to bind the view in Card view(here list_item.xml with data in ContactModel
    @Override
    protected void onBindViewHolder(@NonNull FireBaseAdapter.MyViewHolder holder, int position, @NonNull ContactModel model) {


        String url = (model.getAvatar_img());

        holder.nom.setText(model.getNom());
        holder.mail.setText(model.getMail());
        holder.tel.setText(model.getTel());

        /** Utilisation de Glide pour g??rer les images **/
        // La premi??re partie options, obligatoire pour les images venant du web, g??re les erreurs
        RequestOptions options = new RequestOptions()
                // Centrage et d??coupage de l'image
                .centerCrop()
                // Image ?? afficher en cas d'erreur
                .error(R.mipmap.ic_launcher_round)
                // Image affich??e par d??faut
                .placeholder(R.drawable.ic_baseline_perm_identity_32);

        Context context = holder.avatar.getContext();
        // La seconde partie est pour l'affichage de l'image plac??e dans le storage
        Glide.with(context)
                // On r??cup??re l'url de l'image pr??sente de le storage et dont l'url est stock?? dans Firestore
                .load(url)
                // On applique les options de chargement
                .apply(options)
                // Resize et alignement au centre
                .fitCenter()
                // Resize pour que les images soient toutes ?? la m??me taille
                .override(150, 150)
                // Decoupage de l'image pour en faire un rond
                .circleCrop()
                // Gestion des images dans le cache pour am??liorer l'affichage
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                // Emplacement o?? afficher l'image
                .into(holder.avatar);

        // Ajout du clic sur tout l'??l??ment d'une ligne du recyclerView
        holder.itemView.setOnClickListener(v -> {
            // Ici on lance la m??thode qui r??pond ?? l'action du clic
//            actionDuClic();
        });
    }

    // M??thode onCreateViewHolder pour remplir le design associ?? au ViewHolder, ici list_item.xml
    // On encapsule le layoutInflater dans une view dans le cas o?? on ait besoin de travailler avec la vue
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(view);
    }

    // Gestion des erreurs gr??ce ?? la m??thode h??rit??e de FirebaseFirestoreException
    @Override
    public void onError(FirebaseFirestoreException e) {
        // S'il y a des erreurs on les affiche dans le log error
        Log.e("error", e.getMessage());
    }


//        adapter.notifyDataSetChanged();
//        recyclerView.setAdapter(adapter);
}

//    @Override
//    public Filter getFilter() {
//
//        return new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence charSequence) {
//
//                String charString = charSequence.toString();
//
//                if (charString.isEmpty()) {
//
//                    listContacts = mArrayList;
//                } else {
//
//                    ArrayList<Contacts> filteredList = new ArrayList<>();
//
//                    for (Contacts contacts : mArrayList) {
//
//                        if (contacts.getName().toLowerCase().contains(charString)) {
//
//                            filteredList.add(contacts);
//                        }
//                    }
//
//                    listContacts = filteredList;
//                }
//
//                FilterResults filterResults = new FilterResults();
//                filterResults.values = listContacts;
//                return filterResults;
//            }
//
//            @Override
//            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
//                listContacts = (ArrayList<Contacts>) filterResults.values;
//                notifyDataSetChanged();
//            }
//        };


