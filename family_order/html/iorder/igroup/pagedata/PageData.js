PageData = function(id) {
	this.id = id;
};

var pageData;

$(function(){
	
	pageData = new PageData();
	
	// 创建页面数据存放区域
	pageData.addHtmlField("e_pageDatas");
});

(function(c){
	var o = c.prototype;
	
	/**
	 * 设置DataMap,或DataList数据到指定的隐藏域
	 * 隐藏域的格式为: <span id="xxx" style="display:none"></span>
	 */
	c.setData = function ($el, data) {
		if (data) {
			var str = data.toString();
			if($el.nodeName()=="INPUT"){
				$el.val(str);
			}else{
				$el.text(str);
			}
		}
	};
	
	/**
	 * 从隐藏域获取DataMap类型的数据
	 */
	c.getData = function ($el) {
		var str = "";
		if($el.nodeName()=="INPUT"){
			str = $el.val();
		}else{
			str = $el.text();
		}
		var data = new Wade.DataMap(str);
		return data;
	};
	
	/**
	 * 从隐藏域获取DatasetList类型的数据
	 */
	c.getDataList = function ($el) {
		var str = "";
		if($el.nodeName()=="INPUT"){
			str = $el.val();
		}else{
			str = $el.text();
		}
		var data = new Wade.DatasetList(str);
		return data;
	};
	
	c.getPathData = function(obj, expr) {
		return jsonPath(obj, expr);
	};
	
	c.submit = function() {
		
		var self = new PageData($(".e_PAGE_DATAS").attr("id"));
		//保存商品信息
		if($.isFunction(window["saveOffers"]))
		{
			saveOffers();
		}
		else
		{
			self.saveOffers();
		}
		//保存集团客户信息
		if($.isFunction(window["saveEcCustInfo"]))
		{
			saveEcCustInfo();
		}
		else
		{
			self.saveEcCustInfo();
		}
		//保存集团账户信息
		if($.isFunction(window["saveEcAcctInfo"]))
		{
			saveEcAcctInfo();
		}
		else
		{
			self.saveEcAcctInfo();
		}
		//保存集团用户信息
		if($.isFunction(window["saveEcSubscriberInfo"]))
		{
			saveEcSubscriberInfo();
		}
		else
		{
			self.saveEcSubscriberInfo();
		}
		//保存成员客户信息
		if($.isFunction(window["saveMemCustInfo"]))
		{
			saveMemCustInfo();
		}
		else
		{
			self.saveMemCustInfo();
		}
		//保存成员账户信息
		if($.isFunction(window["saveMemAcctInfo"]))
		{
			saveMemAcctInfo();
		}
		else
		{
			self.saveMemAcctInfo();
		}
		//保存成员用户信息
		if($.isFunction(window["saveMemSubscriberInfo"]))
		{
			saveMemSubscriberInfo();
		}
		else
		{
			self.saveMemSubscriberInfo();
		}
		//保存公共信息
		if($.isFunction(window["saveCommonInfo"]))
		{
			saveCommonInfo();
		}
		//保存其他信息
		if($.isFunction(window["saveOtherInfo"]))
		{
			saveOtherInfo();
		}
		
		//保存稽核信息
		if($.isFunction(window["saveAuditInfo"]))
		{
			self.setAuditInfo(saveAuditInfo());
		}
		
		
		var submitData = new Wade.DatasetList();
		var data = new Wade.DataMap();
		data.put("SERIAL_NUMBER", $("#cond_SERIAL_NUMBER").val());
//		data.put("type", $(".e_PAGE_DATAS").attr("type"));
		data.put("OPER_TYPE", self.getOperType());
		data.put("ACCT_INFO", self.getAccountInfo());
		data.put("CUST_INFO", self.getCustInfo());
		data.put("SUBSCRIBER", self.getSubscriber());
		data.put("COMMON_INFO", self.getCommonInfo());
		data.put("OTHER_INFO", self.getOtherInfo());
		data.put("OFFERS", self.getOffers());
		data.put("AUDIT_INFO", self.getAuditInfo());
//		if("MEM" == $(".e_PAGE_DATAS").attr("type"))
//		{
			data.put("MEM_ACCT_INFO", self.getMemAccountInfo());
			data.put("MEM_CUST_INFO", self.getMemCustInfo());
			data.put("MEM_SUBSCRIBER", self.getMemSubscriber());
//		}
		
		// 添加费用信息
		if($.feeMgr){
			data.put("X_TRADE_FEESUB", $.feeMgr.getFeeList());
			data.put("X_TRADE_PAYMONEY", $.feeMgr.getPayModeList());
		}
		submitData.add(data);
		$.cssubmit.setParam("SUBMIT_DATA", encodeURIComponent(submitData.toString())); 	
		$.cssubmit.setParam("OFFER_CODE", $("#cond_OFFER_CODE").val()); 	
		$.cssubmit.setParam("BRAND_CODE", $("#cond_EC_BRAND").val()); 	

		$.cssubmit.submitTrade();	
		
//		$.beginPageLoading("业务受理中，请稍后...");
//		ajaxSubmit("", "submit", "&SUBMIT_DATA="+encodeURIComponent(submitData.toString())+"&SERIAL_NUMBER="+$("#ACCESS_NUM").val()+"&OFFER_CODE="+$("#cond_OFFER_CODE").val()+"&BRAND_CODE="+$("#cond_EC_BRAND").val(), "", function (json) {
//            // success
//			$.endPageLoading();
//			$.cssubmit.callBack.beforePay(json);
//
//        }, function (errCode, errDesc, errStack) {
//            // faild
//        	$.endPageLoading();
//        	MessageBox.error("错误提示", "业务受理失败！", "", null, errStack);	
//        });
	};
	
	/**
	 * 往页面添加隐藏数据域
	 */
	o.addDataField = function(id) {
		$("body").append("<span id=\""+id+"\" style=\"display:none\"></span>");
	};
	
	o.addHtmlField = function(id, type) {
		this.id = id;
		var html = "";
		type = type ? type : "MEM";
		html += '<p id="'+id+'" class="e_PAGE_DATAS" type="'+type+'">';
	    html += '<span class="e_ACCT_INFO" desc="账户信息" style="display: none"></span>';
		html += '<span class="e_CUST_INFO" desc="客户信息" style="display: none"></span>';
		html += '<span class="e_SUBSCRIBER" desc="用户信息" style="display: none"></span>';
		html += '<span class="e_COMMON_INFO" desc="公共信息" style="display: none"></span>';
		html += '<span class="e_OTHER_INFO" desc="其他信息" style="display: none"></span>';
		html += '<span class="e_OFFERS" desc="商品信息" style="display: none"></span>';
		html += '<span class="e_AUDIT_INFO" desc="稽核信息" style="display: none"></span>';
//		if("MEM" == type)
//		{
			html += '<span class="e_MEM_ACCT_INFO" desc="成员账户信息" style="display: none"></span>';
			html += '<span class="e_MEM_CUST_INFO" desc="成员客户信息" style="display: none"></span>';
			html += '<span class="e_MEM_SUBSCRIBER" desc="成员用户信息" style="display: none"></span>';
//		}
		html += '</p>';
		$("body").append(html);
	};
	
	o.saveOffers = function() {
		var list = new Wade.DatasetList();
		// 主体商品
		var mainOffer = PageData.getData($(".e_SelectOfferPart"));
		list.add(mainOffer);
		
		this.setOffers(list);
	};
	
	o.saveEcCustInfo = function() {//保存集团客户信息
		var custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
		this.setCustInfo(custInfo);
	};
	
	o.saveEcAcctInfo = function() {//保存集团账户信息
		
	};
	
	o.saveEcSubscriberInfo = function() {//保存集团用户信息
		
	};
	
	o.saveMemCustInfo = function() {//保存成员客户信息
		
	};
	
	o.saveMemAcctInfo = function() {//保存成员账户信息
		
	};
	
	o.saveMemSubscriberInfo = function() {//保存成员用户信息
		
	};
	
	o.getOperType = function() {
		return $("#cond_OPER_TYPE").val();
	}
	
	o.setAccountInfo = function(data) {
		c.setData($("#"+this.id+" .e_ACCT_INFO"), data);
	};
	
	o.getAccountInfo = function() {
		return c.getData($("#"+this.id+" .e_ACCT_INFO"));
	};
	
	o.setCustInfo = function(data) {
		c.setData($("#"+this.id+" .e_CUST_INFO"), data);
	};
	
	o.getCustInfo = function() {
		return c.getData($("#"+this.id+" .e_CUST_INFO"));
	};
	
	o.setSubscriber = function(data) {
		c.setData($("#"+this.id+" .e_SUBSCRIBER"), data);
	};
	
	o.getSubscriber = function() {
		return c.getData($("#"+this.id+" .e_SUBSCRIBER"));
	};
	
	o.setMemAccountInfo = function(data) {
		c.setData($("#"+this.id+" .e_MEM_ACCT_INFO"), data);
	};
	
	o.getMemAccountInfo = function() {
		return c.getData($("#"+this.id+" .e_MEM_ACCT_INFO"));
	};
	
	o.setMemCustInfo = function(data) {
		c.setData($("#"+this.id+" .e_MEM_CUST_INFO"), data);
	};
	
	o.getMemCustInfo = function() {
		return c.getData($("#"+this.id+" .e_MEM_CUST_INFO"));
	};
	
	o.setMemSubscriber = function(data) {
		c.setData($("#"+this.id+" .e_MEM_SUBSCRIBER"), data);
	};
	
	o.getMemSubscriber = function() {
		return c.getData($("#"+this.id+" .e_MEM_SUBSCRIBER"));
	};
	
	o.setCommonInfo = function(data) {
		c.setData($("#"+this.id+" .e_COMMON_INFO"), data);
	};
	
	o.getCommonInfo = function() {
		return c.getData($("#"+this.id+" .e_COMMON_INFO"));
	};
	
	o.setOtherInfo = function(data) {
		c.setData($("#"+this.id+" .e_OTHER_INFO"), data);
	};
	
	o.getOtherInfo = function() {
		return c.getData($("#"+this.id+" .e_OTHER_INFO"));
	};
	
	o.setOffers = function(datas) {
		c.setData($("#"+this.id+" .e_OFFERS"), datas);
	};
	
	o.getOffers = function() {
		return c.getDataList($("#"+this.id+" .e_OFFERS"));
	};
	
	
	o.setAuditInfo = function(data) {
		c.setData($("#"+this.id+" .e_AUDIT_INFO"), data);
	};
	
	o.getAuditInfo = function() {
		return c.getData($("#"+this.id+" .e_AUDIT_INFO"));
	};
	
	
})(PageData);