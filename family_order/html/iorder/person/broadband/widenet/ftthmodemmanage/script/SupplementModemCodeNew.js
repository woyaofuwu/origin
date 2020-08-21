
var userInfo = "";
var modemSupplementType = "";
function refreshPartAtferAuth(data){
	userInfo = data.get("USER_INFO");
//	$("#FTTHBusiModemManage").attr("class","e_hideX");
//	$("#FTTHModemManage").attr("class","");
	$("#FTTHModemTable tbody").html("");
	$("#busiModemTable tbody").html("");
//	$.table.get("FTTHModemTable").cleanRows();
//	$.table.get("busiModemTable").cleanRows();
	$("#check_MODEMID").val("");
	$.ajax.submit('', 'checkFTTHBusi', "&USER_ID="+userInfo.get("USER_ID")+"&SERIAL_NUMBER="+userInfo.get("SERIAL_NUMBER"), 'FTTHModemPart,busiModemPart,KDNumberPart,checkModemPart', function(rtnData) { 
		
		if(rtnData!=null&&rtnData.length > 0){//该用户不存在7341 集团商务宽带产品，不能办理FTTH商务光猫补录
			$('#SupplementBusiModemCode').css('display', 'none');
			$('#SupplementModemCode').css('display', '');
			modemSupplementType = "1";
			queryModermInfo();
			$.endPageLoading();
		}else{
			$('#SupplementBusiModemCode').css('display', '');
			$('#SupplementModemCode').css('display', 'none');
			modemSupplementType = "2";
			$.endPageLoading();
		}
	}, function(error_code, error_info,detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
	}); 

	$.endPageLoading();
}

/**
 * 控制基本信息显示\隐藏
 * @param btn
 * @param o
 */
function displaySwitch(btn, o) {
	var button = $(btn);
	var div = $('#' + o);

	if (div.css('display') != "none") 
	{
		div.css('display', 'none');
		button.children("i").attr('className', 'e_ico-unfold');
		button.children("span:first").text("展示客户基本信息");
	} 
	else 
	{
		div.css('display', '');
		button.children("i").attr('className', 'e_ico-fold');
		button.children("span:first").text("隐藏客户基本信息");
	}
}

function queryBusiModermInfo(){
	if(userInfo != ""){
		var kdNumber = $("#cond_KD_NUMBER").val();
		if(kdNumber == ""){
			MessageBox.alert("提示", "请输入宽带号码!");
			return;
		}
		var serial_number = userInfo.get("SERIAL_NUMBER");
		$.beginPageLoading("正在查询数据...");
		$.ajax.submit('','queryBusiModemSupplementInfo',"&SERIAL_NUMBER="+serial_number+"&KD_NUMBER="+kdNumber, 'busiModemPart',function(rtnData) { 
			if(rtnData!=null&&rtnData.length > 0){
				$.endPageLoading();
			}else{
				$.endPageLoading();
				MessageBox.alert("提示","该用户没有需要补录的光猫信息！");
//				alert("该用户没有需要补录的光猫信息！");
				return false; 
			}
		}, function(error_code, error_info,detail) {
			$.endPageLoading();
			MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
		});
	}else{
		MessageBox.alert("鉴权提示","用户请先鉴权");
	}
}

function queryModermInfo(){
	if(userInfo != ""){
		var user_id = userInfo.get("USER_ID");
		var serial_number = userInfo.get("SERIAL_NUMBER");
		$.beginPageLoading("正在查询数据...");
		$.ajax.submit('','queryModemSupplementInfo',"&USER_ID="+user_id+"&SERIAL_NUMBER="+serial_number, 'FTTHModemPart',function(rtnData) { 
			if(rtnData!=null&&rtnData.length > 0){
				$.endPageLoading();
			}else{
				$.endPageLoading();
				MessageBox.alert("提示","该用户没有需要补录的光猫信息！");
//				alert("该用户没有需要补录的光猫信息！");
				return false; 
			}
		}, function(error_code, error_info,detail) {
			$.endPageLoading();
			MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
		});
	}else{
		MessageBox.alert("鉴权提示","用户请先鉴权");
	}
}

function checkSUPPLEMENTModemId(btn){
	if(userInfo == ""){
		MessageBox.alert("鉴权提示","用户请先鉴权");
		return;
	}
//	popupPage('光猫校验','checkModempopup',null,null,'','c_popup c_popup-half c_popup-half-hasBg','','');
	showPopup('checkModempopup','checkModempopup_item',true);
//	popupDiv('checkModempopup','40%','光猫校验');
	var oldModermId = btn.getAttribute('str1');
	if(oldModermId == null || oldModermId == ""){
		$("#cond_OLD_MODEM_ID").val("");
	}else{
		$("#cond_OLD_MODEM_ID").val(oldModermId);
	}
	
}


