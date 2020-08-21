package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
* 物联网折扣率，在产品变更时，不允许变动
 */
public class CheckWLWDisconutAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        //System.out.println("CheckWLWDisconutAction.javaxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx29 " + btd);

        //BRAND_CODE
        boolean isCheck = true;
        List<MainTradeData> mainTrades = btd.getTradeDatas(TradeTableEnum.TRADE_MAIN);
        if (mainTrades != null && mainTrades.size() > 0) {
            String brandCode = mainTrades.get(0).getBrandCode().trim();
            if (brandCode.equals("PWLW")) {

                //{"TF_B_TRADE_PRODUCT":{"USER_ID_A":"-1","MODIFY_TAG":"0","PRODUCT_MODE":"15","RSRV_NUM4":null,"RSRV_NUM5":null,"END_DATE":"2050-12-31 23:59:59","RSRV_NUM1":null,"USER_ID":"1118042636001634","RSRV_NUM2":null,"OLD_BRAND_CODE":"PWLW","RSRV_NUM3":null,"MAIN_TAG":"1","INST_ID":"1118062099477462","OLD_PRODUCT_ID":"20120706","BRAND_CODE":"PWLW","REMARK":null,"START_DATE":"2018-07-01","PRODUCT_ID":"20120707","RSRV_DATE3":null,"RSRV_DATE2":null,"RSRV_DATE1":null,"RSRV_STR5":null,"RSRV_STR3":null,"RSRV_STR4":null,"RSRV_STR1":null,"RSRV_STR2":null,"CAMPN_ID":null,"RSRV_TAG2":null,"RSRV_TAG3":null,"RSRV_TAG1":null}}
                //{"TF_B_TRADE_PRODUCT":{"USER_ID_A":"-1","MODIFY_TAG":"1","PRODUCT_MODE":"15","RSRV_NUM4":null,"RSRV_NUM5":null,"END_DATE":"2018-06-30 23:59:59","RSRV_NUM1":null,"USER_ID":"1118042636001634","RSRV_NUM2":null,"OLD_BRAND_CODE":null,"RSRV_NUM3":null,"MAIN_TAG":"1","INST_ID":"1118042695893066","OLD_PRODUCT_ID":null,"BRAND_CODE":"PWLW","REMARK":null,"START_DATE":"2018-04-26 15:19:37","PRODUCT_ID":"20120706","RSRV_DATE3":null,"RSRV_DATE2":null,"RSRV_DATE1":null,"RSRV_STR5":null,"RSRV_STR3":null,"RSRV_STR4":null,"RSRV_STR1":null,"RSRV_STR2":null,"CAMPN_ID":null,"RSRV_TAG2":null,"RSRV_TAG3":null,"RSRV_TAG1":null}}
                List<ProductTradeData> productTrades = btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);
                if (productTrades != null && productTrades.size() > 0) {
                    IDataset productAttrsDs = new DatasetList();
                    for (int i = 0; i < productTrades.size(); i++) {
                        IData data = new DataMap();
                        ProductTradeData productdata = productTrades.get(i);
                        data.put("MODIFY_TAG", productdata.getModifyTag());
                        productAttrsDs.add(data);
                    }
                    String filterStr1 = "MODIFY_TAG=0";
                    String filterStr2 = "MODIFY_TAG=1";
                    IDataset filterData1 = DataHelper.filter(productAttrsDs, filterStr1);
                    IDataset filterData2 = DataHelper.filter(productAttrsDs, filterStr2);
                    if (IDataUtil.isNotEmpty(filterData1) && IDataUtil.isNotEmpty(filterData2)) {//如果是变更主产品，则不进行校验
                        isCheck = false;
                    }
                }

                //{"TF_B_TRADE_DISCNT":{"USER_ID_A":"-1","MODIFY_TAG":"1","RSRV_NUM4":null,"RSRV_NUM5":null,"END_DATE":"2018-06-30 23:59:59","RSRV_NUM1":null,"USER_ID":"1118061936721632","RSRV_NUM2":null,"RSRV_NUM3":null,"INST_ID":"1118061999459375","REMARK":null,"IS_NEED_PF":"","START_DATE":"2018-06-19 11:24:25","PRODUCT_ID":"20120706","RSRV_DATE3":null,"OPER_CODE":"","RSRV_DATE2":null,"RSRV_DATE1":null,"RSRV_STR5":null,"RSRV_STR3":null,"RSRV_STR4":null,"RSRV_STR1":null,"RSRV_STR2":null,"DISCNT_CODE":"20122163","CAMPN_ID":null,"SPEC_TAG":"0","RELATION_TYPE_CODE":null,"RSRV_TAG2":null,"PACKAGE_ID":"70000005","RSRV_TAG3":null,"RSRV_TAG1":null}}

                List<DiscntTradeData> discntTrades = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
                if (discntTrades != null && discntTrades.size() > 0) {
                    IDataset discntAttrsDs = new DatasetList();
                    for (int i = 0; i < discntTrades.size(); i++) {
                        IData data = new DataMap();
                        DiscntTradeData discntdata = discntTrades.get(i);
                        data.put("MODIFY_TAG", discntdata.getModifyTag());
                        discntAttrsDs.add(data);
                    }
//                    String filterStr1 = "MODIFY_TAG=0";
                    String filterStr2 = "MODIFY_TAG=1";
//                    IDataset filterData1 = DataHelper.filter(discntAttrsDs, filterStr1);
                    IDataset filterData2 = DataHelper.filter(discntAttrsDs, filterStr2);
                    if (IDataUtil.isNotEmpty(filterData2)) {//如果是退订优惠，则不进行校验
                        isCheck = false;
                    }
                }
                

                if (isCheck) {
                    List<AttrTradeData> attrTrades = btd.getTradeDatas(TradeTableEnum.TRADE_ATTR);

                    //{"TF_B_TRADE_ATTR":{"MODIFY_TAG":"0","RSRV_NUM4":null,"RSRV_NUM5":null,"END_DATE":"2050-12-31 23:59:59","RSRV_NUM1":"20122160","USER_ID":"1118042636001634","RSRV_NUM2":null,"RSRV_NUM3":null,"INST_ID":"1118053098402819","REMARK":null,"RELA_INST_ID":"1118053098402702","ELEMENT_ID":"20122160","START_DATE":"2018-05-30 17:23:55","RSRV_DATE3":null,"ATTR_CODE":"Discount","RSRV_DATE2":null,"INST_TYPE":"D","RSRV_DATE1":null,"RSRV_STR5":null,"ATTR_VALUE":"98","RSRV_STR3":null,"RSRV_STR4":null,"RSRV_STR1":null,"RSRV_STR2":null,"RSRV_TAG2":null,"RSRV_TAG3":null,"RSRV_TAG1":null}}
                    //{"TF_B_TRADE_ATTR":{"MODIFY_TAG":"1","RSRV_NUM4":null,"RSRV_NUM5":null,"END_DATE":"2018-05-30 17:23:54","RSRV_NUM1":"20122160","USER_ID":"1118042636001634","RSRV_NUM2":null,"RSRV_NUM3":null,"INST_ID":"1118053098402705","REMARK":null,"RELA_INST_ID":"1118053098402702","ELEMENT_ID":"20122160","START_DATE":"2018-05-30 17:11:19","RSRV_DATE3":null,"ATTR_CODE":"Discount","RSRV_DATE2":null,"INST_TYPE":"D","RSRV_DATE1":null,"RSRV_STR5":null,"ATTR_VALUE":"100","RSRV_STR3":null,"RSRV_STR4":null,"RSRV_STR1":null,"RSRV_STR2":null,"RSRV_TAG2":null,"RSRV_TAG3":null,"RSRV_TAG1":null}}
                    if (attrTrades != null && attrTrades.size() > 0) {

                        IDataset tradeAttrsDs = new DatasetList();
                        for (int i = 0; i < attrTrades.size(); i++) {
                            IData data = new DataMap();
                            AttrTradeData attrdata = attrTrades.get(i);
                            data.put("ATTR_CODE", attrdata.getAttrCode());
                            data.put("MODIFY_TAG", attrdata.getModifyTag());
                            data.put("RELA_INST_ID", attrdata.getRelaInstId());
                            tradeAttrsDs.add(data);
                        }

                        String commparavalue = "";
                        IDataset ds = CommparaInfoQry.getCommpara("CSM", "3900", "DiscountAttrCode", "0898");
                        for (int i = 0; i < ds.size(); i++) {
                            commparavalue += "," + ds.getData(i).getString("PARA_CODE1").trim() + ",";
                        }
                        //System.out.println("CheckWLWDisconutAction.javaxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx64 " +commparavalue);

                        for (int i = 0; i < tradeAttrsDs.size(); i++) {
                            IData attrTrade = tradeAttrsDs.getData(i);
                            String attrCode = attrTrade.getString("ATTR_CODE", "").trim();
                            String modifyTag = attrTrade.getString("MODIFY_TAG", "").trim();
                            String relaInstId = attrTrade.getString("RELA_INST_ID", "").trim();

                            if (BofConst.MODIFY_TAG_UPD.equals(modifyTag) && commparavalue.indexOf("," + attrCode + ",") != -1) {
                                CSAppException.apperr(CrmCommException.CRM_COMM_103, "物联网优惠折扣属性不允许做修改变更.");
                            }

                            if (commparavalue.indexOf("," + attrCode + ",") != -1) {
                                //                            String filterStr1 = "RELA_INST_ID=" + relaInstId + ",ATTR_CODE=" + attrCode + ",MODIFY_TAG=0";
                                String filterStr2 = "RELA_INST_ID=" + relaInstId + ",ATTR_CODE=" + attrCode + ",MODIFY_TAG=1";
                                //                            IDataset filterData1 = DataHelper.filter(tradeAttrsDs, filterStr1);
                                IDataset filterData2 = DataHelper.filter(tradeAttrsDs, filterStr2);
                                //                            if (IDataUtil.isNotEmpty(filterData1) && IDataUtil.isNotEmpty(filterData2)) {
                                if (IDataUtil.isNotEmpty(filterData2)) {
                                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "物联网优惠折扣属性不允许做修改变更!");
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
