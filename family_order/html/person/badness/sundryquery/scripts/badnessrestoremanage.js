function queryBadnessInfo() {
	if(!verifyAll('badInfoPart'))
   	{
	   return false;
   	}
	$.beginPageLoading("信息查询中..");
	
    $.ajax.submit('badInfoPart,badInfoNav', 'queryBadnessInfo', null, 'badInfoTablePart,TipInfoPart', function(data){
    	$("#CSSUBMIT_BUTTON").attr("disabled",false).removeClass("e_dis");
		$.endPageLoading();
		var display = $("#TipInfoPart").css("display");
		if(data.get('ALERT_INFO') != null && data.get('ALERT_INFO') != '') {
			if(display == 'none') {
				$("#TipInfoPart").css("display","block");
			}
		}else {
			if(display= 'block' || display == null || display == '') {
				$("#TipInfoPart").css("display","none");
			}
		}
	},
	function(error_code,error_info,detail){
		$("#CSSUBMIT_BUTTON").attr("disabled",true).addClass("e_dis");
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}

function getSelectedRowDatas() {
	var data = $.table.get("badTable").getCheckedRowDatas();//获取选择中的数据
	//alert(data);
	return data;
}

function restoreBadnessInfos() {
	var data = getSelectedRowDatas();
	if(data == null || data.length == 0) {
		alert('请选择待处理信息后，再继续办理业务!');
		return false;
	}
	if(!verifyAll('dealInfoPart'))
   	{
	   return false;
   	}
   	
	var param = "&BADNESS_TABLE=" + data;
	$.beginPageLoading("业务处理中..");
     $.ajax.submit('dealInfoPart,remarkInfoPart', 'restoreBadnessInfos', param, 'badInfoPart,badInfoTablePart,dealInfoPart,remarkInfoPart', function(data){
		$.endPageLoading();
		if(data.get('ALERT_INFO') != null && data.get('ALERT_INFO') != '') {
			$.showSucMessage(data.get('ALERT_INFO'));
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


function changeReportType(){
	$.beginPageLoading("载入中..");
     $.ajax.submit('dealInfoPart', 'getFourthTypeCodes', null, 'FourthTypeCodePart,FifthTypeCodePart,ServRequestTypePart,SeventhTypeCodePart',function(data){
		$.endPageLoading();
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}

function changeFourthType(){	
	$.beginPageLoading("载入中..");
     $.ajax.submit('dealInfoPart', 'getFifthTypeCodes', null, 'FifthTypeCodePart,ServRequestTypePart,SeventhTypeCodePart',function(data){
		$.endPageLoading();
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}

function changeFifthType(){	
	$.beginPageLoading("载入中..");
     $.ajax.submit('dealInfoPart', 'getServRequestType', null, 'ServRequestTypePart,SeventhTypeCodePart',function(data){
		$.endPageLoading();
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}

function changeRequestType(){	
	$.beginPageLoading("载入中..");
     $.ajax.submit('dealInfoPart', 'getSevenTypeCodes', null, 'SeventhTypeCodePart',function(data){
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
							$('#cond_FOURTH_TYPE_CODE').val("");
							$('#cond_FIFTH_TYPE_CODE').val("");
							$('#cond_SERV_REQUEST_TYPE').val("");
							$('#cond_SEVEN_TYPE_CODE').val("");
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
				+ "&SERV_REQUEST_TYPE="+data.get(0).get('SIXTH_TYPE_CODE');

	$.beginPageLoading("载入中..");
	$.ajax
			.submit(
					null,
					'revcIdCheckBoxOn',
					cond,
					'FourthTypeCodePart,FifthTypeCodePart,ServRequestTypePart,SeventhTypeCodePart',
					function(dt) {
						$('#cond_DEAL_ASSORT').val(
								data.get(0).get('REPORT_TYPE_CODE'));
						$('#cond_FOURTH_TYPE_CODE').val(
								data.get(0).get('FOURTH_TYPE_CODE'));
						$('#cond_FIFTH_TYPE_CODE').val(
								data.get(0).get('FIFTH_TYPE_CODE'));
						$('#cond_SIXTH_TYPE_CODE').val(
								data.get(0).get('SIXTH_TYPE_CODE'));
						$('#cond_SEVEN_TYPE_CODE').val(
								data.get(0).get('SEVEN_TYPE_CODE'));
						$.endPageLoading();
					},

					function(error_code, error_info, detail) {
						$.endPageLoading();
						MessageBox.error("错误提示", error_info, null, null, null,
								detail);
					});

}