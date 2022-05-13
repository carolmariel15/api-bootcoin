package com.nttdata.api.bootcoin.handler;

import com.nttdata.api.bootcoin.document.BootCoinMovement;
import com.nttdata.api.bootcoin.document.Report;
import com.nttdata.api.bootcoin.producer.KafkaJsonProducer;
import com.nttdata.api.bootcoin.repository.BootCoinMovementRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class BootCoinMovementHandler {

    private final BootCoinMovementRepository bootCoinMovementRepository;

    private final KafkaJsonProducer kafkaJsonProducer;

    private static final Logger LOGGER = LoggerFactory.getLogger(BootCoinMovementHandler.class);

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
                    .body(bootCoinMovementRepository.save(c), BootCoinMovement.class);
        });
    }

    public Mono<ServerResponse> edit(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(BootCoinMovement.class)
            .flatMap(v -> {
                return bootCoinMovementRepository.findById(v.getId()).flatMap(c -> {
                    c.setAmount(v.getAmount());
                    c.setPayMode(v.getPayMode());
                    return ServerResponse.status(HttpStatus.CREATED)
                            .contentType(MediaType.TEXT_EVENT_STREAM)
                            .body(bootCoinMovementRepository.save(c), BootCoinMovement.class);
                });
            }).switchIfEmpty(notFound);
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
        var accountNumber = serverRequest.pathVariable("accountNumber");
        List<Report> rep = new ArrayList<>();
        var r = bootCoinMovementRepository.getByDestinationAccount(accountNumber)
                .collectList()
                .flatMapIterable(v -> {
                    v.forEach(x -> rep.add(new Report(x.getId(), x.getApplicantId(), x.getAmount(),
                            x.getAccepted(), x.getAccountNumber(), x.getTransactionNumber())));
                    return rep;
                });

        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(r, Report.class);
    }

    public Mono<ServerResponse> acceptedRequest(ServerRequest serverRequest) {
        var accepted = Boolean.parseBoolean(serverRequest.pathVariable("accepted"));
        var id = Integer.parseInt(serverRequest.pathVariable("id"));

        return bootCoinMovementRepository.findById(id).filter(x-> !x.getAccepted())
            .flatMap(v -> {
                    v.setAccepted(accepted);
                    v.setTransactionNumber(UUID.randomUUID().toString());
                    kafkaJsonProducer.sendBootcoinM(v);
                    return ServerResponse.status(HttpStatus.OK)
                            .contentType(MediaType.TEXT_EVENT_STREAM).body(bootCoinMovementRepository.save(v),
                                    BootCoinMovement.class);
        }).switchIfEmpty(ServerResponse.status(HttpStatus.OK)
                .contentType(MediaType.valueOf(MediaType.TEXT_EVENT_STREAM_VALUE))
                .bodyValue("La solicitud ya fue aceptada."));
    }

}
