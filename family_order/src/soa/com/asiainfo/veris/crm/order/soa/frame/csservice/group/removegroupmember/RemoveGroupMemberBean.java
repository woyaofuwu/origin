
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.removegroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserImpuInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember.RemoveGrpMemReqData;

public class RemoveGroupMemberBean extends MemberBean
{
    protected RemoveGrpMemReqData reqData = null;

    public RemoveGroupMemberBean()
    {

    }

    /**
     * 生成其它台帐数据（生成台帐后） *
     * 
     * @throws Exception
     */
    @Override
    public void actTradeSub() throws Exception
    {

        super.actTradeSub();

        // 用户资料注销
        infoRegDataUser();

        // UU表注销
        infoRegDataRelation();

        // 付费关系注销
        infoRegDataPayRelation();

        // 产品子表注销
        infoRegDataProduct();

        // 优惠子表注销
        infoRegDataDiscnt();

        // 服务子表注销
        infoRegDataSvc();

        // 资源子表注销
        infoRegDataRes();

        // 个性参数表注销
        makRegParamData();

        // IMPU参数表注销
        infoRegDataIMPU();

    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new RemoveGrpMemReqData();
    }

    /**
     * 优惠子表注销
     * 
     * @author liaoyi
     * @throws Exception
     */
    public void infoRegDataDiscnt() throws Exception
    {

        IData inparams = new DataMap();
        inparams.put("USER_ID", reqData.getUca().getUserId());
        IDataset dataset = UserDiscntInfoQry.getAllValidDiscntByUserId(reqData.getUca().getUserId());

        for (int i = 0, size = dataset.size(); i < size; i++)
        {
            dataset.getData(i).put("ELEMENT_ID", dataset.getData(i).getString("DISCNT_CODE"));
            dataset.getData(i).put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            dataset.getData(i).put("END_DATE", SysDateMgr.getSysTime());
        }

        addTradeDiscnt(dataset);
    }

    /**
     * IMPU子表注销
     * 
     * @throws Exception
     */
    public void infoRegDataIMPU() throws Exception
    {

        String userId = reqData.getUca().getUserId();
        String eparchyCode = reqData.getUca().getUser().getEparchyCode();

        IDataset dataset = UserImpuInfoQry.queryUserImpuInfo(userId, eparchyCode);

        if (IDataUtil.isEmpty(dataset))
            return;

        for (int i = 0, size = dataset.size(); i < size; i++)
        {
            dataset.getData(i).put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            dataset.getData(i).put("END_DATE", SysDateMgr.getSysTime());
        }

        addTradeImpu(dataset);
    }

    /**
     * OTHER子表注销
     * 
     * @author liaoyi
     * @throws Exception
     */
    public void infoRegDataOther() throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", reqData.getUca().getUserId());
        IDataset dataset = UserOtherInfoQry.getUserOtherInfoByAllUserId(inparams);
        String productId = reqData.getUca().getProductId();
        for (int i = 0, size = dataset.size(); i < size; i++)
        {
            dataset.getData(i).put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            dataset.getData(i).put("END_DATE", getAcceptTime());
            if("801111".equals(productId)){
                //用于服务开通，注销用1
                dataset.getData(i).put("OPER_CODE", "1");
            }
        }

