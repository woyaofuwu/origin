
package com.asiainfo.veris.crm.order.soa.person.busi.specialtrademgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class ProtectPassInfoSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 密保信息查询 (老接口名: ITF_CRM_QueryProtectPass) yangsh6
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public IData queryProtectPass(IData data) throws Exception
    {
        ProtectPassInfoBean protectBean = BeanManager.createBean(ProtectPassInfoBean.class);
        return protectBean.queryProtectPass(data);
    }

    public IDataset queryUserOtherUserId(IData input) throws Exception
    {

        IDataset userOtherInfo = UserOtherInfoQry.getUserOtherInfoByAll(input.getString("USER_ID"), "SPWP");
        return userOtherInfo;
    }

    public IDataset queryUserSvcUserId(IData input) throws Exception
    {

        IDataset userSvcInfo = UserSvcInfoQry.qrySvcInfoByUserIdSvcId(input.getString("USER_ID"), "3312");
        return userSvcInfo;
    }

}
