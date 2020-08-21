
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

/**
 * 
 * 
 * @办理B套餐，要求该用户证件名下的用户必须办理A套餐
 *
 */
public class DependOtherDiscntAction implements ITradeAction
{
	@SuppressWarnings("unchecked")
	@Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
		
        List<DiscntTradeData> lsDiscntTrade = btd.get("TF_B_TRADE_DISCNT");
        List<ProductTradeData> lsProTrade = btd.get("TF_B_TRADE_PRODUCT");
        if(CollectionUtils.isEmpty(lsDiscntTrade) && CollectionUtils.isEmpty(lsProTrade))
        {
        	return;
        }
        String psptTypeCode = btd.getRD().getUca().getCustomer().getPsptTypeCode();
        IDataset config ;
        boolean flag = false;
        //遍历用户的优惠信息
        for(DiscntTradeData data:lsDiscntTrade){
        	if("0".equals(data.getModifyTag())){
        		String discntCode = data.getDiscntCode();
        		config = CommparaInfoQry.getContainCode1("CSM","8891","1",psptTypeCode,discntCode,"0898");
        		if(CollectionUtils.isEmpty(config))
        		{
        			continue;
        		}
        		String dependDiscnts = config.first().getString("PARA_CODE3");
        		if(com.ailk.org.apache.commons.lang3.StringUtils.isBlank(dependDiscnts)){
        			continue;
        		}
        		String psptId = btd.getRD().getUca().getCustomer().getPsptId();
        		IDataset lsCustId = CustomerInfoQry.getCustIdByPspt(psptId);
        		for (int i = 0; i < lsCustId.size(); i++) {
					
        			String custId = ((IData)lsCustId.get(i)).getString("CUST_ID");
        			flag = DiscntInfoQry.qryHaveSomeDiscnts(dependDiscnts,custId);
        			if(flag){
        				break;
        			}
				}
        		if(!flag){
        			CSAppException.apperr(CrmCommException.CRM_COMM_888, config.first().getString("PARAM_NAME"));
        		}
        		
        	}else if("1".equals(data.getModifyTag())){
        		flag = false;
        		String discntCode = data.getDiscntCode();
        		config = CommparaInfoQry.getContainCode3("CSM","8891",psptTypeCode,discntCode,"0898");
        		if(CollectionUtils.isEmpty(config))
        		{
        			continue;
        		}
        		String psptId = btd.getRD().getUca().getCustomer().getPsptId();
        		IDataset lsCustId = CustomerInfoQry.getCustIdByPspt(psptId);
        		for (int i = 0; i < lsCustId.size(); i++) {
					
        			String custId = ((IData)lsCustId.get(i)).getString("CUST_ID");
        			flag = DiscntInfoQry.qryHaveSomeDiscnts(config.first().getString("PARA_CODE2"),custId);
        			if(flag){
        				break;
        			}
        		}
        		if(flag){
        			CSAppException.apperr(CrmCommException.CRM_COMM_888, 
        					"办理"+config.first().getString("PARA_CODE2")+"套餐的用户，不能取消指定套餐");
        		}
        	}
        		
        }
        //遍历用户的产品信息
        for(ProductTradeData data:lsProTrade){
        	if("0".equals(data.getModifyTag())){
        		flag = false;
        		String productId = data.getProductId();
        		config = CommparaInfoQry.getContainCode1("CSM","8891","2",psptTypeCode,productId,"0898");
        		System.out.println("config:"+config.toString());
        		if(CollectionUtils.isEmpty(config))
        		{
        			continue;
        		}
        		String dependDiscnts = config.first().getString("PARA_CODE3");
        		if(com.ailk.org.apache.commons.lang3.StringUtils.isBlank(dependDiscnts)){
        			continue;
        		}
        		String psptId = btd.getRD().getUca().getCustomer().getPsptId();
        		IDataset lsCustId = CustomerInfoQry.getCustIdByPspt(psptId);
        		
        		System.out.println("lsCustId:"+lsCustId.toString());
        		if(IDataUtil.isEmpty(lsCustId)){
    				CSAppException.apperr(CrmCommException.CRM_COMM_888,  config.first().getString("PARAM_NAME"));
    			}
        		String dependProducts = config.first().getString("PARA_CODE3");
        		String[] productArry = dependProducts.split("\\|");
        		for (int i = 0; i < lsCustId.size(); i++) {
        			IDataset lsUser = UserInfoQry.getUserInfoByCusts(((IData)lsCustId.get(i)).getString("CUST_ID"));
        			for(int k=0;k<lsUser.size();k++){
        				for(int j=0;j<productArry.length;j++){
        					IDataset dataset = ProductInfoQry.qryProductByUserIdAndProId(lsUser.getData(k).getString("USER_ID"),productArry[j]);
        					if(IDataUtil.isNotEmpty(dataset)){
        						flag = true;
        						break;
        					}
        				}
        			}
        		}
        		if(!flag){
        			CSAppException.apperr(CrmCommException.CRM_COMM_888,  config.first().getString("PARAM_NAME"));
        		}
        	}else if("1".equals(data.getModifyTag())){
        		flag = false;
        		String productId = data.getProductId();
        		config = CommparaInfoQry.getContainCode3("CSM","8891",psptTypeCode,productId,"0898");
        		if(CollectionUtils.isEmpty(config))
        		{
        			continue;
        		}
        		String psptId = btd.getRD().getUca().getCustomer().getPsptId();
        		IDataset lsCustId = CustomerInfoQry.getCustIdByPspt(psptId);
        		
        		String dependProducts = config.first().getString("PARA_CODE2");
        		String[] productArry = dependProducts.split("\\|");
        		for (int i = 0; i < lsCustId.size(); i++) {
        			IDataset lsUser = UserInfoQry.getUserInfoByCusts(((IData)lsCustId.get(i)).getString("CUST_ID"));
        			for(int k=0;k<lsUser.size();k++){
        				for(int j=0;j<productArry.length;j++){
        					IDataset dataset = ProductInfoQry.qryProductByUserIdAndProId(lsUser.getData(k).getString("USER_ID"),productArry[j]);
        					if(IDataUtil.isNotEmpty(dataset)){
        						flag = true;
        						if(flag){
        							break;
        						}
        					}
        				}
        			}
        		}
        		if(flag){
        			CSAppException.apperr(CrmCommException.CRM_COMM_888, 
        					"办理"+config.first().getString("PARA_CODE2")+"产品的用户，不能取消指定产品");
        		}
        	}
        		
        }
    }
}
