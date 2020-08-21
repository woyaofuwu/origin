
package com.asiainfo.veris.crm.order.web.group.param.supertele;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;

/**
 * @description
 * @author yish
 */
public class UserParamInfo extends IProductParamDynamic
{
    /**
     * 移动总机: 验证总机号码有效性
     * 
     * @param bp
     * @param data
     * @return
     * @throws Exception
     */
    public IData queryExchangeUserInfo(IBizCommon bp, IData data) throws Exception
    {
        IData result = new DataMap();

        // 查询手机号码的三户信息+账期信息
        String serialNumber = data.getString("SERIAL_NUMBER");
        IData mebUCAAndAcctDayData = UCAInfoIntfViewUtil.qryMebUCAAndAcctDayInfoBySn(bp, serialNumber);

        IData mebUserInfoData = mebUCAAndAcctDayData.getData("MEB_USER_INFO");
        IData mebCustInfoData = mebUCAAndAcctDayData.getData("MEB_CUST_INFO");

        String user_id = mebUserInfoData.getString("USER_ID", "");
        String eparchy_code = mebUserInfoData.getString("EPARCHY_CODE");
        String open_mode = mebUserInfoData.getString("OPEN_MODE", "");
        String user_state_codeset = mebUserInfoData.getString("USER_STATE_CODESET", "");

        // 移动总机关系
        IData uuInfo_25 = RelationUUInfoIntfViewUtil.qryRelaUUInfoByUserIdBAndRelationTypeCode(bp, user_id, "25", eparchy_code, false);
        if (IDataUtil.isNotEmpty(uuInfo_25))
        {
            CSViewException.apperr(GrpException.CRM_GRP_278, serialNumber);
        }

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("TRADE_TYPE_CODE", "2924");
        param.put(Route.ROUTE_EPARCHY_CODE, eparchy_code);
        // 查未完工工单
        IData tradeInfo = CSViewCall.callone(bp, "CS.TradeInfoQrySVC.queryTradeBySnGrp", param);
        if (IDataUtil.isNotEmpty(tradeInfo))
        {
            CSViewException.apperr(GrpException.CRM_GRP_628, serialNumber);
        }

        if (!"0".equals(open_mode))
        {
            CSViewException.apperr(GrpException.CRM_GRP_629);
        }

        // 只有开通状态的号码可以办理此业务
        if (!"0".equals(user_state_codeset) && !"N".equals(user_state_codeset) && !"00".equals(user_state_codeset))
        {
            CSViewException.apperr(GrpException.CRM_GRP_630);
        }

        // VPMN关系
        IData uuInfo_20 = RelationUUInfoIntfViewUtil.qryRelaUUInfoByUserIdBAndRelationTypeCode(bp, user_id, "20", eparchy_code, false);

        // 海南判断总机号码是否属于VPMN集团: 如果是,取VPMN短号
        if (IDataUtil.isNotEmpty(uuInfo_20))
        {
            String strShortSN = uuInfo_20.getString("SHORT_CODE", "");
            mebUserInfoData.put("FLAG", "1");
            mebUserInfoData.put("EXCHANGE_SHORT_SN", strShortSN); // 短号码
        }
        else
        {
            mebUserInfoData.put("FLAG", "0");
        }

        mebUserInfoData.put("EXCHANGETELE_SN", serialNumber); // 总机号码
        mebUserInfoData.put("CUST_NAME", mebCustInfoData.getString("CUST_NAME"));
        mebUserInfoData.put("PSPT_TYPE_NAME", mebCustInfoData.getString("PSPT_TYPE_NAME"));
        mebUserInfoData.put("PSPT_ID", mebCustInfoData.getString("PSPT_ID"));

        result.put("AJAX_DATA", mebUserInfoData);

        return result;
    }

    /**
     * 移动总机: 验证总机号码短号的有效性
     * 
     * @param bp
     * @param data
     * @return
     * @throws Exception
     */
    public IData shortNumValidateSuperTeleAdmin(IBizCommon bp, IData data) throws Exception
    {
        IData param = new DataMap();
        IData returnData = new DataMap();
        param.put("SHORT_CODE", data.getString("SHORT_CODE"));
        // 验证短号
        IData validateData = CSViewCall.callone(bp, "SS.GroupValidateSVC.shortNumValidateSuperTeleAdmin", param);

        returnData.put("AJAX_DATA", validateData);
        return returnData;
    }

    /**
     * 验证海南管理员号码对应的VPMN号码是否存在
     * 
     * @param bp
     * @param data
     * @throws Exception
     */
    public IData vpmnNumValidateSuperTele(IBizCommon bp, IData data) throws Exception
    {

        IData returnData = new DataMap();
        IData idata = new DataMap();
        String serialNumber = data.getString("VPMN_SN");
        // 查用户信息 验证短号
        IData userinfo = UCAInfoIntfViewUtil.qryUserInfoBySn(bp, serialNumber, false);
        if (IDataUtil.isNotEmpty(userinfo))
        {
            String productId = userinfo.getString("PRODUCT_ID", "");
            if (!"8000".equals(productId)) // 本处写死了
            {
                idata.put("FLAG", "0");
                idata.put("ERROR_MESSAGE", "请输入对应的VPMN编码！");
            }
        }
        else
        {
            idata.put("FLAG", "1");
            idata.put("ERROR_MESSAGE", "填写输入参数VPMN号码失败！");
        }

        returnData.put("AJAX_DATA", idata);
        return returnData;
    }
}
