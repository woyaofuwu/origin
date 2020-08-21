package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class GrpWideNetExportTask extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData paramIData, Pagination arg1) throws Exception
    {
        IData inparams = paramIData.subData("cond", true);
        System.out.print("wuhao+++++++++"+inparams);
        String sn = paramIData.getString("cond_SERIAL_NUMBER");
        String finishTag=paramIData.getString("cond_FINISHTAG");
        
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", sn);
        // 查用户信息
        IDataset userlist = CSAppCall.call("CS.UcaInfoQrySVC.qryUserMainProdInfoBySnForGrp", inparam);
        if (IDataUtil.isEmpty(userlist))
        {
        	CSAppException.apperr(GrpException.CRM_GRP_471, sn);
        }
        
        IData grpUserInfo = userlist.getData(0);
        String cust_id = grpUserInfo.getString("CUST_ID", "");
        String user_id = grpUserInfo.getString("USER_ID", "");
        String product_id = userlist.getData(0).getString("PRODUCT_ID", "");

        if (!product_id.equals("7341"))
        {
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "只有融合V网才能查询此业务");
        }
        
        // 查询集团客户资料
        IData param = new DataMap();
        param.put("CUST_ID", cust_id);
        IDataset custinfos = CSAppCall.call("CS.UcaInfoQrySVC.qryGrpInfoByCustId", param);
        
        if (IDataUtil.isEmpty(custinfos))
        {
        	CSAppException.apperr(GrpException.CRM_GRP_190);
        }
        IData grpcustinfo = custinfos.getData(0);
        String group_id = grpcustinfo.getString("GROUP_ID", "");
        String cust_name = grpcustinfo.getString("CUST_NAME", "");
        
        param.clear();
        param.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        param.put("USER_ID_A", user_id);
        param.put("RELATION_TYPE_CODE", "47");
        
        String callIntf="";
        if("1".equals(finishTag)){
            callIntf="CS.RelaUUInfoQrySVC.qryRelationUUAllForKDMemNew";
        }else if("0".equals(finishTag)){
            param.put("SERIAL_NUMBER", sn);
            callIntf="SS.FTTHBusiModemApplySVC.queryFTTHBusiMem"; //很可能取不到
        } 
        
        IDataset dataOutput = CSAppCall.call(callIntf, param);
        if(dataOutput!=null && dataOutput.size()>0){
        	for (int i = 0; i < dataOutput.size(); i++) {
                IData map = dataOutput.getData(i);
                map.put("GROUP_ID", group_id);
                map.put("CUST_NAME", cust_name);
                map.put("SERIAL_NUMBER", sn);
                map.put("FINISHTAG", StaticUtil.getStaticValue("FINISH_TAG", finishTag));
                IDataset productinfos = CSAppCall.call("CS.ProductInfoQrySVC.getProductInfoByID", map);
                if (IDataUtil.isEmpty(productinfos))
                {
                	CSAppException.apperr(GrpException.CRM_GRP_190);
                }else{
                	map.put("PRODUCT_NAME", productinfos.getData(0).getString("PRODUCT_NAME", ""));
                }
            }
        }
        return dataOutput;
    }
}
