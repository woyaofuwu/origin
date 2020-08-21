
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.grppayaccountedit;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public class GrpPayAccountEditHttpHandler extends CSBizHttpHandler
{
    /**
     * 作用： 根据账户ID查询账户信息
     */
    public void getAcctByActId() throws Exception
    {

        IData inpara = getData();
        String acctId = inpara.getString("ACCT_ID");
        if (StringUtils.isEmpty(acctId))
            return;
        String routeId = inpara.getString("ROUTE_ID");
        IData acctInfo = new DataMap();
        if (StringUtils.isNotBlank(acctId))
        {
            acctInfo = UCAInfoIntfViewUtil.qryGrpAcctInfoByAcctId(this, acctId);
        }
        this.setAjax(acctInfo);

    }

}
