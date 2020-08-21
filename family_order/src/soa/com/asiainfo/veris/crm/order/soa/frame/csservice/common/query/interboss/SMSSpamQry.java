
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.interboss;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class SMSSpamQry
{
    public static IDataset getGroupBadInfo(String serialNum, String blackState) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNum);
        param.put("BLACK_STATE", blackState);
        IDataset res = Dao.qryByCodeParser("TF_F_GROUP_BADINFO", "SEL_DATA_TORELEASE", param, Route.CONN_CRM_CEN);
        return res;
    }

    public static String queryOperateNum() throws Exception
    {
        IData dmData = new DataMap();
        dmData.put("SUBSYS_CODE", "CSM");
        dmData.put("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
        dmData.put("PARAM_ATTR", "3271");
        dmData.put("PARAM_CODE", "LIMITNUM");
        IDataset dsCommpara = Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PARAMCODE", dmData);
        if (dsCommpara == null || dsCommpara.size() <= 0 || (dsCommpara != null && dsCommpara.size() > 0 && !"1".equals(dsCommpara.getData(0).getString("PARA_CODE1"))))
        {
            return "100";
        }
        return dsCommpara.getData(0).getString("PARA_CODE2");
    }

}
