
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidenetdestroy.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidenetdestroy.order.requestdata.DestroyNoPhoneWideUserNowRequestData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestroynew.order.buildrequest.BuildDestroyUserNowRequestData;

public class BuildDestroyNoPhoneWideUserNowRequestData extends BuildDestroyUserNowRequestData implements IBuilder
{
	
    public void buildBusiRequestData(IData data, BaseReqData brd) throws Exception
    {
        super.buildBusiRequestData(data, brd);
        
        DestroyNoPhoneWideUserNowRequestData reqData = (DestroyNoPhoneWideUserNowRequestData) brd;
        
        reqData.setCreateWideUserTradeId(data.getString("CREATE_WIDEUSER_TRADE_ID", ""));
        
        reqData.setRemark(data.getString("REMARK", ""));
    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new DestroyNoPhoneWideUserNowRequestData();
    }

}
