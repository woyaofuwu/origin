package com.asiainfo.veris.crm.iorder.soa.frame.csservice.common.component.login;


import java.util.Map;

import com.ailk.biz.BizVisit;
import com.ailk.biz.util.Encryptor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.cache.memcache.MemCacheFactory;
import com.ailk.cache.memcache.interfaces.IMemCache;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.DataUtils;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.exception.*;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.CreditCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.mvelmisc.MvelMiscQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserClassInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.SmsVerifyCodeBean;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidenetchangeprod.NoPhoneWideChangeProdBean;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.userinfo.QuerySnByUsrpidBean;
import com.asiainfo.veris.crm.order.soa.person.busi.view360.Qry360InfoBean;


public class LoginBean extends CSBizBean {

    public IData getLoginInfo(IData param) throws Exception {
        //根据局方要求，需要记录日志，为了记录日志，强制转型
        BizVisit bizVisit = (BizVisit) param.get("visit");
        if ("true".equals(param.getString("NEED_LOG"))) {
            CSBizBean.getVisit().setSourceName("personlogin.Login");
        }
        CSBizBean.getVisit().setRemoteAddr(bizVisit.getRemoteAddr());
        CSBizBean.getVisit().setLoginMAC(bizVisit.getLoginMAC());
        CSBizBean.getVisit().setInModeCode(bizVisit.getInModeCode());
        CSBizBean.getVisit().setStaffId(bizVisit.getStaffId());
        CSBizBean.getVisit().setDepartId(bizVisit.getDepartId());
        CSBizBean.getVisit().setCityCode(bizVisit.getCityCode());
        CSBizBean.getVisit().setStaffEparchyCode(bizVisit.getStaffEparchyCode());
        IData result = new DataMap();
        this.checkParams(param);
        String accessNum = param.getString("SERIAL_NUMBER");
        String loginTypeCode = param.getString("LOGIN_TYPE_CODE");
        String loginVal = param.getString("LOGIN_VAL");// 密码 或者 验证码信息等等
        String authTrace = param.getString("AUTH_TRACE");
        String loginUserId = param.getString("LOGIN_USER_ID");
        IData userInfo = new DataMap();
        if (DataUtils.isNotEmpty(loginUserId)) {
            userInfo = this.getUserInfoByUserId(loginUserId);
        } else {
            userInfo = this.getUserInfo(accessNum);
        }
        if (DataUtils.isNotEmpty(userInfo)) {
            String custId = userInfo.getString("CUST_ID");
            result.put("REMOVE_TAG", userInfo.getString("REMOVE_TAG"));
            this.getPartyInfo(result, custId);
            this.verifyResult(result, authTrace, loginTypeCode, loginVal, userInfo);
            this.getUserExtendInfo(result, userInfo);
        }
        return result;
    }

    public IData getExtendLoginInfo(IData param) throws Exception {
        String serialNumber = param.getString("SERIAL_NUMBER");
        String userId = param.getString("USER_ID");
        String removeTag = param.getString("REMOVE_TAG");

        return this.getUserFeeInfo(serialNumber, userId, removeTag);
    }

    private IData getUserFeeInfo(String serialNumber, String userId, String removeTag) throws Exception {
        IData result = new DataMap();
        IDataset subList = new DatasetList();
        IData subData = new DataMap();
        IData acct = UcaInfoQry.qryAcctInfoByUserId(userId);
        if (DataUtils.isEmpty(acct)) {
            subData.put("ACCT_error", "无帐户资料！");
            subList.add(subData);
            result.put("SUB_INFO", subList);
            return result;
        }
        try {
            IData feeInfo = feeInfo = this.getFeeInfo(serialNumber, userId);
            subData.put("BALANCE", feeInfo.getString("BALANCE"));
            subData.put("REAL_TIME_FEE", feeInfo.getString("REAL_FEE"));
            subData.put("FLOW", String.format("%.2f", Float.parseFloat(feeInfo.getString("GPRS_BALANCE"))));
        } catch (Exception e) {
            subData.put("BALANCE_error", "调用费用信息接口报错");
        }
        if (StringUtils.equals(removeTag, "0")) {
            try {
                IData creaditInfo = this.getStartLevel(userId);
                subData.put("STAR_LEVEL", creaditInfo.getString("CREDIT_CLASS"));
                subData.put("CREDIT_VALUE", creaditInfo.getString("CREDIT_VALUE") == "" ? "0" : creaditInfo.getString("CREDIT_VALUE"));
            } catch (Exception e) {
                subData.put("BALANCE_error", "调用星级查询接口报错");
            }
            subData.put("ACC_ID", acct.getString("ACCT_ID"));
            subData.put("ACCT_TYPE", StaticUtil.getStaticValue("TD_S_PAYMODE", acct.getString("PAY_MODE_CODE")));
            subList.add(subData);
            result.put("SUB_INFO", subList);
        } else {
            subData.put("ACC_ID", "");
            subData.put("MAIN_OFFER", "");
            subList.add(subData);
            result.put("SUB_DESTORY_INFO", subList);
        }
        return result;
    }

