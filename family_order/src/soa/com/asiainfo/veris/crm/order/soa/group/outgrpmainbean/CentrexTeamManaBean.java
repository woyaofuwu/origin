
package com.asiainfo.veris.crm.order.soa.group.outgrpmainbean;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

public class CentrexTeamManaBean extends GroupBean
{
    private String rsrvStr1 = ""; // 扩展1

    private String rsrvStr3 = ""; // 扩展3

    private String rsrvStr4 = ""; // 扩展4

    private String rsrvStr5 = ""; // 扩展5

    private String rsrvStr6 = ""; // 扩展6

    private String memNumber = ""; // 组成员号码

    private String memUserId = ""; // USER_ID_B(成员用户ID或者寻呼组代答组用户ID)

    private String memTeam = ""; // 成员选择组

    private String operCode = ""; // 操作方式 0新增 1删除 2修改

    protected CentrexTeamManaBeanReqData reqData = null;

    private String teamType = ""; // 组类型

    private String vpnno = "";

    public void actTradeBefore() throws Exception
    {
    }

    /**
     * 设置登记后信息
     * 
     * @author hjx
     * @throws Exception
     */
    public void actTradeSub() throws Exception
    {

        dealTeamUserInfo();

        if ("0".equals(teamType))
        {
            rsrvStr1 = reqData.getHuntType();// 寻呼组类型
            rsrvStr5 = reqData.getTeamserial();
        }
        else if ("1".equals(teamType))
        {
            rsrvStr1 = reqData.getAccessCode();// 接入码
        }
        else if ("3".equals(teamType))
        {
            rsrvStr3 = reqData.getMEBUSERINFO().getString("SERIAL_NUMBER");
        }

        String userId = reqData.getUca().getUser().getUserId();
        IDataset userVpnList = UserVpnInfoQry.qryUserVpnByUserId(userId);
        if (IDataUtil.isEmpty(userVpnList))
        {
            CSAppException.apperr(GrpException.CRM_GRP_346);
        }
        vpnno = userVpnList.getData(0).getString("VPN_NO", "");

        if (StringUtils.isBlank(rsrvStr1))
        {
            rsrvStr1 = reqData.getMEBUSERINFO().getString("USER_ID", "");
        }

        if ("3".equals(teamType))
        {// 成员群组业务
            if ("0".equals(operCode))
            {
                infoRegDataMemberCentreOther(memUserId, "860", "29", TRADE_MODIFY_TAG.Add.getValue());
            }
            else
            {
                infoRegDataMemberCentreOther(memUserId, "860", "30", TRADE_MODIFY_TAG.DEL.getValue());
            }

        }
        else
        {// 非成员群组业务，新增需要生成或删除User信息
            infoRegDataUser();
            infoRegDataProduct();
            String userida = reqData.getUca().getUser().getUserId();
            if ("0".equals(operCode))
            {
                infoRegDataCentreOther(userida, "8001", "21", TRADE_MODIFY_TAG.Add.getValue());
            }
            else
            {
                infoRegDataCentreOther(userida, "8001", "22", TRADE_MODIFY_TAG.DEL.getValue());
            }

        }
        infoRegDataRelation();

    }

