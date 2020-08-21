package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;


/**
 * 
 * 双跨融合通信业务处理类
 * @author luys
 *
 */
public class BbossDcrossFusionBizDealBean {
	 /**
	 * 双跨企业融合通信业务下产品发服开判断
	 * @param String
	 * @throws Exception
	 */
 public static boolean isBbossDcrossBizSendPF(String productId) throws Exception {
		boolean isSendFlag = false;

		IData isSendFlagData = StaticInfoQry.getStaticInfoByTypeIdDataId(
				"BBOSS_DCROSS_BIZ", productId);

		if (IDataUtil.isNotEmpty(isSendFlagData)) {
			isSendFlag = true;
		}

		return isSendFlag;
	}
}
