package com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter.order.requestdata.BenefitBindRelReqData;

/**
 * @author 梁端刚
 * @version V1.0
 * @date 2019/12/11 10:23
 */
public class BuildBenefitBindRel extends BaseBuilder implements IBuilder {
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception{
        BenefitBindRelReqData request=(BenefitBindRelReqData)brd;
        request.setRelId(param.getString("REL_ID"));
        request.setDiscntCode(param.getString("DISCNT_CODE"));
        request.setRightId(param.getString("RIGHT_ID"));
        request.setModifyTag(param.getString("MODIFY_TAG"));
        request.setNewRelId(param.getString("NEW_REL_ID"));
        //根据DISCNT_CODE构建生失效时间,
        IDataset enableModes = UpcCall.queryOfferEnableModeByCond("D", param.getString("DISCNT_CODE"));
        if(IDataUtil.isNotEmpty(enableModes)){
            IData enableMode = enableModes.first();
            String enableMode1 = enableMode.getString("ENABLE_MODE");
            String absoluteEnableDate = enableMode.getString("ABSOLUTE_ENABLE_DATE");
            String enableOffset = enableMode.getString("ENABLE_OFFSET");
            String enableUnit = enableMode.getString("ENABLE_UNIT");
            String disableMode = enableMode.getString("DISABLE_MODE");
            String absoluteDisableDate = enableMode.getString("ABSOLUTE_DISABLE_DATE");
            String disableOffset = enableMode.getString("DISABLE_OFFSET");
            String disableUnit = enableMode.getString("DISABLE_UNIT");
            request.setStartDate(SysDateMgr.startDate(enableMode1,absoluteEnableDate,enableOffset,enableUnit));
            request.setEndDate(SysDateMgr.endDate(request.getStartDate(),disableMode,absoluteDisableDate,disableOffset,disableUnit));

        }else{
            //未配置优惠省生失效时间,按自然年年底为结束时间
            request.setStartDate(SysDateMgr.getSysTime());
            request.setEndDate(SysDateMgr.getLastDayOfThisYear());
        }

    }

    @Override
    public BaseReqData getBlankRequestDataInstance(){
        return new BenefitBindRelReqData();
    }

}
