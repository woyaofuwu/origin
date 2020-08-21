package com.asiainfo.veris.crm.order.soa.person.busi.batscoredonate;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.TwoCheckSms;

public class BatScoreDonateBean extends CSBizBean {
	
    public void importBatData(IData input) throws Exception
    {
    	 IDataset set = new DatasetList(); // 上传excel文件内容明细
         IDataset results = new DatasetList();
         String fileId = input.getString("cond_STICK_LIST"); // 上传OCS监控excelL文件的编号
         String[] fileIds = fileId.split(",");
         ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
         for (String strfileId : fileIds)
         {
             IData array = ImpExpUtil.beginImport(null, strfileId, ExcelConfig.getSheets("import/bat/ScoreDonateBatImport.xml"));
             IDataset[] suc = (IDataset[]) array.get("right");// 解析成功的数据
             set.addAll(suc[0]);
         }

         String batchId = input.getString("BATCH_ID"); // 批次ID
         String staff_id = CSBizBean.getVisit().getStaffId();
         String city_code = CSBizBean.getVisit().getCityCode();
         String depart_id = CSBizBean.getVisit().getDepartId();
         String accept_month = SysDateMgr.getCurMonth();
         
         String CS_SDGRPLIMIT = getSysTagInfo("CS_SDGRPLIMIT", "TAG_NUMBER", "1000");// 参数字段
         int countLimit = Integer.parseInt(CS_SDGRPLIMIT);
         if(set.size() > countLimit){
        	 CSAppException.apperr(CrmCommException.CRM_COMM_103, "导入数据条数不能超出"+CS_SDGRPLIMIT+"，请重新导入！");
         }
         for (int i = 0; i < set.size(); i++)
         {
             IData result = new DataMap();
             result.clear();
             result.put("BATCH_ID", batchId);
             result.put("SERIAL_NUMBER_OUT", set.getData(i).getString("SERIAL_NUMBER_OUT"));
             result.put("SERIAL_NUMBER_IN", set.getData(i).getString("SERIAL_NUMBER_IN"));
             result.put("ACCT_ID", set.getData(i).getString("ACCT_ID"));
             result.put("ACCEPT_MONTH", accept_month);
             result.put("DONATE_SCORE", set.getData(i).getString("DONATE_SCORE"));
             result.put("IMPORT_TIME", SysDateMgr.getSysTime());
             result.put("TRADE_STAFF_ID", staff_id);
             result.put("TRADE_CITY_CODE", city_code);
             result.put("TRADE_DEPART_ID", depart_id);
             result.put("STATUS", "0");
             IData temp = new DataMap();
             temp.putAll(result);
             results.add(temp);
         }

         Dao.insert("TF_B_GRP_SCOREDONATE", results);
    }
    
    public void dealSubmit(IData input) throws Exception
    {
    	userScoreMove(input);
    }
    
