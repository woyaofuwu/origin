function refreshPartAtferAuth(data)
{
	var userId = data.get('USER_INFO').get('USER_ID');
	var serialNumber = data.get('USER_INFO').get('SERIAL_NUMBER');
	var param = '&SERIAL_NUMBER=' + serialNumber;
	$.ajax.submit(null, 'loadInfo', param, 'viceInfopart,rejectModeInfo');
}

/**
 * 选择拒收方式
 */
function onChangeMode(){
	var rejectMode = $('#REJECT_MODE').val();
	if(rejectMode=="1"){	//全网拒收
		disableCheckBox('viceCheckBox', true);
	}else if(rejectMode=="2"){	//成员拒收
		disableCheckBox('viceCheckBox', false);
	}
	
}

function disableCheckBox(objName, disable){
	var chkBox = document.getElementsByName(objName);
	if(chkBox){
		for(var i=0;i<chkBox.length;i++){
			chkBox[i].checked = false;
			chkBox[i].disabled = disable;
			var rowIndex = chkBox[i].parentNode.parentNode.rowIndex;
			var rowData = $.table.get("viceInfoTable").getRowData(null,rowIndex);
			var havaRRSvc = rowData.get("HAVA_RR_SVC");//如果已经办理过挂机提醒，则不能再办理
			if(havaRRSvc == "true"){
				chkBox[i].disabled = true;
			}
		}
	}
}

function isCanChecked(obj){
	if(obj.checked){
		var checkBoxs = document.getElementsByName("viceCheckBox");
		for(var i=0;i<checkBoxs.length;i++){
			if(checkBoxs[i].checked){
				checkBoxs[i].checked = false;
			}
		}
		obj.checked = true;
	}
}

function onTradeSubmit()
{
	var rejectMode = $('#REJECT_MODE').val();
	var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
	param += '&REMARK='+$('#REMARK').val();
	param += '&REJECT_MODE=' + rejectMode;
	var data = $.table.get("viceInfoTable").getCheckedRowDatas();
	param += '&MEB_LIST='+data;
	
	if(rejectMode == "2" && data.length == 0){
		alert("选择[成员拒收]时，请选择需要办理拒收的成员！");
		return false;
	}
	
	$.cssubmit.addParam(param);
	
	return true;
}