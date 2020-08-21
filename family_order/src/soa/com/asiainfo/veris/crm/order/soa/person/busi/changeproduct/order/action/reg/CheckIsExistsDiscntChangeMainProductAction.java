package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class CheckIsExistsDiscntChangeMainProductAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		/**
		 * step.1 查询当前台账产品数据
		 */
		List<ProductTradeData> changeProducts=btd.get("TF_B_TRADE_PRODUCT"); 
		
		String newProductId=null;
    	String oldProductId=null;
    	List<DiscntTradeData> discntTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
		for(int i=0,size=changeProducts.size();i<size;i++){
			ProductTradeData data=changeProducts.get(i);
			if(data.getModifyTag().equals(BofConst.MODIFY_TAG_ADD)){
				newProductId=data.getProductId();
    			if(data.getOldProductId()!=null&&!data.getOldProductId().equals("")){
    				oldProductId=data.getOldProductId();
    			}
    			break;
			}
		}

		/**
		 * step.2 查询commparaInfo9624配置中的优惠
		 */
		IDataset commparaInfo9624 = null;
		if(StringUtils.isNotBlank(oldProductId) && StringUtils.isNotBlank(newProductId)) {
			commparaInfo9624 = CommparaInfoQry.getCommparaAllColByParser("CSM", "9624","110", btd.getRD().getUca().getUserEparchyCode());
		}
        String discntCode = "";
		if (commparaInfo9624!=null && commparaInfo9624.size()>0)
        {
        	discntCode=commparaInfo9624.getData(0).getString("PARA_CODE1");//para_code1=优惠
        }

		//若discntCode为空，则结束action
		if(!"".equals(discntCode)) {
			/**
			 * step.3 若旧产品符合条件，则查询绑定的优惠是否在生效期内
			 */
			UcaData uca=btd.getRD().getUca();
			String userId=uca.getUserId();
			IDataset userDiscs=UserDiscntInfoQry.getAllDiscntByUser(userId,discntCode);
			if(userDiscs!=null && userDiscs.size()>0){
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "您好，您有工作手机基础主套餐变更限定功能，不能对基础主套餐进行变更，如有疑问，请咨询业务办理人或客户经理。");

			}
		}
	}
}
