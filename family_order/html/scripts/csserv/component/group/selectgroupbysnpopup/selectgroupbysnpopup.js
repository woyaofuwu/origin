function getGrpInfoBySn()
{	  
	
  var group = $('#cond_GROUP_SERIAL_NUMBER').val();
  var limitType = $('#SELECTGROUP_BYSNPOPUP_LIMITTYPE').val();
  var limitProducts = $('#SELECTGROUP_BYSNPOPUP_LIMITPRODUCTS').val();
	if (group == ""){
		return false;
	}else{	
		$.beginPageLoading();
		Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.selectgroupbysnpopup.SelectGroupBySNPopupHttpHandler','getGrpUCAInfoBySn','&cond_GROUP_SERIAL_NUMBER='+group+'&cond_LIMIT_TYPE='+limitType+'&cond_LIMIT_PRODUCTS='+limitProducts,
    	function(data){
    		$.endPageLoading();
    		var resultcode = data.get('X_RESULTCODE','0');
	    	if(resultcode!='0'){
	    	
    			var aftererrorAction = $("#SN_AFTER_ERR_ACTION").val();
			    if (aftererrorAction != '') {
			        eval(""+aftererrorAction);
			    }
    			$.showWarnMessage(data.get('X_RESULTTITLE',''),data.get('X_RESULTINFO',''));
    			
	    	}else{
	    	
		    	var afterAction = $("#SN_AFTER_ACTION").val();
			    if (afterAction != '') {
			        eval(""+afterAction);
			    }
	    	
	    	}
	    	
    	},
		function(error_code,error_info,derror){
		
			$.endPageLoading();
			var aftererrorAction = $("#SN_AFTER_ERR_ACTION").val();
		    if (aftererrorAction != '') {
		        eval(""+aftererrorAction);
		    }
		    
			showDetailErrorInfo(error_code,error_info,derror);
			
	    });
	}
}
