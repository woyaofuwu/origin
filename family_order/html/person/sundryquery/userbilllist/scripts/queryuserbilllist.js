function query()
{
	$.ajax.submit('QueryPart', 'queryUserBillHisInfo', null, 'ResultDataPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

/**
 * 自动完成终止服务号码
 */
function completeEndSn(startObj, endId){
	var e = $('#'+endId);
	e.val(startObj.value);
	focusEnd(e[0]);
}

/**
 * 每输入一个开始号码，自动填充终止号码
 */
function synOnKeyup(startObj, endId){
	var e = $('#'+endId);
	e.val(startObj.value);
}

/**
 * 光标停在最后
 */
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