    private void getUserExtendInfo(IData result, IData user) throws Exception {
        String serialNumber = user.getString("SERIAL_NUMBER");
        String userId = user.getString("USER_ID");
        String removeTag = user.getString("REMOVE_TAG");
        IDataset subList = new DatasetList();
        IData subData = new DataMap();
        subData.putAll(user);
        subData.put("SUBSCRIBER_INS_ID", userId);
        subData.put("ACCESS_NUM", serialNumber);
        if (serialNumber.startsWith("h") || serialNumber.startsWith("NT")) {
            subData.put("SERIAL_NUMBER", serialNumber);
            subData.put("ACCESS_NUM", serialNumber);
            subList.add(subData);
            result.put("SUB_INFO", subList);
            return;
        }
        
        // REQ201812100001关于全球通客户标识的需求 wuhao5
        IData inData = new DataMap();
        inData.put("USER_ID", user.getString("USER_ID",""));
        IDataset dataset = UserClassInfoQry.queryUserClass(inData);
		if(IDataUtil.isNotEmpty(dataset) && dataset.size()>0){ 
			IData data = dataset.getData(0);
			String QQTClass = StaticUtil.getStaticValue("USER_INFO_CLASS", data.getString("USER_CLASS"));
			subData.put("QQTClass", QQTClass);
		}else{
			subData.put("QQTClass", "");
		}
		//END REQ201812100001关于全球通客户标识的需求 wuhao5
        
        // 判断是否4G用户
        IDataset resDatas = UserResInfoQry.queryUserResByUserIdResType(userId, "1");
        if (IDataUtil.isNotEmpty(resDatas)) {
            String lteTag = resDatas.getData(0).getString("RSRV_TAG3", "");
            if ("1".equals(lteTag)) {
                subData.put("IS_4GUSER", "Y");
            } else {
                String simCardNo = resDatas.getData(0).getString("RES_CODE");
                IDataset simCardDatas = ResCall.getSimCardInfo("0", simCardNo, "", null);
                if (IDataUtil.isEmpty(simCardDatas)) {
                    subData.put("IS_4GUSER_error", "SIM卡资源为空");
                } else {
                    String cardModeType = simCardDatas.getData(0).getString("CARD_MODE_TYPE");
                    String opc = simCardDatas.getData(0).getString("OPC", "");
                    subData.put("IS_4GUSER", "Y");
                    if ("1".equals(cardModeType) || ((!("2".equals(cardModeType))) && "".equals(opc))) {
                        subData.put("IS_4GUSER", "N");
                    }
                }
            }

        }
        subData.put("REMOVE_TAG", removeTag);
        String prodStatus = "0";
        String prodStaName = "开通";
        IDataset mainSvcState = UserSvcStateInfoQry.getUserMainState(userId);
        if (DataUtils.isEmpty(mainSvcState)) {
        } else {
            prodStatus = mainSvcState.first().getString("STATE_CODE");
            prodStaName = StaticUtil.getStaticValue("USER_STATE_CODESET", prodStatus);
            //判断是否非实名制全停
            if("5".equals(prodStatus)) {
                IData allStopIData = new DataMap();
                allStopIData.put("USER_ID",userId);
                Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
                IDataset allStop =  bean.qryUserIfAllStop(allStopIData);
                if(DataUtils.isNotEmpty(allStop)) {
                    prodStatus  = "AT";
                    prodStaName = "非实名制全停";
                }
            }
        }
        if (StringUtils.equals("0", removeTag)) {
            IData product = UcaInfoQry.qryMainProdInfoByUserId(userId);

            if (DataUtils.isNotEmpty(product)) {
                subData.put("MAIN_OFFER", product.getString("PRODUCT_NAME"));
                subData.put("BRAND_NAME", product.getString("BRAND_NAME"));
                String brandCode = product.getString("BRAND_CODE");
                if (!brandCode.equals("G001") && !brandCode.equals("G002") && !brandCode.equals("G010")) {
                    subData.put("IS_BRANDCODE", "false");
                }
                subData.put("PRODUCT_ID", product.getString("PRODUCT_ID"));
            } else {
                subData.put("IS_BRANDCODE", "false");
                subData.put("MAIN_OFFER_error", "取得用户主产品信息出错！");
            }
            subData.put("PROD_STA", prodStatus);
            subData.put("PROD_STA_NAME", prodStaName);
            IData custDate = UcaInfoQry.qryCustomerInfoByCustId(user.getString("CUST_ID"));
            String psptTypeCode = custDate.getString("PSPT_TYPE_CODE");
            String psptId = custDate.getString("PSPT_ID");
            String custName = custDate.getString("CUST_NAME");
            //是否宽带用户查询
            String kdSerialNumber = serialNumber;
            if (!serialNumber.startsWith("KD")) {
                kdSerialNumber = "KD_" + serialNumber;
            }
            IDataset isWideNetUserIData = WidenetInfoQry.getUserWidenetInfoBySerialNumber(kdSerialNumber);
            if (DataUtils.isNotEmpty(isWideNetUserIData)) {
                subData.put("IS_WIDENET", "TRUE");
            }
            // 判断是否是无线固话用户
            String isTDUser = "18".equals(user.getString("NET_TYPE_CODE")) ? "1" : "0";
            subData.put("IS_TD_USER", isTDUser);
            //是否无手机宽带用户
            IData isNoPhoneParam = new DataMap();
            isNoPhoneParam.put("SERIAL_NUMBER", serialNumber);
            IDataset isNoPhoneUser = NoPhoneWideChangeProdBean.checkIfNoPhoneWideUser(isNoPhoneParam);
            if (DataUtils.isNotEmpty(isNoPhoneUser)) {
                subData.put("IS_NOPHONEUSER", "true");
            }
            //界面互联网新增
            //是否黑名单
//				IData custDate = UcaInfoQry.qryCustomerInfoByCustId(user.getString("CUST_ID"));
//				String psptTypeCode = custDate.getString("PSPT_TYPE_CODE");
//				String psptId = custDate.getString("PSPT_ID");
//				String custName = custDate.getString("CUST_NAME");
//				if(DataUtils.isNotEmpty(custDate)) {
//					String isBackUser = UCustBlackInfoQry.isBlackCust(psptTypeCode,psptId)?"true":"false";
//					subData.put("IS_BLACK_USER", isBackUser);
//				}
            if (DataUtils.isNotEmpty(custDate)) {
                IDataset callSets = null;
                try {
                    callSets = AcctCall.qryBlackListByPsptId(custDate);//调账务接口查黑名单
                    for (int i = 0; i < callSets.size(); i++) {
                        IData blackInfo = callSets.getData(i);
                        String owe_fee = blackInfo.getString("OWE_FEE", "0");
                        if (owe_fee != null && !"0".equals(owe_fee)) {
                            subData.put("IS_BLACK_USER", "true");
                        }
                    }
                } catch (Exception e) {
                    subData.put("IS_BLACK_USER_error", "调用黑名单接口报错！");
                }
            }
            //积分查询
            try {
                IDataset scoreResult = scoreResult = AcctCall.queryUserScore(userId);
                if (DataUtils.isNotEmpty(scoreResult)) {
                    IData scoreInfo = scoreResult.getData(0);
                    subData.put("SUM_SCORE", scoreInfo.getString("SUM_SCORE"));
                }
            } catch (Exception e) {
                subData.put("SUM_SCORE_error", "调用积分接口报错！");
            }

            //近三个月月均
            IData qryMonthFeeParam = new DataMap();
            IData acctInfo = UcaInfoQry.qryAcctInfoByUserId(userId);
            if (IDataUtil.isNotEmpty(acctInfo)) {
//                String endCycleId = SysDateMgr.getLastCycle(); // 获取上月时间，格式: yyyyMM
//                String startCycleId = SysDateMgr.decodeTimestamp(SysDateMgr.getAddMonthsNowday(-3, endCycleId), SysDateMgr.PATTERN_TIME_YYYYMM);
//                qryMonthFeeParam.put("START_CYCLE_ID", startCycleId);
//                qryMonthFeeParam.put("END_CYCLE_ID", endCycleId);
//                qryMonthFeeParam.put("ACCT_ID", acctInfo.getString("ACCT_ID"));
//                try {
//                    IDataOutput output = CSAppCall.callAcct("AM_CRM_QueryMonFee", qryMonthFeeParam, true); // 近三月月均
//                    IDataset monFeeInfo = output.getData();
//                    if (IDataUtil.isNotEmpty(monFeeInfo)) {
//                        long totalFee = monFeeInfo.first().getLong("TOTAL_FEE");
//                        BigDecimal totolBig = new BigDecimal(totalFee + "");
//                        BigDecimal monthBig = new BigDecimal("3.00");
//                        subData.put("AVERAGE_FEE", totolBig.divide(monthBig, 2, BigDecimal.ROUND_HALF_DOWN).toString() + "元");
//                    }
//                } catch (Exception e) {
//                    subData.put("AVERAGE_FEE_error", "调用近三个月月均接口报错");
//                }
                IDataset fees = MvelMiscQry.getUserAvgPayFee(userId,"3");
                if(IDataUtil.isNotEmpty(fees))
                {
                    subData.put("AVERAGE_FEE", fees.getData(0).getString("PARAM_CODE")+"元");
                }
                //新需求需要展示在首页上面的精准营销
                String realAvgStr = MvelMiscQry.getSaleplatInfoBySn(serialNumber,product.getString("PRODUCT_ID"));
                //改变一下样式
                StringBuilder newRealAvgStr = new StringBuilder();
                String tempStr = "";
                if(DataUtils.isNotEmpty(realAvgStr)) {
                    //去掉第一个~字符
                    if(realAvgStr.indexOf("近三个月平均消费")>-1) {
                        realAvgStr = realAvgStr.substring(1,realAvgStr.length());
                        String threeStr = realAvgStr.substring(0,realAvgStr.indexOf("~"));
                        String escapThreeStr = realAvgStr.substring(realAvgStr.indexOf("~")+1,realAvgStr.length());
                        realAvgStr = escapThreeStr+"~"+threeStr;
                    }
                }
                String[] realAvgArr = realAvgStr.split("~");
                for (int i = 0; i < realAvgArr.length; i++) {
                    if(i==realAvgArr.length-1 && realAvgArr[i].toString().indexOf("近三个月平均消费")>-1){
                        tempStr = realAvgArr[i].replaceAll("style=\"font-weight: bold;\"","class=\"value\"");
                    }
                    else {
                        tempStr = realAvgArr[i].replaceAll("style=\"font-weight: bold;\"","class=\"value e_red\"");
                    }
                    newRealAvgStr.append("<li>"+tempStr+"</li>");
                }
                String realAvg = newRealAvgStr.toString().replaceAll("span","div");
                realAvg = realAvg.replaceAll("近三个月平均消费","近三个月月均").replaceAll("推荐语音自选套餐","推荐语音").replaceAll("推荐流量自选套餐","推荐流量");
                subData.put("REAL_AVERAGE_FEE", realAvg);

            }
            //用户费用信息查询
            IData userFeeInfo = new DataMap(getUserFeeInfo(serialNumber, userId, removeTag).getString("SUB_INFO").replaceAll("\\[", "").replaceAll("\\]", ""));
            subData.putAll(userFeeInfo);
            subList.add(subData);
            result.put("SUB_INFO", subList);
        } else {
            subData.put("ACC_ID", "");
            subData.put("MAIN_OFFER", "");
            subData.put("DES_TAG", "1");
            subData.put("PROD_STA_NAME", "已销户");
            IData product = UcaInfoQry.qryMainProdInfoByUserId(userId);
            //品牌
            if(DataUtils.isNotEmpty(product)) {
                subData.put("BRAND_NAME", product.getString("BRAND_NAME"));
            }
            //星级信用度
//            try {
                IData creaditInfo = this.getStartLevelDestroy(user.getString("CUST_ID"));
                subData.put("STAR_LEVEL", creaditInfo.getString("CREDIT_CLASS","无"));
                subData.put("CREDIT_VALUE", creaditInfo.getString("CREDIT_VALUE") == "" ? "无" : creaditInfo.getString("CREDIT_VALUE"));
//                  subData.put("STAR_LEVEL", "0");
//                  subData.put("CREDIT_VALUE", "0");
//            } catch (Exception e) {
////                subData.put("BALANCE_error", "调用星级查询接口报错");
//            }
            //积分查询
            try {
                IDataset scoreResult = scoreResult = AcctCall.queryUserScore(userId);
                if (DataUtils.isNotEmpty(scoreResult)) {
                    IData scoreInfo = scoreResult.getData(0);
                    subData.put("SUM_SCORE", scoreInfo.getString("SUM_SCORE"));
                }
                else {
                    subData.put("SUM_SCORE", "无");
                }
            } catch (Exception e) {
                subData.put("SUM_SCORE_error", "调用积分接口报错！");
            }
            //近三个月月均
            IData qryMonthFeeParam = new DataMap();
            IData acctInfo = UcaInfoQry.qryAcctInfoByUserId(userId);
            if (IDataUtil.isNotEmpty(acctInfo)) {
                IDataset fees = MvelMiscQry.getUserAvgPayFee(userId,"3");
                if(IDataUtil.isNotEmpty(fees))
                {
                    subData.put("AVERAGE_FEE", fees.getData(0).getString("PARAM_CODE")+"元");
                }
                else {
                    subData.put("AVERAGE_FEE", "无");
                }
            }
            //用户费用信息查询
//            IData userFeeInfo = new DataMap();
//            IData userFeeData = getUserFeeInfo(serialNumber, userId, removeTag);
//            if(DataUtils.isNotEmpty(userFeeData)) {
//                String userFee = userFeeData.getString("SUB_INFO").replaceAll("\\[", "").replaceAll("\\]", "");
//                userFeeInfo = new DataMap(userFee);
//            }
//            subData.putAll(userFeeInfo);
            IData feeInfo = AcctCall.getOweFeeByUserId(userId);
            subData.put("BALANCE", feeInfo.getString("ACCT_BALANCE","0"));
            subData.put("REAL_TIME_FEE", feeInfo.getString("REAL_FEE","0"));
            //是否宽带用户查询
            String kdSerialNumber = serialNumber;
            if (!serialNumber.startsWith("KD")) {
                kdSerialNumber = "KD_" + serialNumber;
            }
            IDataset isWideNetUserIData = WidenetInfoQry.getUserWidenetInfoBySerialNumber(kdSerialNumber);
            if (DataUtils.isNotEmpty(isWideNetUserIData)) {
                subData.put("IS_WIDENET", "TRUE");
            }
            subList.add(subData);
            result.put("SUB_DESTORY_INFO", subList);
        }
    }

