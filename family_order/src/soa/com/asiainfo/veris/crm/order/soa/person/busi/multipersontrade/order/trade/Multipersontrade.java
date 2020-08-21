package com.asiainfo.veris.crm.order.soa.person.busi.multipersontrade.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.FamilyTradeHelper;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.FamilyMemberData;
import com.asiainfo.veris.crm.order.soa.person.busi.multipersontrade.order.requestdata.GroupCreateReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.multipersontrade.order.requestdata.GroupMemberData;
import com.asiainfo.veris.crm.order.soa.person.common.action.sms.PerSmsAction;

public class Multipersontrade extends BaseTrade implements ITrade{

	/*
	 * 组建组网/添加成员
	 * */
	@Override
	public void createBusiTradeData(BusiTradeData bd) throws Exception {

		GroupCreateReqData reqData = (GroupCreateReqData) bd.getRD();
		MainTradeData mainTD = bd.getMainTradeData();
		String processTagSet = mainTD.getProcessTagSet();
		String verifyMode = reqData.getVerifyMode();// 副卡校验方式
		UcaData uca = reqData.getUca();
        String virtualSn = "DR" + uca.getSerialNumber();
        String userId = uca.getUserId();
        
        IData user = UcaInfoQry.qryUserInfoBySn(virtualSn);
        
        if (IDataUtil.isEmpty(user))
        {
            // 虚拟用户不存在，需创组网
        	createGroup(bd);

        }
        
        IDataset userSvcList = RelaUUInfoQry.getRelationsByUserIdAndTypeAndRoleCodeB("61", userId, "1");
        if (IDataUtil.isEmpty(userSvcList)) {
        	
        	if( IDataUtil.isNotEmpty(user)){
        		CSAppException.apperr(FamilyException.CRM_GROUP_3737);
        	}
        	
			List<GroupMemberData> mebs = reqData.getMemberDataList();
			int Number = mebs.size() + 1;
			if( Number > 3 ){
				 //您的组网成员超出3个，请删除成员！
	        	CSAppException.apperr(FamilyException.CRM_GROUP_3939);
	        }
			
		}else{
			IData userSvc = userSvcList.getData(0);
			String uIdA = userSvc.getString("USER_ID_A");
			
			List<GroupMemberData> mebs = reqData.getMemberDataList();
	        IDataset mebList = RelaUUInfoQry.qryAllRelation(uIdA);
	        int Number = mebList.size() + mebs.size();
	        if( Number > 3 ){
	        	//您的组网成员超出3个，请删除成员！
	        	CSAppException.apperr(FamilyException.CRM_GROUP_3939);
	        }
		}
        
            if (StringUtils.equals(verifyMode, "1")) { // 短信
            	
            	List<GroupMemberData> mebDataList = reqData.getMemberDataList();
            	for (int i = 0, size = mebDataList.size(); i < size; i++) {
            		GroupMemberData memberData = mebDataList.get(i);
                    UcaData ucaData = memberData.getUca();
                    String memberSN = ucaData.getSerialNumber();// 成员号码
                    IData ucaB = UcaInfoQry.qryUserInfoBySn(memberSN);
                    
                    String smsContent = uca.getSerialNumber()+"现正在办理多人约消畅享宽带，需要您一起助力，若同意，请回复Y，若不同意，请回复N，若24小时内未回复，视同为不同意。中国移动海南公司";
            		
            		//下发提醒短信
            		IData smsInfo = new DataMap();
            		smsInfo.put("POST_PHONE", memberSN);
            		smsInfo.put("CONTENT", smsContent);
            		smsInfo.put("USER_ID_B", ucaB.getString("USER_ID"));
            		smsInfo.put("TRADE_STAFF_ID", getVisit().getStaffId());
            		smsInfo.put("TRADE_DEPART_ID", getVisit().getDepartId());
            		sendSMS(smsInfo);
            	}
            	
            } else {
                dealMebList(bd);
            }
        
        UcaData virtualUca = UcaDataFactory.getNormalUca(virtualSn);
        
        mainTD.setRsrvStr1(virtualUca.getUserId());// 虚拟用户ID
        mainTD.setRsrvStr3("");// 主号短号：多人约消组网不用短号。
        mainTD.setRsrvStr5("61");// 关系类型
        
        String remark="";
            if (StringUtils.equals(verifyMode, "1")) {
                remark = "短信校验";
            } else if (StringUtils.equals(verifyMode, "2")) {
                remark = "免密码校验";
            }else if (StringUtils.equals(verifyMode, "3")) {
                remark = "密码校验";
            }
            
        mainTD.setRsrvStr10(verifyMode);
        mainTD.setRemark(remark);
        
        //处理短信
        dealSMS(bd);
	}
	
