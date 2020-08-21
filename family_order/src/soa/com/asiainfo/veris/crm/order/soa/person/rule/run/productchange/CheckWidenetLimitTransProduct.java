
package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;

/**
 * 存在宽带用户禁止变更以下产品
 * 	10000781 TD无线座机商务160套餐  
	10000784 TD无线座机商务200套餐  
	10000787 TD无线座机商务280套餐  
	10009432 TD无线座机（铁通）套餐  
	10009433 TD无线固话18元套餐  
	10001270 神州行户户通座机套餐 
 */
public class CheckWidenetLimitTransProduct extends BreBase implements IBREScript
{
	private static Logger logger = Logger.getLogger(CheckWidenetLimitTransProduct.class);
     
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {	
    	String xChoiceTag = databus.getString("X_CHOICE_TAG");//0-查询时校验 1=提交时候校验

        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
	    {
	    	
	    	String tradeTypeCode = databus.getString("TRADE_TYPE_CODE"); 
	    	IData reqData = databus.getData("REQDATA");// 请求的数据
	        if(!IDataUtil.isEmpty(reqData)){
//	        	IDataset selectedElements = new DatasetList(reqData.getString("SELECTED_ELEMENTS"));
//	        	if (IDataUtil.isNotEmpty(selectedElements)){
//	        		for (int j = 0, size = selectedElements.size(); j < size; j++)
//                    {
//                        IData element = selectedElements.getData(j); 
//                        String elementTypeCode = element.getString("ELEMENT_TYPE_CODE");
//                        String modifyTag = element.getString("MODIFY_TAG");
	        	String userProductId = databus.getString("PRODUCT_ID");// 老产品
	        	String newProductId = reqData.getString("NEW_PRODUCT_ID","");// 新产品        
                //变更主产品时候才处理
                if (StringUtils.isNotBlank(newProductId) &&!userProductId.equals(newProductId)){
                        	String serialNumber = databus.getString("SERIAL_NUMBER");    	
                	    	String limtProds=param.getString(databus, "LIMIT_PRODUCT");//禁止变更产品
                	    	
                	    	//检查用户是否存在宽带产品
                	    	IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfoBySerialNumber("KD_" + serialNumber); 
                	    	//只有该用户拥有宽带产品才进入处理
                	    	if (!IDataUtil.isEmpty(widenetInfos)&&widenetInfos.size()>0) {
		        					
					        	StringTokenizer st=new StringTokenizer(limtProds,","); 
						   	    for(int i=1;st.hasMoreTokens();i++){
						   	    	 String limtProd=st.nextToken();
						   	    	 if(newProductId.equals(limtProd)){
						   	    		String errorMsg="办理了宽带的用户，在办理产品变更时不能变更为以下产品：10000781 TD无线座机商务160套餐，10000784 TD无线座机商务200套餐，10000787 TD无线座机商务280套餐，10009432 TD无线座机（铁通）套餐，10009433 TD无线固话18元套餐，10001270 神州行户户通座机套餐";
						   	             BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 150512345, errorMsg);
						   	             return true;
						   	    	 }
						   	    }
                	        }
                        }
//                    }
//	        	} 
	        }  
	    }
        return false;
    } 
}
