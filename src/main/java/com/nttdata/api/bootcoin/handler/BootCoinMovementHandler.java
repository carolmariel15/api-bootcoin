package com.nttdata.api.bootcoin.handler;

import com.nttdata.api.bootcoin.document.BootCoinMovement;
import com.nttdata.api.bootcoin.document.Report;
import com.nttdata.api.bootcoin.producer.KafkaJsonProducer;
import com.nttdata.api.bootcoin.repository.BootCoinMovementRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class BootCoinMovementHandler {

    private final BootCoinMovementRepository bootCoinMovementRepository;

    private final KafkaJsonProducer kafkaJsonProducer;

    private static Mono<ServerResponse> notFound = ServerResponse.notFound().build();

    @Cacheable(value="userCache")
    public Mono<ServerResponse> getAllBMovements(ServerRequest serverRequest) {
        return  ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(bootCoinMovementRepository.findAll().log(), BootCoinMovement.class);
    }

    public Mono<ServerResponse> getBMovement(ServerRequest serverRequest) {
        var id = Integer.parseInt(serverRequest.pathVariable("id"));
        var bootCoinMovementMono = bootCoinMovementRepository.findById(id);
        return bootCoinMovementMono.flatMap(i -> ServerResponse.ok()
                        .contentType(MediaType.TEXT_EVENT_STREAM)
                        .body(bootCoinMovementMono, BootCoinMovement.class))
                .switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> create(ServerRequest serverRequest) {
        var bootCoinMovementMono = serverRequest.bodyToMono(BootCoinMovement.class);
        return  bootCoinMovementMono.flatMap(c -> {
            c.setAccepted(false);
            return ServerResponse.status(HttpStatus.CREATED)
                    .contentType(MediaType.TEXT_EVENT_STREAM)
                    .body(bootCoinMovementRepository.save(c)
                            .subscribe(kafkaJsonProducer::sendBootcoinM), BootCoinMovement.class);
        });
    }

    public Mono<ServerResponse> edit(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(BootCoinMovement.class).flatMap(v -> {
            return bootCoinMovementRepository.findById(v.getId()).flatMap(c -> {
                c.setAccepted(v.getAccepted());
                return ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.TEXT_EVENT_STREAM)
                        .body(bootCoinMovementRepository.save(c), BootCoinMovement.class);
            }).switchIfEmpty(notFound);
        });
    }

    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        var id = Integer.parseInt(serverRequest.pathVariable("id"));
        return bootCoinMovementRepository.findById(id)
                .flatMap(c -> ServerResponse.status(HttpStatus.OK)
                        .contentType(MediaType.TEXT_EVENT_STREAM)
                        .body(bootCoinMovementRepository.delete(c), Void.class))
                .switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> listReport(ServerRequest serverRequest) {
        var accountNumber = String.valueOf(serverRequest.pathVariable("accountNumber"));
        Report report = new Report();
        var r = bootCoinMovementRepository.findAll()
                .filter(i-> i.getDestinationAccount() == accountNumber)
                .map(x-> {
                    report.setId(x.getId());
                    report.setApplicantId(x.getApplicantId());
                    report.setAmountBootCoin(x.getAmountBootCoin());
                    report.setAmount(x.getAmount());
                    report.setAccepted(x.getAccepted());
                    report.setOriginAccount(x.getOriginAccount());
                    return report;
        });

        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(r, Report.class);
    }
}
