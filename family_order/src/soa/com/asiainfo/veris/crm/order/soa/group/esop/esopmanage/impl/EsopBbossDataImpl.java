package com.asiainfo.veris.crm.order.soa.group.esop.esopmanage.impl;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.iorder.pub.consts.EcConstants;
import com.asiainfo.veris.crm.iorder.pub.consts.EcEsopConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IDealEospUtil;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformProductBean;


public class EsopBbossDataImpl extends EsopBaseDataImpl {

	@Override
	public void queryOfferInfo() throws Exception
	{
		IDataset eopProudctList = WorkformProductBean.qryProductByIbsysid(ibsysId);
		String operType = repMap.getString("BUSIFORM_OPER_TYPE");
		String operCode = EcEsopConstants.BbossOperTypeToCrmOperCode(operType);
		if (IDataUtil.isNotEmpty(eopProudctList)) 
		{
			IDataset subofferlist = new DatasetList();
			for (int i = 0,iSize= eopProudctList.size(); i < iSize; i++) 
			{
				IData data = eopProudctList.getData(i);
				String productId = data.getString("PRODUCT_ID");
				String recordNum = data.getString("RECORD_NUM");
				String subIbsysId_RNum = subIbsysid+"_"+recordNum;
				if (mainProdcutId.equals(productId))//主商品
				{
					offers.put("OFFER_CODE", productId);
					offers.put("OFFER_ID", UProductInfoQry.getOfferIdByProductId(productId));
					offers.put("BRAND_CODE",  UProductInfoQry.getBrandCodeByProductId(productId));
					offers.put("OFFER_NAME", data.getString("PRODUCT_NAME"));
					offers.put("OPER_CODE", operCode);
					offers.put("OFFER_INDEX", recordNum);
					offers.put("START_DATE", SysDateMgr.getSysTime());
					offers.put("END_DATE", SysDateMgr.getTheLastTime());
					offers.put(EcConstants.SUBIBID_RNUM, subIbsysId_RNum);//需要登记到CRM-tf_B_trade表RSRV_STR4，用于CRM与ESOP产品对应关系 
					offers.put("USER_ID", data.getString("USER_ID"));
					
					//查询Attr表数据转换成offerchaspecs格式
					IDataset offerChaSpecs = IDealEospUtil.qryAttrTranOfferChaSpecs(repMap.getString("SUB_IDSYSID"), recordNum);
					if (IDataUtil.isNotEmpty(offerChaSpecs)) 
					{
						offers.put("OFFER_CHA_SPECS", offerChaSpecs);
					}
					
				}
				else //子商品
				{
					IData suboffers = new DataMap();
					suboffers.put("OFFER_CODE", productId);
					suboffers.put("OFFER_ID", UProductInfoQry.getOfferIdByProductId(productId));
					suboffers.put("BRAND_CODE", UProductInfoQry.getOfferIdByProductId(productId));
					suboffers.put("OFFER_NAME", data.getString("PRODUCT_NAME"));
					suboffers.put("GROUP_ID", "-1");
					suboffers.put("OPER_CODE", operCode);
					suboffers.put("OFFER_INDEX", recordNum);
					suboffers.put(EcConstants.SUBIBID_RNUM, subIbsysId_RNum);//需要登记到CRM-tf_B_trade表RSRV_STR4，用于CRM与ESOP产品对应关系
					suboffers.put("START_DATE", SysDateMgr.getSysTime());
					suboffers.put("END_DATE", SysDateMgr.getTheLastTime());
					suboffers.put("USER_ID", data.getString("USER_ID"));
					//查询Attr表数据转换成offerchaspecs格式
					IDataset offerChaSpecs = IDealEospUtil.qryAttrTranOfferChaSpecs(repMap.getString("SUB_IDSYSID"), recordNum);
					if (IDataUtil.isNotEmpty(offerChaSpecs)) 
					{
						suboffers.put("OFFER_CHA_SPECS", offerChaSpecs);
					}
					subofferlist.add(suboffers);
					offers.put("SUBOFFERS", subofferlist);
				}
			}
			
		}
	}
	
}
