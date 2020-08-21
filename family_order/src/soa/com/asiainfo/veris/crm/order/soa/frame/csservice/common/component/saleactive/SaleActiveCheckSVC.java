
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class SaleActiveCheckSVC extends CSBizService
{
    private static final long serialVersionUID = -3406788939544014429L;

    public IData checkBeforeTrade(IData param) throws Exception
    {
        String productId = param.getString("PRODUCT_ID");
        String packageId = param.getString("PACKAGE_ID");
        String eparchyCode = param.getString(Route.ROUTE_EPARCHY_CODE);

        IDataset bookConfigs = CommparaInfoQry.getCommparaByParaAttr("CSM", "158", eparchyCode);
        boolean noBook = SaleActiveUtil.isInCommparaConfigs(productId, packageId, bookConfigs);

        if (noBook)
        {
            return null;
        }

        String tagSet2 = SaleActiveUtil.getPackageExtTagSet2(packageId, null);
        if (StringUtils.isNotBlank(tagSet2) && tagSet2.length() > 3 && !tagSet2.substring(2, 3).equals("0"))
        {
            return null;
        }

        SaleActiveCheckBean saleActiveCheckBean = BeanManager.createBean(SaleActiveCheckBean.class);

        IData BreResultData = saleActiveCheckBean.checkBeforeTrade(param);

        CSAppException.breerr(BreResultData);

        return null;
    }

    public IData checkBindSerialNumber(IData param) throws Exception
    {
        String bindSn = param.getString("BIND_SERIAL_NUMBER");
        String productId = param.getString("PRODUCT_ID");
        String packageId = param.getString("PACKAGE_ID");

        SaleActiveCheckBean saleActiveCheckBean = BeanManager.createBean(SaleActiveCheckBean.class);
        IData bindCheckResultData = saleActiveCheckBean.checkBindSerialNumber(bindSn, productId, packageId);

        return bindCheckResultData;
    }
    
    public IDataset qryNewTempInfo(IData input) throws Exception
    {
    	SQLParser parser = new SQLParser(input); 
  	  
        parser.addSQL(" select T.* "); 
        parser.addSQL(" from TF_F_NEW_ACTIVE_TEMPLATE t ");
        parser.addSQL(" where t.trade_type_code=:TRADE_TYPE_CODE ");
        parser.addSQL(" AND T.SERIAL_NUMBER=:SERIAL_NUMBER "); 
        parser.addSQL(" AND T.USER_ID=:USER_ID "); 
        parser.addSQL(" AND T.DEAL_TAG='0' "); 
        parser.addSQL(" AND T.PRODUCT_ID=:PRODUCT_ID "); 

        return Dao.qryByParse(parser); 
    	
    }

    public IData checkByPackage(IData param) throws Exception
    {
        SaleActiveCheckBean saleActiveCheckBean = BeanManager.createBean(SaleActiveCheckBean.class);

        IData breResultData = saleActiveCheckBean.checkPackage(param);

        //REQ202001020014关于信用购活动查询界面的开发需求 start by liangdg3
        //需求内容:泛渠道信用购机拦截(69900631)提示信息特殊处理:若拦截code包含20191204,只显示该条规则拦截信息
        //该条规则拦截信息为"号码不满足营销活动办理条件"
        //按需求处理下TIPS_TYPE_ERROR结果集
        String productId2 = param.getString("PRODUCT_ID","");
        if("69900631".equals(productId2)){
            IDataset tipsTypeErrors = breResultData.getDataset("TIPS_TYPE_ERROR");
            if(IDataUtil.isNotEmpty(tipsTypeErrors)){
                for (int i = 0; i < tipsTypeErrors.size(); i++) {
                    IData typeErrorsData = tipsTypeErrors.getData(i);
                    String tipsCode = typeErrorsData.getString("TIPS_CODE");
                    if("20191204".equals(tipsCode)){
                        DatasetList newTipsTypeErrors = new DatasetList();
                        newTipsTypeErrors.add(typeErrorsData);
                        breResultData.put("TIPS_TYPE_ERROR",newTipsTypeErrors);
                        break;
                    }
                }
            }
        }
        //REQ202001020014关于信用购活动查询界面的开发需求 end by liangdg3

        CSAppException.breerr(breResultData);
        
        //add by zhangxing3 start
        String productId = param.getString("PRODUCT_ID","");
        IDataset commparaInfos9957 = CommparaInfoQry.getCommparaAllCol("CSM","9957", productId, "0898"); 
        
        if(IDataUtil.isNotEmpty(commparaInfos9957)){
	        String serialNumber = param.getString("SERIAL_NUMBER","");
	        UcaData uca = UcaDataFactory.getNormalUca(serialNumber);

	        IData input = new DataMap();
	    	input.put("SERIAL_NUMBER", serialNumber);
	    	input.put("USER_ID", uca.getUserId());
	    	input.put("TRADE_TYPE_CODE", "240");
	    	input.put("PRODUCT_ID", productId);
	
	    	IDataset oldCustInfos=qryNewTempInfo(input);
	    	if(oldCustInfos!=null && oldCustInfos.size()>=1){
	    		
	    		String checkSerialNum = oldCustInfos.getData(0).getString("CHECK_SERIAL_NUMBER", "");
	    		String productIdB = oldCustInfos.getData(0).getString("PRODUCT_ID_B", "");
	    		String packageIdB = "";
//        		IDataset commsets = CommparaInfoQry.getCommparaAllCol("CSM","9957", productId, "0898"); 
//            	if(commsets!=null && commsets.size()>0){
            		packageIdB=commparaInfos9957.getData(0).getString("PARA_CODE4","");

            	//}
	    		IData param1 = new DataMap();
		        param1.put("SERIAL_NUMBER", checkSerialNum);
		        param1.put("PRODUCT_ID", productIdB);
		        param1.put("TRADE_TYPE_CODE", "240");
		        param1.put("PACKAGE_ID", packageIdB);
		        param1.put("CAMPN_TYPE", "YX04");
		        param1.put("PRE_TYPE", "1");

		        IDataset set=CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", param1);
				
	    	}
        }
        //add by zhangxing3 end
        
        IData returnData = new DataMap();

        IDataset choiceInfo = breResultData.getDataset("TIPS_TYPE_CHOICE");
        if (IDataUtil.isNotEmpty(choiceInfo))
        {
            returnData.put("TIPS_TYPE_CHOICE", choiceInfo);
        }
        

        IDataset tipInfo = breResultData.getDataset("TIPS_TYPE_TIP");
        if (IDataUtil.isNotEmpty(tipInfo))
        {
            returnData.put("TIPS_TYPE_TIP", tipInfo);
        }

        IData smsVeriCodeData = saleActiveCheckBean.checkIsSmsVeriCodeTrade(param);

        if (IDataUtil.isNotEmpty(smsVeriCodeData))
        {
            returnData.putAll(smsVeriCodeData);
        }

        IData bindSerialNumberBdata = saleActiveCheckBean.checkIsBindSerialNumberB(param);

        if (IDataUtil.isNotEmpty(bindSerialNumberBdata))
        {
            returnData.putAll(bindSerialNumberBdata);
        }

        return returnData;
    }
}
