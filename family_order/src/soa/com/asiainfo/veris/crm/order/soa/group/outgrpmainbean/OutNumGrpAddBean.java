
package com.asiainfo.veris.crm.order.soa.group.outgrpmainbean;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlInfo;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
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
 * 集团外网号码新增
 * 
 * @author loyoveui
 */
public class OutNumGrpAddBean extends MemberBean
{

    protected OutNumGrpAddBeanReqData reqData = null;

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

        // 检查网外号码唯一性
        checkOutGrpNum();

        // 判断是否融合V网
        checkIfCentrexVPMN(reqData.getGrpUca().getUserId());
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

        // centrex 查other表发集团配置业务指令
        infoRegDataCentreOther();
    }

    /**
     * 判断是否融合V网
     * 
     * @author:tengg
     * @param userida
     * @param useridmeb
     * @throws Exception
     */
    public void checkIfCentrexVPMN(String userIdA) throws Exception
    {
        if (StringUtils.isNotBlank(userIdA))
        {
            IDataset userVpnList = UserVpnInfoQry.qryUserVpnByUserId(userIdA);
            if (IDataUtil.isNotEmpty(userVpnList))
            {
                IData userVpnData = userVpnList.getData(0);

                reqData.setVpn_no(userVpnData.getString("VPN_NO", ""));
            }
        }
    }

    /**
     * 检查网外号码唯一性
     */
    public void checkOutGrpNum() throws Exception
    {
        String strGrpOutSnB = reqData.getUca().getSerialNumber(); // 取网外号码
        // 用SN得到集团用户USER_ID
        String user_id_a = reqData.getGrpUca().getUserId();

        // 检查网外号码唯一性
        IDataset outparams = RelaUUInfoQry.getGrpOutinfo(user_id_a, null, Route.CONN_CRM_CG);
        for (int i = 0; i < outparams.size(); i++)
        {
            IData uuinfoData = outparams.getData(i);
            String serial_number_b = uuinfoData.getString("SERIAL_NUMBER_B", "");
            if (serial_number_b.equals(strGrpOutSnB))
            {
                CSAppException.apperr(GrpException.CRM_GRP_41, strGrpOutSnB);
            }
        }
    }

    protected void getProductCtrlInfo(String productId, String ctrlType) throws Exception
    {
        // 产品控制信息
        BizCtrlInfo ctrlInfo = BizCtrlBean.qryProductCtrlInfo(productId, ctrlType);
        reqData.setProductCtrlInfo(productId, ctrlInfo);
    }

    protected BaseReqData getReqData() throws Exception
    {
        return new OutNumGrpAddBeanReqData();
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

            IDataset dataset = new DatasetList();
            IData centreData = new DataMap();

            centreData.put("USER_ID", reqData.getUca().getUserId());
            centreData.put("RSRV_VALUE_CODE", "CNTRX");// domain域
            centreData.put("RSRV_VALUE", "融合V网");

            centreData.put("RSRV_STR9", "8001"); // 服务id
            centreData.put("OPER_CODE", "03"); // 操作类型
            centreData.put("RSRV_STR10", userVpnList.getData(0).getString("VPN_NO", ""));
            centreData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            centreData.put("START_DATE", getAcceptTime());
            centreData.put("END_DATE", SysDateMgr.getTheLastTime());
            centreData.put("INST_ID", SeqMgr.getInstId());
            dataset.add(centreData);
            this.addTradeOther(dataset);
        }
    }

    public void infoRegDataRelation() throws Exception
    {
        IData rela = new DataMap();
        rela.put("USER_ID_A", reqData.getGrpUca().getUserId()); // 集团用户ID
        rela.put("SERIAL_NUMBER_A", reqData.getGrpUca().getSerialNumber()); // 集团服务号码
        rela.put("USER_ID_B", reqData.getUca().getUserId()); // 网外号码user_id
        rela.put("SERIAL_NUMBER_B", reqData.getUca().getSerialNumber()); // 网外号码
        rela.put("ROLE_CODE_A", "0");
        rela.put("ROLE_CODE_B", "1");
        rela.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        rela.put("START_DATE", getAcceptTime());
        rela.put("RSRV_STR1", "G");
        rela.put("END_DATE", SysDateMgr.getTheLastTime());
        rela.put("RELATION_TYPE_CODE", "VO"); // 网外号码
        rela.put("INST_ID", SeqMgr.getInstId());
        this.addTradeRelation(rela);
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();
        reqData = (OutNumGrpAddBeanReqData) getBaseReqData();
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        makUcaForMebOutNum(map);
    }

    /**
     * 手动构建网外号码uca
     * 
     * @param map
     * @throws Exception
     */
    protected void makUcaForMebOutNum(IData map) throws Exception
    {
        String user_id_b_outphone = SeqMgr.getUserId();
        String out_grp_num = map.getString("OUT_GRP_NUM"); // 网外号码 sn

        UcaData ucaData = DataBusManager.getDataBus().getUca(out_grp_num);

        if (ucaData == null)
        {
            ucaData = new UcaData();
            IData userInfo = new DataMap();
            userInfo.put("USER_ID", user_id_b_outphone); // 网外号码 userId
            userInfo.put("SERIAL_NUMBER", out_grp_num); // 网外号码 sn

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
    }

    /**
     * 处理台帐主表的数据
     */
    protected void regTrade() throws Exception
    {
        IData data = bizData.getTrade();

        data.put("CUST_NAME", ""); // 客户名称
        data.put("CUST_ID", "-1");
        data.put("ACCT_ID", "-1"); // 帐户标识
        data.put("NET_TYPE_CODE", "G"); // 网别编码
        data.put("EPARCHY_CODE", reqData.getGrpUca().getUser().getEparchyCode()); // 归属地州
        data.put("CITY_CODE", reqData.getGrpUca().getUser().getCityCode()); // 归属业务区
        data.put("PRODUCT_ID", "-1"); // 产品标识
        data.put("CUST_ID_B", reqData.getGrpUca().getCustId()); // 客户标识B：关联业务中的B客户标识，通常为一集团客户或虚拟客户。对于非关联业务填-1
        data.put("ACCT_ID_B", reqData.getGrpUca().getAcctId()); // 帐户标识B：关联业务中的B帐户标识，通常为一集团帐户或虚拟帐户。对于非关联业务填-1
        data.put("BRAND_CODE", reqData.getGrpUca().getBrandCode());// 品牌编码

        data.put("USER_ID", reqData.getUca().getUserId()); // 网外的
        data.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());

        data.put("USER_ID_B", reqData.getGrpUca().getUserId()); // 集团的
        data.put("SERIAL_NUMBER_B", reqData.getGrpUca().getSerialNumber());

        data.put("RSRV_STR1", ""); // 预留字段1
        data.put("RSRV_STR2", ""); // 预留字段2
        data.put("RSRV_STR3", ""); // 预留字段3
        data.put("RSRV_STR4", ""); // 预留字段4
        data.put("RSRV_STR5", ""); // 预留字段5
        data.put("RSRV_STR6", ""); // 预留字段6
        data.put("RSRV_STR7", ""); // 预留字段7
        data.put("RSRV_STR8", ""); // 预留字段8
        data.put("RSRV_STR9", ""); // 预留字段9
        data.put("RSRV_STR10", reqData.getVpn_no()); // 预留字段10
    }

    protected String setTradeTypeCode() throws Exception
    {
        return "3915"; // 集团网外号码维护
    }
}
