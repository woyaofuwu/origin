package com.asiainfo.veris.crm.order.soa.person.busi.cttTerminalSale;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CTTException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

public class CttTerminalSaleSVC extends CSBizService{

	private static final long serialVersionUID = 1L;
	
	/**
	 * 核对用户的网别
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IDataset checkUserInfo(IData param)throws Exception{
		
		//查询用户是否是规定的网别的用户
		String userId=param.getString("USER_ID");
		IDataset userInfos=UserInfoQry.getUserInfoByUserId(userId);
		IData userInfo=userInfos.getData(0);
		
		String netTypeCode=userInfo.getString("NET_TYPE_CODE","");
		if(!(netTypeCode.equals("11")		//铁通宽带
				||netTypeCode.equals("12")	//固话网别
				||netTypeCode.equals("13")	//集团专网(铁通)
				||netTypeCode.equals("14")	//铁通400业务
				||netTypeCode.equals("15"))){	//铁通集团虚拟网
			CSAppException.apperr(CTTException.CRM_CTT_91);
		}
		
		
		IDataset userMainProducts = UserProductInfoQry.queryUserMainProduct(userId);
		
		String sysDate = SysDateMgr.getSysTime();
		

        String userProductId = "";
        String userBrandCode = "";
        
        if (IDataUtil.isNotEmpty(userMainProducts))
        {
            int size = userMainProducts.size();
            for (int i = 0; i < size; i++)
            {
                IData userProduct = userMainProducts.getData(i);
                if (userProduct.getString("START_DATE").compareTo(sysDate) < 0)
                {
                    userProductId = userProduct.getString("PRODUCT_ID");
                    userBrandCode = userProduct.getString("BRAND_CODE");
                }
            }
        }
        
        String userBrandName="";
        String userProductName="";
        if(userProductId!=null&&!userProductId.trim().equals("")){
        	userBrandName = UBrandInfoQry.getBrandNameByBrandCode(userBrandCode);
        }
        if(userBrandCode!=null&&!userBrandCode.trim().equals("")){
        	userProductName = UProductInfoQry.getProductNameByProductId(userProductId);
        }
 
        IDataset resultList=new DatasetList();
        IData result=new DataMap();
        result.put("USER_PRODUCT_NAME", userProductName);
        result.put("USER_PRODUCT_ID", userProductId);
        result.put("USER_BRAND_NAME", userBrandName);
        
        resultList.add(result);
        
        return resultList;
	}
	
	
	public IDataset obtainTerminalTypeFeeType(IData param)throws Exception{
		String terminalType=param.getString("TERMINAL_TYPE");
		IData feeTypeCodeInfo=StaticInfoQry.
        		getStaticInfoByTypeIdDataId("CTT_TERMIAL_SALE_TYPE_FEE_TYPE", terminalType);
        if(IDataUtil.isEmpty(feeTypeCodeInfo)){
    	   CSAppException.apperr(CTTException.CRM_CTT_92);
        }
		
		IDataset result=new DatasetList();
		result.add(feeTypeCodeInfo);
		
		
		return result;
	}
}
