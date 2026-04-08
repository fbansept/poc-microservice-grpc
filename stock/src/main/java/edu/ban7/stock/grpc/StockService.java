package edu.ban7.stock.grpc;

import com.example.grpc.stock.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@GrpcService
public class StockService extends StockServiceGrpc.StockServiceImplBase {

    // Simulation BDD : produitId -> quantité disponible
    private final Map<String, Integer> stocks = new ConcurrentHashMap<>(Map.of(
            "P001", 100,
            "P002", 50,
            "P003", 0
    ));

    @Override
    public void verifierStock(StockRequest request,
                              StreamObserver<StockResponse> responseObserver) {
        String produitId = request.getProduitId();

        Integer quantite = stocks.get(produitId);
        if (quantite == null) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Produit introuvable : " + produitId)
                    .asRuntimeException());
            return;
        }

        responseObserver.onNext(StockResponse.newBuilder()
                .setProduitId(produitId)
                .setQuantiteDisponible(quantite)
                .setDisponible(quantite > 0)
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void decrementerStock(DecrementerRequest request,
                                 StreamObserver<DecrementerResponse> responseObserver) {
        String produitId = request.getProduitId();
        int quantiteDemandee = request.getQuantite();

        Integer quantiteActuelle = stocks.get(produitId);
        if (quantiteActuelle == null) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Produit introuvable : " + produitId)
                    .asRuntimeException());
            return;
        }

        if (quantiteActuelle < quantiteDemandee) {
            responseObserver.onError(Status.FAILED_PRECONDITION
                    .withDescription("Stock insuffisant : " + quantiteActuelle + " disponible(s)")
                    .asRuntimeException());
            return;
        }

        int quantiteRestante = quantiteActuelle - quantiteDemandee;
        stocks.put(produitId, quantiteRestante);

        responseObserver.onNext(DecrementerResponse.newBuilder()
                .setSucces(true)
                .setQuantiteRestante(quantiteRestante)
                .setMessage("Stock mis à jour avec succès")
                .build());
        responseObserver.onCompleted();
    }
}
