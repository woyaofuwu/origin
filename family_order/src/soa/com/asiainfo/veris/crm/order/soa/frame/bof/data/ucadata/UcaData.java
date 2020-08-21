
package com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnv;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnvManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.IAcctDayChangeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustFamilyTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustGroupTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PricePlanTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleDepositTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleGoodsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.VipTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;

public final class UcaData
{
	private AccountTradeData account;

	private CustGroupTradeData custgroup;

	private CustomerTradeData customer;

	private CustPersonTradeData custPerson;

	private UserTradeData user;

	private CustFamilyTradeData custFamily;

	private VipTradeData vip;

	List<AttrTradeData> userAttrs;

	List<DiscntTradeData> userDiscnts;

	List<PlatSvcTradeData> userPlatSvcs;

	List<ProductTradeData> userProducts;

	List<SaleActiveTradeData> userSaleActives;

	List<SaleDepositTradeData> userSaleDeposits;

	List<SaleGoodsTradeData> userSaleGoods;

	List<SvcTradeData> userSvcs;

	List<SvcStateTradeData> userSvcsState;

	List<ResTradeData> userRes;
	
	List<OfferRelTradeData> userOfferRels;
	
	List<OfferRelTradeData> parentOfferRels;
	
	List<PricePlanTradeData> userPricePlans;

	private OriginalUcaData userOriginalData = new OriginalUcaData();

	private String acctDay;

	private String changeAcctDay;

	private String firstDate;

	private String nextAcctDay;

	private String nextFirstDate;

	private String acctDayStartDate;

	private String nextAcctDayStartDate;

	private String brandCode;

	private String productId;

	private String lastOweFee = null;

	private String realFee = null;

	private String acctBlance = null;

	private String switchId; // 号码对应的交换机

	private String simCardNo;// sim卡号

	private String isVip = "";

	private String userScore = "";

	private String creditValue = "";// 用户信用度

	private String creditClass = "";// 信用等级

	private String isRedUser = "";

	private String submitType = "";

	/**
	 * 检查用户是否存在有效的某服务
	 * 
	 * @param serviceId
	 *            填写服务ID
	 * @return
	 * @throws Exception
	 */
	public boolean checkUserIsExistSvcId(String serviceId) throws Exception
	{
		boolean flag = false;
		String sysdate = SysDateMgr.getSysDate();

		List<SvcTradeData> svcTradeDatas = this.getUserSvcs();
		int size = svcTradeDatas.size();
		for (int i = 0; i < size; i++)
		{
			SvcTradeData std = svcTradeDatas.get(i);
			if (std.getElementId().equals(serviceId))
			{
				if (!std.getModifyTag().equals(BofConst.MODIFY_TAG_DEL) && std.getEndDate().compareTo(sysdate) >= 0)
				{
					flag = true;
					break;
				}
			}
		}

		return flag;
	}

	/**
	 * 获取用户默认付费关系的帐户信息
	 * 
	 * @return
	 */
	public AccountTradeData getAccount()
	{
		return account;
	}

	/**
	 * 获取用户当前的余额信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getAcctBlance() throws Exception
	{
		if (this.acctBlance == null)
		{
			this.getAcctFee();
		}
		return acctBlance;
	}

	/**
	 * 获取用户当前的结帐日信息
	 * 
	 * @return
	 */
	public String getAcctDay()
	{
		return acctDay;
	}

	/**
	 * 获取用户当前结帐日的开始时间,对应tf_f_user_acctday表的start_date字段
	 * 
	 * @return
	 */
	public String getAcctDayStartDate()
	{
		return acctDayStartDate;
	}

	/**
	 * 获取用户费用信息
	 * 
	 * @throws Exception
	 */
	private void getAcctFee() throws Exception
	{
		try
		{
			IData oweFee = AcctCall.getOweFeeByUserId(this.getUserId());
			this.setLastOweFee(oweFee.getString("LAST_OWE_FEE", "0"));
			this.setAcctBlance(oweFee.getString("ACCT_BALANCE", "0"));
			this.setRealFee(oweFee.getString("REAL_FEE", "0"));
		}
		catch (Exception e)
		{
			this.setLastOweFee("0");
			this.setAcctBlance("0");
			this.setRealFee("0");
		}
	}

	/**
	 * 获取三户资料中的帐户ID
	 * 
	 * @return
	 */
	public String getAcctId()
	{
		return this.account.getAcctId();
	}

	/**
	 * 获取用户主产品品牌
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getBrandCode() throws Exception
	{
		return this.brandCode;
	}

	/**
	 * 获取用户变化的帐期信息
	 * 
	 * @return
	 */
	public String getChangeAcctDay()
	{
		return changeAcctDay;
	}

	/**
	 * 获取家庭客户信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public CustFamilyTradeData getCustFamily() throws Exception
	{
		if (this.custFamily == null)
		{
			IDataset tmp = BofQuery.queryCustFamily(this.getCustId(), this.getUserEparchyCode());
			if (IDataUtil.isEmpty(tmp))
			{
				return null;
			}
			if (tmp.size() > 1)
			{
				// 报错
			}

			custFamily = new CustFamilyTradeData(tmp.getData(0));
		}

		return this.custFamily;
	}

	/**
	 * 如果该用户是集团用户，由获取集团tf_f_cust_group表信息
	 * 
	 * @return
	 */
	public CustGroupTradeData getCustGroup()
	{
		return custgroup;
	}

	/**
	 * 获取三户资料中的客户ID
	 * 
	 * @return
	 */
	public String getCustId()
	{
		return this.customer.getCustId();
	}

	/**
	 * 获取tf_f_customer表信息
	 * 
	 * @return
	 */
	public CustomerTradeData getCustomer()
	{
		return customer;
	}

	/**
	 * 获取tf_f_cust_person信息
	 * 
	 * @return
	 */
	public CustPersonTradeData getCustPerson()
	{
		return custPerson;
	}

	/**
	 * 获取用户当前结帐日对应的首次结帐日信息，对应tf_f_user_acctday表的first_date字段
	 * 
	 * @return
	 */
	public String getFirstDate()
	{
		return firstDate;
	}

	/**
	 * 获取集团用户的custId
	 * 
	 * @return
	 */
	public String getGrpCustId()
	{
		return this.custgroup.getCustId();
	}

	/**
	 * 获取用户的往月欠费信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getLastOweFee() throws Exception
	{
		if (this.lastOweFee == null)
		{
			this.getAcctFee();
		}
		return lastOweFee;
	}

	/**
	 * 获取用户预约帐期的结帐日信息
	 * 
	 * @return
	 */
	public String getNextAcctDay()
	{
		return nextAcctDay;
	}

	/**
	 * 获取用户预约帐期的开始时间
	 * 
	 * @return
	 */
	public String getNextAcctDayStartDate()
	{
		return nextAcctDayStartDate;
	}

	/**
	 * 获取用户预约帐期的首次结帐日信息
	 * 
	 * @return
	 */
	public String getNextFirstDate()
	{
		return nextFirstDate;
	}

	/**
	 * 根据优惠编码查询用户当前有效的优惠信息
	 * 
	 * @param discntCode
	 * @return
	 * @throws Exception
	 */
	public List<DiscntTradeData> getNowValidUserDiscntByDiscntId(String discntCode) throws Exception
	{
		OrderDataBus dataBus = DataBusManager.getDataBus();
		List<DiscntTradeData> rtDiscntTradeDatas = new ArrayList<DiscntTradeData>();
		List<DiscntTradeData> discntTradeDatas = this.getUserDiscnts();
		int size = discntTradeDatas.size();
		for (int i = 0; i < size; i++)
		{
			DiscntTradeData discntTradeData = discntTradeDatas.get(i);
			if (discntTradeData.getElementId().equals(discntCode))
			{
				if (discntTradeData.getStartDate().compareTo(dataBus.getAcceptTime()) < 0 && discntTradeData.getEndDate().compareTo(dataBus.getAcceptTime()) > 0)
				{
					rtDiscntTradeDatas.add(discntTradeData);
				}
			}
		}
		return rtDiscntTradeDatas;
	}

	/**
	 * 获取用户主产品ID
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getProductId() throws Exception
	{
		return this.productId;
	}

	/**
	 * 获取查询时的路由 如果当前服务默认路由是CG，则继续连CG，否则为用户的归属地州
	 * 
	 * @return
	 */
	private String getQueryRoute() throws Exception
	{
		String route = BizRoute.getRouteId().equals(Route.CONN_CRM_CG) ? BizRoute.getRouteId() : this.getUserEparchyCode();
		return route;
	}

