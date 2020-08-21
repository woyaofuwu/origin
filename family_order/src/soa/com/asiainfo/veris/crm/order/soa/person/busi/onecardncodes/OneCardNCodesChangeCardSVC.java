
package com.asiainfo.veris.crm.order.soa.person.busi.onecardncodes;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class OneCardNCodesChangeCardSVC extends CSBizService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    protected static Logger log = Logger.getLogger(OneCardNCodesChangeCardSVC.class);

    public IDataset changeCard(IData input) throws Exception
    {
        String sn = input.getString("SERIAL_NUMBER");

        OneCardNCodesChangeCardBean bean = (OneCardNCodesChangeCardBean) BeanManager.createBean(OneCardNCodesChangeCardBean.class);
        return bean.changeCard(sn);
    }

    public IDataset CheckNewSIM(IData input) throws Exception
    {
        String simcardNo = input.getString("SIM_CARD_NO");
        String snA = input.getString("SERIAL_NUMBER_A");
        String snB = input.getString("SERIAL_NUMBER_B");
        String oldSimcardNo = input.getString("commInfo_IMSIBAK");
        OneCardNCodesChangeCardBean bean = (OneCardNCodesChangeCardBean) BeanManager.createBean(OneCardNCodesChangeCardBean.class);
        return bean.CheckNewSIM(simcardNo,snA,snB,oldSimcardNo);
    }
}
