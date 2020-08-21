
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.userreservecombo;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UDepartInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;

/**
 * 功能：用户预约产品的导出 作者：GongGuang
 */
public class ExportQueryUserReserveCombo extends ExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData paramIData, Pagination paramPagination) throws Exception
    {
        IData data = paramIData.subData("cond", true);
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        String userId = UcaDataFactory.getNormalUca(data.getString("SERIAL_NUMBER", "")).getUserId();
        if ("".equals(userId))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_112);
        }
        data.put("USER_ID", userId);
        IDataset res = CSAppCall.call("SS.QueryUserReserveComboSVC.queryUserReserveTrade", data);
        // 手动转换编码
        for (int i = 0, size = res.size(); i < size; i++)
        {
            IData tempInfo = res.getData(i);
            String tradeTypeCode = tempInfo.getString("TRADE_TYPE_CODE", "");
            if (!"".equals(tradeTypeCode))
            {
                String tradeType = UTradeTypeInfoQry.getTradeTypeName(tradeTypeCode);
                res.getData(i).put("TRADE_TYPE_CODE", tradeType);
            }

            String departId = tempInfo.getString("TRADE_DEPART_ID", "");
            if (!"".equals(departId))
            {
                String departName = UDepartInfoQry.getDepartNameByDepartId(departId);
                res.getData(i).put("TRADE_DEPART_ID", departName);
            }
        }
        return res;
    }
}