    public void userScoreMove(IData input) throws Exception
    {        
        String batchId = input.getString("BATCH_ID");
        String serialNumberOut = "";
        String serialNumberIn = "";
        String acctID = "";
        String donateScore = "";
        
        IData param = new DataMap();
        param.put("BATCH_ID", batchId);
        IDataset dataset = Dao.qryByCodeParser("TF_B_GRP_SCOREDONATE", "SEL_BY_BID", param);
        if(IDataUtil.isEmpty(dataset)){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据BATCH_ID="+batchId+"查询无符合条件的数据！提交前请先[导入]数据");
        }else{
        	for(int i=0;i<dataset.size();i++){
        		try{
        			serialNumberOut = dataset.getData(i).getString("SERIAL_NUMBER_OUT");
        			serialNumberIn = dataset.getData(i).getString("SERIAL_NUMBER_IN");
        			acctID = dataset.getData(i).getString("ACCT_ID");
        			donateScore = dataset.getData(i).getString("DONATE_SCORE");
        			
        			UcaData ucaDataOut = UcaDataFactory.getNormalUca(serialNumberOut);
        			UcaData ucaDataIn = UcaDataFactory.getNormalUca(serialNumberIn);
        			if(!"1".equals(ucaDataOut.getCustomer().getIsRealName())){
        				CSAppException.apperr(CrmCommException.CRM_COMM_103, "积分转出号码"+serialNumberOut+"为非实名制用户！");
        			}
        			if(!"1".equals(ucaDataIn.getCustomer().getIsRealName())){
        				CSAppException.apperr(CrmCommException.CRM_COMM_103, "积分转入号码"+serialNumberIn+"为非实名制用户！");
        			}
        			if(!"E".equals(ucaDataOut.getCustomer().getPsptTypeCode())){
        				CSAppException.apperr(CrmCommException.CRM_COMM_103, "积分转出号码"+serialNumberOut+"实名证件类型不为营业执照！");
        			}
        			if(!"E".equals(ucaDataIn.getCustomer().getPsptTypeCode())){
        				CSAppException.apperr(CrmCommException.CRM_COMM_103, "积分转入号码"+serialNumberIn+"实名证件类型不为营业执照！");
        			}
        			if(!(ucaDataIn.getCustomer().getCustName()).equals(ucaDataOut.getCustomer().getCustName())){
        				CSAppException.apperr(CrmCommException.CRM_COMM_103, "积分转出号码与积分转入号码实名制名称不同！");
        			}
        			
        			String userIdOut = ucaDataOut.getUserId();
        			String userIdIn = ucaDataIn.getUserId();
        			if(IDataUtil.isEmpty(queryValidPayRela(userIdOut,acctID))||IDataUtil.isEmpty(queryValidPayRela(userIdIn,acctID))){
        				CSAppException.apperr(CrmCommException.CRM_COMM_103, "提交集团统付账号与转入转出号码所属集团统付账号不一致！");
        			}
        			
        			IDataset donateResult = AcctCall.userScoreMove(serialNumberOut, serialNumberIn, "ZZ", donateScore);
        			
        			if(IDataUtil.isEmpty(donateResult)){
        				
	    				IData inparam = new DataMap();
	    				inparam.put("BATCH_ID", batchId);
	    				inparam.put("SERIAL_NUMBER_OUT",serialNumberOut );
	    				inparam.put("SERIAL_NUMBER_IN",serialNumberIn );
	    				inparam.put("STATUS", "2");
	                    inparam.put("DONATE_RESULT", "积分转出号码或积分转入号码积分账户异常！");
	    				Dao.executeUpdateByCodeCode("TF_B_GRP_SCOREDONATE", "UPD_STATUS_BY_BID", inparam);
	    				continue;
        				
        			}else if(IDataUtil.isNotEmpty(donateResult)&&!"0".equals(donateResult.getData(0).getString("X_RESULTCODE"))){
        				
	    				IData inparam = new DataMap();
	    				inparam.put("BATCH_ID", batchId);
	    				inparam.put("SERIAL_NUMBER_OUT",serialNumberOut );
	    				inparam.put("SERIAL_NUMBER_IN",serialNumberIn );
	    				inparam.put("STATUS", "2");
	                    inparam.put("DONATE_RESULT", donateResult.getData(0).getString("X_RESULTINFO"));
	    				Dao.executeUpdateByCodeCode("TF_B_GRP_SCOREDONATE", "UPD_STATUS_BY_BID", inparam);
	    				continue;
	    				
        			}else{
        				
        			insertSms(serialNumberOut,userIdOut,serialNumberIn,donateScore);
        			
			        IData indata = new DataMap();
			        indata.put("BATCH_ID", batchId);
			        indata.put("SERIAL_NUMBER_OUT", serialNumberOut);
			        indata.put("SERIAL_NUMBER_IN", serialNumberIn);
			        indata.put("STATUS", "1");
			        Dao.executeUpdateByCodeCode("TF_B_GRP_SCOREDONATE", "UPD_STATUS_BY_BID_SUC", indata);
        			}
        		}catch (Exception ex){
	    				IData inparam = new DataMap();
	    				inparam.put("BATCH_ID", batchId);
	    				inparam.put("SERIAL_NUMBER_OUT",serialNumberOut );
	    				inparam.put("SERIAL_NUMBER_IN",serialNumberIn );
	    				inparam.put("STATUS", "2");
	                    inparam.put("DONATE_RESULT", ex.getMessage());
	    				Dao.executeUpdateByCodeCode("TF_B_GRP_SCOREDONATE", "UPD_STATUS_BY_BID", inparam);
	    				continue;
        		}
        	}
        }
    }    
    
