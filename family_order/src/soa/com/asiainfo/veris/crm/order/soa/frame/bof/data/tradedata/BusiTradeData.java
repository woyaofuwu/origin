
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustFamilyTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.FeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.OfferRelHandler;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.PricePlanHandler;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductTimeEnv;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcStateInfoQry;

public class BusiTradeData<K extends BaseTradeData>
{
	private BaseReqData rd;

	private String route;

	private Map<String, List<K>> tradeDatasMap = new HashMap<String, List<K>>();

	private List<AcctDayData> userAcctDayChangeList = new ArrayList<AcctDayData>(); // 帐期需要变化的数据，user_id+acctDay

	private List<AcctDayData> userAcctDayOpenList = new ArrayList<AcctDayData>();// 新增用户的帐期数据,user_id+acctDay

	private List<AcctDayData> accountAcctDayOpenList = new ArrayList<AcctDayData>();// 新增帐户时新增的帐期数据,acct_id+acctDay

	private ProductTimeEnv productTimeEnv;

	private boolean needSms = true;

	public BusiTradeData() throws Exception
	{
		this.route = BizRoute.getRouteId();
	}

	public void add(String serialNumber, K value) throws Exception
	{
		String table = value.getTableName();
		List<K> list = this.tradeDatasMap.get(value.getTableName());
		if (list == null)
		{
			list = new ArrayList<K>();
			this.tradeDatasMap.put(table, list);
		}
		list.add(value);
		if (table.equals(TradeTableEnum.TRADE_SVC.getValue()))
		{
			this.addUserSvc(serialNumber, (SvcTradeData) value);
			this.dealSvcStateBySvc(serialNumber, (SvcTradeData) value);
			this.dealAttrByElementForDel(serialNumber, (ProductModuleTradeData) value);
			UcaData uca = DataBusManager.getDataBus().getUca(serialNumber);
			SvcTradeData svcTD = (SvcTradeData) value;
			OfferRelHandler.runn(svcTD, this, uca);
//			svcTD.setProductId("-1");
//			svcTD.setPackageId("-1");
			if (BofConst.MODIFY_TAG_INHERIT.equals(svcTD.getModifyTag()))
			{
				svcTD.setIsNeedPf(BofConst.IS_NEED_PF_NO);
			}
		}
		else if (table.equals(TradeTableEnum.TRADE_DISCNT.getValue()))
		{
			this.addUserDiscnt(serialNumber, (DiscntTradeData) value);
			this.dealAttrByElementForDel(serialNumber, (ProductModuleTradeData) value);
			UcaData uca = DataBusManager.getDataBus().getUca(serialNumber);

			DiscntTradeData discntTD = (DiscntTradeData) value;
			OfferRelHandler.runn(discntTD, this, uca);
			PricePlanHandler.runn(discntTD, this, uca);
//			discntTD.setProductId("-1");
//			discntTD.setPackageId("-1");
			if (BofConst.MODIFY_TAG_INHERIT.equals(discntTD.getModifyTag()))
			{
				discntTD.setIsNeedPf(BofConst.IS_NEED_PF_NO);
			}
		}
		else if (table.equals(TradeTableEnum.TRADE_PLATSVC.getValue()))
		{
			this.addUserPlatSvc(serialNumber, (PlatSvcTradeData) value);
			this.dealAttrByElementForDel(serialNumber, (ProductModuleTradeData) value);
		}
		else if (table.equals(TradeTableEnum.TRADE_PRODUCT.getValue()))
		{
			this.addUserProduct(serialNumber, (ProductTradeData) value);
		}
		else if (table.equals(TradeTableEnum.TRADE_FEESUB.getValue()))
		{
			this.addFeeTradeData((FeeTradeData) value);
		}
		else if (table.equals(TradeTableEnum.TRADE_USER.getValue()))
		{
			this.addUserTradeData((UserTradeData) value);
		}
		else if (table.equals(TradeTableEnum.TRADE_SVCSTATE.getValue()))
		{
			this.addUserSvcStateTradeData(serialNumber, (SvcStateTradeData) value);
		}
		else if (table.equals(TradeTableEnum.TRADE_ATTR.getValue()))
		{
			this.addUserAttrTradeData(serialNumber, (AttrTradeData) value);
		}
		else if(table.equals(TradeTableEnum.TRADE_OFFER_REL.getValue())){
			this.addOfferRelTradeData(serialNumber, (OfferRelTradeData)value);
		}
		else if (table.equals(TradeTableEnum.TRADE_ACCOUNT.getValue()))
		{
			this.addAccountTradeData(serialNumber, (AccountTradeData) value);
		}
		else if (table.equals(TradeTableEnum.TRADE_CUSTOMER.getValue()))
		{
			this.addCustomerTradeData(serialNumber, (CustomerTradeData) value);
		}
		else if (table.equals(TradeTableEnum.TRADE_CUST_PERSON.getValue()))
		{
			this.addCustPersonTradeData(serialNumber, (CustPersonTradeData) value);
		}
		else if (table.equals(TradeTableEnum.TRADE_PAYRELATION.getValue()))
		{
			this.addPayRelationTradeData(serialNumber, (PayRelationTradeData) value);
		}
		else if (table.equals(TradeTableEnum.TRADE_RES.getValue()))
		{

		}
		else if (table.equals(TradeTableEnum.TRADE_CUST_FAMILY.getValue()))
		{

			this.addCustFamilyTradeData(serialNumber, (CustFamilyTradeData) value);
		}
		else if (table.equals(TradeTableEnum.TRADE_SALEACTIVE.getValue()))
		{
			this.addUserSaleActive(serialNumber, (SaleActiveTradeData) value);
		}
	}
	
