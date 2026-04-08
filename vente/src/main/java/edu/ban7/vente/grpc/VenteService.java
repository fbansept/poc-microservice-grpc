package edu.ban7.vente.grpc;

import com.example.grpc.stock.*;
import edu.ban7.vente.exception.*;
import edu.ban7.vente.model.VenteResultat;
import io.grpc.StatusRuntimeException;
import org.springframework.stereotype.Service;

@Service
public class VenteService {

    private final StockServiceGrpc.StockServiceBlockingStub stockStub;

    public VenteService(StockServiceGrpc.StockServiceBlockingStub stockStub) {
        this.stockStub = stockStub;
    }

    public VenteResultat effectuerVente(String produitId, int quantite) {

        // ── Étape 1 : vérifier le stock ──────────────────────────────────
        StockRequest verifReq = StockRequest.newBuilder()
                .setProduitId(produitId)
                .build();

        StockResponse stockResp;
        try {
            stockResp = stockStub.verifierStock(verifReq);
        } catch (StatusRuntimeException e) {
            throw new ServiceIndisponibleException(
                    "Service stock inaccessible : " + e.getStatus().getDescription()
            );
        }

        if (!stockResp.getDisponible()) {
            throw new StockInsuffisantException(
                    "Produit " + produitId + " en rupture de stock"
            );
        }

        // ── Étape 2 : décrémenter le stock ───────────────────────────────
        DecrementerRequest decrReq = DecrementerRequest.newBuilder()
                .setProduitId(produitId)
                .setQuantite(quantite)
                .build();

        try {
            DecrementerResponse decrResp = stockStub.decrementerStock(decrReq);

            if (!decrResp.getSucces()) {
                throw new VenteException("Échec décrémentation : " + decrResp.getMessage());
            }

            return VenteResultat.success(produitId, quantite, decrResp.getQuantiteRestante());

        } catch (StatusRuntimeException e) {
            return handleGrpcError(e);
        }
    }

    private VenteResultat handleGrpcError(StatusRuntimeException e) {
        switch (e.getStatus().getCode()) {
            case NOT_FOUND          -> throw new ProduitIntrouvableException(e.getStatus().getDescription());
            case FAILED_PRECONDITION -> throw new StockInsuffisantException(e.getStatus().getDescription());
            case UNAVAILABLE         -> throw new ServiceIndisponibleException("Service stock hors ligne");
            default                  -> throw new VenteException("Erreur gRPC : " + e.getStatus());
        }
    }
}