
package com.asiainfo.veris.crm.order.soa.person.busi.destroyuser.order.action;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;

public class CreditDestroyWideUserAction implements ITradeAction
{

    private static final Logger logger = Logger.getLogger(CreditDestroyWideUserAction.class);

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        UcaData ucaData = btd.getRD().getUca();
        String serialNumber = ucaData.getSerialNumber();
        String KDSerialNumber = "KD_" + serialNumber;
        String tradeTypeCode = btd.getTradeTypeCode();

        IData wideInfo = UcaInfoQry.qryUserMainProdInfoBySn(KDSerialNumber);
        if (IDataUtil.isNotEmpty(wideInfo))
        {
            IData param = new DataMap();
            param.put("SERIAL_NUMBER", KDSerialNumber);
            param.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
            param.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
            param.put("VALID_FLAG", "1");// 必传，宽带规则判断标示，传1宽带不判断手机状态
            // param.put("NET_TYPE_CODE", mainTrade.getString("NET_TYPE_CODE"));
            // param.put("TRADE_CITY_CODE", mainTrade.getString("TRADE_CITY_CODE"));
            // param.put("TRADE_DEPART_ID", mainTrade.getString("TRADE_DEPART_ID"));
            // param.put("TRADE_STAFF_ID", mainTrade.getString("TRADE_STAFF_ID"));
            // param.put("TRADE_TERMINAL_ID", termIp);
            // param.put("TRADE_BRAND_CODE", wideInfo.getString("BRAND_CODE"));

            // 宽带业务类型
            String operName = "";
            IDataset userWidenetInfo = WidenetInfoQry.getUserWidenetInfo(wideInfo.getString("USER_ID"));
            if (IDataUtil.isNotEmpty(userWidenetInfo))
            {
                IData userWidenet = userWidenetInfo.getData(0);
                String widenetType = userWidenet.getString("RSRV_STR2", "1");
                if ("192".equals(tradeTypeCode))
                {// 立即销户
                    operName = "发起宽带销户";
                    if ("1".equals(widenetType))
                    {
                        param.put("TRADE_TYPE_CODE", "605");
                    }
                    else if ("2".equals(widenetType)||"5".equals(widenetType)||"6".equals(widenetType))
                    {
                        //param.put("TRADE_TYPE_CODE", "624");
                    	param.put("TRADE_TYPE_CODE", "605");
                    }
                    else if ("3".equals(widenetType))
                    {
                        //param.put("TRADE_TYPE_CODE", "625");
                    	param.put("TRADE_TYPE_CODE", "605");
                    }
                    else if ("4".equals(widenetType))
                    { // 增加校园宽带
                        param.put("TRADE_TYPE_CODE", "635");
                    }

                }
            }

            String remark = "";
            try
            {
            	if(tradeTypeCode.equals("192"))
            	{
            		param.put("SKIP_RULE", "TRUE");  //传入SKIP_RULE 跳过规则校验
            		param.put("PHONE_DESTROY_TYPE", tradeTypeCode);
            	}
            		
                //IDataset result = CSAppCall.call("SS.DestroyWidenetUserNowRegSVC.tradeReg", param);
            	IDataset result = CSAppCall.call("SS.WidenetDestroyNewRegSVC.tradeReg", param); //调用新宽带拆机接口
            	
                IData idata = result.getData(0);

                // if(!"0".equals(idata.getString("X_RESULTCODE"))) {
                // remark = idata.getString("X_RESULTCODE") + "：" + idata.getString("X_RESULTINFO");
                // } else{
                // remark = idata.getString("X_RESULTCODE") + "：" + idata.getString("X_RESULTINFO") + "[" +
                // idata.getString("TRADE_ID") + "]" ;
                // }
                remark = "[" + idata.getString("TRADE_ID") + "]";
            }
            catch (Exception e)
            {
                remark = new StringBuilder(operName).append("：").append(Utility.getBottomException(e).getMessage()).toString();
                if (logger.isDebugEnabled())
                {
                    logger.debug(remark);
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
                logData.put("OPER_MOD", "手机立即销户关联宽带拆机");
                logData.put("OPER_TYPE", "INS");
                logData.put("OPER_TIME", SysDateMgr.getSysTime());
                logData.put("OPER_DESC", errorMsg);
                logData.put("RSRV_STR1", btd.getMainTradeData().getUserId());
                logData.put("RSRV_STR2", btd.getMainTradeData().getSerialNumber());
                logData.put("RSRV_STR2", btd.getMainTradeData().getCustId());
                
                Dao.insert("TL_B_CRM_OPERLOG", logData);
                //错误的关联拆机同时插入TF_B_ERROR_TRADE，aee调用重跑
                IData errorInfo = new DataMap();
                errorInfo.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
                errorInfo.put("RELA_TRADE_ID", btd.getTradeId());
                errorInfo.put("TRADE_TYPE_CODE", btd.getMainTradeData().getTradeTypeCode());
                errorInfo.put("RELA_SERIAL_NUMBER", btd.getRD().getUca().getSerialNumber());
                errorInfo.put("DEAL_NUM", "0");
                errorInfo.put("STATUS", "0");
                errorInfo.put("CREATE_TIME", SysDateMgr.getSysTime());
                errorInfo.put("CREATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                errorInfo.put("CREATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                errorInfo.put("SVC_ID", "SS.WidenetDestroyNewRegSVC.tradeReg");
                errorInfo.put("IN_PARAM", param.toString());
                errorInfo.put("IN_MODE_CODE",CSBizBean.getVisit().getInModeCode());
                errorInfo.put("REMARK", errorMsg);
                
                Dao.insert("TF_B_ERROR_TRADE", errorInfo,Route.getJourDb());
            }

            // 标记手机报销户带宽带销户的成功与否
            List<SvcStateTradeData> list = btd.getRD().getUca().getUserSvcsState();
            for (int i = 0, size = list.size(); i < size; i++)
            {
                SvcStateTradeData tradeData = list.get(i);
                String mainTag = tradeData.getMainTag();
                String stateCode = tradeData.getStateCode();

                if (StringUtils.equals("1", mainTag))
                {// && StringUtils.equals("0", stateCode)
                    tradeData.setRsrvStr5(subRemark(remark));
                    tradeData.setRsrvStr4(param.getString("TRADE_TYPE_CODE"));
                    break;
                }
            }
        }
    }

    private String subRemark(String remark)
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
}
