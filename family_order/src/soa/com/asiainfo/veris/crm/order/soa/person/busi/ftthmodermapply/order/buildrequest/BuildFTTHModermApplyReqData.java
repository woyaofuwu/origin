
package com.asiainfo.veris.crm.order.soa.person.busi.ftthmodermapply.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.ftthmodermapply.order.requestdata.FTTHModermApplyReqData;

public class BuildFTTHModermApplyReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
    	FTTHModermApplyReqData fttRd=new FTTHModermApplyReqData();
    	fttRd.setRemark(param.getString("REMARK"));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new FTTHModermApplyReqData();
    }

}
