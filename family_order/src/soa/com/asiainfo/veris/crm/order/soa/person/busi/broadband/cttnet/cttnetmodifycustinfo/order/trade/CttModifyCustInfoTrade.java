
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetmodifycustinfo.order.trade;

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
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetmodifycustinfo.order.requestdata.CttModifyCustInfoReqData;

/**
 * 客户资料变更TRADE类
 */
public class CttModifyCustInfoTrade extends BaseTrade implements ITrade
{

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        // 1、处理个人客户
        createCustPersonTradeInfo(btd);
        // 2、处理客户资料
        createCustomerTradeData(btd);
        // 3、处理other表
        createOtherTradeInfo(btd);
        // 4、实名制积分处理 个性化的功能通过配置添加action进行处理
    }

    /**
     * 处理客户资料台账
     * 
     * @param btd
     * @throws Exception
     */
    private void createCustomerTradeData(BusiTradeData btd) throws Exception
    {
        CttModifyCustInfoReqData custInfoRD = (CttModifyCustInfoReqData) btd.getRD();
        CustomerTradeData customerTradeData = btd.getRD().getUca().getCustomer().clone();// 用老的客户资料克隆出一个客户订单数据对象
        if (StringUtils.isNotBlank(custInfoRD.getIsRealName()))// 如果页面有将实名制标记赋值，则使用界面上的值覆盖
        {
            customerTradeData.setIsRealName(custInfoRD.getIsRealName());
        }
        // 可能修改下面3个字段的值
        customerTradeData.setCustName(custInfoRD.getCustName());
        customerTradeData.setPsptTypeCode(custInfoRD.getPsptTypeCode());
        customerTradeData.setPsptId(custInfoRD.getPsptId());
        customerTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
        
        customerTradeData.setRsrvStr7(custInfoRD.getAgentCustName());// 经办人名称
        customerTradeData.setRsrvStr8(custInfoRD.getAgentPsptTypeCode());// 经办人证件类型
        customerTradeData.setRsrvStr9(custInfoRD.getAgentPsptId());// 经办人证件号码
        customerTradeData.setRsrvStr10(custInfoRD.getAgentPsptAddr());// 经办人地址

        // 注意：其他不变的字段就不需要再设值了，克隆出来的对象就存在
        btd.add(custInfoRD.getUca().getUser().getSerialNumber(), customerTradeData);
    }

    /**
     * 处理个人客户台账表
     * 
     * @throws Exception
     */
    private void createCustPersonTradeInfo(BusiTradeData btd) throws Exception
    {

        CttModifyCustInfoReqData rd = (CttModifyCustInfoReqData) btd.getRD();// 获取请求数据对象
        CustPersonTradeData custPersonTradeData = rd.getUca().getCustPerson().clone();// 克隆一个个人客户资料数据对象

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
        custPersonTradeData.setBirthday(rd.getBirthday());
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

        btd.add(rd.getUca().getUser().getSerialNumber(), custPersonTradeData);// 添加订单对象到btd
        

		
       /*******add by liangdg3 for REQ201908310001关于优化铁通模块客户资料变更界面的需求 at 20190912  start *******/
        String strCustId = custPersonTradeData.getCustId();
    	String strPartition_id = strCustId.substring(strCustId.length() - 4);    	
    	String strTradeTpyeCode = btd.getTradeTypeCode(); 
    	
        IDataset list = CustPersonInfoQry.qryCustPersonOtherByCustId(strCustId);//Dao.qryByParse(parser, BizRoute.getRouteId());
		
		if( IDataUtil.isNotEmpty(list) )
		{
			IData custPersonOtherData = list.first();
		
        	custPersonOtherData.put("UPDATE_TIME", SysDateMgr.getSysTime());
        	custPersonOtherData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        	custPersonOtherData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        	custPersonOtherData.put("RSRV_STR1",strTradeTpyeCode);
        	custPersonOtherData.put("RSRV_STR2", rd.getRespCustName());
        	custPersonOtherData.put("RSRV_STR3", rd.getRespPsptTypeCode());
        	custPersonOtherData.put("RSRV_STR4", rd.getRespPsptId());
        	custPersonOtherData.put("RSRV_STR5", rd.getRespPsptAddr());
			
			Dao.update("TF_F_CUST_PERSON_OTHER", custPersonOtherData, new String[] { "PARTITION_ID", "CUST_ID" });
		}else{
			IData custPersonOtherData = new DataMap();
        	custPersonOtherData.put("PARTITION_ID", strPartition_id);
        	custPersonOtherData.put("CUST_ID", custPersonTradeData.getCustId());
        	custPersonOtherData.put("CREATE_TIME", SysDateMgr.getSysTime());
        	custPersonOtherData.put("UPDATE_TIME", SysDateMgr.getSysTime());
        	custPersonOtherData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        	custPersonOtherData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        	custPersonOtherData.put("REMARK", "客户资料变更(铁通)");
        	custPersonOtherData.put("RSRV_STR1",strTradeTpyeCode);
        	custPersonOtherData.put("RSRV_STR2", rd.getRespCustName());
        	custPersonOtherData.put("RSRV_STR3", rd.getRespPsptTypeCode());
        	custPersonOtherData.put("RSRV_STR4", rd.getRespPsptId());
        	custPersonOtherData.put("RSRV_STR5", rd.getRespPsptAddr());
			Dao.insert("TF_F_CUST_PERSON_OTHER", custPersonOtherData);
		}
		/*******add by liangdg3 for REQ201908310001关于优化铁通模块客户资料变更界面的需求 at 20190912  end *******/
	
    }

    /**
     * 处理other台账表
     * 
     * @param btd
     * @throws Exception
     */
    private void createOtherTradeInfo(BusiTradeData btd) throws Exception
    {
        CttModifyCustInfoReqData rd = (CttModifyCustInfoReqData) btd.getRD();
        CustomerTradeData oldCustomer = rd.getUca().getCustomer();// 原来的客户资料
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
        otherTradeData.setRsrvStr7(oldCustomer.getRsrvStr7());
        otherTradeData.setRsrvStr8(oldCustomer.getRsrvStr8());
        otherTradeData.setRsrvStr9(oldCustomer.getRsrvStr9());
        otherTradeData.setRsrvStr10(oldCustomer.getRsrvStr10());
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
            tradeOther.setRsrvStr4("9726");// 业务类型
            tradeOther.setRsrvStr5(CSBizBean.getVisit().getStaffName());// 办理员工名称
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
            tradeOther.setRsrvValue("户特殊资料修改");
            tradeOther.setRsrvStr1(CSBizBean.getVisit().getStaffId());
            tradeOther.setRsrvStr2(rd.getAcceptTime());
            tradeOther.setRsrvStr3("1");
            tradeOther.setRsrvStr4("9726");// 业务类型
            tradeOther.setRsrvStr5(CSBizBean.getVisit().getStaffName());// 办理员工名称
            tradeOther.setStartDate(rd.getAcceptTime());
            tradeOther.setEndDate(SysDateMgr.END_DATE_FOREVER);
            tradeOther.setStaffId(CSBizBean.getVisit().getStaffId());
            tradeOther.setDepartId(CSBizBean.getVisit().getDepartId());
            tradeOther.setModifyTag(BofConst.MODIFY_TAG_ADD); // 新增
            tradeOther.setInstId(SeqMgr.getInstId());
            btd.add(serialNumber, tradeOther);
        }
    }
}
