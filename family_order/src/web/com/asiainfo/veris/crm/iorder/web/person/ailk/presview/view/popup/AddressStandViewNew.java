package com.asiainfo.veris.crm.iorder.web.person.ailk.presview.view.popup;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.search.SearchResponse;
import com.ailk.search.client.SearchClient;
import com.ailk.service.client.ServiceFactory;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 
 * @author admin
 *
 */
public abstract class AddressStandViewNew extends PersonBasePage{
	
	private static Logger logger = Logger.getLogger(AddressStandViewNew.class);
	
	public abstract void setCondition(IData condition); 
	
	public abstract void setConditions(IData condition);

	public abstract void setInfos(IDataset infos);
	
	public abstract void setInfoss(IDataset infos);
	
	public abstract void setAddr1Source(IDataset addr1);
	
	public abstract void setAddr2Source(IDataset addr2);
	
	public abstract void setEdit(IData condition);
	
	public abstract void setSelectInfos(IDataset infos);
	
	public abstract void setSelectSecondInfos(IDataset infos);
	
	public abstract void setCount(long count);

	public void init(IRequestCycle cycle) throws Exception{
		String area_code = this.getVisit().getCityCode();
		IData data =  getData();
		
		data.put("AREA_CODE1", area_code);
//		data.put("AREA_CODE1", "HNHK");
		
		setCondition(data);
	}
	
	
	/**
	 * 宽带需求收集初始化五级地址
	 * @param cycle
	 * @throws Exception
	 */
	public void initAddrSourceMethod(IRequestCycle cycle) throws Exception{
		
		IData paramAddrId1 = new DataMap();
		paramAddrId1.put("ADDR_LEVEL", "1");
		paramAddrId1.put("PARENT_ADDR_ID", "0");
		paramAddrId1.put("EPARCHY_CODE", this.getVisit().getStaffEparchyCode());
		IDataOutput outputAddrId1 = this.call1("PB.AddressManageSvc.queryAddressNameLevelOne", paramAddrId1);
		if(outputAddrId1!=null&&outputAddrId1.getData().size()>0){
			this.setAddr1Source(outputAddrId1.getData());
		}
		
	}
	
	public void SetSecondAddrSourceMethod(IRequestCycle cycle) throws Exception{
		IData param = this.getData();
		String addrId1 = param.getString("ADDR_ID1", "");
		if(StringUtils.isBlank(addrId1)){
			
		}else{
			IData paramAddrId2 = new DataMap();
			paramAddrId2.put("PARENT_ADDR_ID", addrId1);
			paramAddrId2.put("ADDR_LEVEL", "2");
			paramAddrId2.put("EPARCHY_CODE", this.getVisit().getStaffEparchyCode());
			
			IDataOutput outputAddrId2 = this.call1("PB.AddressManageSvc.queryAddressNameLevelOne", paramAddrId2);
			
			if(outputAddrId2!=null&&outputAddrId2.getData().size()>0){
				this.setAddr2Source(outputAddrId2.getData());
			}
		}
	}
	
	
	
