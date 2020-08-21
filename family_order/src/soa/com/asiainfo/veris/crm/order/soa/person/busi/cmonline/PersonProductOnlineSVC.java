package com.asiainfo.veris.crm.order.soa.person.busi.cmonline;

import com.ailk.biz.bean.BizDAOLogger;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ComFuncUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CmOnlineUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UDepartInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.rsa.Rsa;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductModuleCalDate;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductTimeEnv;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ExceptionUtils;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.LocalDecPreData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.LocalDecPreDataRsp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.service.WebServiceClient;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.CreatePostPersonUserBean;
import com.asiainfo.veris.crm.order.soa.person.busi.interboss.selfhelpcard.KIFunc;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.other.util.RealNameMsDesPlus;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PersonProductOnlineSVC extends CSBizService {
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(BizDAOLogger.class);

    /**
     * 3.3.1.9.手机号与SIM卡校验接口(simOnlineCheckOrder)
     * 实名认证平台将SIM卡信息发送到省公司，省公司校验SIM卡号和手机号是否一致并且将购卡人的身份信息及订单信息返回给实名制平台。
     */
    public IData C898HQSimOnlineCheckOrder(IData inParamStr) throws Exception {
        IData inParam = new DataMap(inParamStr.toString());
        IData params = inParam.getData("params");
        IData reqInfo = params.getData("reqInfo");
        String transactionID = reqInfo.getString("transactionID");
        String simCardNo = reqInfo.getString("simCardNo");
        String billId = reqInfo.getString("billId");

        IData returnMap = new DataMap();
        returnMap.put("rtnCode", ComFuncUtil.CODE_RIGHT);
        returnMap.put("rtnMsg", "success");
        IData object = new DataMap();
        returnMap.put("object", object);
        object.put("respCode", ComFuncUtil.CODE_RIGHT);
        object.put("respDesc", "success");
        IDataset result = new DatasetList();
        object.put("result", result);
        IData resultItem = new DataMap();
        result.add(resultItem);
        resultItem.put("transactionID", transactionID);
        resultItem.put("returnCode", ComFuncUtil.CODE_RIGHT);
        resultItem.put("returnMessage", "校验成功");
        IData resInfo = new DataMap();
        resultItem.put("resInfo", resInfo);
        String targetCode = params.getString("targetCode");
        IDataset pubKeySet = CmOnlineUtil.queryPushKey(targetCode);
        if (IDataUtil.isEmpty(pubKeySet)) {
            returnMap.put("respCode", ComFuncUtil.CODE_ERROR);
            returnMap.put("respDesc", "找不到对应的公钥信息");
            object.put("respCode", ComFuncUtil.CODE_ERROR);
            object.put("respDesc", "找不到对应的公钥信息");
            resultItem.put("returnCode", ComFuncUtil.CODE_ERROR);
            resultItem.put("returnMessage", "找不到对应的公钥信息");
            return returnMap;
        }
        String pubKey = pubKeySet.getData(0).getString("KEY");
        RealNameMsDesPlus plus = new RealNameMsDesPlus(pubKey);

        IData postParam = new DataMap();
        postParam.put("SIM_CARD_NO", simCardNo);
        postParam.put("BILL_ID", billId);
        SQLParser qryParser = new SQLParser(postParam);
        qryParser.addSQL(" SELECT A.SERIAL_NUMBER, A.SIM_CARD_NO, A.CONDITION1, TO_CHAR(A.CREATE_TIME, 'yyyy-mm-dd hh24:mi:ss') CREATE_TIME ");
        qryParser.addSQL(" FROM TF_B_POSTOPEN A WHERE A.REMOVE_TAG = '0' ");
        qryParser.addSQL(" AND A.SIM_CARD_NO = :SIM_CARD_NO AND A.SERIAL_NUMBER = :BILL_ID ");
        qryParser.addSQL(" AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE ");
        IDataset qrylist = Dao.qryByParse(qryParser, Route.CONN_CRM_CEN);
        if (IDataUtil.isNotEmpty(qrylist)) {
            if (StringUtils.isNotBlank(qrylist.getData(0).getString("CONDITION1"))) {
                //证件号CUST_CERT_NO|用户姓名CUST_NAME|订单号ORDER_ID|
                String[] splitString = qrylist.getData(0).getString("CONDITION1").split("\\|");
                if (splitString.length > 1) {
                    resInfo.put("orderId", splitString[2]);
                    resInfo.put("orderCreate", qrylist.getData(0).getString("CREATE_TIME"));
                    resInfo.put("prestationCreate", qrylist.getData(0).getString("CREATE_TIME"));
                    String psptId = plus.encrypt(splitString[0]);
                    String custName = plus.encrypt(splitString[1]);
                    reqInfo.put("custCertNo", psptId);
                    reqInfo.put("custName", custName);
                    reqInfo.put("busiCategory", "1");
                } else {
                    returnMap.put("respCode", ComFuncUtil.CODE_ERROR);
                    returnMap.put("respDesc", "非网售卡不能办理");
                    object.put("respCode", ComFuncUtil.CODE_ERROR);
                    object.put("respDesc", "非网售卡不能办理");
                    resultItem.put("returnCode", ComFuncUtil.CODE_ERROR);
                    resultItem.put("returnMessage", "非网售卡不能办理");
                    return returnMap;
                }
            } else {
                returnMap.put("respCode", ComFuncUtil.CODE_ERROR);
                returnMap.put("respDesc", "非网售卡不能办理");
                object.put("respCode", ComFuncUtil.CODE_ERROR);
                object.put("respDesc", "非网售卡不能办理");
                resultItem.put("returnCode", ComFuncUtil.CODE_ERROR);
                resultItem.put("returnMessage", "非网售卡不能办理");
                return returnMap;
            }
        } else {
            returnMap.put("respCode", ComFuncUtil.CODE_ERROR);
            returnMap.put("respDesc", "校验失败，未找到售卡记录");
            object.put("respCode", ComFuncUtil.CODE_ERROR);
            object.put("respDesc", "校验失败，未找到售卡记录");
            resultItem.put("returnCode", ComFuncUtil.CODE_ERROR);
            resultItem.put("returnMessage", "校验失败，未找到售卡记录");
            return returnMap;
        }
        return returnMap;
    }

    /**************************************20180529************************************/
    /**
     * 3.1.1.6.用户身份信息校验(authUserIdentInfo)
     * 用户身份信息校验功能。
     */
    public IData C898HQAuthUserIdentInfo(IData inParamStr) throws Exception {
        IData inParam = new DataMap(inParamStr.toString());
        IData params = inParam.getData("params");
        IDataset inParamSet1 = new DatasetList();
        IData set1Map1 = new DataMap();
        set1Map1.put("FIELD", "userMobile"); // 服务号码	客户手机号码
        set1Map1.put("TYPE", "String");
        inParamSet1.add(set1Map1);
        IData set1Map2 = new DataMap();
        set1Map2.put("FIELD", "userName"); // 客户姓名	加密传输，参见总册：加密算法
        set1Map2.put("TYPE", "String");
        inParamSet1.add(set1Map2);
        IData set1Map3 = new DataMap();
        set1Map3.put("FIELD", "idType"); // 证件类型 参见总册：证件类型编码
        set1Map3.put("TYPE", "String");
        inParamSet1.add(set1Map3);
        IData set1Map4 = new DataMap();
        set1Map4.put("FIELD", "idValue"); // 证件号码	加密传输，参见总册：加密算法
        set1Map4.put("TYPE", "String");
        inParamSet1.add(set1Map4);
        IData inParamResult = ComFuncUtil.checkInParam(params, inParamSet1);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        /****************************业务受理**************************/
        String userMobile = params.getString("userMobile", "");
        String userName = params.getString("userName", "");
        String idType = params.getString("idType", "");
        String idValue = params.getString("idValue", "");

        IData returnMap = new DataMap();
        returnMap.put("rtnCode", "0");
        returnMap.put("rtnMsg", "成功!");
        IData object = new DataMap();
        returnMap.put("object", object);
        object.put("respCode", "0");
        object.put("respDesc", "success");
        try {
            UcaData ucaData = UcaDataFactory.getNormalUca(userMobile);
            String custName = ucaData.getCustPerson().getCustName();
            String psptId = ucaData.getCustPerson().getPsptId();
            String psptTypeCode = ucaData.getCustPerson().getPsptTypeCode();
            String targetCode = "898"; // 默认是898：集中交付
            IDataset pubKeySet = CmOnlineUtil.queryPushKey(targetCode);
            if (IDataUtil.isEmpty(pubKeySet)) {
                returnMap.put("respCode", ComFuncUtil.CODE_ERROR);
                returnMap.put("respDesc", "找不到对应的公钥信息");
                return ComFuncUtil.transOutParam(returnMap);
            }
            String pubKey = pubKeySet.getData(0).getString("KEY");
            RealNameMsDesPlus plus = new RealNameMsDesPlus(pubKey);
            String idValueDec = plus.decrypt(idValue);
            String userNameDec = plus.decrypt(userName);

            if ("00".equals(idType) || "01".equals(idType) || "02".equals(idType) || "04".equals(idType) || "05".equals(idType)
                    || "06".equals(idType) || "07".equals(idType) || "08".equals(idType) || "99".equals(idType))//身份证件
            {
                boolean checkPass = true;
                String checkMsg = "success";
                if (!idValueDec.equals(psptId)) {
                    checkPass = false;
                    checkMsg = "身份证不正确";
                } else if (!idType.equals(HandlePsptTypeCode(psptTypeCode))) {
                    checkPass = false;
                    checkMsg = "证件类型不正确";
                } else if (!userNameDec.equals(custName)) {
                    checkPass = false;
                    checkMsg = "客户姓名不正确";
                }
                if (!checkPass) {
                    returnMap.put("rtnCode", "2999");
                    returnMap.put("rtnMsg", checkMsg);
                    object.put("respCode", "2999");
                    object.put("respDesc", checkMsg);
                    return returnMap;
                }
            } else {
                returnMap.put("rtnCode", "2999");
                returnMap.put("rtnMsg", "不支持的证件类型!");
                object.put("respCode", "2999");
                object.put("respDesc", "不支持的证件类型");
                return returnMap;
            }
        } catch (Exception e) {
            returnMap.put("rtnCode", "2999");
            returnMap.put("rtnMsg", e.getMessage());
            object.put("respCode", "2999");
            object.put("respDesc", e.getMessage());
            return returnMap;
        }
        return returnMap;
    }

    public String HandlePsptTypeCode(String psptTypeCode) {
        if ("0".equals(psptTypeCode) || "1".equals(psptTypeCode)) //身份证件
        {
            return "00";
        } else if ("01".equals(psptTypeCode)) //VIP卡
        {
            return "01";
        } else if ("A".equals(psptTypeCode)) //护照
        {
            return "02";
        } else if ("C".equals(psptTypeCode)) //军官证、军人证
        {
            return "04";
        } else if ("K".equals(psptTypeCode)) //武装警察身份证
        {
            return "05";
        } else if ("I".equals(psptTypeCode)) //台胞证
        {
            return "06";
        } else if ("2".equals(psptTypeCode)) //户口簿
        {
            return "07";
        } else if ("J".equals(psptTypeCode)) //港澳证
        {
            return "08";
        } else if ("Z".equals(psptTypeCode)) //其他证件
        {
            return "99";
        } else {
            return "";
        }
    }

    /**
     * 3.3.1.3.可选套餐/活动列表查询接口(searchProductList)
     * 实名认证系统在线上选号业务环节中，通过此接口获取用户可选择订购的套餐/活动。
     * 封装产商品接口：UPC.Out.ICMOnlineFSV.searchProductList
     */
    public IData C898HQSearchProductList(IData inParamStr) throws Exception {
        IData inParam = new DataMap(inParamStr.toString());
        IData result = UpcCall.searchProductList(inParam);

        String type = inParam.getData("params").getData("reqInfo").getString("type");
        if (!"1".equals(type)) {
            return result;
        }
        IDataset productList = result.getData("object").getDataset("result").getData(0).getDataset("resInfo");
        if (IDataUtil.isEmpty(productList)) {
            return result;
        }
        String staffId = inParam.getData("params").getData("crmpfPubInfo").getString("staffId");
        ProductPrivUtil.filterProductListByPriv(staffId, productList);
        return result;

    }

    /**
     * 补卡号码校验
     *
     * @return
     * @throws Exception
     */
    public IData C898HQChangCardCheckSvcNum(IData inParamStr) throws Exception {
        IData input = new DataMap(inParamStr.toString());
        IData params = input.getData("params");
        IData data = IntfTransUtil.transInput(params);
        IDataUtil.chkParam(data, "transactionID");
        IDataUtil.chkParam(data, "svcNum");
        String serialNumber = data.getString("svcNum");
        IData userInfo = UserInfoQry.getUserInfoBySN(serialNumber);
        String netTypeCode = userInfo.getString("NET_TYPE_CODE");
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("NET_TYPE_CODE", netTypeCode);
        data.put("TRANSACTION_ID", data.getString("transactionID"));
        IData result = CSAppCall.call("SS.ChangeCardSVC.checkSerialNumber", data).first();
        return result;
    }

    /**
     * 3.1.4.9卡数据秘钥获取 pushCardPrikey
     *
     * @return
     * @throws Exception
     */
    public IData C898HQPushCardPrikey(IData inParamStr) throws Exception {
        IData input = new DataMap(inParamStr.toString());
        IData params = input.getData("params");
        IData reqInfo = IntfTransUtil.transInput(params);
        IDataUtil.chkParam(reqInfo, "transactionID");
        IDataUtil.chkParam(reqInfo, "prikey");

        /**************************业务受理*************************/
        String prikey = reqInfo.getString("prikey");
        String transactionIdPkey = "1122334488"; // 数据库里的TRANSACTION_ID写死，保证只一条数据
        String remark = "集中交付卡数据密钥推送";
        IDataset pushCardPkey = CmOnlineUtil.queryPushCardPkey(transactionIdPkey);
        if (IDataUtil.isEmpty(pushCardPkey)) { // 没有则往数据库里插入记录
            IData outparams = new DataMap();
            outparams.put("TRANSACTION_ID", transactionIdPkey);
            outparams.put("PROV_CODE", "cprikey");
            outparams.put("KEY", prikey);
            outparams.put("CREATE_DATE", SysDateMgr.getSysTime());//创建时间
            outparams.put("UPDATE_TIME", SysDateMgr.getSysTime());//更新时间
            outparams.put("RSRV_STR1", "C898HQPushCardPrikey");// 备注
            outparams.put("RSRV_STR2", remark);// 备注
            CmOnlineUtil.insertPushKey(outparams);
        } else { // 有则更新此条记录
            CmOnlineUtil.updatePushCardPkey(prikey, transactionIdPkey);
        }

        String transactionID = reqInfo.getString("transactionID");
        IData returnMap = new DataMap();
        returnMap.put("rtnCode", "0");
        returnMap.put("rtnMsg", "成功");
        IData object = new DataMap();
        returnMap.put("object", object);
        object.put("respCode", ComFuncUtil.CODE_RIGHT);
        object.put("respDesc", "success");
        IDataset result = new DatasetList();
        object.put("result", result);
        IData resultItem = new DataMap();
        result.add(resultItem);
        resultItem.put("transactionID", transactionID);
        return returnMap;
    }

    /**
     * 3.1.4.6卡数据公钥获取(pushCardPkey)
     *
     * @return
     * @throws Exception
     */
    public IData C898HQPushCardPkey(IData inParamStr) throws Exception {
        IData input = new DataMap(inParamStr.toString());
        IData params = input.getData("params");
        IData reqInfo = IntfTransUtil.transInput(params);
        IDataUtil.chkParam(reqInfo, "transactionID");
        IDataUtil.chkParam(reqInfo, "pkey");

        /**************************业务受理*************************/
        String pkey = reqInfo.getString("pkey");
        String transactionIdPkey = "1122334477"; // 数据库里的TRANSACTION_ID写死，保证只一条数据
        String remark = "集中交付卡数据公钥推送";
        IDataset pushCardPkey = CmOnlineUtil.queryPushCardPkey(transactionIdPkey);
        if (IDataUtil.isEmpty(pushCardPkey)) { // 没有则往数据库里插入记录
            IData outparams = new DataMap();
            outparams.put("TRANSACTION_ID", transactionIdPkey);
            outparams.put("PROV_CODE", "cpubkey");
            outparams.put("KEY", pkey);
            outparams.put("CREATE_DATE", SysDateMgr.getSysTime());//创建时间
            outparams.put("UPDATE_TIME", SysDateMgr.getSysTime());//更新时间
            outparams.put("RSRV_STR1", "C898HQPushCardPkey");// 备注
            outparams.put("RSRV_STR2", remark);// 备注
            CmOnlineUtil.insertPushKey(outparams);
        } else { // 有则更新此条记录
            CmOnlineUtil.updatePushCardPkey(pkey, transactionIdPkey);
        }

        String transactionID = reqInfo.getString("transactionID");
        IData returnMap = new DataMap();
        returnMap.put("rtnCode", "0");
        returnMap.put("rtnMsg", "成功");
        IData object = new DataMap();
        returnMap.put("object", object);
        object.put("respCode", ComFuncUtil.CODE_RIGHT);
        object.put("respDesc", "success");
        IDataset result = new DatasetList();
        object.put("result", result);
        IData resultItem = new DataMap();
        result.add(resultItem);
        resultItem.put("transactionID", transactionID);
        return returnMap;
    }

    /**
     * 3.3.1.32.SIM卡校验和预占接口(checkSim)
     * SIM卡校验和预占接口
     * 实名认证系统在线上选号业务流程中省公司配送订单信息环节，新用户实名认证后，将用户激活信息传递给接入方做激活操作。
     */
    public IData C898HQCheckSim(IData inParamStr) throws Exception {
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

        IData returnMap = new DataMap();
        IDataset result = new DatasetList();
        returnMap.put("result", result);
        IData resultItem = new DataMap();
        result.add(resultItem);
        resultItem.put("transactionID", reqInfo.getString("transactionID"));
        try {
            // 选占
            ResCall.onlineCheckSim(params); // 选占
            // ResCall.checkResourceForSim(sim, serialNumber, "0", "", "", "2", "", "0", "选占"); // 选占
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
     * 3.4.4.5	补卡激活接口(replaceCardSvcNum)
     * 入参：transactionID,svcNum,imsi,iccid,KI,OPC
     */
    public IData C898HQReplaceCardSvcNum(IData inParamStr) throws Exception {
        IData input = new DataMap(inParamStr.toString());
        IData params = input.getData("params");
        IData data = IntfTransUtil.transInput(params);
        IDataUtil.chkParam(data, "transactionID");
        IDataUtil.chkParam(data, "svcNum");
        IData param = new DataMap();
        param.put("TRANSACTION_ID", data.getString("transactionID"));
        param.put("SERIAL_NUMBER", data.getString("svcNum"));
        param.put("IMSI", data.getString("imsi"));
        param.put("ICCID", data.getString("iccid"));

        IData userInfo = UcaInfoQry.qryUserInfoBySn(data.getString("svcNum"));
        String netTypeCode = "";
        if (IDataUtil.isNotEmpty(userInfo)) {
            netTypeCode = userInfo.getString("NET_TYPE_CODE");
        }
        IDataset simSet = ResCall.getSimCardInfo("0", data.getString("iccid"), "", "0", netTypeCode);
        String ki = simSet.getData(0).getString("KI");
        String opc = simSet.getData(0).getString("OPC");

        param.put("OPC", ki);
        param.put("KI", opc);
        IDataset result = new DatasetList();
        IData cardResult = CSAppCall.call("SS.ChangeCardRegSVC.replaceCardTest", param).first();
        return cardResult;
    }

    /**
     * 3.3.1.6.根据套餐和营销活动获取费用明细接口(obtainOfferFee)
     * 实名认证系统将开户号码、开户套餐、开户营销活动等信息传给省公司，省公司返回需要缴纳的费用明细信息，
     * 用于在选号页面上展示给用户，告知购买号码需要支付的金额。
     */
    public IData C898HQObtainOfferFee(IData inParamStr) throws Exception {
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
        set2Map1.put("FIELD", "productCode"); // 用户选择套餐
        set2Map1.put("TYPE", "String");
        inParamSet2.add(set2Map1);
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
        String transactionID = params.getString("transactionID");
        String productCode = reqInfo.getString("productCode");
        String offerCodes = reqInfo.getString("offerCodes", "");
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
            IDataset productFeeList = new DatasetList();
            priceInfo.addAll(productFeeList);
            IData priceMap = new DataMap();
            resInfo.put("total", "0");
            productFeeList.add(priceMap);
            priceMap.put("priceItem", "未查到费用信息"); // 收费项目
            priceMap.put("priceFee", "0"); // 收费金额 以分为单位
            priceMap.put("priceDesc", "未查到费用信息"); // 收费项目描述
            returnMap.put("respCode", ComFuncUtil.CODE_RIGHT);
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

        resInfo.put("total", String.valueOf(total));
        returnMap.put("respCode", ComFuncUtil.CODE_RIGHT);
        returnMap.put("respDesc", "success");
        return ComFuncUtil.transOutParam(returnMap);
    }

    /**
     * 3.1.4.7号码预占释放(cancelBookSvcnum)
     * 实名认证系统在线上选号业务流程中，用户发起取消订单操作，省公司需要对号码进行回收释放。
     * 参考海南的接口：SS.QueryOtherInfoSVC.cancelPreemptionResNo
     */
    public IData C898HQCancelBookSvcnum(IData inParamStr) throws Exception {
        IData inParam = new DataMap(inParamStr.toString());
        IData params = inParam.getData("params");
        IDataset inParamSet1 = new DatasetList();
        IData set1Map1 = new DataMap();
        set1Map1.put("FIELD", "busiCode"); // 业务编码	CANCEL_BOOK_SVCNUM
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
        IData set2Map3 = new DataMap();
        set2Map3.put("FIELD", "custName"); // 客户名称 加密传输，接口平台仅做透传
        set2Map3.put("TYPE", "String");
        inParamSet2.add(set2Map3);
        IData set2Map4 = new DataMap();
        set2Map4.put("FIELD", "custCertNo"); // 客户身份证号码 加密传输，接口平台仅做透传
        set2Map4.put("TYPE", "String");
        inParamSet2.add(set2Map4);
        inParamResult = ComFuncUtil.checkInParam(reqInfo, inParamSet2);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        /********************************业务受理********************************/
        String transactionID = params.getString("transactionID");
        String svcNum = reqInfo.getString("svcNum");
        String custName = reqInfo.getString("custName");
        String custCertNo = reqInfo.getString("custCertNo");

        // des字段解密
        String targetCode = params.getString("targetCode");
        IDataset pubKeySet = CmOnlineUtil.queryPushKey(targetCode);
        if (IDataUtil.isEmpty(pubKeySet)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "找不到对应的公钥信息");
        }
        String pubKey = pubKeySet.getData(0).getString("KEY");
        RealNameMsDesPlus plus = new RealNameMsDesPlus(pubKey);
        custName = plus.decrypt(custName);
        custCertNo = plus.decrypt(custCertNo);

        IData returnMap = new DataMap();
        IDataset result = new DatasetList();
        returnMap.put("result", result);
        IData resultItem = new DataMap();
        result.add(resultItem);
        resultItem.put("transactionID", transactionID);
        resultItem.put("isSuc", "0"); // 是否成功处理	1成功，0不成功

        IData param = new DataMap();
        param.put("X_GET_MODE", "0");
        param.put("RES_TYPE_CODE", "0");
        param.put("RES_NO", svcNum);
        IDataset resResult = ResCall.callRes("RCF.resource.IResPublicIntfOperateSV.releaseRes", param);
        if (IDataUtil.isEmpty(resResult)) {
            returnMap.put("respCode", ComFuncUtil.CODE_ERROR);
            returnMap.put("respDesc", "取消号码选占异常");
            return ComFuncUtil.transOutParam(returnMap);
        } else if (!"0".equals(resResult.getData(0).getString("X_RESULTCODE"))) {
            returnMap.put("respCode", ComFuncUtil.CODE_ERROR);
            returnMap.put("respDesc", "取消号码选占异常" + resResult.getData(0).getString("X_RESULTCODE"));
            return ComFuncUtil.transOutParam(returnMap);
        }

        resultItem.put("isSuc", "1"); // 是否成功处理	1成功，0不成功
        returnMap.put("respCode", ComFuncUtil.CODE_RIGHT);
        returnMap.put("respDesc", "success");
        return ComFuncUtil.transOutParam(returnMap);
    }

    /**
     * 3.3.1.28.号码选择校验接口(chooseSvcnumCheck)
     * 实名认证平台在APP+NFC模式做开户全流程中需要对代销商输入的号码进行校验，判断代销商是否可以办理该号码。
     */
    public IData C898HQChooseSvcnumCheck(IData inParamStr) throws Exception {
        IData inParam = new DataMap(inParamStr.toString());
        IData params = inParam.getData("params");
        IDataset inParamSet1 = new DatasetList();
        IData set1Map1 = new DataMap();
        set1Map1.put("FIELD", "busiCode"); // 业务编码	choose_SVCNUM_check
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
        inParamResult = ComFuncUtil.checkInParam(reqInfo, inParamSet2);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        /********************************业务受理********************************/
        String transactionID = reqInfo.getString("transactionID");
        String svcNum = reqInfo.getString("svcNum");
        String operCode = reqInfo.getString("operCode");
        String channelId = reqInfo.getString("channelId");

        IData returnMap = new DataMap();
        IDataset result = new DatasetList();
        returnMap.put("result", result);
        IData resultItem = new DataMap();
        result.add(resultItem);
        resultItem.put("transactionID", transactionID);
        IData checkParam = new DataMap();
        checkParam.put("RES_CHECK_BY_DEPART", "0");
        checkParam.put("OPEN_TYPE", "AGENT_OPEN");
        checkParam.put("SERIAL_NUMBER", svcNum);
        checkParam.put("AGENT_DEPART_ID", channelId);
        checkParam.put("INFO_TAG", "0");
        try {
            CSAppCall.call("SS.CreatePersonUserSVC.checkSerialNumber", checkParam);
        } catch (Exception e) {
            returnMap.put("respCode", ComFuncUtil.CODE_ERROR);
            returnMap.put("respDesc", "号码选择校验接口：" + e.getMessage());
            return ComFuncUtil.transOutParam(returnMap);
        }
        returnMap.put("respCode", ComFuncUtil.CODE_RIGHT);
        returnMap.put("respDesc", "success");
        return ComFuncUtil.transOutParam(returnMap);
    }

    /**
     * 3.3.1.26.预占号码列表查询 (queryPhoneList)
     * 实名认证平台在APP+NFC模式做开户全流程中，根据客户身份证信息查询预占号码列表。
     */
    public IData C898HQQueryPhoneList(IData inParamStr) throws Exception {
        IData inParam = new DataMap(inParamStr.toString());
        IData params = inParam.getData("params");
        IDataset inParamSet1 = new DatasetList();
        IData set1Map1 = new DataMap();
        set1Map1.put("FIELD", "busiCode"); // 业务编码	QUERY_PHONELIST
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
        set2Map4.put("FIELD", "custCertNo"); // 客户身份证号码	加密传输，加密规则参见DES加密算法
        set2Map4.put("TYPE", "String");
        inParamSet2.add(set2Map4);
        inParamResult = ComFuncUtil.checkInParam(reqInfo, inParamSet2);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        /********************************业务受理********************************/
        IData returnMap = new DataMap();
        IDataset result = new DatasetList();
        returnMap.put("result", result);
        IData resultItem = new DataMap();
        result.add(resultItem);
        resultItem.put("transactionID", reqInfo.getString("transactionID"));
        String custCertNo = reqInfo.getString("custCertNo");
        String targetCode = params.getString("targetCode");
        IDataset pubKeySet = CmOnlineUtil.queryPushKey(targetCode);
        if (IDataUtil.isEmpty(pubKeySet)) {
            returnMap.put("respCode", ComFuncUtil.CODE_ERROR);
            returnMap.put("respDesc", "找不到对应的公钥信息");
            return ComFuncUtil.transOutParam(returnMap);
        }
        String pubKey = pubKeySet.getData(0).getString("KEY");
        RealNameMsDesPlus plus = new RealNameMsDesPlus(pubKey);
        String psptId = plus.decrypt(custCertNo);
        reqInfo.put("custCertNo", psptId);

        IDataset resSet = new DatasetList();
        try {
            // 选占列表查询
            resSet = ResCall.onlineQueryPhoneList(params); // 选占
        } catch (Exception e) {
            returnMap.put("respCode", ComFuncUtil.CODE_ERROR);
            returnMap.put("respDesc", "查询失败" + e.getMessage());
            return ComFuncUtil.transOutParam(returnMap);
        }
        IDataset resInfo = resSet.getData(0).getDataset("resInfo");
        for (int i = 0; i < resInfo.size(); i++) {
            IData resInfoItem = resInfo.getData(i);
            String cardType = resInfoItem.getString("cardType");
            String cardNo = resInfoItem.getString("cardNo");
            String cardTypeEnc = plus.encrypt(cardType);
            String cardNoEnc = plus.encrypt(cardNo);
            resInfoItem.put("cardType", cardTypeEnc);
            resInfoItem.put("cardNo", cardNoEnc);
        }

        resultItem.put("resInfo", resInfo);
        returnMap.put("respCode", ComFuncUtil.CODE_RIGHT);
        returnMap.put("respDesc", "success");
        return ComFuncUtil.transOutParam(returnMap);
    }

    /**
     * 3.1.2.6.呼转号码设置(submitCallFowardingNumber)
     * 为指定号码的用户设置呼转号码。
     * 参考接口：SS.PlatOrderIntfSVC.setVEMLAttr
     */
    public IData C898HQSubmitCallFowardingNumber(IData inParamStr) throws Exception {
        IData returnMap = new DataMap(); // 返回结果
        IDataset result = new DatasetList();
        returnMap.put("result", result);
        IData inParam = new DataMap(inParamStr.toString());
        IData params = inParam.getData("params");
        IDataset inParamSet1 = new DatasetList();
        IData set1Map1 = new DataMap();
        set1Map1.put("FIELD", "userMobile"); // 服务号码 客户手机号码
        set1Map1.put("TYPE", "String");
        inParamSet1.add(set1Map1);
        IData set1Map2 = new DataMap();
        set1Map2.put("FIELD", "callFowardingList"); // 呼转类型及号码列表
        set1Map2.put("TYPE", "List");
        inParamSet1.add(set1Map2);
        try{
        IData inParamResult = ComFuncUtil.checkInParam(params, inParamSet1);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        IDataset callFowardingList = params.getDataset("callFowardingList");
        for (int i = 0; i < callFowardingList.size(); i++) {
            IData callFowardingListItem = callFowardingList.getData(i);
            IDataset inParamSet2 = new DatasetList();
            IData set2Map1 = new DataMap();
            set2Map1.put("FIELD", "callFowardingType"); // 呼转类型编码
            set2Map1.put("TYPE", "String");
            inParamSet2.add(set2Map1);
            IData set2Map2 = new DataMap();
            set2Map2.put("FIELD", "effType"); // 生效方式：0-立即，1-次月。默认取值为立即
            set2Map2.put("TYPE", "String");
            inParamSet2.add(set2Map2);
            inParamResult = ComFuncUtil.checkInParam(callFowardingListItem, inParamSet2);
            if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
                return ComFuncUtil.transOutParam(inParamResult);
            }
        }

        // 根据手机号码查询用户信息
        String serialNumber = params.getString("userMobile"); // 服务号码 客户手机号码
        IDataset UserInfoSet = UserInfoQry.getUserInfoBySn(serialNumber, "0");
        if (IDataUtil.isEmpty(UserInfoSet)) {
            //CSAppException.apperr(CrmCommException.CRM_COMM_103, serialNumber + "已经失效或者不存在");
            returnMap.put("respCode", "-1");
            returnMap.put("respDesc", serialNumber + "已经失效或者不存在");
            return ComFuncUtil.transOutParamNew(returnMap);
        }
        String userId = UserInfoSet.getData(0).getString("USER_ID");

        /*****************************业务受理*****************************/


        for (int i = 0; i < callFowardingList.size(); i++) {
            IData callFowardingListItem = callFowardingList.getData(i);
            // 呼转类型编码  1：无条件呼转 2：遇忙呼转 3：无应答转 4：不可及转 , 新增类型：HJQZ00001111 无条件呼转
            String callFowardingType = callFowardingListItem.getString("callFowardingType");
            if("HJQZ00001111".equals(callFowardingType))
            {
            	callFowardingType = "1";
            }
            // 呼转号码,为空则表示取消呼转号码，有值表示设置或更改呼转号码
            String callFowardingNumber = callFowardingListItem.getString("callFowardingNumber", "");
            String effType = callFowardingListItem.getString("effType"); // 生效方式,0-立即,1-次月,默认取值为立即。
            String remark = callFowardingListItem.getString("remark"); // 备注

            IData tradeParamListItem = new DataMap();
            String operCode = "01";
            if (StringUtils.isBlank(callFowardingNumber)) {
                operCode = "02";
            }
            tradeParamListItem.put("SERIAL_NUMBER", serialNumber);
            tradeParamListItem.put("OPER_CODE", operCode);
            tradeParamListItem.put("OPER_TYPE", callFowardingType);
            tradeParamListItem.put("OPR_NUMB", callFowardingNumber);
            String serviceId = "";
            String attrCode = "";
            String modifyTag = "";
            if (StringUtils.equals("01", operCode)) {
                modifyTag = "0";
                tradeParamListItem.put("IS_PLAT_ORDER", "true");
            }
            if (StringUtils.equals("02", operCode)) {
                modifyTag = "1";
            }
            // 根据呼转类型搜索对应的呼转service_id
            IData set = StaticInfoQry.getStaticInfoByTypeIdDataId("VEML_SVC_CODE", callFowardingType);
            if (IDataUtil.isEmpty(set)) {
                //CSAppException.apperr(CrmCommException.CRM_COMM_103, "TD_S_STATIC表VEML_SVC_CODE参数配置错误");
                returnMap.put("respCode", "-1");
                returnMap.put("respDesc", "TD_S_STATIC表VEML_SVC_CODE参数配置错误");
                return ComFuncUtil.transOutParamNew(returnMap);
            }
            serviceId = set.getString("DATA_NAME"); // 呼转service_id
            attrCode = set.getString("PDATA_ID");

            tradeParamListItem.put("IN_MODE_CODE", "6");
            tradeParamListItem.put("ELEMENT_TYPE_CODE", "S");
            tradeParamListItem.put("ELEMENT_ID", serviceId);
            tradeParamListItem.put("MODIFY_TAG", modifyTag);
            tradeParamListItem.put("BOOKING_TAG", "0");
            tradeParamListItem.put("ATTR_STR1", attrCode);
            tradeParamListItem.put("ATTR_STR2", callFowardingNumber);

            IDataset sevSet = BofQuery.getUserSvc(userId, serviceId, Route.CONN_CRM_CG); //查询用户服务

            if (modifyTag.equals("0")) {    //新增，会验证
                if (IDataUtil.isNotEmpty(sevSet)) {
                    //CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户已经存在此服务，无法重复办理！");
                    returnMap.put("respCode", "-1");
                    returnMap.put("respDesc", "用户已经存在此服务，无法重复办理！");
                    return ComFuncUtil.transOutParamNew(returnMap);
                }
            } else if (modifyTag.equals("1")) {        //取消
                if (IDataUtil.isEmpty(sevSet)) {
                    //CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户不存在此服务，无需取消！");
                    returnMap.put("respCode", "-1");
                    returnMap.put("respDesc", "用户不存在此服务，无需取消！");
                    return ComFuncUtil.transOutParamNew(returnMap);
                }
            }
            IData resultItem = new DataMap();
            result.add(resultItem);
            IDataset tradeResult = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", tradeParamListItem);
            String order_id = tradeResult.getData(0).getString("ORDER_ID", "");
            if (StringUtils.isBlank(order_id)) {
                //CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用呼叫转移受理接口出错");
                returnMap.put("respCode", "-1");
                returnMap.put("respDesc", "调用呼叫转移受理接口出错");
                return ComFuncUtil.transOutParamNew(returnMap);
            }
            resultItem.put("orderId", order_id);
        }
	        returnMap.put("respCode", ComFuncUtil.CODE_RIGHT);
	        returnMap.put("respDesc", "success");
	        return ComFuncUtil.transOutParamNew(returnMap);
        }
        catch (Exception e)
        {	
        	returnMap.put("respCode", "-1");
	        returnMap.put("respDesc", "调用呼叫转移受理接口异常");
	        return ComFuncUtil.transOutParamNew(returnMap);
        }
    }

    /**
     * 3.3.1.10.订单状态同步（实名认证系统写卡）(returnObtainOrderStatus)
     * 省公司同步到实名认证系统的订单，当在实名认证系统订单状态变化时，实名认证系统通知省公司。
     */
    public IData C898HQReturnObtainOrderStatus(IData inParamStr) throws Exception {
        IData inParam = new DataMap(inParamStr.toString());
        IData params = inParam.getData("params");
        IDataset inParamSet1 = new DatasetList();
        IData set1Map1 = new DataMap();
        set1Map1.put("FIELD", "busiCode"); // 业务编码	RETURN_OBTAIN_ORDER_STATUS
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
        set2Map1.put("FIELD", "busiSeq"); // 订单流水
        set2Map1.put("TYPE", "String");
        inParamSet2.add(set2Map1);
        IData set2Map2 = new DataMap();
        set2Map2.put("FIELD", "svcNum"); // 开户号码
        set2Map2.put("TYPE", "String");
        inParamSet2.add(set2Map2);
        IData set2Map3 = new DataMap();
        set2Map3.put("FIELD", "status"); // 1：待发货 2：待签收（此状态返回物流信息） 3：已完成 4：已写卡 5：已签收
        set2Map3.put("TYPE", "String");
        inParamSet2.add(set2Map3);
        inParamResult = ComFuncUtil.checkInParam(reqInfo, inParamSet2);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        /***********************************业务受理***********************************/
        IData returnMap = new DataMap(); // 返回结果
        IDataset result = new DatasetList();
        returnMap.put("result", result);
        IData resultItem = new DataMap();
        result.add(resultItem);

        String busiSeq = reqInfo.getString("busiSeq");
        String serialNumber = reqInfo.getString("svcNum");
        String status = reqInfo.getString("status");
        String trackingNO = reqInfo.getString("trackingNO", "");
        String expressCompany = reqInfo.getString("expressCompany", "");
        String sn = reqInfo.getString("sn", "");
        IDataset orderBusiSet = queryOrderByBusiSeq(busiSeq);
        if (IDataUtil.isEmpty(orderBusiSet)) {
            IData insertParam = new DataMap();
            insertParam.put("BUSI_SEQ", busiSeq);
            insertParam.put("SERIAL_NUMBER", serialNumber);
            insertParam.put("MONTH", busiSeq.substring(8, 10));
            insertParam.put("STATUS", status);
            insertParam.put("TRACKING_NO", trackingNO);
            insertParam.put("EXPRESS_COMPANY", expressCompany);
            insertParam.put("SN", sn);
            insertParam.put("UPDATE_TIME", SysDateMgr.getSysTime());
            insertParam.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            insertParam.put("REMARK", "实名认证系统同步开户工单状态");
            Dao.insert("TF_B_OPEN_SYNC_ORDER", insertParam, Route.CONN_CRM_CEN);
        } else {
            IData updateParam = new DataMap();
            updateParam.put("BUSI_SEQ", busiSeq);
            updateParam.put("SERIAL_NUMBER", serialNumber);
            updateParam.put("MONTH", busiSeq.substring(8, 10));
            updateParam.put("STATUS", status);
            updateParam.put("TRACKING_NO", trackingNO);
            updateParam.put("EXPRESS_COMPANY", expressCompany);
            updateParam.put("SN", sn);
            updateParam.put("UPDATE_TIME", SysDateMgr.getSysTime());
            updateParam.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            Dao.save("TF_B_OPEN_SYNC_ORDER", updateParam, Route.CONN_CRM_CEN);
        }
        resultItem.put("transactionID", params.getString("transactionID"));
        returnMap.put("respCode", ComFuncUtil.CODE_RIGHT);
        returnMap.put("respDesc", "success");
        return ComFuncUtil.transOutParam(returnMap);
    }

    public static IDataset queryOrderByBusiSeq(String busiSeq) throws Exception {
        IData param = new DataMap();
        param.put("BUSI_SEQ", busiSeq);
        param.put("MONTH", busiSeq.substring(8, 10));
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT BUSI_SEQ, SERIAL_NUMBER, STATUS, TRACKING_NO, EXPRESS_COMPANY, ");
        parser.addSQL(" SN, MONTH,TO_CHAR(UPDATE_TIME,'yyyy-MM-dd hh24:mi:ss') UPDATE_TIME , ");
        parser.addSQL(" UPDATE_STAFF_ID, RSRV_TAG1, RSRV_TAG2, RSRV_STR1, RSRV_STR2, RSRV_STR3, ");
        parser.addSQL(" RSRV_STR4, TO_CHAR(RSRV_DATE1,'yyyy-MM-dd hh24:mi:ss') RSRV_DATE1 , ");
        parser.addSQL(" TO_CHAR(RSRV_DATE2,'yyyy-MM-dd hh24:mi:ss') RSRV_DATE2 , REMARK  ");
        parser.addSQL(" FROM TF_B_OPEN_SYNC_ORDER ");
        parser.addSQL(" WHERE BUSI_SEQ=:BUSI_SEQ ");
        parser.addSQL(" AND MONTH=:MONTH  ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    /**
     * 3.1.4.3获取写卡基础数据(queryWriteCardBasicData)
     * 实名认证系统在线上选号流程中，根据手机号获取可用的写卡基础数据信息。写卡数据预占时间为12小时。
     */
    public IData C898HQQueryWriteCardBasicData(IData inParamStr) throws Exception {
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
        String sn = spesiminfo.getString("SN", "");
        resInfo.put("imsi", imsi);
        resInfo.put("iccid", iccid);
        resInfo.put("puk1", puk1);
        resInfo.put("puk2", puk2);
        resInfo.put("pin1", pin1);
        resInfo.put("pin2", pin2);
        resInfo.put("CenterPhone", smsp);

//        String tradeId = SeqMgr.getTradeId().substring(6);
//        AssemDynData ass = new AssemDynData();
//        EncAssemDynData enAss = new EncAssemDynData();
//        List<EncAssemDynData> enAsses = new ArrayList<EncAssemDynData>();
//        IssueData issue = new IssueData();
//        issue.setIccId(iccid);
//        issue.setImsi(imsi);
//        issue.setPin1(pin1);
//        issue.setPin2(pin2);
//        issue.setPuk1(puk1);
//        issue.setPuk2(puk2);
//        issue.setSmsp(smsp);
//        enAss.setMsisdn(svcNum);
//        enAss.setIssueData(issue);
//        enAsses.add(enAss);
//        ass.setChanelflg("1");
//        String cardInfo = "080A" + iccid + "0E0A" + sn;
//        ass.setCardInfo(cardInfo);
//        ass.setEnc(enAsses);
//        ass.setSeqNo(tradeId);
//        WebServiceClient client = new WebServiceClient();
//        EncAssemDynDataRsp resAssemData = client.encAssemClient(ass);
//        String encodeStr = resAssemData.getIssueData();
//        String result_code = resAssemData.getResultCode();
//        if (!"0".equals(result_code)) {
//            CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用现场实时写卡系统失败");
//        }

        String encodeStr = "72070003A30101700000681106000505B000F2A5C830519147408DDF252274C123F42E8EB15B57DF966136AA8190D39B0ECB695B1167EB9F22B0D9E5566ABC162A0CD47E666FD4AF76C2DB65754A23ED30814B59073995D0C22B0C8F4C9AAA06BF78E44CD436EE9A3617ABBEEABAB91EDD1592";
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
     * 3.3.4.7开户接口(openSvcNum)：参考：SS.CreatePersonIntfSVC.openPersonUser
     * 实名认证平台将开户号码、SIM卡号、身份证信息等信息传给省公司，省公司进行开户。
     */
    public IData C898HQOpenSvcNum(IData inParamStr) throws Exception {
        IData inParam = new DataMap(inParamStr.toString());
        IData params = inParam.getData("params");
        IDataset inParamSet1 = new DatasetList();
        IData set1Map1 = new DataMap();
        set1Map1.put("FIELD", "busiCode"); // 业务编码	OPEN_SVCNUM
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
        set2Map3.put("FIELD", "custName"); // 客户名称	加密传输
        set2Map3.put("TYPE", "String");
        inParamSet2.add(set2Map3);
        IData set2Map4 = new DataMap();
        set2Map4.put("FIELD", "custCertNo"); // 客户身份证号码	加密传输
        set2Map4.put("TYPE", "String");
        inParamSet2.add(set2Map4);
        IData set2Map5 = new DataMap();
        set2Map5.put("FIELD", "operCode"); // 登录员工工号
        set2Map5.put("TYPE", "String");
        inParamSet2.add(set2Map5);
        IData set2Map6 = new DataMap();
        set2Map6.put("FIELD", "channelId"); // 员工所属渠道
        set2Map6.put("TYPE", "String");
        inParamSet2.add(set2Map6);
        IData set2Map7 = new DataMap();
        set2Map7.put("FIELD", "sim"); // Sim或空白卡序列号
        set2Map7.put("TYPE", "String");
        inParamSet2.add(set2Map7);
        IData set2Map8 = new DataMap();
        set2Map8.put("FIELD", "simType"); // Sim卡类型	空白卡:1 实体卡 2空白卡
        set2Map8.put("TYPE", "String");
        inParamSet2.add(set2Map8);
        IData set2Map9 = new DataMap();
        set2Map9.put("FIELD", "iccid"); // 写卡信息获取接口中有，实体卡不传 空白卡传
        set2Map9.put("TYPE", "String");
        inParamSet2.add(set2Map9);
        IData set2Map10 = new DataMap();
        set2Map10.put("FIELD", "custCertAddr"); // 证件地址	加密传输，接口平台仅做透传
        set2Map10.put("TYPE", "String");
        inParamSet2.add(set2Map10);
        IData set2Map11 = new DataMap();
        set2Map11.put("FIELD", "gender"); // 性别 	传1:代表男，传0:代表女
        set2Map11.put("TYPE", "String");
        inParamSet2.add(set2Map11);
        IData set2Map12 = new DataMap();
        set2Map12.put("FIELD", "nation"); // 民族
        set2Map12.put("TYPE", "String");
        inParamSet2.add(set2Map12);
        IData set2Map13 = new DataMap();
        set2Map13.put("FIELD", "birthday"); // 生日	YYYY-MM-DD
        set2Map13.put("TYPE", "String");
        inParamSet2.add(set2Map13);
        IData set2Map14 = new DataMap();
        set2Map14.put("FIELD", "issuingAuthority"); // 签证机关	加密传输
        set2Map14.put("TYPE", "String");
        inParamSet2.add(set2Map14);
        IData set2Map15 = new DataMap();
        set2Map15.put("FIELD", "certValiddate"); // 生效日期	YYYY-MM-DD
        set2Map15.put("TYPE", "String");
        inParamSet2.add(set2Map15);
        IData set2Map16 = new DataMap();
        set2Map16.put("FIELD", "certExpdate"); // 失效日期	YYYY-MM-DD
        set2Map16.put("TYPE", "String");
        inParamSet2.add(set2Map16);
        IData set2Map17 = new DataMap();
        set2Map17.put("FIELD", "productCode"); // 选择套餐的编号
        set2Map17.put("TYPE", "String");
        inParamSet2.add(set2Map17);
        inParamResult = ComFuncUtil.checkInParam(reqInfo, inParamSet2);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        String operCode = reqInfo.getString("operCode");
        String transactionId = reqInfo.getString("transactionID");
        /**********************业务受理*********************/
        IData returnMap = new DataMap();
        IDataset result = new DatasetList();
        returnMap.put("result", result);
        IData resultItem = new DataMap();
        result.add(resultItem);
        resultItem.put("transactionID", transactionId);

        String SERIAL_NUMBER = reqInfo.getString("svcNum", "");
        String CUST_NAME = reqInfo.getString("custName", "");
        String PSPT_ID = reqInfo.getString("custCertNo", "");
        String PSPT_ADDR = reqInfo.getString("custCertAddr", "");
        String SEX = reqInfo.getString("gender", "");
        String sim_type = reqInfo.getString("simType");
        String SIM_CARD_NO = reqInfo.getString("iccid", "");
        String EMPTY_CARD_ID = reqInfo.getString("sim", "");
        String NATIONALITY_CODE = reqInfo.getString("nation", "");
        String BIRTHDAY = reqInfo.getString("birthday", "");
        String PSPT_END_DATE = reqInfo.getString("certExpdate", "");
        String PRODUCT_ID = reqInfo.getString("productCode", "");
        String SALE_PACKAGE_ID = reqInfo.getString("offCode", "");
        String srvpkgId = reqInfo.getString("srvpkgId", "");
        String issuingAuthority = reqInfo.getString("issuingAuthority", "");
        String targetCode = params.getString("targetCode");
        IDataset pubKeySet = CmOnlineUtil.queryPushKey(targetCode);
        if (IDataUtil.isEmpty(pubKeySet)) {
            returnMap.put("respCode", ComFuncUtil.CODE_ERROR);
            returnMap.put("respDesc", "找不到对应的公钥信息");
            return ComFuncUtil.transOutParam(returnMap);
        }
        String pubKey = pubKeySet.getData(0).getString("KEY");
        RealNameMsDesPlus plus = new RealNameMsDesPlus(pubKey);
        SERIAL_NUMBER = plus.decrypt(SERIAL_NUMBER);
        CUST_NAME = plus.decrypt(CUST_NAME);
        PSPT_ID = plus.decrypt(PSPT_ID);
        PSPT_ADDR = plus.decrypt(PSPT_ADDR);
        issuingAuthority = plus.decrypt(issuingAuthority);

        // 先进行sim卡选占
        IData checkSimData = new DataMap();
        checkSimData.put("SIM_CARD_NO", SIM_CARD_NO);
        checkSimData.put("SERIAL_NUMBER", SERIAL_NUMBER);
        IDataset checkSimResult = CSAppCall.call("SS.CreatePersonUserSVC.checkSimCardNo", checkSimData);
        String SIM_CARD_SALE_MONEY = checkSimResult.getData(0).getString("SIM_CARD_SALE_MONEY", "");
        String SIM_FEE_TAG = checkSimResult.getData(0).getString("SIM_FEE_TAG", "");

        IData openParam = new DataMap();
        openParam.put("SIM_CARD_SALE_MONEY", SIM_CARD_SALE_MONEY);
        openParam.put("SIM_FEE_TAG", SIM_FEE_TAG);
        openParam.put("SERIAL_NUMBER", SERIAL_NUMBER);
        openParam.put("SIM_CARD_NO", SIM_CARD_NO);
        openParam.put("PSPT_TYPE_CODE", "0");
        openParam.put("PSPT_ID", PSPT_ID);
        openParam.put("CUST_NAME", CUST_NAME);
        openParam.put("PSPT_ADDR", PSPT_ADDR);
        openParam.put("USER_TYPE_CODE", "0");
        openParam.put("PHONE", SERIAL_NUMBER);
        openParam.put("PAY_NAME", CUST_NAME);
        openParam.put("PAY_MODE_CODE", "0");
        openParam.put("ACCT_DAY", "1");
        openParam.put("USER_PASSWD", PSPT_ID.substring(PSPT_ID.length() - 7, PSPT_ID.length() - 1));
        openParam.put("PRODUCT_ID", PRODUCT_ID);
        String brandCode = UProductInfoQry.getBrandCodeByProductId(PRODUCT_ID);
        openParam.put("BRAND_CODE", brandCode);
        openParam.put("TRADE_TYPE_CODE", "10");
        openParam.put("ORDER_TYPE_CODE", "10");
        openParam.put("TRADE_DEPART_PASSWD", "0");//孙鑫说没用到，但被校验不能空，所以随便赋值了
        openParam.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        openParam.put("ADVANCE_PAY", "-1");//预存款 2 0  为了避免出现多一条记录的情况，所以赋值为-1
        openParam.put("OPER_FEE", "0");//卡费  0 10
        openParam.put("FOREGIFT", "0");//押金费 1 0

        String custName = CUST_NAME.replaceAll(" +", "");
        openParam.put("CUST_NAME", custName);
        openParam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());

        IDataset X_TRADE_FEESUB = new DatasetList();
        openParam.put("X_TRADE_FEESUB", X_TRADE_FEESUB);
        IDataset product_fee = UpcCall.qryDynamicPrice(PRODUCT_ID, BofConst.ELEMENT_TYPE_CODE_PRODUCT, "-1", null, "10", null, null, null);
        int totalFee = 0;
        if (IDataUtil.isNotEmpty(product_fee)) {
            for (int i = 0; i < product_fee.size(); i++) {
                IData X_TRADE_FEESUB_ITEM = new DataMap();
                X_TRADE_FEESUB.add(X_TRADE_FEESUB_ITEM);
                int fee = Integer.parseInt(product_fee.getData(i).getString("FEE"));
                totalFee = totalFee + fee;
                X_TRADE_FEESUB_ITEM.put("TRADE_TYPE_CODE", "10");
                X_TRADE_FEESUB_ITEM.put("FEE_TYPE_CODE", product_fee.getData(i).getString("FEE_TYPE_CODE"));
                X_TRADE_FEESUB_ITEM.put("FEE", "" + 0);
                X_TRADE_FEESUB_ITEM.put("OLDFEE", "" + 0);
                X_TRADE_FEESUB_ITEM.put("FEE_MODE", "2");
            }
        }

        IDataset X_TRADE_PAYMONEY = new DatasetList();
        openParam.put("X_TRADE_PAYMONEY", X_TRADE_PAYMONEY);
        IData X_TRADE_PAYMONEY_ITEM = new DataMap();
        X_TRADE_PAYMONEY.add(X_TRADE_PAYMONEY_ITEM);
        X_TRADE_PAYMONEY_ITEM.put("PAY_MONEY_CODE", "0");
        X_TRADE_PAYMONEY_ITEM.put("MONEY", totalFee);
        openParam.put("PAY_MONEY_CODE", "0");
        // REQ202004240028_关于国漫作为基础服务的开发需求 add by wuhao5 200514
        // 一体机开户添加以下标识,通过AddGMToOAOAction默认绑定国际漫游
        openParam.put("ROAM_TAG", "1");

        IDataset outer = CSAppCall.call("SS.CreatePersonUserIntfSVC.tradeReg", openParam);
        if (IDataUtil.isNotEmpty(outer)) {
            IData outData = outer.getData(0);
            String orderId = outData.getString("ORDER_ID", "");
            String tradeId = outData.getString("TRADE_ID", "");
            if (StringUtils.isNotEmpty(orderId) && StringUtils.isNotEmpty(tradeId)) {
                String busiContent = "";
                busiContent += "证件类型：身份证";
                busiContent += "，证件号码：" + PSPT_ID;
                busiContent += "，联系电话：" + SERIAL_NUMBER;
                busiContent += "，身份证地址：" + PSPT_ADDR;
                busiContent += "，SIM卡号：" + SIM_CARD_NO;
                String productName = UProductInfoQry.getProductNameByProductId(PRODUCT_ID);
                busiContent += "，资费产品：" + productName;

                logElecWorkOrder(busiContent, transactionId, SERIAL_NUMBER, outData, PRODUCT_ID, SALE_PACKAGE_ID, srvpkgId); //在线公司发起的开户接口，需要记录电子工单
                //业务流水号、身份证正面、身份证反面、拍摄照片等信息入表td_b_picture_info add by liwei29
                String userid = outData.getString("USER_ID", "");
                insertTdPictureInfo(tradeId, transactionId, SERIAL_NUMBER, userid, PSPT_ID);
                // 入表 end
                returnMap.put("respCode", ComFuncUtil.CODE_RIGHT);
                returnMap.put("respDesc", "success");
                resultItem.put("isSuc", "1");
                resultItem.put("order_id", orderId);
                resultItem.put("trade_id", tradeId);
            } else {
                returnMap.put("respCode", ComFuncUtil.CODE_ERROR);
                returnMap.put("respDesc", "开户失败,没有产生订单");
                resultItem.put("isSuc", "0");
            }
        } else {
            returnMap.put("respCode", ComFuncUtil.CODE_ERROR);
            returnMap.put("respDesc", "开户失败,返回列表为空");
            resultItem.put("isSuc", "0");
        }
        return ComFuncUtil.transOutParam(returnMap);
    }

    private void logElecWorkOrder(String busiContent, String transactionID, String sn, IData OrderInfo, String PRODUCT_ID, String SALE_PACKAGE_ID, String srvpkgId) throws Exception {
        IData data = new DataMap();
        data.put("TRANSACTION_ID", transactionID);//保存全网流水号。
        data.put("ORDER_ID", OrderInfo.getString("ORDER_ID"));
        data.put("ELECTRADE_TAG", "1");
        data.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("UPDATE_TIME", SysDateMgr.getSysTime());
        data.put("CHANNEL_ID", CSBizBean.getVisit().getDepartId());
        data.put("SERIAL_NUMBER", sn);
        data.put("REGION_ID", CSBizBean.getVisit().getStaffEparchyCode());
        data.put("BUSI_CONTENT", busiContent);
        data.put("REMARK", "用户开户");
        data.put("VERIFY_MODE", "一体机实名认证");
        data.put("RSRV_STR1", PRODUCT_ID);
        data.put("RSRV_STR2", SALE_PACKAGE_ID);
        data.put("RSRV_STR3", srvpkgId);
        Dao.insert("TF_B_ELECTRADE_TRANS", data, Route.CONN_CRM_CEN);
    }
    
    /**
     * @REQ202004300045_关于优化在线公司自助一体机设备实名制入网数据留存功能的需求
     * @author liwei29
     * @data 2019-05-13
     * @return td_b_picture_info
     */
    private void insertTdPictureInfo(String tradeId, String transactionID, String sn,String userId, String pastId) throws Exception {
    	IData reqInfo = new DataMap();
    	String picNameR = "BOSS898_" + transactionID + "_BACK_R.jpg";
    	String picNameT = "BOSS898_" + transactionID + "_BACK_T.jpg";
    	reqInfo.put("TRADE_ID", tradeId); 
    	reqInfo.put("USER_ID", userId); 
    	reqInfo.put("SERIAL_NUMBER", sn); 
    	reqInfo.put("CARD_ID", pastId); 
    	reqInfo.put("BUSINESS_TYPE", "10"); 
    	reqInfo.put("TRANSACTION_ID", transactionID); 
    	reqInfo.put("PIC_NNAME_R", picNameR); 
    	reqInfo.put("PIC_NNAME_V1", picNameT); 
    	reqInfo.put("DEAL_TAG", "0"); 
    	reqInfo.put("CHANNEL_TYPE", "7");  
    	reqInfo.put("ACCEPT_MONTH", SysDateMgr.getCurMonth()); 
    	reqInfo.put("UPDATE_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss")); 
    	Dao.insert("TD_B_PICTURE_INFO", reqInfo, Route.CONN_CRM_CEN);
    }

    /**
     * 3.3.1.23.写卡结果反馈接口(returnWriteCardResult)
     * 实名认证平台在APP+NFC模式做开户全流程中空白卡写卡结束后需要把写卡结果反馈给省公司。
     * RCF.resource.ISimcardIntfOperateSV.remoteWriteCardRet
     */
    public IData C898HQReturnWriteCardResult(IData inParamStr) throws Exception {
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

        try {
            IDataset resInfos = ResCall.backWriteSimCard(imsi, sn, "", CSBizBean.getTradeEparchyCode(), "", iccid, "", "", "2", "");
            if (IDataUtil.isEmpty(resInfos) || !"0".equals(resInfos.getData(0).getString("X_RESULTCODE"))) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "写卡信息回写错误！");
            }
        } catch (Exception e) {
            returnMap.put("respCode", ComFuncUtil.CODE_ERROR);
            returnMap.put("respDesc", "回写失败" + e.getMessage());
            return ComFuncUtil.transOutParam(returnMap);
        }
        returnMap.put("respCode", ComFuncUtil.CODE_RIGHT);
        returnMap.put("respDesc", "success");
        return ComFuncUtil.transOutParam(returnMap);
    }

    /**
     * 3.3.7. 传输内容加密秘钥下发接口(pushKey)
     * 实名认证系统主动给接入方发起下发秘钥不定期下发，主要用于实时接口敏感内容加密。
     */
    public IData C898HQPushKey(IData inParamStr) throws Exception {
        IData inParam = new DataMap(inParamStr.toString());
        IData data = inParam.getData("params");
        IDataset inParamSet1 = new DatasetList();
        IData set1Map1 = new DataMap();
        set1Map1.put("FIELD", "busiCode"); // 业务编码	PUSH_KEY
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
        IData inParamResult = ComFuncUtil.checkInParam(data, inParamSet1);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        IData reqInfo = data.getData("reqInfo");
        IDataset inParamSet2 = new DatasetList();
        IData set2Map1 = new DataMap();
        set2Map1.put("FIELD", "transactionID"); // 全网唯一操作流水号
        set2Map1.put("TYPE", "String");
        inParamSet2.add(set2Map1);
        IData set2Map2 = new DataMap();
        set2Map2.put("FIELD", "key"); // 加密秘钥
        set2Map2.put("TYPE", "String");
        inParamSet2.add(set2Map2);
        IData set2Map3 = new DataMap();
        set2Map3.put("FIELD", "certExpdate"); // 失效日期	格式：yyyy-mm-dd
        set2Map3.put("TYPE", "String");
        inParamSet2.add(set2Map3);
        inParamResult = ComFuncUtil.checkInParam(reqInfo, inParamSet2);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        /**************************业务受理*************************/
        String key = reqInfo.getString("key");
        String certExpdate = reqInfo.getString("certExpdate");
        String transactionId = "";
        String remark = "";
        IData returnMap = new DataMap();
        // 首先查询数据库里是否有此条数据，数据库里的TRANSACTION_ID写死，保证只一条数据
        String targetCode = data.getString("targetCode"); // 898990:人证一体机; 898:集中交付
        if ("898990".equals(targetCode)) {
            transactionId = "1122334455"; // 898990:人证一体机
            remark = "人证一体机";
        } else {
            transactionId = "1122334466"; // 898:集中交付
            remark = "集中交付";
        }
        IDataset pushKey = CmOnlineUtil.queryPushKey(targetCode);
        if (IDataUtil.isEmpty(pushKey)) { // 没有则往数据库里插入记录
            IData outparams = new DataMap();
            outparams.put("TRANSACTION_ID", transactionId);
            outparams.put("PROV_CODE", targetCode);
            outparams.put("KEY", key);
            outparams.put("CERT_EXPDATE", certExpdate);
            outparams.put("CREATE_DATE", SysDateMgr.getSysTime());//创建时间
            outparams.put("UPDATE_TIME", SysDateMgr.getSysTime());//更新时间
            outparams.put("EXPIRE_DATE", certExpdate);// 过期时间
            outparams.put("RSRV_STR1", "C898HQPushKey");// 备注
            outparams.put("RSRV_STR2", remark);// 备注
            CmOnlineUtil.insertPushKey(outparams);
        } else { // 有则更新此条记录
            CmOnlineUtil.updatePushKey(key, targetCode, certExpdate, transactionId);
        }
        returnMap.put("respCode", ComFuncUtil.CODE_RIGHT);
        returnMap.put("respDesc", "success");
        return ComFuncUtil.transOutParam(returnMap);
    }

    /**
     * 3.1.9.6.业务办理日志查询(querySerivceHandleLog)
     * 查询指定服务号码在时间段内的业务办理日志，所查询的办理业务不包含缴费业务。
     */
    public IData C898HQQuerySerivceHandleLog(IData inParamStr) throws Exception {
        IData inParam = new DataMap(inParamStr.toString());
        IData params = inParam.getData("params");
        IDataset inParamSet1 = new DatasetList();
        IData set1Map1 = new DataMap();
        set1Map1.put("FIELD", "userMobile"); // 服务号码 客户手机号码
        set1Map1.put("TYPE", "String");
        inParamSet1.add(set1Map1);
        IData set1Map2 = new DataMap();
        set1Map2.put("FIELD", "beginTime"); // 开始时间 格式：yyyy-MM-dd hh:mi:ss
        set1Map2.put("TYPE", "String");
        inParamSet1.add(set1Map2);
        IData set1Map3 = new DataMap();
        set1Map3.put("FIELD", "endTime"); // 结束时间 格式：yyyy-MM-dd hh:mi:ss
        set1Map3.put("TYPE", "String");
        inParamSet1.add(set1Map3);
        IData inParamResult = ComFuncUtil.checkInParam(params, inParamSet1);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        /***********************************业务受理***********************************/
        IData returnMap = new DataMap(); // 返回结果
        IDataset result = new DatasetList();
        returnMap.put("result", result);
        IData resultItem = new DataMap();
        result.add(resultItem);
        IDataset tradeInfo = new DatasetList();
        resultItem.put("tradeInfo", tradeInfo);

        String serial_number = params.getString("userMobile");
        String beginTime = params.getString("beginTime");
        String endTime = params.getString("endTime");
        IDataset tradeSet = this.qryTradeHisBySnAndTime(serial_number, beginTime, endTime);
        for (int i = 0; i < tradeSet.size(); i++) {
            IData tradeInfoItem = new DataMap();
            tradeInfo.add(tradeInfoItem);
            tradeInfoItem.put("tradeId", tradeSet.getData(i).get("TRADE_ID"));
            tradeInfoItem.put("tradeTime", tradeSet.getData(i).get("ACCEPT_DATE"));
            tradeInfoItem.put("tradeType", tradeSet.getData(i).get("TRADE_TYPE_CODE"));
            String cust_id = tradeSet.getData(i).getString("CUST_ID");
            IDataset custInfo = CustomerInfoQry.getCustomerByCustID(cust_id);
            String cust_name = custInfo.getData(0).getString("CUST_NAME");
            tradeInfoItem.put("tradeCustName", cust_name);
            tradeInfoItem.put("tradeSerNumber", serial_number);
            tradeInfoItem.put("tradeResult", "成功");
            String trade_depart_id = tradeSet.getData(0).getString("TRADE_DEPART_ID");
            String depart_name = UDepartInfoQry.getDepartNameByDepartId(trade_depart_id);
            tradeInfoItem.put("tradeHandlerOrg", depart_name);
            String trade_staff_id = tradeSet.getData(i).getString("TRADE_STAFF_ID");
            tradeInfoItem.put("tradeStaffID", trade_staff_id);
            IDataset staffInfo = StaffInfoQry.qryStaffInfoByStaffId(trade_staff_id);
            String trade_staff_name = staffInfo.getData(0).getString("STAFF_NAME");
            tradeInfoItem.put("tradeStaffName", trade_staff_name);
            String in_mode_code = tradeSet.getData(i).getString("IN_MODE_CODE");
            tradeInfoItem.put("tradeChannelID", in_mode_code);
            String inModeName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_INMODE", "IN_MODE_CODE", "IN_MODE", in_mode_code);
            tradeInfoItem.put("tradeChannelName", inModeName);
        }
        returnMap.put("resultRows", tradeSet.size());
        returnMap.put("respCode", ComFuncUtil.CODE_RIGHT);
        returnMap.put("respDesc", "success");
        return ComFuncUtil.transOutParam(returnMap);
    }

    public static IDataset qryTradeHisBySnAndTime(String serial_number, String beginTime, String endTime) throws
            Exception {
        IData inparams = new DataMap();
        inparams.put("SERIAL_NUMBER", serial_number);
        inparams.put("BEGIN_TIME", beginTime);
        inparams.put("END_TIME", endTime);
        SQLParser parser = new SQLParser(inparams);
        parser.addSQL(" SELECT TRADE_ID, ACCEPT_MONTH, BATCH_ID, ORDER_ID, PROD_ORDER_ID, BPM_ID, CAMPN_ID, ");
        parser.addSQL(" TRADE_TYPE_CODE, PRIORITY, SUBSCRIBE_TYPE, SUBSCRIBE_STATE, NEXT_DEAL_TAG, ");
        parser.addSQL(" IN_MODE_CODE, CUST_ID, CUST_NAME, USER_ID, ACCT_ID, SERIAL_NUMBER, NET_TYPE_CODE, ");
        parser.addSQL(" EPARCHY_CODE, CITY_CODE, PRODUCT_ID, BRAND_CODE, CUST_ID_B, USER_ID_B, ACCT_ID_B, ");
        parser.addSQL(" SERIAL_NUMBER_B, CUST_CONTACT_ID, SERV_REQ_ID, INTF_ID, ACCEPT_DATE, TRADE_STAFF_ID, ");
        parser.addSQL(" TRADE_DEPART_ID, TRADE_CITY_CODE, TRADE_EPARCHY_CODE, TERM_IP, OPER_FEE, FOREGIFT, ");
        parser.addSQL(" ADVANCE_PAY, INVOICE_NO, FEE_STATE, FEE_TIME, FEE_STAFF_ID, PROCESS_TAG_SET, ");
        parser.addSQL(" OLCOM_TAG, FINISH_DATE, EXEC_TIME, EXEC_ACTION, EXEC_RESULT, EXEC_DESC, CANCEL_TAG, ");
        parser.addSQL(" CANCEL_DATE, CANCEL_STAFF_ID, CANCEL_DEPART_ID, CANCEL_CITY_CODE, CANCEL_EPARCHY_CODE, ");
        parser.addSQL(" UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_STR1, RSRV_STR2, ");
        parser.addSQL(" RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_STR6, RSRV_STR7, RSRV_STR8, RSRV_STR9, ");
        parser.addSQL(" RSRV_STR10, IS_NEED_HUMANCHECK, PF_TYPE, FREE_RESOURCE_TAG, PF_WAIT ");
        parser.addSQL(" FROM TF_BH_TRADE A ");
        parser.addSQL(" WHERE A.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND A.ACCEPT_DATE > TO_DATE(:BEGIN_TIME, 'YYYY-MM-DD HH24:MI:SS') ");
        parser.addSQL(" AND A.ACCEPT_DATE < TO_DATE(:END_TIME, 'YYYY-MM-DD HH24:MI:SS') ");
        return Dao.qryByParse(parser, Route.getJourDb());
    }

    /**
     * 3.1.2.8.备卡激活(activatePreparedCard)
     * 备卡激活操作提交。
     */
    public IData C898HQActivatePreparedCard(IData inParamStr) throws Exception {
        IData inParam = new DataMap(inParamStr.toString());
        IData params = inParam.getData("params");
        IDataset inParamSet1 = new DatasetList();
        IData set1Map1 = new DataMap();
        set1Map1.put("FIELD", "userMobile"); // 服务号码 客户手机号码
        set1Map1.put("TYPE", "String");
        inParamSet1.add(set1Map1);
        IData set1Map2 = new DataMap();
        set1Map2.put("FIELD", "preparedSimId"); // 备卡sim卡号
        set1Map2.put("TYPE", "String");
        inParamSet1.add(set1Map2);
        IData inParamResult = ComFuncUtil.checkInParam(params, inParamSet1);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        /***********************************业务受理***********************************/
        IData returnMap = new DataMap(); // 返回结果
        IDataset result = new DatasetList();
        returnMap.put("result", result);
        IData resultItem = new DataMap();
        result.add(resultItem);
        String serial_number = params.getString("userMobile");
        String bak_sim_card_no = params.getString("preparedSimId");
        String remark = params.getString("remark", "");

        IData tradeParam = new DataMap();
        tradeParam.put("SERIAL_NUMBER", serial_number);
        tradeParam.put("AUTH_SERIAL_NUMBER", serial_number);
        tradeParam.put("BAK_CARD_NO", bak_sim_card_no);
        tradeParam.put("REMARK", remark);
        tradeParam.put("START_DATE", SysDateMgr.getSysTime());
        tradeParam.put("TRADE_TYPE_CODE", "144");
        tradeParam.put("SUBMIT_TYPE", "submit");
        tradeParam.put("listener", "onTradeSubmit");

        // 查找用户的user_id
        IDataset userInfoSet = UserInfoQry.getUserInfoBySn(serial_number, "0");
        if (IDataUtil.isEmpty(userInfoSet)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据手机号找不到用户信息");
        }
        IData userInfo = userInfoSet.getData(0);
        String userId = userInfo.getString("USER_ID");
        IData vipSimBakInfo = new DataMap();
        IData vipIdData = new DataMap();
        IDataset mobilePays = UserPlatSvcInfoQry.queryUserPlatByUserType(userId, "54");
        if (mobilePays.size() > 0) {
            // 目前大客户备卡、备卡激活、异地补卡界面对手机支付用户有拦截，根据集团的规范是不进行拦截的，请业务支撑部进行优化，取消拦截。
            CSAppException.apperr(CrmCardException.CRM_CARD_265);
        }
        vipSimBakInfo.put("VIP_ID", userId);
        vipSimBakInfo.put("ACT_TAG", "0");
        IDataset simbakInfos = CustVipInfoQry.getSimBakInfos(vipSimBakInfo);
        if (simbakInfos == null || simbakInfos.size() <= 0) {
            IData param = new DataMap();
            param.put("SERIAL_NUMBER", serial_number);
            param.put("REMOVE_TAG", "0");
            IDataset vipInfos = this.getVipInfos(param);
            if (vipInfos == null || vipInfos.size() <= 0) { // 当无大客户信息，查询备卡
                IData vipInfoTemp = new DataMap();
                IDataset vipInfosTemp = CustVipInfoQry.queryCustVipInfoBySn(serial_number, "1");
                if (vipInfosTemp == null || vipInfosTemp.size() <= 0) {
                    CSAppException.apperr(CrmCardException.CRM_CARD_14); // common.error("550202:该客户还没有备卡！");
                }
            } else {// 当有大客户信息，查询备卡
                vipIdData = (IData) vipInfos.get(0);
                String vipId = vipIdData.getString("VIP_ID", "");
                IData vipInfoTemp = new DataMap();
                vipInfoTemp.put("VIP_ID", vipId);
                vipInfoTemp.put("ACT_TAG", "0");
                simbakInfos = CustVipInfoQry.getSimBakInfos(vipInfoTemp);
                if (simbakInfos == null || simbakInfos.size() <= 0) {
                    CSAppException.apperr(CrmCardException.CRM_CARD_16); // common.error("550203:该用户没有办理备卡，业务无法继续！");
                } else {
                    tradeParam.put("NEW_IMSI", vipIdData.getString("IMSI"));
                }
            }
        } else {
            IData tempInfo = (IData) simbakInfos.get(0);
            tradeParam.put("NEW_IMSI", tempInfo.getString("IMSI"));
        }
        // 用户有效SIM卡资源信息
        IData userResInfo = new DataMap();
        userResInfo.put("X_CONN_DB_CODE", CSBizBean.getTradeEparchyCode());
        userResInfo.put("USER_ID", userId);
        userResInfo.put("RES_TYPE_CODE", "1");
        IDataset userResInfoList = this.getUserResInfo(userResInfo);
        if (userResInfoList == null || userResInfoList.size() <= 0) {
            CSAppException.apperr(CrmCardException.CRM_CARD_20); // common.error("550205:获取用户有效SIM卡信息无数据!");
        }
        IData userSimInfo = (IData) userResInfoList.get(0);
        // 获取老卡的opc值start
        IDataset oldResInfos = ResCall.getSimCardInfo("0", userSimInfo.getString("RES_CODE"), "", "");
        tradeParam.put("RES_CODE", userSimInfo.getString("RES_CODE"));
        String opc = "";
        String imsi = "";
        if (oldResInfos != null && oldResInfos.size() > 0) {
            opc = oldResInfos.getData(0).getString("OPC", "");
            imsi = oldResInfos.getData(0).getString("IMSI", "");

        }
        tradeParam.put("OLD_OPC", opc);
        tradeParam.put("IMSI", imsi);
        getSimBakKindName(simbakInfos);
        tradeParam.put("OPC", simbakInfos.getData(0).getString("OPC"));
        IDataset tradeSet = new DatasetList();
        try {
            tradeSet = CSAppCall.call("SS.VipSimBakActRegSVC.tradeReg", tradeParam);
        } catch (Exception e) {
            returnMap.put("respCode", ComFuncUtil.CODE_ERROR);
            returnMap.put("respDesc", e.getMessage().toString());
        }
        resultItem.put("orderId", tradeSet.getData(0).getString("ORDER_ID"));
        returnMap.put("respCode", ComFuncUtil.CODE_RIGHT);
        returnMap.put("respDesc", "success");
        return ComFuncUtil.transOutParam(returnMap);
    }

    private void getSimBakKindName(IDataset dataset) throws Exception {
        for (int i = 0; i < dataset.size(); i++) {
            IData simCard = dataset.getData(i);
            String simCardName = simCard.getString("CLIENT_INFO3");
            // simCard.put("SIM_TYPE_NAME","testName");
            simCard.put("SIM_TYPE_NAME", ((simCardName == null || "".equals(simCardName))) ? this.getSimCardKindName(simCard.getString("SIM_CARD_NO")) : simCardName);
            IDataset oldResInfos = ResCall.getSimCardInfo("0", simCard.getString("SIM_CARD_NO"), "", "");
            String resTypeCode = "";
            String opc = "";
            if (oldResInfos != null && oldResInfos.size() > 0) {
                resTypeCode = oldResInfos.getData(0).getString("RES_TYPE_CODE", "");
                opc = oldResInfos.getData(0).getString("OPC", "");
            } else {
                resTypeCode = "";
            }
            simCard.put("RES_TYPE_CODE", resTypeCode);
            simCard.put("OPC", opc);
        }
    }

    private String getSimCardKindName(String simCardNo) throws Exception {
        IDataset simCardInfo = ResCall.getSimCardInfo("0", simCardNo, "", "");
        if (simCardInfo != null && simCardInfo.size() > 0) {
            IData tempInfo = (IData) simCardInfo.get(0);
            if (tempInfo.getInt("X_RESULTCODE") == 0) {
                return tempInfo.getString("RES_KIND_NAME", "未知卡类型");
            } else {
                CSAppException.apperr(CrmCommException.CRM_COMM_13, "550206", tempInfo.getString("X_RESULTINFO"));
                // common.error("550206:" +outParam.getString("X_RESULTINFO"));
            }
            return "未知卡类型";
        } else {
            return "未知卡类型";
        }
    }

    public IDataset getUserResInfo(IData params) throws Exception {
        return UserResInfoQry.queryUserResByUserIdResType(params.getString("USER_ID"), params.getString("RES_TYPE_CODE"));
    }

    public IDataset getVipInfos(IData params) throws Exception {
        return CustVipInfoQry.queryVipInfoBySn(params.getString("SERIAL_NUMBER"), params.getString("REMOVE_TAG"));
    }

    /**
     * 根据用户号码查询可用备卡信息。
     * SS.VipSimBakQuerySVC.getVipSimBakActInfo
     */
    public IData C898HQQueryPreparedCard(IData inParamStr) throws Exception {
        IData inParam = new DataMap(inParamStr.toString());
        IData params = inParam.getData("params");
        IDataset inParamSet1 = new DatasetList();
        IData set1Map1 = new DataMap();
        set1Map1.put("FIELD", "userMobile"); // 服务号码 客户手机号码
        set1Map1.put("TYPE", "String");
        inParamSet1.add(set1Map1);
        IData inParamResult = ComFuncUtil.checkInParam(params, inParamSet1);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        // 根据手机号码查询用户信息
        String serialNumber = params.getString("userMobile"); // 服务号码 客户手机号码
        IDataset UserInfoSet = UserInfoQry.getUserInfoBySn(serialNumber, "0");
        if (IDataUtil.isEmpty(UserInfoSet)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, serialNumber + "已经失效或者不存在");
        }
        String userId = UserInfoSet.getData(0).getString("USER_ID");

        IData vipIdData = new DataMap();
        IData vipSimBakInfo = new DataMap();
        vipSimBakInfo.put("VIP_ID", userId);
        IDataset simbakInfos = CustVipInfoQry.getSimBakInfos(vipSimBakInfo); // 查询用户备卡信息
        if (IDataUtil.isEmpty(simbakInfos)) {
            IDataset vipInfos = CustVipInfoQry.queryVipInfoBySn(serialNumber, "0");
            if (IDataUtil.isEmpty(vipInfos)) { // 当无大客户信息，查询备卡
                IDataset vipInfoTempSet = CustVipInfoQry.queryCustVipInfoBySn(serialNumber, "1");
                if (IDataUtil.isEmpty(vipInfoTempSet)) {
                    CSAppException.apperr(CrmCardException.CRM_CARD_14); // 该客户还没有备卡！
                }
            }
            IData vipInfoTemp = new DataMap();
            vipInfoTemp.put("VIP_ID", vipInfos.getData(0).getString("VIP_ID"));
            simbakInfos = CustVipInfoQry.getSimBakInfos(vipInfoTemp); // 查询大客户备卡信息
            if (IDataUtil.isEmpty(simbakInfos)) {
                CSAppException.apperr(CrmCardException.CRM_CARD_16); // 该用户没有办理备卡，业务无法继续！
            }
        }

        IData returnMap = new DataMap(); // 返回结果
        IDataset result = new DatasetList();
        returnMap.put("result", result);
        IData resultItem = new DataMap();
        result.add(resultItem);

        // 查询用户当前有效SIM卡资源信息
        IDataset userResInfoList = UserResInfoQry.queryUserResByUserIdResType(userId, "1");
        if (IDataUtil.isEmpty(userResInfoList)) {
            CSAppException.apperr(CrmCardException.CRM_CARD_20); // 获取用户有效SIM卡信息无数据!
        }
        IData userSimInfo = (IData) userResInfoList.get(0);
        resultItem.put("oldSimId", ((IData) userResInfoList.get(0)).getString("RES_CODE")); // 用户当前使用的SIM卡号
        IDataset preparedSimList = new DatasetList();
        resultItem.put("preparedSimList", preparedSimList);
        for (int i = 0; i < simbakInfos.size(); i++) {
            IData simbakInfoItem = simbakInfos.getData(i);
            String act_tag = simbakInfoItem.getString("ACT_TAG");
            if (!"0".equals(act_tag)) {
                continue;
            }
            IData preparedSimListItem = new DataMap();
            preparedSimList.add(preparedSimListItem);
            String sim_card_no = simbakInfoItem.getString("SIM_CARD_NO");
            preparedSimListItem.put("preparedSimId", sim_card_no); // 备卡sim卡号

            //调资源接口判断是否4G卡
            IDataset simCardDatas = ResCall.getSimCardInfo("0", sim_card_no, "", null);
            if (IDataUtil.isEmpty(simCardDatas)) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据SIM卡号" + sim_card_no + "查询资源接口获取SIM卡信息失败");
            }
            String cardModeType = simCardDatas.getData(0).getString("CARD_MODE_TYPE");
            String opc = simCardDatas.getData(0).getString("OPC", "");
            boolean is4G = true;
            if ("1".equals(cardModeType) || ((!("2".equals(cardModeType))) && "".equals(opc))) {
                is4G = false;
            }
            String preparedSimTypeName = is4G ? "4G " : "非4G ";
            if ("1".equals(cardModeType)) {
                preparedSimTypeName += "SIM卡";
            } else if ("2".equals(cardModeType)) {
                preparedSimTypeName += "USIM卡";
            }
            preparedSimListItem.put("preparedSimTypeName", preparedSimTypeName); // 备卡sim卡类型名称

            String preparedSimTime = simbakInfoItem.getString("SEND_DATE");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date preparedSimTimeDate = sdf.parse(preparedSimTime);
            sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            preparedSimTime = sdf.format(preparedSimTimeDate);
            preparedSimListItem.put("preparedSimTime", preparedSimTime); // 备卡发卡时间
            preparedSimListItem.put("preparedSimState", "0".equals(act_tag) ? "未激活" : "正常"); // 备卡状态
        }
        returnMap.put("respCode", ComFuncUtil.CODE_RIGHT);
        returnMap.put("respDesc", "success");
        return ComFuncUtil.transOutParam(returnMap);
    }

    /**
     * 根据号码查询所有支持的呼转类型及已设置的呼转号码。无论是否设置呼转号码都要返回支持的呼转类型。
     * 参考接口：SS.QueryInfoSVC.queryVEMLAttr
     */
    public IData C898HQQueryCallFowardingNumber(IData inParamStr) throws Exception {
        IData inParam = new DataMap(inParamStr.toString());
        IData params = inParam.getData("params");
        IDataset inParamSet1 = new DatasetList();
        IData set1Map1 = new DataMap();
        set1Map1.put("FIELD", "userMobile"); // 服务号码 客户手机号码
        set1Map1.put("TYPE", "String");
        inParamSet1.add(set1Map1);
        IData inParamResult = ComFuncUtil.checkInParam(params, inParamSet1);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        IData returnMap = new DataMap(); // 返回结果
        returnMap.put("respCode", ComFuncUtil.CODE_RIGHT);
        returnMap.put("respDesc", "success");
        IDataset result = new DatasetList();
        returnMap.put("result", result);
        IData resultItem = new DataMap();
        result.add(resultItem);
        resultItem.put("cfEffType", "0");
        IDataset callFowardingList = new DatasetList();
        resultItem.put("callFowardingList", callFowardingList);

        IDataset staticInfos = StaticInfoQry.getStaticValueByTypeId("VEML_SVC_CODE"); // 查询四种配置的呼叫转移类型
        if (IDataUtil.isEmpty(staticInfos)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "呼叫转移参数配置错误，请检查");
        }
        String serialNumber = params.getString("userMobile");
        IData user = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(user)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据手机号码未找到合法用户");
        }
        String user_id = user.getString("USER_ID");

        for (int i = 0; i < staticInfos.size(); i++) {
            IData staticInfo = staticInfos.getData(i);
            String forward_type = staticInfo.getString("DATA_ID"); // 1：无条件呼转,2：遇忙呼转,3：无应答转,4：不可及转
            String service_id = staticInfo.getString("DATA_NAME"); // 呼叫转移的SERVICE_ID
            IData callFowardingListItem = new DataMap();
            callFowardingList.add(callFowardingListItem);
            callFowardingListItem.put("callFowardingType", forward_type);
            String callFowardingTypeName = "";
            if ("1".equals(forward_type)) {
                callFowardingTypeName = "无条件呼转";
            } else if ("2".equals(forward_type)) {
                callFowardingTypeName = "遇忙呼转";
            } else if ("3".equals(forward_type)) {
                callFowardingTypeName = "无应答转";
            } else if ("4".equals(forward_type)) {
                callFowardingTypeName = "不可及转";
            }
            callFowardingListItem.put("callFowardingTypeName", callFowardingTypeName);
            IDataset userAttrs = this.getUserAttrSvc(user_id, service_id);
            if (IDataUtil.isEmpty(userAttrs)) {
                continue;
            }
            callFowardingListItem.put("callFowardingNumber", userAttrs.getData(0).getString("ATTR_VALUE"));
            callFowardingListItem.put("effDate", userAttrs.getData(0).getString("START_DATE"));
            callFowardingListItem.put("expDate", userAttrs.getData(0).getString("END_DATE"));
        }
        return ComFuncUtil.transOutParam(returnMap);
    }

    /**
     * 获取用户呼叫转移的服务
     */
    private IDataset getUserAttrSvc(String userId, String serviceId) throws Exception {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERVICE_ID", serviceId);
        StringBuilder sql = new StringBuilder(1000);
        sql.append(" SELECT V.USER_ID,V.SERVICE_ID,V.MAIN_TAG,V.INST_ID,V.START_DATE,V.END_DATE,V.INST_ID,R.INST_TYPE,R.RELA_INST_ID,R.ATTR_CODE,R.ATTR_VALUE ");
        sql.append(" FROM TF_F_USER_SVC V,TF_F_USER_ATTR R ");
        sql.append(" WHERE 1=1 ");
        sql.append(" AND V.USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append(" AND V.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        sql.append(" AND V.SERVICE_ID = :SERVICE_ID ");
        sql.append(" AND SYSDATE < V.END_DATE ");
        sql.append(" AND V.INST_ID = R.RELA_INST_ID ");
        sql.append(" AND R.USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append(" AND R.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        sql.append(" AND SYSDATE < R.END_DATE ");
        sql.append(" AND R.INST_TYPE = 'S' ");
        IDataset ids = Dao.qryBySql(sql, param);
        return ids;
    }

    /**
     * 3.3.1.33.号码用户校验接口(checkSvcNumUser)
     * 实名认证系统在APP+NFC模式做补卡全流程，需要进行号码用户信息进行校验。
     * 判断号码用户信息和当前身份证读取的是否一致。
     */
    public IData C898HQCheckSvcNumUser(IData inParamStr) throws Exception {
        IData inParam = new DataMap(inParamStr.toString());
        IData params = inParam.getData("params");
        IDataset inParamSet1 = new DatasetList();
        IData set1Map1 = new DataMap();
        set1Map1.put("FIELD", "busiCode"); // 业务编码	CHECK_SVCNUM_USER
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
        set2Map2.put("FIELD", "svcNum"); // 需要补卡的号码
        set2Map2.put("TYPE", "String");
        inParamSet2.add(set2Map2);
        IData set2Map3 = new DataMap();
        set2Map3.put("FIELD", "custCertNo"); // 客户身份证号码
        set2Map3.put("TYPE", "String");
        inParamSet2.add(set2Map3);
        IData set2Map4 = new DataMap();
        set2Map4.put("FIELD", "custName"); // 客户名称
        set2Map4.put("TYPE", "String");
        inParamSet2.add(set2Map4);
        inParamResult = ComFuncUtil.checkInParam(reqInfo, inParamSet2);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }

        /***********************************业务受理***********************************/
        String transactionID = reqInfo.getString("transactionID");
        String svcNum = reqInfo.getString("svcNum");
        String custName = reqInfo.getString("custName");
        String custCertNo = reqInfo.getString("custCertNo");

        String targetCode = params.getString("targetCode");
        IDataset pubKeySet = CmOnlineUtil.queryPushKey(targetCode);
        if (IDataUtil.isEmpty(pubKeySet)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "找不到对应的公钥信息");
        }
        String pubKey = pubKeySet.getData(0).getString("KEY");
        RealNameMsDesPlus plus = new RealNameMsDesPlus(pubKey);
        custName = plus.decrypt(custName);
        custCertNo = plus.decrypt(custCertNo);

        IData returnMap = new DataMap(); // 返回结果
        IDataset result = new DatasetList();
        returnMap.put("result", result);
        IData resultItem = new DataMap();
        result.add(resultItem);
        resultItem.put("transactionID", transactionID);
        IData resInfo = new DataMap();
        resultItem.put("resInfo", resInfo);
        // 根据手机号查找用户信息
        IData userInfo = UcaInfoQry.qryUserInfoBySn(svcNum);
        if (IDataUtil.isEmpty(userInfo)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据手机号未找到用户信息");
        }
        // 根据custId查找客户信息
        String custId = userInfo.getString("CUST_ID");
        IDataset custInfo = CustomerInfoQry.getCustomerByCustID(custId);
        if (IDataUtil.isEmpty(custInfo)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到客户信息");
        }
        String realCustName = custInfo.getData(0).getString("CUST_NAME");
        String realPsptId = custInfo.getData(0).getString("PSPT_ID");
        if (StringUtils.equals(custName, realCustName) && StringUtils.equals(custCertNo, realPsptId)) {
            resInfo.put("isSuc", "1");
        } else {
            resInfo.put("isSuc", "0");
        }
        returnMap.put("respCode", ComFuncUtil.CODE_RIGHT);
        returnMap.put("respDesc", "success");
        return ComFuncUtil.transOutParam(returnMap);
    }

    private void transKI(IData input) throws Exception {
        // 加密K和OPC，并写卡参数回传
        LocalDecPreData local = new LocalDecPreData();
        String seqNo = SeqMgr.getTradeId().substring(6);
        local.setSeqNo(seqNo);
        local.setEncPresetDataK(input.getString("KI", ""));
        local.setEncPresetDataOPc(input.getString("OPC", ""));
        local.setLocalProvCode(input.getString("LOCAL_PROVCODE", "898"));
        local.setSignature(input.getString("SIGNATURE", ""));
        WebServiceClient wsc = new WebServiceClient();
        LocalDecPreDataRsp rsp = new LocalDecPreDataRsp();
        try {
            rsp = wsc.decPreData(local);
            if ("0".equals(rsp.getResultCode())) {
                String ki = rsp.getPresetData().getK();
                String opc = rsp.getPresetData().getOPC();
                input.put("KI", ki);
                input.put("OPC", opc);
            } else {
                String strError = "加密机加解密错误，" + rsp.getResultMessage();
                CSAppException.apperr(CrmCommException.CRM_COMM_103, strError);
            }
        } catch (Exception e) {
            log.error(e);
            String strError = "加密机加解密错误，" + rsp.getResultMessage();
            CSAppException.apperr(CrmCommException.CRM_COMM_103, strError);
        }
    }

    /**
     * 3.1.4.4 预售卡(presaleCardOrder)
     * 实名认证系统将客户所选号码、身份证信息、卡等信息传给省公司，省公司进行预售卡处理，
     * 预售卡状态时号码不能进行正常通信，需经过认证激活后才可以正常使用。
     */
    public IData C898HQPresaleCardOrder(IData inParamStr) throws Exception {
        IData inParam = new DataMap(inParamStr.toString());
        IData params = inParam.getData("params");
        IDataset inParamSet1 = new DatasetList();
        IData set1Map1 = new DataMap();
        set1Map1.put("FIELD", "busiCode"); // 业务编码	PRESALE_CARD_ORDER
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
        set2Map1.put("FIELD", "busiSeq"); // 订单流水
        set2Map1.put("TYPE", "String");
        inParamSet2.add(set2Map1);
        IData set2Map2 = new DataMap();
        set2Map2.put("FIELD", "svcNum"); // 开户号码
        set2Map2.put("TYPE", "String");
        inParamSet2.add(set2Map2);
        IData set2Map3 = new DataMap();
        set2Map3.put("FIELD", "custName"); // 客户名称 加密传输，接口平台仅做透传
        set2Map3.put("TYPE", "String");
        inParamSet2.add(set2Map3);
        IData set2Map4 = new DataMap();
        set2Map4.put("FIELD", "custCertNo"); // 客户身份证号码
        set2Map4.put("TYPE", "String");
        inParamSet2.add(set2Map4);
        IData set2Map5 = new DataMap();
        set2Map5.put("FIELD", "telephone"); // 客户联系电话
        set2Map5.put("TYPE", "String");
        inParamSet2.add(set2Map5);
        IData set2Map6 = new DataMap();
        set2Map6.put("FIELD", "productCode"); // 套餐编码
        set2Map6.put("TYPE", "String");
        inParamSet2.add(set2Map6);
        IData set2Map7 = new DataMap();
        set2Map7.put("FIELD", "simType"); // 卡类型 01:128K空白USIM卡
        set2Map7.put("TYPE", "String");
        inParamSet2.add(set2Map7);
        IData set2Map8 = new DataMap();
        set2Map8.put("FIELD", "sn"); // 卡序列号
        set2Map8.put("TYPE", "String");
        inParamSet2.add(set2Map8);
        IData set2Map9 = new DataMap();
        set2Map9.put("FIELD", "ki"); // 鉴权码
        set2Map9.put("TYPE", "String");
        inParamSet2.add(set2Map9);
        IData set2Map10 = new DataMap();
        set2Map10.put("FIELD", "ki"); // 鉴权码
        set2Map10.put("TYPE", "String");
        inParamSet2.add(set2Map10);
        IData set2Map11 = new DataMap();
        set2Map11.put("FIELD", "opc"); // opc
        set2Map11.put("TYPE", "String");
        inParamSet2.add(set2Map11);
        IData set2Map12 = new DataMap();
        set2Map12.put("FIELD", "imsi"); // imsi
        set2Map12.put("TYPE", "String");
        inParamSet2.add(set2Map12);
        IData set2Map13 = new DataMap();
        set2Map13.put("FIELD", "iccid"); // iccid
        set2Map13.put("TYPE", "String");
        inParamSet2.add(set2Map13);
        IData set2Map14 = new DataMap();
        set2Map14.put("FIELD", "signature"); // signature 全网唯一操作流水号+订单流水+开户号码+归属地市编码+客户联系电话+套餐编码+收费金额
        set2Map14.put("TYPE", "String");
        inParamSet2.add(set2Map14);
        inParamResult = ComFuncUtil.checkInParam(reqInfo, inParamSet2);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }
        /********************************业务受理****************************/
        String transId = params.getString("transactionID");
        IData returnMap = new DataMap();
        IDataset result = new DatasetList();
        returnMap.put("result", result);
        IData resultItem = new DataMap();
        result.add(resultItem);
        resultItem.put("transactionID", transId);
        String serialNumber = reqInfo.getString("svcNum");

        String transactionIdPriKey = "1122334488";
        IDataset pushCardPriKey = CmOnlineUtil.queryPushCardPkey(transactionIdPriKey);
        if (IDataUtil.isEmpty(pushCardPriKey)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到卡数据密钥");
        }
        String pri = pushCardPriKey.getData(0).getString("KEY");

        String transactionIdPubkey = "1122334477";
        IDataset pushCardPubKey = CmOnlineUtil.queryPushCardPkey(transactionIdPubkey);
        if (IDataUtil.isEmpty(pushCardPubKey)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到卡数据公钥");
        }
        String pub = pushCardPubKey.getData(0).getString("KEY");

        String cityCode = reqInfo.getString("cityCode");
        String custName = reqInfo.getString("custName");
        String custCertNo = reqInfo.getString("custCertNo");
        String telephone = reqInfo.getString("telephone");
        String PRODUCT_ID = reqInfo.getString("productCode");
        String simType = reqInfo.getString("simType");
        String sn = reqInfo.getString("sn");
        String ki = reqInfo.getString("ki");
        String opc = reqInfo.getString("opc");
        String imsi = reqInfo.getString("imsi");
        String iccid = reqInfo.getString("iccid");
        String busiSeq = reqInfo.getString("busiSeq");
        String priceFee = reqInfo.getString("priceFee");
        String signatureIn = reqInfo.getString("signature");

        // des字段解密
        String targetCode = params.getString("targetCode");
        IDataset pubKeySet = CmOnlineUtil.queryPushKey(targetCode);
        if (IDataUtil.isEmpty(pubKeySet)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "找不到对应的公钥信息");
        }
        String pubKey = pubKeySet.getData(0).getString("KEY");
        RealNameMsDesPlus plus = new RealNameMsDesPlus(pubKey);
        custName = plus.decrypt(custName);
        custCertNo = plus.decrypt(custCertNo);

        sn = new Rsa.Decoder(pub).decode(sn);
        ki = new Rsa.Decoder(pub).decode(ki);
        opc = new Rsa.Decoder(pub).decode(opc);
        imsi = new Rsa.Decoder(pub).decode(imsi);
        iccid = new Rsa.Decoder(pub).decode(iccid);

        // 查询此号码是否已经有预售卡记录了
        IDataset existValidRecord = this.qryPreCardRecord(serialNumber);
        if (IDataUtil.isNotEmpty(existValidRecord)) {
            resultItem.put("isSuc", "0");
            String signature = transId + ComFuncUtil.CODE_ERROR + "0";
            signature = new Rsa.Encoder(pri).encode(signature);
            resultItem.put("signature", signature);
            returnMap.put("respCode", ComFuncUtil.CODE_ERROR);
            returnMap.put("respDesc", "该号码" + serialNumber + "已存在有效的预售卡记录！ ");
            return ComFuncUtil.transOutParam(returnMap);
        }

        // KI和OPC加密
        IData encData = new DataMap();
        encData.put("KI", ki);
        encData.put("OPC", opc);
        encData.put("SIGNATURE", "");
        encData.put("LOCAL_PROVCODE", "898");
        transKI(encData);
        ki = encData.getString("KI");
        opc = encData.getString("OPC");

        // KI,OPC省内规则加密
        KIFunc kifunc = new KIFunc();
        ki = kifunc.EncryptKI(ki);
        opc = kifunc.EncryptKI(opc);

        // 调用写卡结果回写接口
        IData backInput = new DataMap();
        backInput.put("SN", sn);
        backInput.put("OPC", opc);
        backInput.put("KI", ki);
        backInput.put("IMSI", imsi);
        backInput.put("ICCID", iccid);
        IDataset resInfos = ResCall.backWriteOnLineSaleCard(backInput);

        // 先进行sim卡选占
        IData checkSimData = new DataMap();
        checkSimData.put("SIM_CARD_NO", iccid);
        checkSimData.put("SERIAL_NUMBER", serialNumber);
        checkSimData.put("PRE_SALE", "Y");
        IDataset checkSimResult = CSAppCall.call("SS.CreatePersonUserSVC.checkSimCardNo", checkSimData);

        // 预开户
        IData preOpenParam = new DataMap();
        preOpenParam.put("SERIAL_NUMBER", serialNumber);
        preOpenParam.put("SIM_CARD_NO", iccid);
        preOpenParam.put("TRADE_TYPE_CODE", "500");
        preOpenParam.put("ORDER_TYPE_CODE", "500");
        preOpenParam.put("PRODUCT_ID", PRODUCT_ID);
        preOpenParam.put("M2M_FLAG", "0");
        preOpenParam.put("USER_TYPE_CODE", "0");
        preOpenParam.put("ACCT_TAG", "2");
        preOpenParam.put("CUST_NAME", custName);
        preOpenParam.put("PSPT_ID", custCertNo);
        preOpenParam.put("PAY_MODE_CODE", "0");
        preOpenParam.put("AGENT_PRESENT_FEE", "");
        preOpenParam.put("TEST_CARD_TYPE", "1");
        preOpenParam.put("LINK_PHONE", "10086");
        preOpenParam.put("PREOPENSELF", "Y");
        preOpenParam.put("OPEN_MODE", "0");
        // 根据PRODUCT_ID查询预必选服务
        IDataset serviceSets = UPackageElementInfoQry.queryOfferForceElementsByProductId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, PRODUCT_ID, BofConst.ELEMENT_TYPE_CODE_SVC);
        if (serviceSets.isEmpty()) {
            CSAppException.apperr(ProductException.CRM_PRODUCT_192, PRODUCT_ID);
        }
        boolean gprsFlag = false;
        String packageIdForGprs = "";
        for (int i = 0; i < serviceSets.size(); i++) {
            // 计算服务的开始和结束时间（生失效关系）
            IData elem = serviceSets.getData(i);
            String serviceId = elem.getString("SERVICE_ID");
            String packageId = elem.getString("PACKAGE_ID");
            SvcData svcData = new SvcData(elem);
            ProductTimeEnv env = new ProductTimeEnv();
            env.setBasicAbsoluteStartDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
            String startDate = ProductModuleCalDate.calStartDate(svcData, env);
            svcData.setStartDate(startDate);
            String endDate = ProductModuleCalDate.calEndDate(svcData, startDate);
            // 修改服务的属性
            serviceSets.getData(i).put("ELEMENT_ID", serviceId);
            serviceSets.getData(i).put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
            serviceSets.getData(i).put("END_DATE", endDate);
            serviceSets.getData(i).put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_SVC);
            serviceSets.getData(i).put("START_DATE", startDate);
            serviceSets.getData(i).put("INST_ID", "");
            if ("22".equals(serviceId)) {
                gprsFlag = true;
                packageIdForGprs = packageId;
            }
        }
        // 假如预开户有默认22服务则绑定902优惠
        IData discntSet902 = new DataMap();
        if (gprsFlag) {
            IData elemD = new DataMap();
            ProductTimeEnv env = new ProductTimeEnv();
            elemD.put("PRODUCT_ID", PRODUCT_ID);
            elemD.put("PACKAGE_ID", packageIdForGprs);
            elemD.put("DISCNT_CODE", "902");
            DiscntData discntData902 = new DiscntData(elemD);
            env.setBasicAbsoluteStartDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
            String startDateD = ProductModuleCalDate.calStartDate(discntData902, env);
            discntData902.setStartDate(startDateD);
            String endDateD = ProductModuleCalDate.calEndDate(discntData902, startDateD);
            discntSet902.put("ELEMENT_ID", "902");
            discntSet902.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
            discntSet902.put("END_DATE", endDateD);
            discntSet902.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
            discntSet902.put("START_DATE", startDateD);
            discntSet902.put("PRODUCT_ID", PRODUCT_ID);
            discntSet902.put("PACKAGE_ID", packageIdForGprs);
            discntSet902.put("INST_ID", "");
        }
        // 根据PRODUCT_ID查询预必选优惠
        IDataset discntSets = UPackageElementInfoQry.queryOfferForceElementsByProductId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, PRODUCT_ID, BofConst.ELEMENT_TYPE_CODE_DISCNT);
        for (int i = 0; i < discntSets.size(); i++) {
            // 计算优惠的开始和结束时间（生失效关系）
            IData elem = discntSets.getData(i);
            DiscntData discntData = new DiscntData(elem);
            ProductTimeEnv env = new ProductTimeEnv();
            env.setBasicAbsoluteStartDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
            String startDate = ProductModuleCalDate.calStartDate(discntData, env);
            discntData.setStartDate(startDate);
            String endDate = ProductModuleCalDate.calEndDate(discntData, startDate);
            // 修改优惠的属性
            discntSets.getData(i).put("ELEMENT_ID", serviceSets.getData(i).getString("DISCNT_CODE"));
            discntSets.getData(i).put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
            discntSets.getData(i).put("END_DATE", endDate);
            discntSets.getData(i).put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
            discntSets.getData(i).put("START_DATE", startDate);
            discntSets.getData(i).put("INST_ID", "");
        }
        IDataset eleSets = new DatasetList();
        eleSets.addAll(serviceSets);
        eleSets.addAll(discntSets);
        eleSets.add(discntSet902);
        preOpenParam.put("SELECTED_ELEMENTS", eleSets);
        String flag_4g = "0";
        if ("01".equals(simType)) {
            flag_4g = "1";
        }
        preOpenParam.put("FLAG_4G", flag_4g);
        // 调用预开户接口进行预开户
        IDataset preOpenResult = CSAppCall.call("SS.CreatePersonUserBatchIntfSVC.tradeReg", preOpenParam);
        String trade_id = preOpenResult.getData(0).getString("TRADE_ID");
        String order_id = preOpenResult.getData(0).getString("ORDER_ID");

        // 记录预售信息
        IData preSaleData = new DataMap();
        preSaleData.put("SERIAL_NUMBER", serialNumber);
        preSaleData.put("CUST_NAME", custName);
        preSaleData.put("PSPT_ID", custCertNo);
        preSaleData.put("POST_PHONE", telephone);
        preSaleData.put("IN_MODE", "1");
        preSaleData.put("ORDER_NO", busiSeq);
        preSaleData.put("STATE", "1");
        preSaleData.put("PSPT_TYPE_CODE", "0");
        preSaleData.put("SEND_SMS_FLAG", "0");
        preSaleData.put("CANCEL_FLAG", "0");
        preSaleData.put("ACCEPT_DATE", SysDateMgr.getSysTime());
        preSaleData.put("UPDATA_TIME", SysDateMgr.getSysTime());
        preSaleData.put("END_DATE", SysDateMgr.addDays(25) + " 23:59:59");
        preSaleData.put("UPDATE_STAFF_ID", this.getVisit().getStaffId());
        preSaleData.put("UPDATE_DEPART_ID", this.getVisit().getDepartId());
        preSaleData.put("PRODUCT_INFO", PRODUCT_ID);
        preSaleData.put("RSRT_STR5", trade_id); // 预开户trade_id
        preSaleData.put("RSRT_STR6", order_id); // 预开户order_id
        preSaleData.put("PSPT_ADDR", ""); // 身份证地址
        preSaleData.put("POST_ADDR", ""); // 邮寄地址
        Dao.insert("TD_B_POSTCARD_INFO", preSaleData, Route.getCrmDefaultDb());

        resultItem.put("isSuc", "1");
        String signature = transId + "0000" + "1";
        signature = new Rsa.Encoder(pri).encode(signature);
        resultItem.put("signature", signature);
        returnMap.put("respCode", ComFuncUtil.CODE_RIGHT);
        returnMap.put("respDesc", "success");
        return ComFuncUtil.transOutParam(returnMap);
    }

    /**
     * 3.1.4.8预售卡冲正(reverseSvcnumOrder)
     * 实名认证系统将客户号码给省公司，省公司对号码进行回收释放处理。
     * 湖南参考接口：SS.SellCardOnlineSVC.reverseSvcnumOrder
     */
    public IData C898HQReverseSvcnumOrder(IData inParamStr) throws Exception {
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

        IData data = params.getData("reqInfo");
        IDataset inParamSet2 = new DatasetList();
        IData set2Map1 = new DataMap();
        set2Map1.put("FIELD", "svcNum"); // 开户号码
        set2Map1.put("TYPE", "String");
        inParamSet2.add(set2Map1);
        IData set2Map2 = new DataMap();
        set2Map2.put("FIELD", "cityCode"); // 归属地市编码
        set2Map2.put("TYPE", "String");
        inParamSet2.add(set2Map2);
        IData set2Map3 = new DataMap();
        set2Map3.put("FIELD", "iccid"); // iccid
        set2Map3.put("TYPE", "String");
        inParamSet2.add(set2Map3);
        IData set2Map4 = new DataMap();
        set2Map4.put("FIELD", "amount"); // 金额（分）
        set2Map4.put("TYPE", "String");
        inParamSet2.add(set2Map4);
        IData set2Map6 = new DataMap();
        set2Map6.put("FIELD", "signature"); // 签名
        set2Map6.put("TYPE", "String");
        inParamSet2.add(set2Map6);
        inParamResult = ComFuncUtil.checkInParam(data, inParamSet2);
        if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
            return ComFuncUtil.transOutParam(inParamResult);
        }
        /**********************业务受理*********************/
        String transId = params.getString("transactionID"); //全网操作流水
        IData returnMap = new DataMap();
        IDataset result = new DatasetList();
        returnMap.put("result", result);
        IData resultItem = new DataMap();
        result.add(resultItem);
        String reverseResult = "1";
        resultItem.put("reverseResult", reverseResult);
        resultItem.put("transactionID", transId);
        String serialNumber = data.getString("svcNum");
        String iccid = data.getString("iccid");

        String transactionIdPriKey = "1122334488";
        IDataset pushCardPriKey = CmOnlineUtil.queryPushCardPkey(transactionIdPriKey);
        if (IDataUtil.isEmpty(pushCardPriKey)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到卡数据密钥");
        }
        String pri = pushCardPriKey.getData(0).getString("KEY");

        String transactionIdPubkey = "1122334477";
        IDataset pushCardPubKey = CmOnlineUtil.queryPushCardPkey(transactionIdPubkey);
        if (IDataUtil.isEmpty(pushCardPubKey)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到卡数据公钥");
        }
        String pub = pushCardPubKey.getData(0).getString("KEY");
        iccid = new Rsa.Decoder(pub).decode(iccid);

        //根据手机号查找有效的预售卡记录
        IDataset postCardInfos = qryPreCardRecord(serialNumber);
        if (IDataUtil.isEmpty(postCardInfos)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据手机号未找到有效的预售卡记录");
        }

        // 有则更新TD_B_POSTCARD_INFO表的数据为失效
        try {
            IData reversePostData = new DataMap();
            reversePostData.put("SERIAL_NUMBER", serialNumber);
            CreatePostPersonUserBean CreatePostPersonUserBean = BeanManager.createBean(CreatePostPersonUserBean.class);
            CreatePostPersonUserBean.updateDUfalg(reversePostData);
        } catch (Exception e) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "处理预售数据失败!" + e.getMessage().toString());
        }

        // 调用预开户返销接口返销预开户数据
        String trade_id = postCardInfos.getData(0).getString("RSRT_STR5"); // 查找用户预开户的TRADE_ID
        IData cancelPreOpen = new DataMap();
        cancelPreOpen.put("TRADEID_LIST", trade_id + ",");
        cancelPreOpen.put("ROUTE_EPARCHY_CODE", "0898");
        cancelPreOpen.put("TRADE_EPARCHY_CODE", "0898");
        cancelPreOpen.put("EPARCHY_CODE", "0898");
        cancelPreOpen.put("REMARKS", "预开户返销");
        cancelPreOpen.put("INVOICE_NO", "");
        CSAppCall.call("SS.CancelTradeSVC.cancelTradeReg", cancelPreOpen);

        // 返回
        String signature = transId + "0000" + reverseResult;
        signature = new Rsa.Encoder(pri).encode(signature);
        resultItem.put("signature", signature);
        returnMap.put("respCode", ComFuncUtil.CODE_RIGHT);
        returnMap.put("respDesc", "success");
        return ComFuncUtil.transOutParam(returnMap);
    }

    private IDataset qryPreCardRecord(String serial_number) throws Exception {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT * FROM TD_B_POSTCARD_INFO ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND SERIAL_NUMBER=:SERIAL_NUMBER ");
        parser.addSQL(" AND CANCEL_FLAG='0' ");
        parser.addSQL(" AND STATE='1' ");
        return Dao.qryByParse(parser);
    }

}
