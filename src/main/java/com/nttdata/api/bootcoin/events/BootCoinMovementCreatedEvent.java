package com.nttdata.api.bootcoin.events;

import com.nttdata.api.bootcoin.document.BootCoinMovement;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BootCoinMovementCreatedEvent extends Event<BootCoinMovement> {
}
