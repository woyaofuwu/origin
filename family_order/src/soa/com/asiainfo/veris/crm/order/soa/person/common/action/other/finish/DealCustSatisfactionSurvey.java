package com.asiainfo.veris.crm.order.soa.person.common.action.other.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.satisfactionsurvey.CustSatisfactionSurveyBean;
import org.apache.log4j.Logger;
/**
 * Created by zhaohj3 on 2018/12/19.
 */

/**
 * @ClassName: DealCustSatisfactionSurvey.java
 * @Description: 客户接触满意度调查请求是指在客户服务结束根据服务场景，触发客户满意度调研的功能。
 * 客户服务实时触发接触场景，包括但不限于营业厅业务办理、10086热线、上门安装宽带等
 * 客户接触完成后，触发调用“客服评价平台”发送满意度调研请求
 * @version: v1.0.0
 */
public class DealCustSatisfactionSurvey implements ITradeFinishAction {

    private static transient Logger logger = Logger.getLogger(DealCustSatisfactionSurvey.class);

    @Override
    public void executeAction(IData mainTrade) throws Exception {
        logger.info(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 DealCustSatisfactionSurvey >>>>>>>>>>>>>>>>>>");
        try {
            CustSatisfactionSurveyBean bean = BeanManager.createBean(CustSatisfactionSurveyBean.class);
            String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
            String eparchyCode = mainTrade.getString("EPARCHY_CODE");
            String tradeDepartId = mainTrade.getString("TRADE_DEPART_ID");
            String tradeStaffId = mainTrade.getString("TRADE_STAFF_ID","");
            String inModeCode = mainTrade.getString("IN_MODE_CODE","");
            String rsrvStr1 = mainTrade.getString("RSRV_STR1","");
            String batchId = mainTrade.getString("BATCH_ID","");
            if(!"0".equals(inModeCode)){
                logger.info(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< nosendsmsWuHao5DealCustSatisfactionSurvey1 <<<<<<<<<<<<<<<<<<<");
                return;
            }
            //不发送满意度短信业务类型修改为配置@tanzheng@20100120
            IData param = new DataMap();
            param.put("SUBSYS_CODE", "CSM");
            param.put("PARAM_ATTR", "9045");
            param.put("PARAM_CODE", "0");
            IDataset dataset = CommparaInfoQry.getCommparaInfoByPara(param);
            if(IDataUtil.isNotEmpty(dataset)){
                String paraCode20 = dataset.first().getString("PARA_CODE20");
                logger.debug("DealCustSatisfactionSurvey>>tz>>>>"+paraCode20);
                String tradeTypeCode1 = "|"+tradeTypeCode+"|";
                if(paraCode20.contains(tradeTypeCode1)){
                    logger.debug("DealCustSatisfactionSurvey>>tz>>>>返回");
                    return;
                }
            }



            if (StringUtils.isNotBlank(batchId) && ("1522".equals(tradeTypeCode) || "6019".equals(tradeTypeCode)))
            {
            	logger.info("==DealCustSatisfactionSurvey:return");
                return; //集团产品特殊优惠变更批量业务不发送
            }

            // 一级客服系统集中化服务评价业务——需要调用客户满意度调研请求的渠道类型配置
            IDataset departIdConfig = CommparaInfoQry.getCommparaInfoByCode("CSM", "9544", "SATISFY_DEPART_ID", tradeDepartId, eparchyCode);

            if (IDataUtil.isEmpty(departIdConfig)) {
                return;
            }

            // 根据订单号查询客户满意度调研请求日志记录，如果此订单存在成功发送满意度请求的日志记录则无需再次发送。
            IDataset regLogs = bean.querySurveyCheckRegLog(mainTrade);
            if (IDataUtil.isNotEmpty(regLogs)) {
                for (int i = 0; i < regLogs.size(); i++) {
                    IData regLog = regLogs.getData(i);
                    String rspCode = regLog.getString("RSP_CODE");
                    if ("00000".equals(rspCode)) {
                        return;
                    }
                }
            }
            IDataset satisfyConfig = CommparaInfoQry.getCommparaInfoByCode("CSM", "9544", "SATISFY_TRADETYPE", tradeTypeCode, eparchyCode);
            IDataset switchConfig = CommparaInfoQry.getCommparaInfoByCode("CSM", "9544", "SATISFY_SWITCH", "1", eparchyCode);
            if (IDataUtil.isNotEmpty(satisfyConfig)) {
                if("240".equals(tradeTypeCode)){
                	//不发短信的活动配置
                    IDataset activeConfig = CommparaInfoQry.getCommparaInfoByCode("CSM", "9934", "NO_SEND_SMS", rsrvStr1, eparchyCode);
                    if(IDataUtil.isNotEmpty(activeConfig) && activeConfig.size() > 0){
                    	logger.info(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< nosendsmsWuHao5DealCustSatisfactionSurvey <<<<<<<<<<<<<<<<<<<");
                    	return;
                    }
                }
				IData config = satisfyConfig.getData(0);
				String staff_id = config.getString("PARA_CODE5");
				if(tradeStaffId.equals(staff_id)){
					bean.sendCustSatisfactionSurvey(mainTrade);
				}
				if(IDataUtil.isNotEmpty(switchConfig))
				{
					bean.sendCustSatisfactionSurvey(mainTrade);
				}
            }
        } catch (Exception e) {
            logger.error("客户满意度调研请求ACTION异常：" + e.getMessage());
        }
        logger.info(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 DealCustSatisfactionSurvey <<<<<<<<<<<<<<<<<<<");
    }
}
