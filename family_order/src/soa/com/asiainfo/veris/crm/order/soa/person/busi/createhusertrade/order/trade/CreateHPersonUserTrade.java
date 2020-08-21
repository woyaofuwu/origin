package com.asiainfo.veris.crm.order.soa.person.busi.createhusertrade.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.ProductData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.ProductModuleCreator;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.PasswdMgr;
import com.asiainfo.veris.crm.order.soa.person.busi.createhusertrade.order.requestdata.CreateHPersonUserRequestData;

public class CreateHPersonUserTrade extends BaseTrade implements ITrade {

	/**
	 * 创建开户具体业务台账
	 * 
	 * @author zhaohj3
	 * @param btd
	 * @throws Exception
	 * @date 2018-1-26 16:35:16
	 */
	public void createBusiTradeData(BusiTradeData btd) throws Exception {
		CreateHPersonUserRequestData createHPersonUserRD = (CreateHPersonUserRequestData) btd.getRD();
		createUserTradeData(btd);// 处理用户台账
		createCustomerTradeData(btd);// 处理客户核心资料
		createCustPersonTradeData(btd);// 处理客户个人资料
		createAcctTradeData(btd);// 处理账户资料
		createProductTradeData(btd);
		createTradePayRelation(btd);
		// 处理元素 构建产品
		ProductModuleCreator.createProductModuleTradeData(createHPersonUserRD.getPmds(), btd);

		// 处理分散账期 因为有预约的概念，需要修改 后续处理 sunxin
		btd.addOpenUserAcctDayData(createHPersonUserRD.getUca().getUserId(), createHPersonUserRD.getUca().getAcctDay());

		btd.addOpenAccountAcctDayData(createHPersonUserRD.getUca().getAcctId(), createHPersonUserRD.getUca().getAcctDay());
	}

	/**
	 * 生成台帐用户子表
	 * 
	 * @author zhaohj3
	 * @param btd
	 * @throws Exception
	 * @date 2018-1-26 16:35:16
	 */
	public void createUserTradeData(BusiTradeData btd) throws Exception {
		CreateHPersonUserRequestData createHPersonUserRD = (CreateHPersonUserRequestData) btd.getRD();
		String serialNumber = createHPersonUserRD.getUca().getUser().getSerialNumber();
		String userPasswd = createHPersonUserRD.getUca().getUser().getUserPasswd();
		String user_id = createHPersonUserRD.getUca().getUserId();
		UserTradeData userTD = createHPersonUserRD.getUca().getUser().clone();
		// 处理密码
		if (!"".equals(userPasswd)) {
			userPasswd = PasswdMgr.encryptPassWD(userPasswd, user_id);
		}

		userTD.setUserPasswd(userPasswd);
		if (!userTD.getModifyTag().equals(BofConst.MODIFY_TAG_USER)) {
			btd.add(serialNumber, userTD);
		}
	}

	/**
	 * 客户资料表
	 * 
	 * @author zhaohj3
	 * @param btd
	 * @throws Exception
	 * @date 2018-1-26 16:35:16
	 */
	public void createCustomerTradeData(BusiTradeData btd) throws Exception {
		CreateHPersonUserRequestData createHPersonUserRD = (CreateHPersonUserRequestData) btd.getRD();
		String serialNumber = createHPersonUserRD.getUca().getUser().getSerialNumber();
		CustomerTradeData customerTD = createHPersonUserRD.getUca().getCustomer().clone();
		
		if (!customerTD.getModifyTag().equals(BofConst.MODIFY_TAG_USER)) {
			btd.add(serialNumber, customerTD);
		}
	}

