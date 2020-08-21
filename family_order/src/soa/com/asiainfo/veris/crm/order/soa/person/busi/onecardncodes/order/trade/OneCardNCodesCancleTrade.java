/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.onecardncodes.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.onecardncodes.order.requestdata.OneCardNCodesChangeCardReqData;

public class OneCardNCodesCancleTrade extends BaseTrade implements ITrade
{

    /**
     * @Function: createAcctTradeData()
     * @Description: 账户台帐
     * @param:
     * @return：
     */
    public void createAcctTradeData(BusiTradeData bd) throws Exception
    {
        OneCardNCodesChangeCardReqData reqData = (OneCardNCodesChangeCardReqData) bd.getRD();
        AccountTradeData accout = reqData.getUca().getAccount();
        accout.setModifyTag(BofConst.MODIFY_TAG_ADD);
        accout.setAcctDiffCode("0");
        accout.setAcctTag("0");
        accout.setNetTypeCode("00");
        accout.setDepositPriorRuleId("0");
        accout.setItemPriorRuleId("0");
        bd.add(reqData.getUca().getSerialNumber(), accout);

    }

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {

        OneCardNCodesChangeCardReqData reqData = (OneCardNCodesChangeCardReqData) bd.getRD();
        String osn = reqData.getOtherSN();
        IData ouserInfo = UcaInfoQry.qryUserInfoBySn(osn);
        
        IDataset realationInfo = RelaUUInfoQry.getUserRelation(reqData.getUca().getUserId());
        if (IDataUtil.isEmpty(realationInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询不到用户关系资料，业务无法继续！");
        }
        String useridA = realationInfo.getData(0).getString("USER_ID_A");
        IDataset allRelation = RelaUUInfoQry.getUserRelationAll(useridA);
        if (IDataUtil.isEmpty(allRelation))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询不到用户关系资料，业务无法继续！");
        }
        IData relationA = new DataMap();
        IData relationB = new DataMap();
        for(int i = 0;i<allRelation.size();i++){
        	if("1".equals(allRelation.getData(i).getString("ROLE_CODE_B", ""))){
        		relationA = allRelation.getData(i);
        	}
        	if("2".equals(allRelation.getData(i).getString("ROLE_CODE_B", ""))){
        		relationB = allRelation.getData(i);
        	}
        }

        IData userinfo = UcaInfoQry.qryUserInfoBySn(reqData.getSerialNum());

        
        createMainTable(bd, ouserInfo);
        createUserTradeData(bd,userinfo,useridA);
        createDiscntTradeData(bd,useridA);
        createRelationTradeData(bd, relationA,relationB, ouserInfo);

        bd.addOpenUserAcctDayData(reqData.getUca().getUserId(), reqData.getUca().getAcctDay());
        bd.addOpenAccountAcctDayData(reqData.getUca().getAccount().getAcctId(), reqData.getUca().getAcctDay());

    }

    /**
     * @Function: createCustomerTradeData()
     * @Description:
     * @param:
     * @return：
     */
    public void createCustomerTradeData(BusiTradeData bd) throws Exception
    {
        OneCardNCodesChangeCardReqData reqData = (OneCardNCodesChangeCardReqData) bd.getRD();
        CustomerTradeData custTD = reqData.getUca().getCustomer();
        custTD.setCustType("0");
        custTD.setCustState("0");
        custTD.setOpenLimit("0");
        custTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        bd.add(reqData.getUca().getSerialNumber(), custTD);

    }

    /**
     * @Function: createCustPersonTradeData()
     * @Description:
     * @param:
     * @return：
     */
    public void createCustPersonTradeData(BusiTradeData bd) throws Exception
    {
        OneCardNCodesChangeCardReqData reqData = (OneCardNCodesChangeCardReqData) bd.getRD();

        CustPersonTradeData custTD = reqData.getUca().getCustPerson();
        custTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        bd.add(reqData.getUca().getSerialNumber(), custTD);
    }

