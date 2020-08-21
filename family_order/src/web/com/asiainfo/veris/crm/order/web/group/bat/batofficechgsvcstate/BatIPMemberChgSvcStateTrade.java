
package com.asiainfo.veris.crm.order.web.group.bat.batofficechgsvcstate;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class BatIPMemberChgSvcStateTrade extends GroupBasePage
{
    /**
     * 提交方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {
        IData condData = getData();
        String ipbindnumber1 = condData.getString("members");

        String[] ipbindnumber = ipbindnumber1.split(",");
        String operType = getParameter("OPER_TYPE");
        if (StringUtils.isEmpty(operType))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "没有选择操作类型，请选择操作类型");
        }
        if (ipbindnumber.length == 0)
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "没有需要开机或停机的IP后付费成员");
        }

        String batchopencode = "SVCSTATECHG";
        IData data = new DataMap();
        data.put("BATCH_OPER_TYPE", batchopencode);

        IDataset batDataList = new DatasetList();

        IData batParam = new DataMap();

        for (int i = 0; i < ipbindnumber.length; i++)
        {
            IData info = new DataMap();
            info.put("SERIAL_NUMBER", ipbindnumber[i]);
            batDataList.add(info);
        }

        // 调用服务数据
        IData codingstr = new DataMap();

        codingstr.put("OPER_TYPE", operType);
        codingstr.put("SERIAL_NUMBER", getParameter("cond_SERIAL_NUMBER"));
        codingstr.put("REMARK", getParameter("REMARK"));

        batParam.put("BATCH_OPER_TYPE", batchopencode);
        batParam.put("BATCH_TASK_NAME", "IP后付费开停机业务");
        batParam.put("BATCH_OPER_NAME", "服务状态变更");
        batParam.put("SMS_FLAG", "0");
        batParam.put("CODING_STR", codingstr);

        IData svcData = new DataMap();
        svcData.put("IN_PARAM", batParam);
        svcData.put("BAT_DEAL_LIST", batDataList);
        svcData.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());
        svcData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

        // 调用服务
        IDataset retDataset = CSViewCall.call(this, "CS.BatDealSVC.singlePagecreateBat", svcData);

        String batchId = retDataset.getData(0).getString("BATCH_ID");

        StringBuilder builder = new StringBuilder(50);
        builder.append("\nIP后付费停开机批次号[" + getRedirectHtml(batchId) + "]" + "启动批次任务请点击批次号");

        IData retData = new DataMap();
        retData.put("ORDER_ID", builder.toString());
        IDataset result = new DatasetList();
        result.add(retData);

        // 设置返回数据
        setAjax(result);

    }

    public String getRedirectHtml(String batchId)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<a jwcid=\"@Redirect\" value=\"");
        sb.append(batchId);
        sb.append("\" onclick=\"javascript:openNav('集团批量业务','group.bat.batdeal.GrpBatTradeDeal', 'queryStartTaskInfo', '&cond_BATCH_ID=");
        sb.append(batchId);
        sb.append("&cond_BATCH_OPER_TYPE=SVCSTATECHG', '');\">");
        sb.append(batchId);
        sb.append("</a>");
        return sb.toString();
    }

    public void queryIPMenberNumber(IRequestCycle cycle) throws Exception
    {
        IData condData = getData("cond", true);
        String serial_number = condData.getString("SERIAL_NUMBER");

        IData grpuserData = UCAInfoIntfViewUtil.qryGrpUCAInfoByGrpSn(this, serial_number);
        IData userinfo = grpuserData.getData("GRP_USER_INFO");
        String brand_code = userinfo.getString("BRAND_CODE");
        if (!"IP10".equals(brand_code))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "服务号码不存在，不是IP后付费用户，不能继续办理！");
        }

        queryBindIPPhone(cycle);
        setCondition(condData);
    }

    /**
     * 根据手机USER_ID获取IP直通车固定号码资料
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public void queryBindIPPhone(IRequestCycle cycle) throws Exception
    {
        IDataset ipMemberInfos = new DatasetList();
        IData condData = getData("cond", true);
        String serail_number = condData.getString("SERIAL_NUMBER");

        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serail_number);

        IData grpuserData = UCAInfoIntfViewUtil.qryGrpUCAInfoByGrpSn(this, serail_number);
        IData userInfo = grpuserData.getData("GRP_USER_INFO");

        String userIdA = userInfo.getString("USER_ID");
        IDataset bindipphones = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdAAndRelationTypeCodeRoleCodeB(this, userIdA, "51", "1", getTradeEparchyCode());
        if (IDataUtil.isNotEmpty(bindipphones))
        {
            for (int i = 0; i < bindipphones.size(); i++)
            {
                IData bindinphone = bindipphones.getData(i);
                String user_id = bindinphone.getString("USER_ID_B");
                IData param = new DataMap();
                param.put("USER_ID", user_id);
                param.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
                IData info = UCAInfoIntfViewUtil.qryMebUserInfoByUserIdAndRoute(this, user_id, getTradeEparchyCode(), false);
                if (IDataUtil.isEmpty(info))
                {
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "无IP固定号码用户主表资料");
                }

                IData userinfo = new DataMap();
                userinfo.put("SERIAL_NUMBER", info.getString("SERIAL_NUMBER"));
                ipMemberInfos.add(userinfo);
            }
            setMemberList(ipMemberInfos);
        }
        else
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "IP后付费集团没有成员");
        }
    }

    public abstract void setCondition(IData condition);

    public abstract void setMemberCount(long memberCount);

    public abstract void setMemberList(IDataset memberList);

}
