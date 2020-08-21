package com.asiainfo.veris.crm.order.soa.group.changememelement;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;

public class ChangeOnceAttrForDataMemberElement  extends ChangeUserElement {
	
    public ChangeOnceAttrForDataMemberElement() {

    }
    protected void actTradeBefore() throws Exception {
        super.actTradeBefore();

    }

    protected void initReqData() throws Exception {
        super.initReqData();
    }
    @Override
    protected void makInit(IData map) throws Exception
    {
        super.makInit(map);
    }

    protected void makReqData(IData map) throws Exception {

        super.makReqData(map);
    }
    /**
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception {
        super.actTradeSub();
    }

	 protected void setTradeBase() throws Exception {
	        super.setTradeBase();

	        IData map = bizData.getTrade();
	        map.put("OLCOM_TAG", "0");
	        map.put("PF_WAIT", "0");

	      
	    }

	    protected void regTrade() throws Exception {
	        super.regTrade();

	    }

}
