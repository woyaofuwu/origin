package com.asiainfo.veris.crm.order.soa.person.rule.run.createuser;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.CreatePersonUserBean;
import com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.NationalOpenLimitBean;

/**
 * REQ201608090019  营业执照、组织机构代码证、事业单位法人登记证书不能一个证件号码对应多个不同的单位名称。
 * liquan5 201601101 
 * */
public class CheckGlobal1Pspt5Number extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckGlobal1Pspt5Number.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckGlobal1Pspt5Number() >>>>>>>>>>>>>>>>>>");

            String cityCode = databus.getString("CITY_CODE","").trim();

            System.out.println("CheckGlobal1Pspt5Number.javaxxxxxxxxxxxxxx42 " + cityCode);
            if (cityCode.equals("HNHN") || cityCode.equals("HNSJ")) {//这2个业务区手机号码，不校验全国一证5号
                return false;
            }
        
        
        NationalOpenLimitBean bean1 = (NationalOpenLimitBean) BeanManager.createBean(NationalOpenLimitBean.class);
        boolean stop = bean1.isIgnoreCall();
        if(stop){
            return false;
        }
        IData customer = CustomerInfoQry.qryCustInfo(databus.getString("CUST_ID"));
        if (customer != null && customer.size() > 0) {
            String message = "";
            //开户证件全国一证
            String psptTypeCode = customer.getString("PSPT_TYPE_CODE", "");
            String id = customer.getString("PSPT_ID", "");
            String name = customer.getString("CUST_NAME", "");

            logger.error("CheckGlobal1Pspt5Number开户证件信息66  " + psptTypeCode + "  " + name + " " + id );
            IData input = new DataMap();                    
            input.put("PSPT_TYPE_CODE", psptTypeCode);
            input.put("PSPT_ID", id);
            input.put("CUST_NAME", name);                        
            CreatePersonUserBean bean = (CreatePersonUserBean) BeanManager.createBean(CreatePersonUserBean.class);
            IDataset dataset = bean.checkGlobalMorePsptId(input);
            
            if (dataset != null && dataset.size() > 0) {
                IData data = dataset.getData(0);
                if (data != null && !data.getString("CODE", "").equals("0")) {
					// 同一证件号码下存在多个用户姓名，不限制用户办理业务
					if (data.getString("CODE", "").equals("3")) {
						return false;
					} else {
						logger.error("CheckGlobal1Pspt5Number 50");
						BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20170310, data.getString("MSG", ""));
						return true;
					}
                }
            }
            
            //使用人也需要检查全网一证5号 
            IDataset custperson = CustPersonInfoQry.getPerInfoByCustId(databus.getString("CUST_ID"));                     
            if (custperson != null && custperson.size() > 0) {
                String rsrv5use = custperson.getData(0).getString("RSRV_STR5", "").trim();//姓名
                String rsrv6use = custperson.getData(0).getString("RSRV_STR6", "").trim();//证件类型
                String rsrv7use = custperson.getData(0).getString("RSRV_STR7", "").trim();//证件号码
 
                logger.error("CheckGlobal1Pspt5Number使用人证件信息77 " + rsrv5use + "  " + rsrv6use + " " + rsrv7use );
                if (rsrv5use.length() > 0 && rsrv6use.length() > 0 && rsrv7use.length() > 0) {
                    input.clear();
                    input.put("CUST_NAME", rsrv5use);
                    input.put("PSPT_TYPE_CODE", rsrv6use);
                    input.put("PSPT_ID", rsrv7use);
                    dataset = bean.checkGlobalMorePsptId(input);

                    if (dataset != null && dataset.size() > 0) {
                        IData data = dataset.getData(0);
                        if (data != null && !data.getString("CODE", "").equals("0")) {
                        	//同一证件号码下存在多个用户姓名，不限制用户办理业务
							if (data.getString("CODE", "").equals("3")) {
								return false;
							} else {
								logger.error("CheckGlobal1Pspt5Number 79");
								BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20170310, data.getString("MSG", ""));
								return true;
							}
                        }
                    }
                }
            }
            
        } else {
            return false;
        }

        return false;
    }
}
