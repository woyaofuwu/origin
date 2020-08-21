
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade.esp;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class SynEspMebOrderSVC extends GroupOrderService
{
	/**
	 * 处理Esp归档成员签约同步关系
	 * @param map
	 * @return
	 * @throws Exception
	 */
    public IData dealEspMebOrder(IData map) throws Exception
    {
       //1、校验
        
        //2、创建台账
        IData result=crtTradeInfo(map);       

        return result;
    }
    private IData crtTradeInfo(IData idata)throws Exception
    {
    	IDataset result=new DatasetList();
    	IData info=new DataMap();
    	try
    	{     

    		String oSubTypeID = idata.getString("ACTION", "");//成员操作类型
            
            //  添加用户路由(后台生成虚拟号码用)
//            idata.put(Route.USER_EPARCHY_CODE,idata.get(Route.ROUTE_EPARCHY_CODE));
    		
    		// 3- 根据成员操作类型调用不同的服务进行处理
            if ("1".equals(oSubTypeID)){// 成员新增
            	result=CSAppCall.call("CS.CreateEspMemSVC.crtOrder", idata);
            }else if ("0".equals(oSubTypeID)){// 成员删除
            	result=CSAppCall.call("CS.DestroyEspMemSVC.dealEspMebBiz", idata);
            }else{// 成员变更
            	result=CSAppCall.call("CS.ChangeEspMemSVC.crtOrder", idata);
            }  
            info.put("RESULT_CODE", result.getData(0).getString("RESULT_CODE"));
    		info.put("RESULT_INFO",  result.getData(0).getString("RESULT_INFO"));
            
		}
    	catch(Exception e)
		{
    		info.put("RESULT_CODE", "99");
    		info.put("RESULT_INFO", e.getMessage());
		}
    	return info;
    }
  
}