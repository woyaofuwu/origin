
package com.asiainfo.veris.crm.order.soa.group.outgrpmainbean;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;

/**
 * 寻呼组删除
 * 
 * @author loyoveui
 */
public class HuntGrpDelBean extends MemberBean
{
    boolean ifCentrex = false; // 是否融合网

    protected HuntGrpDelBeanReqData reqData = null;

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

        infoRegDataUser(); // 闭合群用户资料

        // centrex 查other表发集团配置业务指令
        infoRegDataCentreOther();
    }

    protected BaseReqData getReqData() throws Exception
    {
        return new HuntGrpDelBeanReqData();
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
            centreData.put("OPER_CODE", "22"); // 操作类型
            centreData.put("RSRV_STR10", userVpnList.getData(0).getString("VPN_NO", ""));
            centreData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            centreData.put("START_DATE", getAcceptTime());
            centreData.put("END_DATE", getAcceptTime()); // 立即截止
            centreData.put("INST_ID", SeqMgr.getInstId());
            dataset.add(centreData);
            addTradeOther(dataset);
        }
    }

    public void infoRegDataRelation() throws Exception
    {
        String user_id_a = reqData.getGrpUca().getUserId(); // 集团用户userId
        String user_id_b = reqData.getUca().getUserId(); // 寻呼组用户userId

        IDataset relationUUs = RelaUUInfoQry.qryUU(user_id_a, user_id_b, "XH", null);
        IData relationUU = new DataMap();
        for (int i = 0; i < relationUUs.size(); i++)
        {
            relationUU = relationUUs.getData(i);

            relationUU.put("END_DATE", getAcceptTime());
            relationUU.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
        }

        this.addTradeRelation(relationUU);
    }

    /**
     * 添加用户资料
     * 
     * @throws Exception
     */
    public void infoRegDataUser() throws Exception
    {
        // 通过USER_ID查询寻呼组的用户资料
        String user_id_bh = reqData.getUca().getUserId();
        IData userData = UcaInfoQry.qryUserInfoByUserId(user_id_bh);
        if (IDataUtil.isEmpty(userData))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_748);
        }

        userData.put("REMOVE_TAG", "2");
        userData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
        userData.put("REMOVE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode()); // 注销地市
        userData.put("REMOVE_CITY_CODE", CSBizBean.getVisit().getCityCode()); // 注销市县
        userData.put("REMOVE_DEPART_ID", CSBizBean.getVisit().getDepartId()); // 注销渠道
        userData.put("USER_STATE_CODESET", "1"); // 用户主体服务状态集：见服务状态参数表

        this.addTradeUser(userData);
    }

    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (HuntGrpDelBeanReqData) getBaseReqData();
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        reqData.setVPN_NO(map.getString("VPN_NO")); // 集团vpn_no
    }

    protected void makUca(IData map) throws Exception
    {
        makUcaForMebClose(map);
    }

    /**
     * 手动构建寻呼组uca
     * 
     * @param map
     * @throws Exception
     */
    protected void makUcaForMebClose(IData map) throws Exception
    {
        String closeSn = map.getString("SERIAL_NUMBER_B"); // 寻呼组sn
        String closeUserId = map.getString("USER_ID_B"); // 寻呼组user_id

        UcaData ucaData = DataBusManager.getDataBus().getUca(closeSn);

        if (ucaData == null)
        {
            ucaData = new UcaData();
            IData userInfo = new DataMap();
            userInfo.put("USER_ID", closeUserId); // 寻呼组user_id
            userInfo.put("SERIAL_NUMBER", closeSn); // 寻呼组sn
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

        data.put("USER_ID", reqData.getUca().getUserId()); // 寻呼组的
        data.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());

        data.put("USER_ID_B", reqData.getGrpUca().getUserId()); // 集团的
        data.put("SERIAL_NUMBER_B", reqData.getGrpUca().getSerialNumber());

        data.put("RSRV_STR1", ""); // 预留字段1
        data.put("RSRV_STR2", ""); // 预留字段1
        data.put("RSRV_STR3", reqData.getVPN_NO()); // vpn_no
        data.put("RSRV_STR4", "0"); // 组类型- 0：寻呼组1：代答组 2:闭合用户群组

        data.put("RSRV_STR5", ""); // 预留字段5
        data.put("RSRV_STR6", ""); // 预留字段6
        data.put("RSRV_STR7", ""); // 预留字段7
        data.put("RSRV_STR8", ""); // 预留字段8
        data.put("RSRV_STR9", ""); // 预留字段9
        data.put("RSRV_STR10", reqData.getVPN_NO()); // 集团vpn_no
    }

    protected String setTradeTypeCode() throws Exception
    {
        return "1071"; // 寻呼组删除业务
    }
}
