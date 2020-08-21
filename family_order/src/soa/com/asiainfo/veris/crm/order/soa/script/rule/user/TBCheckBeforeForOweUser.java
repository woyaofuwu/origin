
package com.asiainfo.veris.crm.order.soa.script.rule.user;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 业务受理前条件判断：用户欠费检查!【TradeCheckBefore】
 * @author: xiaocl 
 */
public class TBCheckBeforeForOweUser extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(TBCheckBeforeForOweUser.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBCheckBeforeForOweUser() >>>>>>>>>>>>>>>>>>");
        String cityCode = databus.getString("CITY_CODE","").trim();
        logger.debug("TBCheckBeforeForOweUserxxxxxxxxxxxxxxxxxxxxx "+36+",cityCode:"+cityCode);
        if(databus.getString("TRADE_TYPE_CODE","").trim().equals("192")&&(cityCode.equals("HNHN")||cityCode.equals("HNSJ"))){//立即销户
        	logger.debug("TBCheckBeforeForOweUserxxxxxxxxxxxxxxxxxxxxx "+37+",cityCode:"+cityCode);
            return false;
        }   
        /* 自定义区域 */
        boolean bResult = false;

        
        int iFee = 0;
        int iPrtaTag = 0;
        int iCount = 0;
        String strJudgeOweTag = "";
        String strTagSet = "";
        String strInModeCode = "";
        String strAcctId = "";
        IData param = new DataMap();
        
        IDataset listTradeType = databus.getDataset("TD_S_TRADETYPE");
        String strId = databus.getString("ID");
        StringBuilder strbError = new StringBuilder();
        if (IDataUtil.isNotEmpty(listTradeType))
        {
            strJudgeOweTag = listTradeType.getData(0).getString("JUDGE_OWE_TAG");
            strTagSet = databus.getDataset("TD_S_TRADETYPE").getData(0).getString("TAG_SET");
        }
        else
        {
            strJudgeOweTag = "0";
            strTagSet = "00000000000000000000";
        }
        int isRedUser = databus.getInt("REDUSER_TAG", 0);// 红名单 1 不是红名单0
        String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        if (databus.containsKey("IN_MODE_CODE"))
        {
            strInModeCode = databus.getString("IN_MODE_CODE");
        }
        if (databus.containsKey("FEE"))
        {
            iFee = databus.getInt("FEE");
        }
        else
            iFee = databus.getInt("LEAVE_REAL_FEE");

        // 判断是否欠费（托收是否判欠费）
        param.put("USER_ID", strId);
        param.put("RSRV_VALUE_CODE", "PRTA");// 判断用户是否有预约受理业务
        IDataset userOther = Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_VALUECODE", param);
        if (IDataUtil.isNotEmpty(userOther))
        {
            iPrtaTag = userOther.size();
        }

        if (isRedUser == 0 && iFee < 0)
        {
            if (strTagSet.substring(7, 8).equals("1")
                    && (!strInModeCode.equals("0") && !strInModeCode.equals("4") && !strInModeCode.equals("6") && !strInModeCode.equals("8") && !strInModeCode.equals("9") && !strInModeCode.equals("E") && !strTradeTypeCode.equals("62")))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751002, "接口业务受理前条件判断：用户已经欠费不能办理业务！");
            }
            else if (strTagSet.substring(7, 8).equals("2") && strInModeCode.equals("6")) // 调用interBoss流程
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751003, "接口业务受理前条件判断：用户已经欠费不能办理业务！");
            }
            else if (strTagSet.substring(7, 8).equals("3") && strInModeCode.equals("0")) // 初营业前台外其余接入途径
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751004, "接口业务受理前条件判断：用户已经欠费不能办理业务！");
            }
        }

        if (isRedUser == 0 && iFee < 0 && iPrtaTag == 0)
        {
            
            {
                if (strJudgeOweTag.equals("1")) // 1-任何情况都判欠费
                {
                	/**
                	 * REQ201511110016 关于学护卡业务统一付费功能的优化需求
                	 * 特殊权限的用户可以跳过欠费。
                	 * chenxy3 20151118
                	 * */
                	String tradeTypeCode=databus.getString("TRADE_TYPE_CODE");
                	boolean FAMILY_SP = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "FAMILY_SP");
                	if("325".equals(tradeTypeCode) && FAMILY_SP ){
                		
                	}else{
                		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751005, "业务受理前条件判断：用户已经欠费不能办理业务！");
                	}
                }
                else if (strJudgeOweTag.equals("2")) // 2-判欠费并提示是否继续办理
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_CHIOCE, -1, "业务受理前条件判断:用户已经欠费，建议终止业务的办理！<br/>是否要继续业务的办理？选择【是】继续办理业务，选择【否】终止办理业务。");
                }
                else if (strJudgeOweTag.equals("3") || // 3-仅托收不判
                        strJudgeOweTag.equals("4") || // 4-仅托收不判并提示
                        strJudgeOweTag.equals("5") || // 5-仅托收不判（但独立帐户托收欠费判）
                        strJudgeOweTag.equals("6") || // 6-托收不判往月欠费
                        strJudgeOweTag.equals("7") || // 无条件判断往月欠费
                        strJudgeOweTag.equals("8") || // 非托收用户有往月欠费不允许办理，有实时欠费给予提示
                        strJudgeOweTag.equals("9")) // judgeOweTag =="8"时的第二次判断，判是否有实时欠费
                {
                    if (databus.getString("ID_TYPE").equals("1"))// 传入的是用户
                    {
                        int iCountUserPayRelationList = 0;
                        // 先获取默认帐户标识
                        param.clear();
                        param.put("USER_ID", strId);
                        IDataset userPayRelation = Dao.qryByCode("TF_A_PAYRELATION", "SEL_BY_USER", param);

                        if (IDataUtil.isNotEmpty(userPayRelation))
                        {
                            iCountUserPayRelationList = userPayRelation.size();
                        }

                        if (iCountUserPayRelationList == 0)
                        {
                            if (strTradeTypeCode.equals("310") || strTradeTypeCode.equals("191") || strTradeTypeCode.equals("7302") || strTradeTypeCode.equals("290"))// 复机、预约正式销户、缴费复机,押金清退
                            {
                                param.clear();
                                param.put("USER_ID", strId);
                                param.put("DEFAULT_TAG", "1");
                                userPayRelation = Dao.qryByCode("TF_A_PAYRELATION", "SEL_BY_USER", param);
                                if (IDataUtil.isNotEmpty(userPayRelation))
                                {
                                    iCountUserPayRelationList = userPayRelation.size();
                                }
                                iCountUserPayRelationList = userPayRelation.size();
                            }
                        }

                        if (iCountUserPayRelationList == 0 && ( !strTradeTypeCode.equals("310") && !strTradeTypeCode.equals("3813")))
                        {
                            strbError.delete(0, strbError.length());
                            strbError.append("业务受理前条件判断:未找到用户(").append(strId).append("的当前默认帐户");
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751026, strbError.toString());
                        }
                        if (iCountUserPayRelationList > 1)
                        {
                            strbError.delete(0, strbError.length());
                            strbError.append("业务受理前条件判断:用户(").append(strId).append(")的付费关系资料错误!");
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751026, strbError.toString());
                        }
                        if(iCountUserPayRelationList == 0){
                        	strAcctId = databus.getDataset("TF_F_ACCOUNT").first().getString("ACCT_ID");
                        }else{
                        	strAcctId = ((IData) userPayRelation.get(0)).getString("ACCT_ID");
                        }
                        
                    }
                    databus.put("ACCT_ID", strAcctId);
                    if (databus.getString("ID_TYPE").equals("2")) // 传入的是帐户
                    {
                        strAcctId = strId;
                    }

                    IDataset userAccountList = databus.getDataset("TF_F_ACCOUNT");
                    if (IDataUtil.isNotEmpty(userAccountList))
                    {
                        iCount = userAccountList.size();
                    }
                    
                    
                    if (iCount == 0)
                    {
                        strbError.delete(0, strbError.length());
                        strbError.append("业务受理前条件判断:帐户(").append(strAcctId).append(")不存在!");
                    }
                    else
                    {
                        String strPayModeCode = databus.getDataset("TF_F_ACCOUNT").getData(0).getString("PAY_MODE_CODE");
                        if (strPayModeCode.equals("1") && strJudgeOweTag.equals("5"))
                        {
                            IDataset vPayRelation = null;
                            param.clear();
                            param.put("ACCT_ID", strAcctId);
                            param.put("DEFAULT_TAG", "1");
                            param.put("ACT_TAG", "1");
                            vPayRelation = Dao.qryByCode("TF_A_PAYRELATION", "SEL_BY_ACCT_DA", param);
                            if (IDataUtil.isNotEmpty(vPayRelation))
                            {
                                iCount = vPayRelation.size();
                            }

                            if (iCount > 0)
                            {
                                IData tUser = null;
                                for (int iIndex = 0; iIndex < iCount; iIndex++)
                                {

                                    param.clear();
                                    param.put("USER_ID", vPayRelation.get(iIndex, "USER_ID"));
                                    tUser = UcaInfoQry.qryUserInfoByUserId(param.getString("USER_ID"));
                                    if (IDataUtil.isNotEmpty(tUser))
                                    {
                                        if (!((String) tUser.getString("REMOVE_TAG")).equals("0"))
                                        {
                                            iCount--;
                                        }
                                    }

                                }

                                if (iCount == 1)
                                {
                                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751006, "业务受理前条件判断：该用户为托收用户的最后一个，已经欠费，请缴费后办理业务！");

                                }
                            }
                        }
                        if (!strPayModeCode.equals("1") && (strJudgeOweTag.equals("3") || strJudgeOweTag.equals("5"))) // 用户不是托收，终止业务
                        {
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751006, "业务受理前条件判断：用户已经欠费不能办理业务！");
                        }

                        if (!strPayModeCode.equals("1") && strJudgeOweTag.equals("8")) // 用户不是托收，并且有往月欠费，终止业务
                        {
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751008, "业务受理前条件判断：用户有往月欠费，请先结清往月欠费后再办理业务！");
                        }

                        if (!strPayModeCode.equals("1") && strJudgeOweTag.equals("9")) // 用户不是托收，并且有实时欠费，提示是否继续办理
                        {
                            databus.put("X_CHECK_TAG", 1);
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_CHIOCE, 751008, "业务受理前条件判断:用户当前存在实时欠费，建议终止业务的办理！<br/>是否要继续业务的办理？选择【是】继续办理业务，选择【否】终止办理业务。");
                        }

                        if (!strPayModeCode.equals("1") && strJudgeOweTag.equals("4")) // 用户不是托收，提示欠费
                        {
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_CHIOCE, 751008, "业务受理前条件判断:用户已经欠费，建议终止业务的办理！<br/>是否要继续业务的办理？选择【是】继续办理业务，选择【否】终止办理业务。");
                            databus.put("X_CHECK_TAG", 1);
                        }

                        if (!strPayModeCode.equals("1") && strJudgeOweTag.equals("6"))
                        {
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751008, "业务受理前条件判断:用户有往月欠费不能办理业务！");
                        }
                        if (strJudgeOweTag.equals("7")) // 无条件判断往月欠费
                        {
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751009, "业务受理前条件判断:用户有往月欠费不能办理业务！");
                        }
                    }
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBCheckBeforeForOweUser() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }
}
