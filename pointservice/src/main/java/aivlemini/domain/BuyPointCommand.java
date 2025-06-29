package aivlemini.domain;

import lombok.Data;

@Data
public class BuyPointCommand {
    private Long userId;
    private Integer amount;
}