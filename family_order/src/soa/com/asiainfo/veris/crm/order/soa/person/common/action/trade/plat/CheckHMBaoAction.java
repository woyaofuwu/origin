package com.asiainfo.veris.crm.order.soa.person.common.action.trade.plat;

import java.util.List;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
/**
 * 办理和目尝鲜活动（约定使用7天回看云储存功能一年包）的客户，限定在不能退订云存储功能。
 * @author Administrator
 *
 */
public class CheckHMBaoAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		String userId=btd.getRD().getUca().getUser().getUserId();
		List<PlatSvcTradeData> pstds = btd.getTradeDatas(TradeTableEnum.TRADE_PLATSVC);
		
		IDataset productIds=CommparaInfoQry.getCommparaAllColByParser("CSM", "8987", null, "0898");
		
		if(pstds!=null&&pstds.size()>0){
			for(PlatSvcTradeData pstData:pstds){
				//退订
				if("07".equals(pstData.getOperCode())){
					IDataset saleActives=UserSaleActiveInfoQry.queryUserSaleActiveByTag(userId);
					if(IDataUtil.isNotEmpty(saleActives)){
						for(int i=0;i<saleActives.size();i++){
							IData saleData=saleActives.getData(i);
							if(IDataUtil.isNotEmpty(productIds)){
								for(int j=0;j<productIds.size();j++){
									if(saleData.getString("PRODUCT_ID","").equals(productIds.getData(j).get("PARAM_CODE"))
											&&saleData.getString("PACKAGE_ID","").equals(productIds.getData(j).get("PARA_CODE1"))
											&&pstData.getElementId().equals(productIds.getData(j).get("PARA_CODE2"))){
										CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户办理了和目尝鲜活动["+saleData.getString("PRODUCT_ID")+"]包["+saleData.getString("PACKAGE_ID")+"],不允许退订["+pstData.getElementId()+"]!");
									}
								}
							}
						}
					}
				}
			}
		}
	}

}
