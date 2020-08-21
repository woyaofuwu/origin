package com.asiainfo.veris.crm.iorder.soa.merch.merchShoppingCart;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ailk.biz.BizEnv;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.merch.merchShoppingCart.merge.MergeTradeConfig;
import com.asiainfo.veris.crm.iorder.soa.merch.merchShoppingCart.merge.intf.IMerge;
import com.asiainfo.veris.crm.iorder.soa.merch.merchShoppingCart.order.requestdata.MerchShoppingCartRequestData;
import com.asiainfo.veris.crm.iorder.soa.merch.merchShoppingCart.order.requestdata.MerchShoppingDetailData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.ShoppingCartConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.TwoCheckSms;

public class MerchShoppingCartRegSVC extends OrderService {
	private static final long serialVersionUID = -2669181254667443684L;

	@Override
	public String getOrderTypeCode() throws Exception {
		return ShoppingCartConst.ORDER_TYPE_CODE_SHOPPINGCART;
	}

	@Override
	public String getTradeTypeCode() throws Exception {
		return ShoppingCartConst.TRADE_TYPE_CODE_SHOPPINGCART;
	}

	@SuppressWarnings("rawtypes")
	public void otherTradeDeal(IData input, BusiTradeData btd) throws Exception {
		MerchShoppingCartRequestData shoppingCartReqData = (MerchShoppingCartRequestData) btd.getRD();
		List<MerchShoppingDetailData> shoppingDetailList = shoppingCartReqData.getShoppingDetailList();
		new MerchShoppingCartBean().modifyRequestData(shoppingDetailList, btd.getRD().getUca().getUserNewMainProductId());
		IDataset returnSet = new DatasetList();
		
		//默认合并
		boolean mergeFlag = BizEnv.getEnvBoolean("MERCH_MERGE_FLAG", true);
		List<MerchShoppingDetailData> mergeList = shoppingDetailList;
		if(mergeFlag)
			mergeList = mergeShoppingDetailList(shoppingDetailList);
		
		boolean twoFlag = false;
//		if(!"true".equals(shoppingCartReqData.getIsConfirm()) && cycleSendTwoCheckSmsFlag(mergeList) && shoppingCartReqData.isTwoCheckSmsFlag()){
//			twoFlag = true;
//			twoSmsCheck(shoppingCartReqData, input, mergeList);
//		}
		for (int index = 0; index < mergeList.size(); index++) {
            MerchShoppingDetailData shoppingDetailData = mergeList.get(index);
            IData tradePageData = shoppingDetailData.getDetailRequestData();
            String tradeRegSvc = tradePageData.getString("X_TRANS_CODE");
            if (StringUtils.equals("3700", tradePageData.getString("TRADE_TYPE_CODE")))
            {
                if(twoFlag){
                    tradePageData.put("PRE_TYPE", "1");
                    tradePageData.put("IS_SMS_AUTHENTICATION", "N");
                } else
                    tradePageData.remove("PRE_TYPE");
                returnSet = CSAppCall.call(tradeRegSvc, tradePageData);
                mergeList.remove(index);
                index--;
            }
        }
		
		for (int index = 0, size = mergeList.size(); index < size; index++) {
			MerchShoppingDetailData shoppingDetailData = mergeList.get(index);
			IData tradePageData = shoppingDetailData.getDetailRequestData();
			String tradeRegSvc = tradePageData.getString("X_TRANS_CODE");
			if(twoFlag){
				tradePageData.put("PRE_TYPE", "1");
				tradePageData.put("IS_SMS_AUTHENTICATION", "N");
			} else
				tradePageData.remove("PRE_TYPE");
			returnSet = CSAppCall.call(tradeRegSvc, tradePageData);
		}
		
		if (!twoFlag) {
			String shoppingCartId = shoppingCartReqData.getShoppingCartId();
			String orderId = returnSet.getData(0).getString("ORDER_ID");
			IData inputData = new DataMap();
			inputData.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
			inputData.put("SHOPPING_CART_ID", shoppingCartId);
			inputData.put("ORDER_ID", orderId);
			inputData.put("DETAIL_ORDER_IDS", input.getString("DETAIL_ORDER_IDS"));
			CSAppCall.call("SS.MerchShoppingCartSVC.dealCartBySubmit", inputData);
		}
	}
	