	/**
     * 新增组网
     * 
     * @param bd
     * @throws Exception
     */
    private String createGroup(BusiTradeData bd) throws Exception
    {
    	GroupCreateReqData reqData = (GroupCreateReqData) bd.getRD();
         UcaData uca = reqData.getUca();
         String sysdate = reqData.getAcceptTime();
    	
         String userIdA = SeqMgr.getUserId();
         String virtualSn = "DR" + uca.getSerialNumber();
         String custId = SeqMgr.getCustId();
         String acctDay = uca.getAcctDay();
         String acctId = SeqMgr.getAcctId();
         
         /*IData disData = new DataMap();
         disData.put("SUBSYS_CODE", "CSM");
         disData.put("PARAM_ATTR", "8383");
         disData.put("PARAM_CODE", "DR_DISCNT");
         disData.put("USER_ID", uca.getUserId());
         IDataset discntList = RelaUUInfoQry.qryPrincipalDiscnt(disData);
         
         //给主号update一条USER_ID_A
         DiscntTradeData updDiscntTD = new DiscntTradeData();
         updDiscntTD.setUserIdA(userIdA);
         updDiscntTD.setElementId(discntList.getData(0).getString("PARA_CODE1"));
         updDiscntTD.setSpecTag("2");
         updDiscntTD.setRelationTypeCode("61");
         updDiscntTD.setModifyTag(BofConst.MODIFY_TAG_UPD);
         bd.add(uca.getSerialNumber(), updDiscntTD);*/
         
         // 新增用户资料
         UserTradeData user = new UserTradeData();
         UserTradeData tempUser = uca.getUser();
         user.setUserId(userIdA);
         user.setCustId(custId);
         user.setUsecustId(custId);
         user.setEparchyCode(tempUser.getEparchyCode());
         user.setCityCode(tempUser.getCityCode());
         user.setUserPasswd(tempUser.getUserPasswd());
         user.setAcctTag("0");
         user.setUserTypeCode("0");
         user.setContractId(tempUser.getContractId());
         user.setSerialNumber(virtualSn);
         user.setPrepayTag("0");
         user.setOpenDate(sysdate);
         user.setOpenMode("0");
         user.setUserStateCodeset("0");
         user.setNetTypeCode("00");
         user.setMputeMonthFee("0");
         user.setInDate(sysdate);
         user.setRemoveTag("0");
         user.setInStaffId(CSBizBean.getVisit().getStaffId());
         user.setInDepartId(CSBizBean.getVisit().getDepartId());
         user.setOpenStaffId(CSBizBean.getVisit().getStaffId());
         user.setOpenDepartId(CSBizBean.getVisit().getDepartId());
         user.setDevelopStaffId(CSBizBean.getVisit().getStaffId());
         user.setDevelopDepartId(CSBizBean.getVisit().getDepartId());
         user.setModifyTag(BofConst.MODIFY_TAG_ADD);
         bd.add(virtualSn, user);
         
         // 新增帐户
         AccountTradeData acct = new AccountTradeData();
         acct.setEparchyCode(uca.getUserEparchyCode());
         acct.setCityCode(uca.getUser().getCityCode());
         acct.setAcctId(acctId);
         acct.setCustId(custId);
         if (StringUtils.isBlank(reqData.getCustName()))
         {
             acct.setPayName(uca.getCustomer().getCustName());
         }
         else
         {
             acct.setPayName(reqData.getCustName());
         }
         acct.setPayModeCode("0");
         acct.setScoreValue("0");
         acct.setOpenDate(sysdate);
         acct.setRemoveTag("0");
         acct.setBasicCreditValue("0");
         acct.setCreditValue("0");
         acct.setModifyTag(BofConst.MODIFY_TAG_ADD);
         bd.add(virtualSn, acct);

         // 新增付费关系
         PayRelationTradeData payRela = new PayRelationTradeData();
         payRela.setModifyTag(BofConst.MODIFY_TAG_ADD);
         payRela.setUserId(userIdA);
         payRela.setAcctId(acctId);
         payRela.setPayitemCode("0");
         payRela.setAcctPriority("0");
         payRela.setUserPriority("0");
         payRela.setBindType("0");
         payRela.setStartCycleId(SysDateMgr.decodeTimestamp(SysDateMgr.getFirstDayOfThisMonth(), SysDateMgr.PATTERN_TIME_YYYYMMDD));
         payRela.setEndCycleId(SysDateMgr.getEndCycle20501231());
         payRela.setDefaultTag("1");
         payRela.setActTag("1");
         payRela.setLimitType("0");
         payRela.setLimit("0");
         payRela.setComplementTag("0");
         payRela.setInstId(SeqMgr.getInstId());
         bd.add(virtualSn, payRela);
         
         
         // 新增客户资料
         CustomerTradeData customer = new CustomerTradeData();
         CustomerTradeData tempCustomer = uca.getCustomer().clone();
         customer.setCustId(custId);
         customer.setCustName(tempCustomer.getCustName());
         if (StringUtils.isNotBlank(reqData.getCustName()))
         {
             customer.setCustName(reqData.getCustName());
         }
         customer.setCustType("2");
         customer.setCustState("0");
         customer.setPsptTypeCode(tempCustomer.getPsptTypeCode());
         customer.setPsptId(tempCustomer.getPsptId());
         customer.setOpenLimit(tempCustomer.getOpenLimit());
         customer.setEparchyCode(tempCustomer.getEparchyCode());
         customer.setCityCode(tempCustomer.getCityCode());
         customer.setCustPasswd(tempCustomer.getCustPasswd());
         customer.setDevelopDepartId(tempCustomer.getDevelopDepartId());
         customer.setDevelopStaffId(tempCustomer.getDevelopStaffId());
         customer.setInDepartId(tempCustomer.getInDepartId());
         customer.setInStaffId(tempCustomer.getInStaffId());
         customer.setInDate(tempCustomer.getInDate());
         customer.setRemark("办理多人约消添加虚拟客户");
         customer.setModifyTag(BofConst.MODIFY_TAG_ADD);
         customer.setRemoveTag("0");
         bd.add(virtualSn, customer);
         
    	// 建立虚拟用户与主卡的UU关系
        RelationTradeData addRelaUUTD = new RelationTradeData();
        addRelaUUTD.setUserIdA(userIdA);
        addRelaUUTD.setSerialNumberA(virtualSn);
        addRelaUUTD.setUserIdB(uca.getUserId());
        addRelaUUTD.setSerialNumberB(uca.getSerialNumber());
        addRelaUUTD.setRelationTypeCode("61");
        addRelaUUTD.setRoleTypeCode("0");
        addRelaUUTD.setRoleCodeA("0");
        addRelaUUTD.setRoleCodeB("1");
        addRelaUUTD.setOrderno("0");
        addRelaUUTD.setShortCode("");
        addRelaUUTD.setStartDate(reqData.getAcceptTime());
        addRelaUUTD.setEndDate(SysDateMgr.getTheLastTime());
        addRelaUUTD.setInstId(SeqMgr.getInstId());
        addRelaUUTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        bd.add(uca.getSerialNumber(), addRelaUUTD);
        
        
        // 用户账期
        bd.addOpenUserAcctDayData(userIdA, acctDay);
        // 帐户账期
        bd.addOpenAccountAcctDayData(acctId, acctDay);
        
        // 临时办法
        UcaData virtualUca = DataBusManager.getDataBus().getUca(virtualSn);
        virtualUca.setAcctDay(acctDay);
        virtualUca.setFirstDate(reqData.getAcceptTime());
        // 建立用户产品信息
        ProductTradeData addProductTD = new ProductTradeData();
        addProductTD.setUserId(userIdA);
        addProductTD.setProductId("99000211"); //虚拟套餐
        addProductTD.setBrandCode("VPDR");
        addProductTD.setStartDate(sysdate);
        addProductTD.setEndDate(SysDateMgr.getTheLastTime());
        addProductTD.setMainTag("1");
        addProductTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        addProductTD.setProductMode("05");
        addProductTD.setUserIdA(userIdA);
        addProductTD.setCampnId("0");
        addProductTD.setInstId(SeqMgr.getInstId());
        bd.add(virtualSn, addProductTD);
        
        return userIdA;
    	
    }
    
