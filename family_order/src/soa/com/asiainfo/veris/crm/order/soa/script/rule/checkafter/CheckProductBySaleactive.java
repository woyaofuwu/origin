package com.asiainfo.veris.crm.order.soa.script.rule.checkafter;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.SaleservParamInfoQry;

/*
 * 针对用户拥有某些活动，而限制某些主套餐变更
 * by xiaocl
 * */
public class CheckProductBySaleactive extends BreBase implements IBREScript{
	private static final long serialVersionUID = -4040074912670300338L;
	private static Logger logger = Logger.getLogger(CheckProductBySaleactive.class);

	@Override
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
		
		if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step in CheckProductBySaleactive() >>>>>>>>>>>>>>>>>>");
		//设置返回初始值，规则引擎在配置表中IsRevolt没有取反的情况下，默认返回TRUE为符合某一规则触发条件，由规则引擎抛出
		//错误信息，此错误信息需要配置在TD_BRE_RULE_MANAGE中，也可以直接通过BreTipsHelp.addNorTipsInfo来在业务规则代码里面抛出来。
		String xChoiceTag = databus.getString("X_CHOICE_TAG");

        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {   
        	IData reqData = databus.getData("REQDATA");// 请求的数据

            if (IDataUtil.isNotEmpty(reqData))
            {	       
                String userProductId = databus.getString("PRODUCT_ID");// 老产品
                String newProductId = reqData.getString("NEW_PRODUCT_ID");// 新产品
                String bookingDate = reqData.getString("BOOKING_DATE");// 预约时间
                
                if (StringUtils.isNotBlank(newProductId) && !userProductId.equals(newProductId))//主产品变更
                {
                	UcaData uca = (UcaData) databus.get("UCADATA");

                    List<SaleActiveTradeData> saleActives =  uca.getUserSaleActives();
                    
                    if(saleActives != null && saleActives.size() > 0)
                    {
                        for(SaleActiveTradeData saleActive : saleActives)
                        {
                            String saleProductId = saleActive.getProductId();
                            String saleProductName = saleActive.getProductName();
                            String endDate = SysDateMgr.decodeTimestamp(saleActive.getEndDate(), SysDateMgr.PATTERN_STAND);
                              
	                      //根据用户当前的活动，在参数表td_b_saleserv_commpara找出与起互斥的产品列表  
	            			//需要补全代码		
	            			IDataset salelimit=SaleservParamInfoQry.queryByParaCode("SALE_LIMIT", saleProductId, CSBizBean.getTradeEparchyCode());
	            			if(IDataUtil.isNotEmpty(salelimit))
	            			{               				              			
	            				for(int i = 0 , iSize = salelimit.size() ; i < iSize; i ++ )
	            				{
	            					String LimitProductId = salelimit.getData(i).getString("PARA_CODE1");                			
		                            if(LimitProductId.equals(userProductId) && endDate.compareTo(SysDateMgr.decodeTimestamp(bookingDate, SysDateMgr.PATTERN_STAND)) > 0)
		                            {
		                            	StringBuilder strError = new StringBuilder("业务登记后条件判断:").append("用户当前已经办理活动[").append(saleProductName).append("]不能做产品[").append(UProductInfoQry.getProductNameByProductId(userProductId)).append("]的变更！");
		        			            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201157", strError.toString());
		                                return true;                          
		                            } 
	            			    }
	            			}                            
                        }
                    }
                }
            }
        }
		if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step out CheckProductBySaleactive() >>>>>>>>>>>>>>>>>>");
		return false;
    }
//此需要参数配置
//规则定义： 目前这个规则，就配置在通用业务规则集td_bre_business_base中,然后在规则总表td_bre_rule_manage里面定义基本信息。
//其余跟湖南的一样，td_bre_relation , td_bre_definition 。另外关于错误编码的问题，如果外围需要转换编码，那么需要把错误编码先在TD_BRE_INFOCDOE注册
}