	private void addOfferRelTradeData(String serialNumber, OfferRelTradeData offerRel) throws Exception{
		if (StringUtils.isBlank(serialNumber))
		{
			return;
		}
		List<OfferRelTradeData> userOfferRels = DataBusManager.getDataBus().getUca(serialNumber).getOfferRelsByRelUserId();
		if (BofConst.MODIFY_TAG_ADD.equals(offerRel.getModifyTag()))
		{
			userOfferRels.add(offerRel);
		}
		else
		{
			int size = userOfferRels.size();
			for (int i = 0; i < size; i++)
			{
				OfferRelTradeData userOfferRel = userOfferRels.get(i);
				if (userOfferRel.getInstId().equals(offerRel.getInstId()))
				{
					userOfferRels.set(i, offerRel);
					break;
				}
			}
		}
	}

	private void addAccountTradeData(String serialNumber, AccountTradeData atd) throws Exception
	{
		UcaData uca = DataBusManager.getDataBus().getUca(serialNumber);
		uca.setAccount(atd);
	}

	public void addChangeAcctDayData(String userId, String acctDay)
	{
		AcctDayData acctDayChangeData = new AcctDayData(userId, acctDay);
		this.userAcctDayChangeList.add(acctDayChangeData);
	}

	public void addChangeAcctDayData(String userId, String acctDay, boolean needSyncSameAccountUser)
	{
		AcctDayData acctDayChangeData = new AcctDayData(userId, acctDay, needSyncSameAccountUser);
		this.userAcctDayChangeList.add(acctDayChangeData);
	}

	private void addCustFamilyTradeData(String serialNumber, CustFamilyTradeData custFamilyTD) throws Exception
	{
		UcaData uca = DataBusManager.getDataBus().getUca(serialNumber);
		uca.setCustFamily(custFamilyTD);
	}

	private void addCustomerTradeData(String serialNumber, CustomerTradeData ctd) throws Exception
	{
		UcaData uca = DataBusManager.getDataBus().getUca(serialNumber);
		uca.setCustomer(ctd);
	}

	private void addCustPersonTradeData(String serialNumber, CustPersonTradeData custPersontd) throws Exception
	{
		UcaData uca = DataBusManager.getDataBus().getUca(serialNumber);
		uca.setCustPerson(custPersontd);
	}

