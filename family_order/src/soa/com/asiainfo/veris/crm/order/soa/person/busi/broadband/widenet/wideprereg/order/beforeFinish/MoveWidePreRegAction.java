package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.wideprereg.order.beforeFinish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.wideprereg.WidePreRegBean;

public class MoveWidePreRegAction implements ITradeFinishAction{

	
	public void executeAction(IData mainTrade) throws Exception {
		
		IData param = new DataMap();
        String eparchyCode = mainTrade.getString("TRADE_EPARCHY_CODE");
        //登记状态
        WidePreRegBean bean = BeanManager.createBean(WidePreRegBean.class);
        IData input = new DataMap();
        input.put("HOME_ADDR", mainTrade.getString("RSRV_STR1"));
        IDataset widepreregInfo = bean.getPreRegInfosByFiveAddr(input);
        if(DataSetUtils.isBlank(widepreregInfo)){
        	param.put("RSRV_TAG3","1");//未开通
        }else{
        	String resStatus = widepreregInfo.first().getString("RSRV_TAG3");
        	if("4".equals(resStatus) || "5".equals(resStatus) || "6".equals(resStatus)){
        		//此标准地址宽带需求收集用户登记状态表示已支持开通，若用户再预登记，表示该标准地址端口已用完，需重新预警
        		param.put("RSRV_TAG3","1");//未开通
        	}else{
        		param.put("RSRV_TAG3",widepreregInfo.first().getString("RSRV_TAG3","1"));
            	param.put("RSRV_DATE2",widepreregInfo.first().getString("RSRV_DATE2",""));
        	}
        }
        //四级地址编码，每一级地址之间用 "," 分隔
        param.put("RSRV_STR1",mainTrade.getString("RSRV_STR10"));
        //地区
        param.put("RSRV_STR3",mainTrade.getString("RSRV_STR8"));
        //宽带带宽
        param.put("RSRV_TAG2",mainTrade.getString("RSRV_STR4"));
        //预装原因
        param.put("RSRV_STR4",mainTrade.getString("RSRV_STR6"));
        //用户手机号码
        param.put("RSRV_STR2",mainTrade.getString("SERIAL_NUMBER"));
     	//装机详细地址
        param.put("SET_ADDR",mainTrade.getString("RSRV_STR2"));
        //联系人姓名
        param.put("CUST_NAME", mainTrade.getString("RSRV_STR9"));
        //联系电话
        param.put("CONTACT_SN", mainTrade.getString("SERIAL_NUMBER_B"));
        //装机四级详细地址
        param.put("HOME_ADDR", mainTrade.getString("RSRV_STR1"));
        param.put("AUDIT_STATUS", "1");
        param.put("INST_ID", SeqMgr.getInstId(eparchyCode));
        param.put("REG_DATE", mainTrade.getString("UPDATE_TIME"));
        param.put("UPDATE_STAFF_ID",  mainTrade.getString("UPDATE_STAFF_ID"));
        param.put("UPDATE_DEPART_ID", mainTrade.getString("UPDATE_DEPART_ID"));
        Dao.insert("TF_F_WIDENET_BOOK", param, eparchyCode);
		
	}
	

	
}
