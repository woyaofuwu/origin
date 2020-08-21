
package com.asiainfo.veris.crm.order.soa.person.busi.plat.feeprotect.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.plat.feeprotect.order.requestdata.FeeProtectReqData;

public class FeeProtectTrade extends BaseTrade implements ITrade
{	
	public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
		FeeProtectReqData reqdata = (FeeProtectReqData) btd.getRD();
		IDataset selectedElements = new DatasetList(reqdata.getPageRequestData().getString("SELECTED_ELEMENTS"));;
		
		if(IDataUtil.isNotEmpty(selectedElements)){
			for (int i = 0; i < selectedElements.size(); i++) {
				IData selectedElement = selectedElements.getData(i);
		    	DiscntTradeData discntData = new DiscntTradeData();
		    	
		    	discntData.setProductId("-1");
		    	discntData.setRemark("增值业务计费安全保护开关");
		    	discntData.setPackageId("99992222");
		    	discntData.setElementId(selectedElement.getString("ELEMENT_ID"));
		    	discntData.setModifyTag(selectedElement.getString("MODIFY_TAG"));
		    	discntData.setInstId(selectedElement.getString("MODIFY_TAG").equals("0")?SeqMgr.getInstId():selectedElement.getString("INST_ID"));
		    	discntData.setStartDate(selectedElement.getString("MODIFY_TAG").equals("0")?SysDateMgr.getSysTime():selectedElement.getString("START_DATE"));
		    	discntData.setEndDate(selectedElement.getString("MODIFY_TAG").equals("0")?SysDateMgr.getTheLastTime():SysDateMgr.getSysTime());
		    	discntData.setUserIdA("-1");
		    	discntData.setSpecTag("1");
		    	discntData.setRsrvStr1("feeprotect");
		    	discntData.setUserId(btd.getRD().getUca().getUserId());
		    	
		    	btd.add(reqdata.getUca().getSerialNumber(), discntData);
			}
		}
    }
}