	/**
	 * 获取用户的实时话费
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getRealFee() throws Exception
	{
		if (this.realFee == null)
		{
			this.getAcctFee();
		}
		return realFee;
	}

	/**
	 * 获取三户资料中的手机号码
	 * 
	 * @return
	 */
	public String getSerialNumber()
	{
		return this.user.getSerialNumber();
	}

	public String getSubmitType()
	{
		return submitType;
	}

	/**
	 * 根据服务号码调用资源接口获取局向Id信息
	 * 
	 * @throws Exception
	 */
	private String getSwitchId() throws Exception
	{
		if (this.switchId == null)
		{
			IDataset resInfos = ResCall.getMphonecodeInfo(this.getSerialNumber());
			if (IDataUtil.isNotEmpty(resInfos))
			{
				IData resInfo = resInfos.getData(0);
				this.switchId = resInfo.getString("SWITCH_ID");
			}
		}
		return this.switchId;
	}

	/**
	 * 获取三户资料中的tf_f_user表信息
	 * 
	 * @return
	 */
	public UserTradeData getUser()
	{
		return user;
	}

	/**
	 * 获取当帐期变更时需要变化的资料数据
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<IAcctDayChangeData> getUserAcctDayNeedChangeData() throws Exception
	{
		List<IAcctDayChangeData> acctDayDatas = new ArrayList<IAcctDayChangeData>();
		List<SvcTradeData> userSvcs = this.getUserSvcs();
		int size = userSvcs.size();
		for (int i = 0; i < size; i++)
		{
			IAcctDayChangeData svc = userSvcs.get(i);
			if (BofConst.MODIFY_TAG_USER.equals(svc.getModifyTag()))
			{
				acctDayDatas.add(svc);
			}
		}

		List<DiscntTradeData> userDiscnts = this.getUserDiscnts();
		size = userDiscnts.size();
		for (int i = 0; i < size; i++)
		{
			IAcctDayChangeData discnt = userDiscnts.get(i);
			if (BofConst.MODIFY_TAG_USER.equals(discnt.getModifyTag()))
			{
				acctDayDatas.add(discnt);
			}
		}

		List<AttrTradeData> userAttrs = this.getUserAttrs();
		size = userAttrs.size();
		for (int i = 0; i < size; i++)
		{
			IAcctDayChangeData attr = userAttrs.get(i);
			if (BofConst.MODIFY_TAG_USER.equals(attr.getModifyTag()))
			{
				acctDayDatas.add(attr);
			}
		}

		List<ProductTradeData> userProducts = this.getUserProducts();
		size = userProducts.size();
		for (int i = 0; i < size; i++)
		{
			IAcctDayChangeData product = userProducts.get(i);
			if (BofConst.MODIFY_TAG_USER.equals(product.getModifyTag()))
			{
				acctDayDatas.add(product);
			}
		}

		List<SaleActiveTradeData> userSaleActives = this.getUserSaleActives();
		size = userSaleActives.size();
		for (int i = 0; i < size; i++)
		{
			IAcctDayChangeData saleActive = userSaleActives.get(i);
			if (BofConst.MODIFY_TAG_USER.equals(saleActive.getModifyTag()))
			{
				acctDayDatas.add(saleActive);
			}
		}

		List<SaleDepositTradeData> userSaleDeposits = this.getUserSaleDeposit();
		size = userSaleDeposits.size();
		for (int i = 0; i < size; i++)
		{
			IAcctDayChangeData saleDeposit = userSaleDeposits.get(i);
			if (BofConst.MODIFY_TAG_USER.equals(saleDeposit.getModifyTag()))
			{
				acctDayDatas.add(saleDeposit);
			}
		}
		return acctDayDatas;
	}

	/**
	 * 获取用户所有 的资源信息，对应tf_f_user_res表数据
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<ResTradeData> getUserAllRes() throws Exception
	{
		if (this.userRes == null)
		{
			this.userRes = new ArrayList<ResTradeData>();
			IDataset userAllRes = BofQuery.queryUserAllValidRes(this.getUserId(), getQueryRoute());
			IDataset tradeRes = BofQuery.queryTradeResByUserId(this.getUserId(), getQueryRoute());
			IDataset future = new DatasetList();
			if (userAllRes != null && tradeRes != null)
			{
				future = DataBusUtils.getFuture(userAllRes, tradeRes, new String[]
				{ "INST_ID" });
				future = DataBusUtils.filterInValidDataByEndDate(future);
			}
			if (future.size() > 0)
			{
				int size = future.size();
				for (int i = 0; i < size; i++)
				{
					ResTradeData resTradeData = new ResTradeData(future.getData(i));
					resTradeData.setModifyTag(BofConst.MODIFY_TAG_USER);
					this.userRes.add(resTradeData);
				}
			}
		}

		return this.userRes;
	}

	/**
	 * 获取用户所有的属性信息，对应tf_f_user_attr表数据
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<AttrTradeData> getUserAttrs() throws Exception
	{
		if (this.userAttrs == null)
		{
			this.userAttrs = new ArrayList<AttrTradeData>();
			IDataset tradeAttrs = new DatasetList();
			if (BofConst.SUBMIT_TYPE_SHOPPING_CART.equals(this.submitType))
			{
				tradeAttrs = BofQuery.queryTradeAttrsByUserId4Shopping(this.getUserId(), getQueryRoute());
			}
			else
			{
				tradeAttrs = BofQuery.queryTradeAttrsByUserId(this.getUserId(), getQueryRoute());
			}
			IDataset userAttrs = BofQuery.queryUserAllAttr(this.getUserId(), getQueryRoute());
			IDataset future = new DatasetList();
			if (userAttrs != null && tradeAttrs != null)
			{
				future = DataBusUtils.getFutureForSpec(userAttrs, tradeAttrs, new String[]
				{ "INST_ID", "ATTR_CODE" });
				future = DataBusUtils.filterInValidDataByEndDate(future);
			}
			if (future.size() > 0)
			{
				int size = future.size();
				for (int i = 0; i < size; i++)
				{
					AttrTradeData attrTradeData = new AttrTradeData(future.getData(i));
					attrTradeData.setModifyTag(BofConst.MODIFY_TAG_USER);
					this.userAttrs.add(attrTradeData);
					// this.userOriginalData.addData(attrTradeData);
				}
			}

		}
		return this.userAttrs;
	}

	/**
	 * 根据属性的attrCode获取该attrCode对应的用户属性信息
	 * 
	 * @param attrCode
	 * @return
	 * @throws Exception
	 */
	public List<AttrTradeData> getUserAttrsByAttrCode(String attrCode) throws Exception
	{
		List<AttrTradeData> attrs = this.getUserAttrs();
		List<AttrTradeData> result = new ArrayList<AttrTradeData>();
		if (attrs != null && attrs.size() > 0)
		{
			int size = attrs.size();
			for (int i = 0; i < size; i++)
			{
				AttrTradeData attr = attrs.get(i);
				if (attr.getAttrCode().equals(attrCode))
				{
					result.add(attr);
				}
			}
		}
		return result;
	}

	/**
	 * 根据属性的instId获取该instId对应的用户属性信息
	 * 
	 * @param instId
	 * @return
	 * @throws Exception
	 */
	public List<AttrTradeData> getUserAttrsByInstId(String instId) throws Exception
	{
		List<AttrTradeData> attrs = this.getUserAttrs();
		List<AttrTradeData> result = new ArrayList<AttrTradeData>();
		if (attrs != null && attrs.size() > 0)
		{
			int size = attrs.size();
			for (int i = 0; i < size; i++)
			{
				AttrTradeData attr = attrs.get(i);
				if (attr.getInstId().equals(instId))
				{
					result.add(attr);
				}
			}
		}
		return result;
	}

	/**
	 * 根据元素的instId查询对应的属性信息
	 * 
	 * @param relaInstId
	 * @return
	 * @throws Exception
	 */
	public List<AttrTradeData> getUserAttrsByRelaInstId(String relaInstId) throws Exception
	{
		List<AttrTradeData> attrs = this.getUserAttrs();
		List<AttrTradeData> result = new ArrayList<AttrTradeData>();
		if (attrs != null && attrs.size() > 0)
		{
			int size = attrs.size();
			for (int i = 0; i < size; i++)
			{
				AttrTradeData attr = attrs.get(i);
				if (attr.getRelaInstId().equals(relaInstId))
				{
					result.add(attr);
				}
			}
		}
		return result;
	}

	/**
	 * 根据元素的instId查询指定attr_code的属性数据
	 * 
	 * @param relaInstId
	 * @param attrCode
	 * @return
	 * @throws Exception
	 */
	public AttrTradeData getUserAttrsByRelaInstIdAttrCode(String relaInstId, String attrCode) throws Exception
	{
		List<AttrTradeData> attrs = this.getUserAttrs();
		if (attrs != null && attrs.size() > 0)
		{
			int size = attrs.size();
			for (int i = 0; i < size; i++)
			{
				AttrTradeData attr = attrs.get(i);
				if (attr.getRelaInstId().equals(relaInstId) && attr.getAttrCode().equals(attrCode))
				{
					return attr;
				}
			}
		}
		return null;
	}
	