	/**
	 * 五级地址默认调用方法    初始化一级地址
	 * @param cycle
	 * @throws Exception
	 */
	public void initMethod(IRequestCycle cycle) throws Exception{
		IData param = new DataMap();
		param.put("ADDR_LEVEL", "1");
		param.put("PARENT_ADDR_ID", "0");
		String addr_id = changeAreaCodeToAddrId(this.getVisit().getCityCode());
		if(addr_id==null){
			//非18个分区，不尽兴初始化
		}else{
			param.put("ADDR_ID", addr_id);
			IDataOutput output = this.call1("PB.AddressManageSvc.queryInitFiveLevel", param);
			if(output!=null&&output.getData().size()>0){
				IData data = new DataMap();
				data.put("ADDR_ID1", output.getData().getData(0).getString("ADDR_ID"));
				this.setCondition(data);
			}
		}
//		IData param = this.getData();
//		IData condition = new DataMap();
//		
//		
//		String gis = param.getString("ADDR_NAME1","");
//		String gis1 = param.getString("ADDR_NAME2","");
//		String gis2 = param.getString("ADDR_NAME3","");
//		///不存在1 2 3级连续为空的情况，为空则为需求收集界面过来的   。   否则为在标准地址选择地址后设备端口可用数为0，进入需求收集界面后进入的
//		if(!"".equals(gis)||!"".equals(gis1)||!"".equals(gis2)){
//			IDataOutput resp = ServiceFactory.call("PB.AddressManageSvc.queryGisIds", createDataInput(param));
//			if(resp!=null){
//				IDataset datas = resp.getData();
//				IData data = datas.getData(0);
//				param.put("ADDR_ID1", data.getString("ADDR_ID1",""));
//				param.put("ADDR_ID2", data.getString("ADDR_ID2",""));
//				param.put("ADDR_ID3", data.getString("ADDR_ID3",""));
//				param.put("ADDR_ID4", data.getString("ADDR_ID4",""));
//				param.put("ADDR_ID5", data.getString("ADDR_ID5",""));
//			}
//		}
//		
//		param.put("gis", param.getString("ADDR_NAME1",""));
//		param.put("gis1", param.getString("ADDR_NAME2",""));
//		param.put("gis2", param.getString("ADDR_NAME3",""));
//		param.put("gis3", param.getString("ADDR_NAME4",""));
//		param.put("gis4", param.getString("ADDR_NAME5",""));
//		
//		System.out.println(param);
//		/*
//		 * 查找ID
//		 * 注意：先从一级地址开始，找到一级地址ID，并进行二级地址查询，要使用上级ID及名称两个条件进行查询。
//		 * 设置名称，ID到页面
//		 */
//		
//		setCondition(param);
	}
	
	
	/**
	 * 
	 * @param cityCode
	 * @return
	 * @throws Exception
	 */
	 private String changeAreaCodeToAddrId(String cityCode) throws Exception {
		 
		 IData data = new DataMap();
		 data.put("AREA_CODE", cityCode);
		 
		 IDataOutput output = this.call1("PB.AddressManageSvc.queryAddrIdByAreaCode", data);
		 
		 return output==null?null:output.getData().size()>0?output.getData().getData(0).getString("PARA_VALUE3"):null;
	}
	 public void ajaxSetPospecNumber(IRequestCycle cycle) throws Exception
	{
	
	}
	
//	/**
//	 * 根据简拼查询地址
//	 * @param cycle
//	 * @throws Exception
//	 */
//	public void queryReginalsAllBySp(IRequestCycle cycle) throws Exception{
//		IData param = this.createDataInput("cond",true).getData();
//		String area_code = param.getString("AREA_CODE1");
//		String region_sp = param.getString("REGION_SP");
//
//		char[] chars = region_sp.toCharArray();
//		boolean cnFlag = true;
//		for(int i=0;i<chars.length;i++){
//			//判断是否全中文，如果存在一个非中文(仅限字母，需将汉字部分转换成字母简拼)
//			char c = chars[i];
//			if(i==chars.length-1&&c==70){
//				//去除最后一个为F字母
//				break;
//			}
//			if((c>=65&c<=90)){
//				region_sp = StringUtil.getFirstPY(region_sp);
//				cnFlag = false;
//			}
//		}
//		
//		if(cnFlag){
//			param.put("REGION_CN", region_sp);
//		}else{
//			param.put("REGION_JP", region_sp);
//		}
//		
//		if(!"".equals(area_code)){
//			if("HK".equals(area_code.substring(2))||"SY".equals(area_code.substring(2))||"WZ".equals(area_code.substring(2))){
//				param.put("EPARCHY_CODE", area_code.substring(2) +"S");
//			}else{
//				param.put("EPARCHY_CODE", area_code.substring(2));
//			}
//		}
//		    
//		IDataOutput output = this.call1("PB.AddressManageSvc.queryDeviceByReginalName", param);
//		setInfos(output.getData());	
//		setCount(output.getDataCount());
//		param.put("DATACOUNT", output.getDataCount());
//		this.setCondition(param);
//	}
	
	
	/**
	 * 查询地址信息      添加中文，英文检索功能
	 * @param cycle    
	 * @throws Exception
	 */  
	public void queryReginalsAll(IRequestCycle cycle) throws Exception{
		IData param = this.getData();
		
		String area_code = param.getString("AREA_CODE","");
		String region_sp = param.getString("REGION_NAME","");
		String area_code2 = param.getString("AREA_CODE2","");
		String region_sp2 = param.getString("addressSearch","");
		String tempCount = param.getString("tempCount","");
		if("".equals(tempCount)){
			tempCount = "-1";
		}
		if(!"".equals(area_code)){
			param.put("AREA_CODE", area_code);
		}else{
			param.put("AREA_CODE", area_code2);
		}
		
		if("".equals(region_sp)){
			region_sp = region_sp2;
		}
		
		param.put("REGION_SP", region_sp);
		
		Pagination pagination = this.getPagination();
//		IDataOutput result = CSViewCall.callPage(this, "PB.AddressManageSvc.queryAddress", param, pagination);
        IDataOutput result = call2("PB.AddressManageSvc.queryAddress", param ,pagination);//modify by duhj
		
		if(pagination.isNeedCount()){
			pagination.setOnlyCount(true);
//			IDataOutput pageCountResult = CSViewCall.callPage(this, "PB.AddressManageSvc.queryAddress", param, pagination);
			IDataOutput pageCountResult = call2("PB.AddressManageSvc.queryAddress", param, pagination);
			tempCount = String.valueOf(pageCountResult.getDataCount());
			pagination.setOnlyCount(false);
		}
		
//		IDataOutput resp = ServiceFactory.call("PB.AddressManageSvc.queryAddress", createDataInput(param),this.getPagination());
		if(result.getData().size() == 0){
    		tempCount = "0";
    	}
		
		setInfos(result.getData());
		setCount(Long.valueOf(tempCount));
		param.put("DATACOUNT", Long.valueOf(tempCount));
		param.put("tempCount", tempCount);
		param.put("REGION_NAME", region_sp);
		setConditions(param);
		setCondition(param);
	}
	