	private void addFeeTradeData(FeeTradeData feeTradeData) throws Exception
	{
		DataBusManager.getDataBus().addFeeTradeData(feeTradeData);

		// 更新主台账的费用信息
		MainTradeData mainTradeData = this.getMainTradeData();
		String feeMode = feeTradeData.getFeeMode();
		int oldFee = Integer.parseInt(feeTradeData.getOldfee());
		int fee = Integer.parseInt(feeTradeData.getFee());
		if ("0".equals(feeMode))
		{
			String operFee = mainTradeData.getOperFee();
			int newOperFee = Integer.parseInt(operFee) + Integer.parseInt(feeTradeData.getFee());
			mainTradeData.setOperFee(String.valueOf(newOperFee));
		}
		else if ("1".equals(feeMode))
		{
			String foreGift = mainTradeData.getForegift();
			int newForeGift = Integer.parseInt(foreGift) + Integer.parseInt(feeTradeData.getFee());
			mainTradeData.setForegift(String.valueOf(newForeGift));
		}
		else if ("2".equals(feeMode) || "9".equals(feeMode))
		{
			String advancePay = mainTradeData.getAdvancePay();
			int newAdvancePay = Integer.parseInt(advancePay) + Integer.parseInt(feeTradeData.getFee());
			mainTradeData.setAdvancePay(String.valueOf(newAdvancePay));
		}

		String feeState = mainTradeData.getFeeState();
		if ((StringUtils.isBlank(feeState) || feeState.equals(BofConst.FEE_STATE_NO)) && fee != 0)
		{
			mainTradeData.setFeeState(BofConst.FEE_STATE_YES);
			mainTradeData.setFeeStaffId(CSBizBean.getVisit().getStaffId());
			mainTradeData.setFeeTime(this.getRD().getAcceptTime());
		}
	}

	public void addOpenAccountAcctDayData(String acctId, String acctDay)
	{
		AcctDayData acctDayChangeData = new AcctDayData(acctId, acctDay);
		this.accountAcctDayOpenList.add(acctDayChangeData);
	}

	public void addOpenUserAcctDayData(String userId, String acctDay)
	{
		AcctDayData acctDayChangeData = new AcctDayData(userId, acctDay);
		this.userAcctDayOpenList.add(acctDayChangeData);
	}

	private void addPayRelationTradeData(String serialNumber, PayRelationTradeData payRelationTradeData) throws Exception
	{
		UcaData uca = DataBusManager.getDataBus().getUca(serialNumber);
		AccountTradeData accountTradeData = uca.getAccount();
		if (payRelationTradeData.getModifyTag().equals(BofConst.MODIFY_TAG_ADD) && payRelationTradeData.getDefaultTag().equals("1")// 默认账户
				&& accountTradeData != null && !accountTradeData.getAcctId().equals(payRelationTradeData.getAcctId()))
		{
			IData acct = UcaInfoQry.qryAcctInfoByAcctId(payRelationTradeData.getAcctId(), uca.getUserEparchyCode());
			if (IDataUtil.isEmpty(acct))
			{
				CSAppException.apperr(BofException.CRM_BOF_004);
			}
			uca.setAccount(new AccountTradeData(acct));
		}
	}

	private void addUserAttrTradeData(String serialNumber, AttrTradeData attrTradeData) throws Exception
	{
		if (StringUtils.isBlank(serialNumber))
		{
			return;
		}

		UcaData uca = DataBusManager.getDataBus().getUca(serialNumber);
		List<AttrTradeData> userAttrs = uca.getUserAttrs();
		if (uca.getUserAttrsByRelaInstIdAttrCode(attrTradeData.getRelaInstId(), attrTradeData.getAttrCode()) == null)
		{
			userAttrs.add(attrTradeData);
		}

		int size = userAttrs.size();
		for (int i = 0; i < size; i++)
		{
			AttrTradeData userAttr = userAttrs.get(i);
			if (userAttr.getRelaInstId().equals(attrTradeData.getRelaInstId()) && userAttr.getAttrCode().equals(attrTradeData.getAttrCode()))
			{
				if (userAttr.getModifyTag().equals(BofConst.MODIFY_TAG_USER) || !attrTradeData.getModifyTag().equals(BofConst.MODIFY_TAG_DEL))
				{
					userAttrs.set(i, attrTradeData);
					break;
				}
			}
		}
	}

	private void addUserDiscnt(String serialNumber, DiscntTradeData discntTradeData) throws Exception
	{
		if (StringUtils.isBlank(serialNumber))
		{
			return;
		}
		List<DiscntTradeData> userDiscnts = DataBusManager.getDataBus().getUca(serialNumber).getUserDiscnts();
		if (BofConst.MODIFY_TAG_ADD.equals(discntTradeData.getModifyTag()))
		{
			userDiscnts.add(discntTradeData);
		}
		else
		{
			int size = userDiscnts.size();
			for (int i = 0; i < size; i++)
			{
				DiscntTradeData userDiscnt = userDiscnts.get(i);
				if (userDiscnt.getInstId().equals(discntTradeData.getInstId()))
				{
					userDiscnts.set(i, discntTradeData);
					break;
				}
			}
		}
	}

