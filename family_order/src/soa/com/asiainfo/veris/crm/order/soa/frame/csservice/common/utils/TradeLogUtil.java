package com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils;

import com.ailk.biz.BizEnv;
import org.apache.log4j.Logger;

/**
 * Created by zym on 2017/6/26.
 * 打印完工日志
 */
public class TradeLogUtil {

    private static Logger log = Logger.getLogger(TradeLogUtil.class);

    /**
     * 订单日志以INFO等级记录
     *
     * @param tradeId 订单编码
     * @param stage   阶段
     * @param cost    耗时，单位毫秒
     */
    public static void log(String tradeId, String stage, long cost) {
        try {
            if (BizEnv.getEnvBoolean("trade.stage.trace", true)) {
                log.info(String.format("tradeId=%s, stage=%s, cost=%sms", tradeId, stage, String.valueOf(cost)));
            }
        } catch (Exception e) {
            // ignore
        }
    }

    public static void log(String tradeId, String stage, String time) {
        try {
            if (BizEnv.getEnvBoolean("trade.stage.trace", true)) {
                log.info(String.format("tradeId=%s, stage=%s, time=%s", tradeId, stage, time));
            }
        } catch (Exception e) {
            // ignore
        }
    }

    public static void log(String tradeId, String msg) {
        try {
            if (BizEnv.getEnvBoolean("trade.stage.trace", true)) {
                log.info(String.format("tradeId=%s, %s", tradeId, msg));
            }
        } catch (Exception e) {
            // ignore
        }
    }
}