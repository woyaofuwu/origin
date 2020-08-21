
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.common;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.msg.SaleActiveBreConst;

/**
 * 仅仅给营销活动校验接口调用，通过传入的终端机型看是否可以办理传入的活动！
 * 
 * @author Mr.Z
 */
public class CheckSaleActiveByDeviceModelCode extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 3991303554176994462L;

    private static Logger logger = Logger.getLogger(CheckSaleActiveByDeviceModelCode.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step in CheckSaleActiveByDeviceModelCode() >>>>>>>>>>>>>>>>>>");
        }

        String activeCheckMode = databus.getString("ACTIVE_CHECK_MODE");

        if (!SaleActiveBreConst.ACTIVE_CHECK_MODE_INTF_CHEK.equals(activeCheckMode))
        {
            return true;
        }

        String deviceModelCode = databus.getString("DEVICE_MODEL_CODE");
        String resTypeId = databus.getString("RES_TYPE_ID");

        String terminalId = databus.getString("TERMINAL_ID");

        if (StringUtils.isBlank(deviceModelCode) && StringUtils.isBlank(terminalId))
        {
            return true;
        }

        String packageId = databus.getString("PACKAGE_ID");
//        IDataset packageExtDataset = PkgExtInfoQry.queryPackageExtInfo(packageId, databus.getString("EPARCHY_CODE"));
        IData packageExtData = databus.getData("PM_OFFER_EXT");//packageExtDataset.getData(0);
//      IData packageData = PkgInfoQry.getPackageByPK(packageId);
      IData packageData = databus.getData("PM_OFFER");//UpcCall.qryOfferComChaTempChaByCond(packageId, BofConst.ELEMENT_TYPE_CODE_PACKAGE);
      packageExtData.put("RSRV_TAG1", packageData.getString("RSRV_TAG1"));
      packageExtData.put("RSRV_STR3", packageData.getString("RSRV_STR3"));
      packageExtData.put("PRODUCT_ID", databus.getString("PRODUCT_ID"));

        IData terminalInfo = new DataMap();

        if (StringUtils.isNotBlank(deviceModelCode))
        {
            IData param = new DataMap();
            param.put("DEVICE_MODEL_CODE", deviceModelCode);
            param.put("RES_TYPE_ID", resTypeId);
            param.put("TM_CALL_TYPE", "1");
            param.put(Route.ROUTE_EPARCHY_CODE, databus.getString("EPARCHY_CODE"));
            IDataset terminalInfos = CSAppCall.call("CS.TerminalQuerySVC.getTerminalByDeviceModelCode", param);
            terminalInfo = terminalInfos.getData(0);
            String xResultCode = terminalInfo.getString("X_RESULTCODE","0");
            
            if(!StringUtils.equals("0", xResultCode))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14101001, terminalInfo.getString("X_RESULTINFO"));
                //return false;
            }
        }
        else if (StringUtils.isNotBlank(terminalId))
        {
            IData param = new DataMap();
            param.put("RES_NO", terminalId);
            param.put("PRODUCT_ID", databus.getString("PRODUCT_ID"));
            param.put("CAMPN_TYPE", databus.getString("CAMPN_TYPE"));
            param.put("TM_CALL_TYPE", "1");
            param.put(Route.ROUTE_EPARCHY_CODE, databus.getString("EPARCHY_CODE"));
            IDataset terminalInfos = CSAppCall.call("CS.TerminalQuerySVC.getTerminalByResNoOnly", param);
            terminalInfo = terminalInfos.getData(0);
            String xResultCode = terminalInfo.getString("X_RESULTCODE","0");
            
            if(!StringUtils.equals("0", xResultCode))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14101002, terminalInfo.getString("X_RESULTINFO"));
                //return false;
            }
        }

        IData svcParam = new DataMap();

        svcParam.put("SALE_ACTIVES", IDataUtil.idToIds(packageExtData));
        svcParam.put("DEVICE_TYPE_CODE", terminalInfo.getString("TERMINAL_TYPE_CODE",""));
        svcParam.put("DEVICE_MODEL_CODE", deviceModelCode);
        svcParam.put("DEVICE_COST", terminalInfo.getString("DEVICE_COST","0"));
        svcParam.put("SALE_PRICE", terminalInfo.getString("SALE_PRICE","0"));
        svcParam.put(Route.ROUTE_EPARCHY_CODE, databus.getString("EPARCHY_CODE"));

        IDataset salePackages = CSAppCall.call("CS.SalePackagesFilteSVC.filterPackagesByTerminalConfig", svcParam);

        if (IDataUtil.isEmpty(salePackages))
        {
            if (StringUtils.isNotBlank(deviceModelCode))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14070901, "终端机型[" + deviceModelCode + "]不能办理当前营销包！");
            }
            else if (StringUtils.isNotBlank(terminalId))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14070901, "终端串号[" + terminalId + "]不能办理当前营销包！");
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step out CheckSaleActiveByDeviceModelCode() >>>>>>>>>>>>>>>>>>");
        }

        return true;
    }

}
