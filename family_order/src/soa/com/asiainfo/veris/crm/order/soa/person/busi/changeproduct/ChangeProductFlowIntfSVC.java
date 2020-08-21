
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.flow.FlowInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 *  -------------------------------------------------------* 
 * @ClassName: ChangeProductFlowIntfSVC.java
 * @Description: 产品变更接口赠送流量
 * @version: v1.0.0
 * @author:  lic
 *  -------------------------------------------------------* 
 */
public class ChangeProductFlowIntfSVC extends CSBizService
{
	private static Logger log = Logger.getLogger(ChangeProductFlowIntfSVC.class);
	
	/**
     * @Description: 流量平台分配赠送集团订购的GPRS加油包
     * @param data
     * @return
     * @throws Exception
     * @author: lic
     */
    public IDataset AddFlowGprsDiscnt(IData input) throws Exception
    {
    	IDataUtil.chkParam(input, "GRP_ID");//获取集团编码
    	IDataUtil.chkParam(input, "ORDER_ID");//获取流量平台订单编码
    	IDataUtil.chkParam(input, "ITEM");//获取流量分配数据
    	
    	IDataset resultSet = new DatasetList();
    	IDataset itemSet = new DatasetList();
    	IDataset flowInfos = input.getDataset("ITEM");
    	List<String>serialnumbers = new ArrayList<String>();
    	
    	
        String groupId = input.getString("GRP_ID");
        
        //IData grpCustInfo = UcaInfoQry.qryGrpInfoByGrpId(groupId);
        IDataset grpCustInfos = FlowInfoQry.queryOrderedGprsPrdId(groupId, null);
		if(IDataUtil.isEmpty(grpCustInfos))
		{
			CSAppException.apperr(GrpException.CRM_GRP_131, groupId);
		}
		
		String grpcustId = grpCustInfos.getData(0).getString("CUST_ID");
		String grpUserId = grpCustInfos.getData(0).getString("USER_ID");
    	
    	//按号码分组排重
    	for(int i=0;i<flowInfos.size();i++)
    	{
    		String sntxt =flowInfos.getData(i).getString("SERIAL_NUMBER");
    		if(!serialnumbers.contains(sntxt))
    		{
    			serialnumbers.add(sntxt);
    		}
    	}
    	
    	//按号码归并流量包元素
    	IData snElementsList = new DataMap();
	    for (int i = 0; i < serialnumbers.size(); i++)
	    {
	    	IDataset pkgElements = new DatasetList();
	    	
    	    for (int j = 0; j < flowInfos.size(); j++)
    	    {
    		    if(serialnumbers.get(i).equals(flowInfos.getData(j).getString("SERIAL_NUMBER")))
    		    {
    			    pkgElements.add(flowInfos.getData(j));
    		    }
    	    }
    	    snElementsList.put(serialnumbers.get(i), pkgElements);
	    }
	    
	    Iterator iter = snElementsList.keySet().iterator();
        while (iter.hasNext())
        {
        	String serialNumber = (String)iter.next();
        	IDataset elements = (IDataset)snElementsList.get(serialNumber);
        	IDataset pkgElements = new DatasetList(); 
            StringBuffer platTradeIds= new StringBuffer();
        	
            try
        	{
            	SessionManager.getInstance().start();
	        	for(int k =0;k<elements.size();k++)
	    	    {
	        		String pkgGprs = elements.getData(k).getString("PAK_GPRS");//每个流量包的流量   单位：M
	        		String pkgcnt =  elements.getData(k).getString("PAK_NUM","1");//流量包个数
	        		String money = elements.getData(k).getString("PAK_MONEY"); //每个流量包的费用   单位：分
	        		int icnt = Integer.parseInt(pkgcnt);
	        	
	        		//分配赠送的流量大小
	        		IDataset attrSet = new DatasetList();
	        		IData attr1 = new DataMap();	        		 
                    attr1.put("ATTR_CODE", "10059657");		//卡面值属性(不是销售价格)
                    BigDecimal bdMoney = new BigDecimal(Float.parseFloat(money)*icnt);
                    attr1.put("ATTR_VALUE",	bdMoney.toString()); //面值转化成分		
                    attrSet.add(attr1);
	        		IData attr2 = new DataMap();	        		 
	        		BigDecimal bd = new BigDecimal(Float.parseFloat(pkgGprs)*icnt*1024*1024);
                    attr2.put("ATTR_CODE", "10059658");		//流量属性
                    attr2.put("ATTR_VALUE",bd.toString()); //流量转化成BYTE		
                    attrSet.add(attr2);
                    
                    //查pckId
        			IDataset datapckList = FlowInfoQry.getCustDatapckByIDValue(grpcustId,pkgGprs,money);
        			if(IDataUtil.isEmpty(datapckList))
        			{
        				CSAppException.apperr(GrpException.CRM_GRP_895, groupId);
        			}
	        		
	        		String platTradeId = elements.getData(k).getString("TRANS_ID"); //平台流水
	        		platTradeIds.append(platTradeId);
	        		platTradeIds.append(",");
	        		IData element = new DataMap();
	        		element.put("PRODUCT_ID", "-1");  //设置一个元素ID
	        		element.put("PACKAGE_ID", "-1");  //设置一个元素ID
		            element.put("ELEMENT_ID", "52000008");  //设置一个元素ID
		            element.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
		            element.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
		            element.put("START_DATE", elements.getData(k).getString("PAK_START_DTAE"));
		            element.put("END_DATE", SysDateMgr.getAddMonthsLastDay(0));  //强制到月底截止
		            //element.put("END_DATE", elements.getData(k).getString("PAK_END_DTAE"));
		            element.put("TRANS_ID", platTradeId);
		            element.put("ATTR_PARAM", attrSet);
		            pkgElements.add(element);
	    	    }
	        	
	        	//号码校验
	        	IData checkData = new DataMap();
	        	IData resltData = new DataMap();
	        	
	        	checkData.put("SERIAL_NUMBER", serialNumber);
	        	IDataset checkRlt =  CSAppCall.call("SS.UserInfoQrySVC.checkUserStateBySerialNumber", checkData);
    	    	IData tmp  = checkRlt.getData(0);
    	    	if("0".equals(tmp.getString("X_RESULTCODE","0"))){
    	    		if(!"0".equals(tmp.getString("USER_STATUS","")))//非正常状态
        	    	{
    	    			String  msg ="该号码已停机!";
    	    			if("2".equals(tmp.getString("USER_STATUS",""))){
    	    				msg ="该号码用户不存在!";
    	    			}
    	    			resltData.put("SERIAL_NUMBER", serialNumber);
    	    			resltData.put("X_RESULTCODE", tmp.getString("USER_STATUS","2"));
    	    			resltData.put("X_RESULTINFO", msg);
    	    		    getReturnValueForEachTrade(elements,itemSet,resltData);
    	    		    continue;
        	    	}
    	    	}
    	    	else
    	    	{
    	    		String  msg ="号码校验失败!";
    	    		resltData.put("SERIAL_NUMBER", serialNumber);
	    			resltData.put("X_RESULTCODE", "-1");
	    			resltData.put("X_RESULTINFO", msg);
	    		    getReturnValueForEachTrade(elements,itemSet,resltData);
    	    	}
        	    	
	        	
        		IData callData = new DataMap();
     	        callData.put("SERIAL_NUMBER", serialNumber);
     	        callData.put("SELECTED_ELEMENTS", pkgElements);
     	        callData.put("FLOW_BAG_TAG", "1"); //流量经营赠送流量标志
     	        callData.put("OUT_ORDER_ID", input.getString("ORDER_ID"));
     	        callData.put("GROUP_ID", input.getString("GRP_ID"));
     	        callData.put("USER_ID_A", grpUserId);
     	        callData.put("FLOW_ITEM", elements);
     	        
     	        IDataset result =  CSAppCall.call("SS.ChangeProductFlowBagSVC.ChangeProductFlowBag", callData);
     	        IData resultData = new DataMap();
     	        resultData.put("SERIAL_NUMBER", serialNumber);
     	        resultData.put("TRADE_ID", result.getData(0).getString("TRADE_ID"));
     	        resultData.put("X_RESULTCODE", result.getData(0).getString("X_RESULTCODE","0"));
       		    resultData.put("X_RESULTINFO", result.getData(0).getString("X_RESULTINFO","OK"));
       		    
       		    getReturnValueForEachTrade(elements,itemSet,resultData);
            	//事务提交
            	SessionManager.getInstance().commit();
        	}
        	catch(Exception e)
        	{
        		//事务回滚
        		SessionManager.getInstance().rollback();
        		  if (log.isDebugEnabled())
        	        {
        			  //log.info("(e);
        	        }     		
        		IData resultData = new DataMap();
        		resultData.put("X_RESULTCODE", "-1");
        		resultData.put("X_RESULTINFO", e.getMessage());
        		resultData.put("SERIAL_NUMBER", serialNumber);
        		resultData.put("TRADE_ID", "");
        		
        		getReturnValueForEachTrade(elements,itemSet,resultData);
        		//continue; 
        	}
        	finally
        	{
        		//注销
        		SessionManager.getInstance().destroy();
        	}
        }
        
        IData resultData  = new DataMap();
        resultData.put("ORDER_ID", input.getString("ORDER_ID"));
        resultData.put("GRP_ID", input.getString("GRP_ID"));
        resultData.put("ITEM", itemSet);
        resultSet.add(resultData);
        
        return resultSet;
    }
    
    
    public void getReturnValueForEachTrade(IDataset elements,IDataset resultSet,IData oneData) throws Exception
    {
    	if(elements.size()>1)
        {
        	for(int ii =0 ;ii<elements.size();ii++)
        	{
        		IData tmpdata = new DataMap();
        		tmpdata.putAll(oneData);
        		tmpdata.put("TRANS_ID", elements.getData(ii).getString("TRANS_ID"));
        		resultSet.add(tmpdata);
        	}
        }else
        {
        	IData tmpdata = new DataMap();
    		tmpdata.putAll(oneData);
    		tmpdata.put("TRANS_ID", elements.getData(0).getString("TRANS_ID"));
    		resultSet.add(tmpdata);
        }
    }
	
