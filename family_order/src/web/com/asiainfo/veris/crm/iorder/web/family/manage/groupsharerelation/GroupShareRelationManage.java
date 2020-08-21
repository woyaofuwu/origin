
package com.asiainfo.veris.crm.iorder.web.family.manage.groupsharerelation;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

public abstract class GroupShareRelationManage extends PersonBasePage {

	public abstract void setFamilyCustInfo(IData familyCustInfo);

	public abstract void setMemberInfos(IDataset memberInfos);

	public abstract void setMemberInfo(IData memberInfo);

	/**
	 * @Description: auth回调，查询家庭客户信息和家庭手机成员共享信息
	 * @Param: [cycle]
	 * @return: void
	 * @Author: lixx9
	 * @Date: 17:07
	 */ 
	public void loadChildInfo(IRequestCycle cycle) throws Exception {

		IData param = getData();

		IData familyCustInfo = CSViewCall.callone(this,"SS.FamilyCustInfoQrySVC.familyCustInfoQryBySn",param);

		setFamilyCustInfo(familyCustInfo);

		String tradeTypeCode = param.getString("TRADE_TYPE_CODE");
		if(StringUtils.isBlank(tradeTypeCode)){
			CSViewException.apperr(CrmCommException.CRM_COMM_103,"请先选择业务类型");
		}

		IDataset MemberInfos = new DatasetList();
		if(StringUtils.equals("454",tradeTypeCode)){ //代付关系管理

			//查询家庭成员代付关系
			MemberInfos = CSViewCall.call(this,"SS.FamilyCustInfoQrySVC.familyMemberPayRelationQry",param);

		}else if(StringUtils.equals("458",tradeTypeCode)){ //共享关系管理

			//查询家庭中流量/语音共享的手机成员
			 MemberInfos = CSViewCall.call(this,"SS.FamilyCustInfoQrySVC.familyPhoneMemberShareInfoQry",param);
		}

		setMemberInfos(MemberInfos);


		//测试数据
//		IDataset phoneMemberInfos=testJson(4);
//
//		setPhoneMemberInfos(phoneMemberInfos);

	}

	/**
	 * @Description: 提交
	 * @Param: [cycle]
	 * @return: void
	 * @Author: lixx9
	 * @Date: 17:07
	 */ 
	public void onTradeSubmit(IRequestCycle cycle) throws Exception
	{
		IData pageData = getData();
		pageData.put("ROUTE_EPARCHY_CODE",getVisit().getLoginEparchyCode());
		String tradeTypeCode = pageData.getString("TRADE_TYPE_CODE");
		if(StringUtils.isBlank(tradeTypeCode)){
			CSViewException.apperr(CrmCommException.CRM_COMM_103,"请先选择业务类型");
		}
		IDataset rtDataset = new DatasetList();
		if (StringUtils.equals("458",tradeTypeCode)){ //共享关系管理
			rtDataset = CSViewCall.call(this, "SS.GroupShareRelationRegSVC.tradeReg", pageData);
		}else if(StringUtils.equals("454",tradeTypeCode)) { //代付关系管理
			rtDataset = CSViewCall.call(this, "SS.FamilyPayRelationRegSVC.tradeReg", pageData);
		}


		this.setAjax(rtDataset);

	}

	//测试数据
	private IDataset testJson(int num) throws Exception {

		IDataset results = new DatasetList();

		for (int i=0;i<num;i++){
			IData rs = new DataMap();
			int state = Math.random()>0.5?0:1;
			rs.put("MEMBER_SERIAL_NUMBER", SysDateMgr.getSysDateYYYYMMDDHHMMSS().substring(5)+i);
			rs.put("SHARE_TAG",state);
			results.add(rs);
		}

		return results;
	}

}
