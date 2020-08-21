package com.asiainfo.veris.crm.iorder.soa.family.busi.manage.payrelation.trade;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyRolesEnum;
import com.asiainfo.veris.crm.iorder.soa.family.busi.manage.payrelation.requestdata.FamilyPayRelationReqData;
import com.asiainfo.veris.crm.iorder.soa.family.common.query.FamilyMemberChaInfoQry;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.FamilyMemberChaTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

/**
 * @auther : lixx9
 * @createDate :  2020/8/4
 * @describe :
 */
public class FamilyPayRelationTrade extends BaseTrade implements ITrade {

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception {

        FamilyPayRelationReqData reqData = (FamilyPayRelationReqData) btd.getRD();

        String memberRoleCode = reqData.getMemberRoleCode();
        if(StringUtils.isBlank(memberRoleCode)){
            return ;
        }

        UcaData memberUca = reqData.getUca();//成员UCA

        String payTag = reqData.getTag();
        String relInstId = reqData.getMemberRelInstId();
        UcaData familyUca = UcaDataFactory.getNormalUca(reqData.getFamilySerialNumber());//家庭UCA

        if (payTag.equals(BofConst.MODIFY_TAG_ADD)) {

            String payInstId = SeqMgr.getInstId();
            this.addPayRelation(btd,memberUca.getUserId(),payInstId);
            //处理特殊成员的UU关系
            this.dealAddSpecialMember(btd,memberUca,memberRoleCode);
            //处理familt_member_cha表
            this.addFamilyMemberChaTrade(familyUca.getUserId(),memberUca,relInstId,payInstId,btd);

        }
        if (payTag.equals(BofConst.MODIFY_TAG_DEL)) {

            this.delPayRelation(btd,memberUca.getUserId());
            //处理特殊成员的UU关系
            this.dealDelSpecialMember(btd,memberUca,memberRoleCode);

            this.delFamilyMemberChaTrade(memberUca.getUserId(),memberUca.getSerialNumber(),relInstId,btd);
        }
    }

    private void addPayRelation(BusiTradeData btd,String memberUserId,String payInstId) throws Exception {

        PayRelationTradeData payRelaTd = new PayRelationTradeData();

        payRelaTd.setAcctId(btd.getRD().getUca().getAcctId());
        payRelaTd.setUserId(memberUserId);
        payRelaTd.setStartCycleId(SysDateMgr.getDateForYYYYMMDD(btd.getRD().getAcceptTime()));
        payRelaTd.setEndCycleId(SysDateMgr.getEndCycle20501231());
        payRelaTd.setPayitemCode("-1");
        payRelaTd.setAcctPriority("0");
        payRelaTd.setUserPriority("0");
        payRelaTd.setAddupMethod("0");
        payRelaTd.setAddupMonths("0");
        payRelaTd.setBindType("1");
        payRelaTd.setDefaultTag(FamilyConstants.FAMILY_PAY_DEFAULT_TAG);
        payRelaTd.setActTag("1");
        payRelaTd.setLimit("0");
        payRelaTd.setLimitType("0");
        payRelaTd.setComplementTag("0");
        payRelaTd.setInstId(payInstId);
        payRelaTd.setModifyTag(BofConst.MODIFY_TAG_ADD);
        payRelaTd.setRemark("家庭成员新增家庭代付关系");
        btd.add(btd.getRD().getUca().getSerialNumber(), payRelaTd);

    }

