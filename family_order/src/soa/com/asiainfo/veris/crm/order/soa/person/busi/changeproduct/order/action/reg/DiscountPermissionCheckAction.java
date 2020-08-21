
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import oracle.net.aso.e;

import com.ailk.bizcommon.priv.StaffPrivUtil;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.util.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
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
public class DiscountPermissionCheckAction implements ITradeAction
{
	@SuppressWarnings("unchecked")
	@Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        List<AttrTradeData> lsAttrTrade = btd.get("TF_B_TRADE_ATTR");
        
        String staffId = CSBizBean.getVisit().getStaffId();
        if("SUPERUSR".equals(staffId)){
        	staffId = "HNSJ0234";
        }
		if(CollectionUtils.isEmpty(lsAttrTrade))
		{
			return;
		}
		
		for(AttrTradeData data:lsAttrTrade){
			String elementId = data.getElementId();
			String modifyTag = data.getModifyTag();
			//查询是否为配置的指定的优惠套餐
			if("0".equals(modifyTag)){
				IDataset discntConfig = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","8407","1",elementId);
				if(IDataUtil.isNotEmpty(discntConfig)){
					String valueString = data.getAttrValue();
					if(!StringUtils.isInteger(valueString)){
						CSAppException.apperr(CrmCommException.CRM_COMM_888, elementId + "属性值必须为整数");
					}
					int flowValue = Integer.parseInt(valueString);
					int minValue = 0;
					int maxValue = 100;
					if(flowValue<=minValue || flowValue>maxValue){
						CSAppException.apperr(CrmCommException.CRM_COMM_888, elementId + "属性值必须是大于"+minValue+"且小于或等于"+maxValue+"的整数");
					}
					if(flowValue >= 70){
						boolean havePrivH = StaffPrivUtil.isFuncDataPriv(staffId, "PRIV_DIS_THIRTYH");
						if(!havePrivH){
							CSAppException.apperr(CrmCommException.CRM_COMM_888,"当前工号不具有七折及以上折扣的权限");
						}						
					}else 
					{
						boolean havePrivL = StaffPrivUtil.isFuncDataPriv(staffId, "PRIV_DIS_THIRTYL");
						if(!havePrivL){
							CSAppException.apperr(CrmCommException.CRM_COMM_888,"当前工号不具有七折以下折扣的权限");
						}							
					}
					List<DiscntTradeData> lsDiscntTrade = btd.get("TF_B_TRADE_DISCNT");
					for(DiscntTradeData discntData:lsDiscntTrade){
						if(discntData.getDiscntCode().equals(elementId)){
							discntData.setRemark("套餐折扣为 "+flowValue+"%");
						}
					}
				}				
			}			
		}  
    }
	
	public static void main(String []args){
		try{
			String yes = "1";
			if(StaffPrivUtil.isFuncDataPriv("HNSJ0234", "PRIV_DIS_THI")){
				yes = "2";
			}			 
			System.out.print(yes);
		}
		catch(Exception e){
			
		}
	}
}
