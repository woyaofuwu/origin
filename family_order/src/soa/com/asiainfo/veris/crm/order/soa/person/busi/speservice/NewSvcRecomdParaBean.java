
package com.asiainfo.veris.crm.order.soa.person.busi.speservice;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.RecommparaQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.BrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;

public class NewSvcRecomdParaBean extends CSBizBean
{
    private static Logger logger = Logger.getLogger(NewSvcRecomdParaBean.class);

    /**
     * 删除
     * 
     * @param pd
     * @param param
     * @throws Exception
     */
    public void delRecommInfo(String type, String element_id) throws Exception
    {

        IData inparams = new DataMap();
        inparams.put("RECOMM_TYPE", type);
        inparams.put("ELEMENT_ID", element_id);
        inparams.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        inparams.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        inparams.put("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());

        Dao.executeUpdateByCodeCode("TD_B_RECOMMPARA", "DEL_BY_ID_TYPE", inparams, Route.CONN_CRM_CEN);
    }

    public IDataset getAllBrandInfo(IData param) throws Exception
    {

        return UBrandInfoQry.getBrandList() ;//BrandInfoQry.queryAllBrands();以前TD_S_BRAND没有了，改为调用产商品中心,duhj 2017/03/06
    }

    public IDataset getProductByBrand(IData param) throws Exception
    {
        String brand_code = param.getString("BRAND_CODE");
        String product_mode = param.getString("PRODUCT_MODE");
        String eparchy_code = param.getString("EPARCHY_CODE");

        //return ProductInfoQry.getProductListByBRAND(brand_code, product_mode, eparchy_code, null);
        
        return UProductInfoQry.getProductListByBRAND(brand_code);

    }

    public IDataset getUserRecommPara(IData param) throws Exception
    {
        return RecommparaQry.getUserRecommPara(param.getString("RECOMM_TYPE"), param.getString("ELEMENT_ID"), param.getString("RECOMM_SOURCE"), param.getString("EPARCHY_CODE"));
    }

    public IDataset getUserRecommparaDiscnt(IData param) throws Exception
    {
        return RecommparaQry.getUserRecommparaDiscnt(param.getString("PRODUCT_ID"), param.getString("TRADE_EPARCHY_CODE"), param.getString("TRADE_STAFF_ID"));

    }

    public IDataset getUserRecommparaService(IData param) throws Exception
    {

        return RecommparaQry.getUserRecommparaService(param.getString("PRODUCT_ID"), param.getString("TRADE_EPARCHY_CODE"), param.getString("TRADE_STAFF_ID"));
          
    }

    public IData getUserRecommparaServiceAndDiscntCode(IData param) throws Exception
    {

       // return RecommparaQry.getUserRecommparaService(param.getString("PRODUCT_ID"), param.getString("TRADE_EPARCHY_CODE"), param.getString("TRADE_STAFF_ID"));
   
    	IData  svcDicnt=new DataMap();
    	
        IDataset forceSvcList = UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(BofConst.ELEMENT_TYPE_CODE_PRODUCT, param.getString("PRODUCT_ID"),"","");
        
        IDataset  discnts=new DatasetList();
        IDataset  services=new DatasetList();
        if(IDataUtil.isNotEmpty(forceSvcList)){//591
        	for(int i=0;i<forceSvcList.size();i++){
        		IData ss= forceSvcList.getData(i);
        		String offerType=ss.getString("OFFER_TYPE");
        		
        		if(StringUtils.equals(offerType, "S")){
        			services.add(ss);
        		}else if(StringUtils.equals(offerType, "D")){
        			discnts.add(ss);
        		}
        	
        	}
        }

        if(IDataUtil.isNotEmpty(services)){
        	for(int i=0;i<services.size();i++){
        		IData results= services.getData(i);
        		String serviceId=results.getString("OFFER_CODE");
        		IDataset  res=RecommparaQry.getUserRecommparaService(serviceId,param.getString("TRADE_EPARCHY_CODE"), param.getString("TRADE_STAFF_ID"));
        		results.put("SERVICE_ID", serviceId);
        		results.put("SERVICE_NAME", results.getString("OFFER_NAME"));	
        		if(IDataUtil.isNotEmpty(res)){
        			results.put("RECOMM_CONTENT", res.getData(0).getString("RECOMM_CONTENT"));
        			results.put("CHOICETAG", res.getData(0).getString("CHOICETAG"));

        		}


        	}
        }
        
        
        if(IDataUtil.isNotEmpty(discnts)){//353
        	for(int i=0;i<discnts.size();i++){
        		IData results= discnts.getData(i);
        		String discntCode=results.getString("OFFER_CODE");
        		IDataset  res=RecommparaQry.getUserRecommparaDiscnt(discntCode,param.getString("TRADE_EPARCHY_CODE"), param.getString("TRADE_STAFF_ID"));
        		results.put("DISCNT_CODE", discntCode);
        		results.put("DISCNT_NAME", results.getString("OFFER_NAME"));
        		if(IDataUtil.isNotEmpty(res)){
        			results.put("RECOMM_CONTENT", res.getData(0).getString("RECOMM_CONTENT"));
        			results.put("CHOICETAG", res.getData(0).getString("CHOICETAG"));

        		}

        	}
        }
    
        svcDicnt.put("DISCNTCODES", discnts);
        svcDicnt.put("SERVICES", services);

        
        return svcDicnt;
    }
    
    /**
     * 新增
     * 
     * @param pd
     * @param param
     * @throws Exception
     */
    public void insertRecommInfo(String type, String element_id, String element_name, String recomm_content) throws Exception
    {

        IData data = new DataMap();

        data.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        data.put("UPDATE_TIME", SysDateMgr.getSysTime());
        data.put("START_DATE", SysDateMgr.getSysTime());
        data.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
        data.put("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
        data.put("RECOMM_SOURCE", "2");// 来源 手工
        data.put("RECOMM_TYPE", type);// 类型 优惠或服务信息
        data.put("PRODUCT_ID", "-1");// 默认值 -1
        data.put("PACKAGE_ID", "-1");// 默认值 -1
        data.put("ELEMENT_ID", element_id);// 优惠或服务编码
        data.put("ELEMENT_NAME", element_name);// 优惠或服务名称
        data.put("RECOMM_CONTENT", recomm_content); // 优惠推荐或服务用语

        Dao.insert("TD_B_RECOMMPARA", data, Route.CONN_CRM_CEN);
    }

    /**
     * 修改
     * 
     * @param pd
     * @param param
     * @throws Exception
     */
    public void upRecommInfo(String type, String element_id, String recomm_content) throws Exception
    {

        IData inparams = new DataMap();
        inparams.put("RECOMM_TYPE", type);
        inparams.put("ELEMENT_ID", element_id);
        inparams.put("RECOMM_CONTENT", recomm_content);
        inparams.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        inparams.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        inparams.put("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());

        Dao.executeUpdateByCodeCode("TD_B_RECOMMPARA", "UPD_BY_ID_TYPE", inparams, Route.CONN_CRM_CEN);
    }
}
