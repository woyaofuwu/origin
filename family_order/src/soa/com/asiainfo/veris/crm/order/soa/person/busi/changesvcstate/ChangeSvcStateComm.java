
package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.AcctDayDateUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TradeSvcStateParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: ChangeSvcStateComm.java
 * @Description: 服务状态变更公用方法类
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-3-7 下午2:15:42
 */

public class ChangeSvcStateComm
{

    private static transient final Logger logger = Logger.getLogger(ChangeSvcStateComm.class);

    /**
     * 清理服务状态表中扩展字段的一些标记
     * 
     * @throws Exception
     */
    public void clearRsrvSpecTagInfo(BusiTradeData btd) throws Exception
    {
        String userId = btd.getRD().getUca().getUserId();
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        IDataset speInfos = UserSvcStateInfoQry.querySpecTagInfos(userId);
        if (IDataUtil.isNotEmpty(speInfos))
        {
            List<SvcStateTradeData> tradesSvcDataList = btd.getTradeDatas(TradeTableEnum.TRADE_SVCSTATE);
            for (int i = 0, sizeSpeInfos = speInfos.size(); i < sizeSpeInfos; i++)
            {
                boolean flag = false;
                IData tempData = speInfos.getData(i);
                String serviceId = tempData.getString("SERVICE_ID");
                String stateCode = tempData.getString("STATE_CODE");
                String startDate = tempData.getString("START_DATE");
                if (tradesSvcDataList != null)// 先看下前面是不是有对该订单做处理了的
                {
                    for (int j = 0, sizeTradesSvcDataList = tradesSvcDataList.size(); j < sizeTradesSvcDataList; j++)
                    {
                        SvcStateTradeData tempSvcStateData = tradesSvcDataList.get(j);
                        String tempUserId = tempSvcStateData.getUserId();
                        String tempServiceId = tempSvcStateData.getServiceId();
                        String tempStateCode = tempSvcStateData.getStateCode();
                        String tempStartDate = tempSvcStateData.getStartDate();
                        if (StringUtils.equals(tempUserId, userId) && StringUtils.equals(serviceId, tempServiceId) && StringUtils.equals(stateCode, tempStateCode) && StringUtils.equals(startDate, tempStartDate))
                        {
                            tempSvcStateData.setRsrvStr1("");
                            tempSvcStateData.setRsrvTag1("");
                            tempSvcStateData.setRemark("报开清报停和挂失的标志位");
                            flag = true;
                            break;
                        }
                    }
                }
                if (!flag)
                {
                    SvcStateTradeData svcStateData = new SvcStateTradeData(tempData);
                    svcStateData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                    svcStateData.setRsrvStr1("");
                    svcStateData.setRsrvTag1("");
                    svcStateData.setRemark("报开清报停和挂失的标志位");
                    btd.add(serialNumber, svcStateData);
                }
            }
        }
    }

