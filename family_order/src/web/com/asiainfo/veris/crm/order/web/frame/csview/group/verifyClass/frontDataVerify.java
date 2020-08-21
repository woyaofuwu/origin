
package com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.Msisdn.MsisdnInfoViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userattrinfo.UserAttrInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;

public class frontDataVerify extends CSBizHttpHandler
{

    /*
     * @description 根据选择的省份获取对应省份下的城市
     * @author xunyl
     * @date 2013-06-17
     */
    public void chooseCitys() throws Exception
    {
        // 1- 获取省属性编号、省编码、市属性编号

        String provinceAttrCode = getData().getString("PROVINCE_ATTR_CODE");
        String privinceAttrValue = getData().getString("PROVINCE_ATTR_VALUE");
        String cityAttrCode = getData().getString("CITY_ATTR_CODE");

        // 2-根据上面的参数从TD_S_BOUND_DATA表获取对应省得所有城市

        IData inparam = new DataMap();
        inparam.put("PROVINCE_ATTR_CODE", provinceAttrCode);
        inparam.put("PROVINCE_ATTR_VALUE", privinceAttrValue);
        inparam.put("CITY_ATTR_CODE", cityAttrCode);
        IDataset citys = CSViewCall.call(this, "CS.BoundDataQrySVC.qryBoundDataByProValueCityCode", inparam);

        // 3- 返回结果
        IData idata = new DataMap();
        idata.put("result", citys);
        setAjax(idata);
    }
    
    public void verifyIsMobileNumber() throws Exception
    {
    	 String serialNumber = getData().getString("MOBILENUMBER");
    	 IData inparams = new DataMap();
    	 boolean isMobileNumber =false;
    	 inparams.clear();
    	 IDataset MsisdnInfo = MsisdnInfoViewUtil.qryUserMsisdnInfo(this, serialNumber);
    	 if(IDataUtil.isNotEmpty(MsisdnInfo))
    	 {
    		 isMobileNumber = true;
    		 
    	 }
    	 inparams.put("ISMOBILENUMBER", isMobileNumber);
         setAjax(inparams);
    }

    /*
     * @description 9+主办省代码+14位流水，14位流水右对齐左补0，其中总部订购使用省代码001，主办省保证流水唯一，并且保证与本省的一点出卡物联网专网专号业务流水不重复
     * @author chenyi
     * @date 2014-4-22
     */
    public void geneSubsId() throws Exception
    {
        IData param = new DataMap();
        IDataset configs = CSViewCall.call(this, "CS.SeqMgrSVC.getPbssBizSubsId", param);

        if (IDataUtil.isNotEmpty(configs))
        {
            String subs_id = configs.getData(0).getString("seq_id");
            // 3- 返回结果
            IData idata = new DataMap();
            idata.put("result", subs_id);
            setAjax(idata);
        }

    }

    /*
     * @description 9+主办省代码+14位流水，14位流水右对齐左补0，其中总部订购使用省代码001，主办省保证流水唯一，并且保证与本省的一点出卡物联网专网专号业务流水不重复
     * @author chenyi
     * @date 2014-4-22
     */
    public void getPbssBizProdInstId() throws Exception
    {
        IData param = new DataMap();
        IDataset configs = CSViewCall.call(this, "CS.SeqMgrSVC.getPbssBizProdInstId", param);

        if (IDataUtil.isNotEmpty(configs))
        {
            String subs_id = configs.getData(0).getString("seq_id");
            // 3- 返回结果
            IData idata = new DataMap();
            idata.put("result", subs_id);
            setAjax(idata);
        }

    }

