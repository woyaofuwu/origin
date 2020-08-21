package com.asiainfo.veris.crm.iorder.web.person.valuecard;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class SellValueCardNew extends PersonBasePage {
	static Logger logger = Logger.getLogger(SellValueCardNew.class);

	public void addClick(IRequestCycle cycle) throws Exception {
		IData data = getData();
		if ("".equals(data.getString("SERIAL_NUMBER", ""))) {
			data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
		}
		data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
		data.put("import_customer_number", "");
		IDataset results = CSViewCall.call(this, "SS.ValueCardMgrSVC.getValueCardInfo", data);
		IDataset set1 = results.getData(0).getDataset("TABLE1");
		IDataset set2 = results.getData(0).getDataset("TABLE2");
		String temp = data.getString("table2");
		IDataset tempSet = new DatasetList(temp);
		if (tempSet.size() > 0) {
			set2.addAll(tempSet);
		}
		setAjax(set2);
		this.setBasicInfos(set1);
		this.setSaleInfos(set2);

	}

	public void loadChildInfo(IRequestCycle cycle) throws Exception {
		IData data = getData();
		if ("".equals(data.getString("SERIAL_NUMBER", ""))) {
			data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
		}
		this.setCsValueCardDiscount(StaffPrivUtil.isPriv(this.getVisit().getStaffId(), "csValueCardDiscount",
				StaffPrivUtil.PRIV_TYPE_FUNCTION));
		if (data.getString("TRADE_TYPE_CODE").equals("418")) {
			data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
			IDataset results = CSViewCall.call(this, "SS.ValueCardMgrSVC.getgetWorkOrders", data);

			this.setAuditInfos(results);
		}
	}

	public void onInitTrade(IRequestCycle cycle) throws Exception {
		this.setCsValueCardDiscount(StaffPrivUtil.isPriv(this.getVisit().getStaffId(), "csValueCardDiscount",
				StaffPrivUtil.PRIV_TYPE_FUNCTION));
		IData data = getData();
		data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
		IDataset results = CSViewCall.call(this, "SS.ValueCardMgrSVC.getgetWorkOrders", data);
		this.setAuditInfos(results);

		/**
		 * REQ201802260006_有价卡退卡增加付款方式选项的优化
		 * @author zhuoyigzhi
		 * @date 2080416
		 */
		data.put("PAY_MONEY_CODE", "0");//默认付款方式为 0 现金
		this.setCondition(data);
	}

	/**
	 * 业务提交
	 * @param cycle
	 * @throws Exception
	 */
	public void onTradeSubmit(IRequestCycle cycle) throws Exception {
		IData data = getData();
		if ("".equals(data.getString("SERIAL_NUMBER", ""))) {
			data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
		}
		data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
		if (data.getString("TRADE_TYPE_CODE").equals("416")) {
        	/**
        	 * REQ201704270011_关于购买大额有价卡的业务流程优化
        	 * <br/>
        	 * 如果是没有加载客户信息，销售有价卡信息，
        	 * 把扫描得到的证件信息赋给客户信息
        	 * @author zhuoyingzhi
        	 * @date 20170723
        	 */
        	String serialNumber=data.getString("SERIAL_NUMBER");
        	//摄像标识
        	String picId=data.getString("custInfo_PIC_ID", "");
        	if(("".equals(serialNumber)||serialNumber==null)&& !"".equals(picId)){
        		 data.put("CUST_NAME", data.getString("custInfo_CUST_NAME"));
        		 data.put("PSPT_TYPE_CODE", data.getString("custInfo_PSPT_TYPE_CODE"));
        		 data.put("PSPT_ID", data.getString("custInfo_PSPT_ID"));
        		 data.put("PSPT_ADDR", data.getString("custInfo_PSPT_ADDR"));
        	}
        	/*******************end*******************************/
			IDataset dataset = CSViewCall.call(this, "SS.SellValueCardRegSVC.tradeReg", data);
			setAjax(dataset);
		} else if (data.getString("TRADE_TYPE_CODE").equals("418")) {
			IDataset dataset = CSViewCall.call(this, "SS.GiveValueCardRegSVC.tradeReg", data);
			setAjax(dataset);
		} else if (data.getString("TRADE_TYPE_CODE").equals("419")) {
			IDataset dataset = CSViewCall.call(this, "SS.BackValueCardRegSVC.tradeReg", data);
			setAjax(dataset);
		} else if (data.getString("TRADE_TYPE_CODE").equals("420")) {
			IDataset dataset = CSViewCall.call(this, "SS.ChangeValueCardRegSVC.tradeReg", data);
			setAjax(dataset);
		} else {
			IDataset dataset = CSViewCall.call(this, "SS.ValueCardRegSVC.tradeReg", data);
			setAjax(dataset);
		}
	}

	/**
	 * 批量加入 <br/>
	 * @param cycle
	 * @throws Exception
	 */
	public void importGiveValueData(IRequestCycle cycle) throws Exception {
		IData data = getData();
		if ("".equals(data.getString("SERIAL_NUMBER", ""))) {
			data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
		}
		data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
		// 获取上传表单里面的信息
		IDataset dataset = CSViewCall.call(this, "SS.ValueCardMgrSVC.getImportGiveValueData", data);
		IDataset results = new DatasetList();
		IDataset set1 = new DatasetList();
		IDataset set2 = new DatasetList();
		if (IDataUtil.isNotEmpty(dataset)) {
			boolean set1LastOneFlag = false;
			for (int i = 0; i < dataset.size(); i++) {
				IData iData = dataset.getData(i);
				data.put("START_CARD_NO", "");
				data.put("END_CARD_NO", "");
				data.put("START_CARD_NO", iData.getString("START_CARD_NO"));
				data.put("END_CARD_NO", iData.getString("END_CARD_NO"));
				data.put("customerNo", iData.getString("customerNo"));// 模版中的手机号码
				data.put("SERIAL_NUMBER", iData.getString("customerNo"));// 模版中的手机号码
				data.put("X_GETMODE", "0");
				try {
					// 查询验证用户为必输选项用户信息
					IDataset ucaInfos = CSViewCall.call(this, "SS.QueryInfoSVC.getUserInfo", data);
					results = CSViewCall.call(this, "SS.ValueCardMgrSVC.checkValeCardInfo", data);
					set1LastOneFlag = true;
				} catch (Exception e) {
					set1LastOneFlag = false;
					IDataset set = new DatasetList();
					IData d = new DataMap();
					d.put("singlePrice", "0");
					d.put("RES_KIND_CODE", "");
					d.put("advise_price", "0");
					d.put("activeFlag", "1");
					d.put("rowCount", "0");
					d.put("startCardNo", iData.getString("START_CARD_NO"));
					d.put("cardType", "");
					d.put("endCardNo", iData.getString("END_CARD_NO"));
					d.put("valueCode", "");
					d.put("devicePrice", "0");
					d.put("totalPrice", "0");
					d.put("status", "验证失败");
					d.put("statusCode", "1");
					d.put("impCustomerNo", "");
					d.put("impCustomerName", "");
					d.put("impGroupName", "");
					d.put("impGiveName", "");
					// 导入标志
					d.put("importFlag", "");
					set.add(d);
					set2.addAll(set);
					String errStr = e.getMessage();
					if (errStr.indexOf("获取该用户资料失败") != -1) {
						data.put("checkCustomerNumber", 3);// 当为非移动号码时候提示进行提示拦截信息，不显示导入成功。
					}
				}

				/**
				 * set1只记录最后正确的一条
				 */
				if (set1LastOneFlag) {
					set1.clear();
					set1.addAll(results.getData(0).getDataset("TABLE1"));
					IDataset iList = results.getData(0).getDataset("TABLE2");
					if (IDataUtil.isNotEmpty(iList)) {
						for (int j = 0; j < iList.size(); j++) {
							iList.getData(j).put("status", "验证通过");
							iList.getData(j).put("statusCode", "0");
							// 客户号码
							iList.getData(j).put("impCustomerNo", iData.getString("customerNo"));
							iList.getData(j).put("impCustomerName", iData.getString("customerName"));
							iList.getData(j).put("impGroupName", iData.getString("groupName"));
							iList.getData(j).put("impGiveName", iData.getString("giveName"));
							// 导入标志
							iList.getData(j).put("importFlag", iData.getString("importFlag"));
						}
					}
					set2.addAll(iList);
				}
			}
		}
		this.setSaleInfos(set2);
		String temp = data.getString("table2");
		IDataset tempSet = new DatasetList(temp);
		if (tempSet.size() > 0 && tempSet != null && "null".equals(tempSet)) {
			set2.addAll(tempSet);
		}
		// setAjax(set2);
		setAjax(data);
		// logger.info("返回页面值set1:"+set1+"返回页面值set2"+set2);
		this.setBasicInfos(set1);

	}

	/**
	 * 有价卡赠送界面\规则修改 需求 如不输入移动用户手机号码或者输入非移动用户号码，则系统拦截。20170711 <br/>
	 * @param cycle
	 * @throws Exception
	 */
	public void checkCustomerNumber(IRequestCycle cycle) throws Exception {
		String serialNum;
		IData data = getData();
		serialNum = data.getString("SERIAL_NUMBER", "");
		IData returnData = new DataMap();
		IData params = new DataMap();
		params.put("SERIAL_NUMBER", serialNum);
		params.put("X_GETMODE", "0");
		try {
			// 查询用户信息CS.GetInfosSVC.getUCAInfos
			IDataset ucaInfos = CSViewCall.call(this, "SS.QueryInfoSVC.getUserInfo", params);
			if (ucaInfos != null && ucaInfos.size() > 0) {
				returnData.put("SUCC_FLAG", 1);
			} else {
				returnData.put("SUCC_FLAG", 0);
			}
		} catch (Exception e) {
			returnData.put("SUCC_FLAG", 0);
		}
		setAjax(returnData);
	}
	
	  /**
     * REQ201704270011_关于购买大额有价卡的业务流程优化
     * <br/>
     * 判断总价格是否超过约定的价格
     * @param clcle
     * @throws Exception
     * @author zhuoyingzhi
     * @date 20180607
     */
    public void isTotalPrice(IRequestCycle clcle) throws Exception
    {
    	IData data = getData();
    	
    	IData  result=new DataMap();
    	result.put("msg", "正常");
    	//总价格
    	double  totalPrice=data.getDouble("TOTAL_PRICE");
    	
    	IData param = new DataMap();
    		  param.put("PARAM_ATTR", "2019");
    		  param.put("PARA_CODE1", "201707171648");
    	//SS.ValueCardMgrSVC.getCommparaInfo
    	IDataset dataset = CSViewCall.call(this, "SS.ValueCardMgrSVC.getCommparaInfo", param);
    	if(IDataUtil.isEmpty(dataset)){
    		result.put("tag", "2");
    		String msg="纳入人像采集,有价卡约定额度没有配置";
    		result.put("msg", msg);
    	}else{
        	//约定额度
        	double  paramCode=dataset.getData(0).getDouble("PARAM_CODE");
        	if(totalPrice >= (paramCode/100)){
           		//提示拦截
        		result.put("tag", "1");
        		String msg="一次性购买超过"+(paramCode/100)+"元（包含"+(paramCode/100)+"元）有价卡,必须进行人像采集";
        		result.put("msg", msg);
        	}else{
        		result.put("tag", "0");
        	}    		
    	}   	
    	setAjax(result);
    }   

	public abstract void setAuditInfos(IDataset dataset);

	public abstract void setBasicInfos(IDataset dataset);

	public abstract void setCond(IData cond);

	public abstract void setCsValueCardDiscount(boolean can);

	public abstract void setSaleInfos(IDataset dataset);

	public abstract void setCondition(IData condition);
}