	/**
	 * 单属性多值的情况需要不同的处理方法
	 * add by chenwy
	 * @param discntCode
	 * @return
	 * @throws Exception
	 */
	public AttrTradeData getUserAttrsByRelaInstIdAttrCodeAttrValue(String relaInstId, String attrCode,String attrvalue) throws Exception
	{
		List<AttrTradeData> attrs = this.getUserAttrs();
		if (attrs != null && attrs.size() > 0)
		{
			int size = attrs.size();
			for (int i = 0; i < size; i++)
			{
				AttrTradeData attr = attrs.get(i);
				if (attr.getRelaInstId().equals(relaInstId) && attr.getAttrCode().equals(attrCode) && attr.getAttrValue().equals(attrvalue))
				{
					return attr;
				}
			}
		}
		return null;
	}
	

	/**
	 * * 查询用户信用等级
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getUserCreditClass() throws Exception
	{
		this.getUserCreditValue();

		return this.creditClass;
	}

	/**
	 * 查询用户信用度
	 * 
	 * @return
	 * @throws Exception
	 */
	public int getUserCreditValue() throws Exception
	{
		if (StringUtils.isBlank(this.creditValue))
		{
			if (!BofConst.MODIFY_TAG_ADD.equals(this.getUser().getModifyTag()))
			{
				IDataset result = AcctCall.getUserCreditInfos("0", this.getUserId());
				if (IDataUtil.isEmpty(result))
				{
					// 报错
					CSAppException.apperr(BofException.CRM_BOF_018);
				}
				IData creditInfo = result.getData(0);
				this.creditClass = creditInfo.getString("CREDIT_CLASS");
				this.creditValue = creditInfo.getString("CREDIT_VALUE");
			}
			else
			{
				this.creditClass = "-1";
				this.creditValue = "0";
			}
		}

		return Integer.parseInt(this.creditValue);
	}

	/**
	 * 根据优惠编码查询用户的优惠信息
	 * 
	 * @param discntCode
	 * @return
	 * @throws Exception
	 */
	public List<DiscntTradeData> getUserDiscntByDiscntId(String discntCode) throws Exception
	{
		List<DiscntTradeData> rtDiscntTradeDatas = new ArrayList<DiscntTradeData>();
		List<DiscntTradeData> discntTradeDatas = this.getUserDiscnts();
		int size = discntTradeDatas.size();
		for (int i = 0; i < size; i++)
		{
			DiscntTradeData discntTradeData = discntTradeDatas.get(i);
			if (discntTradeData.getElementId().equals(discntCode))
			{
				rtDiscntTradeDatas.add(discntTradeData);
			}
		}
		return rtDiscntTradeDatas;
	}

	/**
	 * 根据instId查询用户的优惠信息
	 * 
	 * @param instId
	 * @return
	 * @throws Exception
	 */
	public DiscntTradeData getUserDiscntByInstId(String instId) throws Exception
	{
		List<DiscntTradeData> discntTradeDatas = this.getUserDiscnts();
		int size = discntTradeDatas.size();
		for (int i = 0; i < size; i++)
		{
			DiscntTradeData dtd = discntTradeDatas.get(i);
			if (dtd.getInstId().equals(instId))
			{
				return dtd;
			}
		}
		return null;
	}

	public List<DiscntTradeData> getUserDiscntByPidPkid(String productId, String packageId) throws Exception
	{
	    List<DiscntTradeData> rtDiscntTradeDatas = new ArrayList<DiscntTradeData>();
	    
        List<DiscntTradeData> discntTradeDatas = this.getUserDiscnts();
        int size = discntTradeDatas.size();
        for (int i = 0; i < size; i++)
        {
            DiscntTradeData discntTradeData = discntTradeDatas.get(i);
            
            if (StringUtils.equals(discntTradeData.getProductId(), productId) && StringUtils.equals(discntTradeData.getPackageId(), packageId))
            {
                rtDiscntTradeDatas.add(discntTradeData);
            }
        }
        
		return rtDiscntTradeDatas;
	}

	/**
	 * 获取用户所有的优惠信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<DiscntTradeData> getUserDiscnts() throws Exception
	{
		if (this.userDiscnts == null)
		{
			this.userDiscnts = new ArrayList<DiscntTradeData>();
			IDataset tradeDiscnts = new DatasetList();
			if (BofConst.SUBMIT_TYPE_SHOPPING_CART.equals(this.submitType))
			{
				tradeDiscnts = BofQuery.queryTradeDiscntsByUserId4Shopping(this.getUserId(), this.getQueryRoute());
			}
			else
			{
				tradeDiscnts = BofQuery.queryTradeDiscntsByUserId(this.getUserId(), this.getQueryRoute());
			}
			IDataset userDiscnts = BofQuery.queryUserAllValidDiscnt(this.getUserId(), getQueryRoute());
			IDataset future = new DatasetList();
			if (userDiscnts != null && tradeDiscnts != null)
			{
				future = DataBusUtils.getFuture(userDiscnts, tradeDiscnts, new String[]
				{ "INST_ID" });
				future = DataBusUtils.filterInValidDataByEndDate(future);
			}
			if (future.size() > 0)
			{
				int size = future.size();
				for (int i = 0; i < size; i++)
				{
					DiscntTradeData discntTradeData = new DiscntTradeData(future.getData(i));
					discntTradeData.setModifyTag(BofConst.MODIFY_TAG_USER);
					this.userDiscnts.add(discntTradeData);
					// this.userOriginalData.addData(discntTradeData);
				}
			}

		}
		return this.userDiscnts;
	}

	/**
	 * 传入多个discntCode,获取符合条件的优惠资料
	 * 
	 * @param discntCodes
	 *            多个discntCode用任意分隔符分隔开
	 * @return
	 * @throws Exception
	 */
	public List<DiscntTradeData> getUserDiscntsByDiscntCodeArray(String discntCodes) throws Exception
	{
		List<DiscntTradeData> rtDiscntTradeDatas = new ArrayList<DiscntTradeData>();
		String[] discntList = discntCodes.split(",");
		for (String discntCode : discntList)
		{
			rtDiscntTradeDatas.addAll(this.getUserDiscntByDiscntId(discntCode));
		}
		return rtDiscntTradeDatas;
	}

	/**
	 * 获取用户的归属地州
	 * 
	 * @author anwx@asiainfo-linkage.com @ 2013-4-26
	 * @return
	 * @throws Exception
	 */
	public String getUserEparchyCode() throws Exception
	{
		return this.user.getEparchyCode();
	}

	/**
	 * 获取三户资料中的用户ID
	 * 
	 * @return
	 */
	public String getUserId()
	{
		return this.user.getUserId();
	}

	/**
	 * 获取用户当前的主产品信息，tf_f_user_product表数据
	 * 
	 * @return
	 * @throws Exception
	 */
	public ProductTradeData getUserMainProduct() throws Exception
	{
		if (this.userProducts == null)
		{
			this.getUserProducts();
		}
		String sysDate = DataBusManager.getDataBus().getAcceptTime();
		int size = this.userProducts.size();
		for (int i = 0; i < size; i++)
		{
			ProductTradeData productTradeData = this.userProducts.get(i);
			if ("1".equals(productTradeData.getMainTag()) && productTradeData.getStartDate().compareTo(sysDate) <= 0)
			{
				return productTradeData;
			}
		}
		return null;
	}

	/**
	 * 获取用户最新的品牌编码，如果有预约产品，则返回的是预约的主产品对应的品牌，否则是当前的主产品对应的品牌
	 * 
	 * @return
	 */
	public String getUserNewBrandCode() throws Exception
	{
		List<ProductTradeData> productTradeDataList = this.getUserProducts();
		String newBrandCode = "";
		String productStartDate = "";
		int size = productTradeDataList.size();
		for (int i = 0; i < size; i++)
		{
			ProductTradeData productTradeData = productTradeDataList.get(i);
			if ("1".equals(productTradeData.getMainTag()) && (StringUtils.isBlank(productStartDate) || productTradeData.getStartDate().compareTo(productStartDate) > 0))
			{
				newBrandCode = productTradeData.getBrandCode();
				productStartDate = productTradeData.getStartDate();
			}
		}

		return newBrandCode;
	}

