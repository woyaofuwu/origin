package com.asiainfo.veris.crm.iorder.web.igroup.pagedata;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public class CreateAdcGroupMemberPageDataTrans extends CreateGroupMemberPageDataTrans {

    /**
     * 将新的商品信息封装为原结构
     *
     * @return
     */
    @Override
    protected IDataset transformOfferList(IDataset offerinfo) throws Exception {

    	 IDataset listOfferInfo = new DatasetList();
          String isSec="";
         IDataset offers = offerinfo.first().getDataset("SUBOFFERS");

         for (int i = 0, size = offers.size(); i < size; i++) {
             IData offerInfo = offers.getData(i);
             IDataset attrList = offers.getData(i).getDataset("OFFER_CHA_SPECS");

             IData elementOfferInfo = new DataMap();

             if(IDataUtil.isNotEmpty(attrList)){
                 String offerType = offerInfo.getString("OFFER_TYPE");
                 if ("S".equals(offerType) && !"-1".equals(offerInfo.getString("GROUP_ID"))) {// 非组合商品
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
						  //如果用户对集团通讯录（升级版打钩需要下发二次确认短信） update by zhuweijun
                        	 if("HECK_SMS_FLAG".equals(key))
                        	 {
                        		 isSec=item.getString("ATTR_VALUE","0");
                        		 if("1".equals(isSec)){
                                 	//如果等于1代表需要下发二次确认短信
                                 	 elementOfferInfo.put("PAGE_SELECTED_TC", true);
                                  }else{//否则不需要下发二次确认短信
                                 	 elementOfferInfo.put("PAGE_SELECTED_TC", false); 
                                  }
                        		              }
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
}
