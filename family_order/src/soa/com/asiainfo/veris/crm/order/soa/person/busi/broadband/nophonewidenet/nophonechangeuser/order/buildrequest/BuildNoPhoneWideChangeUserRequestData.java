
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonechangeuser.order.buildrequest;
  
import com.ailk.common.data.IData; 
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonechangeuser.order.requestdata.NoPhoneWideChangeUserRequestData;
public class BuildNoPhoneWideChangeUserRequestData extends BaseBuilder implements IBuilder
{
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
    	NoPhoneWideChangeUserRequestData reqdata = (NoPhoneWideChangeUserRequestData) brd;
    	reqdata.setWideChangeInfo(param); 
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new NoPhoneWideChangeUserRequestData();
    }

}
