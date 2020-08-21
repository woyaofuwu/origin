
package com.asiainfo.veris.crm.order.soa.person.busi.unitopenchangeactive.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.*;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.unitopenchangeactive.order.requestdata.UnitOpenChangeActiveReqData;
import org.apache.log4j.Logger;

/**
 * 单位证件开户激活TRADE类
 * 
 * @author tanzheng
 */
public class UnitOpenChangeActiveTrade extends BaseTrade implements ITrade
{

	protected static final Logger log = Logger.getLogger(UnitOpenChangeActiveTrade.class);
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        createMainTradeInfo(btd);

        // 1、处理个人客户
        createCustPersonTradeInfo(btd);
        // 2、处理客户资料
        createCustomerTradeData(btd);
        // 3、处理other表
        createOtherTradeInfo(btd);
        // 4、实名制积分处理 个性化的功能通过配置添加action进行处理
        CustomerTradeData customerTD = btd.getRD().getUca().getCustomer();
        String psptTypeCode = customerTD.getPsptTypeCode();
        if(psptTypeCode!=null&&psptTypeCode.trim().equals("E")){//营业执照
            createOtherTradeDataEnterprise(btd);
        }else if(psptTypeCode!=null&&psptTypeCode.trim().equals("M")){//组织机构代码证
            createOtherTradeDataOrg(btd);
        }
        if(psptTypeCode!=null&&("E".equals(psptTypeCode)
                ||"M".equals(psptTypeCode)
                ||"G".equals(psptTypeCode)
                ||"L".equals(psptTypeCode)
                ||"D".equals(psptTypeCode)
                )){
            createUserTradeInfo(btd);

        }
    }

    /**
     * 处理客户资料台账
     * 
     * @param btd
     * @throws Exception
     */
    private void createCustomerTradeData(BusiTradeData btd) throws Exception
    {
        UnitOpenChangeActiveReqData custInfoRD = (UnitOpenChangeActiveReqData) btd.getRD();
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
        
        //@Add yanwu
        String strAcctTag = btd.getRD().getUca().getUser().getAcctTag();
        if( !"0".equals(strAcctTag) ){
        	AccountTradeData acct = btd.getRD().getUca().getAccount();
        	acct.setPayName(custInfoRD.getCustName());
        	acct.setModifyTag(BofConst.MODIFY_TAG_UPD);
        	acct.setRemark("客户资料变更关联修改账户名称");
        	btd.add(custInfoRD.getUca().getUser().getSerialNumber(), acct);
        }
        
    }

    /**
     * 处理个人客户台账表
     * 
     * @throws Exception
     */
    private void createCustPersonTradeInfo(BusiTradeData btd) throws Exception
    {

        UnitOpenChangeActiveReqData rd = (UnitOpenChangeActiveReqData) btd.getRD();// 获取请求数据对象
        CustPersonTradeData custPersonTradeData = rd.getUca().getCustPerson();// 个人客户资料数据对象
        if (custPersonTradeData == null)
        {// 考虑资料未全异常资料的情况下
            CSAppException.apperr(CustException.CRM_CUST_69);// 获取个人客户资料无数据!
        }
        custPersonTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);// 标记订单为修改
        custPersonTradeData.setCustName(rd.getCustName());
        custPersonTradeData.setPsptId(rd.getPsptId());
        custPersonTradeData.setPsptTypeCode(rd.getPsptTypeCode());
        custPersonTradeData.setEparchyCode(CSBizBean.getUserEparchyCode());
        custPersonTradeData.setCustId(rd.getUca().getCustId());
        custPersonTradeData.setCityCode(rd.getUca().getUser().getCityCode());
        custPersonTradeData.setPostAddress(rd.getPostAddress());
        custPersonTradeData.setHomeAddress(rd.getHomeAddress());
        custPersonTradeData.setSex(rd.getSex());
        custPersonTradeData.setPsptEndDate(rd.getPsptEndDate());
        custPersonTradeData.setPhone(rd.getPhone());
        custPersonTradeData.setContact(rd.getContact());
        custPersonTradeData.setContactPhone(rd.getContactPhone());
        custPersonTradeData.setPostCode(rd.getPostCode());
        custPersonTradeData.setContactTypeCode(rd.getContactTypeCode());
        custPersonTradeData.setWorkName(rd.getWorkName());
        custPersonTradeData.setWorkDepart(rd.getWorkDepart());


        //如果是实名制
        if (!StringUtils.equals("1", btd.getRD().getUca().getUserOriginalData().getCustomer().getIsRealName()) && StringUtils.equals("1", rd.getIsRealName()))
        {

            if(rd.getBirthday()==null||rd.getBirthday().trim().length()==0){
                custPersonTradeData.setBirthday("1900-01-01");
            }else{
                custPersonTradeData.setBirthday(rd.getBirthday());
            }

        }else{
            String birthday=btd.getRD().getUca().getUserOriginalData().getCustPerson().getBirthday();
            if(birthday==null||birthday.trim().length()==0){
                birthday="1900-01-01";
            }
            custPersonTradeData.setBirthday(birthday);
        }


        custPersonTradeData.setJobTypeCode(rd.getJobTypeCode());
        custPersonTradeData.setJob(rd.getJob());
        custPersonTradeData.setEducateDegreeCode(rd.getEducateDegreeCode());
        custPersonTradeData.setEmail(rd.getEmail());
        custPersonTradeData.setFaxNbr(rd.getFaxNbr());
        custPersonTradeData.setMarriage(rd.getMarriage());
        custPersonTradeData.setNationalityCode(rd.getNationalityCode());
        custPersonTradeData.setCharacterTypeCode(rd.getCharacterTypeCode());
        custPersonTradeData.setWebuserId(rd.getWebuserId());
        custPersonTradeData.setLanguageCode(rd.getLanguageCode());
        custPersonTradeData.setLocalNativeCode(rd.getLocalNativeCode());
        custPersonTradeData.setCommunityId(rd.getCommunityId());
        custPersonTradeData.setReligionCode(rd.getReligionCode());
        custPersonTradeData.setFolkCode(rd.getFolkCode());
        custPersonTradeData.setRevenueLevelCode(rd.getRevenueLevelCode());
        custPersonTradeData.setPsptAddr(rd.getPsptAddr());
        custPersonTradeData.setRemark(rd.getRemark());
        custPersonTradeData.setRsrvStr5(rd.getUse());
        custPersonTradeData.setRsrvStr6(rd.getUsePsptTypeCode());
        custPersonTradeData.setRsrvStr7(rd.getUsePsptId());
        custPersonTradeData.setRsrvStr8(rd.getUsePsptAddr());
        
        custPersonTradeData.setRsrvStr3(rd.getPassNumber());//港澳居住证通行证号码
        custPersonTradeData.setRsrvStr4(rd.getLssueNumber());//港澳居住证签证次数
        

        btd.add(rd.getUca().getUser().getSerialNumber(), custPersonTradeData);// 添加订单对象到btd
        
        //新增使用人证件类型录入
        String strUseName = custPersonTradeData.getRsrvStr5();
    	String strUsePsptTypeCode = custPersonTradeData.getRsrvStr6();
    	String strUsePsptId = custPersonTradeData.getRsrvStr7();
    	String strUsePsptAddr = custPersonTradeData.getRsrvStr8();
    	
    	String strCustId = custPersonTradeData.getCustId();
    	String strPartition_id = strCustId.substring(strCustId.length() - 4);
    	
    	String strTradeTpyeCode = btd.getTradeTypeCode();
    	
    	IData idRequestData = btd.getRD().getPageRequestData();
    	String strRsrvstr2 = "";
    	String strRsrvstr3 = "";
    	String strRsrvstr4 = "";
    	String strRsrvstr5 = "";
    	
    	if(IDataUtil.isNotEmpty(idRequestData))
    	{
    		String strBatChId = btd.getRD().getBatchId();
    		String ss = idRequestData.getString("SS_PAGE", "");
        	String strBatchOperType = idRequestData.getString("BATCH_OPER_TYPE", "");
        	if((StringUtils.isNotBlank(strBatChId) && "MODIFYCUSTINFO_M2M".equals(strBatchOperType))||"ssPage".equals(ss))
        	{
        		strRsrvstr2 = idRequestData.getString("RSRV_STR2", "");
            	strRsrvstr3 = idRequestData.getString("RSRV_STR3", "");
            	strRsrvstr4 = idRequestData.getString("RSRV_STR4", "");
            	strRsrvstr5 = idRequestData.getString("RSRV_STR5", "");
        	}
    		
    	}
    	
    	if(StringUtils.isNotBlank(strUseName) || StringUtils.isNotBlank(strUsePsptTypeCode)
         ||StringUtils.isNotBlank(strUsePsptId) || StringUtils.isNotBlank(strUsePsptAddr) 
         ||StringUtils.isNotBlank(strRsrvstr2) || StringUtils.isNotBlank(strRsrvstr3) 
         ||StringUtils.isNotBlank(strRsrvstr4) || StringUtils.isNotBlank(strRsrvstr5))
    	{
    		
        	/*IData param = new DataMap();
    		//param.put(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());
    		param.put("CUST_ID", strCustId);
    		SQLParser parser = new SQLParser(param);
    		parser.addSQL(" SELECT T.PARTITION_ID, T.CUST_ID, T.USE_NAME, T.USE_PSPT_TYPE_CODE, T.USE_PSPT_ID, T.USE_PSPT_ADDR ");
    		parser.addSQL(" FROM TF_F_CUST_PERSON_OTHER T ");
    		parser.addSQL(" WHERE T.CUST_ID = :CUST_ID ");
    		parser.addSQL(" AND T.PARTITION_ID = MOD(:CUST_ID, 10000) "); */       
    		IDataset list = CustPersonInfoQry.qryCustPersonOtherByCustId(strCustId);//Dao.qryByParse(parser, BizRoute.getRouteId());
    		if( IDataUtil.isNotEmpty(list) )
    		{
    			IData custPersonOtherData = list.first();
    			if(StringUtils.isNotBlank(custPersonTradeData.getRsrvStr5()))
    			{
    				custPersonOtherData.put("USE_NAME", custPersonTradeData.getRsrvStr5());
    			}
    			if(StringUtils.isNotBlank(custPersonTradeData.getRsrvStr6()))
    			{
    				custPersonOtherData.put("USE_PSPT_TYPE_CODE", custPersonTradeData.getRsrvStr6());
    			}
    			if(StringUtils.isNotBlank(custPersonTradeData.getRsrvStr7()))
    			{
    				custPersonOtherData.put("USE_PSPT_ID", custPersonTradeData.getRsrvStr7());
    			}
    			if(StringUtils.isNotBlank(custPersonTradeData.getRsrvStr8()))
    			{
    				custPersonOtherData.put("USE_PSPT_ADDR", custPersonTradeData.getRsrvStr8());
    			}
            	custPersonOtherData.put("UPDATE_TIME", SysDateMgr.getSysTime());
            	custPersonOtherData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            	custPersonOtherData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
            	custPersonOtherData.put("RSRV_STR1", strTradeTpyeCode);
            	if(StringUtils.isNotBlank(strRsrvstr2))
    			{
            		custPersonOtherData.put("RSRV_STR2", strRsrvstr2);
    			}
            	if(StringUtils.isNotBlank(strRsrvstr3))
    			{
            		custPersonOtherData.put("RSRV_STR3", strRsrvstr3);
    			}
            	if(StringUtils.isNotBlank(strRsrvstr4))
    			{
            		custPersonOtherData.put("RSRV_STR4", strRsrvstr4);
    			}
            	if(StringUtils.isNotBlank(strRsrvstr5))
    			{
            		custPersonOtherData.put("RSRV_STR5", strRsrvstr5);
    			}
    			Dao.update("TF_F_CUST_PERSON_OTHER", custPersonOtherData, new String[] { "PARTITION_ID", "CUST_ID" });
    		}else{
    			IData custPersonOtherData = new DataMap();
            	custPersonOtherData.put("PARTITION_ID", strPartition_id);
            	custPersonOtherData.put("CUST_ID", custPersonTradeData.getCustId());
            	custPersonOtherData.put("USE_NAME", custPersonTradeData.getRsrvStr5());
            	custPersonOtherData.put("USE_PSPT_TYPE_CODE", custPersonTradeData.getRsrvStr6());
            	custPersonOtherData.put("USE_PSPT_ID", custPersonTradeData.getRsrvStr7());
            	custPersonOtherData.put("USE_PSPT_ADDR", custPersonTradeData.getRsrvStr8());
            	custPersonOtherData.put("CREATE_TIME", SysDateMgr.getSysTime());
            	custPersonOtherData.put("UPDATE_TIME", SysDateMgr.getSysTime());
            	custPersonOtherData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            	custPersonOtherData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
            	custPersonOtherData.put("REMARK", "客户资料变更-使用人证据录入");
            	custPersonOtherData.put("RSRV_STR1", strTradeTpyeCode);
            	if(StringUtils.isNotBlank(strRsrvstr2))
    			{
            		custPersonOtherData.put("RSRV_STR2", strRsrvstr2);
    			}
            	if(StringUtils.isNotBlank(strRsrvstr3))
    			{
            		custPersonOtherData.put("RSRV_STR3", strRsrvstr3);
    			}
            	if(StringUtils.isNotBlank(strRsrvstr4))
    			{
            		custPersonOtherData.put("RSRV_STR4", strRsrvstr4);
    			}
            	if(StringUtils.isNotBlank(strRsrvstr5))
    			{
            		custPersonOtherData.put("RSRV_STR5", strRsrvstr5);
    			}
    			Dao.insert("TF_F_CUST_PERSON_OTHER", custPersonOtherData);
    		}
    		
    	}
    }

    /**
     * 把修改之前的一些信息放到主台账表中，在打印的时候需要把该五个字段的原信息打印出来 rsrv_str8（客户名称,证件类型,证件号码） rsrv_str9（联系号码）rsrv_str9(证件地址)
     * 
     * @throws Exception
     */
    private void createMainTradeInfo(BusiTradeData btd) throws Exception
    {
        CustPersonTradeData oldperson = btd.getRD().getUca().getCustPerson();
        String custName = oldperson.getCustName();
        String phone = oldperson.getPhone();
        String psptTypeCode = oldperson.getPsptTypeCode();
        String psptId = oldperson.getPsptId();
        String psptAddr = oldperson.getPsptAddr();
        if (null == custName || "".equals(custName))
            custName = " ";
        if (null == psptTypeCode || "".equals(psptTypeCode))
            psptTypeCode = " ";
        if (null == psptId || "".equals(psptId))
            psptId = " ";
        btd.getMainTradeData().setRsrvStr8(custName + "," + psptTypeCode + "," + psptId);
        btd.getMainTradeData().setRsrvStr9(phone);
        btd.getMainTradeData().setRsrvStr10(psptAddr);
    }

    /**
     * 处理other台账表
     * 
     * @param btd
     * @throws Exception
     */
    private void createOtherTradeInfo(BusiTradeData btd) throws Exception
    {
        UnitOpenChangeActiveReqData rd = (UnitOpenChangeActiveReqData) btd.getRD();
        CustomerTradeData oldCustomer = rd.getUca().getUserOriginalData().getCustomer();// 原来的客户资料
        String serialNumber = btd.getRD().getUca().getUser().getSerialNumber();

        OtherTradeData otherTradeData = new OtherTradeData();
        otherTradeData.setRsrvValueCode("CUST");
        otherTradeData.setRsrvValue("客户资料变更");
        otherTradeData.setUserId(rd.getUca().getUser().getUserId());
        otherTradeData.setStartDate(rd.getAcceptTime());
        otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
        otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
        otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTradeData.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值
        otherTradeData.setRsrvStr1(oldCustomer.getRsrvStr1());
        otherTradeData.setRsrvStr2(oldCustomer.getRsrvStr2());
        // 如果客户首次办理实名制，则需要记录下办理实名制的时间，记录在RSRV_STR3字段中，完工时 写入TF_F_CUSTOMER表的RSRV_STR3字段中
        if (!StringUtils.equals("1", oldCustomer.getIsRealName()) && StringUtils.equals("1", rd.getIsRealName()))
        {
            otherTradeData.setRsrvStr3(rd.getAcceptTime());// 存放实名制办理时间
        }
        else
        {
            otherTradeData.setRsrvStr3(oldCustomer.getRsrvStr3());
        }
        otherTradeData.setRsrvStr4(oldCustomer.getRsrvStr4());
        otherTradeData.setRsrvStr5(oldCustomer.getRsrvStr5());
        otherTradeData.setRsrvStr6(oldCustomer.getRsrvStr6());
        otherTradeData.setRsrvStr7(rd.getAgentCustName());// 经办人名称
        otherTradeData.setRsrvStr8(rd.getAgentPsptTypeCode());// 经办人证件类型
        otherTradeData.setRsrvStr9(rd.getAgentPsptId());// 经办人证件号码
        otherTradeData.setRsrvStr10(rd.getAgentPsptAddr());// 经办人地址
        /*
         * otherTradeData.setRsrvStr7(oldCustomer.getRsrvStr7()); otherTradeData.setRsrvStr8(oldCustomer.getRsrvStr8());
         * otherTradeData.setRsrvStr9(oldCustomer.getRsrvStr9());
         * otherTradeData.setRsrvStr10(oldCustomer.getRsrvStr10());
         */
        otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        otherTradeData.setInstId(SeqMgr.getInstId());
        btd.add(serialNumber, otherTradeData);

        // 如果本次修改 时存在实名制预受理的信息，则需要终止本条实名制预受理的信息
        IDataset dataset = UserOtherInfoQry.getUserOtherUserId(btd.getRD().getUca().getUserId(), "REAL", null);// 字段不全
        if (dataset != null && dataset.size() > 0)
        {
            // 用查询出来的tf_f_user_other对象来构建otherTradeData
            // 注意： 一定要保证查询出来数据包含了tf_f_user_other表的所有列，否则在完工时会将没有的列值update为空了
            OtherTradeData otherTradeDataDel = new OtherTradeData(dataset.getData(0));
            otherTradeDataDel.setModifyTag(BofConst.MODIFY_TAG_DEL);// 终止
            otherTradeDataDel.setEndDate(rd.getAcceptTime());// 取当前时间，取requestData中的
            // acceptTime，不要用sysDateMgr.getSysTime()
            btd.add(serialNumber, otherTradeDataDel);
        }

        // 首次办理实名制 则需要在TF_F_USER_OTHER表中记录 RSRV_VALUE_CODE='CHRN'的记录
        if (!StringUtils.equals("1", oldCustomer.getIsRealName()) && StringUtils.equals("1", rd.getIsRealName()))
        {
            OtherTradeData tradeOther = new OtherTradeData();
            tradeOther.setUserId(rd.getUca().getUserId());
            tradeOther.setRsrvValueCode("CHRN");
            tradeOther.setRsrvValue("实名制办理");
            tradeOther.setRsrvStr1(CSBizBean.getVisit().getStaffId());
            tradeOther.setRsrvStr2(rd.getAcceptTime());
            tradeOther.setRsrvStr3("1");
            tradeOther.setRsrvStr4(rd.getTradeType().getTradeTypeCode());// 业务类型
            tradeOther.setRsrvStr5(CSBizBean.getVisit().getStaffName());// 办理员工名称
            
            /* REQ201702040006优化保存过户前证件号
             * added by zhangxing3
             */
            tradeOther.setRsrvStr9(oldCustomer.getCustName());// 资料变更之前的姓名
            tradeOther.setRsrvStr10(oldCustomer.getPsptId());// 资料变更之前的姓名身份证号
            
            tradeOther.setStartDate(rd.getAcceptTime());
            tradeOther.setEndDate(SysDateMgr.END_DATE_FOREVER);// --取最大结束时间，不要写死
            tradeOther.setStaffId(CSBizBean.getVisit().getStaffId());
            tradeOther.setDepartId(CSBizBean.getVisit().getDepartId());
            tradeOther.setModifyTag(BofConst.MODIFY_TAG_ADD); // 新增
            tradeOther.setInstId(SeqMgr.getInstId());
            btd.add(serialNumber, tradeOther);
            // 办理实名制之后 修改了用户特殊资料，则需要在TF_F_USER_OTHER表中新增 RSRV_VALUE_CODE='CHRN'的记录
        }
        else if (StringUtils.equals("1", oldCustomer.getIsRealName()) && (!StringUtils.equals(rd.getCustName(), oldCustomer.getCustName())) || !StringUtils.equals(rd.getPsptTypeCode(), oldCustomer.getPsptTypeCode())
                || !StringUtils.equals(rd.getPsptId(), oldCustomer.getPsptId()) || !StringUtils.equals(rd.getPsptAddr(), rd.getUca().getCustPerson().getPsptAddr()))
        {

            OtherTradeData tradeOther = new OtherTradeData();
            tradeOther.setUserId(rd.getUca().getUserId());
            tradeOther.setRsrvValueCode("CHRN");
            tradeOther.setRsrvValue("客户特殊资料修改");
            tradeOther.setRsrvStr1(CSBizBean.getVisit().getStaffId());
            tradeOther.setRsrvStr2(rd.getAcceptTime());
            tradeOther.setRsrvStr3("1");
            tradeOther.setRsrvStr4(rd.getTradeType().getTradeTypeCode());// 业务类型
            tradeOther.setRsrvStr5(CSBizBean.getVisit().getStaffName());// 办理员工名称
            
            /* REQ201702040006优化保存过户前证件号
             * added by zhangxing3
             */
            tradeOther.setRsrvStr9(oldCustomer.getCustName());// 资料变更之前的姓名
            tradeOther.setRsrvStr10(oldCustomer.getPsptId());// 资料变更之前的姓名身份证号
            
            tradeOther.setStartDate(rd.getAcceptTime());
            tradeOther.setEndDate(SysDateMgr.END_DATE_FOREVER);
            tradeOther.setStaffId(CSBizBean.getVisit().getStaffId());
            tradeOther.setDepartId(CSBizBean.getVisit().getDepartId());
            tradeOther.setModifyTag(BofConst.MODIFY_TAG_ADD); // 新增
            tradeOther.setInstId(SeqMgr.getInstId());
            btd.add(serialNumber, tradeOther);
        }
    }
    public void createOtherTradeDataEnterprise(BusiTradeData btd) throws Exception
    {
        UnitOpenChangeActiveReqData modifycustinfoRD = (UnitOpenChangeActiveReqData) btd.getRD();
        String serialNumber = modifycustinfoRD.getUca().getUser().getSerialNumber();
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
            otherTD.setUserId(modifycustinfoRD.getUca().getUserId());
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
        UnitOpenChangeActiveReqData modifycustinfoRD = (UnitOpenChangeActiveReqData) btd.getRD();
        String serialNumber = modifycustinfoRD.getUca().getUser().getSerialNumber();        
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
    private void createUserTradeInfo(BusiTradeData btd) throws Exception
    {/*
        CreatePostPersonUserBean CreatePostPersonUserBean = BeanManager.createBean(CreatePostPersonUserBean.class);
    	IData data = new DataMap();
    	data.put("SERIAL_NUMBER", btd.getRD().getUca().getUser().getSerialNumber());
        IDataset postinfo = CreatePostPersonUserBean.getPostCardInfo(data);
        if(IDataUtil.isNotEmpty(postinfo))
          {
        	IData postdata=  postinfo.getData(0);
          	  if( "1".equals(postdata.getString("STATE")) )
        	     {
                  UserTradeData userTradeData = btd.getRD().getUca().getUser();
                  userTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                  //userTradeData.setAcctTag("0");
                  //userTradeData.setOpenMode("0");
                  //userTradeData.setCityCode("HNHK");
                  System.out.println("ModifyCustInfoTrade.javaxxxxxxxxxxxxxxxxx513 "+userTradeData);                  
                  btd.add(btd.getRD().getUca().getSerialNumber(), userTradeData);
        	     }
          }
    */
        UserTradeData userTradeData = btd.getRD().getUca().getUser();
        userTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
        userTradeData.setAcctTag("0");
        //userTradeData.setOpenMode("0");
        //userTradeData.setCityCode("HNHK");
        btd.add(btd.getRD().getUca().getSerialNumber(), userTradeData);
    }

}
