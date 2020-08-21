
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.TradeUtils;

/**
 * 
 * 
 * 一个证件名下的用户只能办理（5212 ONLY_ONE ）配置中的产品一次
 *
 */
public class OnlyOneDiscntAction implements ITradeAction
{
	@SuppressWarnings("unchecked")
	@Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
		
		String tradeTypeCode =btd.getTradeTypeCode();
		if(!"10".equals(tradeTypeCode)){
			
			String isRealName = btd.getRD().getUca().getCustomer().getIsRealName();
			String identityId  = CustomerInfoQry.getCustInfoPsptBySn(btd.getRD().getUca().getSerialNumber()).first().getString("PSPT_ID");
			if(("111111111111111111".equals(identityId)||"0".equals(isRealName)) && !"60".equals(tradeTypeCode)){
				return;
			}
			
		}
		
		IDataset config = CommparaInfoQry.getCommNetInfo("CSM", "5212", "ONLY_ONE");
		
		if(IDataUtil.isNotEmpty(config))
		{
			String onlyProduct = config.first().getString("PARA_CODE1");
			String onlyProductName = config.first().getString("PARA_CODE2");
			String[] productArry = onlyProduct.split("\\|");
			
			
			if(!tradeTypeCode.equals("60")&&!tradeTypeCode.equals("100")){
				
				List<ProductTradeData> lsProductTrade = btd.get("TF_B_TRADE_PRODUCT");
				if(CollectionUtils.isEmpty(lsProductTrade))
				{
					return;
				}
				for(ProductTradeData data:lsProductTrade){
					if("0".equals(data.getModifyTag())){
						String productId = data.getProductId();
						if(com.ailk.org.apache.commons.lang3.StringUtils.isBlank(onlyProduct)){
							return;
						}
						if(!onlyProduct.contains(productId)){
							return;
						}
						String psptId = btd.getRD().getUca().getCustomer().getPsptId();
						IDataset lsCustId = CustomerInfoQry.getCustIdByPspt(psptId);
						for(Object data1:lsCustId){
							String custId = ((IData)data1).getString("CUST_ID");
							IDataset lsUser = UserInfoQry.getUserInfoByCusts(custId);
							for(Object temp:lsUser){
								IData tempData = (IData)temp;
									IDataset dataset = ProductInfoQry.qryProductByUserIdAndProId(tempData.getString("USER_ID"),productId);
									if(IDataUtil.isNotEmpty(dataset)){
										CSAppException.apperr(CrmCommException.CRM_COMM_888, "该证件下的其他用户办理了【"+productId+",不能重复办理"+productId);
									}
								
							}
						}
						
						
					}
					
				}
			}else{
				if(TradeUtils.getChangePsptId(btd)){
					for(int i=0;i<productArry.length;i++){
						IDataset dataset = ProductInfoQry.qryProductByUserIdAndProId(btd.getRD().getUca().getUserId(),productArry[i]);
						if(IDataUtil.isNotEmpty(dataset)){//当前用户如果办理了配置中的产品才继续判断
							IDataset lsUser = UserInfoQry.querySameDocumentUserIds(TradeUtils.getChangeNewPsptType(btd),TradeUtils.getChangeNewPsptId(btd));
							for(Object temp:lsUser){
									
									IData tempData = (IData)temp;
									IDataset productDataset = ProductInfoQry.qryProductByUserIdAndProId(tempData.getString("USER_ID"),productArry[i]);
									if(IDataUtil.isNotEmpty(productDataset)){
										CSAppException.apperr(CrmCommException.CRM_COMM_888, 
												"该用户办理了【"+onlyProductName.split("\\|")[i]+"】,新的证件下有用户办理了【"+productArry[i]+"】,不能变更为新的证件号码！");
									}
							}
						}
					}
				}
			}
		}
		
		
		if("60".equals(tradeTypeCode))
		{
			IDataset confignew = CommparaInfoQry.getCommNetInfo("CSM", "8891", "2");
			if(IDataUtil.isNotEmpty(confignew))
			{
				String pspType = confignew.first().getString("PARA_CODE1");
				String olnyPID = confignew.first().getString("PARA_CODE2");
				String depandPID = confignew.first().getString("PARA_CODE3");
				String depandNAME = confignew.first().getString("PARAM_NAME");
				String[] depandPIDArry = depandPID.split("\\|");

				List<CustomerTradeData> lsCustomerTrade = btd.get("TF_B_TRADE_CUSTOMER");
				String  tradePsptType="";
				if(CollectionUtils.isNotEmpty(lsCustomerTrade)){
					//台帐中的证件类型
					for(int i=0,size=lsCustomerTrade.size();i<size;i++){
						if(lsCustomerTrade.get(i).getModifyTag().equals(BofConst.MODIFY_TAG_UPD)||lsCustomerTrade.get(i).getModifyTag().equals(BofConst.MODIFY_TAG_ADD)){
							tradePsptType=lsCustomerTrade.get(i).getPsptTypeCode();
							break;
						}
					}
				}
				
				if(TradeUtils.getChangePsptId(btd)){
						IDataset dataset = ProductInfoQry.qryProductByUserIdAndProId(btd.getRD().getUca().getUserId(),olnyPID);
						
						
						if(IDataUtil.isNotEmpty(dataset)){//当前用户如果办理了配置中的产品才继续判断
							
							if(pspType.contains(tradePsptType))
							{
								IDataset lsUser = UserInfoQry.querySameDocumentUserIdsByPsptId(TradeUtils.getChangeNewPsptId(btd));
								
								for(Object temp:lsUser){
										
										IData tempData = (IData)temp;
										
										for(int j=0;j<depandPIDArry.length;j++)
										{
											IDataset productDataset = ProductInfoQry.qryProductByUserIdAndProId(tempData.getString("USER_ID"),depandPIDArry[j]);
											if(IDataUtil.isNotEmpty(productDataset)){
												return;
											}
										}	
								}
								
								CSAppException.apperr(CrmCommException.CRM_COMM_888, depandNAME);
							}else
							{
								CSAppException.apperr(CrmCommException.CRM_COMM_888, "你的证件不符合该产品的办理要求！");
							}
							
							
							
							
						}
					
				}
			}
		}

    }
}
