
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.orderdata.MainOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustGroupTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;

public class RegTradeData<K extends BaseTradeData>
{
	private String tradeId;

	private boolean isLoaded = true;

	private MainOrderData orderData = null;

	private Map<String, IDataset> tradeMapDataset = new HashMap<String, IDataset>();

	private Map<String, List<K>> tradeMapObject = new HashMap<String, List<K>>();

	private Map<String, UcaData> ucaMap = new HashMap<String, UcaData>();

	private String acceptTime;

	private String orderTypeCode;

	public RegTradeData(BusiTradeData btd) throws Exception
	{
		this.isLoaded = false;
		this.tradeMapObject.putAll(btd.getTradeDatasMap());
		this.tradeId = btd.getRD().getTradeId();
		this.acceptTime = btd.getRD().getAcceptTime();
		UcaData uca = btd.getRD().getUca();
		this.ucaMap.put(uca.getSerialNumber(), uca);
		this.orderTypeCode = btd.getRD().getOrderTypeCode();
	}

	public RegTradeData(IData mainTradeData) throws Exception
	{
		List<K> mainTrades = new ArrayList<K>();
		MainTradeData main = new MainTradeData(mainTradeData);
		mainTrades.add((K) main);
		this.tradeMapObject.put("TF_B_TRADE", mainTrades);
		IDataset mainTradeDataset = new DatasetList();
		mainTradeDataset.add(mainTradeData);
		this.tradeMapDataset.put("TF_B_TRADE", mainTradeDataset);
		this.tradeId = mainTradeData.getString("TRADE_ID");
		this.acceptTime = mainTradeData.getString("ACCEPT_DATE");
	}

	public RegTradeData(String tradeId) throws Exception
	{
		this.tradeId = tradeId;
		this.getMainTradeData();
	}

	public RegTradeData(String tradeId, MainOrderData orderData) throws Exception
	{
		this.tradeId = tradeId;
		this.orderData = orderData;
		this.getMainTradeData();
	}

	public List<K> get(String tableName) throws Exception
	{
		return this.getTradeDatas(tableName);
	}

	public String getAcceptTime()
	{
		return acceptTime;
	}

	public IData getAllTableDataset() throws Exception
	{
		IData result = new DataMap();
		Iterator<String> iterator = this.tradeMapObject.keySet().iterator();
		while (iterator.hasNext())
		{
			String tableName = iterator.next();
			IDataset tableDatas = this.getDataset(tableName);
			result.put(tableName, tableDatas);
		}
		return result;
	}

