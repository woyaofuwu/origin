package com.asiainfo.veris.crm.order.web.person.addgroupmemberbyorder;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
public abstract class AddGroupmemberByOrder extends PersonBasePage {
	

	public abstract void setMember(IData member);
    public abstract void setInfo(IData info);
	public abstract void setCond(IData cond);
	public abstract void setVpmn(IData vpmn);
	public abstract void setSeral(IData seral);
	public abstract void setQryflag(IData qryflag);
	
	public void querySubscriber(IRequestCycle cycle)throws Exception{
		
		IData qryFlag = new DataMap();
		qryFlag.put("QUERY_FLAG", "1");
		setQryflag(qryFlag);//主要用于判断是否查询过
		String alertInfo = "";
		String remark898 = "";
		String groupBook = "";
		String remarkVpmn = "";
		IData param = getData();
		IData inParam = new DataMap();
		IData serialData = new DataMap();
		serialData.put("SERIAL_NUMBER1", param.getString("SERIAL_NUMBER1"));
		this.setSeral(serialData);
		inParam.put("SERIAL_NUMBER1", param.getString("SERIAL_NUMBER1"));
		IData result = CSViewCall.callone(this, "SS.AddGroupmemberByOrderSVC.querySubscriber", inParam);
		if(IDataUtil.isNotEmpty(result)){
			String mobileFlag = result.getString("MOBILE_FLAG");
			if(mobileFlag == null || mobileFlag == ""){
				//移动号码
				alertInfo = "0";
	        	String userId = result.getString("USER_ID");
	        	//判断号码是否属于898集团成员
	        	remark898 = queryGroup(userId);
	        	//判断是否通讯录成员
	        	groupBook = queryMember(userId);
	        	//判断号码是否属于集团V网成员
	        	remarkVpmn = queryGroupVpmn(userId);
	        	IData iData = new DataMap();
	    		iData.put("ALERT_INFO", alertInfo);
	    		iData.put("REMARK898", remark898);
	    		iData.put("GROUPBOOK", groupBook);
	    		iData.put("REMARKVPMN", remarkVpmn);
	    		setAjax(iData);
	    		result.put("MOBILE_FLAG", "1");//移动号码
	    		this.setInfo(result);
			}else{
				//异网号码
				//判断是否通讯录成员
	        	groupBook = queryMember(param.getString("SERIAL_NUMBER1"));//异网号码的userid为手机号码
	        	IData iData = new DataMap();
	        	iData.put("GROUPBOOK", groupBook);
	        	setAjax(iData);
				this.setInfo(result);
			}
		}else{
			alertInfo = "1";
			IData iData = new DataMap();
			iData.put("ALERT_INFO", alertInfo);
			iData.put("REMARK898", remark898);
			iData.put("GROUPBOOK", groupBook);
			iData.put("REMARKVPMN", remarkVpmn);
			setAjax(iData);
			this.setInfo(result);
		}
	}
	/**
	 * 判断号码是否属于898集团成员
	 * @param cycle
	 * @throws Exception
	 */
	public String queryGroup(String userId)throws Exception{
		String remark898 = "";
		IData inParam = new DataMap();
		inParam.put("USER_ID", userId);
		IData result = CSViewCall.callone(this, "SS.AddGroupmemberByOrderSVC.queryGroup", inParam);
		if (IDataUtil.isEmpty(result))
        {
			result = new DataMap();
			remark898 = "2";
        }else{
        	remark898 = "1";
        }
		result.put("REMARK898", remark898);
		this.setCond(result);
		return remark898;
		
	}
	/**
	 * 系统判断号码是否重复加集团通讯录成员
	 * @param userId
	 * @throws Exception
	 */
	public String queryMember(String userId)throws Exception{
		String groupBook = "";
		IData inParam = new DataMap();
		inParam.put("USER_ID", userId);
		
		IData result = CSViewCall.callone(this, "SS.AddGroupmemberByOrderSVC.queryMember", inParam);
		if (IDataUtil.isEmpty(result))
        {
			result = new DataMap();
			groupBook = "2";
        }else{
        	groupBook = "1";
        }
		result.put("GROUPBOOK", groupBook);
		this.setMember(result);
		return groupBook;
		
	}
	/**
	 * 判断号码是否属于集团V网成员
	 * @param userId
	 * @throws Exception
	 */
	public String queryGroupVpmn(String userId)throws Exception{
		String remarkVpmn = "";
		IData inParam = new DataMap();
		inParam.put("USER_ID", userId);
		IData result = CSViewCall.callone(this, "SS.AddGroupmemberByOrderSVC.queryGroupVpmn", inParam);
		
		if (IDataUtil.isEmpty(result))
        {
			result = new DataMap();
			remarkVpmn = "2";
        }else{
        	remarkVpmn = "1";
        }
		result.put("REMARKVPMN", remarkVpmn);
		this.setVpmn(result);
		return remarkVpmn;
		
	}
	/**
	 * 批量导入通讯录数据数据
	 * @param userId
	 * @throws Exception
	 */
	public void batImportBook(IRequestCycle cycle)throws Exception{
		
		IData data = getData();
        String fileId = data.getString("params");
        ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
        IData fileData = ImpExpUtil.beginImport(null, fileId, ExcelConfig.getSheets("import/bat/group/BOOKMEMBER.xml"));
        int rightCount = fileData.getInt("rightCount", 0);
        int errorCount = fileData.getInt("errorCount", 0);

        if (IDataUtil.isEmpty(fileData)) {
            CSViewException.apperr(BatException.CRM_BAT_89);
        }
        if (rightCount + errorCount == 0) {
            CSViewException.apperr(BatException.CRM_BAT_86);
        }
        if (rightCount > 0) {
            data.put("FILEDATA", fileData);
            IData result = CSViewCall.callone(this, "SS.AddGroupmemberByOrderSVC.bookMemberImportOrder", data);
            String rtnResult = result.getString("SUCCESS");
    		this.setAjax("SUCCESS", rtnResult);// 传给页面提示
        } else {
            CSViewException.apperr(BatException.CRM_BAT_6);
        }
        
    }
		
		

