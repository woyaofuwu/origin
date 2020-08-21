$(function (){
	//禁用导出按钮
	exportButtonDisable();
})
//查询方式chang事件
function initQueryRedMember() {
	var query_type = $('#QUERY_TYPE').val();
	//1－服务号码；2－时间段
	if(query_type == '1') {
		$('#serial_number_li').show();
		$('#start_time_li').hide();
		$('#end_time_li').hide();
		$('#START_TIME').val('');
		$('#END_TIME').val('');
	} else if(query_type == '2'){
		$('#serial_number_li').hide();
		$('#start_time_li').show();
		$('#end_time_li').show();
		$('#INPUT_SERIAL_NUMBER').val('');
	}
}
/*查询数据是否有，有导出按钮就能点，没有则导出按钮置灰*/


function reset(){
	$(':input').val('');
	initQueryRedMember();
}

function query(){
	//查询条件校验
	if(!checkInput()) {
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	
	$.ajax.submit('submit_part', 'queryRedMemberInfos', null, 'result_Table', function(data){
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

//校验不成功返回false
function checkInput(){
	var query_type = $('#QUERY_TYPE').val();
	var eFlag = false;
	if(query_type == '1') {
		eFlag = checkElement("INPUT_SERIAL_NUMBER");
	}else if(query_type == '2'){
		eFlag = checkElement("END_TIME")&&checkElement("START_TIME")&&checkDateRange('START_TIME','END_TIME','31');
	}
	return eFlag
}

//校验不成功返回false
function checkElement(id){
	var jqobj = $("#"+id);
	var eflag =  jqobj.val()==""
	if(eflag){
		alert(jqobj.attr('desc')+'不能为空');
		return !eflag;
	}
	eflag = jqobj.attr('maxlength')!=null&&jqobj.attr('maxlength')!=-1&&jqobj.val().length>jqobj.attr('maxlength')
	if(eflag){
		alert(jqobj.attr('desc')+'长度不能大于'+jqobj.attr('maxlength'));
	}
	return !eflag;
}

