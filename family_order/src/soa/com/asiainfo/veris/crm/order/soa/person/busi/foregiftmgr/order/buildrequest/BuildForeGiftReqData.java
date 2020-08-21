/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.foregiftmgr.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.person.busi.foregiftmgr.order.requestdata.ForeGiftReqData;

/**
 * @CREATED by gongp@2014-4-10 修改历史 Revision 2014-4-10 上午10:17:17
 */
public class BuildForeGiftReqData extends BaseBuilder implements IBuilder
{

    /*
     * (non-Javadoc)
     */
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub

        ForeGiftReqData reqData = (ForeGiftReqData) brd;

        reqData.setInvoiceNo(param.getString("INVOICE_NO"));

        reqData.setNonCustomerUserId(param.getString("NON_CUSTOMER_USER_ID"));

        reqData.setOperType(param.getString("OP_CODE"));

        IDataset dataset = new DatasetList(param.getString("USER_FOREGIFTS"));

        // 去掉没有做修改的押金记录
        for (int i = 0; i < dataset.size(); i++)
        {
            IData data = dataset.getData(i);
            if ("".equals(data.getString("tag")))
            {
                dataset.remove(i);
                i--;
            }
        }

        reqData.setUserForeGifts(dataset);

    }

    @Override
    public UcaData buildUcaData(IData param) throws Exception
    {
        // 设置三户资料对象
        String sn = param.getString("SERIAL_NUMBER");
        if (!StringUtils.isBlank(sn))
        {
            IData userInfo = UcaInfoQry.qryUserInfoByUserId(param.getString("SELECTED_AUTH_USER"));
            
            if(IDataUtil.isNotEmpty(userInfo)){
               if("0".equals(userInfo.getString("REMOVE_TAG"))){
                   return super.buildUcaData(param);
               }else{
                   UcaData ucaData = new UcaData();
                   ucaData.setBrandCode(userInfo.getString("BRAND_CODE"));
                   ucaData.setProductId(userInfo.getString("PRODUCT_ID"));
                   ucaData.setUser(new UserTradeData(userInfo));
                   ucaData.setCustomer(new CustomerTradeData(UcaInfoQry.qryCustomerInfoByCustId(userInfo.getString("CUST_ID"))));
                   ucaData.setCustPerson(new CustPersonTradeData(param));
                   ucaData.setAccount(new AccountTradeData(param));
                   ucaData.setLastOweFee("0");
                   ucaData.setRealFee("0");
                   ucaData.setAcctBlance("0");

                   DataBusManager.getDataBus().setUca(ucaData);
                   return ucaData;
               } 
            }
            
            return super.buildUcaData(param);
        }
        else
        {
            param.put("USER_ID", param.getString("NON_CUSTOMER_USER_ID"));
            param.put("SERIAL_NUMBER", "");
            param.put("CUST_ID", "0");
            param.put("ACCT_ID", "0");
            param.put("NET_TYPE_CODE", "00");

            String routeId = param.getString("EPARCHY_CODE");
            if (StringUtils.isBlank(routeId) || routeId.length() != 4 || !StringUtils.isNumeric(routeId))
            {
                param.put("EPARCHY_CODE", "0898");
            }

            UcaData ucaData = new UcaData();
            ucaData.setUser(new UserTradeData(param));
            ucaData.setCustomer(new CustomerTradeData(param));
            ucaData.setCustPerson(new CustPersonTradeData(param));
            ucaData.setAccount(new AccountTradeData(param));
            ucaData.setLastOweFee("0");
            ucaData.setRealFee("0");
            ucaData.setAcctBlance("0");

            DataBusManager.getDataBus().setUca(ucaData);
            return ucaData;
        }
    }

    @Override
    protected void checkBefore(IData input, BaseReqData reqData) throws Exception
    {
        String sn = input.getString("SERIAL_NUMBER");
        if (!StringUtils.isBlank(sn))
        {
            super.checkBefore(input, reqData);
        }
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new ForeGiftReqData();
    }

}
