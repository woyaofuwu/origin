
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.trade;

import java.util.List;

import com.asiainfo.veris.crm.order.soa.person.busi.internetofthings.order.action.GeneIotInstIdAction;
import org.apache.log4j.Logger;
import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DESUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UBankInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AcctConsignTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PostTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata.CreatePersonUserRequestData;

public class CreatePersonUserTrade extends CreateUserTrade implements ITrade
{

    /**
     * 修改主台帐字段
     * 
     * @author sunxin
     * @param btd
     * @throws Exception
     */
	protected static final Logger log = Logger.getLogger(CreatePersonUserTrade.class);
    public void appendTradeMainData(BusiTradeData btd) throws Exception
    {

        CreatePersonUserRequestData createPersonUserRD = (CreatePersonUserRequestData) btd.getRD();
        btd.getMainTradeData().setCampnId("5");
        btd.getMainTradeData().setRsrvStr1(createPersonUserRD.getSimCardNo());// RSRV_STR1 开户SIM卡号
        btd.getMainTradeData().setRsrvStr2(CSBizBean.getVisit().getStaffName());// RSRV_STR2记录操作员工的名字
        if ("1".equals(createPersonUserRD.getBindSaleTag()))
        {
            btd.getMainTradeData().setRsrvStr3(createPersonUserRD.getSaleProductId());// 绑定的营销活动ID
            btd.getMainTradeData().setRsrvStr4(createPersonUserRD.getSalePackageId());// 绑定的营销包ID
        }
        // add by zhangxing for “签约赠送188号码”营销活动系统开发需求HNYD-REQ-20100426-008 start
        // btd.getMainTradeData().setRsrvStr5(createPersonUserRD.getAuthForSaleTag());// RSRV_STR5 标记是否为签约赠送188号码活动开户
        btd.getMainTradeData().setRsrvStr8(createPersonUserRD.getResKindName());// RSRV_STR8 为 卡型名称+容量名称
        if ("0".equals(createPersonUserRD.getDefaultPwdFlag()) && StringUtils.isNotBlank(createPersonUserRD.getUca().getUser().getUserPasswd()))
            btd.getMainTradeData().setRsrvStr9(DESUtil.encrypt(createPersonUserRD.getUca().getUser().getUserPasswd()));// RSRV_STR9
        // 为
        // 用户密码的明码加密可逆

        btd.getMainTradeData().setInvoiceNo(createPersonUserRD.getInvoiceNo());// 老版本RSRV_STR5为发票号码，新版本为INVOICEID
        if (Integer.parseInt(btd.getAdvanceFee()) >= 5000 && "AGENT_OPEN".equals(createPersonUserRD.getOpenType()))// 如果为代理商开户，且预存费用大于50，则需要处理代办费
            // sunxin
            btd.getMainTradeData().setRsrvStr7("1000");
        if ("500".equals(btd.getRD().getOrderTypeCode()))
        {
            btd.getMainTradeData().setBatchId(createPersonUserRD.getOperateId());
            String brand_code = btd.getMainTradeData().getBrandCode();
            if("MOSP".equals(brand_code)){
            	btd.getMainTradeData().setRsrvStr6("");//一卡多号不送局停
            }else{
            btd.getMainTradeData().setRsrvStr6("1");// 服开处理局停
            }
        }
        if ("700".equals(btd.getRD().getOrderTypeCode()))
        {
            btd.getMainTradeData().setBatchId(createPersonUserRD.getOperateId());
            btd.getMainTradeData().setRsrvStr6("1");// 服开处理局停
            btd.getMainTradeData().setRsrvStr7(createPersonUserRD.getAgentFee());
        }
        
        // 物联网处理  @yanwu
        if ("1".equals(createPersonUserRD.getM2mFlag()))
        	btd.getMainTradeData().setNetTypeCode("07");
        
        btd.getMainTradeData().setRsrvStr10(createPersonUserRD.getAgentPresentFee());//赠送话费 REQ201502050013 放号政策调整需求 by songlm
    }

