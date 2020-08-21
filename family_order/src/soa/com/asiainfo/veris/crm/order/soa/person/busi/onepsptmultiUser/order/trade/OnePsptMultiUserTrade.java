package com.asiainfo.veris.crm.order.soa.person.busi.onepsptmultiUser.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.person.busi.onepsptmultiUser.order.requestdata.OnePsptMultiUserRequestData;
import com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.NationalOpenLimitBean;

public class OnePsptMultiUserTrade extends BaseTrade implements ITrade{

	@Override
	public void createBusiTradeData(BusiTradeData bd) throws Exception {
		OnePsptMultiUserRequestData req = (OnePsptMultiUserRequestData)bd.getRD();
		//首先根据选择的证件类型来判断客户类型（这里没有根据customer表中的cust_type来判断）--判断在JS中已判断，这里直接取用isUnitPsptType;
		//个人客户证件 0--1--2--A--I--H--O--N--P
		//非个人用户证件类型   M--E--D--G--L
		String isUnitPsptType = req.getIsUnitPsptType();
		String smsContext = "";
		boolean ifPerson = true;
		//对于个人客户，户主信息一证多名的号码，点击提交后，将客户证件号码置为18个1，并将该号码实名制标识位置为非实名，同时系统直接提交非实名半停机，并短信告知客户（这里直接调用半停机过程）
		if(StringUtils.equals(isUnitPsptType, "0")){
			ifPerson = true;
			smsContext = "尊敬的客户，您好，该号码登记的身份证信息不符合实名制要求，请您于3天内前往我公司营业网点登记实名信息，逾期将停止通信服务，感谢您的配合。  中国移动";
			updatePersonCustomer(bd);//创建客户台账信息
		//对于单位证件开户，将号码的使用人证件号码置为18个1，并短信催告，
		//发出短信3天后将号码标识位变为非实名，并做半停机，半停机成功后恢复实名标识位，提交半停机3天后进行非实名制全停（先取消实名标识位），提交非实名全停成功后，恢复实名标识位。--这里也是在半停机和全停机procedure中进行处理
		}else if(StringUtils.equals(isUnitPsptType, "1")){
			ifPerson = false;
			updateUnitCustomer(bd);//创建客户台账信息
//			sendMsg = "尊敬的客户，您好，该号码登记的使用人信息不符合国家实名制要求，请您于3天内前往我公司营业网点登记使用人实名信息，逾期将停止通信服务，感谢您的配合。  中国移动 ";
			smsContext = "尊敬的客户，您好，该号码登记的使用人信息不符合国家实名制要求，请您于3天内前往我公司营业网点登记使用人实名信息，逾期将停止通信服务，感谢您的配合。  中国移动";
		}else{
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户非可办理客户类型！");
		}
		insertOterTrade(bd,ifPerson);//创建台账其余表
		insertOnePsptMultiUser(bd,ifPerson);//创建半停机数据
		//调用短信发送接口
		IData inputData = new DataMap();
		for(int i = 0,size = req.getUserList().size();i<size ;i++){
			inputData = (IData)req.getUserList().get(i);
			inputData.put("TRADE_ID", bd.getTradeId());
			smsNotifyCustomer(inputData,smsContext);
		}
	}
	/**
	 * 拼装客户台账信息
	 * 这里进行个人客户账户的变更---将客户证件号码置为18个1  pspt_id，并将该号码实名制标识位置为非实名   is_real_name（0非实名，1实名）
	 * @param req
	 * @throws Exception 
	 */     
	private void updatePersonCustomer(BusiTradeData btd) throws Exception{
		OnePsptMultiUserRequestData req = (OnePsptMultiUserRequestData)btd.getRD();
		IDataset custList = req.getUserList();
		for(int i =0,size=custList.size();i<size;i++){
			IData data = (IData)custList.get(i);
			checkData(data,req);//这里对数据进行校验，验证是否数据有为空现象
			IData custData = UcaInfoQry.qryCustomerInfoByCustId(data.getString("CUST_ID"));
            if (IDataUtil.isNotEmpty(custData))
            {
            	CustomerTradeData customerTradeData = new CustomerTradeData(custData);
            	customerTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
            	customerTradeData.setRsrvStr6(customerTradeData.getPsptId());
                customerTradeData.setPsptId("111111111111111111");
                customerTradeData.setIsRealName("0");
                data.put("CUST_NAME", customerTradeData.getCustName());
                data.put("PSPT_TYPE_CODE", req.getPsptTypeCode());
                data.put("PSPT_ID", req.getPsptId());
                data.put("EPARCHY_CODE", customerTradeData.getEparchyCode());
                btd.add(req.getSerialNumber(), customerTradeData);
            }else{
            	CSAppException.apperr(CrmCommException.CRM_COMM_103,"查询客户信息出错！");	
            }
		}
	}
	/**
	 * 
	 * 拼装客户台账信息
	 * 这里进行非个人用户证件账户的变更---将客户证件号码置为18个1  pspt_id，并将该号码实名制标识位置为非实名   is_real_name（0非实名，1实名）
	 * @author chenfeng9
	 * @date 2017年12月6日
	 * @param btd
	 * @throws Exception
	 * @return void
	 */
	private void updateUnitCustomer(BusiTradeData btd) throws Exception{
		OnePsptMultiUserRequestData req = (OnePsptMultiUserRequestData)btd.getRD();
		IDataset custList = req.getUserList();
		for(int i =0,size=custList.size();i<size;i++){
			IData data = (IData)custList.get(i);
			checkData(data,req);//这里对数据进行校验，验证是否数据有为空现象
			IData personData = UcaInfoQry.qryPerInfoByCustId(data.getString("CUST_ID"));
			String psptTypeCode = personData.getString("PSPT_TYPE_CODE").trim();
            if (IDataUtil.isNotEmpty(personData))
            {
            	CustPersonTradeData custPersonTradeData = new CustPersonTradeData(personData);
             	//这里先验证cust_person表中的pspt_type_code是个人证件还是单位证件。如果是个人证件修改pspt_type_code和pspt_id。如果是单位证件修改RSRV_STR5，RSRV_STR6，RSRV_STR7
                 if(StringUtils.contains("012AIHONP", psptTypeCode)){
                 	if(StringUtils.isBlank(custPersonTradeData.getRsrvStr4())){
                 		custPersonTradeData.setRsrvStr4(custPersonTradeData.getPsptId());
                 	}
                 	data.put("CUST_NAME", custPersonTradeData.getCustName());
                 	data.put("PSPT_TYPE_CODE", custPersonTradeData.getPsptTypeCode());
                 	data.put("PSPT_ID", custPersonTradeData.getPsptId());
                 	custPersonTradeData.setPsptId("11111111111111111");
                    btd.add(req.getSerialNumber(), custPersonTradeData);                 
                 }else if (StringUtils.contains("DEMGL", psptTypeCode)){
                	if(StringUtils.isBlank(custPersonTradeData.getRsrvStr4())){
                 		custPersonTradeData.setRsrvStr4(custPersonTradeData.getRsrvStr6());
                 	}
                	data.put("CUST_NAME", custPersonTradeData.getRsrvStr5());
                 	data.put("PSPT_TYPE_CODE", custPersonTradeData.getRsrvStr6());
                 	data.put("PSPT_ID", custPersonTradeData.getRsrvStr7());
                 	custPersonTradeData.setRsrvStr7("11111111111111111");
                    btd.add(req.getSerialNumber(), custPersonTradeData);       
                 }
                data.put("EPARCHY_CODE", custPersonTradeData.getEparchyCode());
            	custPersonTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                btd.add(req.getSerialNumber(), custPersonTradeData);
            }else{
            	CSAppException.apperr(CrmCommException.CRM_COMM_103,"查询客户信息出错！");	
            }
		}
	}
	/**
     * 短信通知客户账户的变更
     * @param input
     * @throws Exception
     */
    public void smsNotifyCustomer(IData input,String smsContext) throws Exception
    {
    	String serialNumber = input.getString("SERIAL_NUMBER","");
    	String userId = input.getString("USER_ID","");
    	
    	if(serialNumber.startsWith("KD_"))
    	{					
    		serialNumber = serialNumber.substring(3,serialNumber.length());
    	}
    	
		String forceObject1 = "10086235" + serialNumber;
		IData smsData = new DataMap();
		smsData.put("TRADE_ID", input.getString("TRADE_ID"));
		smsData.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
		smsData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
		smsData.put("SMS_PRIORITY", "5000");
		smsData.put("CANCEL_TAG", "3");
		smsData.put("REMARK", "一证多名业务，tradeTypeCode为4912");
		smsData.put("NOTICE_CONTENT_TYPE", "0");
		smsData.put("SMS_TYPE_CODE", "I0");
		smsData.put("RECV_OBJECT", serialNumber);
		smsData.put("RECV_ID", userId);
		smsData.put("FORCE_OBJECT", forceObject1);
		smsData.put("NOTICE_CONTENT", smsContext);
		SmsSend.insSms(smsData);
    }
   