    public IDataset queryImportData(IData data, Pagination pagination) throws Exception
    {    	
    	return Dao.qryByCodeParser("TF_B_GRP_SCOREDONATE", "SEL_BY_CONDS", data, pagination);
    }
    
    public IDataset queryValidPayRela(String userId, String acctId) throws Exception
    {
    	IData param = new DataMap();
    	param.put("USER_ID", userId);
    	param.put("ACCT_ID", acctId);
    	return Dao.qryByCode("TF_A_PAYRELATION", "SEL_BY_USERID_ACCTID", param);
    }
    
    public void insertSms(String serialNumberOut, String userIdOut, String serialNumberIn, String donateScore) throws Exception
    {
    	String sysDate =SysDateMgr.getSysTime();
    	String sysDateChina = SysDateMgr.decodeTimestamp(sysDate,SysDateMgr.PATTERN_CHINA_TIME);
    	StringBuilder SMSInfo = new StringBuilder();
    	SMSInfo.append("尊敬的客户，您已成功办理积分转赠业务，"+sysDateChina+"向"+serialNumberIn+"号码转赠"+donateScore+"积分。如有疑问请咨询您的集团客户经理。中国移动 ");
    	IData input = new DataMap();
    	String sms_notice_id=SeqMgr.getSmsSendId();
		input.put("SMS_NOTICE_ID",sms_notice_id);
		input.put("PARTITION_ID", sms_notice_id.substring(sms_notice_id.length() - 4));
		input.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		input.put("BRAND_CODE", "");
		input.put("IN_MODE_CODE", getVisit().getInModeCode());
		input.put("CHAN_ID","11");
		input.put("SMS_NET_TAG","0");
		input.put("SEND_COUNT_CODE","1");
		input.put("SEND_TIME_CODE","1");
		input.put("RECV_OBJECT_TYPE","00");
		input.put("RECV_OBJECT",serialNumberOut);
		input.put("RECV_ID",userIdOut);
		input.put("SMS_TYPE_CODE","20");
		input.put("SMS_KIND_CODE","02");
		input.put("NOTICE_CONTENT_TYPE","0");
		input.put("REFERED_COUNT","0");
		input.put("FORCE_REFER_COUNT","1");//NOTICE_CONTENT
		input.put("NOTICE_CONTENT", SMSInfo);
		//input.put("FORCE_OBJECT", forceObject);
		//input.put("FORCE_START_TIME",sysDate);//指定发送次数
		//input.put("FORCE_END_TIME", "");
		input.put("SMS_PRIORITY",1000);//短信优先级
		input.put("REFER_TIME",sysDate);//提交时间
		input.put("REFER_STAFF_ID",getVisit().getStaffId());//提交员工
		input.put("REFER_DEPART_ID",getVisit().getDepartId());//提交部门
		input.put("DEAL_TIME",sysDate);//处理时间
		input.put("DEAL_STAFFID", getVisit().getStaffId());
		input.put("DEAL_DEPARTID", getVisit().getDepartId());
		input.put("DEAL_STATE","15");//处理状态:0－未处理
		input.put("REMARK", "集团统付用户积分转赠");
		input.put("MONTH",Integer.parseInt(sysDate.substring(5, 7)));
		input.put("DAY",Integer.parseInt(sysDate.substring(8, 10)));
		SmsSend.insSms(input);
    }
    
    protected String getSysTagInfo(String tagCode, String key, String defaultValue) throws Exception
    {

        IData param = new DataMap();

        param.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        param.put("TAG_CODE", tagCode);
        param.put("SUBSYS_CODE", "CSM");
        param.put("USE_TAG", 0);

        return TagInfoQry.getSysTagInfo(tagCode, key, defaultValue, CSBizBean.getTradeEparchyCode());


    }
    
}
