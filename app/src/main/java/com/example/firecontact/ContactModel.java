package com.example.firecontact;

public class ContactModel {
    /** Définition des attributs, ces attributs sont connexes avec les cahmps de notre base
     * pour plus de simplicité ils sont tous en String
     */
    private String nom;
    private String mail;
    private String tel;
    private String avatar_img;

    public ContactModel() {
    }

    public ContactModel(String nom, String mail, String tel, String avatar_img) {
        this.nom = nom;
        this.mail = mail;
        this.tel = tel;
        this.avatar_img = avatar_img;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAvatar_img() {
        return avatar_img;
    }

    public void setAvatar_img(String avatar_img) {
        this.avatar_img = avatar_img;
    }
}
