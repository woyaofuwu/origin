
package com.asiainfo.veris.crm.order.soa.group.outgroupsyn;

import org.apache.log4j.Logger;

import com.ailk.biz.util.TimeUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;


/**
 * 建档虚拟集团接口
 * @author Administrator
 *
 */
public class ManageOutGroupSynSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;
    
    private static final Logger logger = Logger.getLogger(ManageOutGroupSynSVC.class);
    
	public IData addOutGroupInfos(IData params) throws Exception{
		
		IData result = new DataMap();
    	IData object = new DataMap();
    	IDataset resultDataset = new DatasetList();
    	
    	try
    	{
    		IData param = new DataMap(params.toString()).getData("params");
    		if(logger.isDebugEnabled())
    		{
    			logger.debug("虚拟集团新增接口请求参数:" + params);
    		}
    		
    		if (IDataUtil.isEmpty(param))
    		{
    			//setResult(resultDataset,object,result,"获取params参数为空!");
    			object.put("respCode", "-9999");
    			object.put("respDesc", "获取params参数为空!");
    			object.put("result", resultDataset);
    			
    			result.put("object", object);
    			result.put("rtnCode", "0");
    			result.put("rtnDesc", "成功!");
    			return result;
    		}
    		
    		//集团名称
    		//String custName = request.getString("CUST_NAME");
    		String custName = param.getString("groupNm");
    		if(StringUtils.isBlank(custName)){
    			setResult(resultDataset,object,result,"集团名称不能为空!");
    			return result;
    		}
    		//归属省份
    		//String provinceCode = "898"; //使用默认值
    		//String provinceCode = request.getString("PROVINCE_CODE");
    		String provinceCode = param.getString("belgProv");
    		if(StringUtils.isBlank(provinceCode)){
    			setResult(resultDataset,object,result,"归属省份不能为空!");
    			return result;
    		}
    		//归属地市 SERVICE_CITY
    		//String countyCode = request.getString("COUNTY_CODE");
    		String countyCode = param.getString("belgDistrtCode");
    		if(StringUtils.isBlank(countyCode)){
    			setResult(resultDataset,object,result,"归属地市不能为空!");
    			return result;
    		}
    		//归属地州
    		//String eparchyCode = request.getString("EPARCHY_CODE");
    		String eparchyCode = param.getString("belgState");
    		if(StringUtils.isBlank(eparchyCode)){
    			setResult(resultDataset,object,result,"归属地州不能为空!");
    			return result;
    		}
    		//归属业务区
    		//String cityCode = request.getString("CITY_CODE");
    		String cityCode = param.getString("belgDistrt");
    		if(StringUtils.isBlank(cityCode)){
    			setResult(resultDataset,object,result,"归属业务区不能为空!");
    			return result;
    		}
    		//集团类型
    		//String groupType = "1"; //默认 集团类型     单位集体
    		//String groupType = request.getString("GROUP_TYPE");
    		String groupType = param.getString("groupType");
    		if(StringUtils.isBlank(groupType)){
    			setResult(resultDataset,object,result,"集团类型不能为空!");
    			return result;
    		}
    		//集团状态
    		String groupStatus = "1";//集团状态 默认 “未开通”
    		//String groupStatus = request.getString("GROUP_STATUS");
    		//if(StringUtils.isBlank(groupStatus)){
    		//	setResult(resultDataset,object,result,"集团状态不能为空!");
    		//	return result;
    		//}
    		//集团属性
    		//String groupAttr = request.getString("GROUP_ATTR");
    		String groupAttr = param.getString("groupProperty");
    		if(StringUtils.isBlank(groupAttr)){
    			setResult(resultDataset,object,result,"集团属性不能为空!");
    			return result;
    		}
    		//集团机构大类型
    		//String orgTypeA = request.getString("ORG_TYPE_A");
    		String orgTypeA = param.getString("groupOrgType");
    		if(StringUtils.isBlank(orgTypeA)){
    			setResult(resultDataset,object,result,"集团机构大类型不能为空!");
    			return result;
    		}
    		//集团机构类型中类
    		//String orgTypeB = request.getString("ORG_TYPE_B");
    		String orgTypeB = param.getString("groupOrgMidType");
    		if(StringUtils.isBlank(orgTypeB)){
    			setResult(resultDataset,object,result,"集团机构类型中类不能为空!");
    			return result;
    		}
    		
    		//第二页
    		//项目经理
    		//String proManager = request.getString("PRO_MANAGER");
    		String proManager = param.getString("projectMng");
    		if(StringUtils.isBlank(proManager)){
    			setResult(resultDataset,object,result,"项目经理不能为空!");
    			return result;
    		}
    		//集团经营区域类型
    		//String enterpriseScope = request.getString("ENTERPRISE_SCOPE");
    		String enterpriseScope = param.getString("groupOpeAreaType");
    		if(StringUtils.isBlank(enterpriseScope)){
    			setResult(resultDataset,object,result,"集团经营区域类型不能为空!");
    			return result;
    		}
    		//所属行业类别
    		//String callingTypeCode = request.getString("CALLING_TYPE_CODE");
    		String callingTypeCode = param.getString("belgIndustry");
    		if(StringUtils.isBlank(callingTypeCode)){
    			setResult(resultDataset,object,result,"所属行业类别不能为空!");
    			return result;
    		}
    		//所属子行业类别
    		//String subCallingTypeCode = request.getString("SUB_CALLING_TYPE_CODE");
    		String subCallingTypeCode = param.getString("belgSubIndustry");
    		if(StringUtils.isBlank(subCallingTypeCode)){
    			setResult(resultDataset,object,result,"所属子行业类别不能为空!");
    			return result;
    		}
    		//归属本省行业科室
    		//String industryAttr = request.getString("INDUSTRY_ATTR");
    		String industryAttr = param.getString("belgProvInd");
    		if(StringUtils.isBlank(industryAttr)){
    			setResult(resultDataset,object,result,"归属本省行业科室不能为空!");
    			return result;
    		}
    		//企业类型
    		//String enterpriseTypeCode = request.getString("ENTERPRISE_TYPE_CODE");
    		String enterpriseTypeCode = param.getString("comType");
    		if(StringUtils.isBlank(enterpriseTypeCode)){
    			setResult(resultDataset,object,result,"企业类型不能为空!");
    			return result;
    		}
    		//证件类别
    		//String busiLicenceType = request.getString("BUSI_LICENCE_TYPE");
    		String busiLicenceType = param.getString("cetType");
    		if(StringUtils.isBlank(busiLicenceType)){
    			setResult(resultDataset,object,result,"证件类别不能为空!");
    			return result;
    		}
    		//证件号码
    		//String busiLicenceNo = request.getString("BUSI_LICENCE_NO");
    		String busiLicenceNo = param.getString("idNum");
    		if(StringUtils.isBlank(busiLicenceNo)){
    			setResult(resultDataset,object,result,"证件号码不能为空!");
    			return result;
    		}
    		//客户证件扫描件
    		//String licenceInfo = request.getString("LICENCE_INFO");
    		//if(StringUtils.isBlank(licenceInfo)){
    		//	setResult(resultDataset,object,result,"客户证件扫描件不能为空!");
    		//	return result;
    		//}
    		//集团管理员
    		//String groupMgrCustName = request.getString("GROUP_MGR_CUST_NAME");
    		String groupMgrCustName = param.getString("groupMng");
    		if(StringUtils.isBlank(groupMgrCustName)){
    			setResult(resultDataset,object,result,"集团管理员不能为空!");
    			return result;
    		}
    		//管理员服务号码
    		//String groupMgrSn = request.getString("GROUP_MGR_SN");
    		String groupMgrSn = param.getString("mngSevNum");
    		if(StringUtils.isBlank(groupMgrSn)){
    			setResult(resultDataset,object,result,"管理员服务号码不能为空!");
    			return result;
    		}
    		//集团级别
    		//String classId = request.getString("CLASS_ID");
    		String classId = param.getString("groupLevel");
    		if(StringUtils.isBlank(classId)){
    			setResult(resultDataset,object,result,"集团级别不能为空!");
    			return result;
    		}
    		//集团客户地址
    		//String groupAddr = request.getString("GROUP_ADDR");
    		String groupAddr = param.getString("groupCutAdd");
    		if(StringUtils.isBlank(groupAddr)){
    			setResult(resultDataset,object,result,"集团客户地址不能为空!");
    			return result;
    		}
    		//集团客户邮编
    		//String postCode = request.getString("POST_CODE");
    		String postCode = param.getString("groupCutPostcode");
    		if(StringUtils.isBlank(postCode)){
    			setResult(resultDataset,object,result,"集团客户邮编不能为空!");
    			return result;
    		}
    		//集团客户联系电话
    		//String groupContactPhone = request.getString("GROUP_CONTACT_PHONE");
    		String groupContactPhone = param.getString("groupCutNum");
    		if(StringUtils.isBlank(groupContactPhone)){
    			setResult(resultDataset,object,result,"集团客户联系电话不能为空!");
    			return result;
    		}
    		//是否铁通集团
    		//String mobileGrpTag = request.getString("MOBILE_GRP_TAG");
    		String mobileGrpTag = param.getString("tietongGroup");
    		if(StringUtils.isBlank(mobileGrpTag)){
    			setResult(resultDataset,object,result,"是否铁通集团不能为空!");
    			return result;
    		}
    		
    		//是否省级集团
    		//String topGrpTag = request.getString("TOP_GRP_TAG");
    		String topGrpTag = param.getString("provGroup");
    		if(StringUtils.isBlank(topGrpTag)){
    			setResult(resultDataset,object,result,"是否省级集团不能为空!");
    			return result;
    		}
    		//集团客户级别(2016)
    		//String classIdNew = request.getString("CLASS_ID_NEW");
    		String classIdNew = param.getString("groupCutLev");
    		if(StringUtils.isBlank(classIdNew)){
    			setResult(resultDataset,object,result,"集团客户级别(2016)不能为空!");
    			return result;
    		}
    		//企业员工数
    		//String employeeNums = request.getString("EMPLOYEE_NUMS");
    		String employeeNums = param.getString("comStaffNum");
    		if(StringUtils.isBlank(employeeNums)){
    			setResult(resultDataset,object,result,"企业员工数不能为空!");
    			return result;
    		}
    		//集团年营业额
    		//String yearGain = request.getString("YEAR_GAIN");
    		String yearGain = param.getString("comAnlTover");
    		if(StringUtils.isBlank(yearGain)){
    			setResult(resultDataset,object,result,"集团年营业额不能为空!");
    			return result;
    		}
    		//企业规模
    		//String enterpriseSizeCode = request.getString("ENTERPRISE_SIZE_CODE");
    		String enterpriseSizeCode = param.getString("firmSize");
    		if(StringUtils.isBlank(enterpriseSizeCode)){
    			setResult(resultDataset,object,result,"企业规模不能为空!");
    			return result;
    		}
    		
    		//是否直管客户
    		//String extIssc = request.getString("EXT_ISSC");
    		String extIssc = param.getString("strCut");
    		if(StringUtils.isBlank(extIssc)){
    			setResult(resultDataset,object,result,"是否直管客户不能为空!");
    			return result;
    		}
    		//是否敏感信息集团
    		//String secretGrp = request.getString("SECRET_GRP");
    		String secretGrp = param.getString("senInfoGrou");
    		if(StringUtils.isBlank(secretGrp)){
    			setResult(resultDataset,object,result,"是否敏感信息集团不能为空!");
    			return result;
    		}
    		
    		IData ncustomer = new DataMap();
    		ncustomer.put("CUST_NAME", custName);//集团名称
    		ncustomer.put("PROVINCE_CODE", provinceCode);//归属省份
    		
    		ncustomer.put("COUNTY_CODE", countyCode);//归属地市
    		ncustomer.put("EPARCHY_CODE", eparchyCode);//归属地州
    		ncustomer.put("CITY_CODE", cityCode);//归属业务区
    		ncustomer.put("GROUP_TYPE", groupType);//集团类型
    		ncustomer.put("GROUP_STATUS", groupStatus);//集团状态
    		ncustomer.put("GROUP_ATTR", groupAttr);//集团属性
    		ncustomer.put("ORG_TYPE_A", orgTypeA);//集团机构大类型
    		ncustomer.put("ORG_TYPE_B", orgTypeB);//集团机构类型中类
    		
    		ncustomer.put("PRO_MANAGER", proManager);//项目经理
    		ncustomer.put("ENTERPRISE_SCOPE", enterpriseScope);//集团经营区域类型
    		ncustomer.put("CALLING_TYPE_CODE", callingTypeCode);//所属行业类别
    		ncustomer.put("SUB_CALLING_TYPE_CODE", subCallingTypeCode);//所属子行业类别
    		ncustomer.put("INDUSTRY_ATTR", industryAttr);//归属本省行业科室
    		ncustomer.put("ENTERPRISE_TYPE_CODE", enterpriseTypeCode);//企业类型
    		ncustomer.put("BUSI_LICENCE_TYPE", busiLicenceType);//证件类别
    		ncustomer.put("BUSI_LICENCE_NO", busiLicenceNo);//证件号码
    		//ncustomer.put("LICENCE_INFO", licenceInfo);//客户证件扫描件
    		ncustomer.put("GROUP_MGR_CUST_NAME", groupMgrCustName);//集团管理员
    		ncustomer.put("GROUP_MGR_SN", groupMgrSn);//管理员服务号码
    		ncustomer.put("CLASS_ID", classId);//集团级别
    		ncustomer.put("GROUP_ADDR", groupAddr);//集团客户地址
    		ncustomer.put("POST_CODE", postCode);//集团客户邮编
    		ncustomer.put("GROUP_CONTACT_PHONE", groupContactPhone);//集团客户联系电话
    		ncustomer.put("MOBILE_GRP_TAG", mobileGrpTag);//是否铁通集团
    		ncustomer.put("TOP_GRP_TAG", topGrpTag);//是否省级集团
    		ncustomer.put("CLASS_ID_NEW", classIdNew);//集团客户级别(2016)
    		
    		ncustomer.put("EMPLOYEE_NUMS", employeeNums);//企业员工数
    		ncustomer.put("YEAR_GAIN", yearGain);//集团年营业额
    		ncustomer.put("ENTERPRISE_SIZE_CODE", enterpriseSizeCode);//企业规模
    		ncustomer.put("EXT_ISSC", extIssc);//是否直管客户
    		ncustomer.put("SECRET_GRP", secretGrp);//是否敏感信息集团
    		
    		ncustomer.put("REMOVE_TAG", "0");
    		ncustomer.put("IN_DATE", TimeUtil.getSysTime());
    		
    		ManageOutGroupSynBean bean = new ManageOutGroupSynBean();
    		
    		IData custParam = new DataMap();
    		
    		custParam.put("CUST_NAME", custName);
    		IDataset nameDatas = bean.qryOutGrpByCustName(custParam);
    		if(nameDatas != null && nameDatas.size() > 0)
    		{
    			setResult(resultDataset,object,result,"虚拟集团客户名称已经存在!");
    			return result;
    		}
    		
    		custParam.clear();
    		custParam.put("REMOVE_TAG", "0");
    		custParam.put("BUSI_LICENCE_TYPE", busiLicenceType);
    		custParam.put("BUSI_LICENCE_NO", busiLicenceNo);
    		//校验新增的虚拟集团的证件号是否已经存在
    		IDataset custInfos = bean.qryCustInfoByLicenceTypeNo(custParam);
    		if(custInfos != null && custInfos.size() > 0)
    		{
    			setResult(resultDataset,object,result,"已经存在该证件类型与证件号码的集团资料!");
    			return result;
    		}
    		
    		//开始创建预建虚拟集团编码
    		String mgmtDistrict = "0898";
	        String mgmtCounty = cityCode;
	        String calling_type_code = "";
			String[] paramName =
			        { "iv_v_trade_eparchy_code", "iv_v_trade_city_code", "iv_v_calling_type_code", 
					"ov_v_group_id", "on_v_resultcode", "ov_v_resulterrinfo"};

	        IData paramValue = new DataMap();
	        paramValue.put("iv_v_trade_eparchy_code", mgmtDistrict);
	        paramValue.put("iv_v_trade_city_code", mgmtCounty);
	        paramValue.put("iv_v_calling_type_code", calling_type_code);
    	     
	        try{
				Dao.callProc("UOP_CRM1.P_CMS_GEN_OUTGROUP_ID", paramName, paramValue);
			}catch (Exception e){
				object.put("respCode", "-9999");
				object.put("respDesc", "生成集团编码时异常发生异常。");
				object.put("result", resultDataset);
				
				result.put("object", object);
				result.put("rtnCode", "-9999");
				result.put("rtnDesc", "失败!");
			}
			// 是否成功
	        String resultCode = paramValue.getString("on_v_resultcode");

	        if (!"0".equals(resultCode))
	        {
	            String resultInfo = paramValue.getString("ov_v_resulterrinfo");
	            object.put("respCode", "-9999");
				object.put("respDesc", "生成集团编码时异常,异常原因:" + resultInfo);
				object.put("result", resultDataset);
				
				result.put("object", object);
				result.put("rtnCode", "0");
				result.put("rtnDesc", "成功!");
	        }
	        String groupId = paramValue.getString("ov_v_group_id","");
	        
	        if(StringUtils.isEmpty(groupId) || StringUtils.isBlank(groupId))
	        {
	        	object.put("respCode", "-9999");
				object.put("respDesc", "获取预建集团编码失败!" );
				object.put("result", resultDataset);
				
				result.put("object", object);
				result.put("rtnCode", "0");
				result.put("rtnDesc", "成功!");
	        }
	        //结束
	        
	        ncustomer.put("GROUP_ID", groupId);
	        
    		boolean insertResult = bean.addOutGroupInfos(ncustomer);
    		if(!insertResult){
    			setResult(resultDataset,object,result,"新增虚拟集团资料失败!");
    			return result;
    		}
    		
	        IData groupMap = new DataMap();
	        groupMap.put("groupNo", groupId);
	        resultDataset.add(groupMap);
	        
    		object.put("respCode", "0");
    		object.put("respDesc", "成功!");
    		object.put("result", resultDataset);
    		
    		result.put("object", object);
    		result.put("rtnCode", "0");
    		result.put("rtnDesc", "成功!");
    		
    		return result;
    	}
    	catch(Exception e)
    	{
    		String message = e.getMessage();
    		
    		logger.error("虚拟集团新增异常:" + message);
    		
    		object.put("respCode", "-9999");
    		object.put("respDesc", message);
    		object.put("result", resultDataset);
    		
    		result.put("object", object);
    		result.put("rtnCode", "-9999");
    		result.put("rtnDesc", "失败!");
    		return result;
    	}
		
	}
	
	private void setResult(IDataset resultDataset,IData object,IData result, String message) throws Exception
	{
		object.put("respCode", "-9999");
		object.put("respDesc", message);
		object.put("result", resultDataset);
		
		result.put("object", object);
		result.put("rtnCode", "0");
		result.put("rtnDesc", "成功!");
	}
	
}



