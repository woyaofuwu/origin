
package com.asiainfo.veris.crm.order.web.frame.csview.group.creategroupuser;

import java.util.Iterator;


import org.apache.tapestry.IRequestCycle;

import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.base.MessageBox;
import com.asiainfo.veris.crm.order.web.frame.csview.common.base.MessageBox.Button;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationbbinfo.RelationBBInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userattrinfo.UserAttrInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.bbossattrinfo.BBossAttrInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.bounddatainfo.BoundDataInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productcompinfo.ProductCompInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.staff.staffbbossinfo.StaffBBossInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class EntireCustomerUserParamInfo extends GroupBasePage
{
    // 定义BBOSS商产品信息

    private IData productGoodInfo = new DataMap();

    private String groupId;

    /**
     * AJAX附件校验
     *
     * @param cycle
     * @throws Throwable
     */
    public void attNameChk(IRequestCycle cycle) throws Throwable
    {
        IData paramresult = new DataMap();
        setAjax(paramresult);
    }

    /**
     * 受理成功或失败时，提示窗口返回按键
     */
    public Button getBackBtn() throws Exception
    {
        // 确认返回按钮
        MessageBox.Button btn3 = new MessageBox.Button();
        btn3.setButtonName("返回");
        btn3.setFunction("parent.parent.redirectToNav('component.flow.pageflow.FlowFrame', 'init', '&FLOW_FILE=group/BbossCreate.xml', 'contentframe');");
        return btn3;
    }

    public abstract IData getCondition();

    /*
     * @description 获取集团产品注销中的特殊参数
     * @author xunyl
     * @date 2014-04-22
     */
    protected IDataset getDstParamInfoList(IRequestCycle cycle) throws Exception
    {
        // 1- 获取商品编号
        String productId = getData().getString("PRODUCT_ID", "");
        if (StringUtils.isEmpty(productId))
        {
            productId = getData().getString("GRP_PRODUCT_ID", "");
        }

        // 2- 根据商品编号获取商品对应的关系类型
        String relationTypeCodeString = ProductCompInfoIntfViewUtil.qryRelationTypeCodeStrByProductId(this, productId);
        // 3- 获取集团用户编号
        String userId = getData().getString("GRP_USER_ID", "");

        // 4- 根据集团用户编号及对应的关系类型查找商品下已订购的所有产品
        IDataset relaUUInfoList = RelationBBInfoIntfViewUtil.qryRelaBBInfosByUserIdAAndRelationTypeCodeAllCrm(this, userId, relationTypeCodeString);
        if (IDataUtil.isEmpty(relaUUInfoList))
        {
            return new DatasetList();
        }

        // 5- 获取已定购的多有产品用户信息
        IDataset productInfoList = new DatasetList();
        for (int i = 0; i < relaUUInfoList.size(); i++)
        {
            IData productUserInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, relaUUInfoList.getData(i).getString("USER_ID_B"), false);
            if (IDataUtil.isNotEmpty(productUserInfo))
            {
                productUserInfo.put("PRODUCT_OPER_CODE", GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CANCLE.getValue());
                productInfoList.add(productUserInfo);
            }
        }

        productGoodInfo.put("PRODUCT_INFO_LIST", productInfoList);

        // 6- 返回集团产品注销中的特殊参数
        return queryParams(productInfoList);
    }

    /*
     * @description 特殊参数处理，一般用于产品注销、暂停和恢复
     * @author xunyl
     * @date 2014-04-22
     */
    protected void iniparam(IRequestCycle cycle, IData conditionData) throws Exception
    {
        // 1- 获取商品操作类型
        String merchOperty = getData().getString("operType");
        if (StringUtils.isEmpty(merchOperty))
        {
            merchOperty = conditionData.getString("operType", "");
        }

        // 2- 分情况获取特殊属性，一种情况为集团产品变更(商品注销、产品暂停与恢复、产品退订)，一种为集团产品注销
        IDataset productInfoList = productGoodInfo.getDataset("PRODUCT_INFO_LIST");
        // 2-1 集团产品注销
        if (GroupBaseConst.MERCH_STATUS.MERCH_CANCLE.getValue().equals(merchOperty))
        {
            conditionData.put("merchpParams", getDstParamInfoList(cycle));
            return;
        }
        // 2-2 集团产品暂停、恢复、取消、修改商产品资费
        if (GroupBaseConst.MERCH_STATUS.MERCH_PASTE.getValue().equals(merchOperty) || GroupBaseConst.MERCH_STATUS.MERCH_CONTINUE.getValue().equals(merchOperty) || GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_DISCNT.getValue().equals(merchOperty))
        {
            conditionData.put("merchpParams", queryParams(productInfoList));
            return;
        }
        // 2-3 变更商品组成关系时，只有在产品删除时才会有特殊参数
        if (GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_GROUP.getValue().equals(merchOperty))
        {
            IDataset delProductInfoList = new DatasetList();
            for (int i = 0; i < productInfoList.size(); i++)
            {
                IData productInfo = productInfoList.getData(i);
                String productOperCode = productInfo.getString("PRODUCT_OPER_CODE");
                if (productOperCode.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CANCLE.getValue()))
                {
                    delProductInfoList.add(productInfo);
                }
            }
            conditionData.put("merchpParams", queryParams(delProductInfoList));
        }
    }

    /**
     * 初始化公用方法
     *
     * @param pd
     * @param productId
     * @throws Exception
     */
    private void init(String productId) throws Exception
    {
        // 查询产品是否为纳入一级ESOP页面集成的产品
        String ifIesop = AttrBizInfoIntfViewUtil.qryAttrValueStrByIdAndIdTypeAttrObjAttrCode(this, productId, "P", "0", "IFIESOP");
        if (StringUtils.isBlank(ifIesop))
            ifIesop = "";
        IData condition = new DataMap();
        condition.put("IFIESOP", ifIesop);
        setCondition(condition);
    }

    public void initBbossManageCrtUs(IRequestCycle cycle) throws Exception
    {
        // 1- 获取产品编号
        String productId = getData().getString("PRODUCT_ID");
        if (StringUtils.isEmpty(productId))
        {
            productId = getData().getString("GRP_PRODUCT_ID", "");
        }
        init(productId);

        // 2- 设置页面参数
        IData conditionData = new DataMap();
        conditionData.put("PRODUCT_ID", productId);
        conditionData.put("METHOD", "BbossManageCrtUs");

        //3- 业务特殊化处理(移动云业务资源产品优惠类型为免费测试时，要求合同非必填)
        String merchId= getMerchId(cycle);
        conditionData.put("MANDATORY", true);
        if(StringUtils.equals("1010402", merchId)){
            //自定义MANDATORY值，用于控制合同附件是否必填
            String disType =getDisType(cycle);
            if(StringUtils.equals("1", disType)){//1-免费测试   2-折扣优惠  3-标准资费
                //免费资费的情况下，合同附件不必填
                conditionData.put("MANDATORY", false);
            }
        }

        setCondition(conditionData);
    }

    public void initChgUs(IRequestCycle cycle) throws Exception
    {

        initSimple(cycle);

        //1- 缓存中获取商产品信息(商产品信息过大，只能放在MC中传递)
        String key = CacheKey.getBossProductInfoKey(getVisit().getStaffId(), groupId);
        productGoodInfo = new DataMap(SharedCache.get(key).toString());

        //2- 获取并设置商产品信息
        setProductGoodInfos(productGoodInfo);

        //3- 设置条件值
        IData conditionData = new DataMap();
        conditionData.put("METHOD", "ChgUs");
        //业务特殊化处理(移动云业务资源产品优惠类型为免费测试时，要求合同非必填)
        String merchId= getMerchId(cycle);
        conditionData.put("MANDATORY", false);
        if(StringUtils.equals("1010402", merchId)){
            //自定义MANDATORY值，用于控制合同附件是否必填
            String disType =getDisType(cycle);
            if(StringUtils.equals("2", disType) || StringUtils.equals("3", disType)){//1-免费测试   2-折扣优惠  3-标准资费
                //折扣优惠 和标准资费的情况下，合同附件必填
                conditionData.put("MANDATORY", true);
            }
        }

        iniparam(cycle, conditionData);

        setCondition(conditionData);
    }

    public void initCrtUs(IRequestCycle cycle) throws Exception
    {
        // 集团受理页面时调用
        if (!"1".equals(getData().getString("isBbossManageCreate")))
        {

            initSimple(cycle);

            // - 缓存中获取商产品信息(商产品信息过大，只能放在MC中传递)
            String key = CacheKey.getBossProductInfoKey(getVisit().getStaffId(), groupId);
            productGoodInfo = new DataMap(SharedCache.get(key).toString());
            // - 获取并设置商产品信息
            setProductGoodInfos(productGoodInfo);

            IData good_info = productGoodInfo.getData("GOOD_INFO");

            IDataset product_info_list = productGoodInfo.getDataset("PRODUCT_INFO_LIST");

            // 1- 获取商品操作类型
            String merch_oper_code = good_info.getString("MERCH_OPER_CODE");

            // 2- 获取产品信息
            String product_oper_code = product_info_list.getData(0).getString("PRODUCT_OPER_CODE");

            IData conditionData = new DataMap();
            // 3- 设置页面信息
            if ("1".equals(merch_oper_code) && "10".equals(product_oper_code))
            {

                conditionData.put("ISPRE", true);
            }
            else
            {

                conditionData.put("ISPRE", false);
            }

            conditionData.put("METHOD", "CrtUs");

            setCondition(conditionData);

        }
        // 受理报文发送页面使用
        else
        {
            initBbossManageCrtUs(cycle);
        }
    }


    /**
     * @description 合同附件选填与必填的特殊处理(“资源产品优惠类型”为“折扣优惠”和“标准资费”时，“合同”字段为必填；“免费测试”时为非必填)
     * @auhtor xunyl
     * @date 2015-10-15
     */
    private String getDisType(IRequestCycle cycle)throws Exception{
        //1- 定义返回值
        String disType = "";

        //2- 获取移动云产品参数
        String key = CacheKey.getBossProductInfoKey(getVisit().getStaffId(), groupId);
        productGoodInfo = new DataMap(SharedCache.get(key).toString());
        IDataset productInfoList = productGoodInfo.getDataset("PRODUCT_INFO_LIST");
        if(IDataUtil.isEmpty(productInfoList)){
            return disType;
        }
        IData productInfo = productInfoList.getData(0);
        String productId = productInfo.getString("PRODUCT_ID");
        String productIndex = productInfo.getString("PRODUCT_INDEX");
        IData productParamInfo = productGoodInfo.getData("PRODUCT_PARAM");
        IData attrInfoMap= productParamInfo.getData(productId+ '_' +productIndex);
        if(IDataUtil.isEmpty(attrInfoMap)){
            return disType;
        }
        IDataset attrInfoList = merchPParamsToDataset(attrInfoMap);

        //3- 获取资源产品优惠类型参数值
        disType = getAttrValueByCode("1116013001",attrInfoList);

        //3- 返回取值结果
        return disType;
    }

    /**
     * @descripiton 获取集团公司商品编码
     * @author xunyl
     * @date 2015-10-15
     */
    private String getMerchId(IRequestCycle cycle)throws Exception{
        //1- 定义集团商品编码
        String merchId = "";

        //2- 获取本地商品编号
        String poProductId = getData().getString("PRODUCT_ID");
        if (StringUtils.isEmpty(poProductId))
        {
            poProductId = getData().getString("GRP_PRODUCT_ID", "");
        }

        //3- 本地商品编码转集团商品编码
        IData inparam = new DataMap();
        inparam.put("ID", "1");
        inparam.put("ID_TYPE", "B");
        inparam.put("ATTR_CODE", poProductId);
        inparam.put("ATTR_OBJ", "PRO");
        IDataset attrBizInfoList = CSViewCall.call(this, "CS.AttrBizInfoQrySVC.getBizAttr", inparam);
        if(IDataUtil.isEmpty(attrBizInfoList)){
            return merchId;
        }
        merchId =attrBizInfoList.getData(0).getString("ATTR_VALUE");

        //4- 返回集团商品编码
        return merchId;
    }

    /*
     * @desctiption 将产品参数类型由IData向IDataset转化
     * @author xunyl
     * @Date 2015-10-15
     */
    public static IDataset merchPParamsToDataset(IData paramMap) throws Exception
    {
        IDataset productParams = new DatasetList();
        Iterator<String> iterator = paramMap.keySet().iterator();
        while (iterator.hasNext())
        {
            String mapKey = iterator.next();
            IData valueMap = paramMap.getData(mapKey);
            productParams.add(valueMap);
        }
        return productParams;
    }

    /**
     * @description 查找对应的参数值(仅适合流量统付的成员参数)
     * @author xunyl
     * @date 2015-10-12
     */
    private static String getAttrValueByCode(String attrCode,IDataset attrInfoList)throws Exception{
        if(IDataUtil.isEmpty(attrInfoList)){
            return "";
        }

        for(int i=0;i<attrInfoList.size();i++){
            IData attrInfo = attrInfoList.getData(i);
            String tempAttrCode = attrInfo.getString("ATTR_CODE");
            if(tempAttrCode.endsWith(attrCode)){
                return attrInfo.getString("ATTR_VALUE");
            }
        }

        return "";
    }

    /**
     * 产品注销界面初始化
     */
    public void initDstUs(IRequestCycle cycle) throws Exception
    {
        initSimple(cycle);

        // 方便后面存放联系人信息
        productGoodInfo = new DataMap();
        productGoodInfo.put("GOOD_INFO", new DataMap());

        // - 获取并设置商产品信息
        setProductGoodInfos(productGoodInfo);

        IData conditionData = new DataMap();
        conditionData.put("METHOD", "DstUs");

        conditionData.put("operType", GroupBaseConst.MERCH_STATUS.MERCH_CANCLE.getValue());// 所有商品都为注销
        iniparam(cycle, conditionData);

        setCondition(conditionData);
    }

    /**
     * 产品受理、变更、注销界面初始化公用方法
     *
     * @param cycle
     * @throws Exception
     */
    private void initSimple(IRequestCycle cycle) throws Exception
    {
        String productId = getData().getString("PRODUCT_ID", "");
        if (StringUtils.isEmpty(productId))
        {
            productId = getData().getString("GRP_PRODUCT_ID", "");
        }
        groupId = getData().getString("GROUP_ID");
        if (StringUtils.isBlank(groupId))
        {
        	String grpCustId = getData().getString("CUST_ID");
        	IData data = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, grpCustId);
        	groupId = data.getString("GROUP_ID");
        	getData().put("GROUP_ID", groupId);
			
		}

        setGrpProductId(productId);
        init(productId);
    }

    /**
     * 提交
     */
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {
        redirectTo("group.querygroupinfo.GroupBbossManageCreate", "sendCreateDataBefore");
    }

    /**
     * chenyi 查询产品参数
     *
     * @return
     * @throws Exception
     */
    protected IDataset queryParams(IDataset productInfoList) throws Exception
    {

        IDataset merchpParams = new DatasetList();
        IDataset productInfoListClone= (IDataset) Clone.deepClone(productInfoList);
        
        for (int i = productInfoListClone.size() - 1; i >= 0; i--)
        {

            IData productInfo = productInfoListClone.getData(i);
            // 先做判断是否是相同的产品，且操作相同，如果是 则在页面只显示一组属性
            if (productInfo.getString("PRODUCT_ID").equals(productInfoListClone.getData(0).getString("PRODUCT_ID")) && (i != 0) && (productInfoListClone.getData(0).getString("PRODUCT_OPER_CODE").equals(productInfo.getString("PRODUCT_OPER_CODE"))))
            {

                productInfoListClone.remove(0);
                continue;
            }

            // 获取集团下发编码

            IDataset productBizInfos = AttrBizInfoIntfViewUtil.qryAttrBizInfosByIdAndIdTypeAttrObjAttrCode(this, "1", "B", "PRO", productInfo.getString("PRODUCT_ID"));
            if (IDataUtil.isEmpty(productBizInfos))
            {
                productBizInfos = new DatasetList();
            }
            for (int j = 0; j < productBizInfos.size(); j++)
            {
                IData productBizInfo = productBizInfos.getData(j);
                // 默认集团
                IDataset productParams = BBossAttrInfoIntfViewUtil.qryBBossAttrInfosByProductIdAndOperTypeBizType(this, productBizInfo.getString("ATTR_VALUE"), productInfo.getString("PRODUCT_OPER_CODE"), "1");
                if (IDataUtil.isNotEmpty(productParams))
                {

                    for (int m = 0; m < productParams.size(); m++)
                    {
                        IData productParam = productParams.getData(m);
                        productParam.put("PRODUCT_NAME", ProductInfoIntfViewUtil.qryProductNameStrByProductId(this, productInfo.getString("PRODUCT_ID")));
                        productParam.put("PRODUCT_ID", productInfo.getString("PRODUCT_ID"));

                        /*
                         * 对原来填写过的特殊参数 再次填写做处理 把原来填写的值带出到页面 start liuxx3 2014-07-31
                         */
                        IDataset userParamInfos = UserAttrInfoIntfViewUtil.qryBBossUserAttrInfo(this, productInfo.getString("USER_ID"), productParam.getString("ATTR_CODE"));

                        if (IDataUtil.isNotEmpty(userParamInfos))
                        {
                            IData userParamInfo = userParamInfos.getData(0);
                            productParam.put("ATTR_VALUE", userParamInfo.getString("ATTR_VALUE", ""));
                        }
                        else
                        {
                            productParam.put("ATTR_VALUE", productParam.getString("DEFAULT_VALUE"));
                        }

                        // end start liuxx3 2014-07-31

                        // 根据产品操作编号来判定属性是否必填
                        String attrMandatory = productParam.getString("MANDATORY");
                        productParam.put("MANDATORY", 1);// 默认为不必填
                        if (StringUtils.isNotBlank(attrMandatory))
                        {
                            String[] mandatory = attrMandatory.split(",");
                            for (int n = 0; n < mandatory.length; n++)
                            {
                                if (productInfo.getString("PRODUCT_OPER_CODE").equals(mandatory[n]))
                                {
                                    productParam.put("MANDATORY", 0);
                                    break;
                                }
                            }
                        }
                        // 判断属性是否可编辑
                        String readOnly = productParam.getString("READONLY", "");
                        if (StringUtils.isNotBlank(readOnly))
                        {
                            String[] readOnlyList = readOnly.split(",");
                            for (int n = 0; n < readOnlyList.length; n++)
                            {
                                if (productInfo.getString("PRODUCT_OPER_CODE").equals(readOnlyList[n]))
                                {
                                    productParam.put("READONLY", 0);
                                    break;
                                }
                            }
                        }

                        // 判断属性类型是否为下拉列表类型
                        if (("SELECTION").equals(productParam.getString("EDIT_TYPE")))
                        {
                            // 根据产品属性获取下拉列表的值

                            String paramCode = productParam.getString("ATTR_CODE");
                            // 参数值,用于填充下拉列表
                            IDataset dsValues = querySeletionData(paramCode);
                            productParam.put("VALUE_LIST", dsValues);
                        }

                        merchpParams.add(productParam);
                    }

                }

            }

        }
        return merchpParams;
    }

    /**
     * @author weixb3
     * @description 查询下拉列表的值
     * @2012-09-27
     */
    public IDataset querySeletionData(String paramCode) throws Exception
    {
        return BoundDataInfoIntfViewUtil.qryBoundDataInfosByParamCode(this, paramCode);
    }

    /**
     * 查询联系人电话及其总部用户名
     *
     * @param cycle
     * @throws Exception
     */
    public void queryStaffNumber(IRequestCycle cycle) throws Exception
    {
        String staffId = getData().getString("STAFF_ID");
        IData param = new DataMap();
        IData staffInfoData = StaffBBossInfoIntfViewUtil.qryBBossAttrInfoByProductIdAndOperTypeBizType(this, staffId);
        param.put("STAFF_ID", staffId);
        param.put("STAFF_NUMBER", staffInfoData.getString("STAFF_NUMBER", ""));
        param.put("STAFF_PHONE", staffInfoData.getString("SERIAL_NUMBER", ""));
        setAjax(param);
    }

	public void attFileCheck(IRequestCycle cycle) throws Throwable{
    	IData inParam = new DataMap();
    	inParam.put("FILE_ID", getData().getString("ATT_CODE"));
        IDataset reList = CSViewCall.call(this, "CS.MFileInfoQrySVC.qryFileInfoByFileId", inParam);
        IData redata = new DataMap();
        String reslut = "true";
        if (IDataUtil.isEmpty(reList))
        {
        	reslut = "false";
            redata.put("ERROR_MESSAGE", GrpException.CRM_GRP_642);
        }
        String fileName = reList.getData(0).getString("FILE_NAME");
        String[] fileNameList = fileName.split("\\.");
        if (fileNameList.length <= 1)
        {
        	reslut = "false";
            redata.put("ERROR_MESSAGE", fileName+"合同附件无后缀名,格式错误,请重新上传!");
        }

        redata.put("RESULT", reslut);
        setAjax(redata);
    }

    /*
     * @description 传递商产品信息至产品参数页面
     * @author xunyl
     * @date 2013-06-29
     */
    public void transProductGoodInfos(IRequestCycle cycle) throws Throwable
    {
    	
        // 1- 获取集团编号(该集团编号作为商产品信息的key值，用来保证缓存信息的唯一性)
        String groupId = getData().getString("GROUP_ID");

        // 2- 获取商产品信息
        String productGoodInfos = getData().getString("productGoodInfos");

        // 3- 将商产品信息保存至缓存中
        String key = CacheKey.getBossProductInfoKey(getVisit().getStaffId(), groupId);
        SharedCache.set(key, productGoodInfos, 1200);
    }
    
    public abstract void setAddAttInfo(IData info);

    public abstract void setAddAuditorInfo(IData info);

    public abstract void setAddContactorInfo(IData info);

    public abstract void setAttInfo(IData info);

    public abstract void setAttInfos(IDataset infos);

    public abstract void setAttInfoTag(boolean tag);

    public abstract void setAuditorInfo(IData info);

    public abstract void setAuditorInfos(IDataset infos);

    public abstract void setAuditorInfoTag(boolean tag);

    public abstract void setCondition(IData info);

    public abstract void setContactorInfo(IData info);

    public abstract void setContactorInfos(IDataset infos);

    public abstract void setContactorInfoTag(boolean tag);

    public abstract void setGrpProductId(String grpProductId);

    public abstract void setInfo(IData info);

    public abstract void setProductCtrlInfo(IData info);

    // 商产品信息
    public abstract void setProductGoodInfos(IData productGoodInfos);
}
