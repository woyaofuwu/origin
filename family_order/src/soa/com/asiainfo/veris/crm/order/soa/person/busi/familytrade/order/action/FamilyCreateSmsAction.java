
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.action;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.FamilyCreateReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.FamilyMemberData;
import com.asiainfo.veris.crm.order.soa.person.common.action.sms.PerSmsAction;

public class FamilyCreateSmsAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        FamilyCreateReqData reqData = (FamilyCreateReqData) btd.getRD();
        String serialNumber = reqData.getUca().getSerialNumber();// 主号
        String shortCode = reqData.getShortCode();

        MainTradeData mainTD = btd.getMainTradeData();
        String userId = mainTD.getUserId();
        String userIdA = mainTD.getRsrvStr1();
        String discntCodes = mainTD.getRsrvStr4();// 成员优惠
        String shortCodeBs = mainTD.getRsrvStr6();// 成员短号
        String serialNumberBs = mainTD.getRsrvStr7();// 成员号码
        String appDiscntCodes = mainTD.getRsrvStr8();// 成员叠加优惠
        String processTagSet = mainTD.getProcessTagSet();// 标识位

        IData smsData = new DataMap(); // 短信数据
        smsData.put("SMS_PRIORITY", "5000");// 优先级：老系统默认5000
        smsData.put("CANCEL_TAG", "0");// 返销标志：0-未返销，1-被返销，2-返销 不能为空

        // 二次确认短信
        if (StringUtils.isNotBlank(serialNumberBs))
        {
            String[] discntCode = StringUtils.split(discntCodes, ",");
            String[] shortCodeB = StringUtils.split(shortCodeBs, ",");
            String[] serialNumberB = StringUtils.split(serialNumberBs, ",");
            String[] appDiscntCode = StringUtils.split(appDiscntCodes, ",");

            String mainShortCode = reqData.getShortCode();// 主号短号
            // 如果主号没有短号也要送短号过去，短厅指令需要 ?????
            if (StringUtils.isBlank(mainShortCode))
            {
                mainShortCode = "520";
            }

            StringBuilder secondConfirmContent = new StringBuilder();
            String secondConfirmContentHead = "KTQQW#" + mainShortCode + "#";
            if (serialNumberB.length == discntCode.length)
            {
                secondConfirmContent.append(secondConfirmContentHead);

                for (int i = 0, length = serialNumberB.length; i < length; i++)
                {
                    String snB = serialNumberB[i];
                    String appDC = (appDiscntCode == null || appDiscntCode.length == 0) ? "" : appDiscntCode[i];
                    String scB = (shortCodeB == null || shortCodeB.length == 0) ? "" : shortCodeB[i];
                    String appDiscntFlag = "";

                    if (StringUtils.isBlank(snB))
                    {
                        continue;
                    }

                    // 叠加包标识：如果没有选择叠加包，标识是0；选择1元叠加包，标识是1；选择3元叠加包，标识是3
                    // 注释跟代码不一致 3405为3元叠加包 而标识是1 ???? 已与短厅李文斌确认了：1是1元叠加包，3是3元叠加包，没有为0
                    if (StringUtils.equals(appDC, "3405"))
                    {
                        appDiscntFlag = "3";
                    }
                    else if (StringUtils.equals(appDC, "3406"))
                    {
                        appDiscntFlag = "1";
                    }
                    else
                    {
                        appDiscntFlag = "0";
                    }

                    // 短号为空时，随便传个短号，短信营业厅需要，短号不能传空
                    if (StringUtils.isBlank(scB))
                    {
                        scB = "520";
                    }

                    /**
                     *短信内容拼成这样： KTQQW#主号码短号#成员1长号*成员1短号+叠加包标识#成员2长号*成员2短号+叠加包标识#...#成员9长号*成员9短号+叠加包标识#
                     * 叠加包标识：如果没有选择叠加包，标识是0；选择1元叠加包，标识是1；选择3元叠加包，标识是3； ti_o_sms.recv_object 10086 ti_o_sms.force_object
                     * 主号码
                     */
                    secondConfirmContent.append(snB);
                    secondConfirmContent.append("*");
                    secondConfirmContent.append(scB);
                    secondConfirmContent.append("+");
                    secondConfirmContent.append(appDiscntFlag);
                    secondConfirmContent.append("#");
                }

                smsData.put("FORCE_OBJECT", serialNumber);// 发送对象
                smsData.put("RECV_OBJECT", "10086");// 接收对象
                smsData.put("NOTICE_CONTENT", secondConfirmContent.toString());// 短信内容
                PerSmsAction.insTradeSMS(btd, smsData);
            }
        }

        // 新加入成员短信
        List<FamilyMemberData> mebDataList = reqData.getMemberDataList();
        String pTag1 = StringUtils.substring(processTagSet, 0, 1);// 校验方式 0：密码 1：短信
        
        String discntCode = "3403";
        String discntsArray = "3403,3404,3410,3411";
        UcaData uca = UcaDataFactory.getUcaByUserId(userId);
        List<DiscntTradeData> userDiscntList = uca.getUserDiscntsByDiscntCodeArray(discntsArray);
        //IDataset userDiscntList = UserDiscntInfoQry.getDiscntsByPMode(userId, "05");
        if ( ArrayUtil.isNotEmpty(userDiscntList) ){
        	DiscntTradeData userDiscnt = userDiscntList.get(0);
            discntCode = userDiscnt.getDiscntCode();
        }

        if ("0".equals(mainTD.getRsrvStr2())) { // 0-界面互联网前台受理
                //尊敬的客户，您已成功加入***号码组建的亲亲网。即刻起您可使用短号在亲亲网内便捷互拨及享受网内免费通话10000分钟、免费短信3000条的优惠。
                StringBuilder smsContent = new StringBuilder();
                smsContent.append("尊敬的客户，您已成功加入");
                smsContent.append(serialNumber);

                String strCustName = reqData.getUca().getCustomer().getCustName();
                smsContent.append("(" + strCustName + ")");

                smsContent.append("号码组建的亲亲网。");

                //REQ201506230022 亲亲网短厅指令优化及各环节触点短信内容优化@yanwu begin
                if ("3403".equals(discntCode) || "3404".equals(discntCode)) {
                    //endMsg = "";
                } else if ("3410".equals(discntCode) || "3411".equals(discntCode)) {
                    smsContent.append("即刻起您可使用短号在亲亲网内便捷互拨及享受网内免费通话10000分钟、免费短信3000条的优惠。");
                }
                //REQ201506230022 亲亲网短厅指令优化及各环节触点短信内容优化@yanwu end

                //REQ201912260028取消亲亲网副号码添加确认规则的需求start
                smsContent.append("您可编辑短信TCQQW（或退出亲亲网）发送到10086取消亲亲网成员身份。");
                //REQ201912260028取消亲亲网副号码添加确认规则的需求end

                for (int i = 0, size = mebDataList.size(); i < size; i++) {
                    FamilyMemberData memberInfo = mebDataList.get(i);
                    if (!StringUtils.equals(memberInfo.getMebVerifyMode(), "1")) { // 密码或者免密码方式校验的副卡
                        UcaData mebUca = memberInfo.getUca();
                        String serialNumberB = mebUca.getSerialNumber();

                        smsData.put("FORCE_OBJECT", "10086");// 发送对象
                        smsData.put("RECV_OBJECT", serialNumberB);// 接收对象
                        smsData.put("NOTICE_CONTENT", smsContent.toString());// 短信内容
                        PerSmsAction.insTradeSMS(btd, smsData);
                    }
                }

            // 亲亲网给所有成员号码发送短信
                StringBuilder strContent = new StringBuilder();
                strContent.append("尊敬的客户，有新的成员加入您所在的亲亲网。所有亲亲网成员为：");
                String endMsg = "亲亲网成员间在省内可拨打短号通话、通过短号发送短信及彩信，享受通话优惠。国内业务不含港澳台业务。";

                //REQ201506230022 亲亲网短厅指令优化及各环节触点短信内容优化@yanwu begin
                if ("3403".equals(discntCode) || "3404".equals(discntCode)) {
                    endMsg = "亲亲网成员间在省内可拨打短号通话、通过短号发送短信及彩信，享受通话优惠。国内业务不含港澳台业务。";
                } else if ("3410".equals(discntCode) || "3411".equals(discntCode)) {
                    endMsg = "亲亲网共可加10名成员，亲亲网成员间在省内可拨打短号通话、通过短号发送短信及彩信，享受通话优惠。国内业务不含港澳台业务。";
                }
                //REQ201506230022 亲亲网短厅指令优化及各环节触点短信内容优化@yanwu end

                // 本次新增的成员发送短信拼接
                StringBuilder thisTimeMebContent = new StringBuilder();
                for (int i = 0, size = mebDataList.size(); i < size; i++) {
                    FamilyMemberData memberInfo = mebDataList.get(i);
                    if (!StringUtils.equals(memberInfo.getMebVerifyMode(), "1")) { // 密码或者免密码方式校验的副卡
                        UcaData mebUca = memberInfo.getUca();
                        String serialNumberB = mebUca.getSerialNumber();
                        String shortCodeB = memberInfo.getShortCode();

                        thisTimeMebContent.append("副号码");
                        thisTimeMebContent.append(serialNumberB);

                        if (StringUtils.isNotBlank(shortCodeB)) {
                            thisTimeMebContent.append("短号");
                            thisTimeMebContent.append(shortCodeB);
                            thisTimeMebContent.append(";");
                        } else {
                            thisTimeMebContent.append(";");
                        }
                    }
                }

                IDataset uuList = RelaUUInfoQry.qryRelaUUByUIdAAllDB(userIdA, "45");
                if (IDataUtil.isNotEmpty(uuList) && !StringUtils.equals(thisTimeMebContent,"")) {
                    for (int i = 0, size = uuList.size(); i < size; i++) {
                        IData uuInfo = uuList.getData(i);
                        String serialNumberB = uuInfo.getString("SERIAL_NUMBER_B");
                        String roleCodeB = uuInfo.getString("ROLE_CODE_B");
                        String shortCodeB = uuInfo.getString("SHORT_CODE");

                        if (StringUtils.equals(roleCodeB, "1")) {
                            strContent.append("主号码");
                            strContent.append(serialNumberB);
                        } else if (StringUtils.equals(roleCodeB, "2")) {
                            strContent.append("副号码");
                            strContent.append(serialNumberB);
                        }

                        if (StringUtils.isNotBlank(shortCodeB)) {
                            strContent.append("短号");
                            strContent.append(shortCodeB);
                            strContent.append(";");
                        } else {
                            strContent.append(";");
                        }
                    }

                    strContent.append(thisTimeMebContent.toString());
                    strContent.append(endMsg);

                    smsData.put("FORCE_OBJECT", "10086");// 发送对象
                    smsData.put("NOTICE_CONTENT", strContent.toString());// 短信内容

                    for (int j = 0, size = uuList.size(); j < size; j++) {
                        IData uuInfo = uuList.getData(j);
                        String serialNumberB = uuInfo.getString("SERIAL_NUMBER_B");
                        smsData.put("RECV_OBJECT", serialNumberB);// 接收对象
                        PerSmsAction.insTradeSMS(btd, smsData);
                    }

                    // 本次新增的成员也要发
                    for (int j = 0, size = mebDataList.size(); j < size; j++) {
                        FamilyMemberData memberInfo = mebDataList.get(j);
                        if (!StringUtils.equals(memberInfo.getMebVerifyMode(), "1")) { // 密码或者免密码方式校验的副卡
                            UcaData mebUca = memberInfo.getUca();
                            String serialNumberB = mebUca.getSerialNumber();
                            smsData.put("RECV_OBJECT", serialNumberB);// 接收对象
                            PerSmsAction.insTradeSMS(btd, smsData);
                        }
                    }
                } else if (!StringUtils.equals(thisTimeMebContent,"")){
                    strContent.append("主号码");
                    strContent.append(serialNumber);

                    if (StringUtils.isNotBlank(shortCode)) {
                        strContent.append("短号");
                        strContent.append(shortCode);
                        strContent.append(";");
                    } else {
                        strContent.append(";");
                    }

                    strContent.append(thisTimeMebContent.toString());
                    strContent.append(endMsg);

                    smsData.put("FORCE_OBJECT", "10086");// 发送对象
                    smsData.put("NOTICE_CONTENT", strContent.toString());// 短信内容

                    // 发给主号
                    smsData.put("RECV_OBJECT", serialNumber);
                    PerSmsAction.insTradeSMS(btd, smsData);

                    // 发给副号
                    for (int j = 0, size = mebDataList.size(); j < size; j++) {
                        FamilyMemberData memberInfo = mebDataList.get(j);
                        if (!StringUtils.equals(memberInfo.getMebVerifyMode(), "1")) { // 密码或者免密码方式校验的副卡
                            UcaData mebUca = memberInfo.getUca();
                            String serialNumberB = mebUca.getSerialNumber();
                            smsData.put("RECV_OBJECT", serialNumberB);// 接收对象
                            PerSmsAction.insTradeSMS(btd, smsData);
                        }
                    }
                }
        } else { // 原逻辑
            if (!StringUtils.equals(pTag1, "1")) {
                //尊敬的客户，您已成功加入***号码组建的亲亲网。即刻起您可使用短号在亲亲网内便捷互拨及享受网内免费通话10000分钟、免费短信3000条的优惠。
                StringBuilder smsContent = new StringBuilder();
                smsContent.append("尊敬的客户，您已成功加入");
                smsContent.append(serialNumber);

                String strCustName = reqData.getUca().getCustomer().getCustName();
                smsContent.append("(" + strCustName + ")");

                smsContent.append("号码组建的亲亲网。");

                //REQ201506230022 亲亲网短厅指令优化及各环节触点短信内容优化@yanwu begin
                if ("3403".equals(discntCode) || "3404".equals(discntCode)) {
                    //endMsg = "";
                } else if ("3410".equals(discntCode) || "3411".equals(discntCode)) {
                    smsContent.append("即刻起您可使用短号在亲亲网内便捷互拨及享受网内免费通话10000分钟、免费短信3000条的优惠。国内业务不含港澳台业务。");
                }
                //REQ201506230022 亲亲网短厅指令优化及各环节触点短信内容优化@yanwu end

                //REQ201912260028取消亲亲网副号码添加确认规则的需求start
                smsContent.append("您可编辑短信TCQQW（或退出亲亲网）发送到10086取消亲亲网成员身份。");
                //REQ201912260028取消亲亲网副号码添加确认规则的需求end

                for (int i = 0, size = mebDataList.size(); i < size; i++) {
                    FamilyMemberData memberInfo = mebDataList.get(i);
                    UcaData mebUca = memberInfo.getUca();
                    String serialNumberB = mebUca.getSerialNumber();

                    // 查询用户的亲亲网叠加优惠
                    // IDataset userDiscnts = UserDiscntInfoQry.queryUserDiscntByParamattr(userIdB, "1009");
                /*DiscntData appDiscntData = memberInfo.getAppDiscntData();
                if (null != appDiscntData)
                {
                    String appDiscntCode = appDiscntData.getElementId();// 叠加包优惠编码
                    // 查询产品优惠
                    IData discnt = UDiscntInfoQry.getDiscntInfoByPk(appDiscntCode);
                    if (IDataUtil.isNotEmpty(discnt))
                    {
                        smsContent.append("并办理");
                        smsContent.append(discnt.getString("DISCNT_NAME", ""));
                        smsContent.append("(每月可免费拨打亲亲网成员电话");
                        smsContent.append(discnt.getString("RSRV_STR3", ""));
                        smsContent.append("分钟)。");
                    }
                }*/

                    smsData.put("FORCE_OBJECT", "10086");// 发送对象
                    smsData.put("RECV_OBJECT", serialNumberB);// 接收对象
                    smsData.put("NOTICE_CONTENT", smsContent.toString());// 短信内容
                    PerSmsAction.insTradeSMS(btd, smsData);
                }
            }

            // 亲亲网给所有成员号码发送短信
            // String pTag2 = StringUtils.substring(processTagSet, 1, 2);//是否新建亲亲网 0：否 1：是
            if (!StringUtils.equals(pTag1, "1")) {// || StringUtils.equals(pTag2, "1")
                StringBuilder strContent = new StringBuilder();
                strContent.append("尊敬的客户，有新的成员加入您所在的亲亲网。所有亲亲网成员为：");
                String endMsg = "亲亲网成员间在省内可拨打短号通话、通过短号发送短信及彩信，享受通话优惠。国内业务不含港澳台业务。";

                //REQ201506230022 亲亲网短厅指令优化及各环节触点短信内容优化@yanwu begin
                if ("3403".equals(discntCode) || "3404".equals(discntCode)) {
                    endMsg = "亲亲网成员间在省内可拨打短号通话、通过短号发送短信及彩信，享受通话优惠。国内业务不含港澳台业务。";
                } else if ("3410".equals(discntCode) || "3411".equals(discntCode)) {
                    endMsg = "亲亲网共可加10名成员，亲亲网成员间在省内可拨打短号通话、通过短号发送短信及彩信，享受通话优惠。国内业务不含港澳台业务。";
                }
                //REQ201506230022 亲亲网短厅指令优化及各环节触点短信内容优化@yanwu end

                // 本次新增的成员发送短信拼接
                StringBuilder thisTimeMebContent = new StringBuilder();
                for (int i = 0, size = mebDataList.size(); i < size; i++) {
                    FamilyMemberData memberInfo = mebDataList.get(i);
                    UcaData mebUca = memberInfo.getUca();
                    String serialNumberB = mebUca.getSerialNumber();
                    String shortCodeB = memberInfo.getShortCode();

                    thisTimeMebContent.append("副号码");
                    thisTimeMebContent.append(serialNumberB);

                    if (StringUtils.isNotBlank(shortCodeB)) {
                        thisTimeMebContent.append("短号");
                        thisTimeMebContent.append(shortCodeB);
                        thisTimeMebContent.append(";");
                    } else {
                        thisTimeMebContent.append(";");
                    }
                }

                IDataset uuList = RelaUUInfoQry.qryRelaUUByUIdAAllDB(userIdA, "45");
                if (IDataUtil.isNotEmpty(uuList)) {
                    for (int i = 0, size = uuList.size(); i < size; i++) {
                        IData uuInfo = uuList.getData(i);
                        String serialNumberB = uuInfo.getString("SERIAL_NUMBER_B");
                        String roleCodeB = uuInfo.getString("ROLE_CODE_B");
                        String shortCodeB = uuInfo.getString("SHORT_CODE");

                        if (StringUtils.equals(roleCodeB, "1")) {
                            strContent.append("主号码");
                            strContent.append(serialNumberB);
                        } else if (StringUtils.equals(roleCodeB, "2")) {
                            strContent.append("副号码");
                            strContent.append(serialNumberB);
                        }

                        if (StringUtils.isNotBlank(shortCodeB)) {
                            strContent.append("短号");
                            strContent.append(shortCodeB);
                            strContent.append(";");
                        } else {
                            strContent.append(";");
                        }
                    }

                    strContent.append(thisTimeMebContent.toString());
                    strContent.append(endMsg);

                    smsData.put("FORCE_OBJECT", "10086");// 发送对象
                    smsData.put("NOTICE_CONTENT", strContent.toString());// 短信内容

                    for (int j = 0, size = uuList.size(); j < size; j++) {
                        IData uuInfo = uuList.getData(j);
                        String serialNumberB = uuInfo.getString("SERIAL_NUMBER_B");
                        smsData.put("RECV_OBJECT", serialNumberB);// 接收对象
                        PerSmsAction.insTradeSMS(btd, smsData);
                    }

                    // 本次新增的成员也要发
                    for (int j = 0, size = mebDataList.size(); j < size; j++) {
                        FamilyMemberData memberInfo = mebDataList.get(j);
                        UcaData mebUca = memberInfo.getUca();
                        String serialNumberB = mebUca.getSerialNumber();
                        smsData.put("RECV_OBJECT", serialNumberB);// 接收对象
                        PerSmsAction.insTradeSMS(btd, smsData);
                    }
                } else {
                    strContent.append("主号码");
                    strContent.append(serialNumber);

                    if (StringUtils.isNotBlank(shortCode)) {
                        strContent.append("短号");
                        strContent.append(shortCode);
                        strContent.append(";");
                    } else {
                        strContent.append(";");
                    }

                    strContent.append(thisTimeMebContent.toString());
                    strContent.append(endMsg);

                    smsData.put("FORCE_OBJECT", "10086");// 发送对象
                    smsData.put("NOTICE_CONTENT", strContent.toString());// 短信内容

                    // 发给主号
                    smsData.put("RECV_OBJECT", serialNumber);
                    PerSmsAction.insTradeSMS(btd, smsData);

                    // 发给副号
                    for (int j = 0, size = mebDataList.size(); j < size; j++) {
                        FamilyMemberData memberInfo = mebDataList.get(j);
                        UcaData mebUca = memberInfo.getUca();
                        String serialNumberB = mebUca.getSerialNumber();
                        smsData.put("RECV_OBJECT", serialNumberB);// 接收对象
                        PerSmsAction.insTradeSMS(btd, smsData);
                    }
                }
            }
        }
    }
}
