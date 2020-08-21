
package com.asiainfo.veris.crm.order.soa.person.busi.plat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class PlatBureDataIntfBean
{

    public void modRinpBizArea(IDataset bizAreas) throws Exception
    {
        if (IDataUtil.isNull(bizAreas))
        {
            return;
        }
        IDataset areas = new DatasetList();
        for (Object area : bizAreas)
        {
            String areaCode = ((IData) area).getString("AREA_CODE", "");
            if (areaCode.length() == 0)
            {
                continue;
            }
            else
            {
                areas.add(area);
            }
            String[] keys =
            { "BIZ_TYPE_CODE", "AREA_CODE", "BIZ_CODE" };
            Dao.delete("TD_M_SPBIZ_AREA", (IData) area, keys);
        }

        if (areas.size() > 0)
        {
            Dao.inserts("TD_M_SPBIZ_AREA", areas);
        }
    }

    public void modRinpProPackInfo(IData subBiz) throws Exception
    {
        String[] keys =
        { "PACKAGE_ID", "SP_SVC_ID" };
        Dao.delete("TD_M_SPPACKAGE", subBiz, keys, Route.CONN_CRM_CEN);
        Dao.insert("TD_M_SPPACKAGE", subBiz, Route.CONN_CRM_CEN);
    }

    public void saveBizInfoAttr(IDataset bizAttrs) throws Exception
    {
        String[] keys =
        { "SP_CODE", "BIZ_CODE", "INFO_CODE" };
        Dao.delete("TD_M_SP_BIZ_ATTR", bizAttrs, keys, Route.CONN_CRM_CEN);
        Dao.insert("TD_M_SP_BIZ_ATTR", bizAttrs, Route.CONN_CRM_CEN);
    }

    public void saveSpInfoAttr(IDataset spAttrs) throws Exception
    {
        String[] keys =
        { "SP_CODE", "INFO_CODE" };
        Dao.delete("TD_M_SP_BIZ_ATTR", spAttrs, keys, Route.CONN_CRM_CEN);
        Dao.insert("TD_M_SP_BIZ_ATTR", spAttrs, Route.CONN_CRM_CEN);
    }

}
