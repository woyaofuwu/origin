
package com.asiainfo.veris.crm.order.soa.person.busi.mobileterminal;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.PasswdMgr;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.queryinfo.InterfaceUtil;

/**
 * @author xuwb5
 */
public class CheckUserPWD4MobileBean extends CSBizBean
{
    public IData checkUserPWD(IData data) throws Exception
    {
        IData result = new DataMap();
        // IData params=new DataMap();
        IData res = UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER"));
        // IDataset res =UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(res))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1131);
        }
        String userId = res.getString("USER_ID");
        String userpasswd = res.getString("USER_PASSWD");
        /*
         * params.clear(); params.put("USER_PASSWD", data.getString("PASSWD")); params.put("USER_ID", userId);
         */
        if (StringUtils.isBlank(userpasswd))// 用户服务密码不存在
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1132);
        }
        if (!PasswdMgr.checkUserPassword(data.getString("PASSWD"), userId, userpasswd))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_401);
        }
        String userName = "";
        String userBrand = "";
        String userLevel = "";
        IData custData = UcaInfoQry.qryCustInfoByCustId(res.getString("CUST_ID"));
        // IDataset custDataset = CustomerInfoQry.getCustInfoByPK( ((IData) res.get(0)).getString("CUST_ID"));
        if (IDataUtil.isEmpty(custData))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1134);
        }
        else
        {
            userName = custData.getString("CUST_NAME", "");// 客户名称
        }
        IDataset userProductDataset = UserProductInfoQry.queryMainProductNow(userId);

        if (IDataUtil.isEmpty(userProductDataset))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1135);
        }
        else
        {
            userBrand = userProductDataset.getData(0).getString("BRAND_CODE", "");// 品牌
        }

        IDataset custVipDataset = CustVipInfoQry.qryVipInfoByCustId(custData.getString("CUST_ID"));

        if (IDataUtil.isNotEmpty(custVipDataset))
        {
            userLevel = custVipDataset.getData(0).getString("VIP_CLASS_ID");// VIP等级
        }

        String userStatus = res.getString("USER_STATE_CODESET");

        userBrand = InterfaceUtil.transBrand(userBrand);
        userLevel = InterfaceUtil.getCustLevelParam(userLevel);
        userStatus = InterfaceUtil.getUserStateParam(userStatus);

        result.put("USER_NAME", userName);
        result.put("USER_BRAND", userBrand);
        result.put("USER_LEVEL", userLevel);
        result.put("USER_STATUS", userStatus);

        return result;
    }
}
