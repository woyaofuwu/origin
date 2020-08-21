package com.asiainfo.veris.crm.order.web.person.smsboomprotection;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class SmsBoomProtection extends PersonBasePage
{
	public abstract void setCond(IData cond);

    public abstract void setInfos(IDataset infos);
    
    public abstract void setInfo(IData info);

    public abstract void setResult(long result);
    
    public abstract void setRowIndex(long rowIndex);
    
    
    
    /**
     * 业务
     * 
     * @param cycle
     * @throws Exception
     */
    public void transaction(IRequestCycle cycle) throws Exception
    {
    	 IData data = getData();
         String type = data.getString("SMSBOMB_BUSINESS_TYPE","");
         
         if("1".equals(type)){
        	 transactionInfo(cycle);
         
         }else if("2".equals(type)){
        	 cancelInfo(cycle);

         }else if("3".equals(type)){
        	 modificationInfo(cycle);
         }

    }

    /**
     * 查询被保护名单
     * 
     * @param cycle
     * @throws Exception
     */
	public void queryInfo(IRequestCycle cycle) throws Exception
    {
        IData condParams = getData("cond", true);
        IDataset infos = null;
        String accessNum = condParams.getString("ACCESS_NO", ""); //受理号码
        String serialNum = condParams.getString("SERIAL_NO", ""); // 被保护号码
        
        IData queryConditions = new DataMap();
        
        queryConditions.put("SERIAL_NO",accessNum);
        queryConditions.put("ACCESS_NO",serialNum);

        if("".equals(serialNum)){//按受理号码查
           infos = CSViewCall.call(this, "SS.SmsBoomProtectionSVC.qryProtectinfoBySerialNum", queryConditions, getPagination("NavBarPart"));
        }else if("".equals(accessNum)){
           infos = CSViewCall.call(this, "SS.SmsBoomProtectionSVC.qryProtectinfoByAccessNum", queryConditions,getPagination("NavBarPart"));
        }else{
           infos = CSViewCall.call(this, "SS.SmsBoomProtectionSVC.qryProtectinfo", queryConditions,getPagination("NavBarPart"));
        }
	       
       if(IDataUtil.isNotEmpty(infos)){
    	   infos  = assemblyFormatTime(infos);
       }
       
	   setAjax(infos);
	   setResult(infos.size());
	   setInfos(infos);
    }
	
	 /**
     * 删除业务
     * 
     * @param cycle
     * @throws Exception
     */
    public void cancelInfo(IRequestCycle cycle) throws Exception
    {
    	 IData data = getData();
	     IDataset infos = null;
		 IData transactionData = new DataMap();
		 String accessNum = data.getString("ACCESS_NO", ""); //受理号码
	     String serialNum = data.getString("SERIAL_NO", ""); // 被保护号码
         transactionData.put("SERIAL_NO",accessNum);
		 transactionData.put("ACCESS_NO",serialNum);

	     infos = CSViewCall.call(this, "SS.SmsBoomProtectionSVC.qryProtectinfo", transactionData);
         if(IDataUtil.isEmpty(infos)){
        	 CSViewException.apperr(CrmCommException.CRM_COMM_103, "操作失败，该用户不存在！");
        }
	   
         IData result = infos.getData(0);
		 transactionData.put("CHANNEL_ID", result.getString("CHANNEL_ID"));// 渠道编码
         transactionData.put("PROV_ID", result.getString("PROV_ID"));//被保护省ID
		 transactionData.put("ACCEPT_TIME", result.getString("ACCEPT_TIME"));//受理时间
		 transactionData.put("EXPIRE_DATE",result.getString("EXPIRE_DATE"));//截止时间
		 transactionData.put("UPDATE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());// 更新时间
		 transactionData.put("CREATE_STAFF_ID", result.getString("CREATE_STAFF_ID"));// 受理员工工号
		 transactionData.put("SMSBOMB_BUSINESS_TYPE",data.getString("SMSBOMB_BUSINESS_TYPE"));// 更新方式

         infos = CSViewCall.call(this, "SS.SmsBoomProtectionSVC.delProtectInfo", transactionData);

    }
    
    /**
     * 修改用户信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void modificationInfo(IRequestCycle cycle) throws Exception
    {
   	 	 IData data = getData();
	     IDataset infos = null;
		 IData transactionData = new DataMap();
		 
		 String accessNum = data.getString("ACCESS_NO", ""); //受理号码
	     String serialNum = data.getString("SERIAL_NO", ""); // 被保护号码
		 transactionData.put("SERIAL_NO",accessNum);
		 transactionData.put("ACCESS_NO", serialNum);
		 String expireDate = data.getString("EXPIRE_DATE")+"235959";
		 
		 String oneyearlatedate = SysDateMgr.addYears(SysDateMgr.getSysDate("yyyy-MM-dd"), 1);
		 String oneyearlatetime = DateFormatUtils.format(SysDateMgr.string2Date(oneyearlatedate, "yyyy-MM-dd"), "yyyyMMddHHmmss");
		 int oneyearlatetag = expireDate.compareTo(oneyearlatetime);
		 String nowdate = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
		 int tag = expireDate.compareTo(nowdate);
		 if(tag<0){//判断时间是否大于今天
			CSViewException.apperr(CrmCommException.CRM_COMM_103, "生效截止时间不能小于当前时间！");
  		}
  		if(oneyearlatetag>0){//时间小于一年
  			CSViewException.apperr(CrmCommException.CRM_COMM_103, "生效截止时间不能超过当前时间一年！");
  		}
  			
	     infos = CSViewCall.call(this, "SS.SmsBoomProtectionSVC.qryProtectinfo", transactionData);
         if(IDataUtil.isEmpty(infos)){
        	 CSViewException.apperr(CrmCommException.CRM_COMM_103, "操作失败，该用户不存在！");
         }
         IData result = infos.getData(0);
         transactionData.put("EXPIRE_DATE",expireDate);//有效期
		 transactionData.put("CHANNEL_ID", result.getString("CHANNEL_ID"));// 渠道编码
         transactionData.put("PROV_ID", result.getString("PROV_ID"));//被保护省ID
		 transactionData.put("ACCEPT_TIME", result.getString("ACCEPT_TIME"));//受理时间
		 transactionData.put("UPDATE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());// 更新时间
		 transactionData.put("CREATE_STAFF_ID", result.getString("CREATE_STAFF_ID"));// 受理员工工号
		 transactionData.put("SMSBOMB_BUSINESS_TYPE",data.getString("SMSBOMB_BUSINESS_TYPE"));// 更新方式

         infos = CSViewCall.call(this, "SS.SmsBoomProtectionSVC.updateProtectInfo", transactionData);

    }
    
	
    /**
     * 办理业务
     * 
     * @param cycle
     * @throws Exception
     */
    public void transactionInfo(IRequestCycle cycle) throws Exception
    { 
    	IData data = getData();
    	IDataset infos = null;
 		IData transactionData = new DataMap();
    	String serialNum = data.getString("SERIAL_NO");//被保护号码
    	String accessNum = data.getString("ACCESS_NO");//受理号码
		String expireDate = data.getString("EXPIRE_DATE","");
		String oneyearlatedate = SysDateMgr.addYears(SysDateMgr.getSysDate("yyyy-MM-dd"), 1);
		String oneyearlatetime = DateFormatUtils.format(SysDateMgr.string2Date(oneyearlatedate, "yyyy-MM-dd"), "yyyyMMdd");
		int oneyearlatetag = expireDate.compareTo(oneyearlatetime);
		String nowdate = SysDateMgr.getSysDateYYYYMMDD();
		int tag = expireDate.compareTo(nowdate);
	    if(expireDate==""){
	    	 expireDate=SysDateMgr.getSysDateYYYYMMDD(); //截止时间默认一天
	     }else if(tag<0){
				CSViewException.apperr(CrmCommException.CRM_COMM_103, "生效截止时间不能小于当前时间！");
	     }else if(oneyearlatetag>0){//时间小于一年
	  			CSViewException.apperr(CrmCommException.CRM_COMM_103, "生效截止时间不能超过当前时间一年！");
	  	}
	  
	    
    	transactionData.put("EXPIRE_DATE",expireDate+"235959");//生效截止时间
    	
		 transactionData.put("SERIAL_NO",accessNum);
		 transactionData.put("ACCESS_NO", serialNum);

		 infos = CSViewCall.call(this, "SS.SmsBoomProtectionSVC.qryProtectinfoByAccessNum", transactionData);
	     if(IDataUtil.isNotEmpty(infos)){
	    	 CSViewException.apperr(CrmCommException.CRM_COMM_103, "开通失败，该用户已存在！");
		 }
		 transactionData.put("CREATE_STAFF_ID", getVisit().getStaffId());//受理员工工号
		 transactionData.put("ACCEPT_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS());//受理时间
		 transactionData.put("STATUS","0");//名单状态
		 transactionData.put("UPDATE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());// 更新时间
		 transactionData.put("SMSBOMB_BUSINESS_TYPE",data.getString("SMSBOMB_BUSINESS_TYPE"));// 更新方式
		 String channelCode = "";
		 if(getVisit().getInModeCode().equals("1")){//接触渠道编码
			 channelCode = "01";; // 01-客服人工
		 }else if(getVisit().getInModeCode().equals("0")){
			 channelCode = "08";//08- 营业厅
		 }
		transactionData.put("CHANNEL_ID", channelCode);//接触渠道编码.
	    
	 	infos = CSViewCall.call(this, "SS.SmsBoomProtectionSVC.insertdateProtectInfo", transactionData);
       
	 	setAjax(infos);
    }
    


    

    private IDataset assemblyFormatTime(IDataset infos){
    	for(int i=0; i<infos.size(); i++){
        	String expireTime = formatTime(infos.getData(i).getString("EXPIRE_DATE"));
        	String acceptTime = formatTime(infos.getData(i).getString("ACCEPT_TIME"));
        	infos.getData(i).put("EXPIRE_DATE", expireTime);
        	infos.getData(i).put("ACCEPT_TIME", acceptTime);
    	}
    	return infos;
    }
 

      

    /**
     * 把yyyyMMddHHmmss格式日期转换为yyyy-MM-dd HH:mm:ss
     * @return
     */
    private String formatTime(String time){
    	if(time!= null && time.length()==14){
    		time = time.substring(0, 4)+"-"+time.substring(4, 6)
    				+"-"+time.substring(6, 8)+" "+time.substring(8, 10)+":"
        			+time.substring(10, 12)+":"+time.substring(12, 14);
    	}
    	return time;
    }
}
