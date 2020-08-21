
package com.asiainfo.veris.crm.iorder.web.person.view360;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

public abstract class User360ViewNew extends PersonBasePage {

    public IData errMsg = new DataMap();

    public void init(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IData cond = getData("cond", true);
        data.putAll(cond);
        setCondition(data);
    }

    // 分析结果
    private void parserResults(IData param, IDataset results, IData initParam) throws Exception {
        initParam.remove("MANY_RECORDS");
        if (IDataUtil.isEmpty(results)) {
            initParam.put("MANY_RECORDS", "0");
            setAjax("ALERT_INFO", "用户号码[" + param.getString("SERIAL_NUMBER", "") + "]没有找到用户资料！");
        } else if (results.size() == 1) {
            IData userInfo = results.getData(0);
            String netTypeCode = userInfo.getString("NET_TYPE_CODE", "00");
            initParam.put("NET_TYPE_CODE", netTypeCode);

            // 查询集团客户信息
            String serialNumber = param.getString("SERIAL_NUMBER", "");
            String userId = userInfo.getString("USER_ID", "");
            IData groupParam = new DataMap();
            groupParam.put("USER_ID", userId);
            groupParam.put("SERIAL_NUMBER", serialNumber);
            IDataset groupInfos = CSViewCall.call(this, "SS.GetUser360ViewSVC.queryGroupName", groupParam);
            IData groupInfo;
            if (IDataUtil.isNotEmpty(groupInfos)) {
                groupInfo = groupInfos.getData(0);
                String groupName = groupInfo.getString("GROUP_CUST_NAME");
                userInfo.put("GROUP_CUST_NAME", groupName);
            }

            // 判断是否是无线固话用户
            String isTDUser = PersonConst.TD_NET_TYPE_CODE.equals(netTypeCode) ? "1" : "0";
            userInfo.put("IS_TD_USER", isTDUser);

            // 判断是否是无手机宽带用户
            String isKDUser = isKDUserBySerialNumber(serialNumber);
            userInfo.put("IS_KD_USER", isKDUser);

            setUserInfo(userInfo);
            initParam.put("MANY_RECORDS", "0"); //查询成功

            // 用户360查询台帐记录
            IData userParam = new DataMap();
            userParam.put("USER_ID", userId);
            userParam.put("NET_TYPE_CODE", netTypeCode);
            userParam.put("SERIAL_NUMBER", serialNumber);
            userParam.put("EPARCHY_CODE", userInfo.getString("EPARCHY_CODE", ""));
            userParam.put("CITY_CODE", userInfo.getString("CITY_CODE", ""));
            userParam.put("PRODUCT_ID", userInfo.getString("PRODUCT_ID", ""));
            userParam.put("BRAND_CODE", userInfo.getString("BRAND_CODE", ""));
            userParam.put("CUST_ID", userInfo.getString("CUST_ID", ""));
            CSViewCall.call(this, "SS.GetUser360ViewSVC.writeTradeQueryLog", userParam);
        } else {
            initParam.put("MANY_RECORDS", "1"); // 页面弹出选择框
        }
    }

    /**
     * 根据SerialNumber判断该用户是否是无手机宽带用户
     */
    private String isKDUserBySerialNumber(String serialNumber) throws Exception {
        IData inParam = new DataMap();
        inParam.put("SERIAL_NUMBER", serialNumber.startsWith("KD_") ? serialNumber : "KD_" + serialNumber);
        IDataset userInfo = CSViewCall.call(this, "SS.NoPhoneWideChangeProdSVC.checkIfNoPhoneWideUser", inParam);
        return IDataUtil.isNotEmpty(userInfo) ? "1" : "0";
    }