	/**
	 * 获取用户最新的产品编码，如果有预约产品，则返回的是预约的主产品，否则是当前的主产品
	 * 
	 * @return
	 */
	public String getUserNewMainProductId() throws Exception
	{
		List<ProductTradeData> productTradeDataList = this.getUserProducts();
		String newProductId = "";
		String productStartDate = "";
		int size = productTradeDataList.size();
		for (int i = 0; i < size; i++)
		{
			ProductTradeData productTradeData = productTradeDataList.get(i);
			if ("1".equals(productTradeData.getMainTag()) && (StringUtils.isBlank(productStartDate) || productTradeData.getStartDate().compareTo(productStartDate) > 0))
			{
				newProductId = productTradeData.getProductId();
				productStartDate = productTradeData.getStartDate();
			}
		}

		return newProductId;
	}

	/**
	 * 获取用户预约生效的主产品信息，如果返回null，则表示没有预约的主产品信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public ProductTradeData getUserNextMainProduct() throws Exception
	{
		if (this.userProducts == null)
		{
			this.getUserProducts();
		}
		String sysDate = DataBusManager.getDataBus().getAcceptTime();
		int size = this.userProducts.size();
		for (int i = 0; i < size; i++)
		{
			ProductTradeData productTradeData = this.userProducts.get(i);
			if ("1".equals(productTradeData.getMainTag()) && productTradeData.getStartDate().compareTo(sysDate) > 0)
			{
				return productTradeData;
			}
		}
		return null;
	}

	/**
	 * 获取uca资料原数据
	 * 
	 * @return
	 */
	public OriginalUcaData getUserOriginalData()
	{
		return userOriginalData;
	}

	/**
	 * 根据instId获取用户的平台服务资料
	 * 
	 * @param instId
	 * @return
	 * @throws Exception
	 */
	public PlatSvcTradeData getUserPlatSvcByInstId(String instId) throws Exception
	{
		List<PlatSvcTradeData> platSvcTradeDatas = this.getUserPlatSvcs();
		int size = platSvcTradeDatas.size();
		for (int i = 0; i < size; i++)
		{
			PlatSvcTradeData pstd = platSvcTradeDatas.get(i);
			if (pstd.getInstId().equals(instId))
			{
				return pstd;
			}
		}
		return null;
	}

	/**
	 * 根据服务ID获取用户的平台服务资料
	 * 
	 * @param serviceId
	 * @return
	 * @throws Exception
	 */
	public List<PlatSvcTradeData> getUserPlatSvcByServiceId(String serviceId) throws Exception
	{
		List<PlatSvcTradeData> result = new ArrayList<PlatSvcTradeData>();
		List<PlatSvcTradeData> platSvcTradeDatas = this.getUserPlatSvcs();
		int size = platSvcTradeDatas.size();
		for (int i = 0; i < size; i++)
		{
			PlatSvcTradeData platSvcTradeData = platSvcTradeDatas.get(i);
			if (platSvcTradeData.getElementId().equals(serviceId))
			{
				result.add(platSvcTradeData);
			}
		}
		return result;
	}

	/**
	 * 获取用户所有的平台服务资料（包含生效的和预约生效的以及未完工台帐的合并结果）
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<PlatSvcTradeData> getUserPlatSvcs() throws Exception
	{
		if (this.userPlatSvcs == null)
		{
			this.userPlatSvcs = new ArrayList<PlatSvcTradeData>();
			IDataset tradePlatSvcs = BofQuery.queryUserAllTradePlatSvc(this.getUserId(), getQueryRoute());
			IDataset userPlatSvcs = BofQuery.queryUserAllPlatSvc(this.getUserId(), getQueryRoute());
			IDataset future = new DatasetList();
			if (userPlatSvcs != null && tradePlatSvcs != null)
			{
				future = DataBusUtils.getFuture(userPlatSvcs, tradePlatSvcs, new String[]
				{ "INST_ID" });
				future = DataBusUtils.filterInValidDataByEndDate(future);
			}
			if (future.size() > 0)
			{
				int size = future.size();
				for (int i = 0; i < size; i++)
				{
					PlatSvcTradeData platSvcTradeData = new PlatSvcTradeData(future.getData(i));
					platSvcTradeData.setModifyTag(BofConst.MODIFY_TAG_USER);
					this.userPlatSvcs.add(platSvcTradeData);
					// this.userOriginalData.addData(platSvcTradeData);
				}
			}
		}
		return this.userPlatSvcs;
	}

	/**
	 * 获得本次业务受理中操作过的用户平台服务数据
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<PlatSvcTradeData> getUserPlatSvcsByModi() throws Exception
	{
		List<PlatSvcTradeData> userPlatSvcs = this.getUserPlatSvcs();
		List<PlatSvcTradeData> result = new ArrayList<PlatSvcTradeData>();
		int size = userPlatSvcs.size();
		for (int i = 0; i < size; i++)
		{
			PlatSvcTradeData pstd = userPlatSvcs.get(i);
			if (!pstd.getModifyTag().equals(BofConst.MODIFY_TAG_USER))
			{
				result.add(pstd);
			}
		}
		return result;
	}

	/**
	 * 根据产品ID获取用户的产品信息
	 * 
	 * @param productId
	 * @return
	 * @throws Exception
	 */
	public List<ProductTradeData> getUserProduct(String productId) throws Exception
	{
		List<ProductTradeData> userProducts = this.getUserProducts();
		List<ProductTradeData> result = new ArrayList<ProductTradeData>();
		int size = userProducts.size();
		for (int i = 0; i < size; i++)
		{
			ProductTradeData userProduct = userProducts.get(i);
			if (userProduct.getProductId().equals(productId))
			{
				result.add(userProduct);
			}
		}
		return result;
	}

