

package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.queryterminalsellinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveElementFeeBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.cache.SaleTerminalLimitObject;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.SaleTerminalLimitInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class QueryTerminalSellInfoBean extends CSBizBean
{
	//private static Logger logger = Logger.getLogger(QueryTerminalSellInfoBean.class);
	
	public IDataset queryPriceInfoByCamp(IData inparams, Pagination pagination) throws Exception
    {
		String[] terminals = inparams.getString("TERMINAL_SEARCH1", "").split(",");
		//logger.info("linsl terminals" + inparams.getString("TERMINAL_SEARCH1", ""));
//    	IDataset detailinfos= Dao.qryByCode("TD_B_PRODUCT", "SEL_TERMINAL_BY_CAMP", param,pagination,Route.CONN_CRM_CEN);   
		IDataset detailinfos= UpcCall.qryTerminalOffersByPkgExtRsrvstrCatalogId(inparams.getString("SALE_CAMPN_TYPE",""), terminals[1]);
    	if(IDataUtil.isNotEmpty(detailinfos))
    	{
    		for(int i=0,size=detailinfos.size();i<size;i++)
    		{
    			IData detailinfo = detailinfos.getData(i);
    			String productId = detailinfo.getString("PRODUCT_ID");
    			String packageId = detailinfo.getString("PACKAGE_ID"); 
    			String fee = detailinfo.getString("FEE");
    			String rsrvStr1 = detailinfo.getString("RSRV_STR1");
    			String rsrvStr2 = detailinfo.getString("RSRV_STR2");
    			String rsrvStr3 = detailinfo.getString("RSRV_STR3");
    			String rsrvStr4 = detailinfo.getString("RSRV_STR4");
    			String deviceModeCode = terminals[1]; 
    			String resTypeId = null;
    			String eparchyCode = CSBizBean.getTradeEparchyCode();
    			// 只有deviceModelCode机型才能办理该营销包
                if (!"ZZZZ".equals(deviceModeCode) && !"".equals(deviceModeCode))
                {   			
			    	SaleActiveElementFeeBean elementFeeBean = BeanManager.createBean(SaleActiveElementFeeBean.class);
	    	        String operFee = elementFeeBean.getTerminalOperFeeByDeviceModelCode(productId, packageId, fee, rsrvStr1, rsrvStr2, rsrvStr3, rsrvStr4, deviceModeCode, resTypeId, eparchyCode);
	    	        detailinfo.put("FEE", String.valueOf(Integer.parseInt(operFee)/100));    	        
                }
                
                detailinfo.put("SALE_PRICE", String.valueOf(Integer.parseInt(terminals[3])/100));
    		}
    	}
    	
    	return detailinfos;
    }
	
	public IDataset queryProductsByCamp(IData inparam) throws Exception
    {
		IData param1 = new DataMap();
        String eparchyCode = inparam.getString("EPARCHY_CODE");
        param1.put("EPARCHY_CODE",eparchyCode);
//        IDataset proList = Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_LABEL_CAMP", param1, Route.CONN_CRM_CEN);
        return UpcCall.qryTerminalSaleActiveCatalog();
    }
	
	public IDataset queryPriceInfoByPID(IData inparams, Pagination pagination) throws Exception
    {
		String[] terminals = inparams.getString("TERMINAL_SEARCH", "").split(",");
		//logger.info("linsl TERMINAL_SEARCH" + inparams.getString("TERMINAL_SEARCH", ""));
		IDataset detailinfos= UpcCall.qryTerminalOffersByPkgExtRsrvstrCatalogId(inparams.getString("SALE_CAMPN_TYPE",""), "");   
    	if(IDataUtil.isNotEmpty(detailinfos))
    	{
    		//过滤掉该产品下不符合条件的营销活动包
    		IData svcParam = new DataMap();
            svcParam.put("SALE_ACTIVES", detailinfos);
            svcParam.put("DEVICE_TYPE_CODE", terminals[1]);
            svcParam.put("DEVICE_MODEL_CODE", terminals[0]);
            svcParam.put("DEVICE_COST", terminals[2]);
            svcParam.put("SALE_PRICE", terminals[3]);
            svcParam.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
            
            detailinfos = CSAppCall.call("CS.SalePackagesFilteSVC.filterPackagesByTerminalConfig", svcParam);
            
    		for(int i=0,size=detailinfos.size();i<size;i++)
    		{
    			IData detailinfo = detailinfos.getData(i);
    			String productId = detailinfo.getString("PRODUCT_ID");
    			String packageId = detailinfo.getString("PACKAGE_ID"); 
    			String fee = detailinfo.getString("FEE");
    			String rsrvStr1 = detailinfo.getString("RSRV_STR1");
    			String rsrvStr2 = detailinfo.getString("RSRV_STR2");
    			String rsrvStr3 = detailinfo.getString("RSRV_STR3");
    			String rsrvStr4 = detailinfo.getString("RSRV_STR4");
    			String deviceModeCode = terminals[0]; 
    			String resTypeId = null;
    			String eparchyCode = CSBizBean.getTradeEparchyCode();
    			// 只有deviceModelCode机型才能办理该营销包
    			if (!"ZZZZ".equals(deviceModeCode) && !"".equals(deviceModeCode))
                {   			
    				SaleActiveElementFeeBean elementFeeBean = BeanManager.createBean(SaleActiveElementFeeBean.class);
    				String operFee = elementFeeBean.getTerminalOperFeeByDeviceModelCode(productId, packageId, fee, rsrvStr1, rsrvStr2, rsrvStr3, rsrvStr4, deviceModeCode, resTypeId, eparchyCode);
    				detailinfo.put("FEE", String.valueOf(Integer.parseInt(operFee)/100));    	        
                }
    			detailinfo.put("SALE_PRICE", String.valueOf(Integer.parseInt(terminals[3])/100));
    		}
    	}
    	
    	return detailinfos;
    }
	
	public IDataset filterPackagesByTerminalConfig(IData input) throws Exception
    {
        IDataset saleActives = input.getDataset("SALE_ACTIVES");
        String deviceTypeCode = input.getString("DEVICE_TYPE_CODE");
        String deviceModelCode = input.getString("DEVICE_MODEL_CODE");
        String eparchyCode = input.getString(Route.ROUTE_EPARCHY_CODE);
        String diviceCost = input.getString("DEVICE_COST");
        String salePrice = input.getString("SALE_PRICE");

        IDataset finalSalePackages = filterTerminalSalePackages(saleActives, deviceTypeCode, deviceModelCode, eparchyCode);
        finalSalePackages = filterPackagesByContractConfig(finalSalePackages, diviceCost, eparchyCode);
        finalSalePackages = filterPackageByTerminalSaleOrDeviceCost(finalSalePackages, salePrice, diviceCost, eparchyCode);

        finalSalePackages = filterPackagesByScoreConfig(finalSalePackages, salePrice);

        return finalSalePackages;
    }
    /**
     * 根据终端销售价或终端结算价配置过滤营销活动包
     * 
     * @param activePackages
     * @param salePrice
     * @param deviceCost
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    private IDataset filterPackageByTerminalSaleOrDeviceCost(IDataset activePackages, String salePrice, String deviceCost, String eparchyCode) throws Exception
    {
        IDataset salePriceConfigs = CommparaInfoQry.getCommByParaAttr("CSM", "1745", eparchyCode);

        if (IDataUtil.isEmpty(salePriceConfigs) || IDataUtil.isEmpty(activePackages))
        {
            return activePackages;
        }

        for (int index = 0, size = salePriceConfigs.size(); index < size; index++)
        {
            IData salePriceConfig = salePriceConfigs.getData(index);

            String configProductId = salePriceConfig.getString("PARAM_CODE");
            String configPackageId = salePriceConfig.getString("PARA_CODE1");
            String computeType = salePriceConfig.getString("PARA_CODE2");
            int minMoney = salePriceConfig.getInt("PARA_CODE3");
            int maxMoney = salePriceConfig.getInt("PARA_CODE4");
            int compareVal = "1".equals(computeType) ? Integer.parseInt(salePrice) : Integer.parseInt(deviceCost);

            for (int j = 0, s = activePackages.size(); j < s; j++)
            {
                String saleProductId = activePackages.getData(j).getString("PRODUCT_ID");
                String salePackageId = activePackages.getData(j).getString("PACKAGE_ID");

                if (!configProductId.equals(saleProductId) || !configPackageId.equals(salePackageId))
                {
                    continue;
                }

                if (compareVal < minMoney || compareVal > maxMoney)
                {
                    activePackages.remove(j);
                    s--; j--;
                }
            }
        }

        return activePackages;
    }

    /**
     * 根据终端结算价（终端平台返回）、合约价的特殊配置，过滤营销包！ CRM结算价 = 合约价 - 优惠价（1742中配置） CRM结算价在 1741 的 PARA_CODE2 与 PARA_CODE3 之间的活动才能办理
     * 
     * @param salePackages
     * @param deviceCost
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    private IDataset filterPackagesByContractConfig(IDataset salePackages, String deviceCost, String eparchyCode) throws Exception
    {
        // 合约价，销售价的最大、最小值配置
        IDataset contractPriceConfigs = CommparaInfoQry.getCommparaByCompare45("CSM", "1741", deviceCost, eparchyCode);

        IDataset contractPackages = getPackagesByContractCommparaConfigs(salePackages, contractPriceConfigs);

        if (IDataUtil.isEmpty(contractPackages))
        {
            return salePackages;
        }

        // 优惠价配置
        IDataset packageDiscntConfigs = CommparaInfoQry.getCommByParaAttr("CSM", "1742", eparchyCode);

        if (IDataUtil.isEmpty(packageDiscntConfigs))
        {
            return salePackages;
        }

        IDataset contractDiscntPackages = getPackagesByDiscntCommparaConfigs(contractPackages, packageDiscntConfigs);

        IDataset filterPackages = new DatasetList();

        for (int j = 0, s = contractDiscntPackages.size(); j < s; j++)
        {
            IData contractDiscntPackage = contractDiscntPackages.getData(j);

            int contractMoney = contractDiscntPackage.getInt("CONTRACT_MONEY");
            int discntMoneyMin = contractDiscntPackage.getInt("DISCNT_MONEY_MIN");
            int discntMoneyMax = contractDiscntPackage.getInt("DISCNT_MONEY_MAX");
            int discntMoney = contractDiscntPackage.getInt("DISCNT_MONEY");

            int saleMoney = contractMoney - discntMoney;

            if (saleMoney >= discntMoneyMin && saleMoney <= discntMoneyMax)
            {
                continue;
            }

            filterPackages.add(contractDiscntPackage);
        }

        if (IDataUtil.isEmpty(filterPackages))
        {
            return salePackages;
        }

        for (int j = 0, s = filterPackages.size(); j < s; j++)
        {
            IData filterPackage = filterPackages.getData(j);

            String filterProductId = filterPackage.getString("PRODUCT_ID");
            String filterPackageId = filterPackage.getString("PACKAGE_ID");

            for (int index = 0, size = salePackages.size(); index < size; index++)
            {
                IData salePackage = salePackages.getData(index);

                String productId = salePackage.getString("PRODUCT_ID");
                String packageId = salePackage.getString("PACKAGE_ID");

                if (filterProductId.equals(productId) && filterPackageId.equals(packageId))
                {
                    salePackages.remove(index);
                    size--; index--;
                }
            }
        }

        return salePackages;
    }

    private IDataset filterPackagesByScoreConfig(IDataset salePackages, String salePrice) throws Exception
    {
        if (IDataUtil.isEmpty(salePackages))
        {
            return salePackages;
        }

        for (int index = 0, size = salePackages.size(); index < size; index++)
        {
            IData salePackage = salePackages.getData(index);
            String rsrvTag1 = salePackage.getString("RSRV_TAG1");
            String scoreValueFee = salePackage.getString("RSRV_STR3", "0");
            if(!salePackage.containsKey("RSRV_TAG1"))
            {
//                IData _packageData = PkgInfoQry.getPackageByPK(salePackage.getString("PACKAGE_ID"));
                IDataset packageList = UpcCall.queryTempChaByOfferTable(BofConst.ELEMENT_TYPE_CODE_PACKAGE, salePackage.getString("PACKAGE_ID"), "TD_B_PACKAGE");
                IData _packageData = packageList.getData(0);
                rsrvTag1 = _packageData.getString("RSRV_TAG1");
                scoreValueFee = _packageData.getString("RSRV_STR3", "0");
            }
            if ("Z".equals(rsrvTag1) && Integer.parseInt(salePrice) <= Integer.parseInt(scoreValueFee))
            {
                salePackages.remove(index);
                size--; index--;
            }
        }

        return salePackages;
    }

    private IDataset filterTerminalSalePackages(IDataset salePackages, String deviceTypeCode, String deviceModelCode, String eparchyCode) throws Exception
    {
        if (IDataUtil.isEmpty(salePackages))
        {
            return salePackages;
        }

        IDataset filterPackages = new DatasetList();

        for (int i = 0, size = salePackages.size(); i < size; i++)
        {
            IData salePackage = salePackages.getData(i);

            String productId = salePackage.getString("PRODUCT_ID");
            String packageId = salePackage.getString("PACKAGE_ID");
            String terminalModelCode = salePackage.getString("RSRV_STR2");
            String terminalTypeCode = salePackage.getString("RSRV_STR5");

            // 只有deviceModelCode机型才能办理该营销包
            if (!"ZZZZ".equals(terminalModelCode) && deviceModelCode.equals(terminalModelCode))
            {
                filterPackages.add(salePackage);
            }
            // 某一类终端下的所有机型都可以办理该活动，但是需要排除td_B_sale_terminal_limit的当前产品和包下的terminal_type_code下的terminal_model_code机型。
            else if ("ZZZZ".equals(terminalModelCode) && StringUtils.isNotBlank(terminalTypeCode))
            {
                if (terminalTypeCode.contains("|" + deviceTypeCode + "|"))
                {
                    SaleTerminalLimitObject saleTerminalLimitObj = getSaleTerminalLimitObj(productId, packageId, deviceTypeCode, deviceModelCode, eparchyCode);
                    if (saleTerminalLimitObj == null)
                    {
                        filterPackages.add(salePackage);
                    }
                }
            }
            // 多个终端机型都可以办理该营销包，营销包与机型的对应关系为：TD_B_SALE_TERMINAL_LIMIT的当前产品和包下，且TERMINAL_TYPE_CODE='0'下的terminal_model_code机型。
            else if ("ZZZZ".equals(terminalModelCode) && StringUtils.isBlank(terminalTypeCode))
            {
                SaleTerminalLimitObject saleTerminalLimitObj = getSaleTerminalLimitObj(productId, packageId, "0", deviceModelCode, eparchyCode);
                if (saleTerminalLimitObj != null)
                {
                    filterPackages.add(salePackage);
                }
            }

        }
        return filterPackages;
    }

    private IDataset getPackagesByContractCommparaConfigs(IDataset salePackages, IDataset configs) throws Exception
    {
        if (IDataUtil.isEmpty(salePackages) || IDataUtil.isEmpty(configs))
        {
            return salePackages;
        }

        IDataset returnPackageDataset = new DatasetList();

        for (int i = 0, s = salePackages.size(); i < s; i++)
        {
            String activeProductId = salePackages.getData(i).getString("PRODUCT_ID","");
            for (int j = 0, z = configs.size(); j < z; j++)
            {
                IData config = configs.getData(j);
                String configProductId = config.getString("PARAM_CODE");

                if (!activeProductId.equals(configProductId))
                {
                    continue;
                }

                IData salePackage = salePackages.getData(i);

                salePackage.put("CONTRACT_MONEY", config.getString("PARA_CODE1"));
                salePackage.put("DISCNT_MONEY_MIN", config.getString("PARA_CODE2"));
                salePackage.put("DISCNT_MONEY_MAX", config.getString("PARA_CODE3"));

                returnPackageDataset.add(salePackage);
            }
        }
        return returnPackageDataset;
    }

    private IDataset getPackagesByDiscntCommparaConfigs(IDataset salePackages, IDataset configs) throws Exception
    {
        if (IDataUtil.isEmpty(salePackages) || IDataUtil.isEmpty(configs))
        {
            return null;
        }

        IDataset returnActiveDataset = new DatasetList();
        for (int i = 0, s = salePackages.size(); i < s; i++)
        {
            String activeProductId = salePackages.getData(i).getString("PRODUCT_ID");
            String packageId = salePackages.getData(i).getString("PACKAGE_ID");
            for (int j = 0, z = configs.size(); j < z; j++)
            {
                IData config = configs.getData(j);

                String configProductId = config.getString("PARAM_CODE");
                String configPackageId = config.getString("PARA_CODE1");

                if (!activeProductId.equals(configProductId) || !packageId.equals(configPackageId))
                {
                    continue;
                }

                IData active = salePackages.getData(i);

                active.put("DISCNT_MONEY", config.getString("PARA_CODE2"));

                returnActiveDataset.add(active);
            }
        }
        return returnActiveDataset;
    }

    @SuppressWarnings("unchecked")
    private SaleTerminalLimitObject getSaleTerminalLimitObj(String productId, String packageId, String terminalTypeCode, String terminalModeCode, String eparchyCode) throws Exception
    {
        IData terminalLimit = SaleTerminalLimitInfoQry.queryByPK(productId, packageId, terminalTypeCode, terminalModeCode, eparchyCode);
        if (!IDataUtil.isEmpty(terminalLimit))
        {
            SaleTerminalLimitObject saleTermianlObj = new SaleTerminalLimitObject();
            saleTermianlObj.setProductId(terminalLimit.getString("PRODUCT_ID"));
            saleTermianlObj.setPackageId(terminalLimit.getString("PACKAGE_ID"));
            saleTermianlObj.setTerminalTypeCode(terminalLimit.getString("TERMINAL_TYPE_CODE"));
            saleTermianlObj.setTerminalModeCode(terminalLimit.getString("TERMINAL_MODE_CODE"));
            saleTermianlObj.setEparchyCode(terminalLimit.getString("EPARCHY_CODE"));
            return saleTermianlObj;
        }
        return null;
    }
}

