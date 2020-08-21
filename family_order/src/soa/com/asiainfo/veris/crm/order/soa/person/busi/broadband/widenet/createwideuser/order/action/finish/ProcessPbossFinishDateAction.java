
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createwideuser.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOfferRelInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePbossFinishInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePricePlanInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeUserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeWideNetInfoQry;

/**
 * 依据PBOSS竣工时间更新相关台帐时间
 * 
 * @author yuyj3
 */
public class ProcessPbossFinishDateAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER","");
        String finishDate = "";
        if ("630".equals(tradeTypeCode))
        {
            finishDate = SysDateMgr.getSysTime();
        }
        else
        {
            IDataset finishInfos = TradePbossFinishInfoQry.getTradePbossFinish(tradeId);
            if (IDataUtil.isEmpty(finishInfos))
            {
                CSAppException.apperr(WidenetException.CRM_WIDENET_14);
            }
            finishDate = finishInfos.getData(0).getString("UPDATE_TIME");
            if (StringUtils.isBlank(finishDate))
            {
                CSAppException.apperr(WidenetException.CRM_WIDENET_15);
            }
        }
        if ("600".equals(tradeTypeCode) || "611".equals(tradeTypeCode) || "612".equals(tradeTypeCode) || "613".equals(tradeTypeCode) || "630".equals(tradeTypeCode))
        {
            String execDate = "";
            String tagDate = "";
            String sysDay = "";
            TradeProductInfoQry.updateStartDate(tradeId, finishDate);
            // 判断回单时间在25日的前后
            IDataset tagInos = TagInfoQry.getTagInfo(CSBizBean.getUserEparchyCode(), "CS_WIDENET_OPENFINISH", "0", null);
            if (IDataUtil.isEmpty(tagInos))
            {
                CSAppException.apperr(WidenetException.CRM_WIDENET_16);
            }
            tagDate = tagInos.getData(0).getString("TAG_INFO");
            
            //edit by zhangxing3 for "商务宽带仍需走25号原则"
            if (serialNumber.length() > 14 && "600".equals(tradeTypeCode))
            {
            	tagDate = "25";
            }
            //System.out.println("===========ProcessPbossFinishDateAction============tagDate:"+tagDate);
            //edit by zhangxing3 for "商务宽带仍需走25号原则"
            
            if ("630".equals(tradeTypeCode))
            {
                execDate = finishDate;
            }
            else
            {
                sysDay = finishDate.substring(8, 10);
                // 默认下月开通
                execDate = SysDateMgr.getDateNextMonthFirstDay(finishDate);
                // 如大于标识日期，则下下月开通
                if (sysDay.compareTo(tagDate) >= 0)
                {
                    execDate = SysDateMgr.getDateNextMonthFirstDay(execDate);
                }
                //更新资费开始时间
                //TradeDiscntInfoQry.updateStartDate(tradeId, execDate);
                IDataset idsTradeDiscnt = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
                if (IDataUtil.isNotEmpty(idsTradeDiscnt))
                {
                	for (int i = 0; i < idsTradeDiscnt.size(); i++) 
                	{
                		IData idTradeDiscnt = idsTradeDiscnt.getData(i);
                		String strDisntCode = idTradeDiscnt.getString("DISCNT_CODE", "");
                		IDataset idsDiscnt3966 = CommparaInfoQry.getCommparaAllColByParser("CSM", "3966", strDisntCode, "0898");
                		if (IDataUtil.isNotEmpty(idsDiscnt3966))
                        {
                			IData idDiscnt3966 = idsDiscnt3966.first();
                			//String strPc = idDiscnt3966.getString("PARAM_CODE", "");
                			String strPc1 = idDiscnt3966.getString("PARA_CODE1", "");
                			String strEndDate = SysDateMgr.endDateOffset(execDate, strPc1, "3");
                			TradeDiscntInfoQry.updateEndDate(tradeId, strEndDate, strDisntCode);
                        }
					}
                }
            }

            //更新资费开始时间
            TradeDiscntInfoQry.updateStartDate(tradeId, execDate);
            //更新定价计划开始时间
            TradePricePlanInfoQry.updateStartDate(tradeId, BofConst.ELEMENT_TYPE_CODE_DISCNT, execDate);
            //更新商品关系开始时间
            TradeOfferRelInfoQry.updateStartDate(tradeId, BofConst.ELEMENT_TYPE_CODE_DISCNT, execDate);
            
            IDataset discntInfos = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
            for (int i = 0; i < discntInfos.size(); i++)
            {
                IData discntInfo = discntInfos.getData(i);
                int rsrvStr1 = discntInfo.getInt("RSRV_STR1", 0);

                String rsrvStr2 = discntInfo.getString("RSRV_STR2");// 顺延优惠inst_id
                String resultDate = discntInfo.getString("START_DATE");
                
                //add by zhangxing3 for REQ201804280023优化“先装后付，免费体验”--start
                //宽带回单时顺延免费体验套餐的开始、结束时间
                if("84010038".equals(discntInfo.getString("DISCNT_CODE","")) && BofConst.MODIFY_TAG_ADD.equals(discntInfo.getString("MODIFY_TAG","")))
                {
                	rsrvStr1 = 1;
                }
                if("84010039".equals(discntInfo.getString("DISCNT_CODE",""))&& BofConst.MODIFY_TAG_ADD.equals(discntInfo.getString("MODIFY_TAG","")))
                {
                	rsrvStr1 = 2;
                }
                if("84010040".equals(discntInfo.getString("DISCNT_CODE",""))&& BofConst.MODIFY_TAG_ADD.equals(discntInfo.getString("MODIFY_TAG","")))
                {
                	rsrvStr1 = 3;
                }
                //add by zhangxing3 for REQ201804280023优化“先装后付，免费体验” --end
                
                if (rsrvStr1 != 0)
                {
                    resultDate = SysDateMgr.getAddMonthsLastDay(rsrvStr1, resultDate);
                    // 包月包年资费时间处理
                    TradeDiscntInfoQry.updateStartEndDate(tradeId, discntInfo.getString("INST_ID"), discntInfo.getString("START_DATE"), resultDate);
                    // 更新定价计划开始时间、结束时间
                    TradePricePlanInfoQry.updateStartAndEndDate(tradeId, discntInfo.getString("INST_ID"), discntInfo.getString("START_DATE"), resultDate);
                    //更新商品关系开始时间、结束时间
                    TradeOfferRelInfoQry.updateStartAndEndDate(tradeId, discntInfo.getString("INST_ID"), discntInfo.getString("START_DATE"), resultDate);
                }
                
                if (StringUtils.isNotBlank(rsrvStr2))
                {
                    resultDate = SysDateMgr.getNextSecond(resultDate);
                    // 绑定优惠顺延的开始时间处理
                    TradeDiscntInfoQry.updateStartEndDate(tradeId, rsrvStr2, resultDate, SysDateMgr.getTheLastTime());
                    // 更新定价计划开始时间、结束时间
                    TradePricePlanInfoQry.updateStartAndEndDate(tradeId, rsrvStr2, resultDate, SysDateMgr.getTheLastTime());
                    //更新商品关系开始时间、结束时间
                    TradeOfferRelInfoQry.updateStartAndEndDate(tradeId, rsrvStr2, resultDate, SysDateMgr.getTheLastTime());
                }
            }

            // 更新宽带保底优惠时间
            TradeDiscntInfoQry.updateStartDate2(tradeId, execDate);
            //更新宽带保底优惠定价计划开始时间
            TradePricePlanInfoQry.updateStartDate(tradeId, BofConst.ELEMENT_TYPE_CODE_DISCNT, execDate);
           
            String finishDate2 = finishDate.substring(0, 10) + SysDateMgr.START_DATE_FOREVER;
            
            // 更新服务时间
            TradeSvcInfoQry.updateStartDate(tradeId, finishDate2);
            //更新商品关系开始时间
            TradeOfferRelInfoQry.updateStartDate(tradeId, BofConst.ELEMENT_TYPE_CODE_SVC, finishDate2);
            
        }

        TradeUserInfoQry.updateOpenDate(tradeId, finishDate);
        TradePayRelaInfoQry.updateStartDate(tradeId, finishDate);
        TradeWideNetInfoQry.updateStartDate(tradeId, finishDate);
    }

}
