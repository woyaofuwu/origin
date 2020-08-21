
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util;

import java.util.Arrays;
import java.util.List;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlInfo;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserImpuInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.checkmgr.CheckForGrp;

public class GroupImsUtil extends CSBizBean
{
    public static void checkImsProductLimit(String OperType) throws Exception
    {

        String custId = "";// td.getCustId();
        String productId = "";// td.getCurrentProductId();
        String productName = UProductInfoQry.getProductNameByProductId(productId);

        IDataset userResult = UserInfoQry.getUserInfoByCstId(custId, Route.CONN_CRM_CG, null);

        if ("GrpUserOpen".equals(OperType))
        {// 集团产品受理
            IDataset userInfo = DataHelper.filter(userResult, "PRODUCT_ID=2222");
            if (userInfo.size() < 1)
            {
                CSAppException.apperr(GrpException.CRM_GRP_46, productName);
            }
        }
        if ("GrpUserDestory".equals(OperType))
        {
            IDataset vpnUserInfo = DataHelper.filter(userResult, "PRODUCT_ID=8000");
            IDataset telUserInfo = DataHelper.filter(userResult, "PRODUCT_ID=6130");
            IDataset yhtUserInfo = DataHelper.filter(userResult, "PRODUCT_ID=8016");
            if (vpnUserInfo.size() > 0)
            {
                IData userInfo = (IData) vpnUserInfo.get(0);
                String userId = userInfo.getString("USER_ID");
                IData param = new DataMap();
                param.put("USER_ID", userId);
                IDataset vpnresult = UserVpnInfoQry.qryUserVpnByUserId(userId);
                IDataset vpnData = DataHelper.filter(vpnresult, "VPN_USER_CODE=2");// 2为融合V网
                if (vpnData.size() < 1)
                {
                    CSAppException.apperr(GrpException.CRM_GRP_429);
                }

            }
            if (telUserInfo.size() > 0)
            {
                CSAppException.apperr(GrpException.CRM_GRP_80);
            }
            if (yhtUserInfo.size() > 0)
            {
                CSAppException.apperr(GrpException.CRM_GRP_377);
            }
        }
    }

    public static boolean checkImsShortCode(IData data) throws Exception
    {
        String shortCode = data.getString("SHORT_CODE");
        if ("".equals(shortCode))
        {
            data.put("ERROR_MESSAGE", "短号码不能为空！");
            return false;
        }

        if (!"6".equals(shortCode.substring(0, 1)))
        {
            data.put("ERROR_MESSAGE", "短号码第一位必须为6，请重新选择短号！");
            return false;
        }

        if ("60".equals(shortCode.substring(0, 2)))
        {
            data.put("ERROR_MESSAGE", "短号码不能以60开头，请重新选择短号！");
            return false;
        }

        if ("61860".equals(shortCode))
        {
            data.put("ERROR_MESSAGE", "该短号与系统服务号码相同，请重新选择短号！");
            return false;
        }

        if (shortCode.length() > 6)
        {
            data.put("ERROR_MESSAGE", "该短号长度超过设定的短号长度6,请重新输入短号！");
            return false;
        }

        boolean flag = true;
        flag = selImsShortCode(data);
        if (!flag)
        {
            data.put("ERROR_MESSAGE", "该短号已经被使用！");
        }

        return flag;
    }

    /**
     * 判断付费计划
     * 
     * @author tengg 2011-3-18
     * @param td
     * @throws Exception
     */
    public static void checkPayPlan(IData data) throws Exception
    {

        String serialNumber = null;// j2eely reqData.getUca().getUser().getString("SERIAL_NUMBER");
        boolean operFlag = RouteInfoQry.isChinaMobile(serialNumber);
        IDataset payRels = null;// hy td.getPayItemDesign();
        String payTypeCode = "";
        if (payRels != null)
        {
            for (int i = 0, sz = payRels.size(); i < sz; i++)
            {
                IData payRel = (IData) payRels.get(i);
                if ("ADD".equals(payRel.getString("MODIFY_TAG")))
                {
                    payTypeCode = payRel.getString("PLAN_TYPE", "");
                }
            }
        }
        if (operFlag)
        {
            if (!"P".equals(payTypeCode))
            {
                CSAppException.apperr(GrpException.CRM_GRP_405);
            }
        }
        else
        {
            if (!"U".equals(payTypeCode))
            {
                CSAppException.apperr(GrpException.CRM_GRP_430);
            }
        }
    }

    /**
     * 多媒体短号验证
     * 
     * @author:tengg
     * @param data
     * @return
     * @throws Exception
     */
    public static boolean checkShortCode(IData data) throws Exception
    {

        IData idata = new DataMap();
        IDataset dataset = new DatasetList();
        idata.put("USER_ID", data.getString("USER_ID", ""));
        IData userinfos = UcaInfoQry.qryUserMainProdInfoByUserId(data.getString("USER_ID", ""));
        if (IDataUtil.isEmpty(userinfos))
        {
            CSAppException.apperr(GrpException.CRM_GRP_112, data.getString("USER_ID", ""));
        }
        idata.put("PRODUCT_ID", userinfos.getString("PRODUCT_ID", ""));
        dataset.add(idata);
        /*
         * IData params = new DataMap(); params.put("CUST_ID", userinfos.getData(0).getString("CUST_ID","")); IDataset
         * relationList = UserInfoQry.getUserInfoByCstId(params,BaseConnMgr.GRP_DB,null); for(int i=
         * 0;i<relationList.size();i++){ IData relation = relationList.getData(i); String productId =
         * relation.getString("PRODUCT_ID",""); IData param = new DataMap(); param.put("USER_ID",
         * relation.getString("USER_ID")); IDataset vpnresult = UserVpnInfoQry.getUserVpn(param); String userVpnCode
         * =""; if(null != vpnresult && vpnresult.size() > 0){ IData vpndata = vpnresult.getData(0); userVpnCode =
         * vpndata.getString("VPN_USER_CODE"); if((productId.equals("8000") || productId.equals("2222")) &&
         * "2".equals(userVpnCode)){ param.put("PRODUCT_ID",productId); dataset.add(param); } } }
         */
        String shortCode = data.getString("SHORT_CODE");
        String userId = data.getString("USER_ID");
        // int shortcodelen = UserGrpQry.getShortCodeLenByUserId( userId);
        if ("".equals(shortCode))
        {
            data.put("ERROR_MESSAGE", "短号码不能为空！");
            return false;
        }

        if (shortCode.length() > 5)
        {
            data.put("ERROR_MESSAGE", "多媒体桌面电话短号不能超过5位，请重新选择短号！");
            return false;
        }
        if (!"6".equals(shortCode.substring(0, 1)))
        {
            data.put("ERROR_MESSAGE", "短号码第一位必须为6，请重新选择短号！");
            return false;
        }
        if ("60".equals(shortCode.substring(0, 2)))
        {
            data.put("ERROR_MESSAGE", "短号码不能以60开头，请重新选择短号！");
            return false;
        }
        if ("61860".equals(shortCode))
        {
            data.put("ERROR_MESSAGE", "该短号与系统服务号码相同，请重新选择短号！");
            return false;
        }
        // if (shortCode.length() > shortcodelen)
        // {
        // data.put("ERROR_MESSAGE", "该短号长度超过集团所设定的短号长度【" + shortcodelen +
        // "】,请重新输入短号，或者修改集团短号长度！");
        // return false;
        // }
        boolean flag = true;
        for (int i = 0, sz = dataset.size(); i < sz; i++)
        {
            IData paran = (IData) dataset.get(i);
            data.putAll(paran);
            flag = shortCodeValidateVpn(data);
            if (!flag)
            {
                return false;
            }
        }
        return flag;
    }

