
package com.asiainfo.veris.crm.order.soa.frame.bcf.tradectrl;

import com.ailk.cache.localcache.CacheFactory;
import com.ailk.cache.localcache.interfaces.IReadOnlyCache;

public final class TradeCtrl
{
    public static enum CTRL_TYPE
    {
        IS_PS_YESORNO("U"), // 不确定是否发服务开通，通过规则判断
        IS_PF_NO("N"), // 确定不发服务开通
        IS_PF_YES("Y"), // 确定发服务开通
        IS_PF("isPf"), // 是否服务开通
        IS_FINANCE("is_finance"),  //是否支持财务化BOSS true支持 其它不支持
        IS_PRIORYTYOPEN("is_priorytyopen"),//是否aee优先队列模式开启
        IS_PRIORYTYTRADE("is_priorytytrade"),//是否本业务支持优先队列模式
        PRIORYTYQUELEN("priorytyquelen"),//容许优先队列长度

        IS_FEEITEM_TAX("isFeeitemTax"), // 是否从TD_B_FEEITEM_TAX获取税率

        NEED_PF("1"), // 子台账确定发pf
        NOT_NEED_PF("0"), // 子台账确定不发pf
        PF_WAIT("pfWait"), // 是否开闭环

        SMS_PAY("PaySms"), // 成员代付关系变更二次短信
        SMS_PAY_REPLY_LIMIT("PaySmsReplyLimit"), // 成员代付关系变更二次短信回复时间限制

        SMS_TC("smsTC"), // 是否需要发送二次短信
        SMS_TC_REPLY_LIMIT("smsTCReplyLimit"), // 成员代付关系变更二次短信回复时间限制

        TRADE_BACK("tradeBack"), // 是否备份
        TRADE_ENTRY("tradeEntry"), // 是否调客户接口

        TRADE_REG_FINISH("tradeRegFinish"), // 是否登记时完工
        TRADE_REG_LOCK("tradeRegLock"), // 业务登记是否加锁
        TRADE_REG_LIMIT("tradeRegLimit"), // 业务登记限制

        TRADE_FINISH_MOVE_SECOND("tradeSecond"), // 业务完工move到second表

        TRADE_SYNC_CREDIT("tradeSyncCredit"), // 是否同步信用度
        TRADE_SYNC_RECV("tradeSyncRecv"), // 是否同步营业费
        TRANS_RECVFEE("transRecvFee"), // 账务转账
        SAME_TRADETYPE_DEPEND("sameTradeTypeDepend"), // 同类型工单依赖，子单依赖主工单
        TRADE_SYNC_TRANS_FEE("tradeSyncTransFee"), //
        TRANS_FMYACCT("fmyAcctTrans"), // 是否调用账务家庭账户支付转账接口
        IS_PRT_SINGLE("isPrtSingle"), // 是否单独打印受理单
        SHOPPING_TAG("shoppingTag"),// 是否允许加入购物车
        IS_CARD_SYNC_TO_RES("isCardSyncToRes"),//是否将售卡的信息同步给资源

        SN_BLOCK_LIMIT_ZD("snBlockLimitZD"),//主动业务受限
        SN_BLOCK_LIMIT_BD("snBlockLimitBD");//被动业务不受限
        
        private final String value;

        private CTRL_TYPE(String value)
        {

            this.value = value;
        }

        public String getValue()
        {

            return value;
        }
    }

    /**
     * 获取控制信息
     * 
     * @param tradeTypeCode
     * @param paramName
     * @return
     * @throws Exception
     */
    private static Object getCtrl(String tradeTypeCode, CTRL_TYPE paramName) throws Exception
    {
        IReadOnlyCache cache = CacheFactory.getReadOnlyCache(TradeCtrlCache.class);

        return cache.get(tradeTypeCode + paramName.getValue());
    }

    /**
     * 获取Boolean类型的控制信息
     * 
     * @param tradeTypeCode
     * @param paramName
     * @param defaultValue
     * @return
     * @throws Exception
     */
    public static Boolean getCtrlBoolean(String tradeTypeCode, CTRL_TYPE paramName, boolean defaultValue) throws Exception
    {
        Object value = getCtrl(tradeTypeCode, paramName);

        if (value == null)
        {
            return defaultValue;
        }

        return (Boolean) value;
    }

    /**
     * 获取Int类型的控制信息
     * 
     * @param tradeTypeCode
     * @param paramName
     * @param defaultValue
     * @return
     * @throws Exception
     */
    public static int getCtrlInt(String tradeTypeCode, CTRL_TYPE paramName, int defaultValue) throws Exception
    {

        Object value = getCtrl(tradeTypeCode, paramName);

        if (value == null)
        {
            return defaultValue;
        }

        return Integer.parseInt((String.valueOf(value)));
    }

    /**
     * 获取String类型的控制信息
     * 
     * @param tradeTypeCode
     * @param paramName
     * @param defaultValue
     * @return
     * @throws Exception
     */
    public static String getCtrlString(String tradeTypeCode, CTRL_TYPE paramName, String defaultValue) throws Exception
    {

        Object value = getCtrl(tradeTypeCode, paramName);

        if (value == null)
        {
            return defaultValue;
        }

        return String.valueOf(value);
    }
}
