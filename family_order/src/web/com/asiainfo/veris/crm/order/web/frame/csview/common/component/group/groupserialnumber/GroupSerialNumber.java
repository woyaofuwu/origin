
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.groupserialnumber;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupProductUtilView;

public abstract class GroupSerialNumber extends CSBizTempComponent
{

    /**
     * Check ResultInfo
     * 
     * @param pageData
     * @return
     * @throws Exception
     */
    private String checkResultInfo(IData pageData) throws Exception
    {
        String productId = pageData.getString("GRP_PRODUCT_ID", "");
        String serialNumber = pageData.getString("SERIAL_NUMBER", "");
        String eparchyCode = pageData.getString("GRP_USER_EPARCHYCODE");

        // 查询服务号码前缀
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        param.put(Route.USER_EPARCHY_CODE, eparchyCode);
        IData preSnData = CSViewCall.callone(this, "SS.ProductInfoSVC.getGrpSnBySelectParam", param);
        String preSn = preSnData.getString("PRE_SN", "");// 服务号码前缀

        // 号码不能为空
        if (StringUtils.isEmpty(serialNumber))
        {
            return "服务号码为空,校验失败,请输入集团编码!";
        }

        // 不能为手机号码: 130-188
        if ((serialNumber.substring(0, 2).equals("13") || serialNumber.subSequence(0, 3).equals("188")) && serialNumber.length() == 11)
        {
            return "服务号码请勿设置为手机号码!";
        }

        // 产品规则校验
        if (productId.equals("6200") && !serialNumber.substring(0, 3).equals(eparchyCode.substring(1, 4)))
        {
            return "服务号码前三位必需是所在地区编码的后三位[" + eparchyCode.substring(1, 4) + "]!";
        }

        // VPN类产品规则校验
        if ((productId.equals("8000") || productId.equals("8001") || productId.equals("8010") || productId.equals("8015")) && !serialNumber.substring(0, 4).equals(preSn))
        {
            return "服务号码前四位与配置表中代码[" + preSn + "]不一致,请修改!";
        }

        // 号码位数限制
        if (productId.equals("6200") && serialNumber.length() != 10)
        {
            return "彩铃集团服务号码长度必须为10,请修改!";
        }
        if (productId.equals("7001") && serialNumber.length() != 7)
        {
            return "GPRS集团服务号码长度必须为7,请修改!";
        }
        if (productId.equals("7051") && serialNumber.length() != 10)
        {
            return "行业应用卡集团服务号码长度必须为10,请修改!";
        }
        if (productId.equals("8000") && serialNumber.length() != 10)
        {
            return "智能网VPMN集团服务号码长度必须为10.请修改!";
        }
        if (productId.equals("8001") && serialNumber.length() != 10)
        {
            return "VPMN(家庭集团)服务号码长度必须为10,请修改!";
        }
        if (productId.equals("8010") && serialNumber.length() != 10)
        {
            return "普通虚拟网集团服务号码长度必须为10,请修改!";
        }
        if (productId.equals("8015") && serialNumber.length() != 10)
        {
            return "VPCN(跨区)集团服务号码长度必须为10,请修改!";
        }
        if (productId.equals("9071") && serialNumber.length() != 10)
        {
            return "政企彩漫服务号码长度必须为10，请修改！";
        }
        if (productId.equals("9051"))
        {
            if (serialNumber.length() != 11 || !serialNumber.matches("^[0-9]+$"))
            {
                return "集团通讯录服务号码必须为11位数字，请修改！";
            }
            if (!"12553".equals(serialNumber.substring(0, 5)) || !serialNumber.substring(5, 7).equals(eparchyCode.substring(2, 4)))
            {
                return "集团通讯录服务号码前7位必须为12553" + eparchyCode.substring(2, 4) + "，请修改!";
            }

        }

        // 判断是否有相同的号码
        param.clear();
        param.put("SERIAL_NUMBER", serialNumber);
        IData userList = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, serialNumber, false);

        if (IDataUtil.isNotEmpty(userList))
        {
            return "该服务号码[" + serialNumber + "]已存在,请重新输入!";
        }

        return "";
    }

    /**
     * Check SerialNumber
     * 
     * @param pageData
     * @throws Exception
     */
    public void checkSerialNumber(IData pageData) throws Exception
    {
        String resultDesc = checkResultInfo(pageData);

        IData infoData = new DataMap();
        infoData.put("SERIAL_NUMBER", pageData.getString("SERIAL_NUMBER", ""));
        infoData.put("RES_TYPE_CODE", pageData.getString("RES_TYPE_CODE", ""));

        IData ajaxData = new DataMap();
        if ("".equals(resultDesc)) // Validate Pass
        {
            infoData.put("IF_RES_CODE", "0");

            ajaxData.put("X_RESULTCODE", "0");
            ajaxData.put("X_RESULTINFO", "服务号码检验通过,录入的服务号码可以使用!");
        }
        else
        // Validate Failed
        {
            infoData.put("IF_RES_CODE", "1");
            ajaxData.put("X_RESULTCODE", "-1");
            ajaxData.put("X_RESULTINFO", resultDesc);
        }

        setInfo(infoData);
        this.getPage().setAjax(ajaxData);
    }

    @Override
    protected void cleanupAfterRender(IRequestCycle cycle)
    {

        super.cleanupAfterRender(cycle);
        if (!cycle.isRewinding())
        {
            setInfo(null);
        }
    }

    /**
     * Create SerialNumber
     * 
     * @param pageData
     * @throws Exception
     */
    public void createSerialNumber(IData pageData) throws Exception
    {
        String groupId = pageData.getString("GROUP_ID");

        if (StringUtils.isEmpty(groupId))
        {
            return;
        }

        String productId = pageData.getString("GRP_PRODUCT_ID");
        String grpUserEparchyCode = getGrpUserEparchyCode();
        String resTypeCode = getResTypeCode();

        IData result = GroupProductUtilView.createGrpSn(this, productId, grpUserEparchyCode, resTypeCode);
        setInfo(result);
    }

    public abstract String getGrpUserEparchyCode();

    public abstract String getResTypeCode();

    @Override
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        if (cycle.isRewinding())
            return;

        IData pageData = this.getPage().getData();

        boolean isCheck = pageData.getBoolean("IS_CHECK", false);

        if (isCheck)
        {
            // Check SerialNumber
            checkSerialNumber(pageData);
        }
        else
        {
            // Create SerialNumber
            createSerialNumber(pageData);
        }

        cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/group/groupserialnumber/GroupSerialNumber.js");
    }

    public abstract void setInfo(IData info);

}
