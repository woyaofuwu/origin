
package com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo.order.action.trade;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
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
import com.asiainfo.veris.crm.order.soa.person.common.util.TradeUtils;

/**
 * 如果该用户办理了8891中PARA_CODE2的配置的套餐，则不允许变更证件
 * @author tanzheng
 *
 */
public class NotAllowChangePsptAction implements ITradeAction
{
	private static final Logger log = Logger.getLogger(NotAllowChangePsptAction.class);
    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
    	String tradeTypeCode =btd.getTradeTypeCode();
		if(!"10".equals(tradeTypeCode)){
			
			String isRealName = btd.getRD().getUca().getCustomer().getIsRealName();
			String identityId  = CustomerInfoQry.getCustInfoPsptBySn(btd.getRD().getUca().getSerialNumber()).first().getString("PSPT_ID");
			if("111111111111111111".equals(identityId)||"0".equals(isRealName)){
				return;
			}
			
		}
    	if(TradeUtils.getChangePsptId(btd)){
    		//有变化则查看用户使用有办理指定套餐
    		List<DiscntTradeData> lsDiscnt = btd.getRD().getUca().getUserDiscnts();
    		for(DiscntTradeData data:lsDiscnt){
    			IDataset config = CommparaInfoQry.getContainCode1("CSM","8891","1",btd.getRD().getUca().getCustomer().getPsptTypeCode(),data.getDiscntCode(),"0898");
    			if(IDataUtil.isNotEmpty(config)){
    				//如果用户办理了D套餐，要做变更，变更的名下必须有A套餐
    				boolean flag = false;
    				String  dependDiscnt = config.first().getString("PARA_CODE3");
    				String psptId = btd.getRD().getUca().getCustomer().getPsptId();
    				IDataset lsCustId = CustomerInfoQry.getCustIdByPspt(psptId);
            		for (int i = 0; i < lsCustId.size(); i++) {
            			flag = DiscntInfoQry.qryHaveSomeDiscnts(dependDiscnt,((IData)lsCustId.get(i)).getString("CUST_ID"));
            			if(flag){
            				break;
            			}
            		}
    				if(!flag){
    					
    					CSAppException.apperr(CrmCommException.CRM_COMM_888, "办理"+data.getDiscntCode()+"套餐的用户办理证件变更,变更证件的名下必须有用户办理指定套餐");
    				}
    			}
    			//如果办理了依赖的优惠，需要判断同证件下是否办理了限制优惠。如果办理了则不让变更身份证信息
    			IDataset config2 = CommparaInfoQry.getContainCode3("CSM","8891",btd.getRD().getUca().getCustomer().getPsptTypeCode(),"|"+data.getDiscntCode()+"|","0898");
    			if(IDataUtil.isNotEmpty(config2)){
    				String discntCode = config2.getData(0).getString("PARA_CODE2");
    				String psptId = btd.getRD().getUca().getCustomer().getPsptId();
            		IDataset lsCustId = CustomerInfoQry.getCustIdByPspt(psptId);
            		for (int i = 0; i < lsCustId.size(); i++) {
    					
            			String custId = ((IData)lsCustId.get(i)).getString("CUST_ID");
            			if( DiscntInfoQry.qryHaveSomeDiscnts(discntCode,custId)){
            				CSAppException.apperr(CrmCommException.CRM_COMM_888, "该证件下的用户办理了"+discntCode+"套餐的用户不能办理证件的变更");
            			}
            		}
    			}
    		}
    		
    		List<ProductTradeData> lsProduct = btd.getRD().getUca().getUserProducts();
    		for(ProductTradeData data:lsProduct){
    			IDataset config = CommparaInfoQry.getContainCode1("CSM","8891","2",btd.getRD().getUca().getCustomer().getPsptTypeCode(),data.getProductId(),"0898");
    			if(IDataUtil.isNotEmpty(config)){
    				//如果用户办理了D产品，要做变更，变更的名下必须有A产品
    				boolean flag = false;
    				String  dependProduct = config.first().getString("PARA_CODE3");
    				String[] proArry = dependProduct.split("\\|");
    				String psptId = btd.getRD().getUca().getCustomer().getPsptId();
    				IDataset lsCustId = CustomerInfoQry.getCustIdByPspt(psptId);
    				mark:
    				for (int i = 0; i < lsCustId.size(); i++) {
            			IDataset lsUser = UserInfoQry.getUserInfoByCusts(((IData)lsCustId.get(i)).getString("CUST_ID"));
            			for(int k=0;k<lsUser.size();k++){
            				for(String proId:proArry){
            					IDataset dataset = ProductInfoQry.qryProductByUserIdAndProId(lsUser.getData(k).getString("USER_ID"),proId);
            					if(IDataUtil.isNotEmpty(dataset)){
            						flag = true;
            						break mark;
            					}
            				}
            			}
            		}
    				if(!flag){
    					CSAppException.apperr(CrmCommException.CRM_COMM_888, "办理"+data.getProductId()+"产品的用户办理证件变更,,变更证件的名下必须有用户办理指定产品");
    				}
    				
    			}
    			
    			//如果办理了依赖的优惠，需要判断同证件下是否办理了限制优惠。如果办理了则不让变更身份证信息
    			IDataset config2 = CommparaInfoQry.getContainCode3("CSM","8891",btd.getRD().getUca().getCustomer().getPsptTypeCode(),"|"+data.getProductId()+"|","0898");
    			if(IDataUtil.isNotEmpty(config2)){
    				String productId = config2.getData(0).getString("PARA_CODE2");
    				String psptId = CustomerInfoQry.getCustInfoPsptBySn(btd.getRD().getUca().getSerialNumber()).first().getString("PSPT_ID");
            		IDataset lsCustId = CustomerInfoQry.getCustIdByPspt(psptId);
            		for (int i = 0; i < lsCustId.size(); i++) {
            			IDataset lsUser = UserInfoQry.getUserInfoByCusts(((IData)lsCustId.get(i)).getString("CUST_ID"));
            			for(int k=0;k<lsUser.size();k++){
            				IDataset dataset = ProductInfoQry.qryProductByUserIdAndProId(lsUser.getData(k).getString("USER_ID"),productId);
            				if(IDataUtil.isNotEmpty(dataset)){
            					CSAppException.apperr(CrmCommException.CRM_COMM_888, "该证件下的用户办理了"+productId+"产品的用户不能办理证件的变更");
            				}
            			}
            		}
    			}
    		}
    		
    	}
		
		
	
    } 
}
