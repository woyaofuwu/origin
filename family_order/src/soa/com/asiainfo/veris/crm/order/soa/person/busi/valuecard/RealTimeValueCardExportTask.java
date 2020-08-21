package com.asiainfo.veris.crm.order.soa.person.busi.valuecard;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSExportTaskExecutor;
/**
 * 自办营业厅有价卡销售信息导出
 * @author liutt
 *
 */
public class RealTimeValueCardExportTask extends CSExportTaskExecutor{
	public IDataset executeExport(IData data, Pagination page) throws Exception
    {
		data = IDataUtil.replaceIDataKeyDelPrefix(data,"cond_");
        getVisit().setStaffEparchyCode(data.getString("STAFF_EPARCHY_CODE"));
        String tradeTypeCode = data.getString("TRADE_TYPE_CODE");
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());  
        if ("420".equals(tradeTypeCode)){
        	data.put("config", "export/valuecard/RealTimeValueCardChgCardList.xml");
        }else{
        	data.put("config", "export/valuecard/RealTimeValueCardList.xml");
        }
        return CSAppCall.call("SS.RealTimeQueryValueCardSVC.queryValueCardInfo", data);
    }
}
