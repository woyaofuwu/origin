package com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.NationalOpenLimitBean;

/**
 * 
 * @author ouyang 一证五号完工action 增加号码
 * 
 */
public class OpenLimitAdd310Action implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {

        String cityCode = mainTrade.getString("CITY_CODE", "").trim();
        if (cityCode.equals("HNHN") || cityCode.equals("HNSJ")) {// HNHN HNSJ的号码，不做任何事情

        } else {

            NationalOpenLimitBean bean = (NationalOpenLimitBean) BeanManager.createBean(NationalOpenLimitBean.class);
            boolean isIgnoreCall = bean.isIgnoreCall();

            UcaData ucaData = UcaDataFactory.getNormalUca(mainTrade.getString("SERIAL_NUMBER", "").trim());

            //客户信息
            //CustomerTradeData customerData = ucaData.getCustomer(); 
            CustPersonTradeData custpersonData = ucaData.getCustPerson();
 
            String custName = custpersonData.getCustName();
            String psptTypeCode = custpersonData.getPsptTypeCode();
            String psptId = custpersonData.getPsptId();
            if (psptTypeCode.equals("D") || psptTypeCode.equals("E") || psptTypeCode.equals("G") || psptTypeCode.equals("L") || psptTypeCode.equals("M")) {
                //如开户证件是单位证件类型，则需同步使用人信息
                //RSRV_STR5 名称              RSRV_STR6 证件类型                     RSRV_STR7 证件号码
                custName = custpersonData.getRsrvStr5();
                psptTypeCode = custpersonData.getRsrvStr6();
                psptId = custpersonData.getRsrvStr7();
            }

            if (custName!=null&&custName.length() > 0 &&psptTypeCode!=null&& psptTypeCode.length() > 0 &&psptId!=null&& psptId.length() > 0) {

                IData compara = new DataMap();
                compara.put("SUBSYS_CODE", "CSM");
                compara.put("PARAM_ATTR", "2552");
                compara.put("PARAM_CODE", psptTypeCode);
                compara.put("EPARCHY_CODE", "ZZZZ");
                IDataset openLimitResult = CommparaInfoQry.getCommparaAllCol(compara.getString("SUBSYS_CODE", ""), compara.getString("PARAM_ATTR", ""), compara.getString("PARAM_CODE", ""), compara.getString("EPARCHY_CODE", ""));

                //证件类型转换 
                IDataset checkResults = bean.checkPspt(psptTypeCode);
                if (IDataUtil.isNotEmpty(checkResults)) {

                    String psptTypeCodeChange = checkResults.getData(0).getString("PARA_CODE1");

                    IData custData = new DataMap();
                    //获取交易流水号
                    custData = bean.addSeq(custData);
                    custData.put("IDV", mainTrade.getString("SERIAL_NUMBER"));
                    custData.put("HOME_PROV", mainTrade.getString("EPARCHY_CODE"));
                    custData.put("CUSTOMER_NAME", custName);
                    custData.put("ID_CARD_TYPE", psptTypeCodeChange);
                    custData.put("ID_CARD_NUM", psptId);
                    custData.put("OPR", "01");
                    custData.put("EFFETI_TIME", mainTrade.getString("ACCEPT_DATE"));
                    custData.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                    custData.put("CHECKING_DATE", SysDateMgr.getSysDateYYYYMMDD());

                    custData.put("ID_VALUE", mainTrade.getString("SERIAL_NUMBER"));
                    custData.put("EPARCHY_CODE", mainTrade.getString("EPARCHY_CODE"));
                    custData.put("UPDATE_TIME", SysDateMgr.getSysDate());
                    custData.put("UPDATE_STAFF_ID", mainTrade.getString("UPDATE_STAFF_ID"));
                    custData.put("UPDATE_DEPART_ID", mainTrade.getString("UPDATE_DEPART_ID"));
                    custData.put("REMARK", mainTrade.getString("REMARK"));

                    /*            //先进行预占操作
                                IData input = new DataMap();
                                input.put("CUST_NAME", custName);
                                input.put("PSPT_TYPE_CODE", psptTypeCode);
                                input.put("PSPT_ID", psptId);
                                input.put("TRADE_TYPE_CODE", mainTrade.getString("TRADE_TYPE_CODE",""));
                                input.put("SERIAL_NUMBER", mainTrade.getString("SERIAL_NUMBER",""));
                                IDataset campDs =bean.idCheckAndCampOn(input);                        
                                */

                    if (!isIgnoreCall && DataSetUtils.isNotBlank(openLimitResult)) {

                        //写入IBOSS扫描操作的预占表
                        bean.regCampOnIBOSS(custData);

                        /*
                        //查询预占凭证
                        IData param = new DataMap();
                        param.put("ID_CARD_TYPE", custData.getString("ID_CARD_TYPE"));
                        param.put("ID_CARD_NUM", custData.getString("ID_CARD_NUM"));
                        param.put("CAMP_ON", "01");
                        IDataset PSeqInfos = bean.selCampOn(param);
                        //如果找不到预占凭证，认为可能是网络原因之类的导致没有预占信息，不同步，等待对账处理
                        if (IDataUtil.isNotEmpty(PSeqInfos)) {
                            custData.put("P_SEQ", PSeqInfos.getData(0).getString("P_SEQ", ""));

                            custDataset.add(custData);
                            synParam.put("USER_DATASET", custDataset);
                            //调用同步接口
                            try {
                                bean.userInfoSyn(synParam);
                            } catch (Exception e) {
                                //重发处理
                                //e.printStackTrace();
                            }
                        }
                        */}

                    //登记一证多号对账表
                    bean.regOpenLimitCheck(custData);

                }
            }
        }
    }
}
