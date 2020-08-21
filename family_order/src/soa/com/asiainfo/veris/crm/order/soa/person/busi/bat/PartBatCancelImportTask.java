
package com.asiainfo.veris.crm.order.soa.person.busi.bat;

import com.ailk.biz.impexp.ImportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatDealInfoQry;

public class PartBatCancelImportTask extends ImportTaskExecutor
{

    @Override
    /**
     * 批量数据导入处理，提供给MQ回调
     */
    public IDataset executeImport(IData arg0, IDataset arg1) throws Exception
    {
        IDataset succ_results = new DatasetList();
        for (int i = 0; i < arg1.size(); i++)
        {
            IData succd = (IData) arg1.get(i);

            succ_results = BatDealInfoQry.queryBatDealByBatIdAndSNForCancle(succd);
            if (null != succ_results && succ_results.size() > 0)
            {
                for (int j = 0; j < succ_results.size(); j++)
                {
                    IData succ_result = (IData) succ_results.get(j);
                    IData paramdata = new DataMap();
                    paramdata.put("BATCH_ID", succ_result.getString("BATCH_ID", ""));
                    paramdata.put("SERIAL_NUMBER", succ_result.getString("SERIAL_NUMBER", ""));

                    paramdata.put("TRADE_EPARCHY_CODE", getVisit().getStaffEparchyCode());
                    paramdata.put("TRADE_CITY_CODE", getVisit().getCityCode());
                    paramdata.put("TRADE_DEPART_ID", getVisit().getDepartId());
                    paramdata.put("TRADE_STAFF_ID", getVisit().getStaffId());
                    Dao.executeUpdateByCodeCode("TF_B_TRADE_BATDEAL", "UPD_CANCEL_BY_BATCHID_AND_SN", paramdata, Route.getJourDb(Route.CONN_CRM_CG));
                }
            }
        }
        return new DatasetList();
    }

}
