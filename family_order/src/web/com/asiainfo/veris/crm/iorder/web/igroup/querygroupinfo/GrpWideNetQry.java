
package com.asiainfo.veris.crm.iorder.web.igroup.querygroupinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;

public abstract class GrpWideNetQry extends CSBasePage
{

    /**
     * @Description: 初始化页面方法
     * @author
     * @date
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
        setHintInfo("请输入查询条件~~!");
    }

    /**
     * @Description: 商务宽带集团成员查询
     * @author
     * @date
     * @param cycle
     * @throws Exception
     */
    public void queryInfos(IRequestCycle cycle) throws Exception
    {

        IData condData = getData();
        String sn = condData.getString("cond_SERIAL_NUMBER");
        String finishTag=condData.getString("cond_FINISHTAG");
        
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", sn);
        // 查用户信息
        IDataset userlist = getUserInfos(inparam);

        IData grpUserInfo = userlist.getData(0);
        String cust_id = grpUserInfo.getString("CUST_ID", "");
        String user_id = grpUserInfo.getString("USER_ID", "");
        String product_id = userlist.getData(0).getString("PRODUCT_ID", "");

        if (!product_id.equals("7341"))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "只有融合V网才能查询此业务");
        }

        // 查询集团客户资料
        IData param = new DataMap();
        param.put("CUST_ID", cust_id);
        IDataset custinfos = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryGrpInfoByCustId", param);
        
        if (IDataUtil.isEmpty(custinfos))
        {
            CSViewException.apperr(GrpException.CRM_GRP_190);
        }
        IData grpcustinfo = custinfos.getData(0);
        String group_id = grpcustinfo.getString("GROUP_ID", "");
        String cust_name = grpcustinfo.getString("CUST_NAME", "");
        
        param.clear();
        param.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        param.put("USER_ID_A", user_id);
        param.put("RELATION_TYPE_CODE", "47");
        
        int d = getPagination("pageNav").getPageSize();
        
        String callIntf="";
        if("1".equals(finishTag)){
            callIntf="CS.RelaUUInfoQrySVC.qryRelationUUAllForKDMemNew";
        }else if("0".equals(finishTag)){
            param.put("SERIAL_NUMBER", sn);
            callIntf="SS.FTTHBusiModemApplySVC.queryFTTHBusiMem"; //很可能取不到

        } 
        IDataOutput dataOutput = CSViewCall.callPage(this, callIntf, param,getPagination("pageNav"));
        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(dataOutput.getData()))
        {
        	result.put("FLAG", "1");
        	setAjax(result);
            setInfoCount(dataOutput.getDataCount());
            IDataset infosSet = dataOutput.getData();
            for (int row = 0; row < infosSet.size(); row++){
                IData map = infosSet.getData(row);
                map.put("GROUP_ID", group_id);
                map.put("CUST_NAME", cust_name);
                map.put("SERIAL_NUMBER", sn);
                map.put("FINISHTAG", finishTag);
                map.put("PRODUCT_NAME", ProductInfoIntfViewUtil.qryProductNameStrByProductId(this, map.getString("PRODUCT_ID", "")));

            }
            setInfos(infosSet);
        }else{
        	result.put("FLAG", "0");
        	setAjax(result);
        }
        setCondition(condData);
    }

    
    /**
     * 根据sn查询用户信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    private IDataset getUserInfos(IData param) throws Exception
    {
        String grp_serial = param.getString("SERIAL_NUMBER");

        IDataset userlist = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryUserMainProdInfoBySnForGrp", param);

        if (IDataUtil.isEmpty(userlist))
        {
            CSViewException.apperr(GrpException.CRM_GRP_471, grp_serial);
        }
        return userlist;
    }
    
    public abstract void setCondition(IData condition);

    public abstract void setHintInfo(String hintInfo);

    public abstract void setInfoCount(long infoCount);

    public abstract void setInfos(IDataset infos);
    
    public abstract void setGrpcustinfo(IData grpcust);

    public abstract void setGrpuserinfo(IData grpUserinfo);

}
