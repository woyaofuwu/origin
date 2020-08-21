
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcreateuser.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DESUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BroadBandException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcreateuser.order.requestdata.CttBroadbandCreateReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.buildrequest.BuildCreateUserRequestData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: BuildCttBroadbandCreateReqData.java
 * @Description: 创建铁通宽带开户请求对象
 * @version: v1.0.0
 * @author: yxd
 * @date: 2014-3-12 上午11:07:16 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-3-12 yxd v1.0.0 修改原因
 */
public class BuildCttBroadbandCreateReqData extends BuildCreateUserRequestData implements IBuilder
{

    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        param.getString("PAY_MODE_CODE", "");
        param.getString("BANK_CODE");
        super.buildBusiRequestData(param, brd);
        CttBroadbandCreateReqData cttReqData = (CttBroadbandCreateReqData) brd;
        IData productList = UProductInfoQry.qryProductByPK(param.getString("PRODUCT_ID"));
        if (IDataUtil.isEmpty(productList))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1033, param.getString("PRODUCT_ID"));
        }
        cttReqData.setSuperBankCode(param.getString("SUPER_BANK_CODE"));
        UcaData ucaData = brd.getUca();
        UserTradeData userTradeData = ucaData.getUser();
        ucaData.getAccount().setNetTypeCode("11");
        userTradeData.setNetTypeCode("11");
        userTradeData.setPrepayTag("0");
        // 宽带信息
        cttReqData.setCttPhone(param.getString("CTT_PHONE"));
        cttReqData.setStandAddressCode(param.getString("STAND_ADDRESS_CODE"));
        cttReqData.setStandAddress(param.getString("STAND_ADDRESS"));
        cttReqData.setMofficeId(param.getString("SIGN_PATH"));
        cttReqData.setConnectType(param.getString("CONNECT_TYPE"));
        cttReqData.setAddrDesc(param.getString("DETAIL_ADDRESS"));
        cttReqData.setBroadBandAcctId(param.getString("WIDENET_ACCT_ID"));
        cttReqData.setBroadBandPwd(DESUtil.encrypt(param.getString("WIDENET_ACCT_PASSWD")));

        cttReqData.setModemStyle(param.getString("MODEM_STYLE")); // modem方式
        cttReqData.setModemNumberic(param.getString("SALETYPE_CODE", "")); // 型号
        cttReqData.setModemNumbericDesc(param.getString("SALETYPE_DESC", ""));
        cttReqData.setModemCode(param.getString("MATERIAL_CODE", "")); // 资源串号
        
        cttReqData.setAgentCustName(param.getString("AGENT_CUST_NAME",""));
        cttReqData.setAgentPsptTypeCode(param.getString("AGENT_PSPT_TYPE_CODE", ""));
        cttReqData.setAgentPsptId(param.getString("AGENT_PSPT_ID",""));
        cttReqData.setAgentPsptAddr(param.getString("AGENT_PSPT_ADDR",""));     
        
        // 设置产品速率
        String tmpSvcSpeed = "";
        String svcElementName = "";
        String rate = "";
        int count = 0;
        int svcCount = 0;
        IDataset selectedElements = new DatasetList(param.getString("SELECTED_ELEMENTS"));
        if (IDataUtil.isNotEmpty(selectedElements))
        {
            for (int i = 0; i < selectedElements.size(); i++)
            {
                IData element = selectedElements.getData(i);
                String elementId = selectedElements.getData(i).getString("ELEMENT_ID");

                if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")) && !StringUtils.equals(elementId, "501"))
                {
                    if ("0".equals(element.getString("MODIFY_TAG")))
                    {
                        svcCount++;
                    }
                    IDataset commparas = ParamInfoQry.getCommparaByCode("CSM", "1127", elementId, CSBizBean.getTradeEparchyCode());
                    if (commparas.size() == 0)
                    {
                        CSAppException.apperr(ParamException.CRM_PARAM_503, elementId);
                    }

                    rate = commparas.getData(0).getString("PARA_CODE1");
                    tmpSvcSpeed = Integer.parseInt(rate) / 1024 + "M";
                    if (!"".equals(tmpSvcSpeed))
                    {
                        svcElementName = USvcInfoQry.getSvcNameBySvcId(elementId);
                        break;// 由于目前只能有一种速率,多的速率,页面会做判断.
                    }
                }
            }
            for (int i = 0; i < selectedElements.size(); i++)
            {
                IData element = selectedElements.getData(i);
                String elementId = selectedElements.getData(i).getString("ELEMENT_ID");
                String elementName = UDiscntInfoQry.getDiscntNameByDiscntCode(elementId);
                if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")))
                {
                    count++;
                    if (elementName.indexOf(tmpSvcSpeed) < 0)
                    {
                        CSAppException.apperr(BroadBandException.CRM_BROADBAND_104, elementName, svcElementName);
                    }
                }
            }
            if (count == 0 || svcCount == 0)
            {
                CSAppException.apperr(ProductException.CRM_PRODUCT_71);
            }

            cttReqData.setRate(rate); // 设置速率
        }

        // //用户基本信息
        cttReqData.setPhone(param.getString("PHONE"));
        cttReqData.setPsptTypeCode(param.getString("PSPT_TYPE_CODE"));
        cttReqData.setPsptId(param.getString("PSPT_ID"));
        cttReqData.setPsptAddr(param.getString("PSPT_ADDR"));
        cttReqData.setCustName(param.getString("CUST_NAME"));
        cttReqData.setContact(param.getString("CONTACT"));
        cttReqData.setContactPhone(param.getString("CONTACT_PHONE"));
        cttReqData.setCityName(param.getString("CITY_NAME"));
        cttReqData.setPostAddr(param.getString("ASSURE_POST_ADDRESS"));
        cttReqData.setPostCode(param.getString("ASSURE_POST_CODE"));
        cttReqData.setWidenetIpAddr(param.getString("WIDENET_IP_ADDRESS"));

    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new CttBroadbandCreateReqData();
    }

}
