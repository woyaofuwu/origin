
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import oracle.net.aso.e;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.util.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 
 * 
 * @author 
 *
 */
public class NoLimitFlowInputDiscntAction implements ITradeAction
{
	@SuppressWarnings("unchecked")
	@Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        List<AttrTradeData> lsAttrTrade = btd.get("TF_B_TRADE_ATTR");
		if(CollectionUtils.isEmpty(lsAttrTrade))
		{
			return;
		}
		long kRate = 1024l*1024l*1024l;
		for(AttrTradeData data:lsAttrTrade){
			String elementId = data.getElementId();
			String modifyTag = data.getModifyTag();
			//查询是否为配置的指定的优惠套餐
			if("0".equals(modifyTag)){
				IDataset discntConfig = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","8890","1",elementId);
				if(IDataUtil.isNotEmpty(discntConfig)){
					String valueString = data.getAttrValue();
					if(!StringUtils.isInteger(valueString)){
						CSAppException.apperr(CrmCommException.CRM_COMM_888, elementId + "属性值必须为整数");
					}
					int flowValue = Integer.parseInt(valueString);
					int minValue = Integer.parseInt(discntConfig.getData(0).getString("PARA_CODE2"));
					int maxValue = Integer.parseInt(discntConfig.getData(0).getString("PARA_CODE3"));
					if(flowValue<minValue || flowValue>maxValue){
						CSAppException.apperr(CrmCommException.CRM_COMM_888, elementId + "属性值必须是在"+minValue+"到"+maxValue+"之间的整数");
					}
					long acctValue = flowValue*kRate;
					data.setAttrValue(acctValue+"");
					List<DiscntTradeData> lsDiscntTrade = btd.get("TF_B_TRADE_DISCNT");
					for(DiscntTradeData discntData:lsDiscntTrade){
						if(discntData.getDiscntCode().equals(elementId)){
							discntData.setRemark("每月获赠国内流量 "+valueString+" G");
						}
					}
				}
				
			}
			
			
		}
      
    }
}
