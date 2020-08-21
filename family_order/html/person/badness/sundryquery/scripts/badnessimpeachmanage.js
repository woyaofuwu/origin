function initBtn() {
	$("#bBcommit").attr("disabled",true).addClass("e_dis");
}

function queryBadnessInfo() {
	if(!verifyAll('badInfoPart'))
   	{
	   return false;
   	}
	$.beginPageLoading("信息查询中..");
     $.ajax.submit('badInfoPart', 'queryBadnessInfo', null, 'badInfoTablePart,dealRemarkInfoPart,TipInfoPart', function(data){
    	 var mm =$("#cond_BADNESS_INFO_NET").val();
     	if(mm!='999'){
     		$("#CSSUBMIT_BUTTON").attr("disabled",false).removeClass("e_dis");
     		$("#bBcommit").attr("disabled",false).removeClass("e_dis");
     	}
     	
		$.endPageLoading();
		if(data.get("ALERT_INFO") != null) {
			$("#TipInfoPart").css("display","block");
		}else {
			$("#TipInfoPart").css("display","none");
		}
	},             
	
	function(error_code, error_info, detail){
		$("#CSSUBMIT_BUTTON").attr("disabled",true).addClass("e_dis");
		$("#bBcommit").attr("disabled",true).addClass("e_dis");
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}

function getSelectedRowDatas() {
	var data = $.table.get("badTable").getCheckedRowDatas();//获取选择中的数据
	//alert(data);
	return data;
}

function submitBadnessInfos() {
	var data = getSelectedRowDatas();	
	if(data==null || data.length==0) {
   		alert('请选择待处理信息后，再办理业务!');
   		return false;
   	}
	
	if (data.length > 1) {
		obj.checked = false;
		alert('一次只能选择一条信息进行处理!');
		return false;
	}
   	
	if(!verifyAll('dealRemarkInfoPart'))
   	{
	   return false;
   	}
	var param = "&BADNESS_TABLE=" + data + "&REMARK=" + data;
	$.beginPageLoading("业务受理中..");
     $.ajax.submit('dealRemarkInfoPart', 'submitBadnessInfos', param, 'badInfoTablePart,dealRemarkInfoPart,TipInfoPart', function(data){
		$.endPageLoading();
		if(data.get('ALERT_INFO') != null && data.get('ALERT_INFO') != '') {
			$.showWarnMessage(data.get('ALERT_INFO'));
		}
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}

function tempDeal() {
	if(!verifyAll('dealRemarkInfoPart'))
   	{
	   return false;
   	}
   	var data = getSelectedRowDatas();
	var param = "&BADNESS_TABLE=" + data;
	$.beginPageLoading("业务受理中..");
     $.ajax.submit('dealRemarkInfoPart', 'tempDeal', param, null, function(data){
		$.endPageLoading();
		if(data.get("ALERT_INFO") != null) {
			$("#TipInfoPart").css("display","block");
		}else {
			$("#TipInfoPart").css("display","none");
		}
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}

function exportBeforeAction(obj) {
	var data = $.table.get("badTable").getTableData("INFO_RECV_ID", true);
	//alert(data);
	if(data.length == 0) {
		alert("请先查询出数据再导出!");
		return false;
	}
	return true;
}
function changenet(){
	var mm =$("#cond_BADNESS_INFO_NET").val();
	if(mm=='999'){
		$("#CSSUBMIT_BUTTON").attr("disabled",true).addClass("e_dis");
		$("#bBcommit").attr("disabled",true).addClass("e_dis");
	}else{
		$("#CSSUBMIT_BUTTON").attr("disabled",false).removeClass("e_dis");
     	$("#bBcommit").attr("disabled",false).removeClass("e_dis");
	}
}

function changeReportType(){
	$.beginPageLoading("载入中..");
     $.ajax.submit('dealRemarkInfoPart', 'getFourthTypeCodes', null, 'FourthTypeCodePart,FifthTypeCodePart,ServRequestTypePart,SeventhTypeCodePart',function(data){
		$.endPageLoading();
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}

function changeFourthType(){	
	$.beginPageLoading("载入中..");
     $.ajax.submit('dealRemarkInfoPart', 'getFifthTypeCodes', null, 'FifthTypeCodePart,ServRequestTypePart,SeventhTypeCodePart',function(data){
		$.endPageLoading();
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}

function changeFifthType(){	
	$.beginPageLoading("载入中..");
     $.ajax.submit('dealRemarkInfoPart', 'getServRequestType', null, 'ServRequestTypePart,SeventhTypeCodePart',function(data){
		$.endPageLoading();
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}

function changeRequestType(){	
	$.beginPageLoading("载入中..");
     $.ajax.submit('dealRemarkInfoPart', 'getSevenTypeCodes', null, 'SeventhTypeCodePart',function(data){
		$.endPageLoading();
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}

function setServerType(obj) {
	var data = getSelectedRowDatas();
	if (data == null || data.length == 0) {
		$.beginPageLoading("载入中..");
		$.ajax
				.submit(
						null,
						'revcIdCheckBoxOff',
						null,
						'FourthTypeCodePart,FifthTypeCodePart,ServRequestTypePart,SeventhTypeCodePart',
						function(dt) {
							$('#cond_DEAL_ASSORT').val("");
							$('#cond_DEAL_ASSORT').attr('disabled', false);
							$('#cond_FOURTH_TYPE_CODE').val("");
							$('#cond_FOURTH_TYPE_CODE').attr('disabled', false);
							$('#cond_FIFTH_TYPE_CODE').val("");
							$('#cond_FIFTH_TYPE_CODE').attr('disabled', false);
							$('#cond_SERV_REQUEST_TYPE').val("");
							$('#cond_SERV_REQUEST_TYPE').attr('disabled', false);
							$('#cond_SEVEN_TYPE_CODE').val("");
							$('#cond_SEVEN_TYPE_CODE').attr('disabled', false);
							$.endPageLoading();
						},

						function(error_code, error_info, detail) {
							$.endPageLoading();
							MessageBox.error("错误提示", error_info, null, null,
									null, detail);
						});

		return;
	}

	if (data.length > 1) {
		obj.checked = false;
		alert('一次只能选择一条信息进行处理!');
		return false;
	}
	
	var cond = "&REPORT_TYPE_CODE="+data.get(0).get('REPORT_TYPE_CODE')
				+ "&FOURTH_TYPE_CODE="+data.get(0).get('FOURTH_TYPE_CODE')
				+ "&FIFTH_TYPE_CODE="+data.get(0).get('FIFTH_TYPE_CODE')
				+ "&SERV_REQUEST_TYPE="+data.get(0).get('SERV_REQUEST_TYPE');

	$.beginPageLoading("载入中..");
	$.ajax
			.submit(
					null,
					'revcIdCheckBoxOn',
					cond,
					'FourthTypeCodePart,FifthTypeCodePart,ServRequestTypePart,SeventhTypeCodePart',
					function(dt) {
						$('#cond_DEAL_ASSORT').val(data.get(0).get('REPORT_TYPE_CODE'));
						$('#cond_FOURTH_TYPE_CODE').val(data.get(0).get('FOURTH_TYPE_CODE'));
						$('#cond_FIFTH_TYPE_CODE').val(data.get(0).get('FIFTH_TYPE_CODE'));
						$('#cond_SERV_REQUEST_TYPE').val(data.get(0).get('SERV_REQUEST_TYPE'));
						$('#cond_SEVEN_TYPE_CODE').val(data.get(0).get('SEVEN_TYPE_CODE'));
						
						$('#cond_DEAL_ASSORT').attr('disabled', $('#cond_DEAL_ASSORT').val() != '' ? true : false);
						$('#cond_FOURTH_TYPE_CODE').attr('disabled', $('#cond_FOURTH_TYPE_CODE').val() != '' ? true : false);
						$('#cond_FIFTH_TYPE_CODE').attr('disabled', $('#cond_FIFTH_TYPE_CODE').val() != '' ? true : false);
						$('#cond_SERV_REQUEST_TYPE').attr('disabled', $('#cond_SERV_REQUEST_TYPE').val() != '' ? true : false);
						$('#cond_SEVEN_TYPE_CODE').attr('disabled', $('#cond_SEVEN_TYPE_CODE').val() != '' ? true : false);
						$.endPageLoading();
					},

					function(error_code, error_info, detail) {
						$.endPageLoading();
						MessageBox.error("错误提示", error_info, null, null, null,
								detail);
					});

}
