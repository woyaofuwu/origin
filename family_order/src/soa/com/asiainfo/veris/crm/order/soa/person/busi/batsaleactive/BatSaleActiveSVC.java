package com.asiainfo.veris.crm.order.soa.person.busi.batsaleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.PackagePrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class BatSaleActiveSVC extends CSBizService {
	
	 public IDataset getSaleProductCond(IData params) throws Exception
	    { 
		 String selectType = params.getString("SELECTTYPE","");	
		 IDataset rtnSet = new DatasetList();
		 if("".equals(selectType)){
//			 rtnSet = BatSaleActiveBean.getProductsLists(params);
		     IDataset paraInfos = CommparaInfoQry.getCommparaByParaAttr("CSM", "604", this.getTradeEparchyCode());
		     if(IDataUtil.isNotEmpty(paraInfos))
		     {
		         for(int i=0;i<paraInfos.size();i++)
		         {
		             IData paraInfo = paraInfos.getData(i);
		             String paramCode = paraInfo.getString("PARAM_CODE");
		             IData catalog = UpcCall.qryCatalogByCatalogId(paramCode).getData(0);
		             String lableId = catalog.getString("UP_CATALOG_ID");
		             if("|YX02|YX03|YX04|YX05|YX06|YX07|YX08|YX09|YX10|YX11|YX12|YX13|".indexOf(lableId) < 0)
		             {
		                 continue;
		             }else
		             {
		                 IData data = new DataMap();
		                 data.put("PRODUCT_ID", catalog.getString("CATALOG_ID"));
		                 data.put("PRODUCT_NAME", catalog.getString("CATALOG_NAME"));
		                 data.put("LABLE_ID", catalog.getString("UP_CATALOG_ID"));
		                 data.put("PRODUCT_ID_NAME", catalog.getString("CATALOG_ID") + "|" + catalog.getString("CATALOG_NAME"));
		                 rtnSet.add(data);
		             }
		         }
		     }
		 }else{
//			 rtnSet = BatSaleActiveBean.getPackagesLists(params);
		     String productId = params.getString("PRODUCT_ID");
		     IDataset offers = UpcCall.qryOffersByCatalogId(productId);
             if(IDataUtil.isNotEmpty(offers))
             {
                 IData catalog = UpcCall.qryCatalogByCatalogId(productId).getData(0);
                 String lableId = catalog.getString("UP_CATALOG_ID");
                 
                 if("|YX02|YX03|YX04|YX05|YX06|YX07|YX08|YX09|YX10|YX11|YX12|YX13|".indexOf(lableId) > 0)
                 {
                     for(int i=0;i<offers.size();i++)
                     {
                         IData offer = offers.getData(i);
                         String offerCode = offer.getString("OFFER_CODE");
                         String offerName = offer.getString("OFFER_NAME");
                         
                         IDataset paraInfos = CommparaInfoQry.getCommparaAllCol("CSM", "605", offerCode, this.getTradeEparchyCode());
                         if(IDataUtil.isNotEmpty(paraInfos))
                         {
                             IDataset tempChas = UpcCall.queryTempChaByOfferTable("K", offerCode, "TD_B_PACKAGE_EXT");
                             IData cha = tempChas.getData(0);
                             
                             IData data = new DataMap();
                             data.put("PACKAGE_ID", offerCode);
                             data.put("PACKAGE_NAME", offerName);
                             data.put("PRODUCT_ID", productId);
                             data.put("CAMPN_TYPE", lableId);
                             data.put("RSRV_STR2", cha.getString("RSRV_STR2"));
                             data.put("RSRV_STR5", cha.getString("RSRV_STR5"));
                             data.put("PACKAGE_ID_NAME", offerCode + "|" + offerName);
                             rtnSet.add(data);
                         }
                     }
                 }
             }
		     
			 PackagePrivUtil.filterPackageListByPriv(getVisit().getStaffId(), rtnSet);//包权限控制
		 }
		 	return rtnSet;
	    }
	 
	 public void importBatData(IData input) throws Exception
	 {
		 BatSaleActiveBean bean = (BatSaleActiveBean) BeanManager.createBean(BatSaleActiveBean.class);
		 bean.importBatData(input);
	 }
	 
	 public void dealSubmit(IData input) throws Exception
	 {
		 BatSaleActiveBean bean = (BatSaleActiveBean) BeanManager.createBean(BatSaleActiveBean.class);
		 bean.dealSubmit(input);
	 }
	 
	  public IData  replyTradeInft(IData input) throws Exception
	  {
		  BatSaleActiveBean bean = (BatSaleActiveBean) BeanManager.createBean(BatSaleActiveBean.class);
		  return bean.replyTradeInft(input);
	   }
	 
	 public IDataset queryImportData(IData input) throws Exception
	 {
		 BatSaleActiveBean bean = (BatSaleActiveBean) BeanManager.createBean(BatSaleActiveBean.class);
		 return bean.queryImportData(input,getPagination());
	 }

	public IDataset checkIsNeedPay(IData input) throws Exception
	{
		BatSaleActiveBean bean = (BatSaleActiveBean) BeanManager.createBean(BatSaleActiveBean.class);
		return bean.checkIsNeedPay(input);
	}



}