    /**
     * 新增成员
     */
    private void dealMebList(BusiTradeData bd) throws Exception
    {
    	GroupCreateReqData reqData = (GroupCreateReqData) bd.getRD();
        UcaData uca = reqData.getUca();
        String virtualSn = "DR" + uca.getSerialNumber();
        UcaData virtualUca = UcaDataFactory.getNormalUca(virtualSn);
        String sysdate = reqData.getAcceptTime();
        String tradeTypeCode = bd.getMainTradeData().getTradeTypeCode();
        String userId = uca.getUserId();	
        
        List<GroupMemberData> mebDataList = reqData.getMemberDataList();
        for (int i = 0, size = mebDataList.size(); i < size; i++)
        {
        	GroupMemberData mebData = mebDataList.get(i);
            UcaData mebUca = mebData.getUca();

            // 校验成员未完工工单限制 ----start----
            IData data = new DataMap();
            data.put("TRADE_TYPE_CODE", tradeTypeCode);
            data.put("USER_ID", mebUca.getUserId());
            data.put("SERIAL_NUMBER", mebUca.getSerialNumber());
            data.put("EPARCHY_CODE", mebUca.getUser().getEparchyCode());
            data.put("BRAND_CODE", "");
            FamilyTradeHelper.checkMemberUnfinishTrade(data);
            // 校验成员未完工工单限制 ----end----

            String uIdA = virtualUca.getUserId();
            IData disData = new DataMap();
            disData.put("SUBSYS_CODE", "CSM");
            disData.put("PARAM_ATTR", "8383");
            disData.put("PARAM_CODE", "DR_DISCNT");
            disData.put("USER_ID", userId);
            IDataset discntList = RelaUUInfoQry.qryPrincipalDiscnt(disData);
            
            // 添加优惠
			DiscntTradeData addDiscntTD = new DiscntTradeData();
            addDiscntTD.setUserId(mebUca.getUserId());
            addDiscntTD.setUserIdA(virtualUca.getUserId());
            //addDiscntTD.setProductId(discntData.getProductId());
            //addDiscntTD.setPackageId(discntData.getPackageId());
            addDiscntTD.setElementId(discntList.getData(0).getString("PARA_CODE2"));
            addDiscntTD.setSpecTag("2");
            addDiscntTD.setRelationTypeCode("61");
            addDiscntTD.setStartDate(sysdate);
            addDiscntTD.setEndDate(SysDateMgr.getTheLastTime());
            addDiscntTD.setInstId(SeqMgr.getInstId());
            addDiscntTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
            bd.add(mebUca.getSerialNumber(), addDiscntTD);
            
            // 建立UU关系
            RelationTradeData addRelaUUTD = new RelationTradeData();
            addRelaUUTD.setUserIdA(virtualUca.getUserId());
            addRelaUUTD.setSerialNumberA(virtualSn);
            addRelaUUTD.setUserIdB(mebUca.getUserId());
            addRelaUUTD.setSerialNumberB(mebUca.getSerialNumber());
            addRelaUUTD.setRelationTypeCode("61");
            addRelaUUTD.setRoleTypeCode("0");
            addRelaUUTD.setRoleCodeA("0");
            addRelaUUTD.setRoleCodeB("2");
            addRelaUUTD.setShortCode("");
            addRelaUUTD.setOrderno("0");
            addRelaUUTD.setStartDate(sysdate);
            addRelaUUTD.setEndDate(SysDateMgr.getTheLastTime());
            addRelaUUTD.setInstId(SeqMgr.getInstId());
            addRelaUUTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
            bd.add(uca.getSerialNumber(), addRelaUUTD);
            
        }
    }
    /**
     * 发送添加成员成功后短信通知
     * @param bd
     * @throws Exception
     */
    private void dealSMS(BusiTradeData bd) throws Exception
    {
		  GroupCreateReqData reqData = (GroupCreateReqData) bd.getRD(); 
		  UcaData uca = reqData.getUca();
		  String mainSN = uca.getSerialNumber();
		  List<GroupMemberData> mebs = reqData.getMemberDataList();
		  for(int i=0;i<mebs.size();i++)
		  {
			  GroupMemberData memberData=mebs.get(i);
			  String memberSN = memberData.getUca().getSerialNumber();
			  
			  //给主号通知
			  IData smsData = new DataMap(); // 短信数据
			  String smsContent="尊敬的客户，您已成功邀请"+memberSN+"参加多人保底约消畅享宽带。中国移动海南公司";
		      smsData.put("SMS_PRIORITY", "5000");// 优先级：老系统默认5000
		      smsData.put("CANCEL_TAG", "0");// 返销标志：0-未返销，1-被返销，2-返销 不能为空 
			  smsData.put("FORCE_OBJECT", "10086");// 发送对象
		      smsData.put("RECV_OBJECT", mainSN);// 接收对象
		      smsData.put("NOTICE_CONTENT", smsContent);// 短信内容
		      PerSmsAction.insTradeSMS(bd, smsData);
		      
		      //给副号发通知
		      IData smsData1 = new DataMap(); // 短信数据
			  String smsContent1="尊敬的客户，您已被"+mainSN+"成功邀请参加多人保底约消畅享宽带，若有疑问，详询10086。中国移动海南公司";
		      smsData1.put("SMS_PRIORITY", "5000");// 优先级：老系统默认5000
		      smsData1.put("CANCEL_TAG", "0");// 返销标志：0-未返销，1-被返销，2-返销 不能为空 
			  smsData1.put("FORCE_OBJECT", "10086");// 发送对象
		      smsData1.put("RECV_OBJECT", memberSN);// 接收对象
		      smsData1.put("NOTICE_CONTENT", smsContent1);// 短信内容
		      PerSmsAction.insTradeSMS(bd, smsData1);
		  }
    }
    
    
    /*
     * 短信认证时下发
     * */
	public static void sendSMS(IData input) throws Exception
    
