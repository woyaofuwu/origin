
package com.asiainfo.veris.crm.order.web.group.colorringOpenSpecial;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class ColorringSpecialOpen extends GroupBasePage
{
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {
        IData condData = getData();

        // 业务受理前校验
        onSubmitBaseTradeCheck(cycle);

        // 调用服务数据
        IData svcData = new DataMap();
        svcData.put("GRP_SERIAL_NUMBER", condData.getString("grp_SERIAL_NUMBER"));
        svcData.put("USER_ID", condData.getString("grp_USER_ID"));
        svcData.put("SERIAL_NUMBER", condData.getString("cond_SERIAL_NUMBER"));
        svcData.put("END_DATE", condData.getString("END_DATE"));
        svcData.put("REMARK", condData.getString("REMARK"));

        // 调用服务
        IDataset dataset = CSViewCall.call(this, "SS.ColorringOpenSpecSVC.crtTrade", svcData);
        setAjax(dataset);
    }

    public void onSubmitBaseTradeCheck(IRequestCycle cycle) throws Exception
    {
        IData conData = getData();
        String serialNumber = conData.getString("cond_SERIAL_NUMBER");
        // 1. 未完工工单限制
        IData svcData = new DataMap();
        svcData.put("SERIAL_NUMBER", serialNumber);
        svcData.put("TRADE_TYPE_CODE", "2958");
        svcData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

        IDataset tradeList = CSViewCall.call(this, "CS.TradeInfoQrySVC.getMainTradeBySnEparchyCode", svcData);

        if (IDataUtil.isNotEmpty(tradeList))
        {
            CSViewException.apperr(TradeException.CRM_TRADE_0, serialNumber);
        }

    }

    /**
     * 查询成员所依赖的集团信息
     * 
     * @param serial_number
     * @throws Exception
     */
    public void queryGrpInfo(String serial_number, String userIdB) throws Exception
    {
        IData relaInfo = RelationUUInfoIntfViewUtil.qryRelaUUInfoByUserIdBAndRelationTypeCode(this, userIdB, "26", getTradeEparchyCode(), false);

        if (IDataUtil.isEmpty(relaInfo))
        {
            CSViewException.apperr(GrpException.CRM_GRP_645, serial_number);
        }

        // 查询集团三户资料
        String grpUserId = relaInfo.getString("USER_ID_A");
        IData data = UCAInfoIntfViewUtil.qryGrpUCAInfoByUserId(this, grpUserId);

        if (IDataUtil.isEmpty(data))
        {
            CSViewException.apperr(GrpException.CRM_GRP_646, serial_number);
        }
        IData grpuserinfo = data.getData("GRP_USER_INFO");
        IData groupinfo = data.getData("GRP_CUST_INFO");

        setUserInfo(grpuserinfo);
        setCustInfo(groupinfo);

    }

    /**
     * 根据手机号码查询手机信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryMemberInfo(IRequestCycle cycle) throws Exception
    {
        IData condData = getData("cond", true);
        String serial_number = condData.getString("SERIAL_NUMBER");

        // 查询成员用户信息
        IData mebinfo = UCAInfoIntfViewUtil.qryMebUCAInfoBySn(this, serial_number);

        if (IDataUtil.isEmpty(mebinfo))
            return;

        IData userinfo = mebinfo.getData("MEB_USER_INFO");// 成员信息列表
        IData custinfo = mebinfo.getData("MEB_CUST_INFO");// 成员客户信息列表

        if (IDataUtil.isEmpty(userinfo))
            userinfo = new DataMap();
        if (IDataUtil.isEmpty(custinfo))
            custinfo = new DataMap();

        setMemCustInfo(custinfo);
        setMemUserInfo(userinfo);

        queryGrpInfo(serial_number, userinfo.getString("USER_ID"));

        setCondition(condData);

        IData param = new DataMap();
        param.put("USER_ID", userinfo.getString("USER_ID"));
        param.put("SERVICE_ID", "910");
        param.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

        IDataset svcStateInfos = CSViewCall.call(this, "CS.UserSvcInfoQrySVC.getUserLastStateByUserSvc", param);

        if (IDataUtil.isNotEmpty(svcStateInfos))
        {
            for (int i = 0; i < svcStateInfos.size(); i++)
            {
                IData svcState = svcStateInfos.getData(i);
                if (!"5".equals(svcState.getString("STATE_CODE")))
                {
                    CSViewException.apperr(GrpException.CRM_GRP_647, serial_number);
                }
            }
        }
        else
        {
            CSViewException.apperr(GrpException.CRM_GRP_648, serial_number);
        }
    }

    public abstract void setCondition(IData condition);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setMemCustInfo(IData memCustInfo);

    public abstract void setMemUserInfo(IData memUserInfo);

    public abstract void setUserInfo(IData userInfo);

}
