
package com.asiainfo.veris.crm.order.soa.person.busi.flow.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;


public class FlowChargeReqData extends BaseReqData
{
    private String amount;// 充值额度

    private String transfee;// 话费支付金额

    private String commid;// 商品编码

    private String channelid;// 操作渠道

    private String peerbusinessid;// 外部流水
    
    private String effectivedate;// 有效期开始
    
    private String expiredate;// 有效期结束
    
    private String transneeded;// 话费支付金额
    
    private String commnum;// 购买数量

	public String getAmount() 
	{
		return amount;
	}

	public void setAmount(String amount) 
	{
		this.amount = amount;
	}

	public String getTransfee() 
	{
		return transfee;
	}

	public void setTransfee(String transfee) 
	{
		this.transfee = transfee;
	}

	public String getCommid() 
	{
		return commid;
	}

	public void setCommid(String commid) 
	{
		this.commid = commid;
	}

	public String getChannelid() 
	{
		return channelid;
	}

	public void setChannelid(String channelid) 
	{
		this.channelid = channelid;
	}

	public String getPeerbusinessid() 
	{
		return peerbusinessid;
	}

	public void setPeerbusinessid(String peerbusinessid) 
	{
		this.peerbusinessid = peerbusinessid;
	}

	public String getEffectivedate() 
	{
		return effectivedate;
	}

	public void setEffectivedate(String effectivedate) 
	{
		this.effectivedate = effectivedate;
	}

	public String getExpiredate() 
	{
		return expiredate;
	}

	public void setExpiredate(String expiredate) 
	{
		this.expiredate = expiredate;
	}

	public String getTransneeded() {
		return transneeded;
	}

	public void setTransneeded(String transneeded) {
		this.transneeded = transneeded;
	}

	public String getCommnum() {
		return commnum;
	}

	public void setCommnum(String commnum) {
		this.commnum = commnum;
	}
}
