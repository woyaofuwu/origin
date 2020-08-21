(function($){
	$.extend({usercomplaints:{
		init:function(){
		    $("#queryBtn").bind("click", $.usercomplaints.queryUserComplaints);
	    },
	    queryUserComplaints:function(){
	    	if(!$.validate.verifyAll()){return false;}
	    	var tradetable = $.tableManager.get("UserComplaintsTable");
	 		var params = $.ajax.buildPostData("ConditionPart");
	 		tradetable.refresh(params);
	 		if(tradetable.count() <=1){
	  		     alert("查询宽带投诉信息无数据!");
	  		}
	    }
	}});
	$($.usercomplaints.init);
})(Wade);