package com.asiainfo.veris.crm.order.soa.group.task.imp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ailk.biz.impexp.ImportTaskExecutor;
import com.ailk.bizservice.base.CSAppCall;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class CloudWifiImportTask extends ImportTaskExecutor {

	/**
	 * mac号码正则验证
	 */
	public final static String CLOUD_WIFI_MAC_NUM_REG = "[0-9a-fA-F]{12}";
	
	/**
	 * @Title:executeImport
	 * @Description:云WiFi导入
	 * @param data    请求入参
	 * @param dataset 附件内容
	 * @return
	 * @throws Exception
	 * 
	 */
	@Override
	public IDataset executeImport(IData data, IDataset dataset) throws Exception {
		// 导入文件不为空
		if (IDataUtil.isEmpty(dataset)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "未获取到表格数据，或表格未填写完整！");
		}
		 
		String productId = data.getString("PRODUCT_ID");
		String operType = data.getString("OPER_TYPE");
		IDataset infos = new DatasetList();
		
		IDataset serialNumberList = new DatasetList();
		if ("DelMeb".equals(operType)) {
			String serialNumber = data.getString("EC_SERIAL_NUMBER");
			IDataset userInfoBySn = getUserInfoBySn(serialNumber);
			if (DataUtils.isEmpty(userInfoBySn)) {
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据服务号码【" + serialNumber + "】， 未找到用户信息！");
			}

			IData userInfo = userInfoBySn.first();
			IDataset memberList = qryExistMember(userInfo.getString("USER_ID"));
			for (int i = 0, len = memberList.size(); i < len; i++) {
				IData memberData = (IData) memberList.get(i);
				if (memberData.getBoolean("FLAG")) {
	                serialNumberList.add(memberData.getString("SERIAL_NUMBER"));
	            } else {
	                serialNumberList.add(memberData.getString("MAC_NUMBER"));
	            }
			}

		} else {
			//新增时，自身添加的号码进行过滤
			String mebList = data.getString("MEB_LIST");
			IDataset oldmebList = new DatasetList();
			if (DataUtils.isNotEmpty(mebList)) {
				IDataset mebLists = new DatasetList(mebList);
				for (int i = 0, len = mebLists.size(); i < len; i++) {
					IData memberData = (IData) mebLists.get(i);
					oldmebList.add(memberData.getString("SERIAL_NUMBER"));
				}
			}
			
			//查询历史数据和在途数据
			IData queryData = new DataMap();
			queryData.put("PRODUCT_ID", "380300");
			queryData.put("MEMBERLIST", dataset);
			IDataset existMemberList = qryAllExistMember(queryData);
			
			if (IDataUtil.isNotEmpty(existMemberList)) {
				for (int i = 0, len = existMemberList.size(); i < len; i++) {
					IData memberData = (IData) existMemberList.get(i);
					//过滤掉自身添加的号码
					if (oldmebList.contains(memberData.getString("SERIAL_NUMBER_B"))) {
						continue;
					}
					serialNumberList.add(memberData.getString("SERIAL_NUMBER_B"));
				}
			}
		}
		
		//文件中的遍历过的成员号码
		IDataset existList = new DatasetList();
		for (int i = 0; i < dataset.size(); i++) {
			IData memberData = new DataMap();
			IData importData = dataset.getData(i);
			IDataUtil.chkParam(importData, "MAC_NUMBER");
			String macNumber = importData.getString("MAC_NUMBER", "");
			
			Pattern pattern = Pattern.compile(CLOUD_WIFI_MAC_NUM_REG);
			Matcher matcher = pattern.matcher(macNumber);
			if (!matcher.matches()) {
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "文件中网关mac号[" + macNumber + "] 格式不正确！网关MAC号为12位，由（0-9，A-F，a-f）组成！");
			}
			
			// 是否存在重复mac号
			if (existList.contains(macNumber)) {
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "文件中存在重复网关mac号[" + macNumber + "], 请修改后重新提交！");
			}
			existList.add(macNumber);

			if (BizCtrlType.MinorecAddMember.equals(operType) || DataUtils.isEmpty(operType)) {
				if (serialNumberList.contains(macNumber)) {
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "待添加网关MAC号【" + macNumber + "】已存在，请勿重复添加！请修改后重新提交添加数据！");
				}
			} else if (BizCtrlType.MinorecDestroyMember.equals(operType)) {
				if (!serialNumberList.contains(macNumber)) {
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "待删除网关MAC号【" + macNumber + "】 不存在！请修改后重新提交删除数据！");
				}
			}
			 
			memberData.put("PHONE_NUMBER", importData.getString("SERIAL_NUMBER", ""));
			memberData.put("MAC_NUMBER", macNumber);
			infos.add(memberData);
		}
		SharedCache.set("IMPORT_MEMBER_INFO" + productId, infos);
		return null;
	}
	
	/**
	 ** 根据产品用户id查询BB表中该用户下的成员信息
	 * @param ecUserId   产品用户id
	 * @param serialNumber 成员号码
	 * @return
	 * @throws Exception
	 * @Date 2019年11月5日
	 * @author xieqj 
	 */
	public IDataset qryExistMember(String ecUserId) throws Exception {
		IData params = new DataMap();
		params.put("USER_ID_A", ecUserId);
		IDataset memberList = CSAppCall.call("SS.QryEspMemberSVC.queryEspMember", params);
		return memberList;
	}
	
	/**
	 ** 根据产品id查询BB表所有被使用的成员信息
	 * @return
	 * @throws Exception
	 * @Date 2019年11月6日
	 * @author xieqj 
	 * @param productId 
	 */
	public IDataset qryAllExistMember(IData queryData) throws Exception {
		IData result = new DataMap();
		result.put("PRODUCT_ID", "380300");
 		result.put("MEMBERLIST", queryData.getDataset("MEMBERLIST"));
		IDataset memberList = CSAppCall.call("SS.QuickOrderMemberSVC.qryExistsRelationbbMebInfos", result);
		return memberList;
	}
	
	 
	public IDataset getUserInfoBySn(String serialNumber) throws Exception {
		IData params = new DataMap();
		params.put("SERIAL_NUMBER", serialNumber);
		IDataset memberList = CSAppCall.call("SS.QuickOrderDataSVC.qryUserInfoUserIdBySerialNumber", params);
		return memberList;
	}
}
