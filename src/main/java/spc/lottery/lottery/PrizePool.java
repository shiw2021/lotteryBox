package spc.lottery.lottery;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PrizePool {
    private List<Prize> prizes;


    public Prize getPrizes(float random) {
        BigDecimal bigDecimal = new BigDecimal(random);
        for (Prize p :
                prizes) {

            if (-1 == bigDecimal.compareTo(p.getHitAreaR())) {
                return p;
            }


        }

        return null;
    }

    public Prize searchPrize(String key) {
        for (Prize prize : prizes) {
            if (key.equals(prize.getName())) {
                return prize;
            }
        }
        return null;
    }

    public void setPrizes(List<Prize> prizes) {
        this.prizes = prizes;
        BigDecimal hitAreaR = new BigDecimal("0.0");
        for (Prize p :
                prizes) {
            hitAreaR = hitAreaR.add((p.getRate()));
            p.setHitAreaR(hitAreaR);
        }
    }
}
