package com.asiainfo.veris.crm.order.soa.person.busi.cmonline;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ComFuncUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.FillUserElementInfoUtil;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidenetchangeprod.NoPhoneWideChangeProdBean;
import com.asiainfo.veris.crm.order.soa.person.busi.cmonline.broadband.BroadBandUtil;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

public class PersonBroadBandSVC extends CSBizService {
    private static final long serialVersionUID = 1L;

    /**
     * 4.1.27.宽带移机业务受理(BroadBandMove)
     * 宽带移机业务受理接口。
     */
    public IData C898HQBroadBandMove(IData inParamStr) throws Exception {
        IData inParam = new DataMap(inParamStr.toString());
        IData params = inParam.getData("params");
        IDataset inParamSet1 = new DatasetList();
        IData set1Map1 = new DataMap();
        set1Map1.put("FIELD", "broadBandInfo"); // 宽带信息
        set1Map1.put("TYPE", "Map");
        inParamSet1.add(set1Map1);
        IData set1Map2 = new DataMap();
        set1Map2.put("FIELD", "operatorId"); // 操作员工号
        set1Map2.put("TYPE", "String");
        inParamSet1.add(set1Map2);
        IData set1Map3 = new DataMap();
        set1Map3.put("FIELD", "operatorName"); // 操作员姓名
        set1Map3.put("TYPE", "String");
        inParamSet1.add(set1Map3);
        IData set1Map4 = new DataMap();
        set1Map4.put("FIELD", "provinceId"); // 省编号
        set1Map4.put("TYPE", "String");
        inParamSet1.add(set1Map4);
        IData set1Map5 = new DataMap();
        set1Map5.put("FIELD", "regionId"); // 地市编码
        set1Map5.put("TYPE", "String");
        inParamSet1.add(set1Map5);
        IData set1Map6 = new DataMap();
        set1Map6.put("FIELD", "channelId"); // 渠道标识
        set1Map6.put("TYPE", "String");
        inParamSet1.add(set1Map6);
        IData inParamResult = ComFuncUtil.checkInParam(params, inParamSet1);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        IData broadBandInfo = params.getData("broadBandInfo");
        IDataset inParamSet2 = new DatasetList();
        IData set2Map1 = new DataMap();
        set2Map1.put("FIELD", "standardAddressID"); // 标准地址Id
        set2Map1.put("TYPE", "String");
        inParamSet2.add(set2Map1);
        IData set2Map2 = new DataMap();
        set2Map2.put("FIELD", "standardAddressName"); // 标准名称
        set2Map2.put("TYPE", "String");
        inParamSet2.add(set2Map2);
        IData set2Map3 = new DataMap();
        set2Map3.put("FIELD", "remarkAddress"); // 备注地址
        set2Map3.put("TYPE", "String");
        inParamSet2.add(set2Map3);
        IData set2Map4 = new DataMap();
        set2Map4.put("FIELD", "acceptType"); // 接入方式	FTTB、FTTH、GPON
        set2Map4.put("TYPE", "String");
        inParamSet2.add(set2Map4);
        IData set2Map5 = new DataMap();
        set2Map5.put("FIELD", "cooperationModel"); // 合作模式	0：城市；1：农村
        set2Map5.put("TYPE", "String");
        inParamSet2.add(set2Map5);
        IData set2Map6 = new DataMap();
        set2Map6.put("FIELD", "addressType"); // 地址类型	中文
        set2Map6.put("TYPE", "String");
        inParamSet2.add(set2Map6);
        IData set2Map7 = new DataMap();
        set2Map7.put("FIELD", "broadbandAccount"); // 宽带账号
        set2Map7.put("TYPE", "String");
        inParamSet2.add(set2Map7);
        IData set2Map8 = new DataMap();
        set2Map8.put("FIELD", "appointmentDate"); // 预约上门时间	格式：YY-MM-dd HH:mm
        set2Map8.put("TYPE", "String");
        inParamSet2.add(set2Map8);
        inParamResult = ComFuncUtil.checkInParam(broadBandInfo, inParamSet2);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        IData custInfo = params.getData("custInfo");
        try {
            IDataUtil.chkParam(custInfo,"linkMan");
            IDataUtil.chkParam(custInfo,"linkNum");
            IDataUtil.chkParam(custInfo,"linkMan2");
            IDataUtil.chkParam(custInfo,"linkNum2");
        } catch (Exception e) {
            IData error = new DataMap();
            error.put("FLAG", "-1");
            error.put("MESSAGE", e.getMessage());
            return BroadBandUtil.requestData(IDataUtil.idToIds(error));
        }

        /********************************业务受理*********************************/
        IData returnMap = new DataMap();
        returnMap.put("rtnCode", ComFuncUtil.CODE_RIGHT);
        returnMap.put("rtnMsg", "成功");
        IData object = new DataMap();
        returnMap.put("object", object);
        object.put("respCode", ComFuncUtil.CODE_RIGHT);
        object.put("respDesc", "success");
        IDataset result = new DatasetList();
        object.put("result", result);
        IData resultItem = new DataMap();
        result.add(resultItem);
        resultItem.put("flag", "1");
        resultItem.put("desc", "宽带移机成功");

        String broadbandAccount = broadBandInfo.getString("broadbandAccount");
        IData feeInfo = params.getData("feeInfo");
        String regionId = params.getString("regionId");
        IData tradeParam = new DataMap();
        try {

            tradeParam.put("SERIAL_NUMBER", broadbandAccount);
            tradeParam.put("NEW_STAND_ADDRESS", broadBandInfo.getString("standardAddressName"));
            tradeParam.put("NEW_STAND_ADDRESS_CODE", broadBandInfo.getString("standardAddressID"));
            tradeParam.put("NEW_DETAIL_ADDRESS", broadBandInfo.getString("remarkAddress"));
            tradeParam.put("NEW_AREA_CODE", params.getString("regionId"));
            tradeParam.put("NEW_CONTACT", custInfo.getString("linkMan"));
            tradeParam.put("NEW_PHONE", custInfo.getString("linkNum"));
            tradeParam.put("NEW_CONTACT_PHONE", custInfo.getString("linkNum2"));

            IData wideTypeInfo = WideNetUtil.getWideNetTypeInfo(broadbandAccount);
            String wideUserId = wideTypeInfo.getString("WIDE_USER_ID");
            IData userInfoParam = new DataMap();
            userInfoParam.put("USER_ID", wideUserId);
            userInfoParam.put("ROUTE_EPARCHY_CODE", regionId);
            IDataset wideUserInfoSet = CSAppCall.call("CS.WidenetInfoQuerySVC.getUserWidenetInfo", userInfoParam);
            tradeParam.put("RSRV_STR2", wideUserInfoSet.getData(0).getString("RSRV_STR2"));
            //tradeParam.put("WIDE_TYPE", "ADSL");
            tradeParam.put("NEW_WIDE_TYPE", broadBandInfo.getString("acceptType"));

            tradeParam.put("SUGGEST_DATE", broadBandInfo.getString("appointmentDate"));
            tradeParam.put("TRADE_TYPE_CODE", "606");

            IData userParam2 = new DataMap();
            userParam2.put("SERIAL_NUMBER", broadbandAccount);
            userParam2.put("ROUTE_EPARCHY_CODE", regionId);
            IDataset userInfos = CSAppCall.call("SS.WidenetMoveSVC.getUserInfoBySerial", userParam2);

            String isBusiness = "0";
            if (userInfos != null && userInfos.size() > 0) {
                if ("BNBD".equals(userInfos.getData(0).getString("RSRV_STR10"))) {
                    isBusiness = "1";
                }
            }

            tradeParam.put("IS_BUSINESS_WIDE", isBusiness);
            tradeParam.put("DEVICE_ID", broadBandInfo.getString("standardAddressID"));
            //tradeParam.put("MODEL_MODE", "1");
            tradeParam.put("IS_EXCHANGE_MODEL", "1");
            IDataset expenseItemList = feeInfo.getDataset("expenseItemList");
            for (int i = 0; i < expenseItemList.size(); i++) {
                String expenseItemName = expenseItemList.getData(i).getString("expenseItemName");
                String expenseItemMoney = expenseItemList.getData(i).getString("expenseItemMoney");
                if ("DEPOSIT_MONEY".equals(expenseItemName)) {
                    tradeParam.put("MODEM_DEPOSIT", expenseItemMoney);
                    tradeParam.put("DEPOSIT_MONEY", (Integer.parseInt(expenseItemMoney) * 100) + "");
                } else if ("PURCHASE_MONEY".equals(expenseItemName)) {
                    tradeParam.put("PURCHASE_MONEY", (Integer.parseInt(expenseItemMoney) * 100) + "");
                }
            }
        } catch (Exception e) {
            returnMap.put("rtnCode", ComFuncUtil.CODE_ERROR);
            returnMap.put("rtnMsg", e.getMessage());
            object.put("respCode", ComFuncUtil.CODE_ERROR);
            object.put("respDesc", e.getMessage());
            resultItem.put("flag", "0");
            resultItem.put("desc", e.getMessage());
            return returnMap;
        }
        IDataset dataset = null;
        try {
            dataset = CSAppCall.call("SS.WidenetMoveRegSVC.tradeReg", tradeParam);
            resultItem.put("iteractId", dataset.getData(0).getString("ORDER_ID"));
        } catch (Exception e) {
            resultItem.put("iteractId", SeqMgr.getOrderId());
        }
        return returnMap;
    }

