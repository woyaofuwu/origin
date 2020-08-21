
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.NPScoreCleanReqData;

public class BuildNPScoreCleanReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
		NPScoreCleanReqData rd= (NPScoreCleanReqData)brd;
		rd.setRemark(param.getString("REMARK"));
    }

	@Override
	public BaseReqData getBlankRequestDataInstance() {
		
		return new NPScoreCleanReqData();
	}



}
