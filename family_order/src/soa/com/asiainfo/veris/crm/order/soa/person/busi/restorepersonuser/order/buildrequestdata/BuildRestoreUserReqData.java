
package com.asiainfo.veris.crm.order.soa.person.busi.restorepersonuser.order.buildrequestdata;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.ProductData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.person.busi.restorepersonuser.order.requestdata.RestoreUserReqData;

public class BuildRestoreUserReqData extends BaseBuilder implements IBuilder
{

    public void buildBusiRequestData(IData data, BaseReqData brd) throws Exception
    {
        RestoreUserReqData restoreUserRD = (RestoreUserReqData) brd;
        restoreUserRD.setX_coding_str(data.getString("X_CODING_STR"));
        restoreUserRD.setInvoiceNo(data.getString("INVOICE_NO", ""));
        restoreUserRD.setOldSimCardNo(data.getString("OLD_SIM_CARD_NO", ""));
        restoreUserRD.setNewSimCardNo(data.getString("NEW_SIM_CARD_NO", ""));
        
        String serialNumber = data.getString("SERIAL_NUMBER");
        IDataset resDataset = ResCall.restoreCheckMPhone(serialNumber);
        if (IDataUtil.isEmpty(resDataset))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_95, "ResCall.restoreCheckMPhone接口返回空！");
        }
        IData map = resDataset.getData(0);
        if (StringUtils.equals("5",data.getString("KI_STATE","")) ||  StringUtils.equals("2",data.getString("KI_STATE","")) ||  StringUtils.equals("6",data.getString("KI_STATE","")))
        {
        	restoreUserRD.setNeedRePosses("1");
        }else{
        	restoreUserRD.setNeedRePosses(data.getString("NEED_REPOSSESS",""));
        }
        restoreUserRD.setSimFeeTag(data.getString("SIM_FEE_TAG"));
        restoreUserRD.setSimCardSaleMoney(data.getString("SIM_CARD_SALE_MONEY"));

        ProductData product = new ProductData(data.getString("PRODUCT_ID"));
        restoreUserRD.setMainProduct(product);
        
        buildElems(data, restoreUserRD);
    }
    
    public void addElems(IData param, IDataset data) throws Exception
    {
    	 String strDiscnts = param.getString("RES_DISCNTS", "");
    	 String strSvcs = param.getString("RES_SVCS", "");
    	 String disctStartDate = SysDateMgr.getSysDate();
    	 String disctEndDate = SysDateMgr.END_TIME_FOREVER;
    	 String scvStartDate = SysDateMgr.getSysDate();
    	 String svcEndDate = SysDateMgr.END_TIME_FOREVER;
    	 
    	 for (int i = 0; i < data.size(); i++)
         {
    		 IData elem = data.getData(i);
             String elemTypeCode = elem.getString("ELEMENT_TYPE_CODE", "");
             if ("D".equals(elemTypeCode))
             {
            	 disctStartDate = elem.getString("START_DATE");
            	 disctEndDate = elem.getString("END_DATE");
            	 
             }
             else if ("S".equals(elemTypeCode))
             {
            	 scvStartDate = elem.getString("START_DATE");
            	 svcEndDate = elem.getString("END_DATE");
             }
         }
    	 if (StringUtils.isNotEmpty(strDiscnts))
         {
    		 IDataset elems = new DatasetList(strDiscnts);
    		 int length = elems.size();
    		 for (int j = 0; j < length; j++)
             {
                 IData elem = elems.getData(j);
                 elem.put("PACKAGE_ID", "-1");
                 elem.put("MODIFY_TAG", "0");
                 elem.put("START_DATE", disctStartDate);
                 elem.put("END_DATE", disctEndDate);
                 data.add(elem);
             }
    		 
         }
    	 if (StringUtils.isNotEmpty(strSvcs))
         {
    		 IDataset elems = new DatasetList(strSvcs);
    		 int length = elems.size();
    		 for (int j = 0; j < length; j++)
             {
                 IData elem = elems.getData(j);
                 elem.put("PACKAGE_ID", "-1");
                 elem.put("MODIFY_TAG", "0");
                 elem.put("START_DATE", scvStartDate);
                 elem.put("END_DATE", svcEndDate);
                 data.add(elem);
             }
    		 
         }
    	 
    }

    public void buildElems(IData param, RestoreUserReqData brd) throws Exception
    {
        /* 拼装子元素 */
        String str = param.getString("SELECTED_ELEMENTS", "");
        if (StringUtils.isNotEmpty(str))
        {
            IDataset elems = new DatasetList(str);
            this.addElems(param, elems);
            int len = elems.size();
            for (int i = 0; i < len; i++)
            {
                IData elem = elems.getData(i);
                String elemTypeCode = elem.getString("ELEMENT_TYPE_CODE", "");

                if ("D".equals(elemTypeCode))
                {
                    brd.addPmd(new DiscntData(elem));
                }
                else if ("S".equals(elemTypeCode))
                {
                    // 如果用户有这个服务，则不拼到requestData中
                    if (brd.getUca().checkUserIsExistSvcId(elem.getString("ELEMENT_ID", "")))
                    {
                        continue;
                    }
                    brd.addPmd(new SvcData(elem));
                }
            }
        }
    }

    // 重写构造UCA数据的方法
    public UcaData buildUcaData(IData param) throws Exception
    {
        String userId = param.getString("USER_ID", "");
        return UcaDataFactory.getDestroyUcaByUserId(userId);
    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new RestoreUserReqData();
    }
}
