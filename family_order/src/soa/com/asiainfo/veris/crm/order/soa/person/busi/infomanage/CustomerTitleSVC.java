
package com.asiainfo.veris.crm.order.soa.person.busi.infomanage;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.SvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

public class CustomerTitleSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CustomerTitleSVC.class);

    public void changeCustTitle(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        IDataUtil.chkParam(input, "PARAM_VALUE1");
        IData data = new DataMap();
        data.put("Cust_Title", input.getString("PARAM_VALUE1"));
        data.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));

        IDataset dataset = CSAppCall.call("SS.CustomerTitleRegSVC.tradeReg", data);

    }

    public void checkUserState(IData input) throws Exception
    {
        String userStateCode = input.getString("USER_STATE_CODESET", "");

        if (!"0".equals(userStateCode) && !"N".equals(userStateCode) && !"R".equals(userStateCode))
        {
            String stateName = SvcStateInfoQry.getUserStateName(input);
            CSAppException.apperr(CrmUserException.CRM_USER_676, stateName);
        }

    }

    // 查询用户其他信息，包含客户称谓信息
    public IDataset getUserOtherInfo(IData input) throws Exception
    {
        IDataset dataset = UserOtherInfoQry.getUserOtherUserId(input.getString("USER_ID"), "CTHN", null);

        return dataset;
    }

}
