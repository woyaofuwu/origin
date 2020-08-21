package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;

public class Check20SvcProductLimit extends BreBase implements IBREScript{

	private static Logger logger = Logger.getLogger(Check20SvcProductLimit.class);

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {   
        	IData reqData = databus.getData("REQDATA");// 请求的数据

            if (IDataUtil.isNotEmpty(reqData))
            {	       

                String userProductId = databus.getString("PRODUCT_ID");// 老产品
                String newProductId = reqData.getString("NEW_PRODUCT_ID");// 新产品
                
                if (StringUtils.isNotBlank(newProductId) && !userProductId.equals(newProductId))//主产品变更
                {
                    UcaData uca = (UcaData) databus.get("UCADATA");

                    IDataset elementList = PkgElemInfoQry.getElementByPIdElemId(newProductId, "S", "20", "0898") ;
                    
                    if(IDataUtil.isNotEmpty(elementList) && elementList.size() > 0)
                    {
                    	IData elemet = elementList.getData(0);
                        if("1".equals(elemet.getString("FORCE_TAG"))) //新产品彩铃是必选，如果存在平台业务的彩铃了，则不给予办理产品变更。要求先取消平台业务彩铃才能办理。
                        {
                        	List<SvcTradeData> svcTrade = new ArrayList<SvcTradeData>();
                        	svcTrade = uca.getUserSvcBySvcId("20");
                        	int size = svcTrade.size();
                    		for (int i = 0; i < size; i++)
                    		{
                    			SvcTradeData std = svcTrade.get(i);
                    			if ("50000000".equals(std.getProductId()))
                    			{
                    				//String errorMsg = "该用户存在平台业务的彩铃服务,变更后的主套餐【"+UProductInfoQry.getProductNameByProductId(newProductId)+"】也含有必选的彩铃，这个冲突了，请先取消平台彩铃后办理该产品!";
                                    
                                    //BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "161025001", errorMsg);

                                    //return true;
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
