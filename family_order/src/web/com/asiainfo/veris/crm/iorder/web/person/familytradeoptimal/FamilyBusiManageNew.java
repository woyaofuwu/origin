
package com.asiainfo.veris.crm.iorder.web.person.familytradeoptimal;

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

public abstract class FamilyBusiManageNew extends PersonBasePage
{
	public void init(IRequestCycle cycle) throws Exception {

	}

	public void getVerifyModeList(IRequestCycle cycle) throws Exception {
		IDataset verifyModeList = new DatasetList();
		// 获取校验方式
		IDataset tmpList = StaticUtil.getStaticList("FAMILY_VERIFY_MODE");
		// 判断是否具有“免密码”权限
		boolean isPriv = StaffPrivUtil.isPriv(getVisit().getStaffId(), "FAMILY_NO_VERIFY", "1");
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
	 * 添加副号码的校验（多个）
	 *
	 * @param cycle
	 * @throws Exception
	 * @author
	 * @data
	 */
	public void checkAddMebMul(IRequestCycle cycle) throws Exception {
		IData pageData = getData();
		IDataset rtDataset = CSViewCall.call(this, "SS.FamilyCreateSVC.checkAddMebMul", pageData);
		IData ajaxResult=new DataMap();
		ajaxResult.put("MEB_CHECK_LIST",rtDataset);
		this.setAjax(ajaxResult);
	}
    /**
     * 添加副号码的校验
     * 
     * @param cycle
     * @throws Exception
     * @author zhouwu
     * @data 2014-05-28 15:29:18
     */
	public void checkAddMeb(IRequestCycle cycle) throws Exception {
		IData pageData = getData();
		IDataset rtDataset = CSViewCall.call(this, "SS.FamilyCreateSVC.checkAddMeb", pageData);
		this.setAjax(rtDataset.getData(0));
	}

	public void loadInfo(IRequestCycle cycle) throws Exception {
		IData pageData = getData();
		String sn = pageData.getString("SERIAL_NUMBER");
		IData fmyParam = new DataMap();
		// 获取办理业务号码的信息
		IData loadInfo = CSViewCall.call(this, "SS.FamilyCreateSVC.loadFamilyCreateInfoNew", pageData).getData(0);
		fmyParam.putAll(loadInfo);
		// 获取产品列表信息并传回页面
		IDataset productList = loadInfo.getDataset("PRODUCT_LIST");
		setProductList(productList);

		if (productList.size() == 1) {
			String productId = productList.getData(0).getString("PRODUCT_ID");
			fmyParam.put("PRODUCT_ID", productId);

			// 获取优惠信息列表并传回页面
			IDataset discntList = loadInfo.getDataset("DISCNT_LIST");
			setDiscntList(discntList);

			if (discntList.size() == 1) {
				String discntCode = discntList.getData(0).getString("DISCNT_CODE");
				fmyParam.put("DISCNT_CODE", discntCode);
			}

			// 获取副号优惠名称
			IDataset viceDiscntList = loadInfo.getDataset("VICE_DISCNT_LIST");

			if (viceDiscntList.size() == 1) {
				String viceDiscntCode = viceDiscntList.getData(0).getString("DISCNT_CODE");
				fmyParam.put("VICE_DISCNT_CODE", viceDiscntCode);
			}

			setViceDiscntList(viceDiscntList);
			setAppDiscntList(null);// loadInfo.getDataset("APP_DISCNT_LIST")
		}

		if (StringUtils.isNotBlank(loadInfo.getString("PRODUCT_ID"))) {
			fmyParam.put("PRODUCT_ID", loadInfo.getString("PRODUCT_ID"));
			fmyParam.put("SERVICE_ID", loadInfo.getString("SERVICE_ID"));
			fmyParam.put("CREATE_FLAG", "true");
		}

		IDataset useableShortCodes = getUseableShortCodes(loadInfo);

		IDataset mainList = loadInfo.getDataset("MAIN_LIST");
		if (IDataUtil.isNotEmpty(mainList)) {
			IData mainMap = mainList.getData(0);

			IData tempShortCode = new DataMap();
			if (StringUtils.isNotBlank(mainMap.getString("SHORT_CODE"))) {
				tempShortCode.put("DATA_ID", mainMap.getString("SHORT_CODE"));
				tempShortCode.put("DATA_NAME", mainMap.getString("SHORT_CODE"));
			} else {
				tempShortCode.put("DATA_ID", "");
				tempShortCode.put("DATA_NAME", "");
			}
			IDataset shortCodes = new DatasetList();
			shortCodes.addAll(useableShortCodes);
			shortCodes.add(tempShortCode); // 短号下拉框数据源加上自身短号
			DataHelper.sort(shortCodes, "DATA_ID", 2);

			fmyParam.put("MAIN_SHORT_CODES", shortCodes);

			setMainInfo(mainMap);
		}

		IDataset mebList = loadInfo.getDataset("MEB_LIST");
		String authMainFlag = loadInfo.getString("MAIN_FLAG"); // 查询号码是否主卡：false-副卡，其他

		if (IDataUtil.isNotEmpty(mebList)) {
			for (int i = 0; i < mebList.size(); i++) {
				IData mebMap = mebList.getData(i);
				mebMap.put("DISPLAY", "");
				mebMap.put("DISABLED", false);
				if ("false".equals(authMainFlag)) {
					mebMap.put("DISABLED", true);
					if (sn.equals(mebMap.getString("SERIAL_NUMBER_B"))) {
						mebMap.put("DISPLAY", "");
					} else {
						mebMap.put("DISPLAY", "none");
					}
				}

				IData tempShortCode = new DataMap();
				if (StringUtils.isNotBlank(mebMap.getString("SHORT_CODE"))) {
					tempShortCode.put("DATA_ID", mebMap.getString("SHORT_CODE"));
					tempShortCode.put("DATA_NAME", mebMap.getString("SHORT_CODE"));
				} else {
					tempShortCode.put("DATA_ID", "");
					tempShortCode.put("DATA_NAME", "");
				}
				IDataset shortCodes = new DatasetList();
				shortCodes.addAll(useableShortCodes);
				shortCodes.add(tempShortCode); // 短号下拉框数据源加上自身短号
				DataHelper.sort(shortCodes, "DATA_ID", 2);
				mebMap.put("USEABLE_SHORT_CODES", shortCodes);
			}
		}

		setViceInfos(mebList);

		String isJwt = loadInfo.getString("ISJWT_FLAG");

		int valideMemberNumber = 0;
		// 查询有效成员的数量
		if (IDataUtil.isNotEmpty(loadInfo.getDataset("MEB_LIST")) && loadInfo.getDataset("MEB_LIST").size() > 0) {
			valideMemberNumber = loadInfo.getDataset("MEB_LIST").size();
		}

		int useableMemberNumber = 9 - valideMemberNumber; // 可以添加成员数
		int usedMemberNumber = valideMemberNumber; // 已绑定成员号码数

		fmyParam.put("USEABLE_MEMBER_NUMBER", useableMemberNumber);
		fmyParam.put("USED_MEMBER_NUMBER", usedMemberNumber);

		setFmyParam(fmyParam);

		IData returnParam = new DataMap();
		returnParam.put("isJwt", isJwt);
		returnParam.put("VALIDE_MEBMER_NUMBER", valideMemberNumber);
		returnParam.put("USEABLE_MEMBER_NUMBER", useableMemberNumber);
		returnParam.put("USED_MEMBER_NUMBER", usedMemberNumber);
		returnParam.put("USEABLE_SHORT_CODES", useableShortCodes);
		returnParam.put("AUTH_MAIN_FLAG", authMainFlag); // 是否主卡标记loadInfo
		returnParam.put("MAIN_SERIAL_NUMBER", loadInfo.getString("MAIN_SERIAL_NUMBER")); // 是否主卡标记

		this.setAjax(returnParam);
	}
    
	public void obtainShortCodes(IRequestCycle cycle) throws Exception {
		IData pageData = getData();
		String existShortCodes = pageData.getString("EXIST_SHORT_CODES", "");

		IData param = new DataMap();
		param.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
		IDataset shortCodes = CSViewCall.call(this, "SS.FamilyCreateSVC.obtainShortCodes", param);
		if (IDataUtil.isNotEmpty(shortCodes)) {
			Map<String, IData> shortContainer = new HashMap<String, IData>();
			for (int i = 0, size = shortCodes.size(); i < size; i++) {
				shortContainer.put(shortCodes.getData(i).getString("DATA_ID"), shortCodes.getData(i));
			}

			if (existShortCodes != null && !existShortCodes.trim().equals("")) {
				String[] existShortCodesArr = existShortCodes.split(";");
				for (int i = 0, size = existShortCodesArr.length; i < size; i++) {
					if (existShortCodesArr[i] != null && !existShortCodesArr[i].trim().equals("")) {
						if (shortContainer.containsKey(existShortCodesArr[i])) {
							shortCodes.remove(shortContainer.get(existShortCodesArr[i]));
						}
					}
				}
			}
		} else {
			shortCodes = new DatasetList();
		}

		IData result = new DataMap();
		result.put("SHORT_CODES", shortCodes);

		setAjax(result);
	}

	/**
	 * 获取可用短号
	 * @param loadFamilyCreateInfo 亲亲网信息
	 * @return shortCodes 可用短号
	 * @throws Exception
	 * @author zhaohj3
	 * @date 2018-4-19 15:48:09
	 */
	public IDataset getUseableShortCodes(IData loadFamilyCreateInfo) throws Exception {

		IDataset mebList = loadFamilyCreateInfo.getDataset("MEB_LIST"); // 副号信息

		IDataset usedShortCodes = new DatasetList(); // 已经使用了的短号

		IData usedShortCode = new DataMap();

		String mainShortCode = loadFamilyCreateInfo.getString("SHORT_CODE");
		if (StringUtils.isNotBlank(mainShortCode)) {
			usedShortCode.put("DATA_ID", mainShortCode);
			usedShortCode.put("DATA_NAME", mainShortCode);
			usedShortCodes.add(usedShortCode); // 已经使用了的主号短号
		}

		if (IDataUtil.isNotEmpty(mebList)) {
			for (int i = 0; i < mebList.size(); i++) {
				IData mebMap = mebList.getData(i);
				usedShortCode = new DataMap();
				usedShortCode.put("DATA_ID", mebMap.getString("SHORT_CODE"));
				usedShortCode.put("DATA_NAME", mebMap.getString("SHORT_CODE"));
				usedShortCodes.add(usedShortCode); // 已经使用了的副号短号
			}
		}

		IData param = new DataMap();
		param.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
		IDataset shortCodes = CSViewCall.call(this, "SS.FamilyCreateSVC.obtainShortCodes", param); // 短号集

		if (IDataUtil.isNotEmpty(usedShortCodes)) {
			for (int i = 0; i < usedShortCodes.size(); i++) {
				shortCodes = IDataUtil.removeFilter(shortCodes, "DATA_ID=" + usedShortCodes.getData(i).getString("DATA_ID")); // 空闲短号，过滤掉已使用的短号
			}
		}

		return shortCodes;
	}

	/**
	 * 提交
	 * @param cycle
	 * @throws Exception
	 */
	public void onTradeSubmit(IRequestCycle cycle) throws Exception {
		IDataset rtDataset = new DatasetList();

		IData pageData = getData();
		String tradeOption = pageData.getString("TRADE_OPTION", "");

		if (tradeOption.contains("DESTROY")) {
			pageData.putAll(getData("FMY"));
			pageData.put("IN_TAG", "0");// 0表示前台办理
			rtDataset = CSViewCall.call(this, "SS.DestroyFamilyRegSVC.tradeReg", pageData);
		} else {
			IData mebMap = getMebList(pageData);
			pageData.putAll(mebMap);

			// 被作为主订单的业务，顺序依次为：组建亲亲网/增加成员（283）、删除亲亲网成员（284）、变更亲亲网短号（287）
			if (mebMap.containsKey("ADD_MEB_LIST")) {
				pageData.putAll(getData("FMY"));
				pageData.put("IN_TAG_NEW", "0");// 0表示界面互联网前台办理
				pageData.put("MEB_LIST", mebMap.getDataset("ADD_MEB_LIST"));
				rtDataset = CSViewCall.call(this, "SS.FamilyCreateRegSVC.tradeReg", pageData);
			} else if (mebMap.containsKey("DEL_MEB_LIST")) {
				pageData.put("MEB_LIST", mebMap.getDataset("DEL_MEB_LIST"));
				rtDataset = CSViewCall.call(this, "SS.DelFamilyNetMemberRegSVC.tradeReg", pageData);
			} else if (mebMap.containsKey("CHG_MEB_LIST")) {
				pageData.put("MEB_LIST", mebMap.getDataset("CHG_MEB_LIST"));
				rtDataset = CSViewCall.call(this, "SS.FamilyShortCodeBusiRegSVC.tradeReg", pageData);
			}
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
				// tag取值说明：""-未做改动，"0"-新增，"1"-删除，"2"-修改
				String tag = mebList.getData(i).getString("tag");

				if ("0".equals(tag)) {
					addMebList.add(mebList.getData(i));
				} else if ("1".equals(tag)) {
					delMebList.add(mebList.getData(i));
				} else if ("2".equals(tag)) {
					chgMebList.add(mebList.getData(i));
				}
			}
		}

		if (IDataUtil.isNotEmpty(addMebList)) {
			mebMap.put("ADD_MEB_LIST", addMebList);
		}
		if (IDataUtil.isNotEmpty(delMebList)) {
			mebMap.put("DEL_MEB_LIST", delMebList);
		}
		if (IDataUtil.isNotEmpty(chgMebList)) {
			mebMap.put("CHG_MEB_LIST", chgMebList);
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