	/**
	 * 获取用户所有的产品信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<ProductTradeData> getUserProducts() throws Exception
	{
		if (this.userProducts == null)
		{
			this.userProducts = new ArrayList<ProductTradeData>();
			IDataset userProducts = BofQuery.getUserAllProducts(this.getUserId(), getQueryRoute());
			IDataset tradeProducts = new DatasetList();
			if (BofConst.SUBMIT_TYPE_SHOPPING_CART.equals(this.submitType))
			{
				tradeProducts = BofQuery.getTradeProductByUserId4Shopping(this.getUserId(), getQueryRoute());
			}
			else
			{
				tradeProducts = BofQuery.getTradeProductByUserId(this.getUserId(), getQueryRoute());
			}
			IDataset future = new DatasetList();
			if (userProducts != null && tradeProducts != null)
			{
				future = DataBusUtils.getFuture(userProducts, tradeProducts, new String[]
				{ "INST_ID" });
				future = DataBusUtils.filterInValidDataByEndDate(future);
			}
			if (future.size() > 0)
			{
				int size = future.size();
				for (int i = 0; i < size; i++)
				{
					ProductTradeData productData = new ProductTradeData(future.getData(i));
					productData.setModifyTag(BofConst.MODIFY_TAG_USER);
					this.userProducts.add(productData);
					// this.userOriginalData.addData(productData);
				}
			}

		}
		return this.userProducts;
	}

	public List<SaleActiveTradeData> getUserSaleActiveByProductId(String productId) throws Exception
	{
		List<SaleActiveTradeData> saleactiveTradeDataList = this.getUserSaleActives();

		List<SaleActiveTradeData> returnActiveList = new ArrayList<SaleActiveTradeData>();

		int size = saleactiveTradeDataList.size();
		for (int i = 0; i < size; i++)
		{
			SaleActiveTradeData userSaleActive = saleactiveTradeDataList.get(i);
			String saleProudctId = userSaleActive.getProductId();
			if (productId.equals(saleProudctId))
			{
				returnActiveList.add(userSaleActive);
			}
		}
		return returnActiveList;
	}
	
	public List<SaleActiveTradeData> getUserSaleActiveByPidPkid(String productId, String packageId) throws Exception
    {
        List<SaleActiveTradeData> saleactiveTradeDataList = this.getUserSaleActives();

        List<SaleActiveTradeData> returnActiveList = new ArrayList<SaleActiveTradeData>();

        int size = saleactiveTradeDataList.size();
        for (int i = 0; i < size; i++)
        {
            SaleActiveTradeData userSaleActive = saleactiveTradeDataList.get(i);
            String saleProudctId = userSaleActive.getProductId();
            String salePackageId = userSaleActive.getPackageId();
            if (productId.equals(saleProudctId) && packageId.equals(salePackageId))
            {
                returnActiveList.add(userSaleActive);
            }
        }
        return returnActiveList;
    }

	public List<SaleActiveTradeData> getUserSaleActiveByProductIds(List<String> productIds) throws Exception
	{
		List<SaleActiveTradeData> result = new ArrayList<SaleActiveTradeData>();
		this.getUserSaleActives();
		int size = this.userSaleActives.size();
		for (int i = 0; i < size; i++)
		{
			SaleActiveTradeData userSaleActive = this.userSaleActives.get(i);
			if (productIds.contains(userSaleActive.getProductId()))
			{
				result.add(userSaleActive);
			}
		}
		return result;
	}

	public SaleActiveTradeData getUserSaleActiveByRelaTradeId(String relationTradeId) throws Exception
	{
		List<SaleActiveTradeData> saleactiveTradeDataList = this.getUserSaleActives();
		int size = saleactiveTradeDataList.size();
		for (int i = 0; i < size; i++)
		{
			SaleActiveTradeData userSaleActive = saleactiveTradeDataList.get(i);
			String saleRalationId = userSaleActive.getRelationTradeId();
			if (saleRalationId.equals(relationTradeId))
			{
				return userSaleActive;
			}
		}
		return null;
	}

	public List<SaleActiveTradeData> getUserSaleActiveExceptProductAndPackage(List<String> products, List<String> packages) throws Exception
	{
		List<SaleActiveTradeData> result = new ArrayList<SaleActiveTradeData>();
		this.getUserSaleActives();
		int size = this.userSaleActives.size();
		for (int i = 0; i < size; i++)
		{
			SaleActiveTradeData userSaleActive = this.userSaleActives.get(i);
			if (products.contains(userSaleActive.getProductId()))
			{
				continue;
			}
			if (packages.contains(userSaleActive.getPackageId()))
			{
				continue;
			}
			result.add(userSaleActive);
		}
		return result;
	}

	public List<SaleActiveTradeData> getUserSaleActiveExceptProductIds(List<String> productIds) throws Exception
	{
		List<SaleActiveTradeData> result = new ArrayList<SaleActiveTradeData>();
		this.getUserSaleActives();
		int size = this.userSaleActives.size();
		for (int i = 0; i < size; i++)
		{
			SaleActiveTradeData userSaleActive = this.userSaleActives.get(i);
			if (productIds.contains(userSaleActive.getProductId()))
			{
				continue;
			}
			result.add(userSaleActive);
		}
		return result;
	}

	/**
	 * 获取用户所有的营销活动主信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<SaleActiveTradeData> getUserSaleActives() throws Exception
	{
		if (this.userSaleActives == null)
		{
			this.userSaleActives = new ArrayList<SaleActiveTradeData>();
			IDataset tradeSaleActives = new DatasetList();
			if (BofConst.SUBMIT_TYPE_SHOPPING_CART.equals(this.submitType))
			{
				tradeSaleActives = BofQuery.queryTradeSaleActivesByUserId4Shopping(this.getUserId(), getQueryRoute());
			}
			else
			{
				tradeSaleActives = BofQuery.queryTradeSaleActivesByUserId(this.getUserId(), getQueryRoute());
			}
			IDataset userSaleActives = BofQuery.queryValidSaleActives(this.getUserId(), getQueryRoute());
			IDataset future = new DatasetList();
			if (userSaleActives != null && tradeSaleActives != null)
			{
				future = DataBusUtils.getFuture(userSaleActives, tradeSaleActives, new String[]
				{ "RELATION_TRADE_ID" });
				future = DataBusUtils.filterInValidDataByEndDate(future);
			}
			if (future.size() > 0)
			{
				int size = future.size();
				for (int i = 0; i < size; i++)
				{
					SaleActiveTradeData saleactiveTradeData = new SaleActiveTradeData(future.getData(i));
					saleactiveTradeData.setModifyTag(BofConst.MODIFY_TAG_USER);
					this.userSaleActives.add(saleactiveTradeData);
					// this.userOriginalData.addData(saleactiveTradeData);
				}
			}

		}
		return this.userSaleActives;
	}

	/**
	 * 获取用户所有的营销活动预存信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<SaleDepositTradeData> getUserSaleDeposit() throws Exception
	{
		if (this.userSaleDeposits == null)
		{
			this.userSaleDeposits = new ArrayList<SaleDepositTradeData>();
			IDataset tradeSaleDeposits = BofQuery.queryTradeSaleDepositsByUserId(this.getUserId(), getQueryRoute());
			IDataset userSaleDeposits = BofQuery.queryValidSaleDeposits(this.getUserId(), getQueryRoute());
			IDataset future = new DatasetList();
			if (userSaleDeposits != null && tradeSaleDeposits != null)
			{
				future = DataBusUtils.getFuture(userSaleDeposits, tradeSaleDeposits, new String[]
				{ "INST_ID" });
				future = DataBusUtils.filterInValidDataByEndDate(future);
			}
			if (future.size() > 0)
			{
				int size = future.size();
				for (int i = 0; i < size; i++)
				{
					SaleDepositTradeData saleDepositTradeData = new SaleDepositTradeData(future.getData(i));
					saleDepositTradeData.setModifyTag(BofConst.MODIFY_TAG_USER);
					this.userSaleDeposits.add(saleDepositTradeData);
					// this.userOriginalData.addData(saleDepositTradeData);
				}
			}

		}
		return this.userSaleDeposits;
	}

	public SaleDepositTradeData getUserSaleDepositByInstId(String instId) throws Exception
	{
		List<SaleDepositTradeData> saleDepositTradeDatas = this.getUserSaleDeposit();
		int size = saleDepositTradeDatas.size();
		for (int i = 0; i < size; i++)
		{
			SaleDepositTradeData saleDepositTradeData = saleDepositTradeDatas.get(i);
			if (instId.equals(saleDepositTradeData.getInstId()))
			{
				return saleDepositTradeData;
			}
		}
		return null;
	}

	public List<SaleDepositTradeData> getUserSaleDepositByRelationTradeId(String relationTradeId) throws Exception
	{
		List<SaleDepositTradeData> saleDepositTradeDataList = new ArrayList<SaleDepositTradeData>();
		List<SaleDepositTradeData> saleDepositTradeDatas = this.getUserSaleDeposit();
		int size = saleDepositTradeDatas.size();
		for (int i = 0; i < size; i++)
		{
			SaleDepositTradeData saleDepositTradeData = saleDepositTradeDatas.get(i);
			if (relationTradeId.equals(saleDepositTradeData.getRelationTradeId()))
			{
				saleDepositTradeDataList.add(saleDepositTradeData);
			}
		}
		return saleDepositTradeDataList;
	}

	/**
	 * 获取用户所有的营销活动实物信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<SaleGoodsTradeData> getUserSaleGoods() throws Exception
	{
		if (this.userSaleGoods == null)
		{
			this.userSaleGoods = new ArrayList<SaleGoodsTradeData>();
			IDataset tradeSaleGoods = BofQuery.queryTradeSaleGoodsByUserId(this.getUserId(), getQueryRoute());
			IDataset userSaleGoods = BofQuery.queryValidSaleGoods(this.getUserId(), getQueryRoute());
			IDataset future = new DatasetList();
			if (userSaleGoods != null && tradeSaleGoods != null)
			{
				future = DataBusUtils.getFuture(userSaleGoods, tradeSaleGoods, new String[]
				{ "INST_ID" });
				// future = DataBusUtils.filterInValidDataByEndDate(future);
				for (int i = future.size() - 1; i >= 0; i--)
				{
					IData tmpData = future.getData(i);
					if (!"0".equals(tmpData.getString("GOODS_STATE")))
					{
						future.remove(i);
					}
				}
			}
			if (future.size() > 0)
			{
				int size = future.size();
				for (int i = 0; i < size; i++)
				{
					SaleGoodsTradeData saleGoodsTradeData = new SaleGoodsTradeData(future.getData(i));
					saleGoodsTradeData.setModifyTag(BofConst.MODIFY_TAG_USER);
					this.userSaleGoods.add(saleGoodsTradeData);
					// this.userOriginalData.addData(saleGoodsTradeData);
				}
			}

		}
		return this.userSaleGoods;
	}

	public SaleGoodsTradeData getUserSaleGoodsByInstId(String instId) throws Exception
	{
		List<SaleGoodsTradeData> saleGoodsTradeDatas = this.getUserSaleGoods();
		int size = saleGoodsTradeDatas.size();
		for (int i = 0; i < size; i++)
		{
			SaleGoodsTradeData saleGoodsTradeData = saleGoodsTradeDatas.get(i);
			if (instId.equals(saleGoodsTradeData.getInstId()))
			{
				return saleGoodsTradeData;
			}
		}
		return null;
	}

	public List<SaleGoodsTradeData> getUserSaleGoodsByRelationTradeId(String relationTradeId) throws Exception
	{
		List<SaleGoodsTradeData> saleGoodsTradeDataList = new ArrayList<SaleGoodsTradeData>();
		List<SaleGoodsTradeData> saleGoodsTradeDatas = this.getUserSaleGoods();
		int size = saleGoodsTradeDatas.size();
		for (int i = 0; i < size; i++)
		{
			SaleGoodsTradeData saleGoodsTradeData = saleGoodsTradeDatas.get(i);
			if (relationTradeId.equals(saleGoodsTradeData.getRelationTradeId()))
			{
				saleGoodsTradeDataList.add(saleGoodsTradeData);
			}
		}
		return saleGoodsTradeDataList;
	}

	/**
	 * * 查询用户积分
	 * 
	 * @return
	 * @throws Exception
	 */
	public int getUserScore() throws Exception
	{
		if (StringUtils.isBlank(this.userScore))
		{
			if (!BofConst.MODIFY_TAG_ADD.equals(this.getUser().getModifyTag()))
			{
				IDataset result = AcctCall.queryUserScore(this.getUserId());
				if (IDataUtil.isEmpty(result))
				{
					// 报错
					CSAppException.apperr(BofException.CRM_BOF_019);
				}
				IData scoreInfo = result.getData(0);
				this.userScore = scoreInfo.getString("SUM_SCORE");
			}
			else
			{
				this.userScore = "0";
			}
		}

		return Integer.parseInt(this.userScore);
	}

