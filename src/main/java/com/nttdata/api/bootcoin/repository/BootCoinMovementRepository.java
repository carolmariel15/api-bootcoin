package com.nttdata.api.bootcoin.repository;

import com.nttdata.api.bootcoin.document.BootCoinMovement;
import com.nttdata.api.bootcoin.document.Report;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BootCoinMovementRepository extends ReactiveMongoRepository<BootCoinMovement, String> {

    public Flux<BootCoinMovement> getByDestinationAccount(String destinationAccount);


}
