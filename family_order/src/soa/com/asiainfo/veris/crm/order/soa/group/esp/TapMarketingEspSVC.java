package com.asiainfo.veris.crm.order.soa.group.esp;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.ailk.bizservice.base.CSAppCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformOtherBean;
import com.asiainfo.veris.crm.order.soa.group.esop.tapmarketing.TapMarketingBean;

public class TapMarketingEspSVC extends CSBizService{

	private static final long serialVersionUID = 1L;
	
	public IDataset addTapMarketing(IData map) throws Exception
    {
//		System.out.print("fufn-TapMarketingEspSVC map:"+map);

		IDataset result = new DatasetList();
		String ibsysId = map.getString("IBSYSID");
		IData commonData= new DataMap(); 
		IDataset pattrList = new DatasetList();
		getEopAttrToList(ibsysId,pattrList,commonData);
		Date date= new Date();
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String update_time =simpleDateFormat.format(date);

		
		
		
		if(IDataUtil.isNotEmpty(pattrList)){
			for(int i = 0; i < pattrList.size(); i++){
				String subIbsysId = SeqMgr.getSubIbsysId();
				IData pattr= pattrList.getData(i); 
				IData idataNew=new DataMap();
				if(commonData.getString("C_TAPMARKETING_OPERATION", "").equals("0")){
					idataNew.put("IBSYSID",subIbsysId);
					idataNew.put("IBSYSID_TAPMARKETING",ibsysId);
					idataNew.put("PROVINCEA",commonData.getString("C_PROVINCEA", ""));
					idataNew.put("CITY_ID",commonData.getString("C_RESPONSIBILITY_CITYCODE", ""));
					idataNew.put("CITYA",commonData.getString("C_CITYA", ""));
					idataNew.put("FRIENDBUSINESS_NAME",pattr.getString("NOTIN_FRIENDBUSINESS_NAME", ""));
					idataNew.put("CUST_NAME",pattr.getString("NOTIN_CUST_NAME", ""));
					idataNew.put("RESPONSIBILITY_ID",commonData.getString("C_RESPONSIBILITY_ID", ""));
					idataNew.put("RESPONSIBILITY_NAME",commonData.getString("C_RESPONSIBILITY_NAME", ""));
					idataNew.put("RESPONSIBILITY_PHONE",commonData.getString("C_RESPONSIBILITY_PHONE", ""));
					idataNew.put("LINENAME",pattr.getString("NOTIN_LINENAME", ""));
//					idataNew.put("LINETYPE","7012");
					idataNew.put("LINETYPE",commonData.getString("C_LINETYPE", "")); 
					idataNew.put("BANDWIDTH",pattr.getString("NOTIN_BANDWIDTH", ""));
					idataNew.put("MONTHLYFEE_TAP",pattr.getString("NOTIN_RSRV_STR2", ""));
					idataNew.put("LINEPRICE_TAP",pattr.getString("NOTIN_LINEPRICE_TAP", ""));
					idataNew.put("RESULT_CODE","0");
					idataNew.put("ACCEPT_DATE", update_time);
					idataNew.put("UPDATE_DATE", update_time);
					boolean code= TapMarketingBean.insertWorkformTapMarketing(idataNew);
					if(!code){
						CSAppException.apperr(CrmCommException.CRM_COMM_103,"更新TF_B_WORKFORM_TAPMARKETING表失败，系统异常!");
				 	}
				}else {
					 idataNew.put("IBSYSID",commonData.getString("C_TAPMARKETING_SELECT",""));
			    	  String[] names=new String [15];
			    	  String[] values=new String [15];
			    	  if(commonData.getString("C_TAPMARKETING_OPERATION","").equals("2")){//修改
							names[0]="RESULT_CODE";
							values[0]="0";//录入状态
						}else{
							names[0]="RESULT_CODE";
							values[0]="1";//取消状态
						}
			    	  	names[1]="LINEPRICE_TAP";
					 	values[1]=pattr.getString("NOTIN_LINEPRICE_TAP", "");
					 	names[2]="IBSYSID_TAPMARKETING";
					 	values[2]=ibsysId;
					 	names[3]="PROVINCEA";
					 	values[3]=commonData.getString("C_PROVINCEA", "");
					 	names[4]="CITYA";
					 	values[4]=commonData.getString("C_CITYA", "");
					 	names[5]="FRIENDBUSINESS_NAME" ;
					 	values[5]=pattr.getString("NOTIN_FRIENDBUSINESS_NAME", "");
					 	names[6]="CUST_NAME" ;
					 	values[6]=pattr.getString("NOTIN_CUST_NAME", "");
					 	names[7]="RESPONSIBILITY_NAME" ;
					 	values[7]=commonData.getString("C_RESPONSIBILITY_NAME", "");
					 	names[8]="RESPONSIBILITY_PHONE" ;
					 	values[8]=commonData.getString("C_RESPONSIBILITY_PHONE", "");
					 	names[9]="LINENAME";
					 	values[9]=pattr.getString("NOTIN_LINENAME", "");
					 	names[10]="LINETYPE";
					 	values[10]=commonData.getString("C_LINETYPE", "");
					 	names[11]="BANDWIDTH";
					 	values[11]=pattr.getString("NOTIN_BANDWIDTH", "");
					 	names[12]="MONTHLYFEE_TAP";
					 	values[12]=pattr.getString("NOTIN_RSRV_STR2", "");
					 	names[13]="RESPONSIBILITY_ID";
					 	values[13]=commonData.getString("C_RESPONSIBILITY_ID", "");
					 	names[14]="CITY_ID";
					 	values[14]=commonData.getString("C_RESPONSIBILITY_CITYCODE", "");
					 	
					 	//注意修改数组长度，防止数据越界
//						System.out.print("fufn-TapMarketingEspSVC names:"+names+" values:"+values+" idataNew:"+idataNew);
					 	int code=TapMarketingBean.updateTapMarketingInfo(names,values,idataNew); 
//						System.out.print("fufn-TapMarketingEspSVC code:"+code);
					 	if(code!=1){
				        	 CSAppException.apperr(CrmCommException.CRM_COMM_103,"更新TF_B_WORKFORM_TAPMARKETING表失败，系统异常!");
					 	}
				}
				
				
			}
		}
		
		return result;
    }
	