    /**
     * 将页面填入信息保存到oterTrade表中
     * TODO
     * @author chenfeng9
     * @date 2017年12月6日
     * @param bd
     * @return void
     * @throws Exception 
     */
     private void insertOterTrade(BusiTradeData bd,boolean ifPerson) throws Exception{
		 OnePsptMultiUserRequestData req = (OnePsptMultiUserRequestData)bd.getRD();
		 String serialNumber = req.getSerialNumber();
		 IData inparam = new DataMap();
		 inparam.put("SERIAL_NUMBER", serialNumber);
		 inparam.put("REMOVE_TAG", "0");
		 IDataset UserInfos = Dao.qryByCode("TF_F_USER", "SEL_USER_BY_SN", inparam);
		 if(IDataUtil.isNotEmpty(UserInfos)){
			 String userId = ((IData)UserInfos.get(0)).getString("USER_ID");
			 OtherTradeData tradeOther = new OtherTradeData();
		     tradeOther.setUserId(userId);
		     if(ifPerson){
		    	 tradeOther.setRsrvValueCode("ONEPSPTMULTIUSER_P");
		    	 tradeOther.setRsrvValue("一证多名个人业务处理");
		     }else{
		    	 tradeOther.setRsrvValueCode("ONEPSPTMULTIUSER_U");
		    	 tradeOther.setRsrvValue("一证多名单位业务处理");
		     }
		     tradeOther.setRsrvStr1(CSBizBean.getVisit().getStaffId());
		     tradeOther.setRsrvStr2(req.getStaffId());
		     tradeOther.setRsrvStr3(req.getPsptTypeCode());//存放证件类型
		     tradeOther.setRsrvStr4("4912");// 业务类型
		     tradeOther.setRsrvStr5(CSBizBean.getVisit().getStaffName());// 办理员工名称
		     tradeOther.setStartDate(SysDateMgr.getSysDate());
		     tradeOther.setEndDate(SysDateMgr.END_DATE_FOREVER);// --取最大结束时间，不要写死
		     tradeOther.setStaffId(CSBizBean.getVisit().getStaffId());
		     tradeOther.setDepartId(CSBizBean.getVisit().getDepartId());
		     tradeOther.setModifyTag(BofConst.MODIFY_TAG_ADD); // 新增
		     tradeOther.setInstId(SeqMgr.getInstId());
		     bd.add(serialNumber, tradeOther);
		 }
     }
     
