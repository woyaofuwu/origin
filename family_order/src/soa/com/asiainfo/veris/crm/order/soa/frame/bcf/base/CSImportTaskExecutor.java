
package com.asiainfo.veris.crm.order.soa.frame.bcf.base;

import com.ailk.biz.impexp.ImportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class CSImportTaskExecutor extends ImportTaskExecutor
{
    /**
     * 执行导入
     */
    public IDataset executeImport(IData data, IDataset dataset) throws Exception
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