	public IDataset getDataset(String tableName) throws Exception
	{
		if (tradeMapDataset.containsKey(tableName))
		{
			return tradeMapDataset.get(tableName);
		}
		if (isLoaded)
		{
			IData param = new DataMap();
			param.put("TRADE_ID", this.tradeId);
			IDataset tradeDataList = Dao.qryByCodeParser(tableName, "SEL_BY_TRADE_ID", param,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
			tradeMapDataset.put(tableName, tradeDataList);
			return tradeDataList;
		}
		else
		{
			List<K> datas = this.tradeMapObject.get(tableName);
			if (datas != null && datas.size() > 0)
			{
				IDataset dataset = new DatasetList();
				for (K data : datas)
				{
					dataset.add(data.toData());
				}
				return dataset;
			}
			else
			{
				return new DatasetList();
			}
		}
	}

	public MainTradeData getMainTradeData() throws Exception
	{
		if (this.tradeMapObject.containsKey("TF_B_TRADE"))
		{
			List<K> mtdList = this.tradeMapObject.get("TF_B_TRADE");
			if(ArrayUtil.isNotEmpty(mtdList))
			{
				return (MainTradeData) mtdList.get(0);
			}
		}
		List<K> list = this.getTradeDatas("TF_B_TRADE");
		if (list == null || list.size() <= 0)
		{
			list = this.getTradeDatas("TF_BH_TRADE");
		}
		if (list != null && list.size() > 0)
		{
			List<K> mainTrades = new ArrayList<K>();
			MainTradeData mainTradeData = (MainTradeData) list.get(0);
			mainTrades.add((K) mainTradeData);
			this.tradeMapObject.put("TF_B_TRADE", mainTrades);
			return mainTradeData;
		}
		else
		{
			return null;
		}
	}

	public MainOrderData getOrderData()
	{
		return orderData;
	}

	public String getOrderTypeCode()
	{
		return orderTypeCode;
	}

	public List<K> getTradeDatas(String tableName) throws Exception
	{
		if (tradeMapObject.containsKey(tableName))
		{
			return tradeMapObject.get(tableName);
		}
		else
		{
			IDataset tradeDataList = this.getDataset(tableName);
			if (IDataUtil.isNotEmpty(tradeDataList))
			{
				if ("TF_B_TRADE".equals(tableName) || "TF_BH_TRADE".equals(tableName))
				{
					this.acceptTime = tradeDataList.getData(0).getString("ACCEPT_DATE");
				}
				List<K> result = DataFactory.getInstance().getData(tableName, tradeDataList);
				tradeMapObject.put(tableName, result);
				return result;
			}
			else
			{
				List<K> list = new ArrayList<K>();
				tradeMapObject.put(tableName, list);
				return list;
			}
		}
	}

	public String getTradeId()
	{
		return tradeId;
	}

	public String getTradeTypeCode() throws Exception
	{
		return this.getMainTradeData().getTradeTypeCode();
	}

	public UcaData getUca() throws Exception
	{
		MainTradeData main = this.getMainTradeData();
		if (main != null)
		{
			String sn = main.getSerialNumber();
			String userId = main.getUserId();
			if (ucaMap.containsKey(sn))
			{
				return ucaMap.get(sn);
			}
			UcaData uca = new UcaData();

			// 查询用户信息
			IData userInfo = UcaInfoQry.qryUserInfoBySn(sn, RouteInfoQry.getEparchyCodeBySn(sn));
			if (IDataUtil.isNotEmpty(userInfo))
			{
				uca.setUser(new UserTradeData(userInfo));
			}
			else
			{
				List<K> userList = this.getTradeDatas("TF_B_TRADE_USER");
				if (userList != null && userList.size() > 0)
				{
					int size = userList.size();
					for (int i = 0; i < size; i++)
					{
						UserTradeData user = (UserTradeData) userList.get(i);
						if (user.getSerialNumber().equals(sn) || user.getUserId().equals(userId))
						{
							uca.setUser(user);
							break;
						}
					}
				}
				else
				{
					UserTradeData user = new UserTradeData();
					user.setSerialNumber(sn);
					user.setUserId(main.getUserId());
					user.setCustId(main.getCustId());
					user.setEparchyCode(main.getEparchyCode());
					uca.setUser(user);
					uca.setBrandCode(main.getBrandCode());
					uca.setProductId(main.getProductId());

				}
			}

			// 查询customer表信息
			IData customerInfo = UcaInfoQry.qryCustomerInfoByCustId(uca.getUser().getCustId());
			if (IDataUtil.isNotEmpty(customerInfo))
			{
				uca.setCustomer(new CustomerTradeData(customerInfo));
			}
			else
			{
				List<K> customerList = this.getTradeDatas("TF_B_TRADE_CUSTOMER");
				if (customerList != null && customerList.size() > 0)
				{
					int size = customerList.size();
					for (int i = 0; i < size; i++)
					{
						CustomerTradeData customer = (CustomerTradeData) customerList.get(i);
						if (customer.getCustId().equals(uca.getUser().getCustId()))
						{
							uca.setCustomer(customer);
							break;
						}
					}
				}
				else
				{
					CustomerTradeData customer = new CustomerTradeData();
					customer.setCustId(main.getCustId());
					customer.setCustName(main.getCustName());
					uca.setCustomer(customer);
				}
			}

			// 查询个人客户或者集团客户信息
			IData custInfo = UcaInfoQry.qryCustInfoByCustId(uca.getUser().getCustId());
			if (IDataUtil.isNotEmpty(custInfo))
			{
				if (StringUtils.isNotBlank(custInfo.getString("GROUP_ID")))
				{
					uca.setCustgroup(new CustGroupTradeData(custInfo));
				}
				else
				{
					uca.setCustPerson(new CustPersonTradeData(custInfo));
				}
			}
			else
			{
				List<K> custList = this.getTradeDatas("TF_B_TRADE_CUST_PERSON");
				if (custList != null && custList.size() > 0)
				{
					int size = custList.size();
					for (int i = 0; i < size; i++)
					{
						CustPersonTradeData custPerson = (CustPersonTradeData) custList.get(i);
						if (custPerson.getCustId().equals(uca.getUser().getCustId()))
						{
							uca.setCustPerson(custPerson);
							break;
						}
					}
				}
			}

			// 查询帐户信息
			IData acctInfo = UcaInfoQry.qryAcctInfoByUserId(uca.getUserId());
			if (IDataUtil.isNotEmpty(acctInfo))
			{
				uca.setAccount(new AccountTradeData(acctInfo));
			}
			else
			{
				List<K> accountList = this.getTradeDatas("TF_B_TRADE_ACCOUNT");
				if (accountList != null && accountList.size() > 0)
				{
					AccountTradeData account = (AccountTradeData) accountList.get(0);
					uca.setAccount(account);
				}
				else
				{
					AccountTradeData account = new AccountTradeData();
					account.setAcctId(main.getAcctId());
					uca.setAccount(account);
				}
			}

			IData productInfo = UcaInfoQry.qryMainProdInfoByUserId(uca.getUserId());
			if (IDataUtil.isNotEmpty(productInfo))
			{
				uca.setProductId(productInfo.getString("PRODUCT_ID"));
				uca.setBrandCode(productInfo.getString("BRAND_CODE"));
			}
			else
			{
				List<K> productList = this.getTradeDatas("TF_B_TRADE_PRODUCT");
				if (productList != null && productList.size() > 0)
				{
					int size = productList.size();
					for (int i = 0; i < size; i++)
					{
						ProductTradeData product = (ProductTradeData) productList.get(i);
						if ("1".equals(product.getMainTag()) && (BofConst.MODIFY_TAG_ADD.equals(product.getModifyTag())))
						{
							uca.setProductId(product.getProductId());
							uca.setBrandCode(product.getBrandCode());
							break;
						}
					}
				}
			}

			UcaDataFactory.setUserAcctDay(uca);
			this.ucaMap.put(sn, uca);
			return uca;
		}
		else
		{
			return null;
		}
	}

	public UcaData getUca(String sn) throws Exception
	{
		if (ucaMap.containsKey(sn))
		{
			return ucaMap.get(sn);
		}
		UcaData uca = UcaDataFactory.getNormalUca(sn);
		if (StringUtils.isBlank(DataBusManager.getDataBus().getOrderId()))
		{
			DataBusManager.removeDataBus();
		}
		this.ucaMap.put(sn, uca);
		return uca;
	}

	public Map<String, UcaData> getUcaMap()
	{
		return ucaMap;
	}

	public boolean isLoaded()
	{
		return isLoaded;
	}

	public void setAcceptTime(String acceptTime)
	{
		this.acceptTime = acceptTime;
	}

	public void setLoaded(boolean isLoaded)
	{
		this.isLoaded = isLoaded;
	}

	public void setOrderData(MainOrderData orderData)
	{
		this.orderData = orderData;
	}

	public void setOrderTypeCode(String orderTypeCode)
	{
		this.orderTypeCode = orderTypeCode;
	}

	public void setTradeId(String tradeId)
	{
		this.tradeId = tradeId;
	}

	public void setTradeMapObject(Map<String, List<K>> tradeMapObject)
	{
		this.tradeMapObject.putAll(tradeMapObject);
	}

}
