
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.sms;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.service.bean.BeanManager;

/**
 * 功能：用于短信查询的导出 作者：GongGuang
 */
public class ExportQueryNormalSMS extends ExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData paramIData, Pagination paramPagination) throws Exception
    {
        IData param = paramIData.subData("cond", true);
        QuerySmsBean bean = BeanManager.createBean(QuerySmsBean.class);
        IDataset res = bean.querySms(param, paramPagination);
        if ("1".equals(param.getString("QUERY_MODE"))){//10086,对发送部门和发送渠道做翻译
        	for( int i = 0; i < res.size(); i++ ){
        		IData data = res.getData(i);
        		if( !"".equals(data.getString("PARA_CODE11", ""))){
        			String depName = StaticUtil.getStaticValue(getVisit(), "TD_M_DEPART","DEPART_ID","DEPART_NAME", data.getString("PARA_CODE11",""));
        			data.put("PARA_CODE11", depName);
        		}
        		
        	}
        }
        return res;
    }
}
