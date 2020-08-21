package com.asiainfo.veris.crm.iorder.soa.group.ecintegration.check;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ailk.bizservice.base.CSAppCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public class CloudWifiMebChkBean extends EcIntegrationMebChkBaseBean {

	/**
	 * mac号码正则验证
	 */
	public final static String CLOUD_WIFI_MAC_NUM_REG = "[0-9a-fA-F]{12}";
	
	/**
	 * serial号码正则验证
	 */
	public final static String CLOUD_WIFI_SERIAL_NUM_REG = "^\\d{11}$";

	/**
	 * 批量校验成员
	 */
	@Override
	protected IData checkEcIntegrationMebList(IDataset checkMebList) throws Exception {
		IDataset successList = new DatasetList();
		IDataset failureList = new DatasetList();
		if (IDataUtil.isNotEmpty(checkMebList)) {
			batchCheckOther(checkMebList);
			for (int i = 0, size = checkMebList.size(); i < size; i++) {
				IData mebInfo = checkMebList.getData(i);
				mebInfo.put("EC_USER_ID", ecUserId);
				mebInfo.put("BPM_TEMPLET_ID", bpmTempletId);
				checkOther(mebInfo);

				if (mebInfo.getBoolean(IMPORT_RESULT)) {
					successList.add(mebInfo);
				} else {
					failureList.add(mebInfo);
				}
			}
		}
		IData checkResult = new DataMap();
		checkResult.put("SUCC_LIST", successList);
		checkResult.put("FAIL_LIST", failureList);
		return checkResult;
	}

	/**
	 ** 批量查询成员号码是否已有和正在被占用。
	 * @param checkMebList
	 * @Date 2019年12月21日
	 * @author xieqj 
	 * @throws Exception 
	 */
	private void batchCheckOther(IDataset checkMebList) throws Exception {
		IData queryData = new DataMap();
		queryData.put("PRODUCT_ID", "380300");
		queryData.put("MEMBERLIST", checkMebList);
		IDataset existMemberList = qryExistMember(queryData);
		
		StringBuilder serialNumberBuilder = new StringBuilder();
		for (int i = 0, size = existMemberList.size(); i < size; i++) {
			IData mebInfo = existMemberList.getData(i);
			serialNumberBuilder.append(mebInfo.getString("SERIAL_NUMBER_B") + ",");
		}
		String serialNumbers = serialNumberBuilder.toString();
		
		for (int i = 0, size = checkMebList.size(); i < size; i++) {
			IData mebInfo = checkMebList.getData(i);
			String macNumber = mebInfo.getString("MAC_NUMBER");
			if(serialNumbers.contains(macNumber)) {
				mebInfo.put(IMPORT_RESULT, false);
				mebInfo.put(IMPORT_ERROR, "网关MAC号【" + macNumber + "】已存在或被其他用户占用，请勿重复添加！");
			}
		}
	}

	@Override
	public void checkOther(IData mebInfo) throws Exception {
		IData checkResult = new DataMap();
		String macNumber = mebInfo.getString("MAC_NUMBER");
		String serialNumber = mebInfo.getString("SERIAL_NUMBER");
		
		if (!mebInfo.containsKey(IMPORT_RESULT)) {
			checkResult.put(IMPORT_RESULT, true);
			mebInfo.putAll(checkResult);
		}
		
		Pattern pattern = Pattern.compile(CLOUD_WIFI_MAC_NUM_REG);
		Matcher matcher = pattern.matcher(macNumber);

		if (StringUtils.isEmpty(macNumber)) {
			checkResult.put(IMPORT_RESULT, false);
			checkResult.put(IMPORT_ERROR, "网关MAC号为空，请输入！");
			mebInfo.putAll(checkResult);
		} else if (!matcher.matches()) {
			checkResult.put(IMPORT_RESULT, false);
			checkResult.put(IMPORT_ERROR, "网关MAC号为12位，由（0-9，A-F，a-f）组成！");
			mebInfo.putAll(checkResult);
		} else if (StringUtils.isNotEmpty(serialNumber)) {
			Pattern serialNumPattern = Pattern.compile(CLOUD_WIFI_SERIAL_NUM_REG);
			Matcher serialNumMatcher = serialNumPattern.matcher(serialNumber);
			if (!serialNumMatcher.matches()) {
				checkResult.put(IMPORT_RESULT, false);
				checkResult.put(IMPORT_ERROR, "成员号码只能为宽带开通手机/固话号码，最多为11位数字！");
				mebInfo.putAll(checkResult);
			}
		}
		 
	}

	/**
	 * 单个批量校验成员
	 */
	@Override
	protected IData checkEcIntegrationMebSingle(IData memberInfo) throws Exception {
		
		checkOther(memberInfo);
		
		String macNumber = memberInfo.getString("MAC_NUMBER");

		// 新增成员重复交验
		IDataset memberList = qryExistMember(memberInfo);
		if (IDataUtil.isNotEmpty(memberList)) {
			for (Object obj : memberList) {
				IData mebData = (IData) obj;
				String oldNumber = mebData.getString("SERIAL_NUMBER_B");
				if (macNumber.equals(oldNumber)) {
					memberInfo.put(IMPORT_RESULT, false);
					memberInfo.put(IMPORT_ERROR, "网关MAC号【" + macNumber + "】已存在或被其他用户占用，请勿重复添加！");
				}
			}
		}
		return memberInfo;
	}
	
    /**
	 ** 根据产品id查询BB表所有被使用的成员信息
	 * @return
	 * @throws Exception
	 * @Date 2019年11月6日
	 * @author xieqj 
     * @param queryData 
	 */
	public IDataset qryExistMember(IData queryData) throws Exception {
		IData result = new DataMap();
		result.put("PRODUCT_ID", "380300");
		result.put("SERIAL_NUMBER_B", queryData.getString("MAC_NUMBER"));
		result.put("MEMBERLIST", queryData.getDataset("MEMBERLIST"));
		IDataset memberList = CSAppCall.call("SS.QuickOrderMemberSVC.qryExistsRelationbbMebInfos", result);
		return memberList;
	}
}