    public void dealTeamUserInfo() throws Exception
    {
        IData userData = new DataMap();
        if (!"3".equals(teamType) && operCode.equals("0"))
        {
            /** 处理闭合群的新增ID只能为1-9999的序列 */
            String user_id_b_closegrp = SeqMgr.getUserId();
            String seqVpmnId = SeqMgr.getVpmnIdIdForGrp();
            userData.put("USER_ID", user_id_b_closegrp);
            userData.put("SERIAL_NUMBER", seqVpmnId);
            rsrvStr6 = SeqMgr.getOutGrpId();
        }
        else if ("3".equals(teamType))
        {
            String user_id = memTeam;
            IData userinfo = UcaInfoQry.qryUserInfoByUserId(user_id);
            if (IDataUtil.isEmpty(userinfo))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_570);
            }
            userData = userinfo;
            rsrvStr6 = userinfo.getString("RSRV_STR6");
        }
        else
        {
            IData userinfo = UcaInfoQry.qryUserInfoByUserId(memUserId);
            if (IDataUtil.isEmpty(userinfo))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_570);
            }
            userData = userinfo;
            rsrvStr6 = userinfo.getString("RSRV_STR6");
        }
        userData.put("CUST_ID", reqData.getUca().getCustGroup().getCustId());
        userData.put("USER_CUST_ID", reqData.getUca().getCustGroup().getCustId());
        reqData.setMEBUSERINFO(userData);
    }

    protected BaseReqData getReqData() throws Exception
    {
        return new CentrexTeamManaBeanReqData();
    }

    /**
     * 融合V网 登记平台other表
     * 
     * @throws Exception
     */
    public void infoRegDataCentreOther(String userida, String service_id, String opercode, String state) throws Exception
    {
        IDataset dataset = new DatasetList();
        IData CentreData = new DataMap();

        CentreData.put("USER_ID", userida);
        CentreData.put("RSRV_VALUE_CODE", "CNTRX");// domain域
        CentreData.put("RSRV_VALUE", "融合V网");

        CentreData.put("RSRV_STR9", service_id); // 服务id
        CentreData.put("RSRV_STR11", opercode); // 操作类型
        CentreData.put("OPER_CODE", opercode);
        CentreData.put("MODIFY_TAG", state);
        CentreData.put("START_DATE", getAcceptTime());
        if (state.equals(TRADE_MODIFY_TAG.DEL.getValue()))
        {
            CentreData.put("END_DATE", getAcceptTime()); // 删除操作 立即截止
        }
        else
        {
            CentreData.put("END_DATE", SysDateMgr.getTheLastTime());
        }

        CentreData.put("INST_ID", SeqMgr.getInstId());
        dataset.add(CentreData);
        addTradeOther(dataset);
    }

    /**
     * 融合V网 登记成员平台other表
     * 
     * @throws Exception
     */
    public void infoRegDataMemberCentreOther(String useridmeb, String service_id, String opercode, String state) throws Exception
    {

        IDataset dataset = new DatasetList();
        IData CentreData = new DataMap();

        CentreData.put("USER_ID", useridmeb);
        CentreData.put("RSRV_VALUE_CODE", "CNTRX");// domain域
        CentreData.put("RSRV_VALUE", "融合V网");

        CentreData.put("RSRV_STR9", service_id); // 服务id
        CentreData.put("RSRV_STR11", opercode);
        CentreData.put("OPER_CODE", opercode); // 操作类型
        CentreData.put("MODIFY_TAG", state);
        CentreData.put("START_DATE", getAcceptTime());
        if (state.equals(TRADE_MODIFY_TAG.DEL.getValue()))
        {
            CentreData.put("END_DATE", getAcceptTime()); // 删除操作 立即截止
        }
        else
        {
            CentreData.put("END_DATE", SysDateMgr.getTheLastTime());
        }
        CentreData.put("INST_ID", SeqMgr.getInstId());
        dataset.add(CentreData);

        addTradeOther(dataset);

    }

    public void infoRegDataRelation() throws Exception
    {
        String type = "";
        if ("0".equals(teamType))
        {
            type = "XH";
        }
        else if ("1".equals(teamType))
        {
            type = "DD";
        }
        else if ("3".equals(teamType))
        {
            type = "MB";
        }
        // 寻呼组\代答组\成员群组的新增
        if ("0".equals(operCode))
        {
            // 闭合群的新增的数据全部从TD中取
            IData rela = new DataMap();
            rela.put("RELATION_TYPE_CODE", type);

            if ("3".equals(teamType))
            {
                rela.put("USER_ID_A", reqData.getMEBUSERINFO().getString("USER_ID"));
                rela.put("SERIAL_NUMBER_A", reqData.getMEBUSERINFO().getString("SERIAL_NUMBER"));
                rela.put("USER_ID_B", memUserId);
                rela.put("SERIAL_NUMBER_B", memNumber);
            }
            else
            {
                rela.put("USER_ID_A", reqData.getUca().getUser().getUserId());
                rela.put("SERIAL_NUMBER_A", reqData.getUca().getUser().getSerialNumber());
                rela.put("USER_ID_B", reqData.getMEBUSERINFO().getString("USER_ID"));
                rela.put("SERIAL_NUMBER_B", reqData.getMEBUSERINFO().getString("SERIAL_NUMBER"));
            }

            rela.put("ROLE_CODE_A", "0");

            rela.put("ROLE_CODE_B", "1");

            rela.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

            rela.put("START_DATE", getAcceptTime());
            rela.put("RSRV_STR1", rsrvStr1);// 寻呼组类型或接入码
            rela.put("RSRV_STR3", rsrvStr3);// 成员组服务号码
            rela.put("RSRV_STR5", rsrvStr5);// 组号码
            rela.put("RSRV_STR4", rsrvStr6);// 组ID
            rela.put("END_DATE", SysDateMgr.getTheLastTime());

            String ind_id = SeqMgr.getInstId();
            rela.put("INST_ID", ind_id);

            this.addTradeRelation(rela);
        }
        else if ("1".equals(operCode))
        {
            // 寻呼组或代答组的删除
            String user_id_a = "";
            String user_id_b = "";
            if ("3".equals(teamType))
            {
                user_id_a = reqData.getMEBUSERINFO().getString("USER_ID");
                user_id_b = memUserId;
            }
            else
            {
                user_id_a = reqData.getUca().getUser().getUserId();
                user_id_b = reqData.getMEBUSERINFO().getString("USER_ID");
            }

            IDataset relationUUs = RelaUUInfoQry.getRelationUUbYUserIDa(user_id_a, user_id_b, null, type, null);
            IData relationUU = new DataMap();
            // 执行删除
            if (IDataUtil.isNotEmpty(relationUUs))
            {
                relationUU = relationUUs.getData(0);// 只支持单条删除
            }

            relationUU.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            relationUU.put("END_DATE", getAcceptTime());

            this.addTradeRelation(relationUU);
        }

    }

    /**
     * 添加用户资料
     * 
     * @throws Exception
     */
    public void infoRegDataUser() throws Exception
    {
        // 用户
        IData userData = reqData.getMEBUSERINFO();

        // 闭合群新增业务
        if (operCode.equals("0"))
        {
            userData.put("USER_PASSWD", "123456"); // 用户密码
            userData.put("IN_DATE", getAcceptTime());
            userData.put("OPEN_DATE", getAcceptTime());
            userData.put("OPEN_STAFF_ID", CSBizBean.getVisit().getStaffId());
            userData.put("OPEN_DEPART_ID", CSBizBean.getVisit().getDepartId());
            userData.put("REMOVE_TAG", "0");

            userData.put("DEVELOP_DEPART_ID", reqData.getUca().getUser().getDevelopDepartId()); // 发展渠道
            userData.put("DEVELOP_CITY_CODE", reqData.getUca().getUser().getDevelopCityCode()); // 发展业务区

            userData.put("RSRV_STR1", rsrvStr1);// 寻呼组类型或接入码
            userData.put("RSRV_STR4", rsrvStr4);// 组类型
            userData.put("RSRV_STR6", rsrvStr6);// 组ID
            userData.put("NET_TYPE_CODE", "G");

            userData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue()); // 应为前台传过来
        }
        // 闭合群删除业务(注销用户资料)
        else if (operCode.equals("1"))
        {
            // 通过USER_ID查询闭合群的用户资料
            String user_id_bh = userData.getString("USER_ID", "");
            IData userinfo = UcaInfoQry.qryUserInfoByUserIdForGrp(user_id_bh);
            if (IDataUtil.isEmpty(userinfo))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_748);
            }
            userData = userinfo;

            userData.put("REMOVE_TAG", "2");
            userData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());// 应为前台传过来
            userData.put("REMOVE_EPARCHY_CODE", reqData.getUca().getUser().getEparchyCode()); // 注销地市
            userData.put("REMOVE_CITY_CODE", CSBizBean.getVisit().getCityCode()); // 注销市县
            userData.put("REMOVE_DEPART_ID", CSBizBean.getVisit().getDepartId()); // 注销渠道
            userData.put("USER_STATE_CODESET", "1"); // 用户主体服务状态集：见服务状态参数表
        }
        this.addTradeUser(userData);
    }

    /**
     * 添加用户资料
     * 
     * @throws Exception
     */
    public void infoRegDataProduct() throws Exception
    {
        // 用户
        IData userData = reqData.getMEBUSERINFO();
        String userId = userData.getString("USER_ID");
        // 闭合群新增业务 产品
        if (operCode.equals("0"))
        {
            IData data = new DataMap();
            data.put("USER_ID", userId); // 用户标识
            data.put("USER_ID_A", reqData.getUca().getUser().getUserId());
            data.put("PRODUCT_ID", "-1"); // 产品标识
            data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            data.put("PRODUCT_MODE", "04"); // 产品的模式：04-生成虚拟三户资料
            data.put("BRAND_CODE", "VPMN"); // 品牌编码
            data.put("INST_ID", SeqMgr.getInstId());
            data.put("START_DATE", getAcceptTime()); // 开始时间
            data.put("END_DATE", SysDateMgr.getTheLastTime()); // 结束时间
            data.put("MAIN_TAG", "1");// 主产品标记：0-否，1-是
            this.addTradeProduct(data);
        }
        // 闭合群删除业务(注销用户产品资料)
        else if (operCode.equals("1"))
        {
            // 产品信息
            IDataset userProductInfos = UserProductInfoQry.queryUserMainProduct(userId);
            if (IDataUtil.isEmpty(userProductInfos))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_45, userId);
            }
            IData userProductInfo = userProductInfos.getData(0);
            userProductInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            userProductInfo.put("END_DATE", SysDateMgr.getSysTime());
            this.addTradeProduct(userProductInfo);
        }
    }

    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (CentrexTeamManaBeanReqData) getBaseReqData();
    }

    /* 此方法在设置setTradeTypeCode之前执行 */
    protected void makInit(IData map) throws Exception
    {
        super.makInit(map);
        operCode = map.getString("OPERCODE"); // 操作方式 0新增 1删除 2修改

        teamType = map.getString("TEAMTYPE", "");// 组类型
        memNumber = map.getString("MEMBER_MUMBER", "");
        memUserId = map.getString("TEAM_ID", "");
        memTeam = map.getString("MEMBER_TEAM", "");
        rsrvStr4 = teamType;
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        reqData.setHuntType(map.getString("HUNTTYPE", "")); // 寻呼组类型
        reqData.setAccessCode(map.getString("ACCESSCODE", "")); // 接入码
        reqData.setTeamserial(map.getString("TEAM_SERIAL", ""));
        reqData.setMEBUSERINFO(map.getData("USERINFO"));
    }

    protected void makUca(IData map) throws Exception
    {
        makUcaForGrpNormal(map);
    }

    protected void regTrade() throws Exception
    {
        super.regTrade();

        IData data = bizData.getTrade();

        if ("0".equals(teamType) || "1".equals(teamType))
        {// 寻呼组or代答组
            data.put("RSRV_STR1", rsrvStr1);// 
            data.put("RSRV_STR5", rsrvStr5);// 组号码
            data.put("RSRV_STR4", teamType);// 组类型。0：寻呼组1：代答组 2、闭合用户群组
            data.put("RSRV_STR6", rsrvStr6);// 组ID

            data.put("USER_ID", reqData.getMEBUSERINFO().getString("USER_ID"));
            data.put("SERIAL_NUMBER", reqData.getMEBUSERINFO().getString("SERIAL_NUMBER", ""));

            data.put("USER_ID_B", reqData.getUca().getUser().getUserId());
            data.put("SERIAL_NUMBER_B", reqData.getUca().getUser().getSerialNumber());
        }
        else if ("3".equals(teamType))
        {// 成员群组
            data.put("BRAND_CODE", "VPMN");
            data.put("PRODUCT_ID", "-1");
            data.put("RSRV_STR3", memTeam);// 成员闭合群号,即集团服务号码
            data.put("RSRV_STR6", rsrvStr6);// 组ID

            data.put("USER_ID", memUserId);
            data.put("SERIAL_NUMBER", memNumber);
            data.put("USER_ID_B", memTeam);
            data.put("SERIAL_NUMBER_B", reqData.getMEBUSERINFO().getString("SERIAL_NUMBER", ""));
        }
        data.put("RSRV_STR10", vpnno);
    }

    protected void setTradeBase() throws Exception
    {
        super.setTradeBase();

        IData map = bizData.getTrade();
        map.put("NET_TYPE_CODE", "G");
        String ip = CSBizBean.getVisit().getRemoteAddr();
        if (StringUtils.isNotBlank(ip) && ip.startsWith("10"))
        {
            map.put("IN_MODE_CODE", "0");
        }
        else
        {
            map.put("IN_MODE_CODE", "3");
        }
    }

    protected String setTradeTypeCode() throws Exception
    {
        // 根据操作类型设置业务类型 并设置对应的
        String strTradeTypeCode = "";

        if ("3".equals(teamType) && "0".equals(operCode))
        {
            strTradeTypeCode = "1080";// 闭合群成员新增业务
        }
        else if ("3".equals(teamType) && "1".equals(operCode))
        {
            strTradeTypeCode = "1081";// 闭合群成员删除业务
        }
        else if (!"3".equals(teamType) && "0".equals(operCode))
        {
            strTradeTypeCode = "1070";// 寻呼，代答组业务新增
        }
        else if (!"3".equals(teamType) && "1".equals(operCode))
        {
            strTradeTypeCode = "1071";// 寻呼，代答组业务删除
        }

        return strTradeTypeCode;
    }

    protected void setTradeUser(IData map) throws Exception
    {
        map.put("USER_ID", map.getString("USER_ID", reqData.getMEBUSERINFO().getString("USER_ID"))); // 用户标识
        map.put("CUST_ID", "-1"); // 归属客户标识
        map.put("USECUST_ID", "-1"); // 使用客户标识：如果不指定，默认为归属客户标识
        map.put("BRAND_CODE", "-1"); // 当前品牌编码
        map.put("USER_TYPE_CODE", map.getString("USER_TYPE_CODE", "8")); // 用户类型

        map.put("USER_STATE_CODESET", map.getString("USER_STATE_CODESET", "0")); // 用户主体服务状态集：见服务状态参数表
        map.put("SERIAL_NUMBER", map.getString("SERIAL_NUMBER", reqData.getMEBUSERINFO().getString("SERIAL_NUMBER"))); // 服务号码
        map.put("ACCT_TAG", map.getString("ACCT_TAG", "0")); // 出帐标志：0-正常处理，1-定时激活，2-待激活用户，Z-不出帐
        map.put("PREPAY_TAG", map.getString("PREPAY_TAG", "0")); // 预付费标志：0-后付费，1-预付费。（省内标准）
        map.put("MPUTE_MONTH_FEE", map.getString("MPUTE_MONTH_FEE", "0")); // 固定费用重算标志：0-不重算，1-重算，2-从月初开始重算
        map.put("IN_STAFF_ID", map.getString("IN_STAFF_ID", CSBizBean.getVisit().getStaffId())); // 建档员工
        map.put("IN_DEPART_ID", map.getString("IN_DEPART_ID", CSBizBean.getVisit().getDepartId())); // 建档渠道
        map.put("OPEN_MODE", map.getString("OPEN_MODE", "0")); // 开户方式：0-正常，1-预开未返单，2-预开已返单，3-过户新增，4-当日返单并过户
        map.put("EPARCHY_CODE", map.getString("EPARCHY_CODE", reqData.getUca().getUser().getEparchyCode()));
        map.put("CITY_CODE", map.getString("CITY_CODE", reqData.getUca().getUser().getCityCode()));
    }
}
