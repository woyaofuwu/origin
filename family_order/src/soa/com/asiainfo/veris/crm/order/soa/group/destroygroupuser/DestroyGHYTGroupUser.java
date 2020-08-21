package com.asiainfo.veris.crm.order.soa.group.destroygroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser.DestroyGroupUser;

/**
 * 集团海洋通产品注销类
 * @author chenzg
 *
 */
public class DestroyGHYTGroupUser extends DestroyGroupUser
{
    public DestroyGHYTGroupUser()
    {

    }
    
    /**
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        String userId = this.reqData.getUca().getUserId();
        IData param = new DataMap();
        param.put("USER_ID", userId);
        //产品存在欠费时，需结清欠费方能注销产品(规则CheckOweFeeForGrp.java实现)        
        //产品存在集团统付关系时，不允许注销产品(规则CheckPayRelaAdv.java实现)
        //存在活动时，不允许注销产品
        IDataset ds = SaleActiveInfoQry.getUserSaleActiveInfo(userId);
        if(IDataUtil.isNotEmpty(ds))
        {
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "该集团用户存在有效营销活动，不能注销！");
        }
    }
    
    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
    }
}
