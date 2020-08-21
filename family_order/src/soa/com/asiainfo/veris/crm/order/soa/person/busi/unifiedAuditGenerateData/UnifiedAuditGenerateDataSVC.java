package com.asiainfo.veris.crm.order.soa.person.busi.unifiedAuditGenerateData;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class UnifiedAuditGenerateDataSVC extends CSBizService {
	private static final long serialVersionUID = 1L;

	/**
	 * (BUS201809040014)统一稽核生成数据
	 */

	/*
	 * 2.1.1 个人客户 2.1.2 用户经办人信息 2.1.3 用户使用人信息 2.2.1 用户(当前) 2.2.2 用户与策划实例的关系
	 * 2.2.3 鉴权信息 2.2.4 用户停开机信息 2.2.5 用户付费实例关系 2.2.6 策划实例信息 2.2.7 身份证识别仪验证信息
	 * 2.2.8 用户扩展 2.2.9 免填单信息 2.3.1 帐户 2.4.1 用户付费关系订单（未竣工） 2.4.2 用户付费关系订单(竣工)
	 * 2.4.3 客户订单订购策划信息（未竣工） 2.4.4 客户订单订购策划信息(竣工) 2.4.5 客户订单申请信息（未竣工） 2.4.6
	 * 客户订单申请信息（竣工） 2.4.7 客户订单费用信息（当前） 2.4.8 客户订单费用信息（竣工） 2.4.9 客户订单扩展属性信息 2.5.1
	 * 商品单元信息 2.5.2 科目单元信息 2.5.3 号码档次资源信息 2.5.4 号码档次对应套餐信息 2.5.5 商品扩展信息 2.5.6
	 * 商品权限单元信息 2.5.7 业务单元信息 2.6.1 操作员信息 2.6.2 组织授权关系 2.6.3 组织角色关系信息 2.6.4
	 * 授权和实体关系信息 2.6.5 组织扩展信息 2.7.1 账户资金流动记录 2.7.2 月客户订单订购策划信息(竣工) 2.7.3 积分转移
	 * 2.7.4 月客户订单申请信息（竣工） 2.7.5 客户订单订购策划信息（未竣工） 2.7.6 不可积分转移的用户信息 2.7.7
	 * 4A登录异常信息
	 */

	public IDataset query(IData input) throws Exception {
		UnifiedAuditGenerateDataBean bean = new UnifiedAuditGenerateDataBean();
		return bean.query(input);
	}

}
