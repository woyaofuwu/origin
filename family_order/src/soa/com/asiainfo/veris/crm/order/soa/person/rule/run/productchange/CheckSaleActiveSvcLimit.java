package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

/**    
 * 
 * @ClassName: CheckSaleActiveSvcLimit.java
 * @Description: 存在营销活动某产品 不能退订某个服务的限制【TradeCheckBefore】

 * @author: lizj
 * @date: 20200526

 *-------------------------------------------------------
 */
public class CheckSaleActiveSvcLimit extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");

        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {
            IData reqData = databus.getData("REQDATA");// 请求的数据

            if (IDataUtil.isNotEmpty(reqData))
            {
            	String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
            	IDataset commparaDs = BreQryForCommparaOrTag.getCommpara("CSM", 2510, strTradeTypeCode, CSBizBean.getUserEparchyCode());
            	if (IDataUtil.isNotEmpty(commparaDs))
                {
            		 for (int k = 0; k < commparaDs.size(); k++)
                     {
            			 IData commpara = commparaDs.getData(k);
            			 String paraCode1 = commpara.getString("PARA_CODE1");//服务编码
            			 String paraCode4 = commpara.getString("PARA_CODE4");//活动编码
            			 String paraCode5 = commpara.getString("PARA_CODE5");//包编码
            			 boolean svcTag =false;
            			 
            			 IDataset selectedElements = new DatasetList(reqData.getString("SELECTED_ELEMENTS"));
            			 String serviceName = "";
                         if (IDataUtil.isNotEmpty(selectedElements))
                         {
                         	 for (int i = 0, size = selectedElements.size(); i < size; i++)
                              {
                         		 IData element = selectedElements.getData(i);
                                 String elementTypeCode = element.getString("ELEMENT_TYPE_CODE");
                                 String serviceId = element.getString("ELEMENT_ID");
                                 serviceName = element.getString("ELEMENT_NAME");
                                 String modifyTag = element.getString("MODIFY_TAG");
                                  
                                 if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(elementTypeCode) && BofConst.MODIFY_TAG_DEL.equals(modifyTag) && serviceId.equals(paraCode1))
                                 {
                                	  svcTag = true;
                                	  break;
                                 }
                              
                              }
                         	 
                         	 if(svcTag){
                         		 
                         		UcaData uca = (UcaData) databus.get("UCADATA");
                                List<SaleActiveTradeData> saleActives =  uca.getUserSaleActives();
                                if(saleActives != null && saleActives.size() > 0)
                                {
                                    for(SaleActiveTradeData saleActive : saleActives)
                                    {
                                    	boolean activeTag =false;
                                        String saleProductId = saleActive.getProductId();
                                        String saleProductName = saleActive.getProductName();
                                        String salePackageId = saleActive.getPackageId();
                                        String endDate = SysDateMgr.decodeTimestamp(saleActive.getEndDate(), SysDateMgr.PATTERN_STAND);
                                        
                                        String errorMsg ="";
                                        if("-1".equals(paraCode5)){
                                        	if(saleProductId.equals(paraCode4) && endDate.compareTo(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND)) > 0)
                                            {
                                        		errorMsg = saleProductName+"活动生效期间，用户不能退订";
                                        		activeTag = true;
                                            }
                                        }else{
                                        	if(salePackageId.equals(paraCode5) && endDate.compareTo(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND)) > 0)
                                            {
                                        		errorMsg = saleProductName+"活动生效期间，用户不能退订";
                                        		activeTag = true;
                                        	   
                                            }
                                        }
                                        
                                       
                                    	if(activeTag){
                                    		 IData svcInfo = UpcCall.queryOfferByOfferId(BofConst.ELEMENT_TYPE_CODE_SVC,paraCode1,null);
                                         	if(IDataUtil.isNotEmpty(svcInfo)){
                                         		errorMsg = errorMsg+svcInfo.getString("OFFER_NAME")+"服务";
                                         	}
                                    		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "20200526", errorMsg);
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
