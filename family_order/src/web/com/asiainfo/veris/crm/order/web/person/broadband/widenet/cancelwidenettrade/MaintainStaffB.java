package com.asiainfo.veris.crm.order.web.person.broadband.widenet.cancelwidenettrade;

import java.util.HashMap;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class MaintainStaffB extends PersonBasePage{

	public abstract void setCondition(IData cond);

	public abstract void setInfo(IData info);

	public abstract void setInfos(IDataset infos);
	
	public abstract void setAreas(IDataset dataset);

	public abstract void setPageCount(long l);

	/**
	 * 
	 * 查询界面初始化
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void init(IRequestCycle cycle) throws Exception {
		IData inits = getData("cond",true);
		//返回登录工号信息
		inits.put("ON_CITY_CODE", getVisit().getCityCode());
		//设置查询区
		IDataset areas = getAreas(true);
		setAreas(areas);
		setCondition(inits);
	}
	
	/**
	 * 
	 * 查询B角员工信息
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void queryStaffB(IRequestCycle cycle) throws Exception {
		IData inputData = this.getData("cond");
		inputData.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
		IDataOutput result = CSViewCall.callPage(this, "SS.CancelWidenetTradeService.queryStaffB", inputData, this.getPagination("pageNav"));
		IDataset dataset = result.getData();
		setPageCount(result.getDataCount());
		// 设置页面返回数据
		setInfos(dataset);
		setCondition(inputData);
	}
	
    /**
     * 修改B角员工信息初始化
     * 
     * @param cycle
     */
    public void modifyInit(IRequestCycle cycle) throws Exception {
        IData input = this.getData();
        // 服务返回结果集
        IDataset result = CSViewCall.call(this, "SS.CancelWidenetTradeService.queryStaffB", input);
        if (!IDataUtil.isEmpty(result)){
            IData resultData = result.getData(0);
            resultData.put("OLD_STAFF_ID", result.getData(0).getString("STAFF_ID"));
            this.setCondition(resultData);
        } else{
            CSViewException.apperr(ElementException.CRM_ELEMENT_310,"没有查询到有效的B角员工记录!");
        }
    }
    
    /**
     * 保存B角员工信息
     * 
     * @param cycle
     */
    public void saveStaffB(IRequestCycle cycle) throws Exception {
    	IData input = this.getData();
    	IData param = new DataMap();
    	String msg = "";
    	//查询员工基本信息
    	IDataset info = CSViewCall.call(this, "SS.CancelWidenetTradeService.queryStaffInfo", input);
    	// 新增标示
    	boolean createFlag = true;
    	if(IDataUtil.isNotEmpty(info)){
    		if (input.getString("OLD_STAFF_ID", "").length() > 1){
    			// 修改数据
    			createFlag = false;
    			input.put("UPDATE_STAFF_ID", getVisit().getStaffId());
    			input.put("UPDATE_TIME", SysDateMgr.getSysTime());
    			input.put("createFlag", createFlag);
    			IDataset results = CSViewCall.call(this, "SS.CancelWidenetTradeService.saveStaffB", input);
    			if (!IDataUtil.isEmpty(results)){
    				IData data = results.getData(0);
    				if (data.getBoolean("SUCCESS")){
    					msg = "保存成功！";
    				}else{
    					msg = "保存失败！";
    				}
    			}else{
    				msg = "保存失败！";
    			}
    		} else{
    			input.put("DEPART_CODE", info.getData(0).getString("DEPART_CODE"));
    			input.put("DEPART_NAME", info.getData(0).getString("DEPART_NAME"));
    			input.put("CITY_CODE", info.getData(0).getString("CITY_CODE"));
    			input.put("STAFF_NAME", info.getData(0).getString("STAFF_NAME"));
    			input.put("INSERT_STAFF_ID", getVisit().getStaffId());
    			input.put("INSERT_TIME", SysDateMgr.getSysTime());
    			//新增需要判断TD_M_STAFF_B同一个市县，是否已存在3条或者3条以上的员工记录
    			param.put("CITY_CODE", info.getData(0).getString("CITY_CODE"));
    			IDataset results = CSViewCall.call(this, "SS.CancelWidenetTradeService.queryStaffB", param);
    			if (!IDataUtil.isEmpty(results)){
    				int count = results.size();
    				for (int i = 0; i < count; i++) {
    					IData result = results.getData(i);
    					String oldstaffId = result.getString("STAFF_ID");
    					String staffId = input.getString("STAFF_ID");
    					if (staffId.equals(oldstaffId)) {
    						CSViewException.apperr(ElementException.CRM_ELEMENT_310,"您新增的员工已存在，请重新输入工号!");
    					} else if (count > 2) {
    						CSViewException.apperr(ElementException.CRM_ELEMENT_310,"该市县已存在3个B角员工，不能再增加!");
    					} 
    				}
    			}
    			input.put("createFlag", createFlag);
    			IDataset infos = CSViewCall.call(this, "SS.CancelWidenetTradeService.saveStaffB", input);
    			if (!IDataUtil.isEmpty(infos)){
    				IData data = infos.getData(0);
    				if (data.getBoolean("SUCCESS")){
    					msg = "保存成功！";
    				}else{
    					msg = "保存失败！";
    				}
    			}else{
    				msg = "保存失败！";
    			}
    		}
    	} else {
    		CSViewException.apperr(ElementException.CRM_ELEMENT_310,"没有查到有效的员工记录!");
    	}
    	this.setAjax("ALERT_INFO", msg);
    }
    
    /**
     * 删除B角员工信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void deleteStaffB(IRequestCycle cycle) throws Exception {
    	IData inputData = this.getData();
    	String runInfo = inputData.get("PARAM")==null?"":inputData.get("PARAM").toString();
    	IDataset dataset = new DatasetList(runInfo);
    	if(dataset != null && !dataset.isEmpty()) {
    		IData info = new DataMap();
    		for (int i=0; i<dataset.size(); i++){
    			IData data = dataset.getData(i);
    			info.put("STAFF_ID", data.getString("staffId"));
    	    	// 服务返回结果集
    	    	IDataset results = CSViewCall.call(this, "SS.CancelWidenetTradeService.deleteStaffB", info);
    	    	if (!IDataUtil.isEmpty(results)){
    	    		IData result = results.getData(0);
    	    		boolean flag = result.getBoolean("SUCCESS");
    	    		if (!flag){
    	    			this.setAjax("ALERT_INFO", "删除失败！请联系管理员。");
    	    		}
    	    	}
    		}
    	}
    }
    
	/**
	 * 获取查询区内容
	 * 
	 * @param fltByStaffId 工号是否限制
	 * @return
	 * @throws Exception
	 */
    public IDataset getAreas(boolean fltByStaffId) throws Exception{
        IDataset areaInfos = StaticUtil.getList(this.getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", "PARENT_AREA_CODE", this.getVisit().getStaffEparchyCode());
    	HashMap<String, String> filterMap = new HashMap<String, String>();
    	filterMap.put("HNIB", "一级BOSS业务区");
    	filterMap.put("HWEB", "门户网站接口");
    	filterMap.put("MEDA", "多媒体自助接口");
    	filterMap.put("MESG", "短信接口");
    	filterMap.put("SERV", "客服系统接口");
    	filterMap.put("HNFW", "外拨");
    	filterMap.put("HNKF", "海南客服");
    	filterMap.put("HNKH", "客户");
    	filterMap.put("HNNO", "未知");
    	filterMap.put("HNYD", "客户服务中心");
    	filterMap.put("YKDH", "一卡多号业务区");
    	IDataset outAreas = new DatasetList();
    	String cityId = getVisit().getCityCode().toUpperCase();
    	String staffId = getVisit().getStaffId();
    	//过滤掉：一级BOSS业务区(HNIB)、门户网站接口(HWEB)、多媒体自助接口(MEDA)、短信接口(MESG)、客服系统接口(SERV)
    	for (int i=0; i<areaInfos.size(); i++) {
    		IData areaInfo = areaInfos.getData(i);
    		String areaCode = areaInfo.getString("AREA_CODE");
    		if (!filterMap.containsKey(areaCode)) {
    			//根据工号限制业务区
    			if (fltByStaffId) {
    				//只有省局的账号可以查看所有业务区的数据
    				if(cityId.equals("HNSJ") || staffId.startsWith("HNSJ")){
    					outAreas.add(areaInfo);
    					continue;
    				}
    				//不是省局的账号只能查看所属业务区的数据
    				if (cityId.equals(areaCode) || staffId.startsWith(areaCode)) {
    					outAreas.add(areaInfo);
    					continue;
    				}
    			}
    			else {//不用限制业务区
    				outAreas.add(areaInfo);
    			}
    		}
    	}
    	return outAreas;
    }
}
