/**
 * REQ201510270009 FTTH光猫申领押金金额显示优化【2015业务挑刺】
 * 新增显示押金金额
 * */
var userInfo = "";
var checkDeposit = "";
var applytype = "";
var modemManageType = "";
function refreshPartAtferAuth(data){
	userInfo = data.get("USER_INFO");
	$("#NOPHONE_SERIAL_NUMBER").val(data.get("USER_INFO").get("SERIAL_NUMBER"));
	 
//	$("#FTTHBusiModemManage").attr("class","e_hideX");
//	$("#FTTHModemManage").attr("class","");
	$.beginPageLoading("校验宽带号码...");
	$.ajax.submit('', 'checkFTTHBusi', "&ROUTE_EPARCHY_CODE="+data.get("USER_INFO").get("EPARCHY_CODE")+"&USER_ID="+userInfo.get("USER_ID")+"&SERIAL_NUMBER="+userInfo.get("SERIAL_NUMBER")+"&WARN_TAG=1", 'ModermApplyPart,ModermCheckPart,QueryCondPart,QueryListPart,wideInfoPart', function(rtnData) { 
		$.endPageLoading();
		if(rtnData!=null&&rtnData.length > 0){//该用户不存在7341 集团商务宽带产品，不能办理FTTH商务宽带申领
			if(rtnData.get("CHECK_CODE") == "0"){//有未完工工单
				 MessageBox.alert("提示", rtnData.get("CHECK_INFO"));
				 return false;
			}else{  
				var modemCode=rtnData.get("MODEM_CODE"); 
				if(modemCode!=null && modemCode !="" && modemCode !="0"){
					MODERM_TRADE_TYPE.empty();
					$("#modermInfo").attr("class","");//换机、退还、丢失
					$("#modermapplyinfo").attr("class","e_hide");//申领
					$("#selApplyType").attr("class","li e_hide"); 
					MODERM_TRADE_TYPE.append("光猫更换", "2");
					MODERM_TRADE_TYPE.append("光猫退还", "3");
					MODERM_TRADE_TYPE.append("光猫丢失", "4");
					
					var hTOb=rtnData.get("hTOb"); 
					if(hTOb!=null && hTOb !="1"){
						$("#MODERM_TRADE_TYPE").val("2");
					}
					
				}else{
					MODERM_TRADE_TYPE.empty();
					$("#modermInfo").attr("class","e_hide");//换机、退还、丢失
					$("#modermapplyinfo").attr("class","");//申领
					$("#selApplyType").attr("class","li ");
					MODERM_TRADE_TYPE.append("光猫申领", "1");
					$("#MODERM_TRADE_TYPE").val("1");
				} 
				changeModermtrade();
				modemManageType = "2";//个人光猫状态
			}
		}
	}, function(error_code, error_info,detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
	}); 

	$.endPageLoading();
}

 