    /**
     * 校验入参信息
     *
     * @throws Exception
     */
    private void checkParams(IData param) throws Exception {
        String loginTypeCode = param.getString("LOGIN_TYPE_CODE");
        if (StringUtils.isBlank(loginTypeCode)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_1180, "登录方式");
        }
        String accessNum = param.getString("SERIAL_NUMBER");
        if (StringUtils.isBlank(accessNum)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_1180, "登录号码");
        }
    }

    /**
     * 获取参与人信息
     *
     * @param custId
     * @throws Exception
     */
    private void getPartyInfo(IData result, String custId) throws Exception {
        IData data = UcaInfoQry.qryCustomerInfoByCustId(custId);
        if (DataUtils.isEmpty(data)) {
            CSAppException.apperr(CustException.CRM_CUST_111);
        }
        IData partyInfo = new DataMap();
        partyInfo.put("PARTY_ID", data.getString("CUST_ID"));
        partyInfo.put("PARTY_NAME", data.getString("CUST_NAME"));
        IDataset partyList = new DatasetList();
        partyList.add(partyInfo);
        result.put("CB_PARTY", partyList);
        result.put("CUST_ID", data.getString("CUST_ID"));
        result.put("PARTY_ID", data.getString("PARTY_ID"));
    }

    /**
     * 获取登陆用户的信息
     *
     * @return
     * @throws Exception
     */
    private IData getUserInfo(String serialNumber) throws Exception {

        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        // 如果查询不到有效用户，查询销户的用户
        if (userInfo != null) {
            return userInfo;
        }

        IDataset userInfos = BofQuery.getImproperUserInfoBySn(serialNumber, RouteInfoQry.getEparchyCodeBySn(serialNumber));
        if (userInfos == null || userInfos.isEmpty()) {
            CSAppException.apperr(CrmUserException.CRM_USER_112);
        } else {
            userInfo = userInfos.first();
        }
//
        return userInfo;
    }

    /**
     * 多用户刷新
     *
     * @return
     * @throws Exception
     */
    private IData getUserInfoByUserId(String userId) throws Exception {
        IDataset userInfo = UserInfoQry.queryAllUserByUserId(userId);
        if(DataUtils.isNotEmpty(userInfo)) {
            return userInfo.getData(0);
        }
        // 如果多用户基本信息
        return new DataMap();
    }

    private void verifyResult(IData result, String authTrace, String loginTypeCode, String loginVal, IData subscriber) throws Exception {
        // 返回信息：为验证码服务
        IDataset verifyResultList = new DatasetList();
        IData verifyResult = new DataMap();
        verifyResult.put("RESULT_CODE", "0");
        verifyResult.put("RESULT_INFO", "成功");
        // 1密码登录  0
        if (StringUtils.equals("1", loginTypeCode)) {
            this.verifyPwd(authTrace, loginVal, subscriber, verifyResult);
        }
        // 2.验证码
        else if (StringUtils.equals("7", loginTypeCode)) {
            BizVisit visit = (BizVisit) SessionManager.getInstance().getVisit();

            String tradeTypeCode = "-1";
            String smsKey = SmsVerifyCodeBean.VERIFY_CODE_CACHE_KEY + tradeTypeCode + "_" + subscriber.getString("SERIAL_NUMBER");
            IData smsinfo = (IData) SharedCache.get(smsKey);
            String msg = "验证码校验通过!";
            String flag = "0";

            if (smsinfo == null) {
                msg = "验证码校验超时！";
                flag = "1";
            } else if (!StringUtils.equals(smsinfo.getString("VERIFYCODE"), loginVal)) {
                msg = "输入验证码不正确！";
                flag = "1";
            }

            if (StringUtils.equals("0", flag)) {// 验证通过清除验证码
                SharedCache.delete(smsKey);
            }
            verifyResult.put("RESULT_CODE", flag);
            verifyResult.put("RESULT_INFO", msg);
        } else if (StringUtils.equals("6", loginTypeCode)) {
            String smsKey = SmsVerifyCodeBean.VERIFY_CODE_CACHE_KEY + subscriber.getString("SERIAL_NUMBER");
            String verifyCode = (String) SharedCache.get(smsKey);
            String msg = "验证码校验通过!";
            String flag = "0";

            if (verifyCode == null) {
                msg = "验证码校验超时！";
                flag = "1";
            } else if (!StringUtils.equals(verifyCode, loginVal)) {
                msg = "输入验证码不正确！";
                flag = "1";
            }

            if (StringUtils.equals("0", flag)) {// 验证通过清除验证码
                SharedCache.delete(smsKey);
            }
            verifyResult.put("RESULT_CODE", flag);
            verifyResult.put("RESULT_INFO", msg);
        }
        // 3. 证件类型比对(废弃：登录不做证件校验，需要校验的调用认证组件)
        else {
        }
        // 正常认证通过
        if (StringUtils.equals("0", verifyResult.getString("RESULT_CODE")) && !StringUtils.equals("0", verifyResult.getString("AUTH_TRACE"))) {
            verifyResult.put("AUTH_TRACE", "2");
        }
        verifyResultList.add(verifyResult);
        result.put("VERIFY_RESULT", verifyResultList);

        if ("0".equals(verifyResult.getString("RESULT_CODE"))) {
            getCheckUserPasswdResult(subscriber.getString("SERIAL_NUMBER"), CSBizBean.getVisit().getStaffId(), loginTypeCode, true);
        }
    }

    private void verifyPwd(String authTrace, String loginVal, IData subInst, IData retData) throws Exception {
        String subInstId = subInst.getString("USER_ID");
        String accessNum = subInst.getString("SERIAL_NUMBER");
        String subcriberPwd = subInst.getString("USER_PASSWD");
        String enLogPwd = Encryptor.fnEncrypt(loginVal, subInstId.substring(subInstId.length() - 9));
        // 错误阀值
        int pwdLimit = 5;
        int logCount = 0;
        String smsKey = "NGBOSS_AUTH_" + accessNum + "_ERRCOUNT";
        IMemCache memCache = MemCacheFactory.getCache("shc_cache");
        Map<String, Integer> smsinfo = (Map<String, Integer>) memCache.get(smsKey);
        if (smsinfo != null) {
            logCount = smsinfo.get("count");
        }

        // 如果超过次数，直接抛错
        if (logCount >= pwdLimit) {
            if (!this.hasAuthPriv(retData, authTrace)) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "尊敬的客户，您已累计输入错误服务密码达到[" + pwdLimit + "]次，如需通过服务密码办理业务，请2小时后再试。服务密码关系您个人信息安全，请注意保护，谢谢。");
            }
        }

        if (!StringUtils.equals(subcriberPwd, enLogPwd)) {
            if (this.hasAuthPriv(retData, authTrace))
                return;
            updAuthCountToCache(accessNum, retData);

        } else {// 验证通过清除缓存中错误次数
            clearAuthCountInCache(accessNum);
        }
    }

    public void addAuthCountToCache(String accessNum, int count) throws Exception {
        clearAuthCountInCache(accessNum);
        IData smsinfo = new DataMap();
        smsinfo.put("count", count);
        String smsKey = "NGBOSS_AUTH_" + accessNum + "_ERRCOUNT";
        IMemCache memCache = MemCacheFactory.getCache("shc_cache");
        memCache.set(smsKey, smsinfo, 60 * 60 * 2);
    }

    public void updAuthCountToCache(String accessNum, IData retData) throws Exception {
        String smsKey = "NGBOSS_AUTH_" + accessNum + "_ERRCOUNT";
        IMemCache memCache = MemCacheFactory.getCache("shc_cache");
        Map<String, Integer> smsinfo = (Map<String, Integer>) memCache.get(smsKey);
        int count = 0;
        if (smsinfo == null) {
            addAuthCountToCache(accessNum, 1);
        } else {
            count = smsinfo.get("count");
            addAuthCountToCache(accessNum, ++count);
        }

        if (count == 3) {
            retData.put("RESULT_CODE", "1");
            retData.put("RESULT_INFO", "尊敬的客户，您已累计输入错误服务密码3次，错误5次后密码即会锁定，请知悉！");
        } else {
            retData.put("RESULT_CODE", "1");
            retData.put("RESULT_INFO", "用户密码不正确！");
        }
    }

    public void clearAuthCountInCache(String accessNum) throws Exception {
        String smsKey = "NGBOSS_AUTH_" + accessNum + "_ERRCOUNT";
        IMemCache memCache = MemCacheFactory.getCache("shc_cache");
        memCache.delete(smsKey);
    }

    private IData getStartLevel(String subscriberInsId) throws Exception {
        IData retunMap = new DataMap();
        int level = 0;
        int creatditValue = 0;
        IData IData = CreditCall.queryUserCreditInfos(subscriberInsId);
        if (DataUtils.isNotEmpty(IData)) {
            level = Integer.valueOf(IData.getString("CREDIT_CLASS"));
            creatditValue = Integer.parseInt(IData.getString("CREDIT_VALUE", "0")) / 100;
            retunMap.put("CREDIT_CLASS", level);
            retunMap.put("CREDIT_VALUE", creatditValue);
        }
        return retunMap;
    }

    private IData getStartLevelDestroy(String custId) throws Exception {
        IData retunMap = new DataMap();
        IData iData  = new DataMap();
        int level = 0;
        int creatditValue = 0;
        IDataset IDataset = CreditCall.getCreditInfo(custId,"1");
        if(DataUtils.isNotEmpty(IDataset)) {
            iData = IDataset.getData(0);
            level = Integer.valueOf(iData.getString("CREDIT_CLASS"));
            creatditValue = Integer.parseInt(iData.getString("CREDIT_VALUE", "0")) / 100;
            retunMap.put("CREDIT_CLASS", level);
            retunMap.put("CREDIT_VALUE", creatditValue);
        }
        else {
            retunMap.put("CREDIT_CLASS", "无");
            retunMap.put("CREDIT_VALUE", "无");
        }
        return retunMap;
    }

    private IData getFeeInfo(String serialNumber, String userId) throws Exception {
        IData feeInfo = new DataMap();
        feeInfo.put("BALANCE", "0");
        feeInfo.put("REAL_FEE", "0");
        feeInfo.put("FLUX", "0");
        feeInfo.put("GPRS_BALANCE", "0");

        IData IData = new DataMap();
        try {
            IData = AcctCall.getOweFeeByUserId(userId);
            feeInfo.put("BALANCE", IData.get("ACCT_BALANCE"));//实时结余
            feeInfo.put("REAL_FEE", IData.get("REAL_FEE"));//实时话费
            IDataset discntFees = null;//AcctCall.getUserConsumeInfo(serialNumber, userId, SysDateMgr.getSysDate("yyyyMM"),"0");
            float gprsBalance = 0;// 剩余 BALANCE
            if (discntFees != null && discntFees.size() > 0) {
                for (Object obj : discntFees) {
                    IData discntFee = (IData) obj;
                    float balance = Float.valueOf(discntFee.getString("BALANCE"));
                    String itemTypeCode = discntFee.getString("ITEM_TYPE_CODE", "");
                    if ("10".equals(itemTypeCode)) {// 10：查询gprs套餐
//	                        gprsInfos.add(discntFee);
                        gprsBalance += balance;
                    }
                }
            }
            feeInfo.put("GPRS_BALANCE", gprsBalance);

        } catch (Exception e) {

        } finally {

        }

        return feeInfo;
    }

    public IData getRelationInfo(IData param) throws Exception{
        //客户关联查询（改用ajax异步请求）
        IData subData = new DataMap();
        try {
            IData userInfo = this.getUserInfo(param.getString("SERIAL_NUMBER"));
            IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(userInfo.getString("CUST_ID"));
            QuerySnByUsrpidBean bean = BeanManager.createBean(QuerySnByUsrpidBean.class);
            IData relationQry = new DataMap();
            relationQry.put("QUERY_MODE", "7");
            relationQry.put("REMOVE_TAG", userInfo.getString("REMOVE_TAG"));
            relationQry.put("CUST_NAME", custInfo.getString("CUST_NAME"));
//				relationQry.put("PSPT_TYPE_CODE",psptTypeCode);
            relationQry.put("PSPT_ID", custInfo.getString("PSPT_ID"));
//            IDataset relationDataset = bean.querySnByUsrpid(relationQry, new Pagination());
            IDataset relationDataset = new DatasetList();
            subData.put("USER_RELATION", relationDataset);
			IData send = new DataMap();
			send.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
			send.put("TRADEID", SeqMgr.getTradeIdFromDb());
			send.put("USERTYPE", "1");
			send.put("MSGTYPE", "CrmRequestMessage");
			send.put("CITYCODE", param.getString("MAINLOGIN_CITY_CODE","").trim());
			send.put("MAINLOGIN_STAFF_ID", param.getString("MAINLOGIN_STAFF_ID","").trim());
			send.put("MAINLOGIN_DEPART_ID", param.getString("MAINLOGIN_DEPART_ID","").trim());			
			IDataset httpreturn = CSAppCall.call("SS.RealTimeMarketingInfoSVC.newlandcomputerIntf", send);
			if (httpreturn!=null && httpreturn.size()>0)
			{
				IData requesrult = httpreturn.getData(0);
				if ("0".equals(requesrult.getString("ret_code")))
				{
					IDataset param_list = new DatasetList(requesrult.getString("param_list"));
					if (IDataUtil.isNotEmpty(param_list))
					{
						String precom_value = param_list.getData(0).getString("precom_value","").trim();
						if(StringUtils.isNotEmpty(precom_value)){
							String ARPU  = precom_value.split("Ĩ")[0];
							String MOU  = precom_value.split("Ĩ")[1];
							String DOU  = precom_value.split("Ĩ")[2];
							subData.put("ARPU", ARPU);
							subData.put("MOU", MOU);
							subData.put("DOU", DOU);
						}
					}
				}
			}
        } catch (Exception e) {

        }
        return subData;
    }

    /**
     * 针对特殊权限工号的处理
     *
     * @param authTrace
     * @return
     * @throws Exception
     */
    private boolean hasAuthPriv(IData retData, String authTrace) throws Exception {
        if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "SYS009")) {
            retData.put("RESULT_CODE", "0");
            retData.put("RESULT_INFO", "权限认证成功");
            retData.put("AUTH_TRACE", "0");
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        String subInstId = "3109021744000088";
        String loginVal = "052155";
        String enLogPwd = Encryptor.fnEncrypt(loginVal, subInstId.substring(subInstId.length() - 9));
        ;
    }

    public static String getUserPasswdResultCacheKey(String serialNumber, String staffId) throws Exception {
        // 得到缓存key
        StringBuilder cacheKey = new StringBuilder(80);

        cacheKey.append("USER_PASSWD_RESULT").append("_").append(serialNumber).append("_").append(staffId);
        return cacheKey.toString();
    }

    public static IData getCheckUserPasswdResult(String serialNumber, String staffId, String checkMode, boolean flag) throws Exception {
        // 得到缓存key
        String cacheKey = getUserPasswdResultCacheKey(serialNumber, staffId);

        // 从缓存中取数据
        Object cacheObj = null;
        boolean cacheOK = true;

        try {
            cacheObj = SharedCache.get(cacheKey);
        } catch (Exception e) {
            cacheOK = false;
            cacheObj = null;
        }

        IData map = new DataMap();
        if (flag) {
            map = new DataMap();
            map.put("CHECK_MODE", checkMode);
            map.put("STAFF_ID", staffId);
            map.put("SERIAL_NUMBER", serialNumber);

            // 将数据放入缓存
            if (cacheOK == true) {
                SharedCache.set(cacheKey, map, 600);
            }
        } else {
            if (cacheObj != null) {
                map = (IData) cacheObj;
            }

        }

        return map;
    }
}