	private void addUserPlatSvc(String serialNumber, PlatSvcTradeData platSvcTradeData) throws Exception
	{
		if (StringUtils.isBlank(serialNumber))
		{
			return;
		}
		UcaData uca = DataBusManager.getDataBus().getUca(serialNumber);
		List<PlatSvcTradeData> userPlatSvcs = uca.getUserPlatSvcs();
		if (BofConst.MODIFY_TAG_ADD.equals(platSvcTradeData.getModifyTag()))
		{
			userPlatSvcs.add(platSvcTradeData);
		}
		else
		{
			int size = userPlatSvcs.size();
			for (int i = 0; i < size; i++)
			{
				PlatSvcTradeData userPlatSvc = userPlatSvcs.get(i);
				if (userPlatSvc.getInstId().equals(platSvcTradeData.getInstId()))
				{
					userPlatSvcs.set(i, platSvcTradeData);
					break;
				}
			}
		}
	}

	private void addUserProduct(String serialNumber, ProductTradeData productTradeData) throws Exception
	{
		if (StringUtils.isBlank(serialNumber))
		{
			return;
		}
		UcaData uca = DataBusManager.getDataBus().getUca(serialNumber);
		List<ProductTradeData> userProducts = uca.getUserProducts();
		if (BofConst.MODIFY_TAG_ADD.equals(productTradeData.getModifyTag()))
		{
			userProducts.add(productTradeData);
			if ("1".equals(productTradeData.getMainTag()) && !productTradeData.getProductId().equals(uca.getProductId()) && productTradeData.getStartDate().compareTo(this.getRD().getAcceptTime()) <= 0)
			{
				uca.setProductId(productTradeData.getProductId());
				uca.setBrandCode(productTradeData.getBrandCode());
			}
		}
		else
		{
			int size = userProducts.size();
			for (int i = 0; i < size; i++)
			{
				ProductTradeData userProduct = userProducts.get(i);
				if (userProduct.getInstId().equals(productTradeData.getInstId()))
				{
					userProducts.set(i, productTradeData);
					break;
				}
			}
		}
	}

	private void addUserResTradeData(String serialNumber, ResTradeData resTradeData) throws Exception
	{
		UcaData uca = DataBusManager.getDataBus().getUca(serialNumber);
		// ResTradeData resTD = uca.get
	}

	private void addUserSvc(String serialNumber, SvcTradeData svcTradeData) throws Exception
	{
		if (StringUtils.isBlank(serialNumber))
		{
			return;
		}
		UcaData uca = DataBusManager.getDataBus().getUca(serialNumber);
		List<SvcTradeData> userSvcs = uca.getUserSvcs();
		if (BofConst.MODIFY_TAG_ADD.equals(svcTradeData.getModifyTag()))
		{
			userSvcs.add(svcTradeData);
		}
		else
		{
			int size = userSvcs.size();
			for (int i = 0; i < size; i++)
			{
				SvcTradeData userSvc = userSvcs.get(i);
				if (userSvc.getInstId().equals(svcTradeData.getInstId()))
				{
					userSvcs.set(i, svcTradeData);
					break;
				}
			}
		}
	}

	private void addUserSaleActive(String serialNumber, SaleActiveTradeData saleActiveTradeData) throws Exception
	{
		if (StringUtils.isBlank(serialNumber))
		{
			return;
		}
		UcaData uca = DataBusManager.getDataBus().getUca(serialNumber);
		List<SaleActiveTradeData> userSaleActives = uca.getUserSaleActives();
		if (BofConst.MODIFY_TAG_ADD.equals(saleActiveTradeData.getModifyTag()))
		{
			userSaleActives.add(saleActiveTradeData);
		}
		else
		{
			int size = userSaleActives.size();
			for (int i = 0; i < size; i++)
			{
				SaleActiveTradeData userSaleActive = userSaleActives.get(i);
				if (userSaleActive.getInstId().equals(saleActiveTradeData.getInstId()))
				{
					userSaleActives.set(i, saleActiveTradeData);
					break;
				}
			}
		}
	}

