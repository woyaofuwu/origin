package com.asiainfo.veris.crm.order.soa.script.rule.platsvc;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
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
 * @ClassName: CheckCancelPlatSvcLimit.java
 * @Description: 活动或减免优惠生效期间，用户不能在平台业务办理界面，单独退订平台服务。【TradeCheckBefore】

 * @author: lizj
 * @date: 20200526

 *-------------------------------------------------------
 */
public class CheckPlatSvcLimit extends BreBase implements IBREScript
{
	 private static Logger logger = Logger.getLogger(CheckPlatSvcLimit.class);

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        
        logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckCancelPlatSvcLimit() >>>>>>>>>>>>>>>>>>");

        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {
        	
            IData reqData = databus.getData("REQDATA");// 请求的数据
            if (IDataUtil.isNotEmpty(reqData))
            {
            	String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
            	IDataset commparaDs = BreQryForCommparaOrTag.getCommpara("CSM", 2510, strTradeTypeCode, CSBizBean.getUserEparchyCode());
            	System.out.println("CheckCancelPlatSvcLimit标志"+commparaDs);
            	if (IDataUtil.isNotEmpty(commparaDs))
                {
            		 for (int k = 0; k < commparaDs.size(); k++)
                     {
            			 IData commpara = commparaDs.getData(k);
            			 String paraCode1 = commpara.getString("PARA_CODE1");//平台服务编码
            			 String paraCode2 = commpara.getString("PARA_CODE2");//优惠编码
            			 String paraCode5 = commpara.getString("PARA_CODE5");//包编码
            			 boolean serviceTag = false;
            			 boolean discntTag = false;
                    	 boolean activeTag = false;
            			 
            			 IDataset listTradePlatSvc = databus.getDataset("TF_B_TRADE_PLATSVC");
            			 System.out.println("CheckCancelPlatSvcLimit标志1"+listTradePlatSvc);
            			 IDataset selectedElements = new DatasetList(reqData.getString("SELECTED_ELEMENTS"));
                         if (IDataUtil.isNotEmpty(selectedElements))
                         {
                        	 for (int i = 0; i < selectedElements.size(); i++)
                             {
                        		 IData platSvcTrade = selectedElements.getData(i);
                        		 String modifyTag = platSvcTrade.getString("MODIFY_TAG");
                        		 String serviceId = platSvcTrade.getString("SERVICE_ID");
                        		 if (BofConst.MODIFY_TAG_DEL.equals(modifyTag) && serviceId.equals(paraCode1))
                                 {
                        			 serviceTag = true ;
                        			 break;
                                 }
                        		 
                             }
                        	 
                        	 if(serviceTag){
                        		 //判断用户是否含有有效优惠
                        		 UcaData uca = (UcaData) databus.get("UCADATA");
                             	 List<DiscntTradeData> discntDatas =  uca.getUserDiscnts();
                                 if(discntDatas != null && discntDatas.size() > 0)
                                 {
                                     for(DiscntTradeData discntData : discntDatas)
                                     {
                                         String discntCode = discntData.getDiscntCode();
                                         String endDate = SysDateMgr.decodeTimestamp(discntData.getEndDate(), SysDateMgr.PATTERN_STAND);
                                         if(discntCode.equals(paraCode2) && endDate.compareTo(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND)) > 0)
                                         {
                                        	 discntTag = true;
                                        	 break;
                                         }
                                     }
                                 }
                             	
                             	
                                 List<SaleActiveTradeData> saleActives =  uca.getUserSaleActives();
                                 System.out.println("CheckCancelPlatSvcLimit标志2"+saleActives);
                                 if(saleActives != null && saleActives.size() > 0)
                                 {
                                     for(SaleActiveTradeData saleActive : saleActives)
                                     {
                                         String salePackageId = saleActive.getPackageId();
                                         String endDate = SysDateMgr.decodeTimestamp(saleActive.getEndDate(), SysDateMgr.PATTERN_STAND);
                                         if(salePackageId.equals(paraCode5) && endDate.compareTo(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND)) > 0)
                                         {
                                        	 activeTag = true;
                                        	 break;
                                         }
                                         
                                     }
                                 }
                        	 }
                        	 
                         }
                         
                         if(discntTag||activeTag){
                        	String errorMsg ="";
                        	IData platSvcInfo = UpcCall.queryOfferByOfferId(BofConst.ELEMENT_TYPE_CODE_PLATSVC,paraCode1,null);
                        	if(IDataUtil.isNotEmpty(platSvcInfo)){
                        		if(discntTag){
                        			errorMsg = paraCode1+"优惠生效期间，用户不能在平台业务办理界面，单独退订"+platSvcInfo.getString("OFFER_NAME")+"。";
                        		}else{
                        			errorMsg = paraCode5+"活动生效期间，用户不能在平台业务办理界面，单独退订"+platSvcInfo.getString("OFFER_NAME")+"。";
                        		}
                        	}
                     	    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "20200525", errorMsg);
                            return true;
                     	}
            			 
                     }
                }
            	
            }
        }

        return false;
    }
}