        this.addTradeOther(dataset);
    }

    /**
     * 注销付费关系
     * 
     * @throws Exception
     */
    public void infoRegDataPayRelation() throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", reqData.getUca().getUserId());
        IData dataset = UcaInfoQry.qryDefaultPayRelaByUserId(reqData.getUca().getUserId());

        if (IDataUtil.isEmpty(dataset))
        {
            return;
        }

        dataset.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
        dataset.put("END_DATE", SysDateMgr.getSysTime());
        dataset.put("END_CYCLE_ID", SysDateMgr.getSysDate("yyyyMM"));

        this.addTradePayrelation(dataset);
    }

    /**
     * 产品子表注销
     * 
     * @throws Exception
     */
    public void infoRegDataProduct() throws Exception
    {
        String userID = reqData.getUca().getUserId();
        IDataset dataset = UserProductInfoQry.queryProductByUserId(userID);

        for (int i = 0, size = dataset.size(); i < size; i++)
        {
            dataset.getData(i).put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            dataset.getData(i).put("END_DATE", SysDateMgr.getSysTime());
        }

        this.addTradeProduct(dataset);
    }

    /**
     * 关系子表注销
     * 
     * @throws Exception
     */
    public void infoRegDataRelation() throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID_B", reqData.getUca().getUserId());

        String userIdB = reqData.getUca().getUserId();

        IDataset dataset = RelaUUInfoQry.qryRelaByUserIdB(userIdB, null);
        for (int i = 0, size = dataset.size(); i < size; i++)
        {
            dataset.getData(i).put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            dataset.getData(i).put("END_DATE", SysDateMgr.getSysTime());
            dataset.getData(i).put("INST_ID", "0"); // 实例标识
        }
        this.addTradeRelation(dataset);
    }

    /**
     * 资源子表注销
     * 
     * @throws Exception
     */
    public void infoRegDataRes() throws Exception
    {

        IDataset dataset = UserResInfoQry.getUserResInfoByUserId(reqData.getUca().getUserId());

        for (int i = 0, size = dataset.size(); i < size; i++)
        {
            dataset.getData(i).put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            dataset.getData(i).put("END_DATE", SysDateMgr.getSysTime());
        }

        this.addTradeRes(dataset);
    }

    /**
     * 服务子表注销
     * 
     * @throws Exception
     */
    public void infoRegDataSvc() throws Exception
    {
        String userID = reqData.getUca().getUserId();
        IDataset dataset = UserSvcInfoQry.queryUserSvcByUserIdAll(userID);

        for (int i = 0, size = dataset.size(); i < size; i++)
        {
            dataset.getData(i).put("ELEMENT_ID", dataset.getData(i).getString("SERVICE_ID"));
            dataset.getData(i).put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            dataset.getData(i).put("END_DATE", SysDateMgr.getSysTime());
        }

        this.addTradeSvc(dataset);
    }

    /**
     * 注销用户资料
     * 
     * @author liaoyi
     * @throws Exception
     */
    public void infoRegDataUser() throws Exception
    {
        IData userData = reqData.getUca().getUser().toData();

        if (userData != null)
        {
            userData.put("REMOVE_TAG", "2"); // 注销标志：0-正常、1-主动预销号、2-主动销号、3-欠费预销号、4-欠费销号、5-开户返销、6-过户注销
            userData.put("DESTROY_TIME", SysDateMgr.getSysTime());
            userData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());// 修改标志
            userData.put("REMOVE_EPARCHY_CODE", reqData.getUca().getUser().getEparchyCode()); // 注销地市
            userData.put("REMOVE_CITY_CODE", reqData.getUca().getUser().getCityCode()); // 注销市县
            userData.put("REMOVE_DEPART_ID", CSBizBean.getVisit().getDepartId()); // 注销渠道
            userData.put("REMOVE_REASON_CODE", reqData.getReason_code()); // 注销原因
            userData.put("USER_STATE_CODESET", "1"); // 用户主体服务状态集：见服务状态参数表
            userData.put("REMARK", reqData.getRemark());
        }

        this.addTradeUser(userData);
    }

    protected void initProductCtrlInfo() throws Exception
    {

    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();
        reqData = (RemoveGrpMemReqData) getBaseReqData();
    }

    /**
     * 注销impu信息
     * 
     * @throws Exception
     */
    public void insTradeDataForIms() throws Exception
    {
        String userId = reqData.getUca().getUser().getUserId();
        String serialNumber = reqData.getUca().getUser().getSerialNumber();
        String eparchyCode = reqData.getUca().getUser().getEparchyCode();

        String flag = "";

        if (RouteInfoQry.isChinaMobile(serialNumber))
            flag = "1";
        else
            flag = "0";

        // 查询是否存在IMPU信息
        IDataset dataset = UserImpuInfoQry.queryUserImpuInfoByUserType(userId, flag, eparchyCode);

        if (IDataUtil.isNotEmpty(dataset))
        {
            for (int i = 0, size = dataset.size(); i < size; i++)
            {
                dataset.getData(i).put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                dataset.getData(i).put("END_DATE", getAcceptTime());
            }
            addTradeImpu(dataset);
        }
    }

    /**
     * mak登记参数数据(包括服务,资费,服务参数,资费参数,产品参数)
     * 
     * @throws Exception
     */
    public void makRegParamData() throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", reqData.getUca().getUserId());
        IDataset dataset = UserAttrInfoQry.getUserAttrByUserId(reqData.getUca().getUserId());

        for (int i = 0, size = dataset.size(); i < size; i++)
        {
            dataset.getData(i).put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            dataset.getData(i).put("END_DATE", SysDateMgr.getSysTime());
        }

        this.addTradeAttr(dataset);
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        reqData.setReason(map.getString("REMOVE_REASON"));
        reqData.setReason_code(map.getString("REMOVE_REASON_CODE"));
    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        makUcaForMeb(map);
    }

    protected void makUcaForMeb(IData map) throws Exception
    {
        String serialNumber = map.getString("SERIAL_NUMBER");
        UcaData uca = UcaDataFactory.getNormalUcaForGrp(serialNumber, true, true);

        reqData.setUca(uca);

        String productId = uca.getProductId();
        String brandCode = UProductInfoQry.getBrandCodeByProductId(productId);
        String userIdB = uca.getUserId();

        // 构建集团UCA
        UcaData grpUca = new UcaData();

        // 构建虚的集团用户信息
        UserTradeData grpUserData = new UserTradeData();

        if (brandCode.equals("SRLG"))
        {
            IDataset dataset = RelaUUInfoQry.getRelaUUInfoByUserRelarIdB(userIdB, "81", null);
            IData data = dataset.getData(0);
            String userIdA = data.getString("USER_ID_A");
            String serialNumberA = data.getString("SERIAL_NUMBER_A");

            grpUserData.setCustId("-1");
            grpUserData.setUserId(userIdA);
            grpUserData.setSerialNumber(serialNumberA);
        }
        else
        {
            grpUserData.setCustId("-1");
            grpUserData.setUserId("-1");
            grpUserData.setSerialNumber("-1");
        }

        grpUca.setUser(grpUserData);
        reqData.setGrpUca(grpUca);

    }

    /**
     * 处理台帐主表的数据
     * 
     * @throws Exception
     */
    @Override
    protected void regTrade() throws Exception
    {
        IData data = bizData.getTrade();
        data.put("BATCH_ID", ""); // 批次号
        data.put("ORDER_ID", ""); // 订单标识
        data.put("CUST_ID", reqData.getUca().getCustomer().getCustId()); // 客户标识
        data.put("CUST_NAME", reqData.getUca().getCustomer().getCustName()); // 客户名称
        data.put("USER_ID", reqData.getUca().getUser().getUserId()); // 用户标识
        data.put("ACCT_ID", reqData.getUca().getAccount().getAcctId()); // 帐户标识
        data.put("SERIAL_NUMBER", reqData.getUca().getUser().getSerialNumber()); // 服务号码
        data.put("NET_TYPE_CODE", "G"); // 网别编码
        data.put("EPARCHY_CODE", reqData.getUca().getUser().getEparchyCode()); // 归属地州
        data.put("CITY_CODE", CSBizBean.getVisit().getCityCode()); // 归属业务区
        data.put("PRODUCT_ID", reqData.getUca().getProductId()); // 产品标识
        data.put("BRAND_CODE", reqData.getUca().getBrandCode()); // 品牌编码
        data.put("CUST_ID_B", "-1"); // 客户标识B：关联业务中的B客户标识，通常为一集团客户或虚拟客户。对于非关联业务填-1。
        data.put("USER_ID_B", "-1"); // 用户标识B：关联业务中的B用户标识，通常为一集团用户或虚拟用户。对于非关联业务填-1。
        data.put("ACCT_ID_B", "-1"); // 帐户标识B：关联业务中的B帐户标识，通常为一集团帐户或虚拟帐户。对于非关联业务填-1。
        data.put("SERIAL_NUMBER_B", "-1"); // 服务号码B

        data.put("RSRV_STR1", SysDateMgr.getSysTime().substring(0, 10));
        data.put("RSRV_STR2", reqData.getReason());
        data.put("RSRV_STR3", "");
        data.put("RSRV_STR4", "");
        data.put("RSRV_STR5", "");
        data.put("RSRV_STR6", "");
        data.put("RSRV_STR7", "");
        data.put("RSRV_STR8", "");
        data.put("RSRV_STR9", "");
        data.put("RSRV_STR10", "");
    }

    @Override
    public String setTradeTypeCode() throws Exception
    {
        String tradeTypeCode = "3613";
        return tradeTypeCode;
    }
}
