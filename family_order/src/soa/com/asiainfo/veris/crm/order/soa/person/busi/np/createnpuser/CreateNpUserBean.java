
package com.asiainfo.veris.crm.order.soa.person.busi.np.createnpuser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserNpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.cust.UCustBlackInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UBankInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UPsptTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.DevicePriceQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductFeeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
public class CreateNpUserBean extends CSBizBean
{

    public static IDataset distinct(List<IDataset> sets)
    {
        IDataset datas = new DatasetList();
        Map<String, IData> map = new HashMap<String, IData>();
        for (IDataset set : sets)
        {
            for (int i = 0; i < set.size(); i++)
            {
                IData data = set.getData(i);
                map.put(data.getString("PRODUCT_TYPE_CODE"), data);
            }
        }
        for (String key : map.keySet())
        {
            datas.add(map.get(key));
        }
        return datas;
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
        {
            return "";
        }
        return strOldBindValue;// 转换后参数
    }

    /**
     * 根据默认标记获取产品类型
     * 
     * @param strDefaultTag
     * @return
     * @throws Exception
     */
    public static IDataset getProductTypeCodeByDefaultTag(String strDefaultTag) throws Exception
    {

        IDataset typeset = null;
        if (strDefaultTag.indexOf(",") == -1)
        {// 单参数
            return ProductTypeInfoQry.getProductTypeByDefaultTag(strDefaultTag);
        }
        else
        {
            strDefaultTag = getMulteBindValue(strDefaultTag);
            String[] strBindValueArry = StringUtils.split(strDefaultTag, ',');
            List<IDataset> sets = new ArrayList<IDataset>();
            for (int i = 0; i < strBindValueArry.length; i++)
            {
                typeset = ProductTypeInfoQry.getProductTypeByDefaultTag(strBindValueArry[i]);
                sets.add(typeset);
            }
            return distinct(sets);
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
     * @Function: checkResExistUserNP
     * @Description: P校验证件号码是否存在携转标志为：已携出或者携入携出的用户记录
     * @param:参数描述
     * @return：true-存在, false-不存在
     * @throws：异常描述
     * @version: v1.0.0
     * @author: wangbo3
     * @date: 2015-4-3 下午3:34:38 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2015-4-3 wangbo3 v1.0.0 修改原因
     */
    public boolean checkResExistUserNP(String sn) throws Exception
    {

        IDataset ids = TradeNpQry.getAllTradeNpBySn(sn);
        if (IDataUtil.isNotEmpty(ids))
        {
            IData data = ids.getData(0);
            String npTag = data.getString("NP_TAG");
            if ("4".equals(npTag) || "8".equals(npTag))
            {
                return true;
            }

        }
        return false;
    }

    /**
     * @Function: checkSerialNumber
     * @Description: * 携转号码受理前校验 (1)系统判断申请号码的归属地和办理营业厅归属地相同； (2)系统判断申请号码当前在系统中没有有效的用户REMOVE_TAG='0'；
     *               (3)系统判断申请号码如果在系统中办过携转，那么携出时间须超120天； (4)系统判断申请号码当前在系统中不存在携入申请但还未完工的工单； (5)系统判断申请号码没有办理过联通转接业务；
     *               (6)系统调用资源接口对该号码进行资源校验； (7)系统读取当前未完工的携入申请工单数，如果超过系统设置值N，返回一个提示给用户；
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: wangbo3
     * @date: 2015-4-3 下午3:18:30 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2015-4-3 wangbo3 v1.0.0 修改原因
     */
    public IData checkSerialNumber(IData param) throws Exception
    {
    	
    	/*
    	 * REQ201908210003	携号转网改造点补充20190815 （月末和春节假期正常受理业务）
    	 */
//        String strDate1 = SysDateMgr.getLastDateThisMonth4WEB();
//        String strDate2 = SysDateMgr.getSysTime();
//
//        int day = SysDateMgr.dayInterval(strDate1, strDate2);	
//        if (day <= 1)
//        {
//            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "月末两天不能办理携号转网！请更换时间办理");
//        }
        IData reData = new DataMap();
        reData.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		
        // (1)系统判断申请号码的归属地和办理营业厅归属地相同；
        String home_operator = "";// 归属运营商
        String prov_code = "";
        String sn = param.getString("SERIAL_NUMBER");
        IDataset ids = TradeNpQry.getValidTradeNpBySn(sn);
        String isMobile = "";// 移动号码标识
        String asp = "";

        String tradeEparchyCode = getTradeEparchyCode();

        if (IDataUtil.isEmpty(ids))
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "查询号码归属无记录，携入号码无效！");
        }
        else
        {
            String snEparchyCode = ids.getData(0).getString("AREA_CODE", "").trim();
            prov_code = ids.getData(0).getString("PROV_CODE", "").trim();
            if (!tradeEparchyCode.equals(snEparchyCode))
            {
                CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "登陆员工地州:" + tradeEparchyCode + "与携入号码归属地:" + snEparchyCode + "不一致!");
            }

            asp = ids.getData(0).getString("ASP", "").trim();

            if ("2".equals(asp))
            {
                home_operator = "003";
            }
            if ("3".equals(asp))
            {
                home_operator = "001";
            }
            if ("1".equals(asp))
            {
                home_operator = "002";
                isMobile = "1"; // 表示要携回移动号码
            }
        }

