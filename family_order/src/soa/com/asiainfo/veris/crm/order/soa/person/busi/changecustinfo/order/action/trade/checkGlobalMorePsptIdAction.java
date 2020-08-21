package com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo.order.action.trade;

import java.util.List;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.CreatePersonUserBean;

public class checkGlobalMorePsptIdAction implements ITradeAction
{
    private static Logger logger = Logger.getLogger(checkGlobalMorePsptIdAction.class);

    // 为弥补因前台js未做更新而直接提交到后台的少数错误请求数据，增加该类进行全国一证多号判断
    public void executeAction(BusiTradeData btd) throws Exception
    {
        boolean checkGlobalPspt = true;
        logger.error("checkGlobalMorePsptIdAction.java   25xxxxxxxxxxxxxxxxxxxxxx:  " + btd);

        List<OtherTradeData> otherInfos = btd.get("TF_B_TRADE_OTHER");
        if (otherInfos != null && otherInfos.size() > 0) {
            for (int i = 0; i < otherInfos.size(); i++) {
                String rsrvValueCode = otherInfos.get(i).getRsrvValueCode();
                logger.error("checkGlobalMorePsptIdAction.java40xxxxxxxxxxxxxxxxxxxxxx:  " + rsrvValueCode);
                if (rsrvValueCode != null && rsrvValueCode.trim().equals("HYYYKBATCHOPEN")) {
                    checkGlobalPspt = false;                    
                }
            }
        }
        logger.error("checkGlobalMorePsptIdAction.java44xxxxxxxxxxxxxxxxxxxxxx: " + checkGlobalPspt);

        MainTradeData tradeInfo = (MainTradeData) btd.get("TF_B_TRADE").get(0);
        String brandCode = tradeInfo.getBrandCode();
        if (brandCode!=null&&!brandCode.equals("G001") && !brandCode.equals("G002") && !brandCode.equals("G010") && !brandCode.equals("G005")) {
            checkGlobalPspt = false;
            logger.error("checkGlobalMorePsptIdAction.java50xxxxxxxxxxxxxxxxxxxxxx: " + checkGlobalPspt);            
        }
        logger.error("checkGlobalMorePsptIdAction.java53xxxxxxxxxxxxxxxxxxxxxx: " + checkGlobalPspt);

        if (checkGlobalPspt) {
            String tradeTypeCode = btd.getTradeTypeCode();
            List<CustomerTradeData> customerInfos = btd.get("TF_B_TRADE_CUSTOMER");
            List<CustPersonTradeData> custpersonInfos = btd.get("TF_B_TRADE_CUST_PERSON");
            List<UserTradeData> userInfos = btd.get("TF_B_TRADE_USER");

            if (userInfos != null && userInfos.size() > 0) {
                String userType = userInfos.get(0).getUserTypeCode();
                System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxx67CUST_TYPE="+userType);
                if(userType!=null&&userType.trim().equals("A")){//如是测试机用户， 开户人、使用人、经办人证件都不进行全国一证多号校验               
                    System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxx69CUST_TYPE="+userType);
                    return ;
                }
                System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxx72CUST_TYPE="+userType);   
            }
            String custName = "";
            String psptTypeCode = "";
            String psptId = "";

            if (customerInfos != null && customerInfos.size() > 0) {
 
                
                custName = customerInfos.get(0).getCustName();
                psptTypeCode = customerInfos.get(0).getPsptTypeCode();
                psptId = customerInfos.get(0).getPsptId();
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

            if ("60".equals(tradeTypeCode)) { // 客户资料变更
                List<MainTradeData> mainTradeInfos = btd.get("TF_B_TRADE");
                if (mainTradeInfos != null && mainTradeInfos.size() > 0) {
                    data.put("TRADE_TYPE_CODE", tradeTypeCode);
                    data.put("SERIAL_NUMBER", mainTradeInfos.get(0).getSerialNumber());
                    if ("D".equals(psptTypeCode) || "E".equals(psptTypeCode) || "G".equals(psptTypeCode) || "L".equals(psptTypeCode) || "M".equals(psptTypeCode)) {
                        //集团类型证件校验使用人一证五号
                        if (checkGlobalPspt && StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(userPsptTypeCode) && StringUtils.isNotBlank(userPsptId)) {
                            //BUG20191217092258  关于号码激活后再次修改客户资料信息时，没有进行一证五号校验和人像比对校验的问题 start by liangdg3
                            //客户资料变更使用人校验  未修改使用人身份证信息,只是变更其他资料,不做一证五号平台校验,且拦截返回code1和2状态
                            //原未做是否修改身份证信息判断,导致无法判断是否为修改身份证信息,不拦截返回code为2状态导致一证超5号
                            IDataset custPerson = CustPersonInfoQry.getPerInfoByCustId(custpersonInfos.get(0).getCustId());
                            String oldUserPsptTypeCode="";
                            String oldUserPsptId="";
                            if(IDataUtil.isNotEmpty(custPerson)){
                                oldUserPsptTypeCode = custPerson.first().getString("RSRV_STR6");
                                oldUserPsptId = custPerson.first().getString("RSRV_STR7");
                            }
                            if(!userPsptTypeCode.equals(oldUserPsptTypeCode)||!userPsptId.equals(oldUserPsptId)){
                                data.put("CUST_NAME", userName);
                                data.put("PSPT_TYPE_CODE", userPsptTypeCode);
                                data.put("PSPT_ID", userPsptId);
                                data.put("BRAND_CODE", brandCode);

                                CreatePersonUserBean bean = (CreatePersonUserBean) BeanManager.createBean(CreatePersonUserBean.class);
                                IDataset dataset = bean.checkGlobalMorePsptId(data);
                                IData result = dataset.getData(0);

                                if (result.getString("CODE", "").equals("1")||result.getString("CODE", "").equals("2")) {// 不合法 原只拦截1,改为拦截1和2
                                    CSAppException.apperr(CrmCommException.CRM_COMM_103, result.getString("MSG", ""));
                                }
                            }
                            //BUG20191217092258  关于号码激活后再次修改客户资料信息时，没有进行一证五号校验和人像比对校验的问题 end
                        }

                    }else{
                        //个人类型证件校验开户证件
                        if (StringUtils.isNotBlank(custName) && StringUtils.isNotBlank(psptTypeCode) &&StringUtils.isNotBlank(psptId)) {
                            //BUG20191217092258  关于号码激活后再次修改客户资料信息时，没有进行一证五号校验和人像比对校验的问题 start by liangdg3
                            //客户资料变更身份证校验 未修改身份证信息,只是变更其他资料,不做一证五号平台校验,且拦截返回code1和2状态
                            //原未做是否修改身份证信息判断,导致无法判断是否为修改身份证信息,不拦截返回code为2状态导致一证超5号
                            IDataset customer = CustomerInfoQry.getCustInfoByCustIdPk(customerInfos.get(0).getCustId());
                            String oldPsptTypeCode="";
                            String oldPsptId="";
                            if(IDataUtil.isNotEmpty(customer)){
                                oldPsptTypeCode = customer.first().getString("PSPT_TYPE_CODE");
                                oldPsptId = customer.first().getString("PSPT_ID");
                            }
                            if(!psptId.equals(oldPsptId)||!psptTypeCode.equals(oldPsptTypeCode)){
                                data.put("CUST_NAME", custName);
                                data.put("PSPT_TYPE_CODE", psptTypeCode);
                                data.put("PSPT_ID", psptId);
                                data.put("BRAND_CODE", brandCode);

                                CreatePersonUserBean bean = (CreatePersonUserBean) BeanManager.createBean(CreatePersonUserBean.class);
                                IDataset dataset = bean.checkGlobalMorePsptId(data);
                                IData result = dataset.getData(0);

                                if (result.getString("CODE", "").equals("1")||result.getString("CODE", "").equals("2")) {// 不合法  原只拦截1,改为拦截1和2
                                    CSAppException.apperr(CrmCommException.CRM_COMM_103, result.getString("MSG", ""));
                                }
                            }
                            //BUG20191217092258  关于号码激活后再次修改客户资料信息时，没有进行一证五号校验和人像比对校验的问题 end
                        }
                    }
                }
            } else if ("10".equals(tradeTypeCode) || "100".equals(tradeTypeCode) || "40".equals(tradeTypeCode)) { // 开户、过户、携转入户

                data.clear();
                /**
                 * 物联卡批量任务报错，提示为：接口调用处理失败
                 * @author zhuoyingzhi
                 * @date 20170606
                 */
                List<MainTradeData> mainTrades = btd.getTradeDatas(TradeTableEnum.TRADE_MAIN);
                MainTradeData mainData = mainTrades.get(0);
                //不验证物联网用户
                if ("PWLW".equals(mainData.getBrandCode())) {
                    return;
                }
                /********************end****************************/

                if (!"".equals(custName) && !"".equals(psptTypeCode) && !"".equals(psptId)) {
                    data.put("CUST_NAME", custName);
                    data.put("PSPT_TYPE_CODE", psptTypeCode);
                    data.put("PSPT_ID", psptId);
					data.put("BRAND_CODE", brandCode);

                    CreatePersonUserBean bean = (CreatePersonUserBean) BeanManager.createBean(CreatePersonUserBean.class);
                    IDataset dataset = bean.checkGlobalMorePsptId(data);
                    IData result = dataset.getData(0);

                    if (result.getString("CODE", "").equals("1")) {// 不合法
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, result.getString("MSG", ""));
                    }
                }

                if (checkGlobalPspt && !"".equals(userName) && !"".equals(userPsptTypeCode) && !"".equals(userPsptId)) {
                    data.put("CUST_NAME", userName);
                    data.put("PSPT_TYPE_CODE", userPsptTypeCode);
                    data.put("PSPT_ID", userPsptId);
					data.put("BRAND_CODE", brandCode);

                    CreatePersonUserBean bean = (CreatePersonUserBean) BeanManager.createBean(CreatePersonUserBean.class);
                    IDataset dataset = bean.checkGlobalMorePsptId(data);
                    IData result = dataset.getData(0);
                    if (IDataUtil.isEmpty(result)) {
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "校验【全网一证多号】出现异常，请联系系统管理员！" + userName + "|" + userPsptId + "|" + userPsptTypeCode);
                    }

                    if (result.getString("CODE", "").equals("1")) {// 不合法
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, result.getString("MSG", ""));
                    }

                }
            }
        }
    }
}
