
package com.asiainfo.veris.crm.order.soa.group.groupintf.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserImpuInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupImsUtil;

/**
 * 集团多媒体电话成员批量新增
 * 
 * @author liuzz
 */
public class GrpBatDesktopTelMember extends GroupBatService
{
    private static final long serialVersionUID = 1L;

    private static final String SERVICE_NAME = "CS.CreateGroupMemberSvc.createGroupMember";

    @Override
    public void batInitialSub(IData batData) throws Exception
    {
        svcName = SERVICE_NAME;
        batData.put(BIZ_CTRL_TYPE, BizCtrlType.CreateMember);
    }

    @Override
    public void batValidateSub(IData batData) throws Exception
    {
        String userId = IDataUtil.getMandaData(condData, "USER_ID"); // 集团用户ID
        String serial_number = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码

        // 校验集团三户信息
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        chkGroupUCAByUserId(inparam);

        // 校验成员三户信息
        inparam.clear();
        inparam.put("SERIAL_NUMBER", serial_number);
        chkMemberUCABySerialNumber(inparam);

        // 判断服务号码状态
        if (!"0".equals(getMebUcaData().getUser().getUserStateCodeset()))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_471, serial_number);
        }

        String netTypeCode = getMebUcaData().getUser().getNetTypeCode();
        // 判断当前号码是否是IMS用户
        if (!"05".equals(netTypeCode))
        {
            CSAppException.apperr(GrpException.CRM_GRP_675);
        }
        else
        {
            IDataset userImpu = UserImpuInfoQry.queryUserImpuInfo(getMebUcaData().getUserId());
            String RSRV_STR1 = userImpu.getData(0).getString("RSRV_STR1", "");
            // 判断当前号码是否是无卡PC客户端IMS用户
            if (!"0".equals(RSRV_STR1))
            {
                CSAppException.apperr(GrpException.CRM_GRP_676);
            }
        }
        // 判断成员号码是否为集团号码
        if (isGroupSerialNumber(serial_number))
        {
            CSAppException.apperr(GrpException.CRM_GRP_120, serial_number);
        }

        // 校验短号
        String shortCode = batData.getString("DATA1", ""); // 短号
        if (StringUtils.isBlank(shortCode))
        {
            CSAppException.apperr(GrpException.CRM_GRP_603, serial_number);
        }

        batData.put("SHORT_CODE", shortCode);
        batData.put("USER_ID", userId);
        if (!GroupImsUtil.checkImsShortCode(batData))
        {
            CSAppException.apperr(GrpException.CRM_GRP_502, batData.getString("ERROR_MESSAGE"));
        }

        // 资源信息
        IData resData = new DataMap();
        resData.put("RES_TYPE_CODE", "S");
        resData.put("CHECKED", "true");
        resData.put("DISABLED", "true");
        resData.put("RES_CODE", shortCode);
        resData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        // 产品参数信息
        IData productParam = new DataMap();
        productParam.put("SHORT_CODE", shortCode);
        productParam.put("HSS_IMPIATTR_IMPI_ID", condData.getString("HSS_IMPIATTR_IMPI_ID", ""));
        productParam.put("HSS_AUTH_TYPE", condData.getString("HSS_AUTH_TYPE", ""));
        productParam.put("HSS_CAPS_TYPE", condData.getString("HSS_CAPS_TYPE", ""));
        productParam.put("HSS_CAPS_ID", condData.getString("HSS_CAPS_ID", ""));

        IDataset productParamDataset = new DatasetList();
        buildProductParam(batData.getString("PRODUCT_ID"), productParam, productParamDataset);

        batData.put("RES_INFO", IDataUtil.idToIds(resData));
        batData.put("PRODUCT_PARAM_INFO", productParamDataset);
    }

    /**
     * 构造规则数据
     */
    @Override
    protected void builderRuleData(IData batData) throws Exception
    {
        super.builderRuleData(batData);

        ruleData.put("RULE_BIZ_TYPE_CODE", "chkBeforeForGrp");
        ruleData.put("RULE_BIZ_KIND_CODE", "GrpMebOrder");
        // 集团信息
        ruleData.put("PRODUCT_ID", getGrpUcaData().getProductId());
        ruleData.put("CUST_ID", getGrpUcaData().getCustId());
        ruleData.put("USER_ID", getGrpUcaData().getUserId());
        // 成员信息
        ruleData.put("SERIAL_NUMBER", getMebUcaData().getSerialNumber());
        ruleData.put("USER_ID_B", getMebUcaData().getUserId());
        ruleData.put("BRAND_CODE_B", getMebUcaData().getBrandCode());
        ruleData.put("EPARCHY_CODE_B", getMebUcaData().getUser().getEparchyCode());
        ruleData.put("PRODUCT_ID_B", getMebUcaData().getProductId());
    }

    @Override
    public void builderSvcData(IData batData) throws Exception
    {
        svcData.put("USER_ID", getGrpUcaData().getUserId());
        svcData.put("SERIAL_NUMBER", getMebUcaData().getSerialNumber());
        svcData.put("MEM_ROLE_B", batData.getString("MEM_ROLE_B", "1"));
        svcData.put("PRODUCT_ID", IDataUtil.getMandaData(condData, "PRODUCT_ID"));
        svcData.put("ELEMENT_INFO", new DatasetList(condData.getString("ELEMENT_INFO")));
        svcData.put("RES_INFO", batData.getDataset("RES_INFO"));
        svcData.put("PRODUCT_PARAM_INFO", batData.getDataset("PRODUCT_PARAM_INFO"));
    }
}
