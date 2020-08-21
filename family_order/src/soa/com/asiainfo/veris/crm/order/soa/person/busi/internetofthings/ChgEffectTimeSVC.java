
package com.asiainfo.veris.crm.order.soa.person.busi.internetofthings;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.bizservice.base.CSBizService;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductModuleCalDate;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class ChgEffectTimeSVC extends CSBizService
{

   
    /**
     * 查询当前有效的物联网正常期优惠
     * 
     * @return
     * @throws Exception
     */
    public static IDataset queryNormalValidDiscnt(IData param) throws Exception
    {
        String userId = param.getString("USER_ID");
        ChgEffectTimeBean bean = new ChgEffectTimeBean();

        IDataset normalConfigList = CommparaInfoQry.getCommByParaAttr("CSM", "9013", CSBizBean.getUserEparchyCode());
        if (normalConfigList != null && !normalConfigList.isEmpty())
        {
        	for (int i = 0; i < normalConfigList.size(); i++)
            {
                IData config = normalConfigList.getData(i);
                if("NB_GPRS".equals(config.getString("PARA_CODE20")))
                {
                	IDataset discntList = bean.getDiscntByUser(userId, config.getString("PARAM_CODE"));
                    
                    if (discntList != null && !discntList.isEmpty())
                    {
                    	for (int j = 0; j < discntList.size(); j++)
                        {
                            
                    		IData discnt = discntList.getData(j);
                            discnt.put("DISCNT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(discnt.getString("DISCNT_CODE")));
 }

                    	return discntList;  
                    }
                }
                
             } 
        }
        
        return null;

    }
    public static IDataset queryENDTime(IData param) throws Exception
    {
    	
    	IData result = new DataMap();
    	IDataset results = new DatasetList(); ;
    	ChgEffectTimeBean bean = new ChgEffectTimeBean();
    	IDataset discntList = bean.getDiscntByUser(param.getString("USER_ID"), param.getString("test_ELEMENT_ID"));
    	if (discntList != null && !discntList.isEmpty())
        {
    		
    		IData element  = discntList.getData(0);
    		DiscntData discntData = new DiscntData(element);
    		discntData.setEndDate("");//清空结束时间
            String endDate = ProductModuleCalDate.calEndDate(discntData, param.getString("START_DATE"));
            result.put("END_DATE", endDate);
            results.add(result);
        }
  
     
        return results;
    	
    
    }
    
    /**
     * 查询当前有效的物联网测试期优惠
     * 
     * @return
     * @throws Exception
     */
    public static IDataset queryTestValidDiscnt(IData param) throws Exception
    {
        String userId = param.getString("USER_ID");
        ChgEffectTimeBean bean = new ChgEffectTimeBean();
        IDataset testConfigList = CommparaInfoQry.getCommByParaAttr("CSM", "9013", CSBizBean.getUserEparchyCode());
        if (testConfigList != null && !testConfigList.isEmpty()){
        	for (int i = 0; i < testConfigList.size(); i++)
            {
                IData config = testConfigList.getData(i);
                if("NB_TEST".equals(config.getString("PARA_CODE20")))
                {
                	IDataset discntList = bean.getDiscntByUser(userId, config.getString("PARAM_CODE"));
                    
                    if (discntList != null && !discntList.isEmpty())
                    {
                    	for (int j = 0; j < discntList.size(); j++)
                        {
                            
                    		IData discnt = discntList.getData(j);
                            discnt.put("DISCNT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(discnt.getString("DISCNT_CODE")));
 
                        }
                    	return discntList;  
                    }
                }
                
             }
        }
        return null;

    }
}
