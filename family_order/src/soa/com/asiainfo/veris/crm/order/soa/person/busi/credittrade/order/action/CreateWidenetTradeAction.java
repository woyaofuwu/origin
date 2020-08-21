
package com.asiainfo.veris.crm.order.soa.person.busi.credittrade.order.action;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

public class CreateWidenetTradeAction implements ITradeAction
{
    private static transient final Logger logger = Logger.getLogger(CreateWidenetTradeAction.class);

    /**
     * 将正常业务类型转换成宽带业务类型
     * 
     * @author chenzm
     * @param input
     * @throws Exception
     */
    public static void transWidenetTradeType(IData input,BusiTradeData btd) throws Exception
    {
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE", "");
        // 信控停机\开机\欠停\缴开\欠销等业务需对宽带用户进行相同的操作 7321特开转开通， 459家庭用户欠费停机 460 家庭用户缴费开机
        if ("7220".equals(tradeTypeCode) || "7110".equals(tradeTypeCode) || "7101".equals(tradeTypeCode) || "7321".equals(tradeTypeCode) || "7301".equals(tradeTypeCode) || "7303".equals(tradeTypeCode) || "7240".equals(tradeTypeCode)
                || "7317".equals(tradeTypeCode) || "459".equals(tradeTypeCode) || "460".equals(tradeTypeCode))
        {
            String serialNumber = input.getString("SERIAL_NUMBER");
            IData userInfo = UcaInfoQry.qryUserInfoBySn("KD_" + serialNumber);
            if (IDataUtil.isEmpty(userInfo))
            {
                input.put("MSG", "没有宽带用户！");
                return;
            }
            String userId = userInfo.getString("USER_ID");
            IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfo(userId);
            if (IDataUtil.isNotEmpty(widenetInfos))
            {
                String widenetType = widenetInfos.getData(0).getString("RSRV_STR2");
                if (StringUtils.isBlank(widenetType))
                {
                    widenetType = "1";
                }
                // 如果宽带用户有拆机工单，则没有必要再发起信控工单
                IDataset destoryInfos = TradeInfoQry.getDestoryTradeWidenetInfos(userId);
                if (IDataUtil.isNotEmpty(destoryInfos))
                {
                    input.put("MSG", "宽带用户有拆机工单，则没有必要再发起信控工单！");
                    return;
                }

                // 手机高额半停机、高额停机、欠费停机，家庭用户欠费停机
                if ("7220".equals(tradeTypeCode) || "7110".equals(tradeTypeCode) || "7101".equals(tradeTypeCode) || "459".equals(tradeTypeCode))
                {
                    // 手机用户欠停时，如宽带用户已报停或有报停工单，则不再发起宽带欠停工单
                    String userStateCode = userInfo.getString("USER_STATE_CODESET");
                    if ("1".equals(userStateCode) || "01".equals(userStateCode) || "5".equals(userStateCode) || "05".equals(userStateCode))
                    {
                        input.put("MSG", "如宽带用户已报停或有报停工单，则不再发起宽带欠停工单！");
                        return;
                    }
                    IDataset svcStateInfos = TradeSvcStateInfoQry.queryTradeWidenetUserMainSvcState("0", "1", userId, "0");
                    if (IDataUtil.isNotEmpty(svcStateInfos))
                    {// 已有未执行的宽带停机工单
                        String newSvcState = svcStateInfos.getData(0).getString("STATE_CODE");
                        if ("1".equals(newSvcState) || "5".equals(newSvcState))
                        {// 存在有效的宽带报停工单，不再生成产宽带欠停工单
                            input.put("MSG", "存在有效的宽带报停工单，不再生成产宽带欠停工单！");
                            return;
                        }
                    }
                    IDataset userAllDiscntInfos = UserDiscntInfoQry.getDiscntByUserId(userInfo.getString("USER_ID"));
                	boolean isYearDiscnt = false;
                	String operType = "";
                	if(DataSetUtils.isNotBlank(userAllDiscntInfos)){
                		for(int i = 0 ; i < userAllDiscntInfos.size() ; i++){
                    		String discntId = userAllDiscntInfos.getData(i).getString("DISCNT_CODE");
                    		IDataset commparaInfos = CommparaInfoQry.getCommparaByCodeCode1("CSM","532","600",discntId);
                    		if(DataSetUtils.isNotBlank(commparaInfos)){
                    			isYearDiscnt = true;
                    			operType = "1";//存量用户包年套餐
                    			break;
                    		}
                    	}
                	}
                	//宽带包年活动
                	IData userInfos = UcaInfoQry.qryUserInfoBySn(serialNumber.replace("KD_", ""));
                	if(IDataUtil.isEmpty(userInfos)){
                		return;
                	}
                	IDataset userSaleActiveInfos = UserSaleActiveInfoQry.queryUserSaleActiveByTag(userInfos.getString("USER_ID"));
                	if(DataSetUtils.isNotBlank(userSaleActiveInfos)){
                		IDataset paramSaleActive = CommparaInfoQry.getCommparaInfos("CSM","532","WIDE_YEAR_ACTIVE");
                		if(DataSetUtils.isNotBlank(paramSaleActive)){
                			for(int i = 0 ; i < userSaleActiveInfos.size() ; i++){
                				for(int j = 0 ; j < paramSaleActive.size() ; j++){
                					if(StringUtils.equals(userSaleActiveInfos.getData(i).getString("PRODUCT_ID"), paramSaleActive.getData(j).getString("PARA_CODE1"))){
                						isYearDiscnt = true;
                						operType = "2";//宽带包年优惠营销活动
                            			break;
                					}
                				}
                			}
                		}
                	}
                	if(isYearDiscnt){//有包年套餐或包年活动，停手机不停宽带,不生成宽带欠停工单
                		 // 标记手机报停没有连带宽带报停的原因，修改TF_F_USER_SVCSTATE表
                        List<SvcStateTradeData> list = btd.getRD().getUca().getUserSvcsState();
                        for (int i = 0, size = list.size(); i < size; i++)
                        {
                            SvcStateTradeData tradeData = list.get(i);
                            String mainTag = tradeData.getMainTag();
                            if (StringUtils.equals("1", mainTag))
                            {// && StringUtils.equals("0", stateCode)
                            	String msg = "包年优惠营销活动";
                            	if(StringUtils.equals("1", operType)){
                            		msg = "包年套餐优惠";
                            	}
                                tradeData.setRsrvStr5("有宽带"+msg+"用户，停手机不停宽带!");
                                tradeData.setRsrvStr4(input.toString());
                                break;
                            }
                        }
                        input.put("MSG", "有宽带包年用户，停手机不停宽带！");
                		return;
                	}
                    tradeTypeCode = "7221";// GPON宽带欠费停机
                    if ("2".equals(widenetType))
                    {

                        tradeTypeCode = "7222";// ADSL宽带欠费停机

                    }
                    else if ("3".equals(widenetType))
                    {

                        tradeTypeCode = "7223";// 光纤宽带欠费停机

                    }
                    else if ("4".equals(widenetType))
                    {

                        tradeTypeCode = "7224";// 校园宽带欠费停机
                    }
                }
                else if ("7301".equals(tradeTypeCode) || "7303".equals(tradeTypeCode) || "7317".equals(tradeTypeCode) || "7321".equals(tradeTypeCode) || "460".equals(tradeTypeCode))// 手机缴费开机、高额开机 460-家庭用户缴费开机
                // 7321
                // 特开转开通
                {
                    // 手机用户缴开时，如宽带用户已报停或有报停工单，则不再发起宽带缴开工单

                    String userStateCode = userInfo.getString("USER_STATE_CODESET");
                    if ("1".equals(userStateCode) || "01".equals(userStateCode))
                    {
                        input.put("MSG", "手机用户缴开时，如宽带用户已报停或有报停工单，则不再发起宽带缴开工单！");
                        return;
                    }
                    IDataset svcStateInfos = TradeSvcStateInfoQry.queryTradeWidenetUserMainSvcState("0", "1", userId, "0");
                    if (IDataUtil.isNotEmpty(svcStateInfos))
                    {// 已有未执行的宽带停机工单
                        String newSvcState = svcStateInfos.getData(0).getString("STATE_CODE");
                        if ("1".equals(newSvcState))
                        {// 存在有效的宽带报停工单，不再生成产宽带欠停工单
                            input.put("MSG", "存在有效的宽带报停工单，不再生成产宽带欠停工单！");
                            return;
                        }
                    }
                    // TODO 预约停机工单判段 ，j2ee没有预约工单 pboss判
                    tradeTypeCode = "7306";// GPON宽带缴费开机
                    if ("2".equals(widenetType))
                    {

                        tradeTypeCode = "7307";// ADSL宽带缴费开机

                    }
                    else if ("3".equals(widenetType))
                    {

                        tradeTypeCode = "7308";// 光纤宽带缴费开机

                    }
                    else if ("4".equals(widenetType))
                    {

                        tradeTypeCode = "7309";// 校园宽带缴费开机
                    }
                }
                else if ("7240".equals(tradeTypeCode))
                {

                    tradeTypeCode = "7241";// GPON宽带欠费销号
                    if ("2".equals(widenetType) ||"5".equals(widenetType)||"6".equals(widenetType))
                    {

                        tradeTypeCode = "7242";// ADSL宽带欠费销号

                    }
                    else if ("3".equals(widenetType))
                    {

                        tradeTypeCode = "7243";// FTTH宽带欠费销号

                    }
                    else if ("4".equals(widenetType))
                    {

                        tradeTypeCode = "7244";// 校园宽带宽带欠费销号
                    }
                }

                // REQ201212070013关联宽带欠停和缴开优化
                if ("7221".equals(tradeTypeCode) || "7222".equals(tradeTypeCode) || "7223".equals(tradeTypeCode) || "7224".equals(tradeTypeCode) || "7306".equals(tradeTypeCode) || "7307".equals(tradeTypeCode) || "7308".equals(tradeTypeCode)
                        || "7309".equals(tradeTypeCode))
                {

                    UcaData uca = UcaDataFactory.getUcaByUserId(userId);
                    List<SvcStateTradeData> userSvcStates = uca.getUserSvcsState();

                    String userSvcState = "";
                    for (SvcStateTradeData svcStateTradeData : userSvcStates)
                    {
                        if ("1".equals(svcStateTradeData.getMainTag()))
                        {
                            userSvcState += svcStateTradeData.getStateCode();
                        }
                    }

                    // 如果宽带用户已经是欠停状态，手机发起欠费停机，直接返回，不再发起宽带欠费停机
                    if (("7221".equals(tradeTypeCode) || "7222".equals(tradeTypeCode) || "7223".equals(tradeTypeCode) || "7224".equals(tradeTypeCode)) && "5".equals(userSvcState))
                    {
                        input.put("MSG", "如果宽带用户已经是欠停状态，手机发起欠费停机，直接返回，不再发起宽带欠费停机");
                        return;
                    }
                    // 如果宽带用户已经是开通状态，手机发起缴费开机，直接返回，不再发起宽带缴费开机
                    if (("7306".equals(tradeTypeCode) || "7307".equals(tradeTypeCode) || "7308".equals(tradeTypeCode) || "7309".equals(tradeTypeCode)) && "0".equals(userSvcState))
                    {
                        input.put("MSG", "如果宽带用户已经是开通状态，手机发起缴费开机，直接返回，不再发起宽带缴费开机");
                        return;
                    }

                    // 双状态的特殊处理
                    IDataset tradeSvcStates = TradeSvcStateInfoQry.queryTradeWidenetUserMainSvcState("1", userId, "0");
                    List<SvcStateTradeData> tempUserSvcStateTradeDatas = new ArrayList<SvcStateTradeData>();
                    if (IDataUtil.isNotEmpty(tradeSvcStates))
                    {
                        tradeSvcStates = DataBusUtils.filterInValidDataByEndDate(tradeSvcStates);
                        for (int i = 0, size = tradeSvcStates.size(); i < size; i++)
                        {
                            IData tradeSvcState = tradeSvcStates.getData(i);
                            SvcStateTradeData tempState = new SvcStateTradeData(tradeSvcState);
                            tempUserSvcStateTradeDatas.add(tempState);
                        }
                        uca.setUserSvcStateList(tempUserSvcStateTradeDatas);
                    }
                    else
                    {
                        uca.setUserSvcStateList(null);
                    }
                }

                input.put("SERIAL_NUMBER", "KD_" + serialNumber);
                input.put("USER_ID", userId);
                input.put("TRADE_TYPE_CODE", tradeTypeCode);
            }
            else
            {
                CSAppException.apperr(WidenetException.CRM_WIDENET_22);
            }
        }

    }

