package com.asiainfo.veris.crm.order.soa.person.busi.smartaddvalue.order.action.finish;

import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;

public class DredgeSmartNetworkAction implements ITradeFinishAction {

    protected static Logger log = Logger.getLogger(DredgeSmartNetworkAction.class);

    @Override
    public void executeAction(IData mainTrade) throws Exception {

        String tradeId = mainTrade.getString("TRADE_ID");
        System.out.println("执行DredgeSmartNetworkAction");

        IDataset commparaInfos9211 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM", "9211", null, null);

        //REQ201810090021 回单时把优惠的结束时间改为回单时间的当月月末
        //REQ202003240027  优化智能组网资费的需求—BOSS侧
        //按次按月 竣工次月生效,有效期1年,调测服务,套餐受理后立即生效，竣工后立即失效
        IDataset discnts = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
        if (IDataUtil.isNotEmpty(discnts)) {
            for (int i = 0; i < discnts.size(); i++) {
                String discntCode = discnts.getData(i).getString("DISCNT_CODE");
                for (int j = 0; j < commparaInfos9211.size(); j++) {
                    String paramCode = commparaInfos9211.getData(j).getString("PARAM_CODE");
                    String paraCode10 = commparaInfos9211.getData(j).getString("PARA_CODE10");
                    if (discntCode.equals(paramCode)) {
                        if ("1".equals(paraCode10)) {
                            //回单立即生效,有效期1年
                            TradeDiscntInfoQry.updateStartEndDate(tradeId,discnts.getData(i).getString("INST_ID")
                                    ,SysDateMgr.getSysTime(),SysDateMgr.getAddMonthsLastDay(12,SysDateMgr.getSysTime()));
                        } else if ("2".equals(paraCode10)) {
                            //回单立即失效//修复智能组网调测费活动 回单修改startDate为竣工时间 修改remark
                            String startDate = discnts.getData(i).getString("START_DATE");
                            String finishDate = SysDateMgr.getSysTime();
                            String updateRemark = "受理时间:" + startDate + ",竣工时间:" + finishDate;
                            TradeDiscntInfoQry.updateStartEndDateAndRemark(tradeId, discnts.getData(i).getString("INST_ID")
                                    , finishDate, finishDate, updateRemark);
                        }else if ("3".equals(paraCode10)) {
                            //回单立即失效 原调测包规则 已不用
                            TradeDiscntInfoQry.updateEndDate(tradeId, SysDateMgr.getSysTime(), discnts.getData(i).getString("DISCNT_CODE"));
                        } else {
                            //原有逻辑
                            TradeDiscntInfoQry.updateEndDate(tradeId, SysDateMgr.getAddMonthsLastDay(0), discnts.getData(i).getString("DISCNT_CODE"));
                        }
                        break;
                    }
                }
            }
        }

        //中间存折转正式存折
        String allFee = mainTrade.getString("RSRV_STR1", "0");
        String SerialNumber = mainTrade.getString("SERIAL_NUMBER");
        //REQ202003240027  优化智能组网资费的需求—BOSS侧
        //按月套餐不走存折转账方式
        IDataset othersTradeInfos = TradeOtherInfoQry.getTradeOtherByTradeId(tradeId);
        if (IDataUtil.isEmpty(othersTradeInfos)) {
            return;
        }

        for (int i = 0; i < othersTradeInfos.size(); i++) {
            String rsrvValueCode = othersTradeInfos.getData(i).getString("RSRV_VALUE_CODE", "");//
            if(!"ZNZW_ACCT_IN".equals(rsrvValueCode)){
                continue;
            }
            String rsrvNum1 = othersTradeInfos.getData(i).getString("RSRV_NUM1", "0");// RSRV_NUM1 = 费用
            if ("0".equals(rsrvNum1)) {
                continue;
            }
            String rsrvStr4 = othersTradeInfos.getData(i).getString("RSRV_STR4", "");//优惠编码
            for (int j = 0; j < commparaInfos9211.size(); j++) {
                if(rsrvStr4.equals(commparaInfos9211.getData(j).getString("PARAM_CODE"))){
                    IData params = new DataMap();
                    params.put("OUTER_TRADE_ID", SeqMgr.getTradeId());
                    params.put("SERIAL_NUMBER", SerialNumber);
                    params.put("DEPOSIT_CODE_OUT", commparaInfos9211.getData(j).getString("PARA_CODE6"));//中间存折
                    params.put("DEPOSIT_CODE_IN", commparaInfos9211.getData(j).getString("PARA_CODE7"));//正式存折
                    params.put("TRADE_FEE", Integer.parseInt(rsrvNum1));
                    params.put("CHANNEL_ID", "15000");
                    params.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                    params.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                    params.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
                    params.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                    params.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                    if (StringUtils.isNotBlank(params.getString("TRADE_FEE")) && StringUtils.isNotBlank(allFee)) {
                        //调用接口，将【现金类】——>【押金】
                        IData resultData = AcctCall.transFeeInADSL(params);

                        if (log.isDebugEnabled()) {
                            log.debug("调用AM_CRM_TransFeeInADSL 接口返回结果:" + resultData);
                        }

                        String result = resultData.getString("RESULT_CODE", "");

                        if ("".equals(result) || !"0".equals(result)) {
                            CSAppException.appError("61312", "调用接口AM_CRM_TransFeeInADSL转存押金入参：" + params + "错误:" + resultData.getString("RESULT_INFO"));
                        }
                    }
                    break;
                }
            }
        }


    }

}