/** 新增一条记录，对应表格新增按钮 */
function addItem() {
	/* 校验所有的输入框 */ 	
	var kd_number=$("#KD_NUMBER").val();
	if(kd_number==""||kd_number==null){
		MessageBox.alert("提示","请先录入宽带号码！"); 
		$("#KD_NUMBER").focus();
		return false;
	}
	var cust_id=$("#CUST_ID").val();
	//获取编辑区的数据
	var editData = $.ajax.buildJsonData("editPart");
	editData['KD_NUMBER']=kd_number;  
	if($.table.get("memberTable").isPrimary("KD_NUMBER", editData)){
		MessageBox.alert("提示","该宽带号码已经存在列表中,请重新输入！");
		$("#KD_NUMBER").val("");
		$("#KD_NUMBER").focus();
		return false;
	} 
	$.beginPageLoading("校验宽带号码...");
	var serial_number = userInfo.get("SERIAL_NUMBER");
	$.ajax.submit('', 'checkKDNumber', "&KD_NUMBER="+kd_number+"&CUSTID_GROUP="+cust_id+'&SERIAL_NUMBER='+serial_number, '', function(rtnData) { 
		$.endPageLoading();  	
		if(rtnData!=null&&rtnData.length > 0){
			var checkResz=rtnData.get("RTNCODE");
			if(checkResz=="1" || checkResz=="2"){
				/* 新增表格行 */
				editData['CUST_NAME']=rtnData.get("CUST_NAME"); 
				editData['UPDATE_TIME']=rtnData.get("UPDATE_TIME"); 
				editData['KD_USERID']=rtnData.get("KD_USERID"); 
				editData['KD_TRADE_ID']=rtnData.get("KD_TRADE_ID");
				var newRow = $.table.get("memberTable").addRow(editData);
				newRow.append({tag:"td",style:"class:e_center",html:"<button class='e_button-blue' str3 = '"+kd_number+"' jwcid='@Any' onclick='checkApplyBusiModem(this)'><i class='e_ico-bottom' /><span>光猫录入</span></button>"});
				$("#KD_NUMBER").val("");
			}else{				
				MessageBox.alert("提示",rtnData.get("RTNMSG"));
				$("#KD_NUMBER").val("");
				return false;
			} 
		}
	}, function(error_code, error_info,detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
	});  
	
}
function checkApplyBusiModem(btn){
	popupDiv('checkBusiModermpopup','40%','光猫校验');
	var serial_number = btn.getAttribute('str3');
	if(serial_number == ""){
		$.closePopupDiv('checkBusiModermpopup');
		MessageBox.alert("提示","光猫校验，宽带号码不能为空");
		return;
	}else{
		$("#check_MODEM_SERIAL_NUMBER").val(serial_number);
	}
}
/** 删除一条记录，对应表格删除按钮 */
function delItem() {
//	var rowData = $.table.get("memberTable").getRowData();
	var rowData = memberTable.getRowData(memberTable.selected);
	if (rowData.length == 0) {
		MessageBox.alert("提示","请您选择记录后再进行删除操作！");
		return false;
	}
	MessageBox.confirm("确认提示","您确认要删除宽带号码【"+ rowData.get("KD_NUMBER")+"】？", function(btn){
		if(btn == "ok"){
			memberTable.deleteRow(memberTable.selected);
		}
	});
}

function checkBusiModemStatus(modermtrade){
	var msg = "";
	if(modermtrade == 2){
		msg = "更换";
	}else if(modermtrade == 3){
		msg = "退还";
	}else if(modermtrade == 4){
		msg = "丢失";
	}
	var checkedData = getCheckedTableData("busiModemTable",null,"monitorids");
	var listSize = checkedData.getCount();
	if(listSize > 1){
		MessageBox.alert("提示","一次只能操作一个光猫"+msg+"！");
		return false;
	}
	if(listSize < 1){
		MessageBox.alert("提示","请选择需要"+msg+"的光猫！");
		return false;
	}
	var checkedDataMap = checkedData.get(0);
	var modemStatus = checkedDataMap.get('MODERM_STATUS');
	var statusMsg = "";
	if(modemStatus != "申领"){
		if(modemStatus == ""){
			modemStatus = "不是申领状态";
		}
		MessageBox.alert("提示","该用户此光猫已"+modemStatus+"，不能再做"+msg+"操作");
		return false;
	}
	return true;
}

function checkBusiModermId(){
	if(!checkNumber()){
		return;
	}
	var modermId = $("#check_BUSI_MODEMID").val();
	if(modermId == ""){
		MessageBox.alert("提示","请输入终端串码");
		return false;
	}
	$.beginPageLoading("正在校验光猫信息...");
	var serial_number = $("#check_MODEM_SERIAL_NUMBER").val();
	$.ajax.submit('','checkModermId',"&SERIAL_NUMBER="+serial_number+"&RES_ID="+modermId,'',function(data) { 
		$.endPageLoading();	
		$.closePopupDiv('checkBusiModermpopup');
		$("#check_BUSI_MODEMID").val("");
			var modermInfo = data.get(0);
			var res_no = modermInfo.get("RES_NO");
			var res_kind_code = modermInfo.get("RES_KIND_CODE");
			var modermtrade = $("#BUSI_MODEM_TRADE_TYPE option:selected").val();
//			var table = null;
//			var json = table.getRowData();
			var json = new $.DataMap();
			if(modermtrade == 1){
				json["KD_MODEM_ID"]=res_no;
				json["KD_MODEM_TYPE"]=res_kind_code;
			}else if(modermtrade == 2){
				json["NEW_MODERM_ID"]=res_no;
				json["NEW_MODERM_TYPE"]=res_kind_code;
			}
			if(modermtrade == 1){
//				table = $.table.get("memberTable");
				memberTable.updateRow(json)
			}else if(modermtrade == 2){
//				table = $.table.get("busiModemTable");
				busiModemTable.updateRow(json);
			}
//			var updaterow = table.updateRow(json);
	},function(e,i){
		$.endPageLoading();	
		MessageBox.alert("提示",i,function(){});
		$.closePopupDiv('checkModermpopup');
		$("#check_MODERID").val("");
	});
}

