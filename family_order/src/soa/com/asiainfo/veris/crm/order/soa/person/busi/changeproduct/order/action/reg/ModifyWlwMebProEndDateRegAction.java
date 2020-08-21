
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

/**
 * 
 * 集团有流量池产品时,修改物联网成员产品的结束时间
 * @author 
 *
 */
public class ModifyWlwMebProEndDateRegAction implements ITradeAction
{
	
	@SuppressWarnings("unchecked")
	@Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        UcaData uca = btd.getRD().getUca();
        if(!"PWLW".equals(uca.getBrandCode()))
		{
            return;
        }
        
        String userId = uca.getUserId();
               
        List<ProductTradeData> productTrades = btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);
        ProductTradeData modifyProductData = null;
        boolean modifyTag = false;
        if (productTrades != null && productTrades.size() > 0) 
        {
            for (int i = 0; i < productTrades.size(); i++) 
            {
                ProductTradeData productData = productTrades.get(i);
                String brandCode = productData.getBrandCode();
                //成员产品
                if(StringUtils.isNotBlank(brandCode) && StringUtils.equals("WLWG", brandCode))
                {
                	modifyProductData = productTrades.get(i);
                	modifyTag = true;
                	break;
                }
            }
        }
        
        if(modifyTag && modifyProductData != null)
        {
    		IDataset uuInfos = RelaUUInfoQry.getRelaByUserIdbAndRelaTypeCode(userId, "9A");
    		if(IDataUtil.isNotEmpty(uuInfos))
    		{
    			IData userData = uuInfos.getData(0);
    			String userIdA = userData.getString("USER_ID_A","");
    			IDataset svcInfos =  UserSvcInfoQry.queryGrpWlwShareUserSvcByUserIdA(userIdA);
    			if(IDataUtil.isNotEmpty(svcInfos))
    			{
    				//修改时间至月底
    				modifyProductData.setEndDate( SysDateMgr.getLastDateThisMonth());
    			}
    		}
        }
      
    }
}
