
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.auth;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UDepartInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TerminalOrderInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class UserListSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 校验是否有终端预约信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset querySaleBook(IData input) throws Exception
    {
        IData returnData = new DataMap();
        IDataset returnSet = new DatasetList();
        boolean isAgent = false;
        String serialNumber = input.getString("SERIAL_NUMBER");
        IData departInfo = UDepartInfoQry.qryDepartByDepartId(getVisit().getDepartId());
        if (IDataUtil.isNotEmpty(departInfo))
        {
            String departKindCode = departInfo.getString("DEPART_KIND_CODE", "");
            if (!"100".equals(departKindCode) && !"500".equals(departKindCode))
            {
                isAgent = true;
            }
        }
        if (!isAgent && StringUtils.isNotBlank(serialNumber))
        {
            IDataset saleBooks = TerminalOrderInfoQry.qryTerminalOrderInfo(serialNumber);
            if (IDataUtil.isNotEmpty(saleBooks))
            {
                returnData.put("AUTH_BOOK_SALE", 1);
            }
            else
            {
                returnData.put("AUTH_BOOK_SALE", 0);
            }
        }
        else
        {
            returnData.put("AUTH_BOOK_SALE", 0);
        }
        returnSet.add(returnData);
        return returnSet;
    }

    /**
     * 查询终端预约客户列表
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset querySaleBookList(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");
        return TerminalOrderInfoQry.qryTerminalOrderInfo(serialNumber);
    }

    public IDataset queryUserBySn(IData input) throws Exception
    {
        IDataset userDataset = UserInfoQry.getUsersBySn(input.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(userDataset)) //复机查询多用户的情况下，需要再查询一次历史用户表
        {
            userDataset = UserInfoQry.qryAllUserInfoBySnFromHis(input.getString("SERIAL_NUMBER"));
        }
        
        return userDataset;
    }

    /**
     * 查询多用户
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryUserList(IData input) throws Exception
    {
        IDataset returnSet = new DatasetList();

        IData condition = new DataMap();

        condition.put("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
        condition.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));

        // 查询在线用户
        IDataset netUserDataset = UserInfoQry.getUsersBySn(input.getString("SERIAL_NUMBER"));
        if (IDataUtil.isNotEmpty(netUserDataset))
        {
            returnSet.addAll(netUserDataset);
        }
        // 查询历史用户
        IDataset hisUserDataset = UserInfoQry.qryAllUserInfoBySnFromHis(input.getString("SERIAL_NUMBER"));
        if (IDataUtil.isNotEmpty(hisUserDataset))
        {
            returnSet.addAll(hisUserDataset);
        }
        if (IDataUtil.isNotEmpty(returnSet))
        {
            for (int i = 0, count = returnSet.size(); i < count; i++)
            {
                IData params = returnSet.getData(i);
                String custId = params.getString("CUST_ID");

                IData cust = UcaInfoQry.qryPerInfoByCustId(custId);
                if (IDataUtil.isNotEmpty(cust))
                {
                    params.put("CUST_NAME", cust.getString("CUST_NAME", ""));
                    params.put("PSPT_TYPE", cust.getString("PSPT_TYPE", ""));
                }
            }
        }

        return returnSet;
    }
}
