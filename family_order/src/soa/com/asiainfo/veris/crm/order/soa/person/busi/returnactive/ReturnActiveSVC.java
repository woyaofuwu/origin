
package com.asiainfo.veris.crm.order.soa.person.busi.returnactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;

public class ReturnActiveSVC extends CSBizService
{

    public IDataset checkExchangeCard(IData data) throws Exception
    {
        String cardCode = data.getString("CARD_CODE");
        String cardPassWord = data.getString("CARD_PASS_WORD");
        String sn = data.getString("SERIAL_NUMBER");

        IData user = UcaInfoQry.qryUserMainProdInfoBySn(sn);

        return ReturnActiveBean.checkExchangeCard(user.getString("USER_ID"), cardCode, cardPassWord, user.getString("PRODUCT_ID"));
    }

    public IData checkGGCardInfo(IData data) throws Exception
    {
        ReturnActiveBean bean = BeanManager.createBean(ReturnActiveBean.class);
        return null;
    }

    /**
     * 得到可查询次数
     */
    public IData getReturnActiveInfo(IData data) throws Exception
    {
        ReturnActiveBean bean = BeanManager.createBean(ReturnActiveBean.class);
        return bean.getReturnActiveInfo(data);
    }
}
