function getGrpInfoBySn()
{	  
	var sn = trim($('#cond_GROUP_SERIAL_NUMBER').val());
	
	if (sn == ""){
		$.showWarnMessage('请输入正确的集团服务号码！','集团服务号码信息不能为空');
		return false;
	}
	var lister = $("#SELECTGROUPBYSN_LISTENER_NAME").val();
	
	var params = '&cond_GROUP_SERIAL_NUMBER='+sn+'&BUSI_TYPE='+$("#SELECTGROUPBYSN_BUSI_TYPE").val();
	var limitType = $('#SELECTGROUPBYSN_LIMITTYPE').val();
  	var limitProducts = $('#SELECTGROUPBYSN_LIMITPRODUCTS').val();
  	if(limitType != null && limitType != ''){
		params += '&cond_LIMIT_TYPE='+limitType;
	}
	if(limitProducts != null && limitProducts != ''){
		params += '&cond_LIMIT_PRODUCTS='+limitProducts;
	}
		
	if(lister == null || lister ==''){
		getGrpInfoBySnHttpHandler(params);
	}else{
		getGrpInfoBySnListener(params);
	}
}

function getGrpInfoBySnListener(params){
	$.beginPageLoading();
	var refreshPart = $("#SELECTGROUPBYSN_REFRESH_PART").val();
	var lister = $("#SELECTGROUPBYSN_LISTENER_NAME").val();
	$.ajax.submit(this,lister,params,refreshPart,
	function(data){
   		$.endPageLoading();
   		var afterAction = $("#SELECTGROUPBYSN_AFTER_ACTION").val();
	    if (afterAction != '') {
	        eval(""+afterAction);
	    }else{
	    	var resultcode = data.get('X_RESULTCODE','0');
    		if(resultcode!='0'){
	    	
    			$.showWarnMessage(data.get('X_RESULTTITLE',''),data.get('X_RESULTINFO',''));
    		}
   		}
	},
	function(error_code,error_info,derror){
		var afterAction = $("#SELECTGROUPBYSN_AFTER_ERR_ACTION").val();
	    if (afterAction != '') {
	        eval(""+afterAction);
	    }
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function getGrpInfoBySnHttpHandler(params){
	$.beginPageLoading();
	var cazzname = $("#SELECTGROUPBYSN_CLAZZ_NAME").val();
	if(cazzname == null || cazzname ==''){
		cazzname="com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.selectgroupbysn.SelectGroupBySNHttpHandler";
	}
	var methodname = $("#SELECTGROUPBYSN_METHOD_NAME").val();
	if(methodname == null || methodname ==''){
		methodname ="getGrpUCAInfoBySn";
	}
	Wade.httphandler.submit('',cazzname,methodname,params,
	function(data){
   		$.endPageLoading();
   		var afterAction = $("#SELECTGROUPBYSN_AFTER_ACTION").val();
	    if (afterAction != '') {
	        eval(""+afterAction);
	    }else{
	    	var resultcode = data.get('X_RESULTCODE','0');
    		if(resultcode!='0'){
	    	
    			$.showWarnMessage(data.get('X_RESULTTITLE',''),data.get('X_RESULTINFO',''));
    		}
   		}
	},
	function(error_code,error_info,derror){
		var afterAction = $("#SELECTGROUPBYSN_AFTER_ERR_ACTION").val();
	    if (afterAction != '') {
	        eval(""+afterAction);
	    }
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}