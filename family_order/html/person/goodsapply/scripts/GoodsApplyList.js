
function completeEndSn(startObj, endId){
	var e = $('#'+endId);
	e.val(startObj.value);
	focusEnd(e[0]);
}

function focusEnd(e){ 
	if(e.createTextRange){
		var r =e.createTextRange(); 
		 r.moveStart('character',e.value.length); 
		 r.collapse(true); 
		 r.select(); 
	}else{
		e.focus();
	}
}

function synOnKeyup(startObj, endId){
	var e = $('#'+endId);
	e.val(startObj.value);
}

function queryGoodsList()
{
	if(!$.validate.verifyAll("CondPart")) {
		return false;
	}else{	 
		$.beginPageLoading("正在查询数据...");
		$.ajax.submit('CondPart', 'queryUserScoreGoodsList', "", 'ResultPart', function(){
			$.endPageLoading();
		},function(code, info, detail){
			$.endPageLoading();
			MessageBox.error("错误提示","查询错误！", null, null, info, detail);
		});
	}
}

function checkData (){
	var accMon=$("#cond_ACCEPT_MON").val();
	//当等式不成立时再做有效性校验
	if(!$.isNumeric(accMon)) {
		alert("结算月份应为1-12的数字。");
		$("#cond_ACCEPT_MON").val("");
		$("#cond_ACCEPT_MON").focus();
		return;
	}else{
		if(accMon>12 || accMon<1){
			alert("结算月份应为1-12的数字。");
			return;
		}
	}
}

function reset(){
	//$("#cond_ACCEPT_MON").val('');
	$("#cond_SUPPLIER").val('');
	$("#cond_COMPANY").val('');
	$("#cond_RULE_NAME").val(''); 
	$("#AGENT_DEPART_ID1").val('');
	$("#cond_START_DATE").val('');
	$("#cond_END_DATE").val('');
	$("#cond_START_STAFF_ID").val(''); 
	$("#cond_END_STAFF_ID").val('');
}

function exportBeforeAction(obj) {
	if(!$.validate.verifyAll("CondPart")) {
		return false;
	}
	return true;
}

function importDelayData(){
	$.beginPageLoading("努力导入中...");
	$.ajax.submit('SubmitCondPart','importDelayData','','BatResultPart',function(data){
		alert('导入成功！');
		$.endPageLoading();
		$("#BatResultPart").css("display",""); 
		$("#hideBtn").css("display",""); 
		$("#showBtn").css("display","none");  
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}
function showTheLists(){
	$("#hideBtn").css("display",""); 
	$("#BatResultPart").css("display",""); 
	$("#showBtn").css("display","none");  
}
function hideTheLists(){
	$("#BatResultPart").css("display","none"); 
	$("#showBtn").css("display",""); 
	$("#hideBtn").css("display","none"); 
}
