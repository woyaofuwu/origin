function refreshPartAtferAuth(data)
{
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('AuthPart', 'loadChildInfo',  "&USER_INFO="+(data.get("USER_INFO")).toString(), 'hiddenPart,QueryListPart', function(data1){
		$.endPageLoading();
	},
	function(code, info, detail){
		
		$.endPageLoading();
		MessageBox.error("错误提示","加载业务数据!", $.auth.reflushPage, null, info, detail);
		//alert(error_info);
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
		
		startDate.val(checkbox.attr('old_start_date'));
		endDate.val(checkbox.attr('old_end_date'));
		checkbox.attr('start_date', checkbox.attr('old_start_date'));
		checkbox.attr('end_date', checkbox.attr('old_end_date'));
	}
	else if (oldchecked=="true" && !checked){
		setEndDateForDel(checkbox);
	}
	else if (oldchecked=="false" && checked){
		startDate.css("display","");
		endDate.css("display","");
		setStartDateForAdd(checkbox);
	}
	else if (oldchecked=="false" && !checked){
		startDate.css("display","none");
		endDate.css("display","none");
		startDate.val('');
		endDate.val('');
	}
}

function setEndDateForDel(checkbox){
	checkbox = $(checkbox);
	var elementId = checkbox.attr('element_id');
	var endDate = $("#DISCNT_END_" +elementId); 
	var cancelTag = checkbox.attr('cancelTag');

	checkbox.attr('old_start_date', checkbox.attr('start_date'));
	checkbox.attr('old_end_date', checkbox.attr('end_date'));
	checkbox.attr('old_state', checkbox.attr('state'));
	if (cancelTag!="4"){
		//0---effect now
		//1---yestoday
		//2---today
		//3---last day this month
		//other---yestoday
		var param="&ELEMENT_ID="+elementId+"&PACKAGE_ID="+checkbox.attr('package_id')+"&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
		$.beginPageLoading("数据查询中..");
		$.ajax.submit('QueryListPart,hiddenPart,AuthPart','geneDelDiscntDate',param,'',function(data){
			var tmpEndDate=data.get(0).get("END_DATE");
			endDate.val(tmpEndDate.substr(0,10));
			checkbox.attr('end_date', tmpEndDate);
			$.endPageLoading();
			return;
		});
	}
	else if (cancelTag=="4"){
		//can't cancel
		var date1 = parseDate(checkbox.end_date);
		var date2 = parseDate(getNowDate());
		if (date1 <= date2){
			//setDate(checkbox,endDate,getNowDate());
			endDate.val(getNowDate());
			checkbox.attr('end_date',getNowDate());
		}
		else{
			var msg = checkbox.attr('element_name') + "将在" + checkbox.attr('end_date') + "到期结束，现在无法取消该元素!";
			MessageBox.alert('状态',msg, function(){
			});
			checkbox.attr('checked', true);
			return;
		}

	}
}

function setStartDateForAdd(checkbox){
	checkbox = $(checkbox);
	var elementId = checkbox.attr('element_id');
	var startDate = $("#DISCNT_START_"+elementId);
	var endDate = $("#DISCNT_END_"+elementId);
	var enableTag = checkbox.attr('enable_tag');
	if (enableTag=="0" || enableTag=="1" || enableTag=="2" || enableTag=="4" ||  enableTag=="9"){
		//0---effect now
		//1---next month
		//2---tomorrow
		//4---AbsoluteDate
		var param="&ELEMENT_ID="+elementId+"&PACKAGE_ID="+checkbox.attr('package_id')+"&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
		$.beginPageLoading("数据查询中..");
		$.ajax.submit('QueryListPart,hiddenPart,AuthPart','geneNewDiscntDate',param,'',function(data){
			var tmpStartDate=data.get(0).get("START_DATE");
			var tmpEndDate=data.get(0).get("END_DATE");
			startDate.val(tmpStartDate.substr(0,10));
			checkbox.attr('start_date',tmpStartDate);
			if (startDate) {
				startDate.attr("disabled","true");
			}
			endDate.val(tmpEndDate.substr(0,10));
			checkbox.attr('end_date',tmpEndDate);
			if (endDate) {
				endDate.attr("disabled","true");
			}
			$.endPageLoading();
			return;
		});
	}
	else if (enableTag=="3"){	    
	    MessageBox.confirm("请选择生效时间",checkbox.attr('element_name')+"立即生效?点击【是】立即生效，点击【否】下账期生效.",function(btn){
	    afterSelectStartDate(btn,checkbox);},{ok:"是",cancel:"否"});
	}
	else{
		alert("\u672A\u77E5\u7684\u65E5\u671F\u914D\u7F6E  enable_tag is " +  enableTag);
		return;
	}
}

function afterSelectStartDate(btn,checkbox){
	checkbox = $(checkbox);
	var elementId = checkbox.attr('element_id');
	var startDate = $("#DISCNT_START_"+elementId);
	var endDate = $("#DISCNT_END_"+elementId);
	var param="&ELEMENT_ID="+elementId+"&PACKAGE_ID="+checkbox.attr('package_id')+"&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
	if (btn=="ok"){
		param=param+"&CHOOSE_FLAG=1";
	}else{
		param=param+"&CHOOSE_FLAG=2";
	}
	
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('QueryListPart,hiddenPart,AuthPart','geneNewDiscntDate',param,'',function(data){
		var tmpStartDate=data.get(0).get("START_DATE");
		var tmpEndDate=data.get(0).get("END_DATE");
		startDate.val(tmpStartDate.substr(0,10));
		checkbox.attr('start_date',tmpStartDate);
		if (startDate) {
			startDate.attr("disabled","true");
		}
		endDate.val(tmpEndDate.substr(0,10));
		checkbox.attr('end_date',tmpEndDate);
		if (endDate) {
			endDate.attr("disabled","true");
		}
		$.endPageLoading();
		return;
	});
}


function getNowDate(){
	var datestr = $("#NOW_DATE").val();
	return datestr;
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
	param.put("SPEC_TAG",elem.getAttribute("spec_tag"));
	if(elem.checked){
		param.put("MODIFY_TAG", "0");
	}else{
		param.put("MODIFY_TAG", "1");
	}
	return param;
}


function parseDate(str){  
  if(typeof str == 'string'){   
   	var   d   =   new   Date(Date.parse(str.replace(/-/g,   "/")));  
   	return d; 
  }   
  return null;   
}

