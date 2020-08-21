
package com.asiainfo.veris.crm.order.soa.person.busi.flow.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;


public class FlowExchangeReqData extends BaseReqData
{
    private String fmbalanceid;// 账本流水

    private String fmacctid;// 账户标识

    private String commid;// 商品编码

    private String channelid;// 操作渠道

    private String transvalue;// 兑换流量总额
    
    private String effectivedate;// 有效期开始
    
    private String expiredate;// 有效期结束
    
    private String fmassettypeid;// 账本类型
    
    private String initflow;// 初始额度
    
    private String initfee;// 购买金额
    
    private String balance;// 账本余额

	public String getFmbalanceid() {
		return fmbalanceid;
	}

	public void setFmbalanceid(String fmbalanceid) {
		this.fmbalanceid = fmbalanceid;
	}

	public String getFmacctid() {
		return fmacctid;
	}

	public void setFmacctid(String fmacctid) {
		this.fmacctid = fmacctid;
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

	public String getTransvalue() {
		return transvalue;
	}

	public void setTransvalue(String transvalue) {
		this.transvalue = transvalue;
	}

	public String getFmassettypeid() {
		return fmassettypeid;
	}

	public void setFmassettypeid(String fmassettypeid) {
		this.fmassettypeid = fmassettypeid;
	}

	public String getInitflow() {
		return initflow;
	}

	public void setInitflow(String initflow) {
		this.initflow = initflow;
	}

	public String getInitfee() {
		return initfee;
	}

	public void setInitfee(String initfee) {
		this.initfee = initfee;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}
}
