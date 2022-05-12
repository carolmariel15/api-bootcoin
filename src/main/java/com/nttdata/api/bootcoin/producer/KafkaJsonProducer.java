package com.nttdata.api.bootcoin.producer;

import com.nttdata.api.bootcoin.document.BootCoinMovement;
import com.nttdata.api.bootcoin.events.BootCoinMovementCreatedEvent;
import com.nttdata.api.bootcoin.events.Event;
import com.nttdata.api.bootcoin.events.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class KafkaJsonProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaJsonProducer.class);

    private final KafkaTemplate<Integer, Event<?>> kafkaTemplate;

    public KafkaJsonProducer(@Qualifier("kafkaJsonTemplate") KafkaTemplate<Integer, Event<?>> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendBootcoinM(BootCoinMovement bm) {
        LOGGER.info("Enviando solicitud ", bm);
        BootCoinMovementCreatedEvent created = new BootCoinMovementCreatedEvent();
        created.setData(bm);
        created.setId(UUID.randomUUID().toString());
        created.setType(EventType.CREATED);
        created.setDate(new Date());

        this.kafkaTemplate.send("topic-bootcoinm", created);
    }


}
