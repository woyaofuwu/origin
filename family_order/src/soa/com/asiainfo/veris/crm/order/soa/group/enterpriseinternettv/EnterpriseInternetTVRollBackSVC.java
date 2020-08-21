
package com.asiainfo.veris.crm.order.soa.group.enterpriseinternettv;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class EnterpriseInternetTVRollBackSVC extends GroupOrderService
{
    private static final long serialVersionUID = 1L;

    public IDataset crtTrade(IData inParam) throws Exception
    {
        EnterpriseInternetTVRollBackBean bean = new EnterpriseInternetTVRollBackBean();

        return bean.crtTrade(inParam);
    }
       
    
    
    public  IDataset checkResOtherInfo(IData data) throws Exception
    {
        String user_id = data.getString("USER_ID");
        return UserOtherInfoQry.getOtherInfoByCodeUserId(user_id,"EITV");
    }
       
}