    /**
     * 4.1.28.宽带停复机业务受理(BroadBandHaltOrRecover)
     * 宽带停复机业务受理，且停机支持预约停机和立即停机两种方式。
     */
    public IData C898HQBroadBandHaltOrRecover(IData inParamStr) throws Exception {
        IData inParam = new DataMap(inParamStr.toString());
        IData params = inParam.getData("params");
        IDataset inParamSet1 = new DatasetList();
        IData set1Map1 = new DataMap();
        set1Map1.put("FIELD", "broadBandInfo"); // 宽带信息
        set1Map1.put("TYPE", "Map");
        inParamSet1.add(set1Map1);
        IData set1Map2 = new DataMap();
        set1Map2.put("FIELD", "operatorId"); // 操作员工号
        set1Map2.put("TYPE", "String");
        inParamSet1.add(set1Map2);
        IData set1Map3 = new DataMap();
        set1Map3.put("FIELD", "operatorName"); // 操作员姓名
        set1Map3.put("TYPE", "String");
        inParamSet1.add(set1Map3);
        IData set1Map4 = new DataMap();
        set1Map4.put("FIELD", "provinceId"); // 省编号
        set1Map4.put("TYPE", "String");
        inParamSet1.add(set1Map4);
        IData set1Map5 = new DataMap();
        set1Map5.put("FIELD", "regionId"); // 地市编码
        set1Map5.put("TYPE", "String");
        inParamSet1.add(set1Map5);
        IData set1Map6 = new DataMap();
        set1Map6.put("FIELD", "channelId"); // 渠道标识
        set1Map6.put("TYPE", "String");
        inParamSet1.add(set1Map6);
        IData inParamResult = ComFuncUtil.checkInParam(params, inParamSet1);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        IData broadBandInfo = params.getData("broadBandInfo");
        IDataset inParamSet2 = new DatasetList();
        IData set2Map1 = new DataMap();
        set2Map1.put("FIELD", "broadbandAccount"); // 宽带账号
        set2Map1.put("TYPE", "String");
        inParamSet2.add(set2Map1);
        IData set2Map2 = new DataMap();
        set2Map2.put("FIELD", "busiType"); // 业务类型	0：停机；1：复机
        set2Map2.put("TYPE", "String");
        inParamSet2.add(set2Map2);
        inParamResult = ComFuncUtil.checkInParam(broadBandInfo, inParamSet2);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        /********************************业务受理*********************************/
        IData returnMap = new DataMap();
        returnMap.put("rtnCode", ComFuncUtil.CODE_RIGHT);
        returnMap.put("rtnMsg", "成功");
        IData object = new DataMap();
        returnMap.put("object", object);
        object.put("respCode", ComFuncUtil.CODE_RIGHT);
        object.put("respDesc", "success");
        IDataset result = new DatasetList();
        object.put("result", result);
        IData resultItem = new DataMap();
        result.add(resultItem);
        resultItem.put("flag", "1");

        String busiType = broadBandInfo.getString("busiType");
        String broadbandAccount = broadBandInfo.getString("broadbandAccount");

        IData wideTypeInfo = WideNetUtil.getWideNetTypeInfo(broadbandAccount);
        String phoneSerialNum = wideTypeInfo.getString("SERIAL_NUMBER");

        if ("0".equals(busiType)) { // 停机
            try {
                IData tradeParam = new DataMap();
                tradeParam.put("TRADE_TYPE_CODE", "603");
                tradeParam.put("CHECK_MODE", "F");
                tradeParam.put("SERIAL_NUMBER", phoneSerialNum);
                tradeParam.put("AUTH_SERIAL_NUMBER", phoneSerialNum);
                IDataset dataset = CSAppCall.call("SS.ChangeWidenetSvcStateRegSVC.tradeReg", tradeParam);
                resultItem.put("desc", "宽带停机成功");
                resultItem.put("iteractId", dataset.getData(0).getString("ORDER_ID")); // 业务交互编码
            } catch (Exception e) {
                returnMap.put("rtnCode", ComFuncUtil.CODE_ERROR);
                returnMap.put("rtnMsg", e.getMessage());
                object.put("respCode", ComFuncUtil.CODE_ERROR);
                object.put("respDesc", e.getMessage());
                resultItem.put("flag", "0");
                resultItem.put("desc", e.getMessage());
            }
        } else { // 开机
            try {
                IData tradeParam = new DataMap();
                tradeParam.put("TRADE_TYPE_CODE", "604");
                tradeParam.put("CHECK_MODE", "F");
                tradeParam.put("SERIAL_NUMBER", phoneSerialNum);
                tradeParam.put("AUTH_SERIAL_NUMBER", phoneSerialNum);
                IDataset dataset = CSAppCall.call("SS.ChangeWidenetSvcStateRegSVC.tradeReg", tradeParam);
                resultItem.put("desc", "宽带开机成功");
                resultItem.put("iteractId", dataset.getData(0).getString("ORDER_ID")); // 业务交互编码
            } catch (Exception e) {
                returnMap.put("rtnCode", ComFuncUtil.CODE_ERROR);
                returnMap.put("rtnMsg", e.getMessage());
                object.put("respCode", ComFuncUtil.CODE_ERROR);
                object.put("respDesc", e.getMessage());
                resultItem.put("flag", "0");
                resultItem.put("desc", e.getMessage());
            }
        }
        return returnMap;
    }
    /*
    入参：
    {
"params":{
"custInfo":{
"custName":"测试用户",
"acceptNum":"KD_13976640587",
"certTypeName":"K",
"certNbr":"34128120741719827"
},
"broadBandInfo":{
"broadbandAccount":"KD_13976640587",
"busiType":"1"
},
"operatorId":"SUPERUSR",
"operatorName":"超级用户",
"provinceId":"898",
"regionId":"0898",
"channelId":"36601"
}
}
     */

