/**
 * 初始化页面参数
 * */
function initBatchId(){
	$.beginPageLoading("正在进行中...");
	$.ajax.submit("OcsUserImport","getBatchId",'','NewBatchId',function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

/**
 * 初始化页面参数
 * */
function resetPage(){
	$.beginPageLoading("努力刷新中...");
	$.ajax.submit('','','','SubmitCondPart',function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}


function initQianYue(){
	var checkRadio = $("input[name='cond_RADIO_CODE'][type='radio']:checked");
	var checkRadioValue = checkRadio.val();
	if(checkRadioValue=='1'){
	    $("#JKBZ").attr("disabled", true);
	    $("#JKGZ").attr("disabled", true);
	    $("#JKBZ").css("display", "none");
	    $("#JKGZ").css("display", "none");
	    $("#cond_JKGZ_CODE").val('');
	}else{
	    $("#JKBZ").attr("disabled", false);
	    $("#JKGZ").attr("disabled", false);
	    $("#JKBZ").css("display", "");
	    $("#JKGZ").css("display", "");
	}
}


function importOcsData(){
	if($("#BATCH_ID").val()==""){
	    alert("请先获取批次号");
	    return false;
	}
	if($("#cond_STICK_LIST").val()==""){
		alert('上传文件不能为空！');
		return false;
	}
	
	if($("#cond_SXQX_CODE").val()==""){
		alert('生效期限不能为空！');
		return false;
	}
	
	
	var checkRadio = $("input[name='cond_RADIO_CODE'][type='radio']:checked");
	var checkRadioValue = checkRadio.val();
	var paramCheck = radioParamCheck(checkRadioValue);
	if(!paramCheck){
		return false;
	}
	
	$.beginPageLoading("努力导入中...");
	$.ajax.submit('SubmitCondPart','importOcsData','','',function(data){
		alert('导入成功！');
		resetPage();
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}


function radioParamCheck(checkRadioValue)
{
	//if(checkRadioValue=='1')
	//{
	//}
	//var voiceBusi = $("input[name='voiceBusi'][type='checkbox']:checked").val();
	//var smsBusi = $("input[name='smsBusi'][type='checkbox']:checked").val();
	var gprsBusi = $("input[name='gprsBusi'][type='checkbox']:checked").val();
	//var wapBusi = $("input[name='wapBusi'][type='checkbox']:checked").val();
	if(gprsBusi==undefined || gprsBusi=='')
	{
		alert('至少选择一个OCS业务类型！');
		return false;
	}
	if(checkRadioValue=='0')
	{
		if($("#cond_JKBZ_CODE").val()==""){
			alert('监控标志不能为空！');
			return false;
		}
	}
	return true;
}

function changeMonitorFlag (value)
{
	if(value==2)
	{
		$("#JKGZ").attr("disabled", false);
		$("#JKGZ").css("display", "");
	}
	else
	{
		$("#JKGZ").attr("disabled", true);
		$("#JKGZ").css("display", "none");
		$("#cond_JKGZ_CODE").val('');
		
	}
}
