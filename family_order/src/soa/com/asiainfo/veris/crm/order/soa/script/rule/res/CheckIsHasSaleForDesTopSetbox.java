package com.asiainfo.veris.crm.order.soa.script.rule.res;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

public class CheckIsHasSaleForDesTopSetbox extends BreBase implements IBREScript
{

	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception 
	{
	    String serialNumber = databus.getString("SERIAL_NUMBER");
	    
	    String isMergeWideCancel = databus.getString("IS_MERGE_WIDE_CANCEL");
		
	    //宽带开户撤单发起的魔百和退订，同时会撤销魔百和营销活动业务，所以不需要走此校验
	    if ("1".equals(isMergeWideCancel))
	    {
	        return false;
	    }
	    
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
    	if(IDataUtil.isNotEmpty(userInfo)){
    		
    		String userId=userInfo.getString("USER_ID");
    		
    		boolean isInValid=false;
    		String productIdInValid=null;
    		
    		IDataset saConfigs = CommparaInfoQry.querySaleActiveFeeConfig();
            if (DataSetUtils.isNotBlank(saConfigs))
            {
                for (Object obj : saConfigs)
                {
                    IData config = (IData) obj;
                    String productId = config.getString("PARA_CODE2");
                    String pkgId = config.getString("PARA_CODE1");
                    IDataset userSAInfos = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId, productId, pkgId);
                    if (DataSetUtils.isNotBlank(userSAInfos))
                    {
                    	productIdInValid=productId;
                    	
                    	isInValid=true;
                    	break;
                    }
                }
                
                //核对营销活动预处理表，看是否存在预处理营销活动
                for (Object obj : saConfigs)
                {
                    IData config = (IData) obj;
                    String productId = config.getString("PARA_CODE2");
                    String pkgId = config.getString("PARA_CODE1");
                    IDataset userSAInfos = UserSaleActiveInfoQry.queryUserBookSaleActive(userId, productId, pkgId);
                    if (DataSetUtils.isNotBlank(userSAInfos))
                    {
                    	productIdInValid=productId;
                    	
                    	isInValid=true;
                    	break;
                    }
                }
                
            }
            
            if(isInValid)
            {
//            	IDataset productInfos=ProductInfoQry.queryProductAllInfoById(productIdInValid);
                IData saleActive = UProductInfoQry.qrySaleActiveProductByPK(productIdInValid);
            	String productName = "";
                
            	if (IDataUtil.isNotEmpty(saleActive))
            	{
            	    productName = saleActive.getString("PRODUCT_NAME");
            	}
            	
            	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604004", 
            			"用户办理营销活动"+productName+"（"+productIdInValid+"），不能进行魔百和拆机！");
            	
            	return true;
            }
    		
    	}
		
    	return false;
	}
	
}