	/**
	 * 此处肯定会走二次确认。那么子工单就为预校验。每个子工单要传PRE_TYPE。先注释不影响测试环境
	 * 
	 * @param flag
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	private void twoSmsCheck(MerchShoppingCartRequestData shoppingCartReqData, IData pageInputParams,
			List<MerchShoppingDetailData> mergeList) throws Exception {
		// 获取短信内容
		String smsTradeTypeContent = createSmsTradeTypeContent(mergeList);

		StringBuilder smsContent = new StringBuilder();
		smsContent.append("尊敬的客户：号码");
		smsContent.append(shoppingCartReqData.getUca().getSerialNumber());
		smsContent.append("将要结算购物车");
		if (StringUtils.isNotBlank(smsTradeTypeContent))
			smsContent.append("【" + smsTradeTypeContent + "】");
		smsContent.append("；请在2小时内回复“是”确认订购，回复其他内容或不回复则不订购");
		this.saveTwoCheckRequest(shoppingCartReqData, smsContent.toString(), pageInputParams);
	}
	
	/**
	 * 组装短信 业务类型
	 * 
	 * @param mergeList
	 * @return
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public String createSmsTradeTypeContent(List<MerchShoppingDetailData> mergeList) throws Exception {
		StringBuffer smsBuff = new StringBuffer();
		for (int index = 0, size = mergeList.size(); index < size; index++) {
			MerchShoppingDetailData shoppingDetailData = mergeList.get(index);
			IData tradePageData = shoppingDetailData.getDetailRequestData();
			String tradeTypeCode = tradePageData.getString("TRADE_TYPE_CODE");
			IData tradeType = UTradeTypeInfoQry.getTradeType(tradeTypeCode, getVisit().getLoginEparchyCode());
			if (IDataUtil.isNotEmpty(tradeType))
				smsBuff.append(tradeType.getString("TRADE_TYPE")).append(",");
		}
		String smsContent = smsBuff.toString();
		return StringUtils.isNotBlank(smsContent) ? smsContent.substring(0, smsContent.length() - 1) : "";
	}
	
    private void saveTwoCheckRequest(MerchShoppingCartRequestData reqData, String smsContent, IData param) throws Exception
    {
        reqData.setPreType(BofConst.PRE_TYPE_SMS_CONFIRM);// 台账数据不插数据库

        IData data = new DataMap();
        data.putAll(param);
        data.put("PRE_TYPE", BofConst.SPEC_SVC_SEC);
        data.put("SVC_NAME", reqData.getXTransCode());
        data.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());
        data.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        data.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        data.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        data.put("THIS_FLAG", "1");

        IData twoCheckSms = new DataMap();
        twoCheckSms.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());
        twoCheckSms.put("SMS_CONTENT", smsContent);
        twoCheckSms.put("SMS_TYPE", BofConst.SPEC_SVC_SEC);
        twoCheckSms.put("OPR_SOURCE", "1");
        TwoCheckSms.twoCheck(reqData.getTradeType().getTradeTypeCode(), 2, data, twoCheckSms);
    }
	
	/**
	 * 合并满足要求的业务类型 (合也要根据 order_type_code 合并)
	 * 
	 * @param shoppingDetailList
	 * @return
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	@SuppressWarnings("unchecked")
	public List<MerchShoppingDetailData> mergeShoppingDetailList(List<MerchShoppingDetailData> shoppingDetailList)
			throws Exception {
		List<MerchShoppingDetailData> mergeList = new ArrayList<MerchShoppingDetailData>();
		IData mergeMap = new DataMap();
		for (int i = 0, size = shoppingDetailList.size(); i < size; i++) {
			MerchShoppingDetailData detailData = shoppingDetailList.get(i);
			String typeCode = detailData.getDetailTypeCode();// 就是这笔业务的order_type_code
			// 是否需要合并
			if (!MergeTradeConfig.checkIsMerge(typeCode)) {
				mergeList.add(detailData);
				continue;
			}
			if (mergeMap.containsKey(typeCode)) {
				((List<MerchShoppingDetailData>) mergeMap.get(typeCode)).add(detailData);
			} else {
				List<MerchShoppingDetailData> mergeListTmp = new ArrayList<MerchShoppingDetailData>();
				mergeListTmp.add(detailData);
				mergeMap.put(typeCode, mergeListTmp);
			}
		}
		if (IDataUtil.isNotEmpty(mergeMap))
			mergeRequestData(mergeMap, mergeList);
		return mergeList;
	}

	/**
	 * 合并请求数据
	 * 
	 * @param mergeMap
	 * @param mergeList
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	@SuppressWarnings("unchecked")
	public void mergeRequestData(IData mergeMap, List<MerchShoppingDetailData> mergeList) throws Exception {
		Iterator<String> it = mergeMap.keySet().iterator();
		while (it.hasNext()) {
			String typeCode = it.next();
			List<MerchShoppingDetailData> tmpList = (List<MerchShoppingDetailData>) mergeMap.get(typeCode);
			if (tmpList != null && tmpList.size() == 1)
				mergeList.add(tmpList.get(0));
			else if(tmpList != null)
				mergeList.addAll(mergeMerchTrade(typeCode, tmpList));
		}
	}

	/**
	 * 业务合并
	 * 
	 * @param typeCode
	 * @param merchList
	 * @return
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public List<MerchShoppingDetailData> mergeMerchTrade(String typeCode, List<MerchShoppingDetailData> merchList)
			throws Exception {
		IMerge merge = MergeTradeConfig.getMergeConfigByTypeCode(typeCode);
		if (merge == null)
			return merchList;
		List<MerchShoppingDetailData> mergeList = merge.executeMerge(merchList);
		return mergeList;
	}
	
	/**
	 * 循环判断是否需要发二次短信
	 * 
	 * @param mergeList
	 * @param btd
	 * @return
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	private boolean cycleSendTwoCheckSmsFlag(List<MerchShoppingDetailData> mergeList)throws Exception {
		if(mergeList == null || mergeList.size() == 0)
			return false;
		for (int index = 0, size = mergeList.size(); index < size; index++) {
			MerchShoppingDetailData shoppingDetailData = mergeList.get(index);
			IData tradePageData = shoppingDetailData.getDetailRequestData();
			String tradeTypeCode = tradePageData.getString("TRADE_TYPE_CODE");
			if("110".equals(tradeTypeCode) && isOnlyHasDeleteElement(tradePageData)){
				tradePageData.put("HAVE_DELETE", "TRUE");
			}
			if(isSendTwoCheckSmsFlag(tradeTypeCode, tradePageData))
				return true;
		}
		return false;
	}
	
	/**
	 * 判断产品变更是否只有删除的元素
	 * 
	 * @param requestData
	 * @return
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	private boolean isOnlyHasDeleteElement(IData requestData)throws Exception {
		if (StringUtils.isNotBlank(requestData.getString("NEW_PRODUCT_ID")))
			return false;
		IDataset selectElementSetTmp = new DatasetList(requestData.getString("SELECTED_ELEMENTS"));
		for (int i = 0, size = selectElementSetTmp.size(); i < size; i++) {
			IData element = selectElementSetTmp.getData(i);
			if(!"1".equals(element.getString("MODIFY_TAG")))
				return false;
		}
		return true;
	}
	
	private boolean isSendTwoCheckSmsFlag(String tradeTypeCode, IData param) throws Exception {
		boolean pac_confirm = false;
		// 营销活动走自己单独的二次确认逻辑，根据营销包权限（PACKAGE_CONFIRM）进行判断是否下发二次确认短信，并且营销包编码在1813参数中
		if ("240".equals(tradeTypeCode)) {
			String packageId = param.getString("PACKAGE_ID", "");
			// 根据营销包权限（PACKAGE_CONFIRM）进行判断是否下发二次确认短信
			if (!StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "PACKAGE_CONFIRM")) {
				// 营销包编码在1813参数中,否则不下发二次确认短信
				IDataset needParam = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "1813", "SEC_CONFIRM", packageId,
						CSBizBean.getVisit().getInModeCode(), CSBizBean.getTradeEparchyCode());// 判断是否为需要下发二次短信的营销包
				if (IDataUtil.isNotEmpty(needParam)) {
					pac_confirm = true;
					return true;
				}
			}
		}

		/**
		 * [2017]9645关于批量业务受理增加二次确认短信的需求公共逻辑 ADD BY WANGXY5 START
		 */
		// 判断业务类型和接入渠道是否在1811参数中
		IDataset commpara = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "1811", "SEC_CONFIRM",
				CSBizBean.getVisit().getInModeCode(), tradeTypeCode, CSBizBean.getTradeEparchyCode());
		// 3700有自己的二次确认逻辑，在PlatSmsAction中
		if (IDataUtil.isNotEmpty(commpara) && !"3700".equals(tradeTypeCode) && !pac_confirm) {
			param.put("SVC_NAME", commpara.getData(0).getString("PARA_CODE20", ""));
			param.put("TRADE_NAME", commpara.getData(0).getString("PARA_CODE21", ""));
			param.put("TIME", commpara.getData(0).getString("PARA_CODE3", ""));
			return second_Confirm(param);
		}
		return false;
	}
	
	/**
     * 
     * @author wangxy5
     * @param brd
     * @throws Exception
     * 二次短信确认需求,第一次改造allPri是总入口，只有这一个权限就下发二次确认短信，二次改造后分权限，
     * 只要有其中一个权限（allPri，packagePri，productPri）就下发短信
     */
	private boolean second_Confirm(IData data) throws Exception {
		// 判断工号是否有二次确认权限，并且判断产品变更是否只有删除元素，变更产品、新增优惠、新增服务下发短信，删除元素不下发短信
		boolean allPri = StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "STAFF_CONFIRM");
		boolean packagePri = (StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "STAFF_CONFIRM_PAC")
				&& "240".equals(data.getString("TRADE_TYPE_CODE")));
		boolean productPri = (StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "STAFF_CONFIRM_PRO")
				&& "110".equals(data.getString("TRADE_TYPE_CODE")));
		if ((allPri || packagePri || productPri)
				&& !(data.containsKey("HAVE_DELETE") && "TRUE".equals(data.getString("HAVE_DELETE")))) {
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("rawtypes")
	public void resetReturn(IData input, IData output, BusiTradeData btd) throws Exception
	{
	    if(BofConst.PRE_TYPE_SMS_CONFIRM.equals(btd.getRD().getPreType()))
	    {
            // 设置这个值是因为订单调用完后cssubmit.js中会提示有二次确认
            output.put("SEC_CONFIRM", BofConst.PRE_TYPE_CHECK);
	    }
	}
}
