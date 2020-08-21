
package com.asiainfo.veris.crm.order.soa.group.outgrpmainbean;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;

public class DaidaGrpAddBean extends MemberBean
{
    boolean ifCentrex = false; // 是否融合网

    protected DaidaGrpAddBeanReqData reqData = null;

    /**
     * 生成台帐表其它数据（拼台帐前）
     * 
     * @author tengg
     * @throws Exception
     */
    @Override
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author tengg
     * @throws Exception
     */
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        infoRegDataRelation(); // 湖南网外都走UU

        infoRegDataUser(); // 代答组用户资料

        // centrex 查other表发集团配置业务指令
        infoRegDataCentreOther();
    }

    protected BaseReqData getReqData() throws Exception
    {
        return new DaidaGrpAddBeanReqData();
    }

    /**
     * 融合V网 登记平台other表
     * 
     * @throws Exception
     */
    public void infoRegDataCentreOther() throws Exception
    {
        IDataset userVpnList = UserVpnInfoQry.qryUserVpnByUserId(reqData.getGrpUca().getUserId());
        if (IDataUtil.isNotEmpty(userVpnList))
        {
            if (!ifCentrex)
            {
                return;
            }
            IDataset dataset = new DatasetList();
            IData centreData = new DataMap();

            centreData.put("USER_ID", reqData.getGrpUca().getUserId());
            centreData.put("RSRV_VALUE_CODE", "CNTRX");// domain域
            centreData.put("RSRV_VALUE", "融合V网");

            centreData.put("RSRV_STR9", "800"); // 服务id
            centreData.put("OPER_CODE", "21"); // 操作类型
            centreData.put("RSRV_STR10", userVpnList.getData(0).getString("VPN_NO", ""));
            centreData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            centreData.put("START_DATE", getAcceptTime());
            centreData.put("END_DATE", SysDateMgr.getTheLastTime());
            centreData.put("INST_ID", SeqMgr.getInstId());
            dataset.add(centreData);
            addTradeOther(dataset);
        }
    }

    public void infoRegDataRelation() throws Exception
    {
        IData rela = new DataMap();
        rela.put("RELATION_TYPE_CODE", "DD"); // 代答组业务
        rela.put("USER_ID_A", reqData.getGrpUca().getUserId()); // 集团user_id
        rela.put("SERIAL_NUMBER_A", reqData.getGrpUca().getSerialNumber()); // 集团sn
        rela.put("USER_ID_B", reqData.getUca().getUserId()); // 代答组user_id
        rela.put("SERIAL_NUMBER_B", reqData.getUca().getSerialNumber()); // 代答组sn
        rela.put("ROLE_CODE_A", "0");
        rela.put("ROLE_CODE_B", "1");
        rela.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        rela.put("START_DATE", getAcceptTime());
        rela.put("RSRV_STR1", reqData.getACCESS_CODE()); // 代答码
        rela.put("END_DATE", SysDateMgr.getTheLastTime());

        String ind_id = SeqMgr.getInstId();
        rela.put("INST_ID", ind_id);
        this.addTradeRelation(rela);
    }

    /**
     * 添加用户资料
     * 
     * @throws Exception
     */
    public void infoRegDataUser() throws Exception
    {
        IData userData = new DataMap(); // 代答组用户信息(虚拟用户)

        userData.put("USER_ID", reqData.getUca().getUserId()); // 代答组user_id
        userData.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber()); // 代答组sn

        userData.put("CUST_ID", reqData.getUca().getUser().getCustId()); // 代答组客户
        userData.put("USECUST_ID", "-1");

        userData.put("IN_DATE", getAcceptTime());
        userData.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId());
        userData.put("IN_DEPART_ID", CSBizBean.getVisit().getDepartId());

        userData.put("OPEN_MODE", "0");
        userData.put("OPEN_DATE", getAcceptTime());
        userData.put("OPEN_STAFF_ID", CSBizBean.getVisit().getStaffId());
        userData.put("OPEN_DEPART_ID", CSBizBean.getVisit().getDepartId());
        userData.put("REMOVE_TAG", "0");
        userData.put("ACCT_TAG", "Z"); // 不出账
        userData.put("USER_TYPE_CODE", "0"); // 普通用户
        userData.put("MPUTE_MONTH_FEE", "0"); // 固定费用重算标志：0-不重算，1-重算，2-从月初开始重算
        userData.put("PREPAY_TAG", "0"); // 预付费标志：0-后付费，1-预付费。（省内标准）
        userData.put("USER_STATE_CODESET", "0");
        userData.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        userData.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());

        userData.put("RSRV_STR1", reqData.getACCESS_CODE()); // 代答码
        userData.put("RSRV_STR4", "1"); // 组类型- 0：寻呼组 1：代答组 2:闭合用户群组

        userData.put("NET_TYPE_CODE", "G");
        userData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        this.addTradeUser(userData);
    }

    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (DaidaGrpAddBeanReqData) getBaseReqData();
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        reqData.setACCESS_CODE(map.getString("RSRV_STR1")); // 代答码
        reqData.setVPN_NO(map.getString("VPN_NO")); // 集团vpn_no
    }

    protected void makUca(IData map) throws Exception
    {
        makUcaForMebClose(map);
    }

    /**
     * 手动构建代答组uca
     * 
     * @param map
     * @throws Exception
     */
    protected void makUcaForMebClose(IData map) throws Exception
    {
        /** 处理闭合群的新增ID只能为1-9999的序列 */
        String user_id_b_closegrp = SeqMgr.getOutGrpId();
        int len = user_id_b_closegrp.length();
        if (len > 4)
        {
            user_id_b_closegrp = user_id_b_closegrp.substring(len - 4);
        }
        String seqVpmnId = SeqMgr.getVpmnIdIdForGrp();

        UcaData ucaData = DataBusManager.getDataBus().getUca(seqVpmnId);

        if (ucaData == null)
        {
            ucaData = new UcaData();
            IData userInfo = new DataMap();
            userInfo.put("USER_ID", user_id_b_closegrp); // 代答组user_id
            userInfo.put("SERIAL_NUMBER", seqVpmnId); // 代答组sn
            userInfo.put("CUST_ID", "-1");

            ucaData.setUser(new UserTradeData(userInfo));
        }

        reqData.setUca(ucaData);

        String grpSerialNumber = map.getString("GRP_SERIAL_NUMBER");

        UcaData grpUCA = DataBusManager.getDataBus().getUca(grpSerialNumber);

        if (grpUCA == null)
        {
            grpUCA = UcaDataFactory.getNormalUcaByUserIdForGrp(map);
        }
        reqData.setGrpUca(grpUCA);
    }

    protected void modTradeData() throws Exception
    {
        super.modTradeData();

        IData data = bizData.getTrade();

        data.put("OLCOM_TAG", "0"); // 不发服务开通
    }

    /**
     * 处理台帐主表的数据
     */
    protected void regTrade() throws Exception
    {
        IData data = bizData.getTrade();

        data.put("CUST_NAME", ""); // 客户名称
        data.put("CUST_ID", reqData.getUca().getUser().getCustId());
        data.put("ACCT_ID", "-1"); // 帐户标识
        data.put("NET_TYPE_CODE", "G"); // 网别编码
        data.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode()); // 归属地州
        data.put("CITY_CODE", CSBizBean.getVisit().getCityCode()); // 归属业务区
        data.put("PRODUCT_ID", "-1"); // 产品标识
        data.put("CUST_ID_B", reqData.getGrpUca().getCustId()); // 客户标识B：关联业务中的B客户标识，通常为一集团客户或虚拟客户。对于非关联业务填-1
        data.put("ACCT_ID_B", reqData.getGrpUca().getAcctId()); // 帐户标识B：关联业务中的B帐户标识，通常为一集团帐户或虚拟帐户。对于非关联业务填-1
        data.put("BRAND_CODE", reqData.getGrpUca().getBrandCode());// 品牌编码

        data.put("USER_ID", reqData.getUca().getUserId()); // 代答组的
        data.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());

        data.put("USER_ID_B", reqData.getGrpUca().getUserId()); // 集团的
        data.put("SERIAL_NUMBER_B", reqData.getGrpUca().getSerialNumber());

        data.put("RSRV_STR1", reqData.getACCESS_CODE()); // 代答码
        data.put("RSRV_STR2", ""); // 预留字段2
        data.put("RSRV_STR3", ""); // 预留字段3
        data.put("RSRV_STR4", "1"); // 组类型- 0：寻呼组1：代答组 2:闭合用户群组

        data.put("RSRV_STR5", ""); // 预留字段5
        data.put("RSRV_STR6", ""); // 预留字段6
        data.put("RSRV_STR7", ""); // 预留字段7
        data.put("RSRV_STR8", ""); // 预留字段8
        data.put("RSRV_STR9", ""); // 预留字段9
        data.put("RSRV_STR10", reqData.getVPN_NO()); // 集团vpn_no
    }

    protected String setTradeTypeCode() throws Exception
    {
        return "1070"; // 代答组新增业务
    }
}
