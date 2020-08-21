package com.asiainfo.veris.crm.order.soa.person.busi.wirelessphone.createuser.order.action;

import com.ailk.bizcommon.set.util.DataSetUtils;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizservice.base.CSAppCall;
import com.ailk.bizservice.query.UcaInfoQry;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeCustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeCustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

/**
 * REQ201703090004 关于铁通商务电话8位和11位资料即时同步的需求
 * 
*1、 界面路径：铁通 > 个人业务 > 商务电话业务 > 商务电话开户；
2、  在UCR_CEN1.TD_B_TRADEACTION 新增一个类，用于修改开户号码对应的157号码的资料；
-- 0898的号码 对应的 157 号码 ，，是从 tf_f_user_res 这里查找
3、  涉及修改的表TF_F_CUSTOMER 、TF_F_CUST_PERSON、TF_F_USER_PSPT；
4、  具体修改的字段：
1）  TF_F_CUSTOMER： CUST_NAME、PSPT_TYPE_CODE、PSPT_ID、IS_REAL_NAME（这个字段要改为1）、RSRV_STR7、RSRV_STR8、RSRV_STR9、RSRV_STR10；
这些字段对应改成开户号码（即0898打头的固话号码）TF_F_CUSTOMER表的字段；
2）TF_F_CUST_PERSON
a.cust_name,a.pspt_type_code,a.pspt_id,a.pspt_addr,a.post_address,a.phone,
a.contact,a.contact_phone,a.rsrv_str5 ,a.rsrv_str6 ,rsrv_str7 ,a.rsrv_str8
这些字段对应改成开户号码（即0898打头的固话号码）TF_F_CUST_PERSON表的字段；
3）  TF_F_USER_PSPT ：CUST_NAME、PSPT_TYPE_CODE、PSPT_ID、RSRV_STR2（证件地址）
这些字段对应改成开户号码（即0898打头的固话号码）TF_F_CUST_PERSON表的字段；
    4）注意：以上修改的资料要同步到账务
*
*1、 界面路径：铁通 > 个人业务 > 其它业务 > 资料变更类 > 客户资料变更(铁通)；
2、  这个界面办理的用户涉及多个品牌，本需求只处理其中2个品牌的用户（商务电话甲种 TT02 和 商务电话乙种 TT04）
3、  在UCR_CEN1.TD_B_TRADEACTION 新增一个类，用于修改开户号码对应的157号码的资料；
-- 0898的号码 对应的 157 号码 ，，是从 tf_f_user_res 这里查找
3、具体修改的字段：
1）  TF_F_CUSTOMER： CUST_NAME、PSPT_TYPE_CODE、PSPT_ID、IS_REAL_NAME（这个字段要改为1）、RSRV_STR7、RSRV_STR8、RSRV_STR9、RSRV_STR10；
这些字段对应改成开户号码（即0898打头的固话号码）TF_F_CUSTOMER表的字段；
2）TF_F_CUST_PERSON
a.cust_name,a.pspt_type_code,a.pspt_id,a.pspt_addr,a.post_address,a.phone,
a.contact,a.contact_phone,a.rsrv_str5 ,a.rsrv_str6 ,rsrv_str7 ,a.rsrv_str8
这些字段对应改成开户号码（即0898打头的固话号码）TF_F_CUST_PERSON表的字段；
2）  TF_F_USER_PSPT ：CUST_NAME、PSPT_TYPE_CODE、PSPT_ID、RSRV_STR2（证件地址）
这些字段对应改成开户号码（即0898打头的固话号码）TF_F_CUST_PERSON表的字段；
   4）以上修改的资料要同步到账务
4、如用户由原来的非实名变更为实名时，在资料同步的同时，检测157号码是否处于非实名停机状态，如为停机状态，给对应的157号码生成开机工单（普通用户的开机工单）；

*
 */
public class SynMessageToBindPhoneAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        System.out.println("SynMessageToBindPhoneActionxxxxxxxx40 " + mainTrade);
        String tradeId = mainTrade.getString("TRADE_ID", "").trim();
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE", "").trim();
        UcaData uca = UcaDataFactory.getNormalUca(mainTrade.getString("SERIAL_NUMBER", "").trim());
        IDataset resDs = null;
        String bindSerialNumber = "";
        if (tradeTypeCode.equals("9721")) {//商务电话开户
            resDs = TradeResInfoQry.getTradeRes(tradeId, "0", "0");//查询绑定的11位手机号码             
            if (IDataUtil.isNotEmpty(resDs)) {
                bindSerialNumber = resDs.getData(0).getString("RES_CODE", "").trim();
                synMessage(mainTrade, tradeId, bindSerialNumber, "1");//开户都为实名制，即IS_REAL_NAME都=1
            }
        } else if (tradeTypeCode.equals("9726")) {//客户资料变更(铁通)
            String brandCode = uca.getBrandCode();
            if ("TT02".equals(brandCode) || "TT04".equals(brandCode)) {//商务电话甲种 TT02 和 商务电话乙种 TT04
                System.out.println("SynMessageToBindPhoneActionxxxxxxxx70 " + mainTrade);
                resDs = UserResInfoQry.queryUserResByUserIdResType(uca.getUserId(), "0");
                if (IDataUtil.isNotEmpty(resDs)) {
                    bindSerialNumber = resDs.getData(0).getString("RES_CODE", "").trim();

                    //如用户由原来的非实名变更为实名时，在资料同步的同时，检测157号码是否处于非实名停机状态，如为停机状态，给对应的157号码生成开机工单（普通用户的开机工单）；
                    IData oldCustInfo = UcaInfoQry.qryCustomerInfoByCustId(uca.getCustId());
                    String oldIsRealName = oldCustInfo.getString("IS_REAL_NAME", "").trim();

                    IDataset customerDs = TradeCustomerInfoQry.getTradeCustomerByTradeId(tradeId);
                    if (IDataUtil.isNotEmpty(customerDs)) {
                        IData customer = customerDs.getData(0);
                        String newIsRealName = customer.getString("IS_REAL_NAME", "").trim();
                        System.out.println("SynMessageToBindPhoneActionxxxxxxxx96  new " + newIsRealName + "   old " + oldIsRealName + " 原号码 " + mainTrade.getString("SERIAL_NUMBER") + "  绑定号码 " + bindSerialNumber);
                        if (!oldIsRealName.equals("1") && newIsRealName.equals("1")) {//由原来的非实名变更为实名
                            synMessage(mainTrade, tradeId, bindSerialNumber, "1");//现网业务根据IS_REAL_NAME是否为1， 调用解除非实名停机，即局方开机
                        } else {
                            synMessage(mainTrade, tradeId, bindSerialNumber, null);
                        }           
                    }
                }
            }
        }
    }

    /*
     * bindSerialNumber 绑定的11位手机号码
     */
    private void synMessage(IData mainTrade, String tradeId, String bindSerialNumber, String isRealName) throws Exception
    {

        if (bindSerialNumber != null && bindSerialNumber.trim().length() > 0) {
            //IData tradeRes = resDs.getData(0).getString("RES_CODE", "").trim();
            //String bindSerialNumber = tradeRes.getString("RES_CODE", "").trim();//绑定的11位手机号码
            IData data = new DataMap();
            UcaData uca = UcaDataFactory.getNormalUca(bindSerialNumber);
            String strUserid = uca.getUserId();
            String strCustId = uca.getCustId();

            data.put("SERIAL_NUMBER", bindSerialNumber);
            data.put("USER_ID", strUserid);
            data.put("CUST_ID", strCustId);
            System.out.println("SynMessageToBindPhoneActionxxxxxxxx54 " + data);

            IDataset custInfos = CSAppCall.call("SS.ModifyCustInfoSVC.getCustInfo", data);
            IData params = custInfos.getData(0).getData("CUST_INFO");
            System.out.println("SynMessageToBindPhoneActionxxxxxxxx57 CUST_INFO " + data);

            if (isRealName != null && !isRealName.equals("")) {
                params.put("IS_REAL_NAME", isRealName);
            }
            params.put("TRADE_TYPE_CODE", "60");
            params.put("IS_NEED_SMS", false);//不发送短信     
            params.putAll(data);
            params.put("REMARK", "铁通商务固话开户同步绑定手机号码资料信息");//需要修改
            params.put("SKIP_RULE", "TRUE");

            IDataset customerDs = TradeCustomerInfoQry.getTradeCustomerByTradeId(tradeId);
            if (DataSetUtils.isNotBlank(customerDs)) {
                IData customer = customerDs.getData(0);

                System.out.println("SynMessageToBindPhoneActionxxxxxxxx75 customer " + customer);
                //经办人                
                String agentName = customer.getString("RSRV_STR7", "");
                String agentPsptType = customer.getString("RSRV_STR8", "");
                String agentPsptid = customer.getString("RSRV_STR9", "");
                String agentPsptAddr = customer.getString("RSRV_STR10", "");
                params.put("AGENT_CUST_NAME", agentName);
                params.put("AGENT_PSPT_TYPE_CODE", agentPsptType);
                params.put("AGENT_PSPT_ID", agentPsptid);
                params.put("AGENT_PSPT_ADDR", agentPsptAddr);
                System.out.println("SynMessageToBindPhoneActionxxxxxxxx91   " + agentName + "  " + agentPsptType + "   " + agentPsptid + "   " + agentPsptAddr);

            }

            IDataset custpersonDs = TradeCustPersonInfoQry.getTradeCustPersonByTradeId(tradeId);
            if (DataSetUtils.isNotBlank(custpersonDs)) {
                IData custPersonData = custpersonDs.getData(0);
                System.out.println("SynMessageToBindPhoneActionxxxxxxxx102 custPersonData " + custPersonData);

                params.put("CUST_NAME", custPersonData.getString("CUST_NAME", "").trim());
                params.put("PSPT_TYPE_CODE", custPersonData.getString("PSPT_TYPE_CODE", "").trim());
                params.put("PSPT_ID", custPersonData.getString("PSPT_ID", "").trim());
                params.put("PSPT_ADDR", custPersonData.getString("PSPT_ADDR", "").trim());
                params.put("PSPT_END_DATE", custPersonData.getString("PSPT_END_DATE", "").trim());
                params.put("PHONE", custPersonData.getString("PHONE", "").trim());
                if (custPersonData.getString("POST_ADDRESS", "").trim().length() > 0) {
                    params.put("POST_ADDRESS", custPersonData.getString("POST_ADDRESS", "").trim());
                } else {//商务电话开户界面生产环境保存不上通信地址，做下特殊处理，待后续改好后在统一使用取值 custPersonData.getString("POST_ADDRESS", "").trim()
                    params.put("POST_ADDRESS", custPersonData.getString("PSPT_ADDR", "").trim());
                }
                params.put("BIRTHDAY", custPersonData.getString("BIRTHDAY", "").trim());

                //使用人
                String useName = custPersonData.getString("RSRV_STR5", "");
                String usePsptType = custPersonData.getString("RSRV_STR6", "");
                String usePsptid = custPersonData.getString("RSRV_STR7", "");
                String usePsptAddr = custPersonData.getString("RSRV_STR8", "");
                params.put("USE", useName);
                params.put("USE_PSPT_TYPE_CODE", usePsptType);
                params.put("USE_PSPT_ID", usePsptid);
                params.put("USE_PSPT_ADDR", usePsptAddr);
                System.out.println("SynMessageToBindPhoneActionxxxxxxxx106   " + useName + "  " + usePsptType + "   " + usePsptid + "  " + usePsptAddr);

            }

            System.out.println("SynMessageToBindPhoneActionxxxxxxxx115 ");

            CSAppCall.call("SS.ModifyCustInfoRegSVC.tradeReg", params);

        }
    }
}