    /**
     * 生成台帐帐户托收子表
     * 
     * @author sunxin
     * @param btd
     * @throws Exception
     */
    public void createAcctConsignTradeData(BusiTradeData btd) throws Exception
    {
        AcctConsignTradeData acctConsignTD = new AcctConsignTradeData();
        CreatePersonUserRequestData createPersonUserRD = (CreatePersonUserRequestData) btd.getRD();
        String serialNumber = createPersonUserRD.getUca().getUser().getSerialNumber();
        acctConsignTD.setAcctId(createPersonUserRD.getUca().getAcctId());
        acctConsignTD.setPayModeCode(createPersonUserRD.getUca().getAccount().getPayModeCode());// 帐户付费类型：0-现金，1-托收，2-代扣
        acctConsignTD.setEparchyCode(BizRoute.getRouteId());
        acctConsignTD.setCityCode(createPersonUserRD.getCityCode());
        acctConsignTD.setSuperBankCode(createPersonUserRD.getSuperBankCode());
        acctConsignTD.setBankCode(createPersonUserRD.getUca().getAccount().getBankCode());
        acctConsignTD.setBankAcctNo(createPersonUserRD.getUca().getAccount().getBankAcctNo());
        String bankName = UBankInfoQry.getBankNameByBankCode(createPersonUserRD.getUca().getAccount().getBankCode());
        acctConsignTD.setBankAcctName(bankName);
        acctConsignTD.setConsignMode("1");// 托收方式：默认为1
        acctConsignTD.setPaymentId("4");// 储值方式：默认为4
        acctConsignTD.setPayFeeModeCode("4");
        acctConsignTD.setActTag("1");
        acctConsignTD.setPriority("0");
        acctConsignTD.setInstId(SeqMgr.getInstId());
        acctConsignTD.setStartCycleId(SysDateMgr.getNowCyc());
        acctConsignTD.setEndCycleId(SysDateMgr.getEndCycle205012());
        acctConsignTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        btd.add(serialNumber, acctConsignTD);
    }

