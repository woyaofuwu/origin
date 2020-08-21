
package com.asiainfo.veris.crm.order.soa.person.busi.rentmobile;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.RentCodeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserForegiftInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserRentInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

public class RentMobileBean extends CSBizBean
{

    public IDataset getRentMobile(IData data) throws Exception
    {
        String userId = data.getString("USER_ID");
        String rentTag = data.getString("RENT_TAG", "");

        IDataset result = UserRentInfoQry.queryUserRentInfo(userId);
        if (IDataUtil.isNotEmpty(result))
        {
            IData rentData = result.getData(0);
            if ("1".equals(rentTag))
            {
                String rentSerialNumber = rentData.getString("RENT_SERIAL_NUMBER");
                String rentPhone = rentData.getString("PARA_CODE2");
                CSAppException.apperr(CrmUserException.CRM_USER_324, rentSerialNumber, rentPhone);
            }
            else
            {
                String rentTypeCode = rentData.getString("RENT_TYPE_CODE");
                String rsrvDate = rentData.getString("RSRV_DATE"); // 结算时间
                String startDate = rentData.getString("START_DATE"); // 开始时间
                String rentTypeName = StaticUtil.getStaticValue(getVisit(), "TD_S_COMMPARA", new String[]
                { "SUBSYS_CODE", "PARAM_ATTR", "PARAM_CODE" }, "PARAM_NAME", new String[]
                { "CSM", "743", rentTypeCode });
                rentData.put("RENT_TYPE_NAME", rentTypeName);
                rentData.put("RENT_START_DATE", rsrvDate);
            }
        }
        else
        {
            if (StringUtils.isBlank(rentTag))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_325);
            }
        }
        return result;
    }

    public IDataset getUserResInfo(IData data) throws Exception
    {
        String userId = data.getString("USER_ID");
        IDataset resInfo = UserResInfoQry.queryUserResByUserIdResType(userId, "1");
        if (IDataUtil.isNotEmpty(resInfo))
        {
            IData param = resInfo.getData(0);
            param.put("SIM_CARD_NO", param.getString("RES_CODE"));
            param.put("IMSI_NO", param.getString("IMSI"));
            param.put("START_DATE", SysDateMgr.getSysTime());
        }

        return resInfo;
    }

    public IDataset queryForegiftInfo(IData data) throws Exception
    {
        IData result = new DataMap();
        String userId = data.getString("USER_ID");
        String rentTag = data.getString("RENT_TAG", "");
        if (StringUtils.isNotBlank(rentTag))
        {// 租机、退租
            IDataset custVip = CustVipInfoQry.qryVipInfoByUserId(userId);
            if (IDataUtil.isNotEmpty(custVip))
            {
                if ("1".equals(rentTag))
                {
                    String VipTypeCode = custVip.getData(0).getString("VIP_TYPE_CODE");
                    String VipClassId = custVip.getData(0).getString("VIP_CLASS_ID");
                    if ("5".equals(VipTypeCode) && ("3".endsWith(VipClassId) || "4".equals(VipClassId)))
                    {
                        result.put("MONEY", "0");
                    }
                    else
                    {
                        result.put("MONEY", "3000.00");
                    }
                }
            }
            else
            {
                if ("1".equals(rentTag))
                {
                    result.put("MONEY", "3000.00");
                }
            }
        }
        else
        {// 结算
            return UserForegiftInfoQry.getUserForegift(userId, "1");
        }
        return IDataUtil.idToIds(result);
    }

    public IDataset queryRentMobile(IData data) throws Exception
    {
        String rentTag = data.getString("RENT_TAG");
        String userId = data.getString("USER_ID");
        IDataset result = UserRentInfoQry.queryUserRentInfo(userId);
        if ("1".equals(rentTag))
        {
            if (IDataUtil.isNotEmpty(result))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_320);// 该用户已租用一部手机，请退租后再办理业务
            }
            String cityCode = getVisit().getCityCode();
            result = RentCodeInfoQry.queryRentMobile(data.getString("RENT_TYPE_CODE"), "0", cityCode);
        }

        return result;
    }

    public IDataset queryRentTagInfo(IData data) throws Exception
    {
        String rentTag = data.getString("RENT_TAG");
        IDataset infos = StaticUtil.getList(getVisit(), "TD_S_COMMPARA", "PARAM_CODE", "PARAM_NAME", new String[]
        { "SUBSYS_CODE", "PARAM_ATTR", "PARAM_CODE" }, new String[]
        { "CSM", "742", rentTag });
        return infos;
    }
}
