package com.example.demo;

import jakarta.persistence.*;

@Entity
// Table "article" avec juste les colonnes spécifiques à Article
// La colonne "id" fait référence à la table "post"
public class Article extends Post {

    // Article n'a pas de champs supplémentaires
    // le contenu texte vient de Post.content
    // On peut ajouter ici des champs spécifiques aux articles si besoin
    private String summary; // résumé de l'article

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
}