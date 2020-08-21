
package com.asiainfo.veris.crm.order.web.group.param.pbxline;

import org.apache.log4j.Logger;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attriteminfo.AttrItemInfoIntfViewUtil;

public class UserParamInfo extends IProductParamDynamic
{

    private static transient Logger logger = Logger.getLogger(UserParamInfo.class);

    public IData initChgUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initChgUs(bp, data);
        IData parainfo = result.getData("PARAM_INFO");

        String productNo = parainfo.getString("PRODUCT_ID");
        
        // 调用后台服务查专线名称
        IDataset dataLineInfo = AttrItemInfoIntfViewUtil.qryAttrItemBByIdAndIdtypeAttrCode(bp, productNo, "P", "SP_LINE", "ZZZZ");
        
        parainfo.put("DATALINE_INFO", dataLineInfo);
        parainfo.put("NOTIN_METHOD_NAME", "ChgUs");

        // 查询管理员信息
        String userId = data.getString("USER_ID");
        IData userInParam = new DataMap();
        userInParam.put("USER_ID", userId);
        userInParam.put("REMOVE_TAG", "0");
        IDataset userInfo = CSViewCall.call(bp, "CS.UserInfoQrySVC.getTradeUserInfoByUserIdAndTag", userInParam);
        if (null != userInfo && userInfo.size() > 0)
        {
            IData userData = (IData) userInfo.get(0);
            parainfo.put("NOTIN_DETMANAGER_INFO", userData.get("RSRV_STR7"));
            parainfo.put("NOTIN_DETMANAGER_PHONE", userData.get("RSRV_STR8"));
            parainfo.put("NOTIN_DETADDRESS", userData.get("RSRV_STR9"));
        }

        // 查询优惠信息
        IData priceParam = new DataMap();
        priceParam.put("SUBSYS_CODE", "CSM");
        priceParam.put("PARAM_ATTR", "555");
        priceParam.put("PARAM_CODE", productNo);
        priceParam.put("EPARCHY_CODE", data.getString("USER_EPARCHY_CODE"));

        IDataset priceDataInfo = CSViewCall.call(bp, "SS.BookTradeSVC.getPriceDataByProductId", priceParam);
        parainfo.put("PRICE_DATA", priceDataInfo);

        // 从ESOP获取专线实例编号，暂时取死值
        IData inParam = new DataMap();
        IDataset seqDataSet = CSViewCall.call(bp, "CS.SeqMgrSVC.getMaxNumberLine", inParam);
        IData seqData = (IData) seqDataSet.get(0);
        String maxNumberLine = seqData.getString("seq_id");
        long maxIong = Long.parseLong(maxNumberLine) * 1000;
        parainfo.put("NOTIN_MAX_NUMBER_LINE", String.valueOf(maxIong));

        // 调用后台服务,查询OTHER表信息
        IData inparme = new DataMap();
        inparme.put("USER_ID", userId);
        inparme.put("RSRV_VALUE_CODE", "N001");
        IDataset userAttrInfo = CSViewCall.call(bp, "CS.TradeOtherInfoQrySVC.queryUserOtherInfoByUserId", inparme);

        // 判断OTHER表中有没有数据，没有从ESOP获取
        if (null != userAttrInfo && userAttrInfo.size() > 0)
        {
            IDataset dataset = new DatasetList();
            for (int i = 0; i < userAttrInfo.size(); i++)
            {
                IData userAttrData = (IData) userAttrInfo.get(i);
                IData userAttr = new DataMap();
                userAttr.put("pam_NOTIN_LINE_NUMBER_CODE", userAttrData.get("RSRV_VALUE"));
                userAttr.put("pam_NOTIN_LINE_NUMBER", userAttrData.get("RSRV_STR1"));
                userAttr.put("pam_NOTIN_LINE_BROADBAND", userAttrData.get("RSRV_STR2"));
                userAttr.put("pam_NOTIN_LINE_PRICE", userAttrData.get("RSRV_STR3"));
                userAttr.put("pam_NOTIN_LINE_INSTANCENUMBER", userAttrData.get("RSRV_STR9"));
                dataset.add(userAttr);
            }
            parainfo.put("VISP_INFO", dataset);

            // String eos = pd.getParameter("EOS");
            String esop = "";
            if (StringUtils.isNotBlank(esop))
            {
                // 从ESOP获取数据

            }
            else
            {
                parainfo.put("NOTIN_AttrInternet", dataset);
                parainfo.put("NOTIN_OLD_AttrInternet", dataset);
            }
        }
        else
        {

            // String eos = pd.getParameter("EOS");
            // IDataset resultDataset = new DatasetList();
            // IData idcpamData = new DataMap();
            // if(!"null".equals(eos) && !"".equals(eos)){
            // IDataset eosDataset = new DatasetList(eos);
            // if(null != eosDataset && eosDataset.size() > 0){
            // resultDataset = getEsopData(pd,eosDataset);
            // idcpamData.put("pam_idc", resultDataset);
            // setInfos(resultDataset);
            // }
            // }
            // String maxNumberLine = getMaxNumberLine(pd,resultDataset);
            // idcpamData.put("MAX_NUMBER_LINE", maxNumberLine.toString());
            // setIdcpam(idcpamData);

        }

        // 权限控制优惠价格未实现

        return result;
    }

    public IData initCrtUs(IBizCommon bp, IData data) throws Throwable
    {

        IData result = super.initCrtUs(bp, data);
        IData parainfo = null;

        if (IDataUtil.isNotEmpty(result) && IDataUtil.isNotEmpty(result.getData("PARAM_INFO")))
        {
            parainfo = result.getData("PARAM_INFO");
        }
        String productNo = parainfo.getString("PRODUCT_ID");
        
        // 调用后台服务查专线名称
        IDataset dataLineInfo = AttrItemInfoIntfViewUtil.qryAttrItemBByIdAndIdtypeAttrCode(bp, productNo, "P", "SP_LINE", "ZZZZ");

        // 查询优惠信息
        IData priceParam = new DataMap();
        priceParam.put("SUBSYS_CODE", "CSM");
        priceParam.put("PARAM_ATTR", "555");
        priceParam.put("PARAM_CODE", productNo);
        priceParam.put("EPARCHY_CODE", data.getString("USER_EPARCHY_CODE"));

        IDataset priceDataInfo = CSViewCall.call(bp, "SS.BookTradeSVC.getPriceDataByProductId", priceParam);
        parainfo.put("PRICE_DATA", priceDataInfo);

        // 从ESOP获取专线实例编号，暂时取死值
        IData inParam = new DataMap();
        IDataset seqDataSet = CSViewCall.call(bp, "CS.SeqMgrSVC.getMaxNumberLine", inParam);
        IData seqData = (IData) seqDataSet.get(0);
        String maxNumberLine = seqData.getString("seq_id");
        long maxIong = Long.parseLong(maxNumberLine) * 1000;
        parainfo.put("NOTIN_MAX_NUMBER_LINE", String.valueOf(maxIong));

        parainfo.put("DATALINE_INFO", dataLineInfo);
        return result;
    }

}
