package spc.lottery.lottery;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Prize {

    private String id;
    private String name;
    private BigDecimal rate;
    private BigDecimal hitAreaR;

    public Prize(String value, BigDecimal v) {
        this.name = value;
        this.rate = v;
    }
}
