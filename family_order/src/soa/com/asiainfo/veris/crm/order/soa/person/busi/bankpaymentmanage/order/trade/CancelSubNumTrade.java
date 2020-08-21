/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.BankPaymentManageException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.BankSubSignTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBankMainSignInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage.BankPaymentUtil;
import com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage.order.requestdata.BaseSignContractReqData;

/**
 * @CREATED by gongp@2014-6-25 修改历史 Revision 2014-6-25 上午11:46:26
 */
public class CancelSubNumTrade extends BaseTrade implements ITrade
{

    private String callSignSync(BusiTradeData btd, IData subSignInfo) throws Exception
    {

        BaseSignContractReqData reqData = (BaseSignContractReqData) btd.getRD();

        IData param = new DataMap();

        String operFlowId = reqData.getOperFlowId();

        if ("".equals(operFlowId))
        {
            operFlowId = BankPaymentUtil.getOperFlowId();
        }

        param.put("DEAL_TYPE", "02");
        param.put("SIGN_ID", subSignInfo.getString("SIGN_ID"));
        param.put("MAIN_USER_TYPE", subSignInfo.getString("MAIN_USER_TYPE", "01"));
        param.put("MAIN_USER_VALUE", subSignInfo.getString("MAIN_USER_VALUE"));
        param.put("SUB_USER_TYPE", "01");
        param.put("SUB_USER_VALUE", subSignInfo.getString("SUB_USER_VALUE"));
        param.put("CHNL_TYPE", reqData.getChnlType());
        param.put("SUB_TIME", subSignInfo.getString("START_DATE").replaceAll(" ", "").replaceAll(":", "").replaceAll("-", ""));
        param.put("TRANSACTION_ID", operFlowId);

        IData rData = new DataMap();
        try
        {
            param.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());

            String kindId = "BIP1A159_T1000154_0_0";
            param.put("X_TRANS_CODE", "T1000154");
            param.put("KIND_ID", kindId);

            rData = IBossCall.dealInvokeUrl(kindId, "IBOSS2", param).getData(0);

            // for test;
            /*
             * rData.put("X_RSPCODE", "0000"); rData.put("X_RESULTCODE", "ok!"); rData.put("SIGN_ID",
             * "01000401"+SysDateMgr.getSysTime().replaceAll(" ","").replaceAll(":", "").replaceAll("-", ""));
             */// end test

        }
        catch (Exception e)
        {
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_237, e.getMessage());
        }

        if (!("0000".equals(rData.getString("X_RSPCODE", ""))))
        {
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_237, rData.getString("X_RSPCODE", "") + "," + rData.getString("X_RSPDESC", "") + "," + rData.getString("X_RESULTCODE", "") + "," + rData.getString("X_RESULTINFO", ""));
        }

        return operFlowId;
    }

    private void checkMainSignInfo(IData subSignInfo) throws Exception
    {

        IData temp = new DataMap();
        IData ret = new DataMap();
        temp.put("SERIAL_NUMBER", subSignInfo.getString("MAIN_USER_VALUE", ""));

        BankPaymentUtil.getSnRoute(temp, ret);

        String mainFlag = ret.getString("FLAG");

        if ("A".equals(mainFlag))
        {
            IDataset mainsignInfos = UserBankMainSignInfoQry.getInfoByUser(subSignInfo.getString("MAIN_USER_TYPE", "01"), subSignInfo.getString("MAIN_USER_VALUE", ""));
            if (IDataUtil.isEmpty(mainsignInfos))
            {
                // common.error("1202:主号签约信息不存在");
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_238);
            }
        }
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        BaseSignContractReqData reqData = (BaseSignContractReqData) btd.getRD();

        MainTradeData mainTD = btd.getMainTradeData();
        mainTD.setRsrvStr1("CancelSubSign");

        IDataset cancelDatas = new DatasetList(reqData.getCancelDataStr());

        if (IDataUtil.isNotEmpty(cancelDatas))
        {

            for (int i = 0, size = cancelDatas.size(); i < size; i++)
            {

                IData temp = cancelDatas.getData(i);

                IDataset subSignInfos = UserBankMainSignInfoQry.queryUserBankSubSignByUID(temp.getString("SUB_USER_TYPE", "01"), temp.getString("SUB_USER_VALUE"));

                if (IDataUtil.isEmpty(subSignInfos))
                {
                    // common.error("1201:该用户关联副号码信息已失效！请重新查询！");
                    CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_20, temp.getString("SUB_USER_VALUE"));
                }
                IData subSignInfo = subSignInfos.getData(0);

                this.checkMainSignInfo(subSignInfo);

                String operFlowId = this.callSignSync(btd, subSignInfo);

                this.regSubTrade(btd, subSignInfo, operFlowId);
            }
        }

    }

    private void regSubTrade(BusiTradeData btd, IData subSignInfo, String operFlowId) throws Exception
    {
        // TODO Auto-generated method stub
        BaseSignContractReqData reqData = (BaseSignContractReqData) btd.getRD();

        BankSubSignTradeData subsignTd = new BankSubSignTradeData(subSignInfo);

        subsignTd.setChnlType(reqData.getChnlType());// 01移动营业厅
        subsignTd.setMainSignType("1");
        subsignTd.setSubUserType("01"); // 用户标识类型：01-手机号码、02-邮箱、03-固话、04-宽带
        subsignTd.setEndDate(reqData.getAcceptTime());
        subsignTd.setModifyTag(BofConst.MODIFY_TAG_DEL);
        subsignTd.setSubSignType("1");
        subsignTd.setRsrvStr2(operFlowId);

        btd.add(reqData.getUca().getSerialNumber(), subsignTd);
    }

}
