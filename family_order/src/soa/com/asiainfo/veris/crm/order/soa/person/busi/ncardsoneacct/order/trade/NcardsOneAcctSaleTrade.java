/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.ncardsoneacct.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.AcctDayException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherFeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAcctDayInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.ncardsoneacct.order.requestdata.NcardsOneAcctSaleReqData;

/**
 * @CREATED by gongp@2014-5-15 修改历史 Revision 2014-5-15 下午03:22:57
 */
public class NcardsOneAcctSaleTrade extends BaseTrade implements ITrade
{

    public void acctDayDeal(BusiTradeData bd, NcardsOneAcctSaleReqData reqData) throws Exception
    {

        IData mainAcctData = UserAcctDayInfoQry.getUserAcctDayAndFirstDateInfo(reqData.getUserIdMain());
        IData secondAcctData = UserAcctDayInfoQry.getUserAcctDayAndFirstDateInfo(reqData.getUserIdSecond());

        String sysDate = SysDateMgr.getSysDate();

        if (mainAcctData == null || mainAcctData.size() < 1)
            // common.error("532007:  获取用户" + td.getSerialNumber() + "有效下账期资料失败！请联系管理员。");
            CSAppException.apperr(AcctDayException.CRM_ACCTDAY_16, reqData.getSerialNumberMain());
        if (secondAcctData == null || secondAcctData.size() < 1)
            // common.error("532007:  获取用户" + td.getString("TARGET_NUMBER") + "有效下账期资料失败！请联系管理员。");
            CSAppException.apperr(AcctDayException.CRM_ACCTDAY_16, reqData.getSerialNumberSecond());

        String acctDayMain = reqData.getUca().getAcctDay();

        String acctDaySecond;

        // 如果[目标号码]下账期结账日不存在,没有预约账期
        if (secondAcctData.getString("NEXT_ACCT_DAY") == null || secondAcctData.getString("NEXT_ACCT_DAY").equals(""))
        {
            acctDaySecond = secondAcctData.getString("ACCT_DAY");
        }
        else
        {
            acctDaySecond = secondAcctData.getString("NEXT_ACCT_DAY");
            // common.error("532008: 该用户号码"+ td.getString("TARGET_NUMBER") + "存在预约的帐期，" +
            // "账期生效时间为"+acctDataNew.getString("NEXT_FIRST_DATE")+"，账期生效后才能办理双卡统一付费申请业务！ ");
            CSAppException.apperr(AcctDayException.CRM_ACCTDAY_30, reqData.getSerialNumberSecond(), secondAcctData.getString("NEXT_ACCT_DAY"));
        }

        // 如果副号码是非自然月

        if (!"1".equals(acctDaySecond))
        {

            // this.userAcctDayChange(bd, reqData, sysDate);
            bd.addChangeAcctDayData(reqData.getUserIdSecond(), "1", false);
        }

        // 如果办理号码(主号码)是非自然月
        if (!"1".equals(acctDayMain))
        {

            bd.addChangeAcctDayData(reqData.getUca().getUserId(), "1");
        }

    }

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        // TODO Auto-generated method stub

        NcardsOneAcctSaleReqData reqData = (NcardsOneAcctSaleReqData) bd.getRD();

        String user_id_a = SeqMgr.getUserId();

        this.acctDayDeal(bd, reqData);

        String serialNumberA = geneTradeUser(bd, user_id_a);

        this.geneTradeRelation(bd, reqData, user_id_a, serialNumberA);

        this.geneTradePayRelation(bd, reqData);

        this.geneTradeDiscnt(bd, reqData, user_id_a);

        // this.geneTradeMainUserAcctDay(bd, reqData, user_id_a);

        MainTradeData mainTD = bd.getMainTradeData();

        UcaData secondNoUca = UcaDataFactory.getNormalUca(reqData.getSerialNumberSecond());

