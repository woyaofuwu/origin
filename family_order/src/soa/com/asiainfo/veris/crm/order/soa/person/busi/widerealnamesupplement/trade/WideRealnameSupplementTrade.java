package com.asiainfo.veris.crm.order.soa.person.busi.widerealnamesupplement.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.widerealnamesupplement.requestdata.WideRealnameSupplementReqData;

public class WideRealnameSupplementTrade extends BaseTrade implements ITrade{

	@Override
	public void createBusiTradeData(BusiTradeData btd) throws Exception {
		
		// 1、处理客户资料
        createCustomerTradeData(btd);
        //2、插TF_F_USER_PSPT表
        createUserPsptTradeData(btd);
        
        CustomerTradeData customerTD = btd.getRD().getUca().getCustomer();
        String psptTypeCode = customerTD.getPsptTypeCode();
        if(psptTypeCode!=null&&psptTypeCode.trim().equals("E")){//营业执照
            createOtherTradeDataEnterprise(btd);
        }else if(psptTypeCode!=null&&psptTypeCode.trim().equals("M")){//组织机构代码证
            createOtherTradeDataOrg(btd);
        }
	}
	
	private void createCustomerTradeData(BusiTradeData btd) throws Exception{
		WideRealnameSupplementReqData custInfoRD = (WideRealnameSupplementReqData) btd.getRD();
		CustomerTradeData customerTradeData = btd.getRD().getUca().getCustomer();// 用老的客户资料克隆出一个客户订单数据对象
        // 可能修改下面3个字段的值
        customerTradeData.setCustName(custInfoRD.getCustName());
        customerTradeData.setPsptTypeCode(custInfoRD.getPsptTypeCode());
        customerTradeData.setPsptId(custInfoRD.getPsptId());
        customerTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
        // 如果客户首次办理实名制，则需要记录下办理实名制的时间，记录在RSRV_STR3字段中，完工时 写入TF_F_CUSTOMER表的RSRV_STR3字段中
        if (!StringUtils.equals("1", btd.getRD().getUca().getUserOriginalData().getCustomer().getIsRealName()) && StringUtils.equals("1", custInfoRD.getIsRealName()))
        {
            customerTradeData.setRsrvStr3(custInfoRD.getAcceptTime());// 存放实名制办理时间
        }
        if (StringUtils.isNotBlank(custInfoRD.getIsRealName()))// 如果页面有将实名制标记赋值，则使用界面上的值覆盖
        {
            customerTradeData.setIsRealName(custInfoRD.getIsRealName());
        }

        customerTradeData.setRsrvStr7(custInfoRD.getAgentCustName());// 经办人名称
        customerTradeData.setRsrvStr8(custInfoRD.getAgentPsptTypeCode());// 经办人证件类型
        customerTradeData.setRsrvStr9(custInfoRD.getAgentPsptId());// 经办人证件号码
        customerTradeData.setRsrvStr10(custInfoRD.getAgentPsptAddr());// 经办人地址
        // 注意：其他不变的字段就不需要再设值了，克隆出来的对象就存在
        btd.add(custInfoRD.getUca().getUser().getSerialNumber(), customerTradeData);
		
		
		String custId = btd.getRD().getUca().getCustId();
		
		IDataset idsUserPspt1 = CustomerInfoQry.getCustInfoAddr(custId);    	
    	if (IDataUtil.isEmpty(idsUserPspt1)) {
    		IData idUserPspt = new DataMap();
    		idUserPspt.put("PARTITION_ID", custId.substring(custId.length() - 4));
    		idUserPspt.put("CUST_ID", custId);
        	idUserPspt.put("PSPT_TYPE_CODE", custInfoRD.getPsptTypeCode());
        	idUserPspt.put("PSPT_ID", custInfoRD.getPsptId());
        	idUserPspt.put("PSPT_ADDR", custInfoRD.getPsptAddr());
        	idUserPspt.put("IN_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        	idUserPspt.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId());
        	idUserPspt.put("IN_DEPART_ID", CSBizBean.getVisit().getDepartId());
        	idUserPspt.put("REMOVE_TAG", customerTradeData.getRemoveTag());
        	idUserPspt.put("RSRV_STR1", "");
        	idUserPspt.put("RSRV_STR2", "");
        	idUserPspt.put("RSRV_STR3", "");
        	idUserPspt.put("RSRV_STR4", "");
        	idUserPspt.put("RSRV_STR5", "");
			Dao.insert("TF_F_CUSTOMER_ADDR", idUserPspt);
		}else {
			IData idUserPspt = new DataMap();
			idUserPspt.put("PARTITION_ID", custId.substring(custId.length() - 4));
			idUserPspt.put("CUST_ID", custId);
        	idUserPspt.put("PSPT_TYPE_CODE", custInfoRD.getPsptTypeCode());
        	idUserPspt.put("PSPT_ID", custInfoRD.getPsptId());
        	idUserPspt.put("PSPT_ADDR", custInfoRD.getPsptAddr());
        	idUserPspt.put("IN_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        	idUserPspt.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId());
        	idUserPspt.put("IN_DEPART_ID", CSBizBean.getVisit().getDepartId());
        	idUserPspt.put("REMOVE_TAG", customerTradeData.getRemoveTag());
        	Dao.update("TF_F_CUSTOMER_ADDR", idUserPspt, new String[] { "CUST_ID" });
		}
         
	}
	
	private void createUserPsptTradeData(BusiTradeData btd) throws Exception{
		WideRealnameSupplementReqData custInfoRD = (WideRealnameSupplementReqData) btd.getRD();
		UserTradeData userTradeData = btd.getRD().getUca().getUser();
		
		//经办人信息
		IDataset idsUserPspt1 = CustomerInfoQry.getUserPsptByUserid(userTradeData.getUserId(), "1");    	
    	if (IDataUtil.isEmpty(idsUserPspt1)) {
    		IData idUserPspt = new DataMap();
    		idUserPspt.put("USER_TYPE", "1");
    		idUserPspt.put("CUST_ID", btd.getRD().getUca().getCustId());
    		idUserPspt.put("USER_ID", userTradeData.getUserId());
    		idUserPspt.put("CUST_NAME", custInfoRD.getAgentCustName());
        	idUserPspt.put("PSPT_TYPE_CODE", custInfoRD.getAgentPsptTypeCode());
        	idUserPspt.put("PSPT_ID", custInfoRD.getAgentPsptId());
        	idUserPspt.put("INSERT_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        	idUserPspt.put("INSERT_STAFF_ID", CSBizBean.getVisit().getStaffId());
        	idUserPspt.put("INSERT_DEPART_ID", CSBizBean.getVisit().getDepartId());
        	idUserPspt.put("REMOVE_TAG", "0");
        	idUserPspt.put("REMARK", "826");
        	idUserPspt.put("RSRV_STR1", btd.getRD().getTradeId());
        	idUserPspt.put("RSRV_STR2", custInfoRD.getAgentPsptAddr());
        	idUserPspt.put("RSRV_STR3", "");
        	idUserPspt.put("RSRV_STR4", "");
        	idUserPspt.put("RSRV_STR5", "");
			Dao.insert("TF_F_USER_PSPT", idUserPspt);
		}else{
			IData idUserPspt = idsUserPspt1.first();
    		idUserPspt.put("CUST_ID", btd.getRD().getUca().getCustId());
    		idUserPspt.put("CUST_NAME", custInfoRD.getAgentCustName());
    		idUserPspt.put("PSPT_TYPE_CODE", custInfoRD.getAgentPsptTypeCode());
    		idUserPspt.put("PSPT_ID", custInfoRD.getAgentPsptId());
    		idUserPspt.put("RSRV_STR2", custInfoRD.getAgentPsptAddr());
    		idUserPspt.put("REMARK", "826");
    		idUserPspt.put("RSRV_STR1", btd.getRD().getTradeId());
			Dao.update("TF_F_USER_PSPT", idUserPspt, new String[] { "USER_TYPE", "USER_ID" });
		}
    	
    	//责任人信息
		IDataset idsUserPspt3 = CustomerInfoQry.getUserPsptByUserid(userTradeData.getUserId(), "3");    	
    	if (IDataUtil.isEmpty(idsUserPspt3)) {
    		IData idUserPspt = new DataMap();
    		idUserPspt.put("USER_TYPE", "3");
    		idUserPspt.put("CUST_ID", btd.getRD().getUca().getCustId());
    		idUserPspt.put("USER_ID", userTradeData.getUserId());
    		idUserPspt.put("CUST_NAME", custInfoRD.getRSRV_STR2());
        	idUserPspt.put("PSPT_TYPE_CODE", custInfoRD.getRSRV_STR3());
        	idUserPspt.put("PSPT_ID", custInfoRD.getRSRV_STR4());
        	idUserPspt.put("INSERT_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        	idUserPspt.put("INSERT_STAFF_ID", CSBizBean.getVisit().getStaffId());
        	idUserPspt.put("INSERT_DEPART_ID", CSBizBean.getVisit().getDepartId());
        	idUserPspt.put("REMOVE_TAG", "0");
        	idUserPspt.put("REMARK", "826");
        	idUserPspt.put("RSRV_STR1", btd.getRD().getTradeId());
        	idUserPspt.put("RSRV_STR2", custInfoRD.getRSRV_STR5());
        	idUserPspt.put("RSRV_STR3", "");
        	idUserPspt.put("RSRV_STR4", "");
        	idUserPspt.put("RSRV_STR5", "");
			Dao.insert("TF_F_USER_PSPT", idUserPspt);
		}else{
			IData idUserPspt = idsUserPspt3.first();
    		idUserPspt.put("CUST_ID", btd.getRD().getUca().getCustId());
    		idUserPspt.put("CUST_NAME", custInfoRD.getRSRV_STR2());
    		idUserPspt.put("PSPT_TYPE_CODE", custInfoRD.getRSRV_STR3());
    		idUserPspt.put("PSPT_ID", custInfoRD.getRSRV_STR4());
    		idUserPspt.put("RSRV_STR2", custInfoRD.getRSRV_STR5());
    		idUserPspt.put("REMARK", "826");
    		idUserPspt.put("RSRV_STR1", btd.getRD().getTradeId());
			Dao.update("TF_F_USER_PSPT", idUserPspt, new String[] { "USER_TYPE", "USER_ID" });
		}
		
	}
	
	public void createOtherTradeDataEnterprise(BusiTradeData btd) throws Exception
    {
		WideRealnameSupplementReqData custInfoRD = (WideRealnameSupplementReqData) btd.getRD();
        String serialNumber = custInfoRD.getUca().getUser().getSerialNumber();
        IData idRequestData = btd.getRD().getPageRequestData();
        String legalperson = idRequestData.getString("legalperson", "").trim();//法人
        String startdate = idRequestData.getString("startdate", "").trim();//成立日期
        String termstartdate = idRequestData.getString("termstartdate", "").trim();//营业开始时间
        String termenddate = idRequestData.getString("termenddate", "").trim();//营业结束时间
        IDataset dataset = UserOtherInfoQry.getUserOtherUserId(btd.getRD().getUca().getUserId(), "ENTERPRISE", null);//
        OtherTradeData otherTD = new OtherTradeData();
        if (dataset != null && dataset.size() > 0) {
            otherTD = new OtherTradeData(dataset.getData(0));
            otherTD.setRsrvStr1(legalperson);
            otherTD.setRsrvStr2(startdate);
            otherTD.setRsrvStr3(termstartdate);
            otherTD.setRsrvStr4(termenddate);
            otherTD.setModifyTag(BofConst.MODIFY_TAG_UPD);//更新            
            btd.add(serialNumber, otherTD);
        } else {
            otherTD.setUserId(custInfoRD.getUca().getUserId());
            otherTD.setRsrvValueCode("ENTERPRISE");
            otherTD.setRsrvValue("营业执照");
            otherTD.setRsrvStr1(legalperson);
            otherTD.setRsrvStr2(startdate);
            otherTD.setRsrvStr3(termstartdate);
            otherTD.setRsrvStr4(termenddate);
            otherTD.setRsrvStr5(CSBizBean.getVisit().getStaffName());
            otherTD.setStartDate(SysDateMgr.getSysTime());
            otherTD.setEndDate(SysDateMgr.getTheLastTime());
            otherTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
            otherTD.setStaffId(CSBizBean.getVisit().getStaffId());
            otherTD.setDepartId(CSBizBean.getVisit().getDepartId());
            otherTD.setInstId(SeqMgr.getInstId());
            btd.add(serialNumber, otherTD);
        }
    }
	
	public void createOtherTradeDataOrg(BusiTradeData btd) throws Exception
    {
		WideRealnameSupplementReqData custInfoRD = (WideRealnameSupplementReqData) btd.getRD();
        String serialNumber = custInfoRD.getUca().getUser().getSerialNumber();        
        IData idRequestData = btd.getRD().getPageRequestData();
        String orgtype = idRequestData.getString("orgtype", "").trim();//机构类型
        String effectiveDate = idRequestData.getString("effectiveDate", "").trim();//有效日期
        String expirationDate = idRequestData.getString("expirationDate", "").trim();//失效日期
        IDataset dataset = UserOtherInfoQry.getUserOtherUserId(btd.getRD().getUca().getUserId(), "ORG", null);//               
        OtherTradeData otherTD = new OtherTradeData();
        if (dataset != null && dataset.size() > 0) {
            otherTD = new OtherTradeData(dataset.getData(0));
            otherTD.setRsrvStr1(orgtype);
            otherTD.setRsrvStr2(effectiveDate);
            otherTD.setRsrvStr3(expirationDate);
            otherTD.setModifyTag(BofConst.MODIFY_TAG_UPD);//更新
            btd.add(serialNumber, otherTD);
        } else {
            otherTD.setUserId(btd.getRD().getUca().getUserId());
            otherTD.setRsrvValueCode("ORG");
            otherTD.setRsrvValue("组织机构代码证");
            otherTD.setRsrvStr1(orgtype);
            otherTD.setRsrvStr2(effectiveDate);
            otherTD.setRsrvStr3(expirationDate);
            otherTD.setRsrvStr5(CSBizBean.getVisit().getStaffName());
            otherTD.setStartDate(SysDateMgr.getSysTime());
            otherTD.setEndDate(SysDateMgr.getTheLastTime());
            otherTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
            otherTD.setStaffId(CSBizBean.getVisit().getStaffId());
            otherTD.setDepartId(CSBizBean.getVisit().getDepartId());
            otherTD.setInstId(SeqMgr.getInstId());
            btd.add(serialNumber, otherTD);
        }
    }
	
}
