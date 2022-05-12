package com.nttdata.api.bootcoin.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Report {

    private Integer id;
    private String applicantId;
    private Double amount;
    private boolean accepted;
    private String originAccount;

}
