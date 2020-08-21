
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade.esp;

import org.apache.log4j.Logger;

import com.ailk.bizservice.base.CSBizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ExceptionUtils;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

public class EspFileIntfSvc extends GroupOrderService
{
	protected static final Logger log = Logger.getLogger(EspFileIntfSvc.class);
    /**
     * 反向成员签约接口（ESP向省系统下发成员签约关系）
     * @param data
     * @return 
     * @throws Exception
     */
    public static void dealEspMemBiz(IData data) throws Exception
    {
    	//1、捞取数据    	
    	IDataset MemberNumbers=EspFileIntfBean.getOrderMemberInfo();
    	if(IDataUtil.isNotEmpty(MemberNumbers)){
       	for(int i=0;i<MemberNumbers.size();i++){
       	    IData MemberInfo=MemberNumbers.getData(i);
       		IData orderResult=initEspMemOrderResult(MemberInfo);      			
       		try{
       		    //2、拼装数据(单个成员处理)
             	IData ProductOrderMember=makeEspDatas(MemberInfo);
             	//3、处理业务
                IData dealResult = CSAppCall.call("CS.SynEspMebOrderSVC.dealEspMebOrder", ProductOrderMember).getData(0);
                if(IDataUtil.isNotEmpty(dealResult)){
                orderResult.put("RSP_CODE", dealResult.getString("RESULT_CODE"));	
                orderResult.put("RSP_DESC",dealResult.getString("RESULT_INFO"));	
                }
               }catch(Exception ex){
            	if(log.isDebugEnabled()){
            		log.debug("错误信息2"+ExceptionUtils.getExceptionInfo(ex));
            	}
            	orderResult.put("RSP_CODE", "99");	
            	orderResult.put("RSP_DESC",ex.getMessage());
               }
             //4.插反馈表      
           	EspFileIntfBean.insertEspMemberDealResult(orderResult); 
           	EspFileIntfBean.UpdateSynMebInfo(orderResult); //更新同步信息表       
        	}
          }
    	
    }
    public static IData initEspMemOrderResult(IData data) throws Exception
    {
    	IData retResult=new DataMap();
    	retResult.put("PAKG_ID", data.getString("PAKG_ID"));
    	retResult.put("SEQ_ID", SeqMgr.getEspSynId());
    	retResult.put("SYN_ID", data.getString("SYN_ID"));
    	retResult.put("DEAL_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
    	retResult.put("MEMBER_NUMBER", data.getString("MEMBER_NUMBER",""));
    	retResult.put("PRODUCT_ORDER_ID", data.getString("PRODUCT_ORDER_ID",""));
    	retResult.put("SYN_TAG", "F");//同步标识
    	retResult.put("FLAG", "0");//0:未反馈，1：已反馈
    	retResult.put("RSP_CODE", "00");	
    	retResult.put("RSP_DESC","同步成功！");
    	retResult.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
    	retResult.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
    	return retResult;
    }

    /**
     * 解析表数据拼装报文
     * @param MemberNumber
     * @return
     * @throws Exception
     */
    public static IData makeEspDatas(IData MemberNumber) throws Exception
    {
      if(log.isDebugEnabled()){
    	 log.debug("makeEspDatas-MemberNumber"+MemberNumber);
      }
      IData orderMemberInfo=new DataMap();
      orderMemberInfo.put(IntfField.ANTI_INTF_FLAG[0], "1");// 反向标记 1代表是反向接口     
      String serial_number=MemberNumber.getString("MEMBER_NUMBER");
      orderMemberInfo.put("PAKG_ID", MemberNumber.getString("PAKG_ID",""));
      orderMemberInfo.put("SYN_ID", MemberNumber.getString("SYN_ID",""));
      orderMemberInfo.put("SYNC_TIME", MemberNumber.getString("SYNC_TIME",""));
      orderMemberInfo.put("PRODUCT_ORDER_ID", MemberNumber.getString("PRODUCT_ORDER_ID",""));
      orderMemberInfo.put("PRODUCT_NUMBER", MemberNumber.getString("PRODUCT_NUMBER",""));
      orderMemberInfo.put("PRODUCT_NAME", MemberNumber.getString("PRODUCT_NAME",""));
      orderMemberInfo.put("SERIAL_NUMBER", serial_number);
      orderMemberInfo.put("ACTION", MemberNumber.getString("ACTION",""));
      orderMemberInfo.put("MEMBER_TYPE_ID",  MemberNumber.getString("MEMBER_TYPE_ID",""));
      orderMemberInfo.put("PAYMENT_TYPE", MemberNumber.getString("PAYMENT_TYPE",""));
      orderMemberInfo.put("MEMBER_GROUP_NUMBER", MemberNumber.getString("MEMBER_GROUP_NUMBER",""));
      orderMemberInfo.put("EFF_DATE",  MemberNumber.getString("EFF_DATE",""));
      IDataset elments=new DatasetList();
      String elementId=MemberNumber.getString("MEMBER_PATE_PLANID");
      String elementName=MemberNumber.getString("RATE_PLAN_NAME");
      IDataset elementAttrCodes=new DatasetList(MemberNumber.getString("PARAMETER_NUMBER"));
      IDataset elementAttrNames=new DatasetList(MemberNumber.getString("PARAM_NAME"));
      IDataset elementAttrValues=new DatasetList(MemberNumber.getString("PARAM_VALUE"));
      if(StringUtils.isNotBlank(elementId)&&StringUtils.isNotBlank(elementName)){
    	  String[] elementIds=elementId.split(","); 
    	  String[] elementNames=elementName.split(","); 	  
    	  for(int i=0;i<elementIds.length;i++){
    		IData elementInfo=new DataMap();
    		elementInfo.put("MEMBER_PATE_PLANID", elementIds[i]);
    		elementInfo.put("RATE_PLAN_NAME", elementNames[i]);
    		 IDataset attrs=new DatasetList();
    		if(IDataUtil.isNotEmpty(elementAttrCodes)&&IDataUtil.isNotEmpty(elementAttrNames)&&IDataUtil.isNotEmpty(elementAttrValues)){//有资费属性
    		   if(elementIds.length!=elementAttrCodes.size()||elementIds.length!=elementAttrNames.size()||elementIds.length!=elementAttrValues.size()){
    		    //资费与资费属性个数不符合 
    			   CSAppException.apperr(CrmCommException.CRM_COMM_103,"资费与资费属性个数不符合 !");
    		    }     			
        		 IDataset attrCodeList=elementAttrCodes.getDataset(i);
        		// IDataset attrNameList=elementAttrNames.getDataset(i);
        		 IDataset attrvalueList=elementAttrValues.getDataset(i);
        		 for(int j=0;j<attrCodeList.size();j++){
        			IData attrInfo=new DataMap();
        			attrInfo.put("PARAMETER_NUMBER", attrCodeList.get(j).toString());
        	//		attrInfo.put("PARAM_NAME", attrNameList.get(j).toString());
        			attrInfo.put("PARAM_VALUE", attrvalueList.get(j).toString());
        			attrs.add(attrInfo);
        		 } 
        		 if(log.isDebugEnabled()){
        	     	 log.debug("makeEspDatas-attrs"+attrs);
        		 }        		
      	    }  
    		 elementInfo.put("RATE_PARAM", attrs); 
    		 elments.add(elementInfo);
    	  }
      }
      orderMemberInfo.put("MEMBER_RATE_PLAN",elments);//成员资费
      String characterId=MemberNumber.getString("CHARACTER_ID");
      String charactername=MemberNumber.getString("CHARACTER_NAME");
      String charactervalue=MemberNumber.getString("CHARACTER_VALUE");
      //有成员扩展属性
      IDataset characters=new DatasetList();
      if(StringUtils.isNotBlank(characterId)&&StringUtils.isNotBlank(charactername)&&StringUtils.isNotBlank(charactervalue)){    	
    	String [] charcIdList=characterId.split(",");
    	String [] charcNameList=charactername.split(",");
    	String [] charcValueList=charactervalue.split(",");
    	if(charcIdList.length!=0&&(charcIdList.length!=charcNameList.length||charcIdList.length!=charcValueList.length)){
    		//成员扩展属性ID与成员扩展属性名称或成员扩展属性值个数不一致
    		  CSAppException.apperr(CrmCommException.CRM_COMM_103,"成员扩展属性ID与成员扩展属性名称或成员扩展属性值个数不一致!");
    	}
    	for(int a=0;a<charcIdList.length;a++){
    	 IData param=new DataMap();
    	 param.put("CHARACTER_ID", charcIdList[a]);
    	 param.put("CHARACTER_NAME", charcNameList[a]);
    	 param.put("CHARACTER_VALUE", charcValueList[a]);
    	 characters.add(param);
    	}
      }
      orderMemberInfo.put("EXTENDS",characters);//成员扩展属性

      return orderMemberInfo;
    }
   
    
}