package com.asiainfo.veris.crm.order.soa.person.busi.popularity;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ExchangeRuleInfoQry;

public class PopularityManageBean extends CSBizBean {

	/**
	 * 热门推荐业务查询
	 * @param params
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public IDataset queryPopularityInfo(IData params, Pagination pagination) throws Exception {
		IData param = new DataMap();
		param.put("OFFER_CODE", params.getString("QRY_OFFER_CODE",""));
		param.put("OFFER_NAME", params.getString("QRY_OFFER_NAME",""));
		param.put("POPULARITY_TRADE_TYPE", params.getString("QRY_POPULARITY_TRADE_TYPE",""));
		param.put("POPULARITY_TYPE", params.getString("QRY_POPULARITY_TYPE",""));
		param.put("START_DATE", params.getString("QRY_START_DATE",""));
		param.put("END_DATE", params.getString("QRY_END_DATE",""));
		
		return Dao.qryByCodeParser("TF_F_POPULARITY", "SEL_POPULARITY_INFO", param, pagination);
	}

	/**
	 * 热门推荐业务维护
	 * @param input
	 * @throws Exception
	 */
	public void managePopularityInfo(IData input) throws Exception {
		if (IDataUtil.isNotEmpty(input)) {
			IDataset popularityList = input.getDataset("POPULARITY_LIST");
			if (IDataUtil.isNotEmpty(popularityList)) {
				// 由于新增、修改导致优先级变化，需要同步修改表中其它推荐数据的优先级。
				for (int i = 0; i < popularityList.size(); i++) {
					IData popularityData = popularityList.getData(i);
					
					IData param = new DataMap();
					param.put("POPULARITY_TRADE_TYPE", popularityData.getString("POPULARITY_TRADE_TYPE",""));
					param.put("POPULARITY_TYPE", popularityData.getString("POPULARITY_TYPE",""));
					param.put("PRIORITY", popularityData.getString("PRIORITY",""));
					param.put("UPDATE_TIME", popularityData.getString("UPDATE_TIME",""));
					IDataset datas = Dao.qryByCodeParser("TF_F_POPULARITY", "SEL_POPULARITY_INFO_BY_PRIORITY", param); // 将优先级作为查询条件，如果新增或者修改后的数据的优先级没有重复，则其它推荐数据的优先级不需要后移
					
					String tag = popularityData.getString("tag", "");
					if (IDataUtil.isNotEmpty(datas) && ("0".equals(tag) || "2".equals(tag))) { // 新增 || 修改
						param.put("UPDATE_STAFF_ID", popularityData.getString("UPDATE_STAFF_ID",""));
						param.put("UPDATE_DEPART_ID", popularityData.getString("UPDATE_DEPART_ID",""));
						param.put("UPDATE_TIME", popularityData.getString("UPDATE_TIME",""));
						Dao.executeUpdateByCodeCode("TF_F_POPULARITY", "UPD_POPULARITY_INFO_BY_PRIORITY", param);
					}
				}
				// 新增、删除、修改数据
				for (int i = 0; i < popularityList.size(); i++) {
					IData popularityData = popularityList.getData(i);
					String tag = popularityData.getString("tag", "");
					if ("0".equals(tag)) { // 新增
				        StringBuilder sql = new StringBuilder();
				        sql.append("SELECT TF_F_POPULARITY$SEQ.NEXTVAL FROM DUAL");
						IDataset seq = Dao.qryBySql(sql, null, Route.CONN_CRM_CG);
						popularityData.put("POPULARITY_ID", seq.getData(0).getString("NEXTVAL"));
						Dao.insert("TF_F_POPULARITY", popularityData);
					}

					if ("1".equals(tag)) { // 删除
						Dao.delete("TF_F_POPULARITY", popularityData);
					}

					if ("2".equals(tag)) { // 修改
						Dao.update("TF_F_POPULARITY", popularityData);
					}
				}
			}
		}
	}
	
	/**
	 * 查询产品编码信息
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData queryCode(IData input) throws Exception{
		IData returtnData = new DataMap();
		IDataset results = null;
		
		String offerCode = input.getString("OFFER_CODE");
		String type = input.getString("POPULARITY_TRADE_TYPE");
		if("1".equals(type) || "3".equals(type)){	//1:宽带,3:套餐
			results = UpcCall.queryOfferInfoByOfferCodeAndOfferType(offerCode, "P");
			if(DataUtils.isNotEmpty(results)){
				setReturnValue(results.first(), type);
				returtnData = results.first();
			}
		}
		if("2".equals(type)){	//营销活动
			results = UpcCall.queryOfferInfoByOfferCodeAndOfferType(offerCode, "K");
			if(DataUtils.isNotEmpty(results)){
				IDataset results1 = UpcCall.qryCatalogByOfferId(offerCode, "K");
				if(DataUtils.isNotEmpty(results1)){
					String catalogId = results1.first().getString("CATALOG_ID");
					String catalogName = results1.first().getString("CATALOG_NAME");
					String upCatalogId = results1.first().getString("UP_CATALOG_ID");
					
					results.first().put("PRODUCT_NAME", catalogName);
					results.first().put("PRODUCT_ID", catalogId);
					
					IDataset results2 = UpcCall.qryCatalogByCatalogId(upCatalogId);
					if(DataUtils.isNotEmpty(results2)){
						String upCatalogName = results2.first().getString("CATALOG_NAME");
						
						results.first().put("CAMPN_TYPE", upCatalogId);
						results.first().put("CAMPN_NAME", upCatalogName);
					}
				} else {
	                CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据商品编码[" + offerCode + "]未查询到营销方案记录！");
				}
				setReturnValue(results.first(), type);
				returtnData = results.first();
			}
		}
		if("4".equals(type)){	//积分
			results = ExchangeRuleInfoQry.queryByRuleId(offerCode, getTradeEparchyCode());
			if(DataUtils.isNotEmpty(results)){
				setReturnValue(results.first(), type);
				returtnData = results.first();
			}
		}
		if("5".equals(type)){	//平台业务
			results = UpcCall.queryOfferInfoByOfferCodeAndOfferType(offerCode, "Z");
			if(DataUtils.isNotEmpty(results)){
				setReturnValue(results.first(), type);
				returtnData = results.first();
			}
		}
		if("9".equals(type)){	//其它
			return new DataMap();
		}
		return returtnData;
	}
	
	/**
	 * 返回字段转译
	 * @param param
	 * @param type
	 */
	public void setReturnValue(IData param, String type){
		if("4".equals(type)){
			param.put("OFFER_CODE", param.getString("RULE_ID",""));
			param.put("OFFER_NAME", param.getString("RULE_NAME",""));
			param.put("OFFER_DESCRIPTION", param.getString("RULE_NAME",""));
		}else{
			param.put("START_DATE", param.getString("VALID_DATE",""));
			param.put("END_DATE", param.getString("EXPIRE_DATE",""));
			param.put("OFFER_DESCRIPTION", param.getString("DESCRIPTION",""));
		}
		param.put("POPULARITY_TRADE_TYPE", type);
	}

	/**
	 * 根据类型和商品类型查询最大的优先级
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset queryMaxPriority(IData input) throws Exception{
		return Dao.qryByCodeParser("TF_F_POPULARITY", "SEL_POPULARITY_MAXPRIORITY", input);
	}
}