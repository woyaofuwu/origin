package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createwideuser.order.action.finish;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;

/**
 * 处理地址预受理工单完成
 * @author Administrator
 *
 */
public class DealAddrPredealAction implements ITradeFinishAction{

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		String serialNumber = mainTrade.getString("SERIAL_NUMBER");
		if ("KD_".equals(serialNumber.substring(0, 3))){
	       serialNumber = serialNumber.substring(3);
	    }
		String statffId=mainTrade.getString("TRADE_STAFF_ID");
		String deptId=mainTrade.getString("TRADE_DEPART_ID");
		
		String staffName="";
		String deptName="";
		//查询受理员工名称
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM TD_M_STAFF T WHERE T.STAFF_ID=:STAFF_ID ");
		IData input = new DataMap();
		input.put("STAFF_ID", statffId);
		IDataset staffDatas=Dao.qryBySql(sql, input,Route.CONN_SYS); 
		if(staffDatas!=null&&staffDatas.size()>0){
			staffName=staffDatas.getData(0).getString("STAFF_NAME");
		}
		
		//查询受理部门名称
		StringBuilder sql1 = new StringBuilder();
		sql1.append("SELECT * FROM TD_M_DEPART T WHERE T.DEPART_ID=:DEPART_ID ");
		IData input1 = new DataMap();
		input1.put("DEPART_ID", deptId);
		IDataset deptDatas=Dao.qryBySql(sql1, input1,Route.CONN_SYS); 
		if(deptDatas!=null&&deptDatas.size()>0){
			deptName=deptDatas.getData(0).getString("DEPART_NAME");
		}
		
		//调pboos接口更新预受理地址状态
		IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("STAFF_ID", statffId);
        param.put("STAFF_NAME", staffName);
        param.put("DEPT_ID", deptId);
        param.put("DEPT_NAME", deptName);
        CSAppCall.call("PB.AddrPreDeal.updateAddrPreDealBySerialNumber", param);
	}

}
