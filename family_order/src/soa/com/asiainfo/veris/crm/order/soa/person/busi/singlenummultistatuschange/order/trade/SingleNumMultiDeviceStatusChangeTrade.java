package com.asiainfo.veris.crm.order.soa.person.busi.singlenummultistatuschange.order.trade;

import java.util.List;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.singlenummultistatuschange.order.requestdata.AuxDeviceData;
import com.asiainfo.veris.crm.order.soa.person.busi.singlenummultistatuschange.order.requestdata.SingleNumMultiDeviceStatusChangeReqData;

@SuppressWarnings("unchecked")
public class SingleNumMultiDeviceStatusChangeTrade extends BaseTrade implements ITrade {
	@Override
	public void createBusiTradeData(BusiTradeData btd) throws Exception {
		SingleNumMultiDeviceStatusChangeReqData reqData = (SingleNumMultiDeviceStatusChangeReqData) btd.getRD();
		
		List<AuxDeviceData> auxDeviceDataList = reqData.getAuxDeviceDataList();
		
		for (AuxDeviceData elem : auxDeviceDataList)
        {   
        	IData inputParam = new DataMap();
    		inputParam.put("USER_ID", btd.getRD().getUca().getUserId());
    		inputParam.put("RELATION_TYPE_CODE","OM");
    		inputParam.put("SERIAL_NUMBER_B", elem.getSerialNumberB());
    		inputParam.put("USER_ID_B", elem.getUserIdB());
    		inputParam.put("INST_ID", elem.getInstId());
    		
    		IDataset relationList=RelaUUInfoQry.qryRelationList(inputParam);
    		if(IDataUtil.isNotEmpty(relationList))
    		{
    			updateTradeRelation(btd,relationList,elem);
    		}
    		
        }    
		
	}
	
	/**
	 * 拼装关系表子台帐 TF_B_TRADE_RELATION
	 */
	public void updateTradeRelation(BusiTradeData btd,IDataset relationList,AuxDeviceData elem) throws Exception {
		
		SingleNumMultiDeviceStatusChangeReqData reqData = (SingleNumMultiDeviceStatusChangeReqData) btd.getRD();
		String serial_number = reqData.getSerialNumber();
		String oprFlag = reqData.getOprFlag();
		
		IData relation=relationList.getData(0);	
		
		// RSRV_NUM1 为1 表示服号前台主动暂停，只有 oprFlag为2  副设备前台恢复才能进行恢复
		if(("4".equals(oprFlag) || "6".equals(oprFlag)) && "1".equals(relation.getString("RSRV_NUM1")))
		{
			return ;
		}
		else
		{
			RelationTradeData relationTradeData = new RelationTradeData(relation);		
			relationTradeData.setRsrvNum1(elem.getChannlCode());//业务暂停方式：1--业务主动发起的业务暂停操作
			relationTradeData.setRsrvStr1(elem.getOprCode());//操作类型：01-业务添加 02-业务删除 04-业务暂停 05-业务恢复
			relationTradeData.setModifyTag("2");		
			btd.add(serial_number, relationTradeData);
		}
	}
	

	
}