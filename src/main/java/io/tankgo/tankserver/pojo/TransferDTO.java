package io.tankgo.tankserver.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferDTO {
    private String from;
    private String to;
    private String amount;
    private String code;
    private String symbol;
    private String memo;
}
