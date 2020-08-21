
package com.asiainfo.veris.crm.order.soa.person.busi.ibossqryuserinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;

public class IbossForwardInfoSVC extends CSBizService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public IData ibossForward(IData param) throws Exception
    {
        // 海南特殊要求
        /*
         * ITF_CRM_IBossForwadIntf 接口： 1：新增个传入参数ATTR_STR5。在调用开始的时候，如果ATTR_STR5不为空，则把ATTR_STR5的值赋值给
         * ACTION_TIME，为空的时候不做操作。 2：新增个传入参数ATTR_STR6。在调用开始的时候，如果ATTR_STR6不为空，则把ATTR_STR6的值赋值给 CNL_TYP，为空的时候不做操作。
         * 3：新增一个返回字段：ACNTBALANCE。返回的时候它的值跟PAYED_BALANCE的值一样。
         */
        if (!"".equals(param.getString("ATTR_STR6", "")))
        {
            param.put("CNL_TYP", param.getString("ATTR_STR6"));
            param.put("ACTION_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
        }

        IDataset results = IBossCall.callHttpIBOSS("IBOSS", param);

        IData result = results.getData(0);
        result.put("ACNTBALANCE", result.getString("PAYED_BALANCE", ""));
        return result;

    }

}
