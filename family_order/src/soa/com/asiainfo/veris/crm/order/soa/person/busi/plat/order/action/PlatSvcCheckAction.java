package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.plat.PlatComponentSVC;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQrySvc;

public class PlatSvcCheckAction implements IProductModuleAction
{

    
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;
        PlatSvcData psd = (PlatSvcData) pstd.getPmd();
        /**校验该服务是否为过渡期服务  mod:zhangbo18 begin*/
		if (PlatConstants.OPER_ORDER.equals(pstd.getOperCode())){
			IData map = new DataMap();
            map.put("SERVICE_ID", pstd.getElementId());
            PlatComponentSVC plat = new PlatComponentSVC();
            IDataset results = plat.getPlatSvcByServiceId(map);

            IData service = results.getData(0);
            
            map.clear();
            map.put("OLD_SP_CODE", service.get("SP_CODE"));
            map.put("OLD_BIZ_CODE", service.get("BIZ_CODE"));
            IDataset offData = plat.getOffData(map);
            //如果存在配置数据，说明此服务为过渡服务，不能进行订购
            if (null != offData && offData.size() > 0){
            	CSAppException.apperr(PlatException.CRM_PLAT_90);
            }
            
            map.clear();
            map.put("NEW_SP_CODE", service.get("SP_CODE"));
            map.put("NEW_BIZ_CODE", service.get("BIZ_CODE"));
            
            offData = plat.getNewOffData(map);
            //如果存在配置数据，说明此服务为过渡服务，需要检查是否存在老服务的订购关系
            for (int i = 0 ; null != offData && i < offData.size(); i++){
            	IData officeD = offData.getData(i);
            	map.clear();
                map.put("SP_CODE", officeD.getString("OLD_SP_CODE"));
                map.put("BIZ_CODE", officeD.getString("OLD_BIZ_CODE"));
                map.put("BIZ_TYPE_CODE", officeD.getString("OLD_BIZ_TYPE"));
                offData = UserPlatSvcInfoQrySvc.querySvcAllBySpCodeAndBizCode(map);
                
                if (null != offData && offData.size() > 0){
                	officeD = offData.getData(0);
                	map.clear();
                	map.put("USER_ID", pstd.getUserId());
                	map.put("SERVICE_ID", officeD.getString("SERVICE_ID",officeD.getString("OFFER_CODE")));
                	offData = UserPlatSvcInfoQrySvc.querySvcInfoByUserIdAndSvcIdPf(map);
                	
                	if (null != offData && offData.size() > 0){
                    	CSAppException.apperr(PlatException.CRM_PLAT_91);
                	}
                }
            }
            /**校验该服务是否为过渡期服务  mod:zhangbo18 end*/
		}
    }

}
