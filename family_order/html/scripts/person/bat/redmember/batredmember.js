if(typeof(BatRedMember)=="undefined"){
	window["BatRedMember"]=function(){
		
	};
	var batredmember = new BatRedMember();
}

(function(){
	$.extend(BatRedMember.prototype,{
	
	diabledenddate: function ()
	{
		var s1=$("#OPER_TYPE").val();
		if (s1=="1") {
			$("#chooseTag").val("");
		    $("#END_DATE").val("");
		    $("#END_DATE").attr("disabled", true);
			$("#chooseTag").attr("disabled", true);
		} else if (s1=="0") {
		 	$("#END_DATE").attr("disabled", false);
			$("#chooseTag").attr("disabled", false);
			$("#END_DATE").val($("#END_DATE").attr("enddate"));
		}else{
			$("#chooseTag").val("");
		    $("#END_DATE").val("");
		    $("#END_DATE").attr("disabled", true);
			$("#chooseTag").attr("disabled", true);
		}
	},
	
	getEndDate: function(){
		var chooseFlag = $("#chooseTag")[0].checked;
		if(chooseFlag){
			$("#END_DATE").val($("#END_DATE").attr("startdate"));
		}else{
			$("#END_DATE").val($("#END_DATE").attr("enddate"));
		}
	}
	
	});
	}
)();
