
package com.asiainfo.veris.crm.order.soa.group.largessfluxmgrbean.action;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

public class TradeFinishGrpMebActiveAction implements ITradeFinishAction
{
    private final static Logger logger = Logger.getLogger(TradeFinishGrpMebActiveAction.class);

    
    public void executeAction(IData mainTrade) throws Exception
    {

        String userId = "";
        String serialNumber = "";
        userId = mainTrade.getString("USER_ID");//手机号码的user_id
        serialNumber = mainTrade.getString("SERIAL_NUMBER");//个人号码
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
        
        
        if(logger.isDebugEnabled())
        {
            logger.debug("<<<<<<<===mainTrade参数===>>>>>>>>>" + mainTrade);
        }
        
        if(("3174".equals(tradeTypeCode) || "3618".equals(tradeTypeCode) 
                || "3629".equals(tradeTypeCode)) && StringUtils.isNotBlank(userId))
        {
           
            IDataset productInfos = UserProductInfoQry.queryMainProduct(userId);
            
            if(IDataUtil.isNotEmpty(productInfos))
            {
                String productId = productInfos.getData(0).getString("PRODUCT_ID","");
                
                //用户主产品id是10000765的给做开机操作
                IDataset commInfos = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", 
                        "7358", "VOCATION_CARD",productId, "0898");
                if(IDataUtil.isNotEmpty(commInfos))
                {
                    IDataset custInfos = CustomerInfoQry.getNormalCustInfoBySN(serialNumber);
                    //IData queryData = UcaInfoQry.qryCustomerInfoByCustId(custId);
                    
                    if(IDataUtil.isNotEmpty(custInfos))
                    {
                        String isRealName = custInfos.getData(0).getString("IS_REAL_NAME","");
                        if(StringUtils.equals("1", isRealName))
                        {//实名制了
                            
                            IDataset userInfos = UserInfoQry.getUserInfoBySerailNumber("0",serialNumber);
                            if(IDataUtil.isNotEmpty(userInfos))
                            {
                                
                                IData userInfo = userInfos.getData(0);
                                
                                if(IDataUtil.isNotEmpty(userInfo))
                                {
                                    
                                    String acctTag = userInfo.getString("ACCT_TAG","");
                                    if(StringUtils.equals("2", acctTag))
                                    {//未激活状态
                                        
                                        String eparchyCode = mainTrade.getString("EPARCHY_CODE");
                                        String tradeDepartId = mainTrade.getString("TRADE_DEPART_ID");
                                        String tradeStaffId = mainTrade.getString("TRADE_STAFF_ID");
                                        String inModeCode = mainTrade.getString("IN_MODE_CODE");
                                        String remark = "成员产品订购(" + tradeTypeCode + ")时的开机!";
                                        
                                        IData param = new DataMap();
                                        param.put("V_USER_ID", userId);
                                        param.put("V_REMARK", remark);
                                        param.put("V_TRADE_DEPART_ID", tradeDepartId);
                                        param.put("V_TRADE_STAFF_ID", tradeStaffId);
                                        param.put("V_IN_MODE_CODE", inModeCode);
                                        param.put("V_RESULTCODE", "");
                                        param.put("V_RESULTERRINFO", "");
                                        String[] inParam =
                                            { "V_USER_ID", "V_REMARK", "V_TRADE_DEPART_ID", 
                                              "V_TRADE_STAFF_ID","V_IN_MODE_CODE","V_RESULTCODE", "V_RESULTERRINFO" };
                                        Dao.callProc("P_CSM_OPENMOBILE_GRPMEB", inParam, param, eparchyCode);
                                        
                                        String resultCode = param.getString("V_RESULTCODE", "");
                                        if(logger.isDebugEnabled())
                                        {
                                            logger.debug("<<<<<<<===存储过程param参数===>>>>>>>>>" + param);
                                        }
                                        
                                        if (!"0".equals(resultCode))
                                        {
                                            String resultInfo = param.getString("V_RESULTERRINFO");

                                            CSAppException.apperr(VpmnUserException.VPMN_USER_228, resultCode, resultInfo);
                                        }
                                        
                                        //修改待激活状态后，同步给账务
                                        syncTib(tradeTypeCode, userId, remark);
                                    }
                                }
                            } 
                        }
                    }
                    
                }
                
            }
        }
    }
    
    /**
     * 同步资料
     * @param tradeTypeCode
     * @param userId
     * @param remark
     * @throws Exception
     */
    private void syncTib(String tradeTypeCode, String userId, String remark) throws Exception
    {
        
        IDataset commInfos = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", 
                "7356", "VOCATION_GRPMEB",tradeTypeCode, "0898");
        
        if(IDataUtil.isNotEmpty(commInfos) && StringUtils.isNotBlank(userId))
        {
            //同步用户资料表信息
            String ivSyncSequence = "";
            ivSyncSequence = SeqMgr.getSyncIncreId();
            
            IData synchInfoData = new DataMap();
            synchInfoData.put("SYNC_SEQUENCE", ivSyncSequence);
            String syncDay = StrUtil.getAcceptDayById(ivSyncSequence);
            synchInfoData.put("SYNC_DAY", syncDay);
            synchInfoData.put("SYNC_TYPE", "0");
            synchInfoData.put("TRADE_ID", "0");
            synchInfoData.put("STATE", "0");
            synchInfoData.put("SYNC_TIME", SysDateMgr.getSysTime());
            synchInfoData.put("UPDATE_TIME", SysDateMgr.getSysTime());
            synchInfoData.put("REMARK", remark);
            
            Dao.insert("TI_B_SYNCHINFO", synchInfoData);
            
            IData param = new DataMap();
            param.put("SYNC_SEQUENCE", ivSyncSequence);
            param.put("USER_ID", userId);
            Dao.executeUpdateByCodeCode("TI_B_USER", "INS_TI_USER", param);
            
        }
        
    }
}
