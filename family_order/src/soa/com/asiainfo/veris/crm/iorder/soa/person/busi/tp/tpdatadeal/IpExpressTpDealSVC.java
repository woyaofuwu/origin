package com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tpdatadeal;

import com.ailk.biz.client.BizServiceFactory;
import com.ailk.biz.data.ServiceResponse;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.iorder.soa.person.busi.tp.base.ReqBuildService;
import com.asiainfo.veris.crm.order.pub.consts.TpConsts;
import com.asiainfo.veris.crm.order.pub.exception.TpOrderException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.person.busi.ipexpress.IpExpressBean;

public class IpExpressTpDealSVC extends ReqBuildService {
    @Override
    public IData initReqData(IData input) throws Exception {
        IpExpressBean bean = BeanManager.createBean(IpExpressBean.class);
        //1、获取已开通ip电话号码
        String serialNumber = input.getString("SERIAL_NUMBER","");
        if(StringUtils.isEmpty(serialNumber)){
            CSAppException.apperr(TpOrderException.TP_ORDER_40004);
        }
        UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
        IData data = bean.getIpExpressInfo(ucaData.getUser().toData());

        IDataset bindIpPhones = data.getDataset("BIND_IPPHONE");
        IDataset codingStr = new DatasetList();
        if(DataUtils.isNotEmpty(bindIpPhones)){
            for (int i = 0; i < bindIpPhones.size();i++){
                IData bindIpPhone = bindIpPhones.getData(i);
                IData coding = new DataMap();
                coding.put("col_cond_CAUSE","0");
                coding.put("col_SERIAL_NUMBER_G",bindIpPhone.getString("OLD_SERIALNUMBER"));
                coding.put("col_OPEN_DATE",bindIpPhone.getString("OPEN_DATE"));
                coding.put("col_CONDITIONM","已删除");
                coding.put("col_PRODUCT_ID",bindIpPhone.getString("OLD_PRODUCT_ID"));
                coding.put("col_BRAND_CODE",bindIpPhone.getString("OLD_BRAND_CODE"));
                coding.put("col_TEMP_PWD",bindIpPhone.getString("TEMP_PWD"));
                coding.put("col_IPServiceText",bindIpPhone.getString("IPServiceText"));
                coding.put("col_OLD_BRAND_CODE",bindIpPhone.getString("OLD_BRAND_CODE"));
                coding.put("col_OLD_PRODUCT_ID",bindIpPhone.getString("OLD_PRODUCT_ID"));
                coding.put("col_OLD_IPServiceText",bindIpPhone.getString("OLD_IPServiceText"));
                coding.put("col_OLD_SERIALNUMBER",bindIpPhone.getString("OLD_SERIALNUMBER"));
                coding.put("col_M_DEAL_TAG","1");
                coding.put("col_USER_ID_B",bindIpPhone.getString("USER_ID_B"));
                coding.put("col_OLD_PWD",bindIpPhone.getString("OLD_PWD"));
                coding.put("col_PACKAGESVC",bindIpPhone.getString("PACKAGESVC"));
                codingStr.add(coding);
            }
        }

        IData commInfo = data.getData("COMM_INFO");
        if(DataUtils.isEmpty(commInfo)){
            commInfo = new DataMap();
        }

        //2、拼接数据
        IData result = new DataMap();
        result.put("X_CODING_STR",codingStr);
        result.put("AUTH_CHECK_PSPT_ID",ucaData.getCustomer().getPsptId());
        result.put("AUTH_SERIAL_NUMBER",ucaData.getUser().getSerialNumber());
        result.put("AUTH_CHECK_PSPT_TYPE_CODE",ucaData.getCustomer().getPsptTypeCode());
        result.put("DEFALTIPPASSWD",commInfo.getString("DEFALTIPPASSWD"));
        result.put("ISDEFALTIPPASSWD",commInfo.getString("ISDEFALTIPPASSWD"));
        result.put(TpConsts.comKey.serialNumber,input.getString(TpConsts.comKey.serialNumber));
        result.put("PHONELENTH",commInfo.getString("PHONELENTH"));
        result.put("PHONETYPE",commInfo.getString("PHONETYPE"));
        result.put("LIMITNUM",commInfo.getString("IPPHONECOUNTLIMIT"));
        result.put("SERIAL_NUMBER_HIDDEN",commInfo.getString("SERIAL_NUMBER_F"));
        result.put("TRADE_TYPE_CODE","260");

        ServiceResponse response = BizServiceFactory.call("SS.IpExpressRegSVC.tradeReg", result);
        IData body = response.getBody();
        return result;
    }

    @Override
    public String getTradeTypeCode(IData input) throws Exception {
        return "260";
    }
}