function queryBusiModermInfo(){
	if(!checkNumber()){
		return;
	}
	var modermtrade = $("#BUSI_MODEM_TRADE_TYPE option:selected").val();
	var modermId = $("#cond_BUSI_MODERM_ID").val();
	var kdNumber = $("#cond_KD_NUMBER").val();
	if(kdNumber == ""){
		MessageBox.alert("提示","请输入宽带号码!");
		return;
	}
	var serial_number = userInfo.get("SERIAL_NUMBER");
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('','queryBusiModermInfo',"&SERIAL_NUMBER="+serial_number+"&MODERM_ID="+modermId+"&KD_NUMBER="+kdNumber+"&OPER_TYPE="+modermtrade, 'busiModemInfoPart',function(rtnData) { 
		if(rtnData!=null&&rtnData.length > 0){
			$.endPageLoading();
			if(modermtrade == 2){
				showModermCheck();
			}
		}else{
			$.endPageLoading();
			if(modermtrade == 2){
				showModermCheck();
			}
			MessageBox.alert("提示","用户没有该光猫信息！");
			return false; 
		}
	}, function(error_code, error_info,detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
	});
}


function checkNewBusiModermId(btn){
	var modemStatus = btn.getAttribute("str2");
	if(modemStatus != 1){
		var statusMsg = "";
		if(modemStatus == 2){
			statusMsg = "更换";
		}else if(modemStatus == 3){
			statusMsg = "退还";
		}else if(modemStatus == 4){
			statusMsg = "丢失";
		}else{
			statusMsg = "非申领状态";
		}
		MessageBox.alert("提示","光猫已"+statusMsg+",不能更换，录入新光猫！");
		return;
	}
	popupDiv('checkBusiModermpopup','40%','光猫校验');
	var oldModermId = btn.getAttribute('str1');
	$("#cond_BUSI_OLD_MODERM_ID").val(oldModermId);
	var serial_number = btn.getAttribute('str3');
	if(serial_number == ""){
		$.closePopupDiv('checkBusiModermpopup');
		MessageBox.alert("提示","光猫校验，宽带号码不能为空");
		return;
	}else{
		$("#check_MODEM_SERIAL_NUMBER").val(serial_number);
	}
	
}


