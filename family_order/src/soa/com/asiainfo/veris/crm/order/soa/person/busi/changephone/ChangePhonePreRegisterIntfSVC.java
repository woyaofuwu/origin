
package com.asiainfo.veris.crm.order.soa.person.busi.changephone;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAltsnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

public class ChangePhonePreRegisterIntfSVC extends CSBizService
{

    private IData altSnPreRegisterRetrun(IData input, String BizOrderResult, String RspCode, String desc)
    {
        // TODO Auto-generated method stub
        IData ret = new DataMap();
        ret.put("OLD_ID_VALUE", input.getString("OLD_ID_VALUE"));
        ret.put("NEW_ID_VALUE", input.getString("NEW_ID_VALUE"));
        ret.put("BIZ_ORDER_RESULT", BizOrderResult); // 《一级BOSS枢纽系统二级返回码规范》
        ret.put("BIZ_ORDER_RSP_DESC", desc);
        ret.put("RSPCODE", RspCode);

        ret.put("RESERVE", desc);
        input.put("NPASS_BizOrderResult", BizOrderResult);
        input.put("NPASS_RspCode", RspCode);
        input.put("NPASS_Reserve", desc);
        input.put("NOT_PASS", "1");

        return ret;
    }

    /**
     * 接收渠道向省BOSS发送业务预申请请求
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData changePhonePreRegister(IData input) throws Exception
    {

        ChangePhonePreRegisterBean bean = (ChangePhonePreRegisterBean) BeanManager.createBean(ChangePhonePreRegisterBean.class);

        // input.put("RSRV_STR6", "END");
        input.put("custInfo_PSPT_ID", input.getString("PSPT_ID", input.getString("ID_CARD_NUM")));
        input.put("custInfo_PSPT_TYPE_CODE", input.getString("PSPT_TYPE_CODE", input.getString("ID_CARD_TYPE")));
        if ("".equals(input.getString("custInfo_PSPT_TYPE_CODE")))
        {
            input.put("custInfo_PSPT_TYPE_CODE", input.getString("PSPT_TYPE"));

        }
        bean.setMoveInfoTrans(input);
        bean.checkSerialOldNew(input);

        IDataset dataset = CSAppCall.call("SS.ChangePhonePreRegisterRegSVC.tradeReg", input);

        return altSnPreRegisterRetrun(input, "0000", "00", "OK");

    }

    /**
     * 接收渠道向省BOSS发送业务预激活请求
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData changePhonePreRegisterActive(IData input) throws Exception
    {


            ChangePhonePreRegisterBean bean = (ChangePhonePreRegisterBean) BeanManager.createBean(ChangePhonePreRegisterBean.class);

            IData indata = new DataMap();
            IData outdata = new DataMap();
            String newSn = input.getString("NEW_SN");
            indata.put("SERIAL_NUMBER", input.getString("NEW_SN"));

            IData tradeInfo = bean.checkIsExistsGhTrade(newSn, "", "ACT");

            indata.put("OLD_SN", tradeInfo.getString("RSRV_STR2"));

            bean.checkIsExistsGh(indata.getString("OLD_SN"), newSn);

            IData result = bean.dealActive(indata, input, outdata);
            
            String oldSn = indata.getString("OLD_SN");
            String content = "您好，您的新号码" + newSn + "与原号码" + oldSn + "已关联，关联期为三个月；次月起，您可发送“QXGHGL”到10086取消关联。中国移动";
            IData sendInfo = new DataMap();
            sendInfo.put("EPARCHY_CODE", "0898");
            sendInfo.put("RECV_OBJECT", newSn);
            sendInfo.put("RECV_ID", newSn);
            sendInfo.put("SMS_PRIORITY", "50");
            sendInfo.put("NOTICE_CONTENT", content);
            sendInfo.put("REMARK", "改号业务激活提醒");
            sendInfo.put("FORCE_OBJECT", "10086");
            SmsSend.insSms(sendInfo);
            
            return result;
        
    }

    /**
     * 激活落地方
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData changePhonePreRegisterSnActive(IData input) throws Exception
    {
        ChangePhonePreRegisterBean bean = (ChangePhonePreRegisterBean) BeanManager.createBean(ChangePhonePreRegisterBean.class);

        IData outdata = new DataMap();
        outdata = bean.altSnActivate(input);

        input.put("SYNC_TAG", "MOD");
        bean.setInData(input, input);
        String newImsi = "";
        if (!"02".equals(input.getString("OPR_CODE")))
        {

            input.put("SERIAL_NUMBER", input.getString("OLD_ID_VALUE"));
            IDataset out = CSAppCall.call("SS.ChangePhonePreRegisterReginSVC.tradeReg", input);

            IData tradeImsi = new DataMap();
            tradeImsi.put("TRADE_ID", out.getData(0).getString("TRADE_ID"));
            tradeImsi.put("SERIAL_NUMBER", input.getString("OLD_ID_VALUE"));
            outdata.put("VOLTE_TYPE", "0");
            IDataset out1 = CSAppCall.call("SS.ChangePhoneInfoQuerySVC.queryNewImsiByTrade", tradeImsi);
            if (IDataUtil.isNotEmpty(out1))
            {
                newImsi = out1.getData(0).getString("IMSI");
                outdata.put("OLD_IMPI", out1.getData(0).getString("OLD_IMPI"));
                outdata.put("VOLTE_TYPE", out1.getData(0).getString("VOLTE_TYPE"));
            }
            outdata.put("OLD_IMSI", newImsi);
            IData pAltsnUpdate = new DataMap();
            pAltsnUpdate.put("SERIAL_NUMBER", input.getString("OLD_ID_VALUE", ""));
            pAltsnUpdate.put("FROM_STATUS", "0");
            pAltsnUpdate.put("TO_STATUS", "1");
            pAltsnUpdate.put("RELA_TYPE", "2");
            UserAltsnInfoQry.updStatusInfoBySn(pAltsnUpdate);
        }
        else
        {
            input.put("SERIAL_NUMBER", input.getString("OLD_ID_VALUE"));
            CSAppCall.call("SS.ChangePhonePreRegisterCancelTradeSVC.tradeReg", input);
        }
        outdata.put("X_RSPCODE", "0000");
        return outdata;
    }

    /**
     * 接收关联号码省BOSS发送业务预申请同步请求
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData changePhonePreRegisterSyn(IData input) throws Exception
    {
        ChangePhonePreRegisterBean bean = (ChangePhonePreRegisterBean) BeanManager.createBean(ChangePhonePreRegisterBean.class);

        String oldSn = input.getString("OLD_ID_VALUE");
        String newSn = input.getString("NEW_ID_VALUE");
        // 申请落地方的时候校验该号码是否已经激活,已经激活的先做激活取消再预受理
        IData indata = new DataMap();
        bean.checkIsExistsGh(oldSn, newSn);
        bean.checkIsExistsGhTrade(oldSn, newSn, "PRE");
        IData data = new DataMap();
        bean.setDataInfo(data, input);

        data.put("custInfo_PSPT_ID", input.getString("ID_CARD_NUM"));
        data.put("custInfo_PSPT_TYPE_CODE", input.getString("ID_CARD_TYPE"));
        input.put("MOVE_INFO", input.getString("MOVE_INFO"));
        input.put("RSRV_STR6", "END");
        data.put("RSRV_STR6", "END");
        data.put("REMARK", "END");
        IData userInfo = new DataMap();
        String eparchyKey = "";
        if ("01".equals(data.getString("WH_HANDLE", "")))
        {
            data.put("NEW_PROVINCE", "B");
            data.put("OLD_PROVINCE", "A");
            indata.put("SERIAL_NUMBER", oldSn);
            userInfo = bean.getLocalUserInfo(indata);
            eparchyKey = "OLD_EPARCHY";
            data.put(eparchyKey, userInfo.getString("EPARCHY_CODE"));
            IData localCustInfo = bean.checkIn(data, input);
            if ("1".equals(input.getString("NOT_PASS", "")))
            {

                return localCustInfo;
            }
            data.put("WH_HANDLE", "02");
        }
        else
        {
            data.put("NEW_PROVINCE", "A");
            data.put("OLD_PROVINCE", "B");
            indata.put("SERIAL_NUMBER", newSn) ;
            userInfo = bean.getLocalUserInfo(indata);
            eparchyKey = "NEW_EPARCHY";
            data.put(eparchyKey, userInfo.getString("EPARCHY_CODE"));
            IData localCustInfo = bean.checkIn(data, input);
            if ("1".equals(input.getString("NOT_PASS", "")))
            {

                return localCustInfo;
            }
            data.put("WH_HANDLE", "01");
        }
        // 01:新归属地接收业务申请

        IData syn = new DataMap();
        syn.put("NEW_PROVINCE", data.getString("NEW_PROVINCE"));
        syn.put("OLD_PROVINCE", data.getString("OLD_PROVINCE"));
        syn.put("NEW_EPARCHY", bean.getProvinceEparchyCode(indata));
        syn.put("OLD_EPARCHY", userInfo.getString("EPARCHY_CODE"));

        data.put("SYNC_INFO", syn);
        data.put("SYN_TAG", "N"); // 登记流程不走SYNC步骤调用IBOSS
        CSAppCall.call("SS.ChangePhonePreRegisterRegSVC.tradeReg", data);

        return altSnPreRegisterRetrun(input, "0000", "00", "OK");

    }

    public void setTrans(IData input) throws Exception
    {
        // 家庭账户创建没有传SERIAL_NUMBER，必须进行转换
        if ("SS.ChangePhonePreRegisterIntfSVC.changePhonePreRegister".equals(getVisit().getXTransCode()))
        {
            input.put("SERIAL_NUMBER", input.getString("NEW_SN"));

        }
        else if ("SS.ChangePhonePreRegisterIntfSVC.changePhonePreRegisterSyn".equals(getVisit().getXTransCode()))
        {
            input.put("TRADE_TYPE_CODE", "799");
            input.put("SERIAL_NUMBER", input.getString("OLD_ID_VALUE"));
            String WhHandle = input.getString("WH_HANDLE", "01");
            if ("02".equals(WhHandle))
            {
                input.put("SERIAL_NUMBER", input.getString("NEW_ID_VALUE"));
            }

        }
        else if ("SS.ChangePhonePreRegisterIntfSVC.changePhonePreRegisterActive".equals(getVisit().getXTransCode()))
        {
            input.put("SERIAL_NUMBER", input.getString("NEW_SN"));

        }
        else if ("SS.ChangePhonePreRegisterIntfSVC.changePhonePreRegisterSnActive".equals(getVisit().getXTransCode()))
        {
            input.put("SERIAL_NUMBER", input.getString("OLD_ID_VALUE"));

        }

    }

}
