
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.AgentsException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UDepartInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UDepartKindInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCallIntf;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.*;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.DepartInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.ChangeProductBean;

import org.apache.log4j.Logger;

public class CreatePersonUserSVC extends CSBizService
{
	protected static Logger log = Logger.getLogger(CreatePersonUserSVC.class);

    private static final long serialVersionUID = 1L;

    /**
     * 根据品牌过滤产品类型
     *
     * @param productTypeList
     * @param strBrandCode
     * @return
     * @throws Exception
     */
    public static IDataset filterProductTypeListByBrandCode(IDataset productTypeList, String strBrandCode) throws Exception
    {
        // 通过strBrandCode查询所有对应的产品类型
        IDataset dataset = ProductTypeInfoQry.getProductTypeByBrand(strBrandCode);
        String strProductTypes = "";
        for (int i = 0; i < dataset.size(); i++)
        {
            strProductTypes = strProductTypes + "," + dataset.getData(i).getString("PRODUCT_TYPE_CODE");
        }
        IData productTypeData = null;
        String brandCode = "";
        for (int i = 0; i < productTypeList.size(); i++)
        {
            productTypeData = productTypeList.getData(i);
            brandCode = productTypeData.getString("PRODUCT_TYPE_CODE");
            if (strProductTypes.indexOf(brandCode) < 0)
            {
                productTypeList.remove(i);
                i--;
            }
        }
        return productTypeList;
    }

    /**
     * 根据产品类型默认标记过滤产品类型
     *
     * @param oldList
     * @param strDefaultTag
     * @return
     * @throws Exception
     */
    public static IDataset filterProductTypeListByDefaultTag(IDataset productTypeList, String strDefaultTag) throws Exception
    {
        IData productTypeData = null;
        String defaultTag = "";// 默认标记
        String parentTypeCode = "";// 父结点
        for (int i = 0; i < productTypeList.size(); i++)
        {
            productTypeData = productTypeList.getData(i);
            defaultTag = productTypeData.getString("DEFAULT_TAG");
            if (strDefaultTag.indexOf(defaultTag) < 0)
            {
                productTypeList.remove(i);
                i--;
            }
        }
        return productTypeList;
    }

    /**
     * 获取当前操作员的depart_id和depart_name
     */
    public static String getDeaprtInfo(String tradeDepartId) throws Exception
    {
        IData param = new DataMap();
        IData data = UDepartInfoQry.qryDepartByDepartId(tradeDepartId);
        if (IDataUtil.isEmpty(data))
            return "";
        return data.getString("DEPART_ID", "") + " " + data.getString("DEPART_NAME", "") + " " + data.getString("DEPART_CODE", "");
    }

    /**
     * 处理sql绑定多个值，in方式
     *
     * @param strOldBindValue
     * @return
     * @throws Exception
     */
    public static String getMulteBindValue(String strOldBindValue) throws Exception
    {

        if (strOldBindValue.startsWith(","))
            strOldBindValue = strOldBindValue.substring(1);// 去掉首字符为","号
        if (strOldBindValue.endsWith(","))
            strOldBindValue = strOldBindValue.substring(0, strOldBindValue.length() - 1);// 去掉尾字符为","号
        // 不存在多个值
        if (strOldBindValue.indexOf(",") == -1)
            return "";

        return strOldBindValue;
    }

    /**
     * 获取父子二级产品类型列表
     *
     * @param productTypeList
     * @return
     * @throws Exception
     */
    public static IData getParentChildProductTypeList(IDataset productTypeList) throws Exception
    {
        IData productTypeData = null;
        IData returnData = new DataMap();
        String parentPtypeCode = "";
        IDataset parentProductTypeList = new DatasetList();
        IDataset childProductTypeList = new DatasetList();
        String childParentTypeCode = "";
        for (int i = 0; i < productTypeList.size(); i++)
        {
            productTypeData = productTypeList.getData(i);
            parentPtypeCode = productTypeData.getString("PARENT_PTYPE_CODE");
            if ("0000".equals(parentPtypeCode))
            {
                parentProductTypeList.add(productTypeData);
            }
            else
            {
                childProductTypeList.add(productTypeData);
            }
        }
        returnData.put("PARENT_PRODUCT_TYPE_LIST", parentProductTypeList);
        returnData.put("CHILD_PRODUCT_TYPE_LIST", childProductTypeList);
        return returnData;
    }

    /**
     * 获取产品信息用于产品目录选择(多产品时)
     *
     * @param pd
     * @param strBindProducts
     * @param strEparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getProductInfo(String strBindProducts, String strEparchyCode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("EPARCHY_CODE", strEparchyCode);
        if (strBindProducts.startsWith(","))
        {
            strBindProducts = strBindProducts.substring(1);
        }
        if (strBindProducts.endsWith(","))
        {
            strBindProducts = strBindProducts.substring(0, strBindProducts.length() - 1);
        }
        // 188号码通过“非”的条件查询所有其他全球通的产品
        if (strBindProducts.indexOf("!") != -1)
        {
            String tempProductId = "";
            String productArray[] = strBindProducts.split(",");
            String spellParam = "";
            for (int i = 0; i < productArray.length; i++)
            {
                spellParam += ",:PRODUCT_ID" + i;
                tempProductId = productArray[i].replace("!", "");
                inData.put("PRODUCT_ID" + i, tempProductId);
            }
            inData.put("BRAND_CODE", "G001");

            String reGex = ":PRODUCT_ID";
            String replaceMent = spellParam.substring(1);
            String sqlStmt = "";
            String strTemp = sqlStmt.replace("IN", "NOT IN");
            String sqlStmtNew = strTemp.replaceAll(reGex, replaceMent);

            return new DatasetList();

        }
        // 其他
        if (strBindProducts.indexOf(",") != -1)
        {// 多产品
            String productArray[] = strBindProducts.split(",");
            String spellParam = "";
            for (int i = 0; i < productArray.length; i++)
            {
                spellParam += ",:PRODUCT_ID" + i;
                inData.put("PRODUCT_ID" + i, productArray[i]);
            }

            String reGex = ":PRODUCT_ID";
            String replaceMent = spellParam.substring(1);
            String sqlStmt = "";
            String sqlStmtNew = sqlStmt.replaceAll(reGex, replaceMent);
            return new DatasetList();
        }
        else
        {// 单个产品
            return ProductInfoQry.getBatProductInfo(strBindProducts);
        }
    }

    /**
     * 根据产品标识获取产品类型
     *
     * @param strProductId
     * @param strEparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getProductTypeCodeByProductId(String strProductId, String strEparchyCode) throws Exception
    {

        IData inData = new DataMap();
        inData.put("PRODUCT_ID", strProductId);// 个人产品标识
        inData.put("EPARCHY_CODE", strEparchyCode);// 用户路由
        if (strProductId.indexOf(",") == -1)
        {// 单参数
            return ProductTypeInfoQry.getProductTypeByProductID(strProductId, strEparchyCode);
        }
        else
        {
            // 现没有绑定多个产品情况
            String strOldBindValue = getMulteBindValue(strProductId);
            return ProductTypeInfoQry.getProductTypeByProductID(strOldBindValue, strEparchyCode);
        }
    }

    /**
     * 判断是不是营业厅的tradedepart
     */
    public static int isOperatorDepart(String eparchyCode, String tradeDepartId) throws Exception
    {
        IData param = new DataMap();
        IDataset dataset = UDepartKindInfoQry.qryDeparKindByDepartId(eparchyCode, tradeDepartId);
        if (dataset.isEmpty())
            return -1;
        return dataset.getData(0).getInt("CODE_TYPE_CODE", -1);
    }

    /**
     * 弹出信息提示
     *
     * @param obj
     * @throws Exception
     */
    public static void redirectToMsgForCallSvc(Object obj) throws Exception
    {
        String xResultCode = "";
        String xResultInfo = "";
        if (obj instanceof IDataset)
        {
        	IData xResultData = ((IDataset) obj).getData(0);
            xResultCode = xResultData.getString("X_RESULTCODE", "0");
            xResultInfo = xResultData.getString("X_RESULTINFO", "");
            if (!"0".equals(xResultCode))
            {
                CSAppException.apperr(BizException.CRM_BIZ_5, xResultInfo);
            }
        }
        else if (obj instanceof IData)
        {
        	IData xResultData = ((IData) obj);
            xResultCode = xResultData.getString("X_RESULTCODE", "0");
            xResultInfo = xResultData.getString("X_RESULTINFO", "");
            if (!"0".equals(xResultCode))
            {
                CSAppException.apperr(BizException.CRM_BIZ_5, xResultInfo);
            }
        }

    }

    /**
     * 开户数限制校验
     *
     * @author sunxin
     * @param input
     * @throws Exception
     */
    public IDataset checkRealNameLimitByPspt(IData input) throws Exception
    {

        IDataset ajaxDataset = new DatasetList();
		IData ajaxData = new DataMap();
        String custName = input.getString("CUST_NAME").trim();
        String psptId = input.getString("PSPT_ID").trim();
		String serialNumber = input.getString("SERIAL_NUMBER","").trim();
		CreatePersonUserBean createPersonUserBean = BeanManager.createBean(CreatePersonUserBean.class);
		if(createPersonUserBean.isPwlwOper(serialNumber, "")){//物联网号码取消本地一证多号校验
			ajaxData.put("MSG", "OK");
			ajaxData.put("CODE", "0");
			ajaxDataset.add(ajaxData);
			return ajaxDataset;
		}
        IData param = new DataMap();
        if (!"".equals(custName) && !"".equals(psptId))
        {
        	ajaxData.clear();
        	/**
        	 * REQ201611180016 关于特殊调整我公司营业执照开户使用人不限制5户的需求
        	 * chenxy3 20161212
        	 * */
        	String psptTypeCode=input.getString("PSPT_TYPE_CODE");
        	if("E".equals(psptTypeCode) && "91460000710920952X".equals(psptId) && "中国移动通信集团海南有限公司".equals(custName)){
        		ajaxData.put("MSG", "OK");
                ajaxData.put("CODE", "0");
                ajaxDataset.add(ajaxData);
        		return ajaxDataset;
        	}
        	
        	/**
        	 * 优化单位证件开户阀值权限设置需求
        	 * mengqx
        	 */
        	int rCount;
        	int rLimit;
        	String userType = input.getString("USER_TYPE","");
        	if(userType!=null && !"".equals(userType)){
        		rCount = UserInfoQry.getRealNameUserCountByPspt2New(custName, psptId, userType);
                rLimit = UserInfoQry.getRealNameUserLimitByPsptNew(custName, psptId, userType);
        	}else {
            	rCount = UserInfoQry.getRealNameUserCountByPspt2(custName, psptId);
            	//add by zhangxing3 for REQ201906130010关于本省一证五号优化需求
                rCount += UserInfoQry.getRealNameUserCountByUsePspt(custName, psptId, null);	// 判断一证五号个数以登记该证件为户主和使用人合并计算           
                //add by zhangxing3 for REQ201906130010关于本省一证五号优化需求
                rLimit = UserInfoQry.getRealNameUserLimitByPspt(custName, psptId);
        	}

            ajaxData.put("rCount", rCount);
            ajaxData.put("rLimit", rLimit);
            if (rCount < rLimit)
            {
                ajaxData.put("MSG", "OK");
                ajaxData.put("CODE", "0");
            }
            else
            {
                ajaxData.put("MSG", "证件号码【" + psptId + "】实名制开户的数量已达到最大值【" + rLimit + "个】，请更换其它证件！");
                ajaxData.put("CODE", "1");
            }
            ajaxDataset.add(ajaxData);

        }
        return ajaxDataset;
    }

    /**
     *
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset checkSerialNumberByOperate(IData input) throws Exception
    {
    	IDataset ajaxDataset = new DatasetList();
    	String privTag = "0";
        if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "SYSCHANGPACKAGE"))
            privTag = "1";

        IData ajaxData = new DataMap();
        ajaxData.put("X_RESULTCODE", "0");
		ajaxData.put("X_RESULTINFO", "ok");
        String serialNumber = input.getString("SERIAL_NUMBER", "");
        IDataset dataSet = ResCall.getMphonecodeInfo(serialNumber);
        if (IDataUtil.isNotEmpty(dataSet))
        {
        	IData mphonecodeInfo = dataSet.first();
        	String beautifulTag = mphonecodeInfo.getString("BEAUTIFUAL_TAG");
        	if (StringUtils.equals("1", beautifulTag))
        	{
        		ajaxData.put("BEAUTIFUAL_TAG", beautifulTag);
        		String productId = mphonecodeInfo.getString("BIND_PRODUCT_ID");
        		String packageId = mphonecodeInfo.getString("BIND_PACKAGE_ID");
        		if(StringUtils.isBlank(productId))
        		{
        			ajaxData.put("X_RESULTCODE", "-1");
        			ajaxData.put("X_RESULTINFO", "获取吉祥号码"+serialNumber+"需绑定的营销活动产品编码为空，请检查资源侧配置！");
        		}
        		else if(StringUtils.isBlank(packageId))
        		{
        			ajaxData.put("X_RESULTCODE", "-1");
        			ajaxData.put("X_RESULTINFO", "获取吉祥号码"+serialNumber+"需绑定的营销活动产品编码为空，请检查资源侧配置！");
        		}
        		else if("0".equals(privTag))
        		{
        			ajaxData.put("X_RESULTCODE", "-1");
        			ajaxData.put("X_RESULTINFO", "获取吉祥号码"+serialNumber+"没有开户权限！");
        		}

        	}

        }

        ajaxDataset.add(ajaxData);

        return ajaxDataset;
    }

    /**
     * 使用人证件号码数限制校验
     *
     * @author yanwu
     * @param input
     * @throws Exception
     */
    public IDataset checkRealNameLimitByUsePspt(IData input) throws Exception
    {

        IDataset ajaxDataset = new DatasetList();
        String custName = input.getString("CUST_NAME").trim();
        String psptId = input.getString("PSPT_ID").trim();
        String psptTypeCode=input.getString("PSPT_TYPE_CODE");
        String page=input.getString("page");
        String serialNumber = input.getString("SERIAL_NUMBER","");
        //IData param = new DataMap();
        if (!"".equals(custName) && !"".equals(psptId))
        {
        	IData ajaxData = new DataMap();
        	/**
        	 * REQ201611180016 关于特殊调整我公司营业执照开户使用人不限制5户的需求
        	 * chenxy3 20161212
        	 * */
        	if("E".equals(psptTypeCode) && "91460000710920952X".equals(psptId) && "中国移动通信集团海南有限公司".equals(custName)){
        		ajaxData.put("MSG", "OK");
                ajaxData.put("CODE", "0");
                ajaxDataset.add(ajaxData);
        		return ajaxDataset;
        	}
        	/**
        	 * REQ201712230009关于TD固话业务使用人及国际长途服务功能优化的需求:放开对TD二代固话业务录入使用人信息的限制
        	 * zhangxing3 20170312
        	 * */
        	if(("D".equals(psptTypeCode) || "E".equals(psptTypeCode) || "G".equals(psptTypeCode)|| "L".equals(psptTypeCode)|| "M".equals(psptTypeCode))
        			&& "createtdusertrade.CreateTDPersonUser".equals(page)){
        		ajaxData.put("MSG", "OK");
                ajaxData.put("CODE", "0");
                ajaxDataset.add(ajaxData);
        		return ajaxDataset;
        	}
            int rCount = UserInfoQry.getRealNameUserCountByUsePspt(custName, psptId, serialNumber);	// 获取使用人证件号码已实名制开户的数量
            //add by zhangxing3 for REQ201906130010关于本省一证五号优化需求
            rCount += UserInfoQry.getRealNameUserCountByPspt2(custName, psptId);	// 判断一证五号个数以登记该证件为户主和使用人合并计算           
            //add by zhangxing3 for REQ201906130010关于本省一证五号优化需求
            int rLimit = UserInfoQry.getRealNameUserLimitByPspt(custName, psptId);


            ajaxData.put("rCount", rCount);
            ajaxData.put("rLimit", rLimit);
            if (rCount < rLimit)
            {
                ajaxData.put("MSG", "OK");
                ajaxData.put("CODE", "0");
            }
            else
            {
                ajaxData.put("MSG", "使用人证件号码【" + psptId + "】实名制开户的数量已达到最大值【" + rLimit + "个】，请更换其它证件！");
                ajaxData.put("CODE", "1");
            }
            ajaxDataset.add(ajaxData);

        }
        return ajaxDataset;
    }

    /**
     * 证件号码数限制校验
     *
     * @author yanwu
     * @param input
     * @throws Exception
     */
    public IDataset checkRealNameLimitByPsptCtt(IData input) throws Exception
    {
    	IDataset ajaxDataset = new DatasetList();
        String custName = input.getString("CUST_NAME").trim();
        String psptId = input.getString("PSPT_ID").trim();
        String psptTypeCode=input.getString("PSPT_TYPE_CODE");
        String strNetTypeCode = input.getString("NET_TYPE_CODE").trim();
        if (!"".equals(custName) && !"".equals(psptId))
        {
        	IData ajaxData = new DataMap();
        	/**
        	 * REQ201611180016 关于特殊调整我公司营业执照开户使用人不限制5户的需求
        	 * chenxy3 20161212
        	 * */
        	if("E".equals(psptTypeCode) && "91460000710920952X".equals(psptId) && "中国移动通信集团海南有限公司".equals(custName)){
        		ajaxData.put("MSG", "OK");
                ajaxData.put("CODE", "0");
                ajaxDataset.add(ajaxData);
        		return ajaxDataset;
        	}
            int rCount = UserInfoQry.getRealNameUserCountByPspt2(custName, psptId, strNetTypeCode);	// 获取使用人证件号码已实名制开户的数量
            int rLimit = 5;


            ajaxData.put("rCount", rCount);
            ajaxData.put("rLimit", rLimit);
            if (rCount < rLimit)
            {
                ajaxData.put("MSG", "OK");
                ajaxData.put("CODE", "0");
            }
            else
            {
                ajaxData.put("MSG", "证件号码【" + psptId + "】实名制开户的数量已达到最大值【" + rLimit + "个】，请更换其它证件！");
                ajaxData.put("CODE", "1");
            }
            ajaxDataset.add(ajaxData);
        }
        return ajaxDataset;
    }

