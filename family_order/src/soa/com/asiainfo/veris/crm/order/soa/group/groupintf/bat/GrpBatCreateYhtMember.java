
package com.asiainfo.veris.crm.order.soa.group.groupintf.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.FuncrightException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.UUException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;
import com.asiainfo.veris.crm.order.soa.group.common.query.GroupQueryBean;

/**
 * 集团融合一号通成员批量新增
 * 
 * @author loyoveui
 */
public class GrpBatCreateYhtMember extends GroupBatService
{
    private static final long serialVersionUID = 1L;

    private boolean ctFlag = false;

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
        String user_id = IDataUtil.chkParam(condData, "USER_ID");// 集团用户ID
        String serial_number = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码

        // 一、校验集团三户信息
        IData inparam = new DataMap();
        inparam.put("USER_ID", user_id);
        chkGroupUCAByUserId(inparam);

        // 二、校验成员三户信息
        inparam.clear();
        inparam.put("SERIAL_NUMBER", serial_number);
        chkMemberUCABySerialNumber(inparam);

        // 三、判断服务号码状态
        if (!"0".equals(getMebUcaData().getUser().getUserStateCodeset()))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_471, serial_number);
        }

        // 四、校验是否IMS号码
        String meb_user_id = getMebUcaData().getUserId(); // 成员用户ID

        IData param = new DataMap();
        String custId = condData.getString("CUST_ID", "");
        param.put("USER_ID_B", meb_user_id);
        param.put("CUST_ID", custId);
        String netTypeCode = getMebUcaData().getUser().getNetTypeCode();
        if (!"05".equals(netTypeCode))
        {// 非IMS号码需先加入融合V网

            param.put("RELATION_TYPE_CODE", "20");
            IDataset idsUU = GroupQueryBean.qryRelationUUByCustIdAndUserIdB(param);

            if (idsUU == null || idsUU.size() == 0) // 不是vpmn成员
            {
                CSAppException.apperr(GrpException.CRM_GRP_256);// 非IMS用户在加融合一号通之前，需要先加入融合V网成员！
            }
        }
        else
        {// IMS号码需先加入多媒体桌面电话
            param.put("RELATION_TYPE_CODE", "S1");
            IDataset idsUU = GroupQueryBean.qryRelationUUByCustIdAndUserIdB(param);
            if (idsUU == null || idsUU.size() == 0) // 不是多媒体桌面电话成员
            {
                CSAppException.apperr(GrpException.CRM_GRP_199);// IMS用户在加融合一号通之前，需要先加入多媒体桌面电话成员！
            }
        }
        // 五、判断成员号码是否为集团号码
        if (isGroupSerialNumber(serial_number))
        {
            CSAppException.apperr(GrpException.CRM_GRP_120, serial_number);
        }

        // 六、判断是否可以拨打国内长途 14
        boolean result = RouteInfoQry.isChinaMobile(serial_number);
        if (result)
        {
            // 手机号码是否订购拨打长途服务
            IDataset ds = UserSvcInfoQry.qrySvcInfoByUserIdSvcId(meb_user_id, "14");
            if (IDataUtil.isNotEmpty(ds))
            {
                ctFlag = true;
            }
        }
        else
        {// 为固定电话时只产生了虚拟三户资料，是没有14服务，暂时不判断
            ctFlag = true;
        }

        // 主叫一号通数据
        String zyht_serialNumber = batData.getString("DATA16"); // 主叫一号通数据
        IDataset zyhtds = getZyhtData2Dataset(zyht_serialNumber, zyht_serialNumber);

        // 被叫一号通数据
        String byht_serialNumber = batData.getString("DATA18"); // 被叫一号通数据
        IDataset byhtds = getByhtData2Dataset(byht_serialNumber);

        // 被叫一号通号码验证
        if (IDataUtil.isNotEmpty(byhtds))
        {
            byhtds = DataHelper.distinct(byhtds, "BSERIAL_NUMBER", ","); // 排除重复号码

            if (byhtds.size() > 3)
            {
                CSAppException.apperr(UUException.CRM_UU_27);
            }
            for (int i = 0, size = byhtds.size(); i < size; i++)
            {
                IData yht = (IData) byhtds.get(i);
                if (serial_number.equals(yht.getString("BSERIAL_NUMBER")))
                {
                    CSAppException.apperr(UUException.CRM_UU_26);
                }
            }
        }

        // 产品参数信息
        IData productParam = new DataMap();
        productParam.put("zyht", zyhtds.toString());
        productParam.put("byht", byhtds.toString());
        productParam.put("Z_TAG_VAL", batData.getString("DATA19")); // 同振0；顺振1

        IDataset productParamDataset = new DatasetList();
        buildProductParam(condData.getString("PRODUCT_ID"), productParam, productParamDataset);
        batData.put("PRODUCT_ID", condData.getString("PRODUCT_ID"));
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
        svcData.put("MEM_ROLE_B", batData.getString("ROLE_CODE_B", "1"));
        svcData.put("PRODUCT_ID", IDataUtil.getMandaData(condData, "PRODUCT_ID"));
        svcData.put("ELEMENT_INFO", new DatasetList(condData.getString("ELEMENT_INFO")));
        svcData.put("PRODUCT_PARAM_INFO", batData.getDataset("PRODUCT_PARAM_INFO"));
    }

    /**
     * 作用：主叫一号通字符串转Dataset
     * 
     * @param str
     * @throws Exception
     */
    public IDataset getByhtData2Dataset(String str) throws Exception
    {
        IDataset ds = new DatasetList();
        if (StringUtils.isNotBlank(str))
        {
            String[] strs = StringUtils.split(str, ";");
            for (int i = 0, size = strs.length; i < size; i++)
            {
                IData data = new DataMap();
                String userid = querySNInfo(strs[i].trim());
                data.put("BSERIAL_NUMBER", strs[i].trim());
                data.put("BUSERID", userid);
                ds.add(data);
            }
        }

        return ds;
    }

    /**
     * 作用：主叫一号通字符串转Dataset
     * 
     * @param str
     * @throws Exception
     */
    public IDataset getZyhtData2Dataset(String str, String str2) throws Exception
    {
        IDataset ds = new DatasetList();
        if (StringUtils.isNotBlank(str))
        {
            String[] strs = StringUtils.split(str, ";");
            for (int i = 0, size = strs.length; i < size; i++)
            {
                IData data = new DataMap();
                String userid = querySNInfo(strs[i].trim());
                data.put("SERIAL_NUMBER", strs[i].trim());
                if (strs[i].trim().equals(str2.trim()))
                {
                    data.put("MAIN_FLAG_CODE", "1"); // 主显标志
                }
                else
                {
                    data.put("MAIN_FLAG_CODE", "0");
                }
                data.put("ZUSERID", userid);
                ds.add(data);
            }
        }

        return ds;
    }

    /**
     * 查询号码个人用户信息
     * 
     * @author luoyong
     * @throws Throwable
     */
    public String querySNInfo(String strMebSn) throws Exception
    {
        String memEparchCode = getMebUcaData().getUser().getEparchyCode(); // 成员所属地市编码
        String memSn = getMebUcaData().getSerialNumber(); // 成员sn

        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", strMebSn);

        IData userInfos = UserInfoQry.getUserInfoBySN(strMebSn);
        if (IDataUtil.isEmpty(userInfos))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_472, strMebSn);
        }

        String strEparchCode = userInfos.getString("EPARCHY_CODE");

        // 没有长途拨打权限的不能加外地号码作为副号码
        if (!ctFlag)
        {
            if (!memEparchCode.equals(strEparchCode))
            {
                CSAppException.apperr(FuncrightException.CRM_FUNCRIGHT_9, memSn, strMebSn);
            }
        }
        String userid = userInfos.getString("USER_ID", "");
        return userid;
    }
}
