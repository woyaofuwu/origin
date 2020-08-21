
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.trade;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class QueryTradeMainHisInfoSVC extends CSBizService
{
    /**
     * 业务受理记录查询 xuwb5
     * 
     * @param X_GETMODE
     *            :0 //正常用户 5 //最后销号用户 TRADE_TYPE_CODE 业务类型 select a.*,rowid from td_s_tradetype a;
     * @return
     * @throws Exception
     */
    public IDataset queryTradeMainHisInfo(IData data) throws Exception
    {
        if (!data.containsKey("X_GETMODE"))
        {
            // common.error("-1:X_GETMODE不存在");
            CSAppException.apperr(CrmCommException.CRM_COMM_1120);

        }
        if (data.getString("TRADE_TYPE_CODE", "").equals(""))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1121);
        }

        String sn = data.getString("SERIAL_NUMBER");
        IDataset ids = null;
        if (0 == data.getInt("X_GETMODE"))// 正常用户
        {

            ids = IDataUtil.idToIds(UcaInfoQry.qryUserInfoBySn(sn));
        }
        else if (5 == data.getInt("X_GETMODE"))// 最后销号用户
        {
            ids = UserInfoQry.getDestroyUserInfoBySn(sn);
        }

        if (IDataUtil.isEmpty(ids))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1122);
        }

        String tradeTypeCode = data.getString("TRADE_TYPE_CODE");
        String userId = ids.getData(0).getString("USER_ID");
        String startDate = data.getString("START_DATE");
        String endDate = data.getString("END_DATE");

        IDataset result = TradeInfoQry.queryTradeMainHisInfo2(tradeTypeCode, userId, startDate, endDate);

        if (IDataUtil.isNotEmpty(result))
        {
            for (int i = 0; i < result.size(); i++)
            {
                IData tmp = (IData) result.get(i);
                tmp.put("X_EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(tmp.getString("EPARCHY_CODE")));
                tmp.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(tmp.getString("PRODUCT_ID")));
                tmp.put("CITY_NAME", UAreaInfoQry.getAreaNameByAreaCode(tmp.getString("CITY_CODE")));
                tmp.put("BRAND", UBrandInfoQry.getBrandNameByBrandCode(tmp.getString("BRAND_CODE")));
                tmp.put("TRADE_TYPE", UTradeTypeInfoQry.getTradeTypeName(tmp.getString("TRADE_TYPE_CODE")));
                tmp.put("IN_MODE", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_INMODE", "IN_MODE_CODE", "IN_MODE", tmp.getString("IN_MODE_CODE")));
            }
        }

        return result;
    }
}
