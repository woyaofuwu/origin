/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.grpvaluecard;

import java.math.BigInteger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.SqlUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.DevicePriceQry;

/**
 * @CREATED by gongp@2014-5-13 修改历史 Revision 2014-5-13 下午04:46:12
 */
public class GRPValueCardMgrBean extends CSBizBean {

	public void addSaleStaffInfo(IDataset cardList) throws Exception {

		for (int i = 0, size = cardList.size(); i < size; i++) {
			IData temp = cardList.getData(i);
			temp.put("SALE_TIME", SysDateMgr.getSysTime());
			temp.put("SALE_STAFF_ID", getVisit().getStaffId());
			if ("0".equals(temp.getString("SALE_TAG"))) {
				temp.put("SALE_TAG", "未销售");
			} else if ("1".equals(temp.getString("SALE_TAG"))) {
				temp.put("SALE_TAG", "已销售");
			} else if ("2".equals(temp.getString("SALE_TAG"))) {
				temp.put("SALE_TAG", "已赠送");
			} else {
				temp.put("SALE_TAG", "未知");
			}
		}
	}

	/**
	 * 获取的卡号是否连续
	 * 
	 * @param data
	 * @throws Exception
	 */
	public void checkISConnect(IData data, IDataset cardList) throws Exception {
		String start = data.getString("START_CARD_NO");
		String end = data.getString("END_CARD_NO");
		int start4 = Integer.parseInt(start.substring(start.length() - 4, start.length()));
		int end4 = Integer.parseInt(end.substring(end.length() - 4, end.length()));
		String card = null;
		String temp = null;
		boolean isExist = false;
		for (int i = start4; i <= end4; i++) {
			isExist = false;
			temp = "0000" + i;
			card = start.substring(0, start.length() - 4) + temp.substring(temp.length() - 4, temp.length());
			for (int j = 0; j < cardList.size(); j++) {
				if (card.equals(cardList.get(j, "VALUE_CARD_NO"))) {
					isExist = true;
					break;
				}
			}
			if (!isExist) {
				CSAppException.apperr(ParamException.CRM_PARAM_53, card);
			}
		}
	}

