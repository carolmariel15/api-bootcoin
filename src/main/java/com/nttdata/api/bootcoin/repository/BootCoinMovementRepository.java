package com.nttdata.api.bootcoin.repository;

import com.nttdata.api.bootcoin.document.BootCoinMovement;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BootCoinMovementRepository extends ReactiveMongoRepository<BootCoinMovement, Integer> {
}
