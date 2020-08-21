package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.ProductData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.TroopMemberInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.requestdata.ChangeProductReqData;

/**
 * 校验鉴权为短信验证码办理的套餐
 * @author Administrator
 *
 */
public class CheckSMSCodeProductAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		ChangeProductReqData changeProductRD=(ChangeProductReqData)btd.getRD();
		UcaData uca=changeProductRD.getUca();
		//短信验证码鉴权方式
		if("7".equals(changeProductRD.getCheckMode())){
			ProductData pd=changeProductRD.getNewMainProduct();
			if(pd!=null){
				String newProductId=pd.getProductId();
				//变更的产品是否在配置里
				//IDataset productList=CommparaInfoQry.getCommparaAllCol("CSM", "8989", newProductId, "0898");
				IDataset productList=CommparaInfoQry.getCommparaInfoByCode2("CSM", "8989", newProductId,"P", "0898");
				if(productList==null||productList.size()==0){
					CSAppException.apperr(CrmCommException.CRM_COMM_103,"该套餐不在业务受理范围");
				}
				//是否目标客户
				/*IDataset userList=TroopMemberInfoQry.queryCountByTroopIdAndUserId("2018061300000000", uca.getUserId());
				if(userList==null||userList.size()==0){
					CSAppException.apperr(CrmCommException.CRM_COMM_103,"非目标客户");
				}*/
			}else{
				//没有变更产品的时候，判断新增的优惠是否在配置内
				List<DiscntTradeData> discntTrade=btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
				if(discntTrade!=null&&discntTrade.size()>0){
					for(DiscntTradeData discnt:discntTrade){
						if(BofConst.MODIFY_TAG_ADD.equals(discnt.getModifyTag())){
							IDataset discntList=CommparaInfoQry.getCommparaInfoByCode2("CSM", "8989", discnt.getElementId(),"D", "0898");
							if(discntList==null||discntList.size()==0){
								CSAppException.apperr(CrmCommException.CRM_COMM_103,"该优惠["+discnt.getElementId()+"]不在短信认证码业务受理范围");
							}
						}
					}
				}
			}
		}
		
	}

}