//FTTH光猫
function queryApplyModermInfo(applyTpyeselect)
{  
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('', 'checkFTTHdeposit', "&USER_ID="+userInfo.get("USER_ID")+"&SERIAL_NUMBER="+userInfo.get("SERIAL_NUMBER")+"&APPLYTYPE="+applyTpyeselect, 'ModermApplyPart,ModermCheckPart,QueryCondPart,QueryListPart', function(rtnData) { 
		if(rtnData!=null&&rtnData.length > 0){
				$.endPageLoading();
				var x_info = rtnData.get("X_INFO");
				$("#returnDate").css('display', 'none');
				if(x_info == 0){
					applytype = $("#APPLYTYPE").val();
					//清理费用
					$.feeMgr.clearFeeList("6801","2");	
					if(applyTpyeselect == 0){
						$("#PAYTYPE").html("押金金额");
//						$("#MODERM_APPLY_TYPE option:nth-child(2)").attr("selected","selected"); //设置第二条为已选（租赁）
						$("#MODERM_APPLY_TYPE").val("0");
						$("#returnDate").css('display', '');
						var fee = $("#DEPOSIT").val();
						$.feeMgr.removeFee("6801","2","9002");
						var feeData = $.DataMap();
						feeData.clear();
						feeData.put("MODE", "2");
						feeData.put("CODE", "9002");
						feeData.put("FEE", fee*100);
						feeData.put("PAY", fee*100);
						feeData.put("TRADE_TYPE_CODE", "6801");
						$.feeMgr.insertFee(feeData);
					}else if(applyTpyeselect == 2){
						$("#REMARK").val("光猫赠送，不收取光猫押金。");
						$("#MODERM_APPLY_TYPE option:nth-child(4)").attr("selected","selected");
					}else{
						$("#PAYTYPE").html("购买金额");
//						$("#MODERM_APPLY_TYPE option:nth-child(3)").attr("selected","selected");//赠送
						$("#MODERM_APPLY_TYPE").val("2");
						$.feeMgr.clearFeeList("6801","2");
						var feeData = $.DataMap();
						var fee = $("#DEPOSIT").val();
						feeData.put("MODE", "2"); //0:营业费  1：押金 2：预存
						feeData.put("CODE", "9002");
						feeData.put("FEE",  fee*100);
						feeData.put("PAY",  fee*100);		
						feeData.put("TRADE_TYPE_CODE","6801");
						$.feeMgr.insertFee(feeData);	
					}
				}else if(x_info == 1){
					MessageBox.alert("提示","该用户已存在光猫记录，不能再次办理；如为刚开户已交押金，请在外线处理进行光猫出库。");
					$("#REMARK").val("");
					$("#APPLYTYPE").val("");
					applytype = "";
//					$("#MODERM_APPLY_TYPE option:nth-child(1)").attr("selected","selected");
					$("#MODERM_APPLY_TYPE").val("");
				}else{
					$("#REMARK").val("");
					$("#APPLYTYPE").val("");
					applytype = "";
//					$("#MODERM_APPLY_TYPE option:nth-child(1)").attr("selected","selected");
					$("#MODERM_APPLY_TYPE").val("");
				}
		}else{
			$.endPageLoading();
			MessageBox.alert("提示","程序出错，未找到数据！");
			return false; 
		}
		}, function(error_code, error_info,detail) {
			$.endPageLoading();
			MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
		}); 
} 

function checkNumber(){
	if(userInfo == "" || userInfo == null){
		MessageBox.alert("提示","请校验服务号码！");
		return false;
	}
	var user_id = userInfo.get("USER_ID");
	var serial_number = userInfo.get("SERIAL_NUMBER");
	var serial_number1 = $("#AUTH_SERIAL_NUMBER").val(); 
	if(user_id == "" || serial_number == ""){
		MessageBox.alert("提示","请校验服务号码！");
		return false;
	}
	return true;
}

function queryModermInfo(){
	if(!checkNumber()){
		return;
	}
	var user_id = userInfo.get("USER_ID");
	var serial_number = userInfo.get("SERIAL_NUMBER");
//	var modermtrade = $("#MODERM_TRADE_TYPE option:selected").val();
	var modermtrade = document.getElementById("MODERM_TRADE_TYPE").value;
	var modermId = $("#cond_MODERM_ID").val();
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('','queryModermInfo',"&USER_ID="+user_id+"&SERIAL_NUMBER="+serial_number+"&MODERM_ID="+modermId, 'QueryListPart',function(rtnData) { 
		if(rtnData!=null&&rtnData.length > 0){
			$.endPageLoading();
			if(modermtrade == 2){
				showModermCheck();
			}
		}else{
			$.endPageLoading();
			if(modermtrade == 2){
				showModermCheck();
			}
			MessageBox.alert("提示","用户没有该光猫信息！");
			return false; 
		}
	}, function(error_code, error_info,detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
	});
}


function changeApplyType(){
	if(userInfo != ""){
//		var applyTpyeselect = $("#MODERM_APPLY_TYPE option:selected").val();
		var applyTpyeselect = document.getElementById("MODERM_APPLY_TYPE").value;
		
		if(applyTpyeselect == ""){
			$("#REMARK").val("");
			$("#APPLYTYPE").val("");
			$("#DEPOSIT").val("");
			$("#PAYTYPE").html("押金金额");
			applytype = "";
		}else{
			queryApplyModermInfo(applyTpyeselect);
		}
	}
	
}

function showModermCheck(){
	$("th[hideId=insert]").css('display', '');
	$("td[hideId=insert]").css('display', '');
	$("td[hideId=insert]").attr("class","e_center");
}

function hideModermCheck(){
	$("th[hideId=insert]").css('display', '');
	$("td[hideId=insert]").attr("class","e_center e_hide");
}

