package com.nttdata.api.bootcoin.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Report {

    private String id;
    private String applicantId;
    private Double amount;
    private String phone;
    private boolean accepted;
    private String originAccount;
    private String transactionNumber;

}
