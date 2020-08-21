
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.dm.dmtradeopen;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.DMBusiException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

public class DMTradeOpenBean extends CSBizBean
{
    static transient final Logger logger = Logger.getLogger(DMTradeOpenBean.class);

    /**
     * 输入号码查询时查询业务所需信息
     */
    public IDataset loadChildTradeInfo(IData input) throws Exception
    {
        IData vipInfo = new DataMap(input.getString("VIP_INFO"));

        String userEparchyCode = input.getString("EPARCHY_CODE");
        String userId = input.getString("USER_ID");
        String serialNumber = input.getString("SERIAL_NUMBER");

        // 异地用户不能办理
        if (!CSBizBean.getTradeEparchyCode().equals(userEparchyCode))
        {
            CSAppException.apperr(DMBusiException.CRM_DM_120);
        }

        // 非大客户不能办理
        if (IDataUtil.isEmpty(vipInfo) && vipInfo.getString("VIP_CLASS_ID") == null)
        {
            CSAppException.apperr(DMBusiException.CRM_DM_121);
        }

        // 验证是否已开通DM业务
        IDataset dmInfos = UserResInfoQry.getUserResByRsrvStr(userId, serialNumber);

        return dmInfos;
    }
}
