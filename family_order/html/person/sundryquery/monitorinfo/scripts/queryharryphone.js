$(function (){
	//禁用导出按钮
	exportButtonDisable();
})
//查询方式chang事件
function initQueryHarryPhone() {
	var query_type = $('#QUERY_TYPE').val();
	//1－普通查询；2－日报表查询
	if(query_type == '1') {
		$('#begin_sn_li').show();
		$('#end_sn_li').show();
		$('#query_date_li').hide();
		$('#violation_reason_li').hide();
		$('#deal_result_li').hide();
		
		$('#QUERY_DATE').val('');
		$('#VIOLATION_REASON').val('');
		$('#DEAL_RESULT').val('');
	} else if(query_type == '2'){
		$('#begin_sn_li').hide();
		$('#end_sn_li').hide();
		$('#BEGIN_SN').val('');
		$('#END_SN').val('');
		
		$('#query_date_li').show();
		$('#violation_reason_li').show();
		$('#deal_result_li').show();
	}
}
/*查询数据是否有，有导出按钮就能点，没有则导出按钮置灰*/


function reset(){
	$('#BEGIN_SN').val('');
	$('#END_SN').val('');
	$('#QUERY_DATE').val('');
	$('#VIOLATION_REASON').val('');
	$('#DEAL_RESULT').val('');
	initQueryHarryPhone();
}

function query(){
	//查询条件校验
	if(!checkInput()) {
		return false;
	}
	if(!SNRangCheck()){
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	
	$.ajax.submit('submit_part', 'queryHarryPhones', null, 'result_Table', function(data){
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
		eFlag = checkElement("BEGIN_SN")&& checkElement("END_SN");
	}else if(query_type == '2'){
		eFlag = checkElement("QUERY_DATE");
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

//校验不成功返回false
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
	}
	return true;
}

