package edu.ban7.vente.interceptor;

import edu.ban7.vente.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class VenteControllerAdvice {

    @ExceptionHandler(StockInsuffisantException.class)
    public ResponseEntity<String> handleStockInsuffisant(StockInsuffisantException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(ProduitIntrouvableException.class)
    public ResponseEntity<String> handleProduitIntrouvable(ProduitIntrouvableException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(ServiceIndisponibleException.class)
    public ResponseEntity<String> handleServiceIndisponible(ServiceIndisponibleException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
    }

    @ExceptionHandler(VenteException.class)
    public ResponseEntity<String> handleVenteException(VenteException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}
