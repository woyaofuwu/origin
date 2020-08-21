
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmAccountException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class TransAcctQry
{

    /**
     * 取缴费记录
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    public static IData CheckTradeid(String chargeId) throws Exception
    {

        // String sql =
        // "select serial_number,recv_fee,payment_id,package_id, month_fee,no_get_fee,deposit_zy,deposit_bd,deposit_kc from tf_LIANJIE_ALL_PAYhis where process_flag=0 and charge_id="
        // + chargeId;
        IData param = new DataMap();
        param.put("CHARGE_ID", chargeId);
        IDataset results = Dao.qryByCode("TF_LIANJIE_ALL_PAYHIS", "SEL_BY_CHARGE_ID", param);

        if (results == null || results.size() == 0)
        {
            // common.error("输入缴费流水号错误");
            CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_136);
            return null;
        }
        else
        {
            IData result = results.getData(0);
            return result;
        }

    }

    public static IDataset queryByChargeId(String chargeId, String serialNumber) throws Exception
    {
        IData data = new DataMap();
        data.put("CHARGE_ID", chargeId);
        data.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("TF_LIANJIE_ALL_PAYHIS", "SEL_KZCZ_2012", data);
    }

    public static IDataset queryMaxTime2012(String userId, String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("CHNL_SKY_REC", "SEL_MAXTIME_2012", param);
    }
}