    /**
     * 账户资料
     * 
     * @author sunxin
     * @param btd
     * @throws Exception
     */
    public void createAcctTradeData(BusiTradeData btd) throws Exception
    {
        CreatePersonUserRequestData createPersonUserRD = (CreatePersonUserRequestData) btd.getRD();

        /*
         * if ("1".equals(createPersonUserRD.getBReopenTag())) { String serialNumber =
         * createPersonUserRD.getUca().getUser().getSerialNumber(); AccountTradeData acctTD =
         * createPersonUserRD.getUca().getAccount().clone(); acctTD.setModifyTag(BofConst.MODIFY_TAG_UPD);
         * btd.add(serialNumber, acctTD); } else {
         */
        List<AccountTradeData> accountTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_ACCOUNT);
        for (AccountTradeData accountTradeData : accountTradeDatas)
        {

        	accountTradeData.setNetTypeCode("00");
        	  // 物联网处理 sunxin
            if ("1".equals(createPersonUserRD.getM2mFlag()))
            	accountTradeData.setNetTypeCode("07");
            // accountTradeData.setOpenDate(createPersonUserRD.getSysDate());
        }
    }

    // }

    /**
     * 创建开户具体业务台账
     * 
     * @author sunxin
     * @param btd
     * @throws Exception
     */
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        CreatePersonUserRequestData createPersonUserRD = (CreatePersonUserRequestData) btd.getRD();

        String strCustCityCode = createPersonUserRD.getCityCode();
        createPersonUserRD.setCityCode(StringUtils.isBlank(strCustCityCode) ? CSBizBean.getVisit().getCityCode() : strCustCityCode);
        appendTradeMainData(btd);// 处理主台账

        if (createPersonUserRD.getUca().getAccount().getPayModeCode().equals("1"))
        {
            createAcctConsignTradeData(btd);// 托收资料

        }
        // 如果是实名制开户，需要拼用户其他表
        if (createPersonUserRD.getRealName().equals("1"))
        {
            createOtherTradeData(btd);//

        }

        super.createBusiTradeData(btd);
        CustomerTradeData customerTD = createPersonUserRD.getUca().getCustomer();
        String psptTypeCode = customerTD.getPsptTypeCode();
        if(psptTypeCode!=null&&psptTypeCode.trim().equals("E")){//营业执照
            createOtherTradeDataEnterprise(btd);
        }else if(psptTypeCode!=null&&psptTypeCode.trim().equals("M")){//组织机构代码证
            createOtherTradeDataOrg(btd);
        }
        createPayrelationTradeData(btd);// 付费关系台账表

        createUserTradeData(btd);// 处理用户台账
        createCustomerTradeData(btd);// 处理客户核心资料
        //物联网（批量）或者行业应用卡批量开户
        if ("1".equals(createPersonUserRD.getM2mFlag()) || 
        	"1".equals(createPersonUserRD.getM2mTag()))
        {
        	createCustPersonTradeData(btd);// 处理客户个人资料
        }
        //物联网批量开户，插TF_F_INSTANCE_PF表，从action前移到trade
        if ("1".equals(createPersonUserRD.getM2mFlag()))
        {
            GeneIotInstIdAction geneIotInstIdAction=new GeneIotInstIdAction();
            String serialNumberM2M=createPersonUserRD.getUca().getSerialNumber();
            geneIotInstIdAction.geneUserInstance(serialNumberM2M, btd);
            geneIotInstIdAction.geneProductInstance(serialNumberM2M, btd);
            geneIotInstIdAction.geneElementInstance(serialNumberM2M, btd);
            geneIotInstIdAction.genePkgInstance(serialNumberM2M, btd);
        }
        createAcctTradeData(btd);// 处理账户资料
        
        //行业应用卡批量开户标记OTHER表
        if("1".equals(createPersonUserRD.getM2mFlag()) || 
           "1".equals(createPersonUserRD.getM2mTag())){
        	createM2mTagOtherTrade(btd);
        }
         
        /*
         * if ("1".equals(createPersonUserRD.getBReopenTag()))// 如果是二次开户需要特殊处理下父类拼邮寄信息 sunxin { if
         * (createPersonUserRD.getPostInfoPostTag().equals("1")) DealCreatePostTradeData(btd);// 邮寄资料 }
         */

    }

    
    /**
     * 行业应用卡批量开户标记OTHER表
     * @author Yanwu
     * @param btd
     * @throws Exception
     */
    public void createM2mTagOtherTrade(BusiTradeData btd)throws Exception
    {
    	
    	CreatePersonUserRequestData createPersonUserRD = (CreatePersonUserRequestData) btd.getRD();
    	String agentDepartId = ("".equals(createPersonUserRD.getAgentDepartId()) || createPersonUserRD.getAgentDepartId() == null) ? CSBizBean.getVisit().getDepartId() : createPersonUserRD.getAgentDepartId();

    	UserTradeData userTD = createPersonUserRD.getUca().getUser(); 
		OtherTradeData otherTD = new OtherTradeData();
		otherTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
		otherTD.setInstId(SeqMgr.getInstId());
		otherTD.setUserId(userTD.getUserId());
		otherTD.setRsrvValueCode("HYYYKBATCHOPEN");
		otherTD.setRsrvValue("行业应用卡批量开户标记");
		otherTD.setRsrvStr1(userTD.getSerialNumber());
		otherTD.setRsrvStr2(userTD.getCustId());
        otherTD.setRsrvStr3("m2mTag");
        otherTD.setRsrvStr4("10");
        otherTD.setRsrvStr5(CSBizBean.getVisit().getStaffName());
		otherTD.setRsrvStr11(CSBizBean.getVisit().getStaffId());
		otherTD.setStartDate(SysDateMgr.getSysTime());
		otherTD.setEndDate(SysDateMgr.END_DATE_FOREVER);
		otherTD.setStaffId(CSBizBean.getVisit().getStaffId());
		otherTD.setDepartId(agentDepartId);
		otherTD.setIsNeedPf("");
        
		btd.add(userTD.getSerialNumber(), otherTD);
    	 
    }
    
    // }

    /**
     * 客户资料表
     * 
     * @author sunxin
     * @param btd
     * @throws Exception
     */
    public void createCustomerTradeData(BusiTradeData btd) throws Exception
    {
        CreatePersonUserRequestData createPersonUserRD = (CreatePersonUserRequestData) btd.getRD();

        /*
         * if ("1".equals(createPersonUserRD.getBReopenTag())) { String serialNumber =
         * createPersonUserRD.getUca().getUser().getSerialNumber(); CustomerTradeData customerTD =
         * createPersonUserRD.getUca().getCustomer().clone(); customerTD.setModifyTag(BofConst.MODIFY_TAG_UPD);
         * btd.add(serialNumber, customerTD); } else {
         */
        List<CustomerTradeData> customerTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_CUSTOMER);
        for (CustomerTradeData customerTradeData : customerTradeDatas)
        {

            if ("1".equals(createPersonUserRD.getRealName()))
            {
                customerTradeData.setIsRealName(createPersonUserRD.getRealName());
                customerTradeData.setRsrvStr3(SysDateMgr.getSysTime());
            }
            customerTradeData.setRsrvStr7(createPersonUserRD.getAgentCustName());// 经办人名称
            customerTradeData.setRsrvStr8(createPersonUserRD.getAgentPsptTypeCode());// 经办人证件类型
            customerTradeData.setRsrvStr9(createPersonUserRD.getAgentPsptId());// 经办人证件号码
            customerTradeData.setRsrvStr10(createPersonUserRD.getAgentPsptAddr());// 经办人证件地址
            
            //REQ201911080010 关于实名入网办理日志留存的改造通知 
            customerTradeData.setRsrvStr6(createPersonUserRD.getDevRead()+","+createPersonUserRD.getReadRuslt()+","+createPersonUserRD.getComparisonIs()
            		+","+createPersonUserRD.getComparisonRuslt()+","+createPersonUserRD.getComparisonSeq()+","+createPersonUserRD.getAuthenticityIs()
            		+","+createPersonUserRD.getAuthenticityRuslt()+","+createPersonUserRD.getAuthenticitySeq()+","+createPersonUserRD.getProvenumIs()+","
            		+createPersonUserRD.getProvenumRuslt()+","+createPersonUserRD.getProvenumSeq());
            
        }
    }

    /**
     * 生成台帐个人客户表
     * 
     * @author sunxin
     * @param btd
     * @throws Exception
     */
    public void createCustPersonTradeData(BusiTradeData btd) throws Exception
    {
    	
    	IData idRequestData = btd.getRD().getPageRequestData();
    	if(IDataUtil.isNotEmpty(idRequestData))
    	{
    		String strRsrvstr2 = idRequestData.getString("RSRV_STR2", "");
        	String strRsrvstr3 = idRequestData.getString("RSRV_STR3", "");
        	String strRsrvstr4 = idRequestData.getString("RSRV_STR4", "");
        	String strRsrvstr5 = idRequestData.getString("RSRV_STR5", "");
        	
        	if( StringUtils.isNotBlank(strRsrvstr2) || 
        		StringUtils.isNotBlank(strRsrvstr3) ||
              	StringUtils.isNotBlank(strRsrvstr4) || 
              	StringUtils.isNotBlank(strRsrvstr5) )
        	{
        		
        		
        		String strCustId = btd.getMainTradeData().getCustId();
            	String strPartition_id = strCustId.substring(strCustId.length() - 4);
            	
            	String strTradeTpyeCode = btd.getMainTradeData().getTradeTypeCode();
     
    			IDataset list = CustPersonInfoQry.qryCustPersonOtherByCustId(strCustId);
    			if( IDataUtil.isNotEmpty(list) ){
    				IData custPersonOtherData = list.first();
    	        	custPersonOtherData.put("UPDATE_TIME", SysDateMgr.getSysTime());
    	        	custPersonOtherData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
    	        	custPersonOtherData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
    	        	custPersonOtherData.put("RSRV_STR1", strTradeTpyeCode);
    	        	custPersonOtherData.put("RSRV_STR2", strRsrvstr2);
    	        	custPersonOtherData.put("RSRV_STR3", strRsrvstr3);
    	        	custPersonOtherData.put("RSRV_STR4", strRsrvstr4);
    	        	custPersonOtherData.put("RSRV_STR5", strRsrvstr5);
    	        	
    				Dao.update("TF_F_CUST_PERSON_OTHER", custPersonOtherData, new String[] { "PARTITION_ID", "CUST_ID" });
    			}else{
    				IData custPersonOtherData = new DataMap();
    	        	custPersonOtherData.put("PARTITION_ID", strPartition_id);
    	        	custPersonOtherData.put("CUST_ID", strCustId);
    	        	custPersonOtherData.put("USE_NAME", "");
    	        	custPersonOtherData.put("USE_PSPT_TYPE_CODE", "");
    	        	custPersonOtherData.put("USE_PSPT_ID", "");
    	        	custPersonOtherData.put("USE_PSPT_ADDR", "");
    	        	custPersonOtherData.put("CREATE_TIME", SysDateMgr.getSysTime());
    	        	custPersonOtherData.put("UPDATE_TIME", SysDateMgr.getSysTime());
    	        	custPersonOtherData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
    	        	custPersonOtherData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
    	        	custPersonOtherData.put("REMARK", "开户-责任人录入");
    	        	custPersonOtherData.put("RSRV_STR1", strTradeTpyeCode);
    	        	custPersonOtherData.put("RSRV_STR2", strRsrvstr2);
    	        	custPersonOtherData.put("RSRV_STR3", strRsrvstr3);
    	        	custPersonOtherData.put("RSRV_STR4", strRsrvstr4);
    	        	custPersonOtherData.put("RSRV_STR5", strRsrvstr5);
    				Dao.insert("TF_F_CUST_PERSON_OTHER", custPersonOtherData);
    			}
    			
        	}
    	}
    	
        //CreatePersonUserRequestData createPersonUserRD = (CreatePersonUserRequestData) btd.getRD();
        /*
         * if ("1".equals(createPersonUserRD.getBReopenTag())) { String serialNumber =
         * createPersonUserRD.getUca().getUser().getSerialNumber(); CustPersonTradeData custpersonTD =
         * createPersonUserRD.getUca().getCustPerson().clone(); custpersonTD.setModifyTag(BofConst.MODIFY_TAG_UPD);
         * btd.add(serialNumber, custpersonTD); }
         */

    }

    // }

    /**
     * 生成台帐其它资料表
     * 
     * @author sunxin
     * @param btd
     * @throws Exception
     */
    public void createOtherTradeData(BusiTradeData btd) throws Exception
    {
        CreatePersonUserRequestData createPersonUserRD = (CreatePersonUserRequestData) btd.getRD();
        String serialNumber = createPersonUserRD.getUca().getUser().getSerialNumber();
        String agentDepartId = ("".equals(createPersonUserRD.getAgentDepartId()) || createPersonUserRD.getAgentDepartId() == null) ? CSBizBean.getVisit().getDepartId() : createPersonUserRD.getAgentDepartId();

        OtherTradeData otherTD = new OtherTradeData();
        otherTD.setUserId(createPersonUserRD.getUca().getUserId());
        otherTD.setRsrvValueCode("CHRN");
        otherTD.setRsrvValue("实名制");

        otherTD.setRsrvStr1(CSBizBean.getVisit().getStaffId());
        otherTD.setRsrvStr2(SysDateMgr.getSysTime());
        otherTD.setRsrvStr3("1");
        otherTD.setRsrvStr4("10");
        otherTD.setRsrvStr5(CSBizBean.getVisit().getStaffName());
        otherTD.setStartDate(SysDateMgr.getSysTime());
        otherTD.setEndDate(SysDateMgr.getTheLastTime());
        otherTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        /*
         * if ("1".equals(createPersonUserRD.getBReopenTag())) otherTD.setModifyTag(BofConst.MODIFY_TAG_UPD);
         */
        otherTD.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTD.setDepartId(agentDepartId);
        otherTD.setInstId(SeqMgr.getInstId());
        btd.add(serialNumber, otherTD);
    }
    
    public void createOtherTradeDataEnterprise(BusiTradeData btd) throws Exception
    {
        CreatePersonUserRequestData createPersonUserRD = (CreatePersonUserRequestData) btd.getRD();
        String serialNumber = createPersonUserRD.getUca().getUser().getSerialNumber();
        String agentDepartId = ("".equals(createPersonUserRD.getAgentDepartId()) || createPersonUserRD.getAgentDepartId() == null) ? CSBizBean.getVisit().getDepartId() : createPersonUserRD.getAgentDepartId();
        IData idRequestData = btd.getRD().getPageRequestData();
        String legalperson = idRequestData.getString("legalperson","").trim();//法人
        String startdate = idRequestData.getString("startdate","").trim();//成立日期
        String termstartdate = idRequestData.getString("termstartdate","").trim();//营业开始时间
        String termenddate = idRequestData.getString("termenddate","").trim();//营业结束时间
        OtherTradeData otherTD = new OtherTradeData();
        otherTD.setUserId(createPersonUserRD.getUca().getUserId());
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
        otherTD.setDepartId(agentDepartId);
        otherTD.setInstId(SeqMgr.getInstId());
        btd.add(serialNumber, otherTD);
    }
    public void createOtherTradeDataOrg(BusiTradeData btd) throws Exception
    {
        CreatePersonUserRequestData createPersonUserRD = (CreatePersonUserRequestData) btd.getRD();
        String serialNumber = createPersonUserRD.getUca().getUser().getSerialNumber();
        String agentDepartId = ("".equals(createPersonUserRD.getAgentDepartId()) || createPersonUserRD.getAgentDepartId() == null) ? CSBizBean.getVisit().getDepartId() : createPersonUserRD.getAgentDepartId();
        IData idRequestData = btd.getRD().getPageRequestData();
        String orgtype = idRequestData.getString("orgtype","").trim();//机构类型
        String effectiveDate = idRequestData.getString("effectiveDate","").trim();//有效日期
        String expirationDate = idRequestData.getString("expirationDate","").trim();//失效日期
        OtherTradeData otherTD = new OtherTradeData();
        otherTD.setUserId(createPersonUserRD.getUca().getUserId());
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
        otherTD.setDepartId(agentDepartId);
        otherTD.setInstId(SeqMgr.getInstId());
        btd.add(serialNumber, otherTD);
    }

    /**
     * 付费关系台账表
     * 
     * @author sunxin
     * @param btd
     * @throws Exception
     */
    public void createPayrelationTradeData(BusiTradeData btd) throws Exception
    {
        PayRelationTradeData payrelationTD = new PayRelationTradeData();
        CreatePersonUserRequestData createPersonUserRD = (CreatePersonUserRequestData) btd.getRD();
        String serialNumber = createPersonUserRD.getUca().getUser().getSerialNumber();
        payrelationTD.setUserId(createPersonUserRD.getUca().getUserId());
        payrelationTD.setAcctId(createPersonUserRD.getUca().getAcctId());
        payrelationTD.setPayitemCode("-1");
        payrelationTD.setAcctPriority("0");
        payrelationTD.setUserPriority("0");
        payrelationTD.setBindType("1");
        /*
         * if ("1".equals(createPersonUserRD.getPreOpenTag())) {
         * payrelationTD.setStartCycleId(createPersonUserRD.getSysDate().replaceAll("-", "").substring(0, 10)); } else {
         */
        payrelationTD.setStartCycleId(SysDateMgr.getNowCycle());
        // }
        payrelationTD.setEndCycleId(SysDateMgr.getEndCycle20501231());
        payrelationTD.setActTag("1");
        payrelationTD.setDefaultTag("1");
        payrelationTD.setLimitType("0");
        payrelationTD.setLimit("0");
        payrelationTD.setComplementTag("0");
        payrelationTD.setRemark(createPersonUserRD.getRemark());
        payrelationTD.setInstId(SeqMgr.getInstId());
        payrelationTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        btd.add(serialNumber, payrelationTD);
    }

    /**
     * 产品台账处理
     * 
     * @author sunxin
     * @param btd
     * @throws Exception
     */
    public void createProductTradeData(BusiTradeData btd) throws Exception
    {
        CreatePersonUserRequestData createPersonUserRD = (CreatePersonUserRequestData) btd.getRD();
        String serialNumber = createPersonUserRD.getUca().getUser().getSerialNumber();
        ProductTradeData productTD = new ProductTradeData();
        productTD.setUserId(createPersonUserRD.getUca().getUserId());
        productTD.setUserIdA("-1");
        productTD.setProductId(createPersonUserRD.getMainProduct().getProductId());
        productTD.setProductMode(createPersonUserRD.getMainProduct().getProductMode());
        productTD.setBrandCode(createPersonUserRD.getMainProduct().getBrandCode());
        productTD.setInstId(SeqMgr.getInstId());
        productTD.setStartDate(createPersonUserRD.getUca().getUser().getOpenDate());
        productTD.setEndDate(SysDateMgr.getTheLastTime());
        productTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        productTD.setMainTag("1");
        btd.add(serialNumber, productTD);
    }

    /**
     * 生成台帐用户子表
     * 
     * @author sunxin
     * @param btd
     * @throws Exception
     */
    public void createUserTradeData(BusiTradeData btd) throws Exception
    {
        CreatePersonUserRequestData createPersonUserRD = (CreatePersonUserRequestData) btd.getRD();
        // 分2次开户与普通开户区别 sunxin

        /*
         * if ("1".equals(createPersonUserRD.getBReopenTag())) { String serialNumber =
         * createPersonUserRD.getUca().getUser().getSerialNumber(); String userPasswd =
         * createPersonUserRD.getUca().getUser().getUserPasswd(); String user_id =
         * createPersonUserRD.getUca().getUserId(); UserTradeData userTD =
         * createPersonUserRD.getUca().getUser().clone(); // 处理密码 if (!"".equals(userPasswd)) { String strTemp = ""; if
         * (user_id.length() > 9) strTemp = user_id; else strTemp = "000000000" + user_id; userPasswd =
         * Encryptor.fnEncrypt(userPasswd, strTemp.substring(strTemp.length() - 9)); }
         * userTD.setModifyTag(BofConst.MODIFY_TAG_UPD); userTD.setUserPasswd(userPasswd); btd.add(serialNumber,
         * userTD); } else {
         */
        List<UserTradeData> userTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_USER);// 只能循环 sunxin
        for (UserTradeData userTradeData : userTradeDatas)

        {
            userTradeData.setDevelopDate(SysDateMgr.getSysTime());
            userTradeData.setDevelopDepartId(createPersonUserRD.getAgentDepartId());
            /*
             * if ("1".equals(createPersonUserRD.getActiveTag())) { userTradeData.setAcctTag("2"); // 待激活用户 }
             */
            // 物联网处理 sunxin
            if ("1".equals(createPersonUserRD.getM2mFlag()))
            {
                userTradeData.setNetTypeCode("07");
            }

            if ("TD_OPEN".equals(createPersonUserRD.getOpenType()))
            {
                userTradeData.setNetTypeCode("18");
            }

            // 初始化密码
            if ("1".equals(createPersonUserRD.getDefaultPwdFlag()))
            {
                userTradeData.setUserPasswd(createPersonUserRD.getCardPasswd());
                userTradeData.setRsrvTag1("1");// 密码卡
                userTradeData.setRsrvStr3(createPersonUserRD.getPassCode());
                // 如果存在，即插入密码因子表
                /*
                 * IData param = new DataMap(); param.put("USER_ID", createPersonUserRD.getUca().getUserId());
                 * param.put("ENCRYPT_GENE", createPersonUserRD.getPassCode());
                 * Dao.executeUpdateByCodeCode("TF_F_USER_ENCRYPT_GENE", "INS_ENCRYPT", param);
                 */
            }
            if ("500".equals(btd.getRD().getOrderTypeCode()) || "700".equals(btd.getRD().getOrderTypeCode()))
                userTradeData.setRsrvNum1("1");// 更新用户表标记,用于实名制 sunxin
        }
    }

    /**
     * 生成台帐3G卡属性子表
     * 
     * @author sunxin
     * @param btd
     * @throws Exception
     */
    public void createUsinOpcData(BusiTradeData btd, IData resData) throws Exception
    {
        CreatePersonUserRequestData createPersonUserRD = (CreatePersonUserRequestData) btd.getRD();
        String serialNumber = createPersonUserRD.getUca().getUser().getSerialNumber();
        AttrTradeData attrTD = new AttrTradeData();
        attrTD.setUserId(createPersonUserRD.getUca().getUserId());
        attrTD.setInstType("R");
        attrTD.setInstId(SeqMgr.getInstId());
        attrTD.setAttrCode(resData.getString("OPC_CODE"));
        attrTD.setAttrValue(resData.getString("OPC_VALUE"));
        attrTD.setStartDate(SysDateMgr.getSysTime());
        attrTD.setEndDate(SysDateMgr.getTheLastTime());
        attrTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        attrTD.setRelaInstId(resData.getString("RELA_INST_ID"));
        btd.add(serialNumber, attrTD);

    }

    /**
     * 生成台帐邮寄资料表
     * 
     * @author sunxin
     * @param btd
     * @throws Exception
     */
    public void DealCreatePostTradeData(BusiTradeData btd) throws Exception
    {
        CreatePersonUserRequestData createUserRD = (CreatePersonUserRequestData) btd.getRD();
        String serialNumber = createUserRD.getUca().getUser().getSerialNumber();
        PostTradeData postTD = new PostTradeData();
        postTD.setId(createUserRD.getUca().getUserId()); // 标识:客户、用户或帐户标识
        postTD.setIdType("1");// 标识类型：0-客户，1-用户，2-帐户
        postTD.setPostName(createUserRD.getPostInfoPostName());
        postTD.setPostTag(createUserRD.getPostInfoPostTag());// 邮寄标志：0-不邮寄，1-邮寄
        // postTD.setPostContent(StringUtils.isNotBlank(createUserRD.getPostInfoPostTypeContent()) ?
        // createUserRD.getPostInfoPostTypeContent() : " ");
        postTD.setPostTypeset(createUserRD.getPostInfoPostTypeSet());// 邮寄方式：0-邮政，2-Email
        postTD.setPostCyc(createUserRD.getPostInfoPostCyc());// 邮寄周期：0-按月，1-按季，2-按年
        postTD.setPostAddress(createUserRD.getPostInfoPostAddress());
        postTD.setPostCode(createUserRD.getPostInfoPostCode());
        postTD.setEmail(createUserRD.getPostInfoEmail());
        postTD.setFaxNbr(createUserRD.getPostInfoFaxNbr());
        postTD.setCustType("0");
        postTD.setStartDate(SysDateMgr.getSysTime());// 预约开户需要处理时间，修改 sunxin
        postTD.setEndDate(SysDateMgr.getTheLastTime());
        postTD.setModifyTag(BofConst.MODIFY_TAG_UPD);
        postTD.setInstId(SeqMgr.getInstId());
        btd.add(serialNumber, postTD);

    }
}