    /**
     * @Function: createDiscntTradeData()
     * @Description: 优惠子台帐
     * @param:
     * @return：
     */
    private void createDiscntTradeData(BusiTradeData btd, String userIdA) throws Exception
    {
        OneCardNCodesChangeCardReqData reqData = (OneCardNCodesChangeCardReqData) btd.getRD();
        
        String startTime = reqData.getUca().getUserDiscnts().get(0).getStartDate();
        
        
        UcaData uca = btd.getRD().getUca();
        List<DiscntTradeData> disDatas = uca.getUserDiscntByDiscntId("848");//主卡userid挂848优惠
        for(DiscntTradeData disData:disDatas){
        	DiscntTradeData disTrade = disData.clone();
        	disTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
        	disTrade.setEndDate(SysDateMgr.getLastSecond(btd.getRD().getAcceptTime()));
        	btd.add(reqData.getUca().getSerialNumber(), disTrade);
        }
        
        /*IData a = new DataMap(); 在付费关系取消中已经处理 不需要再次处理 sunxin
        IDataset discnt = UserDiscntInfoQry.getDiscntsByUserIdDiscntCode(userIdA, "5903", getTradeEparchyCode());
        if (IDataUtil.isEmpty(discnt))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询不到用户优惠资料，业务无法继续！");
        }
        DiscntTradeData disData =new DiscntTradeData(discnt.getData(0));
        disData.setModifyTag(BofConst.MODIFY_TAG_DEL);
        disData.setEndDate(SysDateMgr.getLastSecond(btd.getRD().getAcceptTime()));
        btd.add(reqData.getUca().getSerialNumber(), disData);*/
//        disDatas = uca.getUserDiscntByDiscntId("5903");//虚拟用户挂5903
//        for(DiscntTradeData disData:disDatas){
//        	DiscntTradeData disTrade = disData.clone();
//        	disTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
//        	disTrade.setEndDate(SysDateMgr.getLastSecond(btd.getRD().getAcceptTime()));
//        	btd.add(reqData.getUca().getSerialNumber(), disTrade);
//        }
        //主卡userid挂848优惠
//        DiscntTradeData discntTradeData = new DiscntTradeData();
//        discntTradeData.setUserId(reqData.getUca().getUserId());
//        discntTradeData.setUserIdA(userIdA);
//        discntTradeData.setPackageId("55000002");
//        discntTradeData.setProductId("5500");
//        discntTradeData.setSpecTag("2");
//        discntTradeData.setRelationTypeCode("30");
//        discntTradeData.setEndDate(SysDateMgr.getLastSecond(btd.getRD().getAcceptTime()));
//        discntTradeData.setStartDate(startTime);
//        discntTradeData.setModifyTag("1");
//        discntTradeData.setInstId(SeqMgr.getInstId());
//        discntTradeData.setElementId("848");
//        discntTradeData.setElementType("D");
//        btd.add(reqData.getUca().getSerialNumber(), discntTradeData);
        
        //虚拟用户挂5903
//        DiscntTradeData newDiscnt = new DiscntTradeData();
//        newDiscnt.setUserId(userIdA);
//        newDiscnt.setUserIdA("-1");
//        newDiscnt.setPackageId("-1");
//        newDiscnt.setProductId("-1");
//        newDiscnt.setSpecTag("2");
//        newDiscnt.setRelationTypeCode("30");
//        newDiscnt.setEndDate(SysDateMgr.getLastSecond(btd.getRD().getAcceptTime()));
//        newDiscnt.setStartDate(startTime);
//        newDiscnt.setModifyTag("1");
//        newDiscnt.setInstId(SeqMgr.getInstId());
//        newDiscnt.setElementId("5903");
//        newDiscnt.setElementType("D");
//        btd.add(reqData.getUca().getSerialNumber(), newDiscnt);
    }

