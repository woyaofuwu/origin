
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.realname;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

/**
 * 功能：用户实名制信息清单的导出 作者：GongGuang
 */
public class ExportQueryChkRealName extends ExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData paramIData, Pagination paramPagination) throws Exception
    {
        IData data = paramIData.subData("cond", true);
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        
        String isCheck=data.getString("NORMAL_USER_CHECK", "");

        
        IDataset res=new DatasetList();
        if(isCheck.equals("on")){	//勾选了在网用户，查询在网用户信息
        	res =CSAppCall.call("SS.QueryChkRealNameSVC.getUserRealNameInfoValid", data);
        }else{	//没有勾选，根据userId查询具体的用户的信息
        	res =CSAppCall.call("SS.QueryChkRealNameSVC.getUserRealNameInfoByUserId", data);
        }
//        IDataset res = CSAppCall.call("SS.QueryChkRealNameSVC.getUserRealNameInfo", data);
        
        return res;
    }
}
