
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ElementPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;

public abstract class CttBroadbandProductInfoQry extends CSBizBean
{

    public static IDataset getDiscntElementByCondition(String packageId, String userId, String eparchyCode, String tradeTypeCode, String areaCode) throws Exception
    {

        PkgElemInfoQry bean = new PkgElemInfoQry();
        IData data = new DataMap();
        data.put("PACKAGE_ID", packageId);
        data.put("EPARCHY_CODE", eparchyCode);
        data.put("AREA_CODE", areaCode);// 片区编码

        if (tradeTypeCode.equals("9711"))
        {
            data.put("OPER_CODE", "0");// 0 新装,1 续费 2 不限制
            data.put("YEAR_NUM", "0");// 用户年限,开户无年限,默认传0
        }
        else if (tradeTypeCode.equals("9712"))
        {

            IData userinfo = UcaInfoQry.qryUserInfoByUserId(userId);
            String userOpenDate = SysDateMgr.getSysTime();
            if (IDataUtil.isNotEmpty(userinfo))
            {
                userOpenDate = userinfo.getString("OPEN_DATE");
            }
            String strSysdate = SysDateMgr.getSysTime();

            int i = SysDateMgr.yearInterval(userOpenDate, strSysdate);
            data.put("OPER_CODE", "1");// 0 新装,1 续费 2 不限制
            data.put("YEAR_NUM", i);// 用户年限,开户无年限,默认传0
            // 该方法不根据USER_ID查询,传入USER_ID只是为了获取用户开户时间,故清空
            userId = null;
        }

        if (userId == null)
        {
            data.put("USER_ID", "0000000000000000");
        }
        else
        {
            data.put("USER_ID", userId);
        }

        IDataset elementList = Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_WIDENET_PACKAGE_ELEMENT", data);

        // 根据员工工号过滤元素权限
        ElementPrivUtil.filterElementListByPriv(getVisit().getStaffId(), elementList);

        return elementList;
    }
}
