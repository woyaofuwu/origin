package com.asiainfo.veris.crm.order.soa.person.busi.tmall;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tmall.TmallRecordQry;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
public class TmallRecordBean extends CSBizBean{
	/**
	 * 根据条件查询天猫对账差异记录
	 * @param Datamap
	 * @param page
	 * @return
	 * @throws Exception
	 * @author zhuweijun
	 */
	public IDataset queryData(IData Datamap,Pagination page) throws Exception {
		IDataset list=new DatasetList();
		String tradeDate=Datamap.get("tradeDate").toString();//交易日期
		String tel=Datamap.get("tel").toString();//电话号码
        list=TmallRecordQry.queryData(tradeDate,tel,page);
		return list;
	}
   
}
