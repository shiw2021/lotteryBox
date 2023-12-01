package spc.lottery.lottery;

import lombok.Data;

@Data
public class Prize {

    private String id;
    private String name;
    private float rate;

    public Prize(String value, float v) {
        this.name = value;
        this.rate = v;
    }
}
