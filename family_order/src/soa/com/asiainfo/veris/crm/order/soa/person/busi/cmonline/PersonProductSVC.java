package com.asiainfo.veris.crm.order.soa.person.busi.cmonline;

import com.ailk.biz.bean.BizDAOLogger;
import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ComFuncUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CmOnlineUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.rsa.Rsa;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.CreditCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.DevicePriceQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ExceptionUtils;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.PasswdMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.AssemDynData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.EncAssemDynData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.EncAssemDynDataRsp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.IssueData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.service.WebServiceClient;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.other.QueryOtherInfoBeanCopy;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.other.util.RealNameMsDesPlus;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PersonProductSVC extends CSBizService {
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(BizDAOLogger.class);

    /**
     * 3.1.4.3获取写卡基础数据(queryWriteCardBasicData) 集中交付用的
     * 实名认证系统在线上选号流程中，根据手机号获取可用的写卡基础数据信息。写卡数据预占时间为12小时。
     */
    public IData C898HQQueryWriteCardBasicDataInt(IData inParamStr) throws Exception {
        IData inParam = new DataMap(inParamStr.toString());
        IData params = inParam.getData("params");
        IDataset inParamSet1 = new DatasetList();
        IData set1Map1 = new DataMap();
        set1Map1.put("FIELD", "busiCode"); // 业务编码	QUERY_WRITE_CARD_BASIC_DATA
        set1Map1.put("TYPE", "String");
        inParamSet1.add(set1Map1);
        IData set1Map2 = new DataMap();
        set1Map2.put("FIELD", "sourceCode"); // 请求源编码
        set1Map2.put("TYPE", "String");
        inParamSet1.add(set1Map2);
        IData set1Map3 = new DataMap();
        set1Map3.put("FIELD", "targetCode"); // 落地方编码
        set1Map3.put("TYPE", "String");
        inParamSet1.add(set1Map3);
        IData set1Map4 = new DataMap();
        set1Map4.put("FIELD", "version"); // 报文版本
        set1Map4.put("TYPE", "String");
        inParamSet1.add(set1Map4);
        IData set1Map5 = new DataMap();
        set1Map5.put("FIELD", "transactionID"); // 全网唯一操作流水号
        set1Map5.put("TYPE", "String");
        inParamSet1.add(set1Map5);
        IData set1Map6 = new DataMap();
        set1Map6.put("FIELD", "reqInfo"); // 请求信息
        set1Map6.put("TYPE", "Map");
        inParamSet1.add(set1Map6);
        IData inParamResult = ComFuncUtil.checkInParam(params, inParamSet1);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        IData reqInfo = params.getData("reqInfo");
        IDataset inParamSet2 = new DatasetList();
        IData set2Map1 = new DataMap();
        set2Map1.put("FIELD", "svcNum"); // 开户号码
        set2Map1.put("TYPE", "String");
        inParamSet2.add(set2Map1);
        inParamResult = ComFuncUtil.checkInParam(reqInfo, inParamSet2);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }
        /**********************业务受理*********************/
        String transactionID = params.getString("transactionID", "");

        IData returnMap = new DataMap();
        IDataset result = new DatasetList();
        returnMap.put("result", result);
        IData resultItem = new DataMap();
        result.add(resultItem);
        resultItem.put("transactionID", transactionID);
        String svcNum = reqInfo.getString("svcNum");
        String cityCode = reqInfo.getString("cityCode");

        IDataset writeResult = new DatasetList();
        try {
            writeResult = ResCall.queryWriteCardBasicData(svcNum, "", "2", "0");
        } catch (Exception e) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取可用的写卡基础数据信息失败: " + e.getMessage());
        }
        if (IDataUtil.isEmpty(writeResult)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取可用的写卡基础数据信息失败!");
        }
        IData spesiminfo = writeResult.getData(0);
        IData resInfo = new DataMap();
        String imsi = spesiminfo.getString("IMSI", "");
        String iccid = spesiminfo.getString("SIM_CARD_NO", "");
        String puk1 = spesiminfo.getString("PUK1", "");
        String puk2 = spesiminfo.getString("PUK2", "");
        String pin1 = spesiminfo.getString("PIN1", "");
        String pin2 = spesiminfo.getString("PIN2", "");
        String smsp = spesiminfo.getString("SMSP", "");
        String sn = spesiminfo.getString("SN", "");
        resInfo.put("imsi", imsi);
        resInfo.put("iccid", iccid);
        resInfo.put("puk1", puk1);
        resInfo.put("puk2", puk2);
        resInfo.put("pin1", pin1);
        resInfo.put("pin2", pin2);
        resInfo.put("CenterPhone", smsp);

        String resInfoStr = resInfo.toString();

        String transactionIdPriKey = "1122334488";
        IDataset pushCardPriKey = CmOnlineUtil.queryPushCardPkey(transactionIdPriKey);
        if(IDataUtil.isEmpty(pushCardPriKey)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到卡数据密钥");
        }
        String pri = pushCardPriKey.getData(0).getString("KEY");

        String resInfoStrEnc = new Rsa.Encoder(pri).encode(resInfoStr);

        resultItem.put("resInfo", resInfoStrEnc);
        returnMap.put("respCode", ComFuncUtil.CODE_RIGHT);
        returnMap.put("respDesc", "success");
        return ComFuncUtil.transOutParam(returnMap);
    }

    public IData C898HQQueryMoreNum(IData inParamStr) throws Exception {
        IData inParam = new DataMap(inParamStr.toString());
        IData params = inParam.getData("params");
        QueryOtherInfoBeanCopy queryOtherInfo = (QueryOtherInfoBeanCopy) BeanManager.createBean(QueryOtherInfoBeanCopy.class);
        return queryOtherInfo.queryMoreNum(params);
    }

    /**
     * 3.3.4.3号码预占接口(campOnSvcNum)
     * 实名认证系统将选择的号码和身份证号码传给省公司，省公司对所选号码进行预占。
     * 参考海南原来接口SS.QueryOtherInfoSVC.preemptionResNo
     */
    public IData C898HQCampOnSvcNum(IData inParamStr) throws Exception {
        IData inParam = new DataMap(inParamStr.toString());
        IData params = inParam.getData("params");
        IDataset inParamSet1 = new DatasetList();
        IData set1Map1 = new DataMap();
        set1Map1.put("FIELD", "busiCode"); // 业务编码	CAMP_ON_SVCNUM
        set1Map1.put("TYPE", "String");
        inParamSet1.add(set1Map1);
        IData set1Map2 = new DataMap();
        set1Map2.put("FIELD", "sourceCode"); // 请求源编码
        set1Map2.put("TYPE", "String");
        inParamSet1.add(set1Map2);
        IData set1Map3 = new DataMap();
        set1Map3.put("FIELD", "targetCode"); // 落地方编码
        set1Map3.put("TYPE", "String");
        inParamSet1.add(set1Map3);
        IData set1Map4 = new DataMap();
        set1Map4.put("FIELD", "version"); // 报文版本
        set1Map4.put("TYPE", "String");
        inParamSet1.add(set1Map4);
        IData set1Map5 = new DataMap();
        set1Map5.put("FIELD", "reqInfo"); // 请求信息
        set1Map5.put("TYPE", "Map");
        inParamSet1.add(set1Map5);
        IData inParamResult = ComFuncUtil.checkInParam(params, inParamSet1);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        IData reqInfo = params.getData("reqInfo");
        IDataset inParamSet2 = new DatasetList();
        IData set2Map1 = new DataMap();
        set2Map1.put("FIELD", "transactionID"); // 全网唯一操作流水号
        set2Map1.put("TYPE", "String");
        inParamSet2.add(set2Map1);
        IData set2Map2 = new DataMap();
        set2Map2.put("FIELD", "svcNum"); // 预占号码	加密传输
        set2Map2.put("TYPE", "String");
        inParamSet2.add(set2Map2);
        IData set2Map3 = new DataMap();
        set2Map3.put("FIELD", "operCode"); // 登录员工工号
        set2Map3.put("TYPE", "String");
        inParamSet2.add(set2Map3);
        IData set2Map4 = new DataMap();
        set2Map4.put("FIELD", "channelId"); // 员工所属渠道
        set2Map4.put("TYPE", "String");
        inParamSet2.add(set2Map4);
        IData set2Map5 = new DataMap();
        set2Map5.put("FIELD", "custCertNo"); // 客户身份证号码	加密传输
        set2Map5.put("TYPE", "String");
        inParamSet2.add(set2Map5);
        IData set2Map6 = new DataMap();
        set2Map6.put("FIELD", "custCertName"); // 客户姓名	加密传输
        set2Map6.put("TYPE", "String");
        inParamSet2.add(set2Map6);
        inParamResult = ComFuncUtil.checkInParam(reqInfo, inParamSet2);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        /********************************业务受理********************************/
        String transactionID = reqInfo.getString("transactionID");
        String svcNum = reqInfo.getString("svcNum");
        String custCertNo = reqInfo.getString("custCertNo");
        String custCertName = reqInfo.getString("custCertName");
        String operCode = reqInfo.getString("operCode");
        String channelId = reqInfo.getString("channelId");

        IData returnMap = new DataMap();
        IDataset result = new DatasetList();
        returnMap.put("result", result);
        IData resultItem = new DataMap();
        result.add(resultItem);
        resultItem.put("transactionID", transactionID);
        resultItem.put("isSuc", "1"); // 是否预占成功	0预占成功，1预占失败

        String targetCode = params.getString("targetCode");
        IDataset pubKeySet = CmOnlineUtil.queryPushKey(targetCode);
        if (IDataUtil.isEmpty(pubKeySet)) {
            returnMap.put("respCode", ComFuncUtil.CODE_ERROR);
            returnMap.put("respDesc", "找不到对应的公钥信息");
            return ComFuncUtil.transOutParam(returnMap);
        }
        String pubKey = pubKeySet.getData(0).getString("KEY");
        RealNameMsDesPlus plus = new RealNameMsDesPlus(pubKey);

        String preSerialNumber = plus.decrypt(svcNum);
        String psptId = plus.decrypt(custCertNo);
        String psptName = plus.decrypt(custCertName);

        IData resParam = new DataMap();
        resParam.put("OPR_NUMB", transactionID);//操作的流水号
        resParam.put("CHANNEL_ID", channelId);//渠道标识
        resParam.put("RES_NO", preSerialNumber);//调资源接口需传预占号码
        resParam.put("RES_TRADE_CODE", "IRes_NetSel_MphoneCode");//普通网上选号 李全修改
        resParam.put("OCCUPY_TYPE_CODE", "1");//选占类型,1：网上选占
        resParam.put("RES_TYPE_CODE", "0");//0-号码
        resParam.put("USER_EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        resParam.put("ROUTE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        resParam.put("PSPT_ID", psptId);//选占证件号码
        resParam.put("PSPT_TYPE", "");//选占证件类型，非必传
        resParam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode()); // 受理地州
        resParam.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode()); // 受理业务区
        resParam.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId()); // 受理部门
        resParam.put("TRADE_STAFF_ID", operCode); // 受理员工
        resParam.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode()); // 接入渠道
        resParam.put("X_TAG", "3"); // 网上选号标记
        try {
            ResCall.resTempOccupyByNetSel(resParam);
        } catch (Exception e) {
            returnMap.put("respCode", ComFuncUtil.CODE_ERROR);
            returnMap.put("respDesc", "调用资源接口异常" + e.getMessage());
            return ComFuncUtil.transOutParam(returnMap);
        }

        resultItem.put("isSuc", "0"); // 是否预占成功	0预占成功，1预占失败
        returnMap.put("respCode", ComFuncUtil.CODE_RIGHT);
        returnMap.put("respDesc", "success");
        return ComFuncUtil.transOutParam(returnMap);
    }

    /**
     * 3.3.4.4可选套餐/活动列表查询接口(queryProductList)
     * 实名认证系统在线上选号业务环节中，通过此接口获取用户可选择订购的套餐/活动。
     * 封装产商品接口：UPC.Out.ICMOnlineFSV.queryProductList
     */
    public IData C898HQQueryProductList(IData inParamStr) throws Exception {
        IData inParam = new DataMap(inParamStr.toString());
        IData result = UpcCall.queryProductList(inParam);
        IDataset productList = result.getData("object").getDataset("result").getData(0).getDataset("resInfo");
        if (IDataUtil.isEmpty(productList)) {
            return result;
        }
        String staffId = inParam.getData("params").getData("reqInfo").getString("operCode");
        ProductPrivUtil.filterProductListByPriv(staffId, productList);
        return result;
    }

    public IData C898HQCheckSimMachine(IData inParamStr) throws Exception {
        IData inParam = new DataMap(inParamStr.toString());
        IData params = inParam.getData("params");
        IDataset inParamSet1 = new DatasetList();
        IData set1Map1 = new DataMap();
        set1Map1.put("FIELD", "busiCode"); // 业务编码	CHECK_SIM
        set1Map1.put("TYPE", "String");
        inParamSet1.add(set1Map1);
        IData set1Map2 = new DataMap();
        set1Map2.put("FIELD", "sourceCode"); // 请求源编码
        set1Map2.put("TYPE", "String");
        inParamSet1.add(set1Map2);
        IData set1Map3 = new DataMap();
        set1Map3.put("FIELD", "targetCode"); // 落地方编码
        set1Map3.put("TYPE", "String");
        inParamSet1.add(set1Map3);
        IData set1Map4 = new DataMap();
        set1Map4.put("FIELD", "version"); // 报文版本
        set1Map4.put("TYPE", "String");
        inParamSet1.add(set1Map4);
        IData set1Map5 = new DataMap();
        set1Map5.put("FIELD", "reqInfo"); // 请求信息
        set1Map5.put("TYPE", "Map");
        inParamSet1.add(set1Map5);
        IData inParamResult = ComFuncUtil.checkInParam(params, inParamSet1);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        IData reqInfo = params.getData("reqInfo");
        IDataset inParamSet2 = new DatasetList();
        IData set2Map1 = new DataMap();
        set2Map1.put("FIELD", "transactionID"); // 全网唯一操作流水号
        set2Map1.put("TYPE", "String");
        inParamSet2.add(set2Map1);
        IData set2Map2 = new DataMap();
        set2Map2.put("FIELD", "svcNum"); // 开户的电话号码
        set2Map2.put("TYPE", "String");
        inParamSet2.add(set2Map2);
        IData set2Map3 = new DataMap();
        set2Map3.put("FIELD", "operCode"); // 登录员工工号
        set2Map3.put("TYPE", "String");
        inParamSet2.add(set2Map3);
        IData set2Map4 = new DataMap();
        set2Map4.put("FIELD", "channelId"); // 员工所属渠道
        set2Map4.put("TYPE", "String");
        inParamSet2.add(set2Map4);
        IData set2Map5 = new DataMap();
        set2Map5.put("FIELD", "simType"); // Sim卡类型	1:实体卡 2:空白卡
        set2Map5.put("TYPE", "String");
        inParamSet2.add(set2Map5);
        IData set2Map6 = new DataMap();
        set2Map6.put("FIELD", "sim"); // Sim或空白卡序列号
        set2Map6.put("TYPE", "String");
        inParamSet2.add(set2Map6);
        inParamResult = ComFuncUtil.checkInParam(reqInfo, inParamSet2);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        String svcNum = reqInfo.getString("svcNum");
        String sim = reqInfo.getString("sim");
        IData returnMap = new DataMap();
        IDataset result = new DatasetList();
        returnMap.put("result", result);
        IData resultItem = new DataMap();
        result.add(resultItem);
        resultItem.put("transactionID", reqInfo.getString("transactionID"));
        try {
            IData input = new DataMap();
            input.put("PHONE_NUM", "1");
            input.put("EMPTY_CARD_ID", sim);
            input.put("IS_NEW", "1");
            input.put("MODE", "1");
            input.put("SERIAL_NUMBER", svcNum);
            ResCall.checkEmptycardInfo(sim, "IDLE");
        } catch (Exception e) {
            resultItem.put("checkState", "1"); // 校验状态	0 校验成功，1失败
            resultItem.put("campOonState", "1"); // 预占状态:0预占成功，1预占失败
            returnMap.put("respCode", ComFuncUtil.CODE_ERROR);
            returnMap.put("respDesc", "SIM卡预占失败" + e.getMessage());
            return ComFuncUtil.transOutParam(returnMap);
        }
        resultItem.put("checkState", "0"); // 校验状态	0 校验成功，1失败
        resultItem.put("campOonState", "0"); // 预占状态:0预占成功，1预占失败
        returnMap.put("respCode", ComFuncUtil.CODE_RIGHT);
        returnMap.put("respDesc", "success");
        return ComFuncUtil.transOutParam(returnMap);
    }

    /**
     * 根据套餐和营销活动获取费用明细接口(queryOfferPrices)
     * 实名认证系统将开户号码、开户套餐、开户营销活动等信息传给省公司，
     * 省公司返回需要缴纳的费用明细信息，用于在选号页面上展示给用户，告知购买号码需要支付的金额。
     */
    public IData C898HQQueryOfferPrices(IData inParamStr) throws Exception {
        IData inParam = new DataMap(inParamStr.toString());
        IData params = inParam.getData("params");
        IDataset inParamSet1 = new DatasetList();
        IData set1Map1 = new DataMap();
        set1Map1.put("FIELD", "busiCode"); // 业务编码	QUERY_OFFER_PRICES
        set1Map1.put("TYPE", "String");
        inParamSet1.add(set1Map1);
        IData set1Map2 = new DataMap();
        set1Map2.put("FIELD", "sourceCode"); // 请求源编码
        set1Map2.put("TYPE", "String");
        inParamSet1.add(set1Map2);
        IData set1Map3 = new DataMap();
        set1Map3.put("FIELD", "targetCode"); // 落地方编码
        set1Map3.put("TYPE", "String");
        inParamSet1.add(set1Map3);
        IData set1Map4 = new DataMap();
        set1Map4.put("FIELD", "version"); // 报文版本
        set1Map4.put("TYPE", "String");
        inParamSet1.add(set1Map4);
        IData set1Map5 = new DataMap();
        set1Map5.put("FIELD", "reqInfo"); // 请求信息
        set1Map5.put("TYPE", "Map");
        inParamSet1.add(set1Map5);
        IData inParamResult = ComFuncUtil.checkInParam(params, inParamSet1);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        IData reqInfo = params.getData("reqInfo");
        IDataset inParamSet2 = new DatasetList();
        IData set2Map1 = new DataMap();
        set2Map1.put("FIELD", "transactionID"); // 全网唯一操作流水号
        set2Map1.put("TYPE", "String");
        inParamSet2.add(set2Map1);
        IData set2Map2 = new DataMap();
        set2Map2.put("FIELD", "svcNum"); // 开户号码
        set2Map2.put("TYPE", "String");
        inParamSet2.add(set2Map2);
        IData set2Map3 = new DataMap();
        set2Map3.put("FIELD", "operCode"); // 登录员工工号
        set2Map3.put("TYPE", "String");
        inParamSet2.add(set2Map3);
        IData set2Map4 = new DataMap();
        set2Map4.put("FIELD", "channelId"); // 员工所属渠道
        set2Map4.put("TYPE", "String");
        inParamSet2.add(set2Map4);
        IData set2Map5 = new DataMap();
        set2Map5.put("FIELD", "productCode"); // 用户选择套餐
        set2Map5.put("TYPE", "String");
        inParamSet2.add(set2Map5);
        inParamResult = ComFuncUtil.checkInParam(reqInfo, inParamSet2);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        /**********************业务受理*********************/
        IData returnMap = new DataMap();
        IDataset result = new DatasetList();
        returnMap.put("result", result);
        IData resultItem = new DataMap();
        result.add(resultItem);
        String transactionID = reqInfo.getString("transactionID");
        String svcNum = reqInfo.getString("svcNum");
        String operCode = reqInfo.getString("operCode");
        String channelId = reqInfo.getString("channelId");
        String productCode = reqInfo.getString("productCode");
        String offerCodes = reqInfo.getString("offerCodes", "");
        String sim = reqInfo.getString("sim", "");
        String simType = reqInfo.getString("simType", "");

        resultItem.put("transactionID", transactionID);
        IData resInfo = new DataMap();
        resultItem.put("resInfo", resInfo);
        IDataset priceInfo = new DatasetList();
        resInfo.put("priceInfo", priceInfo);
        int total = 0;
        int priceInfoSize = 0;

        // 根据productCode获取产品套餐费用
        IDataset product_fee = UpcCall.qryDynamicPrice(productCode, BofConst.ELEMENT_TYPE_CODE_PRODUCT, "-1", null, "10", null, null, null);
        if (IDataUtil.isNotEmpty(product_fee)) {
            for (int i = 0; i < product_fee.size(); i++) {
                if (priceInfoSize >= 20) {
                    break;
                }
                priceInfoSize++;
                IData priceMap = product_fee.getData(i);
                priceMap.put("priceItem", priceMap.getString("PRICE_PLAN_NAME")); // 收费项目
                priceMap.put("priceFee", priceMap.getString("FEE")); // 收费金额 以分为单位
                priceMap.put("priceDesc", priceMap.getString("PRICE_PLAN_NAME")); // 收费项目描述
                total = total + Integer.parseInt(priceMap.getString("FEE")); // 总费用 以分为单位
            }
            priceInfo.addAll(product_fee);
            if (priceInfoSize >= 20) {
                resInfo.put("total", String.valueOf(total));
                returnMap.put("respCode", ComFuncUtil.CODE_RIGHT);
                returnMap.put("respDesc", "success");
                return ComFuncUtil.transOutParam(returnMap);
            }
        } else {
            returnMap.put("respCode", ComFuncUtil.CODE_ERROR);
            returnMap.put("respDesc", "未查到费用信息");
            return ComFuncUtil.transOutParam(returnMap);
        }

        // 根据offerCodes获取营销活动费用
        if (StringUtils.isNotBlank(offerCodes)) {
            String[] offerCodeArr = offerCodes.split("\\|");
            for (int i = 0; i < offerCodeArr.length; i++) {
                String saleactiveCode = offerCodeArr[i];
                IDataset saleactiveCode_fee = UpcCall.qryDynamicPrice(saleactiveCode, BofConst.ELEMENT_TYPE_CODE_PACKAGE, "-1", null, BofConst.TRADE_TYPE_CODE_SALEACTIVE, null, null, null);
                if (IDataUtil.isNotEmpty(saleactiveCode_fee)) {
                    for (int j = 0; j < saleactiveCode_fee.size(); j++) {
                        if (priceInfoSize >= 20) {
                            break;
                        }
                        priceInfoSize++;
                        IData priceMap = saleactiveCode_fee.getData(j);
                        priceMap.put("priceItem", priceMap.getString("PRICE_PLAN_NAME")); // 收费项目
                        priceMap.put("priceFee", priceMap.getString("FEE")); // 收费金额 以分为单位
                        priceMap.put("priceDesc", "营销活动" + priceMap.getString("PRICE_PLAN_NAME")); // 收费项目描述
                        total = total + Integer.parseInt(priceMap.getString("FEE")); // 总费用 以分为单位
                    }
                    priceInfo.addAll(saleactiveCode_fee);
                    if (priceInfoSize >= 20) {
                        resInfo.put("total", String.valueOf(total));
                        returnMap.put("respCode", ComFuncUtil.CODE_RIGHT);
                        returnMap.put("respDesc", "success");
                        return ComFuncUtil.transOutParam(returnMap);
                    }
                } else {
                    returnMap.put("respCode", ComFuncUtil.CODE_ERROR);
                    returnMap.put("respDesc", "根据offerCodes未查到营销活动费用信息");
                    return ComFuncUtil.transOutParam(returnMap);
                }
            }
        }

        // 根据sim获取SIM卡费用
        if (StringUtils.isNotBlank(sim) && StringUtils.isNotBlank(simType)) {
            IDataset simCardInfo = ResCall.getSimCardInfo("0", sim, "", "0", "");
            if (IDataUtil.isEmpty(simCardInfo)) {
                returnMap.put("respCode", ComFuncUtil.CODE_ERROR);
                returnMap.put("respDesc", "根据sim卡号未查询到SIM卡信息");
                return ComFuncUtil.transOutParam(returnMap);
            }
            String res_kind_code = simCardInfo.getData(0).getString("RES_KIND_CODE");
            String res_type_code = simCardInfo.getData(0).getString("RES_TYPE_CODE");
            IData simCardFeeInfo = DevicePriceQry.getDevicePrice(BizRoute.getRouteId(), "-1", "10", res_kind_code, res_type_code);
            String simCardFee = simCardFeeInfo.getString("DEVICE_PRICE", "0");
            if (!"0".equals(simCardFee)) {
                simCardFeeInfo.put("priceItem", "sim卡费用");
                simCardFeeInfo.put("priceFee", simCardFee);
                simCardFeeInfo.put("priceDesc", "sim卡费用");
                priceInfo.add(simCardFeeInfo);
                total = total + Integer.parseInt(simCardFee); // 总费用 以分为单位
            }
        }
        resInfo.put("total", String.valueOf(total));
        returnMap.put("respCode", ComFuncUtil.CODE_RIGHT);
        returnMap.put("respDesc", "success");
        return ComFuncUtil.transOutParam(returnMap);
    }

    /**
     * 3.3.4.9写卡信息获取接口(queryWriteCardInfo) 人证一体机选号开户用
     */
    public IData C898HQQueryWriteCardInfoOpen(IData inParamStr) throws Exception {
        IData inParam = new DataMap(inParamStr.toString());
        IData params = inParam.getData("params");
        IDataset inParamSet1 = new DatasetList();
        IData set1Map1 = new DataMap();
        set1Map1.put("FIELD", "busiCode"); // 业务编码	QUERY_WRITE_CARD_INFO
        set1Map1.put("TYPE", "String");
        inParamSet1.add(set1Map1);
        IData set1Map2 = new DataMap();
        set1Map2.put("FIELD", "sourceCode"); // 请求源编码
        set1Map2.put("TYPE", "String");
        inParamSet1.add(set1Map2);
        IData set1Map3 = new DataMap();
        set1Map3.put("FIELD", "targetCode"); // 落地方编码
        set1Map3.put("TYPE", "String");
        inParamSet1.add(set1Map3);
        IData set1Map4 = new DataMap();
        set1Map4.put("FIELD", "version"); // 报文版本
        set1Map4.put("TYPE", "String");
        inParamSet1.add(set1Map4);
        IData set1Map5 = new DataMap();
        set1Map5.put("FIELD", "reqInfo"); // 请求信息
        set1Map5.put("TYPE", "Map");
        inParamSet1.add(set1Map5);
        IData inParamResult = ComFuncUtil.checkInParam(params, inParamSet1);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        IData reqInfo = params.getData("reqInfo");
        IDataset inParamSet2 = new DatasetList();
        IData set2Map1 = new DataMap();
        set2Map1.put("FIELD", "transactionID"); // 全网唯一操作流水号
        set2Map1.put("TYPE", "String");
        inParamSet2.add(set2Map1);
        IData set2Map2 = new DataMap();
        set2Map2.put("FIELD", "svcNum"); // 全网唯一操作流水号
        set2Map2.put("TYPE", "String");
        inParamSet2.add(set2Map2);
        IData set2Map3 = new DataMap();
        set2Map3.put("FIELD", "operCode"); // 登录员工工号
        set2Map3.put("TYPE", "String");
        inParamSet2.add(set2Map3);
        IData set2Map4 = new DataMap();
        set2Map4.put("FIELD", "channelId"); // 员工所属渠道
        set2Map4.put("TYPE", "String");
        inParamSet2.add(set2Map4);
        IData set2Map5 = new DataMap();
        set2Map5.put("FIELD", "sn"); // 空白卡序列号
        set2Map5.put("TYPE", "String");
        inParamSet2.add(set2Map5);
        inParamResult = ComFuncUtil.checkInParam(reqInfo, inParamSet2);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }
        /**********************业务受理*********************/
        IData returnMap = new DataMap();
        IData result = new DataMap();
        returnMap.put("result", result);
        String transactionID = reqInfo.getString("transactionID", "");
        String svcNum = reqInfo.getString("svcNum", "");
        String sn = reqInfo.getString("sn", "");
        result.put("transactionID", transactionID);
        IDataset writeResult = new DatasetList();
        try {
            writeResult = ResCall.queryWriteCardInfo(svcNum, sn, "2", "2", "0");
        } catch (Exception e) {
            String errMsg = ExceptionUtils.getExceptionInfo(e).getString("RESULT_INFO", "");
            if (errMsg.length() > 500) {
                errMsg = errMsg.substring(0, 500);
            }
            returnMap.put("respCode", ComFuncUtil.CODE_ERROR);
            returnMap.put("respDesc", "获取可用的写卡基础数据信息失败" + errMsg);
            return ComFuncUtil.transOutParam(returnMap);
        }

        if (IDataUtil.isEmpty(writeResult)) {
            returnMap.put("respCode", ComFuncUtil.CODE_ERROR);
            returnMap.put("respDesc", "获取可用的写卡基础数据信息失败!");
            return ComFuncUtil.transOutParam(returnMap);
        }
        IData spesiminfo = writeResult.getData(0);
        String imsi = spesiminfo.getString("IMSI", "");
        String iccid = spesiminfo.getString("SIM_CARD_NO", "");
        String puk1 = spesiminfo.getString("PUK1", "");
        String puk2 = spesiminfo.getString("PUK2", "");
        String pin1 = spesiminfo.getString("PIN1", "");
        String pin2 = spesiminfo.getString("PIN2", "");
        String ki = spesiminfo.getString("KI", "");
        String smsp = spesiminfo.getString("SMSP", "");
        result.put("imsi", imsi);
        result.put("iccid", iccid);
        result.put("puk1", puk1);
        result.put("puk2", puk2);
        result.put("ki", ki);
        result.put("pin1", pin1);
        result.put("pin2", pin2);
        result.put("smsp", smsp);

        /** test **/
        String tradeId = SeqMgr.getTradeId().substring(6);
        // 调用webservice接口加密
        AssemDynData ass = new AssemDynData();
        EncAssemDynData enAss = new EncAssemDynData();
        List<EncAssemDynData> enAsses = new ArrayList<EncAssemDynData>();
        IssueData issue = new IssueData();
        issue.setIccId(iccid);
        issue.setImsi(imsi);
        issue.setPin1(pin1);
        issue.setPin2(pin2);
        issue.setPuk1(puk1);
        if (StringUtils.isBlank(puk2)) {
            puk2 = "56541837";
        }
        issue.setPuk2(puk2);
        issue.setSmsp(smsp);
        enAss.setMsisdn(svcNum);
        enAss.setIssueData(issue);
        enAsses.add(enAss);
        ass.setChanelflg("1");
        String cardInfo = "080A" + this.replace(iccid) + "0E0A" + sn;
        ass.setCardInfo(cardInfo);
        ass.setEnc(enAsses);
        ass.setSeqNo(tradeId);
        WebServiceClient client = new WebServiceClient();
        EncAssemDynDataRsp resAssemData = client.encAssemClient(ass);
        String encodeStr = resAssemData.getIssueData();
        String result_code = resAssemData.getResultCode();
        if (!"0".equals(result_code)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用现场实时写卡系统失败");
        }
        /** test **/

        result.put("writeCardData", encodeStr);
        returnMap.put("respCode", ComFuncUtil.CODE_RIGHT);
        returnMap.put("respDesc", "success");
        return ComFuncUtil.transOutParam(returnMap);
    }

    private static String replace(String a) {
        char[] chars = a.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i % 2 == 0) {
                char a1 = chars[i];
                chars[i] = chars[i + 1];
                chars[i + 1] = a1;
            }
        }
        String a2 = String.valueOf(chars);
        return a2;
    }

    public IData C898HQReturnWriteCardResultMachine(IData inParamStr) throws Exception {
        IData inParam = new DataMap(inParamStr.toString());
        IData params = inParam.getData("params");
        IDataset inParamSet1 = new DatasetList();
        IData set1Map1 = new DataMap();
        set1Map1.put("FIELD", "busiCode"); // 业务编码	RETURN_WRITE_CARD_RESULT
        set1Map1.put("TYPE", "String");
        inParamSet1.add(set1Map1);
        IData set1Map2 = new DataMap();
        set1Map2.put("FIELD", "sourceCode"); // 请求源编码
        set1Map2.put("TYPE", "String");
        inParamSet1.add(set1Map2);
        IData set1Map3 = new DataMap();
        set1Map3.put("FIELD", "targetCode"); // 落地方编码
        set1Map3.put("TYPE", "String");
        inParamSet1.add(set1Map3);
        IData set1Map4 = new DataMap();
        set1Map4.put("FIELD", "version"); // 报文版本
        set1Map4.put("TYPE", "String");
        inParamSet1.add(set1Map4);
        IData set1Map5 = new DataMap();
        set1Map5.put("FIELD", "reqInfo"); // 请求信息
        set1Map5.put("TYPE", "Map");
        inParamSet1.add(set1Map5);
        IData inParamResult = ComFuncUtil.checkInParam(params, inParamSet1);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        IData reqInfo = params.getData("reqInfo");
        IDataset inParamSet2 = new DatasetList();
        IData set2Map1 = new DataMap();
        set2Map1.put("FIELD", "transactionID"); // 全网唯一操作流水号
        set2Map1.put("TYPE", "String");
        inParamSet2.add(set2Map1);
        IData set2Map2 = new DataMap();
        set2Map2.put("FIELD", "operCode"); // 登录员工工号
        set2Map2.put("TYPE", "String");
        inParamSet2.add(set2Map2);
        IData set2Map3 = new DataMap();
        set2Map3.put("FIELD", "channelId"); // 员工所属渠道
        set2Map3.put("TYPE", "String");
        inParamSet2.add(set2Map3);
        IData set2Map4 = new DataMap();
        set2Map4.put("FIELD", "svcNum"); // 开户号码
        set2Map4.put("TYPE", "String");
        inParamSet2.add(set2Map4);
        IData set2Map5 = new DataMap();
        set2Map5.put("FIELD", "sn"); // 空白卡序列号
        set2Map5.put("TYPE", "String");
        inParamSet2.add(set2Map5);
        IData set2Map6 = new DataMap();
        set2Map6.put("FIELD", "opResult"); // 写卡结果	0成功 1失败
        set2Map6.put("TYPE", "String");
        inParamSet2.add(set2Map6);
        IData set2Map7 = new DataMap();
        set2Map7.put("FIELD", "resultDesc"); // 写卡结果描述 文字描述
        set2Map7.put("TYPE", "String");
        inParamSet2.add(set2Map7);
        inParamResult = ComFuncUtil.checkInParam(reqInfo, inParamSet2);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        /**********************业务受理*********************/
        IData returnMap = new DataMap();
        IDataset result = new DatasetList();
        returnMap.put("result", result);
        IData resultItem = new DataMap();
        result.add(resultItem);
        String transid = reqInfo.getString("transactionID", "");
        resultItem.put("transactionID", transid);

        String operCode = reqInfo.getString("operCode", "");
        String channelId = reqInfo.getString("channelId", "");
        String svcNum = reqInfo.getString("svcNum", "");
        String sn = reqInfo.getString("sn", "");
        String opResult = reqInfo.getString("opResult", "");
        String imsi = reqInfo.getString("imsi", "");
        String iccid = reqInfo.getString("iccid", "");
        String cardKi = reqInfo.getString("cardKi", "");

        IDataset resInfos = ResCall.backWriteSimCard(imsi, sn, "", CSBizBean.getTradeEparchyCode(), "", iccid, "", "", "2", "");
        if (IDataUtil.isEmpty(resInfos) || !"0".equals(resInfos.getData(0).getString("X_RESULTCODE"))) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "写卡信息回写错误！");
        }

        returnMap.put("respCode", ComFuncUtil.CODE_RIGHT);
        returnMap.put("respDesc", "success");
        return ComFuncUtil.transOutParam(returnMap);
    }

    public IData C898HQReturnWriteCardResultReplace(IData inParamStr) throws Exception {
        IData inParam = new DataMap(inParamStr.toString());
        IData params = inParam.getData("params");
        IDataset inParamSet1 = new DatasetList();
        IData set1Map1 = new DataMap();
        set1Map1.put("FIELD", "busiCode"); // 业务编码	RETURN_WRITE_CARD_RESULT
        set1Map1.put("TYPE", "String");
        inParamSet1.add(set1Map1);
        IData set1Map2 = new DataMap();
        set1Map2.put("FIELD", "sourceCode"); // 请求源编码
        set1Map2.put("TYPE", "String");
        inParamSet1.add(set1Map2);
        IData set1Map3 = new DataMap();
        set1Map3.put("FIELD", "targetCode"); // 落地方编码
        set1Map3.put("TYPE", "String");
        inParamSet1.add(set1Map3);
        IData set1Map4 = new DataMap();
        set1Map4.put("FIELD", "version"); // 报文版本
        set1Map4.put("TYPE", "String");
        inParamSet1.add(set1Map4);
        IData set1Map5 = new DataMap();
        set1Map5.put("FIELD", "reqInfo"); // 请求信息
        set1Map5.put("TYPE", "Map");
        inParamSet1.add(set1Map5);
        IData inParamResult = ComFuncUtil.checkInParam(params, inParamSet1);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        IData reqInfo = params.getData("reqInfo");
        IDataset inParamSet2 = new DatasetList();
        IData set2Map1 = new DataMap();
        set2Map1.put("FIELD", "transactionID"); // 全网唯一操作流水号
        set2Map1.put("TYPE", "String");
        inParamSet2.add(set2Map1);
        IData set2Map2 = new DataMap();
        set2Map2.put("FIELD", "operCode"); // 登录员工工号
        set2Map2.put("TYPE", "String");
        inParamSet2.add(set2Map2);
        IData set2Map3 = new DataMap();
        set2Map3.put("FIELD", "channelId"); // 员工所属渠道
        set2Map3.put("TYPE", "String");
        inParamSet2.add(set2Map3);
        IData set2Map4 = new DataMap();
        set2Map4.put("FIELD", "svcNum"); // 开户号码
        set2Map4.put("TYPE", "String");
        inParamSet2.add(set2Map4);
        IData set2Map5 = new DataMap();
        set2Map5.put("FIELD", "sn"); // 空白卡序列号
        set2Map5.put("TYPE", "String");
        inParamSet2.add(set2Map5);
        IData set2Map6 = new DataMap();
        set2Map6.put("FIELD", "opResult"); // 写卡结果	0成功 1失败
        set2Map6.put("TYPE", "String");
        inParamSet2.add(set2Map6);
        IData set2Map7 = new DataMap();
        set2Map7.put("FIELD", "resultDesc"); // 写卡结果描述 文字描述
        set2Map7.put("TYPE", "String");
        inParamSet2.add(set2Map7);
        inParamResult = ComFuncUtil.checkInParam(reqInfo, inParamSet2);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        /**********************业务受理*********************/
        IData returnMap = new DataMap();
        IDataset result = new DatasetList();
        returnMap.put("result", result);
        IData resultItem = new DataMap();
        result.add(resultItem);
        String transid = reqInfo.getString("transactionID", "");
        resultItem.put("transactionID", transid);

        String operCode = reqInfo.getString("operCode", "");
        String channelId = reqInfo.getString("channelId", "");
        String svcNum = reqInfo.getString("svcNum", "");
        String sn = reqInfo.getString("sn", "");
        String opResult = reqInfo.getString("opResult", "");
        String imsi = reqInfo.getString("imsi", "");
        String iccid = reqInfo.getString("iccid", "");
        String cardKi = reqInfo.getString("cardKi", "");

        IDataset resInfos = ResCall.backWriteSimCard(imsi, sn, "", CSBizBean.getTradeEparchyCode(), "", iccid, "", "", "2", "");
        if (IDataUtil.isEmpty(resInfos) || !"0".equals(resInfos.getData(0).getString("X_RESULTCODE"))) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "写卡信息回写错误！");
        }

        // 回写完成之后调用后调资源对写卡后的SIM卡卡号做校验（注：避免后面完工的时候资源报没有预占记录）。
        IData simParam = new DataMap();
        simParam.put("sourceCode", params.getString("sourceCode"));
        simParam.put("targetCode", params.getString("targetCode"));
        simParam.put("busiCode", params.getString("busiCode"));
        simParam.put("version", params.getString("version"));
        simParam.put("transactionID", transid);
        IData simParamReq = new DataMap();
        simParam.put("reqInfo", simParamReq);
        simParamReq.put("simType", "2");
        simParamReq.put("channelId", channelId);
        simParamReq.put("sim", sn);
        simParamReq.put("svcNum", svcNum);
        simParamReq.put("operCode", operCode);
        simParamReq.put("transactionID", transid);
        simParamReq.put("xChoiceTag", "1");
        ResCall.onlineCheckSim(simParam); // 选占

        returnMap.put("respCode", ComFuncUtil.CODE_RIGHT);
        returnMap.put("respDesc", "success");
        return ComFuncUtil.transOutParam(returnMap);
    }

    /**
     * 3.3.4.11获取电子工单信息(queryElectronicWorkOrder)
     * 实名认证平台在APP+NFC模式做开户完成之后，获取各省电子工单信息，生成电子工单。
     */
    public IData C898HQQueryElectronicWorkOrder(IData inParamStr) throws Exception {
        IData inParam = new DataMap(inParamStr.toString());
        IData params = inParam.getData("params");
        IDataset inParamSet1 = new DatasetList();
        IData set1Map1 = new DataMap();
        set1Map1.put("FIELD", "busiCode"); // 业务编码	QUERY_ELECTRONIC_WORK_ORDER
        set1Map1.put("TYPE", "String");
        inParamSet1.add(set1Map1);
        IData set1Map2 = new DataMap();
        set1Map2.put("FIELD", "sourceCode"); // 请求源编码
        set1Map2.put("TYPE", "String");
        inParamSet1.add(set1Map2);
        IData set1Map3 = new DataMap();
        set1Map3.put("FIELD", "targetCode"); // 落地方编码
        set1Map3.put("TYPE", "String");
        inParamSet1.add(set1Map3);
        IData set1Map4 = new DataMap();
        set1Map4.put("FIELD", "version"); // 报文版本	1.0
        set1Map4.put("TYPE", "String");
        inParamSet1.add(set1Map4);
        IData set1Map5 = new DataMap();
        set1Map5.put("FIELD", "reqInfo"); // 请求信息
        set1Map5.put("TYPE", "Map");
        inParamSet1.add(set1Map5);
        IData inParamResult = ComFuncUtil.checkInParam(params, inParamSet1);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        IData reqInfo = params.getData("reqInfo");
        IDataset inParamSet2 = new DatasetList();
        IData set2Map1 = new DataMap();
        set2Map1.put("FIELD", "transactionID"); // 全网唯一操作流水号
        set2Map1.put("TYPE", "String");
        inParamSet2.add(set2Map1);
        IData set2Map2 = new DataMap();
        set2Map2.put("FIELD", "operCode"); // 登录员工工号
        set2Map2.put("TYPE", "String");
        inParamSet2.add(set2Map2);
        IData set2Map3 = new DataMap();
        set2Map3.put("FIELD", "channelId"); // 员工所属渠道
        set2Map3.put("TYPE", "String");
        inParamSet2.add(set2Map3);
        IData set2Map4 = new DataMap();
        set2Map4.put("FIELD", "svcNum"); //  开户号码	加密传输
        set2Map4.put("TYPE", "String");
        inParamSet2.add(set2Map4);
        IData set2Map5 = new DataMap();
        set2Map5.put("FIELD", "trade_id"); //  trade_id
        set2Map5.put("TYPE", "String");
        inParamSet2.add(set2Map5);
        inParamResult = ComFuncUtil.checkInParam(reqInfo, inParamSet2);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        /***************************业务受理****************************/
        String transactionID = reqInfo.getString("transactionID");
        String svcNum = reqInfo.getString("svcNum");
        String trade_id = reqInfo.getString("trade_id");

        // 根据trade_id查询对应的order_id
        IDataset tradeInfo = qryTradeAndTradeHisInfo(trade_id);
        if (IDataUtil.isEmpty(tradeInfo)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据trade_id未查到用户开户信息");
        }

        IData returnMap = new DataMap();
        returnMap.put("rtnCode", "0");
        returnMap.put("rtnMsg", "成功!");
        IData object = new DataMap();
        returnMap.put("object", object);
        object.put("respCode", "0");
        object.put("respDesc", "success");
        IDataset result = new DatasetList();
        object.put("result", result);
        IData resultItem = new DataMap();
        result.add(resultItem);
        resultItem.put("transactionID", transactionID);

        String targetCode = params.getString("targetCode");
        IDataset pubKeySet = CmOnlineUtil.queryPushKey(targetCode);
        if (IDataUtil.isEmpty(pubKeySet)) {
            returnMap.put("respCode", ComFuncUtil.CODE_ERROR);
            returnMap.put("respDesc", "找不到对应的公钥信息");
            return ComFuncUtil.transOutParam(returnMap);
        }
        String pubKey = pubKeySet.getData(0).getString("KEY");
        RealNameMsDesPlus plus = new RealNameMsDesPlus(pubKey);

        String svcNumDec = plus.decrypt(svcNum);
        String order_id = tradeInfo.getData(0).getString("ORDER_ID");
        IDataset elecTradeTransSet = this.qryElectTradeTransInfo1(svcNumDec, order_id);
        if (IDataUtil.isEmpty(elecTradeTransSet)) {
            returnMap.put("rtnCode", ComFuncUtil.CODE_ERROR);
            returnMap.put("rtnMsg", "根据号码未查到电子工单信息!");
            object.put("respCode", ComFuncUtil.CODE_ERROR);
            object.put("respDesc", "根据号码未查到电子工单信息");
            return returnMap;
        }
        IData orderItem = new DataMap();
        resultItem.put("orderItem", orderItem);
        orderItem.put("orderCode", elecTradeTransSet.getData(0).getString("TRANSACTION_ID"));
        String regionId = elecTradeTransSet.getData(0).getString("REGION_ID");
        orderItem.put("regionId", regionId);
        String beLongCity = StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", regionId);
        orderItem.put("regionName", beLongCity);

        String staffCode = elecTradeTransSet.getData(0).getString("TRADE_STAFF_ID");
        orderItem.put("staffCode", staffCode);
        IDataset staffInfoSet = StaffInfoQry.queryValidStaffById(staffCode);
        String staffName = "";
        if (IDataUtil.isNotEmpty(staffInfoSet)) {
            staffName = staffInfoSet.getData(0).getString("STAFF_NAME");
        }
        orderItem.put("staffName", staffName);
        String depart_id = elecTradeTransSet.getData(0).getString("CHANNEL_ID");
        orderItem.put("depart_id", depart_id);

        //查询受理部门名称
        StringBuilder sql1 = new StringBuilder();
        sql1.append("SELECT * FROM TD_M_DEPART T WHERE T.DEPART_ID=:DEPART_ID ");
        IData input1 = new DataMap();
        input1.put("DEPART_ID", depart_id);
        IDataset deptDatas = Dao.qryBySql(sql1, input1, Route.CONN_SYS);
        String depart_name = "";
        if (deptDatas != null && deptDatas.size() > 0) {
            depart_name = deptDatas.getData(0).getString("DEPART_NAME");
        }
        orderItem.put("depart_name", depart_name);
        orderItem.put("trade_type_code", "10");

        orderItem.put("busiContent", elecTradeTransSet.getData(0).getString("BUSI_CONTENT"));
        orderItem.put("notesStr", elecTradeTransSet.getData(0).getString("REMARK"));
        orderItem.put("verifyMode", elecTradeTransSet.getData(0).getString("VERIFY_MODE"));
        return returnMap;
    }

    private IDataset qryTradeAndTradeHisInfo(String TRADE_ID) throws Exception {
        IData param = new DataMap();
        param.put("TRADE_ID", TRADE_ID);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT ORDER_ID,TRADE_ID FROM TF_B_TRADE T1 ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND TRADE_ID = :TRADE_ID ");
        parser.addSQL(" UNION ");
        parser.addSQL(" SELECT ORDER_ID,TRADE_ID FROM TF_BH_TRADE T2 ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND TRADE_ID = :TRADE_ID ");
        return Dao.qryByParse(parser, Route.getJourDb());
    }

    // 查询TF_B_ELECTRADE_TRANS表信息
    public IDataset qryElectTradeTransInfo1(String SERIAL_NUMBER, String order_id) throws Exception {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", SERIAL_NUMBER);
        param.put("ORDER_ID", order_id);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT TRANSACTION_ID, ORDER_ID, ELECTRADE_TAG, TRADE_STAFF_ID, CHANNEL_ID, UPDATE_TIME, ");
        parser.addSQL(" RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_TAG1, RSRV_TAG2,  ");
        parser.addSQL(" RSRV_DATE1, RSRV_DATE2, SERIAL_NUMBER, ELECTRONIC_WORK_ORDER, REGION_ID, BUSI_CONTENT, ");
        parser.addSQL(" VERIFY_MODE, REMARK, STAFF_ID ");
        parser.addSQL(" FROM TF_B_ELECTRADE_TRANS ");
        parser.addSQL(" WHERE SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND ORDER_ID = :ORDER_ID ");
        parser.addSQL(" ORDER BY UPDATE_TIME DESC ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    /**
     * 服务密码校验接口
     *
     * @param data
     * @return
     * @throws Exception
     */
    public IData C898HQPassWDCheck(IData inParamStr) throws Exception {
        IData input = new DataMap(inParamStr.toString());
        IData params = input.getData("params");
        IData data = IntfTransUtil.transInput(params);
        IDataUtil.chkParam(data, "transactionID");
        IDataUtil.chkParam(data, "billId");
        IDataUtil.chkParam(data, "servicePwd");
        String serialNumber = data.getString("billId");
        String passwd = data.getString("servicePwd");

        String targetCode = params.getString("targetCode");
        IDataset pubKeySet = CmOnlineUtil.queryPushKey(targetCode);
        if (IDataUtil.isEmpty(pubKeySet)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "找不到对应的公钥信息");
        }
        String pubKey = pubKeySet.getData(0).getString("KEY");
        RealNameMsDesPlus msDes = new RealNameMsDesPlus(pubKey);

        passwd = msDes.decrypt(passwd);
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("CHECK_MODE", "1");
        param.put("USER_PASSWD", passwd);
        IData result = PasswdMgr.checkUserPasswd(param);
        if (StringUtils.equals(result.getString("RESULT_CODE"), "0")) {
            result.put("TRANSACTION_ID", data.getString("transactionID"));
            result.put("RETURN_CODE", "0000");
            result.put("RETURN_MESSAGE", "SUCCESS");
        }
        IData resultLast = IntfTransUtil.transOutput(result);
        return resultLast;
    }

    /**
     * 3.4.4.4获取写卡基础数据(queryWriteCardInfo)  人证一体机补卡激活用
     */
    public IData C898HQQueryWriteCardInfoReplace(IData inParamStr) throws Exception {
        IData inParam = new DataMap(inParamStr.toString());
        IData params = inParam.getData("params");
        IDataset inParamSet1 = new DatasetList();
        IData set1Map1 = new DataMap();
        set1Map1.put("FIELD", "busiCode"); // 业务编码	QUERY_WRITE_CARD_BASIC_INFO
        set1Map1.put("TYPE", "String");
        inParamSet1.add(set1Map1);
        IData set1Map2 = new DataMap();
        set1Map2.put("FIELD", "sourceCode"); // 请求源编码
        set1Map2.put("TYPE", "String");
        inParamSet1.add(set1Map2);
        IData set1Map3 = new DataMap();
        set1Map3.put("FIELD", "targetCode"); // 落地方编码
        set1Map3.put("TYPE", "String");
        inParamSet1.add(set1Map3);
        IData set1Map4 = new DataMap();
        set1Map4.put("FIELD", "version"); // 报文版本
        set1Map4.put("TYPE", "String");
        inParamSet1.add(set1Map4);
        IData set1Map6 = new DataMap();
        set1Map6.put("FIELD", "reqInfo"); // 请求信息
        set1Map6.put("TYPE", "Map");
        inParamSet1.add(set1Map6);
        IData inParamResult = ComFuncUtil.checkInParam(params, inParamSet1);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        IData reqInfo = params.getData("reqInfo");
        IDataset inParamSet2 = new DatasetList();
        IData set2Map1 = new DataMap();
        set2Map1.put("FIELD", "transactionID"); // 全网唯一操作流水号
        set2Map1.put("TYPE", "String");
        inParamSet2.add(set2Map1);
        IData set2Map2 = new DataMap();
        set2Map2.put("FIELD", "svcNum"); // 补卡号码
        set2Map2.put("TYPE", "String");
        inParamSet2.add(set2Map2);
        IData set2Map3 = new DataMap();
        set2Map3.put("FIELD", "operCode"); // 登录员工工号
        set2Map3.put("TYPE", "String");
        inParamSet2.add(set2Map3);
        IData set2Map4 = new DataMap();
        set2Map4.put("FIELD", "sn"); // 空白卡序列号
        set2Map4.put("TYPE", "String");
        inParamSet2.add(set2Map4);
        inParamResult = ComFuncUtil.checkInParam(reqInfo, inParamSet2);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }
        /**********************业务受理*********************/
        String transactionID = reqInfo.getString("transactionID", "");

        IData returnMap = new DataMap();
        IDataset result = new DatasetList();
        returnMap.put("result", result);
        IData resultItem = new DataMap();
        result.add(resultItem);
        resultItem.put("transactionID", transactionID);
        String svcNum = reqInfo.getString("svcNum");
        String sn = reqInfo.getString("sn");

        IDataset writeResult = new DatasetList();
        try {
            writeResult = ResCall.queryWriteCardBasicData(svcNum, sn, "2", "0");
        } catch (Exception e) {
            String errMsg = ExceptionUtils.getExceptionInfo(e).getString("RESULT_INFO", "");
            if (errMsg.length() > 500) {
                errMsg = errMsg.substring(0, 500);
            }
            returnMap.put("respCode", ComFuncUtil.CODE_ERROR);
            returnMap.put("respDesc", "获取可用的写卡基础数据信息失败" + errMsg);
            return ComFuncUtil.transOutParam(returnMap);
        }
        if (IDataUtil.isEmpty(writeResult)) {
            returnMap.put("respCode", ComFuncUtil.CODE_ERROR);
            returnMap.put("respDesc", "获取可用的写卡基础数据信息失败!");
            return ComFuncUtil.transOutParam(returnMap);
        }
        IData spesiminfo = writeResult.getData(0);
        IData resInfo = new DataMap();
        String imsi = spesiminfo.getString("IMSI", "");
        String iccid = spesiminfo.getString("SIM_CARD_NO", "");
        String puk1 = spesiminfo.getString("PUK1", "");
        String puk2 = spesiminfo.getString("PUK2", "");
        String pin1 = spesiminfo.getString("PIN1", "");
        String pin2 = spesiminfo.getString("PIN2", "");
        String smsp = spesiminfo.getString("SMSP", "");
        String ki = spesiminfo.getString("KI", "");
        resInfo.put("imsi", imsi);
        resInfo.put("iccid", iccid);
        resInfo.put("puk1", puk1);
        resInfo.put("puk2", puk2);
        resInfo.put("pin1", pin1);
        resInfo.put("pin2", pin2);
        resInfo.put("ki", ki);
        resInfo.put("smsp", smsp);

        String tradeId = SeqMgr.getTradeId().substring(6);
        AssemDynData ass = new AssemDynData();
        EncAssemDynData enAss = new EncAssemDynData();
        List<EncAssemDynData> enAsses = new ArrayList<EncAssemDynData>();
        IssueData issue = new IssueData();
        issue.setIccId(iccid);
        issue.setImsi(imsi);
        issue.setPin1(pin1);
        issue.setPin2(pin2);
        issue.setPuk1(puk1);
        issue.setPuk2(puk2);
        issue.setSmsp(smsp);
        enAss.setMsisdn(svcNum);
        enAss.setIssueData(issue);
        enAsses.add(enAss);
        ass.setChanelflg("1");
        String cardInfo = "080A" + iccid + "0E0A" + sn;
        ass.setCardInfo(cardInfo);
        ass.setEnc(enAsses);
        ass.setSeqNo(tradeId);
        WebServiceClient client = new WebServiceClient();
        EncAssemDynDataRsp resAssemData = client.encAssemClient(ass);
        String encodeStr = resAssemData.getIssueData();
        String result_code = resAssemData.getResultCode();
        if (!"0".equals(result_code)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用现场实时写卡系统失败");
        }

        resInfo.put("writeCardData", encodeStr);
        String resInfoStr = resInfo.toString();

        String targetCode = params.getString("targetCode");
        IDataset pubKeySet = CmOnlineUtil.queryPushKey(targetCode);
        if (IDataUtil.isEmpty(pubKeySet)) {
            returnMap.put("respCode", ComFuncUtil.CODE_ERROR);
            returnMap.put("respDesc", "找不到对应的公钥信息");
            return ComFuncUtil.transOutParam(returnMap);
        }
        String pubKey = pubKeySet.getData(0).getString("KEY");
        RealNameMsDesPlus plus = new RealNameMsDesPlus(pubKey);
        String resInfoStrEnc = plus.encrypt(resInfoStr);

        resultItem.put("resInfo", resInfoStrEnc);
        returnMap.put("respCode", ComFuncUtil.CODE_RIGHT);
        returnMap.put("respDesc", "success");
        return ComFuncUtil.transOutParam(returnMap);
    }

    /**
     * 3.4.4.6用户状态查询(querySvmStatus)
     * 此接口有两个作用：（1）号码激活后，需要查询号码的状态是否正常；（2）当用户下单购卡时，
     * 校验联系电话是否正常，如是非正常号码，提醒用户更换手机号码，以便及时接收信息
     */
    public IData C898HQQuerySvmStatus(IData inParamStr) throws Exception {
        IData inParam = new DataMap(inParamStr.toString());
        IData params = inParam.getData("params");
        IDataset inParamSet1 = new DatasetList();
        IData set1Map1 = new DataMap();
        set1Map1.put("FIELD", "busiCode"); // 业务编码	QUERY_SVM_STATUS
        set1Map1.put("TYPE", "String");
        inParamSet1.add(set1Map1);
        IData set1Map2 = new DataMap();
        set1Map2.put("FIELD", "sourceCode"); // 请求源编码
        set1Map2.put("TYPE", "String");
        inParamSet1.add(set1Map2);
        IData set1Map3 = new DataMap();
        set1Map3.put("FIELD", "targetCode"); // 落地方编码
        set1Map3.put("TYPE", "String");
        inParamSet1.add(set1Map3);
        IData set1Map4 = new DataMap();
        set1Map4.put("FIELD", "version"); // 报文版本
        set1Map4.put("TYPE", "String");
        inParamSet1.add(set1Map4);
        IData set1Map5 = new DataMap();
        set1Map5.put("FIELD", "reqInfo"); // 请求信息
        set1Map5.put("TYPE", "Map");
        inParamSet1.add(set1Map5);
        IData inParamResult = ComFuncUtil.checkInParam(params, inParamSet1);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        IData reqInfo = params.getData("reqInfo");
        IDataset inParamSet2 = new DatasetList();
        IData set2Map1 = new DataMap();
        set2Map1.put("FIELD", "transactionID"); // 全网唯一操作流水号
        set2Map1.put("TYPE", "String");
        inParamSet2.add(set2Map1);
        IData set2Map2 = new DataMap();
        set2Map2.put("FIELD", "billID"); // 客户手机号码
        set2Map2.put("TYPE", "String");
        inParamSet2.add(set2Map2);
        inParamResult = ComFuncUtil.checkInParam(reqInfo, inParamSet2);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }
        /****************************业务受理****************************/
        IData returnMap = new DataMap();
        IDataset result = new DatasetList();
        returnMap.put("result", result);
        IData resultItem = new DataMap();
        result.add(resultItem);
        resultItem.put("transactionID", reqInfo.getString("transactionID"));
        IData resInfo = new DataMap();
        resultItem.put("resInfo", resInfo);
        String serialNumber = reqInfo.getString("billID");

        IData beanInput = new DataMap();
        beanInput.put("SERIAL_NUMBER", serialNumber);
        IDataset userInfoSet = qryUserInfoDesc(serialNumber);
        if (IDataUtil.isEmpty(userInfoSet)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据手机号码" + serialNumber + "未找到有效的用户信息");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        IData userInfoMap = userInfoSet.getData(0);
        String REMOVE_TAG = userInfoMap.getString("REMOVE_TAG");
        String CUST_ID = userInfoMap.getString("CUST_ID");
        String USER_ID = userInfoMap.getString("USER_ID");
        String USER_STATE_CODESET = userInfoMap.getString("USER_STATE_CODESET");
        String EPARCHY_CODE = userInfoMap.getString("EPARCHY_CODE");
        String CITY_CODE = userInfoMap.getString("CITY_CODE");
        String OPEN_DATE = userInfoMap.getString("OPEN_DATE");
        OPEN_DATE = sdf.format(sdf.parse(OPEN_DATE));

        resInfo.put("custId", CUST_ID); // 用户唯一标识
        resInfo.put("lastOpenTime", OPEN_DATE); // 最近的入网时间	格式:yyyy-MM-dd HH:mm:ss
        resInfo.put("beLongCityCode", EPARCHY_CODE); // 归属地市编码	按照省公司的地市编码规则
        String beLongCity = StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", CITY_CODE);
        resInfo.put("beLongCity", beLongCity); // 归属地市
        String PROVINCE_CODE_NAME = StaticUtil.getStaticValue("PROVINCE_CODE", ProvinceUtil.getProvinceCode());
        String AREA_CODE_NAME = StaticUtil.getStaticValue("AREA_CODE", EPARCHY_CODE);
        String ownerAddress = PROVINCE_CODE_NAME + AREA_CODE_NAME;
        resInfo.put("ownerAddress", ownerAddress); // 归属地名称	归属省+地市名称

        float balance = 0.0f;
        float oweFee = 0.0f;
        IData oweFeeData = AcctCall.getOweFeeByUserId(USER_ID);
        if (IDataUtil.isNotEmpty(oweFeeData)) {
            String ACCT_BALANCE = oweFeeData.getString("ACCT_BALANCE", "");
            if (StringUtils.isNotBlank(ACCT_BALANCE)) {
                balance = Float.parseFloat(ACCT_BALANCE) / 100.0f; //acct_balance < 0表示欠费
            }
            String LAST_OWE_FEE = oweFeeData.getString("LAST_OWE_FEE", "");
            if (StringUtils.isNotBlank(LAST_OWE_FEE)) {
                oweFee = Float.parseFloat(LAST_OWE_FEE) / 100.0f;
            }
        }
        resInfo.put("balance", balance); // 预付费账户余额	单位：元
        resInfo.put("oweFee", oweFee); // 账户欠费金额	单位：元

        String stoptypeCode = "";
        String stoptypeDesc = "";
        if ("0".equals(USER_STATE_CODESET) && "0".equals(REMOVE_TAG) && balance > 0) {
            stoptypeCode = "1";
            stoptypeDesc = "正常";
        } else if (balance < 0) {
            stoptypeCode = "2";
            stoptypeDesc = "欠费";
        } else if (!"0".equals(USER_STATE_CODESET) && "0".equals(REMOVE_TAG)) {
            stoptypeCode = "3";
            stoptypeDesc = "停机";
        } else if (!"0".equals(REMOVE_TAG)) {
            stoptypeCode = "4";
            stoptypeDesc = "销号";
        } else {
            stoptypeCode = "9";
            stoptypeDesc = "其他";
        }
        resInfo.put("stoptypeCode", stoptypeCode); // 用户状态编码	1:正常 2:欠费 3:停机 4:销号 9:其它
        resInfo.put("stoptypeDesc", stoptypeDesc); // 用户状态描述
        if (!"0".equals(REMOVE_TAG)) {
            returnMap.put("respCode", ComFuncUtil.CODE_RIGHT);
            returnMap.put("respDesc", "success");
            return ComFuncUtil.transOutParam(returnMap);
        }

        IData creditInfo = CreditCall.queryUserCreditInfos(USER_ID);
        if (IDataUtil.isEmpty(creditInfo)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未查到用户的信用度信息");
        }
        String creditClass = creditInfo.getString("CREDIT_CLASS"); //-1未评级,0	准星级,1	一星级,2	二星级,3	三星级,4	四星级,5	五星级,6	五星金,7	五星钻
        if ("-1".equals(creditClass)) {
            creditClass = "00";
        } else {
            creditClass = "0" + creditClass;
        }
        resInfo.put("creditLevel", creditClass); // 客户星级	00:准星;01:一星;02:二星;03:三星;04:四星;05:五星;06:五星金;07:五星钻;09:未评级;

        IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(CUST_ID);
        if (IDataUtil.isEmpty(custInfo)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未查到客户信息");
        }
        String targetCode = params.getString("targetCode");
        IDataset pubKeySet = CmOnlineUtil.queryPushKey(targetCode);
        if (IDataUtil.isEmpty(pubKeySet)) {
            returnMap.put("respCode", ComFuncUtil.CODE_ERROR);
            returnMap.put("respDesc", "找不到对应的公钥信息");
            return ComFuncUtil.transOutParam(returnMap);
        }
        String pubKey = pubKeySet.getData(0).getString("KEY");
        RealNameMsDesPlus plus = new RealNameMsDesPlus(pubKey);

        String custCertNo = plus.encrypt(custInfo.getString("PSPT_ID", ""));
        String custName = plus.encrypt(custInfo.getString("CUST_NAME", ""));
        resInfo.put("custCertNo", custCertNo); // 客户身份证号码 加密传输
        resInfo.put("custName", custName); // 客户名称	加密传输
        String custCertType = custInfo.getString("PSPT_TYPE_CODE", "");
        IData psptTypeData = new DataMap();
        psptTypeData.put("0", "00");// 0:本地身份证 -->00:身份证件
        psptTypeData.put("1", "00");// 1:外地身份证-->00:身份证件
        psptTypeData.put("2", "11");// 2:户口本-->11:户口簿
        psptTypeData.put("A", "02");// A:护照-->02:护照
        psptTypeData.put("C", "04");// C:军官证-->04:军官证
        psptTypeData.put("D", "99");// D:单位证明-->99:其他证件
        psptTypeData.put("E", "99");// E:营业执照-->99:其他证件
        psptTypeData.put("G", "99");// G:事业单位法人证书-->99:其他证件
        psptTypeData.put("H", "99");// H:港澳居民回乡证-->99:其他证件
        psptTypeData.put("I", "99");// I:台湾居民回乡证-->99:其他证件
        psptTypeData.put("J", "99");// J:港澳通行证-->99:其他证件
        psptTypeData.put("L", "99");// L:社会团体法人登记证书-->99:其他证件
        psptTypeData.put("M", "99");// M:组织机构代码证-->99:其他证件
        psptTypeData.put("N", "13");// N:台湾居民来往大陆通行证-->13:台湾居民来往大陆通行证
        psptTypeData.put("O", "12");// O:港澳居民来往内地通行证-->12:港澳居民往来内地通行证
        psptTypeData.put("R", "14");// R:外国人永久居留证-->14:外国人永久居留证
        custCertType = psptTypeData.getString(custCertType);
        resInfo.put("custCertType", custCertType); // 证件类型

        String custLevel = "A";
        String IS_REAL_NAME = custInfo.getString("IS_REAL_NAME", "");
        if ("1".equals(IS_REAL_NAME)) {
            custLevel = "E";
        }
        resInfo.put("custLevel", custLevel); // 客户实名等级 A:未登记;B:未登记;C1:已登记;C2:已登记;D1:已审核（联网）;D2:已审核（原件）;E:已审核（芯片）;F:已审核（综合）

        // 销户是查不到主产品信息的
        IDataset userProductInfoSet = UserProductInfoQry.queryUserMainProduct(USER_ID);
        if (IDataUtil.isEmpty(userProductInfoSet)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到用户的主产品信息");
        }
        String product_id = userProductInfoSet.getData(0).getString("PRODUCT_ID");
        IData productInfo = UProductInfoQry.qryProductByPK(product_id);
        String PRODUCT_NAME = productInfo.getString("PRODUCT_NAME");
        resInfo.put("prodprcName", PRODUCT_NAME); // 基本产品资费实例名称
        resInfo.put("prodprcCode", product_id); // 基本产品资费实例编码
        String BRAND_CODE = productInfo.getString("BRAND_CODE");
        String brandname = UBrandInfoQry.getBrandNameByBrandCode(BRAND_CODE);
        resInfo.put("brandname", brandname); // 品牌	以中文返回，如：全球通
        IDataset userResInfo = UserResInfoQry.getUserResInfosByUserIdResTypeCode(USER_ID, "1");
        if (IDataUtil.isEmpty(userResInfo)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询用户资源错误");
        }
        String simCardNo = userResInfo.getData(0).getString("RES_CODE");
        IDataset simCardDatas = ResCall.getSimCardInfo("0", simCardNo, "", null);
        if (IDataUtil.isEmpty(simCardDatas)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据SIM卡号" + simCardNo + "查询资源为空");
        }
        String cardModeType = simCardDatas.getData(0).getString("CARD_MODE_TYPE");
        String opc = simCardDatas.getData(0).getString("OPC", "");
        String Is4GCard = "4G卡";
        if ("1".equals(cardModeType) || ((!("2".equals(cardModeType))) && "".equals(opc))) {
            Is4GCard = "非4G卡";
        }
        resInfo.put("Is4GCard", Is4GCard); // 是否4G卡
        resInfo.put("hlr", ""); // 手机号对应的交换机信息

        returnMap.put("respCode", ComFuncUtil.CODE_RIGHT);
        returnMap.put("respDesc", "success");
        return ComFuncUtil.transOutParam(returnMap);
    }

    // 查询TF_F_USER表
    public IDataset qryUserInfoDesc(String SERIAL_NUMBER) throws Exception {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", SERIAL_NUMBER);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT * ");
        parser.addSQL(" FROM TF_F_USER ");
        parser.addSQL(" WHERE SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND REMOVE_TAG = '0' ");
        parser.addSQL(" ORDER BY OPEN_DATE DESC ");
        return Dao.qryByParse(parser);
    }

}
