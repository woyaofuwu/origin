package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.finish;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;

public class SynCreditScoreSvcAction implements ITradeFinishAction {

    private static transient Logger logger = Logger.getLogger(SynCreditScoreSvcAction.class);

    @Override
    public void executeAction(IData mainTrade) throws Exception {
        IDataset commparaList = CommparaInfoQry.getCommparaAllCol("CSM", "2001", "XINYF_SVC", CSBizBean.getUserEparchyCode());
        if (IDataUtil.isEmpty(commparaList)) {
            return;
        }

        String creditSvc = commparaList.getData(0).getString("PARA_CODE2");//信用停机保障 信用分服务

        String tradeId = mainTrade.getString("TRADE_ID");
        String userId = mainTrade.getString("USER_ID");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String inModeCode = mainTrade.getString("IN_MODE_CODE");
        String remark = mainTrade.getString("REMARK");

        IDataset svcs = TradeSvcInfoQry.getTradeSvcByTradeId(tradeId);


        if (IDataUtil.isNotEmpty(svcs)) {
            for (int i = 0; i < svcs.size(); i++) {
                IData svc = svcs.getData(i);

                if (svc.getString("SERVICE_ID").equals(creditSvc)) {
                    String creditClass = null;
                    String syncType = null;
                    String releInstId = svc.getString("INST_ID");
                    String startDate = null;
                    String endDate = null;
                    String svcModifyTag = svc.getString("MODIFY_TAG");


                    if (StringUtils.equals(svcModifyTag, BofConst.MODIFY_TAG_ADD) || StringUtils.equals(svcModifyTag, BofConst.MODIFY_TAG_UPD)) {
                        IDataset tradeAttrInfos = TradeAttrInfoQry.getTradeAttrByTradeIDandAttrCode(tradeId, "CREDIT_CLASS", null);
                        if (IDataUtil.isEmpty(tradeAttrInfos)) {
                            return;
                        } else {
                            String attrModifyTag = tradeAttrInfos.getData(0).getString("MODIFY_TAG");
                            if (StringUtils.equals(svcModifyTag, BofConst.MODIFY_TAG_ADD) && StringUtils.equals(attrModifyTag, BofConst.MODIFY_TAG_ADD)) {
                                syncType = "1";//1：信用停机保障申请
                                creditClass = tradeAttrInfos.getData(0).getString("ATTR_VALUE");
                                startDate = tradeAttrInfos.getData(0).getString("START_DATE");
                                endDate = tradeAttrInfos.getData(0).getString("END_DATE");
                            } else if (StringUtils.equals(svcModifyTag, BofConst.MODIFY_TAG_UPD) && StringUtils.equals(attrModifyTag, BofConst.MODIFY_TAG_UPD)) {
                                syncType = "2";//2：信用分档变更
                                creditClass = tradeAttrInfos.getData(0).getString("ATTR_VALUE");
                                startDate = tradeAttrInfos.getData(0).getString("START_DATE");
                                endDate = tradeAttrInfos.getData(0).getString("END_DATE");
                            } else {
                                return;
                            }
                        }
                    } else if (StringUtils.equals(svcModifyTag, BofConst.MODIFY_TAG_DEL)) {
                        IDataset tradeAttrInfos = TradeAttrInfoQry.getTradeAttrByTradeIDandAttrCode(tradeId, "CREDIT_CLASS", null);
                        if (!IDataUtil.isEmpty(tradeAttrInfos)) {
                            String attrModifyTag = tradeAttrInfos.getData(0).getString("MODIFY_TAG");
                            if (StringUtils.equals(attrModifyTag, BofConst.MODIFY_TAG_DEL)) {
                                syncType = "0";//0：信用停机保障取消
                                creditClass = tradeAttrInfos.getData(0).getString("ATTR_VALUE");
                                startDate = tradeAttrInfos.getData(0).getString("START_DATE");
                                endDate = tradeAttrInfos.getData(0).getString("END_DATE");
                            }
                        } else {
                            IData userAttrData = UserAttrInfoQry.getUserAttrByRelaInstIdAndAttrCode(userId, releInstId, "CREDIT_CLASS", null);
                            syncType = "0";//0：信用停机保障取消
                            creditClass = userAttrData.getString("ATTR_VALUE");
                            startDate = userAttrData.getString("START_DATE");
                            endDate = userAttrData.getString("END_DATE");
                        }
                    }

                    if (StringUtils.isNotBlank(syncType)) {
                        IData param = new DataMap();
                        param.put("SERIAL_NUMBER", serialNumber);
                        param.put("TYPE", syncType);
                        param.put("CREDIT_CLASS", creditClass);
                        param.put("START_DATE", startDate);
                        param.put("END_DATE", endDate);
                        param.put("IN_MODE_CODE", inModeCode);
                        param.put("REMARK", remark);
                        AcctCall.modifyScoreLevelService(param);
                    }
                    return;
                }
            }
        }
    }
}