function checkModermId(){
	if(!checkNumber()){
		return;
	}
	var user_id = userInfo.get("USER_ID");
	
//	var modermtrade = $("#MODERM_TRADE_TYPE option:selected").val();
	var modermtrade = document.getElementById("MODERM_TRADE_TYPE").value;
	
	var modermId = $("#check_MODERID").val();
	if(modermId == ""){
		MessageBox.alert("提示","请输入终端串码");
		return false;
	}
	var partids = "";
	if(modermtrade == 1){
		partids = "ModermCheckPart";
	}
	$.beginPageLoading("正在校验光猫信息...");
	var serial_number = userInfo.get("SERIAL_NUMBER");
	$.ajax.submit('','checkModermId',"&SERIAL_NUMBER="+serial_number+"&RES_ID="+modermId,partids,function(data) { 
		$.endPageLoading();	
		hidePopup('checkModermpopup');
//		$.closePopupDiv('checkModermpopup');
		$("#check_MODERID").val("");
		if(modermtrade == 2){
			var modermInfo = data.get(0);
			var res_no = modermInfo.get("RES_NO");
			var res_kind_code = modermInfo.get("RES_KIND_CODE");
//			var table = $.table.get("DataTable");
//			var json = table.getRowData();
			var json = DataTable.getRowData(DataTable.selected);
			json["NEW_MODERM_ID"]=res_no;
			json["NEW_MODERM_TYPE"]=res_kind_code;
//			var updaterow = table.updateRow(json);
			DataTable.updateRow(json, DataTable.selected);
		}
	},function(e,i){
		$.endPageLoading();	
		MessageBox.alert("提示",i,function(){});
//		$.closePopupDiv('checkModermpopup');
		hidePopup('checkModermpopup');
		$("#check_MODERID").val("");
	});
}

function checkNewModermId(btn){
	var modemStatus = btn.getAttribute("str2");
	if(modemStatus != 1){
		var statusMsg = "";
		if(modemStatus == 2){
			statusMsg = "更换";
		}else if(modemStatus == 3){
			statusMsg = "退还";
		}else if(modemStatus == 4){
			statusMsg = "丢失";
		}else{
			statusMsg = "非申领状态";
		}
		MessageBox.alert("提示","光猫已"+statusMsg+",不能更换，录入新光猫！");
		return;
	}
//	popupDiv('checkModermpopup','40%','光猫校验');
	showPopup('checkModermpopup','checkModermpopup_item',true);
	var oldModermId = btn.getAttribute('str1');
	$("#cond_OLD_MODERM_ID").val(oldModermId);
}


