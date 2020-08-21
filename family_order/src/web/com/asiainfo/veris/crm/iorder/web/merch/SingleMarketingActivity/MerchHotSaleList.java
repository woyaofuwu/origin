
package com.asiainfo.veris.crm.iorder.web.merch.SingleMarketingActivity;

import com.ailk.common.data.impl.DatasetList;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class MerchHotSaleList extends PersonBasePage
{
    public abstract IDataset getInfos();

    public void checkHotByPkgIg(IRequestCycle cycle) throws Exception
    {
        IData cond = getData();
        cond.put(Route.ROUTE_EPARCHY_CODE, cond.getString("EPARCHY_CODE", ""));
        IDataset ruleResultDataSet = CSViewCall.call(this, "CS.SaleActiveCheckSVC.checkByPackage", cond);
        IData ruleResultData = ruleResultDataSet.getData(0);
        
        /**
         * REQ201510260003 光猫申领提示优化【2015下岛优化】
         * chenxy3 20151027 
         */
        IData commData = CSViewCall.call(this, "SS.FTTHModermApplySVC.checkProdInCommpara", cond).first();
        String inTag = commData.getString("IN_TAG", "");
        if ("1".equals(inTag)) { 
            IData resultData = CSViewCall.call(this, "SS.FTTHModermApplySVC.checkIfFtthNewUser", cond).first();
            ruleResultData.putAll(resultData); // IS_FTTH_USER 
        }
        //getPage().setAjax(ruleResultData);
    }
    
    
    public void queryHotSaleList(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String productId=data.getString("PRODUCT_ID");
        String campnType=data.getString("CAMPN_TYPE");  
        String newImei = data.getString("NEW_IMEI");
        String searchContent = data.getString("SEARCH_CONTENT");

//        if ("_YX03_YX08_YX09_YX07_YX11_".indexOf(campnType) != -1)
//        {
//            CSViewException.apperr(CrmCommException.CRM_COMM_103, "终端类营销活动请去营销活动标签页去办理");
//        }    
        IData info = new DataMap();

        IDataset saleactives = new DatasetList();
        if (StringUtils.isNotBlank(productId) || StringUtils.isNotBlank(newImei) || StringUtils.isNotBlank(searchContent)) {
            //IData data = getPage().getData();
            String querySaleActivesSvc = "CS.SaleActiveQuerySVC.querySaleActives";
            if (StringUtils.isNotBlank(newImei)) {
                querySaleActivesSvc = "CS.SaleActiveQuerySVC.querySaleActivesByImei";
            }
            try
            {
                saleactives = CSViewCall.call(this, querySaleActivesSvc, data); 
            }
            catch (Exception e)
            {
                info.put("ERROR_TAG", "Y");
                info.put("ERROR_INFO", e.getMessage());
            }
        }

        if(IDataUtil.isNotEmpty(saleactives)){
            for (int i = saleactives.size() - 1; i >= 0; i--) {
                IData saleactive = saleactives.getData(i);
                if ("true".equals(saleactive.getString("OTHER_INFO"))) {
                    info.putAll(saleactive);
                    saleactives.remove(i);
                    continue;
                }
                saleactive.put("PACKAGE_NAME", saleactive.getString("PACKAGE_NAME") + "|" + saleactive.getString("PACKAGE_ID"));
            }

        }
        
        setSaleActives(saleactives);        
        if (IDataUtil.isNotEmpty(saleactives)) {
            // 20160111 by songlm 冼乃捷 紧急需求，暂无需求名，作用：将终端类型传进规则内
            String deviceModelCode = saleactives.getData(0).getString("DEVICE_MODEL_CODE","");
            if (StringUtils.isNotBlank(deviceModelCode)) {
                info.put("DEVICE_MODEL_CODE", deviceModelCode);
            }
            // end
            
            info.put("SALE_SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
            info.put("CAMPN_TYPE", data.getString("CAMPN_TYPE"));
            info.put("SALE_CAMPN_TYPE", data.getString("CAMPN_TYPE"));
            info.put("SALEACTIVE_USER_ID", data.getString("SALEACTIVE_USER_ID"));
            info.put("SALEACTIVE_CUST_ID", data.getString("SALEACTIVE_CUST_ID"));
            info.put("SALE_EPARCHY_CODE", data.getString("SALE_EPARCHY_CODE"));
            data.put("PRODUCT_ID", data.getString("PRODUCT_ID"));
            data.put("USER_ID", data.getString("SALEACTIVE_USER_ID"));
            data.put("CHECK_TYPE", "CHECKPROD");
            IData checkInfos=checkHotProdNeedOldCustSn(data);//针对营销方案需要检验号码的，页面给个标记，先展示检验号码，检验完后展示营销列表 
            if("Y".equals(checkInfos.getString("IF_NEED"))){
                setIsNeed(true);
                info.put("IF_NEED", "Y");
            }
            info.put("IF_NEW_USER_ACTIVE", checkInfos.getString("IF_NEW_USER_ACTIVE",""));
            info.put("SALE_PRODUCT_ID", data.getString("PRODUCT_ID"));
            
            if ("_YX03_YX08_YX09_YX07_YX06_".indexOf(campnType) != -1)
            {
                IData goodInfos=info.getData("GOODS_INFO");
                if(IDataUtil.isNotEmpty(goodInfos)){
                    String battery=goodInfos.getString("BATTERY");
                    String color=goodInfos.getString("COLOR");
                    String deviceBrand=goodInfos.getString("DEVICE_BRAND");
                    String deviceModel =goodInfos.getString("DEVICE_MODEL");          
                    info.put("BATTERY", battery);
                    info.put("COLOR", color);
                    info.put("DEVICE_BRAND", deviceBrand);
                    info.put("DEVICE_MODEL", deviceModel);
                    info.put("SALEACTIVE_IMEI", newImei);
                }
            }
        }else {
             if(!"Y".equals(info.getString("ERROR_TAG"))){//不是报错导致的没查到信息
                 //CSViewException.apperr(CrmCommException.CRM_COMM_103, "没有查到符合条件的营销包");
                 info.put("NO_SALE", "Y");
             }
        }


        setInfo(info);

    }
    
	/**
	 * 老用户免费领取4G手机营销活动
	 * */
	public IData checkHotProdNeedOldCustSn(IData params) throws Exception
	{
		IData rtnData = new DataMap();
		String ifNeedCheckProd = "N";
		String ifNewUserActive="";
		String productId = params.getString("PRODUCT_ID", "");
		String checkType = params.getString("CHECK_TYPE", "");
		if ("CHECKPROD".equals(checkType))
		{
			params.put("PARAM_ATTR", "9957");
			IDataset commpara2017 = CSViewCall.call(this, "SS.SaleActiveCheckSnSVC.check2017ActiveCommpara", params);
			
			if(IDataUtil.isNotEmpty(commpara2017)){
				/**
				 * 2017年老客户感恩大派送活动开发需求
				 * 20170906 chenxy3
				 * */
				for(int j=0; j < commpara2017.size(); j++){
					String commProd = commpara2017.getData(j).getString("PARAM_CODE", "");
					if (productId.equals(commProd))
					{
						ifNeedCheckProd = "Y";
						ifNewUserActive="NEW";
						break;
					}
				}
			}else{
				IDataset commparas = CSViewCall.call(this, "CS.SaleActiveCheckSnSVC.checkProdParaInfo", params);
				if (commparas != null && commparas.size() > 0)
				{
					for (int i = 0; i < commparas.size(); i++)
					{
						String commProd = commparas.getData(i).getString("PARAM_CODE", "");
						if (productId.equals(commProd))
						{
							ifNeedCheckProd = "Y"; 
							break;
						} 
					}
				}
			} 
			rtnData.put("IF_NEED", ifNeedCheckProd);
			rtnData.put("IF_NEW_USER_ACTIVE", ifNewUserActive);
		}
		else if("CHECKPACK".equals(checkType))
		{
			String packageId = params.getString("PACKAGE_ID", "");
			IDataset commparas = CSViewCall.call(this, "CS.SaleActiveCheckSnSVC.checkProdParaInfo", params);
			if (commparas != null && commparas.size() > 0)
			{
				for (int i = 0; i < commparas.size(); i++)
				{
					String commProd = commparas.getData(i).getString("PARAM_CODE", "").substring(0, 8);
					String commPack = commparas.getData(i).getString("PARAM_CODE", "").substring(9, 17);
					if (productId.equals(commProd) && packageId.equals(commPack))
					{
						ifNeedCheckProd = "Y";
						break;
					}
				}
			}
			IDataset commparasSIM = CSViewCall.call(this, "CS.SaleActiveCheckSnSVC.checkSIMInfo", params);
			if (commparasSIM != null && commparasSIM.size() > 0)
			{
				ifNeedCheckProd = "SIM";
				CSViewCall.call(this, "CS.SaleActiveCheckSnSVC.sendSMSTOhebao", params);
				
			}
			rtnData.put("IF_NEED", ifNeedCheckProd);
		
		}else if("NEW_USER_ACTIVE".equals(checkType))
		{/**
		* 2017年老客户感恩大派送活动开发需求
		* 20170906 chenxy3
		* */ 
			IDataset userProds = CSViewCall.call(this, "SS.SaleActiveCheckSnSVC.check2017ActiveInfo", params);
			if (userProds != null && userProds.size() > 0)
			{
				rtnData = userProds.getData(0);
			}
		}
		else
		{
			IDataset userProds = CSViewCall.call(this, "CS.SaleActiveCheckSnSVC.checkIfUseHaveNeedProd", params);
			if (userProds != null && userProds.size() > 0)
			{
				rtnData = userProds.getData(0);
			}
		}
		//this.setAjax(rtnData);
		
		return rtnData;
	}
        
    public abstract void setInfo(IData info);
    public abstract void setSaleActives(IDataset saleactives);
    public abstract String getEparchyCodeCompId();
	public abstract void setIsNeed(boolean can);

}
