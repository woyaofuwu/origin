package com.asiainfo.veris.crm.order.soa.person.busi.unitopenchangeactive.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.*;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.CreatePersonUserBean;
import org.apache.log4j.Logger;

import java.util.List;

public class checkGlobalMorePsptIdUnitAction implements ITradeAction
{
    private static Logger logger = Logger.getLogger(checkGlobalMorePsptIdUnitAction.class);

    // 为弥补因前台js未做更新而直接提交到后台的少数错误请求数据，增加该类进行全国一证多号判断
    public void executeAction(BusiTradeData btd) throws Exception
    {
        boolean checkGlobalPspt = true;
        logger.error("checkGlobalMorePsptIdUnitAction.java   25xxxxxxxxxxxxxxxxxxxxxx:  " + btd);

        List<OtherTradeData> otherInfos = btd.get("TF_B_TRADE_OTHER");
        if (otherInfos != null && otherInfos.size() > 0) {
            for (int i = 0; i < otherInfos.size(); i++) {
                String rsrvValueCode = otherInfos.get(i).getRsrvValueCode();
                logger.error("checkGlobalMorePsptIdUnitAction.java40xxxxxxxxxxxxxxxxxxxxxx:  " + rsrvValueCode);
                if (rsrvValueCode != null && rsrvValueCode.trim().equals("HYYYKBATCHOPEN")) {
                    checkGlobalPspt = false;                    
                }
            }
        }
        logger.error("checkGlobalMorePsptIdUnitAction.java44xxxxxxxxxxxxxxxxxxxxxx: " + checkGlobalPspt);

        MainTradeData tradeInfo = (MainTradeData) btd.get("TF_B_TRADE").get(0);
        String brandCode = tradeInfo.getBrandCode();
        if (brandCode!=null&&!brandCode.equals("G001") && !brandCode.equals("G002") && !brandCode.equals("G010") && !brandCode.equals("G005")) {
            checkGlobalPspt = false;
            logger.error("checkGlobalMorePsptIdUnitAction.java50xxxxxxxxxxxxxxxxxxxxxx: " + checkGlobalPspt);
        }
        logger.error("checkGlobalMorePsptIdUnitAction.java53xxxxxxxxxxxxxxxxxxxxxx: " + checkGlobalPspt);

        if (checkGlobalPspt) {
            String tradeTypeCode = btd.getTradeTypeCode();
            List<CustomerTradeData> customerInfos = btd.get("TF_B_TRADE_CUSTOMER");
            List<CustPersonTradeData> custpersonInfos = btd.get("TF_B_TRADE_CUST_PERSON");
            List<UserTradeData> userInfos = btd.get("TF_B_TRADE_USER");

            if (userInfos != null && userInfos.size() > 0) {
                String userType = userInfos.get(0).getUserTypeCode();
                logger.error("checkGlobalMorePsptIdUnitAction.java67CUST_TYPE="+userType);
                if(userType!=null&&userType.trim().equals("A")){//如是测试机用户， 开户人、使用人、经办人证件都不进行全国一证多号校验               
                    logger.error("checkGlobalMorePsptIdUnitAction.java69CUST_TYPE="+userType);
                    return ;
                }
                logger.error("checkGlobalMorePsptIdUnitAction.java72CUST_TYPE="+userType);
            }
            String userName = "";
            String userPsptTypeCode = "";
            String userPsptId = "";
            if (custpersonInfos != null && custpersonInfos.size() > 0) {
                userName = custpersonInfos.get(0).getRsrvStr5();
                userPsptTypeCode = custpersonInfos.get(0).getRsrvStr6();
                userPsptId = custpersonInfos.get(0).getRsrvStr7();
            }

            IData data = new DataMap();

            if ("494".equals(tradeTypeCode)) { // 单位证件开户激活
                List<MainTradeData> mainTradeInfos = btd.get("TF_B_TRADE");
                if (mainTradeInfos != null && mainTradeInfos.size() > 0) {
                    data.put("TRADE_TYPE_CODE", tradeTypeCode);
                    data.put("SERIAL_NUMBER", mainTradeInfos.get(0).getSerialNumber());


                    data.put("CUST_NAME", userName);
                    data.put("PSPT_TYPE_CODE", userPsptTypeCode);
                    data.put("PSPT_ID", userPsptId);
                    data.put("BRAND_CODE", brandCode);

                    CreatePersonUserBean bean = (CreatePersonUserBean) BeanManager.createBean(CreatePersonUserBean.class);
                    IDataset dataset = bean.checkGlobalMorePsptId(data);
                    IData result = dataset.getData(0);

                    if (result.getString("CODE", "").equals("1")) {// 不合法
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, result.getString("MSG", ""));
                    }
                }
            }
        }
    }
}
