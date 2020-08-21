
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ForeGiftInfoQry
{

    /**
     * 获取TD_S_FOREGIFT中配置的押金数据
     * 
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getForegift() throws Exception
    {

        IDataset tmp = StaticUtil.getList(CSBizBean.getVisit(), "TD_S_FOREGIFT", "FOREGIFT_CODE", "FOREGIFT_NAME");

        tmp = tmp == null ? new DatasetList() : tmp;
        IDataset tmp2 = new DatasetList();

        for (int i = 0; i < tmp.size(); i++)
        {
            IData data = tmp.getData(i);
            IData temp = new DataMap();
            temp.put("CODE", data.getString("FOREGIFT_CODE").trim());
            temp.put("NAME", data.getString("FOREGIFT_NAME").trim());
            tmp2.add(temp);
        }

        return tmp2;
    }

    /**
     * 获取押金类型参数
     * 
     * @throws Exception
     */
    public static IDataset getForegiftTypeDs() throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        inparam.put("TYPE_ID", "TD_S_FOREGIFT");
        return Dao.qryByCode("TD_S_FOREGIFT", "SEL_ALL_TYPE", inparam, Route.CONN_CRM_CEN);

    }

    /**
     * 查询是否红名单用户
     * 
     * @param serialNumber
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-16
     */
    public static IDataset queryRedUser(String serialNumber) throws Exception
    {

        String sql = "SELECT * FROM TF_O_REDUSER WHERE 1 = 1 AND SERIAL_NUMBER = :SERIAL_NUMBER " + "AND SYSDATE BETWEEN start_date AND end_date";

        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", serialNumber);

        return Dao.qryBySql(new StringBuilder(sql), inparam);
    }

}