	/**
	 * 根据instId获取用户的服务资料，单条数据
	 * 
	 * @param instId
	 * @return
	 * @throws Exception
	 */
	public SvcTradeData getUserSvcByInstId(String instId) throws Exception
	{
		List<SvcTradeData> svcTradeDatas = this.getUserSvcs();
		int size = svcTradeDatas.size();
		for (int i = 0; i < size; i++)
		{
			SvcTradeData std = svcTradeDatas.get(i);
			if (std.getInstId().equals(instId))
			{
				return std;
			}
		}
		return null;
	}

	public List<SvcTradeData> getUserSvcByPidPkId(String productId, String packageId) throws Exception
	{
		List<SvcTradeData> svcTradeDatas = this.getUserSvcs();
		List<SvcTradeData> result = new ArrayList<SvcTradeData>();
		int size = svcTradeDatas.size();
		for (int i = 0; i < size; i++)
		{
			SvcTradeData std = svcTradeDatas.get(i);
			if (StringUtils.equals(std.getProductId(), productId) && StringUtils.equals(std.getPackageId(), packageId))
			{
				result.add(std);
			}
		}
		return result;
	}

	/**
	 * 根据服务ID攻取用户的所有服务资料
	 * 
	 * @param serviceId
	 * @return
	 * @throws Exception
	 */
	public List<SvcTradeData> getUserSvcBySvcId(String serviceId) throws Exception
	{
		List<SvcTradeData> svcTradeDatas = this.getUserSvcs();
		List<SvcTradeData> result = new ArrayList<SvcTradeData>();
		int size = svcTradeDatas.size();
		for (int i = 0; i < size; i++)
		{
			SvcTradeData std = svcTradeDatas.get(i);
			if (std.getElementId().equals(serviceId))
			{
				result.add(std);
			}
		}
		return result;
	}

	/**
	 * 获取用户所有的服务资料
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<SvcTradeData> getUserSvcs() throws Exception
	{
		if (this.userSvcs == null)
		{
			this.userSvcs = new ArrayList<SvcTradeData>();
			IDataset tradeSvcs = new DatasetList();
			if (BofConst.SUBMIT_TYPE_SHOPPING_CART.equals(this.submitType))
			{
				tradeSvcs = BofQuery.queryTradeSvcsByUserId4Shopping(this.getUserId(), getQueryRoute());
			}
			else
			{
				tradeSvcs = BofQuery.queryTradeSvcsByUserId(this.getUserId(), getQueryRoute());
			}
			IDataset userSvcs = BofQuery.queryUserAllSvc(this.getUserId(), getQueryRoute());
			IDataset future = new DatasetList();
			if (userSvcs != null && tradeSvcs != null)
			{
				future = DataBusUtils.getFuture(userSvcs, tradeSvcs, new String[]
				{ "INST_ID" });
				future = DataBusUtils.filterInValidDataByEndDate(future);
			}
			if (future.size() > 0)
			{
				int size = future.size();
				for (int i = 0; i < size; i++)
				{
					SvcTradeData svcTradeData = new SvcTradeData(future.getData(i));
					svcTradeData.setModifyTag(BofConst.MODIFY_TAG_USER);
					this.userSvcs.add(svcTradeData);
					// this.userOriginalData.addData(svcTradeData);
				}
			}

		}
		return this.userSvcs;
	}

	/**
	 * 传入多个服务ID，获取对应的服务资料
	 * 
	 * @param serviceIds
	 *            多个serviceId，用分隔符分隔开
	 * @return
	 * @throws Exception
	 */
	public List<SvcTradeData> getUserSvcsBySvcIdArray(String serviceIds) throws Exception
	{
		List<SvcTradeData> result = new ArrayList<SvcTradeData>();
		String[] serviceIdList = serviceIds.split(",");
		for (String serviceId : serviceIdList)
		{
			result.addAll(this.getUserSvcBySvcId(serviceId));
		}
		return result;
	}
	
	public List<OfferRelTradeData> getOfferRelsByRelUserId() throws Exception{
		if(this.userOfferRels != null){
			return this.userOfferRels;
		}
		String userId = this.getUserId();
		if(StringUtils.isBlank(userId)){
			return null;
		}
		
		this.userOfferRels = new ArrayList<OfferRelTradeData>();
		IDataset tradeOfferRels = new DatasetList();
		if (BofConst.SUBMIT_TYPE_SHOPPING_CART.equals(this.submitType))
		{
			tradeOfferRels = BofQuery.queryTradeOfferRelsByUserId4Shopping(this.getUserId(), getQueryRoute());
		}
		else
		{
			tradeOfferRels = BofQuery.queryTradeOfferRelsByUserId(this.getUserId(), getQueryRoute());
		}
		IDataset userOfferRels = BofQuery.queryUserAllOfferRelByUserId(this.getUserId(), getQueryRoute());
		
		IDataset future = new DatasetList();
		if (userOfferRels != null && tradeOfferRels != null)
		{
			future = DataBusUtils.getFuture(userOfferRels, tradeOfferRels, new String[]{ "INST_ID" });
			future = DataBusUtils.filterInValidDataByEndDate(future);
		}
		if (future.size() > 0)
		{
			int size = future.size();
			for (int i = 0; i < size; i++)
			{
				OfferRelTradeData offerRelTradeData = new OfferRelTradeData(future.getData(i));
				offerRelTradeData.setModifyTag(BofConst.MODIFY_TAG_USER);
				this.userOfferRels.add(offerRelTradeData);
			}
		}
		return this.userOfferRels;
	}
	
	public List<OfferRelTradeData> getOfferRelByRelUserIdAndRelOfferInsId(String relOfferInsId) throws Exception{
		if(this.userOfferRels == null){
			this.getOfferRelsByRelUserId();
		}
		if(ArrayUtil.isEmpty(this.userOfferRels)){
			return null;
		}
		List<OfferRelTradeData> rst = new ArrayList<OfferRelTradeData>();
		for(OfferRelTradeData offerRel : this.userOfferRels){
			if(relOfferInsId.equals(offerRel.getRelOfferInsId())){
				rst.add(offerRel);
			}
		}
		return rst;
	}
	
	public List<OfferRelTradeData> getOfferRelByRelUserIdAndOfferInsId(String offerInsId) throws Exception{
        if(this.userOfferRels == null){
            this.getOfferRelsByRelUserId();
        }
        if(ArrayUtil.isEmpty(this.userOfferRels)){
            return null;
        }
        List<OfferRelTradeData> rst = new ArrayList<OfferRelTradeData>();
        for(OfferRelTradeData offerRel : this.userOfferRels){
            if(offerInsId.equals(offerRel.getOfferInsId())){
                rst.add(offerRel);
            }
        }
        return rst;
    }
	
	public List<OfferRelTradeData> getOfferRelByRelUserIdAndTwoInsId(String offerInsId, String relOfferInsId) throws Exception{
		if(this.userOfferRels == null){
			this.getOfferRelsByRelUserId();
		}
		if(ArrayUtil.isEmpty(this.userOfferRels)){
			return null;
		}
		List<OfferRelTradeData> rst = new ArrayList<OfferRelTradeData>();
		for(OfferRelTradeData offerRel : this.userOfferRels){
			if(relOfferInsId.equals(offerRel.getRelOfferInsId()) && offerInsId.equals(offerRel.getOfferInsId())){
				rst.add(offerRel);
			}
		}
		return rst;
	}
	
