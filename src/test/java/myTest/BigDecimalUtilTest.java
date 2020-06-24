package myTest;

import com.luoyi.util.BigDecimalUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * @author luoyi9402
 * @since 20/6/24 下午 2:21
 */
@Slf4j
public class BigDecimalUtilTest {

    /**
     * 测试computeByFormula方法的可用性与时间消耗
     * @author luoyi9402
     * @since 20/6/24 下午 2:46
     */
    @Test
    public void testFormula() {
        //测试计算结果
        BigDecimal grossProfit = new BigDecimal("8844.43");
        int base = 5000;
        String rate = "0.01";
        BigDecimal profit = BigDecimalUtil.INSTANCE.computeByFormula("奖励 =（毛利-标准值）*系数", grossProfit, base, rate);
        log.debug("profit : {}", profit);
        //对比普通写法，写错一个括号会很难发现
        BigDecimal profit0 = grossProfit.subtract(new BigDecimal(base)).multiply(new BigDecimal(rate)).setScale(4, BigDecimal.ROUND_HALF_UP);
        log.debug("profit0 : {}", profit0);
        BigDecimal profit1 = grossProfit.subtract(new BigDecimal(base).multiply(new BigDecimal(rate))).setScale(4, BigDecimal.ROUND_HALF_UP);
        log.debug("profit1 : {}", profit1);

        //测试计算一万次所需时间
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            double iProfit = Math.random() * 10000;
            BigDecimalUtil.INSTANCE.computeByFormula("奖励 =（毛利-标准值）*系数", iProfit, base, rate);
        }
        long endTime = System.currentTimeMillis();
        log.debug("计算一万次共需要{}毫秒", endTime - startTime);
        //测试结果为“计算一万次共需要1460毫秒”，如果不打印debug日志，则“计算一万次共需要482毫秒”
    }
}
