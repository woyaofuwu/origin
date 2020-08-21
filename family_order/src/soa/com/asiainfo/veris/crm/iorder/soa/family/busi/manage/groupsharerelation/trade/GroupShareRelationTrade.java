package com.asiainfo.veris.crm.iorder.soa.family.busi.manage.groupsharerelation.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyRolesEnum;
import com.asiainfo.veris.crm.iorder.pub.family.exception.FamilyException;
import com.asiainfo.veris.crm.iorder.soa.family.busi.manage.groupsharerelation.requestdata.GroupShareRelationReqData;
import com.asiainfo.veris.crm.iorder.soa.family.common.query.FamilyMemberChaInfoQry;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.FamilyMemberChaTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

/**
 * @auther : lixx9
 * @createDate :  2020/7/20
 * @describe :
 */
public class GroupShareRelationTrade extends BaseTrade implements ITrade {

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception {

        GroupShareRelationReqData reqData = (GroupShareRelationReqData) btd.getRD();
        String memberRoleCode = reqData.getMemberRoleCode();

        if(!FamilyRolesEnum.PHONE.getRoleCode().equals(memberRoleCode)){
            return ;
        }

        UcaData memberUca = reqData.getUca();//成员UCA

        String shareTag = reqData.getTag();
        String relInstId = reqData.getMemberRelInstId();
        UcaData familyUca = UcaDataFactory.getNormalUca(reqData.getFamilySerialNumber());
        if (shareTag.equals(BofConst.MODIFY_TAG_ADD)) {


            String shareInstId =SeqMgr.getInstId();
            this.addShareRelation(btd, familyUca.getSerialNumber(), memberUca, familyUca.getUserId(),shareInstId);
            this.addFamilyMemberChaTrade(familyUca.getUserId(),memberUca,relInstId,shareInstId,btd);

        }
        if (shareTag.equals(BofConst.MODIFY_TAG_DEL)) {

            this.delshareRelation(btd, memberUca, familyUca.getUserId());
            this.delFamilyMemberChaTrade(memberUca.getUserId(),memberUca.getSerialNumber(),relInstId,btd);

        }
    }

    //删除共享关系
    private void delshareRelation(BusiTradeData btd, UcaData memberData , String userIdA) throws Exception{

        //校验是否加入共享关系，relation_uu
        IData relabtdata = RelaUUInfoQry.getRelationUUByPk(userIdA,memberData.getUserId(), FamilyConstants.FAMILY_SHARE_RELATION_TYPE_CODE, null);
        if (IDataUtil.isEmpty(relabtdata))
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_3, memberData.getUserId());
        }
        relabtdata.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
        relabtdata.put("END_DATE", btd.getRD().getAcceptTime());
        RelationTradeData delRelation=new RelationTradeData(relabtdata);
        btd.add(btd.getRD().getUca().getSerialNumber(), delRelation);

        //发送短信
        String noticeContent = "尊敬的用户您好! 您受理了家庭手机成员共享关系管理业务，不再享有家庭手机成员语音、流量共享功能";
        sendSms(memberData.getSerialNumber(),memberData.getUserId(),memberData.getBrandCode(),noticeContent);

    }
    //新增共享关系
    private void addShareRelation(BusiTradeData btd,String familySn,UcaData memberData,String familyUserId,String shareInstId)throws Exception {

        RelationTradeData addRelation=new RelationTradeData();
        addRelation.setEndDate(SysDateMgr.END_DATE_FOREVER);
        addRelation.setInstId(shareInstId);
        addRelation.setModifyTag(BofConst.MODIFY_TAG_ADD);
        addRelation.setRelationTypeCode(FamilyConstants.FAMILY_SHARE_RELATION_TYPE_CODE);
        addRelation.setUserIdB(memberData.getUserId());
        addRelation.setSerialNumberB(memberData.getSerialNumber());
        addRelation.setUserIdA(familyUserId);
        addRelation.setSerialNumberA(familySn);
        addRelation.setRoleCodeA("0");
        addRelation.setRoleCodeB("2");// 2表示副卡
        addRelation.setOrderno("0");
        addRelation.setStartDate(btd.getRD().getAcceptTime());
        btd.add(btd.getRD().getUca().getSerialNumber(), addRelation);

        //发送短信
        String noticeContent = "尊敬的用户您好! 您受理了家庭手机成员共享关系管理业务，享有家庭手机成员语音、流量共享功能";
        sendSms(memberData.getSerialNumber(),memberData.getUserId(),memberData.getBrandCode(),noticeContent);

    }

    /**
     * @Description: 构建TF_B_TRADE_MEMBER_CHA台账信息
     * @Param: [familyUserId, memberUca, relInstId, btd]
     * @return: void
     * @Author: lixx9
     * @Date: 16:22
     */ 
    private void addFamilyMemberChaTrade(String familyUserId,UcaData memberUca,String relInstId,String shareInstId,BusiTradeData btd) throws  Exception{

        FamilyMemberChaTradeData newfamilyMemberChaTradeData = new FamilyMemberChaTradeData();
        newfamilyMemberChaTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        newfamilyMemberChaTradeData.setInstId(SeqMgr.getInstId());
        newfamilyMemberChaTradeData.setFamilyUserId(familyUserId);
        newfamilyMemberChaTradeData.setMemberUserId(memberUca.getUserId());
        newfamilyMemberChaTradeData.setChaType(FamilyConstants.CHA_TYPE.MANAGER);
        newfamilyMemberChaTradeData.setRelInstId(relInstId);
        newfamilyMemberChaTradeData.setChaCode(FamilyConstants.FamilyMemCha.FAMILY_SHARE.getValue());
        newfamilyMemberChaTradeData.setChaValue(shareInstId);
        newfamilyMemberChaTradeData.setStartDate(btd.getRD().getAcceptTime());
        newfamilyMemberChaTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
        newfamilyMemberChaTradeData.setRemark("家庭手机成员加入共享关系新增属性信息");

        btd.add(memberUca.getSerialNumber(), newfamilyMemberChaTradeData);

    }
    /**
     * @Description: 删除TF_B_TRADE_MEMBER_CHA台账信息
     * @Param: [memberUserId, relInstId, btd]
     * @return: void
     * @Author: lixx9
     * @Date: 16:23
     */ 
    private void delFamilyMemberChaTrade(String memberUserId,String memberSn,String relInstId,BusiTradeData btd) throws  Exception{
        IDataset familyMemberChaInfo = FamilyMemberChaInfoQry.queryNowValidFamilyMemberChasByMemberUserIdAndRelInstId(memberUserId,relInstId);
        if(IDataUtil.isEmpty(familyMemberChaInfo)){
            return;
        }
        FamilyMemberChaTradeData newfamilyMemberChaTradeData = new FamilyMemberChaTradeData(familyMemberChaInfo.first());
        newfamilyMemberChaTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
        newfamilyMemberChaTradeData.setRemark("家庭手机成员退出共享关系终止属性信息");
        btd.add(memberSn,newfamilyMemberChaTradeData);
    }


    /**
     * @Description: 发送短信(办理业务的成员都会发送，现在trade里发)
     * @Param: 
     * @return: 
     * @Author: lixx9
     * @Date: 17:12
     */
    private void sendSms(String serialNumber,String userId,String brandCode, String noticeContent) throws Exception
    {
        IData smsData = new DataMap();
        smsData.put("RECV_OBJECT", serialNumber);
        smsData.put("NOTICE_CONTENT", noticeContent);
        smsData.put("BRAND_CODE", brandCode);
        smsData.put("RECV_ID", userId);
        SmsSend.insSms(smsData);
    }

}
