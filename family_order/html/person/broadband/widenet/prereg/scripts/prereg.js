(function($){
	$.extend($.BmFrame,{
		gBmFrame_getAllSubmitPageData:function(){
		    if(!$.validate.verifyAll()){return false;}
			var objSubmitData = new $.BmFrame.SubmitData();
	    	var objNodeInfo = objSubmitData.addNewNodeInfo("","");
	    	var jsonStr = $.BmFrame.buildJsonData("PreRegPart");
	    	objNodeInfo.addJSONObjectStr("WIDENET_BOOK_DATA", jsonStr);
	    	return objSubmitData;
	    },
	    buildJsonData:function(areaId,prefix){
		    var data=$.ajax.buildJsonData(areaId,prefix);
		    return new $.DataMap(data);
	    }
	});
})(Wade);