    /**
     * 查询用户CRM信息
     * @param cycle
     * @throws Exception
     */
    public void queryCRMInfo(IRequestCycle cycle) throws Exception {
        IData param = getData();
        IData baseInfo = CSViewCall.callone(this, "SS.GetUser360ViewSVC.queryPersonBaseInfo", param);
        IDataset foregiftInfo = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryForegiftInfo", param); // 查询用户押金信息

        if (IDataUtil.isNotEmpty(baseInfo)) {
            if ("4".equals(baseInfo.getString("USER_STATE_CODESET", ""))) {
                IDataset stopUsers = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryUserIfNotRealName", param);
                if (stopUsers != null && stopUsers.size() > 0) {
                    baseInfo.put("USER_STATE_CODESET", "HT");
                }
            }
            /*
             * REQ201608260010 关于非实名用户关停改造需求
             * 20160912 chenxy3
             * 欠费停机转“非实名制全停”
             */
            if ("5".equals(baseInfo.getString("USER_STATE_CODESET", ""))) {
                IDataset stopUsers = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryUserIfAllStop", param);
                if (stopUsers != null && stopUsers.size() > 0) {
                    baseInfo.put("USER_STATE_CODESET", "AT");
                }
            }
        }

        setBaseInfo(baseInfo);
        setForegiftInfo(foregiftInfo);
    }

    /**
     * 查询用户账管信息
     * @param cycle
     * @throws Exception
     */
    public void queryAcctInfo(IRequestCycle cycle) throws Exception {
        IData param = getData();
        IData acctInfo = CSViewCall.callone(this, "SS.GetUser360ViewSVC.queryPersonAcctInfo", param);
        setAcctInfo(acctInfo);
    }

    /**
     * 小篮筐展示
     *
     * @param cycle
     * @throws Exception
     */
    public void getHintInfo(IRequestCycle cycle) throws Exception {
        IData data = getData();
        data.put("TRADE_STAFF_ID", this.getVisit().getStaffId());
        IDataset hintInfo = CSViewCall.call(this, "SS.HintInfoSVC.getHintInfo", data);
        if (IDataUtil.isNotEmpty(hintInfo)) {
            setAjax(hintInfo.getData(0));
        }
    }

