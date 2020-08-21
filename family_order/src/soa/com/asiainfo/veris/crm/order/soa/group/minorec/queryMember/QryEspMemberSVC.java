package com.asiainfo.veris.crm.order.soa.group.minorec.queryMember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.group.minorec.quickorder.QuickOrderMemberListBean;

public class QryEspMemberSVC extends GroupOrderService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * json字符串最大拼接长度
	 */
	private static final int JSON_MAX_LENGTH = 10;
	/**
	 ** 根据产品用户id获取ESP产品所有成员  
	 * @param param （USER_ID_A 产品用户id）
	 * @return
	 * @throws Exception
	 * @Date 2019年11月1日
	 * @author xieqj 
	 */
	public IDataset queryEspMember(IData param) throws Exception
	{
		IDataset resultDataset = new DatasetList();
		IDataset iDataset = CSAppCall.call("CS.RelaBBInfoQrySVC.getAllMebByUSERIDA", param);
		if (DataUtils.isNotEmpty(iDataset)) {
			IData first = iDataset.first();
			String ecSerialNumber = first.getString("SERIAL_NUMBER_A");
			IData qryData = new DataMap();
			qryData.put("EC_SERIAL_NUMBER", ecSerialNumber);
			IDataset memberList = QuickOrderMemberListBean.getEspMembersBySerialNumber(qryData);
			
			IData memberDatas = new DataMap();
			transferListToMap(memberDatas, memberList);
			
			for (int i = 0, len = iDataset.size(); i < len; i++) {
				
				IData memberRelaBBData = (IData)iDataset.get(i);
				String serialNumber = memberRelaBBData.getString("SERIAL_NUMBER_B");
				IData memberData = memberDatas.getData(serialNumber);
				if (DataUtils.isEmpty(memberData))
				{
					//未获取到开通时数据，组装临时数据，防止报错
					IData newMemberData = new DataMap();
					newMemberData.put("SERIAL_NUMBER", serialNumber);
					newMemberData.put("EC_SERIAL_NUMBER", memberRelaBBData.getString("SERIAL_NUMBER_A"));
 					newMemberData.put("USER_ID", memberRelaBBData.getString("USER_ID_A"));
 					newMemberData.put("FLAG", true);
					resultDataset.add(newMemberData);
					continue;
				}
				resultDataset.add(memberData);
			}
		}
		return resultDataset;
	}

	private void transferListToMap(IData memberData, IDataset memberList) {
		for (int i = 0, len = memberList.size(); i < len; i++) {
			IData member = (IData)memberList.get(i);
			String ecSerialNumber = member.getString("EC_SERIAL_NUMBER");
			String serialNumber = member.getString("SERIAL_NUMBER");
			//转换拼接数据
			String dataStr = buildMemberDataStr(member);
			IData memberStrData = new DataMap(dataStr);
			memberStrData.put("EC_SERIAL_NUMBER", ecSerialNumber);
			memberStrData.put("SERIAL_NUMBER", serialNumber);
			 
			memberData.put(serialNumber, memberStrData);
		}
	}

	/**
	 ** 将json字符串数据片段，拼接成完整json字符串
	 * @param memberInfo
	 * @return
	 * @Date 2019年10月1日
	 * @author xieqj 
	 */
	private String buildMemberDataStr(IData memberInfo) {
		StringBuilder memberInitStr = new StringBuilder();
        for (int i = 1; i <= JSON_MAX_LENGTH; i++) {
            if (StringUtils.isNotBlank(memberInfo.getString("CODING_STR" + i))) {
            	memberInitStr.append(memberInfo.getString("CODING_STR" + i));
            }
        }
        return memberInitStr.toString();
    }
	

}
