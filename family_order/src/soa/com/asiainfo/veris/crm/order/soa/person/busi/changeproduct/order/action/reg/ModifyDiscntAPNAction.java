
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 
 * 
 * @author 
 *
 */
public class ModifyDiscntAPNAction implements ITradeAction
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
        if(CollectionUtils.isNotEmpty(lsDiscnt))
        {
        	int size = lsDiscnt.size();
            for (int i = 0; i < size; i++)
            {
            	DiscntTradeData dtDiscnt = lsDiscnt.get(i);
            	String strElementId = dtDiscnt.getElementId();
            	String strInstID = dtDiscnt.getInstId();
            	IDataset idsDiscnt = CommparaInfoQry.getCommparaByCodeCode1("CSM", "1555", "WLWAPNNAME", strElementId);
            	if(IDataUtil.isNotEmpty(idsDiscnt))
            	{
            		String strNum = idWlwApp.getString(strElementId, "0");
            		int nNum = Integer.valueOf(strNum) + 1;
            		idWlwApp.put(strElementId, nNum);
            		boolean bIsBreak = false;
            		for (int j = 0; j < idsDiscnt.size(); j++) 
            		{
            			IData idDiscnt = idsDiscnt.getData(j);
            			String strComSvcID = idDiscnt.getString("PARA_CODE2", "");
            			List<SvcTradeData> lsSvc = uca.getUserSvcBySvcId(strComSvcID);
            			if(CollectionUtils.isNotEmpty(lsSvc))
                    	{
            				for (int k = 0; k < lsSvc.size(); k++) 
                			{
            					SvcTradeData dtSvc = lsSvc.get(k);
            					String strSvcID = dtSvc.getElementId();
            					String strModifyTag = dtSvc.getModifyTag();
            					if(strSvcID.equals(strComSvcID) && "1".equals(strModifyTag))
            					{
            						String strSysTime = SysDateMgr.getSysTime();
            						dtDiscnt.setEndDate(strSysTime);
            						dtDiscnt.setRemark(strSvcID + "服务与" + strElementId + "优惠同时取消");
            						//取消属性表
            						List<AttrTradeData> lsAttr = dtDiscnt.getAttrTradeDatas();
            						for (int p = 0; p < lsAttr.size(); p++) 
            						{
            							AttrTradeData tdAttr = lsAttr.get(p);
            							tdAttr.setEndDate(strSysTime);
									}
            						//取消OfferRel表
            						List<OfferRelTradeData> lsOfferRel = btd.getTradeDatas(TradeTableEnum.TRADE_OFFER_REL);
            				        if(CollectionUtils.isNotEmpty(lsOfferRel))
            				        {
            				        	for (int p = 0; p < lsOfferRel.size(); p++) 
            				        	{
            				        		OfferRelTradeData tdOfferRel = lsOfferRel.get(p);
            				        		if(strInstID.equals(tdOfferRel.getRelOfferInsId()))
            				        		{
            				        			tdOfferRel.setEndDate(strSysTime);
            				        		}
										}
            				        }
            						bIsBreak = true;
            						break;
            					}
    						} 
                    	}
            			if(bIsBreak)
            			{
            				break;
            			}
					}
            	}
            }
    	} 
    }
}
