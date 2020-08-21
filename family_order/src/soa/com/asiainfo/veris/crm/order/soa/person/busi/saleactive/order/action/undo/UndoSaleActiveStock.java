
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.undo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class UndoSaleActiveStock implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String packageId = mainTrade.getString("RSRV_STR2");
//        IDataset result = PkgExtInfoQry.getPackageExtInfo(packageId, CSBizBean.getUserEparchyCode());
        IDataset result = UpcCall.queryTempChaByOfferTable(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId, "TD_B_PACKAGE_EXT");
        IData pkgExtInfo = result.getData(0);
        String condFactor3 = pkgExtInfo.getString("COND_FACTOR3");

        if (StringUtils.isBlank(condFactor3))
        {
            return;
        }

        if ("ZZZZ".equals(condFactor3))
        {

        }
        else
        {
            /*
             * result = ActiveStockInfoQry.queryByResKind(condFactor3, CSBizBean.getVisit().getStaffId(),
             * CSBizBean.getVisit().getCityCode(), CSBizBean.getTradeEparchyCode()); if(IDataUtil.isEmpty(result)) {
             * CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_43); } IData activeStockInfo =
             * result.getData(0); int warnningValueU = activeStockInfo.getInt("WARNNING_VALUE_U"); int warnningValueD =
             * activeStockInfo.getInt("WARNNING_VALUE_D"); if(warnningValueU >= warnningValueD) {
             * CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_44); }
             */

            IData cond = new DataMap();
            cond.put("RES_KIND_CODE", condFactor3);
            cond.put("STAFF_ID", mainTrade.getString("CANCEL_STAFF_ID"));
            cond.put("EPARCHY_CODE", mainTrade.getString("CANCEL_EPARCHY_CODE"));
            cond.put("CITY_CODE", mainTrade.getString("CANCEL_CITY_CODE"));
            StringBuilder sql = new StringBuilder(200);
            sql.append(" UPDATE TF_F_ACTIVE_STOCK");
            sql.append(" SET WARNNING_VALUE_U = WARNNING_VALUE_U - 1");
            sql.append(" WHERE STAFF_ID = :STAFF_ID");
            sql.append(" AND EPARCHY_CODE = :EPARCHY_CODE");
            sql.append(" AND RES_KIND_CODE = :RES_KIND_CODE");
            sql.append(" AND CITY_CODE = :CITY_CODE");
            Dao.executeUpdate(sql, cond);
        }

    }

}
