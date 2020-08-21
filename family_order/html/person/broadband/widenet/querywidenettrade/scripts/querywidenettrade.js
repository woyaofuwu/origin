(function($){
	$.extend({querywidenettrade:{
		init:function(){
		    $("#SUBSCRIBE_TYPE").bind("change", $.querywidenettrade.reloadStateList);
		    $("#queryBtn").bind("click", $.querywidenettrade.queryWidenetTrade);
	    },
	    reloadStateList:function(){
	    	$.beginPageLoading('Loading.....');
	    	var params = "&SUBSCRIBE_TYPE="+$("#SUBSCRIBE_TYPE").val();
	    	ajaxSubmit(null,"reloadStateList",params,"statelistPart",function(){
	    		$.endPageLoading();
	    	},function(code, info, detail){
	    		$.endPageLoading();
	    		MessageBox.error("错误提示","处理状态刷新失败！",null, null, info, detail);	
	    	});
	    },
	    queryWidenetTrade:function(){
	    	var tradetable = $.tableManager.get("WideNetTradeTable");
	 		var params = $.ajax.buildPostData("ConditionPart");
	 		tradetable.refresh(params);
	    }
	}});
	$($.querywidenettrade.init);
})(Wade);