
package com.asiainfo.veris.crm.order.soa.script.rule.svcstate;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQueryHelp;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.script.rule.utils.RuleUtils;

public class TBCheckLimitByServiceState extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TBCheckLimitByServiceState.class);

    /**
     * Copyright: Copyright 2014 Asiainfo-Linkage
     * 
     * @Description: 服务状态与业务受理判断 【TradeCheckBefore】
     * @author: xiaocl
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBCheckLimitByServiceState() >>>>>>>>>>>>>>>>>>");

        String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");

        boolean bResult = false;

        // 判断用户资料是否存在
        if (!RuleUtils.existsUserById(databus))
        {
            return bResult;
        }

        String xChoiceTag = databus.getString("X_CHOICE_TAG", "");
        if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))
        {
            IData param = new DataMap();
            int iPrtaTag = 0;
            StringBuilder strbError = new StringBuilder();

            String strIdType = databus.getString("ID_TYPE", "1");
            String iRedUser = databus.getString("REDUSER_TAG", "0");

            /* 开始逻辑规则校验 */
            if ("1".equals(strIdType))
            {
                String strId = databus.getString("ID");
                String strEparchyCode = databus.getString("TRADE_EPARCHY_CODE");

                param.put("USER_ID", strId);
                param.put("RSRV_VALUE_CODE", "PRTA");// 判断用户是否有预约受理业务
                IDataset userOther = Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_VALUECODE", param);
                if (IDataUtil.isNotEmpty(userOther))
                {
                    iPrtaTag = userOther.size();
                }
                if (logger.isDebugEnabled())
                    logger.debug(" >>>>iPrtaTag=" + iPrtaTag + ">>>>>>>>>iRedUser=" + iRedUser + ">>>>>>>>>>>>>>>>>");
                if ("0".equals(iRedUser) && iPrtaTag == 0)
                {
                    param.clear();
                    param.put("USER_ID", strId);
                    param.put("TRADE_TYPE_CODE", strTradeTypeCode);
                    param.put("EPARCHY_CODE", strEparchyCode);
                    IDataset listTmp = Dao.qryByCode("TD_S_SVCSTATE_TRADE_LIMIT", "SEL_EXISTS_BY_USERID", param);
                    if (!listTmp.isEmpty())
                    {
//                        String strSvcstateName = BreQueryHelp.getNameByCode("SvcStateName", new String[]
//                        { (String) listTmp.get(0, "SERVICE_ID"), (String) listTmp.get(0, "STATE_CODE") });
                    	String strSvcstateName = USvcStateInfoQry.getSvcStateNameBySvcIdStateCode((String) listTmp.get(0, "SERVICE_ID"), (String) listTmp.get(0, "STATE_CODE"));
                        
                        strbError.delete(0, strbError.length());
                        strbError.append("业务受理前条件判断-用户状态[").append(strSvcstateName).append("],不能办理该项业务!");
                        //BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751026, strbError.toString());
                        
                        
                        boolean stateFlag = false;
                        if ("60".equals(strTradeTypeCode))
                        {
                            param.clear();
                            param.put("USER_ID", strId);
                            IDataset userInfo2 = Dao.qryByCode("TF_F_USER", "SEL_USERCUST_ID", param);
                            if (userInfo2 != null && userInfo2.size() > 0)
                            {
                                String isrealname = userInfo2.getData(0).getString("IS_REAL_NAME");
                                if (!"1".equals(isrealname))
                                {// 非实名客户
                                    IData stateData = listTmp.getData(0);
                                    if (stateData != null && stateData.size() > 0)
                                    {
                                        String serviceId = stateData.getString("SERVICE_ID", "");
                                        String stateCode = stateData.getString("STATE_CODE", "");
                                        if ("0".equals(serviceId) && ("1".equals(stateCode) || "2".equals(stateCode) || "4".equals(stateCode) ))
                                        {
                                            stateFlag = true; // 报停、挂失状态客户时，不做拦截  chenxy3 20160628 非实名制局方停机不拦截 REQ201607140023 关于非实名用户关停改造需求 JK不拦截，保留4不拦截
                                        } 
                                        /**
                                         * REQ201608260010 关于非实名用户关停改造需求
                                         * 20160912 chenxy3
                                         * 非实名制欠费停机允许变更开机
                                         * */
                                        if(("0".equals(serviceId) || "1002".equals(serviceId)) && "5".equals(stateCode)){
                                        	IData inData=new DataMap();
                                            inData.put("USER_ID",strId); 
                                            IDataset stopUsers = CSAppCall.call("SS.GetUser360ViewSVC.qryUserIfNotRealNameForOpen", inData);
                                            if(stopUsers!=null && stopUsers.size()>0){
                                            String recordcount=stopUsers.getData(0).getString("RECORDCOUNT");
                                            	if(!"0".equals(recordcount)){
                                            		stateFlag = true; 
                                            	}
                                            } 
                                        }
                                        if (logger.isDebugEnabled())
                                            logger.debug(" >>>>stateFlag=" + stateFlag + ">>>>>>>>>>>>>>>>>>>>>>>>>>");
                                    }
                                }
                            }
                        }
                        // 允许非实名制的报停、挂失状态客户登记实名制@modify by wangyf6 20130923--------------------end---------
                        if (stateFlag)
                        {

                        }
                        else
                        {
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751026, strbError.toString());

                        }
                    }
                }
            }
        }
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBCheckLimitByServiceState() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
