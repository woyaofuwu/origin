
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.trunktrade.order.trade;

import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.UserTelephoeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.TelephoneTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserTelephoeInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.trunktrade.order.requestdata.ChangeTrunkMainUserReqData;

public class ChangeTrunkMainUserTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {

        ChangeTrunkMainUserReqData changeTrunkMainUserRD = (ChangeTrunkMainUserReqData) btd.getRD();
        IDataset userTrunkSet = UserTelephoeInfoQry.getUserTrunkInfoByUserId(changeTrunkMainUserRD.getUca().getUserId());
        if (IDataUtil.isEmpty(userTrunkSet))
            CSAppException.apperr(UserTelephoeException.CRM_TELEPHOE_1);

        String sn = changeTrunkMainUserRD.getNewMianSn();
        IDataset userInfo = UserInfoQry.getAllUserInfoBySn(sn);
        if (IDataUtil.isEmpty(userInfo))
            CSAppException.apperr(UserTelephoeException.CRM_TELEPHOE_2);
        String userIdNew = userInfo.getData(0).getString("USER_ID");
        IDataset rela = RelaUUInfoQry.getUUInfoByUserIdAB(changeTrunkMainUserRD.getUca().getUserId(), userIdNew, "T1", "2");
        if (IDataUtil.isEmpty(rela))
            CSAppException.apperr(UserTelephoeException.CRM_TELEPHOE_3);

        btd.getMainTradeData().setRsrvStr1(sn);// RSRV_STR1 记录新代表号
        btd.getMainTradeData().setRsrvStr2(userIdNew);// RSRV_STR2记录新代表号userid
        btd.getMainTradeData().setNetTypeCode("12");

