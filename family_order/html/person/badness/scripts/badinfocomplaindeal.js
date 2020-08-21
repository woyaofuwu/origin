function changeReportType(){
	var mm =$("#cond_REPORT_TYPE_CODE").val();
	var requiredStr = $("#cond_RECV_CONTENT").attr("desc");
	
	if(mm=='03'){
		removeContentBind();
	}else if(requiredStr==null||requiredStr==''){
		addContentBind();
	}
	
	$.beginPageLoading("载入中..");
     $.ajax.submit('DadInfoPart', 'getFourthTypeCodes', null, 'FourthTypeCodePart,FifthTypeCodePart,ServRequestTypePart,SeventhTypeCodePart',function(data){
		$.endPageLoading();
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}

function changeFourthType(){	
	$.beginPageLoading("载入中..");
     $.ajax.submit('DadInfoPart', 'getFifthTypeCodes', null, 'FifthTypeCodePart,ServRequestTypePart,SeventhTypeCodePart',function(data){
		$.endPageLoading();
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}

function changeFifthType(){	
	$.beginPageLoading("载入中..");
     $.ajax.submit('DadInfoPart', 'getServRequestType', null, 'ServRequestTypePart,SeventhTypeCodePart',function(data){
		$.endPageLoading();
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}

function changeRequestType(){	
	$.beginPageLoading("载入中..");
     $.ajax.submit('DadInfoPart', 'getSevenTypeCodes', null, 'SeventhTypeCodePart',function(data){
		$.endPageLoading();
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}

/*
 * 去掉RECV_CONTENT必填校验与样式
 */
function removeContentBind(){
	$("#cond_RECV_CONTENT").removeAttr('desc');
	$("#cond_RECV_CONTENT").attr("nullable", "yes");
	$("#SPAN_RECV_CONTENT").attr("class",""); 
	 
}
/*
 * 添加RECV_CONTENT必填校验与样式
 */
function addContentBind(){
	$("#cond_RECV_CONTENT").attr("desc","举报内容");
	$("#SPAN_RECV_CONTENT").attr("class","e_required");
	$("#cond_RECV_CONTENT").attr("nullable", "no");
}

function doDadInfoActive() {
	if(!verifyAll('DadInfoPart'))
   	{
	   return false;
   	}
	var badType =$("#cond_BAD_TYPE").val();
	var badnessInfo =$("#cond_BADNESS_INFO").val();
	if(badType=="01"&&badnessInfo.length<=8){
		alert("被举报固定电话请填写完整区号！");
		return false;
	}
	var reportTypeCode =$("#cond_REPORT_TYPE_CODE").val();
   	if(reportTypeCode == '04') {
   		var bdnessInfo =$("#cond_BADNESS_INFO").val();
   		if(bdnessInfo != '9') {
   			alert('服务请求分类为不良网站时，被举报号码必须为9');
   			return false;
   		}
   	}
	$.beginPageLoading("业务受理中..");
     $.ajax.submit('DadInfoPart', 'dadInfoActive', null, 'DadInfoPart',function(data){
		$.endPageLoading();
		$.showSucMessage("业务受理成功", "业务受理成功！服务请求标识：" + data.get(0).get("INFO_RECV_ID"), this);
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}
function queryState()
{
	//查询条件校验
	if(!$.validate.verifyAll("QueryRecordPart"))
	{
		return false;
	}
	$.beginPageLoading();
	$.ajax.submit('QueryRecordPart', 'queryInfo', null, 'QueryListPart', function(data){
		if(data.get("ALERT_INFO")!=null) {
			MessageBox.alert("状态","没有查询到任何数据。");
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });	
}