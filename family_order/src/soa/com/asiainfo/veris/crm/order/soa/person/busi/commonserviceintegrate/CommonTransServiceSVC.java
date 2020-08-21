package com.asiainfo.veris.crm.order.soa.person.busi.commonserviceintegrate;

import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CommonTransServiceException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.rule.CRMMVELMiscCache;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script.MVELExecutor;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script.MVELMiscCache;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCallIntf;

public class CommonTransServiceSVC extends CSBizService {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(CommonTransServiceSVC.class);
	private static final String FORWORD_TYPE_ORDER_CENTER = "1";
	private static final String FORWORD_TYPE_IUPC_CENTER = "2";
	private static final String FORWORD_TYPE_MVEL = "3";

	/**
	 * 按类型调用接口
	 * 
	 * @param input INTF_NAME:string,INPUT_PARAM:data
	 * @return
	 * @throws Exception
	 */
	public IDataset dispatcherService(IData input) throws Exception {
		String intfName = IDataUtil.chkParam(input, "INTF_NAME");

		IDataset dataset = SvcColleConfigBean.getServiceCollectionByIntfName(intfName, CSBizBean.getTradeEparchyCode());
		if (IDataUtil.isEmpty(dataset)) {
			CSAppException.apperr(CommonTransServiceException.COMMON_TRANS_SERVICE_5);
		}
		IData svcColleConfig = dataset.getData(0);

		IData inputParam = getData(input, "INPUT_PARAM");
		String forwordType = svcColleConfig.getString("FORWORD_TYPE");
		IDataset result = null;
		if (FORWORD_TYPE_ORDER_CENTER.equals(forwordType)) {// 订单中心
			result = callOrderCenterService(svcColleConfig, inputParam);
		} else if (FORWORD_TYPE_IUPC_CENTER.equals(forwordType)) {// 产商品中心
			result = callIupcCenterService(svcColleConfig, inputParam);
		} else if (FORWORD_TYPE_MVEL.equals(forwordType)) {// MVEL
			result = callMvel(svcColleConfig, inputParam);
		} else {
			CSAppException.apperr(CommonTransServiceException.COMMON_TRANS_SERVICE_7);
		}

		return result;
	}

	public IDataset callMvel(IData svcColleConfig, IData inputParam) throws Exception {
		String template = svcColleConfig.getString("SVC_NAME");
		this.setRouteId(CSBizBean.getTradeEparchyCode());
		MVELMiscCache miscCache = CRMMVELMiscCache.getMacroCache();
		MVELExecutor exector = new MVELExecutor();
		exector.setMiscCache(miscCache);
		exector.prepare(inputParam);
		Object data = exector.execTemplate(template);
		IDataset result = IDataUtil.idToIds(data);
		return result;
	}

	private IDataset callIupcCenterService(IData svcColleConfig, IData inputParam) throws Exception {
		String svcName = svcColleConfig.getString("SVC_NAME");
		checkInputParam(svcColleConfig, inputParam);
		Object data = UpcCallIntf.qryUpcDataInfos(svcName, inputParam);
		IDataset result = IDataUtil.idToIds(data);
		return result;
	}

	private IDataset callOrderCenterService(IData svcColleConfig, IData inputParam) throws Exception {
		String svcName = svcColleConfig.getString("SVC_NAME");
		checkInputParam(svcColleConfig, inputParam);
		IDataset result = CSAppCall.call(svcName, inputParam);
		return result;
	}

	private void checkInputParam(IData svcColleConfig, IData inputParam) throws Exception {
		String paraCode1 = svcColleConfig.getString("CHECK_NECESSARY_PARAM");
		if (StringUtils.isNotEmpty(paraCode1)) {
			String[] keys = paraCode1.split(",");
			for (int i = 0; i < keys.length; i++) {
				IDataUtil.chkParam(inputParam, keys[i].trim());
			}
		}
	}

	/**
	 * 获取IData类型数据
	 * 
	 * @param input
	 * @param key
	 * @return
	 */
	private IData getData(IData input, String key) {
		IData result = null;
		if (input.containsKey(key)) {
			Object o = input.get(key);
			if (o instanceof String) {
				result = new DataMap((String) o);
			} else if (o instanceof IData) {
				result = (IData) o;
			} else if (o instanceof Map) {
				result = DataHelper.mapToIData((Map) o);
			}
		}
		return result;
	}

}
