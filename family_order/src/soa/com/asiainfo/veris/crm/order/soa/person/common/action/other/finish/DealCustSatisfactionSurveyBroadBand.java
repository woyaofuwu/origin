package com.asiainfo.veris.crm.order.soa.person.common.action.other.finish;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.person.busi.satisfactionsurvey.CustSatisfactionSurveyBean;
import org.apache.log4j.Logger;
/**
 * Created by zhaohj3 on 2018/2/18.
 */

/**
 * @ClassName: DealCustSatisfactionSurveyBroadBand.java
 * @Description: 客户接触满意度调查请求是指在客户服务结束根据服务场景，触发客户满意度调研的功能。
 * 客户服务实时触发接触场景，包括但不限于营业厅业务办理、10086热线、上门安装宽带等
 * 客户接触完成后，触发调用“客服评价平台”发送满意度调研请求（家庭宽带）
 * @version: v1.0.0
 */
public class DealCustSatisfactionSurveyBroadBand implements ITradeFinishAction {

    private static transient Logger logger = Logger.getLogger(DealCustSatisfactionSurvey.class);

    @Override
    public void executeAction(IData mainTrade) throws Exception {
        logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 DealCustSatisfactionSurveyBroadBand >>>>>>>>>>>>>>>>>>");
        try {
            CustSatisfactionSurveyBean bean = BeanManager.createBean(CustSatisfactionSurveyBean.class);
            bean.sendCustSatisfactionSurveyBroadBand(mainTrade);
        } catch (Exception e) {
            logger.debug("客户满意度调研请求ACTION异常：" + e.getMessage());
        }
        logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 DealCustSatisfactionSurveyBroadBand <<<<<<<<<<<<<<<<<<<");
    }
}
