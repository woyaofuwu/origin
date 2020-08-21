if(typeof(BatHintMessageChg)=="undefined"){
	window["BatHintMessageChg"]=function(){
		
	};
	var bathintmessagechg = new BatHintMessageChg();
}

(function(){
	$.extend(BatHintMessageChg.prototype,{

	changeState: function (){
		var state=$("#MODIFY_TAG").val();
		if(state == '1') {
			$("#END_DATE").attr("disabled", true);
			$("#ELEMENT_ID").attr("disabled", true);
			$("#RSRV_STR8").attr("disabled", true);
			$("#START_DATE").val($("#START_DATE").attr("startdate"));
			$("#END_DATE").val("");
			$("#ELEMENT_ID").val("");
			$("#RSRV_STR8").val("");
			$("#POP_ELEMENT_ID").val("");
			$("#POP_ELEMENT_ID").attr("nullable", "yes");
			$("#ename").attr("class", "");
		}else{
			$("#END_DATE").attr("disabled", false);
			$("#ELEMENT_ID").attr("disabled", false);
			$("#POP_ELEMENT_ID").attr("nullable", "no");
			$("#END_DATE").val($("#END_DATE").attr("enddate"));
			$("#ename").attr("class", "e_required");
			$("#RSRV_STR8").attr("disabled", false);
		}
		
	},
	
	queryElements: function (){
		var state=$("#MODIFY_TAG").val();
		if(state == '1') {
		
		}else{
			popupPage('bat.hintmessage.ElementPopupQry','queryElements','','选择优惠',700,430,'ELEMENT_ID');
		}
	},
	
	resolveReturnValues: function (){
		var value =$("#ELEMENT_ID").val();
		var returnValues = value.split('|');
		$("#ELEMENT_ID").val(returnValues[0]);
		$("#POP_ELEMENT_ID").val(returnValues[1]);
		$("#ELEMENT_TYPE").val(returnValues[2]);
	}

	});
	}
)();