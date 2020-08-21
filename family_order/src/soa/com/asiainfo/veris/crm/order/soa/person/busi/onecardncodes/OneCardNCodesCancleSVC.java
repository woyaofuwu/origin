
package com.asiainfo.veris.crm.order.soa.person.busi.onecardncodes;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.SimCardBean;
import com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.SimCardQueryBean;

public class OneCardNCodesCancleSVC extends CSBizService
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public IDataset checkInfos(IData input) throws Exception
    {
        OneCardNCodesCancleBean bean = (OneCardNCodesCancleBean) BeanManager.createBean(OneCardNCodesCancleBean.class);
        // 获取老卡资源数据
        SimCardBean cardBean = (SimCardBean) BeanManager.createBean(SimCardBean.class);
        IData resInfo = cardBean.getUserResource(input);
        resInfo.putAll(cardBean.getUserSvcState(input));
        resInfo.putAll(SimCardQueryBean.getSimCardInfo(resInfo.getString("SIM_CARD_NO"), input));
        // 查询主副卡关系
        IDataset otherData = bean.checkInfos(input, resInfo);

        return otherData;
    }
}
