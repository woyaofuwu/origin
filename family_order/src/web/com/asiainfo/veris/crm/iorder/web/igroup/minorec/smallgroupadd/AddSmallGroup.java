package com.asiainfo.veris.crm.iorder.web.igroup.minorec.smallgroupadd;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.BizVisit;
import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.priv.StaffPrivUtil;
import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;

public abstract class AddSmallGroup extends EopBasePage{
	
	public abstract void setCustInfo(IData custInfo);
	public abstract void setSubIndustryType(IDataset subIndustryType);
	public abstract void setOgrTypeA(String ogrTypeA);
	public abstract void setOgrTypeB(String ogrTypeB);
	public abstract void setChipAreaCode(IDataset chipAreaCode);
	public abstract void setProvinces(IDataset provinces);
	public abstract void setCitys(IDataset citys);
	public abstract void setAreas(IDataset areas);
	public abstract void setTomns(IDataset tomns);
	public abstract void setCallingTypeA(IDataset callingTypeA);
	public abstract void setCallingTypeB(IDataset callingTypeB);
	public abstract void setCallingTypeC(IDataset callingTypeC);
	public abstract void setClassDType(IDataset classDType);
	public abstract void setGrids(IDataset grids);
	public abstract void setCond(IData cond);
	public abstract void setInfo(IData info);
	public abstract void setInfos(IDataset infos);
	public abstract void setInfoCount(long infoCount);
	public abstract void setOutInfo(IData outInfo);
	public abstract void setOutInfos(IDataset outInfos);
	public abstract void setOutInfoCount(long outInfoCount);
	