    /**
     * @Description: 匹配资费编码
     * @param data
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Jul 24, 2014 9:18:11 AM
     */
    public String autoGetCommparaGprsDiscntID(String gprsValue,String money) throws Exception
    {
    	IDataset commparaInfos = CommparaInfoQry.getCommparaByAttrCode1("CSM", "1111", gprsValue, "ZZZZ",null);
    	
    	if(IDataUtil.isEmpty(commparaInfos))
    	{
    		 CSAppException.apperr(ParamException.CRM_PARAM_145);
    	}
    	else
    	{
    		 return commparaInfos.getData(0).getString("PARA_CODE2");
    	}
    	 return null;
    }
    
    /**
     * @Description: 流量平台流量卡充值接口
     * @param data
     * @return
     * @throws Exception
     * @author: lic
     */
    public IDataset AddFlowUserDiscnt(IData input) throws Exception
    {
    	IDataUtil.chkParam(input, "SERIAL_NUMBER");//获取充值号码
    	IDataUtil.chkParam(input, "FLOW_CARD");//获取充值流量卡
    	
    	String serialnumber = input.getString("SERIAL_NUMBER");
    	String flowcard = input.getString("FLOW_CARD");
    	IData resultData  = new DataMap();
    	IDataset resultSet = new DatasetList();
    	IData checkData = new DataMap();
    	checkData.put("SERIAL_NUMBER", serialnumber);
    	IDataset checkRlt =  CSAppCall.call("SS.UserInfoQrySVC.checkUserStateBySerialNumber", checkData);
    	IData tmp  = checkRlt.getData(0);
    	if("0".equals(tmp.getString("X_RESULTCODE","0"))){
    		if(!"0".equals(tmp.getString("USER_STATUS","")))//非正常状态
	    	{
    			String  msg ="该号码已停机!";
    			if("2".equals(tmp.getString("USER_STATUS",""))){
    				msg ="该号码用户不存在!";
    			}
    			resultData.put("SERIAL_NUMBER", serialnumber);
    			resultData.put("X_RESULTCODE", "-1");
    			resultData.put("X_RESULTINFO", msg);
    			resultSet.add(resultData);
    	        
    	        return resultSet;
	    	}
    	}else
    	{
    		String  msg ="号码校验失败!";
    		resultData.put("SERIAL_NUMBER", serialnumber);
    		resultData.put("X_RESULTCODE", "-1");
    		resultData.put("X_RESULTINFO", msg);
    		resultSet.add(resultData);
            
            return resultSet;
    	}
    	
        IDataset valueCardInfos = ResCall.getUsedEntityCardInfo(flowcard, flowcard, "", "");
        if (IDataUtil.isEmpty(valueCardInfos))
        {
        	String  msg ="没有查询到卡信息！";
    		resultData.put("SERIAL_NUMBER", serialnumber);
    		resultData.put("X_RESULTCODE", "-1");
    		resultData.put("X_RESULTINFO", msg);
    		resultSet.add(resultData);
            
            return resultSet;
        }
        IData valueCardInfo = valueCardInfos.getData(0);
        if("1".equals(valueCardInfo.getString("ACTIVE_FLAG")))
        {
        	IData callData = new DataMap();
            callData.put("SERIAL_NUMBER", serialnumber);
            callData.put("ELEMENT_ID", "52000010");
            callData.put("ELEMENT_TYPE_CODE", "D");
            callData.put("MODIFY_TAG", "0");
            callData.put("BOOKING_TAG", "0");
            callData.put("ATTR_STR1", "52000012");
            callData.put("ATTR_STR2", valueCardInfo.getString("ADVISE_PRICE","0"));
            callData.put("ATTR_STR3", "52000011");
            callData.put("ATTR_STR4", valueCardInfo.getString("FLOW_CODE","0"));
            callData.put("FLOW_CARD", flowcard);
            try {
            	IDataset result =  CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", callData);
			
            	if(IDataUtil.isNotEmpty(result) && !"".equals(result.getData(0).getString("TRADE_ID","")))
                {
                	IDataset modifyvalueCardInfos = ResCall.modifyUsedEntityCardInfo(flowcard, flowcard, "", "", serialnumber);
                	if(IDataUtil.isNotEmpty(modifyvalueCardInfos) &&  "0".equals(modifyvalueCardInfos.getData(0).getString("X_RESULTCODE","")))
                	{
                		
                		
                		resultData.put("SERIAL_NUMBER", serialnumber);
                        resultData.put("FLOW_CARD", flowcard);
                        resultData.put("TRADE_ID", result.getData(0).getString("TRADE_ID"));
                        resultData.put("X_RESULTCODE", "0");
                        resultData.put("X_RECORDNUM", "1");
                	    resultData.put("X_RESULTINFO", flowcard+"充值成功");
                	    resultSet.add(resultData);
                	    
                        Dao.executeUpdateByCodeCode("TL_B_VALUECARD_DETAILED", "UP_BY_FLOW_CARD", resultData);

                	    return resultSet;
                	}else{
                		String  msg ="修改流量卡信息失败！";
                		resultData.put("SERIAL_NUMBER", serialnumber);
                		resultData.put("X_RESULTCODE", "-1");
                		resultData.put("X_RESULTINFO", msg);
                		resultSet.add(resultData);
                        
                        return resultSet;
                	}
                	
                }else{
                	String  msg ="绑定流量卡信息失败！";
            		resultData.put("SERIAL_NUMBER", serialnumber);
            		resultData.put("X_RESULTCODE", "-1");
            		resultData.put("X_RESULTINFO", msg);
            		resultSet.add(resultData);
                    
                    return resultSet;
                }
            } catch (Exception e) {
            	String  msg =e.toString();
        		resultData.put("SERIAL_NUMBER", serialnumber);
        		resultData.put("X_RESULTCODE", "-1");
        		resultData.put("X_RESULTINFO", msg);
        		resultSet.add(resultData);
                
                return resultSet;
			}  
        }else{
        	String  msg ="销售中包含未激活实体卡，不能充值！";
    		resultData.put("SERIAL_NUMBER", serialnumber);
    		resultData.put("X_RESULTCODE", "-1");
    		resultData.put("X_RESULTINFO", msg);
    		resultSet.add(resultData);
            
            return resultSet;
        }
    	    	
    }
    
 
}
