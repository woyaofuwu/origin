
package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckProductHaveSaleActive.java
 * @Description: 主产品变更时判断若有某活动,则无法订购某优惠【TradeCheckBefore】
 * @version: v1.0.0
 * @author: wuhao5
 * @date: May 21, 2019 10:12:45 AM Modification History: Date Author Version Description
 *        
 */
public class CheckProductHaveSaleActive extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        
        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {
            IData reqData = databus.getData("REQDATA");// 请求的数据
            String newStartDateString=reqData.getString("BOOKING_DATE","");//预约套餐开始时间 
            UcaData uca = (UcaData) databus.get("UCADATA");
            IDataset selectedElements = new DatasetList(reqData.getString("SELECTED_ELEMENTS"));
            //获取用户营销活动数据
            List<SaleActiveTradeData> userSaleActive = uca.getUserSaleActives();            	
            if(userSaleActive != null && userSaleActive.size() > 0){
            	for(SaleActiveTradeData saData : userSaleActive){
            		if(saData.getEndDate().compareTo(newStartDateString) >= 0){           			            		
	            		//查询用户的营销活动是否在配置中
	                    IDataset commpara1849 = CommparaInfoQry.getCommparaByCode1("CSM","1849",saData.getProductId(),"0898");
	                    if(IDataUtil.isNotEmpty(commpara1849) && commpara1849.size() > 0){                	
	                		IData commData = commpara1849.getData(0);
	                		//获取配置中的优惠编码
	                		String[] disCodes = commData.getString("PARA_CODE3").split("\\|");
	                		for(int j = 0;j < disCodes.length ;j ++){
	                			String disCode = disCodes[j];
	                            if(IDataUtil.isNotEmpty(selectedElements) && selectedElements.size() > 0){
	    	        				for(int i=0;i<selectedElements.size();i++)
	    	        				{
	    	        					IData element = selectedElements.getData(i);
	    	        					//获取产品类型(如 P , D , K 等)
	    	                            String elementTypeCode = element.getString("ELEMENT_TYPE_CODE");
	    	                            //优惠套餐编码
	    	                            String elementId = element.getString("ELEMENT_ID");
	    	                            //操作类型
	    	                            String modifyTag = element.getString("MODIFY_TAG");    	                            
	    	                            //校验,若订购的优惠为配置中的优惠,则拦截并提示
	    	        					if(BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode) && disCode.equals(elementId) && BofConst.MODIFY_TAG_ADD.equals(modifyTag)){
	    	        						//获取名称
	    	        						String elementName = UDiscntInfoQry.getDiscntNameByDiscntCode(elementId);
	    	        						String errmsg = "用户参与了\""+saData.getPackageName()+"\"营销活动,无法订购\""+elementName+"\"优惠";
	    	        		            	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "190726", errmsg);
	    	        		            	return true;
	    	        					}
	    	        				}
	                            }
	                		}                    	
	                    }                    
            		}
            	}
            }            
        }
        return false;
    }
}
