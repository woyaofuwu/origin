package com.asiainfo.veris.crm.order.soa.person.busi.suppliercharge;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;

public class QueryMobileShopManagerTask extends ExportTaskExecutor{

	@Override
    public IDataset executeExport(IData data, Pagination page) throws Exception
    {
		IData param = new DataMap();
		String shopName = data.getString("query_SHOP_NAME");
		String startDate = data.getString("query_START_DATE");
		String endDate = data.getString("query_END_DATE");
		String corpName = data.getString("query_CORP_NAME");
		String areaCode = data.getString("query_AREA_CODE");
		startDate=startDate.replace("-", "");
		endDate=endDate.replace("-", "");
		param.put("START_TIME", startDate);
		param.put("END_TIME", endDate);
		param.put("SHOP_NAME", shopName);
		param.put("CORP_NAME", corpName);
		param.put("AREA_CODE", areaCode);
		param.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
		IDataset dataset = Dao.qryByCodeParser("TF_R_FEE_MARKET", "SEL_MOBILE_SHOP", param,Route.getCrmDefaultDb());
		if(dataset != null){
			for (Object object : dataset) {
				IData inData = (IData)object;
				String corpNo = inData.getString("CORP_NO");
				String shopNo = inData.getString("SHOP_NO");
				if(StringUtils.isNotBlank(corpNo)){
					inData.put("CORP_NAME", getCorpName("MC",corpNo));
				}
				if(StringUtils.isNotBlank(shopNo)){
					inData.put("SHOP_NAME", getCorpName("M",shopNo));
				}				
			}
		}
		return dataset;
    }
	public String getCorpName(String resTypeId,String corpNo)throws Exception{
		IDataset dataset = ResCall.querySupplierTypeRel(resTypeId, corpNo);
		if(dataset != null && dataset.size() > 0){
			return dataset.getData(0).getString("CORP_NAME");
		}
		return "";
	}
}
