
package com.asiainfo.veris.crm.order.soa.person.busi.onecardncodes;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

public class OneCardNCodesChangeCardRemoteSVC extends CSBizService
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public IDataset getOtherSNInfo(IData input) throws Exception
    {
        String sn = input.getString("SERIAL_NUMBER");

        OneCardNCodesChangeCardRemoteBean bean = (OneCardNCodesChangeCardRemoteBean) BeanManager.createBean(OneCardNCodesChangeCardRemoteBean.class);
        return bean.getOtherSNInfo(sn);
    }

    public IDataset getResInfo(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        IDataset userResInfo = UserResInfoQry.queryUserResByUserIdResType(userId, "1");
        return userResInfo;
    }

    public IData writeCard(IData input) throws Exception
    {
        String sn = input.getString("SERIAL_NUMBER");

        OneCardNCodesChangeCardRemoteBean bean = (OneCardNCodesChangeCardRemoteBean) BeanManager.createBean(OneCardNCodesChangeCardRemoteBean.class);
        return null;
    }

}
