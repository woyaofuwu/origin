
package com.asiainfo.veris.crm.order.soa.person.busi.rentmobile;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class RentMobileSVC extends CSBizService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1143393207030086435L;

    /**
     * 查询用户租机信息
     * 
     * @Function: getRentMobile
     * @Description: TODO
     * @date May 28, 2014 4:34:29 PM
     * @param data
     * @return
     * @throws Exception
     * @author longtian3
     */
    public IDataset getRentMobile(IData data) throws Exception
    {
        RentMobileBean bean = new RentMobileBean();
        String rentTag = data.getString("RENT_TAG", "");
        IDataset result = bean.getRentMobile(data);

        /** 租机结算 **/
        if (StringUtils.isBlank(rentTag) && IDataUtil.isNotEmpty(result))
        {
            String rsrvdate = result.getData(0).getString("RSRV_DATE", ""); // 结算时间
            String startdate = result.getData(0).getString("START_DATE"); // 开始时间
            boolean isBefore = SysDateMgr.string2Date(rsrvdate, SysDateMgr.PATTERN_STAND).before(SysDateMgr.string2Date(SysDateMgr.getSysTime(), SysDateMgr.PATTERN_STAND));
            // 结算时间在当前时间之前
            if (isBefore && !rsrvdate.equals(startdate) && StringUtils.substring(rsrvdate, 0, 10).equals(SysDateMgr.getSysDate()))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_508);
            }
        }
        return result;
    }

    /**
     * 查询SIM_CARD_NO、IMSI
     * 
     * @Function: getUserResInfo
     * @Description: TODO
     * @date May 28, 2014 4:32:54 PM
     * @param data
     * @return
     * @throws Exception
     * @author longtian3
     */
    public IDataset getUserResInfo(IData data) throws Exception
    {
        RentMobileBean bean = new RentMobileBean();
        return bean.getUserResInfo(data);
    }

    /**
     * 查询押金信息
     * 
     * @Function: queryForegiftInfo
     * @Description: TODO
     * @date May 28, 2014 4:36:08 PM
     * @param data
     * @return
     * @throws Exception
     * @author longtian3
     */
    public IDataset queryForegiftInfo(IData data) throws Exception
    {
        RentMobileBean bean = new RentMobileBean();
        return bean.queryForegiftInfo(data);
    }

    /**
     * 查询用户是否有租机
     * 
     * @Function: queryRentMobile
     * @Description: TODO
     * @date May 28, 2014 4:33:23 PM
     * @param data
     * @return
     * @throws Exception
     * @author longtian3
     */
    public IDataset queryRentMobile(IData data) throws Exception
    {
        RentMobileBean bean = new RentMobileBean();
        return bean.queryRentMobile(data);
    }

    public IDataset queryRentTagInfo(IData data) throws Exception
    {
        RentMobileBean bean = new RentMobileBean();
        return bean.queryRentTagInfo(data);
    }
}
