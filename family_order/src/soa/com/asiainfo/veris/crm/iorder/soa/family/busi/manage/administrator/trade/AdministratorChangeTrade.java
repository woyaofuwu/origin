package com.asiainfo.veris.crm.iorder.soa.family.busi.manage.administrator.trade;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.iorder.pub.consts.KeyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.exception.FamilyException;
import com.asiainfo.veris.crm.iorder.soa.family.busi.manage.administrator.requestdata.AdministratorChangeReqData;
import com.asiainfo.veris.crm.iorder.soa.family.common.query.FamilyCustInfoQry;
import com.asiainfo.veris.crm.iorder.soa.family.common.query.FamilyMemberChaInfoQry;
import com.asiainfo.veris.crm.iorder.soa.family.common.query.FamilyUserMemberQuery;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustFamilyTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.FamilyMemberChaTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;

/**
 * @auther : lixx9
 * @createDate :  2020/8/3
 * @describe :
 */
public class AdministratorChangeTrade extends BaseTrade implements ITrade {


    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception {

        AdministratorChangeReqData reqData = (AdministratorChangeReqData) btd.getRD();

        UcaData oldAdminUca = reqData.getUca();//管理员UCA

        UcaData newAdminUca = UcaDataFactory.getNormalUca(reqData.getMemberSerialNumber()); //新管理员uca

        String familyUserId = reqData.getFamilyUserId();

        //构建家庭客户资料台账表
        buildFamilyCustTrade(oldAdminUca, newAdminUca,btd);
        //构建家庭成员关系特征台账表
        buildfamilyMemberChaTrade(oldAdminUca, newAdminUca,familyUserId,btd);


    }

    /**
     * @Description: 构建TF_F_CUST_FAMILY台账信息
     * @Param: [oldAdminUca, newAdminUca]
     * @return: void
     * @Author: lixx9
     * @Date: 10:34
     */
    private void buildFamilyCustTrade(UcaData oldAdminUca, UcaData newAdminUca,BusiTradeData btd) throws Exception {

        IDataset familyCustInfo = FamilyCustInfoQry.qryFamilyCustInfoBySn(oldAdminUca.getSerialNumber());

        if (IDataUtil.isEmpty(familyCustInfo)) {
            CSAppException.apperr(FamilyException.CRM_FAMILY_6,oldAdminUca.getSerialNumber());
        }

        CustFamilyTradeData newFamilyTrade = new CustFamilyTradeData(familyCustInfo.first());

        newFamilyTrade.setModifyTag(BofConst.MODIFY_TAG_UPD);
        newFamilyTrade.setCustName(newAdminUca.getCustomer().getCustName());
        newFamilyTrade.setSerialNumber(newAdminUca.getSerialNumber());
        newFamilyTrade.setHeadCustId(newAdminUca.getCustId());
        newFamilyTrade.setHeadPsptId(newAdminUca.getCustomer().getPsptId());
        newFamilyTrade.setHeadTypeCode(newAdminUca.getCustomer().getPsptTypeCode());
        btd.add(oldAdminUca.getSerialNumber(),newFamilyTrade);
    }

    /**
     * @Description: 构建TF_B_TRADE_MEMBER_CHA台账信息
     * @Param: [oldAdminUca, newAdminUca, familyUserId, btd]
     * @return: void
     * @Author: lixx9
     * @Date: 11:58
     */
    private void buildfamilyMemberChaTrade(UcaData oldAdminUca, UcaData newAdminUca, String familyUserId,BusiTradeData btd) throws Exception {

        //查询旧管理员属性信息
        IDataset oldfamilyMemberChaInfo = FamilyMemberChaInfoQry.queryNowValidByMemberUserIdAndFamilyUserIdAndChaCode(oldAdminUca.getUserId(), familyUserId, FamilyConstants.FamilyMemCha.FAMILY_MANAGER.getValue());
        if(IDataUtil.isEmpty(oldfamilyMemberChaInfo)){
            CSAppException.apperr(FamilyException.CRM_FAMILY_6,oldAdminUca.getSerialNumber());
        }
        //查询新管理员是否加入该家庭
        IDataset newfamilyMemberInfo = FamilyUserMemberQuery.queryinfoByFamilyUserIdMemebrUserId(familyUserId,newAdminUca.getUserId());
        if(IDataUtil.isEmpty(newfamilyMemberInfo)){
            CSAppException.apperr(FamilyException.CRM_FAMILY_14,newAdminUca.getSerialNumber());
        }

        //删除旧管理员信息
        FamilyMemberChaTradeData oldfamilyMemberChaTradeData = new FamilyMemberChaTradeData(oldfamilyMemberChaInfo.first());
        oldfamilyMemberChaTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);

        btd.add(oldAdminUca.getSerialNumber(), oldfamilyMemberChaTradeData);

        //新增新管理员信息
        FamilyMemberChaTradeData newfamilyMemberChaTradeData = new FamilyMemberChaTradeData();
        newfamilyMemberChaTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        newfamilyMemberChaTradeData.setInstId(SeqMgr.getInstId());
        newfamilyMemberChaTradeData.setFamilyUserId(familyUserId);
        newfamilyMemberChaTradeData.setMemberUserId(newAdminUca.getUserId());
        newfamilyMemberChaTradeData.setChaType(FamilyConstants.CHA_TYPE.MANAGER);
        newfamilyMemberChaTradeData.setRelInstId(newfamilyMemberInfo.first().getString(KeyConstants.INST_ID));
        newfamilyMemberChaTradeData.setChaCode(FamilyConstants.FamilyMemCha.FAMILY_MANAGER.getValue());
        newfamilyMemberChaTradeData.setChaValue(FamilyConstants.SWITCH.YES);
        newfamilyMemberChaTradeData.setStartDate(btd.getRD().getAcceptTime());
        newfamilyMemberChaTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
        btd.add(oldAdminUca.getSerialNumber(), newfamilyMemberChaTradeData);

    }

}
