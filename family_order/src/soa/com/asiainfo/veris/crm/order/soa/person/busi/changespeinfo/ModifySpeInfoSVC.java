
package com.asiainfo.veris.crm.order.soa.person.busi.changespeinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;

public class ModifySpeInfoSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 获取子业务信息
     * 
     * @param inData
     * @return
     * @throws Exception
     */
    public IData getCommInfo(IData inData) throws Exception
    {
        IData retData = new DataMap();
        /**
         * 获取用户品牌信息 首先从TF_F_USER_INFOCHANGE表中获取；如果没有记录则取 TF_F_USER 表中的品牌信息
         */
        IDataset brandDataset = UserInfoQry.getUserInfoChgByUserIdCurvalid(inData.getString("USER_ID"));
        if (IDataUtil.isNotEmpty(brandDataset))
        {
            retData.put("BRAND_CODE", brandDataset.getData(0).getString("BRAND_CODE", ""));
        }
        else
        {
            retData.put("BRAND_CODE", inData.getString("BRAND_CODE", ""));
        }

        /**
         * 获取用户服务状态信息 首先从TF_F_USER_SVCSTATE表中获取；如果没有记录则取 TF_F_USER 表中的服务状态信息
         */
        IDataset svcStateDset = UserSvcStateInfoQry.getUserMainState(inData.getString("USER_ID"));
        if (IDataUtil.isNotEmpty(svcStateDset))
        {
            retData.put("USER_STATE_CODESET", svcStateDset.getData(0).getString("STATE_CODE", ""));
        }
        else
        {
            retData.put("USER_STATE_CODESET", inData.getString("USER_STATE_CODESET", "").substring(0, 1));
        }

        // 获取用户资源信息
        IDataset resInfos = UserResInfoQry.queryUserResByUserIdResType(inData.getString("USER_ID"), "1");
        if (IDataUtil.isNotEmpty(resInfos))
        {
            IData resInfo = resInfos.getData(0);
            if (StringUtils.isBlank(resInfo.getString("RES_CODE")))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_890);
            }
            if (StringUtils.isBlank(resInfo.getString("IMSI")))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_891);
            }
            retData.put("SIM_CARD_NO", resInfo.get("RES_CODE"));
            retData.put("IMSI", resInfo.get("IMSI"));
        }
        else
        {
            CSAppException.apperr(CrmUserException.CRM_USER_892);
        }
        //获取品牌列表
        IDataset BrandList=UBrandInfoQry.getBrandList();
        retData.put("BrandList", BrandList);
        //获取用户状态列表
        IDataset StateList= UpcCall.qryOfferFuncStaByAnyOfferIdStatus("0",BofConst.ELEMENT_TYPE_CODE_SVC, null);
        retData.put("StateList", StateList);
        return retData;
    }

    /*
     * 获取simCard信息
     */
    public IDataset getSimCardInfo(IData input) throws Exception
    {
        if (StringUtils.isNotBlank(input.getString("IMSI")))
        {
            return ResCall.getSimCardInfo("1", "", input.getString("IMSI"), null);
        }
        else if (StringUtils.isNotBlank(input.getString("SIM_CARD_NO")))
        {
            return ResCall.getSimCardInfo("0", input.getString("SIM_CARD_NO"), "", null);
        }
        return new DatasetList();
    }

}
