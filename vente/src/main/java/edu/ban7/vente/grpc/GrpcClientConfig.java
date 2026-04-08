package edu.ban7.vente.grpc;

import com.example.grpc.stock.StockServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {

    @Value("${grpc.client.stock-service.host:localhost}")
    private String host;

    @Value("${grpc.client.stock-service.port:9090}")
    private int port;

    @Bean
    ManagedChannel stockChannel() {
        return ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
    }

    @Bean
    StockServiceGrpc.StockServiceBlockingStub stockStub(ManagedChannel stockChannel) {
        return StockServiceGrpc.newBlockingStub(stockChannel);
    }
}
