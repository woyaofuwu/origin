
package com.asiainfo.veris.crm.order.soa.frame.bcf.base;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.fuzzy.DataFuzzySHXI;

public class CSExportTaskExecutor extends ExportTaskExecutor
{
	private String svcName;
	
	public void setSvcName(String svcName)
	{
		this.svcName = svcName;
	}
	
	public String getSvcName()
	{
		return this.svcName;
	}
	
    /**
     * 执行导出
     */
    public IDataset executeExport(IData paramIData, Pagination paramPagination) throws Exception
    {
        IDataset outDataset = executeBizExprot(paramIData, paramPagination);
        
        //数据模糊化
    	if(ProvinceUtil.isProvince(ProvinceUtil.SHXI))
    	{
    		DataFuzzySHXI.fuzzy(getSvcName(), paramIData, outDataset);
    	}
        
        return outDataset;
    }
    
    /**
     * 子类实现导出
     * @param paramIData
     * @param paramPagination
     * @return
     * @throws Exception
     */
    public IDataset executeBizExprot(IData paramIData, Pagination paramPagination) throws Exception
    {
    	return null;
    }

    /**
     * 导入数据转换
     * 
     * @param data
     * @throws Exception
     */
    public void transImpexpData(IData data) throws Exception
    {
        String tradeEparchyCode = data.getString("TRADE_EPARCHY_CODE", "");

        // 取登录地州编码
        if (StringUtils.isBlank(tradeEparchyCode))
        {
            tradeEparchyCode = data.getString("LOGIN_EPARCHY_CODE");
        }

        // 如果是全省工号，则跟前台CSBizService中设置路由逻辑一致，为默认地州编码
        if (StringUtils.isBlank(tradeEparchyCode) || tradeEparchyCode.length() != 4 || !StringUtils.isNumeric(tradeEparchyCode))
        {
            tradeEparchyCode = Route.getCrmDefaultDb();
        }

        data.put("TRADE_EPARCHY_CODE", tradeEparchyCode);
    }
}
