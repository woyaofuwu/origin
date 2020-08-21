package com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.extend;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USaleScoreInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.BaseSaleScoreData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ScoreTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.IProductModuleTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.BaseProductModuleTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductModuleCalDate;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductTimeEnv;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveQueryBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class SaleScoreTrade extends BaseProductModuleTrade implements IProductModuleTrade
{
    @Override
    public ProductModuleTradeData createSubProductModuleTrade(ProductModuleData dealPmd, List<ProductModuleData> pmds, UcaData uca, BaseReqData brd, ProductTimeEnv env) throws Exception
    {
        BaseSaleScoreData bsScoreData = (BaseSaleScoreData) dealPmd;
        ScoreTradeData scoreTradeData = new ScoreTradeData();

        IDataset result = AcctCall.queryUserScore(uca.getUserId());
        if (IDataUtil.isEmpty(result))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_6);
        }
        String userScore = result.getData(0).getString("SUM_SCORE", "0");
        int userScoreInt = Integer.parseInt(userScore);

//        IDataset datas = SaleScoreInfoQry.queryById(bsScoreData.getElementId());
//        IData score = datas.getData(0);
        
        IData score = USaleScoreInfoQry.queryById(bsScoreData.getElementId());
        int iScoreValue = score.getInt("SCORE_VALUE", 0);

        /**
         * REQ201603090003 
         * chenxy3 20160324
         * */
        String productId=brd.getPageRequestData().getString("PRODUCT_ID");
        String packageId=brd.getPageRequestData().getString("PACKAGE_ID");
        IDataset specElems= CommparaInfoQry.getCommParas("CSM", "2401",packageId, productId,  "0898");  
        if(specElems!=null &&specElems.size()>0){
//            IDataset scores = SaleScoreInfoQry.queryByPkgIdEparchy(packageId, "0898");
            IDataset scores = SaleActiveUtil.getSaleActivesByPackageIdAndElementType(packageId, BofConst.ELEMENT_TYPE_CODE_SCORE);
            String scoreTypeCode=scores.getData(0).getString("SCORE_TYPE_CODE","");
        	IData callParam=new DataMap();
        	String userId=uca.getUserId();
	    	callParam.put("USER_ID",userId);
	    	callParam.put("INTEGRAL_TYPE_CODE",scoreTypeCode);
	    	// -----call-----
	    	SaleActiveQueryBean bean = BeanManager.createBean(SaleActiveQueryBean.class);
	    	IDataset callSet= bean.queryUserScoreValue(callParam);
	    	
	    	//-----call-----
	    	if(callSet!=null &&callSet.size()>0){
	    		IData callrtn=callSet.getData(0);
	    		userScoreInt=Integer.parseInt(callrtn.getString("SCORE_VALUE",""));
	    	}
        }
       
        /**--end---*/

        if ((userScoreInt + iScoreValue) < 0)
            CSAppException.apperr(BofException.CRM_BOF_007);

        scoreTradeData.setCancelTag(bsScoreData.getModifyTag());
        scoreTradeData.setUserId(uca.getUserId());
        scoreTradeData.setElementId(bsScoreData.getElementId());
        scoreTradeData.setSerialNumber(uca.getSerialNumber());
        scoreTradeData.setYearId(score.getString("YEAR_ID", "ZZZZ"));
        scoreTradeData.setIdType(score.getString("ID_TYPE", "0"));
        scoreTradeData.setScore(userScore);
        scoreTradeData.setEndCycleId(score.getString("END_CYCLE_ID", "-1"));
        scoreTradeData.setStartCycleId(score.getString("START_CYCLE_ID", "-1"));
        scoreTradeData.setScoreTypeCode(score.getString("SCORE_TYPE_CODE", ""));
        scoreTradeData.setRemark(bsScoreData.getRemark());
        scoreTradeData.setScoreTag("1");
        scoreTradeData.setScoreChanged(String.valueOf(iScoreValue));
        
        if (bsScoreData.getModifyTag().equals(BofConst.MODIFY_TAG_ADD))
		{
        	String acceptTime = brd.getAcceptTime();
        	String startDate = ProductModuleCalDate.calStartDate(bsScoreData, env);
        	if (startDate.compareTo(brd.getAcceptTime()) < 0 && (env == null || !env.isNoResetStartDate()))
        	{
        		startDate = acceptTime;
        	}
        	//scoreTradeData.setRsrvStr1(startDate);
        	String endDate = ProductModuleCalDate.calEndDate(bsScoreData, startDate);
        	scoreTradeData.setRsrvStr2(endDate.substring(0, 10) + SysDateMgr.getEndTime235959());
		}
		
        return scoreTradeData;
    }

}
