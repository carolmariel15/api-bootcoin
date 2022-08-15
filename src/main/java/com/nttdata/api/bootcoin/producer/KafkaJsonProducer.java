package com.nttdata.api.bootcoin.producer;

import com.nttdata.api.bootcoin.document.BootCoinMovement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/*pruebaaaaaaa*/

/*comentario 2*/

@Component
public class KafkaJsonProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaJsonProducer.class);

    private final KafkaTemplate<String, BootCoinMovement> kafkaTemplate;

    public KafkaJsonProducer(@Qualifier("kafkaTemplate") KafkaTemplate<String, BootCoinMovement> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendBootcoinM(BootCoinMovement bm) {
       System.out.println("Enviando solicitud "+ bm);
        this.kafkaTemplate.send("topic-bootcoinm", bm);
    }


}