	/**
	 * 提交录入数据
	 * @param userId
	 * @throws Exception
	 */
	public void submitCheck(IRequestCycle cycle)throws Exception{
		IData iparam = getData();
		//判断是否异网号码，异网号码录入数据封装，IS_MOBILE=2为异网
		String noPhone = iparam.getString("NO_PHONE");//判断根据客户姓名还是手机号码查
		if("1".equals(noPhone)){//根据客户姓名录入
			IData param = new DataMap();
			param.put("SERIAL_NUMBER", iparam.getString("SERIAL_NUMBER1"));
			param.put("CUST_ID", iparam.getString("GROUP_CUSTID"));//集团的custid
			param.put("PARTITION_ID", "1111");
			param.put("USER_ID", "1111");
			param.put("GROUP_ID", iparam.getString("GROUP_CUST_ID"));
			param.put("MEMBER_CUST_ID", "1111");//用户的custid
			param.put("USECUST_ID", "1111");
			param.put("CUST_NAME", iparam.getString("CUST_NAME"));//开户客户
			param.put("USECUST_NAME", iparam.getString("USECUST_NAME"));//使用客户
			param.put("EPARCHY_CODE", iparam.getString("EPARCHY_CODE"));
    		param.put("CITY_CODE", iparam.getString("CITY_CODE"));
    		param.put("JOIN_DATE", SysDateMgr.getSysTime());
    		param.put("REMOVE_TAG", "0");
    		param.put("MEMBER_KIND", iparam.getString("MEMBER_KIND"));
    		param.put("IS_CONTACT", iparam.getString("IS_CONTACT"));
    		param.put("GROUP_CUST_NAME", iparam.getString("GROUP_CUST_NAME"));
    		param.put("USEPSPT_TYPE_CODE", iparam.getString("USEPSPT_TYPE_CODE"));
    		param.put("USEPSPT_ID", iparam.getString("USEPSPT_ID"));
    		param.put("USEPSPT_ADDR", iparam.getString("USEPSPT_ADDR"));
    		param.put("USEPSPT_END_DATE", iparam.getString("USEPSPT_END_DATE"));
    		param.put("USEPOST_ADDR", iparam.getString("USEPOST_ADDR"));
    		param.put("USEPHONE", iparam.getString("USEPHONE"));
    		param.put("SVC_MODE_CODE", iparam.getString("SVC_MODE_CODE"));
    		param.put("DEPART", iparam.getString("DEPART"));
    		param.put("DUTY", iparam.getString("DUTY"));
    		param.put("LIKE_MOBILE_BUSI", iparam.getString("LIKE_MOBILE_BUSI"));
    		param.put("LIKE_DISCNT_TYPE", iparam.getString("LIKE_DISCNT_TYPE"));
    		param.put("LIKE_ACT", iparam.getString("LIKE_ACT"));
    		param.put("REMARK", iparam.getString("REMARK"));
    		param.put("RSRV_TAG5", "1");
    		
    		IData result = CSViewCall.callone(this, "SS.AddGroupmemberByOrderSVC.submitCheck", param);
    		String rtnResult = result.getString("SUCCESS");
    		this.setAjax("SUCCESS", rtnResult);// 传给页面提示
    		
		}else{
			String isMobile = iparam.getString("IS_MOBILE");
			if("2".equals(isMobile)){
				String isRemove = iparam.getString("IS_REMOVE");
				if("1".equals(isRemove)){
					//更新状态
					String userId = iparam.getString("SERIAL_NUMBER1");//异网userid为手机号码
					IData inparams = new DataMap();
		    		inparams.put("USER_ID", userId);
		    		CSViewCall.call(this, "SS.ReqGroupmemberByOrderSVC.submitCancel", inparams);
				}
				//异网号码不做信息查询判断直接录入
				IData param = new DataMap();
				param.put("IS_MOBILE", iparam.getString("IS_MOBILE"));
				param.put("PARTITION_ID", Long.parseLong(iparam.getString("SERIAL_NUMBER1"))%10000);
	    		param.put("USER_ID", iparam.getString("SERIAL_NUMBER1"));
	    		param.put("CUST_ID", iparam.getString("GROUP_CUSTID"));//集团的custid
	    		param.put("GROUP_ID", iparam.getString("GROUP_CUST_ID"));
	    		param.put("MEMBER_CUST_ID", iparam.getString("SERIAL_NUMBER1"));//用户的custid
	    		param.put("USECUST_ID", iparam.getString("SERIAL_NUMBER1"));
	    		param.put("SERIAL_NUMBER", iparam.getString("SERIAL_NUMBER1"));
	    		param.put("CUST_NAME", iparam.getString("CUST_NAME"));//开户客户
	    		param.put("USECUST_NAME", iparam.getString("USECUST_NAME"));//使用客户
	    		param.put("EPARCHY_CODE", iparam.getString("EPARCHY_CODE"));
	    		param.put("CITY_CODE", iparam.getString("CITY_CODE"));
	    		param.put("JOIN_DATE", SysDateMgr.getSysTime());
	    		param.put("REMOVE_TAG", "0");
	    		param.put("MEMBER_KIND", iparam.getString("MEMBER_KIND"));
	    		param.put("IS_CONTACT", iparam.getString("IS_CONTACT"));
	    		
	    		param.put("GROUP_CUST_NAME", iparam.getString("GROUP_CUST_NAME"));
	    		param.put("USEPSPT_TYPE_CODE", iparam.getString("USEPSPT_TYPE_CODE"));
	    		param.put("USEPSPT_ID", iparam.getString("USEPSPT_ID"));
	    		param.put("USEPSPT_ADDR", iparam.getString("USEPSPT_ADDR"));
	    		param.put("USEPSPT_END_DATE", iparam.getString("USEPSPT_END_DATE"));
	    		param.put("USEPOST_ADDR", iparam.getString("USEPOST_ADDR"));
	    		param.put("USEPHONE", iparam.getString("USEPHONE"));
	    		param.put("SVC_MODE_CODE", iparam.getString("SVC_MODE_CODE"));
	    		param.put("DEPART", iparam.getString("DEPART"));
	    		param.put("DUTY", iparam.getString("DUTY"));
	    		
	    		param.put("RSRV_TAG1", iparam.getString("GROUP_898"));
	    		param.put("RSRV_STR1", iparam.getString("GROUNP_NAME"));
	    		param.put("RSRV_TAG4", iparam.getString("GROUNP_V_MEMBER"));
	    		param.put("RSRV_STR4", iparam.getString("VPN_NAME"));
	    		param.put("RSRV_TAG5", "1");
	    		param.put("OLD_GROUP_ID", iparam.getString("GROUP_ID"));
	    		param.put("OLD_GROUP_CUST_NAME", iparam.getString("GROUP_CUST_NAME1"));
	    		param.put("RSRV_TAG2", iparam.getString("IS_REMOVE"));
	    		param.put("LIKE_MOBILE_BUSI", iparam.getString("LIKE_MOBILE_BUSI"));
	    		param.put("LIKE_DISCNT_TYPE", iparam.getString("LIKE_DISCNT_TYPE"));
	    		param.put("LIKE_ACT", iparam.getString("LIKE_ACT"));
	    		param.put("REMARK", iparam.getString("REMARK"));
	    		param.put("CUST_MANAGER_ID", iparam.getString("CUST_MANAGER_ID"));
	    		String fileId = iparam.getString("simpleUpload");
	    		param.put("RSRV_STR3", fileId);
	    		IData result = CSViewCall.callone(this, "SS.AddGroupmemberByOrderSVC.submitCheck", param);
	    		String rtnResult = result.getString("SUCCESS");
	    		this.setAjax("SUCCESS", rtnResult);// 传给页面提示
			}else{//移动号码
				//判断是否迁移，如果是迁移IS_REMOVE=1,原来用户tf_f_addressbook_member的remove_tag改为1
				//并新增一条记录tf_f_addressbook_member,记录原来的group_id group_cust_name 到
				//old_group_id old_group_cust_name
				String isRemove = iparam.getString("IS_REMOVE");
				if("1".equals(isRemove)){
					//更新状态
					String userId = iparam.getString("USER_ID");
					IData inparams = new DataMap();
		    		inparams.put("USER_ID", userId);
		    		CSViewCall.call(this, "SS.ReqGroupmemberByOrderSVC.submitCancel", inparams);
				}
	    		//新增记录
	    		String fileId = iparam.getString("simpleUpload");
	    		IData param = new DataMap();
	    		String user_id = iparam.getString("USER_ID");
	    		param.put("IS_MOBILE", iparam.getString("IS_MOBILE"));
	    		param.put("PARTITION_ID", Long.parseLong(user_id)%10000);
	    		param.put("USER_ID", user_id);
	    		param.put("CUST_ID", iparam.getString("GROUP_CUSTID"));//集团的custid
	    		param.put("GROUP_ID", iparam.getString("GROUP_CUST_ID"));
	    		param.put("MEMBER_CUST_ID", iparam.getString("CUST_ID"));//用户的custid
	    		param.put("USECUST_ID", iparam.getString("CUST_ID"));
	    		param.put("SERIAL_NUMBER", iparam.getString("SERIAL_NUMBER1"));
	    		param.put("CUST_NAME", iparam.getString("CUST_NAME"));
	    		param.put("USECUST_NAME", iparam.getString("CUST_NAME"));
	    		param.put("EPARCHY_CODE", iparam.getString("EPARCHY_CODE"));
	    		param.put("CITY_CODE", iparam.getString("CITY_CODE"));
	    		param.put("JOIN_DATE", SysDateMgr.getSysTime());
	    		param.put("REMOVE_TAG", "0");
	    		param.put("MEMBER_KIND", iparam.getString("MEMBER_KIND"));
	    		param.put("IS_CONTACT", iparam.getString("IS_CONTACT"));
	    		
	    		param.put("GROUP_CUST_NAME", iparam.getString("GROUP_CUST_NAME"));
	    		param.put("USEPSPT_TYPE_CODE", iparam.getString("USEPSPT_TYPE_CODE"));
	    		param.put("USEPSPT_ID", iparam.getString("USEPSPT_ID"));
	    		param.put("USEPSPT_ADDR", iparam.getString("USEPSPT_ADDR"));
	    		param.put("USEPSPT_END_DATE", iparam.getString("USEPSPT_END_DATE"));
	    		param.put("USEPOST_ADDR", iparam.getString("USEPOST_ADDR"));
	    		param.put("USEPHONE", iparam.getString("USEPHONE"));
	    		param.put("SVC_MODE_CODE", iparam.getString("SVC_MODE_CODE"));
	    		param.put("DEPART", iparam.getString("DEPART"));
	    		param.put("DUTY", iparam.getString("DUTY"));
	    		param.put("RSRV_TAG1", iparam.getString("GROUP_898"));
	    		param.put("RSRV_STR1", iparam.getString("GROUNP_NAME"));
	    		param.put("RSRV_TAG4", iparam.getString("GROUNP_V_MEMBER"));
	    		param.put("RSRV_STR4", iparam.getString("VPN_NAME"));
	    		param.put("RSRV_TAG5", "1");
	    		param.put("OLD_GROUP_ID", iparam.getString("GROUP_ID"));
	    		param.put("OLD_GROUP_CUST_NAME", iparam.getString("GROUP_CUST_NAME1"));
	    		param.put("RSRV_TAG2", iparam.getString("IS_REMOVE"));
	    		param.put("LIKE_MOBILE_BUSI", iparam.getString("LIKE_MOBILE_BUSI"));
	    		param.put("LIKE_DISCNT_TYPE", iparam.getString("LIKE_DISCNT_TYPE"));
	    		param.put("LIKE_ACT", iparam.getString("LIKE_ACT"));
	    		param.put("REMARK", iparam.getString("REMARK"));
	    		param.put("CUST_MANAGER_ID", iparam.getString("CUST_MANAGER_ID"));
	    		param.put("RSRV_STR3", fileId);
	    		IData result = CSViewCall.callone(this, "SS.AddGroupmemberByOrderSVC.submitCheck", param);
	    		String rtnResult = result.getString("SUCCESS");
	    		this.setAjax("SUCCESS", rtnResult);// 传给页面提示
				
				
			}
		}
		
		
	}
	
}