    /*
     * @description 属性的省内排重
     * @author xunyl
     * @date 2013-06-09
     */
    public void isDuplicate() throws Exception
    {
        // 1- 获取属性编号和属性值

        String attrCode = getData().getString("ATTR_CODE");
        String attrValue = getData().getString("ATTR_VALUE");
        IData inparam = new DataMap();
        inparam.put("ATTR_CODE", attrCode);
        inparam.put("ATTR_VALUE", attrValue);

        // 2- 根据属性编号和属性值查询对应的记录数，大于等于1的情况说明数据库中已经存在有记录，该值重复

        IData idata = new DataMap();
        idata.put("result", "true");// true表示默认没有重复

        IDataset qryResult = CSViewCall.call(this, "CS.UserAttrInfoQrySVC.queryUserAttrByAttrValue", inparam);

        if (qryResult != null && qryResult.size() >= 1)
        {
            // 表明已经存在
            idata.put("result", "false");

        }
        else
        {

            IDataset qryAttrResult = CSViewCall.call(this, "CS.TradeAttrInfoQrySVC.qryTradeAttrByAttrCodeAttrValue", inparam);

            if (qryAttrResult != null && qryAttrResult.size() >= 1)
            {
                // 表明已存在
                idata.put("result", "false");
            }
        }

        // 3-返回结果
        setAjax(idata);
    }

    /*
     * @description 查询400保底资费
     * @author zhangcheng6
     * @date 2013-06-09
     */
    public void qry400BaseDiscntConfig() throws Exception
    {
        IData param = new DataMap();
        param.put("ID", "0");
        param.put("ID_TYPE", "D");
        param.put("ATTR_OBJ", "400");
        IDataset configs = AttrBizInfoIntfViewUtil.qryAttrBizInfosByIdAndIdTypeAttrObj(this, "0", "D", "400");
        if (IDataUtil.isNotEmpty(configs))
        {
            DataHelper.sort(configs, "ATTR_CODE", 1);
        }
        // 3- 返回结果
        IData idata = new DataMap();
        idata.put("result", configs);
        setAjax(idata);

    }

    /*
     * @description 验证订购的是否为流量叠加包
     * @author chenyi
     * @date 2014-7-19
     */
    public void qryFluxElementInfo() throws Exception
    {
        // 1 获取本地元素编码
        String element_id = getData().getString("ELEMENT_ID");

        // 2- 根据本地元素编码判断是否为流量叠加包
        String attrvalue = StaticUtil.getStaticValue(getVisit(), "TD_B_ATTR_BIZ", new String[]
        { "ID_TYPE", "ATTR_VALUE", "ATTR_OBJ" }, "ATTR_CODE", new String[]
        { "D", element_id, "FluxPay" });

        // 3 如果有值则为流量叠加包操作，反之不是
        IData idata = new DataMap();
        if (StringUtils.isNotBlank(attrvalue))
        {
            idata.put("result", true);
        }
        else
        {
            idata.put("result", false);
        }

        setAjax(idata);
    }

    /**
     * @Title:qryHistoryTradeInfo
     * @Description: 查询历史表信息，若有信息则返回false
     * @param:
     * @throws Exception
     * @return:void
     * @author chenkh
     */
    public void qryHistoryTradeInfo() throws Exception
    {
        // 取得台帐id
        String tradeId = getData().getString("TRADE_ID");

        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        boolean flag = false;
        // 查历史表
        IDataset historyInfo = CSViewCall.call(this, "CS.TradeBhQrySVC.qryTradeHistoryInfo", param);
        // 若结果为空则判断标记为true
        if (IDataUtil.isEmpty(historyInfo))
        {
            flag = true;
        }
        IData idata = new DataMap();
        idata.put("result", flag);

        setAjax(idata);
    }

    /*
     * @description 查询用户旧属性
     * @author zhangcheng6
     * @date 2013-06-09
     */
    public void qryOldValue() throws Exception
    {
        IData param = new DataMap();
        param.put("ATTR_CODE", getData().getString("ATTR_CODE"));
        param.put("USER_ID", getData().getString("USER_ID"));
        param.put("RSRV_STR4", "");
        IDataset configs = CSViewCall.call(this, "CS.UserAttrInfoQrySVC.qryBbossUserAttrForGroupNew", param);

        // 3- 返回结果
        IData idata = new DataMap();
        idata.put("result", configs);
        setAjax(idata);

    }