    /**
     * 二次开户登录员业务区必须和号码归属业务一致
     *
     * @author chenzm
     * @param userInfo
     * @throws Exception
     */
    public void checkReOpenCityCode(IData userInfo) throws Exception
    {

        IData tagInfo = getTagInfo(CSBizBean.getUserEparchyCode(), "CS_AGAINCITYCHECK", "0");
        if ("1".equals(tagInfo.getString("TAG_CHAR", "")))
        {
            if (!getVisit().getCityCode().equals(userInfo.getString("CITY_CODE", getVisit().getCityCode())))
            {
                CSAppException.apperr(BizException.CRM_BIZ_5, PersonConst.SAME_TO_CITY_CODE_IN_RE_OPEN);
            }
        }
    }

    /**
     * 输入新开户号码后的校验，获取开户信息
     *
     * @author chenzm
     * @param input
     * @throws Exception
     */
    public IDataset checkSerialNumber(IData input) throws Exception
    {
        IData returnData = this.checkSerialNumberForSvc(input);
        // 需要判断 returndata里的A_X_CODING_STR
        String saleProductId = returnData.getString("A_X_CODING_STR", "").split("\\|")[0];
        String fee = returnData.getString("FEE_CODE_FEE");
        String kindCodeBySn = returnData.getString("RES_KIND_CODE_SN");// 废弃原先查询“OPEN”里的号码idle记录，改为直接取值判断
        if (!StringUtils.isBlank(saleProductId))
        {
            // add by xuyt 20140401 REQ201401240024 关于取消TD一代吉祥号码管理规则的申请 处理费用也在此

            IDataset dataset5 = ParamInfoQry.getCode1ForOpen("OPEN");
            if (IDataUtil.isNotEmpty(dataset5))
            {
                if ("0W".equals(kindCodeBySn))
                {
                    returnData.put("PACKAGES_SALE", "");
                    returnData.put("BIND_SALE_TAG", "0");
                    returnData.put("X_CODING_STR", "null");
                }
                else
                {
                    IDataset packages = ProductPkgInfoQry.getPackageByProductIdForOpen(saleProductId);
                    returnData.put("PACKAGES_SALE", packages);
                    returnData.put("BIND_SALE_TAG", "1");
                    returnData.put("X_CODING_STR", returnData.getString("A_X_CODING_STR", ""));
                }

            }
            else
            {
                returnData.put("PACKAGES_SALE", "");
                returnData.put("BIND_SALE_TAG", "0");
                returnData.put("X_CODING_STR", "null");
            }
        }
        if (StringUtils.isNotBlank(fee))
        {
            if ("0W".equals(kindCodeBySn))
                returnData.put("FEE_CODE_FEE", "");
        }
        IData data = this.createProductInfoForSvc(returnData);
        returnData.putAll(data);
        /**
         * REQ201608310006 关于海口分公司四级吉祥号码规则优化（二）
	     * 判断是否四级吉祥号码
	     * chenxy3 20160927
         * */
        if (StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "CHANGPACKAGEANDFEE")){
	        IData inparam=new DataMap();
	        inparam.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER",""));
	        inparam.put("SN_CLASS", "4");
	        boolean flag=checkIfBeautyNo(inparam);
	        if(flag){
	        	returnData.put("SYSCHANGPACKAGE4", "1");
	        }
        }
        if (StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "SYSCHANGPACKAGE")){
        	returnData.put("SYSCHANGPACKAGE", "1");
        }
        if (PersonConst.IOT_OPEN.equals(returnData.getString("OPEN_TYPE_CODE", ""))){//物联网开户时，检验吉祥号开户权限 add by fufn REQ201709050002
        	if (!StringUtils.isBlank(saleProductId)&&!StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "SYS_OPENUSER_WLW")){//检验开户权限
        		CSAppException.apperr(BizException.CRM_BIZ_5, "您无物联网吉祥号开户权限");
       	 	}
        }
        IDataset dataset = new DatasetList();
        dataset.add(returnData);
        return dataset;
    }

    /**
     * 输入新开户号码后的校验，获取开户信息
     *
     * @author sunxin
     * @param data
     * @return
     * @throws Exception
     */
    public IData checkSerialNumberForSvc(IData data) throws Exception
    {
    	String numberLimitFlag = data.getString("NUMBER_LIMIT_FLAG");
    	
    	//REQ201903080045  关于魔百和无手机号码使用号码的需求 @tanzheng add by 20190417 start
    	if("1".equals(numberLimitFlag)){
    		 CreatePersonUserBean createPersonUserBean = BeanManager.createBean(CreatePersonUserBean.class);
    		 createPersonUserBean.checkNumberLimit(data);
    	}
    	//REQ201903080045  关于魔百和无手机号码使用号码的需求 @tanzheng add by 20190417 end
    	
    	
    	
        String openType =  data.getString("OPEN_TYPE", "");
        if (openType.equals("IOT_OPEN")) {//物联网开户
            String serialNumber = data.getString("SERIAL_NUMBER", "").trim();
            if (serialNumber.length() >= 3 && serialNumber.startsWith("147")) {
                String privTag = "0";
                if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "SYS_OPENUSER_WLW_147")) {
                    privTag = "1";
                }
                if ("0".equals(privTag)) {
                    CSAppException.apperr(BizException.CRM_BIZ_5, PersonConst.IS_NOT_FOR_OPENUSER_WLW147);
                }
            }
        }
        IData returnData = new DataMap();
        /*
         * add by zhangxing at 2010-05-05 17:10 for "签约赠送188号码" start 判断是否是签约赠送188号码新号码，如果是则修改号码状态、归属
         */
        String authFlag = data.getString("PERSON_AUTH_FLAG", "false");
        String serialNumber = data.getString("SERIAL_NUMBER", "");
        String oldSerialNumber = data.getString("OLD_SERIAL_NUMBER", "");
        String authSerialNumber = data.getString("AUTH_FOR_PERSON_SERIAL_NUMBER", "");
        String authUserId = "";
        IData map = new DataMap();
        IData tmpData = new DataMap();
        IDataset dataset = new DatasetList();
        /*
         * map.put("SERIAL_NUMBER", authSerialNumber); map.put("REMOVE_TAG", "0"); map.put("NET_TYPE_CODE", "00");
         * dataset =UcaInfoQry.qryUserInfoBySn(authSerialNumber); if(dataset != null &&!dataset.isEmpty()){ authUserId =
         * dataset.getData(0).getString("USER_ID", ""); } if( "true".equals(authFlag) && !"".equals(serialNumber) &&
         * !"".equals(authUserId)) { returnData.put("AUTH_FOR_SALE_ACTIVE_SERIAL_NUMBER", authSerialNumber);
         * returnData.put("AUTH_FOR_SALE_ACTIVE_USER_ID", authUserId); returnData.put("AUTH_FOR_SALE_ACTIVE_TAG",
         * authFlag); } 测试屏蔽 sunxin
         */
        /*                      ** add by zhangxing at 2010-05-05 17:10 for "签约赠送188号码" end */
        // 查看是否可开户
        IData canOpenInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);

        // boolean bOpenAgain = false;
        if (IDataUtil.isNotEmpty(canOpenInfo))
        {
            CSAppException.apperr(BizException.CRM_BIZ_5, PersonConst.IS_NOT_FOR_OPEN_TRADE);

        }
        returnData.put("B_REOPEN_TAG", "0"); // 将二次开户的标记位放到页面

        String agentDepartId = data.getString("AGENT_DEPART_ID", "");
        String OCCUPY_TYPE_CODE = "0";

        // 代理商开户时，检查有没有选择代理商
        if (PersonConst.AGENT_OPEN.equals(data.getString("OPEN_TYPE", "")))
        {

            if (StringUtils.isBlank(agentDepartId))
            {
                CSAppException.apperr(AgentsException.CRM_AGENTS_3);
            }
            IData depart = UDepartInfoQry.qryDepartByDepartId(agentDepartId);
            if (IDataUtil.isEmpty(depart))
                CSAppException.apperr(BizException.CRM_BIZ_5, "代理商编码【" + agentDepartId + "】不存在，请重新输入！");
            returnData.put("AGENT_DEPART_ID", agentDepartId); // 代理商部门编码，从开户代理商里取值PARA_CODE1
            // 处理代理商开户:代理商开户并且不以登录员工部门检查
            if (!"1".equals(data.getString("RES_CHECK_BY_DEPART")))
            {
                returnData.put("CHECK_DEPART_ID", agentDepartId); // 代理商部门编码，从开户代理商里取值PARA_CODE1
            }
            else
            {
                returnData.put("CHECK_DEPART_ID", getVisit().getDepartId());// 根据操作员部门校验
            }
            OCCUPY_TYPE_CODE = "2";
        }
        else
        {
            returnData.put("CHECK_DEPART_ID", getVisit().getDepartId());// 根据操作员部门校验
        }

        // 分库标识
        int iResult = getDiffDataBase();
        // 获取号码归属
        IData mofficeInfo = getMphoneAddress(data, iResult);
        returnData.put("B_SAME_EPARCHY_CODE", mofficeInfo.getString("B_SAME_EPARCHY_CODE"));
        returnData.put("B_DIFF_TRADE", mofficeInfo.getString("B_DIFF_TRADE"));

        // 省内异地开户，号码归属不能与当前操作地市一致 暂时不需要，sunxin
        /*
         * if (PersonConst.PROV_REMOTE_OPEN.equals(data.getString("OPEN_TYPE", ""))) { if
         * (BizRoute.getRouteId().equals(CSBizBean.getVisit().getStaffEparchyCode())) {
         * CSAppException.apperr(BizException.CRM_BIZ_5, PersonConst.EPARCHY_IS_MUST_BE_DIF_IN_PROV_REMOTE_OPEN); }
         * }else { if (!BizRoute.getRouteId().equals(CSBizBean.getTradeEparchyCode())) {
         * CSAppException.apperr(BizException.CRM_BIZ_5, PersonConst.EPARCHY_IS_MUST_BE_DIF_IN_PROV_OPEN); } }
         */

        // 二次开户 海南没有此场景，不需要 sunxin
        /*
         * if (bOpenAgain) { returnData.put("CHECK_RESULT_CODE", "0"); // 设置号码校验完成 returnData.put("CHECK_RESULT_CODE",
         * "1"); // 设置sim卡校验完成 returnData.put("B_REOPEN_TAG", "1"); // 将二次开户的标记位放到页面 // 获取三户资料 IData param = new
         * DataMap(); param.put("SERIAL_NUMBER", serialNumber); param.put("TRADE_TYPE_CODE",
         * PersonConst.TRADE_TYPE_CODE_CREATE_PERSON_USER); IDataset datasetUca =
         * CSAppCall.call("CS.GetInfosSVC.getUCAInfos", param); IData userInfo =
         * datasetUca.getData(0).getData("USER_INFO"); IData custInfo = datasetUca.getData(0).getData("CUST_INFO");
         * custInfo.put("BIRTHDAY", custInfo.getString("BIRTHDAY").substring(0, 10)); IData acctInfo =
         * datasetUca.getData(0).getData("ACCT_INFO"); // 二次开户登录员业务区必须和号码归属业务一致 checkReOpenCityCode(userInfo); //
         * 二次开户中获取除三户外的其他信息 IData otherInfo = loadOtherInfo(userInfo); returnData.putAll(userInfo);
         * returnData.putAll(custInfo); returnData.putAll(acctInfo); returnData.putAll(otherInfo); return returnData; }
         */

        // checkResourceForMphone校验需要用到的数据
        /*
         * if("1".equals(data.getString("INFO_TAG"))) OCCUPY_TYPE_CODE="1";
         */
        data.put("CHECK_DEPART_ID", returnData.getString("CHECK_DEPART_ID"));
        data.put("B_REOPEN_TAG", returnData.getString("B_REOPEN_TAG"));
        IDataset checkMphoneDatas = new DatasetList();
        if (PersonConst.IOT_OPEN.equals(data.getString("OPEN_TYPE", "")) || PersonConst.TD_OPEN.equals(data.getString("OPEN_TYPE", "")))
        {
            checkMphoneDatas = ResCall.checkResourceForIOTMphone("0", "0", serialNumber, "0", returnData.getString("CHECK_DEPART_ID"));

        }else if("8".equals(data.getString("OPEN_TYPE", ""))) {

        }
        else
        {
            /*
             * 老代码有处理"1" 网上选号开户 "0" 普通 INFO_TAG if("true".equals(td.getString("AUTH_FOR_SALE_ACTIVE_TAG", "false"))){
             * inData.put("PARA_VALUE8", "SALE_ACTIVE_TAG");//是否为签约赠送188号码用户开户 }
             */
            checkMphoneDatas = ResCall.checkResourceForMphone(OCCUPY_TYPE_CODE, serialNumber, "0", returnData.getString("CHECK_DEPART_ID"), data.getString("INFO_TAG"));

        }

        returnData.put("CHECK_RESULT_CODE", "0"); // 服务号码校验成功！
//        IData checkMphoneData = checkMphoneDatas.getData(0);
        IData checkMphoneData = IDataUtil.isEmpty(checkMphoneDatas)? new DataMap():checkMphoneDatas.getData(0);
        returnData.put("FEE_CODE_FEE", "0".equals(checkMphoneData.getString("RESERVE_FEE", "0")) ? "" : checkMphoneData.getString("RESERVE_FEE", "0")); // 选号费
        // 作为营业费
        // ,资源字段改变
//        if("8".equals(data.getString("OPEN_TYPE", ""))) {
//            IDataset simInfos = ResCall.selOneOccupyESim(serialNumber, getVisit(), "10");
//            System.out.println("--CreatePersonUserSVC.java--simInfos="+simInfos);
//            if(!simInfos.isEmpty()&&StringUtils.isNotEmpty(simInfos.getData(0).getString("ICC_ID"))){
//                checkMphoneData.put("SIM_CARD_NO",simInfos.getData(0).getString("ICC_ID"));
//				returnData.put("IMSI", simInfos.getData(0).getString("IMSI", ""));
//                System.out.println("--CreatePersonUserSVC.java--checkMphoneData="+checkMphoneData);
//            }
//        }
        String strSimCardNo = checkMphoneData.getString("SIM_CARD_NO", ""); // SIM卡
