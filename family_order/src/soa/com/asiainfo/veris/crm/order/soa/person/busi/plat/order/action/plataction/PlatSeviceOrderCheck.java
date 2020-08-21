package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action.plataction;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
 
public class PlatSeviceOrderCheck implements IProductModuleAction
{

    @Override
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd,
			UcaData uca, BusiTradeData btd) throws Exception {  
    	
    	PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd; 
		String Isdeal ="1"; 
		IDataset commpara3704List = CommparaInfoQry.getCommNetInfo("CSM", "3704", pstd.getElementId()); 
		IData commpara = null; 
		 if (!IDataUtil.isEmpty(commpara3704List) && "3700".equals(btd.getTradeTypeCode())) 
			{   
			for(int i =0;i<commpara3704List.size();i++)
			{
				commpara = commpara3704List.getData(i); 
					Isdeal = commpara.getString("PARA_CODE1");//判断是否限制在平台业务界面办理及退订业务
					if("0".equals(Isdeal))
					{
                        CSAppException.apperr(PlatException.CRM_PLAT_3030);

					} 
				}
			}  
		}  
}
