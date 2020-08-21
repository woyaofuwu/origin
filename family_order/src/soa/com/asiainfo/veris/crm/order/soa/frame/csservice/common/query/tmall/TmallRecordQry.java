package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tmall;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TmallRecordQry {
	private static final long serialVersionUID = 1L;
	/**
	 * 根据条件获取天猫对账差异记录
	 * @param tradeDate
	 * @param tel
	 * @param page
	 * @return
	 * @throws Exception
	 * @author zhuweijun
	 */
	public static IDataset queryData(String tradeDate,String tel,Pagination page)throws Exception{
		 IData param = new DataMap();
		 IDataset list=new DatasetList();
		 param.put("SETTLEDATE", tradeDate);
		 param.put("IDVALUE",tel);
		 //手机号码不为空，交易日期为空时
		 if(!tel.equals("") && tradeDate.equals("")){
			 list= Dao.qryByCode("TO_O_TMALLACCOUNTDIFFER", "SEL_TMALLACCOUNTDIFFER_DATA", param, page, Route.CONN_CRM_CEN); 
		 }
		 //如果手机号码为空，交易日期不为空时执行
		 if(tel.equals("") && !tradeDate.equals("")){
			 list= Dao.qryByCode("TO_O_TMALLACCOUNTDIFFER", "SEL_TMALLACCOUNTDIFFER_DATA1", param, page, Route.CONN_CRM_CEN);	 

		 }
		 //如果手机号码和交易日期都不为空时执行
		 if(!tel.equals("") && !tradeDate.equals("")){
			 list= Dao.qryByCode("TO_O_TMALLACCOUNTDIFFER", "SEL_TMALLACCOUNTDIFFER_DATA2", param, page, Route.CONN_CRM_CEN);	 

		  }
		 return list;
	}
}
