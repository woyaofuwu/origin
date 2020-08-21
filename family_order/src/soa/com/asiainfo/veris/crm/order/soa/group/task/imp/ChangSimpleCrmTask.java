package com.asiainfo.veris.crm.order.soa.group.task.imp;

import java.util.Iterator;

import com.ailk.biz.impexp.ImportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.BatchImportException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;

public class ChangSimpleCrmTask  extends ImportTaskExecutor{

	@Override
	public IDataset executeImport(IData data, IDataset dataset) throws Exception {
		IDataset resultInfo =  new DatasetList();
		IDataset result =  new DatasetList();
		if (IDataUtil.isEmpty(dataset)){
	        CSAppException.apperr(BatchImportException.ESOP_BATCH_1);
	    }
		String userId = data.getString("USER_ID");
		String productId = data.getString("PRODUCT_ID");
		String changeMode =  data.getString("CHANGEMODEIMPORT");
		if("".equals(changeMode) || null == changeMode){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"请先选择业务调整场景再导入！");
		}
		for(int i =0 ;i<dataset.size();i++){
			IData dataLine = dataset.getData(i);
			String productNo =  dataLine.getString("NOTIN_LINE_NO");
			//dataLine.put("TRADE_ID", productNo);
			dataLine.put("pattr_TRADEID", productNo);
			dataLine.put("pattr_PRODUCTNO", productNo);
			dataLine.put("NOTIN_RSRV_STR9", productNo);
			
			if("7010".equals(productId)){
				IData voipLineParam =  new DataMap();
				voipLineParam.put("USER_ID", userId);
				voipLineParam.put("PRODUCT_NO", productNo);
				IData dataLineInfo =  CSAppCall.callOne("CS.TradeDataLineAttrInfoQrySVC.qryUserDatalineByProductNO",voipLineParam);
				//IDataset dataLineInfo = TradeDataLineAttrInfoQry.qryUserDatalineByProductNO(userId,productNo);
				if(IDataUtil.isEmpty(dataLineInfo)){
					CSAppException.apperr(CrmCommException.CRM_COMM_103,"集团用户【" + userId + "】不存在专线实例号【"+productNo+"】");
				}
				IData voipParam = new DataMap();
				voipParam.put("USER_ID", userId);
				voipParam.put("PRODUCTNO", productNo);
				IData voipDataLine = CSAppCall.callOne("SS.QcsGrpIntfSVC.queryChangeLineInfosForEsop",voipParam);
				IData tempDiscounts = new DataMap();
                Iterator<String> itr2 = voipDataLine.keySet().iterator();
                while (itr2.hasNext()) {
                   String attrCode = itr2.next();
                   String attrValue = voipDataLine.getString(attrCode);
                   String transCode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMVALUE" }, "PARAMNAME", new String[] { "VOIP_DISCOUNTPARAM_CRM_ESOP", attrCode });
                   if(transCode!=null) {
                   	tempDiscounts.put(transCode, attrValue);
                   }
                }
                if("减容".equals(changeMode)){
                	int tempwidth  = Integer.parseInt(tempDiscounts.getString("NOTIN_RSRV_STR1"));
                	int dataLinewidth  = Integer.parseInt(dataLine.getString("NOTIN_RSRV_STR1"));
                	if(!(dataLinewidth < tempwidth)){
                		CSAppException.apperr(CrmCommException.CRM_COMM_103,"该专线实例号【"+productNo+"】原带宽为【"+tempDiscounts.getString("NOTIN_RSRV_STR1")+"】，减容场景带宽必须小于原来带宽！");
                	}
                }else if("同楼搬迁".equals(changeMode)){
                	if(!tempDiscounts.getString("NOTIN_RSRV_STR1").equals(dataLine.getString("NOTIN_RSRV_STR1"))){ //带宽
                		CSAppException.apperr(CrmCommException.CRM_COMM_103,"该专线实例号【"+productNo+"】带宽为【"+tempDiscounts.getString("NOTIN_RSRV_STR1")+"】,同楼搬迁不能修改带宽！");
                	}
                	if(!dataLineInfo.getString("PHONE_PERMISSION").equals(dataLine.getString("pattr_PHONEPERMISSION"))){ // 开通权限
                		CSAppException.apperr(CrmCommException.CRM_COMM_103,"该专线实例号【"+productNo+"】开通权限为【"+dataLineInfo.getString("PHONE_PERMISSION")+"】,同楼搬迁不能修改开通权限！");
                	}
                	if(!dataLineInfo.getString("PHONE_LIST").equals(dataLine.getString("pattr_PHONELIST"))){ // 码号段表
                		CSAppException.apperr(CrmCommException.CRM_COMM_103,"该专线实例号【"+productNo+"】码号段表为【"+dataLineInfo.getString("PHONE_LIST")+"】,同楼搬迁不能修改码号段表！");
                	}
                }
                
                dataLine.put("pattr_BANDWIDTH", dataLine.getString("NOTIN_RSRV_STR1"));
                String isCustom  = dataLine.getString("pattr_ISCUSTOMERPE");
                isCustom =StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new String[]
    			    	{ "TYPE_ID", "DATA_NAME"}, "DATA_ID", new String[]
    			    	{ "IS_CUSTOMER_PE",isCustom}); 
                dataLine.put("pattr_ISCUSTOMERPE", isCustom);
                dataLine.put("pattr_TRADENAMEOLD", dataLine.getString("pattr_TRADENAME"));
                dataLine.put("USER_ID", userId);
                IData map = UcaInfoQry.qryUserInfoByUserIdForGrp(userId);
                String serialNumber =  map.getString("SERIAL_NUMBER");
                dataLine.put("SERIAL_NUMBER",serialNumber);
                resultInfo.add(dataLine);
			}
		}
		
		SharedCache.set("VOIPDATALINE_INFOS", resultInfo);
		return null;
	}

}
