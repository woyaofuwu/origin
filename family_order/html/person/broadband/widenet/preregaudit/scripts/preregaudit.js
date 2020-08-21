(function($){
	$.extend($.BmFrame,{
		initPreRegAudit:function(){
		    $("#queryBtn").bind("click", $.BmFrame.queryBookData);
        },
        queryBookData:function(){
 		   debugger;
 		   if(!$.validate.verifyAll()){return false;}
 		   var booktable = $.tableManager.get("WideNetBookTable");
 		   var params = $.ajax.buildPostData("ConditionPart");
 		   booktable.refresh(params);
 	    },
 	    auditPreReg:function(rowIndex){
	    	var booktable = $.tableManager.get("WideNetBookTable");
	    	debugger;
	    },
		gBmFrame_getAllSubmitPageData:function(){
			var objSubmitData = new $.BmFrame.SubmitData();
	    	var objNodeInfo = objSubmitData.addNewNodeInfo("","");
	    	var jsonStr = $.BmFrame.buildJsonData();
	    	if(!jsonStr || jsonStr.length <= 0){
	    		alert("您没有做任何修改，请修改后提交!");
	    		return false;
	    	}
	    	objNodeInfo.addJSONArrayStr("WIDENET_BOOK_AUDIT_DATA", jsonStr);
	    	return objSubmitData;
	    },
	    gBmFrame_SuccessFunc:function(data){
	    	$.endPageLoading();
	    	MessageBox.success("成功提示","业务受理成功!",function(btn){
	    		debugger;
	    		$.BmFrame.queryBookData();
	    	});
	    },
	    buildJsonData:function(){
	    	var booktable = $.tableManager.get("WideNetBookTable");
		    return booktable.toJsonArray(true,"INST_ID,AUDIT_STATUS");
	    }
	});
	$($.BmFrame.initPreRegAudit);  //页面加载完成之后才执行
})(Wade);
