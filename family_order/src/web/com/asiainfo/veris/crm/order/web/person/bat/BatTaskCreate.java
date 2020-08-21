package com.asiainfo.veris.crm.order.web.person.bat;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class BatTaskCreate extends PersonBasePage
{

	/**
	 * @param cycle
	 * @throws Exception
	 */
	public void initBatDealInput(IRequestCycle cycle) throws Exception
	{
		IData data = new DataMap();
		data.put("CHECK_PRIV_FLAG", "0");
		data.put("RIGHT_CLASS", "0");
		data.put("TRADE_ATTR", "1");
		IDataset batchOpertypes = CSViewCall.call(this, "CS.BatDealSVC.queryBatchTypes", data);
		setBatchOperTypes(batchOpertypes);
		data.clear();
		data.put("START_DATE1", SysDateMgr.getSysDate());
		data.put("END_DATE1", SysDateMgr.addDays(data.getString("START_DATE1"), 6));

		setInfo(data);
		setTipInfo("选择业务类型然后在创建任务~");
	}

	public void loadPersonProductsTreeForBatCreateuser(IRequestCycle cycle) throws Exception
	{
		IData params = new DataMap();
		params = getData();
		StringBuilder sb = new StringBuilder();
		String productIds = "";
		String strBatchOperType = params.getString("BATCH_OPER_TYPE");
		IDataset products = new DatasetList();
		if ("CREATEPREUSER_M2M".equals(strBatchOperType))
		{
			params.put("PARAM_ATTR", "1994");
			products = CSViewCall.call(this, "SS.BatTaskCreateSVC.loadProductsForBatM2M", params);
		} else
		{
			products = CSViewCall.call(this, "SS.BatTaskCreateSVC.loadPersonProductsTreeForBatCreateuser", params);
		}

		if (IDataUtil.isNotEmpty(products))
		{
			for (int i = 0; i < products.size(); i++)
			{
				sb.append(products.getData(i).getString("PACKAGE_ID", ""));
				if (i < products.size() - 1)
				{
					sb.append(",");
				}
			}
			productIds = sb.toString();
		}
		params.put("PRODUCT_IDS", productIds);
		setAjax(params);
	}

	/**
	 * 批量返销营销活动 chenxy3 20160126 根据活动取产品
	 * */
	public void queryProdByLabel(IRequestCycle cycle) throws Exception
	{
		IData params = getData();
		params.put("EPARCHY_CODE", this.getTradeEparchyCode());
		IDataset prodList = CSViewCall.call(this, "SS.BatActiveCancelSVC.queryProductsByLabel", params);
		setAjax(prodList);
	}

	/**
	 * 批量返销营销活动 chenxy3 20160126 取包列表
	 * */
	public void queryPackageList(IRequestCycle cycle) throws Exception
	{
		IData params = getData();
		params.put("EPARCHY_CODE", this.getTradeEparchyCode());
		IDataset packList = CSViewCall.call(this, "SS.BatActiveCancelSVC.queryPackageByProdID", params);
		setAjax(packList);
	}

	public void selBatchCompByCode(IRequestCycle cycle) throws Exception
	{
		IDataset batchOpertypes = CSViewCall.call(this, "CS.BatDealSVC.queryBatTypeByPK", getData());
		if (batchOpertypes != null && batchOpertypes.size() > 0)
		{
			IData data = batchOpertypes.getData(0);
			setAjax(data);
		}
	}

	public void checkProducts(IRequestCycle cycle) throws Exception
	{
		IData param = getData();
		param.put("TRADE_TYPE_CODE", "500");
		IDataset batchOpertypes = CSViewCall.call(this, "SS.BatTaskCreateSVC.checkProducts", param);
		if (batchOpertypes != null && batchOpertypes.size() > 0)
		{
			IData data = batchOpertypes.getData(0);
			setAjax(data);
		}
	}

	/**
	 * 联系号码校验 @yanwu
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void checkPhone(IRequestCycle cycle) throws Exception
	{
		IData param = getData();
		IData putData = new DataMap();
		putData.put("SERIAL_NUMBER", param.getString("PHONE", ""));
		putData.put("PHONE", param.getString("PHONE", ""));
		putData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
		IDataset datas = CSViewCall.call(this, "SS.BatTaskCreateSVC.checkPhone", putData);
		if (IDataUtil.isNotEmpty(datas))
		{
			IData data = datas.getData(0);
			setAjax(data);
		}
	}

	/**
	 * 集团产品编码校验 @yanwu
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void checkPhoneBNBD(IRequestCycle cycle) throws Exception
	{
		IData param = getData();
		IData putData = new DataMap();
		putData.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER", ""));
		putData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
		IDataset datas = CSViewCall.call(this, "SS.CreatePersonUserSVC.checkPhoneBNBD", putData);
		if (IDataUtil.isNotEmpty(datas))
		{
			IData data = datas.getData(0);
			setAjax(data);
		}
	}

	public abstract void setBatchOperTypes(IDataset batchOperTypes);

	public abstract void setComp(IData comp);

	public abstract void setInfo(IData info);

	public abstract void setTipInfo(String tipInfo);

	/**
	 * @Function: submitBatTask
	 * @Description:
	 * @param: @param cycle
	 * @param: @throws Exception
	 * @return：void
	 * @throws：
	 * @version: v1.0.0
	 * @author: Administrator
	 * @date: 11:21:40 AM Mar 5, 2014 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------* Mar 5,
	 *        2014 tangxy v1.0.0 新建函数
	 */
	public void submitBatTask(IRequestCycle cycle) throws Exception
	{
		IData data = getData();
		String batchOperType = data.getString("BATCH_OPER_CODE");
		if ("BATCREATETRUNKUSER".equals(batchOperType))
		{
			String condings = data.getString("CODEINGSTR");
			IData conding = new DataMap(condings);
			IData param = new DataMap();
			param.put("SERIAL_NUMBER", conding.getString("MAIN_SERIAL_NUMBER"));
			IDataset ids = CSViewCall.call(this, "CS.BatDealSVC.getIds", param);
			if (IDataUtil.isNotEmpty(ids))
			{
				conding.putAll(ids.getData(0));
				String codingStr = String.valueOf(conding);
				data.put("CODEINGSTR", codingStr);
			}
		}
		if ("BATAPPENDTRUNKUSER".equals(batchOperType))
		{
			String condings = data.getString("CODEINGSTR");
			IData conding = new DataMap(condings);
			IData param = new DataMap();
			param.put("SERIAL_NUMBER", conding.getString("MAIN_SERIAL_NUMBER"));
			IDataset returnDatas = CSViewCall.call(this, "CS.BatDealSVC.checkForAppendTrunk", param);
			if (IDataUtil.isNotEmpty(returnDatas))
			{
				conding.putAll(returnDatas.getData(0));
				String codingStr = String.valueOf(conding);
				data.put("CODEINGSTR", codingStr);
			}
		}
		if ("CREATEPREUSER_PWLW".equals(batchOperType))  //批量物联网开户-GPRS服务属性校验
		{
			String strProductid = data.getString("PRODUCT_ID");
			String condings = data.getString("CODEINGSTR");
			IData conding = new DataMap(condings);
			conding.put("PRODUCT_ID", strProductid);
			conding.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
			CSViewCall.call(this, "SS.ChangeProductIntfSVC.checkPwlwSelectElements", conding);
        	//CSViewCall.call(this, "SS.ChangeProductIntfSVC.checkPwlwApnNameK", conding);
		}

		String codingstr = data.getString("CODEINGSTR");
		if ("BATPCRFCHG".equals(batchOperType))  //批量物联网开户-GPRS服务属性校验
		{
			String condings = data.getString("attrs");
			String service = data.getString("SERVICE");
			IDataset conding = new DatasetList(condings);
			IData value = new DataMap();
			IDataset values = new DatasetList();
			for(int i = 0 ; i<conding.size();i++){
				IData index = conding.getData(i);
				IData newValue = new DataMap();
				newValue.put("SERVICE_ID", service);
				newValue.put("SERVICE_CODE", index.getString("SERVICE_CODE"));
				newValue.put("BILLING_TYPE", index.getString("BILLING_TYPE"));
				newValue.put("USAGE_STATE", index.getString("USAGE_STATE"));
				newValue.put("START_DATE", index.getString("START_DATE"));
				newValue.put("END_DATE", index.getString("END_DATE"));
				newValue.put("MODIFY_TAG", index.getString("OPER_TYPE"));
				newValue.put("tag", "0");
				values.add(newValue);
			}
			value.put("X_BATPCRFREQ_STR", values);
			
			String tradeTypeTag = data.getString("TRADE_TYPE_TAG","");
			if(StringUtils.isNotBlank(tradeTypeTag)){
				value.put("TRADE_TYPE_TAG", tradeTypeTag);
			}
			
			if(StringUtils.isEmpty(codingstr)){
				codingstr = value.toString();
			}
		}
		IData codings = null;
		if (StringUtils.isNotEmpty(codingstr))
		{
			codings = new DataMap(codingstr);
		}
		if (IDataUtil.isNotEmpty(codings))
		{
			String selectedElements = codings.getString("SELECTED_ELEMENTS");
			if (StringUtils.isNotEmpty(selectedElements))
			{
				IDataset productElements = new DatasetList(selectedElements);
				IDataset productElementsNew = new DatasetList();
				if (IDataUtil.isNotEmpty(productElements))
				{
					for (int i = 0; i < productElements.size(); i++)
					{
						IData element = productElements.getData(i);
						if (IDataUtil.isNotEmpty(element))
							;
						String startDate = element.getString("START_DATE");
						String startDateS = startDate;
						if (StringUtils.isNotEmpty(startDate) && startDate.length() > 10)
						{
							startDateS = startDate.substring(0, 10);
							startDateS += SysDateMgr.START_DATE_FOREVER;
						}
						element.put("START_DATE", startDateS);
						productElementsNew.add(element);
					}
					codings.put("SELECTED_ELEMENTS", productElementsNew);
				}
			}
			data.put("CODEINGSTR", codings.toString());
		}
		// @SuppressWarnings("unused")
		IDataset result = CSViewCall.call(this, "CS.BatDealSVC.submitBatTask", data);// 返回为null
		if (result != null && result.size() > 0)
		{
			setAjax(result.getData(0));
		}
	}

	/**
	 * 身份证在线校验
	 * 
	 * @author yanwu
	 * @param clcle
	 * @throws Exception
	 */
	public void verifyIdCard(IRequestCycle clcle) throws Exception
	{
		IData data = getData();
		IData param = new DataMap();

		param.putAll(data);
		param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
		param.put("BUSI_TYPE", "2");// 2：存量用户补登记

		IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.verifyIdCard", param);
		setAjax(dataset.getData(0));
	}

	/**
	 * 营业执照、组织机构代码证、事业单位法人登记证书不能一个证件号码对应多个不同的单位名称。
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void verifyIdCardName(IRequestCycle clcle) throws Exception
	{
		IData data = getData();

		IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.verifyIdCardName", data);
		setAjax(dataset.getData(0));
	}

	public void verifyEnterpriseCard(IRequestCycle clcle) throws Exception
	{
		IData data = getData();
		IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.verifyEnterpriseCard", data);
		setAjax(dataset.getData(0));
	}

	public void verifyOrgCard(IRequestCycle clcle) throws Exception
	{
		IData data = getData();
		IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.verifyOrgCard", data);
		setAjax(dataset.getData(0));
	}

	/**
	 * REQ201707170020_新增物联卡开户人像采集功能
	 * <br/>
	 * 人像比对接口
	 * @author zhuoyingzhi
	 * @date 20170814
	 * @param clcle
	 * @throws Exception
	 */
    public void cmpPicInfo(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();
        
        param.putAll(data);
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
 
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.cmpPicInfo", param);
        setAjax(dataset.getData(0));
        
    }    
    
    /**
     * REQ201707170020_新增物联卡开户人像采集功能
     * <br/>
     * 人像比对权限判断
     * @author zhuoyingzhi
     * @date 20170814
     * @param clcle
     * @throws Exception
     */
    public void isCmpPic(IRequestCycle clcle) throws Exception
    {
    	IData data = getData();
    	IData param = new DataMap();
    	
    	param.putAll(data);
    	param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
    	
    	IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.isCmpPic", param);
    	setAjax(dataset.getData(0));
    }

	/**
	 * 个人用户开户权限校验
	 * 
	 * @param cycle
	 * @throws Exception
	 * @author zhaohj3
	 * @date 2018-1-4 16:50:50
	 */
	public void hasPriv(IRequestCycle cycle) throws Exception {
		IData para = this.getData();

		String privId = para.getString("PRIV_ID");
		boolean hasPriv = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(),
				privId);

		IData privData = new DataMap();
		privData.put("HAS_PRIV", hasPriv);
		privData.put("STAFF_ID", getVisit().getStaffId());

		setAjax(privData);
	}
	
	/**
	 * 营销活动 micy 20180226 判断是否配置费用
	 * */
	public void checkIsNeedPay(IRequestCycle cycle) throws Exception
	{
		IData params = getData();
		IDataset packList = CSViewCall.call(this, "SS.BatSaleActiveSVC.checkIsNeedPay", params);
		IData result = new DataMap();
		if (packList.isEmpty())
		{
			result.put("IS_NEED_PAY",0);
		} else
		{
			result.put("IS_NEED_PAY",1);
		}
		setAjax(result);
	}
	
    /**
     * REQ201904260020新增物联网批量开户界面权限控制需求
     * 免人像比对权限判断
     * @author mengqx
     * @date 20190515
     * @param clcle
     * @throws Exception
     */
    public void isBatCmpPic(IRequestCycle clcle) throws Exception
    {
    	IData data = getData();
    	IData param = new DataMap();
    	
    	param.putAll(data);
    	param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
    	
    	IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.isBatCmpPic", param);
    	setAjax(dataset.getData(0));
    }
}
