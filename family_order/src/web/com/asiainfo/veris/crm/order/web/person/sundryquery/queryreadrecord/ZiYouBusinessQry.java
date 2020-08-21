package com.asiainfo.veris.crm.order.web.person.sundryquery.queryreadrecord;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;
import org.apache.tapestry.IRequestCycle;


public  abstract class ZiYouBusinessQry extends PersonQueryPage
{
	public abstract void setInfos(IDataset infos);
	public abstract void setInfo(IData info);
	public abstract void setRowPage(int rowPage);
	public abstract void setInfocount(long count);
	public abstract void setInitinfo(IData initinfo);
	public abstract void setTypes(IDataset types);
	public abstract void setDynamicComponentParam(IData info);
	
	//设置一个全局变量，直接将查询service里面设置的参数放在全局变量里面，在点下一页的时候直接调用，就不从页面读取了
	static IData temp = new DataMap();
	
	public void initPage(IRequestCycle cycle) throws Exception {
		
		IData data = this.getData();
		IData initinfo = new DataMap();
		
		String accessNum = data.getString("ACCESS_NUM","");
		String tradeDepartCode = getVisit().getLoginEparchyCode();
		String busiItemCode = data.getString("BUSI_ITEM_CODE");
		
		//加载页面所需参数
		initinfo.put("eparchyCode", tradeDepartCode);
		initinfo.put("tradetypecode", busiItemCode);//根据传入的tradeTypeCode,动态页面组件加载不同的页面
		initinfo.put("ACCESS_NUM", accessNum);//应客服要求，在页面加载完以后将电话号码显示出来
		
		setInitinfo(initinfo);
		
	}
	
    public void queryBlackList(IRequestCycle cycle) throws Exception
    {
    	IData data = this.getData();
		String operCondition = operateConditions(data);//根据各页面特有的查询条件拼串传入IBoss
    	
    	String provinceCode = getVisit().getProvinceCode();
    	String provinceID = getProvinceID(provinceCode);//获取省ID
		//operCondition = addOtherParamToCond(data,operCondition,provinceID);//各业务对所拼的参数有所补充
		
		//向服务层传入的数据
		data =commonQryInfo(data);//获取传入后台的公共参数,在插日志表的时候使用
		
		String accessNum = data.getString("QRYINFO0","");
		accessNum = otherDealForAccessNum(data,accessNum);//个别业务如果QRYINFO0未传，则使用默认值
		data.put("PROVINCEID", provinceID);
		data.put("OPERATECONDITIONS", operCondition);
		data.put("CALLERNO", accessNum);
		data.put("SERIAL_NUMBER", accessNum);
		log.debug("拼接的数据"+data);
		IDataset results = CSViewCall.call(this,"SS.ZiYouBusinessQrySVC.qryBlackList", data);//查询调用的服务

		IData result = results.getData(0);
		IDataset infos = result.getDataset("OUTDATA");//页面table显示的数据
		IData nextRecordParam = result.getData("NEXT_RECORD_PARAM");//点击下一页查询所需的数据
		
		temp = nextRecordParam;//将下一页所需参数放在静态变量里面
		setDynamicNavBarParam(nextRecordParam);//将每页记录数和总数据设置进分页组件里面
		
		//设置动态页面组件刷新的时候需要的参数，当点击一下页的时候这些参数需提交给组件，用来刷新页面
		IData dynamicParam = new DataMap();
		String tradeDepartCode = getVisit().getLoginEparchyCode();
		String busiItemCode = data.getString("TRADE_TYPE_CODE");
		dynamicParam.put("TRADE_TYPE_CODE", busiItemCode);
		dynamicParam.put("ROUTE_EPARCHY_CODE", tradeDepartCode);

		this.setDynamicComponentParam(dynamicParam);
		this.setInfos(infos);
    }
    /**
     * 点击下一页，查询剩余记录
     * @param cycle
     * @throws Exception
     */
    public void queryOtherRecord(IRequestCycle cycle) throws Exception {
    	
    	int currentPage = getPagination().getCurrent();
    	IData param  = new DataMap();
    	param.put("NEXT_RECORD_PARAM",temp);
    	param = commonQryInfo(param);
    	param.put("CURRENTPAGE", String.valueOf(currentPage));
    	IDataset results = CSViewCall.call(this,"SS.ZiYouBusinessQrySVC.qryOtherRecord", param);

    	IData result = results.getData(0);
    	IDataset infos = result.getDataset("OUTDATA");
    	this.setInfos(infos);
    	IData nextRecordParam = result.getData("NEXT_RECORD_PARAM");
		temp = nextRecordParam;
		
		setDynamicNavBarParam(nextRecordParam);//将每页记录数和总数据设置进分页组件里面
    }
    /**
	 * 关闭页面调用归档接口和更新表数据
	 */
	public void closePage(IRequestCycle cycle) throws Exception {
		if(null!=temp &&temp.size()>0){
			String indictSeq = temp.getData("TEMPPARAM1").getString("INDICTSEQ");
			String pageOnHole = temp.getData("PIGEONHOLE").getString("PIGEONHOLE");
			IData param  = new DataMap();
			param = commonQryInfo(param);
			param.put("INDICTSEQ", indictSeq);
			param.put("PIGEONHOLE", pageOnHole);
			IDataset result = CSViewCall.call(this,"SS.ZiYouBusinessQrySVC.subClosePage", param);
		}
	}
	