    private void delPayRelation(BusiTradeData btd,String memberUserId) throws Exception {

        //查询成员是否有家庭代付关系
        IDataset memberPayInfo = PayRelaInfoQry.qryPayRelaByUserAcctIdDefTag(memberUserId,btd.getRD().getUca().getAcctId(),FamilyConstants.FAMILY_PAY_DEFAULT_TAG);

        if(IDataUtil.isEmpty(memberPayInfo)){
            return ;
        }
        PayRelationTradeData payRelaTd = new PayRelationTradeData(memberPayInfo.first());
        payRelaTd.setModifyTag(BofConst.MODIFY_TAG_DEL);
        payRelaTd.setRemark("家庭成员退出家庭代付关系");

        btd.add(btd.getRD().getUca().getSerialNumber(), payRelaTd);

    }
    /**
     * @Description: 处理特殊成员新增代付关系 额外处理UU关系
     * @Param: [btd, familyUca, memberUca, memberRoleCode]
     * @return: void
     * @Author: lixx9
     * @Date: 20:45
     */ 
    private void dealAddSpecialMember(BusiTradeData btd,UcaData memberUca,String memberRoleCode) throws Exception{

        //宽带成员 取消47关系 新增48
        if(FamilyRolesEnum.WIDENET.getRoleCode().equals(memberRoleCode)){
            IDataset relationInfos = RelaUUInfoQry.getRelationUUInfoByDeputySn(memberUca.getUserId(),FamilyConstants.WIDNET_PHONE_RELATION_TYPE_CODE,null);
            RelationTradeData relationTradeData = null;
            if(IDataUtil.isEmpty(relationInfos)){
                //如果UU资料表里没有47 则认为有可能是家庭开户进来的 需要查询关联工单的UU表中是否有47关系
                relationTradeData =  getUuInfoBySnAndRelationTypeCode(FamilyConstants.WIDNET_PHONE_RELATION_TYPE_CODE);
            }else {
                relationTradeData = new RelationTradeData(relationInfos.first());
            }

            //如果资料表和台账表都查不到UU关系，则说明没有选择代付关系
            if(relationTradeData == null){
                return ;
            }
            //结束47关系
            RelationTradeData relaTd47 = relationTradeData.clone();
            relaTd47.setModifyTag(BofConst.MODIFY_TAG_DEL);
            relaTd47.setEndDate(btd.getRD().getAcceptTime());
            relaTd47.setRemark("家庭宽带成员新增代付关系,结束47关系");
            btd.add(btd.getRD().getUca().getSerialNumber(),relaTd47);

            //新增48关系
            RelationTradeData  relaTd48 = relationTradeData.clone();
            relaTd48.setModifyTag(BofConst.MODIFY_TAG_ADD);
            relaTd48.setRelationTypeCode(FamilyConstants.FAMILY_WIDENET_MEMBER_PAY_CODE);
            relaTd48.setEndDate(SysDateMgr.END_DATE_FOREVER);
            relaTd48.setInstId(SeqMgr.getInstId());
            relaTd48.setRemark("家庭宽带成员新增代付关系,新增48关系");
            btd.add(btd.getRD().getUca().getSerialNumber(),relaTd48);

            //ims固话成员 取消MS 新增SM
        }else if(FamilyRolesEnum.IMS.getRoleCode().equals(memberRoleCode)){
            IDataset relationInfos = RelaUUInfoQry.getRelationUUInfoByDeputySn(memberUca.getUserId(),FamilyConstants.IMS_PHONE_RELATION_TYPE_CODE,null);
            RelationTradeData relationTradeData = null;
            if(IDataUtil.isEmpty(relationInfos)){
                //如果UU资料表里没有47 则认为有可能是家庭开户进来的 需要查询关联工单的UU表中是否有47关系
                relationTradeData =  getUuInfoBySnAndRelationTypeCode(FamilyConstants.IMS_PHONE_RELATION_TYPE_CODE);

            }else {
                relationTradeData = new RelationTradeData(relationInfos.first());
            }
            //如果资料表和台账表都查不到UU关系，则说明没有选择代付关系
            if(relationTradeData == null){
                return ;
            }
            //结束MS关系
            RelationTradeData relaTdMs = relationTradeData.clone();
            relaTdMs.setModifyTag(BofConst.MODIFY_TAG_DEL);
            relaTdMs.setEndDate(btd.getRD().getAcceptTime());
            relaTdMs.setRemark("家庭IMS固话成员新增代付关系,结束MS关系");
            btd.add(btd.getRD().getUca().getSerialNumber(),relaTdMs);

            //新增SM关系
            RelationTradeData relaTdSm = relationTradeData.clone();
            relaTdSm.setModifyTag(BofConst.MODIFY_TAG_ADD);
            relaTdSm.setRelationTypeCode(FamilyConstants.FAMILY_IMS_MEMBER_PAY_CODE);
            relaTdSm.setEndDate(SysDateMgr.END_DATE_FOREVER);
            relaTdSm.setInstId(SeqMgr.getInstId());
            relaTdSm.setRemark("家庭宽带成员新增代付关系,新增SM关系");
            btd.add(btd.getRD().getUca().getSerialNumber(),relaTdSm);

        }
    }
    /**
     * @Description: 处理特殊成员删除代付关系 额外处理UU关系
     * @Param: [btd, familyUca, memberUca, memberRoleCode]
     * @return: void
     * @Author: lixx9
     * @Date: 20:46
     */
    private void dealDelSpecialMember(BusiTradeData btd,UcaData memberUca,String memberRoleCode) throws Exception{

        //宽带成员 取消48关系 恢复47
        if(FamilyRolesEnum.WIDENET.getRoleCode().equals(memberRoleCode)){

            IDataset relation48Infos = RelaUUInfoQry.getRelationUUInfoByDeputySn(memberUca.getUserId(),FamilyConstants.FAMILY_WIDENET_MEMBER_PAY_CODE,null);
            if(IDataUtil.isEmpty(relation48Infos)){
                return ;
            }
            RelationTradeData rtd = new RelationTradeData(relation48Infos.first());
            //结束48关系
            RelationTradeData relaTd48 = rtd.clone();
            relaTd48.setModifyTag(BofConst.MODIFY_TAG_DEL);
            relaTd48.setEndDate(btd.getRD().getAcceptTime());
            relaTd48.setRemark("家庭宽带成员取消代付关系,结束48关系");
            btd.add(btd.getRD().getUca().getSerialNumber(),relaTd48);

            //恢复47关系
            RelationTradeData relaTd47 = rtd.clone();
            relaTd47.setRelationTypeCode(FamilyConstants.WIDNET_PHONE_RELATION_TYPE_CODE);
            relaTd47.setModifyTag(BofConst.MODIFY_TAG_ADD);
            relaTd47.setStartDate(btd.getRD().getAcceptTime());
            relaTd47.setEndDate(SysDateMgr.END_DATE_FOREVER);
            relaTd47.setInstId(SeqMgr.getInstId());
            relaTd47.setRemark("家庭宽带成员取消代付关系,恢复47关系");

            //ims固话成员 取消SM 恢复MS
        }else if(FamilyRolesEnum.IMS.getRoleCode().equals(memberRoleCode)){

            IDataset relationSmInfos = RelaUUInfoQry.getRelationUUInfoByDeputySn(memberUca.getUserId(),FamilyConstants.FAMILY_IMS_MEMBER_PAY_CODE,null);
            if(IDataUtil.isEmpty(relationSmInfos)){
                return ;
            }
            RelationTradeData rtd = new RelationTradeData(relationSmInfos.first());

            //结束SM关系
            RelationTradeData relaTdSm = rtd.clone();
            relaTdSm.setModifyTag(BofConst.MODIFY_TAG_DEL);
            relaTdSm.setEndDate(btd.getRD().getAcceptTime());
            relaTdSm.setRemark("家庭IMS固话成员取消代付关系,结束SM关系");
            btd.add(btd.getRD().getUca().getSerialNumber(),relaTdSm);

            //恢复MS关系
            RelationTradeData relaTdMs = rtd.clone();
            relaTdMs.setModifyTag(BofConst.MODIFY_TAG_ADD);
            relaTdMs.setRelationTypeCode(FamilyConstants.IMS_PHONE_RELATION_TYPE_CODE);
            relaTdMs.setStartDate(btd.getRD().getAcceptTime());
            relaTdSm.setInstId(SeqMgr.getInstId());
            relaTdMs.setEndDate(SysDateMgr.END_DATE_FOREVER);
            relaTdMs.setRemark("家庭IMS固话成员取消代付关系,恢复MS关系");
        }
    }