function onTradeSubmit()
{
	if(modemManageType == 1){//FTTH商务光猫管理
//		var modermtrade = $("#BUSI_MODEM_TRADE_TYPE option:selected").val();
		document.getElementById("BUSI_MODEM_TRADE_TYPE").value;
		var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
//		param += '&TRADE_TYPE_CODE='+$('#TRADE_TYPE_CODE').val();
		if(modermtrade == 1){
			var submitData = $.DatasetList();
//			var listTable=$.table.get("memberTable").getTableData(null,true);
			var listTable=memberTable.getData(true);
			var changeNum =0;
			listTable.each(function(item,index,totalCount){ 
				if(item.get("tag")=="0"){  
					submitData.add(item); 
				}
			});
			if(submitData.length ==0){
				MessageBox.alert("提示","数据未发生变化，请勿提交！");
				$.endPageLoading();
				return false;
			}
			var isBlack = true;
			submitData.each(function(item,index,totalCount){ 
				if(item.get("KD_MODEM_ID")==""){  
					MessageBox.alert("提示","请给宽带号码为"+item.get("KD_NUMBER")+"的用户录入光猫串号！");
					isBlack = false;
					return false;
				}
			});
			if(!isBlack){
				return;
			}
			param += "&FTTH_DATASET="+submitData.toString();
			param=param.replace(/%/g,"%25");
		}else if(modermtrade == 2){
			var modemCheckStatus = checkBusiModemStatus(modermtrade);
			if(!modemCheckStatus){
				return;
			}
			var checkedData = getCheckedTableData("busiModemTable",null,"monitorids");
			var checkedDataMap = checkedData.get(0);
			var inst_id = checkedDataMap.get('INST_ID');
			var new_moderm_id = checkedDataMap.get('NEW_MODERM_ID');
			var new_moderm_type = checkedDataMap.get('NEW_MODERM_TYPE');
			if(new_moderm_id == "" || $.format.undef(new_moderm_id) == ""){
				MessageBox.alert("提示","请录入更换的光猫串号信息！");
				return;
			}
			param += '&INST_ID='+inst_id;
			param += '&NEW_MODERM_ID='+new_moderm_id;
			param += '&NEW_MODERM_TYPE='+new_moderm_type;
		}else{
			var modemCheckStatus = checkBusiModemStatus(modermtrade);
			if(!modemCheckStatus){
				return;
			}
			var checkedData = getCheckedTableData("busiModemTable",null,"monitorids");
			var checkedDataMap = checkedData.get(0);
			var inst_id = checkedDataMap.get('INST_ID');
			param += '&INST_ID='+inst_id;
		}
		$.cssubmit.addParam(param); 
		return true;
	}else if(modemManageType == 2){//FTTH光猫管理
//		var modermtrade = $("#MODERM_TRADE_TYPE option:selected").val();
		var modermtrade = document.getElementById("MODERM_TRADE_TYPE").value;
		var param = '&SERIAL_NUMBER='+$("#NOPHONE_SERIAL_NUMBER").val();//传宽带账号
//		param += '&TRADE_TYPE_CODE='+$('#TRADE_TYPE_CODE').val();
		if(modermtrade == 1){
			if(applytype == ""){
				MessageBox.alert("提示","请选择申领模式!");
				return;
			}
//			var apply_type = $("#MODERM_APPLY_TYPE option:selected").val();
			var apply_type = document.getElementById("MODERM_APPLY_TYPE").value;
			
			if($('#cond_APPLYMODERMID').val() == ""){
				MessageBox.alert("提示","请录入光猫串号！");
				return;
			}
			if(applytype == 0){//租赁光猫
				if(!$.validate.verifyAll("ModermApplyPart")){
					return;
				}
				param += '&RETURN_DATE='+$('#RETURN_DATE').val();
			}
			param += '&REMARK='+$('#REMARK').val();
			param += '&APPLY_TYPE='+apply_type;
			param += '&DEPOSIT='+$('#DEPOSIT').val();
			param += '&MODERMTYPE='+$('#RES_KIND_CODE').val();
			param += '&MODERMID='+$('#cond_APPLYMODERMID').val();
			$.cssubmit.addParam(param);
			return true;
		}else if(modermtrade == 2){
			var modemCheckStatus = checkModemStatus(modermtrade);
			if(!modemCheckStatus){
				return;
			}
			var checkedData = getCheckedTableData("DataTable",null,"monitorids");
			var checkedDataMap = checkedData.get(0)
			var inst_id = checkedDataMap.get('INST_ID');
			var new_moderm_id = checkedDataMap.get('NEW_MODERM_ID');
			var new_moderm_type = checkedDataMap.get('NEW_MODERM_TYPE');
			if(new_moderm_id == "" || $.format.undef(new_moderm_id) == ""){
				MessageBox.alert("提示","请录入更换的光猫串号信息！");
				return;
			}
			param += '&INST_ID='+inst_id;
			param += '&NEW_MODERM_ID='+new_moderm_id;
			param += '&NEW_MODERM_TYPE='+new_moderm_type;
			$.cssubmit.addParam(param);
			return true;
		}else{
			var modemCheckStatus = checkModemStatus(modermtrade);
			if(!modemCheckStatus){
				return;
			}
			var checkedData = getCheckedTableData("DataTable",null,"monitorids");
			var checkedDataMap = checkedData.get(0);
			var modemApplyType = checkedDataMap.get('APPLY_TYPE');
			if(modemApplyType != "租赁"){
				var msg = "";
				if(modermtrade == 3){
					msg = "退还";
				}else{
					msg = "丢失";
				}
				if(modemApplyType == ""){
					modemApplyType = "非租赁";
				}
				MessageBox.alert("提示",modemApplyType+"的光猫不允许"+msg+"操作！");
				return;
			}
			var modemDeposit = checkedDataMap.get('DEPOSIT');
			MessageBox.confirm("确认提示", "系统将提交受理数据信息，您总共需要给用户退费"+modemDeposit+"元，确认要继续吗？", function(btn){
				if(btn == "ok"){
					var inst_id = checkedDataMap.get('INST_ID');
					param += '&INST_ID='+inst_id;
					$.cssubmit.addParam(param);
					$.cssubmit.submitTrade();
				}else{
					return false;
				}
			});
		}
	}else{
		MessageBox.alert("提示","请校验服务号码");
		return;
	}
}

