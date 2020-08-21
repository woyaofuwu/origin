package com.asiainfo.veris.crm.order.soa.person.rule.run.saleactive;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;

/**    
 * 
 * @ClassName: CheckSaleActiveSvcLimit.java
 * @Description: 用户在未终止返销指定活动下，不能终止/返销对应活动时【TradeCheckBefore】

 * @author: lizj
 * @date: 20200526

 *-------------------------------------------------------
 */
public class CheckSaleActiveCancelLimit extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        String rsrvStr1 = databus.getString("RSRV_STR1","");//活动编码
        String rsrvStr2 = databus.getString("RSRV_STR2","");//包编码
        System.out.println("进入CheckSaleActiveCancelLimit"+databus.getData("REQDATA"));
        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {
        	String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        	IDataset commparaDs = BreQryForCommparaOrTag.getCommpara("CSM", 2510, strTradeTypeCode,rsrvStr1,rsrvStr2,CSBizBean.getUserEparchyCode());
        	String userId = databus.getString("USER_ID");
		    IDataset saleActiveInfos = SaleActiveInfoQry.getUserSaleActiveInfo(userId);

        	if (IDataUtil.isNotEmpty(commparaDs))
            {
        		 for (int k = 0; k < commparaDs.size(); k++)
                 {
        			 IData commpara = commparaDs.getData(k);
        			 String paraCode4 = commpara.getString("PARA_CODE4");//活动编码
        			 String paraCode5 = commpara.getString("PARA_CODE5");//包编码
        			 
        			 for(int i = 0; i < saleActiveInfos.size(); i++){
        				 IData saleActive = saleActiveInfos.getData(i);
        				 String saleProductId = saleActive.getString("PRODUCT_ID");
                         String salePackageId = saleActive.getString("PACKAGE_ID");
                         String salePackageName = saleActive.getString("PACKAGE_NAME");
                         
                         String errorMsg ="";
                         if(saleProductId.equals(paraCode4)){
                         	if(salePackageId.equals(paraCode5)){
//                             		 IData info = UpcCall.queryOfferByOfferId(BofConst.ELEMENT_TYPE_CODE_PACKAGE,paraCode5,null);
//                                   	if(IDataUtil.isNotEmpty(info)){
//                                   		errorMsg = "终止活动前需要先办理终止"+info.getString("OFFER_NAME")+"活动！";
//                                   	}
                               	
                               	errorMsg = "终止活动前需要先办理终止"+salePackageName+"活动！";
                          		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "20200530", errorMsg);
                          		return true;
                         	}
                         }
        			 }
                     	 
                 }
            }
                
        }

        return false;
    }
}
