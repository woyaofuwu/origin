package com.asiainfo.veris.crm.order.soa.group.destroygroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser.DestroyGroupUser;

/**
 * 集团电子流量包新增产品成员注销类
 * @author chenzg
 *
 */
public class DestroyGgvpGroupUser extends DestroyGroupUser
{
    public DestroyGgvpGroupUser()
    {

    }
    
    /**
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        String custId = this.reqData.getUca().getCustId();
        IData param = new DataMap();
        param.put("CUST_ID", custId);
        IDataset ds = CSAppCall.call("SS.GprsSaleForGrpSVC.queryOrderedPackge", param);
        if(IDataUtil.isNotEmpty(ds))
        {
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "该集团用户存在未失效或未充值的流量包，不能注销！");
        }
    }
    
    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
    }
}
