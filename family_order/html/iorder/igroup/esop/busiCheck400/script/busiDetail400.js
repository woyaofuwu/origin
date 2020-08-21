var updateArea ="";//标记1为修改数据，否正为新增数据
var checkId ="";//修改是根据序列做修改
var flags ="";//标记1为检查2为考核
var remarks = "";//取表格里面的备注，修改时做比较
var checkResuits ="";//取表格里面的检查结果，修改时做比较
$(function(){
	debugger;
	var newValue = $("#ATTR_NEW_VALUE").val();
	if("2"!=newValue){
		 $("#CheckPart").css("display","none");
		 $("#AuditPart").css("display","none");
		 $("#OpenSubmit").css("display","none");
		 
	}
	
});

function showAddArea(flag){
	flags = flag;
	if("AUDIT"==flag){
		 $("#CheckPart").css("display","none");
		 $("#AuditPart").css("display","");
		 $("#OpenSubmit").css("display","");
		 updateArea="2";
	}else if("CHECK"==flag){
		 $("#CheckPart").css("display","");
		 $("#AuditPart").css("display","none");
		 $("#OpenSubmit").css("display","");
		 updateArea="2";
	}
	
}

function showUpdateArea(obj){
	var flag = $(obj).attr("FLAG");
	remarks = $(obj).attr("REMARK");
	checkResuits = $(obj).attr("CHECK_RESULT");
	flags = flag;
	if("AUDIT"==flag){
		 $("#CheckPart").css("display","none");
		 $("#AuditPart").css("display","");
		 $("#OpenSubmit").css("display","");
		 $("#CHECK_RESULT").val($(obj).attr("CHECK_RESULT"));
		 $("#REMARK").val($(obj).attr("REMARK"));
		 updateArea="1";
		 checkId = $(obj).attr("CHECK_ID");
	}else if("CHECK"==flag){
		 $("#CheckPart").css("display","");
		 $("#AuditPart").css("display","none");
		 $("#OpenSubmit").css("display","");
		 $("#CHECK_RESULT1").val($(obj).attr("CHECK_RESULT"));
		 $("#REMARK1").val($(obj).attr("REMARK"));
		 updateArea="1";
		 checkId = $(obj).attr("CHECK_ID");
	}
}

function submitState(){
	var ibsysId = $("#IBSYSID").val();
	if("1"==updateArea){//修改考核或检查记录
		if("AUDIT"==flags){
			var	checkResuit = $("#CHECK_RESULT").val();
			if(checkResuit==null||checkResuit==""){
				$.validate.alerter.one($("#CHECK_RESULT")[0],"您没有输入考核结果，请输入!");
				return false;
			}
			var remark = $("#REMARK").val();
			if(remark==null||remark==""){
				$.validate.alerter.one($("#REMARK")[0],"您没有备注，请输入!");
				return false;
			}
			if(checkResuit==checkResuits&&remark==remarks){
				$.validate.alerter.one($("#CHECK_RESULT")[0],"您没有对考核结果或备注信息做修改，不能提交，请修改后提交!");
				return false;
			}
			$.beginPageLoading("数据查询中......");
			$.ajax.submit("","updateCheckRecordInfo", "&FLAG="+flags+"&IBSYSID="+ibsysId+"&CHECK_RESULT="+checkResuit+"&REMARK="+remark+"&CHECK_ID="+checkId,'pattrInfos', function(data){
				$.endPageLoading();
				$("#OpenSubmit").css("display","none");
				 $("#CheckPart").css("display","none");
				 $("#AuditPart").css("display","none");
			},function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
			});
		}else if("CHECK"==flags){
			var checkResuit = $("#CHECK_RESULT1").val();
			if(checkResuit==null||checkResuit==""){
				$.validate.alerter.one($("#CHECK_RESULT1")[0],"您没有输入检查结果，请输入!");
				return false;
			}
			var remark = $("#REMARK1").val();
			if(remark==null||remark==""){
				$.validate.alerter.one($("#REMARK1")[0],"您没有备注，请输入!");
				return false;
			}
			if(checkResuit==checkResuits&&remark==remarks){
				$.validate.alerter.one($("#CHECK_RESULT1")[0],"您没有对检查结果或备注信息做修改，不能提交，请修改后提交!");
				return false;
			}
			$.beginPageLoading("数据查询中......");
			$.ajax.submit("","updateCheckRecordInfo", "&FLAG="+flags+"&IBSYSID="+ibsysId+"&CHECK_RESULT="+checkResuit+"&REMARK="+remark+"&CHECK_ID="+checkId,'pattrInfos1', function(data){
				$.endPageLoading();
				$("#OpenSubmit").css("display","none");
				 $("#CheckPart").css("display","none");
				 $("#AuditPart").css("display","none");
			},function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
			});
		}
	}else{
		var busiType = "FOURMANAGE";
		if("AUDIT"==flags){
			var checktype = "2";
			var	checkResuit = $("#CHECK_RESULT").val();
			if(checkResuit==null||checkResuit==""){
				$.validate.alerter.one($("#CHECK_RESULT")[0],"您没有输入检查结果，请输入!");
				return false;
			}
			var remark = $("#REMARK").val();
			if(remark==null||remark==""){
				$.validate.alerter.one($("#REMARK")[0],"您没有备注，请输入!");
				return false;
			}
			$.beginPageLoading("数据查询中......");
			$.ajax.submit("","addCheckRecordInfo", "&FLAG="+flags+"&BUSI_TYPE="+busiType+"&CHECK_TYPE="+checktype+"&IBSYSID="+ibsysId+"&CHECK_RESULT="+checkResuit+"&REMARK="+remark,'pattrInfos', function(data){
				$.endPageLoading();
				$("#OpenSubmit").css("display","none");
				 $("#CheckPart").css("display","none");
				 $("#AuditPart").css("display","none");
			},function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
			});
		}else if("CHECK"==flags){
			var checktype = "1";
			var checkResuit = $("#CHECK_RESULT1").val();
			if(checkResuit==null||checkResuit==""){
				$.validate.alerter.one($("#CHECK_RESULT1")[0],"您没有输入检查结果，请输入!");
				return false;
			}
			var remark = $("#REMARK1").val();
			if(remark==null||remark==""){
				$.validate.alerter.one($("#REMARK1")[0],"您没有备注，请输入!");
				return false;
			}
			$.beginPageLoading("数据查询中......");
			$.ajax.submit("","addCheckRecordInfo", "&FLAG="+flags+"&BUSI_TYPE="+busiType+"&CHECK_TYPE="+checktype+"&IBSYSID="+ibsysId+"&CHECK_RESULT="+checkResuit+"&REMARK="+remark,'pattrInfos1', function(data){
				$.endPageLoading();
				$("#OpenSubmit").css("display","none");
				 $("#CheckPart").css("display","none");
				 $("#AuditPart").css("display","none");
			},function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
			});
		}
	}
}