	private void addUserSvcStateTradeData(String serialNumber, SvcStateTradeData svcStateTd) throws Exception
	{
		if (StringUtils.isBlank(serialNumber))
		{
			return;
		}
		UcaData uca = DataBusManager.getDataBus().getUca(serialNumber);
		List<SvcStateTradeData> userSvcStates = uca.getUserSvcsState();

		if (BofConst.MODIFY_TAG_ADD.equals(svcStateTd.getModifyTag()))
		{
			userSvcStates.add(svcStateTd);
		}
		else
		{
			int size = userSvcStates.size();
			for (int i = 0; i < size; i++)
			{
				SvcStateTradeData userSvcState = userSvcStates.get(i);
				if (userSvcState.getInstId().equals(svcStateTd.getInstId()))
				{
					userSvcStates.set(i, svcStateTd);
				}
			}
		}
	}

	private void addUserTradeData(UserTradeData utd) throws Exception
	{
		UcaData uca = DataBusManager.getDataBus().getUca(utd.getSerialNumber());
		if (uca == null)
		{
			uca = new UcaData();
			uca.setUser(utd);
			DataBusManager.getDataBus().setUca(uca);
		}
		else
		{
			uca.setUser(utd);
		}
	}

	private void dealAttrByElementForDel(String serialNumber, ProductModuleTradeData value) throws Exception
	{
		if (BofConst.MODIFY_TAG_DEL.equals(value.getModifyTag()))
		{
			UcaData uca = DataBusManager.getDataBus().getUca(serialNumber);
			String instId = value.getInstId();
			List<AttrTradeData> attrTradeDatas = uca.getUserAttrsByRelaInstId(instId);
			int size = attrTradeDatas.size();
			List<AttrTradeData> delAttrTradeDatas = new ArrayList<AttrTradeData>();
			for (int i = 0; i < size; i++)
			{
				AttrTradeData attrTradeData = attrTradeDatas.get(i);
				if (BofConst.MODIFY_TAG_DEL.equals(attrTradeData.getModifyTag()))
				{
					continue;
				}
				AttrTradeData delAttrTradeData = attrTradeData.clone();
				delAttrTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
				delAttrTradeData.setEndDate(value.getEndDate());
				this.add(serialNumber, (K) delAttrTradeData);

				delAttrTradeDatas.add(delAttrTradeData);
			}
			value.setAttrTradeDatas(delAttrTradeDatas);
		}
	}

