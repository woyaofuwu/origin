
package com.asiainfo.veris.crm.order.soa.frame.bof.data.consts;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class PlatReload
{

    public static String mail139Standard = "";

    public static String mail139Free = "";

    public static String mail139Vip = "";

    public static String cmmbStandard = "";

    static
    {
        try
        {
            IData param = new DataMap();
            param.put("BIZ_CODE", "+MAILMF");
            //调用产商品接口
            //IDataset mailFrees = Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_139MAIL", param);
            IDataset mailFrees = UpcCall.qryOffersBySpCond(null, param.getString("BIZ_CODE"), "16");
            mail139Free = mailFrees.getData(0).getString("OFFER_CODE");

            param.put("BIZ_CODE", "+MAILBZ");
            //IDataset mailStandards = Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_139MAIL", param);
            IDataset mailStandards = UpcCall.qryOffersBySpCond(null, param.getString("BIZ_CODE"), "16");
            mail139Standard = mailStandards.getData(0).getString("OFFER_CODE");

            param.put("BIZ_CODE", "+MAILVIP");
            //IDataset mailVips = Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_139MAIL", param);
            IDataset mailVips = UpcCall.qryOffersBySpCond(null, param.getString("BIZ_CODE"), "16");
            mail139Vip = mailVips.getData(0).getString("OFFER_CODE");

            param.put("BIZ_CODE", "0001000000000001");
            //IDataset cmmbs = Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_CMMB", param);
            IDataset cmmbs = UpcCall.qryOffersBySpCond(null, param.getString("BIZ_CODE"), "53");
            cmmbStandard = cmmbs.getData(0).getString("OFFER_CODE");
        }
        catch (Exception e)
        {

        }
    }
}