     /**
      * 
      * TODO 插入数据到一证多号表中TF_F_ONEPSPTMULTIUSER
      * @author chenfeng9
      * @date 2017年11月29日
      * @param bdt
      * @return void
      */
     private void insertOnePsptMultiUser(BusiTradeData bd,boolean ifPerson) throws Exception
     {
 		OnePsptMultiUserRequestData req = (OnePsptMultiUserRequestData)bd.getRD();
         IDataset ds = req.getUserList();
         if (DataSetUtils.isNotBlank(ds)) {
             //插入表    TF_F_ONEPSPTMULTIUSER               
             for (int i = 0; i < ds.size(); i++) {
                 IData data = ds.getData(i);
                 //组装TF_F_ONEPSPTMULTIUSER表数据
                 long tradeId = Long.parseLong(SeqMgr.getTradeId());
                 //long tradeId = mainTrade.getLong("TRADE_ID");
                 data.put("TRADE_ID", tradeId);
                 data.put("INSERT_DATE", SysDateMgr.getSysTime());
                 data.put("INSERT_STAFFID", CSBizBean.getVisit().getStaffId());
                 data.put("REMOVE_TAG", "0");
                 
                 //这里进行证件类型的转换 --这里还是需要考虑单位证件如何去同步一证五号平台，是同步证使用人信息，还是同步单位证件信息。
                 //如果同步使用人证件信息，同步了有用吗？如果是同步单位证件信息，那是如何同步？
                 NationalOpenLimitBean bean = (NationalOpenLimitBean) BeanManager.createBean(NationalOpenLimitBean.class);
                 IDataset checkResults = bean.checkPspt(data.getString("PSPT_TYPE_CODE"));
                 data.put("ID_CARD_TYPE", checkResults.getData(0).getString("PARA_CODE1"));
                 data = bean.addSeq(data);
                 //获取用户名和证件类型和证件ID--在修改用户资料时放入；
                 if(ifPerson){
                     data.put("RSRV_STR3", "0");
                     data.put("RSRV_STR4", "非扫描一证多名个人证件业务处理");
                     data.put("RSRV_DATE3", SysDateMgr.getSysTime());
                 }else{
                     data.put("RSRV_STR3", "1");
                     data.put("RSRV_STR4", "非扫描一证多名非个人证件业务处理");
                     data.put("RSRV_DATE3", SysDateMgr.addDays2(3));
                 }
                 data.put("FIRST_STOPTIME", null);
                 data.put("SECOND_STOPTIME", null);
                 data.put("TRIGGER_TRADE_ID", bd.getTradeId());                
                 data.put("RSRV_STR1", null);
                 data.put("RSRV_STR2", null);
                 data.put("RSRV_STR5", null);
                 data.put("RSRV_DATE1", null);
                 data.put("RSRV_DATE2", null);
                 data.put("RSRV_NUM1", null);
                 data.put("RSRV_NUM2", null);
                 data.put("RSRV_NUM3", null);
                 data.put("RSRV_TAG1", null);
                 data.put("RSRV_TAG2", null);
                 data.put("RSRV_TAG3", null);
                 data.put("REMARK", null);
                 //关键字段校验
                 checkInsertData(data);
             }
             Dao.insert("TF_F_ONEPSPTMULTIUSER", ds);
         }
     }
     
