
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class GGCardInfoQry
{

    /**
     * 根据卡号获取刮刮卡信息
     * 
     * @description
     * @author xiaobin
     */
    public static IDataset getGGCardInfoByCardNo(String cardNo, String CardTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("CARD_NO", cardNo);
        param.put("CARD_KIND_CODE", CardTypeCode);
        return Dao.qryByCode("TF_R_KKKL_GGCARD", "SEL_BY_CARDNO", param);
    }

    public static IData getGGCardInfoByPK(String cardCode, String cardPassWord, String processTag) throws Exception
    {
        IData cond = new DataMap();
        cond.put("CARD_CODE", cardCode);
        cond.put("CARD_PASS_WORD", cardPassWord);
        cond.put("PROCESS_TAG", processTag);

        IDataset result = Dao.qryByCode("TF_RH_VALUECARD", "SEL_BY_PK_GGCARD", cond);
        return result.size() > 0 ? result.getData(0) : new DataMap();
    }

    /**
     * 获取短信发送号码本月已发送兑奖短信次数
     * 
     * @description
     * @author chenzg
     * @date 2011-3-30
     */
    public static IDataset getSerialnumberSendSmsCounts(String serialNum) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serialNum);
        return Dao.qryByCode("TF_B_CHANGETRADE", "SEL_CURMONTH_EXCHANGECOUNT", data);
    }

    /**
     * 获取兑奖号码本月已中奖次数
     * 
     * @description
     * @author chenzg
     * @date 2011-3-30
     */
    public static IDataset getSnExchangeSucCounts(String serialNum) throws Exception
    {
        IData data = new DataMap();
        data.put("CHANGE_SERIAL_NUMBER", serialNum);
        return Dao.qryByCode("TF_B_CHANGETRADE", "SEL_CURMONTH_EXCHANGECOUNT_SUC", data);
    }

    /**
     * 获取有效的刮刮卡信息
     * 
     * @description
     * @author xiaobin
     */
    public static IDataset getValidGGCardInfoByCardNo(String cardNo) throws Exception
    {
        IData param = new DataMap();
        param.put("CARD_NO", cardNo);
        return Dao.qryByCode("TF_R_KKKL_GGCARD", "SEL_VALIDCARD_BY_CARDNO", param);
    }
    
    public static IDataset getGGCardInfoByCardPassWord(String giftCode) throws Exception
    {
        IData data = new DataMap();
        data.put("GIFT_CODE", giftCode);
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT * ");
        parser.addSQL(" FROM TF_R_GGCARD WHERE 1=1 ");
        parser.addSQL(" AND CARD_PASS_WORD = :GIFT_CODE ");
        parser.addSQL(" AND SYSDATE BETWEEN START_DATE AND END_DATE ");
        IDataset dataset = Dao.qryByParse(parser);
        return dataset;
    }
}
