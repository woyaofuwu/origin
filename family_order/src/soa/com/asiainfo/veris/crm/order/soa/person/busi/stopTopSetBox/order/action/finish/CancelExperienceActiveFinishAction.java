package com.asiainfo.veris.crm.order.soa.person.busi.stopTopSetBox.order.action.finish;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class CancelExperienceActiveFinishAction implements ITradeFinishAction{
	
	private static Logger logger = Logger.getLogger(CancelExperienceActiveFinishAction.class);

	@Override
	public void executeAction(IData mainTrade) throws Exception {

        // TODO Auto-generated method stub
        IData data = new DataMap();
        String tradeId = mainTrade.getString("TRADE_ID");
        String tradeTypeCode=mainTrade.getString("TRADE_TYPE_CODE");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String userId = mainTrade.getString("USER_ID");
        
        IData userInfo = UcaInfoQry.qryUserMainProdInfoBySnForGrp(serialNumber);
        if(IDataUtil.isNotEmpty(userInfo)){
        	userId = userInfo.getString("USER_ID","");
        }else{
        	return;
        }
        
        IDataset commparaInfos2324=CommparaInfoQry.getCommparaAllColByParser("CSM", "2324",null,"0898");
		if(IDataUtil.isNotEmpty(commparaInfos2324)){
			 String productId = commparaInfos2324.first().getString("PARA_CODE4");
			 if(StringUtils.isNotBlank(productId)){
				 IData params=new DataMap();
				 params.put("USER_ID", userId);
				 params.put("PRODUCT_ID", productId);
				 IDataset iDataset= Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_BY_PACKAGEID", params);  
				 if(iDataset.size()>0){
					 IData endActiveParam = new DataMap();
                     endActiveParam.put("SERIAL_NUMBER", serialNumber);
                     endActiveParam.put("PRODUCT_ID", productId);
                     endActiveParam.put("PACKAGE_ID", iDataset.getData(0).getString("PACKAGE_ID"));
                     endActiveParam.put("RELATION_TRADE_ID", iDataset.getData(0).getString("RELATION_TRADE_ID"));
                     endActiveParam.put("CHECK_MODE", "F");
                     endActiveParam.put("CAMPN_TYPE", "YX04");
                     endActiveParam.put("RETURNFEE", "0");
                     endActiveParam.put("YSRETURNFEE", "0");
                     endActiveParam.put("TRUE_RETURNFEE_COST", "0");
                     endActiveParam.put("TRUE_RETURNFEE_PRICE", "0");
                     endActiveParam.put("END_DATE_VALUE", "0"); 
                     //endActiveParam.put("END_DATE_VALUE", "3"); 
                     endActiveParam.put(Route.ROUTE_EPARCHY_CODE, "0898");
                     endActiveParam.put("END_MONTH_LAST", "Y");
                     endActiveParam.put("NO_TRADE_LIMIT", "TRUE");
                     endActiveParam.put("SKIP_RULE", "TRUE");
                     CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg4Intf", endActiveParam);
				 }
			 }
			
		}
	}

}