    /**
     * @Description: 构建TF_B_TRADE_MEMBER_CHA台账信息
     * @Param: [familyUserId, memberUca, relInstId, btd]
     * @return: void
     * @Author: lixx9
     * @Date: 16:22
     */
    private void addFamilyMemberChaTrade(String familyUserId,UcaData memberUca,String relInstId,String payInstId,BusiTradeData btd) throws  Exception{

        FamilyMemberChaTradeData newfamilyMemberChaTradeData = new FamilyMemberChaTradeData();
        newfamilyMemberChaTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        newfamilyMemberChaTradeData.setInstId(SeqMgr.getInstId());
        newfamilyMemberChaTradeData.setFamilyUserId(familyUserId);
        newfamilyMemberChaTradeData.setMemberUserId(memberUca.getUserId());
        newfamilyMemberChaTradeData.setChaType(FamilyConstants.CHA_TYPE.MANAGER);
        newfamilyMemberChaTradeData.setRelInstId(relInstId);
        newfamilyMemberChaTradeData.setChaCode(FamilyConstants.FamilyMemCha.FAMILY_PAY.getValue());
        newfamilyMemberChaTradeData.setChaValue(payInstId);
        newfamilyMemberChaTradeData.setStartDate(btd.getRD().getAcceptTime());
        newfamilyMemberChaTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
        newfamilyMemberChaTradeData.setRemark("家庭成员加入代付关系新增属性信息");

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
        newfamilyMemberChaTradeData.setRemark("家庭成员退出代付关系终止属性信息");

        btd.add(memberSn, newfamilyMemberChaTradeData);

    }

    /**
     * @Description: 家庭开户关联业务很多 取出600工单登记的UU关系  47 和 MS  加以特殊处理
     * @Param: [btd, relationTypeCode]
     * @return: 
     * @Author: lixx9
     * @Date: 16:03
     */ 
    private RelationTradeData getUuInfoBySnAndRelationTypeCode(String relationTypeCode){

        List<BusiTradeData> btds =  DataBusManager.getDataBus().getBtds();

        for(BusiTradeData btd:btds){
            List<RelationTradeData> rtds = btd.getTradeDatas(TradeTableEnum.TRADE_RELATION);
            if(rtds.isEmpty()){
                continue;
            }
            for(RelationTradeData rtd : rtds){
                if(relationTypeCode.equals(rtd.getRelationTypeCode()) && "2".equals(rtd.getRoleCodeB())){
                    return rtd;
                }
            }
        }
        return null;
    }
}
