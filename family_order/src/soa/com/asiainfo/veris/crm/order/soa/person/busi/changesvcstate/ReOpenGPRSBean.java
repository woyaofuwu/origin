
package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;

public class ReOpenGPRSBean extends CSBizBean
{
    public void childWhetherItIsValid(IData userinfo) throws Exception
    {
        IData params = new DataMap();
        IData userInfoNew = new DataMap(userinfo.getString("USER_INFO"));
        String userId = userInfoNew.getString("USER_ID");

        params.put("USER_ID", userId);
        if (!isUserHasGPRS(userId, "22"))
            CSAppException.apperr(CrmUserException.CRM_USER_783, "该用户没有办理GPRS业务，如需GPRS功能请办理！");
        if (!("2".equalsIgnoreCase(getUserGPRSStateCode(userId, "22"))))
            CSAppException.apperr(CrmUserException.CRM_USER_783, "该用户的GPRS服务状态不是暂停状态！");
        params.put("SERVICE_ID", "22");
        IDataset svcDataset = UserSvcStateInfoQry.getUserLastStateByUserSvc(userId, "22");
        if (svcDataset.getData(0).getString("RSRV_TAG1", "").equals("1"))
        {// 超50G不能再次开通
            CSAppException.apperr(CrmUserException.CRM_USER_783, "该用户的GPRS流量本月已使用超过50G，不能再次开通！");
        }
        String stateCode = getUserGPRSStateCode(userId, "0");
        String stateName = USvcStateInfoQry.getSvcStateNameBySvcIdStateCode("0", stateCode);
        if (!("0".equalsIgnoreCase(stateCode)))
        {
            StringBuilder msg = new StringBuilder();
            msg.append("该用户的语音服务状态为[").append(stateName).append("],不能办理此业务！");
            CSAppException.apperr(CrmUserException.CRM_USER_783, msg.toString());
        }

    }

    /**
     * GPRS开启功能：获取用户GPRS服务状态code
     * 
     * @param userId
     * @return IData
     * @throws Exception
     */
    public IData getUserGprsStateCode(String userId) throws Exception
    {
        IData result = new DataMap();
        String service1, state1, tag1;
        IDataset dataset1 = UserSvcStateInfoQry.getUserLastStateByUserSvc(userId, "22");
        if (dataset1 != null && dataset1.size() > 0)
        {
            service1 = "GPRS服务";
            state1 = dataset1.getData(0).getString("STATE_CODE").trim();
            tag1 = "1";
        }
        else
        {
            service1 = "  ";
            state1 = "  ";
            tag1 = "-1";
        }

        result.put("SERVICE1", service1);
        result.put("STATE1", state1);
        result.put("TAG1", tag1);
        return result;
    }

    /**
     * 获取用户GPRS服务状态code
     */
    public String getUserGPRSStateCode(String userId, String serviceId) throws Exception
    {
        IDataset dataset = UserSvcStateInfoQry.getUserLastStateByUserSvc(userId, serviceId);
        if (dataset.isEmpty())
        {
            return "-1";
        }
        return dataset.getData(0).getString("STATE_CODE");
    }

    /**
     * 获取用户服务表信息
     */
    public boolean isUserHasGPRS(String userId, String serviceId) throws Exception
    {
        IDataset dataset = UserSvcInfoQry.qrySvcInfoByUserIdSvcId(userId, serviceId);
        if (dataset.isEmpty())
            return false;
        return true;
    }

}
