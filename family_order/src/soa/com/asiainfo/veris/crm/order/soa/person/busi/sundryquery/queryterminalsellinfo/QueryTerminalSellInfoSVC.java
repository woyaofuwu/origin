
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.queryterminalsellinfo;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.PackagePrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductPkgInfoQry;

public class QueryTerminalSellInfoSVC extends CSBizService
{
	private static transient final Logger logger = Logger.getLogger(QueryTerminalSellInfoSVC.class);
	//private static Logger logger = Logger.getLogger(QueryTerminalSellInfoSVC.class);
    public IDataset queryPriceInfoByCamp(IData input) throws Exception
    {
    	QueryTerminalSellInfoBean userBillList = (QueryTerminalSellInfoBean) BeanManager.createBean(QueryTerminalSellInfoBean.class);
        return userBillList.queryPriceInfoByCamp(input, getPagination());
    }
    
    public IDataset queryProductsByCamp(IData input) throws Exception
    {
    	QueryTerminalSellInfoBean userBillList = (QueryTerminalSellInfoBean) BeanManager.createBean(QueryTerminalSellInfoBean.class);
        return userBillList.queryProductsByCamp(input);
    }
    
    public IDataset queryPriceInfoByPID(IData input) throws Exception
    {
    	QueryTerminalSellInfoBean userBillList = (QueryTerminalSellInfoBean) BeanManager.createBean(QueryTerminalSellInfoBean.class);
        return userBillList.queryPriceInfoByPID(input, getPagination());
    }
    
    /**
     * 根据活动类型和终端编码查询可以的营销方案
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryProductsByTerminal(IData input) throws Exception{
	
    	IData param1 = new DataMap();
        String eparchyCode = input.getString("EPARCHY_CODE");
        param1.put("EPARCHY_CODE",eparchyCode);
        param1.put("DEVICE_MODEL_CODE",input.getString("DEVICE_MODEL_CODE"));
        IDataset proList = UpcCall.qryTerminalSaleActiveCatalogAll();
        if (IDataUtil.isEmpty(proList))
            return proList;
		
//		IDataset salePackages = new DatasetList();
//		for(int i = 0; i < proList.size(); i++)
//		{
//			IDataset pkgInfos = ProductPkgInfoQry.qryActiveByPId(proList.getData(i).getString("PRODUCT_ID"), eparchyCode);
//			salePackages.addAll(pkgInfos);
//		}
        IDataset salePackages = UpcCall.qryTerminalSaleActiveOffer();
        if (IDataUtil.isEmpty(salePackages))
            return proList;
        
        PackagePrivUtil.filterPackageListByPriv(getVisit().getStaffId(), salePackages);
        IData svcParam = new DataMap();
        svcParam.put("SALE_ACTIVES", salePackages);
        svcParam.put("DEVICE_TYPE_CODE", input.getString("DEVICE_TYPE_CODE"));
        svcParam.put("DEVICE_MODEL_CODE", input.getString("DEVICE_MODEL_CODE"));
        svcParam.put("DEVICE_COST", input.getString("DEVICE_COST"));
        svcParam.put("SALE_PRICE", input.getString("SALE_PRICE"));
        svcParam.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        
        QueryTerminalSellInfoBean bean = (QueryTerminalSellInfoBean)BeanManager.createBean(QueryTerminalSellInfoBean.class);
        salePackages = bean.filterPackagesByTerminalConfig(svcParam);
//        salePackages = CSAppCall.call("CS.SalePackagesFilteSVC.filterPackagesByTerminalConfig", svcParam);
        IDataset retnList = new DatasetList();
        //针对过滤规则过滤后的信息来重新编排产品信息
        if (!IDataUtil.isEmpty(salePackages)) {
			for (int i = 0; i < proList.size(); i++) {
				IData proData = proList.getData(i);				
				for (int j = 0; j < salePackages.size(); j++) {
					if (proData.getString("PRODUCT_ID").equals(salePackages.getData(j).getString("PRODUCT_ID"))) {
						retnList.add(proData);
						break;
					}
				}
			}
		}else {
			retnList = proList;
		}
		return retnList ;
    }

}
