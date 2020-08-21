package com.asiainfo.veris.crm.iorder.soa.family.common.svc;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.iorder.pub.consts.KeyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyRolesEnum;
import com.asiainfo.veris.crm.iorder.pub.family.exception.FamilyException;
import com.asiainfo.veris.crm.iorder.soa.family.common.query.FamilyCustInfoQry;
import com.asiainfo.veris.crm.iorder.soa.family.common.query.FamilyUserMemberQuery;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

/**
 * @auther : lixx9
 * @createDate :  2020/7/21
 * @describe :
 */
public class FamilyCustInfoQrySVC extends CSBizService {

    private static final long serialVersionUID = 1509138745145L;

    /* 
     * @Description: 根据户主手机号码查询家庭客户信息
     * @Param: [input]
     * @return: com.ailk.common.data.IDataset
     * @Author: lixx9
     * @Date: 10:16
     */ 
    public IDataset familyCustInfoQryBySn(IData input) throws Exception {

        String serialNumber = input.getString("SERIAL_NUMBER","");

        IDataset familyCustInfo = FamilyCustInfoQry.qryFamilyCustInfoBySn(serialNumber);

        if (IDataUtil.isEmpty(familyCustInfo)){

            CSAppException.apperr(FamilyException.CRM_FAMILY_4, serialNumber);

        }
        return familyCustInfo;

    }

    /* 
     * @Description: 查询家庭用户下手机成员的共享关系（先按UU表走）
     * @Param: [input]
     * @return: com.ailk.common.data.IDataset
     * @Author: lixx9
     * @Date: 15:07
     */ 
    public IDataset familyPhoneMemberShareInfoQry(IData input) throws  Exception {

        String adminUserId = input.getString(KeyConstants.MEMBER_USER_ID,"");
        //查询家庭下所有的生效的手机成员
        IDataset familyPhoneMembers = FamilyUserMemberQuery.queryFamilyMemInfoByMemNumAndRole(adminUserId, FamilyRolesEnum.PHONE.getRoleCode());
        if(IDataUtil.isEmpty(familyPhoneMembers)){
            CSAppException.apperr(FamilyException.CRM_FAMILY_11);
        }

        String userIdA = familyPhoneMembers.first().getString("FAMILY_USER_ID");

        //查询家庭下所有生效的流量/语音共享关系
        IDataset familyShareMemInfos = RelaUUInfoQry.getRelaUUInfoByUserIda(userIdA,FamilyConstants.FAMILY_SHARE_RELATION_TYPE_CODE,null);
        IDataset familyShareInfos = new DatasetList();
        //遍历得到家庭下所有手机成员是否有生效的流量/语音共享关系
        for(int i=0;i<familyPhoneMembers.size();i++){
            IData shareInfo = new DataMap();
            String memberUserId = familyPhoneMembers.getData(i).getString("MEMBER_USER_ID");
            shareInfo.put("MEMBER_SERIAL_NUMBER",familyPhoneMembers.getData(i).getString("MEMBER_SERIAL_NUM"));
            shareInfo.put("TAG","1");
            for(int j=0 ; j< familyShareMemInfos.size() ; j++){
                if(memberUserId.equals(familyShareMemInfos.getData(j).getString("USER_ID_B"))){
                    shareInfo.put("TAG","0");
                    break;
                }
            }
            familyShareInfos.add(shareInfo);
        }

        return familyShareInfos;

    }

    public IDataset familyMemberPayRelationQry(IData input) throws  Exception {

        String adminUserId = input.getString(KeyConstants.MEMBER_USER_ID,"");

        //查询家庭下所有的生效的手机成员
        IDataset familyMembers = FamilyUserMemberQuery.queryFamilyMemInfoByMemNum(adminUserId);
        if(IDataUtil.isEmpty(familyMembers)){
            CSAppException.apperr(FamilyException.CRM_FAMILY_11);
        }
        String family_user_id = familyMembers.first().getString("FAMILY_USER_ID");

        IData familyUcaInfo =UcaInfoQry.qryUserInfoByUserId(family_user_id);
        if(IDataUtil.isEmpty(familyUcaInfo)){
            CSAppException.apperr(FamilyException.CRM_FAMILY_16,family_user_id);
        }

        String familyAcctId =familyUcaInfo.getString("ACCT_ID");

        IDataset familyPayRelaInfos = PayRelaInfoQry.getPayRelaBySelbyAcctDa1(familyAcctId,FamilyConstants.FAMILY_PAY_DEFAULT_TAG,"1",null);

        IDataset familyPayInfos = new DatasetList();
        //遍历得到家庭下所有实体成员代付关系
        for(int i=0;i<familyMembers.size();i++){
            IData shareInfo = new DataMap();
            String memberUserId = familyMembers.getData(i).getString("MEMBER_USER_ID");
            shareInfo.put("MEMBER_SERIAL_NUMBER",familyMembers.getData(i).getString("MEMBER_SERIAL_NUM"));
            shareInfo.put("TAG","1");
            shareInfo.put("MEMBER_ROLE_CODE",familyMembers.getData(i).getString("MEMBER_ROLE_CODE"));
            for(int j=0 ; j< familyPayRelaInfos.size() ; j++){
                if(memberUserId.equals(familyPayRelaInfos.getData(j).getString("USER_ID"))){
                    shareInfo.put("TAG","0");
                    break;
                }
            }
            familyPayInfos.add(shareInfo);
        }

        return familyPayInfos;
    }


}
