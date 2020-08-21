function refreshPartAtferAuth(data)
{
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('AuthPart', 'feeProtectDeal',  "&USER_INFO="+(data.get("USER_INFO")).toString(), 'hiddenPart,QueryListPart', function(data){
		$.endPageLoading();
	},
	function(code, info, detail){
		$.endPageLoading();
		MessageBox.error("错误提示","加载业务数据!", $.auth.reflushPage, null, info, detail);
    });
}

function clickElement(checkbox){
	checkbox = $(checkbox);
	var elementId = checkbox.attr('element_id');
	var oldchecked = checkbox.attr('oldchecked');
	var checked = checkbox.attr('checked');
	var startDate = $("#DISCNT_START_"+elementId);
	var endDate = $("#DISCNT_END_"+elementId);
	if(oldchecked=="true" && checked){
		startDate.css("display","");
		endDate.css("display","");
		
		checkbox.attr('start_date', checkbox.attr('old_start_date'));
		checkbox.attr('end_date', checkbox.attr('old_end_date'));
	}
	else if (oldchecked=="true" && !checked){
		//setEndDateForDel(checkbox);
	}
	else if (oldchecked=="false" && checked){
		startDate.css("display","");
		endDate.css("display","");
		//setStartDateForAdd(checkbox);
	}
	else if (oldchecked=="false" && !checked){
		startDate.css("display","none");
		endDate.css("display","none");
		startDate.val('');
		endDate.val('');
	}
	if(elementId == "99993222" && checked){
		MessageBox.alert("提示","慎重操作！此开关设置后将不能订购增值业务。");
	}
}

function submitBeforeAction(){
	var dataList = $.DatasetList();
	var inputs = $("#userDiscntTable input");
	for(var i = 0 ;i<inputs.length;i++){
		var input = inputs[i];
		if($(input).attr("type") == "checkbox"){
			var strChecked = input.checked ? 'true' : 'false';
			if(input.getAttribute("oldchecked")!=strChecked){	
				cbId= input.getAttribute("id");
				var data = spDecodeElem(cbId);
				dataList.add(data);
			}	
		}	
	}
	if(dataList.length==0){
	   alert('没有选择一个元素，请选择一个');
	   return false;
	}
	var param = "&SELECTED_ELEMENTS="+dataList;
	$.cssubmit.addParam(param);
       return true;
}

/** 元素拼串，拼公共参数 */
function spDecodeElem(cbId) {
	var param = new Wade.DataMap();
	var elem = document.getElementById(cbId);
	param.put("PACKAGE_ID", elem.getAttribute("package_id"));
	param.put("ELEMENT_ID", elem.getAttribute("element_id"));
	param.put("START_DATE", elem.getAttribute("start_date"));
	param.put("END_DATE", elem.getAttribute("end_date"));
	param.put("INST_ID",elem.getAttribute("inst_id"));
	if(elem.checked){
		param.put("MODIFY_TAG", "0");
	}else{
		param.put("MODIFY_TAG", "1");
	}
	return param;
}