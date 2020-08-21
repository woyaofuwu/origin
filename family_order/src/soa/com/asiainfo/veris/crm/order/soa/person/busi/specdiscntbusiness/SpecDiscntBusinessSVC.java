
package com.asiainfo.veris.crm.order.soa.person.busi.specdiscntbusiness;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.UserDiscntException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.DiscntPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.AcctDayDateUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: SpecDiscntBusinessSVC.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: maoke
 * @date: May 28, 2014 10:59:26 AM Modification History: Date Author Version Description
 *        -------------------------------------------------------* May 28, 2014 maoke v1.0.0 修改原因
 */
public class SpecDiscntBusinessSVC extends CSBizService
{
    /**
     * @Description: 获取特殊优惠列表且进行权限过滤
     * @return
     * @throws Exception
     * @author: maoke
     * @date: May 28, 2014 4:22:57 PM
     */
    public IDataset getChooseDiscnt() throws Exception
    {
        IDataset chooseDiscnt = UDiscntInfoQry.queryDiscntsByDiscntType("A");

        DiscntPrivUtil.filterDiscntListByPriv(CSBizBean.getVisit().getStaffId(), chooseDiscnt);
        return chooseDiscnt;
    }

    /**
     * @Description: 获取用户特殊优惠列表
     * @param param
     * @return
     * @throws Exception
     * @author: maoke
     * @date: May 28, 2014 3:17:15 PM
     */
    public IData getUserSpecDiscntList(IData param) throws Exception
    {
        IData returnData = new DataMap();
        returnData.clear();

        String userId = param.getString("USER_ID");

        IDataset userDiscnt = BofQuery.queryUserAllValidDiscnt(userId, param.getString("EPARCHY_CODE", getTradeEparchyCode()));

        if (IDataUtil.isNotEmpty(userDiscnt))
        {
            // 过滤
            for (int i = 0; i < userDiscnt.size(); i++)
            {
                IData discnt = userDiscnt.getData(i);

                String discntCode = discnt.getString("DISCNT_CODE");

                if (!isExistsDtypeDiscnt(discntCode))
                {
                    userDiscnt.remove(i);

                    i--;
                }
                else
                {
                    discnt.put("DISCNT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(discntCode));
                }
            }
        }
        else
        {
            CSAppException.apperr(UserDiscntException.CRM_USER_DISCNT_3);
        }

        returnData.put("USER_DISCNT", userDiscnt);

        returnData.put("CHOOSE_DISCNT", getChooseDiscnt());

        returnData.put("SYS_DATE", SysDateMgr.getSysTime());
        returnData.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
        returnData.put("LAST_DAY_THIS_MONTH", AcctDayDateUtil.getLastDayThisAcct(userId));
        returnData.put("FIRST_DAY_NEXT_MONTH", AcctDayDateUtil.getFirstDayNextAcct(userId));

        return returnData;
    }

    /**
     * @Description: 优惠是否存在于【TD_B_DTYPE_DISCNT】,且类型为A
     * @param discntCode
     * @return
     * @throws Exception
     * @author: maoke
     * @date: May 26, 2014 3:47:27 PM
     */
    public boolean isExistsDtypeDiscnt(String discntCode) throws Exception
    {
        String dtypeDiscnt = UDiscntInfoQry.getDiscntTypeByDiscntCode(discntCode);

        if ("A".equals(dtypeDiscnt))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