     /**
      *这里对数据进行为空校验--校验字段为CUST_ID,SERIAL_NUMBER,USER_ID
      * @throws Exception 
      *
      */
     private void checkData(IData data,OnePsptMultiUserRequestData req) throws Exception{
     	if(StringUtils.isBlank(data.getString("CUST_ID"))){
     		CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据用户证件号码"+req.getPsptId()+"查询到客户ID为空信息！");
     	}else if(StringUtils.isBlank(data.getString("SERIAL_NUMBER"))){
     		CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据用户证件号码"+req.getPsptId()+"查询到号码为空信息！");
     	}else if(StringUtils.isBlank(data.getString("USER_ID"))){
     		CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据用户证件号码"+req.getPsptId()+"查询到用户ID为空信息！");
     	}
     }
     /**
      *这里对要插入TF_F_ONEPSPTMULTIUSER表的数据进行校验。
      * @throws Exception 
      *
      */
     private void checkInsertData(IData data) throws Exception{
    	 IDataUtil.chkParam(data, "TRIGGER_TRADE_ID");
    	 IDataUtil.chkParam(data, "SERIAL_NUMBER");
    	 IDataUtil.chkParam(data, "CUST_NAME");
    	 IDataUtil.chkParam(data, "ID_CARD_TYPE");
    	 IDataUtil.chkParam(data, "PSPT_ID");
    	 IDataUtil.chkParam(data, "EPARCHY_CODE");
     }
}