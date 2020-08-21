
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class FeeItemInfoQry
{
    /**
     * 获取费用减免的规则
     * 
     * @author zhujm
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getFeeItem(String eparchyCode) throws Exception
    {

        IData data = new DataMap();
        data.put("EPARCHY_CODE", eparchyCode);
        IDataset tmp = Dao.qryByCode("TD_B_FEEITEM", "SEL_BY_EPARCHY_CODE", data, Route.CONN_CRM_CEN);
        tmp = tmp == null ? new DatasetList() : tmp;

        IDataset tmp2 = new DatasetList();
        for (int i = 0; i < tmp.size(); i++)
        {
            IData ele = tmp.getData(i);
            IData temp = new DataMap();

            temp.put("CODE", ele.getString("FEEITEM_CODE").trim());
            temp.put("NAME", ele.getString("FEEITEM_NAME").trim());
            temp.put("PRE_TAG", ele.getString("PRE_TAG").trim());
            temp.put("PREMONEY", ele.getString("PREMONEY").trim());

            tmp2.add(temp);
        }

        return tmp2;
    }

    public static String getFeeItemNameByFeeItemCode(String feeItemCode) throws Exception
    {
        return StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_FEEITEM", "FEEITEM_CODE", "FEEITEM_NAME", feeItemCode);
    }

}
