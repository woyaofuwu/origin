package com.asiainfo.veris.crm.iorder.web.igroup.pagedata;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.iorder.pub.consts.EcConstants;
import com.asiainfo.veris.crm.iorder.web.igroup.util.CommonViewCall;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;


/**
 * ChangeAdcGroupUserPageDataTrans
 *
 * @anthor zhanghy9
 * @date 2018-01-22 16:25
 */
public class ChangeAdcGroupUserPageDataTrans extends ChangeGroupUserPageDataTrans{


	/**
	 * 将新的商品信息封装为原结构
	 *
	 * @return
	 */
	@Override
	protected IDataset transformOfferList(IDataset offerinfo) throws Exception {

   	 IDataset listOfferInfo = new DatasetList();

     IDataset offers = offerinfo.first().getDataset("SUBOFFERS");
     
     for (int i = 0, size = offers.size(); i < size; i++) {
         IData offerInfo = offers.getData(i);
         
         if(ACTION_EXITS.equals(offerInfo.getString("OPER_CODE")))
         {//如果子商品没有发生改变，则移除
        	 offerInfo.remove(i);
             continue;
         }
         IDataset attrList = offers.getData(i).getDataset("OFFER_CHA_SPECS");

         IData elementOfferInfo = new DataMap();
         
         if((ACTION_DELETE.equals(offerInfo.getString("OPER_CODE")))&&("S".equals(offerInfo.getString("OFFER_TYPE")))){//退订服务
        	 String grpUserId = offerInfo.getString("USER_ID");
        	 String serviceId = offerInfo.getString("OFFER_CODE");
        	 IDataset grpplatsvcs = CommonViewCall.getAdcMasPlatSvcParam(grpUserId, serviceId);
        	 if(IDataUtil.isNotEmpty(grpplatsvcs)){
        		 IData grpplatsvc = grpplatsvcs.getData(0);
        		 
        		 IDataset attrParam = new DatasetList();
        		 IData paramVerifySucc = new DataMap();
                 paramVerifySucc.put("PARAM_VERIFY_SUCC", true);
                 attrParam.add(paramVerifySucc);
                 
                 //新页面对grpplatsvc数据进行特殊处理 来符合后台处理类的数据存取
                 grpplatsvc.put("SIBASE_INCODE",grpplatsvc.getString("SI_BASE_IN_CODE",""));
                 grpplatsvc.put("SIBASE_INCODE_A", grpplatsvc.getString("SI_BASE_IN_CODE_A",""));
                 
                 grpplatsvc.put("SERVICE_TYPE", grpplatsvc.getString("RSRV_NUM2",""));
                 grpplatsvc.put("WHITE_TOWCHECK", grpplatsvc.getString("RSRV_NUM3",""));
                 grpplatsvc.put("SMS_TEMPALTE", grpplatsvc.getString("RSRV_NUM5",""));
                 grpplatsvc.put("PORT_TYPE", grpplatsvc.getString("RSRV_STR1",""));
                 grpplatsvc.put("SVR_CODE_HEAD", grpplatsvc.getString("RSRV_NUM4", ""));
                 
                 IData offeritem = new DataMap();
                 offeritem.put("PLATSVC", grpplatsvc);
                 offeritem.put("ID",serviceId);
                 
                 attrParam.add(offeritem);
                 elementOfferInfo.put("ATTR_PARAM", attrParam);
        	 }else{
        		 continue;
        	 }
         }

         else if(IDataUtil.isNotEmpty(attrList)){//新增或修改服务
             String offerType = offerInfo.getString("OFFER_TYPE");
             if ("S".equals(offerType) ) {
                 IDataset attrParam = new DatasetList();
                 IData offeritem = new DataMap();
                 IData paramVerifySucc = new DataMap();
                 paramVerifySucc.put("PARAM_VERIFY_SUCC", true);
                 attrParam.add(paramVerifySucc);
                 
                 IData platSvc = new DataMap();
                 for (int j = 0; j < attrList.size(); j++) {
                     IData item = attrList.getData(j);
                     String key = item.getString("ATTR_CODE");
                     if ("CANCLE_FLAG".equals(key)) {
                         offeritem.put("CANCLE_FLAG", item.getString("ATTR_VALUE"));
                     } else if ("MOLIST_Hidden".equals(key)) {
                         offeritem.put("MOLIST", new DatasetList(item.getString("ATTR_VALUE")));
                     } else if ("paramArea".equals(key)) {
                         offeritem.put("OTHERLIST", new DatasetList(item.getString("ATTR_VALUE")));
                     } else {
                         platSvc.put("pam_" + key, item.getString("ATTR_VALUE"));
                     }
                 }
                 platSvc.put("pam_" + "BIZ_STATUS", "A");
                 //SERV_TYPE CONFIRMFLAG-RSRV_STRB 临时
                 //SERV_TYPE业务类型 枚举值：51:内部管理类；52:外部服务类；53:营销推广类；54:公益类
                 //CONFIRMFLAG-RSRV_STRB白名单二次确认管理标识0：是  1：否
                 platSvc.put("pam_" + "SERV_TYPE", "123");
                 platSvc.put("pam_" + "RSRV_STRB", "1");
                 offeritem.put("PLATSVC", platSvc);
                 
                 offeritem.put("ID", offers.getData(i).getString("OFFER_CODE"));
                 
                 //该字段老逻辑只是用来判断是否点过确定按钮，新逻辑不需要，没点过确定的值不会被保存下来
                 offeritem.put("CANCLE_FLAG","false");
                 
                 attrParam.add(offeritem);
                 elementOfferInfo.put("ATTR_PARAM", attrParam);
             }else{
             	elementOfferInfo.put("ATTR_PARAM", attrList);
             }

         }
         elementOfferInfo.put("ELEMENT_ID", offerInfo.getString("OFFER_CODE"));
         elementOfferInfo.put("PRODUCT_ID", offerinfo.first().getString("OFFER_CODE"));
         elementOfferInfo.put("ELEMENT_TYPE_CODE", offerInfo.getString("OFFER_TYPE"));
         elementOfferInfo.put("PACKAGE_ID",offerInfo.getString("GROUP_ID"));
         elementOfferInfo.put("MODIFY_TAG", offerInfo.getString("OPER_CODE"));
         elementOfferInfo.put("START_DATE", offerInfo.getString("START_DATE"));
         elementOfferInfo.put("END_DATE", offerInfo.getString("END_DATE"));
         listOfferInfo.add(elementOfferInfo);
     }

     return listOfferInfo;

	}

    @Override
    public void setServiceName() throws Exception {
        setSvcName(EcConstants.EC_OPER_SERVICE.CHANGE_ENTERPRISE_SUBSCRIBER.getValue());
    }
}
