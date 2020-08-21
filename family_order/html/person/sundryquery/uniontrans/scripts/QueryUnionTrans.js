/**
 * 自动完成终止服务号码
 */
function completeEndSn(startObj, endId){
	//var e = getElement(endId);
	var e = document.getElementById(endId);
	e.value = startObj.value;
	focusEnd(e);
}

/**
 * 光标停在最后
 */
function focusEnd(e){ 
	 //var e = event.srcElement; 
	 var r =e.createTextRange(); 
	 r.moveStart('character',e.value.length); 
	 r.collapse(true); 
	 r.select(); 
}

/**
 * 每输入一个开始号码，自动填充终止号码
 */
function synOnKeyup(startObj, endId){
	//var e = getElement(endId);
	var e = document.getElementById(endId);
	e.value = startObj.value;
}


/****
 * 校验起始手机号码范围
 */
function checkSerialNumberRange(startId, endId, range){
	var startSn = parseInt(document.getElementById(startId).value);
	var endSn = parseInt(document.getElementById(endId).value);
	if( (endSn - startSn) < 0){
		alert( "【终止服务号码】不能小于【起始服务号码】~");
		return false;
	}
	if( (endSn - startSn) > parseInt(range)){
		alert( "服务号码【起始、终止】范围不能不能超过"+range+"~");
		return false;
	}
	return true;
}


/***
 * 提交查询前校验
 */
 function queryUnionTrans(obj){
	//联通转接查询提交前进行校验
	if (!verifyAll(obj)){
		return false; //先校验已配置的校验属性
	}
	//校验服务号码范围
	if(!checkSerialNumberRange('cond_START_SERIALNUMBER', 'cond_END_SERIALNUMBER', 1000)){
		return false;
	}
	$.ajax.submit('QueryCondPart', 'queryUnionTrans', null, 'QueryListPart', function(data){
		if(data.get('ALERT_INFO') != '')
		{
			alert(data.get('ALERT_INFO'));
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
 }
 
