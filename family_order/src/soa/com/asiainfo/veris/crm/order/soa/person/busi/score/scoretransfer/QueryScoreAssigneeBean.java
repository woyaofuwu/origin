package com.asiainfo.veris.crm.order.soa.person.busi.score.scoretransfer;



import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.SvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class QueryScoreAssigneeBean extends CSBizBean
{
	  public IDataset queryAssigneeInfo(IData param) throws Exception
	    {

	        String sn = param.getString("L_MOBILE");
	        IData input=new DataMap();
	        input.put("ROUTETYPE", "00");
	        input.put("ROUTEVALUE","000");
	        input.put("KIND_ID", "BIP5A049_T5000049_0_0");
	        input.put("L_MOBILE", sn);
	        input.put("ASSIGNEE_STATUS", "00");
		        // 获取客户可用积分余额
	    /*   IData Assigneeset = busiCallIBOSS(input);
	        IData AssigneeInfo=new DataMap();
	        if (IDataUtil.isNotEmpty(Assigneeset))
	        {
	            if("00".equals(Assigneeset.getString("TradeResCode"))){
	            	AssigneeInfo=Assigneeset.getData("AssigneeList");
	            }
	        }*/
	        IData AssigneeInfo=new DataMap();
	       AssigneeInfo.put( "B_MOBILE","13723803897");
	    	AssigneeInfo.put("ASSIGNEE_NAME","张三");
	    	AssigneeInfo.put("VALIDNUM_TYPE","01");
	    	 AssigneeInfo.put("ASSIGNEE_ID","432501198808282829");
	    	AssigneeInfo.put("EFFECTIVE_DATE",SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	    	 AssigneeInfo.put("OPERATOR_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	    	AssigneeInfo.put("ORG_ID","0");
	    	 AssigneeInfo.put("ASSIGNEE_STATUS","01");
	    	 AssigneeInfo.put("USER_ROLE","usersuper");
	    	 AssigneeInfo.put("USER_ID","dsfdsa");

	        
	    	 IDataset dataset = busiCallIBOSS(input);
		        return dataset;
	    }

	  
	  
	  public IDataset ModifyAssigneeInfo(IData param) throws Exception
	    {
	        String opttype=param.getString("OPT_TYPE");
	        IData input=new DataMap();
	        input.put("ROUTETYPE", "00");
	        input.put("ROUTEVALUE", "000");
	        input.put("KIND_ID", "BIP5A048_T5000048_0_0");
	        input.put("OPT_TYPE", param.getString("OPT_TYPE"));
	        input.put("USER_ROLE", "03");
	        input.put("USER_ID", getVisit().getStaffId());
	        input.put("OPER_ATERTIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	        input.put("L_MOBILE", param.getString("L_MOBILE"));
	        input.put("ORG_ID", "0003");
	        IDataset orderRec = new DatasetList();
	        IData modifyInfo=new DataMap();
	        if(opttype.equals("01")){
	        	modifyInfo.put("B_MOBILE",  param.getString("B_MOBILE"));
	        	modifyInfo.put("ASSIGNEE_NAME",  param.getString("ASSIGNEE_NAME"));
	        	modifyInfo.put("VALIDNUM_TYPE",  param.getString("VALIDNUM_TYPE"));
	        	modifyInfo.put("ASSIGNEE_ID",  param.getString("ASSIGNEE_ID"));
	        	orderRec.add(modifyInfo);
	        	input.put("ADD_INFO", orderRec);
	        }else if(opttype.equals("02")){
	        	modifyInfo.put("B_MOBILE1",  param.getString("B_MOBILE"));
	        	modifyInfo.put("B_MOBILE2",  param.getString("B_MOBILE2"));
	        	modifyInfo.put("ASSIGNEE_NAME",  param.getString("ASSIGNEE_NAME"));
	        	modifyInfo.put("VALIDNUM_TYPE",  param.getString("VALIDNUM_TYPE"));
	        	modifyInfo.put("ASSIGNEE_ID",  param.getString("ASSIGNEE_ID"));
	        	orderRec.add(modifyInfo);
	        	input.put("REPLACE_INFO",  orderRec);
	        }else if(opttype.equals("03")){
	        	modifyInfo.put("B_MOBILE",  param.getString("B_MOBILE"));
	        	modifyInfo.put("ASSIGNEE_NAME",  param.getString("ASSIGNEE_NAME"));
	        	modifyInfo.put("VALIDNUM_TYPE",  param.getString("VALIDNUM_TYPE"));
	        	modifyInfo.put("ASSIGNEE_ID",  param.getString("ASSIGNEE_ID"));
	        	orderRec.add(modifyInfo);
	        	input.put("MODIFICATION_INFO",  orderRec);
	        }else if(opttype.equals("04")){
	        	modifyInfo.put("B_MOBILE",  param.getString("B_MOBILE"));
	        	orderRec.add(modifyInfo);
	        	input.put("DISCARD_INFO",  orderRec);
	        }else if(opttype.equals("05")){
	        	modifyInfo.put("B_MOBILE",  param.getString("B_MOBILE"));
	        	orderRec.add(modifyInfo);
	        	input.put("REUSE_INFO",  orderRec);
	        }
	      
	     
	        IDataset dataset = busiCallIBOSS(input);
	        return dataset;
	    }
	  
	  
	  public IDataset busiCallIBOSS(IData input) throws Exception
	    {
	        IDataset interfaceData = IBossCall.invokeIBossInterface(input);  
	        	        
	        return interfaceData;
	    }
	  
	 
}