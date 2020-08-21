
package com.asiainfo.veris.crm.order.soa.person.busi.badness;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

/**
 * 
 * @author zyz
 * @version 1.0
 * 日期：20160612
 *
 */
public class BadWebInfoDealExpTask extends ExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData param, Pagination pagination) throws Exception
    {
    	//获取查询条件
    	IData input = param.subData("cond", true);
    	
        IDataset dataset = CSAppCall.call("SS.BadWebInfoDealSVC.queryBadWebInfo", input);
        if(IDataUtil.isNotEmpty(dataset)){
        	for(int i=0;i<dataset.size();i++){
        		IData data=(IData) dataset.get(i);
        		String name=StaticUtil.getStaticValue("WEB_SOURCE", data.getString("WEB_SOURCE"));
        		data.put("WEB_SOURCE", name);
        	}
        }
        return dataset;
    } 
}