    /**
     * 主台账
     * 
     * @param bd
     * @throws Exception
     */
    public void createMainTable(BusiTradeData bd, IData ouserInfo) throws Exception
    {
        OneCardNCodesChangeCardReqData repData = (OneCardNCodesChangeCardReqData) bd.getRD();

    	IDataset userResInfos =UserResInfoQry.getUserResInfosByUserIdResTypeCode(repData.getUca().getUserId(), "1");
    	String simcardM = "";
    	if(IDataUtil.isNotEmpty(userResInfos)){
    		simcardM = userResInfos.getData(0).getString("RES_CODE","");
    	}
    	
    	IDataset ouserResInfos =UserResInfoQry.getUserResInfosByUserIdResTypeCode(ouserInfo.getString("USER_ID",""), "1");
    	String simcardO = "";
    	if(IDataUtil.isNotEmpty(ouserResInfos)){
    		simcardO = ouserResInfos.getData(0).getString("RES_CODE","");
    	}
    	
        MainTradeData tradeData = bd.getMainTradeData();
        tradeData.setSubscribeType("0");
        tradeData.setNextDealTag("0");
        tradeData.setProductId(ouserInfo.getString("PRODUCT_ID"));
        tradeData.setBrandCode(ouserInfo.getString("BRAND_CODE"));
        tradeData.setFeeState("0");
        tradeData.setOlcomTag("0");
        // tradeData.setProcessTagSet("0");
        tradeData.setCancelTag("0");
        tradeData.setRsrvStr7(simcardM);// 主SIM卡
        tradeData.setRsrvStr4(ouserInfo.getString("SERIAL_NUMBER"));// 副号码
        tradeData.setRsrvStr5(simcardO);// 副SIM卡
    }

    /**
     * @Function: createRelationTradeData()
     * @Description: UU关系
     * @param:
     * @return：
     */
    private void createRelationTradeData(BusiTradeData btd, IData relationA,IData relationB, IData ouserInfo) throws Exception
    {
        OneCardNCodesChangeCardReqData reqData = (OneCardNCodesChangeCardReqData) btd.getRD();
        RelationTradeData tempRelaTradeData = new RelationTradeData();
        
        RelationTradeData relaA =  new RelationTradeData(relationA);
        relaA.setModifyTag(BofConst.MODIFY_TAG_DEL);
        relaA.setEndDate(SysDateMgr.getLastSecond(btd.getRD().getAcceptTime()));
    	btd.add(reqData.getUca().getSerialNumber(), relaA);
        
    	
    	RelationTradeData relaB =  new RelationTradeData(relationB);
    	relaB.setModifyTag(BofConst.MODIFY_TAG_DEL);
        relaB.setEndDate(SysDateMgr.getLastSecond(btd.getRD().getAcceptTime()));
    	btd.add(reqData.getUca().getSerialNumber(), relaB);
//        tempRelaTradeData.setInstId(SeqMgr.getInstId());
//        tempRelaTradeData.setUserIdA(infos.getString("USER_ID_A"));
//        tempRelaTradeData.setUserIdB(reqData.getUca().getUserId());
//        tempRelaTradeData.setSerialNumberB(reqData.getSerialNum());
//        tempRelaTradeData.setSerialNumberA("");
//        tempRelaTradeData.setRelationTypeCode("30");
//        tempRelaTradeData.setRoleTypeCode("0");
//        tempRelaTradeData.setRoleCodeA("0");
//        tempRelaTradeData.setRoleCodeB("1");
//        tempRelaTradeData.setOrderno("0");
//        tempRelaTradeData.setEndDate(SysDateMgr.getLastSecond(btd.getRD().getAcceptTime()));
//        tempRelaTradeData.setStartDate(infos.getString("START_DATE", ""));
//        tempRelaTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
//        btd.add(reqData.getSerialNum(), tempRelaTradeData);

//        RelationTradeData otempRelaTradeData = new RelationTradeData();
//        otempRelaTradeData.setInstId(SeqMgr.getInstId());
//        otempRelaTradeData.setUserIdA(infos.getString("USER_ID_A"));
//        otempRelaTradeData.setUserIdB(ouserInfo.getString("USER_ID"));
//        otempRelaTradeData.setSerialNumberB(ouserInfo.getString("SERIAL_NUMBER"));
//        otempRelaTradeData.setSerialNumberA("");
//        otempRelaTradeData.setRelationTypeCode("30");
//        otempRelaTradeData.setRoleTypeCode("0");
//        otempRelaTradeData.setRoleCodeA("0");
//        otempRelaTradeData.setRoleCodeB("2");
//        otempRelaTradeData.setOrderno("1");
//        otempRelaTradeData.setShortCode("");
//        otempRelaTradeData.setEndDate(SysDateMgr.getLastSecond(btd.getRD().getAcceptTime()));
//        otempRelaTradeData.setStartDate(infos.getString("START_DATE", ""));
//        otempRelaTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
//        btd.add(ouserInfo.getString("SERIAL_NUMBER"), otempRelaTradeData);

    }