    /**
     * 融合V网短号验证
     * 
     * @author:tengg
     * @param data
     * @return
     * @throws Exception
     */
    public static boolean checkShortCodeForVnet(IData data) throws Exception
    {
        String userId = data.getString("USER_ID", "");
        IData idata = new DataMap();
        IDataset dataset = new DatasetList();
        idata.put("USER_ID", userId);
        IData userinfos = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(data.getString("USER_ID", ""));
        if (IDataUtil.isEmpty(userinfos))
        {
            CSAppException.apperr(GrpException.CRM_GRP_347, userId);
        }
        idata.put("PRODUCT_ID", userinfos.getString("PRODUCT_ID", ""));
        dataset.add(idata);

        IDataset relationList = UserInfoQry.getUserInfoByCstId(userinfos.getString("CUST_ID", ""), Route.CONN_CRM_CG, null);

        for (int i = 0, sz = relationList.size(); i < sz; i++)
        {
            IData relation = relationList.getData(i);
            String productId = relation.getString("PRODUCT_ID", "");
            IData param = new DataMap();
            param.put("USER_ID", relation.getString("USER_ID"));
            IDataset vpnresult = UserVpnInfoQry.qryUserVpnByUserId(relation.getString("USER_ID"));
            String userVpnCode = "";
            if (IDataUtil.isNotEmpty(vpnresult))
            {
                IData vpndata = vpnresult.getData(0);
                userVpnCode = vpndata.getString("VPN_USER_CODE");
                if ((productId.equals("8000") || productId.equals("2222")) && "2".equals(userVpnCode))
                {
                    param.put("PRODUCT_ID", productId);
                    dataset.add(param);
                }
            }
        }
        String shortCode = data.getString("SHORT_CODE");
        int shortcodelen = UserVpnInfoQry.getShortCodeLenByUserId(userId);
        if ("".equals(shortCode))
        {
            data.put("ERROR_MESSAGE", "短号码不能为空！");
            return false;
        }

        if (!"6".equals(shortCode.substring(0, 1)))
        {
            data.put("ERROR_MESSAGE", "短号码第一位必须为6，请重新选择短号！");
            return false;
        }

        if ("60".equals(shortCode.substring(0, 2)))
        {
            data.put("ERROR_MESSAGE", "短号码不能以60开头，请重新选择短号！");
            return false;
        }

        if ("61860".equals(shortCode))
        {
            data.put("ERROR_MESSAGE", "该短号与系统服务号码相同，请重新选择短号！");
            return false;
        }

        if (shortCode.length() > shortcodelen)
        {
            data.put("ERROR_MESSAGE", "该短号长度超过集团所设定的短号长度【" + shortcodelen + "】,请重新输入短号，或者修改集团短号长度！");
            return false;
        }
        boolean flag = true;
        for (int i = 0, size = dataset.size(); i < size; i++)
        {
            IData paran = (IData) dataset.get(i);
            data.putAll(paran);
            flag = shortCodeValidateVpn(data);
            if (!flag)
            {
                return false;
            }
        }
        return flag;
    }

    /**
     * 处理融合V网，普通V网公用同一个产品ID时，不同V网跳转不同产品参数页面,V网类型已知的情况下
     * 
     * @description
     * @author hud
     * @throws Exception
     */
    public static void dealProductCtrlInfo(BizCtrlInfo ctrlInfo, String type) throws Exception
    {

        IData IfCentreType = ctrlInfo.getCtrlData("IfCentreType");// 是否IMS产品和老产品共用同一个产品标识

        if (IfCentreType != null)
        {
            if ("2".equals(type))
            {
                IData centrParainfo = ctrlInfo.getCtrlData("CentreParamInfo");

                if (IDataUtil.isNotEmpty(centrParainfo))
                {
                    // 修改个性化参数页面
                    centrParainfo.put("ATTR_CODE", "ParamInfo");
                    ctrlInfo.getCtrlInfo().put("ParamInfo", centrParainfo);
                }

                IData temp = ctrlInfo.getCtrlData("CenCreateClass");
                if (IDataUtil.isNotEmpty(temp))
                {
                    String crtClass = temp.getString("ATTR_VALUE");
                    ctrlInfo.getCtrlData("CreateClass").put("ATTR_VALUE", crtClass);
                }
            }
        }

    }

    /**
     * 判断当前用户是否为
     * 
     * @description
     * @author hud
     * @throws Exception
     */
    public static String dealProductCtrlInfoByUser(BizCtrlInfo ctrlInfo, IData userInfo) throws Exception
    {

        IData IfCentreType = ctrlInfo.getCtrlData("IfCentreType");// 是否IMS产品和老产品共用同一个产品标识

        if (IfCentreType == null)
            return null;

        String productId = userInfo.getString("PRODUCT_ID", "");
        String type = null;
        if (productId.equals("8000"))
        {
            String userId = userInfo.getString("USER_ID", "");
            IDataset vpninfos = UserVpnInfoQry.qryUserVpnByUserId(userId);
            if (IDataUtil.isNotEmpty(vpninfos))
            {
                IData vpnInfo = (IData) vpninfos.get(0);
                type = vpnInfo.getString("VPN_USER_CODE", "0");
            }
        }

        dealProductCtrlInfo(ctrlInfo, type);

        return type;
    }

    /**
     * 生成发送HSS新的IFC内容 捞取现有成员用户已经订购的需发送HSS的产品列表，将产品id以升序排序，用;号隔开，
     * 如果操作类型传入ADD操作，已订购product串做param_code，输入函数的product_id做para_code1，
     * 查询td_s_commpara，得para_code3返回。如果操作类型传入DEL操作，已订购product串做para_code2，
     * 输入函数的product_id做para_code1，查询td_s_commpara，得para_code4返回。
     * 
     * @author zhouwei9 Created on 2011-9-7
     * @param mebUserId
     *            成员用户ID
     * @param product_id
     *            需操作的产品ID
     * @param operCode
     *            操作类型（固定值 订购时传’ADD’ 退订是传’DEL’）
     * @param oldProductIds
     *            用户已订购产品id,用“;”分隔
     * @return ifc取值
     * @throws Exception
     */
    public static String genHSSifc(String mebUserId, String mebProductId, String operCode, String oldProductIds) throws Exception
    {

        String ifcStr = "";

        if (CSBaseConst.TRADE_MODIFY_TAG.Add.getValue().equals(operCode))
        {
            // 如果操作类型传入ADD操作，已订购product串做param_code，输入函数的product_id做para_code1，查询td_s_commpara，得para_code4返回
            ifcStr = getHSSConfigValue(mebUserId, mebProductId, operCode, oldProductIds, "PARA_CODE4");

        }
        else if (CSBaseConst.TRADE_MODIFY_TAG.DEL.getValue().equals(operCode))
        {
            // 如果操作类型传入DEL操作，已订购product串做para_code2，输入函数的product_id做para_code1，查询td_s_commpara，得para_code3返回。
            ifcStr = getHSSConfigValue(mebUserId, mebProductId, operCode, oldProductIds, "PARA_CODE3");
        }
        return ifcStr;
    }

    /**
     * 获取给HSS发指令的操作编码 如用户已经在HSS上有资料，新增某个IMS业务产品成员时需给HSS发 08 修改操作码
     * 
     * @author zhouwei9 Created on 2011-9-7
     * @param mebUserId
     *            成员用户ID
     * @param product_id
     *            需操作的产品ID
     * @param operCode
     *            操作类型（固定值 订购时传’ADD’ 退订是传’DEL’）
     * @param oldProductIds
     *            用户已订购产品id,用“;”分隔
     * @return 模板取值
     * @throws Exception
     */
    public static String genHSSOperCode(String mebUserId, String mebProductId, String operCode) throws Exception
    {

        String operCodeStr = "08";
        String userType = getUserTypeByIMSProductId(mebProductId);
        IDataset orderProducts = getUserOrderIMSProduct(mebUserId, userType);

        if (CSBaseConst.TRADE_MODIFY_TAG.Add.getValue().equals(operCode))
        {
            if (orderProducts.size() == 0)
            {
                operCodeStr = "0"; // 新增
            }

        }
        else if (CSBaseConst.TRADE_MODIFY_TAG.DEL.getValue().equals(operCode))
        {
            if (orderProducts.size() == 1)
            {
                operCodeStr = "1"; // 删除
            }
        }

        return operCodeStr;
    }

