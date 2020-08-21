package com.asiainfo.veris.crm.order.soa.group.destroygroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElementReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser.DestroyGroupUser;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser.DestroyGroupUserReqData;

public class DestroyIdcUser extends DestroyGroupUser
{
    protected DestroyGroupUserReqData reqData = null;
    protected String idcUserId = null;
    public DestroyIdcUser()
    {

    }
    protected void initReqData() throws Exception
    {
        super.initReqData();
        reqData = (DestroyGroupUserReqData) getBaseReqData();
        
    }
    
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        
//        idcParm=map.getData("IDCPARAM");
//        if(map.get("IDCPARAMLIST")!=null){
//            idcParmList=map.getDataset("IDCPARAMLIST");
//
//        }
        idcUserId=map.getString("USER_ID");
    }
    
    /**
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
    }
    

}
