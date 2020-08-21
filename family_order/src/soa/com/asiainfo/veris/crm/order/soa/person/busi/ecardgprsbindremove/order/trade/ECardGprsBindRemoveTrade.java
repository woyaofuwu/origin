
package com.asiainfo.veris.crm.order.soa.person.busi.ecardgprsbindremove.order.trade;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.AcctDayDateUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.ecardgprsbindremove.order.requestdata.ECardGprsBindRemoveReqData;

public class ECardGprsBindRemoveTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        ECardGprsBindRemoveReqData eCardGprsBindRD = (ECardGprsBindRemoveReqData) btd.getRD();

        // 终止副卡付费关系
        geneTradePayrelation(btd);

        String relationTyeCode = "80";
        String roleCodeB = "2";
        // 获得副卡的UU关系
        IDataset relaUUInfo = RelaUUInfoQry.getRelationsByUserIdAndTypeAndRoleCodeB(relationTyeCode, eCardGprsBindRD.getUser_id_b(), roleCodeB);
        if (relaUUInfo == null || relaUUInfo.size() < 1)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取随E行副卡UU关系错误！");
        }
        RelationTradeData relationTradeData = new RelationTradeData(relaUUInfo.getData(0));
        // 结束副卡的UU关系
        geneTradeRelationB(btd, relationTradeData);
        mainTradeDeal(btd);
        String user_id_a = relationTradeData.getUserIdA();
        String serial_number_a = relationTradeData.getSerialNumberA();

        // 如果随E行只有一个副卡，取消掉后要把虚拟账户的UU关系以及资费终止
        IDataset uuResults = RelaUUInfoQry.getSEL_USER_ROLEA(user_id_a, roleCodeB, relationTyeCode);
        if (uuResults.size() == 1)
        {
            geneTradeRelationOne(btd, user_id_a);
            geneTradeDiscnt(btd, user_id_a);
            geneTradeUser(btd, serial_number_a);
        }
    }

    private void geneTradeDiscnt(BusiTradeData btd, String user_id_a) throws Exception
    {
        ECardGprsBindRemoveReqData eCardGprsBindRD = (ECardGprsBindRemoveReqData) btd.getRD();
        IDataset userIdADiscnt = UserDiscntInfoQry.getAllDiscntByUserId(user_id_a, "5904");
        if (userIdADiscnt == null || userIdADiscnt.size() < 1)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取随E行虚拟用户资费信息错误！");
        }
        DiscntTradeData userDiscntTradeData = new DiscntTradeData(userIdADiscnt.getData(0));
        userDiscntTradeData.setEndDate(eCardGprsBindRD.getAcceptTime());
        userDiscntTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
        userDiscntTradeData.setRemark(eCardGprsBindRD.getRemark());
        btd.add(eCardGprsBindRD.getUca().getSerialNumber(), userDiscntTradeData);
    }

    // 插入付费关系表
    public void geneTradePayrelation(BusiTradeData btd) throws Exception
    {
        ECardGprsBindRemoveReqData eCardGprsBindRD = (ECardGprsBindRemoveReqData) btd.getRD();
        UcaData ucaData = eCardGprsBindRD.getUca();
        IDataset payRelationB = PayRelaInfoQry.getPayrelaByPayItemCode(eCardGprsBindRD.getUser_id_b(), "40000");
        if (payRelationB == null || payRelationB.size() < 1)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取随E行副卡付费关系错误！");
        }

        PayRelationTradeData payRelationTradeDataB = new PayRelationTradeData(payRelationB.getData(0));

        payRelationTradeDataB.setModifyTag(BofConst.MODIFY_TAG_DEL);
        payRelationTradeDataB.setEndCycleId(AcctDayDateUtil.getCycleIdLastDayLastAcct(eCardGprsBindRD.getUca().getUserId()));

        btd.add(eCardGprsBindRD.getUca().getSerialNumber(), payRelationTradeDataB);
    }

    // 终止副卡的UU关系
    public void geneTradeRelationB(BusiTradeData btd, RelationTradeData relationTradeData) throws Exception
    {
        ECardGprsBindRemoveReqData eCardGprsBindRD = (ECardGprsBindRemoveReqData) btd.getRD();
        UcaData ucaData = eCardGprsBindRD.getUca();

        relationTradeData.setEndDate(eCardGprsBindRD.getAcceptTime());
        relationTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
        relationTradeData.setRemark(eCardGprsBindRD.getRemark());

        btd.add(eCardGprsBindRD.getUca().getSerialNumber(), relationTradeData);
    }

    private void geneTradeRelationOne(BusiTradeData btd, String user_id_a) throws Exception
    {
        ECardGprsBindRemoveReqData eCardGprsBindRD = (ECardGprsBindRemoveReqData) btd.getRD();
        IDataset uuResults = RelaUUInfoQry.getSEL_USER_ROLEA(user_id_a, "1", "80");
        if (uuResults == null || uuResults.size() < 1)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取随E行副卡UU关系错误！");
        }
        RelationTradeData relationTradeDataA = new RelationTradeData(uuResults.getData(0));
        relationTradeDataA.setEndDate(eCardGprsBindRD.getAcceptTime());
        relationTradeDataA.setModifyTag(BofConst.MODIFY_TAG_DEL);
        btd.add(eCardGprsBindRD.getUca().getSerialNumber(), relationTradeDataA);
    }

    private void geneTradeUser(BusiTradeData btd, String serial_number_a) throws Exception
    {
        ECardGprsBindRemoveReqData eCardGprsBindRD = (ECardGprsBindRemoveReqData) btd.getRD();

        IDataset userInfoA = UserInfoQry.getAllUserInfoBySn(serial_number_a);
        if (userInfoA != null && userInfoA.size() == 1)
        {
            UserTradeData userTradeDataA = new UserTradeData(userInfoA.getData(0));
            userTradeDataA.setRemoveTag(BofConst.MODIFY_TAG_DEL);
            userTradeDataA.setModifyTag(BofConst.MODIFY_TAG_DEL);
            userTradeDataA.setDestroyTime(eCardGprsBindRD.getAcceptTime());
            btd.add(eCardGprsBindRD.getUca().getSerialNumber(), userTradeDataA);
        }
    }

    public void mainTradeDeal(BusiTradeData btd) throws Exception
    {
        ECardGprsBindRemoveReqData eCardGprsBindRD = (ECardGprsBindRemoveReqData) btd.getRD();
        MainTradeData busitradedata = btd.getMainTradeData();
        busitradedata.setRsrvStr5(eCardGprsBindRD.getSerial_number_b());
        busitradedata.setRsrvStr6(eCardGprsBindRD.getUser_id_b());
        // busitradedata.setProcessTagSet("00000000000000000000");

    }

}
