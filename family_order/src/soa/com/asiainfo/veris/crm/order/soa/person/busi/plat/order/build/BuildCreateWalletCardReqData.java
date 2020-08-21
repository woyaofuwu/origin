
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.build;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.plat.order.requestdata.BaseCreateWalletCardReqData;

/**
 * @author zhuyu
 */
public class BuildCreateWalletCardReqData extends BaseBuilder implements IBuilder
{

    /*
     * (non-Javadoc)
     * @see com.ailk.bof.builder.impl.BaseBuilder#buildBusiRequestData(com.ailk.common.data.IData,
     * com.ailk.bof.data.requestdata.BaseReqData)
     */
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
        BaseCreateWalletCardReqData reqData = (BaseCreateWalletCardReqData) brd;
        reqData.setApplicationNum(param.getString("APPLICATION_NUM"));
        reqData.setAuthorizationType(param.getString("AUTHORIZATION_TYPE"));
        reqData.setBossId(param.getString("BOSS_ID"));
        reqData.setCheckTag(param.getString("CHACK_TAG"));
        // reqData.setCustName(param.getString("CUST_NAME"));
        reqData.setIdcardDepartment(param.getString("IDCARD_DEPARTMENT"));
        // reqData.setPsptId(param.getString("PSPT_ID"));
        reqData.setRspCode(param.getString("RSP_CODE"));
        reqData.setYearIncome(param.getString("YEAR_INCOME"));
    }

    /*
     * (non-Javadoc)
     * @see com.ailk.bof.builder.impl.BaseBuilder#getBlankRequestDataInstance()
     */
    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new BaseCreateWalletCardReqData();
    }

}