	/**
	 * 查询地址设备信息 ?
	 * @param cycle
	 * @throws Exception
	 */
	public void loadReginalDevice(IRequestCycle cycle)throws Exception{
		IData param = this.getData();
		IData to = new DataMap();
		to.put("REGION_NAME", param.getString("REGION_NAME"));
		
		IDataOutput output = this.call1("PB.AddressManageSvc.queryDeviceByReginalName", param);
		
		
		if(output!=null&&output.getData().size()>0){
			param.put("ROWCOUNT", output.getDataCount());
			this.setInfoss(output.getData());
		}
	}
	
	 /**
	 * 查询地址设备信息(地址查询嵌入页面,点击地址查询地址对应的端口信息)
	 * @param cycle
	 * @throws Exception
	 */
	public void queryDeviceByReginal(IRequestCycle cycle)throws Exception{
		IData param = this.getData();
		IData to = new DataMap();
		to.put("REGION_NAME", param.getString("REGION_NAME"));
		to.put("DEVICE_ID", param.getString("DEVICE_ID"));
		
//		IDataOutput output = this.call1("PB.AddressManageSvc.queryDeviceByReginalName", param);
        IDataOutput output = call2("PB.AddressManageSvc.queryDeviceByReginalName", param ,null);//modify by duhj
		
		if(output!=null&&output.getData().size()>0){
			param.put("ROWCOUNT", output.getDataCount());
			this.setCondition(param);
			this.setInfoss(output.getData());
		}
	}
	
	public void queryReginalByRegional(IRequestCycle cycle) throws Exception{
		IData param = this.getData();
		IData to = new DataMap();
		IDataset dataset = new DatasetList();
		to.put("REGION_NAME", param.getString("REGION_NAME"));
		IDataOutput output = this.call1("PB.AddressManageSvc.queryDeviceByReginalName", param);
		if(output!=null&&output.getData().size()>0){
			setCount(1);
			dataset.add(output.getData().getData(0));
			this.setInfos(dataset);
		}
	}
	
	
	/**
	 * 搜搜引擎查询
	 * @param cycle
	 * @throws Exception
	 */
	public void loadAddressSvc(IRequestCycle cycle) throws Exception
    {
	 
		IData param = this.getData();
		String searchText = param.getString("REGION_NAME");
	    if(StringUtils.isBlank(searchText)){
	    	searchText = param.getString("q", "");
	    }
	    searchText.toLowerCase();
	     
        String area_code = param.getString("AREA_CODE","");
        
        Map<String,String> map = new HashMap<String,String>();
        
        if(!"".equals(area_code)){
        	map.put("AREA_CODE", area_code);
        }else{
        	map = null;
        }
        
        if (StringUtils.isNotBlank(searchText) && searchText.length() >= 2)
        {
    		SearchResponse resp = SearchClient.search("DEVICE_REGIONAL_SP", searchText,map,0,10);
            IDataset datas = resp.getDatas();
            this.setAjax(datas);
        }
    }
	
	
	/**
	 * 分级地址搜索框
	 * @param cycle
	 * @throws Exception
	 */
	public void loadGisSearch(IRequestCycle cycle) throws Exception{
		 IData param = this.getData();
		 String level = param.getString("level","");
		 String searchText =param.getString("ADDR_NAME");
		 String parent_id = param.getString("parent_id","");
		 Map<String,String> map = new HashMap<String,String>();
		 if(!"".equals(level)){
			 map.put("ADDR_LEVEL",level);
		 }
		 if(!"".equals(parent_id)){
			 map.put("PARENT_ADDR_ID", parent_id);
		 }
		 if("".equals(level)&&"".equals(parent_id)){
			 map = null;
		 }
		 
		 
		 if (StringUtils.isNotBlank(searchText) && searchText.length() >= 2)
	        {
	    		SearchResponse resp = SearchClient.search("LEVEL_ADDRESS_SOURCE", searchText,map,0,10);
	            IDataset datas = resp.getDatas();
	            this.setAjax(datas);
	        }
		 
		 
	}
	
	
	public void selectSecondInfos(IRequestCycle cycle) throws Exception{
		
		 IData param = this.getData();
		 String parent_addr_id = param.getString("PARENT_ADDR_ID","0");
		 
		 IData data = new DataMap();
		 data.put("ADDR_LEVEL", "2");
		 data.put("PARENT_ADDR_ID", parent_addr_id);
		 
		 IDataOutput output = this.call1("PB.AddressManageSvc.queryAddressNameLevelOne", param);
			
		if(output!=null&&output.getData().size()>0){
			this.setSelectSecondInfos(output.getData());
		}
		
	}
	
