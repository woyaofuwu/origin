package com.asiainfo.veris.crm.order.soa.group.changememelement;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElementReqData;
import com.asiainfo.veris.crm.order.soa.group.changeuserelement.ChangeInternetUserElementReqData;

public class ChangeDatalineForDataMemberElement extends ChangeUserElement {
    protected ChangeInternetUserElementReqData reqData = null;

    protected String userIdB = "";
    protected String productId = "";

    public ChangeDatalineForDataMemberElement() {

    }

    protected BaseReqData getReqData() throws Exception {
        return new ChangeInternetUserElementReqData();
    }

    protected void actTradeBefore() throws Exception {
        super.actTradeBefore();

    }

    protected void initReqData() throws Exception {
        super.initReqData();

        reqData = (ChangeInternetUserElementReqData) getBaseReqData();

    }
    @Override
    protected void makInit(IData map) throws Exception
    {
        super.makInit(map);
    }

    protected void makReqData(IData map) throws Exception {

        super.makReqData(map);

//        reqData.setInterData(map.getData("ATTRINTERNET"));

        reqData.setDataline(map.getData("DATALINE"));
        productId=map.getString("PRODUCT_ID", "");

    }

    /**
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception {
        super.actTradeSub();
        userIdB = reqData.getUca().getUser().getUserId();
        actTradeDataline();
    }


    private void actTradeDataline() throws Exception {
        IData dataline = reqData.getDataline();
//        IData internet = reqData.getInterData();
        IData lineInfo = new DataMap();
        IDataset dataset = new DatasetList();


        if (null != dataline && dataline.size() > 0) {
            IData inparam = new DataMap();
        	if(productId.equals("7012")||productId.equals("97012")
        		||productId.equals("70121")||productId.equals("970121")
        		||productId.equals("70122")||productId.equals("970122")){
                inparam.put("SHEET_TYPE", "4");

        	}else if(productId.equals("7011")||productId.equals("97011")
        			||productId.equals("70111")||productId.equals("970111")
        			||productId.equals("70112")||productId.equals("970112")){
                inparam.put("SHEET_TYPE", "6");
        	
        	}else if(productId.equals("7016")||productId.equals("97016")){
                inparam.put("SHEET_TYPE", "8");
        	
        	}
        	else if(productId.equals("7010")){
                inparam.put("SHEET_TYPE", "7");
        	}
    		else{
    			CSAppException.apperr(CrmCommException.CRM_COMM_103,"PRODUCT_ID为空或未识别！");

    		}
        	if(!"".equals(dataline.getString("PORTACONTACT", ""))){
        		dataline.put("PORT_CONTACT_A", dataline.getString("PORTACONTACT", ""));
        	}
        	if(!"".equals(dataline.getString("PORTZCONTACT", ""))){
        		dataline.put("PORT_CONTACT_Z", dataline.getString("PORTZCONTACT", ""));
        	}
        	if(!"".equals(dataline.getString("PORTACONTACTPHONE", ""))){
        		dataline.put("PORT_CONTACT_PHONE_A", dataline.getString("PORTACONTACTPHONE", ""));
        	}
        	if(!"".equals(dataline.getString("PORTZCONTACTPHONE", ""))){
        		dataline.put("PORT_CONTACT_PHONE_Z", dataline.getString("PORTZCONTACTPHONE", ""));
        	}
        	
        	String productNo = dataline.getString("PRODUCTNO");

            // 查询专线信息
            inparam.put("USER_ID", reqData.getUca().getUser().getUserId());
            inparam.put("PRODUCT_NO", productNo);

            IDataset datalineList = TradeOtherInfoQry.queryUserDataLineByUserIdAndProductNo(inparam);
            if (null != datalineList && datalineList.size() > 0)
            {
                lineInfo = datalineList.getData(0);
            }

            IData lineInfoNew = new DataMap();
            lineInfoNew.putAll(lineInfo);
            lineInfoNew.putAll(dataline);



//            dataset = DatalineUtil.updateTradeUserDataline(lineInfoNew, lineInfo, lineInfo, userData);
            
            lineInfoNew.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            super.addTradeDataLine(lineInfoNew);
        }
    }



    protected void setTradeBase() throws Exception {
        super.setTradeBase();

        IData map = bizData.getTrade();
        map.put("OLCOM_TAG", "0");
        map.put("PF_WAIT", "0");

      
    }

    @Override
    protected void regTrade() throws Exception {
        super.regTrade();

    }

   

    

    

}