    /**
     * 获取当前成员用户已订购的HSS相关产品ID
     * 
     * @author zhouwei9 Created on 2011-9-7
     * @param mebUserId
     *            成员用户ID
     * @param product_id
     *            需操作的产品ID
     * @return 用户已订购产品id,用“;”分隔
     * @throws Exception
     */
    public static String genHSSProductIds(String mebUserId, String mebProductId) throws Exception
    {

        String userType = getUserTypeByIMSProductId(mebProductId);
        return genHSSProductIdsByUserType(mebUserId, userType);

    }

    /**
     * 获取当前用户已订购的HSS相关产品ID
     * 
     * @param mebUserId
     *            成员用户ID
     * @param userType
     *            用户类型（1：硬终端 2：软终端）
     * @return
     * @throws Exception
     */
    public static String genHSSProductIdsByUserType(String mebUserId, String userType) throws Exception
    {

        IDataset orderProducts = getUserOrderIMSProduct(mebUserId, userType);

        if (IDataUtil.isEmpty(orderProducts))
        {
            return "";
        }
        IDataset tmpProducts = DataHelper.distinct(orderProducts, "PRODUCT_ID", ";"); // 排除重复产品（防止同一用户订购不通集团的同一产品，数据问题？）

        IData orderData = tmpProducts.toData();
        List<String> list = (List<String>) orderData.get("PRODUCT_ID");
        String[] strs = new String[list.size()];
        list.toArray(strs);
        return splitUnion(strs, ";", true); // 修改入参值

    }

    /**
     * 生成发送HSS新的模板内容
     * 
     * @author zhouwei9 Created on 2011-9-7
     * @param mebUserId
     *            成员用户ID
     * @param product_id
     *            需操作的产品ID
     * @param operCode
     *            操作类型（固定值 订购时传’ADD’ 退订是传’DEL’）
     * @param oldProductIds
     *            用户已订购产品id,用“;”分隔
     * @return 模板取值
     * @throws Exception
     */
    public static String genHSStempleID(String mebUserId, String mebProductId, String operCode, String oldProductIds) throws Exception
    {

        String templeIdStr = "";

        if (CSBaseConst.TRADE_MODIFY_TAG.Add.getValue().equals(operCode))
        {
            // 如果操作类型传入ADD操作，已订购product串做param_code，输入函数的product_id做para_code1，查询td_s_commpara，得para_code6返回
            templeIdStr = getHSSConfigValue(mebUserId, mebProductId, operCode, oldProductIds, "PARA_CODE6");

        }
        else if (CSBaseConst.TRADE_MODIFY_TAG.DEL.getValue().equals(operCode))
        {
            // 如果操作类型传入DEL操作，已订购product串做para_code2，输入函数的product_id做para_code1，查询td_s_commpara，得para_code5返回。
            templeIdStr = getHSSConfigValue(mebUserId, mebProductId, operCode, oldProductIds, "PARA_CODE5");
        }

        return templeIdStr;
    }

    /**
     * 生成IMPI
     * 
     * @author tengg
     * @param serialNumber
     * @param strImpi
     * @param telType
     * @throws Exception
     */
    public static void genImsIMPI(String serialNumber, StringBuilder strImpi, String telType) throws Exception
    {

        String provice = getVisit().getProvinceCode();
        IDataset imsDomains = CommparaInfoQry.getCommparaAllCol("CGM", "9980", provice, "ZZZZ");
        String imsDomain = "";
        if (IDataUtil.isEmpty(imsDomains))
        {
            imsDomain = "@ims.hi.chinamobile.com";
        }
        else
        {
            imsDomain = imsDomains.getData(0).getString("PARAM_NAME", "");
        }

        if ("1".equals(telType))
        { // 固定终端（SIP硬终端或POTS话机）
            strImpi.append(serialNumber).append(imsDomain);
        }
        if ("2".equals(telType))
        { // 无卡PC客户端
            strImpi.append(serialNumber).append("_s").append(imsDomain);
        }
        if ("3".equals(telType))
        { // 签约IMS的CS手机（如签约一号通）
            strImpi.append(serialNumber).append(imsDomain);
        }
    }

    /**
     * 生成IMPU
     * 
     * @author tengg
     * @param serialNumber
     * @param strTel
     * @param strSip
     * @param telType
     * @throws Exception
     */
    public static void genImsIMPU(String serialNumber, StringBuilder strTel, StringBuilder strSip, String telType) throws Exception
    {

        String provice = getVisit().getProvinceCode();
        IDataset imsDomains = CommparaInfoQry.getCommparaAllCol("CGM", "9980", provice, "ZZZZ");
        String imsDomain = "";
        if (IDataUtil.isEmpty(imsDomains))
        {
            imsDomain = "@ims.hi.chinamobile.com";
        }
        else
        {
            imsDomain = imsDomains.getData(0).getString("PARAM_NAME", "");
        }

        if ("1".equals(telType))
        { // 固定终端（SIP硬终端或POTS话机）
            strTel.append(serialNumber);
            strSip.append(serialNumber).append(imsDomain);
        }
        if ("2".equals(telType))
        { // 无卡PC客户端
            strTel.append(serialNumber);
            strSip.append(serialNumber).append("_s").append(imsDomain);
        }
        if ("3".equals(telType))
        { // 签约IMS的CS手机（如签约一号通）
            strTel.append(serialNumber);
            strSip.append(serialNumber).append(imsDomain);
        }
    }

    /**
     * 获取配置的参数，根据PARA_CODE1进行IMS业务产品id过滤，PARA_CODE2进行IMS业务成员关系类型过滤
     * 
     * @param pd
     * @return params
     * @throws Exception
     */
    public static IDataset getConfigProductParam() throws Exception
    {
        IDataset params = CommparaInfoQry.getCommparaAllCol("CGM", "9981", "0", "ZZZZ");
        return params;
    }

    /**
     * 判断是否需要创建成员
     * 
     * @param pd
     *            ,commData
     * @return crtFlag
     * @throws Exception
     */
    public static boolean getCreateMebFlag(String cust_id, String user_id_b) throws Exception
    {
        boolean crtFlag = true; // 判断是否还有订购的ims业务，如果没有则需要发创建成员报文到Centrex平台

        IDataset imsProParams = GroupImsUtil.getImsProductParam(); // 获取现有的ims业务编码和关系类型编码

        IDataset relationList = UserInfoQry.getRelaUserInfoByCstId(cust_id, null, user_id_b, null);
        if (IDataUtil.isNotEmpty(relationList))
        {
            For1: for (int i = 0, sz = relationList.size(); i < sz; i++)
            {
                IData relation = relationList.getData(i);
                String productId = relation.getString("PRODUCT_ID", ""); // 成员订购的集团产品id
                if (IDataUtil.isNotEmpty(imsProParams))
                {
                    for (int j = 0, size = imsProParams.size(); j < size; j++)
                    {
                        IData data = imsProParams.getData(j);
                        String imsProductId = data.getString("PRODUCT_ID", ""); // ims产品id
                        if (productId.equals(imsProductId))
                        {
                            crtFlag = false;
                            break For1;
                        }
                    }
                }
            }
        }
        return crtFlag;
    }

