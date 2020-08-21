
package com.asiainfo.veris.crm.order.soa.person.busi.plat;

import java.util.ArrayList;
import java.util.List;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.plat.PlatFamilyManage;

public class PlatOrderIntfBean extends CSBizBean
{

    public void getTradeData(IData data) throws Exception
    {
        // if (td != null) { return td; }

        // in_mode_code=6是一级boss传过来的接入码
        String inModeCode = data.getString("IN_MODE_CODE", "");
        if ("6".equals(inModeCode))
        {
            String trade_type_code = StaticUtil.getStaticValue("CSINFT_BIZ_TRADETYPECODE", data.getString("KIND_ID"));
            if (StringUtils.isEmpty(trade_type_code))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_205);
            }

            data.put("TRADE_TYPE_CODE", trade_type_code);
        }

        // 路由原则 如果传入了SERIAL_NUMBER 根据手机号码路由
        // 否则通过ROUTE——_EPARCHY_CODE字段路由 注意不是TRADE_EPARCHY_CODE
        // 如果以上方式都无法确定路由 则路由到中心库
        String routeEparchyCode = Route.ROUTE_EPARCHY_CODE;
        String serialNumber = data.getString("SERIAL_NUMBER");
        // 设置路由信息
        if (serialNumber != null && serialNumber.trim().length() > 0)
        {
            IData routeData = RouteInfoQry.getMofficeInfoBySn(serialNumber);
            if (IDataUtil.isNotEmpty(routeData))
            {
                String route = routeData.getString("AREA_CODE");
                if (StringUtils.isNotEmpty(route))
                {
                    routeEparchyCode = route;
                }
            }
        }

        if (routeEparchyCode == null || routeEparchyCode.length() == 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_206);
        }

        IData termData = new DataMap();// huanggs 2010-04-14 add
        termData.put("TRADE_TERMINAL_ID", data.getString("TRADE_TERMINAL_ID", ""));

    }

    /**
     * 获取td信息,与csbasebean中的逻辑一样,不同的是需要修改错误码,主要用于接口中构建td的三户模型
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    public void getTradeInfo(String routeEparchyCode) throws Exception
    {

        IData data = new DataMap();

        String tradeTypeCode = "3700";

        IData tradePara = UTradeTypeInfoQry.getTradeType(tradeTypeCode, routeEparchyCode);
        String infoTagSet = tradePara.getString("INFO_TAG_SET");
        String extendTag = tradePara.getString("EXTEND_TAG");

        if (StringUtils.equals(routeEparchyCode, CSBizBean.getTradeEparchyCode()) && "0".equals(extendTag))
        {

            String resultInfo = "该业务不允许在异地受理！";
            data.put("PLAT_ERROR_CODE", "0999");
            data.put("PLAT_ERROR_INFO", "该业务不允许在异地受理");
            CSAppException.apperr(CrmCommException.CRM_COMM_177);
        }

    }

    /**
     * 通行证变更
     * 
     * @param pd
     * @param param
     * @return
     */
    public IData updateInfo(IData data) throws Exception
    {
        String ACTION_REASON = data.getString("ACTION_REASON");// 操作原因
        IData param = new DataMap();
        String passId = data.getString("PASS_ID");
        String userId = data.getString("USER_ID");
        String rsrvValueCode = "NET_USER_INFO";
        // 手机号码更换通行证号
        if ("10".equals(ACTION_REASON))
        {
            String staffId = CSBizBean.getVisit().getStaffId();
            String departId = CSBizBean.getVisit().getDepartId();
            UserOtherInfoQry.updPassIDInfoByUserId(userId, "NET_USER_INFO", passId, staffId, departId);
        }
        // 手机号与通行证号绑定
        if ("11".equals(ACTION_REASON))
        {
            String tradeId = data.getString("TRADE_ID");
            String remark = "互联网通行证号";
            String staffId = CSBizBean.getVisit().getStaffId();
            String departId = CSBizBean.getVisit().getDepartId();
            UserOtherInfoQry.insPassIDInfoByUserId(userId, rsrvValueCode, passId, passId, remark, staffId, departId);
        }
        // 手机号与通行证号解绑定
        if ("12".equals(ACTION_REASON))
        {
            String staffId = CSBizBean.getVisit().getStaffId();
            String departId = CSBizBean.getVisit().getDepartId();
            UserOtherInfoQry.cancelPassIDInfoByUserId(userId, rsrvValueCode, passId, staffId, departId);
        }
        param.clear();
        param.put("X_RESULTCODE", "0");
        param.put("X_RESULTINFO", "OK");
        return param;
    }
    /**
	 * 亲情圈成员同步
	 * @param pd
	 * @param data
	 * @return
	 * @throws Exception
	 * @author zhangbo18
	 */
	public static IData synFamilyCircle(IData data) throws Exception{
		IData result = new DataMap();

		result.put("X_RESULTCODE", "0000");
		result.put("BIZ_ORDER_RESULT", "0000");
		result.put("X_RESULTINFO", "OK！");
		result.put("PKG_SEQ", data.getString("PKG_SEQ"));
		result.put("ID_TYPE", "01");
		result.put("ID_VALUE", data.getString("ID_VALUE"));
		
		String id_type = data.getString("ID_TYPE");
		String main_msisdn = data.getString("ID_VALUE");
		if ("01".equals(id_type) && "".equals(main_msisdn)){
			result.put("X_RESULTCODE", "2998");
			result.put("BIZ_ORDER_RESULT", "2998");
			result.put("X_RESULTINFO", "手机号码不能为空！");
			return result;
		}
		IDataset userList = data.getDataset("USER_LIST");
		if (userList.size() <= 0){
			result.put("X_RESULTCODE", "2998");
			result.put("BIZ_ORDER_RESULT", "2998");
			result.put("X_RESULTINFO", "亲情圈列表不能为空！");
			return result;
		}
		result.put("X_RECORDNUM", userList.size());
		
		IDataset add = new DatasetList();
		IDataset upd = new DatasetList();
		IDataset del = new DatasetList();
		IData member = null;
		IData fc = null;
		String operFlag = "";
		for(int i = 0 ; i < userList.size() ; i++){
			fc = new DataMap();
			member = userList.getData(i);
			fc.put("MAIN_MSISDN", main_msisdn);
			fc.put("TARGET_MSISDN", member.getString("MID_VALUE"));
			fc.put("TARGET_NAME", member.getString("MEM_NAME"));
			fc.put("TARGET_ROLE", member.getString("MEM_ROLE"));
			operFlag = member.getString("OPR_CODE");	// 01 - 新增、02 –删除、03 –变更成员
			if ("01".equals(operFlag)){
				add.add(fc);
			}else if ("02".equals(operFlag)){
				del.add(fc);
			}else if ("03".equals(operFlag)){
				upd.add(fc);
			}
		}
		
		//获取该用户原亲情圈成员
		IData inparams = new DataMap();
		inparams.put("SQL_REF", "SEL_ALL_MEMBER_BY_SN");
		inparams.put("MSISDN", main_msisdn);
		inparams.put("STATUS", "0");
		inparams.put("GROUP_TYPE", "01");
		IDataset family_result = PlatFamilyManage.getUserFamilyCircle(inparams);
		
		//获取此次新增成员，但该成员在原有失效列表中已经存在的数据
		IDataset exists = checkExistsMember(family_result, add);
		if (null == exists){
			exists = new DatasetList();
		}
		
		//获取该用户下有效的亲情圈成员
		inparams.put("STATUS", "1");
		family_result = PlatFamilyManage.getUserFamilyCircle(inparams);

		IDataset exists1 = checkExistsMember(family_result, add);
		
		if (null != exists1 && exists1.size() > 0){
			exists.addAll(exists1);
		}
		
		PlatFamilyManage.updateExistsMembers(exists);
		
		//批处理亲情成员
		PlatFamilyManage.batchOperFamilyMember(add, del, upd,new DataMap());
		
		return result; 
	}
	
	/***
	 * 校验新增的数据是否存在失效状态数据
	 * @param result
	 * @param add
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static IDataset checkExistsMember(IDataset result,IDataset add)throws Exception{
		IDataset exists = new DatasetList();
		
		if (result == null || result.size() <= 0 || add == null || add.size() <= 0){
			return null;
		}
		for(int i = 0 ; i < result.size() ; i++){
			IData temp = result.getData(i);
			IData atemp = null;
			boolean flag = false;
			for(int j = 0 ; add.size() > 0 && j < add.size() ; j++){
				atemp = add.getData(j);
				if (temp.getString("MAIN_MSISDN").equals(atemp.getString("MAIN_MSISDN"))
						&& temp.getString("TARGET_MSISDN").equals(atemp.getString("TARGET_MSISDN"))){
					flag = true;
					exists.add(atemp);
					break;
				}
			}
			if (flag){
				add.remove(atemp);
			}
		}
		
		return exists;		
	}
	
	/**
	 * 平安群组成员同步
	 * @param pd
	 * @param data
	 * @return
	 * @throws Exception
	 * @author zhangbo18
	 */
	public static IData synSafeGroupInfo(IData data) throws Exception{
		IData result = new DataMap();
		result.put("X_RESULTCODE", "0");
		result.put("X_RESULTINFO", "OK！");
		//UDR 成员列表
		IDataset group = data.getDataset("UDR");
		if (null == group || group.size() <= 0){
			result.put("X_RESULTCODE", "2998");
			result.put("X_RESULTINFO", "平安互助群组信息不能为空！");
			return result;
		}
		
		IData operList = getOperGroup(group);
		
		IData operResult = operSafeGroup(operList);
		
		result.put("X_RECORDNUM", operResult.getInt("OPERNUM"));
		
		return result; 
	}
	/***
	 * 将用户依据不同的操作分组
	 * @param pd
	 * @param group
	 * @return
	 * @throws Exception
	 */
	public static IData getOperGroup(IDataset group)throws Exception {
		//定义列表用于存储不同的操作数据
		IData result = new DataMap();
		IDataset add = new DatasetList();
		IDataset upd = new DatasetList();
		IDataset del = new DatasetList();
		IDataset full = new DatasetList();
		List addList = new ArrayList();
		//用于多个群组同时更新时，不重复操作查询
		IData group_code = new DataMap();
		
		IData qryParam = new DataMap();
		String groupName = "";
		//ID_TYPE  01-手机号码
		
		for(int i = 0 ; i < group.size() ; i++){
			qryParam.clear();
			IData operData = new DataMap();
			IData temp = group.getData(i);
			
			operData.put("TARGET_NAME", temp.getString("MEM_NAME"));//MEM_NAME  成员姓名
			operData.put("TARGET_ROLE", temp.getString("MEM_ROLE"));//MEM_ROLE  关于成员角色、权限的描述性文字
			operData.put("GROUP_NAME", temp.getString("GROUP_NAME"));//GROUP_NAME 所属群组名称
			operData.put("GROUP_AREA", temp.getString("GROUP_AREA"));//GROUP_AREA 格式：省-市-县-镇-村
			operData.put("GROUP_TYPE", "02");
			operData.put("TARGET_MSISDN", temp.getString("ID_VALUE"));  //ID_VALUE  成员手机号码
			operData.put("START_DATE", temp.getString("OPR_TIME")); //OPR_TIME  格式：YYYYMMDDHHMISS，HH24小时制
			
			groupName = temp.getString("GROUP_NAME");
			if (addList.contains(groupName)){
				operData.put("MAIN_MSISDN", group_code.getString(groupName));
				operData.put("GROUP_CODE", group_code.getString(groupName));
			}else{
				addList.add(groupName);
				qryParam.put("GROUP_NAME", groupName);
				//获取群组对应的群组编号
				String groupcode = PlatFamilyManage.getGroupCodeByName(qryParam);
				group_code.put(groupName, groupcode);
				operData.put("MAIN_MSISDN", groupcode);
				operData.put("GROUP_CODE", groupcode);
			}
			//OPR_CODE  A：新增，U：变更，D：删除，W：全量
			if ("W".equals(temp.getString("OPR_CODE"))){
				operData.put("STATUS", "1");
				IDataset re = PlatFamilyManage.getGroupByMainSN(operData);
				if (null != re && re.size() > 0){
					upd.add(operData);
				}else{
					add.add(operData);
				}
				//full.add(operData);
			}else if("A".equals(temp.getString("OPR_CODE"))){
				operData.put("STATUS", "1");
				add.add(operData);
			}else if("U".equals(temp.getString("OPR_CODE"))){
				operData.put("STATUS", "1");
				upd.add(operData);
			}else if ("D".equals(temp.getString("OPR_CODE"))){
				operData.put("STATUS", "0");
				del.add(operData);
			}
		}
		
		result.put("ADD", add);
		result.put("UPD", upd);
		result.put("DEL", del);
		result.put("FULL", full);
		
		return result;
	}
	
	
	/***
	 * 依据操作列表，对数据做不同的处理
	 * @param pd
	 * @param operList
	 * @return
	 * @throws Exception
	 */
	public static IData operSafeGroup(IData operList)throws Exception{
		IData operResult = new DataMap();
		
		IDataset temp = new DatasetList();
		
		StringBuffer sb = new StringBuffer("处理成功,\n");
		int operNum = 0;
		//全量操作处理
		if (operList.getDataset("FULL").size() > 0){
			temp = operList.getDataset("FULL");
			IData param = new DataMap();
			param.put("STATUS", "0");
			param.put("GROUP_TYPE", "02");
			
			PlatFamilyManage.delSafeGroupMembers(param);
			
			PlatFamilyManage.instMembersBySG(temp);
			sb.append("全量操作处理记录["+temp.size()+"]条;\n");
			operNum = operNum + temp.size();
		}
		//新增操作处理
		if (operList.getDataset("ADD").size() > 0){
			temp = operList.getDataset("ADD");
			IData param = new DataMap();
			param.put("MSISDN", temp.getData(0).getString("GROUP_CODE"));
			param.put("STATUS", "0");
			param.put("GROUP_TYPE", "02");
			IDataset sgResult = PlatFamilyManage.getSafeGroupByInvalid(param);
			//获取此次新增成员，但该成员在原有失效列表中已经存在的数据
			IDataset exists = checkExistsMember(sgResult, temp);
			if (null == exists){
				exists = new DatasetList();
			}
			param.put("STATUS", "1");
			sgResult = PlatFamilyManage.getSafeGroupByInvalid(param);
			
			IDataset exists1 = checkExistsMember(sgResult, temp);
			
			if (null != exists1 && exists1.size() > 0){
				exists.addAll(exists1);
			}
			//插入全新的记录
			PlatFamilyManage.instMembersBySG(temp);
			//更新原失效记录
			PlatFamilyManage.updateSafeGroupMembers(exists);
			sb.append("新增记录["+temp.size()+"]条;\n");
			operNum = operNum + temp.size();
		}
		//修改操作处理
		if (operList.getDataset("UPD").size() > 0){
			temp = operList.getDataset("UPD");
			PlatFamilyManage.updateSafeGroupMembers(temp);
			sb.append("更新记录["+temp.size()+"]条;\n");
			operNum = operNum + temp.size();
		}
		//删除操作处理
		if (operList.getDataset("DEL").size() > 0){
			temp = operList.getDataset("DEL");
			PlatFamilyManage.updateMembers(temp);
			sb.append("删除记录["+temp.size()+"]条;\n");
			operNum = operNum + temp.size();
		}
		operResult.put("SUCC_MSG", sb.toString());
		operResult.put("OPERNUM", operNum);
		return operResult;
	}
	
	public static IData timeoutFlowCardQuery(IData data) throws Exception
    {
        IData result = new DataMap();
        String serialNumber = data.getString("SERIAL_NUMBER", "");

        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (userInfo == null)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_21);
        }
        
        IDataset userDiscnt = UserDiscntInfoQry.getAllDiscntByUser(userInfo.getString("USER_ID"),data.getString("DISCNT_CODE"));

        if(userDiscnt != null && userDiscnt.size() > 0){
			result.put("X_RESULTCODE", "00");
			result.put("X_RESULTINFO", "查询成功");
			result.put("BIZ_ORDER_RESULT", "0");
			String startDate = userDiscnt.getData(0).getString("START_DATE");
			String endDate = userDiscnt.getData(0).getString("END_DATE");
			result.put("RSP_EFFETI_TIME", startDate.substring(0, 10).replace("-", ""));
			result.put("RSP_ABATE_TIME", endDate.substring(0, 10).replace("-", ""));
		}else{
			result.put("X_RESULTCODE", "00");
			result.put("X_RESULTINFO", "查询成功");
			result.put("BIZ_ORDER_RESULT", "1");
		}
        return result;
    }
}
