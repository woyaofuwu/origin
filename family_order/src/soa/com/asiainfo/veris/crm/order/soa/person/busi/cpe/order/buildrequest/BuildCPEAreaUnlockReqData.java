
package com.asiainfo.veris.crm.order.soa.person.busi.cpe.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.cpe.order.requestdata.CPEAreaUnlockReqData; 

public class BuildCPEAreaUnlockReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
    	CPEAreaUnlockReqData rd=(CPEAreaUnlockReqData) brd;
    	rd.setRemark(param.getString("REMARK"));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new CPEAreaUnlockReqData();
    }

}
