
package com.asiainfo.veris.crm.order.soa.person.busi.multisnonecard;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

public class MultiSNOneCardBean extends CSBizBean
{
    /**
     * 校验
     *
     * @param params
     * @return
     * @throws Exception
     */
    public IDataset getCheck(IData params) throws Exception
    {
        String userId = params.getString("USER_ID");
        IDataset userPro = UserProductInfoQry.getProductInfo(userId, "-1");
        String brand_code = userPro.getData(0).getString("BRAND_CODE");
        // 是否是全球通用户
        if (!brand_code.equals("G001"))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户不是全球通用户,不能办理该业务");
        }
        
        //-----移动员工不能办理----------
        boolean isCmccStaffUser = RelaUUInfoQry.isCMCCstaffUserNew(userId);
        if(isCmccStaffUser){
        	CSAppException.apperr(FamilyException.CRM_FAMILY_16316);
        }
        
        IDataset userResInfos = UserResInfoQry.queryUserResByUserIdResType(userId, "1");

        if (userResInfos.getData(0).getString("IMSI", "").startsWith("46007"))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "TD用户（IMSI为46007）,不能办理该业务!");
        }
        return userResInfos;
    }

    /**
     * 从配置表中查询服务类型信息
     *
     * @param params
     * @return
     * @throws Exception
     */
    public IDataset getCommonParam(IData params) throws Exception
    {
        String subsysCode = params.getString("SUBSYS_CODE");
        String paramAttr = params.getString("PARAM_ATTR");
        String paramCode = params.getString("PARAM_CODE");
        String eparchyCode = params.getString("EPARCHY_CODE");
        return CommparaInfoQry.getCommPkInfo(subsysCode, paramAttr, paramCode, eparchyCode);
    }

    /**
     * 从配置表中查询合作网络信息
     *
     * @param params
     * @return
     * @throws Exception
     * @author
     */
    public IDataset getCommonParam1(IData params) throws Exception
    {
        String subsysCode = params.getString("SUBSYS_CODE");
        String paramAttr = params.getString("PARAM_ATTR");
        String eparchyCode = params.getString("EPARCHY_CODE");
        return CommparaInfoQry.getCommByParaAttr(subsysCode, paramAttr, eparchyCode);
    }

    /**
     * 通过IBOSS查询已办理了国漫一卡多号业务的平台信息
     *
     * @param param
     * @return
     * @throws Exception
     * @author
     */
    public IDataset getMultiPlatInfo(IData param) throws Exception
    {
        String serialNumber = param.getString("SERIAL_NUMBER");
        String provinceA = "8981";
        String kindId = "BIP1A101_T1000101_0_0";
        String routeType = "00";
        String routeValue = "000";
        String routeEparchycode = "8980";
        // IData data = (IData) HttpHelper.callHttpSvc(pd, "TCS_CrmToPlat", inparam, true);//yangyztest
        IData data = IBossCall.queryOneCardMultiPlatInfo(serialNumber, provinceA, kindId, routeType, routeValue, routeEparchycode);
        // 经IBOSS同事确认，目前因平台原因，IBOSS接口无法测试，故此自己默认返回成功，自己造报文
        IDataset retDataset = new DatasetList();
        retDataset.add(data);

        return retDataset;

    }

    /**
     * 通过产品ID从产品配置表中获取产品信息
     *
     * @param params
     * @return
     * @throws Exception
     * @author
     */
    public IDataset getProductInfo(IData params) throws Exception
    {

        String productId = params.getString("PRODUCT_ID");
        return IDataUtil.idToIds(UProductInfoQry.qryProductByPK(productId));
    }

    /**
     * 查询已办理了国漫一卡多号业务的用户信息
     *
     * @param params
     * @return
     * @throws Exception
     * @author
     */
    public IDataset getUserOther(IData params) throws Exception
    {
        String userId = params.getString("USER_ID");
        String rsrvValueCode = params.getString("RSRV_VALUE_CODE");
        return UserOtherInfoQry.getUserOtherInfo(userId, rsrvValueCode);
    }
}