	/**
	 * 页面点击提交时触发的方法
	 */
	public void submitProcess(IRequestCycle cycle) throws Exception {
		
		IData data = this.getData();
		
    	String provinceCode = getVisit().getProvinceCode();
    	String provinceID = getProvinceID(provinceCode);//获取省ID
    	
		String operCondition = operateConditions(data);//获取各页面的查询条件传入IBoss
		
		operCondition = addOtherParamToCond(data,operCondition,provinceID);//各业务对所拼的参数有所补充
		
		//像服务层传入的数据
		data =commonQryInfo(data);//获取传入后台的公共参数
		String accessNum = data.getString("QRYINFO0");
		data.put("PROVINCEID", provinceID);
		data.put("HOMEPROV", provinceID);
		data.put("OPERATECONDITIONS", operCondition);
		data.put("CALLERNO", accessNum);
		log.debug("拼接的数据"+data);
		IDataset result = CSViewCall.call(this,"SS.BaseCommRecordSVC.subSubmitProcess", data,this.getPagination());
	}
	//获取查询及提交后台时的公共入参
	public IData commonQryInfo(IData data) throws Exception {
		
		String eparchyCode = getVisit().getLoginEparchyCode();// eparchyCode没有值，框架不完整，后续替换
		
		String opID  = getVisit().getStaffId();
		String orgID = getVisit().getDepartId();
		String provinceCode = getVisit().getProvinceCode();
		String tradeCityCode = getVisit().getCityCode();
		
		data.put("EPARCHYCODE", eparchyCode);
		data.put("OP_ID", opID);
		data.put("ORG_ID", orgID);
		data.put("PROVINCE_CODE", provinceCode);
		data.put("TRADE_CITY_CODE", tradeCityCode);
		data.put("TRADE_EPARCHY_CODE", eparchyCode);
		return data;
	}
	
	
	/**
	 * 将IBOSS查询出来的最大记录数和每页数据量设置进分页组件里面，目前分页组件不具备使用ognl动态设置pagesize的属性
	 */
	public void setDynamicNavBarParam(IData tempParam){
		
		int rowPage = Integer.parseInt(tempParam.getData("TEMPPARAM1").getString("RSLTPAGECURRCNT"));
		Long pagecount = Long.parseLong(tempParam.getData("TEMPPARAM1").getString("MAXRECORDNUM"));
		
		//分页组件目前不支持动态设置每页展示数量，目前按集团规范每页展示150条，写死在代码里面
		this.setRowPage(rowPage);
		this.setInfocount(pagecount);
	}
	
	/**
	 * 部分页面会对查询条件的拼串做特殊处理
	 * @throws Exception
	 */
	public String addOtherParamToCond(IData data, String operationCondition, String provinceId) throws Exception {
		
		String pageSize = data.getString("navbar1_pagesize");
		String page = data.getString("navbar1_current");
		String tradeTypeCode = data.getString("TRADE_TYPE_CODE","");
		//如果是139邮箱查询，将分页参数加上
		if("71".equals(tradeTypeCode)){
			operationCondition=operationCondition+"|1|20";
		}
		
		return operationCondition;
		
	}
	
