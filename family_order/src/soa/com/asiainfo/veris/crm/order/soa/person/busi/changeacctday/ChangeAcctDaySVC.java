
package com.asiainfo.veris.crm.order.soa.person.busi.changeacctday;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.AcctDayException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class ChangeAcctDaySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 获取个人业务账期生效方式【0-立即生效，1-下周期生效】
     * 
     * @param pd
     * @return
     * @throws Exception
     */
    private String getAcctDayChgModeByPerson() throws Exception
    {
        String chgMode = "";
        // 根据ACCT_CHG_MODE_PERSON获取相应的账期生效方式配置信息
        IDataset returnDataset = CommparaInfoQry.getCommparaAllColByParser("CSM", "1004", "ACCT_CHG_MODE_PERSON", CSBizBean.getUserEparchyCode());
        if (returnDataset.isEmpty())
        {
            CSAppException.apperr(AcctDayException.CRM_ACCTDAY_20);
        }
        else
        {
            chgMode = returnDataset.getData(0).getString("PARA_CODE1");
        }
        return chgMode;
    }

    /**
     * 获取新结账日下新的账期数据
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getNewAcctDayByModify(IData input) throws Exception
    {
        input.put("NOW_DATE", "");// 当前时间
        input.put("CHG_MODE", getAcctDayChgModeByPerson());// 下账期生效

        IDataset returnNewAcctData = DiversifyAcctUtil.getNewAcctDaysByModi(input);

        if (returnNewAcctData != null && returnNewAcctData.size() > 0)
        {
            return returnNewAcctData;
        }
        else
        {
            CSAppException.apperr(AcctDayException.CRM_ACCTDAY_18);
            return null;
        }
    }
}
