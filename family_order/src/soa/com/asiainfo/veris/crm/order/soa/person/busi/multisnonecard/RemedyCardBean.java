
package com.asiainfo.veris.crm.order.soa.person.busi.multisnonecard;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

public class RemedyCardBean extends CSBizBean
{

    /**
     * 校验
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public IDataset getCheck(IData params) throws Exception
    {
        String userId = params.getString("USER_ID");
        String rsrvValue = "SIMM";
        String rsrvStr4 = "01";
        IDataset otherInfos = UserOtherInfoQry.queryUserInfoByRsrvValue(userId, rsrvValue, rsrvStr4);
        if (IDataUtil.isEmpty(otherInfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户没有办理过国漫一卡多号业务，不能办理该业务！");
        }
        else
        {
            if ("".equals(otherInfos.getData(0).getString("RSRV_STR1", "")))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户办理的隐号业务，不能办理该业务！");
            }
        }
        return otherInfos;
    }

    /**
     * 获取ismi
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public IDataset getImsi(IData params) throws Exception
    {
        String userId = params.getString("USER_ID");
        return TradeInfoQry.getHisMainTradeLast(userId);
    }

    /**
     * 获取操作类型
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public IDataset getOperType(IData params) throws Exception
    {
        return CommparaInfoQry.getCommparaInfos("CSM", "952", "A3");

    }

    public IDataset getOtherSNInfo(String sn) throws Exception
    {

        IDataset userInfo = UserInfoQry.getUserinfo(sn);
        IData custInfo = UcaInfoQry.qryPerInfoByUserId(userInfo.getData(0).getString("USER_ID"));
        IDataset userResInfo = UserResInfoQry.qryUserResByIdDescEd(userInfo.getData(0).getString("USER_ID"));

        IDataset userPro = UserProductInfoQry.getProductInfo(userInfo.getData(0).getString("USER_ID"), "-1");
        String brand_code = userPro.getData(0).getString("BRAND_CODE");
        String product_id = userPro.getData(0).getString("PRODUCT_ID");
        // 是否是全球通用户
        if (!brand_code.equals("G001"))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户不是全球通用户,不能办理该业务");
        }

        userInfo.getData(0).put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(product_id));
        userInfo.getData(0).put("BRAND_NAME", UBrandInfoQry.getBrandNameByBrandCode(brand_code));
        userInfo.getData(0).put("PSPT_ADDR", custInfo.getString("PSPT_ADDR", ""));
        userInfo.getData(0).put("STATE_NAME", UpcCall.qryOfferFuncStaByAnyOfferIdStatus("0", "S", userInfo.getData(0).getString("USER_STATE_CODESET","")));

        IDataset dataset = new DatasetList();
        IData data = new DataMap();
        data.put("OUSERINFO", userInfo.getData(0));
        data.put("OCUSTINFO", custInfo);
        data.put("ORESINFO", userResInfo.getData(0));
        dataset.add(data);
        return dataset;
    }

    public IDataset loadInfos(IData params) throws Exception
    {
        String userId = params.getString("USER_ID");
        String tradeTypeCode = params.getString("TRADE_TYPE_CODE");
        String rsrvValue = "SIMM";
        String rsrvStr4 = "01";
        IDataset otherInfos = UserOtherInfoQry.queryUserInfoByRsrvValue(userId, rsrvValue, rsrvStr4);
        if (IDataUtil.isEmpty(otherInfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户没有办理过国漫一卡多号业务，不能办理该业务！");
        }
        else
        {
            if ("".equals(otherInfos.getData(0).getString("RSRV_STR1", "")))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户办理的隐号业务，不能办理该业务！");
            }
        }
        IData data = new DataMap();
        IDataset dataset = new DatasetList();
        data.put("DEPUTY_INFOS", otherInfos);

        IDataset opertypeInfos = CommparaInfoQry.getCommparaInfos("CSM", "952", "A3");
        data.put("OPERTYPE_INFOS", opertypeInfos);

        //IDataset imsiInfos = UserOtherInfoQry.getUserOther(userId, "SIMM");
        IDataset imsiInfos = TradeInfoQry.getHisMainTradeLast(userId);
        String imsiCode = "";
        String imsiCodeOld = "";
        if (!IDataUtil.isEmpty(imsiInfos))
        {
           /* for (int i = 0; i < imsiInfos.size(); i++)
            {
                if ("1".equals(imsiInfos.getData(i).getString("RES_TYPE_CODE")))
                {
                    imsiCode = imsiInfos.getData(i).getString("RSRV_VALUE");
                    break;
                }
            }
            */
        	imsiCodeOld = imsiInfos.getData(0).getString("RSRV_STR6");
        	imsiCode = imsiInfos.getData(0).getString("RSRV_STR7");
        }
	
        data.put("IMSI_CODE_OLD", imsiCodeOld);
        data.put("IMSI_CODE", imsiCode);
        dataset.add(data);
        return dataset;
    }

}