function checkModemStatus(modermtrade){
	var msg = "";
	if(modermtrade == 2){
		msg = "更换";
	}else if(modermtrade == 3){
		msg = "退还";
	}else if(modermtrade == 4){
		msg = "丢失";
	}
	var checkedData = getCheckedTableData("DataTable",null,"monitorids");
	var listSize = checkedData.getCount();
	if(listSize > 1){
		MessageBox.alert("提示","一次只能操作一个光猫"+msg+"！");
		return false;
	}
	if(listSize < 1){
		MessageBox.alert("提示","请选择需要"+msg+"的光猫！");
		return false;
	}
	var checkedDataMap = checkedData.get(0);
	var modemStatus = checkedDataMap.get('MODERM_STATUS');
	var statusMsg = "";
	if(modemStatus != "申领"){
		if(modemStatus == ""){
			modemStatus = "不是申领状态";
		}
		MessageBox.alert("提示","该用户此光猫已"+modemStatus+"，不能再做"+msg+"操作");
		return false;
	}
	return true;
}

function changeModermtrade(){
	var table = DataTable.getData(true);
	$("#tbdoyHide").html("");
//	table.cleanRows();
//	var modermtrade = $("#MODERM_TRADE_TYPE option:selected").val();
	var modermtrade = document.getElementById("MODERM_TRADE_TYPE").value;
	$("#modermapplyinfo").css('display', 'none');
	$("#modermInfo").css('display', 'none');
	hideModermCheck();
	$("#oldModermId").attr("class","li e_hide");
	$("#selApplyType").attr("class","li");
	//清理费用
	$.feeMgr.clearFeeList("9711","0");
//	$("#MODERM_APPLY_TYPE option:nth-child(1)").attr("selected","selected");
	$("#MODERM_APPLY_TYPE").val("");
	DataTable.adjust();
	if(modermtrade == 1){
		$("#TRADE_TYPE_CODE").val("6801");
		$("#modermapplyinfo").css('display', '');
	}else if(modermtrade == 2){
		$("#TRADE_TYPE_CODE").val("6802");
		$("#modermInfo").css('display', '');
		$("#oldModermId").attr("class","li");
		$("#selApplyType").attr("class","li e_hide");
		showModermCheck();
	}else if(modermtrade == 3){
		$("#TRADE_TYPE_CODE").val("6803");
		$("#modermInfo").css('display', '');
		$("#selApplyType").attr("class","li e_hide");
	}else if(modermtrade == 4){
		$("#TRADE_TYPE_CODE").val("6804");
		$("#modermInfo").css('display', '');
		$("#selApplyType").attr("class","li e_hide");
	}
	$.ajax.submit('', 'checkOperType', "&SERIAL_NUMBER="+userInfo.get("SERIAL_NUMBER")+"&OPER_TYPE="+modermtrade, '', function(rtnData) {
		null;
	}, function(error_code, error_info,detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
	}); 
}


function getCheckedTableData(tbName, g, chekboxName)
{
	var b = new Wade.DatasetList();
	if(tbName == "busiModemTable"){
		var c = busiModemTable.getData(true);
	}
	if(tbName == "DataTable"){
		var c = DataTable.getData(true);
	}
//	var d = Wade.table.get(tbName);
	var checkboxname = chekboxName;
//	var c = Wade("tbody", d.getTable()[0]);
//	var e = d.tabHeadSize;
	if (c)
	{
		for(var i=0;i < c.length; i++){
			var isChecked = $('input[name=' + checkboxname + ']').attr("checked");
			if (isChecked) 
			{
				var j = null;
				if(tbName == "busiModemTable"){
					 j = busiModemTable.getRowData( i,g);
				}
				if(tbName == "DataTable"){
					 j = DataTable.getRowData( i,g);
				}
//				var j = d.getRowData(g, h + e);
				if (j)
				{
						b.add(j);
				}
			}
			j = null;
		}
	}
	c = null;
	d = null;
	return b;
}