    /**
     * 4.1.31.宽带提速业务受理预校验(BroadBandSpeedUpCheck)
     * 宽带提速业务受理预校验接口。
     */
    public IData C898HQBroadBandSpeedUpCheck(IData inParamStr) throws Exception {
        IData inParam = new DataMap(inParamStr.toString());
        IData params = inParam.getData("params");
        IDataset inParamSet1 = new DatasetList();
        IData set1Map2 = new DataMap();
        set1Map2.put("FIELD", "operatorId"); // 操作员工号
        set1Map2.put("TYPE", "String");
        inParamSet1.add(set1Map2);
        IData set1Map3 = new DataMap();
        set1Map3.put("FIELD", "operatorName"); // 操作员姓名
        set1Map3.put("TYPE", "String");
        inParamSet1.add(set1Map3);
        IData set1Map4 = new DataMap();
        set1Map4.put("FIELD", "provinceId"); // 省编号
        set1Map4.put("TYPE", "String");
        inParamSet1.add(set1Map4);
        IData set1Map5 = new DataMap();
        set1Map5.put("FIELD", "regionId"); // 地市编码
        set1Map5.put("TYPE", "String");
        inParamSet1.add(set1Map5);
        IData set1Map6 = new DataMap();
        set1Map6.put("FIELD", "channelId"); // 渠道标识
        set1Map6.put("TYPE", "String");
        inParamSet1.add(set1Map6);
        IData set1Map7 = new DataMap();
        set1Map7.put("FIELD", "custInfo"); // 客户信息
        set1Map7.put("TYPE", "Map");
        inParamSet1.add(set1Map7);
        IData set1Map8 = new DataMap();
        set1Map8.put("FIELD", "speedPackageInfo"); // 提速包信息
        set1Map8.put("TYPE", "Map");
        inParamSet1.add(set1Map8);
        IData inParamResult = ComFuncUtil.checkInParam(params, inParamSet1);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }
        /********************************业务受理*********************************/
        IData returnMap = new DataMap();
        returnMap.put("rtnCode", ComFuncUtil.CODE_RIGHT);
        returnMap.put("rtnMsg", "成功");
        IData object = new DataMap();
        returnMap.put("object", object);
        object.put("respCode", ComFuncUtil.CODE_RIGHT);
        object.put("respDesc", "success");
        IDataset result = new DatasetList();
        object.put("result", result);
        IData resultItem = new DataMap();
        result.add(resultItem);
        resultItem.put("flag", "1");
        resultItem.put("desc", "提速业务受理预校验通过");

