
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.score;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

/**
 * 功能：用于积分兑奖汇总小计查询的导出 作者：GongGuang
 */
public class ExportQueryScoreRewardSum extends ExportTaskExecutor
{
    @Override
    /**
     * 包括按用户和按县市
     */
    public IDataset executeExport(IData paramIData, Pagination paramPagination) throws Exception
    {
        IData paramQry = paramIData.subData("qry");
        IDataset results = null;
        if ("a".equals(paramQry.getString("qry_tag")))
        {
            results = CSAppCall.call("SS.QueryScoreRewardSumSVC.queryUserScoreExchange", paramIData);
            if (IDataUtil.isNotEmpty(results))
            {
                for (int i = 0; i < results.size(); i++)
                {
                    String tradeTypeCodeName = StaticUtil.getStaticValue(getVisit(), "TD_S_TRADETYPE", "TRADE_TYPE_CODE", "TRADE_TYPE", results.getData(i).getString("TRADE_TYPE_CODE"));
                    String tradeStaffName = StaticUtil.getStaticValue(getVisit(),"TD_M_STAFF", "STAFF_ID","STAFF_NAME",results.getData(i).getString("TRADE_STAFF_ID"));
                    String tradeDepartCode = StaticUtil.getStaticValue(getVisit(),"TD_M_DEPART", "DEPART_ID","DEPART_CODE",results.getData(i).getString("TRADE_DEPART_ID"));
                    String tradeDepartName = StaticUtil.getStaticValue(getVisit(),"TD_M_DEPART", "DEPART_ID","DEPART_NAME",results.getData(i).getString("TRADE_DEPART_ID"));
                    String cancelStaffName = StaticUtil.getStaticValue(getVisit(),"TD_M_STAFF", "STAFF_ID","STAFF_NAME",results.getData(i).getString("CANCEL_STAFF_ID"));
                    results.getData(i).put("TRADE_TYPE_CODE", tradeTypeCodeName);
                    results.getData(i).put("TRADE_STAFF_NAME", tradeStaffName);
                    results.getData(i).put("TRADE_DEPART_CODE", tradeDepartCode);
                    results.getData(i).put("TRADE_DEPART_NAME", tradeDepartName);
                    results.getData(i).put("CANCEL_STAFF_ID", cancelStaffName);
                }
            }
        }
        else
        {
            results = CSAppCall.call("SS.QueryScoreRewardSumSVC.queryCityScoreExchange", paramIData);
        }
        return results;
    }
}
