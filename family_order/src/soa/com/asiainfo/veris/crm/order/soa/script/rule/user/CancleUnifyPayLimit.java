
package com.asiainfo.veris.crm.order.soa.script.rule.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CancleUnifyPayLimit.java
 * @Description: 在CRM-个人业务-日常业务-统一付费业务办理界面，增加判断规则。 
				   当用户办理了某个活动时，无法进行统付成员删除操作，但可以进行新增操作。
 * @version: v1.0.0
 * @author: tanzheng
 * @date: 2018-4-9
 */
public class CancleUnifyPayLimit extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(CancleUnifyPayLimit.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CancleUnifyPayLimit() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        String masterUserId = databus.getString("USER_ID", "0");//主号userId
        List<HashMap<String, String>> slaveUserIdList = new ArrayList<HashMap<String,String>>();//副号userId列表
        String trade_type_code = databus.getString("TRADE_TYPE_CODE", "0");
        IDataset listTradeRelation = databus.getDataset("TF_B_TRADE_RELATION");
        //循环台账列表，找出删除的副号userId
        for (Iterator iter = listTradeRelation.iterator(); iter.hasNext();)
        {
            IData tradeAttr = (IData) iter.next();
            if ("1".equals(tradeAttr.getString("MODIFY_TAG")) && "56".equals(tradeAttr.getString("RELATION_TYPE_CODE")))
            {
            	HashMap map = new HashMap<String,String>();
            	map.put("userId", tradeAttr.getString("USER_ID_B"));
            	map.put("serialNumber", tradeAttr.getString("SERIAL_NUMBER_B"));
            	slaveUserIdList.add(map);
            }
            
        }
        
        //如果没有副号的删除就直接返回
        if(slaveUserIdList.size()==0){
        	return bResult;
        }
        //判断主号是否办理了指定的活动
        boolean isSpecifyActivty = false;
       	IDataset comparas =BreQryForCommparaOrTag.getCommpara("CSM",9881,trade_type_code,"ZZZZ");
       	String product_id_Str=((IData)comparas.get(0)).getString("PARA_CODE1");
       	String buf[]=product_id_Str.split("\\|");
        if (IDataUtil.isNotEmpty(comparas))
        {
              for (int i = 0, len = buf.length; i < len; i++)
              {
                  String pro_id=buf[i];
                  IDataset sales =UserSaleActiveInfoQry.queryUserSaleActiveProdId(masterUserId,pro_id,"0");
                  if (IDataUtil.isNotEmpty(sales)&&sales.size()>0)
                  {
                	  isSpecifyActivty = true;
                	  break;
                  }
              }
        }else{
        	logger.error("CancleUnifyPayLimit规则类执行异常，缺失9881配置！！！！");
        	return bResult;
        }
        
        
      
        	
    	
    	
	    if(isSpecifyActivty){
	        	//如果主卡存在指定的营销活动，就判断副卡是不是有指定的优惠
	        	String discntLimit=((IData)comparas.get(0)).getString("PARA_CODE2");
	        	String discnts[]=discntLimit.split("\\|");
	        	
	        	for(HashMap<String, String> map : slaveUserIdList){
	        		for(String discntCode : discnts){
	        			IDataset discnt = UserDiscntInfoQry.getAllDiscntByUserId(map.get("userId"),discntCode);
	        			if(IDataUtil.isNotEmpty(discnt)){
	        				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2018040901, 
	        						"该号码"+map.get("serialNumber")+"办理了"+discntCode+"优惠，不能取消统一付费业务！");
	        				return true;
	        			}
	        		}
	        	}
	        }
	    
	    
	    //如果主号没有办理指定的活动,判断副号是不是有办理指定的活动
    	String slaveProduct=((IData)comparas.get(0)).getString("PARA_CODE3");
    	buf = slaveProduct.split("\\|");
    	for(HashMap<String, String> map : slaveUserIdList){
        	for(String pro_id : buf){
        		IDataset discnt = UserSaleActiveInfoQry.queryUserSaleActiveProdId(map.get("userId"),pro_id,"0");
        		if(IDataUtil.isNotEmpty(discnt)){
	        		  BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2018040901, 
	        				  "该号码"+map.get("serialNumber")+"办理了"+pro_id+"营销活动，不能取消统一付费业务！");
	              	  bResult = true;
	              	  break;
        		}
        	}
        }
    	
    	
        
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBCheckLimitMasterPay() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
