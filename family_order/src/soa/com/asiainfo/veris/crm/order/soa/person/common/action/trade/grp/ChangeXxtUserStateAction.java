
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.grp;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.group.groupintf.credit.GrpCreditBaseBean;

/**
 * Copyright: Copyright (c) 2018 Asiainfo
 * 
 * @ClassName: ChangeXxtUserStateAction.java
 * @Description: 服务状态变更处理和校园本省异网号码用户资料
 * @version: v1.0.0
 * @author: zhengdx
 * @date: 2018-3-30 10:05:29
 */
public class ChangeXxtUserStateAction implements ITradeAction
{
    /**
     * 集团接口返回值 <IDataset> <IData> <key>GRP_TRADE_DATA</key> <value> List </value> ... .... </IData> </IDataset>
     * 如果没有需要处理的iTrade请直接new IDatasetList()对象出来。
     */
    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
    	 UcaData uca = btd.getRD().getUca();
    
    	String userId = uca.getUserId();
        String serialNumber = uca.getSerialNumber();
        String tradeTypeCode = btd.getTradeTypeCode();
        String productId = uca.getProductId();
        String brandCode=uca.getBrandCode();
 
        //和校园业务本省异网号码才进行信控停开机处理
        if (!"XYYW".equals(brandCode)) {
        	return;
        }
        
        GrpCreditBaseBean bean = new GrpCreditBaseBean();
        IData inParam =  new DataMap();
        inParam.put("USER_ID", userId);
        inParam.put("TRADE_TYPE_CODE", tradeTypeCode);
        List<BaseTradeData> retList = new ArrayList<BaseTradeData>();
        bean.GetOperationCode(inParam);
        bean.CreateBlackWhiteTrade(inParam, retList);
        if (null != retList && retList.size() > 0)
        {
            for (int i = 0; i < retList.size(); i++)
            {
                BaseTradeData tData = (BaseTradeData) retList.get(i);
                btd.add(serialNumber, tData);// 将集团返回的数据添加到busiTradeData中
            }
        }
    }

}
