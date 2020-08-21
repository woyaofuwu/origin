
package com.asiainfo.veris.crm.order.soa.person.busi.onecardncodes;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

public class OneCardNCodesChangeCardRemoteBean extends CSBizBean
{

    public IDataset getOtherSNInfo(String sn) throws Exception
    {
    	
        IDataset userInfo = UserInfoQry.getUserinfo(sn);
        IDataset dataset = new DatasetList();
        if(!IDataUtil.isEmpty(userInfo)){
        	IData custInfo = UcaInfoQry.qryPerInfoByUserId(userInfo.getData(0).getString("USER_ID"));
            IDataset userResInfo = UserResInfoQry.queryUserResByUserIdResType(userInfo.getData(0).getString("USER_ID"), "1");

            IDataset userPro = UserProductInfoQry.getProductInfo(userInfo.getData(0).getString("USER_ID"), "-1");
            String brand_code = userPro.getData(0).getString("BRAND_CODE");
            String product_id = userPro.getData(0).getString("PRODUCT_ID");
            String openDate = userInfo.getData(0).getString("OPEN_DATE","");
            String custName =userInfo.getData(0).getString("CUST_NAME");
            userInfo.getData(0).put("PRODUCT_NAME", UpcCall.queryOfferByOfferId("P", product_id).getString("OFFER_NAME"));
            userInfo.getData(0).put("BRAND_NAME", UBrandInfoQry.getBrandNameByBrandCode(brand_code));
            userInfo.getData(0).put("PSPT_ADDR", "*****");
            userInfo.getData(0).put("OPEN_DATE", openDate.substring(0, openDate.length()-2));
            userInfo.getData(0).put("CUST_NAME", custName.substring(0, 1)+"**");
            
            IData data = new DataMap();
            data.put("OUSERINFO", userInfo.getData(0));
            data.put("OCUSTINFO", custInfo);
            data.put("ORESINFO", userResInfo.getData(0));
            dataset.add(data);
        }else{
        	IData misinfo =  MsisdnInfoQry.getCrmMsisonBySerialnumber(sn);
        	if(IDataUtil.isNotEmpty(misinfo)){
        		String sap = misinfo.getString("ASP","");
        		if(!"1".equals(sap)){
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "该号码不是移动号段，不能办理一卡双号业务！");
        		}
        	}
        }
        return dataset;
    }
}
