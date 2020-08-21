
package com.asiainfo.veris.crm.order.soa.person.busi.specialtrademgr.order.trade;

import java.util.List;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.specialtrademgr.order.requestdata.OtherProtectPassInfoData;
import com.asiainfo.veris.crm.order.soa.person.busi.specialtrademgr.order.requestdata.ProtectPassInfoRequestData;
import com.asiainfo.veris.crm.order.soa.person.busi.specialtrademgr.order.requestdata.SvcProtectPassInfoData;

public class ProtectPassInfoTrade extends BaseTrade implements ITrade
{

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        ProtectPassInfoRequestData reqData = (ProtectPassInfoRequestData) btd.getRD();
        // 记录渠道类型
        dealMainInfo(btd);

        // 插入other表动作
        dealOtherInfo(btd);

        if ("false".equals(reqData.getFlag()))
        {
            // 插入SVC表动作
            dealSvcInfo(btd);
        }

    }

    public void dealMainInfo(BusiTradeData btd) throws Exception
    {
        btd.getMainTradeData().setRsrvStr10(CSBizBean.getVisit().getInModeCode());
    }

    public void dealOtherInfo(BusiTradeData btd) throws Exception
    {
        ProtectPassInfoRequestData reqData = (ProtectPassInfoRequestData) btd.getRD();
        String modfiyTag = "0";

        List<OtherProtectPassInfoData> otherInfos = reqData.getOtherInfo();
        OtherProtectPassInfoData otherInfo = new OtherProtectPassInfoData();
        if (otherInfos.size() > 0)
        {
            otherInfo = otherInfos.get(0);
        }

        String questionFirst = "";
        String questionSecond = "";
        String questionThird = "";

        // 问题选择如果为自定义则直接获取自定义问题内容，如果非自定义选项则填写选择问题对应的内容
        if ("z".equals(otherInfo.getQuestion_first()))
        {
            questionFirst = otherInfo.getCustom_question_first();
        }
        else
        {
            questionFirst = StaticUtil.getStaticValue("QUESTION_AND_ANSWER", otherInfo.getQuestion_first());
        }
        if ("z".equals(otherInfo.getQuestion_second()))
        {
            questionSecond = otherInfo.getCustom_question_second();
        }
        else
        {
            questionSecond = StaticUtil.getStaticValue("QUESTION_AND_ANSWER", otherInfo.getQuestion_second());
        }
        if ("z".equals(otherInfo.getQuestion_third()))
        {
            questionThird = otherInfo.getCustom_question_third();
        }
        else
        {
            questionThird = StaticUtil.getStaticValue("QUESTION_AND_ANSWER", otherInfo.getQuestion_third());
        }

        if ("true".equals(reqData.getFlag()))
        {
            IDataset retOtherInfo = UserOtherInfoQry.getUserOtherInfoByAll(btd.getRD().getUca().getUserId(), "SPWP");
            if (IDataUtil.isNotEmpty(retOtherInfo))
            {
                OtherTradeData otherTradeData = new OtherTradeData(retOtherInfo.getData(0));
                otherTradeData.setRsrvStr6(otherInfo.getQuestion_first()); // 问题编码一
                otherTradeData.setRsrvStr7(otherInfo.getQuestion_second());// 问题编码二
                otherTradeData.setRsrvStr8(otherInfo.getQuestion_third()); // 问题编码三

                otherTradeData.setRsrvStr11(questionFirst); // 问题一
                otherTradeData.setRsrvStr12(otherInfo.getAnswer_first()); // 答案一
                otherTradeData.setRsrvStr13(questionSecond); // 问题二
                otherTradeData.setRsrvStr14(otherInfo.getAnswer_second()); // 答案二
                otherTradeData.setRsrvStr15(questionThird); // 问题三
                otherTradeData.setRsrvStr16(otherInfo.getAnswer_third()); // 答案三
                otherTradeData.setRsrvStr17(otherInfo.getEmail()); // 电子邮箱地址
                otherTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                btd.add(btd.getRD().getUca().getSerialNumber(), otherTradeData);
            }
            else
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1167);
            }

        }
        else
        {
            OtherTradeData otherTradeData = new OtherTradeData();
            otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
            otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());

            otherTradeData.setUserId(btd.getRD().getUca().getUserId());
            otherTradeData.setRsrvValueCode("SPWP");
            otherTradeData.setRsrvValue("服务密码保护");
            otherTradeData.setRsrvStr6(otherInfo.getQuestion_first()); // 问题编码一
            otherTradeData.setRsrvStr7(otherInfo.getQuestion_second());// 问题编码二
            otherTradeData.setRsrvStr8(otherInfo.getQuestion_third()); // 问题编码三

            otherTradeData.setRsrvStr11(questionFirst); // 问题一
            otherTradeData.setRsrvStr12(otherInfo.getAnswer_first()); // 答案一
            otherTradeData.setRsrvStr13(questionSecond); // 问题二
            otherTradeData.setRsrvStr14(otherInfo.getAnswer_second()); // 答案二
            otherTradeData.setRsrvStr15(questionThird); // 问题三
            otherTradeData.setRsrvStr16(otherInfo.getAnswer_third()); // 答案三
            otherTradeData.setRsrvStr17(otherInfo.getEmail()); // 电子邮箱地址

            otherTradeData.setStartDate(btd.getRD().getAcceptTime());
            otherTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
            otherTradeData.setModifyTag(modfiyTag);
            otherTradeData.setInstId(SeqMgr.getInstId());
            otherTradeData.setRemark("问题和答案存放RSRV_STR11开始的预留字段中");

            otherTradeData.setRsrvDate10(SysDateMgr.getSysTime()); // 当时第一次设置密保保存下第一次的时间

            btd.add(btd.getRD().getUca().getSerialNumber(), otherTradeData);
        }

    }

    public void dealSvcInfo(BusiTradeData btd) throws Exception
    {

        ProtectPassInfoRequestData reqData = (ProtectPassInfoRequestData) btd.getRD();

        List<SvcProtectPassInfoData> svcInfos = reqData.getSvcInfo();

        SvcProtectPassInfoData svcInfo = new SvcProtectPassInfoData();
        if (svcInfos.size() > 0)
        {
            svcInfo = svcInfos.get(0);
        }

        SvcTradeData svcTradeData = new SvcTradeData();

        svcTradeData.setUserId(btd.getRD().getUca().getUserId());
        svcTradeData.setUserIdA("-1");
        svcTradeData.setProductId("-1");
        svcTradeData.setPackageId("-1");
        svcTradeData.setElementId("3312");
        svcTradeData.setRemark("服务密码保护操作");
        svcTradeData.setMainTag("0");
        svcTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        svcTradeData.setInstId(SeqMgr.getInstId());
        svcTradeData.setStartDate(SysDateMgr.getSysTime());
        svcTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);

        btd.add(btd.getRD().getUca().getSerialNumber(), svcTradeData);

    }

}
