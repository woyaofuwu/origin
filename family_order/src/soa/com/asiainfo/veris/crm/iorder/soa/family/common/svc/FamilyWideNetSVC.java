
package com.asiainfo.veris.crm.iorder.soa.family.common.svc;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.iorder.pub.consts.KeyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyRolesEnum;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.FamilyCallerBean;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.FamilyMemUtil;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

public class FamilyWideNetSVC extends CSBizService
{

    /**
     * @author duhj
     */
    private static final long serialVersionUID = 1L;

    /**
     * 判断当前角色下是否有子角色
     * 固话、tv 和宽带没有关系，但装机得依赖宽带，还是根据手机去查
     * @param input
     * @return
     * @throws Exception
     */
    public IData checkIsHasChildrenRole(IData input) throws Exception
    {
        IData result = new DataMap();
        String phoneSn = input.getString("SERIAL_NUMBER");
        String roleCode = input.getString("ROLE_CODE");
        IData userInfoData = UcaInfoQry.qryUserInfoBySn(phoneSn);
        if (IDataUtil.isNotEmpty(userInfoData))
        {
            if ("1".equals(roleCode))
            {
                IDataset wideUserList = FamilyMemUtil.queryWideMembers(phoneSn);
                if (IDataUtil.isNotEmpty(wideUserList))
                {
                    result.put("IS_HAS_WIDE", "Y");
                }
            }
            else if ("2".equals(roleCode))
            {
                IDataset imsUserList = FamilyMemUtil.queryImsMembersOfPhone(phoneSn);// 当前一个手机一个固话
                if (IDataUtil.isNotEmpty(imsUserList))
                {
                    result.put("IS_HAS_IMS", "Y");
                    result.put("USER_IMS_NUM", imsUserList.getData(0).getString("SERIAL_NUMBER"));
                    result.put("USER_IMS_USER_ID", imsUserList.getData(0).getString("USER_ID"));
                }

                IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1(userInfoData.getString("USER_ID"), "4", "J");
                //查询用户有效的平台服务
                IDataset platSvcInfosOtt = UserPlatSvcInfoQry.getPlatSvcByUserBizType(userInfoData.getString("USER_ID"), "51");// biz_type_code=51未来电视
                IDataset platSvcInfosIptv = UserPlatSvcInfoQry.getPlatSvcByUserBizType(userInfoData.getString("USER_ID"), "86");// biz_type_code=51IPTV

                if((IDataUtil.isEmpty(platSvcInfosOtt) && IDataUtil.isEmpty(platSvcInfosIptv)) || IDataUtil.isEmpty(boxInfos)){
                    result.put("IS_HAS_TV", "N");
                }else{
                    result.put("IS_HAS_TV", "Y");
                }                              
            }

        }

        return result;
    }
    
