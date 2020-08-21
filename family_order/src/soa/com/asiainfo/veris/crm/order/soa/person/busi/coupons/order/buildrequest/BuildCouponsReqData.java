
package com.asiainfo.veris.crm.order.soa.person.busi.coupons.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.coupons.order.requestdata.CouponsReqData; 

public class BuildCouponsReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub 
    	CouponsReqData tikRd=(CouponsReqData)brd;  
    	tikRd.setSpendValue(param.getString("SPEND_VALUE"));
    	tikRd.setTicketValue(param.getString("TICKET_VALUE"));
    	tikRd.setTicketCode(param.getString("TICKET_CODE"));
    	tikRd.setRepairNO(param.getString("REPAIR_NO"));
    	tikRd.setRemark(param.getString("REMARK"));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new CouponsReqData();
    }

}
