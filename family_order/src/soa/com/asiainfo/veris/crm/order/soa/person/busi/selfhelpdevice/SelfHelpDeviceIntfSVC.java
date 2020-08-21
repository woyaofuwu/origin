
package com.asiainfo.veris.crm.order.soa.person.busi.selfhelpdevice;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;

/**
 * @Des 自助终端查询接口
 * 根据自助终端状态与终端工号或自助终端状态与终端编号查询，返回自助终端信息
 * @author huangsl
 *
 */
public class SelfHelpDeviceIntfSVC extends CSBizService
{
    public IDataset getSelfHelpDeviceInfo(IData data) throws Exception
    {
    	String getMode = IDataUtil.chkParam(data, "X_GETMODE");
        String resNo = IDataUtil.chkParam(data, "RES_NO");
        
        String tradeEparchyCode = data.getString("TRADE_EPARCHY_CODE");
        String routeEparchyCode = "";
        if (StringUtils.isNotBlank(data.getString(Route.ROUTE_EPARCHY_CODE)))
        	routeEparchyCode = data.getString(Route.ROUTE_EPARCHY_CODE);
        else{
        	routeEparchyCode = tradeEparchyCode;
        }
        setRouteId(routeEparchyCode);
      //终端设备状态为可传项，为空则查询出所有状态的终端设备，不为空则查询出传入状态下的终端设备
        String strRemoveTag = "";
        if (StringUtils.isNotBlank(data.getString("REMOVE_TAG"))){
            strRemoveTag = data.getString("REMOVE_TAG");
        }
        IDataset resultDs = new DatasetList();
        if("1".equals(getMode)){
        	resultDs = TradeResInfoQry.getSelfHelpDeviceInfoByDeviceId(resNo,strRemoveTag);
        }else if("0".equals(getMode)){
        	resultDs = TradeResInfoQry.getSelfHelpDeviceInfoByStaffId(resNo,strRemoveTag);
        }
        return resultDs;
    }
}