	public void queryLevelUp(IRequestCycle cycle) throws Exception{
		
		IData data  = new DataMap();
		
		IData param = this.getData();
		data.put("ADDR_ID", param.getString("ADDR_ID"));
		
		IDataOutput output = this.call1("PB.AddressManageSvc.queryLevelUp", param);
		
		if(output!=null&&output.getData().size()>0){
			
			logger.info("queryLevelUp"+output.getData());
			this.setAjax(output.getData());
		}
	}
	
	/**
	 * 预受理地址
	 * @param cycle
	 * @throws Exception
	 */
	public void addAddrPredeal(IRequestCycle cycle) throws Exception {
		IData param = this.getData();
		
		IData data=new DataMap();
		data.put("DETAIL_ADDRESS", param.getString("REGION_NAME"));
		data.put("AREA_CODE", param.getString("AREA_CODE"));
		data.put("MOBILE_NO", param.getString("SERIAL_NUMBER"));
		data.put("DEVICE_ID", param.getString("DEVICE_ID"));
		data.put("DEVICE_NAME", param.getString("DEVICE_NAME"));
		
		data.put("PRE_STAFF_NAME", this.getVisit().getStaffName());
		data.put("PRE_STAFF_ID", this.getVisit().getStaffId());
		data.put("PRE_DEPT_NAME",this.getVisit().getDepartName());
		data.put("PRE_DEPT_ID", this.getVisit().getDepartId());
		data.put("PRE_STAFF_SERIAL_NUMBER", this.getVisit().getSerialNumber());
		data.put("EPARCHY_CODE", this.getVisit().getStaffEparchyCode());
		data.put("CITY_CODE", this.getVisit().getCityCode());
		
		String svcname = "PB.AddrPreDeal.addAddrPreDeal";
		IDataset info = this.call(svcname, data);
		String flag = info.getData(0).getString("FLAG");
		String result = info.getData(0).getString("RESULT");
		IData ajax = new DataMap();
		ajax.put("FLAG", flag);
		ajax.put("RESULT",result);		
		this.setAjax(ajax);
	}
	
	protected IDataOutput call1(String svcName, IData data) throws Exception{
        if (logger.isDebugEnabled()){
            logger.debug("ServiceFactory.call Service=[" + svcName + "]");
        }

        IDataInput input = createDataInput(data);
        IDataOutput output = ServiceFactory.call(svcName, input);
        return output;
    }
	
	/**
	 * @param svcName
	 * @param data
	 * @return
	 * @throws Exception
	 */
	protected IDataset call(String svcName, IData data) throws Exception{
        if (logger.isDebugEnabled()){
            logger.debug("ServiceFactory.call Service=[" + svcName + "]");
        }

        IDataInput input = createDataInput(data);
        IDataOutput output = ServiceFactory.call(svcName, input);
        IDataset result = output.getData();
        return result;
    }
	
    // modify by duhj
    protected IDataOutput call2(String svcName, IData data, Pagination pagination) throws Exception
    {
        IDataInput input = createDataInput(data);
        String url = "http://10.200.138.15:10001/service";
        IDataOutput output = ServiceFactory.call(url, svcName, input, pagination);
        return output;
    }
}
