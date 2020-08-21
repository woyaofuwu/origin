var EnterpriseAcctMgr = function(id, param) {
    this.id = id;
    this.param = param;
    this.cacheData = {};
    EnterpriseAcctMgr.instances.push(this);
};

(function(c){
	var o = c.prototype;
	c.instances = [];
	
	o._validate = function() {
		var fild = this.id + "_form2";
		if (!$.validate.verifyAll(fild)) {
			return false;
		}
		fild = this.id + "_form3";
		if (!$.validate.verifyAll(fild)) {
			return false;
		}
		
		fild = this.id + "_form4";
		if (!isHide(fild)) {
			if (!$.validate.verifyAll(fild)) {
				return false;
			}
		}
		
		return true;
    };
	
	o.submit = function(el) {
		if (!this._validate()) return;
		
		var self = this;
        var callback = this.param.callback;
        var isCommit = this.param.isCommit;
        var title = $("#"+this.id).data("title");
        title = title ? title : "addAccount";
        
        var data = {};
		data["CUST_INFO"] = this._getCustInfo();
		data["ACCT_INFO"] = this._assemberData();
        
        var param = "&ajaxListener="+title;
        param += "&SUBMIT_DATA="+JSON.stringify(data);
        
        if (isCommit) {
        	$.beginPageLoading("操作中...");
        	$.ajax.submit(null, null, param, this.id+"MgrPart", function (json) {
                // success
        		$.endPageLoading();
            	var result = JSON.parse(json);
            	
            	if (callback) {
            		data["ACCT_ID"] = result.ACCT_ID;
            		callback.call(self, new Wade.DataMap(data));
    			}
            	
            	$.MessageBox.success("操作成功!", "流水: "+result.flowId, function(btn){
    				if ("ok" == btn) {
    					hidePopup(el);
    				}
    			});
            }, function (errCode, errDesc, errStack) {
                // faild
            	$.endPageLoading();
            	$.MessageBox.error("错误提示", "操作失败！", "", null, errStack);	
            });
		} else {
			callback.call(self, new Wade.DataMap(data));
		}
        
        hidePopup($(el).closest(".c_popup").attr("id"));
    };
    
    /**
     * 重置
     */
    o.reset = function(el) {
    	var title = $("#"+this.id).data("title");
        title = title ? title : "addAccount";
        
    	this.fillAcctPopup({}, true);
    	
    	if (title == "modifyAccount") {
    		this.showModifyPopup(this.cacheData.ACCT_ID);
    	} else if (title == "addAccount") {
    		this.showAddPopup();
    	}
    };
    
    /**
     * title: 监听方法
     */
    o.showPopup = function(title, acctId) {
    	if (this.param.isClear) {
    		this._clearAcctPopup();
		}
    	
        $("#"+this.id).data("title", title ? title : "");
    };
    
    o.showAddPopup = function() {
    	this.showPopup("addAccount");
    	this._addAccount();
    };
    
    o.showModifyPopup = function(acctId) {
    	if (acctId) {
    		this.showPopup("modifyAccount", acctId);
        	this._modifyAccount(acctId);
		}
    };
    
    o.showInfoPopup = function(acctId, title, callback) {
    	var self = this;
    	$("#"+this.id+" .c_submit").css("display", "none");
    	if (acctId) {
    		this.showPopup("modifyAccount", acctId);
        	this._modifyAccount(acctId, function() {
        		self.setPopupTitle(title);
        		self._disableAll(true);
            	
            	if (callback) {
            		callback.call(self, self);
        		}
        	});
		}
    };
    
    o.deleteAccount = function(acctId) { 
    	var data = {};
		data["CUST_INFO"] = this._getCustInfo();
        
    	var param = "&ajaxListener=deleteAccount";
    	param += "&ACCT_ID="+acctId;
    	param += "&SUBMIT_DATA="+JSON.stringify(data);
    	
    	$.beginPageLoading("操作中...");
        ajaxPost(null, null, param, this.id+"MgrPart", function (json) {
            // success
        	$.endPageLoading();
        	var result = JSON.parse(json);
        	
        	$.MessageBox.success("操作成功!", "流水: "+result.flowId, function(btn){
				if ("ok" == btn) {
				}
			});
        }, function (errCode, errDesc, errStack) {
            // faild
        	$.endPageLoading();
        	$.MessageBox.error("错误提示", "操作失败！", "", null, errStack);
        });
    };
    
    o.queryContactMediumByContNumber = function(contNum) {
    	var self = this;
    	var param = "&ajaxListener=queryContactMedium";
    	if (contNum) {
    		param += "&CONTACT_PHONE="+contNum;
		}
    	
    	var notQuery = function() {
    		$.MessageBox.error("错误提示", "没有查询到该联系人信息！");	
    	};
    	
        ajaxPost(null, null, param, this.id+"MgrPart", function (json) {
            // success
        	var data = JSON.parse(json);
        	
        	if (!data[0]) {
        		notQuery();
        		return;
			}
        	
        	var dat = data[0] ? data[0] : {};
        	
        	self._getInput("CONTACT").val(dat["PARTY_NAME"]);;
        }, function (errCode, errDesc) {
            // faild
        	notQuery();
        	return;
        });
    };
    
    o.onchangeAcctType = function(el) {
    	var val = $(el).val();
		
		this._acctTypeShowModel(val);
    };
    
    o.setPopupTitle = function(title) {
    	if (title) {
    		$("#"+this.id+" .c_header .back").text(title);
		}
    }
    
    /**
     * 账户信息显示模式
     */
    o._acctTypeShowModel = function(type) {
    	// 现金
		if (type == "0") {
			$("#"+this.id+"_form1").css("display", "");
			$("#"+this.id+"_form2").css("display", "");
			$("#"+this.id+"_form3").css("display", "");
			$("#"+this.id+"_form4").css("display", "none");
			$("#"+this.id+"_form4 + .c_space").css("display", "none");
		}
		// 托收||定期代付||定额代付
		else if (type == "1" || type == "2" || type == "3") {
			$("#"+this.id+"_form1").css("display", "");
			$("#"+this.id+"_form2").css("display", "");
			$("#"+this.id+"_form3").css("display", "");
			$("#"+this.id+"_form4").css("display", "");
			$("#"+this.id+"_form4 + .c_space").css("display", "");
		}
		
		this._isShowAcctId();
		this._afterShowPopup();
    };
    
    o._isShowAcctId = function() {
		var title = $("#"+this.id).data("title");
        title = title ? title : "addAccount";
        // 新增
        if ("addAccount" == title) {
        	$("#"+this.id+"_form1").css("display", "none");
        	$("#"+this.id+"_form1 + .c_space").css("display", "none");
        	var group = this._getGroupInfo();
        	$("#"+this.id+"_ACCT_NAME").val(group.REAL_GROUP_NAME);
            return false;
        }
        // 修改
        else if ("modifyAccount" == title) {
        	$("#"+this.id+"_form1").css("display", "");
        	$("#"+this.id+"_form1 + .c_space").css("display", "");
            return true;
        }
	};
    
    o._addAccount = function() {
    	this.setPopupTitle("新增账户");
        var type = this._getInput("ACCT_TYPE").val();
        type = type ? type : "0";
        this._acctTypeShowModel(type);
    };
    o._modifyAccount = function(acctId, sucfn) {
    	this.setPopupTitle("修改账户");
        
        var self = this;
        var param = "&ACCT_ID="+acctId;
    	param += "&ajaxListener=accountDetial";
    	
    	$.beginPageLoading("查询中...");
        ajaxPost(null, null, param, this.id+"BankPart", function (json) {
            // success
        	$.endPageLoading();
            var data = JSON.parse(json);
            self.fillAcctPopup(data, true);
            self._acctTypeShowModel(data.ACCT_TYPE);
            self.cacheData = data;
            
            if (sucfn) sucfn.call(self);
        }, function (){
        	$.endPageLoading();
        });
    };
    
    o._afterShowPopup = function() {
    	// 滚动条刷新
    	var scroll = window[this.id + "Scroller"];
    	scroll.refresh();
    };
    
    o._getCustInfo = function() {
    	if(!$.os.phone){
        	var custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
    	}else{
    		var custInfo = $("#CUST_INFO").text();
    	}
    	return JSON.parse(custInfo.toString());
    };
    
    o._getGroupInfo = function() {
    	if(!$.os.phone){
    		var groupInfo = $.enterpriseLogin.getInfo().get("GROUP_INFO");
    	}else{
    		var groupInfo="10000";
    	}
    	return JSON.parse(groupInfo.toString());
    };
    
    o._getInput = function(name) {
    	return $("#"+this.id+"_"+name);
    };
    
    o._getInputli = function(name) {
    	return this._getInput(name).closest("li");
    };
    
    o._clearAcctPopup = function() {
    	this.fillAcctPopup({}, true);
    	// 默认值的设置
    	this._getInput("ACCT_TYPE").val("0");
    };
    
    o._setInputValue = function(name, value, overwrite) {
    	if (!this._getInput(name).val()) {
        	this._getInput(name).val(value ? value : "");
		} else {
			if (overwrite) {
				this._getInput(name).val(value ? value : "");
			}
		}
    };
    
    o._disableAll = function(flag) {
    	var setDisabled = function(el, flag) {
        	if (flag) {
    			el.attr("disabled", "disabled");
    		} else {
    			el.removeAttr("disabled");
    		}
        };
    	setDisabled($("#"+this.id+"_ACCT_NAME"), flag);
    	window[this.id+"_ACCT_TYPE"].setDisabled(flag);
    	setDisabled($("#"+this.id+"_CONTRACT_ID"), flag);
    	window[this.id+"_SUPER_BANK_CODE"].setDisabled(flag);
    	window[this.id+"_BANK_CODE"].setDisabled(flag);
    	setDisabled($("#"+this.id+"_BANK_ACCT_NO"), flag);
    	setDisabled($("#"+this.id+"_CONTACT_PHONE"), flag);
    	setDisabled($("#"+this.id+"_CONTACT"), flag);
    	setDisabled($("#"+this.id+"_POST_ADDRESS"), flag);
    	setDisabled($("#"+this.id+"_POST_CODE"), flag);
    	setDisabled($("#"+this.id+"_REMARKS"), flag);
    	
    	var __otherShow = function(self, flag) {
        	if (flag) {
        		$("#"+self.id+"qryContact").css("display", "none");
    		} else {
    			$("#"+self.id+"qryContact").css("display", "");
    		}
        };
    	__otherShow(this, flag);
    };
    
    o.fillAcctPopup = function(data, overwrite) {
    	this._setInputValue("ACCT_ID", data.ACCT_ID, overwrite);
    	this._setInputValue("OPEN_DATE", data.OPEN_DATE, overwrite);
    	this._setInputValue("REGION_ID", data.REGION_ID, overwrite);
    	this._setInputValue("REGION_NAME", data.REGION_NAME, overwrite);
    	this._setInputValue("CITY_CODE", data.CITY_CODE, overwrite);
    	this._setInputValue("ACCT_NAME", data.ACCT_NAME, overwrite);
    	this._setInputValue("ACCT_TYPE", data.ACCT_TYPE, overwrite);
    	this._setInputValue("CONTRACT_ID", data.CONTRACT_ID, overwrite);
    	this._setInputValue("SUPER_BANK_CODE", data.SUPER_BANK_CODE, overwrite);
    	this._setInputValue("BANK_CODE", data.BANK_CODE, overwrite);
    	this._setInputValue("BANK_NAME", data.BANK_NAME, overwrite);
    	this._setInputValue("BANK_ACCT_NO", data.BANK_ACCT_NO, overwrite);
    	this._setInputValue("CONTACT_PHONE", data.CONTACT_PHONE, overwrite);
    	this._setInputValue("CONTACT", data.CONTACT, overwrite);
    	this._setInputValue("REMARKS", data.REMARKS, overwrite);
    	this._setInputValue("POST_ADDRESS", data.POST_ADDRESS, overwrite);
    	this._setInputValue("POST_CODE", data.POST_CODE, overwrite);
    	this._setInputValue("CONT_MED_ID", data.CONT_MED_ID, overwrite);
    };
    
    o._assemberData = function() {
    	var data = {};
    	var acctId = this._getInput("ACCT_ID").val();
    	if(acctId && "" != acctId){
            data["ACCT_ID"] = acctId;
    	}
    	var openDate = this._getInput("OPEN_DATE").val();
    	if(openDate && "" != openDate){
            data["OPEN_DATE"] = openDate;
    	}

        data["REGION_ID"] = this._getInput("REGION_ID").val();
        data["REGION_NAME"] = this._getInput("REGION_NAME").val();
        data["CITY_CODE"] = this._getInput("CITY_CODE").val();
        data["ACCT_NAME"] = this._getInput("ACCT_NAME").val();
        data["RSRV_STR8"] = this._getInput("RSRV_STR8").val();
        data["RSRV_STR9"] = this._getInput("RSRV_STR9").val();
        var type = this._getInput("ACCT_TYPE").val();
        data["ACCT_TYPE"] = type;
        if (type == "1" || type == "5") {
        	data["CONTRACT_ID"] = this._getInput("CONTRACT_ID").val();
        	data["SUPER_BANK_CODE"] = this._getInput("SUPER_BANK_CODE").val();
        	data["BANK_CODE"] = this._getInput("BANK_CODE").val();
        	data["BANK_NAME"] = window[this.id +"_BANK_CODE"].selectedText;
        	data["BANK_ACCT_NO"] = this._getInput("BANK_ACCT_NO").val();	
        }
        return data;
    };
    
    isHide = function(id) {
		return $("#"+id).css("display") == "none";
	};
    
    $(function(){
    	var insts = c.instances;
    	for (var i = 0; i < insts.length; i++) {
			var self = insts[i];
			
			$("#"+self.id+"_SUPER_BANK_CODE").bind("change", function(){
	    		var param = "&SUPER_BANK_CODE=" + this.value;
	    		param += "&ajaxListener=queryBanksBySuperBank";
	    		
	    		$.ajax.submit(null, null, param, self.id+"BankPart", function (json) {
	                // success
	            	var result = JSON.parse(json);
	            	
	            }, function (errCode, errDesc) {
	                // faild
	            });
	    	});
		}
    });
    
})(EnterpriseAcctMgr);