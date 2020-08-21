package com.asiainfo.veris.crm.order.soa.person.busi.internetofthings.order.requestdata;

import java.util.ArrayList;
import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
/**
 * PCRF策略变更请求数据
 * 
 * @author huping
 */
public class DealPcrfTacticsReqData extends BaseReqData
{
	List<PcrfTacticData> pcrfList = new ArrayList<PcrfTacticData>();
	
	public List<PcrfTacticData> getPcrfList() {
			return pcrfList;
	}

	public void setPcrfList(List<PcrfTacticData> pcrfList) {
		this.pcrfList = pcrfList;
	}
}
