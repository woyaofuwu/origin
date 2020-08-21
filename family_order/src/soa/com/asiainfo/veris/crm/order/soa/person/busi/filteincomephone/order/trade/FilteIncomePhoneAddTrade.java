/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.filteincomephone.order.trade;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.person.busi.filteincomephone.order.requestdata.FilteIncomePhoneTradeReqData;

/**
 * @CREATED by gongp@2014-5-28 修改历史 Revision 2014-5-28 下午04:40:42
 */
public class FilteIncomePhoneAddTrade extends FilteIncomePhoneBaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        // TODO Auto-generated method stub
        FilteIncomePhoneTradeReqData reqData = (FilteIncomePhoneTradeReqData) bd.getRD();
        MainTradeData mainTD = bd.getMainTradeData();

        String userId = reqData.getUca().getUserId();

        int effectCount = this.getEffectRejectNoCount(bd, userId);

        if (effectCount + 1 > 11)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "一个用户最多只能设置10个拒接号码,您已经达到最大限度");
        }

        if (effectCount == 0)
        {// 没有办理业务的时候--开通来电拒接

            if (reqData.getUca().getUserSvcBySvcId("560").size() < 1)
            {
                geneTradeSvc(bd, BofConst.MODIFY_TAG_ADD);
            }
            if (reqData.getUca().getNowValidUserDiscntByDiscntId(getDiscntCode()).size() < 1)
            {
                geneTradeDistcnt(bd, BofConst.MODIFY_TAG_ADD);
            }

            if ("1".equals(reqData.getIsOpenSms()))
            {
                genSendSMSTradeOther(bd, BofConst.MODIFY_TAG_ADD);
                mainTD.setRsrvStr9("1");
            }
            else
            {
                genSendSMSTradeOther(bd, BofConst.MODIFY_TAG_DEL);
                mainTD.setRsrvStr9("2");
            }
            if (StringUtils.isBlank(reqData.getRejectSn()))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "拒接号码不能为空！");
            }
            geneTradeOther(bd, reqData.getRejectSn(), reqData.getRemark(), BofConst.MODIFY_TAG_ADD);

            mainTD.setRsrvStr1(reqData.getRejectSn());// 主台帐表中记录拒接号码
            mainTD.setRsrvStr2("0");// 主台帐表中记录操作类型
            mainTD.setRsrvStr8("0");

            dealMainTradeTag(mainTD, reqData);
        }
        else
        {
            if ("1".equals(reqData.getIsOpenSms()))
            {
                if (this.getTradeUserFilterPhoneSmsData(BofConst.MODIFY_TAG_ADD) == null)
                {
                    genSendSMSTradeOther(bd, BofConst.MODIFY_TAG_ADD);
                    mainTD.setRsrvStr9("1");
                }
            }
            else
            {
                if (this.getTradeUserFilterPhoneSmsData(BofConst.MODIFY_TAG_DEL) == null)
                {
                    genSendSMSTradeOther(bd, BofConst.MODIFY_TAG_DEL);
                    mainTD.setRsrvStr9("2");
                }
            }
            if (StringUtils.isBlank(reqData.getRejectSn()))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "拒接号码不能为空！");
            }
            if (this.getUserFilterPhoneOtherData(reqData.getUca().getUserId(), reqData.getRejectSn()) != null || this.getUserFilterPhoneOtherTradeData(bd, reqData.getRejectSn()) != null)
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "该号码【" + reqData.getRejectSn() + "】已经存在，请勿重复提交！");
            }
            geneTradeOther(bd, reqData.getRejectSn(), reqData.getRemark(), BofConst.MODIFY_TAG_ADD);

            mainTD.setRsrvStr1(reqData.getRejectSn());// 主台帐表中记录拒接号码
            mainTD.setRsrvStr2("0");// 主台帐表中记录操作类型
            mainTD.setRsrvStr8("1");
            dealMainTradeTag(mainTD, reqData);
        }
    }
}