	public IDataset updateMarketing(IData map) throws Exception
    {
//		System.out.print("fufn-TapMarketingEspSVC map:"+map);

		IDataset result = new DatasetList();
		String ibsysId = map.getString("IBSYSID");
		String nodeId = map.getString("NODE_ID");
    	if(nodeId!=null && (nodeId.equals("appvoeI")||nodeId.equals("appvoeII"))){
    		boolean flag=false;
    		String auditText="";
        	IDataset otherList = WorkformOtherBean.qryByIbsysidNodeId(ibsysId,nodeId);
        	if(IDataUtil.isNotEmpty(otherList)){
        		for (int i=0,size=otherList.size();i<size;i++){
        			IData otherData=otherList.getData(i);
        			if(otherData.getString("ATTR_CODE","").equals("AUDIT_RESULT")){
        				if(otherData.getString("ATTR_VALUE","").equals("1")){//通过
        					flag=true;
        				}
        			}
        			if(otherData.getString("ATTR_CODE","").equals("AUDIT_TEXT")){
        				auditText=otherData.getString("ATTR_VALUE","");
        			}
        		}
        	}
    		updateMarketingInfo(ibsysId,nodeId,true,auditText);
    	}else if(nodeId!=null && (nodeId.equals("apply")||nodeId.equals("applyConfirm"))){
    		updateMarketingInfo(ibsysId,nodeId,false,"");
    	}


		
		return result;

		
    }
	
