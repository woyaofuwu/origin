package com.asiainfo.veris.crm.order.soa.person.busi.credittrade.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.KeyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyRolesEnum;
import com.asiainfo.veris.crm.iorder.soa.family.common.query.FamilyCustInfoQry;
import com.asiainfo.veris.crm.iorder.soa.family.common.query.FamilyUserMemberQuery;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.ChangeSvcStateRegSVC;

/**
 * @auther : lixx9
 * @createDate :  2020/8/13
 * @describe :家庭欠费停机，家庭代付成员也关联停机
 */
public class FamilyMemberDealTradeAction implements ITradeAction {


    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {

        UcaData familyUca = btd.getRD().getUca();

        //根据 TF_F_CUST_FAMILY 判断是否家庭客户
        IDataset familyCustInfos = FamilyCustInfoQry.qryFamilyCustInfoByHomeCustId(familyUca.getCustId());
        if(IDataUtil.isEmpty(familyCustInfos)){
            return;
        }
        String familyAcctId = familyUca.getAcctId();
        String familyUserId = familyUca.getUserId();
        //查询家庭所有手机成员；如果没有跳过该逻辑；（页面上选择代付关系只有手机能选，手机选上底下的宽带，固话，魔百和都默认代付
        // 所以只用查询手机成员，然后调用原有的欠费接口，里面的action会处理宽带，固话，魔百和）
        IDataset familyPhoneMembers = FamilyUserMemberQuery.queryMembersByFamilyUserIdAndRole(familyUserId, FamilyRolesEnum.PHONE.getRoleCode());
        if(IDataUtil.isEmpty(familyPhoneMembers)){
            return ;
        }

        IDataset familyPayRelaInfos = PayRelaInfoQry.getPayRelaBySelbyAcctDa1(familyAcctId, FamilyConstants.FAMILY_PAY_DEFAULT_TAG,"1",null);
        if(IDataUtil.isEmpty(familyPayRelaInfos)){
            return ;
        }
        IData input = new DataMap();
        ChangeSvcStateRegSVC modiSvcState = new ChangeSvcStateRegSVC();

        String memberTradeTypeCode = "";
        if(FamilyConstants.FamilyTradeType.FAMILY_CREDIT_OPEN.getValue().equals(btd.getTradeTypeCode())){

            memberTradeTypeCode=FamilyConstants.CREDIT_TRADE_TYPE.CREDIT_OPEN;

        }else if (FamilyConstants.FamilyTradeType.FAMILY_CREDIT_STOP.getValue().equals(btd.getTradeTypeCode())){

            memberTradeTypeCode=FamilyConstants.CREDIT_TRADE_TYPE.CREDIT_STOP;
        }
        if(StringUtils.isBlank(memberTradeTypeCode)){
            return;
        }

        //家庭代付的成员关联停开机
        for(int i=0;i<familyPhoneMembers.size();i++){

            IData familyPhoneMember = familyPhoneMembers.getData(i);

            for (int j=0;j<familyPayRelaInfos.size();j++){
                IData familyPatRelation = familyPayRelaInfos.getData(j);
                if(familyPhoneMember.get(KeyConstants.MEMBER_USER_ID).equals(familyPatRelation.getString(KeyConstants.USER_ID))){
                    input.clear();
                    input.put(KeyConstants.TRADE_TYPE_CODE,memberTradeTypeCode);
                    input.put(KeyConstants.USER_ID,familyPhoneMember.getString("MEMBER_USER_ID",""));
                    input.put(KeyConstants.SERIAL_NUMBER,familyPhoneMember.getString("MEMBER_SERIAL_NUM",""));
                    modiSvcState.tradeReg(input);
                }
            }
        }
    }
}
