
function ruleCheck(claszz,method,params) {
		var result = true;
    	Wade.httphandler.submit('',claszz,method,params,
    	function(data){
    		var ruleTipDataset = data.get('TIPS_TYPE_TIP');
    		if(ruleTipDataset && ruleTipDataset.length>0){
	    		ruleTipDataset.each(function(item,index,totalcount){
					var tipsInfo = item.get("TIPS_INFO");
					window.alert(tipsInfo);
					result = false;
					return result;
				});
			}
			
	    	var ruleChoiceDataset = data.get('TIPS_TYPE_CHOICE');
	    	if(ruleChoiceDataset && ruleChoiceDataset.length>0){
	    		ruleChoiceDataset.each(function(item,index,totalcount){
					var tipsInfo = item.get("TIPS_INFO");
					if(!window.confirm(tipsInfo)){
						result = false;
						return result;
					}
				});
			}
		},
		function(error_code,error_info,derror){
		
			showDetailErrorInfo(error_code,error_info,derror);
			result = false;
			return false;
	    }, {async:false});
	    return result;

}

function pageFlowRuleCheck(claszz,method,params) {
		var result = true;
    	Wade.httphandler.submit('',claszz,method,params,
    	function(data){
    	
    		var ruleTipDataset = data.get('TIPS_TYPE_TIP');
    		if(ruleTipDataset && ruleTipDataset.length>0){
	    		ruleTipDataset.each(function(item,index,totalcount){
					var tipsInfo = item.get("TIPS_INFO");
					window.alert(tipsInfo);
				});
			}
			
	    	var ruleChoiceDataset = data.get('TIPS_TYPE_CHOICE');
	    	if(ruleChoiceDataset && ruleChoiceDataset.length>0){
	    		ruleChoiceDataset.each(function(item,index,totalcount){
					var tipsInfo = item.get("TIPS_INFO");
					if(!window.confirm(tipsInfo)){
						$.endPageLoading();
						result = false;
						return false;
					}
				});
			}
			//如果TIPS_TYPE_CHOICE 中选择的是取消操作,则终止
			if(!result) return true;
			
			//增加规则验证成功后调用的方法
			var afterCheckResult =true;
			if(typeof(pageFlowCheckAfterAction)=="function"){
				afterCheckResult = pageFlowCheckAfterAction();
			}
			if(!afterCheckResult){
				$.endPageLoading();
				return false;
			}
			$.endPageLoading();
			parent.getFlow().next(null,false,true);
			
			
		},
		function(error_code,error_info,derror){
		
			showDetailErrorInfo(error_code,error_info,derror);
			$.endPageLoading();
			return false;
	    });
	    return true;

}

function grpBatruleCheck(claszz,method,params) {
		var result = true;
    	Wade.httphandler.submit('',claszz,method,params,
    	function(data){
    		var ruleTipDataset = data.get('TIPS_TYPE_TIP');
    		if(ruleTipDataset && ruleTipDataset.length>0){
	    		ruleTipDataset.each(function(item,index,totalcount){
					var tipsInfo = item.get("TIPS_INFO");
					window.alert(tipsInfo);
					result = false;
					return result;
				});
			}
			
	    	var ruleChoiceDataset = data.get('TIPS_TYPE_CHOICE');
	    	if(ruleChoiceDataset && ruleChoiceDataset.length>0){
	    		ruleChoiceDataset.each(function(item,index,totalcount){
					var tipsInfo = item.get("TIPS_INFO");
					if(!window.confirm(tipsInfo)){
						result = false;
						return result;
					}
				});
			}
		},
		function(error_code,error_info,derror){
			showDetailErrorInfo(error_code,error_info,derror);
			result = false;
			return false;
	    }, {async:false});
	    return result;

}
