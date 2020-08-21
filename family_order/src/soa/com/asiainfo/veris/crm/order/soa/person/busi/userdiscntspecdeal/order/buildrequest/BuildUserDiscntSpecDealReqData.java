
package com.asiainfo.veris.crm.order.soa.person.busi.userdiscntspecdeal.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.userdiscntspecdeal.order.requestdata.UserDiscntSpecDealReqData;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: BuildUserDiscntSpecDealReqData.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: maoke
 * @date: May 27, 2014 8:00:21 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* May 27, 2014 maoke v1.0.0 修改原因
 */
public class BuildUserDiscntSpecDealReqData extends BaseBuilder implements IBuilder
{
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        UserDiscntSpecDealReqData request = (UserDiscntSpecDealReqData) brd;

        if (IDataUtil.isNotEmpty(param))
        {
            String discntCode = param.getString("DISCNT_CODE");
            String startDate = param.getString("START_DATE");
            String oldStartDate = param.getString("OLD_START_DATE");
            String endDate = param.getString("END_DATE");
            String oldEndDate = param.getString("OLD_END_DATE");
            String instId = param.getString("INST_ID");
            String discntRemark = param.getString("DISCNT_REMARK");
            String tag = param.getString("tag");

            if (BofConst.MODIFY_TAG_UPD.equals(tag))
            {
                request.setStartDate(startDate);
                request.setOldStartDate(oldStartDate);
                request.setEndDate(endDate);
                request.setOldEndDate(oldEndDate);
                request.setInstId(instId);// "|"+DiscntInfoQry.getDiscntNameByDiscntCode(discntCode)
                request.setDiscntRemark(discntRemark);
                request.setRemark("优惠编码【" + discntCode + "】原开始结束时间[" + oldStartDate + "][" + oldEndDate + "]");
            }
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_363);
        }
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new UserDiscntSpecDealReqData();
    }
    
    /**
     * 重写，不做业务校验
     */
    protected void checkBefore(IData input, BaseReqData reqData) throws Exception
    {
    	
    }
}