     public void init(IRequestCycle cycle)throws Exception{
 		IData params = new DataMap();
 		BizVisit visit = getVisit();
 		params.put("REMOVE_METHOD", "99");    //默认 流失类型     为未流失
        params.put("GROUP_TYPE", "1");    //默认 集团类型     单位集体
        params.put("REMOVE_FLAG", "1");       //默认 流失条件     为低于5户
        params.put("CUST_MANAGER_ID", visit.getStaffId());  //默认 客户经理 为当前操作员
        params.put("CUST_MANAGER_NAME", visit.getStaffName());
        params.put("PROVINCE_CODE", "898");
        params.put("EPARCHY_NAME", visit.getLoginEparchyName());//visit.getStaffEparchyCode()));
        params.put("EPARCHY_CODE", visit.getStaffEparchyCode());
        params.put("STAFF_NAME", visit.getStaffName());     //当前操作员的名称
        params.put("CITY_CODE", visit.getCityCode());        //
        params.put("CITY_NAME", visit.getCityCode()+"|"+visit.getCityName());  
        //params.put("CALL_TYPE", "99");  //行业应用 默认 为 其它
        params.put("PSPT_ID", "1"); 
        params.put("GROUP_STATUS", "1"); //集团状态 默认 “未开通”
        params.put("RSRV_STR4", "86");	//国家编码,默认中国
        params.put("PROVINCE_CODE", "898");//add by wuhao 归属省份 默认“海南”
        params.put("ACCEPT_CHANNEL", "1");	//默认 发展渠道 为客户经理
        params.put("RSRV_TAG3", "1");	//默认 服务渠道 为客户经理
        if(StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(),"MODIFY_CLASS_ID"))
		{
        	params.put("MODIFY_CLASS_ID", "true");
		}
        if(StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(),"CMG_ALL_VIEW"))
		{
        	params.put("RSRV_NUM3", "0");
		}else{
			String rsrv_tag1 = StaticUtil.getStaticValue(visit, "TD_M_STAFF", "STAFF_ID", "RSRV_TAG1", this.getVisit().getStaffId());
			if("1".equals(rsrv_tag1)){
				params.put("RSRV_NUM3", "1");
			}else{
				params.put("RSRV_NUM3", "0");
			}
		}
        //add by zhoumx 2010/11/26 铁通融合改造
        //PDCommonBean.setParameter(pd, params);
        
 		setCustInfo(params);
     }
     
     
 	public void getIndustryType(IRequestCycle cycle)throws Exception{
		IData cond = this.getData();
		String industryCode = cond.getString("CALLING_TYPE_CODE");
		if(industryCode == null || industryCode.equals("")){
			setSubIndustryType(null);
			return;
		}
		//IDataOutput result = cs.callPage("CC.group.IGroupQuerySV.qrySubCallingTypeCode", cond);
		IDataset infos = CSViewCall.call(this, "CC.group.IGroupQuerySV.qrySubCallingTypeCode", cond);
		if(infos == null || infos.size() <= 0){
			setSubIndustryType(null);
			return;
		}
		setSubIndustryType(infos);
	}
 	
 	public void setOgrType(IRequestCycle cycle)throws Exception{
		IData pageData = this.getData();
		String ogrType = pageData.getString("OGR_TYPE");
		String ogrTypeValue = pageData.getString("OGR_TYPE_VALUE");
		if(ogrType.equals("A")){
			setOgrTypeA(ogrTypeValue);
			IDataset classDTypeList = pageutil.getStaticListByParent("CUSTGROUP_ORGTYPEB",ogrTypeValue);
			setCallingTypeB(classDTypeList);	
			IDataset classDTypeList1 = null;
			setCallingTypeC(classDTypeList1);
		}else if(ogrType.equals("B")){
			setOgrTypeB(ogrTypeValue);
			IDataset classDTypeList = pageutil.getStaticListByParent("CUSTGROUP_ORGTYPEC",ogrTypeValue);
			setCallingTypeC(classDTypeList);
		}		
	}

	public void getCallingTypeB(IRequestCycle cycle)throws Exception{
		
		IData param = this.getData();
		String callingTypeValue = param.getString("CALLING_TYPEA","");
		String callingTypeValueB = param.getString("CALLING_TYPEB","");
		if("".equals(callingTypeValue)){
			setCallingTypeB(null);
			return;
		}
		//行业中类
		IDataset callintTypeB = pageutil.getStaticListByParent("CUSTGROUP_ORGTYPEB",callingTypeValue);		
		setCallingTypeB(callintTypeB);
		
		//行业小类
		IDataset callingTypeC = pageutil.getStaticListByParent("CUSTGROUP_ORGTYPEC",callingTypeValueB);		
		if(!callingTypeC.isEmpty() && callingTypeC.size()>0){
			setAjax("hasTypeC", "1");
		}
		setCallingTypeC(callingTypeC);
	}
	
	public void getCallingTypeC(IRequestCycle cycle)throws Exception{
		
		IData param = this.getData();
		String callingTypeValue = param.getString("CALLING_TYPEB","");
		if("".equals(callingTypeValue)){
			setCallingTypeC(null);
			return;
		}
		//行业小类
		IDataset callingTypeC = StaticUtil.getStaticList("CUSTGROUP_CALLINGTYPE_C_"+callingTypeValue);		
		if(!callingTypeC.isEmpty() && callingTypeC.size()>0){
			setAjax("hasTypeC", "1");
		}
		setCallingTypeC(callingTypeC);
	}
	
	public void checkEnterName(IRequestCycle cycle)throws Exception{
		IData pageData = this.getData();
		IData result = CSViewCall.callone(this,"CC.group.IGroupQuerySV.validGroupName", pageData);
		IDataset info = result.getDataset("DATAS");
		if(null != info && !info.isEmpty()){
			setAjax("isExit", "0");
		}else{
			setAjax("isExit", "1");
		}
	}
	
	public void addEnterprise(IRequestCycle cycle)throws Exception{
		IData pageData = this.getData();	
		
		//上传附件名字转换
		String licenceinfo = pageData.getString("LICENCE_INFO","");
		if(StringUtils.isNotBlank(licenceinfo)){
			pageData.put("LICENCE_INFO", pageData.getString("LICENCE_INFO","")+":"+pageData.getString("LICENCE_INFO_name",""));
		}
		//add by zhoumx 2010/12/2 铁通融合改造
        String rsrv_num3 = pageData.getString("RSRV_NUM3", "0");
        if("1".equals(rsrv_num3)) {
        	pageData.put("CUST_MANAGER_ID", "");//铁通客户经理为空
        }
        
        IData result = CSViewCall.callone(this,"CC.group.IGroupQuerySV.checkCustName", pageData);
        IData info = result.getData("DATAS");
        //集团名称存在，新增失败，流程结束
        if(null != info && !info.isEmpty()){
        	pageutil.abort("集团客户名称已经存在，请重新填写集团客户名称");
        }
        
        if("".equals(pageData.getString("YEAR_GAIN"))) {
        	pageutil.abort("集团年营业额(万元)必须填写(且为数字)！");
        } else {
        	try {
        		int emp = pageData.getInt("YEAR_GAIN");
        	} catch (Exception e) {
        		pageutil.abort("集团年营业额(万元)必须填写(且为数字)！");
        	}
        }
        
        //REQ201904110008优化集团客户资料、集团合同相关功能的需求  校验营业执照、组织机构代码
        checkVerifyCard(pageData);
        
        IData groupInfo =CSViewCall.callone(this,"CC.group.IGroupOperateSV.addGroup", pageData);
        String groupId = groupInfo.getString("GROUP_ID");
        setCustInfo(groupInfo);
        setAjax("GROUP_ID", groupId);
        //Dao.callProc("p_Cms_Insertgpmforgrouppp1", paramName, paramValue, memEparchyCode);
        
	}
	
	public void queryManager(IRequestCycle cycle)throws Exception{
		IData cond = this.getData("cond");
		IData result = CSViewCall.callone(this,"CC.person.IPersonInfoQuerySV.qryUserCustInfo",cond/*,getPagination("navbar2")*/);
		IDataset adver = result.getDataset("DATAS");
		setInfos(adver);
		setInfoCount(result.getLong("X_RESULTCOUNT",0));
	}
	
	public void querySoc(IRequestCycle cycle)throws Exception{
		IData cond = this.getData("cond");
		IData result = CSViewCall.callone(this,"CC.person.IPersonInfoQuerySV.querySocInfoList",cond/*,getPagination("navbar3")*/);
		IDataset adver = result.getDataset("DATAS");
		setInfos(adver);
		setInfoCount(result.getLong("X_RESULTCOUNT",0));
	}
	
	public void queryParentGroup(IRequestCycle cycle)throws Exception{
		IData cond = this.getData("cond");
		IData data = new DataMap();
		data.put("CUST_NAME",cond.getString("SUPER_CUST_NAME"));
		data.put("GROUP_ID",cond.getString("SUPER_GROUP_ID"));
		data.put("REMOVE_TAG", "0");
		if ("HNSJ".equals(getVisit().getCityCode()) ){

		}
		else {
			data.put("CITY_CODE", getVisit().getCityCode());
		}
		
		IData result = CSViewCall.callone(this,"CC.group.IGroupQuerySV.qryGroupByAll",data/*,getPagination("navbar4")*/);
		IDataset adver = result.getDataset("DATAS");
		setInfos(adver);
		setInfoCount(result.getLong("X_RESULTCOUNT",0));
	}
	
	/**
	 * REQ201903010057新增在线建档虚拟集团新增界面
	 * @param cycle
	 * @throws Exception
	 */
	/*public void queryOutGroupSynInfo(IRequestCycle cycle)throws Exception{
		IData cond = this.getData("cond");
		cond.put("CITY_CODE", getVisit().getCityCode());
		IData result = this.call("CC.outsvc.ICCOutGroupOperateSV.qryOutGroupSynInfo",cond,getPagination("navbar9"));
		IDataset adver = result.getDataset("DATAS");
		setOutInfos(adver);
		setOutInfoCount(result.getLong("X_RESULTCOUNT",0));
	}*/
	
	/**
	 * REQ201904110008优化集团客户资料、集团合同相关功能的需求  校验营业执照、组织机构代码
	 * @param pageData
	 * @author chenhh6
	 */
	public void checkVerifyCard(IData pageData) throws Exception{
		String pspt_type_code = pageData.getString("BUSI_LICENCE_TYPE");
		String pspt_id = pageData.getString("BUSI_LICENCE_NO");
		String name = pageData.getString("CUST_NAME");
        IData cond = new DataMap();
        if(pspt_type_code.equals("E")){
            cond.put("regitNo", pspt_id);
            cond.put("enterpriseName", name);
            cond.put("legalperson",  pageData.getString("custInfo_legalperson","").trim());
            cond.put("termstartdate",  pageData.getString("custInfo_termstartdate","").trim());
            cond.put("termenddate",  pageData.getString("custInfo_termenddate","").trim());
            cond.put("startdate",  pageData.getString("custInfo_startdate","").trim());
            
            IData result = CSViewCall.callone(this,"SS.CreatePersonUserSVC.verifyEnterpriseCard", cond);
            System.out.println("EnterpriseResult："+result);
            if(!result.getString("X_RESULTCODE","").trim().equals("0")){
            	pageutil.abort(result.toString());
            }
        }else if (pspt_type_code.equals("3")) {
            cond.put("orgCode", pspt_id);
            cond.put("orgName", name);
            cond.put("orgtype",  pageData.getString("custInfo_orgtype","").trim());
            cond.put("effectiveDate",  pageData.getString("custInfo_effectiveDate","").trim());
            cond.put("expirationDate",  pageData.getString("custInfo_expirationDate","").trim());
            
            IData result = CSViewCall.callone(this,"SS.CreatePersonUserSVC.verifyOrgCard", cond);
            System.out.println("OrgResult："+result);
            if(!result.getString("X_RESULTCODE","").trim().equals("0")){                     
            	pageutil.abort(result.toString());
            }
        }
	}
}