	private void updateMarketingInfo (String ibsysIdEx,String nodeId,boolean flag,String auditText) throws Exception{
		Date date= new Date();
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String update_time =simpleDateFormat.format(date);
		IData commonData= new DataMap(); 
		IDataset pattrList = new DatasetList();
		getEopAttrToList(ibsysIdEx,pattrList,commonData);
		String ibsysId=commonData.getString("C_TAPMARKETING_SELECT", "");
		if(IDataUtil.isNotEmpty(pattrList)){
			for(int i = 0; i < pattrList.size(); i++){
				IData pattr= pattrList.getData(i); 
				String[] names=new String [15];
		  	  	String[] values=new String [15];
		  	  	if("apply".equals(nodeId)){//
			 		names=new String [10];
			    	values=new String [10];
					names[0]="RESULT_CODE";
					values[0]="2";//发起激励
					names[1]="IBSYSID_EXCITATION";
				 	values[1]=ibsysIdEx;
				 	names[2]="CUST_ID" ;
				 	values[2]=commonData.getString("CUSTOMNO", "");
				 	names[3]="CUST_NAME" ;
				 	values[3]=commonData.getString("CUSTOMNAME","");
					names[4]="LINEPRICE_EXCITATION";//激励金额
				 	values[4]=pattr.getString("NOTIN_LINEPRICE_EXCITATION", "");
				 	names[5]="MONTHLYFEE_EXCITATION";//生成月租费
				 	values[5]=pattr.getString("NOTIN_MONTHLYFEE_EXCITATION","");
				 	names[6]="PRODUCT_NO" ;
				 	values[6]=pattr.getString("NOTIN_PRODUCTNO","");
					names[7]="CONTRACT_AGE";//
				 	values[7]=pattr.getString("NOTIN_CONTRACT_AGE", "");
				 	names[8]="PRODUCT_NUMBER";//
				 	values[8]=pattr.getString("NOTIN_PRODUCTNUMBER","");
					names[9]="EXCITATION_DATE";
				 	values[9]="to_date('"+update_time+"','yyyy-MM-dd HH24:mi:ss')";
				 	
			 	}else if("appvoeI".equals(nodeId)){
					names=new String [3];
			    	values=new String [3];
			 		if(flag){
			 			names[0]="RESULT_CODE";
						values[0]="3";//稽核通过
			 		}else{
						names[0]="RESULT_CODE";
						values[0]="-1";//稽核不通过
			 		}
					names[1]="AUDIT_OPINION";
				 	values[1]=auditText;
				 	names[2]="AUDIT_DATE";
				 	values[2]="to_date('"+update_time+"','yyyy-MM-dd HH24:mi:ss')";
				}else if("appvoeII".equals(nodeId)){
			 		names=new String [3];
			    	values=new String [3];
			 		if(flag){
			 			names[0]="RESULT_CODE";
						values[0]="4";//稽核通过
			 		}else{
			 			names[0]="RESULT_CODE";
						values[0]="-2";//审核不通过
			 		}
					names[1]="LEADER_OPINION";
				 	values[1]=auditText;
				 	names[2]="LEADER_DATE";
				 	values[2]="to_date('"+update_time+"','yyyy-MM-dd HH24:mi:ss')";
				}
			 	else if("applyConfirm".equals(nodeId)){//
			 		names=new String [1];
			    	values=new String [1];
//			    	if(idata0.getString("IS_DEAL_OVER", "").equals("否")){
//			    		names[0]="RESULT_CODE";
//						values[0]="1";//用户确认取消
//			    	}else
//			    	{
			    		names[0]="RESULT_CODE";
						values[0]="5";//已完成
//			    	}
		 			
			 	}
		  	  	IData idataNew=new DataMap();
				idataNew.put("IBSYSID",ibsysId);

				//注意修改数组长度，防止数据越界
//				System.out.print("fufn-TapMarketingEspSVC names:"+names+" values:"+values+" idataNew:"+idataNew);
			 	int code=TapMarketingBean.updateTapMarketingInfo(names,values,idataNew); 
//				System.out.print("fufn-TapMarketingEspSVC code:"+code);
			 	if(code!=1){
		        	 CSAppException.apperr(CrmCommException.CRM_COMM_103,"更新TF_B_WORKFORM_TAPMARKETING表失败，系统异常!");
			 	}
		  	  	
			}
		}
		
	}
	
	
	protected void  getEopAttrToList(String ibsysid,IDataset pattrList,IData commonData) throws Exception{
    	if(pattrList==null){
    		pattrList=new DatasetList();
    	}
    	if(commonData==null){
    		commonData= new DataMap();
    	}
    	if(ibsysid==null){
    		ibsysid="";
    	}
        IData inparam = new DataMap();
    	inparam.put("IBSYSID", ibsysid);
    	IDataset attrtInfo = WorkformAttrBean.getEopAttrToList(inparam);
//    	IDataset attrtInfo = CSViewCall.call(this, "SS.WorkformAttrSVC.getInfoByIbsysidAttrtype", inparam);
    	if (IDataUtil.isNotEmpty(attrtInfo)) {
        	
            for (int i = 0; i < attrtInfo.size(); i++) {
            	String recordNum=attrtInfo.getData(i).getString("RECORD_NUM","");
            	String key = attrtInfo.getData(i).getString("ATTR_CODE");
                String value = attrtInfo.getData(i).getString("ATTR_VALUE");
                if(recordNum.matches("^[0-9]*$")){
                	int recordNumInt=Integer.valueOf(recordNum);
                	if(recordNumInt<=0){
                		commonData.put(key, value);
                	}else{
                		int num =recordNumInt-1;
                		
                		if(pattrList.size()<recordNumInt){
                			for(int size=pattrList.size();size<recordNumInt;size++){
                				pattrList.add(new DataMap());
                			}
                		}
                		pattrList.getData(num).put(key, value);
                	}
                }
            	
            }
        }
    }

}
