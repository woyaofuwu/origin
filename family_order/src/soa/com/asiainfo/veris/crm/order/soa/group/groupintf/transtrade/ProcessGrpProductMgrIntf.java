
package com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.GrpProductManagerQry;

/**
 * 集团产品管理员处理接口(ng2-crm3.5为集团门户提供)
 * 
 * @author sungq3
 */
public class ProcessGrpProductMgrIntf
{
    // 集团产品管理员操作类型
    public enum OperGrpProMgrType
    {

        // 新增集团产品管理员操作类型
        createGrpProManager("0"),

        // 变更集团产品管理员操作类型
        changeGrpProManager("1"),

        // 注销集团产品管理员操作类型
        destroyGrpProManager("2"),

        // 查询集团产品管理员操作类型
        queryGrpProManager("3"),

        // 产品管理员登陆鉴权操作类型
        grpProManagerLogin("4");

        OperGrpProMgrType(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }

        public final String value;
    }

    /**
     * 集团产品管理员接口的相关操作：新增、删除、修改、查询、登陆鉴权
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset processGrpProductMgr(IData inparam) throws Exception
    {
        // 操作类型
        String operType = inparam.getString("OPER_TYPE");

        IDataset processIds = new DatasetList();
        if (OperGrpProMgrType.createGrpProManager.value.equals(operType))
        { // 0-新增
            processIds = createGrpProManager(inparam);
        }
        else if (OperGrpProMgrType.changeGrpProManager.value.equals(operType))
        { // 1-变更
            processIds = updateGrpProManager(inparam);
        }
        else if (OperGrpProMgrType.destroyGrpProManager.value.equals(operType))
        { // 2-注销
            processIds = deleteGrpProManager(inparam);
        }
        else if (OperGrpProMgrType.queryGrpProManager.value.equals(operType))
        { // 3-查询
            processIds = queryGrpProManager(inparam);
        }
        else if (OperGrpProMgrType.grpProManagerLogin.value.equals(operType))
        { // 4-产品管理员登陆鉴权
            processIds = grpProManagerLogin(inparam);
        }
        else
        {
            CSAppException.apperr(GrpException.CRM_GRP_686, operType);
        }
        return processIds;
    }

    /**
     * 新增集团产品管理员
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset createGrpProManager(IData inparam) throws Exception
    {
        // 集团编码
        String groupId = inparam.getString("GROUP_ID");

        // 联系人号码
        String grpMgrSN = inparam.getString("GROUP_MGR_SN");

        // 产品管理员号码
        String prodMgrSN = inparam.getString("PRODUCT_MGR_SN");

        // 产品标识
        String productId = inparam.getString("PRODUCT_ID");

        if (StringUtils.isEmpty(groupId))
        {
            CSAppException.apperr(GrpException.CRM_GRP_687);
        }

        if (StringUtils.isEmpty(grpMgrSN))
        {
            CSAppException.apperr(GrpException.CRM_GRP_688);
        }

        if (StringUtils.isEmpty(prodMgrSN))
        {
            CSAppException.apperr(GrpException.CRM_GRP_689);
        }

        if (StringUtils.isEmpty(productId))
        {
            CSAppException.apperr(GrpException.CRM_GRP_690);
        }

        // 判断产品管理员必须要是集团成员
        IDataset grpMemberDataset = GrpMebInfoQry.queryGrpMebInfoByGroupIdSN(groupId, prodMgrSN);
        if (IDataUtil.isEmpty(grpMemberDataset))
        {
            CSAppException.apperr(GrpException.CRM_GRP_691, prodMgrSN, groupId);
        }
        IData grpMebData = grpMemberDataset.getData(0);

        // 判断产品管理员的用户密码是否存在
        if (!checkGrpMgrPwd(prodMgrSN, grpMebData.getString("EPARCHY_CODE")))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1102, prodMgrSN);
        }

        // 正则验证产品格式 必须为数字或者逗号分割的数字，如：8000或者8000,80001
        Matcher matcher = Pattern.compile("((\\d+),{1})*(\\d+)").matcher(productId);
        if (!matcher.matches())
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_502, productId);
        }
        // 获取产品编码
        String[] productIds = productId.split(",");

        // 判断一个集团的一个产品只能由一个产品管理员管理
        for (int i = 0, size = productIds.length; i < size; i++)
        { // 产品是否存在
            IData productData = UProductInfoQry.qryProductByPK(productIds[i]);
            if (IDataUtil.isEmpty(productData))
            {
                CSAppException.apperr(ProductException.CRM_PRODUCT_135, productIds[i]);
            }

            // 获取产品所属管理员
            IDataset tmpProdMgrDataset = GrpProductManagerQry.qryProductMgrInfoByGrpIdProdId(groupId, productIds[i]);
            if (IDataUtil.isNotEmpty(tmpProdMgrDataset))
            {
                String tmpProductMgrSN = tmpProdMgrDataset.getData(0).getString("PRODUCT_MGR_SN");
                CSAppException.apperr(GrpException.CRM_GRP_692, productIds[i], groupId, tmpProductMgrSN);
            }
        }

        // 判断一个产品管理员只属于一个集团客户
        IDataset prodMgrDataset = GrpProductManagerQry.qryProductMgrByMgrSN(prodMgrSN);
        if (IDataUtil.isNotEmpty(prodMgrDataset))
        {
            String tmpGroupId = prodMgrDataset.getData(0).getString("GROUP_ID");
            if (!groupId.equals(tmpGroupId))
            {
                CSAppException.apperr(GrpException.CRM_GRP_693, prodMgrSN, tmpGroupId);
            }
        }

        // 判断集团客户中的产品管理员是否已管理了某个产品，如是，则只可进行修改操作
        IDataset prodMgrDataset1 = GrpProductManagerQry.qryProductMgrByGrpIdProdMgrSN(groupId, prodMgrSN);
        if (IDataUtil.isNotEmpty(prodMgrDataset1))
        {
            CSAppException.apperr(GrpException.CRM_GRP_694, groupId, prodMgrSN);
        }

        // 设置保存参数
        IData param = new DataMap();
        param.put("GROUP_ID", groupId); // 集团编码
        param.put("GROUP_MGR_SN", grpMgrSN); // 联系人号码
        param.put("PRODUCT_MGR_SN", prodMgrSN); // 产品管理员号码
        param.put("PRODUCT_ID", productId); // 产品编码
        param.put("PRODUCT_MGR_NAME", grpMebData.getString("CUST_NAME")); // 产品管理员名称
        param.put("DEPART_NAME", grpMebData.getString("DEPART")); // 部门名称
        param.put("EMAIL", inparam.getString("EMAIL", "")); // 邮箱
        param.put("VALID_TAG", inparam.getString("VALID_TAG", "0")); // 移除标识默认为"0"
        param.put("UPDATE_STAFF_ID", inparam.getString("TRADE_STAFF_ID", "")); // 员工标识
        param.put("UPDATE_DEPART_ID", inparam.getString("TRADE_DEPART_ID", ""));// 员工所在的部门
        param.put("REMARK", inparam.getString("REMARK", "门户新增产品管理员")); // 备注
        param.put("START_DATE", SysDateMgr.getSysTime());
        param.put("END_DATE", SysDateMgr.getTheLastTime());
        param.put("UPDATE_DATE", SysDateMgr.getSysTime());

        param.put("RSRV_STR1", inparam.getString("RSRV_STR1", "")); // 预留字段1
        param.put("RSRV_STR2", inparam.getString("RSRV_STR2", "")); // 预留字段2
        param.put("RSRV_STR3", inparam.getString("RSRV_STR3", "")); // 预留字段3
        param.put("RSRV_STR4", inparam.getString("RSRV_STR4", "")); // 预留字段4
        param.put("RSRV_STR5", inparam.getString("RSRV_STR5", "")); // 预留字段5

        // 保存产品管理员信息
        boolean flag = GrpProductManagerQry.saveGroupProductMgr(param);

        return dealResult(flag);
    }

    /**
     * 变更集团产品管理员
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset updateGrpProManager(IData inparam) throws Exception
    {
        // 集团编码
        String groupId = inparam.getString("GROUP_ID");

        // 联系人号码
        String grpMgrSN = inparam.getString("GROUP_MGR_SN");

        // 产品管理员号码
        String prodMgrSN = inparam.getString("PRODUCT_MGR_SN");

        // 产品标识
        String productId = inparam.getString("PRODUCT_ID");

        if (StringUtils.isEmpty(groupId))
        {
            CSAppException.apperr(GrpException.CRM_GRP_687);
        }

        if (StringUtils.isEmpty(grpMgrSN))
        {
            CSAppException.apperr(GrpException.CRM_GRP_688);
        }

        if (StringUtils.isEmpty(prodMgrSN))
        {
            CSAppException.apperr(GrpException.CRM_GRP_689);
        }

        if (StringUtils.isEmpty(productId))
        {
            CSAppException.apperr(GrpException.CRM_GRP_690);
        }

        // 根据集团客户编码和产品管理员号码判断是否存在需要变更的产品管理员信息
        IDataset isOrderDataset = GrpProductManagerQry.qryProductMgrByGrpIdProdMgrSN(groupId, prodMgrSN);
        if (IDataUtil.isEmpty(isOrderDataset))
        {
            CSAppException.apperr(GrpException.CRM_GRP_700, groupId, prodMgrSN);
        }

        // 判断产品管理员必须要是集团成员
        IDataset grpMemberDataset = GrpMebInfoQry.queryGrpMebInfoByGroupIdSN(groupId, prodMgrSN);
        if (IDataUtil.isEmpty(grpMemberDataset))
        {
            CSAppException.apperr(GrpException.CRM_GRP_691, prodMgrSN, groupId);
        }
        IData grpMebData = grpMemberDataset.getData(0);

        // 判断产品管理员的用户密码是否存在
        if (!checkGrpMgrPwd(prodMgrSN, grpMebData.getString("EPARCHY_CODE")))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1102, prodMgrSN);
        }

        // 正则验证产品格式 必须为数字或者逗号分割的数字，如：8000或者8000,80001
        Matcher matcher = Pattern.compile("((\\d+),{1})*(\\d+)").matcher(productId);
        if (!matcher.matches())
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_502, productId);
        }
        // 获取产品编码
        String[] productIds = productId.split(",");

        // 判断一个集团的一个产品只能由一个产品管理员管理
        for (int i = 0, size = productIds.length; i < size; i++)
        { // 产品是否存在
            IData productData = UProductInfoQry.qryProductByPK(productIds[i]);
            if (IDataUtil.isEmpty(productData))
            {
                CSAppException.apperr(ProductException.CRM_PRODUCT_135, productIds[i]);
            }

            // 获取产品所属管理员
            IDataset tmpProdMgrDataset = GrpProductManagerQry.qryProductMgrInfoByGrpIdProdId(groupId, productIds[i]);
            if (IDataUtil.isNotEmpty(tmpProdMgrDataset))
            {
                String tmpProductMgrSN = tmpProdMgrDataset.getData(0).getString("PRODUCT_MGR_SN");
                if (!prodMgrSN.equals(tmpProductMgrSN))
                {
                    CSAppException.apperr(GrpException.CRM_GRP_699, productIds[i], groupId, tmpProductMgrSN, prodMgrSN);
                }
            }
        }

        // 判断一个产品管理员只属于一个集团客户
        IDataset prodMgrDataset = GrpProductManagerQry.qryProductMgrByMgrSN(prodMgrSN);
        if (IDataUtil.isNotEmpty(prodMgrDataset))
        {
            String tmpGroupId = prodMgrDataset.getData(0).getString("GROUP_ID");
            if (!groupId.equals(tmpGroupId))
            {
                CSAppException.apperr(GrpException.CRM_GRP_693, prodMgrSN, tmpGroupId);
            }
        }

        // 设置保存参数
        IData param = new DataMap();
        param.put("GROUP_ID", groupId); // 集团编码
        param.put("GROUP_MGR_SN", grpMgrSN); // 联系人号码
        param.put("PRODUCT_MGR_SN", prodMgrSN); // 产品管理员号码
        param.put("PRODUCT_ID", productId); // 产品编码
        param.put("PRODUCT_MGR_NAME", grpMebData.getString("CUST_NAME")); // 产品管理员名称
        param.put("DEPART_NAME", grpMebData.getString("DEPART")); // 部门名称
        param.put("EMAIL", inparam.getString("EMAIL", "")); // 邮箱
        param.put("VALID_TAG", inparam.getString("VALID_TAG", "0")); // 移除标识默认为"0"
        param.put("REMARK", inparam.getString("REMARK", "门户更新产品管理员")); // 备注
        param.put("UPDATE_DATE", SysDateMgr.getSysTime());

        param.put("RSRV_STR1", inparam.getString("RSRV_STR1", "")); // 预留字段1
        param.put("RSRV_STR2", inparam.getString("RSRV_STR2", "")); // 预留字段2
        param.put("RSRV_STR3", inparam.getString("RSRV_STR3", "")); // 预留字段3
        param.put("RSRV_STR4", inparam.getString("RSRV_STR4", "")); // 预留字段4
        param.put("RSRV_STR5", inparam.getString("RSRV_STR5", "")); // 预留字段5

        // 变更产品管理员信息
        boolean flag = GrpProductManagerQry.updateGrpProMgrByPK(param) > 0 ? true : false;

        return dealResult(flag);
    }

    /**
     * 删除集团产品管理员
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset deleteGrpProManager(IData inparam) throws Exception
    {
        // 集团编码
        String groupId = inparam.getString("GROUP_ID");

        // 联系人号码
        String grpMgrSN = inparam.getString("GROUP_MGR_SN");

        // 产品管理员号码
        String prodMgrSN = inparam.getString("PRODUCT_MGR_SN");

        if (StringUtils.isEmpty(groupId))
        {
            CSAppException.apperr(GrpException.CRM_GRP_687);
        }

        if (StringUtils.isEmpty(grpMgrSN))
        {
            CSAppException.apperr(GrpException.CRM_GRP_688);
        }

        // 设置保存参数
        IData param = new DataMap();
        param.put("GROUP_ID", groupId); // 集团编码
        param.put("GROUP_MGR_SN", grpMgrSN); // 联系人号码
        param.put("VALID_TAG", "1"); // 移除标识
        param.put("UPDATE_DATE", SysDateMgr.getSysTime()); // 删除时间
        param.put("REMARK", inparam.getString("REMARK", "门户删除产品管理员")); // 备注

        // 删除产品管理员信息
        boolean flag = false;
        if (StringUtils.isEmpty(prodMgrSN))
        { // 根据集团编号和联系人号码删除产品管理员信息
            flag = GrpProductManagerQry.deleteGrpProdMgrByGrpIdGrpMgrSN(param) > 0 ? true : false;
        }
        else
        { // 根据集团编号、联系人号码和产品管理员号码删除指定的产品管理员信息
            param.put("PRODUCT_MGR_SN", prodMgrSN);
            flag = GrpProductManagerQry.deleteGrpProdMgrByGrpIdGrpMgrSNProdMgrSN(param) > 0 ? true : false;
        }

        return dealResult(flag);
    }

    /**
     * 查询集团产品管理员
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset queryGrpProManager(IData inparam) throws Exception
    {
        // 集团编码
        String groupId = inparam.getString("GROUP_ID");

        // 联系人号码
        String grpMgrSN = inparam.getString("GROUP_MGR_SN");

        if (StringUtils.isEmpty(groupId))
        {
            CSAppException.apperr(GrpException.CRM_GRP_687);
        }

        if (StringUtils.isEmpty(grpMgrSN))
        {
            CSAppException.apperr(GrpException.CRM_GRP_688);
        }

        // 查询产品管理员信息
        IDataset grpProdMgrDataset = GrpProductManagerQry.qryProductMgrByGrpIdGrpMgrSN(groupId, grpMgrSN);
        if (IDataUtil.isEmpty(grpProdMgrDataset))
        {
            CSAppException.apperr(GrpException.CRM_GRP_701, groupId, grpMgrSN);
        }
        IDataset resultDataset = new DatasetList();
        for (int i = 0, size = grpProdMgrDataset.size(); i < size; i++)
        {
            IData grpProdMgrData = grpProdMgrDataset.getData(i);
            // 获取产品名称
            String productName = UProductInfoQry.getProductNameByProductId(grpProdMgrData.getString("PRODUCT_ID"));
            grpProdMgrData.put("PRODUCT_NAME", productName);

            resultDataset.add(grpProdMgrData);
        }
        return resultDataset;
    }

    /**
     * 产品管理员登陆鉴权
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset grpProManagerLogin(IData inparam) throws Exception
    {
        // 产品管理员号码
        String productMgrSN = inparam.getString("PRODUCT_MGR_SN");

        // 产品管理员密码
        String productMgrPwd = inparam.getString("PRODUCT_MGR_PASSWD");

        // 集团编码
        String groupId = inparam.getString("GROUP_ID");

        if (StringUtils.isEmpty(productMgrSN))
        {
            CSAppException.apperr(GrpException.CRM_GRP_689);
        }

        if (StringUtils.isEmpty(productMgrPwd))
        {
            CSAppException.apperr(GrpException.CRM_GRP_702);
        }

        if (StringUtils.isEmpty(groupId))
        {
            CSAppException.apperr(GrpException.CRM_GRP_687);
        }

        // 获取产品管理员用户信息
        IData userData = UcaInfoQry.qryUserInfoBySn(productMgrSN);
        if (IDataUtil.isEmpty(userData))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1103, productMgrSN);
        }

        // 获取产品管理员输入密码的密文
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", productMgrSN);
        params.put("USER_ID", userData.getString("USER_ID"));
        params.put("PASSWORD", productMgrPwd);
        IDataset pwdDataset = CSAppCall.call("CS.AuthCheckSVC.getEncryptKey", params);
        String enPwd = pwdDataset.get(0, "ENCRYPT_PASSWD").toString();

        // 将输入密码的加密密文与DB中保存的密文
        if (!userData.getString("USER_PASSWD").equals(enPwd))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_774);
        }

        // 根据集团客户编码和产品管理员号码查询产品管理员信息
        IDataset prodMgrDataset = GrpProductManagerQry.qryProductMgrByGrpIdProductMgrSN(groupId, productMgrSN);
        if (IDataUtil.isEmpty(prodMgrDataset))
        {
            CSAppException.apperr(GrpException.CRM_GRP_703, productMgrSN);
        }
        IDataset resultDataset = new DatasetList();
        for (int i = 0, size = prodMgrDataset.size(); i < size; i++)
        {
            IData grpProdMgrData = prodMgrDataset.getData(i);
            // 获取产品名称
            String productName = UProductInfoQry.getProductNameByProductId(grpProdMgrData.getString("PRODUCT_ID"));
            grpProdMgrData.put("PRODUCT_NAME", productName);

            resultDataset.add(grpProdMgrData);
        }
        return resultDataset;
    }

    /**
     * 校验产品管理员的密码是否存在
     * 
     * @param serialNumber
     * @return
     * @throws Exception
     */
    private static boolean checkGrpMgrPwd(String serialNumber, String eparchy_code) throws Exception
    {
        IData userData = UcaInfoQry.qryUserInfoBySn(serialNumber, eparchy_code);
        if (IDataUtil.isEmpty(userData))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1103, serialNumber);
        }
        String userPwd = userData.getString("USER_PASSWD");
        if (StringUtils.isEmpty(userPwd))
        {
            return false;
        }
        return true;
    }

    /**
     * 统一处理返回参数
     * 
     * @param boo
     * @return
     * @throws Exception
     */
    private static IDataset dealResult(boolean boo) throws Exception
    {
        IData result = new DataMap();
        if (boo)
        {
            result.put("X_LAST_RESULTINFO", "Trade OK!");
            result.put("X_RESULTINFO", "Trade OK!");
            result.put("X_RESULTCODE", "0");
        }
        else
        {
            result.put("X_LAST_RESULTINFO", "Trade ERROR!");
            result.put("X_RESULTINFO", "Trade ERROR!");
            result.put("X_RESULTCODE", "-1");
        }
        return IDataUtil.idToIds(result);
    }
}
