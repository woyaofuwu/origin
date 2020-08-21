
package com.asiainfo.veris.crm.order.soa.person.busi.changeTeleNber.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RentTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.PBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserRentInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changeTeleNber.order.requestdata.ChangeTeleNbrReqData;

public class ChangeTeleNbrTrade extends BaseTrade implements ITrade
{
    final static String RES_TYPE_CODE_PHONE = "N";

    final static String NOTICE_FEE_TYPE_CODE = "9004";

    final static String NOTICE_SERVICE_ID = "347";

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        ChangeTeleNbrReqData changeTeleNbrRD = (ChangeTeleNbrReqData) btd.getRD();
        // TODO Auto-generated method stub
        dealResItf(btd); // 资源接口处理

        reGeneTradeOrder(btd); // 重写客户订单台帐表部分字段
        reGeneTradeMain(btd); // 重写用户订单台帐表部分字段

        geneTradeUser(btd); // 用户资料台帐子表
        geneTradeRes(btd); // 资源台帐子表
        geneTradeActive(btd);// 修改号码
        geneTradeRelation(btd);// 修改relation表
        geneTradeRent(btd);// 租机修改号码
        dealSpeTrade(btd); // 特殊业务处理

        if (StringUtils.isNotBlank(changeTeleNbrRD.getChangteleNotice()))
            ;
        {
            geneTradeSvc(btd); // 服务台帐子表
        }
    }

    /**
     * 资源接口处理
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    private void dealResItf(BusiTradeData btd) throws Exception
    {
        ChangeTeleNbrReqData changeTeleRD = (ChangeTeleNbrReqData) btd.getRD();
        String serialNumber = changeTeleRD.getNewSerialNumber();
        String psptId = btd.getRD().getUca().getCustomer().getCustId();
        PBossCall.resPreOccupy(serialNumber, psptId);
        // inParams.put("PSPT_ID", td.getCustInfo().get("PSPT_ID"));
        // inParams.put("RES_NO",
        // pd.getData().getString("userInfo_NEW_SERIAL_NUMBER"));

        /*
         * IDataset dataset = TuxedoHelper.callTuxSvc(pd, "QRM_IChkResInfoTT", inParams, true); if(dataset == null ||
         * dataset.isEmpty() || !"0".equals(dataset.get(0, "X_RESULTCODE"))) { common.error("调用资源号码预占接口错误!"); }
         */

    }

    /**
     * 其它特殊业务处理
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    private void dealSpeTrade(BusiTradeData btd) throws Exception
    {
        IData param = new DataMap();
        // param.put("USER_ID", btd.getRD().getUca().getUserId());
        String user_id = btd.getRD().getUca().getUserId();
        // 固话无条件呼叫转移服务编码346
        // param.put("SERVICE_ID", "346"); // 无条件呼叫转移
        IDataset svcs346 = UserSvcInfoQry.getUserSvcByUserIdAndSvcId(user_id, "346");
        if (svcs346 != null && svcs346.size() > 0)
        {
            geneTradeSvc(btd, svcs346);
        }
    }

    /**
     * 重写客户订单台帐表部分字段
     * 
     * @param pd
     * @param td
     * @throws Exception
     */

    // 修改营销活动相关表的号码资料
    private void geneTradeActive(BusiTradeData btd) throws Exception
    {

        String userId = btd.getRD().getUca().getUserId();
        ChangeTeleNbrReqData changeRD = (ChangeTeleNbrReqData) btd.getRD();
        IDataset saleSet = SaleActiveInfoQry.getUserSaleActiveInfo(userId);
        String newSerialNumber = changeRD.getNewSerialNumber();
        if (IDataUtil.isNotEmpty(saleSet))
        {
            int i;
            for (i = 0; i < saleSet.size(); i++)
            {
                SaleActiveTradeData saleData = new SaleActiveTradeData(saleSet.getData(i));
                saleData.setSerialNumber(newSerialNumber);
                saleData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                btd.add(btd.getRD().getUca().getSerialNumber(), saleData);
            }
        }

    }

    // 修改relation_uu相关表的号码资料
    private void geneTradeRelation(BusiTradeData btd) throws Exception
    {

        String userId = btd.getRD().getUca().getUserId();
        ChangeTeleNbrReqData changeRD = (ChangeTeleNbrReqData) btd.getRD();
        IDataset relationset = RelaUUInfoQry.getUserRelationByUserIdB(userId);
        String newSerialNumber = changeRD.getNewSerialNumber();
        if (IDataUtil.isNotEmpty(relationset))
        {
            int i;
            for (i = 0; i < relationset.size(); i++)
            {
                RelationTradeData relationData = new RelationTradeData(relationset.getData(i));
                relationData.setSerialNumberB(newSerialNumber);
                relationData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                btd.add(btd.getRD().getUca().getSerialNumber(), relationData);
            }
        }

    }

    // 修改营销活动相关表的号码资料
    private void geneTradeRent(BusiTradeData btd) throws Exception
    {

        String userId = btd.getRD().getUca().getUserId();
        ChangeTeleNbrReqData changeRD = (ChangeTeleNbrReqData) btd.getRD();
        IDataset rentSet = UserRentInfoQry.queryUserRentInfo(userId);
        String newSerialNumber = changeRD.getNewSerialNumber();
        if (IDataUtil.isNotEmpty(rentSet))
        {
            int i;
            for (i = 0; i < rentSet.size(); i++)
            {
                RentTradeData renTradeData = new RentTradeData(rentSet.getData(i));
                renTradeData.setSerialNumber(newSerialNumber);
                renTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                btd.add(btd.getRD().getUca().getSerialNumber(), renTradeData);
            }
        }

    }

    /**
     * 资源台帐子表
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    private void geneTradeRes(BusiTradeData btd) throws Exception
    {
        ChangeTeleNbrReqData changeTeleRD = (ChangeTeleNbrReqData) btd.getRD();
        IDataset resInfos = UserResInfoQry.queryUserSimInfo(changeTeleRD.getUca().getUserId(), "N");// 获取固话号码信息
        if (IDataUtil.isEmpty(resInfos))
        {
            // CAppException.("没有获取到有效的用户资源信息！"); TODO:添加异常
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有获取到有效的用户资源信息！");
        }

        ResTradeData odlResTradeData = new ResTradeData(resInfos.getData(0));

        odlResTradeData.setEndDate(SysDateMgr.getSysTime());
        odlResTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
        odlResTradeData.setRemark("因改号业务而终止原号码资源");
        btd.add(changeTeleRD.getUca().getSerialNumber(), odlResTradeData);

        // ResTradeData resTradeData = new ResTradeData();
        // resTradeData.setEndDate(SysDateMgr.getTheLastTime());
        // resTradeData.setInstId(odlResTradeData.getInstId());
        // resTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
        // resTradeData.setRemark(telUserMoveRD.getTelChangeData().getRemark());
        // resTradeData.setRsrvTag1(telUserMoveRD.getNumChangeData().getResKindCode());//
        // 资源小类
        // resTradeData.setRsrvStr1(telUserMoveRD.getNumChangeData().getSwitchId());//
        // 交换机编号
        // resTradeData.setRsrvStr2(telUserMoveRD.getNumChangeData().getSwitchType());//
        // 交换机类型

        // btd.add(changeTeleRD.getUca().getSerialNumber(), odlResTradeData);
        ResTradeData NewResTradeData = new ResTradeData();
        NewResTradeData.setUserId(changeTeleRD.getUca().getUserId());
        NewResTradeData.setUserIdA("-1");
        NewResTradeData.setResTypeCode("N");
        NewResTradeData.setResCode(changeTeleRD.getNewSerialNumber());
        NewResTradeData.setImsi("0");
        NewResTradeData.setKi("");
        NewResTradeData.setInstId(SeqMgr.getInstId());
        NewResTradeData.setCampnId("");
        NewResTradeData.setStartDate(SysDateMgr.getSysTime());
        NewResTradeData.setEndDate(SysDateMgr.getTheLastTime());
        NewResTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        NewResTradeData.setRemark(changeTeleRD.getRemark());
        NewResTradeData.setRsrvTag1(changeTeleRD.getResKindCode());// 资源小类
        NewResTradeData.setRsrvStr1(changeTeleRD.getSwitchId());// 交换机编号
        NewResTradeData.setRsrvStr2(changeTeleRD.getSwitchType());// 交换机类型
        btd.add(btd.getRD().getUca().getSerialNumber(), NewResTradeData);
    }

    /**
     * 服务台帐子表
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    private void geneTradeSvc(BusiTradeData btd) throws Exception
    {
        SvcTradeData svcData = new SvcTradeData();
        ChangeTeleNbrReqData changeTeleNbrRD = (ChangeTeleNbrReqData) btd.getRD();
        String monthString = changeTeleNbrRD.getChangteleNotice();
        int iMonths = Integer.parseInt(monthString);
        String update_time = btd.getRD().getAcceptTime();
        svcData.setUserId(btd.getRD().getUca().getUserId());
        svcData.setUserIdA("-1");
        svcData.setProductId("-1");
        svcData.setPackageId("-1");
        svcData.setElementId(NOTICE_SERVICE_ID);
        svcData.setMainTag("0");
        svcData.setInstId(SeqMgr.getInstId());
        svcData.setStartDate(update_time);
        svcData.setEndDate(SysDateMgr.getAddMonthsNowday(iMonths, update_time));
        svcData.setModifyTag("0");
        svcData.setRemark("因选择改号报知而新增的服务");

        btd.add(btd.getRD().getUca().getSerialNumber(), svcData);
    }

    /**
     * 服务台帐子表
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    private void geneTradeSvc(BusiTradeData btd, IDataset services) throws Exception
    {
        IDataset svcTradeDatas = new DatasetList();

        // String tradeId = td.getTradeId();
        String update_time = btd.getRD().getAcceptTime();
        String update_staff_id = getVisit().getStaffId();
        String update_depart_id = getVisit().getDepartId();

        for (int i = 0; i < services.size(); i++)
        {
            SvcTradeData svcData = new SvcTradeData(services.getData(i));
            // SvcTradeData svcData = (SvcTradeData)services.getData(i);
            // svcData.setInstId(svcData.getInstId());
            svcData.setUserId(btd.getRD().getUca().getUserId());
            svcData.setEndDate(update_time);
            svcData.setModifyTag("1");
            svcData.setRemark("因改号业务而终止该服务");
            // svcData.se("UPDATE_STAFF_ID", update_staff_id);
            // svcData.put("UPDATE_DEPART_ID", update_depart_id);
            btd.add(btd.getRD().getUca().getSerialNumber(), svcData);
        }

    }

    /**
     * 用户资料台帐子表
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    private void geneTradeUser(BusiTradeData btd) throws Exception
    {
        UserTradeData userTradeData = btd.getRD().getUca().getUser().clone();

        String userId = btd.getRD().getUca().getUserId();

        userTradeData.setUserId(userId);
        userTradeData.setCustId(btd.getRD().getUca().getCustId());
        userTradeData.setModifyTag("2");
        ChangeTeleNbrReqData changetelNbrData = (ChangeTeleNbrReqData) btd.getRD();
        userTradeData.setSerialNumber(changetelNbrData.getNewSerialNumber());
        // userTradeData.put("USECUST_ID",
        // btd.getRD().getUca());--确认USECUST_ID怎么放入
        // userTradeData.put("PRODUCT_ID", btd.getRD().getUca().getProductId());
        // userTradeData.put("CITY_CODE", getVisit().getCityCode());
        // userTradeData.setUserPasswd(btd.getRD().getUca()); --确认
        // userTradeData.put("USER_TYPE_CODE",
        // userInfo.getString("USER_TYPE_CODE"));
        // userTradeData.put("USER_STATE_CODESET",
        // userInfo.getString("USER_STATE_CODESET"));
        // userTradeData.put("SERIAL_NUMBER", td.getSerialNumber());
        // userTradeData.put("SCORE_VALUE", userInfo.getString("SCORE_VALUE"));
        // userTradeData.put("CREDIT_CLASS",
        // userInfo.getString("CREDIT_CLASS"));
        // userTradeData.put("BASIC_CREDIT_VALUE",
        // userInfo.getString("BASIC_CREDIT_VALUE","0"));
        // userTradeData.put("CREDIT_VALUE",
        // userInfo.getString("CREDIT_VALUE"));
        // userTradeData.put("ACCT_TAG", userInfo.getString("ACCT_TAG"));
        // userTradeData.put("PREPAY_TAG", userInfo.getString("PREPAY_TAG"));
        // userTradeData.put("MPUTE_MONTH_FEE",
        // userInfo.getString("MPUTE_MONTH_FEE"));
        // userTradeData.put("IN_DATE", userInfo.getString("IN_DATE"));
        // userTradeData.put("IN_STAFF_ID", userInfo.getString("IN_STAFF_ID"));
        // userTradeData.put("IN_DEPART_ID",
        // userInfo.getString("IN_DEPART_ID"));
        // userTradeData.put("OPEN_MODE", userInfo.getString("OPEN_MODE"));
        // userTradeData.put("OPEN_DATE", userInfo.getString("OPEN_DATE"));
        // userTradeData.put("REMOVE_TAG", userInfo.getString("REMOVE_TAG"));
        // userTradeData.put("MODIFY_TAG", "2");
        // userTradeData.put("UPDATE_TIME", update_time);
        // userTradeData.put("UPDATE_STAFF_ID", update_staff_id);
        // userTradeData.put("UPDATE_DEPART_ID", update_depart_id);
        btd.add(btd.getRD().getUca().getSerialNumber(), userTradeData);

        // td.addTradeDetailSeg(tradeId, userTradeData,
        // X_TRADE_DATA.X_TRADE_USER);
    }

    /**
     * 重写用户订单台帐表部分字段
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    private void reGeneTradeMain(BusiTradeData btd) throws Exception
    {
        ChangeTeleNbrReqData changeTeleNbrRD = (ChangeTeleNbrReqData) btd.getRD();
        String newSeriNum = changeTeleNbrRD.getNewSerialNumber();
        btd.getMainTradeData().setRsrvStr1(newSeriNum);
        btd.getMainTradeData().setSubscribeType("300");
        btd.getMainTradeData().setPfType("300");
        btd.getMainTradeData().setOlcomTag("1");
        btd.getMainTradeData().setRemark(changeTeleNbrRD.getRemark());
    }

    /**
     * 重写客户订单台帐表部分字段
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    private void reGeneTradeOrder(BusiTradeData btd) throws Exception
    {
        /*
         * td.setChildOrderInfo(X_TRADE_ORDER.X_CUST_ORDER, "APP_TYPE", "300");
         * td.setChildOrderInfo(X_TRADE_ORDER.X_CUST_ORDER, "ORDER_INSTANCE_STATE", "0"); }
         */
    }
}
