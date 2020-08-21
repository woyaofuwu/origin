package com.asiainfo.veris.crm.order.soa.person.busi.coupons; 

import java.math.BigDecimal;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;


public class CouponsQuotaInfoExportTask extends ExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData param, Pagination pagination) throws Exception
    {
        IData input = param.subData("cond", true);
        String eparchyCode=this.getTradeEparchyCode();
        if("".equals(eparchyCode) || eparchyCode==null){
        	eparchyCode="0898";
        }
        input.put(Route.ROUTE_EPARCHY_CODE, eparchyCode); 
        IDataset dataset = CSAppCall.call("SS.CouponsQuotaMgrSVC.queryCouponsQuotaInfos", input); 
        IDataset rtnSet=new DatasetList();
        for(int i=0;i<dataset.size();i++){
        	IData id=dataset.getData(i);
        	String area_id=id.getString("AREA_CODE");
        	String area_name=StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", area_id);
        	String amount=id.getString("TOTAL_AMOUNT","");
        	String balance=id.getString("BALANCE","");
        	double totalAmount=0;
        	double tBalance=0;
        	if(!"".equals(amount)){
        		totalAmount=Double.parseDouble(amount)/100; 
        		id.put("TOTAL_AMOUNT", new BigDecimal(totalAmount).setScale(2, BigDecimal.ROUND_HALF_DOWN));
        	} 
        	if(!"".equals(balance)){
        		tBalance=Double.parseDouble(balance)/100; 
        		id.put("BALANCE", new BigDecimal(tBalance).setScale(2, BigDecimal.ROUND_HALF_DOWN));
        	} 
        	id.put("AREA_CODE", area_name);
        	rtnSet.add(id);
        }
        return rtnSet;
    }
}
