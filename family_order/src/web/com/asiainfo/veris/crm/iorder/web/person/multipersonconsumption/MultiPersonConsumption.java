
package com.asiainfo.veris.crm.iorder.web.person.multipersonconsumption;

import java.util.HashMap;
import java.util.Map;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class MultiPersonConsumption extends PersonBasePage
{
	public void init(IRequestCycle cycle) throws Exception {

	}
	
	public void getVerifyModeList(IRequestCycle cycle) throws Exception {
		IDataset verifyModeList = new DatasetList();
		// 获取校验方式
		IDataset tmpList = StaticUtil.getStaticList("DR_CONSUME_NO_VERIFY");
		// 判断是否具有“免密码”权限
		boolean isPriv = StaffPrivUtil.isPriv(getVisit().getStaffId(), "DR_CONSUME_NO_VERIFY", "1");
		if (!isPriv) {
			for (int i = 0, size = tmpList.size(); i < size; i++) {
				IData tmp = tmpList.getData(i);
				String dataId = tmp.getString("DATA_ID");
				if (dataId.contains("2")) {
					continue;
				}
				verifyModeList.add(tmp);
			}
		} else {
			verifyModeList.addAll(tmpList);
		}
		IData ajax = new DataMap();
		ajax.put("VERIFY_MODE_LIST", verifyModeList);
		setAjax(ajax);
	}

    /**
     * 添加副号码的校验
     * 
     * @param cycle
     * @throws Exception
     * @author cnc
     * @data 2014-05-28 15:29:18
     */
	public void checkAddMeb(IRequestCycle cycle) throws Exception {
		IData pageData = getData();
		IDataset rtDataset = CSViewCall.call(this, "SS.MultiPersonConsumptionSVC.checkGroupByMenber", pageData);
		
		System.out.println("rtDataset++++cnc=="+rtDataset);
		this.setAjax(rtDataset.getData(0));
	}

	/**
	 * 主号查询校验
	 * @param cycle
	 * @throws Exception
	 */
	public void loadInfo(IRequestCycle cycle) throws Exception {
		IData pageData = getData();
		String sn = pageData.getString("SERIAL_NUMBER");
		IData fmyParam = new DataMap();
		// 获取办理业务号码的信息
		IData loadInfo = CSViewCall.call(this, "SS.MultiPersonConsumptionSVC.QryMemberInfo", pageData).getData(0);
		System.out.println("loadInfo++++cnc=="+loadInfo);
		
		IDataset snbList = loadInfo.getDataset("SNB_LIST");
		System.out.println("snbList++++cnc=="+snbList);
		
		if (IDataUtil.isNotEmpty(snbList)) {
			setViceInfos(snbList);
			
			int valideMemberNumber = 0;
			// 查询有效成员的数量
			if (IDataUtil.isNotEmpty(loadInfo.getDataset("SNB_LIST")) && loadInfo.getDataset("SNB_LIST").size() > 0) {
				valideMemberNumber = loadInfo.getDataset("SNB_LIST").size();
			}
			
			int useableMemberNumber = 3 - (valideMemberNumber +1); // 可以添加成员数
			int usedMemberNumber = valideMemberNumber; // 已绑定成员号码数
			
			/*int useableMemberNumber = 0;
			int usedMemberNumber = 0;
			if (valideMemberNumber>0) {
				useableMemberNumber = 3 - (valideMemberNumber +1); // 可以添加成员数
				usedMemberNumber = valideMemberNumber; // 已绑定成员号码数
			}
			
			if (valideMemberNumber == 0) {
				useableMemberNumber = 3 - (valideMemberNumber +1); // 可以添加成员数
				usedMemberNumber = valideMemberNumber; // 已绑定成员号码数
			}*/
			
			fmyParam.put("USEABLE_MEMBER_NUMBER", useableMemberNumber);
			fmyParam.put("USED_MEMBER_NUMBER", usedMemberNumber);

			setFmyParam(fmyParam);

			IData returnParam = new DataMap();
			returnParam.put("VALIDE_MEBMER_NUMBER", valideMemberNumber);
			returnParam.put("USEABLE_MEMBER_NUMBER", useableMemberNumber);
			returnParam.put("USED_MEMBER_NUMBER", usedMemberNumber);

			if (IDataUtil.isNotEmpty(snbList)) {
				for (int i = 0; i < snbList.size(); i++) {
					IData mebMap = snbList.getData(i);
					if ( sn.equals(mebMap.getString("SERIAL_NUMBER_B"))) {
						returnParam.put("VRITUAL_NUM", "2");//2为副号
					}
				}
			}
			
			System.out.println("returnParam++++cnc=="+returnParam);
			
			this.setAjax(returnParam);
		}else {
			
			IData returnParam = new DataMap();
			returnParam.put("RESULT_CODE", loadInfo.getString("RESULT_CODE"));
			returnParam.put("RESULT_INFO", loadInfo.getString("RESULT_INFO"));
			
			System.out.println("RESULT_CODE++++cnc=="+returnParam);
			this.setAjax(returnParam);
		}
		
	}
    
	/**
	 * 提交
	 * @param cycle
	 * @throws Exception
	 */
	public void onTradeSubmit(IRequestCycle cycle) throws Exception {
		IDataset rtDataset = new DatasetList();

		IData pageData = getData();

		IData mebMap = getMebList(pageData);
		pageData.putAll(mebMap);
		
		// 被作为主订单的业务，顺序依次为：主号组网/添加成员（383）、删除多人约消组网成员（384）
		if (mebMap.containsKey("ADD_MEB_LIST")) {
			pageData.putAll(getData("FMY"));
			pageData.put("MEB_LIST", mebMap.getDataset("ADD_MEB_LIST"));
			rtDataset = CSViewCall.call(this, "SS.GroupCreateRegSVC.tradeReg", pageData);
		} else if (mebMap.containsKey("DEL_MEB_LIST")) {
			pageData.put("MEB_LIST", mebMap.getDataset("DEL_MEB_LIST"));
			rtDataset = CSViewCall.call(this, "SS.DelGroupNetMemberRegSVC.tradeReg", pageData);
		}

		this.setAjax(rtDataset);
	}
	
	/**
	 * 梳理受理数据
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData getMebList(IData input) throws Exception {
		IData mebMap = new DataMap();

		IDataset addMebList = new DatasetList(); // 新增成员号码列表
		IDataset delMebList = new DatasetList(); // 删除成员号码列表
		IDataset chgMebList = new DatasetList(); // 变更成员号码列表

		IDataset mebList = new DatasetList(input.getString("MEB_LIST", "[]"));
		if (IDataUtil.isNotEmpty(mebList)) {
			for (int i = 0; i < mebList.size(); i++) {
				// tag取值说明：""-未做改动，"0"-新增，"1"-删除
				String tag = mebList.getData(i).getString("tag");

				if ("0".equals(tag)) {
					addMebList.add(mebList.getData(i));
				} else if ("1".equals(tag)) {
					delMebList.add(mebList.getData(i));
				}
			}
		}

		if (IDataUtil.isNotEmpty(addMebList)) {
			mebMap.put("ADD_MEB_LIST", addMebList);
		}
		if (IDataUtil.isNotEmpty(delMebList)) {
			mebMap.put("DEL_MEB_LIST", delMebList);
		}

		return mebMap;
	}

    public abstract void setAppDiscntList(IDataset appDiscntList);

    public abstract void setDiscntList(IDataset discntList);

    public abstract void setFmyParam(IData fmyParam);

    public abstract void setProductList(IDataset productList);

    public abstract void setVerifyModeList(IDataset verifyModeList);

    public abstract void setViceDiscntList(IDataset viceDiscntList);

    public abstract void setViceInfo(IData viceInfo);

    public abstract void setViceInfos(IDataset viceInfos);

	public abstract void setMainInfo(IData mainInfo);

}