	/**
	 * 规则校验：只能是同类型、同面值的卡
	 * 
	 * @param dataset1
	 * @throws Exception
	 */
	public void checkRule(IDataset dataset) throws Exception {

		String kindCode = dataset.getData(0).getString("RES_KIND_CODE");
		String ADVISE_PRICE = dataset.getData(0).getString("ADVISE_PRICE");
		String cardKindCode = dataset.getData(0).getString("CARD_KIND_CODE") == null ? "" : dataset.getData(0).getString("CARD_KIND_CODE");
		String activeFlag = dataset.getData(0).getString("ACTIVE_FLAG");
		for (int i = 0; i < dataset.size(); i++) {
			IData temp = dataset.getData(i);
			if (!kindCode.equals(temp.getString("RES_KIND_CODE"))) {
				CSAppException.apperr(CrmCardException.CRM_CARD_210, temp.getString("VALUE_CARD_NO"));
			}
			if ("31m".equals(temp.getString("RES_KIND_CODE").substring(0, 3))) {
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "刮刮卡不能在此办理!");
			}
			if (!ADVISE_PRICE.equals(temp.getString("ADVISE_PRICE"))) {
				CSAppException.apperr(CrmCardException.CRM_CARD_212, temp.getString("VALUE_CARD_NO"));
			}
			if (null != temp.getString("CARD_KIND_CODE")) {
				if (!cardKindCode.equals(temp.getString("CARD_KIND_CODE"))) {
					CSAppException.apperr(CrmCardException.CRM_CARD_215, temp.getString("VALUE_CARD_NO"));
				}
			}
			if (StringUtils.isBlank(activeFlag) || StringUtils.isBlank(temp.getString("ACTIVE_FLAG"))) {
				CSAppException.apperr(CrmCardException.CRM_CARD_206);
			}
			if (!activeFlag.equals(temp.getString("ACTIVE_FLAG"))) {
				CSAppException.apperr(CrmCardException.CRM_CARD_273);
			}

		}
	}

	public IData createTable2(IData data, IDataset cardList) throws Exception {
		String tradeTypeCode = data.getString("TRADE_TYPE_CODE");
		IData tableInfo = new DataMap();
		tableInfo.put("RES_KIND_CODE", cardList.getData(0).getString("RES_KIND_CODE", ""));
		tableInfo.put("startCardNo", data.getString("START_CARD_NO", ""));
		tableInfo.put("endCardNo", data.getString("END_CARD_NO", ""));
		// tableInfo.put("VALUE_CODE",
		// cardList.getData(0).getString("VALUE_CODE", ""));
		// 获取单价
		String realPrice = cardList.getData(0).getString("RSRV_NUM1");

		float singlePrice = 0;
		float oldCardPrice = 0;

		String devicePrice = getDevicePrice(cardList.getData(0));
		singlePrice = getSinglePrice(devicePrice, data);
		String cardKindCode = cardList.getData(0).getString("RES_TYPE_CODE", "");
		tableInfo.put("singlePrice", singlePrice / 100);// 单价(元)
		tableInfo.put("totalPrice", singlePrice * cardList.size() / 100);// 总价
		tableInfo.put("rowCount", cardList.size());

		tableInfo.put("valueCode", cardList.getData(0).getString("VALUE_CODE", "")); // 原价格代码
		tableInfo.put("advise_price", cardList.getData(0).getString("ADVISE_PRICE", ""));// 原价
		tableInfo.put("devicePrice", cardList.getData(0).getString("ADVISE_PRICE", ""));// 原价
		tableInfo.put("cardType", cardKindCode.substring(cardKindCode.length() - 1));// 
		tableInfo.put("activeFlag", cardList.getData(0).getString("ACTIVE_FLAG", ""));

		return tableInfo;
	}

	public IData getCardInfoParam(String tradeTypeCode, IData param, int iTag) throws Exception {
		IData data = new DataMap();
		data.put("RES_TYPE_CODE", "3");

		data.put("STOCK_ID", getVisit().getDepartId());
		data.put("RES_NO_S", param.getString("START_CARD_NO"));
		data.put("RES_NO_E", param.getString("END_CARD_NO"));
		return data;
	}

	/**
	 * @param cardSet
	 * @return
	 * @throws Exception
	 * @CREATE BY GONGP@2014-7-4
	 */
	public String getDevicePrice(IData cardSet) throws Exception {
		String valueCode = cardSet.getString("VALUE_CODE");

		if (StringUtils.isBlank(valueCode)) {
			valueCode = cardSet.getString("RES_KIND_CODE").substring(cardSet.getString("RES_KIND_CODE").length() - 1);
		}

		IDataset devicePrice = DevicePriceQry.getDevicePrices(cardSet.getString("RES_TYPE_CODE"), cardSet.getString("RES_KIND_CODE"), "-1", valueCode, "461", "-1", "Z", "0898");

		String price = "0";

		if (IDataUtil.isNotEmpty(devicePrice)) {
			for (int i = 0; i < devicePrice.size(); i++) {
				IData temp = devicePrice.getData(i);
				if ((null != cardSet.getString("CARD_KIND_CODE")) && (!"".equals(cardSet.getString("CARD_KIND_CODE"))) && (cardSet.getString("CARD_KIND_CODE").equals(devicePrice.get(i, "CARD_KIND_CODE")))) {
					price = temp.getString("DEVICE_PRICE");
					return price;
				} else if (null == temp.getString("CARD_KIND_CODE") || "-1".equals(temp.getString("CARD_KIND_CODE"))) {
					price = temp.getString("DEVICE_PRICE");
				}
			}
		} else {
			// common.error("没有获取到有效的有价卡售价参数，该段卡无法销售！");
			CSAppException.apperr(CrmCardException.CRM_CARD_116);
		}

		return price;
	}

	/**
	 * 根据前台是否打折单选框获取单价
	 * 
	 * @param cardPrice
	 * @return
	 * @throws Exception
	 */
	public Float getSinglePrice(String cardPrice, IData data) throws Exception {

		String radio = data.getString("baseinfo_radio", "");
		Float salePrice = null; // 销售价格
		if ("a".equals(radio)) {
			salePrice = Float.parseFloat(cardPrice);
		} else {
			String discount = data.getString("DISCOUNT", "");
			String strPrice = data.getString("SALEPRICE", "");
			if (!"".equals(discount)) {
				salePrice = Float.parseFloat(cardPrice) * Float.parseFloat(discount) / 10;
			} else if (!"".equals(strPrice)) {
				salePrice = Float.parseFloat(strPrice) * 100;
			} else {
				CSAppException.apperr(CrmCommException.CRM_COMM_152);
			}
		}
		return salePrice;
	}

	/**
	 * 批量获取有价卡信息，包括有价卡销售，返销的数据
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData getValeCardListInfo(IData data) throws Exception {// 461="流量卡销售"
		String tradeTypeCode = data.getString("TRADE_TYPE_CODE");
		IDataset cardList = new DatasetList();// 资源接口获取的有价卡信息

		// 流量卡销售
		IData param = getCardInfoParam(tradeTypeCode, data, 0);
		cardList = ResCall.checkResourceForFlowValueCard(param.getString("RES_NO_S", ""), param.getString("RES_NO_E", ""), param.getString("STOCK_ID", ""), param.getString("RES_TYPE_CODE", ""), tradeTypeCode);

		if (IDataUtil.isEmpty(cardList) || "0".equals(cardList.get(0, "X_RECORDNUM"))) {
			CSAppException.apperr(CrmCardException.CRM_CARD_208);
		}

		// 检查流量卡段是否连续
		checkISConnect(data, cardList);
		// 海南流量卡销售校验规则
		this.hainCheckRule(cardList, tradeTypeCode, data.getString("START_CARD_NO"), data.getString("END_CARD_NO"));
		// 验证必须为同一类型、同一面值的卡
		checkRule(cardList);
		// 添加销售员工，时间
		addSaleStaffInfo(cardList);
		// 构造table2 数据
		IData table2 = createTable2(data, cardList);

		// 组织结果集
		IData result = new DataMap();
		result.put("TABLE1", cardList);
		IDataset tableSet = new DatasetList();
		tableSet.add(table2);
		result.put("TABLE2", tableSet);
		return result;
	}

	public IData getValueCardInfo(IData data) throws Exception {

		String tradeTypeCode = data.getString("TRADE_TYPE_CODE");

		// 获取流量卡信息，销售返销。
		return getValeCardListInfo(data);
	}

	/***************************************************************************
	 * 海南特殊校验规则
	 * 
	 * @param args
	 */
	private void hainCheckRule(IDataset args, String tradeTypeCode, String startNo, String endNo) throws Exception {
		if (null != args) {
			// 20120330
			BigInteger end = new BigInteger(endNo);
			BigInteger start = new BigInteger(startNo);
			BigInteger one = new BigInteger("1");
			BigInteger num = end.subtract(start).add(one);
			BigInteger size = BigInteger.valueOf(args.size());
			int count = num.intValue();
			if (args.size() < count) {
				StringBuilder result = new StringBuilder("");
				int hk = 0;
				for (int i = 0; i < count; i++) {
					String tempId = (start.add(BigInteger.valueOf(i))) + "";
					if (tempId.length() < startNo.length()) {
						int t = startNo.length() - tempId.length();
						for (int jj = 0; jj < t; jj++) {
							tempId = "0" + tempId;
						}
					}
					for (int j = 0; j < args.size(); j++) {
						if ((tempId).equals(args.get(j, "VALUE_CARD_NO"))) {
							break;
						} else {
							if (j == args.size() - 1) {
								if (hk != 0) {
									result.append(",");
								}
								result.append(tempId);
								hk = 1;
							}
						}
					}
				}
				// common.error("输入卡段不是全满足操作条件["+result.toString()+"]"+"，请减少卡段范围分批销售！");
			}
			for (int i = 0; i < args.size(); i++) {
				if ("f".equals(args.get(i, "RES_KIND_CODE"))) {
					// common.error("不能销售话费卡！");
					CSAppException.apperr(CrmCardException.CRM_CARD_220);
				}
			}
		}
	}

	/**
	 * 流量卡使用查询
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 * @Author:chenzg
	 * @Date:2017-1-9
	 */
	public IDataset queryGrpValueCardUseInfo(IData params, Pagination page) throws Exception {
		SQLParser parser = new SQLParser(params);
		parser.addSQL(SqlUtil.trimSql("SELECT A.TRADE_ID,																"));
		parser.addSQL(SqlUtil.trimSql("       A.ACCEPT_MONTH,                                                           "));
		parser.addSQL(SqlUtil.trimSql("       A.TRADE_TYPE_CODE,                                                        "));
		parser.addSQL(SqlUtil.trimSql("       A.KIND_NAME,                                                              "));
		parser.addSQL(SqlUtil.trimSql("       A.CARD_NUMBER,                                                            "));
		parser.addSQL(SqlUtil.trimSql("       (A.DEVICE_PRICE / 100) DEVICE_PRICE,                                      "));
		parser.addSQL(SqlUtil.trimSql("       A.AUDITORDERNUMBER,                                                       "));
		parser.addSQL(SqlUtil.trimSql("       A.VALID_DATA,                                                             "));
		parser.addSQL(SqlUtil.trimSql("       A.SALE_TAG,                                                               "));
		parser.addSQL(SqlUtil.trimSql("       A.LOCATION,                                                               "));
		parser.addSQL(SqlUtil.trimSql("       A.TRADE_STAFF_ID,                                                         "));
		parser.addSQL(SqlUtil.trimSql("       A.ACCEPT_DATE,                                                            "));
		parser.addSQL(SqlUtil.trimSql("       A.SALE_PRICE,                                                             "));
		parser.addSQL(SqlUtil.trimSql("       A.CUST_NUMBER,                                                            "));
		parser.addSQL(SqlUtil.trimSql("       A.CUST_ID,                                                                "));
		parser.addSQL(SqlUtil.trimSql("       A.GROUP_ID,                                                               "));
		parser.addSQL(SqlUtil.trimSql("       B.CUST_NAME GROUP_NAME,                                                   "));
		parser.addSQL(SqlUtil.trimSql("       A.DISCNT_TRADE_ID,                                                        "));
		parser.addSQL(SqlUtil.trimSql("       A.RECORD_TIME,                                                            "));
		parser.addSQL(SqlUtil.trimSql("       A.CITY_CODE,                                                              "));
		parser.addSQL(SqlUtil.trimSql("       A.DEPART_CODE,                                                            "));
		parser.addSQL(SqlUtil.trimSql("       DECODE(A.STATE_CODE,'2','',4,'',A.UPDATE_TIME) UPDATE_TIME,               "));
		parser.addSQL(SqlUtil.trimSql("       A.STATE_NAME,                                                             "));
		parser.addSQL(SqlUtil.trimSql("       A.STATE_CODE,                                                             "));
		parser.addSQL(SqlUtil.trimSql("       A.REMARK,                                                                 "));
		parser.addSQL(SqlUtil.trimSql("       A.RSRV_STR1,                                                              "));
		parser.addSQL(SqlUtil.trimSql("       A.RSRV_STR2,                                                              "));
		parser.addSQL(SqlUtil.trimSql("       A.RSRV_STR3,                                                              "));
		parser.addSQL(SqlUtil.trimSql("       A.RSRV_STR4,                                                              "));
		parser.addSQL(SqlUtil.trimSql("       A.RSRV_STR5,                                                              "));
		parser.addSQL(SqlUtil.trimSql("       A.RSRV_STR6,                                                              "));
		parser.addSQL(SqlUtil.trimSql("       A.RSRV_STR7,                                                              "));
		parser.addSQL(SqlUtil.trimSql("       A.RSRV_STR8,                                                              "));
		parser.addSQL(SqlUtil.trimSql("       A.RSRV_STR9,                                                              "));
		parser.addSQL(SqlUtil.trimSql("       A.RSRV_STR10                                                              "));
		parser.addSQL(SqlUtil.trimSql("  FROM TL_B_VALUECARD_DETAILED A, TF_F_CUST_GROUP B                              "));
		parser.addSQL(SqlUtil.trimSql(" WHERE A.GROUP_ID = B.GROUP_ID                                                   "));
		parser.addSQL(SqlUtil.trimSql("   AND A.TRADE_TYPE_CODE = 461                                                   "));
		parser.addSQL(SqlUtil.trimSql("   AND A.CARD_NUMBER BETWEEN :START_CARD_NUMBER AND :END_CARD_NUMBER             "));
		parser.addSQL(SqlUtil.trimSql("   AND A.GROUP_ID = :GROUP_ID                                                    "));
		parser.addSQL(SqlUtil.trimSql("   AND B.CUST_NAME LIKE '%'||:GROUP_NAME||'%'                                    "));
		parser.addSQL(SqlUtil.trimSql("   AND A.STATE_CODE = :STATE_CODE                                                "));
		IDataset results = Dao.qryByParse(parser, page);
		return results;
	}

	/**
	 * 流量卡查询资源 信息
	 * <p>
	 * Title: queryValuecardInfo
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * <p>
	 * Company: AsiaInfo
	 * </p>
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 * @author XUYT
	 * @date 2017-1-13 上午11:18:09
	 */
	public IDataset queryValuecardInfo(IData data) throws Exception {
		IDataset cardList = new DatasetList();

		cardList = ResCall.queryValuecardInfo(data);

		return cardList;
	}

}