    /**
     * @Function: createUserTradeData()
     * @Description: 用户台帐
     * @param:
     * @return：
     */
    public void createUserTradeData(BusiTradeData bd, IData userinfo, String userIdA) throws Exception
    {
        OneCardNCodesChangeCardReqData reqData = (OneCardNCodesChangeCardReqData) bd.getRD();

        UserTradeData userTD = new UserTradeData(userinfo);
        
        userTD.setUserId(userIdA);
        userTD.setCustId(userinfo.getString("CUST_ID", ""));
        userTD.setUsecustId(userinfo.getString("CUST_ID", ""));
        userTD.setCityCodeA("0");
        userTD.setUserTypeCode(userinfo.getString("USER_TYPE_CODE", ""));
        userTD.setUserStateCodeset(userinfo.getString("USER_STATE_CODESET", ""));
        userTD.setPrepayTag(userinfo.getString("PREPAY_TAG", ""));
        userTD.setMputeMonthFee(userinfo.getString("MPUTE_MONTH_FEE", ""));
        userTD.setInDate(userinfo.getString("IN_DATE", ""));
        userTD.setInStaffId(userinfo.getString("IN_STAFF_ID", ""));
        userTD.setInDepartId(userinfo.getString("IN_DEPART_ID", ""));
        userTD.setOpenMode(userinfo.getString("OPEN_MODE", ""));
        userTD.setOpenDate(userinfo.getString("OPEN_DATE", ""));
        userTD.setRemoveTag("2");
        userTD.setEparchyCode(reqData.getUca().getUser().getEparchyCode());
        userTD.setCityCode(reqData.getUca().getUser().getCityCode());
        userTD.setSerialNumber(userIdA.substring(6));
        userTD.setAcctTag(reqData.getUca().getUser().getAcctTag());
        userTD.setNetTypeCode("00");
        userTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
        bd.add(reqData.getUca().getSerialNumber(), userTD);
        
//        ProductTradeData addProductTD = new ProductTradeData();
//        addProductTD.setUserId(userIdA);
//        addProductTD.setProductId("5500");
//        addProductTD.setBrandCode("OCNC");
//        addProductTD.setStartDate(reqData.getUca().getUserProducts().get(0).getStartDate());
//        addProductTD.setEndDate(SysDateMgr.getLastSecond(bd.getRD().getAcceptTime()));
//        addProductTD.setMainTag("1");
//        addProductTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
//        addProductTD.setProductMode("04");
//        addProductTD.setUserIdA("-1");
//        addProductTD.setCampnId("0");
//        addProductTD.setInstId(SeqMgr.getInstId());
//        bd.add(reqData.getUca().getSerialNumber(), addProductTD);
        // 建立用户产品信息
        IDataset produtInfos = UserProductInfoQry.getUserProductByUserIdProductId(userIdA, "5500");
        if(IDataUtil.isNotEmpty(produtInfos)){
        	IData product =  new DataMap();
            for(int i = 0 ;i<produtInfos.size();i++){
            		product = produtInfos.getData(i);
            }
            ProductTradeData addProductTD = new ProductTradeData(product);
            addProductTD.setUserId("-1");
            addProductTD.setUserIdA(userIdA);
            addProductTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
            addProductTD.setEndDate(SysDateMgr.getLastSecond(bd.getRD().getAcceptTime()));
        	bd.add(reqData.getUca().getSerialNumber(), addProductTD);
        }
    }

}
