package com.asiainfo.veris.crm.order.soa.group.groupintf.intelligence.bean;

import com.ailk.bizservice.base.CSBizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.pub.consts.GroupStandardConstans;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;

public class ChannelIfnoQueryBean extends CSBizBean {
	private final static String CHANNEL_TYPE_ID ="IN_MODE_CODE_IN_CHNL";
	
	public IData channelIfnoQuery(IData inParams) throws Exception{
		String channelID = IDataUtil.chkParam(inParams, "channelID");
		IData channelDatas =  StaticInfoQry.getStaticInfoByTypeIdDataId(CHANNEL_TYPE_ID,channelID);
		IData retDs = new DataMap();
		retDs.put(GroupStandardConstans.RES_BIZ_CODE, GroupStandardConstans.RES_SUCCESS );
		retDs.put("channelID", channelID );
		retDs.put("channelName", channelDatas.getString("DATA_NAME") );
		retDs.put("channelType", channelDatas.getString("PDATA_ID") );
		
		return retDs;
	}
}