function checkModemId(){
	var user_id = userInfo.get("USER_ID");
	var modermId = $("#check_MODEMID").val();
	if(modermId == ""){
		MessageBox.alert("提示", "请输入终端串码");
		return false;
	}
	var operType = null;
	if(modemSupplementType == 1){//FTTH光猫补录
		operType = 'FTTHModemTable';
	}else if(modemSupplementType == 2){//FTTH商务光猫补录
		operType = 'busiModemTable';
	}
	var table = FTTHModemTable.selected;
	var json = FTTHModemTable.getRowData(table);
	if(json.get('SUPPLEMENT_TYPE') == "0" && json.get('SUPPLEMENT_MODEM_ID') == modermId){
		MessageBox.alert("提示","该光猫已预占，不能重复预占!");
		return;
	}
	$.beginPageLoading("正在校验光猫信息...");
	var serial_number = userInfo.get("SERIAL_NUMBER");
	$.ajax.submit('','checkModemId',"&SERIAL_NUMBER="+serial_number+"&RES_ID="+modermId,"",function(data) { 
		$.endPageLoading();	
		hidePopup('checkModempopup');
//		$.closePopupDiv('checkModempopup');
		$("#check_MODEMID").val("");
		var modermInfo = data.get(0);
		var res_no = modermInfo.get("RES_NO");
		var res_kind_code = modermInfo.get("RES_KIND_CODE");
		var supplementType = modermInfo.get("SUPPLEMENT_TYPE");
		json["SUPPLEMENT_MODEM_ID"]=res_no;
		json["SUPPLEMENT_MODEM_TYPE"]=res_kind_code;
		json["SUPPLEMENT_TYPE"]=supplementType;
		var updaterow = FTTHModemTable.updateRow(json,FTTHModemTable.selected);
	},function(e,i){
		$.endPageLoading();	
		MessageBox.alert("提示",i,function(){});
		hidePopup('checkModempopup');
//		$.closePopupDiv('checkModempopup');
		$("#check_MODEMID").val("");
	});
}

function onTradeSubmit()
{
	var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
	var paramList = new Wade.DatasetList();
	var operType = null;
	var tables = null;
	var checkedData = null;
	if(modemSupplementType == 1){//FTTH光猫补录
		operType = 'FTTHModemTable';
		tables = FTTHModemTable.getData(true);
		checkedData = FTTHModemTable.getCheckedRowsData("monitorids");
	}else if(modemSupplementType == 2){//FTTH商务光猫补录
		operType = 'busiModemTable';
		tables = busiModemTable.getData(true);
		checkedData = busiModemTable.getCheckedRowsData("monitorids");
	}
//	var table = $.table.get(operType);
	if(userInfo == ""){
		MessageBox.alert("提示","用户请先鉴权");
		return;
	}
	if(tables == null){
		MessageBox.alert("提示","该用户没有需要补录的光猫!");
		return;
	}
//	var checkedData = getCheckedTableData(modemSupplementType,null,"monitorids");
	if(checkedData == null){
		MessageBox.alert("提示","请选择需要补录的光猫！");
		return;
	}
	var listSize = checkedData.length;
	if(listSize > 1){
		MessageBo.alert("提示","一次只能操作一个光猫补录！");
		return;
	}
	if(listSize == null || listSize == 0){
		MessageBox.alert("提示","请选择需要补录的光猫！");
		return;
	}
	var checkedDataMap = checkedData.get(0)
	var inst_id = checkedDataMap.get('INST_ID');
	var modem_id = checkedDataMap.get('SUPPLEMENT_MODEM_ID');
	var modem_type = checkedDataMap.get('SUPPLEMENT_MODEM_TYPE');
	var supplementType = checkedDataMap.get('SUPPLEMENT_TYPE');
	if(modem_id == "" || $.format.undef(modem_id) == ""){
		MessageBox.alert("提示", "请校验补录的光猫串号信息！");
		return;
	}
	param += '&INST_ID='+inst_id;
	param += '&MODEM_ID='+modem_id;
	param += '&MODEM_TYPE='+modem_type;
	param += '&SUPPLEMENT_TYPE='+supplementType;
	param += '&OPER_TYPE='+modemSupplementType;
	$.cssubmit.addParam(param);
	return true;
}


/*function getCheckedTableData(modemSupplementType, g, chekboxName)
{
	
	var b = new Wade.DatasetList();
	var d = Wade.table.get(tbName);
	var checkboxname = chekboxName;
	var c = Wade("tbody", d.getTable()[0]);
	var e = d.tabHeadSize;
	if (c)
	{
		Wade("tr", c[0]).each(function(h, i) 
		{
			var isChecked = $('input[name=' + checkboxname + ']', this).attr("checked");
			if (isChecked) 
			{
				var j = d.getRowData(g, h + e);
				if (j)
				{
						b.add(j);
				}
			}
			j = null;
		});
	}
	c = null;
	d = null;
	return b;
}*/