	/**
     * 获取provinceID
     */
  	public String getProvinceID (String provinceCode){
  		
  		String provinceID = "";
  		if("XINJ".equals(provinceCode)){
  			provinceID = "991";
  		}
  		if("HAIN".equals(provinceCode)){
  			provinceID = "898";
  		}
  		if("HNAN".equals(provinceCode)){
  			provinceID = "731";
  		}
  		if("QHAI".equals(provinceCode)){
  			provinceID = "971";
  		}
  		if("SHXI".equals(provinceCode)){
  			provinceID = "290";
  		}
  		if("TJIN".equals(provinceCode)){
  			provinceID = "220";
  		}
  		if("YUNN".equals(provinceCode)){
  			provinceID = "871";
  		}
  		return provinceID;
  	}
  	
  	/**
  	 * 获取页面查询条件
  	 */
  	public String operateConditions(IData data){
  		
  		//在页面拼的个性化操作参数
  		StringBuilder operateConditions = new StringBuilder();
  		String pageOpeCondition = data.getString("OPERATECONDITIONS","");
  		if(StringUtils.isNotEmpty(pageOpeCondition)){
  			return pageOpeCondition;
  		}
  		
  		//各页面统一拼的操作参数
  		for(int i = 0 ; i<data.size(); i++){
  			if(data.containsKey(("QRYINFO")+i)){
  				operateConditions.append(data.getString(("QRYINFO"+i),"")).append("|");
  			}
  		}
  		String operCondition = operateConditions.substring(0,operateConditions.length()-1);
  		
  		return operCondition;
  	}
  	
  	public String otherDealForAccessNum(IData data, String accessNum){
  		
  		
  		String busiItemCode = data.getString("BUSI_ITEM_CODE","");
  		if(StringUtils.isEmpty(accessNum)){
  			//手机游戏->业务基本信息查询,若业务名称未传，默认11111222223
  			if("14".equals(busiItemCode)){
  				accessNum = "11111222223";
  			}if("71".equals(busiItemCode)){

			}
  		}
  		
  		return accessNum;
  	}

	public void qryAbilityList(IRequestCycle cycle) throws Exception
	{
		IData data = this.getData();
		String operCondition = operateConditions(data);//根据各页面特有的查询条件拼串传入IBoss

		String provinceCode = getVisit().getProvinceCode();
		String provinceID = getProvinceID(provinceCode);//获取省ID


		//向服务层传入的数据
		data =commonQryInfo(data);//获取传入后台的公共参数,在插日志表的时候使用

		data.put("PROVINCEID", provinceID);
		log.debug("拼接的数据"+data);
		IDataset results = CSViewCall.call(this,"SS.ZiYouBusinessQrySVC.qryAbilityList", data);//查询调用的服务

		IData result = results.getData(0);
		IDataset infos = result.getDataset("ReturnElements");//页面table显示的数据


		//设置动态页面组件刷新的时候需要的参数，当点击一下页的时候这些参数需提交给组件，用来刷新页面
		IData dynamicParam = new DataMap();
		String tradeDepartCode = getVisit().getLoginEparchyCode();
		String busiItemCode = data.getString("TRADE_TYPE_CODE");
		dynamicParam.put("TRADE_TYPE_CODE", busiItemCode);
		dynamicParam.put("ROUTE_EPARCHY_CODE", tradeDepartCode);

		this.setDynamicComponentParam(dynamicParam);
		this.setInfos(infos);
	}
	public void qrySingleAbilityList(IRequestCycle cycle) throws Exception
	{
		IData data = this.getData();
		String operCondition = operateConditions(data);//根据各页面特有的查询条件拼串传入IBoss

		String provinceCode = getVisit().getProvinceCode();
		String provinceID = getProvinceID(provinceCode);//获取省ID


		//向服务层传入的数据
		data =commonQryInfo(data);//获取传入后台的公共参数,在插日志表的时候使用

		data.put("PROVINCEID", provinceID);
		log.debug("拼接的数据"+data);
		IDataset results = CSViewCall.call(this,"SS.ZiYouBusinessQrySVC.singleAbilityQry", data);//查询调用的服务

		IData result = results.getData(0);
		IDataset infos = result.getDataset("ReturnElements");//页面table显示的数据


		//设置动态页面组件刷新的时候需要的参数，当点击一下页的时候这些参数需提交给组件，用来刷新页面
		IData dynamicParam = new DataMap();
		String tradeDepartCode = getVisit().getLoginEparchyCode();
		String busiItemCode = data.getString("TRADE_TYPE_CODE");
		dynamicParam.put("TRADE_TYPE_CODE", busiItemCode);
		dynamicParam.put("ROUTE_EPARCHY_CODE", tradeDepartCode);

		this.setDynamicComponentParam(dynamicParam);
		this.setInfos(infos);
	}
}
