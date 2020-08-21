
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.FuncrightException;
import com.asiainfo.veris.crm.order.pub.exception.UUException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat.util.GroupBatTransUtil;

public class BatGrpMemYhtCreateTrans implements ITrans
{
    private boolean ctFlag = false;

    @Override
    public void transRequestData(IData batData) throws Exception
    {

        // 校验请求参数
        checkRequestDataSub(batData);

        // 构建服务请求数据
        builderSvcData(batData);
    }

    protected void checkRequestDataSub(IData batData) throws Exception
    {
        IData condData = batData.getData("condData", new DataMap());

        IDataUtil.chkParam(condData, "PLAN_TYPE");

        IDataUtil.chkParam(condData, "MEM_ROLE_B");

        IDataUtil.chkParam(condData, "PRODUCT_ID");

        IDataUtil.chkParam(condData, "ELEMENT_INFO");

        IDataUtil.chkParam(condData, "USER_ID");

        String serial_number = IDataUtil.chkParam(batData, "SERIAL_NUMBER");

        IData userInfo = UserInfoQry.getUserInfoBySN(serial_number);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_472, userInfo);
        }

        String eparchyCode = userInfo.getString("EPARCHY_CODE");

        // 六、判断是否可以拨打国内长途 14
        boolean result = RouteInfoQry.isChinaMobile(serial_number);
        if (result)
        {
            // 手机号码是否订购拨打长途服务
            IDataset ds = UserSvcInfoQry.qrySvcInfoByUserIdSvcId(userInfo.getString("USER_ID"), "14");
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
        IDataset zyhtds = getZyhtData2Dataset(zyht_serialNumber, zyht_serialNumber, eparchyCode, serial_number);

        // 被叫一号通数据
        String byht_serialNumber = batData.getString("DATA18"); // 被叫一号通数据
        IDataset byhtds = getByhtData2Dataset(byht_serialNumber, eparchyCode, serial_number);

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
        productParam.put("zpause", "1");// 0暂停、1恢复
        productParam.put("bpause", "1");
        IDataset productParamDataset = new DatasetList();
        GroupBatTransUtil.buildProductParam(condData.getString("PRODUCT_ID"), productParam, productParamDataset);

        batData.put("PRODUCT_ID", condData.getString("PRODUCT_ID"));
        batData.put("PRODUCT_PARAM_INFO", productParamDataset);
    }

    protected void builderSvcData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());

        svcData.put("USER_ID", condData.getString("USER_ID"));
        svcData.put("SERIAL_NUMBER", batData.getString("SERIAL_NUMBER"));
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
    public IDataset getZyhtData2Dataset(String str, String str2, String eparchyCode, String serial_number) throws Exception
    {
        IDataset ds = new DatasetList();
        if (StringUtils.isNotBlank(str))
        {
            String[] strs = StringUtils.split(str, ";");
            for (int i = 0, size = strs.length; i < size; i++)
            {
                IData data = new DataMap();
                String userid = querySNInfo(strs[i].trim(), eparchyCode, serial_number);
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
     * 作用：主叫一号通字符串转Dataset
     * 
     * @param str
     * @throws Exception
     */
    public IDataset getByhtData2Dataset(String str, String eparchyCode, String serial_number) throws Exception
    {
        IDataset ds = new DatasetList();
        if (StringUtils.isNotBlank(str))
        {
            String[] strs = StringUtils.split(str, ";");
            for (int i = 0, size = strs.length; i < size; i++)
            {
                IData data = new DataMap();
                String userid = querySNInfo(strs[i].trim(), eparchyCode, serial_number);
                data.put("BSERIAL_NUMBER", strs[i].trim());
                data.put("BUSERID", userid);
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
    public String querySNInfo(String strMebSn, String eparchyCode, String serial_number) throws Exception
    {

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
            if (!eparchyCode.equals(strEparchCode))
            {
                CSAppException.apperr(FuncrightException.CRM_FUNCRIGHT_9, serial_number, strMebSn);
            }
        }
        String userid = userInfos.getString("USER_ID", "");
        return userid;
    }

}
