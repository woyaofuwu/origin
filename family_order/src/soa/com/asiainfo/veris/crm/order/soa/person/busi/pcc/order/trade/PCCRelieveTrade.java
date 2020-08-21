package com.asiainfo.veris.crm.order.soa.person.busi.pcc.order.trade;

import java.util.List;
import java.util.UUID;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.client.request.Wade3ClientRequest;
import com.ailk.service.server.hessian.wade3tran.Wade3DataTran;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.person.busi.pcc.order.requestdata.PCCRelieveReqData;

public class PCCRelieveTrade extends BaseTrade implements ITrade {

	public void createBusiTradeData(BusiTradeData btd) throws Exception {
		
		//处理主台账数据
		PCCRelieveReqData PCCRelieveReqData = (PCCRelieveReqData) btd.getRD();
		MainTradeData mainTradeData = btd.getMainTradeData();
		String relieveFlag = PCCRelieveReqData.getRelieveFlag();
		String remark = "";
		String usrStatus = "";
		if ("0".equals(relieveFlag)) {
			remark = "解速：" + PCCRelieveReqData.getRemark();
			usrStatus = "1";
		} else if ("1".equals(relieveFlag)) {
			remark = "一级限速：" + PCCRelieveReqData.getRemark();
			usrStatus = "2";
		} else if ("2".equals(relieveFlag)) {
			remark = "二级限速：" + PCCRelieveReqData.getRemark();
			usrStatus = "3";
		} else {
			CSAppException.appError("2018060401", "没有此操作类型！");
		}

		mainTradeData.setRemark(remark);
		mainTradeData.setRsrvStr1(relieveFlag);

		String id = SysDateMgr.getSysDateYYYYMMDD()
				+ UUID.randomUUID().toString().toUpperCase().replace("-", "")
						.substring(0, 20);

		mainTradeData.setRsrvStr2(id);
		

		//调用一级boss同步网维
		String user_identifier = "86" + mainTradeData.getSerialNumber();
		IData ibossData = new DataMap();
		ibossData.put("OPER_CODE", "02");
		ibossData.put("PCC_USR_IDENT", user_identifier);
		ibossData.put("PCC_USR_STATUS", usrStatus);
		ibossData.put("BASS_FLOW_ID", id);

		ibossData.put("KIND_ID", "BOSS_PCC_PCRF01_0");

		IData stopResult = new DataMap();

		String url = BizEnv.getEnvString("crm.PCCRELIEVE");
		stopResult = callIBOSS("IBOSS", ibossData,url);
		
		
		//插入PCC表
		if (IDataUtil.isNotEmpty(stopResult)) {
			if (stopResult.getString("X_RSPCODE").equals("0000")) {
				String month = SysDateMgr.getTheMonth(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD));
				String userId = mainTradeData.getUserId();
				String rsrv_str10 = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_SHORT);
				
				IData param = new DataMap();
				param.put("EPARCHY_CODE", "0898");
				param.put("USR_IDENTIFIER", user_identifier);
				param.put("BASS_FLOW_ID", id);
				param.put("ACCEPT_MONTH", month);
				param.put("IN_DATE", SysDateMgr.getSysTime());
				param.put("OPERATION_TYPE", "U");
				param.put("USER_ID", userId);
				param.put("USR_STATUS", usrStatus);
				param.put("EXEC_TYPE", "1");// 1单个，0批量
				param.put("EXEC_STATE", "2");
				param.put("RSRV_STR5", "1");
				param.put("RSRV_STR6", "1");
				param.put("RSRV_STR9", "OK!");
				param.put("RSRV_STR10", rsrv_str10);
				param.put("EXEC_TIME", SysDateMgr.getSysTime());
				Dao.insert("TI_O_PCC_SUBSCRIBER", param, Route.CONN_CRM_CEN);
				
			}else{
				CSAppException.appError("2018060402", stopResult.getString("X_RSPDESC", "unknow error"));
			}
		}else
		{
			CSAppException.appError("2018060403", "调用一级boss出错");
		}	
	}
	
	private IData callIBOSS(String svcName, IData data,String url) throws Exception
    { 
        String inparams = Wade3DataTran.toWadeString(data);

        String out = Wade3ClientRequest.request(url, svcName, inparams, "GBK");
        List list = Wade3DataTran.strToList(out);

        IDataset dataset = Wade3DataTran.wade3To4Dataset(list);
        return dataset.getData(0);
    }
}
