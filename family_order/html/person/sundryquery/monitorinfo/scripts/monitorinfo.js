$(function (){
	exportButtonDisable();
})

//监控信息查询页面初始化
function initQueryMonitorInfo() {
	var query_type = $('#QUERY_TYPE').val();
	
	//1－普通查询；2－日报表查询；3-时段表查询
	if(query_type == '1') {
		$('#begin_sn_li').show();
		$('#end_sn_li').show();
		
		$('#query_date_li').hide();
		$('#violation_reason_li').hide();
		$('#deal_result_li').hide();
		$('#start_date_li').hide();
		$('#end_date_li').hide();
	} else if(query_type == '2'){
		$('#query_date_li').show();
		$('#violation_reason_li').show();
		$('#deal_result_li').show();
		
		$('#begin_sn_li').hide();
		$('#end_sn_li').hide();
		$('#start_date_li').hide();
		$('#end_date_li').hide();
	} else if(query_type == '3'){
		$('#start_date_li').show();
		$('#end_date_li').show();
		$('#violation_reason_li').show();
		$('#deal_result_li').show();
		
		$('#begin_sn_li').hide();
		$('#end_sn_li').hide();
		$('#query_date_li').hide();
		
	}
}
/*查询数据是否有，有导出按钮就能点，没有则导出按钮置灰*/


function reset(){
	$(':input').val('');
	initQueryMonitorInfo();
}

function query(){
	var query_type = $('#QUERY_TYPE').val();
	if( !SNRangCheck()){
		return false;
	}
	
	//查询条件校验
	/*
	if(!$.validate.verifyAll("submit_part")) {//先校验已配置的校验属性
		return false;
	}
	*/
	//查询条件校验
	if(!checkInput()) {
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	
	$.ajax.submit('submit_part', 'queryMonitorInfos', null, 'result_Table', function(data){
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
		eFlag = checkElement("BEGIN_SN")&&checkElement("END_SN")
	}else if(query_type == '2'){
		eFlag = checkElement("QUERY_DATE");
	}else if(query_type == '3'){
		eFlag = checkElement("START_DATE")&&checkElement("END_DATE");
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

function SNRangCheck() {
	var query_type =  $("#QUERY_TYPE").val();
	var para_code1 =  $("#BEGIN_SN").val();//起始号码
	var para_code2 =  $("#END_SN").val();//终止号码
	if(query_type == '1') {
		if(parseInt(para_code1) > parseInt(para_code2)) {
			alert('开始号码不能大于结束号码');
			return false;
		}
		if(parseInt(para_code1) + 1000 < parseInt(para_code2)) {
			alert('服务号码【开始、结束】范围不能超过1000');
			return false;
		}
	}else if(query_type == '3'){
		return  checkDateRange('START_DATE','END_DATE','30');
	}
	return true;
}