    /**
     * @Title:qryPackageInfo
     * @Description: 取得包信息
     * @param:@throws Exception
     * @return:void
     * @author chenkh
     */
    public void qryPackageInfo() throws Exception
    {
        // 取得产品id
        String productId = getData().getString("PRODUCT_ID");
        IData param = new DataMap();

        param.put("PRODUCT_ID", productId);
        param.put("EPARCHY_CODE", Route.CONN_CRM_CEN);
        IDataset packageInfo = CSViewCall.call(this, "CS.PackageSVC.getPackagesByProduct", param);
        if (IDataUtil.isEmpty(packageInfo))
            packageInfo = new DatasetList();
        IData idata = new DataMap();
        idata.put("result", packageInfo);

        setAjax(idata);
    }

    /*
     * @description 查询属性对应的集团资费
     * @author zhangcheng6
     * @date 2013-06-09
     */
    public void qryProductToMerch() throws Exception
    {
        IDataset configs = AttrBizInfoIntfViewUtil.qryAttrBizInfosByIdAndIdTypeAttrObjAttrCode(this, "1", "B", "DIS", getData().getString("ELEMENT_ID"));

        // 3- 返回结果
        IData idata = new DataMap();
        idata.put("result", "false");// false表示默认为本地资费
        if (IDataUtil.isNotEmpty(configs))
        {// 表明已经存在
            idata.put("result", "true"); // true 表示未集团资费
        }
        setAjax(idata);
    }

    /*
     * @description 属性变更时获取已选区属性
     * @author zhangcheng6
     * @date 2013-06-09
     */
    public void qrySelectParms() throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", getData().getString("USER_ID"));
        param.put("PRODUCT_ID", getData().getString("PRODUCT_ID"));
        param.put(Route.USER_EPARCHY_CODE, getData().getString("GRP_USER_EPARCHYCODE", ""));
        IDataset configs = CSViewCall.call(this, "CS.SelectedElementSVC.getGrpUserChgElements", param);