        IData custInfo = params.getData("custInfo");
        String acceptNum = custInfo.getString("acceptNum");
        IData wideTypeInfo = WideNetUtil.getWideNetTypeInfo(acceptNum);
        try {
            //如果是无手宽带业务
            if ("Y".equals(wideTypeInfo.getString("IS_NOPHONE_WIDENET"))) {
                checkBeforeTrade(wideTypeInfo.getString("WIDE_SERIAL_NUMBER"), "681");
            } else {
                checkBeforeTrade(wideTypeInfo.getString("WIDE_SERIAL_NUMBER"), "601");
            }
        } catch (Exception e) {
            returnMap.put("rtnCode", ComFuncUtil.CODE_ERROR);
            returnMap.put("rtnMsg", e.getMessage());
            object.put("respCode", ComFuncUtil.CODE_ERROR);
            object.put("respDesc", e.getMessage());
            resultItem.put("flag", "0");
            resultItem.put("desc", e.getMessage());
        }
        return returnMap;
    }

    /**
     * 4.1.26.宽带移机业务受理预校验(BroadBandMoveCheck)
     * 宽带移机业务受理预校验接口。
     */
    public IData C898HQBroadBandMoveCheck(IData inParamStr) throws Exception {
        IData inParam = new DataMap(inParamStr.toString());
        IData params = inParam.getData("params");
        IDataset inParamSet1 = new DatasetList();
        IData set1Map2 = new DataMap();
        set1Map2.put("FIELD", "operatorId"); // 操作员工号
        set1Map2.put("TYPE", "String");
        inParamSet1.add(set1Map2);
        IData set1Map3 = new DataMap();
        set1Map3.put("FIELD", "operatorName"); // 操作员姓名
        set1Map3.put("TYPE", "String");
        inParamSet1.add(set1Map3);
        IData set1Map4 = new DataMap();
        set1Map4.put("FIELD", "provinceId"); // 省编号
        set1Map4.put("TYPE", "String");
        inParamSet1.add(set1Map4);
        IData set1Map5 = new DataMap();
        set1Map5.put("FIELD", "regionId"); // 地市编码
        set1Map5.put("TYPE", "String");
        inParamSet1.add(set1Map5);
        IData set1Map6 = new DataMap();
        set1Map6.put("FIELD", "channelId"); // 渠道标识
        set1Map6.put("TYPE", "String");
        inParamSet1.add(set1Map6);
        IData set1Map7 = new DataMap();
        set1Map7.put("FIELD", "custInfo"); // 客户信息
        set1Map7.put("TYPE", "Map");
        inParamSet1.add(set1Map7);
        IData set1Map8 = new DataMap();
        set1Map8.put("FIELD", "broadBandInfo"); // 宽带信息
        set1Map8.put("TYPE", "Map");
        inParamSet1.add(set1Map8);
        IData inParamResult = ComFuncUtil.checkInParam(params, inParamSet1);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }
        /********************************业务受理*********************************/
        IData returnMap = new DataMap();
        returnMap.put("rtnCode", ComFuncUtil.CODE_RIGHT);
        returnMap.put("rtnMsg", "成功");
        IData object = new DataMap();
        returnMap.put("object", object);
        object.put("respCode", ComFuncUtil.CODE_RIGHT);
        object.put("respDesc", "success");
        IDataset result = new DatasetList();
        object.put("result", result);
        IData resultItem = new DataMap();
        result.add(resultItem);
        resultItem.put("flag", "1");
        resultItem.put("desc", "移机预校验通过");

        IData custInfo = params.getData("custInfo");
        String acceptNum = custInfo.getString("acceptNum");
        IData wideTypeInfo = WideNetUtil.getWideNetTypeInfo(acceptNum);
        try {
            //如果是无手宽带业务
            if ("Y".equals(wideTypeInfo.getString("IS_NOPHONE_WIDENET"))) {
                checkBeforeTrade(wideTypeInfo.getString("WIDE_SERIAL_NUMBER"), "686");
            } else {
                checkBeforeTrade(wideTypeInfo.getString("WIDE_SERIAL_NUMBER"), "606");
            }
        } catch (Exception e) {
            returnMap.put("rtnCode", ComFuncUtil.CODE_ERROR);
            returnMap.put("rtnMsg", e.getMessage());
            object.put("respCode", ComFuncUtil.CODE_ERROR);
            object.put("respDesc", e.getMessage());
            resultItem.put("flag", "0");
            resultItem.put("desc", e.getMessage());
        }
        return returnMap;
    }

    /**
     * 4.1.22.宽带续约业务受理预校验(BroadBandRenewCheck)
     * 宽带续约业务受理预校验接口。
     */
    public IData C898HQBroadBandRenewCheck(IData inParamStr) throws Exception {
        IData inParam = new DataMap(inParamStr.toString());
        IData params = inParam.getData("params");
        IDataset inParamSet1 = new DatasetList();
        IData set1Map1 = new DataMap();
        set1Map1.put("FIELD", "remarkInfo"); // 新装备注
        set1Map1.put("TYPE", "String");
        inParamSet1.add(set1Map1);
        IData set1Map2 = new DataMap();
        set1Map2.put("FIELD", "operatorId"); // 操作员工号
        set1Map2.put("TYPE", "String");
        inParamSet1.add(set1Map2);
        IData set1Map3 = new DataMap();
        set1Map3.put("FIELD", "operatorName"); // 操作员姓名
        set1Map3.put("TYPE", "String");
        inParamSet1.add(set1Map3);
        IData set1Map4 = new DataMap();
        set1Map4.put("FIELD", "provinceId"); // 省编号
        set1Map4.put("TYPE", "String");
        inParamSet1.add(set1Map4);
        IData set1Map5 = new DataMap();
        set1Map5.put("FIELD", "regionId"); // 地市编码
        set1Map5.put("TYPE", "String");
        inParamSet1.add(set1Map5);
        IData set1Map6 = new DataMap();
        set1Map6.put("FIELD", "channelId"); // 渠道标识
        set1Map6.put("TYPE", "String");
        inParamSet1.add(set1Map6);
        IData set1Map7 = new DataMap();
        set1Map7.put("FIELD", "custInfo"); // 客户信息
        set1Map7.put("TYPE", "Map");
        inParamSet1.add(set1Map7);
        IData set1Map8 = new DataMap();
        set1Map8.put("FIELD", "broadBandInfo"); // 宽带信息
        set1Map8.put("TYPE", "Map");
        inParamSet1.add(set1Map8);
        IData inParamResult = ComFuncUtil.checkInParam(params, inParamSet1);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }
        /********************************业务受理*********************************/
        IData returnMap = new DataMap();
        returnMap.put("rtnCode", ComFuncUtil.CODE_RIGHT);
        returnMap.put("rtnMsg", "成功");
        IData object = new DataMap();
        returnMap.put("object", object);
        object.put("respCode", ComFuncUtil.CODE_RIGHT);
        object.put("respDesc", "success");
        IDataset result = new DatasetList();
        object.put("result", result);
        IData resultItem = new DataMap();
        result.add(resultItem);
        resultItem.put("flag", "1");
        resultItem.put("desc", "续约受理预校验通过");

        IData custInfo = params.getData("custInfo");
        String acceptNum = custInfo.getString("acceptNum");
        IData wideTypeInfo = WideNetUtil.getWideNetTypeInfo(acceptNum);
        try {
            //如果是无手机魔百和业务
            if ("Y".equals(wideTypeInfo.getString("IS_NOPHONE_WIDENET"))) {
                checkBeforeTrade(wideTypeInfo.getString("WIDE_SERIAL_NUMBER"), "681");
                IData checkParam = new DataMap();
                checkParam.put("USER_ID", wideTypeInfo.getString("WIDE_USER_ID"));
                //宽带预约拆机信息
                IDataset destroyInfos = CSAppCall.call("SS.NoPhoneWideDestroyUserSVC.getDestroyInfo", checkParam);
                //有预约拆机报错
                if (IDataUtil.isNotEmpty(destroyInfos)) {
                    String destroyState = destroyInfos.getData(0).getString("DESTORY_STATE", "");
                    if ("已预约".equals(destroyState)) {
                        CSAppException.appError("-1", "业务受理限制:该用户含有宽带预约拆机记录,不能办理该业务!");
                    }
                }
                IDataset userDiscntList = NoPhoneWideChangeProdBean.qryNoPhoneUserDiscnt(checkParam);
                if (IDataUtil.isNotEmpty(userDiscntList)) {
                    FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userDiscntList, null, null);// 填充productId和packageId
                    if (userDiscntList.size() > 1) {
                        CSAppException.appError("-1", "已续约，不能再次办理!");
                    } else if (userDiscntList.size() == 1) {
                        //如果是下月后才生效的套餐，也不允许办。
                        if (SysDateMgr.compareTo(SysDateMgr.getSysDate(), userDiscntList.getData(0).getString("START_DATE")) <= 0) {
                            CSAppException.appError("-1", "存在即将生效的宽带套餐!");
                        }
                    }
                } else {
                    //这种没有有效优惠的情况，则分为真的没有优惠；已经到期停机的优惠；2种情况。
                    //取该人员的优惠最后一条（日期最大）失效的记录。
                    IDataset userDiscntList2 = NoPhoneWideChangeProdBean.qryNoPhoneUserDiscnt2(inParam);
                    if (IDataUtil.isEmpty(userDiscntList2)) {
                        CSAppException.appError("-1", "未查询到该用户有效的优惠信息!");
                    }
                }
            } else { // 有手机宽带
                checkBeforeTrade(wideTypeInfo.getString("WIDE_SERIAL_NUMBER"), "601");
                IData checkParam = new DataMap();
                checkParam.put("SERIAL_NUMBER", wideTypeInfo.getString("SERIAL_NUMBER"));
                CSAppCall.call("SS.WidenetChangeProductNewSVC.checkWidenetProductInfo", checkParam);
            }
        } catch (Exception e) {
            returnMap.put("rtnCode", ComFuncUtil.CODE_ERROR);
            returnMap.put("rtnMsg", e.getMessage());
            object.put("respCode", ComFuncUtil.CODE_ERROR);
            object.put("respDesc", e.getMessage());
            resultItem.put("flag", "0");
            resultItem.put("desc", e.getMessage());
        }
        return returnMap;
    }
       /* 入参：
{
"params": {
"remarkInfo": "备注",
"operatorId": "SUPERUSR",
"operatorName": "超级用户",
"provinceId": "898",
"regionId": "0898",
"channelId": "36601",
"custInfo": {
"custName": "长研",
"acceptNum": "KD_6007136",
"certTypeName": "0",
"certNbr": "110229200001013897",
"linkMan": "",
"linkNum": "",
"linkMan2": "",
"linkNum2": ""
},
"broadBandInfo": {
"broadbandAccount": "KD_6009106",
"appointmentDate": "",
"packageId": "",
"packageName": "",
"activityId": "",
"activityName": ""
}
}
}
*/

    /**
     * 业务校验
     */
    private void checkBeforeTrade(String serialNum, String tradeTypeCode) throws Exception {
        IData input = new DataMap();
        IData userInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNum);
        if (IDataUtil.isEmpty(userInfo)) {
            CSAppException.appError("-1", "通过该服务号码查询不到有效的用户信息！");
        }
        IData customerInfo = UcaInfoQry.qryCustomerInfoByCustId(userInfo.getString("CUST_ID"));
        if (IDataUtil.isEmpty(customerInfo)) {
            CSAppException.appError("-1", "通过该服务号码查询不到有效的客户信息！");
        }
        input.putAll(userInfo);
        input.put("IS_REAL_NAME", customerInfo.getString("IS_REAL_NAME"));
        input.put("TRADE_TYPE_CODE", tradeTypeCode);
        input.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE", getTradeEparchyCode()));
        input.put("X_CHOICE_TAG", "0");
        //将inmodeCode设置为0，因为APP接口那边传过来是SD，会跳过规则校验。
        getVisit().setInModeCode("0");
        IDataset infos = CSAppCall.call("CS.CheckTradeSVC.checkBeforeTrade", input);
        CSAppException.breerr(infos.getData(0));
    }
}



