
package com.asiainfo.veris.crm.order.soa.person.busi.np.restoreuser;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserNpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.DevicePriceQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductFeeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserNpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

public class RestoreUserNpBean extends CSBizBean
{

    /**
     * @Function: checkResource
     * @Description: 前台页面发启校验
     * @param param
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014年8月7日 下午4:06:08
     */
    public IData checkResource(IData param) throws Exception
    {

        String sn = param.getString("SERIAL_NUMBER");

        ResCall.chkNpMphoneInfo(sn, "2");// 号码校验

        return checkSimReource(param);// 校验sim卡
    }

    /**
     * @Function: checkSimReource
     * @Description: 校验sim卡资源并且配置卡费
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-4-23 下午3:03:35 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-23 lijm3 v1.0.0 修改原因
     */
    public IData checkSimReource(IData param) throws Exception
    {

        String sn = param.getString("SERIAL_NUMBER");
        IData returnData = new DataMap();
        IData resInfoData = new DataMap();

        String simCardNo = param.getString("SIM_CARD_NO");
        IDataset simInfos = ResCall.chkNpSimcardInfo(simCardNo, "2", sn);
        if (IDataUtil.isNotEmpty(simInfos))
        {
            String strCardKindCode = simInfos.getData(0).getString("RES_KIND_CODE", "");
            String resTypeCode = simInfos.getData(0).getString("RES_TYPE_CODE").substring(0, 1);

            resInfoData.put("IMSI", simInfos.getData(0).getString("IMSI", ""));
            resInfoData.put("KI", simInfos.getData(0).getString("KI", ""));
            resInfoData.put("CAPACITY_TYPE_CODE", simInfos.getData(0).getString("CAPACITY_TYPE_CODE", ""));// 卡空量
            resInfoData.put("OPC", simInfos.getData(0).getString("OPC", ""));
            resInfoData.put("SIM_CARD_NO", simInfos.getData(0).getString("SIM_CARD_NO", ""));
            resInfoData.put("START_DATE", SysDateMgr.getSysTime());
            resInfoData.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
            resInfoData.put("RES_TYPE_CODE", simInfos.getData(0).getString("RES_TYPE_CODE"));

            IData feeData = DevicePriceQry.getDevicePrice(BizRoute.getRouteId(), "-1", "39", strCardKindCode, resTypeCode);
            if (IDataUtil.isNotEmpty(feeData))
            {
                returnData.put("FEE_MODE", "0");
                returnData.put("FEE_TYPE_CODE", feeData.getString("FEEITEM_CODE"));
                returnData.put("FEE", feeData.getString("DEVICE_PRICE"));
            }
        }
        else
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "资源接口校验SIM卡失败");
        }
        returnData.put("RES_INFO_DATA", resInfoData);
        return returnData;
    }

    public IDataset filterProductType(String parent_ptype_code, String productId) throws Exception
    {
        IDataset productTypes = getProductCatalog(parent_ptype_code);
        IDataset ids = ProductTypeInfoQry.getProductTypeByProductId(productId);
        if (IDataUtil.isNotEmpty(ids))
        {
            String productTypeCode = ids.getData(0).getString("PRODUCT_TYPE_CODE", "");
            if (IDataUtil.isNotEmpty(productTypes))
            {
                for (int i = 0; i < productTypes.size(); i++)
                {
                    IData data = productTypes.getData(i);
                    String _productTypeCode = data.getString("PRODUCT_TYPE_CODE");
                    if (!productTypeCode.equals(_productTypeCode))
                    {
                        productTypes.remove(i);
                        i--;
                    }
                }
            }
        }
        return productTypes;
    }

    public IDataset filterShortPhone(IDataset resDataset) throws Exception
    {
        if (!resDataset.isEmpty())
        {
            for (int i = 0; i < resDataset.size(); i++)
            {
                if ("S".equals(resDataset.getData(i).getString("RES_TYPE_CODE")))
                {
                    resDataset.remove(i);
                    i--;
                }
            }
        }
        return resDataset;
    }

    /**
     * @Function: getNewUserTagSet
     * @Description: 对应完工流程RestoreUserForNp
     * @param asp
     * @param userId
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-6-7 下午3:49:51
     */
    public String getNewUserTagSet(String userId) throws Exception
    {

        IData userInfo = UcaInfoQry.qryUserInfoByUserId(userId);

        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "519 查询需要恢复的用户基本资料异常！");
        }

        IDataset sns = TradeNpQry.getValidTradeNpBySn(userInfo.getString("SERIAL_NUMBER"));
        if (IDataUtil.isNotEmpty(sns) && sns.size() != 1)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "520 该号码查询号段资源表异常！");
        }

        String asp = sns.getData(0).getString("ASP");

        String userTagSet = userInfo.getString("USER_TAG_SET");
        String newTagSet = "";
        if (StringUtils.isBlank(userTagSet))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "512 用户携转标识USER_TAG_SET为空异常！");
        }
        else if (userTagSet.length() == 1)
        {
            if ("4".equals(userTagSet) && "1".equals(asp))// 4-已携出
            {
                newTagSet = "6";// 6-携回
            }
            else if ("8".equals(userTagSet) && !"1".equals(asp))// 8-携入携出
            {
                newTagSet = "1";// 1-已携入
            }
            else
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, " 522 该用户当前携转标识USER_TAG_SET=【%s】，该状态不能携入复机！", userTagSet);
            }
        }
        else if (userTagSet.length() > 1)
        {
            if ("4".equals(userTagSet.substring(0, 1)) && "1".equals(asp))// 4-已携出
            {
                newTagSet = "6" + userTagSet.substring(1);// 6-携回
            }
            else if ("8" == userTagSet.substring(0, 1) && !"1".equals(asp))// 8-携入携出
            {
                newTagSet = "1" + userTagSet.substring(1);// 1-已携入
            }
            else
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "523 该用户当前携转标识USER_TAG_SET=【%s】，该状态不能携入复机！", userTagSet);
            }
        }

        return newTagSet;
    }

    public IDataset getProductCatalog(String parent_ptype_code) throws Exception
    {
        return ProductInfoQry.getProductsType(parent_ptype_code, null);
    }

    /**
     * @Function: getProductFeeInfo
     * @Description: 获取产品费用 老系统查产品费用，按业务类型10查的，数据库里没有39的配置
     * @param data
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-6-3 下午4:16:56
     */
    public IDataset getProductFeeInfo(IData data) throws Exception
    {

        String product_id = data.getString("PRODUCT_ID");
        String brand_code = data.getString("BRAND_CODE");
        String eparchy_code = CSBizBean.getUserEparchyCode();
        IDataset dataset = ProductFeeInfoQry.getProductFeeInfo("10", product_id, "-1", "-1", "P", "3", eparchy_code);
        return dataset;

    }

    public IDataset getResInfos(String userId) throws Exception
    {

        IDataset ids = UserResInfoQry.getUserResMaxDateByUserId(userId);
        ids = filterShortPhone(ids);

        if (IDataUtil.isEmpty(ids))
        {

            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "根据userId【" + userId + "】在 TF_F_USER_RES表里获取数据有误！");
        }

        for (int i = 0, len = ids.size(); i < len; i++)
        {
            IData data = ids.getData(i);
            String resTypeCode = data.getString("RES_TYPE_CODE");
            // 手机号码
            if ("0".equals(resTypeCode))
            {
                data.put("SERIAL_NUMBER", data.getString("RES_CODE"));
                data.put("NEW_SERIAL_NUMBER", data.getString("RES_CODE"));

                // 携入复机使用原号码，此时移动号码资源状态为已携出，非移动号码则删除记录了
                data.put("MODIFY_TAG", "3");// 0-增加 1-删除 2-修改 3-复机恢复

            }

            // sim卡
            if ("1".equals(resTypeCode))
            {
                data.put("MODIFY_TAG", "1");
            }

        }
        return ids;
    }

    public IData getUserNpInfos(IData param) throws Exception
    {
        String serialNumber = param.getString("SERIAL_NUMBER");

        String userId = param.getString("USER_ID");
        IDataset nps = UserNpInfoQry.qryUserNpInfosByUserId(userId);

        if (IDataUtil.isEmpty(nps))
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_540017, serialNumber);
        }
        else if (nps.size() > 1)
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_540018, serialNumber);
        }

        IDataset msisdnInfos = TradeNpQry.getValidTradeNpBySn(serialNumber);

        if (IDataUtil.isEmpty(msisdnInfos))
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_540019, serialNumber);
        }

        IData info = new DataMap();
        info.putAll(nps.getData(0));
        info.putAll(msisdnInfos.getData(0));
        return info;
    }

    /**
     * 判断现有的销户用户和在网用户的状态
     * 
     * @param pd
     * @param td
     * @return
     * @throws Exception
     */
    public IData prepareForRes(String serialNumber, String netTypeCode, String userId) throws Exception
    {
        IData result = new DataMap();
        // 通过SEL_BY_SN_DESTROY，查询最后销户的用户（如果有在网用户则返回为空）
        IDataset dataset = UserInfoQry.getDestroyUserInfoBySn(serialNumber);
        // 如果上面查询的结果为空则查询是否存在在网用户（因为存在没有任何用户的可能）
        if (IDataUtil.isEmpty(dataset))
        {
            IDataset infos = UserInfoQry.getUserInfoBySN(serialNumber, "0", netTypeCode);
            if (IDataUtil.isEmpty(infos))
            {
                result.put("HAVE_ONLINE_USER", false);
            }
            else
            {
                result.put("HAVE_ONLINE_USER", true);
            }
        }
        // 如果查询有记录则将userid保存起来
        else
        {
            result.put("LAST_DESTROYED_USER", dataset.getData(0).getString("USER_ID"));
        }

        if (result.getBoolean("HAVE_ONLINE_USER"))
        {
            result.put("NEED_CHANGE_NUMBER", true);
            result.put("NEED_CHANGE_SIM", true);
            result.put("CHANGE_REASON_CODE", "0");
            result.put("RESET_REASON", "该号码存在在网用户，请改号并换卡！");
        }
        else
        {
            // 该用户不是最后一个使用该号的用户
            if (!userId.equals(result.getString("LAST_DESTROYED_USER")))
            {
                result.put("NEED_CHANGE_NUMBER", true);
                result.put("NEED_CHANGE_SIM", true);
                result.put("CHANGE_REASON_CODE", "1");
                result.put("RESET_REASON", "该号码已经被其他用户使用过，无优先使用该号码等级，请改号并换卡！");
            }
            else
            {
                // checkPhoneNumber(serialNumber, result);
                IDataset datas = ResCall.chkNpMphoneInfo(serialNumber, "2");// 号码校验
            }
        }
        return result;
    }

    /**
     * queryUserInfo 查询客户列表
     * 
     * @param cycle
     * @throws Exception
     */
    public IDataset queryUserList(IData param) throws Exception
    {

        String serial_number = param.getString("SERIAL_NUMBER");
        IDataset infos = UserInfoQry.getDestroyedUserInfo(serial_number);

        if (IDataUtil.isEmpty(infos))
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_540010, serial_number);
        }

        int len = infos.size();
        if (len != 1)
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_540016, serial_number);
        }

        for (int i = 0; i < len; i++)
        {

            IData params = (IData) infos.get(i);
            String custId = params.getString("CUST_ID");

            IData custs = UcaInfoQry.qryPerInfoByCustId(custId);
            if (custs != null)
            {
                IData cust = custs;
                params.put("CUST_NAME", cust.getString("CUST_NAME", ""));
                params.put("PSPT_TYPE", cust.getString("PSPT_TYPE", ""));
            }
            else
            {
                CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "根据客户标识【" + custId + "】无法获取到客户信息！");
            }
        }

        return infos;
    }

}
