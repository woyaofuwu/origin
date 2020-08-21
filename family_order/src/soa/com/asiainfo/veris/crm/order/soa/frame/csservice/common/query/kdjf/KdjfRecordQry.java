package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.kdjf;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class KdjfRecordQry {
	private static final long serialVersionUID = 1L;
	/**
	 * 根据条件获取宽带缴费对账差异记录
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
			 list= Dao.qryByCode("TI_B_KDJF_RECON", "SEL_KFJF_RECON_RESULT", param, page);	 
		  }
		 return list;
	}
}