	public OfferRelTradeData getNowValidMainProductOfferRelByRelUserIdAndRelOfferInsId(String offerInsId) throws Exception{
		ProductTradeData product = this.getUserMainProduct();
		if(product == null){
			return null;
		}
		String productInsId = product.getInstId();
		List<OfferRelTradeData> offerRels = this.getOfferRelByRelUserIdAndTwoInsId(productInsId, offerInsId);
		if(ArrayUtil.isEmpty(offerRels)){
			return null;
		}
		String sysTime = DataBusManager.getDataBus().getAcceptTime();
		for(OfferRelTradeData offerRel : offerRels){
			if(SysDateMgr.compareTo(offerRel.getStartDate(), sysTime) <= 0){
				return offerRel;
			}
		}
		return null;
	}
	
	public OfferRelTradeData getNowValidOfferRelWithTwoInstId(String offerInsId, String relOfferInsId) throws Exception{
		List<OfferRelTradeData> offerRels = this.getOfferRelByRelUserIdAndTwoInsId(offerInsId, relOfferInsId);
		if(ArrayUtil.isEmpty(offerRels)){
			return null;
		}
		String sysTime = DataBusManager.getDataBus().getAcceptTime();
		for(OfferRelTradeData offerRel : offerRels){
			if(SysDateMgr.compareTo(offerRel.getStartDate(), sysTime) <= 0){
				return offerRel;
			}
		}
		return null;
	}
	
	public OfferRelTradeData getOfferRelByRelUserIdAndInstId(String instId) throws Exception{
		if(this.userOfferRels == null){
			this.getOfferRelsByRelUserId();
		}
		if(ArrayUtil.isEmpty(this.userOfferRels)){
			return null;
		}
		for(OfferRelTradeData offerRel : this.userOfferRels){
			if(instId.equals(offerRel.getInstId())){
				return offerRel;
			}
		}
		return null;
	}
	
	public OfferRelTradeData getNewestMainProductRelByUserIdAndInstId(String instId) throws Exception{
		ProductTradeData product = this.getUserMainProduct();
		ProductTradeData nextProduct = this.getUserNextMainProduct();
		List<OfferRelTradeData> offerRels = null;
		if(nextProduct != null){
			offerRels = this.getOfferRelByRelUserIdAndTwoInsId(nextProduct.getInstId(), instId);
			if(ArrayUtil.isNotEmpty(offerRels)){
				return offerRels.get(0);
			}
		}
		
		offerRels = this.getOfferRelByRelUserIdAndTwoInsId(product.getInstId(), instId);
		if(ArrayUtil.isNotEmpty(offerRels)){
			return offerRels.get(0);
		}
		return null;
	}
	
	public OfferRelTradeData getLastMainProductRel(String relOfferInsId) throws Exception{
		List<OfferRelTradeData> offerRels = this.getOfferRelByRelUserIdAndRelOfferInsId(relOfferInsId);
		if(ArrayUtil.isEmpty(offerRels)){
			return null;
		}
		String lastProductId = this.getUserNewMainProductId();
		for(OfferRelTradeData offerRel : offerRels){
			if(lastProductId.equals(offerRel.getOfferCode()) && "C".equals(offerRel.getRelType()) && BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(offerRel.getOfferType())){
				return offerRel;
			}
		}
		return null;
	}
	
	public List<DiscntTradeData> getDiscntsByGroupId(String groupId) throws Exception{
		if(this.userOfferRels == null){
			this.getOfferRelsByRelUserId();
		}
		
		if(ArrayUtil.isEmpty(this.userOfferRels)){
			return null;
		}
		
		List<DiscntTradeData> rst = new ArrayList<DiscntTradeData>();
		for(OfferRelTradeData offerRel : this.userOfferRels){
			if(BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(offerRel.getRelOfferType()) && groupId.equals(offerRel.getGroupId())){
				String relOfferInsId = offerRel.getRelOfferInsId();
				DiscntTradeData discnt = this.getUserDiscntByInstId(relOfferInsId);
				if(discnt != null){
					rst.add(discnt);
				}
			}
		}
		return rst;
	}
	
	public List<PricePlanTradeData> getUserPricePlans() throws Exception{
		if(this.userPricePlans != null){
			return this.userPricePlans;
		}
		String userId = this.getUserId();
		if(StringUtils.isBlank(userId)){
			return null;
		}
		
		this.userPricePlans = new ArrayList<PricePlanTradeData>();
		IDataset tradePricePlans = new DatasetList();
		if (BofConst.SUBMIT_TYPE_SHOPPING_CART.equals(this.submitType))
		{
			tradePricePlans = BofQuery.queryTradePricePlanByUserId4Shopping(this.getUserId(), getQueryRoute());
		}
		else
		{
			tradePricePlans = BofQuery.queryTradePricePlanByUserId(this.getUserId(), getQueryRoute());
		}
		IDataset userPricePlans = BofQuery.queryUserAllPricePlanByUserId(this.getUserId(), getQueryRoute());
		
		IDataset future = new DatasetList();
		if (userPricePlans != null && tradePricePlans != null)
		{
			future = DataBusUtils.getFuture(userPricePlans, tradePricePlans, new String[]{ "INST_ID" });
			future = DataBusUtils.filterInValidDataByEndDate(future);
		}
		if (future.size() > 0)
		{
			int size = future.size();
			for (int i = 0; i < size; i++)
			{
				PricePlanTradeData pricePlanTradeData = new PricePlanTradeData(future.getData(i));
				pricePlanTradeData.setModifyTag(BofConst.MODIFY_TAG_USER);
				this.userPricePlans.add(pricePlanTradeData);
			}
		}
		return this.userPricePlans;
	}
	
	public List<PricePlanTradeData> getPricePlansByOfferInsId(String offerInsId) throws Exception{
		if(this.userPricePlans == null){
			this.getUserPricePlans();
		}
		if(ArrayUtil.isEmpty(this.userPricePlans)){
			return null;
		}
		List<PricePlanTradeData> rst = new ArrayList<PricePlanTradeData>();
		for(PricePlanTradeData pricePlan : this.userPricePlans){
			if(offerInsId.equals(pricePlan.getOfferInsId())){
				rst.add(pricePlan);
			}
		}
		return rst;
	}

	/**
	 * 获取用户所有的服务状态信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<SvcStateTradeData> getUserSvcsState() throws Exception
	{
		if (this.userSvcsState == null)
		{
			this.userSvcsState = new ArrayList<SvcStateTradeData>();
			IDataset tradeSvcState = BofQuery.queryTradeSvcsStateByUserId(this.getUserId(), getQueryRoute());
			IDataset userSvcsState = BofQuery.getUserValidSvcStateByUserId(this.getUserId(), getQueryRoute());
			IDataset future = new DatasetList();
			if (userSvcsState != null && tradeSvcState != null)
			{
				future = DataBusUtils.getFutureForSpec(userSvcsState, tradeSvcState, new String[]
				{ "SERVICE_ID" });
				future = DataBusUtils.filterInValidDataByEndDate(future);
			}
			if (future.size() > 0)
			{
				int size = future.size();
				for (int i = 0; i < size; i++)
				{
					SvcStateTradeData svcStateTradeData = new SvcStateTradeData(future.getData(i));
					svcStateTradeData.setModifyTag(BofConst.MODIFY_TAG_USER);
					this.userSvcsState.add(svcStateTradeData);
					// this.userOriginalData.addData(svcStateTradeData);
				}
			}

		}
		return this.userSvcsState;
	}

	/**
	 * 根据服务ID获取对应的服务状态信息
	 * 
	 * @param serviceId
	 * @return
	 * @throws Exception
	 */
	public SvcStateTradeData getUserSvcsStateByServiceId(String serviceId) throws Exception
	{
		List<SvcStateTradeData> svcStateTradeDatas = this.getUserSvcsState();
		int size = svcStateTradeDatas.size();
		for (int i = 0; i < size; i++)
		{
			SvcStateTradeData svcStateTd = svcStateTradeDatas.get(i);
			if (svcStateTd.getServiceId().equals(serviceId))
			{
				return svcStateTd;
			}
		}
		return null;
	}

	/**
	 * 获取vip信息，如果该用户不是VIP，则返回null
	 * 
	 * @return
	 * @throws Exception
	 */
	public VipTradeData getVip() throws Exception
	{
		if ("".equals(this.isVip))
		{
			IDataset vipInfos = BofQuery.getVip(this.getUserId(), "0", this.getUserEparchyCode());
			if (IDataUtil.isNotEmpty(vipInfos))
			{
				this.vip = new VipTradeData(vipInfos.getData(0));
				this.isVip = "true";
			}
			else
			{
				this.isVip = "false";
			}
		}
		return vip;
	}

