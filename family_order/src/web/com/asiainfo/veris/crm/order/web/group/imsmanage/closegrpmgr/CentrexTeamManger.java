
package com.asiainfo.veris.crm.order.web.group.imsmanage.closegrpmgr;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class CentrexTeamManger extends GroupBasePage
{
    public void checkTeamSerial(IRequestCycle cycle) throws Exception
    {
        IData reuslt = new DataMap();
        String flag = "true";
        String message = "组号码验证可以使用";
        String serialnumber = getParameter("SERIAL_NUMBER", "");

        IData userInfo = UCAInfoIntfViewUtil.qryMebUserInfoBySn(this, serialnumber, false);
        if (IDataUtil.isEmpty(userInfo))
        {
            flag = "false";
            message = "根据组号码[" + serialnumber + "],查询用户信息失败！";
            reuslt.put("RESULTMESSAGE", message);
            reuslt.put("FLAG", flag);
            setAjax(reuslt);
            return;
        }
        String eparchycode = userInfo.getString("EPARCHY_CODE", "");
        // 判断是否为IMS用户
        String user_id_a = getParameter("USER_ID_A", "");
        String user_id = userInfo.getString("USER_ID", "");

        String custId = getParameter("GROUP_CUST_ID", "");
        IDataset imsinfos = UCAInfoIntfViewUtil.qryGrpUserInfoByCustIdAndProId(this, custId, "2222", false);
        if (IDataUtil.isEmpty(imsinfos))
        {
            flag = "false";
            message = "集团未订购多媒体桌面电话产品！";
            reuslt.put("RESULTMESSAGE", message);
            reuslt.put("FLAG", flag);
            setAjax(reuslt);
            return;
        }
        // 判断是否是当前集团用户的成员
        IDataset uuinfos = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdBAndUserIdARelationTypeCode(this, user_id, imsinfos.getData(0).getString("USER_ID"), "S1", eparchycode);

        if (IDataUtil.isEmpty(uuinfos))
        {
            flag = "false";
            message = "组号码[" + serialnumber + "]不是当前集团的IMS用户，不能作为组号码使用！";
            reuslt.put("RESULTMESSAGE", message);
            reuslt.put("FLAG", flag);
            setAjax(reuslt);
            return;
        }

        // 判断组号码是否已经被使用
        uuinfos = RelationUUInfoIntfViewUtil.qryGrpRelaUUInfosByUserIdBAndUserIdARelationTypeCode(this, "", user_id_a, "XH");

        if (IDataUtil.isNotEmpty(uuinfos))
        {
            for (int i = 0, size = uuinfos.size(); i < size; i++)
            {
                String rsrv_str5 = uuinfos.getData(i).getString("RSRV_STR5");
                if (serialnumber.equals(rsrv_str5))
                {
                    flag = "false";
                    message = "该组号码[" + serialnumber + "]已经被使用！请核实后重新录入！";
                    reuslt.put("RESULTMESSAGE", message);
                    reuslt.put("FLAG", flag);
                    setAjax(reuslt);
                    return;
                }
            }
        }

        uuinfos = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdBAndUserIdARelationTypeCode(this, user_id, "", "MB", eparchycode);
        if (IDataUtil.isNotEmpty(uuinfos))
        {
            flag = "false";
            message = "该号码[" + serialnumber + "]已是其他组成员！请核实后重新录入！";
            reuslt.put("RESULTMESSAGE", message);
            reuslt.put("FLAG", flag);
            setAjax(reuslt);
            return;
        }

        reuslt.put("RESULTMESSAGE", message);
        reuslt.put("FLAG", flag);
        setAjax(reuslt);
    }

    /**
     * 作用：覆盖父类方法
     * 
     * @author luojh
     */
    public void confirm(IRequestCycle cycle) throws Exception
    {
        IData teaminfo = getData();
        String teamType = teaminfo.getString("teaminfo_TEAM_TYPE", ""); // 组类型
        String huntType = teaminfo.getString("teaminfo_HUNT_TYPE", ""); // 寻呼组类型
        String accessCode = teaminfo.getString("teaminfo_ACCESS_CODE", ""); // 接入码
        String memNumber = teaminfo.getString("teaminfo_MEM_NUMBER", ""); // 成员号码
        String memTeam = teaminfo.getString("teaminfo_MEM_TEAM", ""); // 成员组
        String operCode = teaminfo.getString("OPER_CODE"); // 操作方式 0新增 1删除 2修改
        String teamSerial = teaminfo.getString("teaminfo_TEAM_SERIAL", "");
        String userIdB = teaminfo.getString("MEM_USER_ID", "");

        String teamInfos = teaminfo.getString("USERINFO");
        IData userInfo = new DataMap(teamInfos);
        String userId = userInfo.getString("USER_ID");
        String eparchyCode = userInfo.getString("EPARCHY_CODE"); // 集团用户地州

        IData conParams = new DataMap();
        conParams.put("USER_ID", userId);
        // 校验是否完工
        IData tempData = CSViewCall.callone(this, "SS.CentrexTeamManaBeanSVC.checkTradeTeam", conParams);
        if (IDataUtil.isNotEmpty(tempData))
        {
            String tradeid = tempData.getString("TRADE_ID", "");
            String acceptDate = tempData.getString("ACCEPT_DATE");
            CSViewException.apperr(GrpException.CRM_GRP_426, tradeid, acceptDate);
        }

        conParams.put("TEAMTYPE", teamType);
        conParams.put("HUNTTYPE", huntType);
        conParams.put("ACCESSCODE", accessCode);
        conParams.put("OPERCODE", operCode);
        conParams.put("TEAM_ID", userIdB);
        conParams.put("TEAM_SERIAL", teamSerial);
        conParams.put("MEMBER_MUMBER", memNumber);
        conParams.put("MEMBER_TEAM", memTeam);
        // 用户归属地州设置为交易地州
        conParams.put(Route.USER_EPARCHY_CODE, eparchyCode);

        IDataset result = CSViewCall.call(this, "SS.CentrexTeamManaBeanSVC.crtTrade", conParams);

        setAjax(result);
    }

    /**
     * 作用：查询集团客户信息
     * 
     * @author luojh
     */
    public void getGroupBaseInfo(IRequestCycle cycle) throws Throwable
    {
        IData custInfo = super.queryGroupCustInfo(cycle);
        if (IDataUtil.isEmpty(custInfo))
        {
            return;
        }
        String custId = custInfo.getString("CUST_ID", "");

        // 查询是否办理了融合V网产品
        IDataset imsinfos = UCAInfoIntfViewUtil.qryGrpUserInfoByCustIdAndProId(this, custId, "8001", false);
        if (IDataUtil.isEmpty(imsinfos))
        {
            IData errresult = setRetInfo(-1, "查询集团信息失败", "只有融合V网用户才能办理此业务!");
            setAjax(errresult);
            return;
        }

        IData userInfo = imsinfos.getData(0);
        setGroupInfo(custInfo);
        setUserInfo(userInfo);
        queryTeamInfo(userInfo.getString("USER_ID"), userInfo.getString("EPARCHY_CODE"));
    }

    /**
     * 查询成员的信息
     * 
     * @param cycle
     * @throws Exception
     */

    public void queryMemInfo(IRequestCycle cycle) throws Exception
    {

        String mem_sn = getParameter("SERIAL_NUMBER", "");
        String flag = "true";
        String message = "";
        IData reuslt = new DataMap();
        // 用SN得到用户USER_ID提高下一步查询效率
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", mem_sn);

        IData userdata = UCAInfoIntfViewUtil.qryMebUserInfoBySn(this, mem_sn, false);
        if (userdata == null || userdata.size() == 0)
        {
            flag = "false";
            message = "根据成员服务号码[" + mem_sn + "]查询成员用户资料不存在！";
            reuslt.put("RESULTMESSAGE", message);
            reuslt.put("FLAG", flag);
            setAjax(reuslt);
            return;
        }
        if (!"05".equals(userdata.getString("NET_TYPE_CODE")))
        {
            flag = "false";
            message = "只有固话号码允许添加为组成员！";
            reuslt.put("RESULTMESSAGE", message);
            reuslt.put("FLAG", flag);
            setAjax(reuslt);
            return;
        }
        String user_id_mem = userdata.getString("USER_ID", "");
        String user_id_grp = getParameter("USER_ID_A", "");
        String eparchyCode = userdata.getString("EPARCHY_CODE");
        IDataset uuinfos = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdBAndUserIdARelationTypeCode(this, user_id_mem, user_id_grp, "20", eparchyCode);
        if (uuinfos == null || uuinfos.size() == 0)
        {
            flag = "false";
            message = "您输入的成员服务号码[" + mem_sn + "]不是该集团的融合V网成员，业务不能办理！";
            reuslt.put("RESULTMESSAGE", message);
            reuslt.put("FLAG", flag);
            setAjax(reuslt);
            return;
        }

        inparam.clear();
        inparam.put("USER_ID", user_id_mem);
        inparam.put("SERVICE_ID", "10122817");
        inparam.put(Route.ROUTE_EPARCHY_CODE, userdata.getString("EPARCHY_CODE"));
        IDataset result = CSViewCall.call(this, "CS.UserSvcInfoQrySVC.querySvcByUserIDandSVC", inparam);

        if (null == result || result.size() == 0)
        {
            flag = "false";
            message = "服务号码[" + mem_sn + "]没有开通闭合群功能，请在多媒体桌面电话成员变更增加！";
            reuslt.put("RESULTMESSAGE", message);
            reuslt.put("FLAG", flag);
            setAjax(reuslt);
            return;
        }
        setMeminfo(userdata);// 设置成员用户信息

        // 查询成员的闭合群列表
        inparam.clear();

        // 闭合群用户的虚拟ID
        IDataset outMemCloseInfo = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdBAndUserIdARelationTypeCode(this, user_id_mem, "", "MB", eparchyCode);

        if (null != outMemCloseInfo && outMemCloseInfo.size() > 0)
        {
            setMemTeamDatas(outMemCloseInfo);// 设置成员群信息
        }
        reuslt.put("RESULTMESSAGE", message);
        reuslt.put("FLAG", flag);
        reuslt.put("MEM_USER_ID", user_id_mem);
        setAjax(reuslt);

    }

    /**
     * @author liuzz
     * @param cycle
     * @throws Exception
     */
    public void queryTeamInfo(String userId, String eparchyCode) throws Exception
    {
        IDataset outparams = new DatasetList();
        // 查询寻呼组信息
        IDataset xhInfos = RelationUUInfoIntfViewUtil.qryGrpRelaUUInfosByUserIdBAndUserIdARelationTypeCode(this, "", userId, "XH");

        if (null != xhInfos && xhInfos.size() > 0)
        {
            setXhTeamInfos(xhInfos);
            outparams.addAll(xhInfos);
        }
        // 查询代答组信息
        IDataset ddInfos = RelationUUInfoIntfViewUtil.qryGrpRelaUUInfosByUserIdBAndUserIdARelationTypeCode(this, "", userId, "DD");

        if (null != ddInfos && ddInfos.size() > 0)
        {
            setDdTeamInfos(ddInfos);
            outparams.addAll(ddInfos);
        }

        if (outparams.size() > 0)
        {
            for (int i = 0; i < outparams.size(); i++)
            {
                if (outparams.getData(i).getString("RELATION_TYPE_CODE").equals("XH"))
                {
                    outparams.getData(i).put("TEAM_NAME", "寻呼组标识 : " + outparams.getData(i).getString("USER_ID_B"));
                }
                else if (outparams.getData(i).getString("RELATION_TYPE_CODE").equals("DD"))
                {
                    outparams.getData(i).put("TEAM_NAME", "代答组标识 : " + outparams.getData(i).getString("USER_ID_B"));
                }
            }
            setClosegrplist(outparams);
        }
    }

    /**
     * 查询成员的信息
     * 
     * @param cycle
     * @throws Exception
     */

    public void queryMemInfoForTeam(IRequestCycle cycle) throws Exception
    {
        String user_id_grp = getParameter("USER_ID_A", "");

        // 闭合群用户的虚拟ID
        IDataset outMemCloseInfo = RelationUUInfoIntfViewUtil.qryGrpRelaUUInfosByUserIdBAndUserIdARelationTypeCode(this, "", user_id_grp, "MB");
        if (null != outMemCloseInfo && outMemCloseInfo.size() > 0)
        {
            setMemTeamDatas(outMemCloseInfo);// 设置成员群信息
        }

    }

    public abstract void setCondition(IData condition);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setGroupInfo(IData groupInfo);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setXhTeamInfo(IData xhTeamInfo);

    public abstract void setXhTeamInfos(IDataset xhTeamInfos);

    public abstract void setDdTeamInfo(IData ddTeamInfo);

    public abstract void setDdTeamInfos(IDataset ddTeamInfos);

    public abstract void setUserInfo(IData userInfo);

    public abstract void setMeminfo(IData meminfo);

    public abstract void setMemTeamDatas(IDataset memTeamDatas);

    public abstract void setClosegrplist(IDataset closegrplist);
}
