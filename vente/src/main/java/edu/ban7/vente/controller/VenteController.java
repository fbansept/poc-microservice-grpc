package edu.ban7.vente.controller;

import edu.ban7.vente.grpc.VenteService;
import edu.ban7.vente.model.VenteResultat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ventes")
public class VenteController {

    private final VenteService venteService;

    public VenteController(VenteService venteService) {
        this.venteService = venteService;
    }

    @PostMapping
    public ResponseEntity<VenteResultat> effectuerVente(
            @RequestParam String produitId,
            @RequestParam int quantite) {
        VenteResultat resultat = venteService.effectuerVente(produitId, quantite);
        return ResponseEntity.ok(resultat);
    }
}