	/**
	 * * 获取用户VIP级别
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getVipTypeCode() throws Exception
	{
		if ("".equals(isVip))
		{
			this.getVip();
		}
		if ("true".equals(this.isVip))
		{
			return vip.getVipTypeCode();
		}
		else
		{
			return "";
		}
	}

	/**
	 * * 是否红名单
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean isRedUser() throws Exception
	{
		boolean isRedUserFlag = false;
		if (StringUtils.isBlank(this.isRedUser))
		{
			if (!BofConst.MODIFY_TAG_ADD.equals(this.getUser().getModifyTag()))
			{
				// 调接口
				String userId = this.getUserId();
				isRedUserFlag = AcctCall.checkIsRedUser(userId);

				this.isRedUser = isRedUserFlag ? "true" : "false";
			}
			else
			{
				this.isRedUser = "false";
			}
		}
		else
		{
			if ("true".equals(this.isRedUser))
			{
				isRedUserFlag = true;
			}
			else
			{
				isRedUserFlag = false;
			}
		}

		return isRedUserFlag;
	}

	/**
	 * 设置帐户信息
	 * 
	 * @param account
	 */
	public void setAccount(AccountTradeData account)
	{
		this.account = account;
	}

	/**
	 * 设置实时结余信息
	 * 
	 * @param acctBlance
	 */
	public void setAcctBlance(String acctBlance)
	{
		this.acctBlance = acctBlance;
	}

	/**
	 * 设置当前结帐日信息
	 * 
	 * @param acctDay
	 */
	public void setAcctDay(String acctDay)
	{
		this.acctDay = acctDay;
	}

	/**
	 * 设置当前结帐日的开始时间
	 * 
	 * @param acctDayStartDate
	 */
	public void setAcctDayStartDate(String acctDayStartDate)
	{
		this.acctDayStartDate = acctDayStartDate;
	}

	public void setAcctTimeEnv() throws Exception
	{
		AcctTimeEnv env = new AcctTimeEnv(this.getAcctDay(), this.getFirstDate(), this.getNextAcctDay(), this.getNextFirstDate(), this.getAcctDayStartDate(), this.getNextAcctDayStartDate());
		AcctTimeEnvManager.setAcctTimeEnv(env);
	}

	/**
	 * 设置用户品牌信息
	 * 
	 * @param brandCode
	 */
	public void setBrandCode(String brandCode)
	{
		this.brandCode = brandCode;
	}

	/**
	 * 设置用户要变更的结帐日信息
	 * 
	 * @param changeAcctDay
	 */
	public void setChangeAcctDay(String changeAcctDay)
	{
		this.changeAcctDay = changeAcctDay;
	}

	/**
	 * 设置家庭客户信息，即tf_f_cust_family数据
	 * 
	 * @param customer
	 */
	public void setCustFamily(CustFamilyTradeData custFamily)
	{
		this.custFamily = custFamily;
	}

	/**
	 * 设置集团客户信息,即tf_f_cust_group表数据
	 * 
	 * @param custgroup
	 */
	public void setCustgroup(CustGroupTradeData custgroup)
	{
		this.custgroup = custgroup;
	}

	/**
	 * 设置客户表信息，即tf_f_customer数据
	 * 
	 * @param customer
	 */
	public void setCustomer(CustomerTradeData customer)
	{
		this.customer = customer;
	}

	/**
	 * 设置个人客户信息，即tf_f_cust_person数据
	 * 
	 * @param custPerson
	 */
	public void setCustPerson(CustPersonTradeData custPerson)
	{
		this.custPerson = custPerson;
	}

	/**
	 * 设置当前结帐日的首次结帐日数据
	 * 
	 * @param firstDate
	 */
	public void setFirstDate(String firstDate)
	{
		this.firstDate = firstDate;
	}

	/**
	 * 设置往月欠费信息
	 * 
	 * @param lastOweFee
	 */
	public void setLastOweFee(String lastOweFee)
	{
		this.lastOweFee = lastOweFee;
	}

	/**
	 * 设置预约结帐日信息
	 * 
	 * @param nextAcctDay
	 */
	public void setNextAcctDay(String nextAcctDay)
	{
		this.nextAcctDay = nextAcctDay;
	}

	/**
	 * 设置预约结帐日的开始时间
	 * 
	 * @param nextAcctDayStartDate
	 */
	public void setNextAcctDayStartDate(String nextAcctDayStartDate)
	{
		this.nextAcctDayStartDate = nextAcctDayStartDate;
	}

	/**
	 * 设置预约结帐日的首次结帐日
	 * 
	 * @param nextFirstDate
	 */
	public void setNextFirstDate(String nextFirstDate)
	{
		this.nextFirstDate = nextFirstDate;
	}

	/**
	 * 设置用户的主产品信息
	 * 
	 * @param productId
	 */
	public void setProductId(String productId)
	{
		this.productId = productId;
	}

	/**
	 * 设置用户的实时话费信息
	 * 
	 * @param realFee
	 */
	public void setRealFee(String realFee)
	{
		this.realFee = realFee;
	}

	public void setSubmitType(String submitType)
	{
		this.submitType = submitType;
	}

	/**
	 * 设置用户数据，即tf_f_user表的数据
	 * 
	 * @param user
	 */
	public void setUser(UserTradeData user)
	{
		this.user = user;
	}

	/**
	 * 切换用户，适用于省内携号，替换用户相关资料的userId,其它信息不变
	 * 
	 * @param ucaData
	 * @throws Exception
	 */
	public void su(UcaData ucaData) throws Exception
	{
		List<SvcTradeData> svcList = (List) Clone.deepClone(this.getUserSvcs());
		for (int i = svcList.size() - 1; i >= 0; i--)
		{
			svcList.get(i).setUserId(ucaData.getUserId());
		}
		ucaData.userSvcs = svcList;

		List<AttrTradeData> attrList = (List) Clone.deepClone(this.getUserAttrs());
		for (int i = attrList.size() - 1; i >= 0; i--)
		{
			attrList.get(i).setUserId(ucaData.getUserId());
		}
		ucaData.userAttrs = attrList;

		List<DiscntTradeData> discntList = (List) Clone.deepClone(this.getUserDiscnts());
		for (int i = discntList.size() - 1; i >= 0; i--)
		{
			discntList.get(i).setUserId(ucaData.getUserId());
		}
		ucaData.userDiscnts = discntList;

		List<PlatSvcTradeData> platSvcList = (List) Clone.deepClone(this.getUserPlatSvcs());
		for (int i = platSvcList.size() - 1; i >= 0; i--)
		{
			platSvcList.get(i).setUserId(ucaData.getUserId());
		}
		ucaData.userPlatSvcs = platSvcList;

		List<ProductTradeData> productList = (List) Clone.deepClone(this.getUserProducts());
		for (int i = productList.size() - 1; i >= 0; i--)
		{
			productList.get(i).setUserId(ucaData.getUserId());
		}
		ucaData.userProducts = productList;

		List<SaleActiveTradeData> saleActiveList = (List) Clone.deepClone(this.getUserSaleActives());
		for (int i = saleActiveList.size() - 1; i >= 0; i--)
		{
			saleActiveList.get(i).setUserId(ucaData.getUserId());
		}
		ucaData.userSaleActives = saleActiveList;

		List<SaleDepositTradeData> saleDepositList = (List) Clone.deepClone(this.getUserSaleDeposit());
		for (int i = saleDepositList.size() - 1; i >= 0; i--)
		{
			saleDepositList.get(i).setUserId(ucaData.getUserId());
		}
		ucaData.userSaleDeposits = saleDepositList;

		List<SaleGoodsTradeData> saleGoodsList = (List) Clone.deepClone(this.getUserSaleGoods());
		for (int i = saleGoodsList.size() - 1; i >= 0; i--)
		{
			saleGoodsList.get(i).setUserId(ucaData.getUserId());
		}
		ucaData.userSaleGoods = saleGoodsList;

		List<SvcStateTradeData> svcStateList = (List) Clone.deepClone(this.getUserSvcsState());
		for (int i = svcStateList.size() - 1; i >= 0; i--)
		{
			svcStateList.get(i).setUserId(ucaData.getUserId());
		}
		ucaData.userSvcsState = svcStateList;
	}

	public void setUserSvcStateList(List<SvcStateTradeData> svcStateTradeDatas) throws Exception
	{
		this.userSvcsState = svcStateTradeDatas;
	}

	public void setUserAttrs(List<AttrTradeData> userAttrs) {
		this.userAttrs = userAttrs;
	}

	public void setUserDiscnts(List<DiscntTradeData> userDiscnts) {
		this.userDiscnts = userDiscnts;
	}

	public void setUserSvcs(List<SvcTradeData> userSvcs) {
		this.userSvcs = userSvcs;
	}

}
