(function($){
	$.selmember = {
	   init:function(){
		   $("#queryBtn").bind("click", (function(){
			   return function(){$.userCheck.checkUser('SERIAL_NUMBER');};
		   })());
		   $("#okBtn").bind("click",$.selmember.okFn);
		   $("#okBtn").attr("disabled",true); //禁用确定按钮
		   $("#cancelBtn").bind("click",$.selmember.cancelFn);
	   },
	   userCheckAction:function(data){
		   debugger;
		   var param = "&UCAInfoParam="+data;
		   var menuParam = $.popupDialogArguments();
		   param += $.selmember.buildParams(menuParam);
		   $.beginPageLoading('Loading.....');
		   ajaxPost(null,"setUCAViewInfos",param,"UCAViewPart,RoleProdPart",function(){
			   $.endPageLoading();
			   $("#okBtn").attr("disabled",false); //启用确定按钮
			   if($("#PACKAGE_ID").length > 0){
				   $("#PACKAGE_ID").bind("change",$.selmember.qryPackProdList);
			   }
		   },function(code, info, detail){
			   $.endPageLoading();
			   MessageBox.error("错误提示","获取页面信息出错！",null, null, info, detail);
		   });
	   },
	   qryPackProdList:function(){
		   debugger;
		   var packageId = $("#PACKAGE_ID").val();
		   var param = "&PACKAGE_ID="+packageId;
		   $.beginPageLoading('Loading.....');
		   ajaxPost(null,"getPackProdList",param,"ProdSelPart",function(){
			   $.endPageLoading();
		   },function(code, info, detail){
			   $.endPageLoading();
			   MessageBox.error("错误提示","获取页面信息出错！",null, null, info, detail);
		   });
	   },
	   okFn:function(){
		   var returnData = {};
		   var serialNumber = $("#SERIAL_NUMBER").val();
		   returnData["SERIAL_NUMBER"] = serialNumber;
		   var productId = $("#PRODUCT_ID").val();
		   if(productId){
			   returnData["NEW_PRODUCT_ID"] = productId;
		   }
		   $.closeDialog(returnData);
	   },
	   cancelFn:function(){
		   $.closeDialog(); 
	   },
	   buildParams:function(menuParam){
	       var params = "";
	       var paramsArray=[];
	       for(var p in menuParam){
	    		if(menuParam[p]&&$.isString(menuParam[p])){
					paramsArray.push("&");
					paramsArray.push(p);
					paramsArray.push("=");
					paramsArray.push(menuParam[p]);
				}
	       }
	       if(paramsArray.length>0){
	    		params=paramsArray.join("");
		   }
	       return params;
	   }
	};
	$($.selmember.init);
})(Wade);