//        returnData.put("SIM_CARD_NO", strSimCardNo);
        String strPreOpenTag = checkMphoneData.getString("PREOPEN_TAG", "0"); // 预开
        String strPreCodeTag = checkMphoneData.getString("PRECODE_TAG", "0"); // 预配
        String strResKindCode = checkMphoneData.getString("RES_KIND_CODE", ""); // 用于处理手机号码小类判断
        
        if("1".equals(numberLimitFlag)){
        	String beautifulTag = checkMphoneData.getString("BEAUTIFUL_TAG", ""); // 是否为吉祥号
        	String allowBeauty = data.getString("ALLOW_BEAUTY", ""); // 是否允许吉祥号开户
        	//如果资源返回的靓号标识是靓号且配置不允许靓号开户就报错
        	if("1".equals(beautifulTag)&&"0".equals(allowBeauty)){
        		CSAppException.apperr(BizException.CRM_BIZ_5, PersonConst.BEAUTY_NUM_NOT_ALLOW_OPEN);//吉祥号码不允许开户！
        	}
        	
        }
        
        //添加网厅选号权限@tanzheng
        String strPoolCode = checkMphoneData.getString("POOL_CODE", ""); //
        if ("7".equals(strPoolCode)&&!StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "NETSEL_SERIALNUMBER")){
        	CSAppException.apperr(BizException.CRM_BIZ_5, "该号码属于网厅选号开户号码，您不具备操作权限。");
        }
        //添加电渠合作渠道选号权限
        if ("10".equals(strPoolCode)&&!StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "SN_POOL10_PRI")){
        	CSAppException.apperr(BizException.CRM_BIZ_5, "该号码属于电渠合作渠道选号开户号码，您不具备操作权限。");
        }
        //添加吉祥号码选号权限
        if ("11".equals(strPoolCode)&&!StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "SN_POOL11_PRI")){
        	CSAppException.apperr(BizException.CRM_BIZ_5, "该号码属于吉祥号码选号开户号码，您不具备操作权限。");
        }
        //添加线上销售全球通权益客户选号权限
        if ("13".equals(strPoolCode)&&!StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "SN_POOL13_PRI")){
        	CSAppException.apperr(BizException.CRM_BIZ_5, "该号码属于线上销售全球通权益客户选号开户号码，您不具备操作权限。");
        }
        //BUG20180604160336  自助号码池号码开户业务区问题
        String mphoneCityCode = checkMphoneData.getString("CITY_CODE","");
        String staffCityCode = CSBizBean.getVisit().getCityCode();
        //如果是自助选号池的号码，不可跨业务区办理：例如：在海口选号，不可在三亚办理开户
        if("3".equals(strPoolCode) && !StringUtils.equals(mphoneCityCode, staffCityCode)){
        	CSAppException.apperr(BizException.CRM_BIZ_5, "该号码属于营业厅自助选号专用池号码，不能跨业务区办理。号码归属业务区【"+mphoneCityCode+"】，你当前工号归属业务区【"+staffCityCode+"】！");
        }

        //add by sunxin
        if("0S".equals(strResKindCode))
        	 CSAppException.apperr(BizException.CRM_BIZ_5, "普通个人开户界面不允许开通二代TD无线座机号！请到相关页面开户！");
        returnData.put("RES_KIND_CODE_SN", strResKindCode);// 用于处理手机号码小类判断
        // 资源修改后重新组织数据
        String saleProduct = checkMphoneData.getString("PRODUCT_ID", "");
        String salePackage = checkMphoneData.getString("PACKAGE_ID", "");
        String bindDiscnt = checkMphoneData.getString("ELEMENT_ID", "");
        String bindMonth = checkMphoneData.getString("DEPOSIT_MONTH", "");
        String saleString = "";
        String bindDiscntString = "";

        if (StringUtils.isNotBlank(saleProduct) && StringUtils.isNotBlank(salePackage))
            saleString = saleProduct + "|" + salePackage;
        if (StringUtils.isNotBlank(bindDiscnt) && StringUtils.isNotBlank(bindMonth))
            bindDiscntString = bindDiscnt + "|" + bindMonth;
        returnData.put("A_X_CODING_STR", saleString); // add by wenhj2012.12.18 REQ201211200008 69900703|60007243
        returnData.put("X_BIND_DEFAULT_DISCNT", bindDiscntString); // "优惠编码|绑定月份"
        /**
         * ******************选号、密码卡、交预存送预存、号码绑定产品，优惠、预配预开等处理开始**************************************** ceshi sunxin //
         * 获取号费 String strModeCode = checkMphoneData.getString("RSRV_DATE1", "");// 费号类型 String strItemCodeMp =
         * checkMphoneData.getString("RSRV_DATE2", "");// 费号子类型 String strCodeFee =
         * checkMphoneData.getString("RSRV_DATE3", "");// 费号金额 // 获取号码类型 CODE_TYPE_CODE 0－普通 A－神州行 String
         * strCodeTypeCode = checkMphoneData.getString("CODE_TYPE_CODE", ""); // 品牌集 String strBrandCodeSet =getESIMSerialNumber
         * checkMphoneData.getString("RSRV_STR4", ""); String strGetCodeMode =
         * checkMphoneData.getString("OCCUPY_GET_CODE", ""); // 获取号码获取方式 OCCUPY_GET_CODE 0－普通号码 // 1－网上选号 2－大屏幕选号 String
         * strNetUsrpid = checkMphoneData.getString("PSPT_ID", ""); // 密码，用于网上选号时与客户证件比对值 if
         * ("1".equals(strGetCodeMode)) { returnData.put("CHOICE_GetCodeMode", "2"); returnData.put("CHOICE_NetUsrpid",
         * strNetUsrpid); } String tieTBusyTag = checkMphoneData.getString("RSRV_STR3", ""); //铁通无线座机 String
         * strSimCardNo = checkMphoneData.getString("SIM_CARD_NO", ""); // SIM卡 String strPreOpenTag =
         * checkMphoneData.getString("PREOPEN_TAG", "0"); // 预开 String strPreCodeTag =
         * checkMphoneData.getString("PRECODE_TAG", "0"); // 预配 returnData.put("TIETBUSY_TAG", tieTBusyTag);
         */
        /***********************************************************************
         * 选号费处理 开始(188号码特殊处理) ****************************************************** ceshi sunxin if
         * (!serialNumber.startsWith("188") && "false".equals(returnData.getString("AUTH_FOR_SALE_ACTIVE_TAG",
         * "false"))) { if (!"".equals(strModeCode) && !"".equals(strItemCodeMp) && !"".equals(strCodeFee))// 如果有选号费 {
         * if (Integer.parseInt(strCodeFee) > 0) { returnData.put("FEE_MODE", strModeCode);
         * returnData.put("FEE_TYPE_CODE", strItemCodeMp); returnData.put("FEE", strCodeFee); } } }
         */

        /**
         * ************************************* 选号费处理 结束 ******************************************************
         */
        /**
         * ********************************************************188号码处理
         * 开始*************************************************************
         */
        /*
         * if (serialNumber.startsWith("188") && "false".equals(returnData.getString("AUTH_FOR_SALE_ACTIVE_TAG",
         * "false"))) { // 188网上竞拍,从TF_SA_ACTION_CODE表获取预存 IData inparam = new DataMap(); IDataset compara =
         * CommparaInfoQry.getCommparaByAction(serialNumber); if (IDataUtil.isNotEmpty(compara)) {
         * returnData.put("FEE_MODE", "2"); returnData.put("FEE_TYPE_CODE", "0"); returnData.put("FEE",
         * compara.getData(0).getString("PARA_CODE21", "0")); //
         * “188靓号抢鲜”活动要求保证金大于或等于1188元的号码，须登记且连续使用月租费或月最低消费额在80元以上的全球通套餐满12个月！ if
         * (compara.getData(0).getInt("PARA_CODE20") >= 1188 && compara.getData(0).getInt("PARA_CODE12") == 1) {
         * returnData.put("EXISTS_BIND_BRAND", "G001");// 只能为全球通品牌 returnData.put("EXISTS_MULTE_PRODUCT",
         * "!10000000,!10001311");// 全球通的产品中不能为这两个产品 } } else {// 188靓号预存，从td_s_commpara表获取 ceshi sunxin /* if
         * ("".equals(strModeCode) && "-1".equals(strItemCodeMp) && "0".equals(strCodeFee))// 如果没有选号费 { inparam.clear();
         * inparam.put("SUBSYS_CODE", "CSM"); inparam.put("PARAM_ATTR", "1888"); inparam.put("PARAM_CODE", "188");
         * inparam.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode()); returnData.put("FEE_MODE", "2");
         * returnData.put("FEE_TYPE_CODE", "0"); returnData.put("FEE", compara.getData(0).getString("PARA_CODE2", "0")+
         * "00"); } //188吉祥号预存，从号码空闲表的费号规则获取 else if (!"".equals(strModeCode) && !"".equals(strItemCodeMp) &&
         * !"".equals(strCodeFee)) { if (Integer.parseInt(strCodeFee) > 0) { returnData.put("FEE_MODE", strModeCode);
         * returnData.put("FEE_TYPE_CODE", strItemCodeMp); returnData.put("FEE", strCodeFee); } } } }
         */
        /**
         * ********************************************************188号码处理
         * 结束*************************************************************
         */

        /**
         * ************************************* 产品集合处理 开始 ******************************************************
         */

        String strDefaultTagProductType = getProductTypeByFilter(0);
        returnData.put("EXISTS_DEFAULT_TAG", strDefaultTagProductType);
        // 根据号段或者号码类型获取默认产品(ng里还有号码规则产品表？)
        String strCodeTypeCode = "";// ceshi sunxin
        IDataset defaultData = ProductInfoQry.getDefaultProductByPhone(serialNumber, strCodeTypeCode, CSBizBean.getUserEparchyCode());
        if (IDataUtil.isEmpty(defaultData))
        {
            defaultData = ProductInfoQry.getDefaultProductByResType(serialNumber, strCodeTypeCode, CSBizBean.getUserEparchyCode());
        }
        if (IDataUtil.isNotEmpty(defaultData))
        {
            IData proSetData = defaultData.getData(0);
            if (proSetData.size() > 0)
            {
                String strForceTag = proSetData.getString("FORCE_TAG");
                int iProductId = proSetData.getInt("PRODUCT_ID");
                if (iProductId != -1)
                {// 有具体产品ID
                    if ("1".equals(strForceTag))
                    {// 强制该产品
                        returnData.put("EXISTS_SINGLE_PRODUCT", "" + iProductId);// 强制使用该产品，在产品信息页面检查是否存在绑定产品
                    }
                    else
                    {
                    }
                }
            }
        }
        /**
         * ************************************* 产品集合处理 结束 ******************************************************
         */
        // 有关费用的处理，需要仔细处理下 ceshi sunxin
        /**
         * ************************************* Sim卡处理 开始 ******************************************************
         */

        if (!"".equals(strSimCardNo) && ("1".equals(strPreOpenTag) || "1".equals(strPreCodeTag)))
        {
            returnData.put("PRE_CODE_TAG", "1"); // 设置标记位，如果是预配则不显示读卡和写卡按钮
            returnData.put("SIM_CARD_NO", strSimCardNo);
            IDataset checkSimDatas = new DatasetList();
            if (PersonConst.IOT_OPEN.equals(data.getString("OPEN_TYPE", "")))
            {
                checkSimDatas = ResCall.checkResourceForIOTSim("0", "0", serialNumber, strSimCardNo, "1", data.getString("CHECK_DEPART_ID"), "", "0");
            }

            else
            {
                checkSimDatas = ResCall.checkResourceForSim("0", serialNumber, strSimCardNo, "", returnData.getString("CHECK_DEPART_ID"));
            }
            IData checkSimData = checkSimDatas.getData(0);
            returnData.put("FEE_TAG", false);
            if ("0".equals(checkSimData.getString("FEE_TAG", "")) || "2".equals(checkSimData.getString("FEE_TAG", "")))
                returnData.put("FEE_TAG", true);

            returnData.put("SIM_FEE_TAG", checkSimData.getString("FEE_TAG", ""));
            String rsrvStr = checkSimData.getString("RSRV_STR3");
            if ("1".equals(rsrvStr))
                returnData.put("SIM_FEE_TAG", "1");
            // add by wenhj HNYD-REQ-20110402-010
            returnData.put("SIM_CARD_SALE_MONEY", "" + checkSimData.getString("SALE_MONEY", "0")); // add by
            // wenhjHNYD-REQ-20110402-010
            returnData.put("CHECK_RESULT_CODE", "1");// SIM校验成功，且服务号码成功！
            returnData.put("RES_KIND_CODE", checkSimData.getString("RES_KIND_CODE", ""));// 卡类型名称
            returnData.put("RES_KIND_NAME", checkSimData.getString("RES_KIND_NAME", ""));// 卡类型编码
            returnData.put("IMSI", checkSimData.getString("IMSI", ""));
            returnData.put("KI", checkSimData.getString("KI", ""));
            returnData.put("CAPACITY_TYPE_CODE", checkSimData.getString("NET_TYPE_CODE", "1"));
            returnData.put("CARD_KIND_CODE", checkSimData.getString("CARD_KIND_CODE", ""));
            returnData.put("EMPTY_CARD_ID", checkSimData.getString("EMPTY_CARD_ID", ""));
            returnData.put("RES_TYPE_CODE", checkSimData.getString("RES_TYPE_CODE", ""));
            returnData.put("CARD_PASSWD", checkSimData.getString("PASSWORD", ""));// 密码密文 ceshi
            returnData.put("PASSCODE", checkSimData.getString("KIND", ""));// 密码加密因子 ceshi
            String strSimTypeCode = checkSimData.getString("RES_TYPE_CODE", "0").substring(1);// 对应老系统的simtypecode
            String strNewAgentSaleTag = checkSimData.getString("RSRV_STR3", "");// //该白卡是否为代理商空卡买断。
            String strResTypeCode = checkSimData.getString("RES_TYPE_CODE", "0"); // 如果是写卡写的白卡则将白卡的类型放进rsrv_str8
            String resKindCode = checkSimData.getString("RES_KIND_CODE", "");
            if (!"".equals(checkSimData.getString("EMPTY_CARD_ID", "")) && !"U".equals(strSimTypeCode) && !"X".equals(strSimTypeCode))
            {
                // 如果SIM卡表中EMPTY_CARD_ID字段不为空，标明该卡由白卡写成，到白卡表中取卡类型 IData
                IData newEmptyCardInfo = ResCall.getEmptycardInfo(checkSimData.getString("EMPTY_CARD_ID"), "", "").getData(0);// 资源接口
                // sunxin
                String newSimCardType = newEmptyCardInfo.getString("RES_TYPE_CODE", "0").substring(1);
                String newCapacityTypeCode = newEmptyCardInfo.getString("CAPACITY_TYPE_CODE");
                String str1 = "";// StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_S_RESKIND",new
                // java.lang.String[]{"RES_TYPE_CODE","RES_KIND_CODE"},"KIND_NAME",newjava.lang.String[]{"6",newSimCardType});
                String str2 = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_SIMCAPACITY", "CAPACITY_TYPE_CODE", "CAPACITY_TYPE", newCapacityTypeCode);
                String str = str1 + str2;
                returnData.put("MAIN_RSRV_STR8", str);// 预留字段用于登记主台帐:MAIN_XXX
                // 为获取设备价格准备白卡属性参数
                strResTypeCode = newEmptyCardInfo.getString("RES_TYPE_CODE", "0");
                resKindCode = newEmptyCardInfo.getString("RES_KIND_CODE", "");
                strSimTypeCode = newSimCardType;
                strNewAgentSaleTag = newEmptyCardInfo.getString("RSRV_STR2", "");// //该白卡是否为代理商空卡买断。
                returnData.put("RES_KIND_CODE", newEmptyCardInfo.getString("RES_KIND_CODE", ""));//
                returnData.put("RES_TYPE_CODE", newEmptyCardInfo.getString("RES_TYPE_CODE", ""));// 卡类型
                returnData.put("MAIN_RSRV_STR8", newEmptyCardInfo.getString("RSRV_STR4", "") + " " + newEmptyCardInfo.getString("RSRV_STR1", ""));// 预留字段用于登记主台帐:MAIN_XXX
                // }
                String rsrvTag = newEmptyCardInfo.getString("RSRV_TAG1");
                if ("3".equals(rsrvTag))
                returnData.put("SIM_FEE_TAG", "1");
            }
            else
            {

                returnData.put("MAIN_RSRV_STR8", checkSimData.getString("RSRV_STR4", "") + " " + checkSimData.getString("RSRV_STR1", ""));// 预留字段用于登记主台帐:MAIN_XXX
                // }
            }
            returnData.put("NEW_AGENT_SALE_TAG", strNewAgentSaleTag); // xiekl物联网网修改 物联网 机器卡类型1001 1002需要写入OPC值
            // 是否为USIM卡,3G,将OPC记录在attr表 只要有opc值，则记录
            String uSimOpc = checkSimData.getString("OPC", "");
            if (!StringUtils.isBlank(uSimOpc))
            {
                returnData.put("OPC_CODE", "OPC_VALUE");
                returnData.put("OPC_VALUE", uSimOpc);
            }
            // add byzhangxiaobao for lte begin 先从sim卡里取RES_TYPE_CODE去掉第一位
            // 4g的晚点处理 sunxin
            IDataset uimInfo =ResCall.qrySimCardTypeByTypeCode(strResTypeCode);//ResParaInfoQry.checkUser4GUsimCard(strSimTypeCode);
            returnData.put("FLAG_4G", "");
            if ("01".equals(uimInfo.getData(0).getString("NET_TYPE_CODE")) && null != uSimOpc && !"".equals(uSimOpc))
            {
                returnData.put("FLAG_4G", "1");

            }

            returnData.put("EMPTY_CARD_ID", checkSimData.getString("EMPTY_CARD_ID", "")); // add by zhangxiaobaofor lte

            //物联网调资源接口返回的sim卡2、3、4G标识
            if (PersonConst.IOT_OPEN.equals(data.getString("OPEN_TYPE", "")))
            {
            	returnData.put("RSRV_STR5", checkSimData.getString("RSRV_STR5", ""));
            }

            // end
            String strProductId = "-1";
            String strClassId = "Z";
            // 获取卡费 sunxin
            IData feeData = DevicePriceQry.getDevicePrice(BizRoute.getRouteId(), strProductId, "10", resKindCode, strResTypeCode);
            if (IDataUtil.isNotEmpty(feeData))
            {
                returnData.put("FEE_MODE", "0");
                returnData.put("FEE_TYPE_CODE", feeData.getString("FEEITEM_CODE"));
                returnData.put("FEE", feeData.getString("DEVICE_PRICE"));
            }
            returnData.put("CHECK_RESULT_CODE", "1");// SIM校验成功，且服务号码成功！
        }


        /**
         * ************************************* Sim卡处理 结束 ******************************************************
         */

        /**
         * *********************选号、密码卡、交预存送预存、号码绑定产品，优惠、预配预开等处理 结束**************************************
         */

        IDataset acctInfoDays = DiversifyAcctUtil.queryAcctInfoDay();
        returnData.put("ACCT_INFO_DAYS", acctInfoDays);
        IDataset noteTypes = StaticInfoQry.getStaticListByTypeIdEparchy(BizRoute.getRouteId(), "NOTE_TYPE_", null);
        returnData.put("NOTE_TYPE_LIST", noteTypes);
        IDataset cityCodes = StaticUtil.getList(CSBizBean.getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", "PARENT_AREA_CODE", BizRoute.getRouteId());
        returnData.put("CITY_CODE_LIST", cityCodes);
        returnData.put("OPEN_TYPE_CODE", data.getString("OPEN_TYPE", ""));// 为了后面处理，不能传递OPEN_TYPE过去
        returnData.put("INFO_TAG", data.getString("INFO_TAG"));
        // 新增,产品组件需要传入
        returnData.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        returnData.put("SERIAL_NUMBER", serialNumber);
        // returnData.put("OPEN_TYPE", data.getString("OPEN_TYPE", ""));
        // returnData.put("CHECK_RESULT_CODE", "0"); // 服务号码校验成功！
        // returnData.put("B_REOPEN_TAG", "0"); // 测试 先用 sunxin
        return returnData;

    }

    /**
     * 输入SIM卡后的校验，获取卡信息
     *
     * @author chenzm
     * @param input
     * @throws Exception
     */
    public IDataset checkSimCardNo(IData input) throws Exception
    {
        String pre_sale = input.getString("PRE_SALE");
        IData returnData = new DataMap();
        if("Y".equals(pre_sale)) {
            returnData = this.checkSimResourcePreSale(input);
        } else {
            returnData = this.checkSimResource(input);
        }
        IDataset dataset = new DatasetList();
        dataset.add(returnData);
        return dataset;
    }

    /**
     * 资源sim卡校验
     *
     * @author chenzm
     * @param data
     * @throws Exception
     */
    public IData checkSimResource(IData data) throws Exception
    {
        IData returnData = new DataMap();
        String serialNumber = data.getString("SERIAL_NUMBER", "");
        String simCardNo = data.getString("SIM_CARD_NO", "");
        String oldSimCardNo = data.getString("OLD_SIM_CARD_NO", "");
        IDataset checkSimDatas = new DatasetList();
        // 非第一次时并新老资源不一样时，先释放上一次资源 add by yinhq 2009-09-25
        if (!StringUtils.isBlank(oldSimCardNo) && !oldSimCardNo.equals(simCardNo))
        {
            /*
             * IData resOccupyData = IResOccupyRelease(pd,oldSimCardNo,"1","ReleaseResTempoccupySingle");
             * redirectToMsgForCallSvc(resOccupyData);
             */// 同手机一样，需要资源提供接口 sunxin
        }
        // 产品限制SIM卡类型，EXISTS_SINGLE_PRODUCT依赖于号段绑定产品 没有找到对应值，不知道是否还需要 sunxin
        /*
         * String forceProduct = pd.getParameter("EXISTS_SINGLE_PRODUCT", ""); if(!"".equals(forceProduct)){
         * checkSimForProduct(pd, forceProduct, simCardNo); }
         */
        // SIM卡校验
        // 代理商开户时，检查有没有选择代理商
        String agentDepartId = data.getString("AGENT_DEPART_ID", "");
        if (PersonConst.AGENT_OPEN.equals(data.getString("OPEN_TYPE", "")))
        {
            if (StringUtils.isBlank(agentDepartId))
            {
                CSAppException.apperr(AgentsException.CRM_AGENTS_3);
            }
            IData depart = UDepartInfoQry.qryDepartByDepartId(agentDepartId);
            if (IDataUtil.isEmpty(depart))
                CSAppException.apperr(BizException.CRM_BIZ_5, "代理商编码【" + agentDepartId + "】不存在，请重新输入！");
            returnData.put("AGENT_DEPART_ID", agentDepartId); // 代理商部门编码，从开户代理商里取值PARA_CODE1
            // 处理代理商开户:代理商开户并且不以登录员工部门检查
            if (!"1".equals(data.getString("RES_CHECK_BY_DEPART")))
            {
                returnData.put("CHECK_DEPART_ID", agentDepartId); // 代理商部门编码，从开户代理商里取值PARA_CODE1
            }
            else
            {
                returnData.put("CHECK_DEPART_ID", getVisit().getDepartId());// 根据操作员部门校验
            }
        }
        else
        {
            returnData.put("CHECK_DEPART_ID", getVisit().getDepartId());// 根据操作员部门校验
        }

        if (PersonConst.IOT_OPEN.equals(data.getString("OPEN_TYPE", "")) || PersonConst.TD_OPEN.equals(data.getString("OPEN_TYPE", "")))
        {
            checkSimDatas = ResCall.checkResourceForIOTSim("0", "0", serialNumber, simCardNo, "1", returnData.getString("CHECK_DEPART_ID"), "", "0");
        }
        else if("8".equals(data.getString("OPEN_TYPE", ""))){
            checkSimDatas = ResCall.selOneOccupyESim(serialNumber, getVisit(), "10");
            returnData.put("SIM_CARD_NO",checkSimDatas.getData(0).getString("ICC_ID"));
        }
        else
        {
            checkSimDatas = ResCall.checkResourceForSim("0", serialNumber, simCardNo, "", returnData.getString("CHECK_DEPART_ID"));
        }

        IData checkSimData = checkSimDatas.getData(0);
        returnData.put("FEE_TAG", false);
        if ("0".equals(checkSimData.getString("FEE_TAG", "")) || "2".equals(checkSimData.getString("FEE_TAG", "")))
            returnData.put("FEE_TAG", true);

        returnData.put("SIM_FEE_TAG", checkSimData.getString("FEE_TAG", ""));
        String rsrvStr = checkSimData.getString("RSRV_STR3");
        if ("1".equals(rsrvStr))
            returnData.put("SIM_FEE_TAG", "1");
        // add by wenhj HNYD-REQ-20110402-010
        returnData.put("SIM_CARD_SALE_MONEY", "" + checkSimData.getString("SALE_MONEY", "0")); // add by
        // wenhjHNYD-REQ-20110402-010
        returnData.put("CHECK_RESULT_CODE", "1");// SIM校验成功，且服务号码成功！
        returnData.put("RES_KIND_CODE", checkSimData.getString("RES_KIND_CODE", ""));// 卡类型名称
        returnData.put("RES_KIND_NAME", checkSimData.getString("RES_KIND_NAME", ""));// 卡类型编码
        returnData.put("IMSI", checkSimData.getString("IMSI", ""));
        returnData.put("KI", checkSimData.getString("KI", ""));
        returnData.put("CAPACITY_TYPE_CODE", checkSimData.getString("NET_TYPE_CODE", "1"));
        returnData.put("CARD_KIND_CODE", checkSimData.getString("CARD_KIND_CODE", ""));
        returnData.put("EMPTY_CARD_ID", checkSimData.getString("EMPTY_CARD_ID", ""));
        returnData.put("RES_TYPE_CODE", checkSimData.getString("RES_TYPE_CODE", ""));
        returnData.put("CARD_PASSWD", checkSimData.getString("PASSWORD", ""));// 密码密文 ceshi
        returnData.put("PASSCODE", checkSimData.getString("KIND", ""));// 密码加密因子 ceshi
        String strSimTypeCode = checkSimData.getString("RES_TYPE_CODE", "0").substring(1);// 对应老系统的simtypecode
        //String netTypeCode = checkSimData.getString("NET_TYPE_CODE", "");// 01为4g卡 sunxin
        String strNewAgentSaleTag = checkSimData.getString("RSRV_STR3", "");// //该白卡是否为代理商空卡买断。
        String strResTypeCode = checkSimData.getString("RES_TYPE_CODE", "0"); // 如果是写卡写的白卡则将白卡的类型放进rsrv_str8
        String resKindCode = checkSimData.getString("RES_KIND_CODE", "");
        // 下列if中代码应该可以去掉 sunxin
        if (!"".equals(checkSimData.getString("EMPTY_CARD_ID", "")) && !"U".equals(strSimTypeCode) && !"X".equals(strSimTypeCode))
        {
            // 如果SIM卡表中EMPTY_CARD_ID字段不为空，标明该卡由白卡写成，到白卡表中取卡类型 IData
        	IDataset emptyCardInfo = ResCall.getEmptycardInfo(checkSimData.getString("EMPTY_CARD_ID"), "", "");
        	if(IDataUtil.isEmpty(emptyCardInfo)){
        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "该SIM卡的白卡信息为空");
        	}
            IData newEmptyCardInfo = emptyCardInfo.getData(0);// 资源接口
            // sunxin
            strSimTypeCode = newEmptyCardInfo.getString("RES_TYPE_CODE", "0").substring(1);
            String newCapacityTypeCode = newEmptyCardInfo.getString("CAPACITY_TYPE_CODE");
            String str1 = "";// StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_S_RESKIND",new
            // java.lang.String[]{"RES_TYPE_CODE","RES_KIND_CODE"},"KIND_NAME",newjava.lang.String[]{"6",newSimCardType});
            String str2 = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_SIMCAPACITY", "CAPACITY_TYPE_CODE", "CAPACITY_TYPE", newCapacityTypeCode);
            String str = str1 + str2;
            returnData.put("MAIN_RSRV_STR8", str);// 预留字段用于登记主台帐:MAIN_XXX
            // 为获取设备价格准备白卡属性参数
            strResTypeCode = newEmptyCardInfo.getString("RES_TYPE_CODE", "0");
            resKindCode = newEmptyCardInfo.getString("RES_KIND_CODE", "");
            strNewAgentSaleTag = newEmptyCardInfo.getString("RSRV_STR2", "");// //该白卡是否为代理商空卡买断。
            returnData.put("RES_KIND_CODE", newEmptyCardInfo.getString("RES_KIND_CODE", ""));//
            returnData.put("RES_TYPE_CODE", newEmptyCardInfo.getString("RES_TYPE_CODE", ""));// 卡类型
            returnData.put("MAIN_RSRV_STR8", newEmptyCardInfo.getString("RSRV_STR4", "") + " " + newEmptyCardInfo.getString("RSRV_STR1", ""));// 预留字段用于登记主台帐:MAIN_XXX
            // }
            String rsrvTag = newEmptyCardInfo.getString("RSRV_TAG1");
            if ("3".equals(rsrvTag))
            returnData.put("SIM_FEE_TAG", "1");
        }
        else
        {

            returnData.put("MAIN_RSRV_STR8", checkSimData.getString("RSRV_STR4", "") + " " + checkSimData.getString("RSRV_STR1", ""));// 预留字段用于登记主台帐:MAIN_XXX
            // }
        }
        returnData.put("NEW_AGENT_SALE_TAG", strNewAgentSaleTag); // xiekl物联网网修改 物联网 机器卡类型1001 1002需要写入OPC值
        // 是否为USIM卡,3G,将OPC记录在attr表
        String uSimOpc = checkSimData.getString("OPC", "");
        if (!StringUtils.isBlank(uSimOpc))
        {
            returnData.put("OPC_CODE", "OPC_VALUE");
            returnData.put("OPC_VALUE", uSimOpc);
        }
        // add byzhangxiaobao for lte begin
        // 4g的晚点处理 sunxin
        IDataset uimInfo =ResCall.qrySimCardTypeByTypeCode(strResTypeCode);
        returnData.put("FLAG_4G", "");
        if (IDataUtil.isNotEmpty(uimInfo) && IDataUtil.isNotEmpty(uimInfo.getData(0)) && "01".equals(uimInfo.getData(0).getString("NET_TYPE_CODE")) && null != uSimOpc && !"".equals(uSimOpc))
        {
            returnData.put("FLAG_4G", "1");
        }

        returnData.put("EMPTY_CARD_ID", checkSimData.getString("EMPTY_CARD_ID", "")); // add by zhangxiaobaofor lte end
        // */
        String strProductId = "-1";
        String strClassId = "Z";
        // 获取卡费 sunxin
        IData feeData = DevicePriceQry.getDevicePrice(BizRoute.getRouteId(), strProductId, "10", resKindCode, strResTypeCode);
        if (IDataUtil.isNotEmpty(feeData))
        {
            returnData.put("FEE_MODE", "0");
            returnData.put("FEE_TYPE_CODE", feeData.getString("FEEITEM_CODE"));
            returnData.put("FEE", feeData.getString("DEVICE_PRICE"));
        }
        /*
         * ceshi returnData.put("CHECK_RESULT_CODE", "1");//SIM校验成功，且服务号码成功！ returnData.put("IMSI", "11111111");
         * returnData.put("KI", "11111111");
         */
        //物联网调资源接口返回的sim卡2、3、4G标识
        if (PersonConst.IOT_OPEN.equals(data.getString("OPEN_TYPE", "")))
        {
        	returnData.put("RSRV_STR5", checkSimData.getString("RSRV_STR5", ""));
        }
        return returnData;

    }

    /**
     * 资源sim卡校验
     *
     * @author chenzm
     * @param data
     * @throws Exception
     */
    public IData checkSimResourcePreSale(IData data) throws Exception
    {
        IData returnData = new DataMap();
        String serialNumber = data.getString("SERIAL_NUMBER", "");
        String simCardNo = data.getString("SIM_CARD_NO", "");
        String oldSimCardNo = data.getString("OLD_SIM_CARD_NO", "");
        IDataset checkSimDatas = new DatasetList();
        // 非第一次时并新老资源不一样时，先释放上一次资源 add by yinhq 2009-09-25
        if (!StringUtils.isBlank(oldSimCardNo) && !oldSimCardNo.equals(simCardNo))
        {
        }
        // 产品限制SIM卡类型，EXISTS_SINGLE_PRODUCT依赖于号段绑定产品 没有找到对应值，不知道是否还需要 sunxin
        // SIM卡校验
        String agentDepartId = data.getString("AGENT_DEPART_ID", "");
        if (PersonConst.AGENT_OPEN.equals(data.getString("OPEN_TYPE", "")))
        {
            if (StringUtils.isBlank(agentDepartId))
            {
                CSAppException.apperr(AgentsException.CRM_AGENTS_3);
            }
            IData depart = UDepartInfoQry.qryDepartByDepartId(agentDepartId);
            if (IDataUtil.isEmpty(depart))
                CSAppException.apperr(BizException.CRM_BIZ_5, "代理商编码【" + agentDepartId + "】不存在，请重新输入！");
            returnData.put("AGENT_DEPART_ID", agentDepartId); // 代理商部门编码，从开户代理商里取值PARA_CODE1
            // 处理代理商开户:代理商开户并且不以登录员工部门检查
            if (!"1".equals(data.getString("RES_CHECK_BY_DEPART")))
            {
                returnData.put("CHECK_DEPART_ID", agentDepartId); // 代理商部门编码，从开户代理商里取值PARA_CODE1
            }
            else
            {
                returnData.put("CHECK_DEPART_ID", getVisit().getDepartId());// 根据操作员部门校验
            }
        }
        else
        {
            returnData.put("CHECK_DEPART_ID", getVisit().getDepartId());// 根据操作员部门校验
        }

        if (PersonConst.IOT_OPEN.equals(data.getString("OPEN_TYPE", "")) || PersonConst.TD_OPEN.equals(data.getString("OPEN_TYPE", "")))
        {
            checkSimDatas = ResCall.checkResourceForIOTSim("0", "0", serialNumber, simCardNo, "1", returnData.getString("CHECK_DEPART_ID"), "", "0");
        }
        else
        {
            checkSimDatas = ResCall.checkResourceForSimPreSale("0", serialNumber, simCardNo, "", returnData.getString("CHECK_DEPART_ID"));
        }

        IData checkSimData = checkSimDatas.getData(0);
        returnData.put("FEE_TAG", false);
        if ("0".equals(checkSimData.getString("FEE_TAG", "")) || "2".equals(checkSimData.getString("FEE_TAG", "")))
            returnData.put("FEE_TAG", true);

        returnData.put("SIM_FEE_TAG", checkSimData.getString("FEE_TAG", ""));
        String rsrvStr = checkSimData.getString("RSRV_STR3");
        if ("1".equals(rsrvStr))
            returnData.put("SIM_FEE_TAG", "1");
        // add by wenhj HNYD-REQ-20110402-010
        returnData.put("SIM_CARD_SALE_MONEY", "" + checkSimData.getString("SALE_MONEY", "0")); // add by
        // wenhjHNYD-REQ-20110402-010
        returnData.put("CHECK_RESULT_CODE", "1");// SIM校验成功，且服务号码成功！
        returnData.put("RES_KIND_CODE", checkSimData.getString("RES_KIND_CODE", ""));// 卡类型名称
        returnData.put("RES_KIND_NAME", checkSimData.getString("RES_KIND_NAME", ""));// 卡类型编码
        returnData.put("IMSI", checkSimData.getString("IMSI", ""));
        returnData.put("KI", checkSimData.getString("KI", ""));
        returnData.put("CAPACITY_TYPE_CODE", checkSimData.getString("NET_TYPE_CODE", "1"));
        returnData.put("CARD_KIND_CODE", checkSimData.getString("CARD_KIND_CODE", ""));
        returnData.put("EMPTY_CARD_ID", checkSimData.getString("EMPTY_CARD_ID", ""));
        returnData.put("RES_TYPE_CODE", checkSimData.getString("RES_TYPE_CODE", ""));
        returnData.put("CARD_PASSWD", checkSimData.getString("PASSWORD", ""));// 密码密文 ceshi
        returnData.put("PASSCODE", checkSimData.getString("KIND", ""));// 密码加密因子 ceshi
        String strSimTypeCode = checkSimData.getString("RES_TYPE_CODE", "0").substring(1);// 对应老系统的simtypecode
        //String netTypeCode = checkSimData.getString("NET_TYPE_CODE", "");// 01为4g卡 sunxin
        String strNewAgentSaleTag = checkSimData.getString("RSRV_STR3", "");// //该白卡是否为代理商空卡买断。
        String strResTypeCode = checkSimData.getString("RES_TYPE_CODE", "0"); // 如果是写卡写的白卡则将白卡的类型放进rsrv_str8
        String resKindCode = checkSimData.getString("RES_KIND_CODE", "");
        // 下列if中代码应该可以去掉 sunxin
        if (!"".equals(checkSimData.getString("EMPTY_CARD_ID", "")) && !"U".equals(strSimTypeCode) && !"X".equals(strSimTypeCode))
        {
            // 如果SIM卡表中EMPTY_CARD_ID字段不为空，标明该卡由白卡写成，到白卡表中取卡类型 IData
            IDataset emptyCardInfo = ResCall.getEmptycardInfo(checkSimData.getString("EMPTY_CARD_ID"), "", "");
            if(IDataUtil.isEmpty(emptyCardInfo)){
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "该SIM卡的白卡信息为空");
            }
            IData newEmptyCardInfo = emptyCardInfo.getData(0);// 资源接口
            // sunxin
            strSimTypeCode = newEmptyCardInfo.getString("RES_TYPE_CODE", "0").substring(1);
            String newCapacityTypeCode = newEmptyCardInfo.getString("CAPACITY_TYPE_CODE");
            String str1 = "";// StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_S_RESKIND",new
            // java.lang.String[]{"RES_TYPE_CODE","RES_KIND_CODE"},"KIND_NAME",newjava.lang.String[]{"6",newSimCardType});
            String str2 = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_SIMCAPACITY", "CAPACITY_TYPE_CODE", "CAPACITY_TYPE", newCapacityTypeCode);
            String str = str1 + str2;
            returnData.put("MAIN_RSRV_STR8", str);// 预留字段用于登记主台帐:MAIN_XXX
            // 为获取设备价格准备白卡属性参数
            strResTypeCode = newEmptyCardInfo.getString("RES_TYPE_CODE", "0");
            resKindCode = newEmptyCardInfo.getString("RES_KIND_CODE", "");
            strNewAgentSaleTag = newEmptyCardInfo.getString("RSRV_STR2", "");// //该白卡是否为代理商空卡买断。
            returnData.put("RES_KIND_CODE", newEmptyCardInfo.getString("RES_KIND_CODE", ""));//
            returnData.put("RES_TYPE_CODE", newEmptyCardInfo.getString("RES_TYPE_CODE", ""));// 卡类型
            returnData.put("MAIN_RSRV_STR8", newEmptyCardInfo.getString("RSRV_STR4", "") + " " + newEmptyCardInfo.getString("RSRV_STR1", ""));// 预留字段用于登记主台帐:MAIN_XXX
            // }
            String rsrvTag = newEmptyCardInfo.getString("RSRV_TAG1");
            if ("3".equals(rsrvTag))
                returnData.put("SIM_FEE_TAG", "1");
        }
        else
        {

            returnData.put("MAIN_RSRV_STR8", checkSimData.getString("RSRV_STR4", "") + " " + checkSimData.getString("RSRV_STR1", ""));// 预留字段用于登记主台帐:MAIN_XXX
            // }
        }
        returnData.put("NEW_AGENT_SALE_TAG", strNewAgentSaleTag); // xiekl物联网网修改 物联网 机器卡类型1001 1002需要写入OPC值
        // 是否为USIM卡,3G,将OPC记录在attr表
        String uSimOpc = checkSimData.getString("OPC", "");
        if (!StringUtils.isBlank(uSimOpc))
        {
            returnData.put("OPC_CODE", "OPC_VALUE");
            returnData.put("OPC_VALUE", uSimOpc);
        }
        // add byzhangxiaobao for lte begin
        // 4g的晚点处理 sunxin
        IDataset uimInfo =ResCall.qrySimCardTypeByTypeCode(strResTypeCode);
        returnData.put("FLAG_4G", "");
        if (IDataUtil.isNotEmpty(uimInfo) && IDataUtil.isNotEmpty(uimInfo.getData(0)) && "01".equals(uimInfo.getData(0).getString("NET_TYPE_CODE")) && null != uSimOpc && !"".equals(uSimOpc))
        {
            returnData.put("FLAG_4G", "1");
        }

        returnData.put("EMPTY_CARD_ID", checkSimData.getString("EMPTY_CARD_ID", "")); // add by zhangxiaobaofor lte end
        // */
        String strProductId = "-1";
        String strClassId = "Z";
        // 获取卡费 sunxin
        IData feeData = DevicePriceQry.getDevicePrice(BizRoute.getRouteId(), strProductId, "10", resKindCode, strResTypeCode);
        if (IDataUtil.isNotEmpty(feeData))
        {
            returnData.put("FEE_MODE", "0");
            returnData.put("FEE_TYPE_CODE", feeData.getString("FEEITEM_CODE"));
            returnData.put("FEE", feeData.getString("DEVICE_PRICE"));
        }
        /*
         * ceshi returnData.put("CHECK_RESULT_CODE", "1");//SIM校验成功，且服务号码成功！ returnData.put("IMSI", "11111111");
         * returnData.put("KI", "11111111");
         */
        //物联网调资源接口返回的sim卡2、3、4G标识
        if (PersonConst.IOT_OPEN.equals(data.getString("OPEN_TYPE", "")))
        {
            returnData.put("RSRV_STR5", checkSimData.getString("RSRV_STR5", ""));
        }
        return returnData;

    }

    /**
     * 输入新开户号码后的校验，获取开户信息
     *
     * @author chenzm
     * @param input
     * @throws Exception
     */
    public IDataset createProductInfo(IData input) throws Exception
    {
        IDataset returndatas = new DatasetList();
        IData data = this.createProductInfoForSvc(input);
        returndatas.add(data);
        return returndatas;
    }

    /**
     * 处理通常及特殊情况下产品信息
     *
     * @param pd
     * @param td
     * @return
     * @throws Exception
     */
    public IData createProductInfoForSvc(IData data) throws Exception
    {

        IData returnData = new DataMap();
        boolean specDealTag = false;// 不同处理标志
        String strBindSingleProduct = data.getString("EXISTS_SINGLE_PRODUCT", "");
        /*
         * if (data.getString("B_REOPEN_TAG", "").equals("1"))// 如果是二次开户，则肯定绑定的是单个产品，获取绑定的单个产品id { IData pData =
         * getMainProduct(data.getString("USER_ID")); if (IDataUtil.isNotEmpty(pData)) { strBindSingleProduct =
         * pData.getString("PRODUCT_ID"); } }
         */
        String strBindDefaultTag = data.getString("EXISTS_DEFAULT_TAG", "");
        String strBindBrand = data.getString("EXISTS_BIND_BRAND", "");// 考虑前面传过来的！条件(反向条件)
        String strBindMulteProduct = data.getString("EXISTS_MULTE_PRODUCT", "");// 绑定多个产品
        String strBindDiscntCode = data.getString("EXISTS_SINGLE_DISCNT", "");// 绑定优惠，针对密码卡，优惠存在时，单个产品绑定一定存在
        // strBindMulteProduct="71004500,71004501";
        // 绑定产品集时，显示产品目录，同时将产品集对应的结果集传给"产品目录"做为入参，只显示这些产品
        String strBindMulteProductBrandCode = "";// 多产品对应的brand_code
        IData multeProductData = null;
        /*
         * 暂时先不管，sunxin if (!StringUtils.isBlank(strBindMulteProduct)) { IDataset moreProductList =
         * getProductInfo(strBindMulteProduct, CSBizBean.getVisit().getTradeEparchyCode());
         * returnData.put("MORE_PRODUCT_LIST", moreProductList);// 给产品目录传值，只显示此列表 }
         */

        // 判断标志位:只要有一个不为空，作特殊处理
        if (!StringUtils.isBlank(strBindSingleProduct) || !StringUtils.isBlank(strBindDefaultTag))
        {
            specDealTag = true;// 单产品
        }
        IDataset productTypeList = null;
        // 产品类型:不存在绑定产品:无特殊处理
        if (!specDealTag)
        {
            productTypeList = ProductTypeInfoQry.getProductsType("0000", null);

        }
        else
        {
            if (!StringUtils.isBlank(strBindSingleProduct))
            {
                productTypeList = getProductTypeCodeByProductId(strBindSingleProduct, BizRoute.getRouteId());

                // 号码绑定单个产品时，不显示产品目录，直接将此产品下的必选包下的必选择默认元素显示
                if (IDataUtil.isNotEmpty(productTypeList))
                {
                    IData productInfo = UProductInfoQry.qryProductByPK(strBindSingleProduct);
                    /*
                     * 测试先屏蔽，sunxin 老代码有此处理 IDataset forceElements = getForcePackageElementsByProductId( pd, td,
                     * strBindSingleProduct, strBindDiscntCode); returnData.put("FORCE_ELEMENTS", forceElements);
                     */
                    returnData.put("PRODUCT_NAME", productInfo.getString("PRODUCT_NAME"));
                    returnData.put("PRODUCT_DESC", productInfo.getString("PRODUCT_EXPLAIN"));
                    returnData.put("PRODUCT_ID", strBindSingleProduct);
                    returnData.put("BRAND_CODE", productInfo.getString("BRAND_CODE"));
                }
            }
            else
            {
                productTypeList = getAllProductCatalog("0000");
                IData productTypeData = getParentChildProductTypeList(productTypeList);
                IDataset parentProductTypeList = productTypeData.getDataset("PARENT_PRODUCT_TYPE_LIST");
                String strParentTypeCode = "";
                // 绑定默认标记
                if (!StringUtils.isBlank(strBindDefaultTag))
                {
                    filterProductTypeListByDefaultTag(parentProductTypeList, strBindDefaultTag);
                }
                // 绑定 品牌
                if (!StringUtils.isBlank(strBindBrand))
                {
                    filterProductTypeListByBrandCode(parentProductTypeList, strBindBrand);
                }
                // 绑定 多个产品
                // if (!isNull(strBindMulteProduct)) {
                // filterProductTypeListByMutleProduct(parentProductTypeList,
                // strBindMulteProductBrandCode);
                // }
                productTypeList = parentProductTypeList;
            }
        }
        returnData.put("PRODUCT_TYPE_LIST", productTypeList);
        IDataset productTypeListNew = new DatasetList();
        IData productTypeData = new DataMap();
        String productTypeCode = "";
         // 非物联网开户，去物联网产品
        if (!PersonConst.IOT_OPEN.equals(data.getString("OPEN_TYPE_CODE", "")) && IDataUtil.isNotEmpty(productTypeList))
        {
            for (int i = 0; i < productTypeList.size(); i++)
            {
                productTypeData = productTypeList.getData(i);
                productTypeCode = productTypeData.getString("PRODUCT_TYPE_CODE");
                if (!"WLW0".equals(productTypeCode))
                {
                    productTypeListNew.add(productTypeData);
                }
            }
        }
        else if (PersonConst.IOT_OPEN.equals(data.getString("OPEN_TYPE_CODE", "")) && IDataUtil.isNotEmpty(productTypeList))
        {
            // 物联网开户
            for (int i = 0; i < productTypeList.size(); i++)
            {
                productTypeData = productTypeList.getData(i);
                productTypeCode = productTypeData.getString("PRODUCT_TYPE_CODE");
                if ("WLW0".equals(productTypeCode))
                {
                    productTypeListNew.add(productTypeData);
                    break;
                }
            }
        }
        returnData.put("PRODUCT_TYPE_LIST", productTypeListNew);
        // 147开户特殊处理 sunxin 测试环境没有数据
        if (data.getString("SERIAL_NUMBER", "").startsWith("147"))
        {
            IDataset productTypeListSpcNew = new DatasetList();
            IDataset map = new DatasetList();
            IDataset spcProduct = this.getProductForSpc(data);
            if (IDataUtil.isNotEmpty(spcProduct))
            {
                String productIdSpc = "";
                for (int i = 0; i < spcProduct.size(); i++)
                {
                    productIdSpc = spcProduct.getData(i).getString("PARA_CODE1");
                    IDataset productTypeListSpc = getProductTypeCodeByProductId(productIdSpc, BizRoute.getRouteId());
                    productTypeListSpcNew.add(productTypeListSpc.getData(0));

                }

                map = DataHelper.distinct(productTypeListSpcNew, "PRODUCT_TYPE_CODE", "");// IDataset.TYPE_DOUBLE
                returnData.put("PRODUCT_TYPE_LIST", map);
            }

        }
        // 如果是网上选号，将可以办理的产品类型保留
        if (data.getString("INFO_TAG", "").equals("1"))
        {
            IDataset productTypeListNetNew = new DatasetList();
            IDataset map = new DatasetList();
            IDataset netProduct = this.getProductForNet(data);
            if (IDataUtil.isNotEmpty(netProduct))
            {
                String productIdNet = "";
                for (int i = 0; i < netProduct.size(); i++)
                {
                    productIdNet = netProduct.getData(i).getString("PARA_CODE1");
                    IDataset productTypeListNet = getProductTypeCodeByProductId(productIdNet, BizRoute.getRouteId());
                    productTypeListNetNew.add(productTypeListNet.getData(0));

                }

                map = DataHelper.distinct(productTypeListNetNew, "PRODUCT_TYPE_CODE", "");// IDataset.TYPE_DOUBLE
            }
            if (IDataUtil.isNotEmpty(map))
                returnData.put("PRODUCT_TYPE_LIST", map);
        }
        return returnData;
    }

    /**
     * 获取全量结果：产品类型
     *
     * @param pd
     * @param inData
     * @return
     * @throws Exception
     */
    public IDataset getAllProductCatalog(String parent_ptype_code) throws Exception
    {

        return UProductInfoQry.getProductsType(parent_ptype_code, this.getPagination());
    }

    /**
     * 获取是否分库标志
     *
     * @author chenzm
     * @return int
     * @throws Exception
     */
    public int getDiffDataBase() throws Exception
    {
        int iResult = -1;

        IDataset dataset = CommparaInfoQry.getOnlyByAttr("CSM", "1013", CSBizBean.getUserEparchyCode());

        if (!IDataUtil.isEmpty(dataset))
        {
            IData resultData = dataset.getData(0);

            // 有异地业务，需在省中心记录台帐资料
            if ("1".equals(resultData.getString("PARAM_CODE")))
            {
                iResult = 0;
            }
            // 有异地业务，无需在省中心记录台帐资料
            else if ("2".equals(resultData.getString("PARAM_CODE")))
            {
                iResult = 1;
            }
            else
            {
                iResult = -1;
            }
        }

        return iResult;
    }

    /**
     * 获取用户绑定产品信息
     *
     * @author chenzm
     * @param strUserId
     * @throws Exception
     */
    public IData getMainProduct(String strUserId) throws Exception
    {
        IData data = UcaInfoQry.qryMainProdInfoByUserId(strUserId);
        return data;
    }

    /**
     * 获取号码归属
     *
     * @author chenzm
     * @param data
     * @param intDiffData
     * @throws Exception
     */
    public IData getMphoneAddress(IData data, int intDiffData) throws Exception
    {
        boolean bSameEparchyCode = true;
        boolean bDiffTrade = false;
        String strRouteEparchyCode = "";
        String tradeEparchyCode = CSBizBean.getTradeEparchyCode();
        // 根据号码获取路由的方法，需要公用出来，最好放专门的路由类，或放基类里，直接调用
        String strEparchyCode = RouteInfoQry.getEparchyCodeBySn(data.getString("SERIAL_NUMBER"));
        if (StringUtils.isBlank(strEparchyCode))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_475, data.getString("SERIAL_NUMBER"));
        }

        // 异地业务
        if (!strEparchyCode.equals(tradeEparchyCode))
        {
            bSameEparchyCode = false;
            strRouteEparchyCode = strEparchyCode;

            if (intDiffData == 0)
            {
                bDiffTrade = true; // 省中心记台帐
            }
            else
            {
                bDiffTrade = false; // 省中心不记台帐
            }
        }
        else
        {
            strRouteEparchyCode = tradeEparchyCode;
            bDiffTrade = false; // 非异地
        }

        IData temp = new DataMap();
        temp.put("B_SAME_EPARCHY_CODE", bSameEparchyCode ? "0" : "1"); // 0:同地市
        // ;1:不同地市
        temp.put("B_DIFF_TRADE", bDiffTrade ? "0" : "1"); // 是否登记省中心台帐:0:登记;1:不登记

        return temp;
    }

    /**
     * 获取产品费用
     *
     * @author sunxin
     * @param input
     * @throws Exception
     */
    public IDataset getProductFeeInfo(IData input) throws Exception
    {
        IDataset dataset = this.getProductFeeInfoForSvc(input);
        return dataset;
    }

    /**
     * 获取产品费用
     *
     * @author sunxin
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset qryComRelOffersByOfferCode(IData data) throws Exception
    {
        String element_id = data.getString("ELEMENT_ID");
        IDataset idsOfferInfos = UpcCall.qryComRelOffersByOfferId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, element_id);
   	    if(IDataUtil.isNotEmpty(idsOfferInfos))
   	    {
   	    	for(int i = 0; i < idsOfferInfos.size(); i++)
   	    	{
   	    		IData idOffer = idsOfferInfos.getData(i);
   	    		if(!("D".equals(idOffer.getString("OFFER_TYPE", ""))))
   	    		{
   	    			idsOfferInfos.remove(i);
   	    			i--;
   	    		}else
   	    		{
   	    			idOffer.put("ELEMENT_ID",idOffer.getString("OFFER_CODE"));
   	    			idOffer.put("MAIN_TAG", idOffer.getString("IS_MAIN_SVC"));
   	    			idOffer.put("ELEMENT_TYPE", idOffer.getString("OFFER_TYPE"));
   	    		}
   	    	}
   	    }
   	    return idsOfferInfos;
    }

    /**
     * 获取产品费用
     *
     * @author sunxin
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getProductFeeInfoForSvc(IData data) throws Exception
    {

        String product_id = data.getString("PRODUCT_ID");
        // String element_id = data.getString("ELEMENT_ID");
        String eparchy_code = CSBizBean.getUserEparchyCode();
        // 需要修改 将类型传递
//        IDataset dataset = ProductFeeInfoQry.getProductFeeInfo("10", product_id, "-1", "-1", "P", "3", eparchy_code);
        IDataset dataset = UpcCall.qryDynamicPrice(product_id, BofConst.ELEMENT_TYPE_CODE_PRODUCT, "-1", null, "10", null, null, null);
        if(IDataUtil.isNotEmpty(dataset))
        {
            for(Object obj : dataset)
            {
                IData feeInfo = (IData) obj;
                feeInfo.put("FEE_MODE", feeInfo.getString("FEE_TYPE"));
            }
        }
        /*
         * if (StringUtils.isNotBlank(element_id)) { IDataset datasetSvc = ProductFeeInfoQry.getProductFeeInfo("10",
         * product_id, "-1", element_id, "S", "3", eparchy_code); datasetSvc.removeAll(dataset);
         * dataset.addAll(datasetSvc); }
         */
        //REQ201911220019关于优化无线宽带号码办理的需求
        String limitCPETag = "0";
        IDataset commParaInfos = CommparaInfoQry.getCommparaAllCol("CSM", "9122","LIMIT_CPE_TAG", CSBizBean.getUserEparchyCode());
        if(IDataUtil.isNotEmpty(commParaInfos))
        {
        	limitCPETag=commParaInfos.getData(0).getString("PARA_CODE1", "0");
        }
        String brandCode = data.getString("BRAND_CODE","");
        String serialNumber = data.getString("SERIAL_NUMBER","");
        //System.out.println("------------------------zhangxing3---------------brandCode:"+brandCode+",serialNumber:"+serialNumber);
        if("1".equals(limitCPETag) && "CPE1".equals(brandCode) && !"".equals(serialNumber))
        {
            IDataset dataSet = ResCall.getMphonecodeInfo(serialNumber);
    		if (IDataUtil.isNotEmpty(dataSet))
    		{
    			String beautifulTag = dataSet.first().getString("BEAUTIFUAL_TAG");
    			//System.out.println("----------------------zhangxing3------------------BEAUTIFUAL_TAG:"+beautifulTag+",brandCode:"+brandCode);
    			if (StringUtils.equals("1", beautifulTag) && StringUtils.equals("CPE1", brandCode))
    			{
    				CSAppException.apperr(BizException.CRM_GRP_713,"吉祥号码不能用于CPE开户！");
    			}
    		}
        }
        //REQ201911220019关于优化无线宽带号码办理的需求
        return dataset;

    }

    /**
     * 获取网上选号产品
     *
     * @author sunxin
     * @param input
     * @throws Exception
     */
    public IDataset getProductForNet(IData input) throws Exception
    {
        IDataset returnData = ParamInfoQry.getCommparaByCode("CSM", "7639", "netchoose_phone_product", "0898");

        return returnData;
    }

    /**
     * 获取147号码开户产品
     *
     * @author sunxin
     * @param input
     * @throws Exception
     */
    public IDataset getProductForSpc(IData input) throws Exception
    {
        IDataset returnData = ParamInfoQry.getCommparaByCode("CSM", "2001", "G3CARD", CSBizBean.getTradeEparchyCode());

        return returnData;
    }

    /**
     * 获取产品类型 iMode 0-普通开户(神州行除外) 1-神州行开户 2-所有
     *
     * @author chenzm
     * @param iMode
     * @return
     * @throws Exception
     */
    public String getProductTypeByFilter(int iMode) throws Exception
    {
        String filterDefaultTag = "";

       /* if (iMode == 0 || iMode == 1)
        {*/
        if (iMode == 0)
        {
            filterDefaultTag = "1,2";// DEFAULT_TAG
        }
        else if (iMode == 1)
        {
            filterDefaultTag = "6";// DEFAULT_TAG
        }
        /*}*/

        return filterDefaultTag;
    }

    /**
     * 查询td_s_tag表参数
     *
     * @param strEparchyCode
     * @param tagCode
     * @param userTag
     * @return
     * @throws Exception
     */
    public IData getTagInfo(String strEparchyCode, String tagCode, String userTag) throws Exception
    {
        IDataset tagList = new DatasetList();
        tagList = TagInfoQry.getTagInfo(strEparchyCode, tagCode, userTag, null);
        return IDataUtil.isEmpty(tagList) ? new DataMap() : tagList.getData(0);
    }

    /**
     * 界面初始化方法
     *
     * @author chenzm
     * @param data
     * @return
     * @throws Exception
     */
    public IData InitPara(IData data) throws Exception
    {
        IData returnData = new DataMap();

        IData rDualInfo;
        String strTagInfo = "";
        String strTagChar = "";
        String strTagNumber = "";
        String strOpenType = data.getString("OPEN_TYPE", ""); // 是否代理商开户，是否物联网开户 通过地址栏参数
        String strEparchyCode = CSBizBean.getTradeEparchyCode();

        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_CANPREOPEN", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        // returnData.put("CAN_PRE_OPEN", strTagChar);// 标识页面"预约开户"checkbox可用否
        // 暂时不用没发现 sunxin

        // 获取是否允许合户
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_ISSAMEACCT", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        // returnData.put("IS_SAME_ACCT", strTagChar); 暂时不用没发现 sunxin

        // 获取黑名单提示方式标记
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_BLACKCHECKMODE", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        // returnData.put("BLACK_CHECK_MODE", strTagChar); 暂时不用没发现 sunxin

        // 获取开户是否打印票据(0:不打印，1:根据前台选择打印，默认不打印)
        boolean printEnabledTag = true;// 普通用户默认为打印，处理代理商开户打印标记
        rDualInfo = getTagInfo(strEparchyCode, "CS_ALL_CREATEUSERPRINT", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        strTagInfo = rDualInfo.getString("TAG_INFO", "");
        strTagNumber = rDualInfo.getString("TAG_NUMBER", "0");

        /*
         * if (strCurRightCode.equals(strTagInfo)) {// 控制不同地市是否需要打印，一般strTagInfo为菜单编码RIGHT_CODE if
         * ("1".equals(strTagChar)) {// 0:不打印，1:打印 printEnabledTag = "1".equals(strTagNumber) ? true : false; } else {
         * printEnabledTag = false; } } dualInfo.put("CHK_PRINT_ENABLE", printEnabledTag);// 业务登记时，根据此标记是否提示打印(是否显示打印按钮)
         * 先屏蔽 sunxin
         */

        // 获取特殊号段免卡费标记
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_SPECSNSECTNOSIMFEE", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        // returnData.put("SPEC_SN_SECTNO_SIM_FEE", strTagChar);
        // 测试环境下数据没有，暂时不用sunxin

        // 获取开户免卡费(不收卡费)的品牌信息
        rDualInfo = getTagInfo(strEparchyCode, "CS_INF_NOCARDFEEBRAND", "0");
        strTagInfo = rDualInfo.getString("TAG_INFO", "");
        // returnData.put("NO_CARD_FEE_BRAND", strTagInfo); 测试环境下数据没有，暂时不用sunxin

        // 返单开户已有客户资料是否copy客户资料
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_RETURNOPENCOPYCUST", "0");
        strTagInfo = rDualInfo.getString("TAG_CHAR", "");
        // returnData.put("RETURN_OPEN_COPY_CUST",
        // strTagInfo);测试环境下数据没有，暂时不用sunxin

        // 获取默认预存款存折标记
        rDualInfo = getTagInfo(strEparchyCode, "CS_NUM_OPENPREPAYDEPOSIT", "0");
        strTagNumber = rDualInfo.getString("TAG_NUMBER", "0");
        // returnData.put("PRE_PAY_DEPOSIT", strTagNumber);测试环境下数据没有，暂时不用sunxin

        // 获取省内跨区开户默认预存款标记
        rDualInfo = getTagInfo(strEparchyCode, "CS_NUM_PROVOPENADVANCEPAY", "0");
        strTagInfo = rDualInfo.getString("TAG_CHAR", "");
        // returnData.put("PROV_OPEN_ADVANCE_PAY", strTagInfo);// 预存款标记
        // 测试环境下数据没有，暂时不用sunxin

        // 获取省内跨区开户默认卡费标记
        rDualInfo = getTagInfo(strEparchyCode, "CS_NUM_PROVOPENOPERFEE", "0");
        strTagInfo = rDualInfo.getString("TAG_CHAR", "");
        // returnData.put("PROV_OPEN_OPERFEE", strTagInfo); 测试环境下数据没有，暂时不用sunxin

        // 获取是否支持购座机入网
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_OPENBUYDESKDEVICE", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        // returnData.put("OPEN_BUY_DESKDEVICE",
        // strTagChar);测试环境下数据没有，暂时不用sunxin
        // 获取默认开户用户数
        rDualInfo = getTagInfo(strEparchyCode, "CS_NUM_OPENLIMITCOUNT", "0");
        strTagNumber = rDualInfo.getString("TAG_NUMBER", "");
        // returnData.put("OPEN_LIMIT_COUNT", strTagNumber);测试环境下数据没有，暂时不用sunxin
        // 获取智能网在前台是否允许收费标志
        rDualInfo = getTagInfo(strEparchyCode, "CS_CSM_GSFEETAG", "0");

        strTagInfo = rDualInfo.getString("TAG_INFO", "");
        returnData.put("GS_FEE_TAG", strTagInfo);// 需要在正式库确认数据 有判断使用 sunxin
        // 获取开户确认时是否判断黑名单停机标志
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_JUDGEBLACKSTOP", "0");

        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        // returnData.put("JUDGE_BLACK_STOP", strTagChar);测试环境下数据没有，暂时不用sunxin
        // 获取本省代码
        rDualInfo = getTagInfo(strEparchyCode, "PUB_INF_PROVINCE", "0");

        strTagInfo = rDualInfo.getString("TAG_INFO", "");
        returnData.put("PROVINCE", strTagInfo);// 有记录，但是没有使用 sunxin
        // 获取是否显示用户提示信息的标记
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_SHOWHINTINFO", "0");

        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("SHOW_HINT_INFO", strTagChar);// 有记录，但是没有使用 sunxin
        // 获取默认密码的使用方式
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_DEFAULTPWDMODE", "0");

        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("DEFAULT_PWD_MODE", strTagChar);// 有记录，但是没有使用 sunxin
        // 获取默认密码
        rDualInfo = getTagInfo(strEparchyCode, "CS_INF_DEFAULTPWD", "0");

        strTagInfo = rDualInfo.getString("TAG_INFO", "");
        returnData.put("DEFAULT_PWD", strTagInfo);// 有记录，但是没有使用 sunxin
        // 获取密码长度
        rDualInfo = getTagInfo(strEparchyCode, "CS_NUM_PASSWORDLENGTH", "0");

        strTagNumber = rDualInfo.getString("TAG_NUMBER", "");
        returnData.put("DEFAULT_PWD_LENGTH", strTagNumber);// 有记录，但是没有使用 sunxin
        // 获取业务办理身份验证顺序
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_IDCHKDEALDISMODE", "0");

        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        // returnData.put("ID_CHKDEALDIS_MODE", strTagChar);测试环境下数据没有，暂时不用sunxin
        // 获取是否使用密码键盘标记
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_USEPASSWDKEYBOARD", "0");

        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("USE_PASSWD_KEYBOARD", strTagChar);// 有记录，但是没有使用 sunxin
        // 获取业务区句柄
        rDualInfo = getTagInfo(strEparchyCode, "CS_INF_CITYCODEHANDLE", "0");
        // td_m_area30
        strTagInfo = rDualInfo.getString("TAG_INFO", "");
        returnData.put("CITY_CODE_HANDLE", strTagInfo);// 有记录，但是没有使用 sunxin

        // 获取押金的名称
        rDualInfo = getTagInfo(strEparchyCode, "CS_INF_FOREGIFTNAME", "0");

        strTagInfo = rDualInfo.getString("TAG_INFO", "");
        // returnData.put("FOREGIFT_NAME", strTagInfo);测试环境下数据没有，暂时不用sunxin
        // 获取服务参数值的显示方式
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_SERVPARAMODE", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        // returnData.put("SERV_PARAM_MODE", strTagChar);测试环境下数据没有，暂时不用sunxin
        // 获取是否提示重新打印标志
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_ISAFRESHPRINT", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("IS_AFRESH_PRINT", strTagChar);// 有记录，但是没有使用 sunxin
        // 获取提示信息蓝框控制标志
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_HINTINFOCONTROL", "0");

        strTagInfo = rDualInfo.getString("TAG_INFO", "");
        // returnData.put("HINTINFO_CONTROL", strTagInfo);测试环境下数据没有，暂时不用sunxin
        // 获取员工是否有费用减免权限SYS002
        // 默认用户类型
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_DEFAULTUSERTYPE", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("DEFAULT_USER_TYPE", strTagChar);// 有记录，但是没有使用 sunxin
        // 默认证件类型
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_DEFAULTPSPTTYPE", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("DEFAULT_PSPT_TYPE", strTagChar);// 有记录，但是没有使用 sunxin
        // 默认帐户类型
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_DEFAULTPAYMODE", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("DEFAULT_PAY_MODE", strTagChar);// 有记录，但是没有使用 sunxin

        returnData.put("OPEN_TYPE", strOpenType);
        if (strOpenType.equals("IOT_OPEN"))
            returnData.put("M2M_FLAG", "1");

        if (strOpenType.equals("IOT_OPEN"))
        {
            returnData.put("MAX_LENGTH", "13"); // 物联网
        }
        else if (strOpenType.equals("TD_OPEN"))
        {
            returnData.put("MAX_LENGTH", "11"); // TD无线固话
        }
        else
        {
            returnData.put("MAX_LENGTH", "11");// 手机
        }

        String tradeDepartId = CSBizBean.getVisit().getDepartId();
        if (isOperatorDepart(strEparchyCode, tradeDepartId) == 0)// 是代理商，填充代理商编码区
        // 原先为0
        // 测试先改3
        {
            returnData.put("AGENT_DEPART_ID", getDeaprtInfo(tradeDepartId));
            returnData.put("IS_AGENT", "1");
            if (!PersonConst.IOT_OPEN.equals(strOpenType)) {
                returnData.put("OPEN_TYPE", PersonConst.AGENT_OPEN);
            }
            if ("".equals(getDeaprtInfo(tradeDepartId)))
                returnData.put("IS_AGENT", "0");
        }
        else
            returnData.put("IS_AGENT", "0");
        // returnData.put("REUSE_TYPE",
        // "".equals(strReuseType)?td.getString("REUSE_TYPE",""):strReuseType);
        // 先屏蔽 sunxin
        // 代理商开户是否只使用操作员部门(登录员工部门)进行资料校验：0,不使用登录员工部门,1,仅使用操作员工部门
        rDualInfo = getTagInfo(strEparchyCode, "CS_RESCHECK_BYDEPART", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "0");
        returnData.put("RES_CHECK_BY_DEPART", strTagChar);

        // initSelectParam 暂时不处理，sunxin

        String privTag = "0";
        if (StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "SYSCHANGPACKAGE"))
            privTag = "1";

        returnData.put("SYSCHANGPACKAGE", privTag);
        
        /*
         *@tanzheng@REQ201702140005 关于消磁身份证办理业务的优化
         *  本地身份证、外地身份证、军人身份证，当前系统规定规则，限制只有渠道类型是100、500的可以手工输入。
		 *	改造内容：取消该限制，新加入权限有该权限的工号才允许手工输入。
 		 *
         */
        if(StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "HIGH_PRIV")){
        	returnData.put("INPUT_PERMISSION", "1");
        }else{
        	returnData.put("INPUT_PERMISSION", "0");
        }

        return returnData;
    }

    /**
     * 查询某个工号的登陆时间限制
     * 
     * @author chenzm
     * @param data
     * @throws Exception
     */
    public void IsCanOperate(IData data) throws Exception
    {
        // 查询配置，如果不做限制，则直接返回
        IDataset dsCommpara = CommparaInfoQry.getCommparaAllCol("CSM", "3270", "10", getVisit().getStaffEparchyCode());
        if (IDataUtil.isEmpty(dsCommpara) || !"1".equals(dsCommpara.getData(0).getString("PARA_CODE1")))
        {
            return;
        }
        String errorcontent = dsCommpara.getData(0).getString("PARA_CODE23");

        // 根据员工查询登录时间
        IData inData = new DataMap();
        inData.put("STAFF_ID", getVisit().getStaffId());
        IDataset resultList = StaffInfoQry.queryDateTimeByStaffId(getVisit().getStaffId());
        if (resultList != null && resultList.size() > 0)
        {
            String sysdate = SysDateMgr.getSysDate();
            String flag = "0";
            for (int i = 0; i < resultList.size(); i++)
            {
                if (!(sysdate.compareTo(resultList.getData(i).getString("ALLOW_STARTTIME")) < 0) && !(resultList.getData(i).getString("ALLOW_ENDTIME").compareTo(sysdate) < 0))
                {
                    flag = "1";
                    break;

                }
            }

            if ("0".equals(flag))
            {
                CSAppException.apperr(BizException.CRM_BIZ_5, errorcontent);
            }
        }
    }

    /**
     * 二次开户中获取除三户外的其他信息
     * 
     * @author chenzm
     * @param userInfo
     * @throws Exception
     */
    public IData loadOtherInfo(IData userInfo) throws Exception
    {

        IData inData = new DataMap();
        String user_id = userInfo.getString("USER_ID");
        IDataset dataset = UserResInfoQry.queryUserResByUserIdResType(user_id, "1");
        if (dataset.isEmpty())
            CSAppException.apperr(BizException.CRM_BIZ_5, PersonConst.NO_FOUND_DATA_IN_RES_TABLE);
        String simCardNo = dataset.getData(0).getString("RES_CODE");
        inData.put("SIM_CARD_NO", simCardNo);
        return inData;
    }

    /**
     * 费用重算
     * 
     * @author chenzm
     * @param input
     * @throws Exception
     */
    public IDataset mputeFee(IData input) throws Exception
    {
        CreatePersonUserBean createPersonUserBean = BeanManager.createBean(CreatePersonUserBean.class);
        return createPersonUserBean.mputeFee(input);
    }

    /**
     * 界面初始化参数
     * 
     * @author chenzm
     * @param input
     * @throws Exception
     */
    public IDataset onInitTrade(IData input) throws Exception
    {
        this.IsCanOperate(input);
        IData returnData = this.InitPara(input);
        IDataset dataset = new DatasetList();
        dataset.add(returnData);
        return dataset;
    }

    /**
     * 开户代理商选择
     * 
     * @author chenzm
     * @param input
     * @throws Exception
     */
    public IDataset queryAgent(IData input) throws Exception
    {

        String strEpachyCode = CSBizBean.getTradeEparchyCode();
        String strCityCode = CSBizBean.getVisit().getCityCode();
        String strParaCode = input.getString("GENT_QUERY_CONDITION", "");
        IDataset result = new DatasetList();
        if (!StringUtils.isBlank(strParaCode))
        {
            IData param = new DataMap();
            param.put("CITY_CODE", strCityCode);
            param.put("EPARCHY_CODE", strEpachyCode);
            param.put("PARA_CODE2", strParaCode);
            result = DepartInfoQry.getAgentInfoByNotManagerId(strEpachyCode, strCityCode, strParaCode);

        }
        return result;
    }

    /**
     * 开户选号
     * 
     * @author sunxin
     * @param input
     * @throws Exception
     */
    public IDataset queryIDlePhone(IData input) throws Exception
    {
        /*
         * CreatePersonUserBean createPersonUserBean = BeanManager.createBean(CreatePersonUserBean.class); return
         * createPersonUserBean.queryIDlePhone(input);
         */
        String psptId = input.getString("NETCHOOSE_PSPT_ID", "");// 证件号码
        String netchooseType = input.getString("NETCHOOSE_TYPE", "");// 选号来源

        IDataset dataset = ResCall.getSelTempOccupyNum("0", netchooseType, psptId);
        /*
         * 下列数据测试 sunxin IDataset dataset = new DatasetList(); IData ss1 = new DataMap(); ss1.put("RES_NO",
         * "15091932045"); ss1.put("RANDOM_NO", "430"); ss1.put("RSRV_STR3", "111"); IData ss2 = new DataMap();
         * ss2.put("RES_NO", "15091952767"); ss2.put("RANDOM_NO", "440"); ss2.put("RSRV_STR3", "222"); dataset.add(ss1);
         * dataset.add(ss2);
         */
        return dataset;
    }

    /**
     * 校验号码进行营销活动处理
     * 
     * @author sunxin
     * @param input
     * @throws Exception
     */
    public IDataset querySaleActive(IData input) throws Exception
    {
        IData returnData = this.querySaleActiveForSvc(input);
        IDataset dataset = new DatasetList();
        dataset.add(returnData);
        return dataset;
    }

    /**
     * 判断号码是否绑定营销活动号码
     * 
     * @param pd
     * @param inData
     * @return
     * @throws Exception
     */
    public IData querySaleActiveForSvc(IData inData) throws Exception
    {
        IData data = new DataMap();
        IDataset resultList = RelaUUInfoQry.queryMMinfo(inData.getString("SERIAL_NUMBER"));

        if (IDataUtil.isEmpty(resultList))
            data.put("SERIAL_NUMBER_A", "");
        else
            data.put("SERIAL_NUMBER_A", resultList.getData(0).getString("SERIAL_NUMBER_A", ""));

        if (StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "SYS009"))
            data.put("SYS009", "TRUE");

        return data;
    }

    /**
     * 释放资源
     * 
     * @author sunxin
     * @param input
     * @throws Exception
     */
    public void releaseSingleRes(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");
        String simCardNo = input.getString("SIM_CARD_NO");
        String type = input.getString("TYPE");
        String typeCode = "0";
        //update by wukw3@物联网资源释放。
        if("IOT_OPEN".equals(type)){
        	if (StringUtils.isNotEmpty(serialNumber))
        		ResCall.releaseResWL("4", serialNumber, "0", getVisit().getStaffId(), typeCode);
        	if (StringUtils.isNotEmpty(simCardNo))
        		ResCall.releaseResWL("4", simCardNo, "1", getVisit().getStaffId(), typeCode);
        }else{
        	if (PersonConst.AGENT_OPEN.equals(type))
        		typeCode = "2";
        	if (StringUtils.isNotEmpty(serialNumber))
        		ResCall.releaseRes("2", serialNumber, "0", getVisit().getStaffId(), typeCode);
        	if (StringUtils.isNotEmpty(simCardNo))
        		ResCall.releaseRes("2", simCardNo, "1", getVisit().getStaffId(), typeCode);
       }
    }
    /**
     * 代理商开户预存款
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryAdvanceFees(IData input) throws Exception{
    	
    	 IDataset advanceFees = StaticUtil.getStaticList("AGENT_ADVANCE_FEE");
         IDataset advanceFeeP = new DatasetList();
         if (IDataUtil.isNotEmpty(advanceFees))
         {
             for (int i = 0; i < advanceFees.size(); i++)
             {
                 IData advanceFee = advanceFees.getData(i);
                 String feeValue = advanceFee.getString("DATA_ID");
                 String privKey = "AGENTADVANCEFEE_" + feeValue;
                 if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), privKey))
                 {
                     advanceFeeP.add(advanceFee);
                 }
             }
         }

         return advanceFeeP;
    	
    }
    /**
     * 代理商开户预存款
     * @param input
     * @return
     * @throws Exception
     * @REQ201502050013放号政策调整需求 by songlm
     */
    public IDataset queryPresentFees(IData input) throws Exception{
    	String paramAttr = input.getString("PARAM_ATTR","");
    	if(StringUtils.isBlank(paramAttr))
    	{
    		return null;
    	}
    	IDataset commData = CommparaInfoQry.getCommpara("CSM", paramAttr, null, CSBizBean.getTradeEparchyCode());
    	IDataset presentFeeList = new DatasetList();
        if (IDataUtil.isNotEmpty(commData))
        {
            for (int i = 0; i < commData.size(); i++)
            {
                IData presentFee = commData.getData(i);
                String dataId = presentFee.getString("PARAM_CODE","");//选项值
                String dataName = presentFee.getString("PARA_CODE1","");//选项名称
                String packageId = presentFee.getString("PARA_CODE3","");//PARA_CODE3对应包ID、PARA_CODE2对应产品ID，
                String defaultValue = presentFee.getString("PARA_CODE4","");//默认展示的项配置
                IData tempData = new DataMap();
                tempData.put("DATA_ID", dataId);
                tempData.put("DATA_NAME", dataName);
                tempData.put("PACKAGE_ID", packageId);
                tempData.put("DEFAULT_VALUE", defaultValue);
                
                presentFeeList.add(tempData);
            }
            //DataHelper.sort(presentFeeList, "DATA_ID", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
        }
        return presentFeeList;
    }
    
    /**
     * @author yanwu
     * @exception 物联网开户 联系号码校验 
     * @param cycle
     * @throws Exception
     */
    public IDataset checkPhone(IData param) throws Exception
    {
    	IDataset phones = new DatasetList();
    	String sn = param.getString("PHONE","");
    	String strInModeCode = param.getString("IN_MODE_CODE");
    	IData data = null;
    	try {
    		data = UcaInfoQry.qryUserInfoBySn(sn);
    		if( IDataUtil.isNotEmpty(data) ){
        		data.put("X_RESULT_CODE", "0");
        		data.put("X_RESULTINFO", "ok");
        	}else{
        		data = new DataMap();
        		String info = "联系电话必须是有效的移动号码!";
        		data.put("X_RESULT_CODE", "-1");
        		data.put("X_RESULTINFO", info);
        		if( "0".equals(strInModeCode) ){
        			CSAppException.apperr(BizException.CRM_BIZ_5, info);
        		}
        		
        	}
		} catch (Exception e) {
			String info = "联系电话必须是有效的移动号码!";
			data = new DataMap();
			data.put("X_RESULT_CODE", "-1");
    		data.put("X_RESULTINFO", info);
    		if( "0".equals(strInModeCode) ){
    			CSAppException.apperr(BizException.CRM_BIZ_5, info);
    		}
    		//log.info("(e);
		}
		phones.add(data);
    	return phones;
    }
    
    /**
     * 无手机宽带开户-判断是否有线上预约工单
     * REQ201809300014新增线上无手机宽带开户功能的需求—BOSS新增界面 
     * zhangxing3
     */
    public IDataset checkTradeBookInfo(IData input) throws Exception
    {
    	CreatePersonUserBean createPersonUserBean = BeanManager.createBean(CreatePersonUserBean.class);
    	return createPersonUserBean.queryUserTradeBook(input);
    }
    
    /**
     * 开户-身份证判定是否黑名单用户 
     * REQ201510090022 关于新建黑名单库的需求
     */
    public IDataset checkPsptidBlackListInfo(IData input) throws Exception
    {
    	CreatePersonUserBean createPersonUserBean = BeanManager.createBean(CreatePersonUserBean.class);
    	return createPersonUserBean.checkPsptidBlackListInfo(input);
    }
    
    /**
     * @author yanwu
     * @exception 行业应用卡类用户资料变更
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset checkProductsForM2M(IData param) throws Exception
    {
    	IDataset rs = new DatasetList();
    	
    	String serialNumber = param.getString("SERIAL_NUMBER","");
    	UcaData uca = null;
    	try {
    		uca = UcaDataFactory.getNormalUca(serialNumber);
		} catch (Exception e) {
			//log.info("(e);
			IData data = new DataMap();
			data.put("X_RESULTCODE", "-1");
			data.put("X_RESULTINFO", e);
			rs.add(data);
			return rs;
		}
    	
    	//限制9985 TD_S_COMMPARA配置编码
		//IDataset CommparaParas = CommparaInfoQry.getCommByParaAttr("CSM", "9985", "0898");
		boolean isRight = false;
		IDataset idsOther = UserOtherInfoQry.getUserOtherByUserRsrvValueCodeByEc(uca.getUserId(), "HYYYKBATCHOPEN");
		if(IDataUtil.isNotEmpty(idsOther))
		{
			isRight = true;
			/*for (int i = 0; i < CommparaParas.size(); i++) 
			{
				IData CommparaPara = CommparaParas.getData(i);
				String strParaCode1 = CommparaPara.getString("PARA_CODE1", "");
				//String strParamName = CommparaPara.getString("PARAM_NAME", "");
				String strParamCode = CommparaPara.getString("PARAM_CODE", "");
				if("P".equals(strParamCode) && StringUtils.isNotBlank(strParaCode1))
				{
					
					IDataset productDatas = UserProductInfoQry.getUserProductByUserIdProductId(uca.getUserId(), strParaCode1);
					if(IDataUtil.isNotEmpty(productDatas))
					{
						isRight = true;
						break;
					}
					
				}else if("S".equals(strParamCode) && StringUtils.isNotBlank(strParaCode1))
				{
					
					List<SvcTradeData> svcDatas = uca.getUserSvcBySvcId(strParaCode1);
					if(CollectionUtils.isNotEmpty(svcDatas))
					{
						isRight = true;
						break;
					}
					
				}else if("D".equals(strParamCode) && StringUtils.isNotBlank(strParaCode1))
				{
					
					List<DiscntTradeData> discntDatas = uca.getUserDiscntByDiscntId(strParaCode1);
					if(CollectionUtils.isNotEmpty(discntDatas))
					{
						isRight = true;
						break;
					}
					
				}
			}*/
		}/*else{
			isRight = true;
		}*/
		
		if(isRight){
			IData data = new DataMap();
			data.put("X_RESULTCODE", "0");
			data.put("X_RESULTINFO", "OK");
			data.put("USER_ID", uca.getUserId());
			data.put("CUST_ID", uca.getCustId());
			rs.add(data);
		}else{
			IData data = new DataMap();
			data.put("X_RESULTCODE", "-1");
			data.put("X_RESULTINFO", "只允许具备行业应用卡标识的号码导入！");
			rs.add(data);
		}
    	
    	return rs;
    }
    
    /**
     * @author yanwu
     * @exception 商务宽带批量开户地址校验
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset checkProductsForADD(IData param) throws Exception
    {
    	IDataset rs = new DatasetList();
    	
    	String strOPENTYPE = param.getString("OPEN_TYPE","");
    	
    	//限制1999 TD_S_COMMPARA配置编码
		IDataset CommparaParas = CommparaInfoQry.getCommPkInfo("CSM", "1999", strOPENTYPE, "0898");
		boolean isRight = false;
		if(IDataUtil.isNotEmpty(CommparaParas))
		{
			isRight = true;
		}
		
		if(isRight)
		{
			IData data = new DataMap();
			data.put("X_RESULTCODE", "0");
			data.put("X_RESULTINFO", "OK");
			rs.add(data);
		}
		else
		{
			IData data = new DataMap();
			data.put("X_RESULTCODE", "-1");
			data.put("X_RESULTINFO", "标准地址填写错误，仅支持FTTB和FTTH开户,请重新填写！");
			rs.add(data);
		}
    	
    	return rs;
    }

    /** 
     * @author yanwu
     * @exception 商务宽带批量开户带宽校验
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset checkProductsForSpeed(IData param) throws Exception
    {
    	IDataset rs = new DatasetList();
    	
    	String strPc1 = param.getString("PRODUCT_ID","");
    	String strPc2 = param.getString("OPEN_TYPE","");
    	String strPc = "CREATEPREUSER_" + strPc2;
    	
    	//限制1999 TD_S_COMMPARA配置编码
		IDataset CommparaParas = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "1999", strPc, strPc1, strPc2, "0898");
		boolean isRight = false;
		if(IDataUtil.isNotEmpty(CommparaParas))
		{
			isRight = true;
		}
		
		if(isRight)
		{
			String strDiscntCode = "";
			String strServiceID = "";
			IData idSpeed = CommparaParas.first();
			String strSpeed = idSpeed.getString("PARA_CODE3", "");
			String strPARA_CODE24 = idSpeed.getString("PARA_CODE24", "");
			String[] strSplits = strPARA_CODE24.split(",");
			for (int i = 0; i < strSplits.length; i++) 
			{
				String strSplit[] = strSplits[i].split("_");
				if(strSplit.length != 2)
				{
					IData data = new DataMap();
					data.put("X_RESULTCODE", "-1");
					data.put("X_RESULTINFO", "TD_S_COMMPARA配置不正确");
					rs.add(data);
					return rs;
				}
				String strElementTypeCode = strSplit[0];
				String strElementID = strSplit[1];
				if("D".equals(strElementTypeCode))
				{
					if(StringUtils.isBlank(strDiscntCode))
					{
						strDiscntCode = strElementID;
					}
					else
					{
						strDiscntCode = strDiscntCode + "," + strElementID ;
					}
				}
				else if("S".equals(strElementTypeCode))
				{
					if(StringUtils.isBlank(strServiceID))
					{
						strServiceID = strElementID;
					}
					else
					{
						strServiceID = strServiceID + "," + strElementID ;
					}
				}
			}
			IData data = new DataMap();
			data.put("X_RESULTCODE", "0");
			data.put("X_RESULTINFO", "OK");
			data.put("WIDE_PRODUCT_ID", strSpeed);
			if(StringUtils.isNotBlank(strDiscntCode))
			{
				data.put("DISCNT_CODE", strDiscntCode);
			}
			if(StringUtils.isNotBlank(strServiceID))
			{
				data.put("SERVICE_ID", strServiceID);
			}
			rs.add(data);
		}
		else
		{
			IData data = new DataMap();
			data.put("X_RESULTCODE", "-1");
			data.put("X_RESULTINFO", "速率填写错误,请重新填写！");
			rs.add(data);
		}
    	
    	return rs;
    }
    
    /**
     * @author yanwu
     * @exception 集团产品编码校验
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset checkPhoneBNBD(IData param) throws Exception
    {
    	IDataset rs = new DatasetList();
    	String serialNumberGrp = param.getString("SERIAL_NUMBER");
        
        if(StringUtils.isNotBlank(serialNumberGrp))
        {
        	IData productInfo = UcaInfoQry.qryUserMainProdInfoBySnForGrp(serialNumberGrp);
            if (IDataUtil.isNotEmpty(productInfo))
            {
            	// 判断集团商务宽带用户
                if ("7341".equals(productInfo.getString("PRODUCT_ID")))
                {
                	IData data = new DataMap();
        			data.put("X_RESULTCODE", "0");
        			data.put("X_RESULTINFO", "ok");
        			rs.add(data);
                }
                else
                {
                	IData data = new DataMap();
        			data.put("X_RESULTCODE", "-1");
        			data.put("X_RESULTINFO", "集团产品编码错误，请重新填写");
        			rs.add(data);
				}
            }
            else
            {
            	IData data = new DataMap();
    			data.put("X_RESULTCODE", "-1");
    			data.put("X_RESULTINFO", "集团产品编码错误，请重新填写！");
    			rs.add(data);
			}
        }
    	return rs;
    }

    /**
     *
     * 批量导入使用人证件号码数限制校验 	 
     * REQ201608150016新增“以单位证件开户集团成员实名资料维护界面”需求
     * @author zhuoyingzhi
     * @param input
     * @throws Exception	 	 
     */
    public IDataset checkImportRealNameLimitByUsePspt(IData input) throws Exception	 	 
    {
 	 	 
        IDataset ajaxDataset = new DatasetList();	 	 
        String custName = input.getString("CUST_NAME").trim();	 	 
        String psptId = input.getString("PSPT_ID").trim();	
        String serialNumber = input.getString("SERIAL_NUMBER","");	 	
        //IData param = new DataMap();	 	 
        if (!"".equals(custName) && !"".equals(psptId))	 	 
        {	 	 
        	IData ajaxData = new DataMap();	
        	/**
        	 * REQ201611180016 关于特殊调整我公司营业执照开户使用人不限制5户的需求
        	 * chenxy3 20161212 
        	 * */
        	String psptTypeCode=input.getString("PSPT_TYPE_CODE");
        	if("E".equals(psptTypeCode) && "91460000710920952X".equals(psptId) && "中国移动通信集团海南有限公司".equals(custName)){
        		ajaxData.put("MSG", "OK");
                ajaxData.put("CODE", "0");
                ajaxDataset.add(ajaxData);
        		return ajaxDataset;
        	}
        	
            int rCount = UserInfoQry.getRealNameUserCountByUsePspt(custName, psptId, serialNumber);   // 获取使用人证件号码已实名制开户的数量	 	 
            int rLimit = UserInfoQry.getRealNameUserLimitByPspt(custName, psptId);	 	 
 	 	 
             	 
            ajaxData.put("rCount", rCount);	 	 
            ajaxData.put("rLimit", rLimit);	 	 
            if (rCount < rLimit)	 	 
            {	 	 
                ajaxData.put("MSG", "OK");	 	 
                ajaxData.put("CODE", "0");	 	 
            }	 	 
            else	 	 
            {	 	 
                ajaxData.put("MSG", "使用人证件号码【" + psptId + "】实名制开户的数量已达到最大值【" + rLimit + "个】，请更换其它证件！");	 	 
                ajaxData.put("CODE", "1");	 	 
            }	 	 
            ajaxDataset.add(ajaxData);	 	 
 	 	 
        }	 	 
        return ajaxDataset;	 	 
    }  
    /**
     * 实名认证客户二代身份证
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset verifyIdCard(IData input) throws Exception{
        
        CreatePersonUserBean bean = (CreatePersonUserBean) BeanManager.createBean(CreatePersonUserBean.class);
        IData data = bean.verifyIdCard(input);
        
        IDataset dataset = new DatasetList();
        dataset.add(data);
        
        //获取经办人一个自然月和一年内经办人证件号的数量
        if(input.getString("checkAgent","").equals("1")){
	        IData reqData=new DataMap();
	        reqData.put("PSPT_ID", input.getString("CERT_ID"));
	        reqData.put("CUST_AGE", input.getString("CUST_AGE"));
	        IData retData= this.AgentIdCardNums(reqData);
	        data.put("CODE2", retData.getString("CODE2"));
	        data.put("RESULT_INFO", retData.getString("RESULT_INFO"));
        }
        return dataset;
    }
    
    /**
     * 获取经办人一个自然月和一年内经办人证件号的数量
     * @param input
     * @return
     * @throws Exception
     */
	 public IData AgentIdCardNums(IData input) throws Exception{
	 	IData retData=new DataMap();
	 	retData.put("CODE2", "0");
	 	retData.put("RESULT_INFO", "");
	 	
	 	/**
	 	 * REQ201907220024 关于代办一证两卡优化需求
	 	 * 通过TD_S_COMMPARA表设置特定有效时间
	 	 */
	 	//IDataset cAgent = CommparaInfoQry.getCommpara("CSM", "9730", "COMMISSION_AGENT", CSBizBean.getTradeEparchyCode());
	 	IDataset cAgent = CommparaInfoQry.getCommparaInfoByCode3("CSM", "9730", "COMMISSION_AGENT", getVisit().getStaffId(), CSBizBean.getTradeEparchyCode());
	 	if(IDataUtil.isEmpty(cAgent)){
	 		CreatePersonUserBean bean = (CreatePersonUserBean) BeanManager.createBean(CreatePersonUserBean.class);
	        //查询一个自然月的经办数量
		 	IDataset data = bean.AgentIdCardMonth(input);
	        if(IDataUtil.isNotEmpty(data)
	        		&&data.getData(0).getInt("AGENT_NUM")>=2){
	        	retData.put("CODE2", "-1");
	        	retData.put("RESULT_INFO", "同一经办人1个自然月内仅允许代办2个号码新入网");
	        	return retData;
	        }
	        //查询一年的经办数量
	        IDataset data1 = bean.AgentIdCardYear(input);
	        if(IDataUtil.isNotEmpty(data1)
	        		&&data1.getData(0).getInt("AGENT_NUM")>=5){
	        	retData.put("CODE2", "-1");
	        	retData.put("RESULT_INFO", "同一经办人1个自然年内最多允许代办5个号码新入网");
	        	return retData;
	        }
	 	}else{
	 		boolean isInvalidate=false;
	 		//判断是否在有效期
	 		for (int i = 0; i < cAgent.size(); i++) {
				IData params = cAgent.getData(i);
				String timeSet=SysDateMgr.decodeTimestamp(SysDateMgr.getSysDateYYYYMMDDHHMMSS(),"yyyy-MM-dd HH:mm:ss");
			 	String paraCode1=SysDateMgr.decodeTimestamp(params.getString("PARA_CODE1"),"yyyy-MM-dd HH:mm:ss");
			 	String paraCode2=SysDateMgr.decodeTimestamp(params.getString("PARA_CODE2"),"yyyy-MM-dd HH:mm:ss");
			 	if (timeSet.compareTo(paraCode1)>0 && timeSet.compareTo(paraCode2)<0){
			 		isInvalidate=true;
			 		break;
			 	}
	 		}
	 		//有权限在有效期（只判断大于16岁的）
	 		if(isInvalidate){
		 			int custAge = Integer.parseInt(input.getString("CUST_AGE"));
					if (custAge>16) {
						
						CreatePersonUserBean bean = (CreatePersonUserBean) BeanManager.createBean(CreatePersonUserBean.class);
				        //查询一个自然月的经办数量
					 	IDataset data = bean.AgentIdCardMonth(input);
				        if(IDataUtil.isNotEmpty(data)
				        		&&data.getData(0).getInt("AGENT_NUM")>=2){
				        	retData.put("CODE2", "-1");
				        	retData.put("RESULT_INFO", "同一经办人1个自然月内仅允许代办2个号码新入网");
				        	return retData;
				        }
				        //查询一年的经办数量
				        IDataset data1 = bean.AgentIdCardYear(input);
				        if(IDataUtil.isNotEmpty(data1)
				        		&&data1.getData(0).getInt("AGENT_NUM")>=5){
				        	retData.put("CODE2", "-1");
				        	retData.put("RESULT_INFO", "同一经办人1个自然年内最多允许代办5个号码新入网");
				        	return retData;
				        }
					}
	 		//有权限不在有效期
	 		}else{
	 			CreatePersonUserBean bean = (CreatePersonUserBean) BeanManager.createBean(CreatePersonUserBean.class);
		        //查询一个自然月的经办数量
			 	IDataset data = bean.AgentIdCardMonth(input);
		        if(IDataUtil.isNotEmpty(data)
		        		&&data.getData(0).getInt("AGENT_NUM")>=2){
		        	retData.put("CODE2", "-1");
		        	retData.put("RESULT_INFO", "同一经办人1个自然月内仅允许代办2个号码新入网");
		        	return retData;
		        }
		        //查询一年的经办数量
		        IDataset data1 = bean.AgentIdCardYear(input);
		        if(IDataUtil.isNotEmpty(data1)
		        		&&data1.getData(0).getInt("AGENT_NUM")>=5){
		        	retData.put("CODE2", "-1");
		        	retData.put("RESULT_INFO", "同一经办人1个自然年内最多允许代办5个号码新入网");
		        	return retData;
		        }
	 		}
	 	}
	 	
        return retData;
	}
    
    /**
     * 人像信息比对
     * @param input
     * @return
     * @throws Exception
     * @author dengyi
     */
    public IDataset cmpPicInfo(IData input) throws Exception{
        
        CreatePersonUserBean bean = (CreatePersonUserBean) BeanManager.createBean(CreatePersonUserBean.class);
        IData data = bean.cmpPicInfo(input);
        
        IDataset dataset = new DatasetList();
        dataset.add(data);
        return dataset;
    }
    /**
     * 人像信息比对员工信息
     * @param input
     * @return
     * @throws Exception
     * @author dengyi
     */
    public IDataset isCmpPic(IData input) throws Exception{

    	CreatePersonUserBean bean = (CreatePersonUserBean) BeanManager.createBean(CreatePersonUserBean.class);
    	IData data = bean.isCmpPic(input);

    	IDataset dataset = new DatasetList();
    	dataset.add(data);
    	return dataset;
    }


    /**
     * 工号是否具有某功能权限
     * @param input
     * @return
     * @throws Exception
     * @author chenchunni
     */
    public IDataset isFuncDataPriv(IData input) throws Exception{

        CreatePersonUserBean bean = (CreatePersonUserBean) BeanManager.createBean(CreatePersonUserBean.class);
        IData data = bean.isFuncDataPriv(input);

        IDataset dataset = new DatasetList();
        dataset.add(data);
        return dataset;
    }

    
    /**
     * 跨区补卡是否免人像比对和身份证可手动输入权限
     * @param input
     * @return
     * @throws Exception
     * @author 
     */
    public IDataset kqbkDataRight(IData input) throws Exception{
        
        CreatePersonUserBean bean = (CreatePersonUserBean) BeanManager.createBean(CreatePersonUserBean.class);
        IData data = bean.kqbkDataRight(input);        
        IDataset dataset = new DatasetList();
        dataset.add(data);
        return dataset;
    }    
    

    /**
     * 营业执照、组织机构代码证、事业单位法人登记证书不能一个证件号码对应多个不同的单位名称。
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset verifyIdCardName(IData input) throws Exception{
        
        CreatePersonUserBean bean = (CreatePersonUserBean) BeanManager.createBean(CreatePersonUserBean.class);
        IData data = bean.verifyIdCardName(input);
        
        IDataset dataset = new DatasetList();
        dataset.add(data);
        
      //获取经办人一个自然月和一年内经办人证件号的数量(校验护照)
        if(input.getString("checkAgent","").equals("1")){
	        IData reqData=new DataMap();
	        reqData.put("PSPT_ID", input.getString("CERT_ID"));
	        reqData.put("CUST_AGE", input.getString("CUST_AGE"));
	        IData retData= this.AgentIdCardNums(reqData);
	        data.put("CODE2", retData.getString("CODE2"));
	        data.put("RESULT_INFO", retData.getString("RESULT_INFO"));
        }
        
        return dataset;
    }
	
	public IDataset isPwlwOper(IData input) throws Exception {
		CreatePersonUserBean bean = (CreatePersonUserBean) BeanManager.createBean(CreatePersonUserBean.class);
		boolean flag = bean.isPwlwOper(input.getString("SERIAL_NUMBER"), input.getString("BUISUSERTYPE", "").trim());
		IData data = new DataMap();
		data.put("FLAG", flag);
		IDataset dataset = new DatasetList();
		dataset.add(data);
		return dataset;
	}
    public IDataset verifyEnterpriseCard(IData input) throws Exception{
        CreatePersonUserBean bean = (CreatePersonUserBean) BeanManager.createBean(CreatePersonUserBean.class);
        IData data = bean.verifyEnterpriseCard(input);
        IDataset dataset = new DatasetList();
        dataset.add(data);
        return dataset;
    }
    public IDataset verifyOrgCard(IData input) throws Exception{
        CreatePersonUserBean bean = (CreatePersonUserBean) BeanManager.createBean(CreatePersonUserBean.class);
        IData data = bean.verifyOrgCard(input);
        IDataset dataset = new DatasetList();
        dataset.add(data);
        return dataset;
    }
	/**
     * REQ201608310006 关于海口分公司四级吉祥号码规则优化（二）
     * 判断是否四级吉祥号码
     * chenxy3 20160927
     * BEAUTIFUAL_TAG：是否是吉祥号：0-非；1-是
    	RSRV_STR4 级别（取值说明 1、2、3、4等）
    	CLASS_ID 级别编码
     */
    public boolean checkIfBeautyNo(IData input) throws Exception{
    	boolean flag=false;
        String serialNum=input.getString("SERIAL_NUMBER");
        String snClass=input.getString("SN_CLASS");
    	IDataset numberInfo = ResCall.getMphonecodeInfo(serialNum);// 查询号码信息
    	if(IDataUtil.isNotEmpty(numberInfo)){
    		String beautyTag=numberInfo.getData(0).getString("BEAUTIFUAL_TAG","");//BEAUTIFUAL_TAG：是否是吉祥号：0-非；1-是 
    		if("1".equals(beautyTag)){
    			String classNo=numberInfo.getData(0).getString("RSRV_STR4","");//RSRV_STR4 级别（取值说明 1、2、3、4等）
    			if(snClass.equals(classNo)){
    				flag=true;
    			}
    		}
    	}
        return flag;
    }
    
    /**
     * 获取军人身份证类型
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset psptTypeCodePriv(IData input) throws Exception{
        
        CreatePersonUserBean bean = (CreatePersonUserBean) BeanManager.createBean(CreatePersonUserBean.class);
        IData data = bean.psptTypeCodePriv(input);
        
        IDataset dataset = new DatasetList();
        dataset.add(data);
        return dataset;
        
    }  
    /*
     * 全网一证5号校验
     */
     public IDataset checkGlobalMorePsptId(IData input) throws Exception
        {
         CreatePersonUserBean bean = (CreatePersonUserBean) BeanManager.createBean(CreatePersonUserBean.class);
         IDataset dataset = bean.checkGlobalMorePsptId(input);
         return dataset;         
        }
    


     /**
      * BUS201807260017	关于调整实名制相关规则的需求 by mqx 20180823
      * 代办入网业务权限判断
      * @param input
      * @return
      * @throws Exception
      */
     public IDataset verifyAgentPriv(IData input) throws Exception{
         
    	IDataset dataset = new DatasetList();
    	IData data = new DataMap();
    	String staffId = getVisit().getStaffId();	
    	
    	data.put("hasAgentPriv", StaffPrivUtil.isFuncDataPriv(staffId, "OP_AGENT_PRIV"));
    	dataset.add(data);
    	 
    	return dataset;          
         
     }
     
     /**
      * @Description: 选择产品时业务提示信息
      * @param:参数描述
      * @return：返回结果描述
      * @version: v1.0.0
      * @author: xurf3
      * @date: 2019-01-03
      */
     public IData createPersonUserTipsInfo(IData param) throws Exception
     {
         IData result = new DataMap();
         
         result.put("resultCode", "-1");

         String productId = param.getString("PRODUCT_ID");
         
         IData commpara = new DataMap();
    	 commpara.put("PARA_CODE4", "NKD");
    	 commpara.put("SUBSYS_CODE", "CSM");
         commpara.put("PARAM_ATTR", "5453");
         commpara.put("PARAM_CODE", "TIPINFO");
         commpara.put("PARA_CODE1", productId);
         IDataset commpara5453 = CommparaInfoQry.getCommparaInfoBy1To7(commpara);

         if(IDataUtil.isNotEmpty(commpara5453))
         {
         	String resultInfo = commpara5453.getData(0).getString("PARA_CODE20");
         	result.put("resultInfo", resultInfo);
         	result.put("resultCode", "0");
         }

         //权益中心改造：开户选择产品时弹出权益提醒 add by liwei29
         //根据productid去产商品查找相关的权益配置
         IDataset welFarelist = UpcCall.queryMainOfferRelaWelfareOffers(productId);
         String walerConten = "";
         if(IDataUtil.isNotEmpty(welFarelist)) {
             for(int i=0;i<welFarelist.size();i++) {
                 String walerFareName = welFarelist.getData(i).getString("OFFER_NAME");
                 walerConten += walerFareName+",";
             }
             String walerTip = "您选择的主产品具有关联的权益包:" + walerConten + "产品办理成功后便可订购";
             result.put("walerTipInfo", walerTip);
             result.put("walerTipCode", "0");
         }
         return result;
     }


    public IDataset getESIMSerialNumber(IData input) throws Exception{
        IDataset result = ResCall.selOnePhoneNum("1");
        if (IDataUtil.isEmpty(result)) {
            CSAppException.apperr(BizException.CRM_BIZ_5, "获取一号一终端号码为空！");
        }
        IDataset dataset = new DatasetList();
        for(int i = 0; i < result.size(); i++){
            IData temp = new DataMap();
            temp.put("SERIAL_NUMBER", result.getData(i).getString("SERIAL_NUMBER"));
            dataset.add(temp);
        }
        return dataset;
    }


     /**
      * REQ201904260020新增物联网批量开户界面权限控制需求
      * 免人像比对权限判断
      * @author mengqx
      * @date 20190515
      * @param clcle
      * @throws Exception
      */
     public IDataset isBatCmpPic(IData input) throws Exception{
     	
     	CreatePersonUserBean bean = (CreatePersonUserBean) BeanManager.createBean(CreatePersonUserBean.class);
     	IData data = bean.isBatCmpPic(input);
     	
     	IDataset dataset = new DatasetList();
     	dataset.add(data);
     	return dataset;
     }
     
     /**
      * REQ201909040010在和家固话实名认证环节增加校验客户的固话开户实名信息与手机号码实名信息—BOSS侧
      * 家庭IMS固话开户(新),在界面进行人像对比后，所获取的证件号码和姓名，要与界面输入的手机号码对应的证件号码和姓名进行对比，信息一致才能提交办理
      * mengqx 20190912
      */
     public IDataset checkIMSPhoneCustInfo(IData input) throws Exception
     {
    	 
    	 CreatePersonUserBean bean = (CreatePersonUserBean) BeanManager.createBean(CreatePersonUserBean.class);
         
    	 IData data = bean.checkIMSPhoneCustInfo(input);
    	 
    	 IDataset dataset = new DatasetList();
    	 dataset.add(data);
    	 return dataset;
     } 
	 
	 /**
     * REQ201810190032 	和家固话开户界面增加实名制校验—BOSS侧  by mqx 20190108
     * 和家固话单位开户权限判断
     *
     */
    public IDataset verifyOrganizationPriv(IData input) throws Exception{

        CreatePersonUserBean bean = (CreatePersonUserBean) BeanManager.createBean(CreatePersonUserBean.class);
        IDataset dataset = new DatasetList();
        IData data = bean.verifyOrganizationPriv(input);
        dataset.add(data);

        return dataset;

    }

    /**
     * REQ201810190032 	和家固话开户界面增加实名制校验—BOSS侧  by mqx 20190108
     * 和家固话代办权限判断
     *
     */
    public IDataset verifyIMSOpAgentPriv(IData input) throws Exception{

        CreatePersonUserBean bean = (CreatePersonUserBean) BeanManager.createBean(CreatePersonUserBean.class);
        IDataset dataset = new DatasetList();
        IData data = bean.verifyIMSOpAgentPriv(input);
        dataset.add(data);

        return dataset;

    }

    /**
     * REQ201911290007_【携号转网】关于发布CSMS和SOA间接口协议上身份证件传递要求的通知
     * 修改携转界面使用的客户组件，过滤掉不符合规范证件
     * @author mengqx
     * @date 20200323
     */
    public IDataset queryNpPsptTypeList(IData input) throws Exception{

        CreatePersonUserBean bean = (CreatePersonUserBean) BeanManager.createBean(CreatePersonUserBean.class);
        IDataset dataset = new DatasetList();
        IData data = bean.queryNpPsptTypeList(input);
        dataset.add(data);

        return dataset;
    }
}
