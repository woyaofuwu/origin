
package com.asiainfo.veris.crm.order.soa.person.busi.modifysaleactiveimei.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.modifysaleactiveimei.order.requestdata.ModifySaleActiveIMEIReqData;

public class BuildModifySaleActiveIMEIReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        ModifySaleActiveIMEIReqData msaird = (ModifySaleActiveIMEIReqData) brd;
        msaird.setOldIMEI(param.getString("OLD_RES_CODE"));
        msaird.setNewIMEI(param.getString("NEW_RES_CODE"));
        msaird.setRelationTradeId(param.getString("RELATION_TRADE_ID"));
        msaird.setCheckTradeId(param.getString("CHECK_TRADE_ID")); /*REQ201712040014销户业务限制查询新增*/
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new ModifySaleActiveIMEIReqData();
    }
}