    /**
     * 判断是否需要删除成员
     * 
     * @param pd
     *            ,commData
     * @return delflag
     * @throws Exception
     */
    public static boolean getDelMebFlag(String cust_id, String user_id_b) throws Exception
    {
        boolean crtFlag = true; // 判断是否还有订购的ims业务，如果没有则需要发删除成员报文到Centrex平台
        IDataset datas = GroupImsUtil.getImsProductParam(); // 获取现有的ims业务编码和关系类型编码

        int num = 0;
        IDataset relationList = UserInfoQry.getRelaUserInfoByCstId(cust_id, null, user_id_b, null);
        if (IDataUtil.isNotEmpty(relationList))
        {
            For1: for (int i = 0, sz = relationList.size(); i < sz; i++)
            {
                IData relation = relationList.getData(i);
                String productId = relation.getString("PRODUCT_ID", ""); // 成员订购的集团产品id
                if (IDataUtil.isNotEmpty(datas))
                {
                    for (int j = 0, size = datas.size(); j < size; j++)
                    {
                        IData data = datas.getData(j);
                        String imsProductId = data.getString("PRODUCT_ID", ""); // ims产品id
                        if (productId.equals(imsProductId) && !productId.equals("8000"))
                        {
                            num++;
                            if (num > 1)
                            { // 有两个ims产品订购关系了
                                crtFlag = false;
                                break For1;
                            }
                        }

                        if (productId.equals(imsProductId) && productId.equals("8000"))
                        {
                            String userId = relation.getString("USER_ID_A", "");
                            IData idata = new DataMap();
                            idata.put("USER_ID", userId);
                            IDataset userattrs = UserVpnInfoQry.qryUserVpnByUserId(userId);

                            if (IDataUtil.isNotEmpty(userattrs))
                            {
                                if (userattrs.getData(0).getString("VPN_USER_CODE", "0").equals("2"))
                                {
                                    num++;
                                    if (num > 1)
                                    { // 有两个ims产品订购关系了
                                        crtFlag = false;
                                        break For1;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return crtFlag;
    }

    /**
     * 获取修改操作时HSS ifc templeId 配置数据
     * 
     * @author zhouwei9 Created on 2011-9-14
     * @param mebProductId
     *            成员产品ID
     * @param oldProductIds
     *            用户已订购产品id,用“;”分隔
     * @return 查询结果集第一条记录 para_code4为结果ifc信息 para_code6为结果模板元素发送信息
     * @throws Exception
     */
    public static IData getHSSConfigData4Modi(String mebProductId, String oldProductIds) throws Exception
    {

        String userType = getUserTypeByIMSProductId(mebProductId);
        return getHSSConfigData4ModiByUserType(userType, oldProductIds);
    }

    /**
     * 获取修改操作时HSS ifc templeId 配置数据
     * 
     * @author zhouwei9 Created on 2011-12-12
     * @param userType
     * @param oldProductIds
     * @return
     * @throws Exception
     */
    public static IData getHSSConfigData4ModiByUserType(String userType, String oldProductIds) throws Exception
    {

        String paramAttr = "3501";
        if ("2".equals(userType))
        {
            paramAttr = "3502";
        }

        IData inparam = new DataMap();
        inparam.put("PARAM_ATTR", paramAttr);
        inparam.put("PARA_CODE2", oldProductIds); // para_code2为加入新产品后，形成的订购串product_id
        inparam.put("SUBSYS_CODE", "CSM");

        IDataset queryInfos = ParamInfoQry.getHSSConfigData4ModiByUserType(inparam);
        if (IDataUtil.isEmpty(queryInfos))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_297, paramAttr, oldProductIds);
        }
        return queryInfos.getData(0);
    }

    /**
     * 获取HSS ifc templeId 配置数据
     * 
     * @author zhouwei9 Created on 2011-9-7
     * @param mebUserId
     *            成员用户ID
     * @param product_id
     *            需操作的产品ID
     * @param operCode
     *            操作类型（固定值 订购时传’ADD’ 退订是传’DEL’）
     * @param oldProductIds
     *            用户已订购产品id,用“;”分隔
     * @return 模板取值
     * @throws Exception
     */
    public static String getHSSConfigValue(String mebUserId, String mebProductId, String operCode, String oldProductIds, String paraCode) throws Exception
    {

        String paraValue = null;
        String userType = getUserTypeByIMSProductId(mebProductId);

        String paramAttr = "3501";
        if ("2".equals(userType))
        {
            paramAttr = "3502";
        }
        oldProductIds = "".equals(oldProductIds) ? "begin" : oldProductIds; // 未订购产品时需设置为“begin”;

        if (CSBaseConst.TRADE_MODIFY_TAG.Add.getValue().equals(operCode))
        {
            // 如果操作类型传入ADD操作，已订购product串做param_code，输入函数的product_id做para_code1，查询td_s_commpara，得para_code6返回
            paraValue = StaticUtil.getStaticValue(getVisit(), "TD_S_COMMPARA", new String[]
            { "PARAM_ATTR", "PARAM_CODE", "PARA_CODE1" }, paraCode, new String[]
            { paramAttr, oldProductIds, mebProductId });
            if (StringUtils.isBlank(paraValue))
            {
                CSAppException.apperr(ParamException.CRM_PARAM_295, paramAttr, oldProductIds, mebProductId);
            }

        }
        else if (CSBaseConst.TRADE_MODIFY_TAG.DEL.getValue().equals(operCode))
        {
            // 如果操作类型传入DEL操作，已订购product串做para_code2，输入函数的product_id做para_code1，查询td_s_commpara，得para_code5返回。
            paraValue = StaticUtil.getStaticValue(getVisit(), "TD_S_COMMPARA", new String[]
            { "PARAM_ATTR", "PARA_CODE2", "PARA_CODE1" }, paraCode, new String[]
            { paramAttr, oldProductIds, mebProductId });
            if (StringUtils.isBlank(paraValue))
            {
                CSAppException.apperr(ParamException.CRM_PARAM_296, paramAttr, oldProductIds, mebProductId);
            }
            paraValue = "begin".equals(paraValue) ? "" : paraValue; // “begin”值需要转换为"";
        }

        return paraValue;
    }

    /**
     * 获取impu表扩展字段4字符串
     * 
     * @param userId
     * @param userType
     *            用户类型0:固定终端；1：移动电话
     * @param index
     *            索引，0：成员角色 1：短号
     * @return rstr
     * @throws Exception
     */
    public static String getImpuStr4(String userId, String userType, int index) throws Exception
    {
        String eparchyCode = BizRoute.getRouteId();
        String rstr = "";
        String[] str4s = null;

        if ("4".equals(userType))
        {
            userType = "1";
        }
        IDataset impuInfo = UserImpuInfoQry.queryUserImpuInfoByUserType(userId, userType, eparchyCode);
        if (IDataUtil.isNotEmpty(impuInfo))
        {
            IData impuData = impuInfo.getData(0);
            if (IDataUtil.isNotEmpty(impuData))
            {
                String str4 = impuData.getString("RSRV_STR4", "");
                str4s = str4.split("\\|");
                if (str4s != null && str4s.length > index)
                {
                    rstr = str4s[index];
                }
            }
        }
        return rstr;
    }

    /**
     * 查询号码加入到哪个IMS集团中
     * 
     * @param td
     * @param data
     * @throws Exception
     */

    public static String getIMSGrpCustIdBySN(String userid) throws Exception
    {
        IDataset relationList = UserInfoQry.getRelaUserInfoByCstId(null, null, userid, null);
        String custid = "";
        if (IDataUtil.isNotEmpty(relationList))
        {
            for (int i = 0, sz = relationList.size(); i < sz; i++)
            {
                IData relation = relationList.getData(i);
                String productIdother = relation.getString("PRODUCT_ID", "");
                if (productIdother.equals("8000") || productIdother.equals("8016") || productIdother.equals("6130") || productIdother.equals("2222"))
                {
                    custid = relation.getString("CUST_ID", "");
                    return custid;
                }
            }
        }
        return custid;
    }

    /**
     * 获取配置参数的所有Ims产品id和成员关系类型
     * 
     * @param pd
     * @return dataset
     * @throws Exception
     */
    public static IDataset getImsProductParam() throws Exception
    {
        IDataset dataset = new DatasetList();
        // 获取配置的所有特定的产品参数
        IDataset productParams = getConfigProductParam();
        if (IDataUtil.isNotEmpty(productParams))
        {
            for (int i = 0, sz = productParams.size(); i < sz; i++)
            {
                IData param = productParams.getData(i);
                IData map = new DataMap();
                map.put("PRODUCT_ID", param.getString("PARA_CODE1", ""));
                map.put("RELATION_TYPE_CODE", param.getString("PARA_CODE2", ""));
                dataset.add(map);
            }
        }
        return dataset;
    }

    /**
     * 判断成员是否已订购融合V网
     * 
     * @param cust_id
     * @param user_id_b
     * @return
     * @throws Exception
     */
    public static boolean getMebOrderImsVpn(String cust_id, String user_id_b) throws Exception
    {
        boolean vpnflag = false; // 判断是否有订购融合V网

        IDataset relationList = UserInfoQry.getRelaUserInfoByCstId(cust_id, null, user_id_b, null);
        if (IDataUtil.isNotEmpty(relationList))
        {
            for (int i = 0, size = relationList.size(); i < size; i++)
            {
                IData relation = relationList.getData(i);
                String productId = relation.getString("PRODUCT_ID", "");
                if ("8001".equals(productId))
                {
                    vpnflag = true;
                    break;
                }
            }
        }
        return vpnflag;
    }

    /**
     * 获取指定长度的随机数字序列，长度限制为1-128位
     * 
     * @author lixin5
     */
    public static String getRandomNumAndChar(int length) throws Exception
    {
        if (length < 1 || length > 128)
        {
            CSAppException.apperr(GrpException.CRM_GRP_430);// 请输入1-128的整数！！
        }
        String[] data = new String[]
        { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "1", "2", "3", "4", "6", "7", "8", "9", "0", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };
        StringBuilder sb = new StringBuilder("");
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < length; i++)
        {
            sb.append(data[random.nextInt(78)]);
        }
        return sb.toString();
    }

    /**
     * 作用:根据USER_ID查询用户信息
     * 
     * @author luojh
     * @param userId
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IData getUserInfoByUserId(String userId, String eparchyCode) throws Exception
    {

        IData tempParam = new DataMap();
        tempParam.put("USER_ID", userId);
        String REMOVE_TAG = "0";
        IData grpUserData = UserInfoQry.getGrpUserInfoByUserId(userId, REMOVE_TAG, eparchyCode); // 查询集团用户信息
        if (grpUserData == null || grpUserData.size() < 1)
            CSAppException.apperr(GrpException.CRM_GRP_473);
        return grpUserData;
    }

    /**
     * 捞取现有成员用户已经订购的需发送HSS的产品列表,按集团产品ID升序排列
     * 
     * @author zhouwei9 Created on 2011-9-8
     * @param mebUserId
     * @param userType
     * @return
     * @throws Exception
     */
    public static IDataset getUserOrderIMSProduct(String mebUserId, String userType) throws Exception
    {
        IDataset dataset = new DatasetList();
        IData param = new DataMap();
        param.put("USER_ID", mebUserId);
        IDataset products = ProductInfoQry.getUserOrderIMSProduct(param);

        IDataset staticData = StaticUtil.getStaticList("IMS_USER_TYPE"); // DATA_NAME = userType 对取td_s_static表没用，去掉
        for (int i = 0, len = staticData.size(); i < len; i++)
        {
            IData tmpData = staticData.getData(i);
            String data_id = tmpData.getString("DATA_ID");

            IDataset productDataSet = DataHelper.filter(products, "PRODUCT_ID=" + data_id);
            dataset.addAll(productDataSet);
        }

        return dataset;
    }

    /**
     * 根据IMS成员产品编码查询办理此产品用户的用户类型（1：硬终端 2：软终端）
     * 
     * @author zhouwei9 Created on 2011-9-8
     * @param mebProductId
     * @return
     * @throws Exception
     */
    public static String getUserTypeByIMSProductId(String mebProductId) throws Exception
    {

        String dataName = StaticUtil.getStaticValue("IMS_USER_TYPE", mebProductId);
        return dataName == null ? "1" : dataName;
    }

    public static IDataset ifMofficeTelePhoneUser(IData idata) throws Exception
    {
        String serial_number = idata.getString("SERIAL_NUMBER");
        boolean result = RouteInfoQry.isChinaMobile(serial_number);
        IDataset idataset = new DatasetList();
        IData data = new DataMap();
        data.put("RESULT_FLAG", result);
        idataset.add(data);
        return idataset;
    }

    /**
     * 往OTHER台账表 TF_B_TRADE_OTHER 插入一条记录
     * 
     * @author zhouwei9 Created on 2011-8-26
     * @param inParam
     * @throws Exception
     */
    public static void insertTradeOther(IData map) throws Exception
    {

        IData data = new DataMap();

        // 必传属性
        data.put("TRADE_ID", map.getString("TRADE_ID"));// 业务流水号
        data.put("ACCEPT_MONTH", map.getString("ACCEPT_MONTH", ""));// 受理月份：受理时间的月份，可作为分区字段。正常情况下可从trade_id的第5、6位获得。
        data.put("USER_ID", map.getString("USER_ID", ""));// 集团用户标识
        data.put("RSRV_VALUE_CODE", map.getString("RSRV_VALUE_CODE", "")); // 预留资料编码
        data.put("START_DATE", map.getString("START_DATE", "")); // 开始时间
        data.put("END_DATE", map.getString("END_DATE", "")); // 结束时间
        data.put("MODIFY_TAG", map.getString("MODIFY_TAG", "0"));// 状态属性：0-增加，1-删除，2-变更
        // 可选属性
        data.put("RSRV_VALUE", map.getString("RSRV_VALUE", "")); // 预留资料

        data.put("RSRV_NUM1", map.getString("RSRV_NUM1", "")); // 预留数值1
        data.put("RSRV_NUM2", map.getString("RSRV_NUM2", "")); // 预留数值2
        data.put("RSRV_NUM3", map.getString("RSRV_NUM3", "")); // 预留数值3
        data.put("RSRV_NUM4", map.getString("RSRV_NUM4", "")); // 预留数值4
        data.put("RSRV_NUM5", map.getString("RSRV_NUM5", "")); // 预留数值5
        data.put("RSRV_NUM6", map.getString("RSRV_NUM6", "")); // 预留数值6
        data.put("RSRV_NUM7", map.getString("RSRV_NUM7", "")); // 预留数值7
        data.put("RSRV_NUM8", map.getString("RSRV_NUM8", "")); // 预留数值8
        data.put("RSRV_NUM9", map.getString("RSRV_NUM9", "")); // 预留数值9
        data.put("RSRV_NUM10", map.getString("RSRV_NUM10", "")); // 预留数值10
        data.put("RSRV_NUM11", map.getString("RSRV_NUM11", "")); // 预留数值11
        data.put("RSRV_NUM12", map.getString("RSRV_NUM12", "")); // 预留数值12
        data.put("RSRV_NUM13", map.getString("RSRV_NUM13", "")); // 预留数值13
        data.put("RSRV_NUM14", map.getString("RSRV_NUM14", "")); // 预留数值14
        data.put("RSRV_NUM15", map.getString("RSRV_NUM15", "")); // 预留数值15
        data.put("RSRV_NUM16", map.getString("RSRV_NUM16", "")); // 预留数值16
        data.put("RSRV_NUM17", map.getString("RSRV_NUM17", "")); // 预留数值17
        data.put("RSRV_NUM18", map.getString("RSRV_NUM18", "")); // 预留数值18
        data.put("RSRV_NUM19", map.getString("RSRV_NUM19", "")); // 预留数值19
        data.put("RSRV_NUM20", map.getString("RSRV_NUM20", "")); // 预留数值20
        data.put("RSRV_STR1", map.getString("RSRV_STR1", "")); // 预留字段1
        data.put("RSRV_STR2", map.getString("RSRV_STR2", "")); // 预留字段2
        data.put("RSRV_STR3", map.getString("RSRV_STR3", "")); // 预留字段3
        data.put("RSRV_STR4", map.getString("RSRV_STR4", "")); // 预留字段4
        data.put("RSRV_STR5", map.getString("RSRV_STR5", "")); // 预留字段5
        data.put("RSRV_STR6", map.getString("RSRV_STR6", "")); // 预留字段6
        data.put("RSRV_STR7", map.getString("RSRV_STR7", "")); // 预留字段7
        data.put("RSRV_STR8", map.getString("RSRV_STR8", "")); // 预留字段8
        data.put("RSRV_STR9", map.getString("RSRV_STR9", "")); // 预留字段9
        data.put("RSRV_STR10", map.getString("RSRV_STR10", "")); // 预留字段10
        data.put("RSRV_STR11", map.getString("RSRV_STR11", "")); // 预留字段11
        data.put("RSRV_STR12", map.getString("RSRV_STR12", "")); // 预留字段12
        data.put("RSRV_STR13", map.getString("RSRV_STR13", "")); // 预留字段13
        data.put("RSRV_STR14", map.getString("RSRV_STR14", "")); // 预留字段14
        data.put("RSRV_STR15", map.getString("RSRV_STR15", "")); // 预留字段15
        data.put("RSRV_STR16", map.getString("RSRV_STR16", "")); // 预留字段16
        data.put("RSRV_STR17", map.getString("RSRV_STR17", "")); // 预留字段17
        data.put("RSRV_STR18", map.getString("RSRV_STR18", "")); // 预留字段18
        data.put("RSRV_STR19", map.getString("RSRV_STR19", "")); // 预留字段19
        data.put("RSRV_STR20", map.getString("RSRV_STR20", "")); // 预留字段20
        data.put("RSRV_STR21", map.getString("RSRV_STR21", "")); // 预留字段21
        data.put("RSRV_STR22", map.getString("RSRV_STR22", "")); // 预留字段22
        data.put("RSRV_STR23", map.getString("RSRV_STR23", "")); // 预留字段23
        data.put("RSRV_STR24", map.getString("RSRV_STR24", "")); // 预留字段24
        data.put("RSRV_STR25", map.getString("RSRV_STR25", "")); // 预留字段25
        data.put("RSRV_STR26", map.getString("RSRV_STR26", "")); // 预留字段26
        data.put("RSRV_STR27", map.getString("RSRV_STR27", "")); // 预留字段27
        data.put("RSRV_STR28", map.getString("RSRV_STR28", "")); // 预留字段28
        data.put("RSRV_STR29", map.getString("RSRV_STR29", "")); // 预留字段29
        data.put("RSRV_STR30", map.getString("RSRV_STR30", "")); // 预留字段30
        data.put("RSRV_DATE1", map.getString("RSRV_DATE1", "")); // 预留日期1
        data.put("RSRV_DATE2", map.getString("RSRV_DATE2", "")); // 预留日期2
        data.put("RSRV_DATE3", map.getString("RSRV_DATE3", "")); // 预留日期3
        data.put("RSRV_DATE4", map.getString("RSRV_DATE4", "")); // 预留日期4
        data.put("RSRV_DATE5", map.getString("RSRV_DATE5", "")); // 预留日期5
        data.put("RSRV_DATE6", map.getString("RSRV_DATE6", "")); // 预留日期6
        data.put("RSRV_DATE7", map.getString("RSRV_DATE7", "")); // 预留日期7
        data.put("RSRV_DATE8", map.getString("RSRV_DATE8", "")); // 预留日期8
        data.put("RSRV_DATE9", map.getString("RSRV_DATE9", "")); // 预留日期9
        data.put("RSRV_DATE10", map.getString("RSRV_DATE10", "")); // 预留日期10
        data.put("RSRV_TAG1", map.getString("RSRV_TAG1", "")); // 预留标志1
        data.put("RSRV_TAG2", map.getString("RSRV_TAG2", "")); // 预留标志2
        data.put("RSRV_TAG3", map.getString("RSRV_TAG3", "")); // 预留标志3
        data.put("RSRV_TAG4", map.getString("RSRV_TAG4", "")); // 预留标志4
        data.put("RSRV_TAG5", map.getString("RSRV_TAG5", "")); // 预留标志5
        data.put("RSRV_TAG6", map.getString("RSRV_TAG6", "")); // 预留标志6
        data.put("RSRV_TAG7", map.getString("RSRV_TAG7", "")); // 预留标志7
        data.put("RSRV_TAG8", map.getString("RSRV_TAG8", "")); // 预留标志8
        data.put("RSRV_TAG9", map.getString("RSRV_TAG9", "")); // 预留标志9
        data.put("RSRV_TAG10", map.getString("RSRV_TAG10", "")); // 预留标志10
        data.put("PROCESS_TAG", map.getString("TRADE_TAG", "")); // 处理标志
        data.put("STAFF_ID", map.getString("STAFF_ID", "")); // 受理员工
        data.put("DEPART_ID", map.getString("DEPART_ID", "")); // 受理部门
        data.put("UPDATE_TIME", map.getString("SYS_DATE", "")); // 更新时间
        data.put("UPDATE_STAFF_ID", map.getString("UPDATE_STAFF_ID", "")); // 更新员工
        data.put("UPDATE_DEPART_ID", map.getString("UPDATE_DEPART_ID", "")); // 更新部门
        data.put("REMARK", map.getString("REMARK", "")); // 备注
        data.put("INST_ID", map.getString("INST_ID", SeqMgr.getInstId()));

        Dao.insert("TF_B_TRADE_OTHER", data,Route.getJourDb());
    }

    /**
     * 往VPN台账表 TF_B_TRADE_VPN 插入一条记录
     * 
     * @author zhouwei9 Created on 2011-8-26
     * @param inParam
     * @throws Exception
     */
    public static void insertTradeVpn(IData map) throws Exception
    {

        IData data = new DataMap();
        // 必传属性
        data.put("TRADE_ID", map.getString("TRADE_ID"));// 业务流水号
        data.put("ACCEPT_MONTH", map.getString("ACCEPT_MONTH", ""));// 受理月份：受理时间的月份，可作为分区字段。正常情况下可从trade_id的第5、6位获得。
        data.put("USER_ID", map.getString("USER_ID", ""));// 集团用户标识
        data.put("VPN_NO", map.getString("VPN_NO", ""));// VPN集团号
        data.put("SCP_CODE", map.getString("SCP_CODE", "00"));// SCP代码
        data.put("VPMN_TYPE", map.getString("VPMN_TYPE", "0"));// VPN集团类型：0:本地集团;1:全省集团;2:全国集团;3:本地化省级集
        data.put("MAX_USERS", map.getString("MAX_USERS", ""));// 集团可拥有的最大用户数：正整数，范围[20,100000]，缺省1000。
        data.put("REMOVE_TAG", map.getString("REMOVE_TAG", ""));// 注销标志：0-正常、1-已注销
        data.put("MODIFY_TAG", map.getString("MODIFY_TAG", "0"));// 状态属性：0-增加，1-删除，2-变更

        // 可选属性
        data.put("USER_ID_A", map.getString("USER_ID", ""));// 归属集团标识
        data.put("VPN_NAME", map.getString("CUST_NAME", ""));// 集团名称
        data.put("GROUP_AREA", map.getString("GROUP_AREA"));// 集团所在业务区号
        data.put("VPN_TYPE", map.getString("VPN_TYPE", ""));// VPN类型：0-IVPN
        // 1-WVPN
        data.put("PROVINCE", map.getString("PROVINCE", ""));// 省编码
        data.put("SUB_STATE", map.getString("SUB_STATE", ""));// 业务激活标志：0:未激活1:激活；缺省为0
        data.put("FUNC_TLAGS", map.getString("FUNC_TLAGS", ""));// 集团功能集：40位数字串，缺省1100000000000000000000000001000000000000。
        data.put("FEEINDEX", map.getString("FEEINDEX", ""));// 费率索引
        data.put("INTER_FEEINDEX", map.getString("INTER_FEEINDEX", "-1"));// 网内费率索引：非负整数。
        data.put("OUT_FEEINDEX", map.getString("OUT_FEEINDEX", "-1"));// 非负整数：为任何可用的费率索引
        data.put("OUTGRP_FEEINDEX", map.getString("OUTGRP_FEEINDEX", "-1"));// 非负整数：为任何可用的费率索引
        data.put("SUBGRP_FEEINDEX", map.getString("SUBGRP_FEEINDEX", ""));// 非负整数：各子集团可以不一致。为任何可用的费率索引
        data.put("NOTDISCNT_FEEINDEX", map.getString("NOTDISCNT_FEEINDEX", "-1"));// 非优惠费率索引
        data.put("PRE_IP_NO", map.getString("PRE_IP_NO", ""));// 集团预置IP接入码：1到10位字符串，缺省为17951。
        data.put("PRE_IP_DISC", map.getString("PRE_IP_DISC", ""));// 预置IP计费折扣：0到100的非负整数。缺省为100
        data.put("OTHOR_IP_DISC", map.getString("OTHOR_IP_DISC", ""));// 其他IP计费折扣：0到100的非负整数。缺省为100
        data.put("TRANS_NO", map.getString("TRANS_NO", ""));// 呼叫话务员转接号码：1到18位数字字符串
        data.put("MAX_CLOSE_NUM", map.getString("MAX_CLOSE_NUM", ""));// 最大闭合用户群数：非负整数，缺省为10。
        data.put("MAX_NUM_CLOSE", map.getString("MAX_NUM_CLOSE", ""));// 单个闭合用户群能包含的最大用户数：非负整数，缺省为100
        data.put("PERSON_MAXCLOSE", map.getString("PERSON_MAXCLOSE", ""));// 单个用户最大可加入的闭合群数：非负整数，范围0-5，缺省为1。
        data.put("MAX_OUTGRP_NUM", map.getString("MAX_OUTGRP_NUM", ""));// 最大网外号码组组总数：可选参数，整数类型，最小值为1，缺省为1
        data.put("MAX_OUTGRP_MAX", map.getString("MAX_OUTGRP_MAX", ""));// 每一组网外号码组最大号码数：可选参数，整数类型，最小值为1，缺省为100。
        data.put("MAX_INNER_NUM", map.getString("MAX_INNER_NUM", ""));// 最大网内号码总数
        data.put("MAX_OUTNUM", map.getString("MAX_OUTNUM", ""));// 最大网外号码总数：非负整数，缺省为100。
        data.put("MAX_LINKMAN_NUM", map.getString("MAX_LINKMAN_NUM", ""));// 联络员最大数
        data.put("MAX_TELPHONIST_NUM", map.getString("MAX_TELPHONIST_NUM", ""));// 话务员最大数
        data.put("MAX_LIMIT_USERS", map.getString("MAX_LIMIT_USERS", ""));// 受限用户最大数
        data.put("PKG_START_DATE", map.getString("PKG_START_DATE", ""));// 资费套餐生效日期：
        // 缺省为第二天。必须晚于当前
        data.put("PKG_TYPE", map.getString("PKG_TYPE", ""));// 资费套餐类型：非负整数，0表示没有套餐，缺省为0。
        // 为任何可用的资费套餐类型
        data.put("DISCOUNT", map.getString("DISCOUNT", ""));// 总折扣：非负整数。缺省为100。
        data.put("LIMIT_FEE", map.getString("LIMIT_FEE", ""));// 集团月费用限额：非负整数，单位：分。0表示不限制，缺省为1000000。
        data.put("ZONE_MAX", map.getString("ZONE_MAX", ""));// 最大可设置归属分区个数：非负整数。缺省为1。
        data.put("ZONEFREE_NUM", map.getString("ZONEFREE_NUM", ""));// 免费修改归属分区次数：非负整数。缺省为10。
        data.put("ZONE_FEE", map.getString("ZONE_FEE", ""));// 修改归属分区费用：单位为分，缺省为0。
        data.put("MT_MAXNUM", map.getString("MT_MAXNUM", ""));// 最大可加入的移动中继数：非负整数，不能大于MAX_USERS，缺省为1。
        data.put("AIP_ID", map.getString("AIP_ID", ""));// AIP号码：正整数，范围[1，100]
        data.put("WORK_TYPE_CODE", map.getString("WORK_TYPE_CODE", ""));// 行业类型编码
        data.put("VPN_SCARE_CODE", map.getString("VPN_SCARE_CODE", ""));// 集团范围属性
        data.put("VPN_TIME_CODE", map.getString("VPN_TIME_CODE", ""));// 集团时间属性
        data.put("VPN_USER_CODE", map.getString("VPN_USER_CODE", ""));// 集团用户属性
        data.put("VPN_BUNDLE_CODE", map.getString("VPN_BUNDLE_CODE", ""));// 集团绑定属性
        data.put("MANAGER_NO", map.getString("MANAGER_NO", ""));// 管理流程接入码
        data.put("CALL_NET_TYPE", map.getString("CALL_NET_TYPE", ""));// 呼叫网络类型
        data.put("CALL_AREA_TYPE", map.getString("CALL_AREA_TYPE", ""));// 呼叫区域类型
        data.put("OVER_FEE_TAG", map.getString("OVER_FEE_TAG", ""));// 呼叫超出限额处理标记
        data.put("LIMFEE_TYPE_CODE", map.getString("LIMFEE_TYPE_CODE", ""));// 费用限额类型编码
        data.put("SINWORD_TYPE_CODE", map.getString("SINWORD_TYPE_CODE", ""));// 语种选择
        data.put("MOVE_TAG", map.getString("MOVE_TAG", ""));// 前转标记
        data.put("TRANS_TAG", map.getString("TRANS_TAG", ""));// 转接标记
        data.put("LOCK_TAG", map.getString("LOCK_TAG", ""));// 封锁标志
        data.put("CUST_MANAGER", map.getString("CUST_MANAGER", ""));// 主管客户经理
        data.put("LINK_MAN", map.getString("LINK_MAN", ""));// 联络员
        data.put("MONTH_FEE_LIMIT", map.getString("MONTH_FEE_LIMIT", ""));// 月费用限额
        data.put("SHORT_CODE_LEN", map.getString("SHORT_CODE_LEN", ""));// 短号长度
        data.put("CALL_ROAM_TYPE", map.getString("CALL_ROAM_TYPE", ""));// 主叫漫游权限
        data.put("BYCALL_ROAM_TYPE", map.getString("BYCALL_ROAM_TYPE", ""));// 被叫漫游权限
        data.put("PAYITEM_CODE", map.getString("PAYITEM_CODE", ""));// 付费帐目
        data.put("ITEM_FEE", map.getString("ITEM_FEE", ""));// 付费金额
        data.put("USRGRP_ID", map.getString("USRGRP_ID", ""));// 用户群标识
        data.put("OPEN_DATE", map.getString("OPEN_DATE", ""));// 建档时间
        data.put("REMOVE_DATE", map.getString("REMOVE_DATE", ""));// 注销时间
        data.put("UPDATE_TIME", map.getString("SYS_DATE", "")); // 更新时间
        data.put("UPDATE_STAFF_ID", map.getString("UPDATE_STAFF_ID", "")); // 更新员工
        data.put("UPDATE_DEPART_ID", map.getString("UPDATE_DEPART_ID", "")); // 更新部门
        data.put("REMARK", map.getString("REMARK", ""));// 备注
        data.put("RSRV_NUM1", map.getString("RSRV_NUM1", ""));// 预留数值1
        data.put("RSRV_NUM2", map.getString("RSRV_NUM2", ""));// 预留数值2
        data.put("RSRV_NUM3", map.getString("RSRV_NUM3", ""));// 预留数值3
        data.put("RSRV_NUM4", map.getString("RSRV_NUM4", ""));// 预留数值4
        data.put("RSRV_NUM5", map.getString("RSRV_NUM5", ""));// 预留数值5
        data.put("RSRV_STR1", map.getString("RSRV_STR1", ""));// 预留字段1
        data.put("RSRV_STR2", map.getString("RSRV_STR2", ""));// 预留字段2
        data.put("RSRV_STR3", map.getString("RSRV_STR3", ""));// 预留字段3
        data.put("RSRV_STR4", map.getString("RSRV_STR4", ""));// 预留字段4
        data.put("RSRV_STR5", map.getString("RSRV_STR5", ""));// 预留字段5
        data.put("RSRV_DATE1", map.getString("RSRV_DATE1", ""));// 预留日期1
        data.put("RSRV_DATE2", map.getString("RSRV_DATE2", ""));// 预留日期2
        data.put("RSRV_DATE3", map.getString("RSRV_DATE3", ""));// 预留日期3
        data.put("RSRV_TAG1", map.getString("RSRV_TAG1", ""));// 预留标志1
        data.put("RSRV_TAG2", map.getString("RSRV_TAG2", ""));// 预留标志2
        data.put("RSRV_TAG3", map.getString("RSRV_TAG3", ""));// 预留标志3

        Dao.insert("TF_B_TRADE_VPN", data,Route.getJourDb());
    }

    /**
     * 检查是否为升级融合V网的号码
     * 
     * @param pd
     * @param params
     * @return
     * @throws Exception
     */
    public static boolean isUpgradeUser(IData params) throws Exception
    {
        // 暂时屏蔽，等待V网升级功能完成再放开 liuzz

        return false;
    }

    public static boolean selImsShortCode(IData data) throws Exception
    {
        String shortCode = data.getString("SHORT_CODE", "");
        String usecustId = data.getString("USER_ID", "");
        String eparchyCode = data.getString(Route.ROUTE_EPARCHY_CODE);

        IDataset dataset = UserImpuInfoQry.selShortCodeByUserId(usecustId, shortCode, eparchyCode);
        if (IDataUtil.isNotEmpty(dataset))
        {
            return false;
        }
        return true;
    }

    /**
     * 成员新增短号校验[融合总机]
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static boolean shortCodeValidateSupTeleMeb(IData data) throws Exception
    {

        /*
         * 1、分机短号码必须为3-6位数字 2、分机号码的短号码不能用【1】开头 3、分机号码的短号码不能用【9】开头 4、分机号码的短号码不能用【0】开头
         */
        String shortCode = data.getString("SHORT_CODE").trim();

        if (shortCode.length() < 3)
        {
            data.put("ERROR_MESSAGE", "短号码必须为大于等于3位的数字！");
            return false;
        }

        if (!shortCode.substring(0, 1).equals("6"))
        {
            data.put("ERROR_MESSAGE", "短号码要用【6】开头！");
            return false;
        }

        if (shortCode.substring(1, 2).equals("0"))
        {
            data.put("ERROR_MESSAGE", "短号码第二位不能为【0】！");
            return false;
        }
        data.put("PRODUCT_ID", "6130");
        shortCodeValidateVpn(data);
        return true;
    }

    /**
     * IMS业务短号校检
     * 
     * @author:tengg
     * @param data
     * @param td
     * @return
     * @throws Exception
     */
    public static boolean shortCodeValidateVpn(IData data) throws Exception
    {
        BizCtrlInfo bizCtrlInfo = BizCtrlBean.qryProductCtrlInfo(data.getString("PRODUCT_ID"), BizCtrlType.CreateMember);

        String tradeTypeCode = bizCtrlInfo.getTradeTypeCode();

        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(data.getString("PRODUCT_ID"));
        String shortCode = data.getString("SHORT_CODE");
        String userId = data.getString("USER_ID");

        IData checkData = new DataMap();
        checkData.put("TRADE_TYPE_CODE", tradeTypeCode);
        checkData.put("PRODUCT_ID", "-1");
        checkData.put("USER_ID_A", userId);
        checkData.put("SHORT_CODE", shortCode);
        checkData.put("RELATION_TYPE_CODE", relationTypeCode);
        CheckForGrp.chk(checkData);

        return true;
    }

    /**
     * 将数组字符串合并
     * 
     * @author zhouwei9 Created on 2011-9-9
     * @param strs
     * @param reg
     * @return
     */
    public static String splitUnion(String[] strs, String reg, boolean needSort)
    {

        if (null == strs)
        {
            return null;
        }
        if (strs.length <= 0)
        {
            return "";
        }

        if (needSort)
        {
            Arrays.sort(strs);
        }

        StringBuilder buf = new StringBuilder();
        buf.append(strs[0]);
        for (int i = 1; i < strs.length; i++)
        {
            buf.append(reg);
            buf.append(strs[i]);
        }
        return buf.toString();
    }
    
    /**
     * 与GrpImsUtilView类中的方法checkImsVpnShortCode的校验逻辑一样的,拷贝过来的
     * @param data
     * @return
     * @throws Exception
     */
    public static boolean checkImsVpnShortCode(IData data) throws Exception
    {
        String shortCode = data.getString("SHORT_CODE");
        if ("".equals(shortCode))
        {
            data.put("ERROR_MESSAGE", "短号码不能为空！");
            return false;
        }

        if (!"6".equals(shortCode.substring(0, 1)))
        {
            data.put("ERROR_MESSAGE", "短号码第一位必须为6，请重新选择短号！");
            return false;
        }

        if ("60".equals(shortCode.substring(0, 2)))
        {
            data.put("ERROR_MESSAGE", "短号码不能以60开头，请重新选择短号！");
            return false;
        }

        if ("61860".equals(shortCode))
        {
            data.put("ERROR_MESSAGE", "该短号与系统服务号码相同，请重新选择短号！");
            return false;
        }

        if (shortCode.length() > 6)
        {
            data.put("ERROR_MESSAGE", "该短号长度超过设定的短号长度6,请重新输入短号！");
            return false;
        }

        boolean flag = true;
        flag = selImsVpnShortCode(data);
        if (!flag)
        {
            data.put("ERROR_MESSAGE", "该短号已经被使用，请重新输入短号！");
        }

        return flag;
    }
    
    private static boolean selImsVpnShortCode(IData data) throws Exception
    {
        String shortCode = data.getString("SHORT_CODE", "");
        String userIdA = data.getString("USER_ID", "");
        String eparchyCode = data.getString(Route.ROUTE_EPARCHY_CODE);
        IDataset dataSet = UserImpuInfoQry.selImsVpnShortCodeByUserId(userIdA, shortCode, eparchyCode);
        if (IDataUtil.isNotEmpty(dataSet))
        {
            return false;
        }
        return true;
    }
    
}
