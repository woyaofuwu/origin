package com.asiainfo.veris.crm.iorder.web.family.viewquery;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @author zhangxi
 *
 */
public abstract class Family360View extends PersonBasePage {

	/**
	 * 页面初始化
	 * @param cycle
	 * @throws Exception
	 */
	public void init(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IData cond = getData("cond", true);
        data.putAll(cond);
        setCondition(data);
    }

	/**
	 * 家庭综合资料详情查询
	 * @param cycle
	 * @throws Exception
	 */
	public void queryInfo(IRequestCycle cycle) throws Exception {
		IData param = getData();
        IData initParam = new DataMap();
        String serialNumberInput = param.getString("SERIAL_NUMBER_INPUT", "");
        String normalFamilyCheck = param.getString("NORMAL_FAMILY_CHECK", "");
        String eparchCode = getVisit().getStaffEparchyCode();
        String routeEparchyCode = getVisit().getStaffEparchyCode();

        param.put("SERIAL_NUMBER", serialNumberInput);

        initParam.put("SERIAL_NUMBER", serialNumberInput);
        initParam.put("EPARCHY_CODE", eparchCode);
        initParam.put("SERIAL_NUMBER_INPUT", serialNumberInput);
        initParam.put("NORMAL_FAMILY_CHECK", StringUtils.isBlank(normalFamilyCheck) ? "off" : normalFamilyCheck);

        if (StringUtils.isNotBlank(normalFamilyCheck) && "on".equals(normalFamilyCheck)) {
            param.put("REMOVE_TAG", "0");
        } else {
            param.remove("REMOVE_TAG");
        }

        if(StringUtils.isBlank(serialNumberInput)){
        	setCondition(initParam);
            setAjax("ALERT_INFO", "请输入服务号码！");
            return;
        }

        IDataset familyInfo = CSViewCall.call(this, "SS.GetFamily360ViewSVC.qryFamilyInfoBySerialNumber", param);

        if (StringUtils.isBlank(routeEparchyCode)) {
            routeEparchyCode = eparchCode;
        }
        initParam.put(Route.ROUTE_EPARCHY_CODE, routeEparchyCode);

        parserResults(param,familyInfo,initParam);

        setCondition(initParam);
	}

	// 分析结果
    private void parserResults(IData param, IDataset results, IData initParam) throws Exception {

    	if (IDataUtil.isEmpty(results)) {
            setAjax("ALERT_INFO", "用户号码[" + param.getString("SERIAL_NUMBER", "") + "]没有找到家庭资料！");
            return;
        }

    	IData result = results.first();

    	if (IDataUtil.isEmpty(result.getDataset("FAMILY_CUST_INFO_SET"))) {
            setAjax("ALERT_INFO", "用户号码[" + param.getString("SERIAL_NUMBER", "") + "]没有找到家庭资料！");
            return;
        }

    	IData familyCustInfo = result.getDataset("FAMILY_CUST_INFO_SET").first();
    	String familyUserId = result.getString("FAMILY_USER_ID");
    	String headCustId = familyCustInfo.getString("HEAD_CUST_ID");

    	initParam.put("FAMILY_USER_ID", familyUserId);
    	initParam.put("HEAD_CUST_ID", headCustId);

    	setFamilyInfo(familyCustInfo);

    }

    /**
     * 查询用户CRM信息
     * @param cycle
     * @throws Exception
     */
    public void queryCRMInfo(IRequestCycle cycle) throws Exception {
    	IData param = getData();

    	IData familyBaseInfo = CSViewCall.callone(this, "SS.GetFamily360ViewSVC.queryFamilyBaseInfo", param);

    	setFamilyBaseInfo(familyBaseInfo);
    }

    /**
     * 查询家庭账管信息
     * @param cycle
     * @throws Exception
     */
    public void queryAcctInfo(IRequestCycle cycle) throws Exception {

        IData param = getData();
        IData acctInfo = CSViewCall.callone(this, "SS.GetFamily360ViewSVC.queryFamilyAcctInfo", param);
        setAcctInfo(acctInfo);
    }

	public abstract void setAcctInfo(IData acctInfo);
	public abstract void setCondition(IData condition);
	public abstract void setFamilyInfo(IData familyInfo);
	public abstract void setFamilyBaseInfo(IData familyBaseInfo);

}
