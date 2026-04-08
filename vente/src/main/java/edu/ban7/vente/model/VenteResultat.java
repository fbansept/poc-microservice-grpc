package edu.ban7.vente.model;

public record VenteResultat(String produitId, int quantite, int quantiteRestante) {

    public static VenteResultat success(String produitId, int quantite, int quantiteRestante) {
        return new VenteResultat(produitId, quantite, quantiteRestante);
    }
}
