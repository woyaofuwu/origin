
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.saleactive.saleactivelist;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class SaleActiveList extends CSBizTempComponent
{

    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        super.cleanupAfterRender(cycle);
        if (!cycle.isRewinding())
        {
            setSaleActives(null);
            this.setInfo(null);
        }
    }

    public abstract String getCampnType();

    public abstract String getEparchyCodeCompId();

    public abstract String getGoodsId();

    public abstract String getNewImei();

    public abstract String getProductId();
    
    public abstract void setSaleActiveListUserId(String saleActiveListUserId);
    public abstract void setSaleActiveListCustId(String saleActiveListCustId);

    public void renderComponent(StringBuilder infoParamsBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        getPage().addResAfterBodyBegin("scripts/csserv/component/saleactive/saleactiveList/saleactivelist.js");
        String specTag = getPage().getParameter("SPEC_TAG", "");
        String productId = getPage().getParameter("PRODUCT_ID", "");
        String newImei = getPage().getParameter("NEW_IMEI", getNewImei());
        String searchContent = getPage().getParameter("SEARCH_CONTENT", "");
        String SALEACTIVE_USER_ID = getPage().getParameter("SALEACTIVE_USER_ID", ""); 
        String SALEACTIVE_CUST_ID = getPage().getParameter("SALEACTIVE_CUST_ID", ""); 
        setSaleActiveListUserId(SALEACTIVE_USER_ID);
        setSaleActiveListCustId(SALEACTIVE_CUST_ID);
        //如果是IPHONE6的 则进行处理 caolin 20141016
        String isIphone6 = getPage().getParameter("IS_IPHONE6", "");
        String iphone6Imei = getPage().getParameter("IPHONE6_IMEI", "");
        if ("renderByActiveQry".equals(specTag))
        {
            renderComponentAction(productId, newImei, searchContent,isIphone6,iphone6Imei);
        }
        else if ("checkByPkgId".equals(specTag))
        {
        	checkByPkgId(infoParamsBuilder, writer, cycle);
        }
    }
    
    private void checkByPkgId(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
    	String userId = getPage().getParameter("USER_ID", "");
        String custId = getPage().getParameter("CUST_ID", "");
        String productId = getPage().getParameter("PRODUCT_ID", "");
        String packageId = getPage().getParameter("PACKAGE_ID", "");
        String campnType = getPage().getParameter("CAMPN_TYPE", "");
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("CUST_ID", custId);
        param.put("PRODUCT_ID", productId);
        param.put("PACKAGE_ID", packageId);
        param.put("CAMPN_TYPE", campnType);
        IDataset results = CSViewCall.call(this, "CS.SaleActiveQuerySVC.choicePackageNode", param);
    }

    private void renderComponentAction(String productId, String newImei, String searchContent,String isIphone6,String iphone6Imei) throws Exception
    {
        IDataset saleactives = new DatasetList();
        if (StringUtils.isNotBlank(productId) || StringUtils.isNotBlank(newImei) || StringUtils.isNotBlank(searchContent)|| StringUtils.isNotBlank(isIphone6)|| StringUtils.isNotBlank(iphone6Imei))
        {
            IData data = getPage().getData();
            String querySaleActivesSvc = "CS.SaleActiveQuerySVC.querySaleActives";
            if (StringUtils.isNotBlank(newImei))
            {
                querySaleActivesSvc = "CS.SaleActiveQuerySVC.querySaleActivesByImei";
            }
            saleactives = CSViewCall.call(this, querySaleActivesSvc, data);
            //如果是IPHONE6的 则进行处理 caolin 20141016
            if(StringUtils.isNotBlank(isIphone6)){
            	saleactives=iphone6Deal(saleactives, productId, isIphone6, iphone6Imei);
            }
        }

        IData info = new DataMap();
        for (int i = saleactives.size() - 1; i >= 0; i--)
        {
            IData saleactive = saleactives.getData(i);
            if ("true".equals(saleactive.getString("OTHER_INFO")))
            {
                getPage().setAjax(saleactive);
                saleactives.remove(i);
                continue;
            }
            saleactive.put("PACKAGE_NAME", saleactive.getString("PACKAGE_NAME") + "|" + saleactive.getString("PACKAGE_ID"));
        }

        if (IDataUtil.isEmpty(saleactives))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "没有查到符合条件的营销包");
        }

        setSaleActives(saleactives);
        
        //20160111 by songlm 冼乃捷 紧急需求，暂无需求名，作用：将终端类型传进规则内
        String deviceModelCode = saleactives.getData(0).getString("DEVICE_MODEL_CODE","");
        if (StringUtils.isNotBlank(deviceModelCode))
        {
            info.put("DEVICE_MODEL_CODE", deviceModelCode);
        }
        //end
        
        if (StringUtils.isNotBlank(newImei))
        {
            info.put("IMEI", newImei);
            setInfo(info);
        }
        setEparchyCodeCompId(getPage().getParameter("SALEACTIVELIST_EPARCHY_CODE_COMPID", getEparchyCodeCompId()));
    }

    public abstract void setEparchyCodeCompId(String eparchyCodeCompId);

    public abstract void setInfo(IData info);

    public abstract void setSaleActives(IDataset saleactives);
    /**
	 *根据IMEI情况显示免预存还是预存IPHONE6营销包 caolin 20141016 
	 */
	public IDataset iphone6Deal(IDataset saleActives, String productId, String isIphone6, String iphone6Imei) throws Exception {
		//输入IPHONE6IMEI号，调接口校验
		if ("1".equals(isIphone6)) {
			IData data = getPage().getData();
			//查询该产品的SALE_TAG值，调用华为的接口校验
			IData product =CSViewCall.call(this,"CS.ProductInfoQrySVC.getProductByPK", data).first();
			data.put("IPHONE6_IMEI", iphone6Imei);
			data.put("SALE_TAG", product.getString("RSRV_TAG1",""));
			IData iphone6CheckIMEISaleInfoResults = CSViewCall.call(this,"CS.SaleActiveQuerySVC.iphone6CheckIMEISaleInfo", data).first();
			String x_resultCode = iphone6CheckIMEISaleInfoResults.getString("X_RESULTCODE");
			//输入的IMEI符合免预存条件,校验机型
			if (StringUtils.equals(x_resultCode, "0")) {//华为测接口文档有误，0为成功，其他失败
				String terminal_type_code = iphone6CheckIMEISaleInfoResults.getString("TERMINAL_TYPE_CODE");
				String device_model_code = iphone6CheckIMEISaleInfoResults.getString("DEVICE_MODEL_CODE");
				return this.filterTerminalSalePackages(saleActives,terminal_type_code,device_model_code,data.getString("EPARCHY_CODE"));
			}
			if(IDataUtil.isNotEmpty(saleActives)){
				saleActives.getData(saleActives.size()-1).put("IPHONE6_X_RESULTCODE", "-1");
				saleActives.getData(saleActives.size()-1).put("IPHONE6_X_RESULTINFO", "校验结果:["+iphone6CheckIMEISaleInfoResults.getString("X_RESULTINFO")+"],只能办理预存活动！");
			}
			
		} 
		//不输入IPHONE6IMEI号,或者输入的IMEI没有免预存条件的活动，过滤掉免预存的
		return this.filterFreeStoredSalePackages(saleActives);
	}
	/**
	 * 过滤掉免预存的营销活动
	 */
	private IDataset filterFreeStoredSalePackages(IDataset salePackages) throws Exception
    {
        if (IDataUtil.isNotEmpty(salePackages)){
        	for (int i = salePackages.size() - 1; i >= 0; i--) {
    			IData salePackage = salePackages.getData(i);
    			String package_type = salePackage.getString("PACKAGE_TYPE");
    			if (StringUtils.equals("IS_IPHONE6",package_type)) {
    				salePackages.remove(i);
    			}
    		}
        }
        return salePackages;
    }
	/**
	 * 根据华为查找的终端信息 查找能办理的营销活动
	 */
	private IDataset filterTerminalSalePackages(IDataset salePackages,String terminal_type_code, String device_model_code,String eparchy_code) throws Exception
    {
        if (IDataUtil.isNotEmpty(salePackages)) {
			IDataset iphone6PackageList = new DatasetList();
			for (int i = salePackages.size() - 1; i >= 0; i--) {
				IData salePackage = salePackages.getData(i);
				String package_type = salePackage.getString("PACKAGE_TYPE");
				String package_terminal_type_code = salePackage.getString("RSRV_STR5");
				String package_device_model_code = salePackage.getString("RSRV_STR2");
				String productId = salePackage.getString("PRODUCT_ID");
				String packageId = salePackage.getString("PACKAGE_ID");
				if (StringUtils.equals("IS_IPHONE6", package_type)) {
					// 1、只有deviceModelCode机型才能办理该营销包
					if (!"ZZZZ".equals(package_device_model_code)&& device_model_code.equals(package_device_model_code)) {
						iphone6PackageList.add(salePackage);
					}
					// 2、某一类终端下的所有机型都可以办理该活动，但是需要排除td_B_sale_terminal_limit的当前产品和包下的terminal_type_code下的terminal_model_code机型。
					else if ("ZZZZ".equals(package_device_model_code)&& StringUtils.isNotBlank(package_terminal_type_code)) {
						if (package_terminal_type_code.contains("|"	+ terminal_type_code + "|")) {
							IData inputData = new DataMap();
							inputData.put("PRODUCT_ID", productId);
							inputData.put("PACKAGE_ID", packageId);
							inputData.put("TERMINAL_TYPE_CODE", terminal_type_code);
							inputData.put("DEVICE_MODEL_CODE", device_model_code);
							inputData.put("EPARCHY_CODE", eparchy_code);
							IDataset terminalLimitList = CSViewCall.call(this,"CS.SaleActiveQuerySVC.getSaleTerminalLimitInfo", inputData);
							if (IDataUtil.isEmpty(terminalLimitList)) {
								iphone6PackageList.add(salePackage);
							}
						}
					}
					//3、多个终端机型都可以办理该营销包，营销包与机型的对应关系为：TD_B_SALE_TERMINAL_LIMIT的当前产品和包下，且TERMINAL_TYPE_CODE='0'下的terminal_model_code机型。
					else if ("ZZZZ".equals(package_device_model_code)&& StringUtils.isBlank(package_terminal_type_code)) {
						IData inputData = new DataMap();
						inputData.put("PRODUCT_ID", productId);
						inputData.put("PACKAGE_ID", packageId);
						inputData.put("TERMINAL_TYPE_CODE", "0");
						inputData.put("DEVICE_MODEL_CODE", device_model_code);
						inputData.put("EPARCHY_CODE", eparchy_code);
						IDataset terminalLimitList = CSViewCall.call(this,"CS.SaleActiveQuerySVC.getSaleTerminalLimitInfo", inputData);
						if (IDataUtil.isNotEmpty(terminalLimitList)) {
							iphone6PackageList.add(salePackage);
						}
					}
					salePackages.remove(i);
				}
			}
			// 有符合条件的营销活动，则返回符合条件的
			if (IDataUtil.isNotEmpty(iphone6PackageList)) {
				return iphone6PackageList;
			}
			if(IDataUtil.isNotEmpty(salePackages)){
				// 没有，则返回默认的营销活动（即预存的）
		        salePackages.getData(salePackages.size()-1).put("IPHONE6_X_RESULTCODE", "-1");
		        salePackages.getData(salePackages.size()-1).put("IPHONE6_X_RESULTINFO", "没有该机型匹配的营销活动，只能办理预存活动！");
			}
		}
		
		return salePackages;
    }
}