    // 生成用户服务状态订单
    public void getSvcStateChangeTrade(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        String tradeTypeCode = btd.getTradeTypeCode();
        UcaData ucaData = btd.getRD().getUca();
        UserTradeData userTD = ucaData.getUser();
        String userId = userTD.getUserId();
        String serialNumber = userTD.getSerialNumber();

        IDataset svcStateParaBuf = new DatasetList();
        String strExecTime = btd.getRD().getAcceptTime();
        
        // 获取用户服务状态
        List<SvcStateTradeData> userSvcStateList = ucaData.getUserSvcsState();// 从uca取提高效率
        if (userSvcStateList == null || userSvcStateList.size() == 0)
        {
            String str = "没有获取到原用户为:" + userId + "服务状态资料";
            CSAppException.apperr(CrmUserException.CRM_USER_783, str);
        }

        // 获取服务状态变更业务参数
        svcStateParaBuf = TradeSvcStateParamInfoQry.querySvcStateParamByKey(tradeTypeCode, ucaData.getBrandCode(), ucaData.getProductId(), userTD.getEparchyCode());
        if (IDataUtil.isEmpty(svcStateParaBuf))
        {
            return;
        }
        if (logger.isDebugEnabled())
        {
            logger.debug("服务状态变更参数为：" + svcStateParaBuf.toString());
            logger.debug("用户服务状态为：" + userSvcStateList.toString());
        }

        List<SvcTradeData> userSvcList = ucaData.getUserSvcs();
        this.addSmsSvc(tradeTypeCode, ucaData, userSvcList);//701,711没有短信服务，增加短信服务
        this.addSmsSvcState(ucaData, userSvcStateList,svcStateParaBuf);//增加短信服务状态。老数据存在很多存在有短信的服务，但是不存在短信的服务状态，导致无法开关短信状态
        
        boolean isBGFlag = false;// 是否存在报停和挂失的数据
        String BGSvcState = "";
        // 对报停和挂失后，状态变更为高额停机，如果这个时候缴费，那么状态不变为开机，而为报停或者挂失！
        if (StringUtils.equals("7301", tradeTypeCode) // 缴费开机
                || StringUtils.equals("7303", tradeTypeCode))// 高额开机
        {
            // 这里要取的是用户的服务状态表，看有没有MAIN_TAG='1' AND RSRV_STR1='BTorGS' AND RSRV_TAG1='1' 的记录。
            IDataset specTagInfos = UserSvcStateInfoQry.querySpecTagInfos(userId);
            if (IDataUtil.isNotEmpty(specTagInfos))
            {
                BGSvcState = specTagInfos.get(0, "STATE_CODE").toString();
                isBGFlag = true;
            }
        }

        int sizeSvcStateList = userSvcStateList.size(); // 外面循环，UCA里面的SvcStateList会有变化
        // 本次变化的服务列表
        for (int i = 0, sizeParaBuf = svcStateParaBuf.size(); i < sizeParaBuf; i++)
        {
            IData paramSvcState = svcStateParaBuf.getData(i);
            String paramServiceId = paramSvcState.getString("SERVICE_ID");
            String paramOldStateCode = paramSvcState.getString("OLD_STATE_CODE");
            String paramNewStateCode = paramSvcState.getString("NEW_STATE_CODE");
            if (!StringUtils.equals(paramOldStateCode, "%"))
            {
                for (int j = 0; j < sizeSvcStateList; j++)
                {
                    SvcStateTradeData userSvcStateJ = userSvcStateList.get(j);
                    if (StringUtils.equals(userSvcStateJ.getServiceId(), paramServiceId))
                    {
                        String svcStateGroups = "";
                        if (!StringUtils.equals("Y", userSvcStateJ.getStateCode()))
                        {
                            for (int k = 0; k < sizeSvcStateList; k++)
                            {
                                SvcStateTradeData userSvcStateK = userSvcStateList.get(k);
                                if (StringUtils.equals(paramServiceId, userSvcStateK.getServiceId()))
                                {
                                    String svcStateCode = userSvcStateK.getStateCode();
                                    if (StringUtils.equals("Y", svcStateCode))
                                    {
                                        svcStateGroups = svcStateGroups + svcStateCode;
                                    }
                                    else
                                    {
                                        svcStateGroups = svcStateCode + svcStateGroups;
                                    }
                                }
                            }
                        }
                        else
                        {
                            int svcStateCount = 0;
                            for (int k = 0; k < sizeSvcStateList; k++)
                            {
                                if (StringUtils.equals(paramServiceId, userSvcStateList.get(k).getServiceId()))
                                {
                                    svcStateCount++;
                                }
                            }

                            if (svcStateCount == 1)
                            {
                                svcStateGroups = "Y";
                            }
                            else
                            {
                                svcStateGroups = "%";
                            }
                        }

                        if (StringUtils.equals(svcStateGroups, paramOldStateCode))
                        {
                            for (int k = 0; k < sizeSvcStateList; k++)
                            {
                                SvcStateTradeData tempSvcStateTradeData = userSvcStateList.get(k);
                                if (!StringUtils.equals(tempSvcStateTradeData.getServiceId(), paramServiceId))
                                {
                                    continue;
                                }
                                boolean notRepeat = true;
                                String oldSvsState = tempSvcStateTradeData.getStateCode();
                                for (int m = 0, mCount = paramNewStateCode.length(); m < mCount; m++)
                                {
                                    String newSvsState = StringUtils.substring(paramNewStateCode, m, m + 1);
                                    if (StringUtils.equals(newSvsState, oldSvsState))
                                    {
                                        notRepeat = false;
                                        break;
                                    }
                                }
                                if (notRepeat)
                                {
                                    if (logger.isDebugEnabled())
                                    {
                                        logger.debug("需要终止的服务为[" + paramServiceId + "],状态为：" + oldSvsState);
                                    }
                                    // 终止老状态中不等于新状态的记录
                                    SvcStateTradeData delStateTrade = tempSvcStateTradeData.clone();
                                    delStateTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
                                    delStateTrade.setEndDate(strExecTime);
                                    // add by zhangxing for HNYD-REQ-20110517-011关于完善欠费停机状态下用户使用业务的需
                                    String strAcceptDay = SysDateMgr.decodeTimestamp(strExecTime,SysDateMgr.PATTERN_STAND_YYYYMMDD);
                                    String firstDayThisAcct = AcctDayDateUtil.getFirstDayThisAcct(userId);
                                    String strFirstDayThisAcct = SysDateMgr.decodeTimestamp(firstDayThisAcct,SysDateMgr.PATTERN_STAND_YYYYMMDD);
                                    
                                    if (StringUtils.equals("7220", tradeTypeCode)
                                            && StringUtils.equals(strAcceptDay, strFirstDayThisAcct))
                                    {
                                        //账期第一天的欠费停机工单，提前到上账期最后一天生效
                                        String tempEndDate = SysDateMgr.getLastSecond(strFirstDayThisAcct);
                                        tempEndDate = SysDateMgr.getLastSecond(tempEndDate);
                                        delStateTrade.setEndDate(tempEndDate);
                                    }
                                    btd.add(serialNumber, delStateTrade);// 加入btd
                                }
                            }

                            if (!StringUtils.equals(paramNewStateCode, "%"))
                            {
                            	
                            	
                                String strAcceptDay = SysDateMgr.decodeTimestamp(strExecTime,SysDateMgr.PATTERN_STAND_YYYYMMDD);
                                String firstDayThisAcct = AcctDayDateUtil.getFirstDayThisAcct(userId);
                                String strFirstDayThisAcct = SysDateMgr.decodeTimestamp(firstDayThisAcct,SysDateMgr.PATTERN_STAND_YYYYMMDD);
                                //newbilling修改 特殊处理的情况，不修改服务状态生效时间	
                                String startDate = SysDateMgr.getNextSecond(strExecTime);
                                if (StringUtils.equals("7220", tradeTypeCode)
                                        && StringUtils.equals(strAcceptDay, strFirstDayThisAcct))
                                {
                                    //账期第一天的欠费停机工单，提前到上账期最后一天生效
                                	startDate = strExecTime;
                                }
                                
                                for (int k = 0, kCount = paramNewStateCode.length(); k < kCount; k++)
                                {
                                    boolean notRepeat = true;
                                    String newSvsState = StringUtils.substring(paramNewStateCode, k, k + 1);
                                    for (int m = 0, mCount = paramOldStateCode.length(); m < mCount; m++)
                                    {
                                        String oldSvsState = StringUtils.substring(paramOldStateCode, m, m + 1);
                                        if (StringUtils.equals(newSvsState, oldSvsState))
                                        {
                                            notRepeat = false;
                                            break;
                                        }
                                    }
                                    if (notRepeat)
                                    {
                                        String mainTag = userSvcStateJ.getMainTag();
                                        SvcStateTradeData addStateTrade = new SvcStateTradeData();
                                        addStateTrade.setUserId(userId);
                                        addStateTrade.setServiceId(paramServiceId);
                                        addStateTrade.setStateCode(newSvsState);
                                        addStateTrade.setMainTag(mainTag);
                                        
                                        addStateTrade.setStartDate(startDate);//newbiling修改生效时间
                                        addStateTrade.setEndDate(SysDateMgr.END_DATE_FOREVER);
                                        addStateTrade.setInstId(SeqMgr.getInstId());
                                        addStateTrade.setModifyTag(BofConst.MODIFY_TAG_ADD);
                                        if (StringUtils.equals("1", mainTag))
                                        {
                                            if (StringUtils.equals("131", tradeTypeCode) || StringUtils.equals("132", tradeTypeCode))
                                            {
                                                addStateTrade.setRsrvStr1("BTorGS");
                                                addStateTrade.setRsrvTag1("1");
                                            }
                                            if (isBGFlag)
                                            {
                                                addStateTrade.setStateCode(BGSvcState);
                                                addStateTrade.setRemark("报停或挂失后欠费预销号后缴费开机或复机");
                                            }
                                        }
                                        // add by zhangxing for HNYD-REQ-20110517-011关于完善欠费停机状态下用户使用业务的需
//                                        String strAcceptDay = SysDateMgr.decodeTimestamp(strExecTime,SysDateMgr.PATTERN_STAND_YYYYMMDD);
//                                        String firstDayThisAcct = AcctDayDateUtil.getFirstDayThisAcct(userId);
//                                        String strFirstDayThisAcct = SysDateMgr.decodeTimestamp(firstDayThisAcct,SysDateMgr.PATTERN_STAND_YYYYMMDD);
                                        if (StringUtils.equals("7220", tradeTypeCode)
                                                && StringUtils.equals(strAcceptDay, strFirstDayThisAcct))
                                        {
                                            //账期第一天的欠费停机工单，提前到上账期最后一天生效
                                            String tempDate = SysDateMgr.getLastSecond(strFirstDayThisAcct);
                                            addStateTrade.setStartDate(tempDate);
                                        }
                                        btd.add(serialNumber, addStateTrade);// 加入btd
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else
            // 原状态为空时仅增加新状态
            {
                if (!StringUtils.equals(paramNewStateCode, "%"))// 新状态不为'%'才插
                {
                    SvcStateTradeData addStateTrade = new SvcStateTradeData();
                    addStateTrade.setUserId(userId);
                    addStateTrade.setServiceId(paramServiceId);
                    addStateTrade.setStateCode(paramNewStateCode);
                    addStateTrade.setMainTag("1");
                    addStateTrade.setStartDate(strExecTime);
                    addStateTrade.setEndDate(SysDateMgr.END_DATE_FOREVER);
                    addStateTrade.setInstId(SeqMgr.getInstId());
                    addStateTrade.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    if ((StringUtils.equals("131", tradeTypeCode) || StringUtils.equals("132", tradeTypeCode)))
                    {
                        addStateTrade.setRsrvStr1("BTorGS");
                        addStateTrade.setRsrvTag1("1");
                    }
                    btd.add(serialNumber, addStateTrade);// 加入btd
                }
            }
        }
    }

    // 查询是否已经存在了用户订单数据
    private UserTradeData getUserTradeData(BusiTradeData btd, String userId) throws Exception
    {
        List<UserTradeData> tradeUserDataList = btd.getTradeDatas(TradeTableEnum.TRADE_USER);
        if (tradeUserDataList.size() > 0)
        {
            for (int i = 0, size = tradeUserDataList.size(); i < size; i++)
            {
                if (StringUtils.equals(userId, tradeUserDataList.get(i).getUserId()))
                {
                    return tradeUserDataList.get(i);
                }
            }
        }
        UserTradeData userData = btd.getRD().getUca().getUser().clone();
        btd.add(btd.getRD().getUca().getSerialNumber(), userData);// 加入busiTradeData中
        return userData;
    }

    // 生成宽带用户服务状态订单（宽带报开时，有预约报停会进行当下处理）
    public void getWidenetSvcStateChangeTrade(BusiTradeData btd) throws Exception
    {
        List<SvcStateTradeData> tradesSvcDataList = btd.getTradeDatas(TradeTableEnum.TRADE_SVCSTATE);
        if (tradesSvcDataList.size() > 0)
        {
            return;
        }
        else
        {
        	// 获取用户服务状态
        	List<SvcStateTradeData> userSvcStateList = btd.getRD().getUca().getUserSvcsState();// 从uca取提高效率
        	if("7220".equals(btd.getRD().getOrderTypeCode()) && "683".equals(btd.getTradeTypeCode())){
        		//手机欠费停机连带无手机宽带停机特殊处理 下月停机
        		for (int j = 0, sizeSvcStateList = userSvcStateList.size(); j < sizeSvcStateList; j++)
                {
                    SvcStateTradeData userSvcTradeData = userSvcStateList.get(j);
                    String userSvcState = userSvcTradeData.getStateCode();
                    if ("1".equals(userSvcState) || "5".equals(userSvcState))
                    {
                        userSvcTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        userSvcTradeData.setEndDate(SysDateMgr.getLastDateThisMonth());
                        btd.add(btd.getRD().getUca().getSerialNumber(), userSvcTradeData);
                    }
                    else if ("0".equals(userSvcState))
                    {
                        userSvcTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                        userSvcTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
                        btd.add(btd.getRD().getUca().getSerialNumber(), userSvcTradeData);
                    }
                }
        	}else{
        		 for (int j = 0, sizeSvcStateList = userSvcStateList.size(); j < sizeSvcStateList; j++)
                 {
                     SvcStateTradeData userSvcTradeData = userSvcStateList.get(j);
                     String userSvcState = userSvcTradeData.getStateCode();
                     if ("1".equals(userSvcState) || "5".equals(userSvcState))
                     {
                         userSvcTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                         userSvcTradeData.setEndDate(btd.getRD().getAcceptTime());
                         btd.add(btd.getRD().getUca().getSerialNumber(), userSvcTradeData);
                     }
                     else if ("0".equals(userSvcState))
                     {
                         userSvcTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                         userSvcTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
                         btd.add(btd.getRD().getUca().getSerialNumber(), userSvcTradeData);
                     }
                 }
        	}
        }
    }

    // 服务状态变更之后修改用户主体服务
    // 注：请注意userId参数，考虑到有可能对一笔业务中对多个用户的服务状态做修改，所以需要制定一下具体的用户ID
    public void modifyMainSvcStateByUserId(BusiTradeData btd) throws Exception
    {
        String tradeTypeCode = btd.getTradeTypeCode();
        if(!"701".equals(tradeTypeCode) && !"711".equals(tradeTypeCode) && !"138".equals(tradeTypeCode) && !"128".equals(tradeTypeCode)) 
        {
            List<SvcStateTradeData> newTradesSvcDataList = btd.getTradeDatas(TradeTableEnum.TRADE_SVCSTATE);
            if(newTradesSvcDataList==null || newTradesSvcDataList.size()<=0)
            {
                CSAppException.apperr(TradeException.CRM_TRADE_214);// 无用户服务状态订单信息
            }
        }
        List<SvcStateTradeData> tradesSvcDataList = btd.getRD().getUca().getUserSvcsState();
        String userId = btd.getRD().getUca().getUserId();// 取业务请求的用户ID
        String acceptTime = btd.getRD().getAcceptTime();// 取业务请求的用户ID
        StringBuilder sb = new StringBuilder(10);
        String lastStopTime = "";
        if (tradesSvcDataList != null && !tradesSvcDataList.isEmpty())
        {
            for (int i = 0, size = tradesSvcDataList.size(); i < size; i++)
            {
                SvcStateTradeData svcStateData = tradesSvcDataList.get(i);
                String mainTag = svcStateData.getMainTag();
                String tempUserId = svcStateData.getUserId();
                // 对userId做个比较,只寻找主体服务的
                if (StringUtils.equals(tempUserId, userId) && StringUtils.equals("1", mainTag))
                {
                    String stateCode = svcStateData.getStateCode();
                    String endDate = svcStateData.getEndDate();
                    // add by chenzm 宽带有预约停机情况，根据结束时间来判
                    if (endDate.compareTo(acceptTime) > 0)
                    {
                        sb.append(stateCode);
                        if (!StringUtils.equals("0", stateCode) && !StringUtils.equals("6", stateCode) && !StringUtils.equals("8", stateCode) && !StringUtils.equals("9", stateCode) && !StringUtils.equals("E", stateCode)
                                && !StringUtils.equals("F", stateCode) && !StringUtils.equals("N", stateCode) && !StringUtils.equals("X", stateCode) && !StringUtils.equals("Z", stateCode))
                        {
                            lastStopTime = svcStateData.getStartDate();
                        }
                    }
                }
            }
        }
        else
        {
            CSAppException.apperr(TradeException.CRM_TRADE_214);// 无用户服务状态订单信息
        }

        if (sb.length() > 0)// 加个判断
        {
            if (StringUtils.isEmpty(lastStopTime))
            {
                lastStopTime = UserSvcStateInfoQry.getUserLastStopTime(userId);
            }
            // 获取userTradeData,一个userId同一次y业务应该只有一个userTrade
            UserTradeData utd = this.getUserTradeData(btd, userId);
            if (StringUtils.isEmpty(utd.getModifyTag()) || StringUtils.equals(utd.getModifyTag(), BofConst.MODIFY_TAG_USER))
            {
                utd.setModifyTag(BofConst.MODIFY_TAG_UPD);
            }
            utd.setUserStateCodeset(sb.toString());
            utd.setLastStopTime(lastStopTime);
            /**
             * REQ201612080012 优化手机销户关联宽带销号的相关规则
             * chenxy3 20170728
             * */
            if(StringUtils.equals(tradeTypeCode, "7221")|| StringUtils.equals(tradeTypeCode, "7222") ||StringUtils.equals(tradeTypeCode, "7223")){
            	utd.setRsrvTag2("1");//欠费停机将宽带USER表RSRV_STR2的RSRV_TAG设为1 
            }else if("604".equals(tradeTypeCode)||"7301".equals(tradeTypeCode)||"7302".equals(tradeTypeCode)||"7306".equals(tradeTypeCode)||"7307".equals(tradeTypeCode)||"7308".equals(tradeTypeCode)){
            	if(utd.getRsrvTag2()!=null && "1".equals(utd.getRsrvTag2())){
            		utd.setRsrvTag2("");//缴费复机将宽带USER表RSRV_STR2的RSRV_TAG设为空
            	}
            }
        }
        else
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("当前业务没有获取主体服务相关信息！");
            }
        }
    }
    
  //增加短信服务状态
    private void addSmsSvcState(UcaData uca,List<SvcStateTradeData> usetSvcStateList,IDataset svcStateParaBuf)throws Exception
    {
        boolean flag=false;//判断当前服务状态变化中是否存在短信的服务状态变化
        if(IDataUtil.isNotEmpty(svcStateParaBuf))
        {
            for(int i=0;i<svcStateParaBuf.size();i++)
            {
                if(StringUtils.equals("5", svcStateParaBuf.getData(i).getString("SERVICE_ID","")))
                {
                    flag=true;
                    break;
                }
            }
        }
        
        //当前业务需要改变短信的服务状态，存在短信服务，但是不存在短信服务状态
        if(flag && null==uca.getUserSvcsStateByServiceId("5"))
        {
            List smsSvcDataList  =uca.getUserSvcBySvcId("5");
            if(smsSvcDataList.size()>0)
            {
                for(int i=0;i<smsSvcDataList.size();i++)
                {
                    SvcTradeData smsSvcData = (SvcTradeData)smsSvcDataList.get(i);
                    String endDate = smsSvcData.getEndDate();
                    String sysDate = SysDateMgr.getSysTime();
                    if(SysDateMgr.getTimeDiff(sysDate,endDate,SysDateMgr.PATTERN_STAND)>0)
                    {
                        SvcStateTradeData smsSvcSateData =  new SvcStateTradeData();
                        smsSvcSateData.setUserId(smsSvcData.getUserId());
                        smsSvcSateData.setServiceId(smsSvcData.getElementId());
                        smsSvcSateData.setMainTag(smsSvcData.getMainTag());
                        smsSvcSateData.setStateCode("0");
                        smsSvcSateData.setStartDate(smsSvcData.getStartDate());
                        smsSvcSateData.setEndDate(smsSvcData.getEndDate());
                        smsSvcSateData.setInstId(SeqMgr.getInstId());
                        smsSvcSateData.setRemark("补充服务状态");
                        smsSvcSateData.setModifyTag("USER");
                        
                        usetSvcStateList.add(smsSvcSateData);//补上短信的服务状态
                        break;
                    }
                }
            }
        }
    }
    //增加短信服务for 701 711
    private void addSmsSvc(String tradeTypeCode,UcaData uca,List<SvcTradeData> usetSvcList) throws Exception
    {
        boolean flag = true;
        
        if("701".equals(tradeTypeCode)||"711".equals(tradeTypeCode) ||"7121".equals(tradeTypeCode)){//仅有701和711服务状态变更，手动增加短信服务
            List<SvcTradeData> smsSvcList = uca.getUserSvcBySvcId("5");
            if(smsSvcList!=null && smsSvcList.size()>0){
                flag = false;
            }
            if(flag){//缺失短信服务
                SvcTradeData sData =  new SvcTradeData();
                sData.setUserId(uca.getUserId());
                sData.setElementId("5");
                sData.setUserIdA("-1");
                String productId = uca.getUserNewMainProductId();
                if(StringUtils.isNotEmpty(productId)){
                    IDataset pkgset = PkgElemInfoQry.getPackageElementByProductId(productId, "S", "5");
                    if(IDataUtil.isNotEmpty(pkgset)){
                        sData.setProductId(productId);
                        sData.setPackageId(pkgset.getData(0).getString("PACKAGE_ID"));
                    }else{
                        sData.setProductId("-1");
                        sData.setPackageId("-1");
                    }
                }else{
                    sData.setProductId("-1");
                    sData.setPackageId("-1");
                }
                sData.setInstId(SeqMgr.getInstId());
                sData.setMainTag("0");
                sData.setStartDate(SysDateMgr.getSysTime());
                sData.setEndDate(SysDateMgr.getSysTime());
                sData.setRemark("增加垃圾短信缺失短信服务");
                usetSvcList.add(sData);
            }
        }
    }
}
