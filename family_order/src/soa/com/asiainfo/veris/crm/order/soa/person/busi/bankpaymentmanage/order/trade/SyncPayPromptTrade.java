/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage.order.trade;

import java.util.List;

import com.asiainfo.veris.crm.order.pub.exception.BankPaymentManageException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage.order.requestdata.SyncPayPromptReqData;

/**
 * @CREATED by gongp@2014-7-14 修改历史 Revision 2014-7-14 下午09:35:19
 */
public class SyncPayPromptTrade extends BaseTrade implements ITrade
{

    /*
     * (non-Javadoc)
     */
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub

        SyncPayPromptReqData reqData = (SyncPayPromptReqData) btd.getRD();

        List<SvcTradeData> svcTradeDatas = reqData.getUca().getUserSvcBySvcId("6206");

        if (svcTradeDatas.isEmpty())
        {

            if ("0".equals(reqData.getModifyTag()))
            {

                String instId = SeqMgr.getInstId();

                SvcTradeData svcTd = new SvcTradeData();

                svcTd.setInstId(instId);
                svcTd.setUserId(reqData.getUca().getUserId());
                svcTd.setUserIdA("-1");
                svcTd.setProductId("-1");
                svcTd.setPackageId("-1");
                svcTd.setElementId("6206");
                svcTd.setMainTag("0");
                svcTd.setStartDate(reqData.getAcceptTime());
                svcTd.setEndDate(SysDateMgr.END_DATE_FOREVER);
                svcTd.setRsrvTag1("1");
                svcTd.setRsrvNum1("0");
                svcTd.setRsrvNum2("0");
                svcTd.setRsrvNum3("0");
                svcTd.setRsrvNum4("0");
                svcTd.setRsrvNum5("0");
                svcTd.setCampnId("0");

                svcTd.setModifyTag(BofConst.MODIFY_TAG_ADD);

                AttrTradeData svcAttrTd = new AttrTradeData();

                svcAttrTd.setInstId(SeqMgr.getInstId());
                svcAttrTd.setUserId(reqData.getUca().getUserId());
                svcAttrTd.setInstType("S");
                svcAttrTd.setAttrCode("V6206V1");
                svcAttrTd.setAttrValue(reqData.getWarningFee());
                svcAttrTd.setStartDate(reqData.getAcceptTime());
                svcAttrTd.setRelaInstId(instId);
                svcAttrTd.setEndDate(SysDateMgr.END_DATE_FOREVER);
                svcAttrTd.setRsrvNum1("6206");

                svcAttrTd.setModifyTag(BofConst.MODIFY_TAG_ADD);

                btd.add(reqData.getUca().getSerialNumber(), svcTd);
                btd.add(reqData.getUca().getSerialNumber(), svcAttrTd);

            }
            else if ("1".equals(reqData.getModifyTag()))
            {// 删除6206
                // common.error("用户没有订购关键时刻缴费提醒服务，删除不成功！");
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_263);
            }
            else
            {
                // common.error("MODIFY_TAG值不正确，业务未受理！");
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_264);
            }
        }
        else
        {

            if ("0".equals(reqData.getModifyTag()))
            {// 有6206的服务就只修改WARNING_FEE

                SvcTradeData svcTd = svcTradeDatas.get(0);

                String instId = svcTd.getInstId();

                List<AttrTradeData> attrTds = reqData.getUca().getUserAttrsByRelaInstId(instId);

                if (!attrTds.isEmpty())
                {

                    AttrTradeData attrTd = attrTds.get(0);

                    attrTd.setAttrValue(reqData.getWarningFee());
                    attrTd.setModifyTag(BofConst.MODIFY_TAG_UPD);

                    btd.add(reqData.getUca().getSerialNumber(), attrTd);
                }
                else
                {
                    CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_265, instId);
                }

                btd.add(reqData.getUca().getSerialNumber(), svcTd);
            }
            else if ("1".equals(reqData.getModifyTag()))
            { // 删除6206

                SvcTradeData svcTd = svcTradeDatas.get(0);

                String instId = svcTd.getInstId();

                List<AttrTradeData> attrTds = reqData.getUca().getUserAttrsByRelaInstId(instId);

                if (!attrTds.isEmpty())
                {

                    AttrTradeData attrTd = attrTds.get(0);

                    attrTd.setEndDate(reqData.getAcceptTime());
                    attrTd.setModifyTag(BofConst.MODIFY_TAG_DEL);

                    btd.add(reqData.getUca().getSerialNumber(), attrTd);
                }
                else
                {
                    CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_265, instId);
                }

                svcTd.setEndDate(reqData.getAcceptTime());
                svcTd.setModifyTag(BofConst.MODIFY_TAG_DEL);

                btd.add(reqData.getUca().getSerialNumber(), svcTd);

            }
            else
            {
                // common.error("MODIFY_TAG值不正确，业务未受理！");
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_264);
            }
        }
    }

}