    /**
     * 开始进入客户资料综合查询视图
     *
     * @param cycle
     * @throws Exception
     * @author huanghui@asiainfo.com
     */
    public void queryInfo(IRequestCycle cycle) throws Exception {
        boolean flag2 = BizEnv.getEnvBoolean("crm_realtimemarketing_webswitch");

        IData param = getData();
        IData initParam = new DataMap();
        String serialNumberInput = param.getString("SERIAL_NUMBER_INPUT", "");
        String simNumberInput = param.getString("SIM_NUMBER_INPUT", "");
        String normalUserCheck = param.getString("NORMAL_USER_CHECK", "");
        String eparchCode = getVisit().getStaffEparchyCode();
        String routeEparchyCode = getVisit().getStaffEparchyCode();
        param.put("SERIAL_NUMBER", serialNumberInput);
        param.put("SIM_NUMBER", simNumberInput);
        initParam.put("SERIAL_NUMBER", serialNumberInput);
        initParam.put("SIM_CHECK", param.getString("SIM_CHECK", ""));
        initParam.put("SIM_NUMBER", simNumberInput);
        initParam.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        initParam.put("NORMAL_USER_CHECK", StringUtils.isBlank(normalUserCheck) ? "off" : normalUserCheck);
        initParam.put("CRM_REALTIMEMARKETING_WEBSWITCH", flag2 ? "1" : "0");
        if (StringUtils.isNotBlank(normalUserCheck) && "on".equals(normalUserCheck)) {
            param.put("REMOVE_TAG", "0"); // on是点击复选框后的默认值
        } else {
            param.remove("REMOVE_TAG");
        }

        String flag = param.getString("FLAG", ""); // FLAG标志来自通过手机号码查询出多条记录弹出页面
        if (StringUtils.isBlank(flag)) {
            param.remove("CUST_ID");
        }

        // 手机号码查询用户
        if (StringUtils.isNotBlank(param.getString("SERIAL_NUMBER"))) {
            String serialNumber = param.getString("SERIAL_NUMBER", "");
            if (StringUtils.isBlank(serialNumber)) {
                setCondition(initParam);
                setAjax("ALERT_INFO", "请输入服务号码！");
                return;
            }

            IDataset userInfo = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryUserInfoBySerialNumber", param);

            if (StringUtils.isBlank(routeEparchyCode)) {
                routeEparchyCode = eparchCode;
            }

            initParam.put(Route.ROUTE_EPARCHY_CODE, routeEparchyCode);
            parserResults(param, userInfo, initParam);

            // 判断是否是铁通号码，综合查询页面tab清单信息子页面嵌入不同的账管页面 Add By HuangHui 2014-09-06
            String netTypeCode = initParam.getString("NET_TYPE_CODE", "");
            if ("11".equals(netTypeCode) || "12".equals(netTypeCode)
                    || "13".equals(netTypeCode) || "14".equals(netTypeCode)) {
                initParam.put("BILL_LOG_INFO_PAGE", "cdr.TTBillQry");
            }

            if (!param.getString("CALLED_SN", "").equals("")) {
                param.put("SERIAL_NUMBER_B", param.getString("CALLED_SN", ""));
            }
            if (getVisit().getInModeCode().equals("1") && !"".equals(param.getString("SERIAL_NUMBER_B", "")) && !"".equals(param.getString("SERIAL_NUMBER", ""))) {
                // 查询接入号码是否是客户经理，接入号码是否是服务号码的客户经理，接入号码是否是一卡双号
                IDataset serialNumberBInfo = CSViewCall.call(this, "SS.GetUser360ViewSVC.qrySerialNumberBInfo", param);
                initParam.put("IS_VIP_MANAGER", serialNumberBInfo.getData(0).getString("IS_VIP_MANAGER"));
                initParam.put("VIP_MANAGER_PASS", serialNumberBInfo.getData(0).getString("VIP_MANAGER_PASS"));
                initParam.put("IS_BOTH_SN", serialNumberBInfo.getData(0).getString("IS_BOTH_SN"));
                initParam.put("SERIAL_NUMBER_B", param.getString("SERIAL_NUMBER_B", ""));
            }
            setCondition(initParam);
        } else {
            String simNumber = param.getString("SIM_NUMBER", "");
            if (StringUtils.isNotBlank(simNumber)) {
                IDataset qrySerialNumberBySim = CSViewCall.call(this, "SS.GetUser360ViewSVC.qrySerialNumberBySim", param);
                IDataset serialNumberDataset;

                /**
                 * REQ201701040013客户资料综合查询界面增加可以根据白卡号查询
                 * @author zhuoyingzhi
                 * @date 20170228
                 */
                IData checkResult = checkWhiteSIM(qrySerialNumberBySim, simNumber);
                String resultCode = checkResult.getString("resultCode");
                if ("0".equals(resultCode)) {
                    // 第一次查询用户信息不存在，再调用资源接口，查询到sim卡
                    String resultWhiteSimNumber = checkResult.getString("resultWhiteSimNumber", "");

                    IData paramNew = new DataMap();
                    // 新的sim卡
                    paramNew.put("SIM_NUMBER", resultWhiteSimNumber);
                    // 再通过新的sim卡查询用户信息
                    serialNumberDataset = CSViewCall.call(this, "SS.GetUser360ViewSVC.qrySerialNumberBySim", paramNew);

                } else if ("2".equals(resultCode)) {
                    // 第一次查询用户信息不存在，再调用资源接口，查询不到新sim卡
                    serialNumberDataset = new DatasetList();
                } else {
                    // 第一次查询就查询到用户信息了
                    serialNumberDataset = qrySerialNumberBySim;
                }
                /***************  END  ***************/

                if (serialNumberDataset.size() <= 0) {
                    setCondition(initParam);
                    setAjax("ALERT_INFO", "通过SIM卡号码没有找到服务号码！");
                    return;
                }
                IData serialNumberData = serialNumberDataset.getData(0);
                String serialNumber = serialNumberData.getString("SERIAL_NUMBER", "");
                if (serialNumber == null || "".equals(serialNumber)) {
                    setCondition(initParam);
                    setAjax("ALERT_INFO", "通过SIM卡号码没有找到服务号码！");
                    return;
                }
                // step3：根据SERIAL_NUMBER查询用户资料
                param.remove("SERIAL_NUMBER");
                param.put("SERIAL_NUMBER", serialNumber);
                routeEparchyCode = serialNumberData.getString("EPARCHY_CODE");

                if (StringUtils.isBlank(routeEparchyCode)) {
                    routeEparchyCode = eparchCode;
                } else {
                    //if (!routeEparchyCode.equals(eparchCode) && !"07XX".equals(eparchCode) && !"HNAN".equals(eparchCode))
                    if (!routeEparchyCode.equals(eparchCode)) {
                        if (!StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "SYS_USER_QRY")) {
                            setCondition(initParam);
                            setAjax("ALERT_INFO", "您没有权限查询异地用户！");
                            return;
                        }
                    }
                }
                initParam.put(Route.ROUTE_EPARCHY_CODE, routeEparchyCode);
                IDataset results = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryUserInfoBySerialNumber", param);
                parserResults(param, results, initParam);
                initParam.put("SERIAL_NUMBER", serialNumber);
                setCondition(initParam);
            }
        }
    }

    /**
     * REQ201701040013客户资料综合查询界面增加可以根据白卡号查询
     *
     * @param qrySerialNumberBySim
     * @param simNumber
     * @return
     * @throws Exception
     * @author zhuoyingzhi
     * @date 20170228
     */
    private IData checkWhiteSIM(IDataset qrySerialNumberBySim, String simNumber) throws Exception {
        IData result = new DataMap();
        // -1：默认值  0：通过白卡sim获取sim卡号标识  1：第一次查询就获取用户信息  2：第一次查询获取不到用户信息，第二次调用接口还是获取不到用户信息
        result.put("resultCode", "-1");
        result.put("resultWhiteSimNumber", ""); // 通过白卡sim获取sim卡号

        IData param = new DataMap();
        // 白卡信息
        param.put("SIMNUMBER", simNumber);
        if (IDataUtil.isEmpty(qrySerialNumberBySim)) {
            //  无用户信息，调用新接口：界面输入的“SIM卡号码”当作白卡卡号调资源接口（4.2.1节新增的接口）得到SIM卡号
            qryEmptycardInfo(param, result);
        } else {
            IData serialNumberData = qrySerialNumberBySim.getData(0);
            String serialNumber = serialNumberData.getString("SERIAL_NUMBER", "");
            if ("".equals(serialNumber) || serialNumber == null) {
                // 无用户信息，调用新接口：界面输入的“SIM卡号码”当作白卡卡号调资源接口（4.2.1节新增的接口）得到SIM卡号
                qryEmptycardInfo(param, result);
            } else {
                // 存在用户信息(第一次查询的时候用户信息已经存在)
                result.put("resultCode", "1");
            }
        }
        return result;
    }

    /**
     * REQ201701040013客户资料综合查询界面增加可以根据白卡号查询
     *
     * @param param
     * @param result
     * @throws Exception
     * @author zhuoyingzhi
     * @date 20170301
     */
    private void qryEmptycardInfo(IData param, IData result) throws Exception {
        String simCardNo;
        //调用新接口：界面输入的“SIM卡号码”当作白卡卡号调资源接口（4.2.1节新增的接口）得到SIM卡号
        IDataset simInfo = CSViewCall.call(this, "SS.GetUser360ViewSVC.qrySIMByWhiteSIM", param);
        if (IDataUtil.isNotEmpty(simInfo)) {
            simCardNo = simInfo.getData(0).getString("SIM_CARD_NO", "");
            if (!"".equals(simCardNo) && simCardNo != null) {
                result.put("resultCode", "0");
                result.put("resultWhiteSimNumber", simCardNo);//通过白卡sim获取sim卡号
            } else {
                //SIM卡信息不存在
                result.put("resultCode", "2");
            }
        } else {
            //第一次查询获取不到用户信息，第二次调用接口还是获取不到用户信息
            result.put("resultCode", "2");
        }
    }

    public abstract void setCondition(IData condition);

    public abstract void setUserInfo(IData userInfo);

    public abstract void setBaseInfo(IData baseInfo);

    public abstract void setAcctInfo(IData acctInfo);

    public abstract void setForegiftInfo(IDataset foregiftInfo);
}
