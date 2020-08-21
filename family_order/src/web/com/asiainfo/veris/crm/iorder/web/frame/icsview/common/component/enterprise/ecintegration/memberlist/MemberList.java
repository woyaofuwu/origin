package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.ecintegration.memberlist;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.biz.view.BizTempComponent;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class MemberList extends BizTempComponent {
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception {
        boolean isAjax = isAjaxServiceReuqest(cycle);
        String jsFile = "scripts/iorder/icsserv/component/enterprise/ecintegration/memberlist/MemberList.js";

        if (isAjax) {
            includeScript(writer, jsFile, false, false);
        } else {
            getPage().addResAfterBodyBegin(jsFile, false, false);
        }

        String action = getPage().getData().getString("ACTION");
        if ("excuteImport".equals(action)) {
            excuteImport();
        } else if ("addMemberSingle".equals(action)) {
            addMemberSingle();
        } else if ("addTVMemberSingle".equals(action)) {
            addTVMemberSingle();
        } else if ("deleteMemberSn".equals(action)) {
        	deleteMemberSn();
        } else {
            if (StringUtils.isNotBlank(getEcUserId())) {
                queryMebList();
            }
        }
    }

	public void deleteMemberSn() throws Exception {
		String ecOfferCode = getPage().getData().getString("EC_OFFER_CODE");
 	    String serialNumber = getPage().getData().getString("SERIAL_NUMBER");
        String ecUserId = getPage().getData().getString("EC_USER_ID");
        IData result = null;
        
        String tradeTypeCode = StaticUtil.getStaticValue(this.getVisit(), "TD_B_ATTR_BIZ", new String[]
                { "ID", "ID_TYPE", "ATTR_OBJ", "ATTR_CODE" }, "ATTR_VALUE", new String[]
                { ecOfferCode, "P", BizCtrlType.DestoryMember, "TradeTypeCode" });
        
        //调用 业务规则校验 成员号码
		if ("8001".equals(ecOfferCode)) {
			 result = checkMebBaseInfoRule(ecUserId, serialNumber, tradeTypeCode, ecOfferCode);
		} else if ("2222".equals(ecOfferCode)) {
			 result = checkMebBaseInfoRule(ecUserId, serialNumber, tradeTypeCode, ecOfferCode);
		} else if ("8000".equals(ecOfferCode)) {
			 result = checkMebBaseInfoRule(ecUserId, serialNumber, tradeTypeCode, ecOfferCode);
		}
		getPage().setAjax(result);
	}

	/**
    *
    * 调用公共业务规则，校验成员号码
    *
    * */
	public IData checkMebBaseInfoRule(String grpUserId, String mebSN, String tradeTypeCode, String ecOfferCode) throws Exception {
		IData result = new DataMap();
		result.put("CHECK_RESULT", true);
		try {
			String svc = "CS.chkGrpMebDestory";

			IData conParam = new DataMap();

			if ("8001".equals(ecOfferCode)) // 融合V网
			{
				conParam.put("IF_CENTRETYPE", "2");
			}
			conParam.put("CHK_FLAG", "BaseInfo");
			conParam.put("USER_ID", grpUserId);
			conParam.put("SERIAL_NUMBER", mebSN);
			conParam.put("TRADE_TYPE_CODE", tradeTypeCode);
			conParam.put("PRODUCT_ID", ecOfferCode);
			CSViewCall.call(this, svc, conParam);
		} catch (Exception e) {
			result.put("CHECK_RESULT", false);
			result.put("CHECK_ERROR", e.getMessage().substring(e.getMessage().indexOf("`"), e.getMessage().lastIndexOf("`")));
		}
		return result;
	}
   
	public void addMemberSingle() throws Exception {
        IData memberInfo = new DataMap(getPage().getData().getString("MEMBER_INFO", ""));
        String checkSvcName = getPage().getData().getString("CHECK_SVC_NAME");
        String ecOfferCode = getPage().getData().getString("EC_OFFER_CODE");
        String custId = getPage().getData().getString("EC_CUST_ID");
        String ecUserId = getPage().getData().getString("EC_USER_ID");

        IData result = new DataMap();
        // 云WiFi 单个成员新增处理
        if ("380300".equals(ecOfferCode)) {
            String macSerialNumber = memberInfo.getString("MAC_NUMBER");
            if (StringUtils.isNotBlank(checkSvcName) && StringUtils.isNotBlank(macSerialNumber)) {
                IData input = new DataMap();
                // 变更所需参数，开通无
                String bmTempletId = getPage().getData().getString("BPM_TEMPLET_ID");
                memberInfo.put("BPM_TEMPLET_ID", bmTempletId);
                memberInfo.put("EC_USER_ID", ecUserId);

                input.put("CHECK_MEMBERINFO", memberInfo);
                input.put("PRODUCT_ID", ecOfferCode);
                input.put("CUST_ID", custId);
                input.put("IS_BATCH", false);
                IData checkResult = CSViewCall.callone(this, checkSvcName, input); // {"IMPORT_RESULT":true, "IMPORT_ERROR":"", ...}
                result.putAll(checkResult);
            }
		} else {
        	// 调用服务校验成员号码的业务合法性
        	String serialNumber = memberInfo.getString("SERIAL_NUMBER");
        	if (StringUtils.isNotBlank(checkSvcName) && StringUtils.isNotBlank(serialNumber)) {
        		IData input = new DataMap();
        		input.put("CHECK_MEMBERINFO", memberInfo);
        		input.put("PRODUCT_ID", ecOfferCode);
        		input.put("CUST_ID", custId);
        		input.put("EC_USER_ID", ecUserId);
        		input.put("IS_BATCH", false);
        		IData checkResult = CSViewCall.callone(this, checkSvcName, input); // {"IMPORT_RESULT":true, "IMPORT_ERROR":"", ...}
        		result.putAll(checkResult);
        	}
        }
        getPage().setAjax(result);
    }

    // 和商务TV单个成员新增
    public void addTVMemberSingle() throws Exception {
        IData memberInfo = new DataMap(getPage().getData().getString("MEMBER_INFO", ""));
        String checkSvcName = getPage().getData().getString("CHECK_SVC_NAME");
        String ecOfferCode = getPage().getData().getString("EC_OFFER_CODE");
        String bmTempletId = getPage().getData().getString("BPM_TEMPLET_ID");
        String serialNumber = getPage().getData().getString("SERIAL_NUMBER");
        String ecUserId = getPage().getData().getString("EC_USER_ID");
        IData result = new DataMap();

        // 调用服务校验成员号码的业务合法性
        String macNumber = memberInfo.getString("DEV_MAC_NUMBER");
        String snNumber = memberInfo.getString("DEV_SN_NUMBER");
        memberInfo.put("BPM_TEMPLET_ID", bmTempletId);
        memberInfo.put("SERIAL_NUMBER", serialNumber);
        memberInfo.put("EC_USER_ID", ecUserId);
        if (StringUtils.isNotBlank(checkSvcName) && StringUtils.isNotBlank(macNumber) && StringUtils.isNotBlank(snNumber)) {
            IData input = new DataMap();
            input.put("CHECK_MEMBERINFO", memberInfo);
            input.put("PRODUCT_ID", ecOfferCode);
            input.put("IS_BATCH", false);
            IData checkResult = CSViewCall.callone(this, checkSvcName, input);
            result.putAll(checkResult);
        }
        getPage().setAjax(result);
    }

    public void excuteImport() throws Exception {
        String fileId = getPage().getData().getString("MEBLIST_FILE");
        String importXmlPath = getPage().getData().getString("IMPORT_XML_PATH");
        String checkSvcName = getPage().getData().getString("CHECK_SVC_NAME");
        String ecOfferCode = getPage().getData().getString("EC_OFFER_CODE");
        String custId = getPage().getData().getString("EC_CUST_ID");
        String ecUserId = getPage().getData().getString("EC_USER_ID","");

        ImpExpUtil.getImpExpManager().getFileAction().setVisit(this.getVisit());
        IData fileData = ImpExpUtil.beginImport(null, fileId, ExcelConfig.getSheets(importXmlPath));

        // 失败数据
        IDataset failds = new DatasetList();
        failds.addAll(((IDataset[]) fileData.get("error"))[0]);

        // 成功数据
        IDataset succds = new DatasetList();

        succds.addAll(((IDataset[]) fileData.get("right"))[0]);

        // 调用服务校验成员号码的业务合法性
        if (StringUtils.isNotBlank(checkSvcName) && succds.size() > 0) {
            IData input = new DataMap();
            input.put("PRODUCT_ID", ecOfferCode);
            input.put("CHECK_MEMBERLIST", succds);
            input.put("EC_USER_ID", ecUserId);
            input.put("BPM_TEMPLET_ID", getPage().getData().getString("BPM_TEMPLET_ID", ""));
            input.put("CUST_ID", custId);
            input.put("IS_BATCH", true);
            IData checkResult = CSViewCall.callone(this, checkSvcName, input);
            IDataset failList = checkResult.getDataset("FAIL_LIST");
            if (IDataUtil.isNotEmpty(failList)) {
                failds.addAll(failList);
                succds = checkResult.getDataset("SUCC_LIST");
            }
        }
        int succdNum = succds.size();
        int faildNum = failds.size();

        IData ajaxData = new DataMap();
        if (faildNum > 0) {
            String fileName = "import_failed.xls";
            String exportXmlPath = getPage().getData().getString("EXPORT_XML_PATH");
            String faildUrl = exportExcel(fileName, new IDataset[] { failds }, exportXmlPath);
            ajaxData.put("FAILD_FILE_URL", faildUrl);

        }
        ajaxData.put("SUCCESS_NUM", succdNum);
        ajaxData.put("FAIL_NUM", faildNum);
        ajaxData.put("TOTAL_NUM", succdNum + faildNum);
        ajaxData.put("SUCC_LIST", succds);

        getPage().setAjax(ajaxData);
    }

    public void queryMebList() throws Exception {
        String bmTempletId = getBpmTempletId();
        String serialNumber = getSerialNumber();
        IData bpmData = new DataMap();
        bpmData.put("BPM_TEMPLET_ID", bmTempletId);
        bpmData.put("SERIAL_NUMBER", serialNumber);
        bpmData.put("EC_USER_ID", getEcUserId());
        bpmData.put("CUST_ID", getEcCustId());
        setBpmData(bpmData);
        if ("DelMeb".equals(getOperType())) {
            String ecUserId = getEcUserId();
            IDataset delMebList = getDelMebList();
            String initMebSvcName = getInitMebSvc();
            if (StringUtils.isBlank(initMebSvcName)) {
                return;
            }
            IData data = new DataMap();
            data.put("USER_ID_A", ecUserId);
            data.put("ROUTE_EPARCHY_CODE", "0898");
            IDataset resultList = CSViewCall.call(this, initMebSvcName, data);
            IDataset mebList = new DatasetList();
            for (int i = 0, size = resultList.size(); i < size; i++) {
                IData meb = buildMebInfo(resultList.getData(i));
                removeDelMember(delMebList, mebList, meb);
            }
            setMebList(mebList);
            setMebDelList(delMebList);
        } else {
            setMebList(new DatasetList());
        }
    }

    private IData buildMebInfo(IData mebInfo) throws Exception {
        IData meb = new DataMap();
        String ecOfferCode = getEcOfferCode();
        if ("2222".equals(ecOfferCode) || "8001".equals(ecOfferCode) || "8000".equals(ecOfferCode)) {
            meb.put("SERIAL_NUMBER", mebInfo.getString("SERIAL_NUMBER_B"));
            meb.put("SHORT_CODE", mebInfo.getString("SHORT_CODE"));
            meb.put("OPER_CODE", "3"); // 3-不变

        } else if ("380300".equals(ecOfferCode)) {
            // esp产品 云WiFi安审版成员设置
            if (mebInfo.getBoolean("FLAG")) {
                meb.put("MAC_NUMBER", mebInfo.getString("SERIAL_NUMBER"));
            } else {
                meb.put("MAC_NUMBER", mebInfo.getString("MAC_NUMBER"));
                meb.put("SERIAL_NUMBER", mebInfo.getString("PHONE_NUMBER"));
                meb.put("OPER_CODE", "3");
            }
            return meb;
        } else if ("380700".equals(ecOfferCode)) {
            // esp产品 和商务TV成员设置
            if (mebInfo.getBoolean("FLAG")) {
                meb.put("DEV_MAC_NUMBER", mebInfo.getString("SERIAL_NUMBER"));
            } else {
                meb.put("DEV_MAC_NUMBER", mebInfo.getString("DEV_MAC_NUMBER"));
                meb.put("DEV_SN_NUMBER", mebInfo.getString("DEV_SN_NUMBER"));
                meb.put("OPER_CODE", "3");
            }
            return meb;
        } else {
            meb.putAll(mebInfo);
        }

        String sn = meb.getString("SERIAL_NUMBER", meb.getString("SERIAL_NUMBER_B"));

        // 根据手机号码，查询成员用户信息
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", sn);
        IData userInfo = CSViewCall.callone(this, "CS.UcaInfoQrySVC.qryUserInfoBySn", param);
        if (IDataUtil.isEmpty(userInfo)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据手机号码 【SERIAL_NUMBER=" + sn + "】没有获取到客户信息！");
        }

        meb.put("CUST_ID", userInfo.getString("CUST_ID"));
        meb.put("USER_ID", userInfo.getString("USER_ID"));

        return meb;
    }

    private void removeDelMember(IDataset delMebList, IDataset mebList, IData meb) throws Exception {
        String ecOfferCode = getEcOfferCode();

        String serNum = "";
        String key = "";
        if ("2222".equals(ecOfferCode) || "8001".equals(ecOfferCode) || "8000".equals(ecOfferCode)) {
            serNum = meb.getString("SERIAL_NUMBER");
            key = "SERIAL_NUMBER";
        } else if ("380300".equals(ecOfferCode)) {
            serNum = meb.getString("MAC_NUMBER");
            key = "MAC_NUMBER";
        } else if ("380700".equals(ecOfferCode)) {
            serNum = meb.getString("DEV_MAC_NUMBER");
            key = "DEV_MAC_NUMBER";
        }

        boolean flag = true;
        if (IDataUtil.isNotEmpty(delMebList)) {
            for (int j = 0; j < delMebList.size(); j++) {
                IData delMebInfo = delMebList.getData(j);
                String delSerNum = delMebInfo.getString(key);
                if (serNum.equals(delSerNum)) {
                    flag = false;
                }
            }
        }
        if (flag) {
            mebList.add(meb);
        }
    }

    public String exportExcel(String fileName, IDataset[] exportInfos, String xmlPath) throws Exception {
        // 创建导出需要的固定参数
        IData params = new DataMap();
        params.put("posX", "0");
        params.put("posY", "0");
        params.put("ftpSite", "order");
        ImpExpUtil.getImpExpManager().getFileAction().setVisit(this.getVisit());
        // 将数据写入文件并返回文件ID
        String fileId = ImpExpUtil.beginExport(null, params, fileName, exportInfos, ExcelConfig.getSheets(xmlPath));
        // 获取文件下载的URL
        String url = ImpExpUtil.getDownloadPath(fileId, fileName);

        return url;
    }

    // private IDataset getMemberTypeList() throws Exception
    // {
    // IDataset memberTypeList = new DatasetList();
    // IData type1 = new DataMap();
    // type1.put("TEXT", "批量");
    // type1.put("VALUE", "0");
    // IData type2 = new DataMap();
    // type2.put("TEXT", "单条");
    // type2.put("VALUE", "1");
    // return memberTypeList;
    // }

    public abstract String getEcOfferCode() throws Exception;

    public abstract String getEcUserId() throws Exception;

    public abstract String getEcCustId() throws Exception;

    public abstract String getOperType() throws Exception;

    public abstract String getInitMebSvc() throws Exception; // 查询集团成员服务

    public abstract String getBpmTempletId() throws Exception; // 获取流程名称BPM_TEMPLET_ID

    public abstract String getSerialNumber() throws Exception; // 获取服务号码

    public abstract void setMebList(IDataset mebList) throws Exception;

    public abstract void setMebDelList(IDataset mebList) throws Exception;

    public abstract void setBpmData(IData data) throws Exception;

    public abstract IDataset getDelMebList() throws Exception;

}
