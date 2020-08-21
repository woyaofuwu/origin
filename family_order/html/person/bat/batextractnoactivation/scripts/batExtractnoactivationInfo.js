$(function (){
	exportButtonDisable();
})

function reset(){
	$('#submit_part :input').val('');
}

function query(){
	//查询条件校验
	if(!$.validate.verifyAll("submit_part")) {//先校验已配置的校验属性
		return false;
	}
	//校验服务号码范围
	if(!checkSerialNumberRange('cond_START_SERIALNUMBER', 'cond_END_SERIALNUMBER', 5000)){
		return false;
	}
	
	$.beginPageLoading("正在查询数据...");
	
	$.ajax.submit('submit_part', 'queryExtractnoactivationInfo', null, 'result_Table', function(data){
			if($('#DeptTable tbody tr').length>0){
				//使用导出
				exportButtonEnable();
			}else{
				//禁用导出按钮
				exportButtonDisable();
			}
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
	/*if( (endSn - startSn) > parseInt(range)){
		alert( "服务号码【起始、终止】范围不能不能超过"+range+"~");
		return false;
	}*/
	return true;
}

function queryBadUserInfo4Import(obj){
	document.getElementById("cond_SERIAL_NUMBER").value="";
	setTimeout("queryDelay()",3000);
	
}