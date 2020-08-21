
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.ungotgift;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

public class UngotGiftQuerySVC extends CSBizService
{

    public IData queryUngotGiftCondition(IData param) throws Exception
    {
        IData result = new DataMap();
        String startDate = SysDateMgr.getFirstDayOfThisMonth();
        String endDate = SysDateMgr.getSysDate();
        result.put("START_DATE", startDate);
        result.put("END_DATE", endDate);
        IDataset areas = StaticUtil.getList(this.getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", "PARENT_AREA_CODE", this.getVisit().getStaffEparchyCode());
        result.put("AREAS", areas);

        IDataset actives = CommparaInfoQry.getCommByParaAttr("CSM", "50", this.getVisit().getStaffEparchyCode());
        IDataset activeTemps = DataHelper.distinct(actives, "PARAM_CODE,PARAM_NAME", ",");
        result.put("ACTIVES", activeTemps);

        if (IDataUtil.isNotEmpty(actives))
        {
            IData active = actives.getData(0);
            IDataset packages = CommparaInfoQry.getCommpara("CSM", "50", active.getString("PARAM_CODE"), this.getVisit().getStaffEparchyCode());
            result.put("PACKAGES", packages);
        }

        return result;
    }

    public IDataset queryUngotGiftList(IData param) throws Exception
    {
        IDataset result = UserSaleActiveInfoQry.queryUngotGiftList(param.getString("PRODUCT_ID"), param.getString("PACKAGE_ID"), param.getString("TRADE_STAFF_ID_S"), param.getString("TRADE_STAFF_ID_E"), param.getString("TRADE_CITY_CODE"), param
                .getString("TRADE_DEPART_ID"), param.getString("START_DATE"), param.getString("END_DATE"));
        return result;
    }
}
