package com.asiainfo.veris.crm.order.soa.person.rule.run.createuser;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * 客户证件是集团证件时，经办人使用人属于必填项，检查这2项是否都有值
 * 
 * liquan5 20170113 
 * */
public class CheckAgentAndUse extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckAgentAndUse.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        // 单位证明="D" ||  营业执照=="E" ||  事业单位法人证书=="G" ||  社会团体法人登记证书=="L" ||  组织机构代码证=="M")
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckAgentAndUse() >>>>>>>>>>>>>>>>>>");

        logger.error("CheckAgentAndUse30:  " + databus);

        logger.error("CheckAgentAndUse29:  " + databus.getString("SERIAL_NUMBER"));
        IDataset tradeDs = databus.getDataset("TF_B_TRADE");
        if (tradeDs != null && tradeDs.size() > 0) {

            IData trade = tradeDs.getData(0);
            logger.error("CheckAgentAndUse.java   33xxxxxxxxxxxxxxxxxxxxxx:  " + trade);
            //如该笔操作来自批量任务的“MODIFYGROUPPSPTINFO”(以单位证件开户的开户证件变更)，则不进行校验。 TF_B_TRADE的batch_id  对应 TF_B_TRADE_BATDEAL表的 operate_id
            if (trade.getString("BATCH_ID", "").trim().length() > 0) {
                IData cond = new DataMap();
                cond.put("OPERATE_ID", trade.getString("BATCH_ID", "").trim());
                logger.error("CheckAgentAndUse.java   45xxxxxxxxxxxxxxxxxxxxxx:  " + cond);
                IDataset ds = Dao.qryByCode("TF_B_TRADE_BATDEAL", "SEL_BY_OPERATEID", cond, Route.getJourDb(Route.CONN_CRM_CG));
                logger.error("CheckAgentAndUse.java   46xxxxxxxxxxxxxxxxxxxxxx:  " + ds);
                if (ds != null && ds.size() > 0) {  
                    logger.error("CheckAgentAndUse.java   48xxxxxxxxxxxxxxxxxxxxxx:  " + ds.getData(0));
                    String batchOperType = ds.getData(0).getString("BATCH_OPER_TYPE", "").trim();
                    if (batchOperType.equals("MODIFYGROUPPSPTINFO")) {
                        logger.error("CheckAgentAndUse.java   51xxxxxxxxxxxxxxxxxxxxxx:  ");
                        return false;
                    }
                }
            }

            if (!trade.getString("BRAND_CODE", "").trim().equals("PWLW") && !tradeDs.getData(0).getString("BRAND_CODE", "").trim().equals("G005")) {//不是物联网和行业应用卡
                IDataset customer = databus.getDataset("TF_B_TRADE_CUSTOMER");
                logger.error("CheckAgentAndUse.java   35xxxxxxxxxxxxxxxxxxxxxx:  " + customer.size());
                if (customer != null && customer.size() > 0) {
                    IData data = customer.getData(0);
                    String pspt_type_code = data.getString("PSPT_TYPE_CODE", "").trim();//开户人证件类型
                    if (pspt_type_code.equals("D") || pspt_type_code.equals("E") || pspt_type_code.equals("G") || pspt_type_code.equals("L") || pspt_type_code.equals("M  ")) {
                        String message = "";
                        //select t.*, rowid from   TF_B_TRADE_CUST_PERSON    t  ;  --使用人 5678 名型号址
                        //select t.*, rowid from   TF_B_TRADE_CUSTOMER       t  ;  --经办人 789(10)  名型号址

                        String rsrv7agent = data.getString("RSRV_STR7", "").trim();//姓名
                        String rsrv8agent = data.getString("RSRV_STR8", "").trim();//证件类型
                        String rsrv9agent = data.getString("RSRV_STR9", "").trim();//证件号码
                        String rsrv10agent = data.getString("RSRV_STR10", "").trim();//地址
                        logger.error("CheckAgentAndUse.java 46" + rsrv7agent + "  " + rsrv8agent + " " + rsrv9agent + " " + rsrv10agent);
                        if (rsrv7agent.length() == 0 || rsrv8agent.length() == 0 || rsrv9agent.length() == 0 || rsrv10agent.length() == 0) {
                            logger.error("CheckAgentAndUse.java 50");
                            message = "经办人";
                        }

                        IDataset custperson = databus.getDataset("TF_B_TRADE_CUST_PERSON");
                        if (custperson != null && custperson.size() > 0) {
                            String rsrv5use = custperson.getData(0).getString("RSRV_STR5", "").trim();//姓名
                            String rsrv6use = custperson.getData(0).getString("RSRV_STR6", "").trim();//证件类型
                            String rsrv7use = custperson.getData(0).getString("RSRV_STR7", "").trim();//证件号码
                            String rsrv8use = custperson.getData(0).getString("RSRV_STR8", "").trim();//地址
                            logger.error("CheckAgentAndUse.java 58" + rsrv5use + "  " + rsrv6use + " " + rsrv7use + " " + rsrv8use);
                            if (rsrv5use.length() == 0 || rsrv6use.length() == 0 || rsrv7use.length() == 0 || rsrv8use.length() == 0) {
                                logger.error("CheckAgentAndUse.java 61");
                                if (message.length() > 0) {
                                    message += "、使用人";
                                } else {
                                    message += "使用人";
                                }
                            }
                        }
                        if (message.trim().length() > 0) {
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20170113, "证件类型选择集团证件时，" + message + "的各项信息须填写！");
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