        // (2)系统判断申请号码当前在系统中没有有效的用户REMOVE_TAG='0'；
        IData userInfo = UcaInfoQry.qryUserInfoBySn(sn);
        if (IDataUtil.isNotEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "系统中已存在有效的" + sn + "资料，不能再携入！");
        }

        IDataset dataset = TradeNpQry.getAllTradeNpBySn(sn);
        if (IDataUtil.isEmpty(dataset) && "1".equals(isMobile))
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "普通移动号码不能办理携入开户! ");
        }

        //代码屏蔽，工信部要求不需要关于携出未满120天的校验
        // (3)系统判断申请号码如果在系统中办过携转，那么携出时间须超120天；
//        if (IDataUtil.isNotEmpty(dataset))
//        {
//            String portDate = dataset.getData(0).getString("PORT_OUT_DATE");
//            String np_tag = dataset.getData(0).getString("NP_TAG");// TF_B_TRADE_NP表无NP_TAG字段，是否查TF_F_USER_NP
//
//            if ("4".equals(np_tag) || "8".equals(np_tag))
//            {
//
//                int dateNum = SysDateMgr.dayInterval(portDate, SysDateMgr.getSysDate());
//
//                if ("1".equals(asp))
//                {
//                    if (dateNum < 120)
//                    {
//                        CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "携转号码必须携出120天后才能办理携入申请业务！");
//                    }
//                }
//
//            }
//        }

        // (4)系统判断申请号码当前在系统中不存在携入申请但还未完工的工单；
        IDataset trades = TradeInfoQry.getMainTradeBySN(sn, "40");
        if (IDataUtil.isNotEmpty(trades))
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, sn + "有携入申请未完工的工单，不能再申请！");
        }

        // (6)系统调用资源接口对该号码进行资源校验
        IDataset datas = ResCall.chkNpMphoneInfo(sn, "2");// 号码校验

        // (7)系统读取当前未完工的携入申请工单数，如果超过系统设置值N，返回一个提示给用户
        IDataset tags = TagInfoQry.queryNormalTagInfoByTagCode(tradeEparchyCode, "NP_IN_TRADENUM_LIMIT", "CSM", "0");
        String message = "";
        if (IDataUtil.isNotEmpty(tags))
        {
            IData tagInfo = tags.getData(0);
            int tagNumber = tagInfo.getInt("TAG_NUMBER");

            if (tagNumber > 0)
            {
                int tradeNpCount = TradeNpQry.getTradeNpCountByTradeTypeCode("40");
                if (tradeNpCount >= tagNumber)
                {
                    message = tagInfo.getString("TAG_INFO");
                    reData.put("MESSAGE", message);// (7)系统读取当前未完工的携入申请工单数，如果超过系统设置值N，返回一个提示给用户
                }
            }
        }

        reData.put("HOME_OPERATOR", home_operator);
        reData.put("PROV_CODE", prov_code);

        return reData;
    }

    /**
     * @Function: checkSimCardNo
     * @Description: sim资源可用校验
     * @param data
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: wangbo3
     * @date: 2015-4-3 上午9:33:31
     */
    public IData checkSimCardNo(IData data) throws Exception
    {

        String serialNumber = data.getString("SERIAL_NUMBER");
        String simCardNo = data.getString("SIM_CARD_NO");
        IData returnData = new DataMap();

        IData resInfoData = new DataMap();

        IDataset checkSimDatas = ResCall.chkNpSimcardInfo(simCardNo, "2", serialNumber);
        //IDataset checkSimDatas=new DatasetList();
        IData checkSimData = new DataMap();
        if (IDataUtil.isNotEmpty(checkSimDatas))
        {
            checkSimData = checkSimDatas.getData(0);
        }

        resInfoData.put("IMSI", checkSimData.getString("IMSI", ""));
        resInfoData.put("KI", checkSimData.getString("KI", ""));
        resInfoData.put("CAPACITY_TYPE_CODE", checkSimData.getString("CAPACITY_TYPE_CODE", ""));// 卡空量
        resInfoData.put("OPC", checkSimData.getString("OPC", ""));
        resInfoData.put("SIM_CARD_NO", checkSimData.getString("SIM_CARD_NO", ""));
        resInfoData.put("NET_TYPE_CODE", checkSimData.getString("NET_TYPE_CODE"));
        resInfoData.put("RES_KIND_CODE", checkSimData.getString("RES_KIND_CODE"));
        resInfoData.put("RES_KIND_NAME", checkSimData.getString("RES_KIND_NAME", ""));
        resInfoData.put("RES_TYPE_CODE", checkSimData.getString("RES_TYPE_CODE", ""));
		String SIM_CARD_TYPE = checkSimData.getString("RES_TYPE_CODE", "");
		if (StringUtils.isNotBlank(SIM_CARD_TYPE) && SIM_CARD_TYPE.length() > 1) {
			SIM_CARD_TYPE = SIM_CARD_TYPE.substring(1);
		}
        resInfoData.put("SIM_CARD_TYPE", SIM_CARD_TYPE);
        resInfoData.put("SIM_CARD_SALE_MONEY", "" + checkSimData.getString("SALE_MONEY", "0"));
        resInfoData.put("SIM_FEE_TAG", checkSimData.getString("FEE_TAG", ""));
        String resKindCode = checkSimData.getString("RES_KIND_CODE", "");
        returnData.put("SIM_CHECK_RESULT_CODE", "1");
        resInfoData.put("MAIN_RSRV_STR8", checkSimData.getString("RSRV_STR6", "") + " " + checkSimData.getString("RSRV_STR7", ""));
        String uSimOpc = checkSimData.getString("OPC", "");// usim卡的一种属性
        resInfoData.put("OPC_CODE", "OPC_VALUE");
        resInfoData.put("OPC_VALUE", uSimOpc);

        IData feeData = DevicePriceQry.getDevicePrice(BizRoute.getRouteId(), "1", "-1", "40", checkSimData.getString("CAPACITY_TYPE_CODE", ""), resKindCode);
        if (IDataUtil.isNotEmpty(feeData))
        {
            returnData.put("FEE_MODE", "0");
            returnData.put("FEE_TYPE_CODE", feeData.getString("FEEITEM_CODE"));
            returnData.put("FEE", feeData.getString("DEVICE_PRICE"));
        }

        returnData.put("RES_INFO_DATA", resInfoData);

        return returnData;
    }

    /**
     * @Function: getProductFeeInfo
     * @Description: 获取产品费用
     * @param data
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: wangbo3
     * @date: 2015-4-3 下午4:16:56
     */
    public IDataset getProductFeeInfo(IData data) throws Exception
    {

        String product_id = data.getString("PRODUCT_ID");
        String brand_code = data.getString("BRAND_CODE");
        String eparchy_code = CSBizBean.getUserEparchyCode();
        IDataset dataset = ProductFeeInfoQry.getProductFeeInfo("40", product_id, "-1", null, null, "3", eparchy_code);
        return dataset;

    }

    public IData getProductInfos(IData data) throws Exception
    {

        IData returnData = new DataMap();
        boolean specDealTag = false;// 不同处理标志
        String strBindSingleProduct = data.getString("EXISTS_SINGLE_PRODUCT", "");

        String strBindDefaultTag = data.getString("EXISTS_DEFAULT_TAG", "");

        // 判断标志位:只要有一个不为空，作特殊处理
        if (!StringUtils.isBlank(strBindSingleProduct) || !StringUtils.isBlank(strBindDefaultTag))
        {
            specDealTag = true;// 单产品
        }
        IDataset productTypeList = null;
        // 产品类型:不存在绑定产品:无特殊处理
        if (!specDealTag)
        {
//            productTypeList = ProductTypeInfoQry.getProductCatalogByPTC("0000");
        }
        else
        {
            if (!StringUtils.isBlank(strBindSingleProduct))
            {
                productTypeList = getProductTypeCodeByProductId(strBindSingleProduct, BizRoute.getRouteId());

                // 号码绑定单个产品时，不显示产品目录，直接将此产品下的必选包下的必选择默认元素显示
                if (productTypeList.size() > 0)
                {
                    // IData productInfo = ProductManager.qryProductByPK(strBindSingleProduct);
                    // returnData.put("PRODUCT_NAME", productInfo.getString("PRODUCT_NAME"));
                    // returnData.put("PRODUCT_ID", strBindSingleProduct);
                    // returnData.put("BRAND_CODE", productInfo.getString("BRAND_CODE"));
                }
            }
            else
            {
                // 绑定默认标记
                if (!StringUtils.isBlank(strBindDefaultTag))
                {
                    productTypeList = getProductTypeCodeByDefaultTag(strBindDefaultTag);

                }
            }
        }

        IData productTypeData = new DataMap();
        String productTypeCode = "";

        if (IDataUtil.isNotEmpty(productTypeList))
        {
            for (int i = 0; i < productTypeList.size(); i++)
            {
                productTypeData = productTypeList.getData(i);
                productTypeCode = productTypeData.getString("PRODUCT_TYPE_CODE");
                if (productTypeCode.indexOf("0500") >= 0 || productTypeCode.indexOf("0600") >= 0 || productTypeCode.indexOf("0700") >= 0 || productTypeCode.indexOf("0800") >= 0 || productTypeCode.indexOf("TEL1") >= 0)
                {
                    productTypeList.remove(i);
                    i--;
                }
            }
        }

        returnData.put("PRODUCT_TYPE_LIST", productTypeList);
        return returnData;
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
     * @Function: InitPara
     * @Description: 暂时不用，
     * @param data
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: wangbo3
     * @date: 2015-4-3 上午10:57:03
     */
    public IData InitPara(IData data) throws Exception
    {
        IData returnData = new DataMap();

        IData rDualInfo;
        String strTagInfo = "";
        String strTagChar = "";
        String strTagNumber = "";
        String strOpenType = data.getString("OPEN_TYPE", ""); // 是否代理商开户，通过地址栏参数
        String strEparchyCode = CSBizBean.getTradeEparchyCode();

        // 设置开户方式 OPEN_TYPE
        returnData.put("OPEN_TYPE", strOpenType);

        // 获取开户限制标记
        rDualInfo = getTagInfo(strEparchyCode, "CS_TAG_OPEN_LIMIT", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("OPEN_LIMIT_TAG", "1".equals(strTagChar) ? true : false);

        // 获取默认开户用户数
        rDualInfo = getTagInfo(strEparchyCode, "CS_NUM_OPENLIMITCOUNT", "0");
        strTagNumber = rDualInfo.getString("TAG_NUMBER", "0");
        returnData.put("OPEN_LIMIT_COUNT", strTagNumber);

        // 获取客户名称限制标志
        rDualInfo = getTagInfo(strEparchyCode, "CS_CUSTNAME_LIMIT", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("CUSTNAME_LIMIT", strTagChar);

        // 获取是否显示用户提示信息的标记(用户开户时，仅做新业务推荐用,各省都没有实现)
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_SHOWHINTINFO", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("SHOW_HINT_INFO", strTagChar);

        // 获取是否显示引导信息
        rDualInfo = getTagInfo(strEparchyCode, "CS_SHOWUSERNUM", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("SHOW_USER_NUM_TAG", "1".equals(strTagChar) ? true : false);
        if ("1".equals(strTagChar))
        {
            returnData.put("SHOW_USER_NUM", rDualInfo.getString("TAG_NUMBER", "0"));
        }

        // 获取默认密码的使用方式
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_DEFAULTPWDMODE", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("DEFAULT_PWD_MODE", strTagChar);

        // 获取默认密码
        rDualInfo = getTagInfo(strEparchyCode, "CS_INF_DEFAULTPWD", "0");
        strTagInfo = rDualInfo.getString("TAG_INFO", "");
        returnData.put("DEFAULT_PWD", strTagInfo);

        // 默认密码未变更是否继续(用户开户密码必输，代理商开户默认，还要这个标志何用？)
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_PWDNOCHANGE", "0");
        strTagNumber = rDualInfo.getString("TAG_NUMBER", "");
        rDualInfo.put("PWDNOCHANGE", strTagNumber);

        // 获取密码长度
        rDualInfo = getTagInfo(strEparchyCode, "CS_NUM_PASSWORDLENGTH", "0");
        strTagNumber = rDualInfo.getString("TAG_NUMBER", "");
        returnData.put("DEFAULT_PWD_LENGTH", strTagNumber);

        // 配置是否需要新版本的身份校验,0:不需要,1:需要
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_IDCHKDEALDISMODE", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("ID_CHKDEALDIS_MODE", strTagChar);

        // 是否使用密码键盘 0-否 1-是
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_USEPASSWDKEYBOARD", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("USE_PASSWD_KEYBOARD", strTagChar);

        // 默认用户类型
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_DEFAULTUSERTYPE", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("DEFAULT_USER_TYPE", strTagChar);

        // 默认证件类型
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_DEFAULTPSPTTYPE", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("DEFAULT_PSPT_TYPE", strTagChar);

        // 默认帐户类型
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_DEFAULTPAYMODE", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("DEFAULT_PAY_MODE", strTagChar);

        // 获取黑名单提示方式标记（0-不允许办理[默认]，1-提示是否继续）
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_BLACKCHECKMODE", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("CHR_BLACKCHECKMODE", strTagChar);

        // 获取是否根据证件号码判断欠费标记(默认为不按证件号码判欠费)
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_CHECKOWEFEEBYPSPT", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        strTagNumber = rDualInfo.getString("TAG_NUMBER", "");
        returnData.put("CHR_CHECKOWEFEEBYPSPT", "1".equals(strTagChar) ? true : false);
        returnData.put("CHR_CHECKOWEFEEBYPSPT_ALLUSER", "1".equals(strTagNumber) ? true : false);

        // 代理商开户,代理商远程写卡开户
        if ("AGENT_OPEN".equals(strOpenType) || "REMOTE_AGENT_OPEN".equals(strOpenType))
        {
            // 代理商开户是否只使用操作员部门(登录员工部门)进行资料校验：0,不使用登录员工部门,根据选择代理商,1,仅使用操作员工部门
            rDualInfo = getTagInfo(strEparchyCode, "CS_RESCHECK_BYDEPART", "0");
            strTagChar = rDualInfo.getString("TAG_CHAR", "0");
            returnData.put("RES_CHECK_BY_DEPART", strTagChar);

            // 代理商开户是否设置用户密码,1:不用输入0:输入
            rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_AUTOPASSWD", "0");
            strTagChar = rDualInfo.getString("TAG_CHAR", "1");
            returnData.put("CHR_AUTO_PASSWD", strTagChar);
        }

        // 省内异地开户(远程写卡)
        if ("PROV_REMOTE_OPEN".equals(strOpenType))
        {
            // 获取省内跨区开户默认预存款标记
            rDualInfo = getTagInfo(strEparchyCode, "CS_NUM_PROVOPENADVANCEPAY", "0");
            strTagChar = rDualInfo.getString("TAG_CHAR", "");
            if ("1".equals(strTagChar))
            {
                returnData.put("PROV_OPEN_ADVANCE_PAY_FLAG", true);// 预存款标记
                returnData.put("PROV_OPEN_ADVANCE_PAY", rDualInfo.getString("TAG_NUMBER", "0"));
            }

            // 获取省内跨区开户默认卡费标记
            rDualInfo = getTagInfo(strEparchyCode, "CS_NUM_PROVOPENOPERFEE", "0");
            strTagChar = rDualInfo.getString("TAG_CHAR", "");
            if ("1".equals(strTagChar))
            {
                returnData.put("PROV_OPEN_OPERFEE_FLAG", true);
                returnData.put("PROV_OPEN_OPERFEE", rDualInfo.getString("TAG_NUMBER", "0"));
            }
        }

        // 从标记类中获取业务办理是否可以同时输入刮刮卡号标志
        /**
         * rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_USEGGCARD", "0"); strTagChar = rDualInfo.getString("TAG_CHAR",
         * ""); returnData.put("CHR_USEGGCARD", strTagChar);
         **/

        // 开户是否支持邮寄（0-支持[默认]，1-不支持）
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_OPENPOSTINFO", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "0");
        returnData.put("CHR_OPENPOSTINFO", strTagChar);

        // 是否已检查号码选占数据
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_CHECKSELENUM", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "0");
        returnData.put("CHR_CHECKSELENUM", strTagChar);

        return returnData;
    }

    public IData checkCustQueryNo(IData param, Pagination pagination) throws Exception
    {
        IData returnDatas = new DataMap();
        IDataset ajaxReturnData = new DatasetList();
        IData ajaxData = new DataMap();
        boolean isBlack = UCustBlackInfoQry.isBlackCust(param.getString("PSPT_TYPE_CODE"), param.getString("PSPT_ID"));
        if (isBlack)
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "对不起，您输入的证件类型和证件号码对应的客户为黑名单客户!");
        }

        IDataset custInfos = CustomerInfoQry.getCustInfoByPspt(param.getString("PSPT_TYPE_CODE"), param.getString("PSPT_ID"));
        String cityCode = "";
        int iOnlineNum = 0;// 在网用户数
        String oweFeeSerialNumber = "";// 欠费号码
        boolean isExistsOweFeeFlag = false;
        String message = "";
        if (custInfos.size() > 0)
        {
            cityCode = custInfos.getData(0).getString("CITY_CODE");
            for (int i = 0; i < custInfos.size(); i++)
            {
                IData custInfo = custInfos.getData(0);
                String custType = StaticUtil.getStaticValue("CUST_TYPE", custInfo.getString("CUST_TYPE"));
                String removeTag = StaticUtil.getStaticValue("REMOVE_TAG", custInfo.getString("REMOVE_TAG"));
                String ciCode = StaticUtil.getStaticValue("TD_M_CUSTCITY", custInfo.getString("CITY_CODE"));
                String rsrvStr1 = StaticUtil.getStaticValue("TD_M_CUSTCITY", custInfo.getString("RSRV_STR1"));
                custInfo.put("CUST_TYPE_NAME", custType);
                custInfo.put("REMOVE_TAG_NAME", removeTag);
                custInfo.put("CITY_CODE_NAME", ciCode);
                custInfo.put("RSRV_STR1_NAME", rsrvStr1);
                IDataset personResult = IDataUtil.idToIds(UcaInfoQry.qryPerInfoByCustId(custInfos.getData(i).getString("CUST_ID")));
                if (personResult.size() > 0)
                {
                    for (int n = 0; n < personResult.size(); n++)
                        custInfos.getData(i).putAll(personResult.getData(n));
                }
                else
                {
                    custInfos.remove(i);
                    i--;
                }
            }

            if (custInfos == null || custInfos.size() < 1)
            {
                CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "对不起，根据您输入的证件类型和证件号码获取客户档案资料无数据!");
            }
            else
            {
                custInfos.getData(0).put("CITY_CODE", cityCode);
                for (int i = 0; i < custInfos.size(); i++)
                {
                    IDataset userList = UserInfoQry.getAllNormalUserInfoByCustId(custInfos.getData(i).getString("CUST_ID"));
                    if (userList.size() > 0)
                    {
                        iOnlineNum = iOnlineNum + userList.size();

                        for (int j = 0; j < userList.size(); j++)
                        {
                            String userID = userList.getData(j).getString("USER_ID");
                            IData userOweFee = AcctCall.getOweFeeByUserId(userID);
                            if (IDataUtil.isNotEmpty(userOweFee))
                            {
                                double oweFee1 = userOweFee.getDouble("LAST_OWE_FEE");// 往月欠费
                                double oweFee2 = userOweFee.getDouble("LEAVE_REAL_FEE");// 实时欠费
                                if (oweFee2 < 0)
                                {
                                    isExistsOweFeeFlag = true;
                                    String strFee = String.valueOf((oweFee2) / 100);
                                    message = message + "\n号码【" + userList.getData(j).getString("SERIAL_NUMBER") + "】有实时欠费：" + strFee + "元,";
                                }
                            }
                        }
                    }
                }
                if (iOnlineNum > 0)
                {
                    message = "当前用户证件下共有在网用户【" + iOnlineNum + "】个！" + message;
                }

                ajaxData.put("IS_EXISTS_OWE_FEE_FLAG", isExistsOweFeeFlag);
                ajaxData.put("OWE_CONFIRM_HINT_MSG", message);

                IDataset openUserNumLimitResult = CommparaInfoQry.getCommparaAllCol("CSM", "1891", "0", "0898");
                if (iOnlineNum >= openUserNumLimitResult.getData(0).getInt("PARA_CODE1"))
                {
                    if (param.getString("PSPT_TYPE_CODE").equals("1") || param.getString("PSPT_TYPE_CODE").equals("0") || param.getString("PSPT_TYPE_CODE").equals("A") || param.getString("PSPT_TYPE_CODE").equals("C")
                            || param.getString("PSPT_TYPE_CODE").equals("G"))
                    {
                        ajaxData.put("USER_NUM_LIM", "1");
                        ajaxData.put("NOWCOUNT1", iOnlineNum);
                        ajaxData.put("LIMCOUNT1", openUserNumLimitResult.getData(0).getInt("PARA_CODE1"));
                    }
                    else if (param.getString("PSPT_TYPE_CODE").equals("E") || param.getString("PSPT_TYPE_CODE").equals("K") || param.getString("PSPT_TYPE_CODE").equals("N"))
                    {

                    }
                    else
                    {
                        ajaxData.put("USER_NUM_LIM", "2");
                        ajaxData.put("NOWCOUNT1", iOnlineNum);
                        ajaxData.put("LIMCOUNT1", openUserNumLimitResult.getData(0).getInt("PARA_CODE1"));
                    }
                }
                ajaxData.put("HAS_TAG", "1");
                ajaxData.put("CUST_ID", custInfos.getData(0).getString("CUST_ID"));
                ajaxReturnData.add(ajaxData);
            }
        }
        returnDatas.put("custInfos", custInfos);
        returnDatas.put("ajaxData", ajaxReturnData);
        return returnDatas;
    }

    public IDataset checkAccQueryNo(IData param, Pagination pagination) throws Exception
    {
        IDataset ajaxReturnData = new DatasetList();
        IData ajaxData = new DataMap();
        IDataset userInfo = UserInfoQry.getUserInfoBySerailNumber("0", param.getString("accquery_SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "根据号码:" + param.getString("accquery_SERIAL_NUMBER") + "查询合户用户信息不存在!");
        }
        String accquery_user_id = userInfo.getData(0).getString("USER_ID");

        if (getVisit().getProvinceCode().equals("TJIN"))
        {
            IData userOweFee = AcctCall.getOweFeeByUserId(accquery_user_id);
            double oweFee1 = 0.00;// 往月欠费
            double oweFee2 = 0.00;// 实时欠费
            if (userOweFee.size() > 0)
            {
                oweFee1 = userOweFee.getDouble("LAST_OWE_FEE");
                oweFee2 = userOweFee.getDouble("REAL_FEE");
                if (oweFee1 > 0)
                {
                    String strFee = String.valueOf(((float) oweFee1) / 100);
                    String strOweFeeConfirmHintMsg = "该用户有往月欠费【" + strFee + "】元。";
                    ajaxData.put("IS_EXISTS_OWE_FEE_FLAG", true);
                    ajaxData.put("OWE_CONFIRM_HINT_MSG", strOweFeeConfirmHintMsg);// 欠费信息提示
                }
                else if (oweFee1 <= 0 && oweFee2 > 0)
                {
                    String strFee = String.valueOf(((float) oweFee2) / 100);
                    String strOweFeeConfirmHintMsg = "该用户有实时欠费【" + strFee + "】元。";
                    ajaxData.put("IS_EXISTS_OWE_FEE_FLAG", true);
                    ajaxData.put("OWE_CONFIRM_HINT_MSG", strOweFeeConfirmHintMsg);// 欠费信息提示
                }

            }

            ajaxReturnData.add(ajaxData);
        }

        return ajaxReturnData;
    }

    public IData queryCustInfoByCustId(IData param) throws Exception
    {
        IData resultData = new DataMap();
        IDataset personResult = IDataUtil.idToIds(UcaInfoQry.qryPerInfoByCustId(param.getString("CUST_ID")));
        if (personResult != null && !personResult.isEmpty())
        {
            resultData.putAll(personResult.getData(0));
        }

        IDataset customerResult = CustomerInfoQry.getCustInfoByCustIdOnly(param);
        if (customerResult != null && !customerResult.isEmpty())
        {
            resultData.putAll(customerResult.getData(0));
        }

        return resultData;
    }
    
    public IData queryAuthStaffId(IData param) throws Exception
    {
        IData resultData = new DataMap();
        //查询需要显示授权码输入的员工工号
        IDataset paramIsAuth = CommparaInfoQry.getCommparaInfoByCode("CSM", "173", "IS_AUTH",getVisit().getStaffId(),"0898");
        if (IDataUtil.isNotEmpty(paramIsAuth)) {
        	//配置员工工号
        	resultData.put("IS_AUTH", "1");
        }else{
        	//未配置员工工号
        	resultData.put("IS_AUTH", "0");
        }
        return resultData;
    }

    public IData querySameAcctInfo(IData param, Pagination pagination) throws Exception
    {
        IDataset ajaxReturnData = new DatasetList();
        IData ajaxData = new DataMap();
        IData returnData = new DataMap();
        IDataset result = new DatasetList();

        if (getVisit().getProvinceCode().equals("SHXI"))
        {
            result = TradeNpQry.getAllInfoBySn(param.getString("accquery_SERIAL_NUMBER"), pagination);

            for (int i = 0; i < result.size(); i++)
            {
                IData resultSub = result.getData(i);
                String pspt_type = UPsptTypeInfoQry.getPsptTypeName(resultSub.getString("PSPT_TYPE_CODE"));
                String pay_mode = StaticUtil.getStaticValue("CHNL_ACCT_PAYMODE", resultSub.getString("PAY_MODE_CODE"));
                String bank = UBankInfoQry.getBankNameByBankCode(resultSub.getString("BANK_CODE"));
                resultSub.put("PSPT_TYPE", pspt_type);
                resultSub.put("PAY_MODE", pay_mode);
                resultSub.put("BANK", bank);
                IDataset TD_MIFI_DS = CommparaInfoQry.getCommparaAllCol("CSM", "396", resultSub.getString("PRODUCT_ID", ""), "0898");
                if (TD_MIFI_DS != null && TD_MIFI_DS.size() > 0)
                {
                    result.remove(i);
                }
            }
            if (result.size() < 1)
            {
                CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "获取合户用户信息失败,或目标号码不可合户");
            }
            
    		// 非实名制，不可进行合户
    		if ("80".equals(result.getData(0).getString("USER_DIFF_CODE", ""))) {
    			CSAppException.apperr(BizException.CRM_BIZ_5, "合户用户是非实名制用户，不能办理合户业务!");
    		}
    		// 身份证号码为15位 不可进行合户
    		String pTCode = result.getData(0).getString("PSPT_TYPE_CODE");
    		String psptId = result.getData(0).getString("PSPT_ID");
    		if (psptId.length() == 15 && ("0".equals(pTCode) || "1".equals(pTCode) || "2".equals(pTCode))) {
    			CSAppException.apperr(BizException.CRM_BIZ_5, "合户用户身份证号码为15位，不能办理合户业务!");
    		}
            ajaxData.put("SAME_ACCT_PAY_MODE", result.getData(0).getString("PAY_MODE_CODE"));
            ajaxData.put("SAME_ACCT_PRODUCT_ID", result.getData(0).getString("PRODUCT_ID"));
            ajaxData.put("SAME_ACCT_TAG", "1");
            ajaxData.put("SAME_CUST_TAG", "1");

        }
        IData userInfo = UcaInfoQry.qryUserInfoBySn(param.getString("accquery_SERIAL_NUMBER"));
        IData custInfo = UcaInfoQry.qryCustInfoByCustId(userInfo.getString("CUST_ID"));
        IData acctInfo = UcaInfoQry.qryAcctInfoByUserId(userInfo.getString("USER_ID"));
        acctInfo.put("ACCT_DAY", UcaInfoQry.qryUserAcctDaysByUserId(userInfo.getString("USER_ID")).getData(0).getString("ACCT_DAY", "1"));

        ajaxData.put("CUST_ID", custInfo.getString("CUST_ID"));
        ajaxData.put("ACCT_ID", acctInfo.getString("ACCT_ID"));
        ajaxReturnData.add(ajaxData);

        returnData.put("infos", result);
        returnData.put("custInfo", custInfo);
        returnData.put("acctInfo", acctInfo);
        returnData.put("ajaxData", ajaxReturnData);
        
        return returnData;
    }

	public IData openSaleActiveNpReg(IData param) throws Exception{
		IData returnData = new DataMap();
		IDataset params =  new DatasetList();
		String serialNumber=param.getString("SERIAL_NUMBER","");
		if("".equals(serialNumber)){
			returnData.put("RESULT_CODE", "-1");
			returnData.put("RESULT_INFO", "手机号码不能为空");
			return returnData;			
		}
	    param.put("ACCEPT_DATE", SysDateMgr.getSysTime());
	    param.put("TRADE_STAFF_ID", getVisit().getStaffId());
	    param.put("TRADE_DEPART_ID", getVisit().getDepartId());
		params.add(param);
		Dao.inserts("TF_F_NP_BOOK_SALEACTIVE", params);	
		returnData.put("RESULT_CODE", "0");
		returnData.put("RESULT_INFO", "成功");
		return returnData;
	}
}