    {
    	// 拼短信表参数
    	IDataUtil.chkParam(input, "POST_PHONE");//发送短信的号码
    	IDataUtil.chkParam(input, "CONTENT");
 
        IData param = new DataMap();
        param.put("NOTICE_CONTENT", input.getString("CONTENT"));
        param.put("EPARCHY_CODE", input.getString("EPARCHY_CODE","0898"));
        param.put("IN_MODE_CODE", "0");
        param.put("RECV_OBJECT", input.getString("POST_PHONE"));
        param.put("RECV_ID", input.getString("USER_ID_B"));
        param.put("REFER_STAFF_ID", input.getString("TRADE_STAFF_ID",""));
        param.put("REFER_DEPART_ID", input.getString("TRADE_DEPART_ID",""));
        param.put("REMARK", "多人约消畅享宽带校验提醒");
        String seq = SeqMgr.getSmsSendId();
        long seq_id = Long.parseLong(seq);
        param.put("SMS_NOTICE_ID", seq_id);
        param.put("PARTITION_ID", seq_id % 1000);
        param.put("SEND_COUNT_CODE", "1");
        param.put("REFERED_COUNT", "0");
        param.put("CHAN_ID", "11");
        param.put("SMS_NET_TAG", "0");
        param.put("RECV_OBJECT_TYPE", "00");
        param.put("SMS_TYPE_CODE", "20");
        param.put("SMS_KIND_CODE", "02");
        param.put("NOTICE_CONTENT_TYPE", "0");
        param.put("FORCE_REFER_COUNT", "1");
        param.put("FORCE_OBJECT", "10086");
        param.put("SMS_PRIORITY", "3000");
        param.put("DEAL_STATE", "15");
        param.put("SEND_TIME_CODE", "1");
        param.put("SEND_OBJECT_CODE", "6");
        param.put("REFER_TIME", SysDateMgr.getSysTime());
        param.put("DEAL_TIME", SysDateMgr.getSysTime());
        param.put("MONTH", SysDateMgr.getCurMonth());
        param.put("DAY", SysDateMgr.getCurDay());
        param.put("DEAL_STAFFID", getVisit().getStaffId());
        param.put("DEAL_DEPARTID", getVisit().getDepartId());

        Dao.insert("ti_o_sms", param);
    }

}
