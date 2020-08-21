
package com.asiainfo.veris.crm.order.soa.person.busi.singlenummultidevicemanage.order.action.reg;


import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.singlenummultistatuschange.OneNoMultiTerminalConstants;

/**
 * 一号多终端主号销户同时连带副号销户
 * @author yuyj3
 *
 */
public class DealAuxNumInPriNumDestroyUserAction implements ITradeAction
{

	public void executeAction(BusiTradeData btd) throws Exception
    {
		//查询销户用户是否是一号多终端主号码
		IData inParam = new DataMap();
    	inParam.put("USER_ID", btd.getRD().getUca().getUserId());
    	inParam.put("RELATION_TYPE_CODE",OneNoMultiTerminalConstants.RELATION_TYPE_CODE);
		
		//所有有效的一号多终端记录
		IDataset allOneNoMultiTerMebs = RelaUUInfoQry.qryRelationList(inParam);
		
		if (IDataUtil.isNotEmpty(allOneNoMultiTerMebs))
		{
			for (int i = 0; i < allOneNoMultiTerMebs.size(); i++) 
			{
				IData oneNoMultiTerMeb = allOneNoMultiTerMebs.getData(i);
				
				
				IData busiInputData = new DataMap();
				//
				busiInputData.put("SERIAL_NUMBER", oneNoMultiTerMeb.getString("SERIAL_NUMBER_A"));
				busiInputData.put("SERIAL_NUMBER_B", oneNoMultiTerMeb.getString("SERIAL_NUMBER_B"));
				
				busiInputData.put("ORDER_TYPE_CODE", "396");
				busiInputData.put("TRADE_TYPE_CODE", "396");
				
				// 操作类型：01-业务添加 02-业务删除 04-业务暂停 05-业务恢复
				busiInputData.put("OPER_CODE", "02");
				
				CSAppCall.call("SS.SingleNumMultiDeviceManageRegSVC.tradeReg", busiInputData);
				
			}
		}
    }
}
