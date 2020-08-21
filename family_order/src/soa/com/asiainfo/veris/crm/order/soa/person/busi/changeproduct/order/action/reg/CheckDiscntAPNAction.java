
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 
 * 
 * @author 
 *
 */
public class CheckDiscntAPNAction implements ITradeAction
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
        
        IData idWlwApp = new DataMap();
        List<DiscntTradeData> lsDiscnt = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
        if( CollectionUtils.isNotEmpty(lsDiscnt) )
        {
        	int size = lsDiscnt.size();
            for (int i = 0; i < size; i++)
            {
            	DiscntTradeData dtDiscnt = lsDiscnt.get(i);
            	String strElementId = dtDiscnt.getElementId();
            	IDataset idsDiscnt = CommparaInfoQry.getCommparaByCodeCode1("CSM", "1555", "WLWAPNNAME", strElementId);
            	if(IDataUtil.isNotEmpty(idsDiscnt))
            	{
            		String strNum = idWlwApp.getString(strElementId, "0");
            		int nNum = Integer.valueOf(strNum) + 1;
            		idWlwApp.put(strElementId, nNum);
            		boolean bIsExit = true;
            		for (int j = 0; j < idsDiscnt.size(); j++) 
            		{
            			IData idDiscnt = idsDiscnt.getData(j);
            			String strComSvcID = idDiscnt.getString("PARA_CODE2", "");
            			List<SvcTradeData> lsSvc = uca.getUserSvcBySvcId(strComSvcID);
            			if(CollectionUtils.isNotEmpty(lsSvc))
                    	{
            				bIsExit = false;
            				break;
                    	}
            			
					}
            		if(bIsExit)
        			{
        				String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(strElementId);
                        String discntMsg = strElementId + "|" + discntName + ",";
        				CSAppException.apperr(CrmCommException.CRM_COMM_103, discntMsg + " 没有找到对应的服务。");
        			}
            	}
            }
            if(IDataUtil.isNotEmpty(idWlwApp))
        	{
        		String []strsName = idWlwApp.getNames();
        		for (int i = 0; i < strsName.length; i++) 
        		{
        			String strKey = strsName[i];
        			String strValue = idWlwApp.getString(strKey, "0");
        			int nValue = Integer.valueOf(strValue);
        			if(nValue > 1)
        			{
        				String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(strKey);
                        String discntMsg = strKey + "|" + discntName + "！";
        				CSAppException.apperr(CrmCommException.CRM_COMM_103, discntMsg + " 只能订购一个产品");
        			}
        		}
        	}
    	} 
    }
}