        createTelephoneTrade(btd, userIdNew);// 处理固话装机台账
        createDiscntTrade(btd, userIdNew);// 处理优惠及属性台账
        createSvcTrade(btd, userIdNew);// 处理服务及属性台账
        createRelationUUTrade(btd, userIdNew);// 处理uu关系台账

    }

    public void createDiscntTrade(BusiTradeData btd, String userIdNew) throws Exception
    {
        ChangeTrunkMainUserReqData changeTrunkMainUserRD = (ChangeTrunkMainUserReqData) btd.getRD();

        IDataset discnt = UserDiscntInfoQry.getAllValidDiscntByUserId(changeTrunkMainUserRD.getUca().getUserId());

        if (IDataUtil.isNotEmpty(discnt))
        {
            IDataset attrSet = new DatasetList();
            String relaInstId = "";
            for (int i = 0; i < discnt.size(); i++)
            {
                // 先处理老号码优惠
                relaInstId = discnt.getData(i).getString("RELA_INST_ID");
                DiscntTradeData oldDiscnt = new DiscntTradeData(discnt.getData(i));
                oldDiscnt.setModifyTag(BofConst.MODIFY_TAG_DEL);
                oldDiscnt.setEndDate(SysDateMgr.getSysTime());
                oldDiscnt.setRemark("代表号修改");
                btd.add(changeTrunkMainUserRD.getUca().getSerialNumber(), oldDiscnt);
                // 处理属性
                attrSet = UserAttrInfoQry.getUserAttrByRelaInstId(changeTrunkMainUserRD.getUca().getUserId(), relaInstId, CSBizBean.getVisit().getStaffEparchyCode());
                if (IDataUtil.isNotEmpty(attrSet))
                {
                    for (int j = 0; j < attrSet.size(); j++)
                    {
                        AttrTradeData oldAttrTradeData = new AttrTradeData(attrSet.getData(j));
                        oldAttrTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        oldAttrTradeData.setEndDate(SysDateMgr.getSysTime());
                        oldAttrTradeData.setRemark("代表号修改");
                        btd.add(changeTrunkMainUserRD.getUca().getSerialNumber(), oldAttrTradeData);
                    }
                }
                // 处理新号码优惠
                String newRelaInstId = SeqMgr.getInstId();
                DiscntTradeData newDiscnt = new DiscntTradeData(discnt.getData(i));
                newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
                newDiscnt.setUserId(userIdNew);
                newDiscnt.setStartDate(SysDateMgr.getSysTime());
                newDiscnt.setEndDate(SysDateMgr.getTheLastTime());
                newDiscnt.setInstId(SeqMgr.getInstId());
                btd.add(changeTrunkMainUserRD.getNewMianSn(), newDiscnt);
                // 处理属性
                if (IDataUtil.isNotEmpty(attrSet))
                {
                    for (int j = 0; j < attrSet.size(); j++)
                    {
                        AttrTradeData newAttrTradeData = new AttrTradeData(attrSet.getData(j));
                        newAttrTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                        newAttrTradeData.setStartDate(SysDateMgr.getSysTime());
                        newAttrTradeData.setEndDate(SysDateMgr.getTheLastTime());
                        newAttrTradeData.setRemark("代表号修改");
                        newAttrTradeData.setInstId(SeqMgr.getInstId());
                        newAttrTradeData.setRelaInstId(newRelaInstId);
                        btd.add(changeTrunkMainUserRD.getNewMianSn(), newAttrTradeData);
                    }
                }
            }
        }
    }

    public void createRelationUUTrade(BusiTradeData btd, String userIdNew) throws Exception
    {
        ChangeTrunkMainUserReqData changeTrunkMainUserRD = (ChangeTrunkMainUserReqData) btd.getRD();
        IDataset rela = RelaUUInfoQry.getUUInfoByUserIdAB(changeTrunkMainUserRD.getUca().getUserId(), userIdNew, "T1", "2");
        // 需要处理所有的记录的user_id_a
        IDataset otherRela = RelaUUInfoQry.getMemForOther(changeTrunkMainUserRD.getUca().getUserId(), userIdNew, "T1");
        for (int i = 0; i < otherRela.size(); i++)
        {
            RelationTradeData otherRelationUU = new RelationTradeData(otherRela.getData(i));
            otherRelationUU.setModifyTag(BofConst.MODIFY_TAG_UPD);
            otherRelationUU.setUserIdA(userIdNew);
            otherRelationUU.setSerialNumberA(changeTrunkMainUserRD.getNewMianSn());
            otherRelationUU.setRemark("代表号修改");
            btd.add(changeTrunkMainUserRD.getUca().getSerialNumber(), otherRelationUU);
        }

        RelationTradeData oldRelationUU = new RelationTradeData(rela.getData(0));
        oldRelationUU.setModifyTag(BofConst.MODIFY_TAG_DEL);
        oldRelationUU.setEndDate(SysDateMgr.getSysTime());
        oldRelationUU.setRemark("代表号修改");
        btd.add(changeTrunkMainUserRD.getUca().getSerialNumber(), oldRelationUU);

        RelationTradeData newRelationUU = new RelationTradeData(rela.getData(0));
        newRelationUU.setModifyTag(BofConst.MODIFY_TAG_ADD);
        newRelationUU.setStartDate(SysDateMgr.getSysTime());
        newRelationUU.setEndDate(SysDateMgr.getTheLastTime());
        newRelationUU.setInstId(SeqMgr.getInstId());
        newRelationUU.setUserIdA(userIdNew);
        newRelationUU.setSerialNumberA(changeTrunkMainUserRD.getNewMianSn());
        newRelationUU.setUserIdB(changeTrunkMainUserRD.getUca().getUserId());
        newRelationUU.setSerialNumberB(changeTrunkMainUserRD.getUca().getSerialNumber());
        newRelationUU.setRemark("代表号修改");
        btd.add(changeTrunkMainUserRD.getNewMianSn(), newRelationUU);

    }

    public void createSvcTrade(BusiTradeData btd, String userIdNew) throws Exception
    {
        ChangeTrunkMainUserReqData changeTrunkMainUserRD = (ChangeTrunkMainUserReqData) btd.getRD();

        IDataset svc = UserSvcInfoQry.getUserSvcByIdForNotMain(changeTrunkMainUserRD.getUca().getUserId());

        if (IDataUtil.isNotEmpty(svc))
        {
            IDataset attrSet = new DatasetList();
            String relaInstId = "";
            for (int i = 0; i < svc.size(); i++)
            {
                // 先处理老号码优惠
                relaInstId = svc.getData(i).getString("RELA_INST_ID");
                SvcTradeData oldSvcTradeData = new SvcTradeData(svc.getData(i));
                oldSvcTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                oldSvcTradeData.setEndDate(SysDateMgr.getSysTime());
                oldSvcTradeData.setRemark("代表号修改");
                btd.add(changeTrunkMainUserRD.getUca().getSerialNumber(), oldSvcTradeData);
                // 处理属性
                attrSet = UserAttrInfoQry.getUserAttrByRelaInstId(changeTrunkMainUserRD.getUca().getUserId(), relaInstId, CSBizBean.getVisit().getStaffEparchyCode());
                if (IDataUtil.isNotEmpty(attrSet))
                {
                    for (int j = 0; j < attrSet.size(); j++)
                    {
                        AttrTradeData oldAttrTradeData = new AttrTradeData(attrSet.getData(j));
                        oldAttrTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        oldAttrTradeData.setEndDate(SysDateMgr.getSysTime());
                        oldAttrTradeData.setRemark("代表号修改");
                        btd.add(changeTrunkMainUserRD.getUca().getSerialNumber(), oldAttrTradeData);
                    }
                }
                // 处理新号码优惠
                String newRelaInstId = SeqMgr.getInstId();
                SvcTradeData newSvcTradeData = new SvcTradeData(svc.getData(i));
                newSvcTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                newSvcTradeData.setUserId(userIdNew);
                newSvcTradeData.setStartDate(SysDateMgr.getSysTime());
                newSvcTradeData.setEndDate(SysDateMgr.getTheLastTime());
                newSvcTradeData.setInstId(SeqMgr.getInstId());
                btd.add(changeTrunkMainUserRD.getNewMianSn(), newSvcTradeData);
                // 处理属性
                if (IDataUtil.isNotEmpty(attrSet))
                {
                    for (int j = 0; j < attrSet.size(); j++)
                    {
                        AttrTradeData newAttrTradeData = new AttrTradeData(attrSet.getData(j));
                        newAttrTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                        newAttrTradeData.setStartDate(SysDateMgr.getSysTime());
                        newAttrTradeData.setEndDate(SysDateMgr.getTheLastTime());
                        newAttrTradeData.setRemark("代表号修改");
                        newAttrTradeData.setInstId(SeqMgr.getInstId());
                        newAttrTradeData.setRelaInstId(newRelaInstId);
                        btd.add(changeTrunkMainUserRD.getNewMianSn(), newAttrTradeData);
                    }
                }
            }
        }
    }

    public void createTelephoneTrade(BusiTradeData btd, String userIdNew) throws Exception
    {
        ChangeTrunkMainUserReqData changeTrunkMainUserRD = (ChangeTrunkMainUserReqData) btd.getRD();

        IDataset newMainTelRes = UserTelephoeInfoQry.getUserTelephoneByUserId(userIdNew);
        IDataset oldMainTelRes = UserTelephoeInfoQry.getUserTelephoneByUserId(changeTrunkMainUserRD.getUca().getUserId());
        if (IDataUtil.isEmpty(oldMainTelRes))
            CSAppException.apperr(UserTelephoeException.CRM_TELEPHOE_4, changeTrunkMainUserRD.getUca().getSerialNumber());

        TelephoneTradeData oldTelePhoneTD = new TelephoneTradeData(oldMainTelRes.getData(0));
        TelephoneTradeData newTelePhoneTD = new TelephoneTradeData(oldMainTelRes.getData(0));

        oldTelePhoneTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
        oldTelePhoneTD.setEndDate(SysDateMgr.getSysTime());
        oldTelePhoneTD.setRemark("代表号修改");
        btd.add(changeTrunkMainUserRD.getUca().getSerialNumber(), oldTelePhoneTD);

        if (IDataUtil.isEmpty(newMainTelRes))
        {
            newTelePhoneTD.setUserId(userIdNew);
            newTelePhoneTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
            newTelePhoneTD.setInstId(SeqMgr.getInstId());
            newTelePhoneTD.setStartDate(SysDateMgr.getSysTime());
            newTelePhoneTD.setEndDate(SysDateMgr.getTheLastTime());
            newTelePhoneTD.setRemark("代表号修改");
            btd.add(changeTrunkMainUserRD.getNewMianSn(), newTelePhoneTD);
        }

    }
}