        mainTD.setRsrvStr1(secondNoUca.getAcctId());// 副号acct_id
        mainTD.setRsrvStr2(reqData.getUserIdSecond());// 副号user_id
        mainTD.setRsrvStr3(DiversifyAcctUtil.getFirstDayThisAcct(reqData.getUca().getUserId())); // 当前账期
        mainTD.setRsrvStr4(reqData.getSerialNumberSecond());// 副号码
        mainTD.setRsrvStr5(this.getUserSimCardNum(reqData.getUserIdSecond())); // 副SIM卡
        mainTD.setRsrvStr7(this.getUserSimCardNum(reqData.getUserIdMain()));// 主SIM卡

    }

    public void createProductTradeData(BusiTradeData btd, String serialNumber, String userId) throws Exception
    {
        NcardsOneAcctSaleReqData reqData = (NcardsOneAcctSaleReqData) btd.getRD();

        ProductTradeData productTD = new ProductTradeData();
        productTD.setUserId(userId);
        productTD.setUserIdA("-1");
        productTD.setProductId(reqData.getUca().getProductId());
        productTD.setProductMode("00");
        productTD.setBrandCode("NCOA");
        productTD.setInstId(SeqMgr.getInstId());
        productTD.setStartDate(reqData.getAcceptTime());
        productTD.setEndDate(SysDateMgr.getTheLastTime());
        productTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        productTD.setMainTag("1");
        btd.add(serialNumber, productTD);
    }

    /**
     * 生成绑定优惠台账
     * 
     * @param bd
     * @param reqData
     * @param user_id_a
     * @throws Exception
     * @CREATE BY GONGP@2014-5-15
     */
    public void geneTradeDiscnt(BusiTradeData bd, NcardsOneAcctSaleReqData reqData, String user_id_a) throws Exception
    {

        DiscntTradeData td = new DiscntTradeData();

        td.setUserId(user_id_a);
        td.setUserIdA("-1");
        td.setPackageId("-1");
        td.setProductId("-1");
        td.setElementId("5905");
        td.setSpecTag("2");
        td.setRelationTypeCode("34");
        td.setCampnId("");
        td.setInstId(SeqMgr.getInstId());
        td.setModifyTag("0");
        td.setStartDate(reqData.getAcceptTime());
        td.setEndDate(SysDateMgr.END_TIME_FOREVER);

        bd.add(reqData.getUca().getSerialNumber(), td);

    }

    /**
     * 简单算法描述： 1.如果副卡号码原来有付费关系，则修改付费关系 2.如果副卡号码原来没有付费关系，则新增一条付费关系
     * 
     * @param bd
     * @param reqData
     * @throws Exception
     * @CREATE BY GONGP@2014-5-15
     */
    public void geneTradePayRelation(BusiTradeData bd, NcardsOneAcctSaleReqData reqData) throws Exception
    {

        IData mainPayRelations = UcaInfoQry.qryDefaultPayRelaByUserId(reqData.getUserIdMain());

        if (IDataUtil.isEmpty(mainPayRelations))
        {
            // common.error("主卡号码无默认付费帐户！");
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "主卡号码无默认付费帐户！");
        }

        IDataset userValidPayRelas = PayRelaInfoQry.qryValidPayRelationByUserId(reqData.getUserIdSecond(), "1", "1");

        if (IDataUtil.isNotEmpty(userValidPayRelas))
        {

            IData data = userValidPayRelas.getData(0);

            String acctId = data.getString("ACCT_ID");

            if ("true".equals(PayRelaInfoQry.isAcctOnly(acctId, "1", "1")))
            {

                OtherFeeTradeData feeTrade = new OtherFeeTradeData();
                feeTrade.setOperType(BofConst.OTHERFEE_DIFF_TRANS);
                feeTrade.setUserId(reqData.getUserIdSecond());
                feeTrade.setAcctId(acctId);// ???
                feeTrade.setUserId2(reqData.getUserIdSecond());
                feeTrade.setAcctId2(mainPayRelations.getString("ACCT_ID", "-1"));
                feeTrade.setPaymentId("0");
                feeTrade.setStartDate(reqData.getAcceptTime());
                feeTrade.setEndDate(SysDateMgr.END_DATE_FOREVER);
                bd.add(reqData.getSerialNumberSecond(), feeTrade);

            }

            PayRelationTradeData oldPayTd = new PayRelationTradeData(data);

            oldPayTd.setModifyTag(BofConst.MODIFY_TAG_DEL);

            oldPayTd.setEndCycleId(StringUtils.replace(SysDateMgr.addDays(SysDateMgr.decodeTimestamp(reqData.getStartCycId(), "yyyy-MM-dd"), -1), "-", ""));

            bd.add(reqData.getUca().getSerialNumber(), oldPayTd);
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未查询到副卡的默认付费帐户！");
        }

        PayRelationTradeData td = new PayRelationTradeData();

        td.setAcctId(mainPayRelations.getString("ACCT_ID", "-1"));
        td.setUserId(reqData.getUserIdSecond());
        td.setPayitemCode("-1");
        td.setAcctPriority("0");
        td.setUserPriority("0");
        td.setBindType("1");
        td.setActTag("1");
        td.setDefaultTag("1");
        td.setModifyTag("0");
        td.setLimit("0");
        td.setLimitType("0");
        td.setComplementTag("0");
        td.setAddupMethod("0");
        td.setAddupMonths("0");
        td.setInstId(SeqMgr.getInstId());
        td.setStartCycleId(reqData.getStartCycId());
        td.setEndCycleId(mainPayRelations.getString("END_CYCLE_ID"));

        bd.add(reqData.getUca().getSerialNumber(), td);
    }

    public void geneTradeRelation(BusiTradeData bd, NcardsOneAcctSaleReqData reqData, String user_id_a, String sna) throws Exception
    {

        RelationTradeData td1 = new RelationTradeData();// 主号码

        td1.setUserIdA(user_id_a);
        td1.setSerialNumberA(sna);
        td1.setUserIdB(reqData.getUserIdMain());
        td1.setSerialNumberB(reqData.getSerialNumberMain());
        td1.setRelationTypeCode("34");
        td1.setRoleCodeA("0");
        td1.setRoleCodeB("1");
        td1.setOrderno("0");
        td1.setInstId(SeqMgr.getInstId());
        td1.setStartDate(reqData.getAcceptTime());
        td1.setEndDate(SysDateMgr.END_TIME_FOREVER);
        td1.setModifyTag("0");

        bd.add(reqData.getUca().getSerialNumber(), td1);

        RelationTradeData td2 = new RelationTradeData();// 副号码

        td2.setUserIdA(user_id_a);
        td2.setSerialNumberA(sna);
        td2.setUserIdB(reqData.getUserIdSecond());
        td2.setSerialNumberB(reqData.getSerialNumberSecond());
        td2.setRelationTypeCode("34");
        td2.setRoleCodeA("0");
        td2.setRoleCodeB("2");
        td2.setOrderno("1");
        td2.setInstId(SeqMgr.getInstId());
        td2.setStartDate(reqData.getAcceptTime());
        td2.setEndDate(SysDateMgr.END_TIME_FOREVER);
        td2.setModifyTag("0");

        bd.add(reqData.getUca().getSerialNumber(), td2);
    }

    private String geneTradeUser(BusiTradeData btd, String userIdA) throws Exception
    {

        NcardsOneAcctSaleReqData reqData = (NcardsOneAcctSaleReqData) btd.getRD();
        UcaData ucaDta = reqData.getUca();

        CustomerTradeData custData = ucaDta.getCustomer();
        UserTradeData userData = new UserTradeData();// ucaDta.getUser().clone();

        String serialNumber = "34" + userIdA.substring(userIdA.length() - 8); // User表的SerialNumber
        String custId = custData.getCustId();

        userData.setUserId(userIdA);
        userData.setSerialNumber(serialNumber);
        userData.setUserTypeCode("0");
        userData.setUserStateCodeset("0");
        userData.setAcctTag("0");
        userData.setOpenMode("0");
        userData.setCustId(custId);
        userData.setUsecustId(custId);
        userData.setMputeMonthFee("0");
        userData.setNetTypeCode(ucaDta.getUser().getNetTypeCode());
        userData.setEparchyCode(ucaDta.getUser().getEparchyCode());
        userData.setCityCode(ucaDta.getUser().getCityCode());
        userData.setInDate(reqData.getAcceptTime());
        userData.setInStaffId(CSBizBean.getVisit().getStaffId());// 建档员工
        userData.setInDepartId(CSBizBean.getVisit().getDepartId());// 建档渠道
        userData.setOpenDate(reqData.getAcceptTime());// 开户时间
        userData.setOpenStaffId(CSBizBean.getVisit().getStaffId());// 开户员工
        userData.setOpenDepartId(CSBizBean.getVisit().getDepartId());// 开户渠道
        userData.setPrepayTag("1");// 预付费标记：0：后付费，1：预付费
        userData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        userData.setRemoveTag("0");

        btd.add(ucaDta.getSerialNumber(), userData);

        this.createProductTradeData(btd, serialNumber, userIdA);

        btd.addOpenUserAcctDayData(userIdA, "1");

        return serialNumber;
    }

    public String getUserSimCardNum(String userId) throws Exception
    {

        IDataset resInfos = UserResInfoQry.queryUserSimInfo(userId, "1");

        if (resInfos.size() == 0)
        {
            // common.error("获取用户资源资料无数据");
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取用户资源资料无数据");
        }
        return resInfos.getData(0).getString("RES_CODE");

    }

}