	private void dealSvcStateBySvc(String serialNumber, SvcTradeData svcTd) throws Exception
	{
		UcaData uca = DataBusManager.getDataBus().getUca(serialNumber);
		IData cond = new DataMap();
		IDataset result = new DatasetList();
		if (svcTd.getModifyTag().equals(BofConst.MODIFY_TAG_ADD))
		{
			// 新增服务状态
			try{ //modify hefeng 对抛错的信息进行捕获处理
				result = USvcStateInfoQry.qrySvcStateBySvcId(svcTd.getElementId());
			}catch (Exception e){
				//log.info("(""+e);
				result=null;
			}
			
			if (IDataUtil.isNotEmpty(result))
			{
				SvcStateTradeData userSvcStateTd = uca.getUserSvcsStateByServiceId(svcTd.getElementId());
				if (userSvcStateTd != null && userSvcStateTd.getEndDate().compareTo(SysDateMgr.getLastSecond(svcTd.getStartDate())) > 0)
				{
					SvcStateTradeData delSvcStateTd = userSvcStateTd.clone();
					delSvcStateTd.setModifyTag(BofConst.MODIFY_TAG_DEL);
					delSvcStateTd.setEndDate(SysDateMgr.getLastSecond(svcTd.getStartDate()));

					this.add(serialNumber, (K) delSvcStateTd);
				}
				SvcStateTradeData addSvcStateTd = new SvcStateTradeData();
				addSvcStateTd.setUserId(svcTd.getUserId());
				addSvcStateTd.setServiceId(svcTd.getElementId());
				addSvcStateTd.setMainTag(svcTd.getMainTag());
				addSvcStateTd.setStateCode("0");
				addSvcStateTd.setModifyTag(BofConst.MODIFY_TAG_ADD);
				addSvcStateTd.setStartDate(svcTd.getStartDate());
				addSvcStateTd.setEndDate(svcTd.getEndDate());
				addSvcStateTd.setInstId(SeqMgr.getInstId());
				this.add(serialNumber, (K) addSvcStateTd);
			}
		}
		else if (svcTd.getModifyTag().equals(BofConst.MODIFY_TAG_DEL))
		{
			SvcStateTradeData userSvcStateTd = DataBusManager.getDataBus().getUca(serialNumber).getUserSvcsStateByServiceId(svcTd.getElementId());

			if (userSvcStateTd != null && userSvcStateTd.getEndDate().compareTo(svcTd.getEndDate()) > 0)
			{
				SvcStateTradeData delSvcStateTd = userSvcStateTd.clone();
				delSvcStateTd.setModifyTag(BofConst.MODIFY_TAG_DEL);
				delSvcStateTd.setEndDate(svcTd.getEndDate());
				this.add(serialNumber, (K) delSvcStateTd);

				String oldSvcState = userSvcStateTd.getStateCode();
				String tradeTypeCode = this.getTradeTypeCode();

				IDataset svcStateParaSet = TradeSvcStateInfoQry.querySvcStateBySvcIdAndOldState(tradeTypeCode, uca.getBrandCode(), uca.getProductId(), uca.getUser().getEparchyCode(), svcTd.getElementId(), oldSvcState);
				if (IDataUtil.isNotEmpty(svcStateParaSet))
				{
					IData svcStatePara = svcStateParaSet.getData(0);
					String paraNewStateCode = svcStatePara.getString("NEW_STATE_CODE", "");
					// 新状态为%则不新增状态
					if (!paraNewStateCode.equals("%"))
					{
						SvcStateTradeData addSvcStateTd = new SvcStateTradeData();
						addSvcStateTd.setEndDate(SysDateMgr.getTheLastTime());
						addSvcStateTd.setStartDate(SysDateMgr.getNextSecond(svcTd.getEndDate()));
						addSvcStateTd.setUserId(uca.getUserId());
						addSvcStateTd.setServiceId(svcTd.getElementId());
						addSvcStateTd.setStateCode(paraNewStateCode);
						addSvcStateTd.setModifyTag(BofConst.MODIFY_TAG_ADD);
						addSvcStateTd.setMainTag(svcTd.getMainTag());
						addSvcStateTd.setInstId(SeqMgr.getInstId());
						this.add(serialNumber, (K) addSvcStateTd);
					}
				}
			}
		}
	}

	public List<K> get(String tableName)
	{
		List<K> tradeDatas = tradeDatasMap.get(tableName);
		if (tradeDatas == null)
		{
			tradeDatas = new ArrayList<K>();
			// tradeDatasMap.put(tradeSeg.getValue(), tradeDatas);
		}

		return tradeDatas;
	}

	public List<AcctDayData> getAccountAcctDayOpenList()
	{
		return accountAcctDayOpenList;
	}

	public String getAdvanceFee() throws Exception
	{
		return getFee(BofConst.FEE_MODE_ADVANCEFEE);
	}

	public String getAllRegTableName()
	{
		StringBuilder tableName = new StringBuilder();
		Iterator itr = this.tradeDatasMap.keySet().iterator();
		while (itr.hasNext())
		{
			String key = (String) itr.next();
			tableName.append(key + ",");
		}
		return tableName.toString();
	}

	public List<K> getAllTradeData()
	{
		List<K> list = new ArrayList<K>();
		Iterator lter = tradeDatasMap.keySet().iterator();
		while (lter.hasNext())
		{
			String key = (String) lter.next();
			list.addAll(tradeDatasMap.get(key));
		}

		return list;
	}

	private String getFee(String feeMode) throws Exception
	{
		int fee = 0;
		List feeTradeDatas = this.getTradeDatas(TradeTableEnum.TRADE_FEESUB);
		if (feeTradeDatas != null && feeTradeDatas.size() > 0)
		{
			for (int i = 0; i < feeTradeDatas.size(); i++)
			{
				FeeTradeData feeTradeData = (FeeTradeData) feeTradeDatas.get(i);
				if (feeTradeData.getFeeMode().equals(feeMode))
				{
					fee += Integer.parseInt(feeTradeData.getFee());
				}
			}
		}

		return String.valueOf(fee);
	}

