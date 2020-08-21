package com.asiainfo.veris.crm.order.soa.person.common.action.other.finish;

import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.satisfactionsurvey.CustSatisfactionSurveyBean;
/**
 * 企业专线场景客户满意度调研请求
 * REQ201909290018_(集团全网)关于企业宽带、企业专线业务接入场景评测的改造通知
 * 2019-10-17 11:18:15
 * @author xuzh5
 */
public class DealGroupSpecialLineAction implements ITradeFinishAction {
    private static transient Logger logger = Logger.getLogger(DealGroupSpecialLineAction.class);
    @Override
    public void executeAction(IData mainTrade) throws Exception {
        logger.info(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 DealGroupSpecialLineAction >>>>>>>>>>>>>>>>>>");
        try {
        	logger.info("DealGroupSpecialLineAction-trade_id:"+mainTrade.getString("TRADE_ID"));
            CustSatisfactionSurveyBean bean = BeanManager.createBean(CustSatisfactionSurveyBean.class);

            String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
            String batchId = mainTrade.getString("BATCH_ID","");
            if (StringUtils.isNotBlank(batchId))
            {
                logger.info("==DealGroupSpecialLineAction:return");
                return;
            }

            //不发送配置
            IData param = new DataMap();
            param.put("SUBSYS_CODE", "CSM");
            param.put("PARAM_ATTR", "9046");
            param.put("PARAM_CODE", "0");
            IDataset dataset = CommparaInfoQry.getCommparaInfoByPara(param);
            if(IDataUtil.isNotEmpty(dataset)){
                String paraCode20 = dataset.first().getString("PARA_CODE20");
                String tradeTypeCode1 = "|"+tradeTypeCode+"|";
                if(paraCode20.contains(tradeTypeCode1)){
                    logger.info("==DealGroupSpecialLineAction:return");
                    return;
                    }
            }


            // 根据订单号查询客户满意度调研请求日志记录，如果此订单存在成功发送满意度请求的日志记录则无需再次发送。
            IDataset regLogs = bean.querySurveyCheckRegLog(mainTrade);
            if (IDataUtil.isNotEmpty(regLogs)) {
                for (int i = 0; i < regLogs.size(); i++) {
                    IData regLog = regLogs.getData(i);
                    String rspCode = regLog.getString("RSP_CODE");
                    if ("0000".equals(rspCode)) {
                        return;
                    }
                }
            }
            IDataset productIdInfo=TradeProductInfoQry.getESOPTradeProductByTradeId(mainTrade);
            if(IDataUtil.isNotEmpty(productIdInfo)){
                IDataset satisfyConfig = CommparaInfoQry.getCommparaByCodeCode1("CSM", "5313", "GroupSpecialLine",productIdInfo.getData(0).getString("PRODUCT_ID",""));
                if (IDataUtil.isNotEmpty(satisfyConfig)) {
                mainTrade.put("PRODUCT_TYPE", satisfyConfig.getData(0).getString("PARA_CODE2",""));
                //调用 企业宽带专线场景客户满意度调研请求接口
    			bean.sendGroupSpecialLine(mainTrade);
                }
            }
            
        } catch (Exception e) {
            logger.error("DealGroupSpecialLineAction客户满意度调研请求ACTION异常：" + e.getMessage());
        }
        logger.info(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 DealGroupSpecialLineAction <<<<<<<<<<<<<<<<<<<");
    }
}
