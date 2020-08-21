
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchMebInfoQry;

public class BatGroupBBossBizMebExportByFile extends ExportTaskExecutor
{

    /**
     * 执行数据导出
     * 
     * @param arg0
     *            导出的参数
     * @param arg1
     *            分页参数
     */
    @Override
    public IDataset executeExport(IData arg0, Pagination arg1) throws Exception
    {
        IDataset exportData = new DatasetList();
        // String productOfferId, String serialNumber, String ecSerialNumber, String state, String groupId

        this.setRouteId(getUserEparchyCode());

        exportData = TradeGrpMerchMebInfoQry
                .qryBBossBizMebQy(arg0.getString("cond_PRODUCT_OFFER_ID"), arg0.getString("cond_SERIAL_NUMBER"), arg0.getString("cond_EC_SERIAL_NUMBER"), arg0.getString("cond_STATE"), arg0.getString("cond_GROUP_ID"), arg1);
        return exportData;
    }

}