	public String getForeGift() throws Exception
	{
		return getFee(BofConst.FEE_MODE_FOREGIFT);
	}

	public MainTradeData getMainTradeData() throws Exception
	{
		MainTradeData mainTradeData = null;
		List list = this.getTradeDatas(TradeTableEnum.TRADE_MAIN);
		if (list != null && list.size() > 0)
		{
			return (MainTradeData) list.get(0);
		}

		return mainTradeData;
	}

	public String getOperFee() throws Exception
	{
		return getFee(BofConst.FEE_MODE_OPERFEE);
	}

	public ProductTimeEnv getProductTimeEnv()
	{
		return productTimeEnv;
	}

	public BaseReqData getRD()
	{
		return rd;
	}

	public String getRoute()
	{
		return this.route;
	}

	public List<K> getTradeDatas(TradeTableEnum tradeSeg)
	{
		List<K> tradeDatas = tradeDatasMap.get(tradeSeg.getValue());
		if (tradeDatas == null)
		{
			tradeDatas = new ArrayList<K>();
			// tradeDatasMap.put(tradeSeg.getValue(), tradeDatas);
		}

		return tradeDatas;
	}

	public Map<String, List<K>> getTradeDatasMap()
	{
		return tradeDatasMap;
	}

	public String getTradeId()
	{
		return this.getRD().getTradeId();
	}

	public String getTradeTypeCode()
	{
		return this.getRD().getTradeType().getTradeTypeCode();
	}

	public List<AcctDayData> getUserAcctDayChangeList()
	{
		return userAcctDayChangeList;
	}

	public List<AcctDayData> getUserAcctDayOpenList()
	{
		return userAcctDayOpenList;
	}

	public boolean isBrandChange() throws Exception
	{
		boolean isBrandChange = false;
		List productTDList = this.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);
		for (int i = 0, size = productTDList.size(); i < size; i++)
		{
			ProductTradeData productTD = (ProductTradeData) productTDList.get(i);
			if ("1".equals(productTD.getMainTag()) && BofConst.MODIFY_TAG_ADD.equals(productTD.getModifyTag()))
			{
				String brandCode = productTD.getBrandCode();
				String oldbrandCode = productTD.getOldBrandCode();
				if (!brandCode.equals(oldbrandCode))
				{
					isBrandChange = true;
					break;
				}
			}
		}

		return isBrandChange;
	}

	public boolean isNeedSms()
	{
		return needSms;
	}

	public boolean isProductChange() throws Exception
	{
		boolean isProductChange = false;
		List productTDList = this.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);
		for (int i = 0, size = productTDList.size(); i < size; i++)
		{
			ProductTradeData productTD = (ProductTradeData) productTDList.get(i);
			if ("1".equals(productTD.getMainTag()) && BofConst.MODIFY_TAG_ADD.equals(productTD.getModifyTag()))
			{
				isProductChange = true;
				break;
			}
		}

		return isProductChange;
	}

	public void setBrd(BaseReqData brd)
	{
		this.rd = brd;
	}

	/**
	 * @param index
	 *            第几位，比如修改第1位，就传1
	 * @param value
	 *            修改的值
	 * @throws Exception
	 */
	public void setMainTradeProcessTagSet(int index, String value) throws Exception
	{
		String processTagSet = this.getMainTradeData().getProcessTagSet();
		processTagSet = processTagSet.substring(0, index - 1) + value + processTagSet.substring(index);
		this.getMainTradeData().setProcessTagSet(processTagSet);
	}

	public void setNeedSms(boolean needSms)
	{
		this.needSms = needSms;
	}

	public void setProductTimeEnv(ProductTimeEnv productTimeEnv)
	{
		this.productTimeEnv = productTimeEnv;
	}

	public void setRoute(String route)
	{
		this.route = route;
	}

	public void setTradeDatasMap(Map<String, List<K>> tradeDatasMap)
	{
		this.tradeDatasMap = tradeDatasMap;
	}

	@Override
	public String toString()
	{
		StringBuilder str = new StringBuilder();
		List allTradeDataList = this.getAllTradeData();
		for (int i = 0; i < allTradeDataList.size(); i++)
		{
			str.append(allTradeDataList.get(i).toString());
		}
		return str.toString();
	}
}