        // 3- 返回结果
        IData idata = new DataMap();
        idata.put("result", configs);
        setAjax(idata);

    }

    /*
     * @description 本地产品编码转化为集团产品编码
     * @author chenyi
     * @date 2014-7-19
     */
    public void qrySpecNumber() throws Exception
    {
        // 1 获取本地产品编码
        String productId = getData().getString("PRODUCT_ID");

        // 2- 根据省内产品编号获取全网产品编号
        String proSpecNumber = AttrBizInfoIntfViewUtil.qryAttrValueStrByIdAndIdTypeAttrObjAttrCode(this, "1", "B", "PRO", productId);

        // 3- 返回集团产品编号
        IData idata = new DataMap();
        idata.put("result", proSpecNumber);
        setAjax(idata);
    }

    /**
     * @description 特殊参数验证
     * @throws Exception
     * @author chenkh
     * @date 2014年8月28日
     */
    public void qryTradeSpecAttr() throws Exception
    {
        // 取得user_id
        String userId = getData().getString("USER_ID");
        // 取得ATTR_CODE
        String attrCode = getData().getString("ATTR_CODE");
        // 取得ATTR_VALUE
        String attrValue = getData().getString("ATTR_VALUE");
        // 是否需要回收
        String recover = "";
        // 判断标记
        boolean flag = false;
        IData idata = new DataMap();
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("ATTR_CODE", attrCode);
        param.put("MODIFY_TAG", "0");
        // 查用户资料表
        IDataset userParamInfos = UserAttrInfoIntfViewUtil.qryBBossUserAttrInfo(this, param.getString("USER_ID"), param.getString("ATTR_CODE"));
        if (IDataUtil.isEmpty(userParamInfos))
            flag = true;
        else
        {
            idata.put("ATTR_VALUE", userParamInfos.getData(0).getString("ATTR_VALUE", ""));
            if (userParamInfos.getData(0).getString("ATTR_VALUE", "").equals(attrValue))
                recover = "M";
        }
        idata.put("recover", recover);
        idata.put("result", flag);

        setAjax(idata);
    }

    /**
     * 查询本地编码
     * 
     * @throws Exception
     * @author chenkh 2014年10月8日
     */
    public void qryLocalNumber() throws Exception
    {
        // 1、取得全网编码
        String specNumber = getData().getString("GRP_PRODUCT_ID");
        // 2、查询本地编码
        String localNumber = StaticUtil.getStaticValue(getVisit(), "TD_B_ATTR_BIZ", new String[]
        { "ID_TYPE", "ATTR_OBJ", "ATTR_VALUE" }, "ATTR_CODE", new String[]
        { "B", "PRO", specNumber });
        // 3、将本地编码返回
        IData retData = new DataMap();
        retData.put("PRODUCT_ID", localNumber);

        setAjax(retData);
    }
    
    public void isExistDiscnt() throws Exception
    {
        String productId = getData().getString("PRODUCT_ID");
        String eparchyCode = "";
        boolean flag = false;
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        param.put("EPARCHY_CODE", eparchyCode);

        IDataset packageInfo = CSViewCall.call(this, "CS.ProductPkgInfoQrySVC.getPackageByProductId", param);
        
        
        if (IDataUtil.isNotEmpty(packageInfo))
        {
            flag=qryPackageElementByPackageId(packageInfo);
        }
        IData idata = new DataMap();
        idata.put("ret", flag);

        setAjax(idata);
    }
    
    public boolean qryPackageElementByPackageId(IDataset packageInfo) throws Exception
    {
        for (int i = 0, sizeI = packageInfo.size(); i < sizeI; i++)
        {
            String packageId = packageInfo.getData(i).getString("PACKAGE_ID");
            IData param = new DataMap();
            param.put("PACKAGE_ID", packageId);
            IDataset elementInfo = CSViewCall.call(this, "CS.PkgElemInfoQrySVC.queryElementByPkgId", param);
            for (int j = 0, sizeJ = elementInfo.size(); j < sizeJ; j++)
            {
                IData data = elementInfo.getData(j);
                String elementTypeCode = data.getString("ELEMENT_TYPE_CODE");
                String elementId = data.getString("ELEMENT_ID");
                if (!elementTypeCode.equals("D"))
                {
                    continue;
                }
                String discntExist = AttrBizInfoIntfViewUtil.qryAttrValueStrByIdAndIdTypeAttrObjAttrCode(this, "1", "B", "DIS", elementId);
                if (StringUtils.isNotEmpty(discntExist))
                {
                    return true;
                }
            }
        }
        return false;
    }
    
    /*
     * @description 查询集团级别
     * @author songxw
     * @date 2019-10-21
     */
    public void qryGrpClassId() throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", getData().getString("CUST_ID"));
        IDataset custGroup = CSViewCall.call(this, "CS.GrpInfoQrySVC.queryCustGroupInfoByCustId", param);

        // 3- 返回结果
        IData idata = new DataMap();
        idata.put("result", custGroup);
        setAjax(idata);

    }

    /*
     * @description 查询集团级别
     * @author huangfm
     * @date 2020-04-20
     */
    public void qryGrpByGroupId() throws Exception
    {
        IData param = new DataMap();
        param.put("GROUP_ID", getData().getString("GROUP_ID"));
        IDataset custGroup = CSViewCall.call(this, "CS.GrpInfoQrySVC.queryCustGroupInfoByGroupId", param);

        // 3- 返回结果
        IData idata = new DataMap();
        idata.put("result", custGroup);
        setAjax(idata);

    }
}
