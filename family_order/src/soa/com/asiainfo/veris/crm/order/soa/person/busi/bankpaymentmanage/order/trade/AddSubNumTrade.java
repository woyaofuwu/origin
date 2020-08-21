/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.BankPaymentManageException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.BankSubSignTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBankMainSignInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage.BankPaymentManageBean;
import com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage.BankPaymentUtil;
import com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage.order.requestdata.BaseSignContractReqData;

/**
 * @CREATED by gongp@2014-6-25 修改历史 Revision 2014-6-25 上午09:32:39
 */
public class AddSubNumTrade extends BaseTrade implements ITrade
{

    public void addSubNum(BusiTradeData btd) throws Exception
    {
        BaseSignContractReqData reqData = (BaseSignContractReqData) btd.getRD();

        IDataset mainSignInfos = UserBankMainSignInfoQry.queryUserBankMainSignByUID("01", reqData.getUca().getSerialNumber());

        if (IDataUtil.isEmpty(mainSignInfos))
        {
            // common.error("1202:该用户当前无有效的总对总缴费签约记录！");
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_233);
        }

        IData mainSignInfo = mainSignInfos.getData(0);

        String signId = mainSignInfo.getString("SIGN_ID", "");
        String homeArea = mainSignInfo.getString("HOME_AREA", "");

        String subTime = reqData.getAcceptTime().replaceAll(" ", "").replaceAll(":", "").replaceAll("-", "");

        // 预付费类型转换
        String rsrvStr4 = BankPaymentUtil.convertPrePayTag(reqData.getUca());

        // 签约信息同步----副号落地不需要发同步
        IData param = new DataMap();

        param.put("DEAL_TYPE", "01");
        param.put("SIGN_ID", signId);
        param.put("MAIN_USER_TYPE", "01");
        param.put("MAIN_USER_VALUE", reqData.getUca().getSerialNumber());
        param.put("SUB_USER_TYPE", "01");
        param.put("SUB_USER_VALUE", reqData.getSubNumber());
        param.put("CHNL_TYPE", reqData.getChnlType());
        param.put("SUB_TIME", subTime);

        String operFlowId = reqData.getOperFlowId();

        if ("".equals(operFlowId))
        {
            operFlowId = BankPaymentUtil.getOperFlowId();
        }

        param.put("TRANSACTION_ID", operFlowId);

        IData rData = new DataMap();
        try
        {
            param.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());
            param.put("KIND_ID", "BIP1A158_T1000154_0_0");
            param.put("X_TRANS_CODE", "T1000154");

            String kindId = "BIP1A158_T1000154_0_0";

            rData = IBossCall.dealInvokeUrl(kindId, "IBOSS2", param).getData(0);
        }
        catch (Exception e)
        {
            // / common.error("1301:调用一级接口副号签约信息同步出错");
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_11, e.getMessage());
        }
        if (rData == null)
        {
            // common.error("1301:调用一级接口副号签约信息同步出错:返回为空！");
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_57);
        }
        if (rData != null && !rData.getString("X_RSPCODE", "").equals("0000"))
        {
            // common.error("1301:调用一级接口副号签约信息同步出错:"+rData.getString("X_RSPDESC",""));
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_11, rData.getString("X_RSPDESC", ""));
        }

        this.regSubTrade(btd, signId, homeArea, rsrvStr4, reqData.getUca().getSerialNumber(), reqData.getSubNumber());
    }

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        BaseSignContractReqData reqData = (BaseSignContractReqData) btd.getRD();

        BankPaymentManageBean bean = BeanManager.createBean(BankPaymentManageBean.class);

        IData param = new DataMap();
        param.put("SUB_NUMBER", reqData.getSubNumber());
        param.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());

        bean.subNumCheck(param);

        this.addSubNum(btd);

        MainTradeData mainTD = btd.getMainTradeData();
        mainTD.setRsrvStr1("AddSubNum");

    }

    private void regSubTrade(BusiTradeData btd, String signId, String homeArea, String rsrvStr4, String main_value, String sub_value) throws Exception
    {
        // TODO Auto-generated method stub
        BaseSignContractReqData reqData = (BaseSignContractReqData) btd.getRD();

        BankSubSignTradeData bsstradedata = new BankSubSignTradeData();

        bsstradedata.setChnlType(reqData.getChnlType());// 01移动营业厅
        bsstradedata.setSignId(signId);
        bsstradedata.setMainUserType("01");
        bsstradedata.setMainUserValue(main_value);
        bsstradedata.setMainSignType("0");
        bsstradedata.setSubUserType("01"); // 用户标识类型：01-手机号码、02-邮箱、03-固话、04-宽带
        bsstradedata.setSubUserValue(sub_value);
        bsstradedata.setMainEparchyCode(homeArea);
        bsstradedata.setStartDate(SysDateMgr.getSysTime());
        bsstradedata.setUpdateDate(SysDateMgr.getSysTime());
        bsstradedata.setEndDate(SysDateMgr.END_DATE_FOREVER);
        bsstradedata.setModifyTag(BofConst.MODIFY_TAG_ADD);
        bsstradedata.setSubSignType("0");
        bsstradedata.setRemark(reqData.getRemark());
        bsstradedata.setRsrvStr4(rsrvStr4);
        bsstradedata.setInstId(SeqMgr.getInstId());
        btd.add(main_value, bsstradedata);
    }

}