    public void executeAction(BusiTradeData btd) throws Exception
    {

        String tradeTypeCode = btd.getMainTradeData().getTradeTypeCode();
        String serialNumber = btd.getMainTradeData().getSerialNumber();
        String eparchyCode = btd.getMainTradeData().getEparchyCode();
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        transWidenetTradeType(param,btd);
        String remark = "";
        IData resultInfo = new DataMap();
        resultInfo.put("SERIAL_NUMBER",param.getString("SERIAL_NUMBER"));
        resultInfo.put("TRADE_TYPE_CODE",param.getString("TRADE_TYPE_CODE"));
        resultInfo.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        tradeTypeCode = param.getString("TRADE_TYPE_CODE");
        logger.debug("***********<>cxy<>********tradeTypeCode="+tradeTypeCode);
        //System.out.println("***********<>cxy<>********tradeTypeCode="+tradeTypeCode);
        if (StringUtils.equals(tradeTypeCode, "7306") // GPON宽带缴费开机
                || StringUtils.equals(tradeTypeCode, "7307") // ADSL宽带缴费开机
                || StringUtils.equals(tradeTypeCode, "7308") // 光纤宽带欠费停机
                || StringUtils.equals(tradeTypeCode, "7309")// 校园宽带缴费开机
                || StringUtils.equals(tradeTypeCode, "7221") // GPON宽带欠费停机
                || StringUtils.equals(tradeTypeCode, "7222") // ADSL宽带欠费停机
                || StringUtils.equals(tradeTypeCode, "7223") // 光纤宽带欠费停机
                || StringUtils.equals(tradeTypeCode, "7224")// 校园宽带欠费停机
                || StringUtils.equals(tradeTypeCode, "7241") // GPON宽带欠费拆机 20170726 修改，不再拆机，改为报停
                || StringUtils.equals(tradeTypeCode, "7242") // ADSL宽带欠费拆机 20170726 修改，不再拆机，改为报停
                || StringUtils.equals(tradeTypeCode, "7243") //光纤宽带欠费拆机  20170726 修改，不再拆机，改为报停
        )
        { 
        	if(StringUtils.equals(tradeTypeCode, "7241") // GPON宽带欠费拆机 20170726 修改，不再拆机，改为报停
            ){
        		param.put("TRADE_TYPE_CODE", "7221"); //GPON宽带欠费停机
        	}
        	if(StringUtils.equals(tradeTypeCode, "7242") // GPON宽带欠费拆机 20170726 修改，不再拆机，改为报停
            ){
        		param.put("TRADE_TYPE_CODE", "7222"); //ADSL宽带欠费停机
        	}
        	if(StringUtils.equals(tradeTypeCode, "7243") // GPON宽带欠费拆机 20170726 修改，不再拆机，改为报停
            ){
        		param.put("TRADE_TYPE_CODE", "7223");//FTTH宽带欠费停机
        	} 
        	logger.debug("***********<>cxy<>********param="+param);
        	//System.out.println("***********<>cxy<>********param="+param);
        	try
            {
                IDataset result = CSAppCall.call("SS.ChangeWidenetSvcStateRegSVC.tradeReg", param);
                logger.debug("***********<>cxy<>********result="+result);
                //System.out.println("***********<>cxy<>********result="+result);
                IData idata = result.getData(0);
                remark = "[" + idata.getString("TRADE_ID") + "]";
            }
            catch (Exception e)
            {
                e.printStackTrace();
                if( logger.isDebugEnabled() )
            	{
                logger.debug(e.getMessage());
            	}
                remark = e.getMessage();
                if(remark == null || "".equals(remark))
                {
                    remark = getCause(e);
                }
            }
            // 标记手机报停没有连带宽带报停的原因，修改TF_F_USER_SVCSTATE表
            List<SvcStateTradeData> list = btd.getRD().getUca().getUserSvcsState();
            for (int i = 0, size = list.size(); i < size; i++)
            {
                SvcStateTradeData tradeData = list.get(i);
                String mainTag = tradeData.getMainTag();
                if (StringUtils.equals("1", mainTag))
                {// && StringUtils.equals("0", stateCode)
                    tradeData.setRsrvStr5(subRemark(remark));
                    tradeData.setRsrvStr4(subRemark(resultInfo.toString()));
                    break;
                }
            } 
            //增加绑定一个优惠
            if(StringUtils.equals(tradeTypeCode, "7241") // GPON宽带欠费拆机 20170726 修改，不再拆机，改为报停
                    || StringUtils.equals(tradeTypeCode, "7242") // ADSL宽带欠费拆机 20170726 修改，不再拆机，改为报停
                    || StringUtils.equals(tradeTypeCode, "7243") //光纤宽带欠费拆机  20170726 修改，不再拆机，改为报停
            ){
            	 DiscntTradeData newDiscnt = new DiscntTradeData();
                 newDiscnt.setUserId(btd.getMainTradeData().getUserId());
                 newDiscnt.setProductId("-1");
                 newDiscnt.setPackageId("-1");
                 newDiscnt.setElementId("20170731");
                 newDiscnt.setInstId(SeqMgr.getInstId());
                 newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
                 newDiscnt.setSpecTag("0");
                 newDiscnt.setStartDate(SysDateMgr.getFirstDayOfNextMonth());
                 newDiscnt.setEndDate(SysDateMgr.END_DATE_FOREVER);
                 newDiscnt.setRemark("宽带减免套餐");
                 btd.add(serialNumber, newDiscnt);
            }
        } 
        else if (StringUtils.equals(tradeTypeCode, "7244")) // 校园宽带欠费拆机
        {
            try
            {
				//add begin by zhangyc5 on 20160609 手机欠费销号，宽带无条件撤单，
        		param.put("SKIP_RULE", "TRUE");  //传入SKIP_RULE 跳过规则校验
        		param.put("PHONE_DESTROY_TYPE", "7240");
        		//add end
            	IDataset result = CSAppCall.call("SS.DestroyWidenetUserNowRegSVC.tradeReg", param);
            	IData idata = result.getData(0);
                remark = "[" + idata.getString("TRADE_ID") + "]";
            }
            catch (Exception e)
            {
            	logger.error(e);
                remark = e.getMessage();
                if(remark == null || "".equals(remark))
                {
                	remark = getCause(e);
                }
                if( logger.isDebugEnabled() )
            	{
                logger.debug(e.getMessage());
            	}
                //记录失败日志
                String errorMsg = Utility.getBottomException(e).getMessage();
                if(errorMsg != null && !"".equals(errorMsg))
                	if(errorMsg.length() > 2000)
                		errorMsg = errorMsg.substring(0,1999);
                IData logData = new DataMap();
                logData.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
                logData.put("OPER_ID", btd.getMainTradeData().getOrderId());
                logData.put("STAFF_ID", "SUPERUSR");
                logData.put("OPER_MOD", "手机欠费销号关联宽带拆机");
                logData.put("OPER_TYPE", "INS");
                logData.put("OPER_TIME", SysDateMgr.getSysTime());
                logData.put("OPER_DESC", errorMsg);
                logData.put("RSRV_STR1", btd.getMainTradeData().getUserId());
                logData.put("RSRV_STR2", btd.getMainTradeData().getSerialNumber());
                logData.put("RSRV_STR2", btd.getMainTradeData().getCustId());
                
                Dao.insert("TL_B_CRM_OPERLOG", logData);
            }
        	resultInfo.put("IN_MODE_CODE",param.getString("IN_MODE_CODE"));
        	resultInfo.put("X_TRANS_CODE",param.getString("X_TRANS_CODE"));
        	resultInfo.put("ORDER_TYPE_CODE",param.getString("ORDER_TYPE_CODE"));
        	 // 标记手机报停没有连带宽带报停的原因，修改TF_F_USER_SVCSTATE表
            List<SvcStateTradeData> list = btd.getRD().getUca().getUserSvcsState();
            for (int i = 0, size = list.size(); i < size; i++)
            {
                SvcStateTradeData tradeData = list.get(i);
                String mainTag = tradeData.getMainTag();
                if (StringUtils.equals("1", mainTag))
                {// && StringUtils.equals("0", stateCode)
                    tradeData.setRsrvStr5(subRemark(remark));
                    tradeData.setRsrvStr4(subRemark(resultInfo.toString()));
                    break;
                }
            }
        }else{ //记录不发起原因
            // 标记手机没有连带宽带的原因，修改TF_F_USER_SVCSTATE表
            List<SvcStateTradeData> list = btd.getRD().getUca().getUserSvcsState();
            for (int i = 0, size = list.size(); i < size; i++)
            {
                SvcStateTradeData tradeData = list.get(i);
                String mainTag = tradeData.getMainTag();
                if (StringUtils.equals("1", mainTag))
                {// && StringUtils.equals("0", stateCode)
                    remark = param.getString("MSG");
                    tradeData.setRsrvStr5(subRemark(remark));
                    break;
                }
            }
        }
    }
	
	private String subRemark(String remark)
    {
	    if(remark != null && !"".equals(remark))
		{
			byte[] bytes = remark.getBytes();
	        if (bytes.length > 200)
	        {
	            byte[] newbytes = new byte[200];
	            for (int j = 200; j > 0; j--)
	            {
	                newbytes[200 - j] = bytes[bytes.length - j];
	            }
	            remark = new String(newbytes);
	        }
	        return remark;
		}
        
		return "";
    }
    
    private String getCause(Throwable throwable) {

		if (throwable.getCause() != null) {
			return getCause(throwable.getCause());
		} else {
			return getStackTrace(throwable);
		}
	}
	
	private String getStackTrace(Throwable e)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String str = sw.toString();
        return str;
    }

}
