package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tyzf;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TyzfRecordQry {
	private static final long serialVersionUID = 1L;
	/**
	 * 根据条件获取统一支付对账差异记录
	 * @param tradeDate
	 * @param tel
	 * @param page
	 * @return
	 * @throws Exception
	 * @author chenbl6
	 */
	public static IDataset queryData(String tradeDate,Pagination page)throws Exception{
		 IData param = new DataMap();
		 IDataset list=new DatasetList();
		 param.put("SETTLEDATE", tradeDate);

		 //交易日期不为空时执行
		 if(!tradeDate.equals("")){
			 list= Dao.qryByCode("TI_B_TYZF_UPG_RECON_RESULT", "SEL_TI_B_TYZF_UPG_RECON_RESULT", param, page, Route.CONN_CRM_CEN);	 
		  }
		 return list;
	}
}
