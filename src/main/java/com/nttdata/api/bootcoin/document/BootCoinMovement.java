package com.nttdata.api.bootcoin.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Unwrapped.Nullable;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "bootcoin")
public class BootCoinMovement {

    @Id
    private String id;
    private String applicantId;
    private Double amount;
    private Integer payMode;
    @Nullable
    private Boolean accepted;
    @Nullable
    private String accountNumber;
    @Nullable
    private String phone;
    @Nullable
    private String transactionNumber;
    @NotNull
    private String destinationAccount;

}