    /**
     * 查询该宽带基本信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData getUserWideNetInfos(IData input) throws Exception
    {
        IData result = new DataMap();
        // 查询手机号码的宽带用户信息
        IData wUserInfo = UcaInfoQry.qryUserInfoBySn(input.getString("WIDE_SERIAL_NUMBER"));

        if (IDataUtil.isEmpty(wUserInfo))
        {
            CSAppException.appError("-1", "该用户没有办理宽带！");
        }

        String wideUserId = wUserInfo.getString("USER_ID");// 宽带USER_ID
        // 用户宽带资料
        IDataset widenetInofs = WidenetInfoQry.getUserWidenetInfo(wideUserId);
        if (IDataUtil.isEmpty(widenetInofs))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询用户宽带信息出错：无记录！");
        }

        IDataset userProducts = UserProductInfoQry.queryMainProductNow(wideUserId);
        if (IDataUtil.isEmpty(userProducts))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_224);
        }
        String wideProductName = UProductInfoQry.getProductNameByProductId(userProducts.getData(0).getString("PRODUCT_ID"));
        String widetype = widenetInofs.getData(0).getString("RSRV_STR2");// 宽带类型
        String wideTypeName =FamilyConstants.WIDENET_TYPE.get(widetype);             
        String productmode = WideNetUtil.getWideProductMode(widetype);
        result.put("WIDE_USER_ID", wideUserId);
        result.put("STAND_ADDRESS", widenetInofs.getData(0).getString("STAND_ADDRESS"));
        result.put("DETAIL_ADDRESS", widenetInofs.getData(0).getString("DETAIL_ADDRESS"));
        result.put("STAND_ADDRESS_CODE", widenetInofs.getData(0).getString("STAND_ADDRESS_CODE"));
        result.put("USER_WIDE_PRODUCT_ID", userProducts.getData(0).getString("PRODUCT_ID"));
        result.put("USER_WIDE_PRODUCT_NAME", wideProductName);
        result.put("USER_WIDE_PRODUCT_TYPE", widetype);
        result.put("USER_WIDE_PRODUCT_TYPE_NAME", wideTypeName);
        result.put("USER_WIDE_PRODUCT_MODE", productmode);
        result.put("USER_WIDE_AREA_CODE", widenetInofs.getData(0).getString("RSRV_STR4"));

        return result;

    }

    /**
     * 查询手机下的角色信息 duhj
     * 固话和TV
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryRoleUserList(IData input) throws Exception
    {
        String phoneSn = input.getString("SERIAL_NUMBER");
        String roleCode = input.getString("ROLE_CODE");
        IDataset roleUserList = new DatasetList();
        IData phoneUserInfo = UcaInfoQry.qryUserInfoBySn(phoneSn);
        if (IDataUtil.isEmpty(phoneUserInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_472, phoneSn);
        }

        if (FamilyRolesEnum.WIDENET.getRoleCode().equals(roleCode))
        {
            roleUserList = FamilyMemUtil.queryWideMembers(phoneSn);
        }
        else if (FamilyRolesEnum.IMS.getRoleCode().equals(roleCode))
        {
            roleUserList = FamilyMemUtil.queryImsMembersOfPhone(phoneSn);
        }

        if (IDataUtil.isNotEmpty(roleUserList))
        {
            for (int i = 0, roleSize = roleUserList.size(); i < roleSize; i++)
            {
                IData temp = roleUserList.getData(i);
                IDataset userProducts = UserProductInfoQry.queryMainProductNow(temp.getString("USER_ID"));
                if (IDataUtil.isNotEmpty(userProducts))
                {
                    String roleProductName = UProductInfoQry.getProductNameByProductId(userProducts.getData(0).getString("PRODUCT_ID"));
                    temp.put("ROLE_PRODUCT_NAME", roleProductName);
                    temp.put("ROLE_SERIAL_NUMBER", temp.getString("SERIAL_NUMBER"));
                    temp.put("ROLE_USER_ID", temp.getString("USER_ID"));
                }
            }
        }

        return roleUserList;
    }
    
    /**
     * @Description: 添加成员校验，宽带和固话、tv
     * @Param: [input]
     * @return: com.ailk.common.data.IDataset
     * @Author: duhj
     * @Date: 
     */
    public IDataset checkAddChilrenRole(IData input) throws Exception
    {
        IDataset results = new DatasetList();
        IData result = new DataMap();
        results.add(result);
        String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
        String roleCode = IDataUtil.chkParam(input, "ROLE_CODE");
        String roleType = IDataUtil.chkParam(input, "ROLE_TYPE");
        String tradeTypeCode = IDataUtil.chkParam(input, "TRADE_TYPE_CODE");
        input.put(KeyConstants.ROLE_CODE, roleCode);
        input.put(KeyConstants.ROLE_TYPE, roleType);
        input.put(KeyConstants.BUSI_TYPE, tradeTypeCode);
        FamilyCallerBean.busiCheckNoCatch(input, FamilyConstants.TriggerPoint.ADD_MEMBER.toString());
        result.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        return results;
    }

}
