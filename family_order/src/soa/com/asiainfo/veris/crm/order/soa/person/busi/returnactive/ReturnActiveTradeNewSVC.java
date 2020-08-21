
package com.asiainfo.veris.crm.order.soa.person.busi.returnactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ReturnActiveTradeNewSVC extends CSBizService
{

    public IDataset getResInfo(IData data) throws Exception
    {
        String tradeTypeCode = data.getString("TRADE_TYPE_CODE");
        String cardCode_s = data.getString("CARDCODE_S");
        String cardCode_e = data.getString("CARDCODE_E");

        return ReturnActiveUtilBean.getResInfo(tradeTypeCode, cardCode_s, cardCode_e);
    }

    /**
     * 得到可查询次数
     */
    public IData getReturnActiveNewInfo(IData data) throws Exception
    {
        // ReturnActiveUtilBean bean = BeanManager.createBean(ReturnActiveUtilBean.class);
        return ReturnActiveUtilBean.getReturnActiveNewInfo(data);
    }

}
