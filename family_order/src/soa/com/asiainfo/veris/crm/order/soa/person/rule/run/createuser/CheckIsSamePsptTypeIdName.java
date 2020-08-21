package com.asiainfo.veris.crm.order.soa.person.rule.run.createuser;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.CreatePersonUserBean;

/**
 * REQ201608090019  营业执照、组织机构代码证、事业单位法人登记证书不能一个证件号码对应多个不同的单位名称。
 * liquan5 201601101 
 * */
public class CheckIsSamePsptTypeIdName extends BreBase implements IBREScript {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckIsSamePsptTypeIdName.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckIsSamePsptTypeIdName() >>>>>>>>>>>>>>>>>>");
        String strError = null;
        logger.error("CheckIsSamePsptTypeIdName   24:  "+databus.getString("CUST_ID"));  
        
        //String serialNumber = databus.getString("SERIAL_NUMBER", "");
        //IDataset custds = CustomerInfoQry.getABNormalCustInfoBySn(serialNumber);
        //IData customer = custds.getData(0);

        IData customer = CustomerInfoQry.qryCustInfo(databus.getString("CUST_ID"));
        if (customer != null && customer.size() > 0) {
            
            String psptTypeCode = customer.getString("PSPT_TYPE_CODE", "");
            if (!psptTypeCode.equals("E") && !psptTypeCode.equals("G") && !psptTypeCode.equals("M") ) {//营业执照、组织机构代码证、事业单位法人登记证书
                logger.error("CheckIsSamePsptTypeIdName 30");
                return false;
            } else {
                String id = customer.getString("PSPT_ID", "");
                String name = customer.getString("CUST_NAME", "");
                logger.error("CheckIsSamePsptTypeIdName 35");
                IData input = new DataMap();
                input.put("CERT_TYPE", psptTypeCode);
                input.put("CERT_ID", id);
                input.put("CERT_NAME", name);
                
                CreatePersonUserBean bean = (CreatePersonUserBean) BeanManager.createBean(CreatePersonUserBean.class);
                IData data = bean.verifyIdCardName(input);
                if (data != null && data.getString("X_RESULTCODE", "").equals("1")) {
                    logger.error("CheckIsSamePsptTypeIdName 46");
                    strError = "单位类型的证书不能一个证件号码对应多个不同的单位名称！";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20161101, strError);
                    return true;
                }
            }
        } else {
            return false;
        }
        return false;
    }
}
