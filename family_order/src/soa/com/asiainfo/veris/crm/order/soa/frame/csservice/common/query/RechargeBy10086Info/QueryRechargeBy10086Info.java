
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.RechargeBy10086Info;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class QueryRechargeBy10086Info
{
    /**
     * 配置参数获取
     * 
     * @param data
     * @param name
     * @return
     * @throws Exception
     */
    public static IDataset getCommPara(String subsys_code, String param_attr, String param_code) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsys_code);
        param.put("PARAM_ATTR", param_attr);
        param.put("PARAM_CODE", param_code);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_RECHARGE_TD_S_COMMPARA", param);
    }

    /**
     * 取出充值管控充值信息
     * 
     * @param data
     * @param name
     * @return
     * @throws Exception
     */
    public static IDataset getPaymemntSurelyList(String surely_serial_number, String main_serial_number, int limit_times) throws Exception
    {
        IData param = new DataMap();
        param.put("X_CALL_EDMPHONECODE", surely_serial_number);
        param.put("SERIAL_NUMBER", main_serial_number);
        param.put("LIMIT_TIMES", limit_times);
        return Dao.qryByCode("TF_F_PAYMEMNT_SURELY", "SEL_BY_SERIAL_NUMBER", param);
    }

    /**
     * 取出当前号码的代充值监控信息
     * 
     * @param data
     * @param name
     * @return
     * @throws Exception
     */
    public static IDataset getSupplypayInspectList(String surely_serial_number, String main_serial_number, String eparchy_code, int limit_times) throws Exception
    {
        IData param = new DataMap();
        param.put("SURELY_SERIAL_NUMBER", surely_serial_number);
        param.put("MAIN_SERIAL_NUMBER", main_serial_number);
        param.put("EPARCHY_CODE", eparchy_code);
        param.put("LIMIT_TIMES", limit_times);
        return Dao.qryByCode("TF_F_SUPPLYPAY_INSPECT", "SEL_BY_SERIAL_NUMBER", param);
    }
}
