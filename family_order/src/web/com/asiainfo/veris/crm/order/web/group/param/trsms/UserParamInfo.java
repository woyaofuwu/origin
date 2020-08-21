
package com.asiainfo.veris.crm.order.web.group.param.trsms;

import org.apache.log4j.Logger;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

/**
 * @description 旅信通参数页面处理类
 * @author yish
 */
public class UserParamInfo extends IProductParamDynamic
{
    private static transient Logger logger = Logger.getLogger(UserParamInfo.class);

    public IData checkValidServCode(IBizCommon bp, IData data) throws Exception
    {
        IData result = new DataMap();

        String servCode = data.getString("SERV_CODE");

        IData param = new DataMap();
        param.put("SERV_CODE", servCode);
        param.put("BIZ_STATE_CODE", "");

        // 调用后台服务，判断tf_f_user_grp_platsvc和台账记录中 是否已经存在(使用)该serCode;
        IDataset userSerCode = CSViewCall.call(bp, "CS.UserGrpPlatSvcInfoQrySVC.getuserPlatsvcbyservcode", param);
        IDataset tradeSerCode = CSViewCall.call(bp, "CS.TradeInfoQrySVC.queryTradeByRsrvstr1", param);

        if (IDataUtil.isNotEmpty(userSerCode) || IDataUtil.isNotEmpty(tradeSerCode))
        {
            param.put("ERROR_MESSAGE", "该服务代码已经使用!请重新输入!");
            param.put("RESULT", "false");
        }

        result.put("AJAX_DATA", param);
        setAttrItem(new DataMap());

        return result;
    }

    public IData getBizCodeTail(IBizCommon bp, IData data) throws Exception
    {
        IData result = new DataMap();

        String codeC = "00001";

        IData sidata = CSViewCall.callone(bp, "CS.SeqMgrSVC.getGrpMolist", result);
        codeC = sidata.getString("SEQ_GRP_MOLIST");

        String sclen = "5";
        int cLen = 0;
        try
        {
            cLen = Integer.parseInt(sclen);
        }
        catch (Exception e)
        {
            cLen = 0;
        }

        codeC = codeC.substring((codeC.length() - cLen), codeC.length());

        IData retData = new DataMap();
        retData.put("strSvcCodeTail", codeC);

        result.put("AJAX_DATA", retData);
        return result;
    }

    public IData initChgUs(IBizCommon bp, IData data) throws Throwable
    {
        IData userattr = new DataMap();

        IData result = super.initChgUs(bp, data);
        IData parainfo = new DataMap();
        if (IDataUtil.isNotEmpty(result) && IDataUtil.isNotEmpty(result.getData("PARAM_INFO")))
            parainfo = result.getData("PARAM_INFO");

        String userId = data.getString("USER_ID", "");

        IData datatemp = new DataMap();
        datatemp.put("USER_ID", userId);
        // 调用后台服务，获取用户平台服务信息
        IDataset dataset = CSViewCall.call(bp, "CS.UserGrpPlatSvcInfoQrySVC.getLxtGrpPlatSvcByUserId", datatemp);
        if (IDataUtil.isNotEmpty(dataset))
        {
            userattr = dataset.getData(0);
        }

        IData userInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(bp, userId);

        IData uinfos = new DataMap();
        uinfos.put("RSRV_STR6", userInfo.getString("RSRV_STR6"));
        uinfos.put("RSRV_STR7", userInfo.getString("RSRV_STR7"));
        uinfos.put("RSRV_STR8", userInfo.getString("RSRV_STR8"));

        userattr.putAll(uinfos);

        IData userattritem = IDataUtil.iDataA2iDataB(userattr, "ATTR_VALUE");
        
        // 方便前台取下拉框选项值
        transComboBoxValue(userattritem, getAttrItem());

        result.put("ATTRITEM", userattritem); // 页面上属性值，取是的userattritem里面的，而不是父类attrtiem中的
        result.put("ATTRITEMSET", IDataUtil.iData2iDataset(userattr, "ATTR_CODE", "ATTR_VALUE"));

        // 设置变更操作
        parainfo.put("METHOD_NAME", "ChgUs");
        result.put("PARAM_INFO", parainfo);

        return result;
    }

    /**
     * @description 初始化 集团产品受理 参数页面
     * @author yish
     */
    public IData initCrtUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initCrtUs(bp, data);
        IData parainfo = new DataMap();
        if (IDataUtil.isNotEmpty(result) && IDataUtil.isNotEmpty(result.getData("PARAM_INFO")))
        {
            parainfo = result.getData("PARAM_INFO");
        }

        String biz_code = getAttrItemValue("BIZ_CODE", "ATTR_VALUE");

        /*IData param = new DataMap();
        param.put("BIZ_CODE", biz_code);
        // 调用后台服务,查服务信息
        IData sidata = CSViewCall.callone(bp, "CS.UserSvcInfoQrySVC.getSerByBS", param);

        parainfo.put("COPINFOS", sidata);*/ //没找到调用的地方 

        // 设置为受理操作
        parainfo.put("METHOD_NAME", "CrtUs");
        result.put("PARAM_INFO", parainfo);

        return result;
    }
}