	/**
	 * 生成台帐个人客户表
	 * 
	 * @author zhaohj3
	 * @param btd
	 * @throws Exception
	 * @date 2018-1-26 16:35:16
	 */
	public void createCustPersonTradeData(BusiTradeData btd) throws Exception {
		CreateHPersonUserRequestData createHPersonUserRD = (CreateHPersonUserRequestData) btd.getRD();
		String serialNumber = createHPersonUserRD.getUca().getUser().getSerialNumber();
		CustPersonTradeData custpersonTD = createHPersonUserRD.getUca().getCustPerson().clone();
		if (!custpersonTD.getModifyTag().equals(BofConst.MODIFY_TAG_USER)) {
			btd.add(serialNumber, custpersonTD);

			// 新增使用人证件类型录入
			String strUseName = custpersonTD.getRsrvStr5();
			String strUsePsptTypeCode = custpersonTD.getRsrvStr6();
			String strUsePsptId = custpersonTD.getRsrvStr7();
			String strUsePsptAddr = custpersonTD.getRsrvStr8();

			String strTradeTpyeCode = btd.getTradeTypeCode();

			if (StringUtils.isNotBlank(strUseName)
					|| StringUtils.isNotBlank(strUsePsptTypeCode)
					|| StringUtils.isNotBlank(strUsePsptId)
					|| StringUtils.isNotBlank(strUsePsptAddr)) {

				String strCustId = custpersonTD.getCustId();
				String strPartition_id = strCustId.substring(strCustId.length() - 4);

				/*IDataset list = CustPersonInfoQry.qryCustPersonOtherByCustId(strCustId);
				if (IDataUtil.isNotEmpty(list)) {
					IData custPersonOtherData = list.first();
					custPersonOtherData.put("USE_NAME", custpersonTD.getRsrvStr5());
					custPersonOtherData.put("USE_PSPT_TYPE_CODE", custpersonTD.getRsrvStr6());
					custPersonOtherData.put("USE_PSPT_ID", custpersonTD.getRsrvStr7());
					custPersonOtherData.put("USE_PSPT_ADDR", custpersonTD.getRsrvStr8());
					custPersonOtherData.put("UPDATE_TIME", SysDateMgr.getSysTime());
					custPersonOtherData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
					custPersonOtherData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
					custPersonOtherData.put("RSRV_STR1", strTradeTpyeCode);
					Dao.update("TF_F_CUST_PERSON_OTHER", custPersonOtherData, new String[] { "PARTITION_ID", "CUST_ID" });
				} else {
					IData custPersonOtherData = new DataMap();
					custPersonOtherData.put("PARTITION_ID", strPartition_id);
					custPersonOtherData.put("CUST_ID", custpersonTD.getCustId());
					custPersonOtherData.put("USE_NAME", custpersonTD.getRsrvStr5());
					custPersonOtherData.put("USE_PSPT_TYPE_CODE", custpersonTD.getRsrvStr6());
					custPersonOtherData.put("USE_PSPT_ID", custpersonTD.getRsrvStr7());
					custPersonOtherData.put("USE_PSPT_ADDR", custpersonTD.getRsrvStr8());
					custPersonOtherData.put("CREATE_TIME", SysDateMgr.getSysTime());
					custPersonOtherData.put("UPDATE_TIME", SysDateMgr.getSysTime());
					custPersonOtherData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
					custPersonOtherData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
					custPersonOtherData.put("REMARK", "个人开户-使用人证据录入");
					custPersonOtherData.put("RSRV_STR1", strTradeTpyeCode);
					custPersonOtherData.put("RSRV_STR2", "");
					custPersonOtherData.put("RSRV_STR3", "");
					custPersonOtherData.put("RSRV_STR4", "");
					custPersonOtherData.put("RSRV_STR5", "");
					Dao.insert("TF_F_CUST_PERSON_OTHER", custPersonOtherData);
				}*/
			}
		}
	}
	
	/**
	 * 账户资料
	 * 
	 * @author zhaohj3
	 * @param btd
	 * @throws Exception
	 * @date 2018-1-26 16:35:16
	 */
	public void createAcctTradeData(BusiTradeData btd) throws Exception {
		CreateHPersonUserRequestData createHPersonUserRD = (CreateHPersonUserRequestData) btd.getRD();
		String serialNumber = createHPersonUserRD.getUca().getUser().getSerialNumber();
		AccountTradeData acctTD = createHPersonUserRD.getUca().getAccount().clone();
		
		if (!acctTD.getModifyTag().equals(BofConst.MODIFY_TAG_USER)) {
			btd.add(serialNumber, acctTD);
		}
	}
	
	/**
	 * 产品台账处理
	 * 
	 * @author zhaohj3
	 * @param btd
	 * @throws Exception
	 * @date 2018-1-26 16:35:16
	 */
	public void createProductTradeData(BusiTradeData btd) throws Exception {
		CreateHPersonUserRequestData createHPersonUserRD = (CreateHPersonUserRequestData) btd.getRD();
		ProductData productData = createHPersonUserRD.getMainProduct();
		
		if (productData != null) {
			String serialNumber = createHPersonUserRD.getUca().getUser().getSerialNumber();
			ProductTradeData productTD = new ProductTradeData();
			productTD.setUserId(createHPersonUserRD.getUca().getUserId());
			productTD.setUserIdA("-1");
			productTD.setProductId(createHPersonUserRD.getMainProduct().getProductId());
			productTD.setProductMode(createHPersonUserRD.getMainProduct().getProductMode());
			productTD.setBrandCode(createHPersonUserRD.getMainProduct().getBrandCode());
			productTD.setInstId(SeqMgr.getInstId());
			productTD.setStartDate(createHPersonUserRD.getUca().getUser().getOpenDate());
			productTD.setEndDate(SysDateMgr.getTheLastTime());
			productTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
			productTD.setMainTag("1");
			btd.add(serialNumber, productTD);
		}
	}

    /**
     * 生成台帐付费表
     * 
     * @author zhaohj3
     * @param btd
     * @throws Exception
	 * @date 2018-1-26 16:35:16
     */
    private void createTradePayRelation(BusiTradeData btd) throws Exception {
    	CreateHPersonUserRequestData createHPersonUserRD = (CreateHPersonUserRequestData) btd.getRD();
        PayRelationTradeData payrelationTD = new PayRelationTradeData();
        payrelationTD.setUserId(createHPersonUserRD.getUca().getUserId());
        payrelationTD.setAcctId(createHPersonUserRD.getUca().getAcctId());
        payrelationTD.setPayitemCode("-1");
        payrelationTD.setAcctPriority("0");
        payrelationTD.setUserPriority("0");
        payrelationTD.setBindType("1");
        payrelationTD.setStartCycleId(SysDateMgr.getNowCycle());
        payrelationTD.setEndCycleId(SysDateMgr.getEndCycle20501231());
        payrelationTD.setActTag("1");
        payrelationTD.setDefaultTag("1");
        payrelationTD.setLimitType("0");
        payrelationTD.setLimit("0");
        payrelationTD.setComplementTag("0");
        payrelationTD.setInstId(SeqMgr.getInstId());
        payrelationTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        btd.add(createHPersonUserRD.getUca().getSerialNumber(), payrelationTD);
    }
}
