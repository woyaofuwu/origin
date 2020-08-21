package com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter.order.filter;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

/**
 * @author 梁端刚
 * @version V1.0
 * @date 2019/12/12 10:53
 */
public class BenefitUseFilter implements IFilterIn {
    /**
     * 必输参数检查
     *
     * @param input
     * @throws Exception
     */
    public void checkInputData(IData input) throws Exception{
        IDataUtil.chkParam(input, "REL_ID");
        IDataUtil.chkParam(input, "DISCNT_CODE");
        IDataUtil.chkParam(input, "START_DATE");
    }

    @Override
    public void transferDataInput(IData input) throws Exception {
        this.checkInputData(input);
        //根据REL_ID找到对应SERIAL_NUMBER
        IDataset UserOtherInfos = UserOtherInfoQry.getUserIdByRelID(PersonConst.BENEFIT_TAG, input.getString("REL_ID")
                , null, input.getString("DISCNT_CODE"));
        if(IDataUtil.isEmpty(UserOtherInfos)){
            CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据权益关联标识未找到用户");
        }
        IDataset userInfos = UserInfoQry.selUserInfo(UserOtherInfos.first().getString("USER_ID"));
        input.put("SERIAL_NUMBER",userInfos.first().getString("SERIAL_NUMBER"));
    }
}
