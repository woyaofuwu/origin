var EnterpriseLogin = function(id) {
	this.id = id;
	this.pageSize = 7;
	this.offerPageSize = 20;
	this.loginData;
	
	Wade.extend({
		enterpriseLogin:this
	});
};
(function(c){
	var o = c.prototype;
	o.getInfo = function() {
		var custInfo = $.DataMap();
		var groupInfo = $.DataMap();
		var enterpriseInfo = $.DataMap();
		var custID=$("#cond_CUST_ID").val();
		custInfo.put("CUST_ID",custID);
		
		var groupId=$("#login_groupID").val();
		groupInfo.put("CUST_ID",groupId); 
		
		enterpriseInfo.put("GROUP_INFO",groupInfo);
		enterpriseInfo.put("CUST_INFO",custInfo);
    	return enterpriseInfo;
    };
    o.isLogin = function() {
    	return true;
    };
    o._getInfoText = function() {
    	return $("#"+this.id+"Info").text();
    };
    o.refreshGroupInfo = function(groupId) {
    	if (!groupId || groupId=="null") {
    		return;
    	}
    };
    
})(EnterpriseLogin);

$.enterpriseLogin = (function() {
	var login="";
	var scope = window;
	if(undefined==scope.parent.$.enterpriseLogin){
		login=new EnterpriseLogin();
	}
	return login;
})();

//if(typeof(EnterpriseLoginItem) == "undefined")
//{
//	window["EnterpriseLoginItem"] = function(id){
//		this.id = id;
//	};
//};


//(function(){
//	$.extend(EnterpriseLogin.prototype, {
//		queryCustInfoTypes:function (){
//	    	$.beginPageLoading("数据加载中......");
//	    	var groupSearchValue= $("#groupSearchBox").val();
//	    	var custOperType= $("#custOperType").val();
//	    	var param="";
//	    	 param += "&ajaxListener=ajaxQuery";
//	         param += "&CODE="+custOperType;
//	         param += "&VALUE="+groupSearchValue;
//	         param += "&pagin=grpListPage";
//	    	 ajaxPost(null, null, param, "custInfoPart", function (json) {
//	    		 
//	    		 
//	    	 },function (errCode, errDesc, errStack) {
//	             // faild
//	         	$.endPageLoading();
//	         	$.MessageBox.error("错误提示", "获取集团信息失败！", "", null, errStack);	
//	         });
//	     };
//	    	showPopup('popup','EnterpriseLoginItem', true);
//	    },
//		
//	});
//})();


 function queryCustInfoTypes (){
	$.beginPageLoading("数据加载中......");
	var groupSearchValue= $("#groupSearchBox").val();
	var custOperType= $("#custOperType").val();
	var param="";
	 param += "&ajaxListener=ajaxQuery";
     param += "&CODE="+custOperType;
     param += "&VALUE="+groupSearchValue;
     param += "&NOLogin_FLAG=true";
     param += "&pagin=grpListPage";
     ajaxPost(null, null, param, "custInfoPart",null,function (errCode, errDesc, errStack) {
         // faild
     	$.endPageLoading();
     	$.MessageBox.error("错误提示", "获取集团信息失败！", "", null, errStack);	
     });
     $.endPageLoading();
 };
 
 function selectCustInfo (obj){
	 var cust_ID= $(obj).attr("CUST_ID");
	 var groupID= $(obj).attr("GROUP_ID");
	 $("#cond_CUST_ID").val(cust_ID);
	 $("#login_groupID").val(groupID);
	 hidePopup(obj);

	 if($.isFunction(queryGroupCustInfo))
	 {
        queryGroupCustInfo();
     }

 }