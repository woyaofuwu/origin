package com.asiainfo.veris.crm.order.soa.person.busi.kdjf;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.kdjf.KdjfRecordQry;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
public class KdjfRecordBean extends CSBizBean{
	/**
	 * 根据条件统一支付对账差异记录
	 * @param Datamap
	 * @param page
	 * @return
	 * @throws Exception
	 * @author chenbl6
	 */
	public IDataset queryData(IData Datamap,Pagination page) throws Exception {
		IDataset list=new DatasetList();
		String tradeDate=Datamap.get("tradeDate").toString();//交易日期
		
        list=KdjfRecordQry.queryData(tradeDate,page);
		return list;
	}
   
}
