/* $Id: BatChangeVPMNScpCode.java,v 1.2 2010/09/15 11:28:50 lixiuyu Exp $ */

package com.asiainfo.veris.crm.order.web.group.changevpmnscpcode;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class ChangeVPMNScpCode extends GroupBasePage
{

    public abstract void setVpmnInfo(IData condition);

    /**
     * 提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {

        // 服务数据
        // 集团变更的必传参数
        IData svcData = new DataMap();
        svcData.put("USER_ID", getData().getString("GRP_USER_ID", null));
        svcData.put("PRODUCT_ID", "8000");
        svcData.put(Route.USER_EPARCHY_CODE, getData().getString("GRP_USER_EPARCHYCODE"));
        // 成员变更的必传参数
        // inparam.put("USER_ID", getData().getString("GRP_USER_ID", ""));
        svcData.put("SERIAL_NUMBER", getData().getString("MEB_SERIAL_NUMBER", ""));
        svcData.put("MEM_DISCNT_CODE", getData().getString("DISCNT_CODE"));
        // svcData.put("PRODUCT_ID", "8000");

        // 调用服务
        IDataset retDataset = CSViewCall.call(this, "SS.ChangeVpmnScpCodeSVC.crtOrder", svcData);

        // 设置返回值
        setAjax(retDataset);

        // IDataset result = CSViewCall.call(this, "SS.CreateColorRingGroupUserSVC.crtOrder", inparam);

        // add by lixiuyu@20100826 新增SCP6,用户要求根据界面的SCP_CODE做修改
        // String scpCode = getData().getString("SCP_CODE", "");

        // ChangeVPMNScpCode bean = new ChangeVPMNScpCode();
        // bean.changeScpTradeReg(pd, tradedata, scpCode);
    }

    /**
     * create campaign
     * 
     * @param cycle
     * @throws Exception
     */
    public void hasTradeTypecode(IRequestCycle cycle) throws Exception
    {
        // PageData pd = getPageData();
        // String tradeTypeCode = "3153";
        //
        // GroupInfoQueryBean bean = new GroupInfoQueryBean();
        //
        // IData dt = new DataMap();
        // dt.put("TRADE_TYPE_CODE", tradeTypeCode);
        //
        // boolean hasTpCod = bean.hasTradeTypeCode(pd, dt);
        //
        // if (!hasTpCod)
        // {
        // common.warn("业务类型编码不存在！");
        // }
        // else
        // {
        // redirectToMsg("业务类型编码存在！");
        // }

    }

    public void qryVPMNScpInfo(IRequestCycle cycle) throws Exception
    {
        String grpUserId = getData().getString("GRP_USER_ID");
        // 查询VPMN资料
        IData userVpnInfo = UCAInfoIntfViewUtil.qryUserVpnInfoByUserId(this, grpUserId, false);
        if (IDataUtil.isEmpty(userVpnInfo))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_122, userVpnInfo.getString("SERIAL_NUMBER"));
        }
        // userVpnInfo.put("VPN_NO", grpCustName);
        // userVpnInfo.put("SERIAL_NUMBER", grpSn);
        // userVpnInfo.put("BRAND_CODE", grpSn);
        userVpnInfo.put("PRODUCT_NAME", getData().getString("GRP_PRODUCT_NAME"));

        setVpmnInfo(userVpnInfo);

    }

    // j2ee 不确定必须判定否
    public void qryAcctInfo(IData param) throws Exception
    {
        // GroupInfoQueryBean bean = new GroupInfoQueryBean();
        // IDataset result = bean.qryAcctInfo(param);

        // IData res = new DataMap();
        // if (null != result && result.size() > 0)
        // {
        // res = (IData) result.get(0);
        // }
        // else
        // {
        // common.warn("该VPMN用户不存在账户资料，业务不能继续！");
        // }
        //
        // return res;
    }

}
