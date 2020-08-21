
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;

public class CreateWorkPhoneGroupUser extends CreateGroupUser
{        
    private static transient Logger logger = Logger.getLogger(CreateWorkPhoneGroupUser.class);

    /**
     * 构造函数
     */
    public CreateWorkPhoneGroupUser()
    {

    }
    

    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
    }
    
    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData tradeData = bizData.getTrade();

        String curProductId = reqData.getUca().getProductId();
        // 获取参数
        IData paramData = reqData.cd.getProductParamMap(curProductId);

        tradeData.put("RSRV_STR6", paramData.getString("WORKPHONE_CODE", ""));// 产品类型
    }
    
    /**
     * @description 处理user表数据
     */
    public void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);
        String curProductId = reqData.getUca().getProductId();
        // 获取参数
        IData paramData = reqData.cd.getProductParamMap(curProductId);
        if (IDataUtil.isNotEmpty(paramData))
        {
            map.put("RSRV_STR6", paramData.getString("WORKPHONE_CODE", "")); // 产品类型
        }
    }

}
