package spc.lottery.lottery;

import lombok.Data;

import java.util.List;

@Data
public class PrizePool {
    private List<Prize> prizes;


    public Prize getPrizes(float random) {

        return null;
    }

    public Prize searchPrize(String key) {
        for (Prize prize : prizes) {
            if (key.contains(prize.getName())) {
                return prize;
            }
        }
        return null;
    }
}
