
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.dm.dmtradecancel;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.DMBusiException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

public class DMTradeCancelBean extends CSBizBean
{
    static transient final Logger logger = Logger.getLogger(DMTradeCancelBean.class);

    /**
     * 查询用户资料
     * 
     * @param pd
     * @return
     * @throws Exception
     */
    public IDataset getUserInfo(IData data) throws Exception
    {
        IDataset ret = new DatasetList();
        IData userInfos = UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER"));
        ret.add(userInfos);

        return ret;
    }

    /**
     * 输入号码查询时查询业务所需信息
     */
    public IDataset loadChildTradeInfo(IData input) throws Exception
    {
        String userEparchyCode = input.getString("EPARCHY_CODE");
        String userId = input.getString("USER_ID");
        String serialNumber = input.getString("SERIAL_NUMBER");

        if (logger.isDebugEnabled())
            logger.debug("---------------loadChildTradeInfo--------------------" + input.toString());

        // 异地用户不能办理
        if (!CSBizBean.getTradeEparchyCode().equals(userEparchyCode))
        {
            CSAppException.apperr(DMBusiException.CRM_DM_126);
        }

        // 验证是否已开通DM业务
        IDataset dmInfos = UserResInfoQry.getUserResByRsrvStr(userId, serialNumber);

        if (IDataUtil.isEmpty(dmInfos))
        {
            CSAppException.apperr(DMBusiException.CRM_DM_147);
        }

        return dmInfos;
    }

    /**
     * 根据IMEI查询DM业务信息
     * 
     * @param pd
     * @param imei
     * @return
     * @throws Exception
     */
    public IDataset queryDmByImei(IData input) throws Exception
    {
        String imei = input.getString("RES_CODE");

        IDataset dmInfos = UserResInfoQry.getUserResByResCode(imei);

        if (IDataUtil.isEmpty(dmInfos))
        {
            CSAppException.apperr(DMBusiException.CRM_DM_125);

        }
        return dmInfos;
    }
}
