package com.asiainfo.veris.crm.order.soa.person.busi.saleactive;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;  

public class SaleActiveRuleCheckSVC extends CSBizService
{ 
    private static final Logger logger = Logger.getLogger(SaleActiveRuleCheckSVC.class);
        /**
         * REQ201602230002 营销活动规则校验
         * chenxy3
         * 获取错误代码的链接
         * SS.SaleActiveRuleCheckSVC.getCommparaInfo
         * */
    public IDataset getCommparaInfo(IData input) throws Exception
    {        
            String rtnCode=input.getString("ERR_CODE","0");
            return CommparaInfoQry.getCommPkInfo("CSM", "1581", rtnCode, "0898");
    }
    
    /**
     * 查询TD_S_MODFILE表取菜单地址
     * SS.SaleActiveRuleCheckSVC.qryModfileInfo
     */
    public IDataset qryModfileInfo(IData input) throws Exception
    {        
            return SaleActiveRuleCheckBean.qryModfileInfo(input);
    }
    /**
     * REQ201606020021 补充保底消费送宽带活动参数的开发需求 
     * chenxy3 2016-06-15
     * 获取所有约定消费送宽带相关的套餐编码
     * */
    public IDataset getGiveWilenActiveInfo(IData params) throws Exception
    {
        IDataset rtnSet = new DatasetList(); 
        IDataset commSet=CommparaInfoQry.getCommByParaAttr("CSM", "1025", "0898");
        for(int k=0; k <commSet.size(); k++){
                params.put("PRODUCT_ID", commSet.getData(k).getString("PARAM_CODE",""));
                rtnSet.add(SaleActiveRuleCheckBean.queryGiveWilenActiveInfo(params)) ;
        }
        logger.info(">>>>>>>>>>>>>cxy>>>>>>>rtnSet="+rtnSet);
        return rtnSet;
    }
    
}
