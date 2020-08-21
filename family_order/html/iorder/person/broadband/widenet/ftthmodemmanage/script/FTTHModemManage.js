/**
 * REQ201510270009 FTTH光猫申领押金金额显示优化【2015业务挑刺】
 * 新增显示押金金额
 * */
var userInfo = "";
var checkDeposit = "";
var applytype = "";
var modemManageType = "";
//var checkModenIDIsExistFlag = true;
var HWReturnCode = "";
var checkModenIDIsExistID = "";
function refreshPartAtferAuth(data){
    $.beginPageLoading("查询校验...");
	userInfo = data.get("USER_INFO");
	$("#FTTHBusiModemManage").attr("class","e_hide");
	$("#FTTHModemManage").attr("class","");
	$.ajax.submit('', 'checkFTTHBusi', "&USER_ID="+userInfo.get("USER_ID")+"&SERIAL_NUMBER="+userInfo.get("SERIAL_NUMBER"), 'ModermApplyPart,ModermCheckPart,QueryCondPart,QueryListPart,busiModemInfoPart,memberSnPart,EditPart', function(rtnData) {

		if(rtnData!=null&&rtnData.length > 0){//该用户不存在7341 集团商务宽带产品，不能办理FTTH商务宽带申领
		//	MessageBox.error("错误提示", rtnData.get("RTNMSG"), $.auth.reflushPage, null, null, null);
		//	queryApplyModermInfo("");
            setValByIndex("MODERM_APPLY_TYPE","0");//$("#MODERM_APPLY_TYPE option:nth-child(1)").attr("selected","selected");
            setValByIndex("MODERM_TRADE_TYPE","1");//$("#MODERM_TRADE_TYPE option:nth-child(1)").attr("selected","selected");
			changeModermtrade();
			modemManageType = "2";
			$("#TRADE_TYPE_CODE").val("6131");
			$.endPageLoading();
			if(rtnData.get("CHECK_CODE") == "0"){//有未完工工单，并且申领了光猫,提示用户
				MessageBox.confirm("确认提示",rtnData.get("CHECK_INFO")+",业务是否继续？",function(btn){
					if(btn == "ok"){

					}else{
						$.redirect.toPage("personserv","broadband.wideband.FTTHModemManage","initApplyType",'');
					}
				});
			}
			return false;
		}else{
            memberTable.adjust();
			modemManageType = "1";
            setValByIndex("BUSI_MODEM_TRADE_TYPE","1");//$("#BUSI_MODEM_TRADE_TYPE option:nth-child(1)").attr("selected","selected");
			$("#TRADE_TYPE_CODE").val("6132");
			$("#EditPart").removeClass("e_dis");
			$("#EditPart").attr("disabled",false);
			$("#addbtn").attr("disabled",false);
			$("#delbtn").attr("disabled",false);
			$("#CUST_ID").val(userInfo.get("CUST_ID"));
			$("#FTTHBusiModemManage").attr("class","");
			$("#FTTHModemManage").attr("class","e_hide");
            $("#modermapplyinfo").attr("class","e_hide");
			$.endPageLoading();
		}
	}, function(error_code, error_info,detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
	});


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

//FTTH商务光猫


function changeBusiModermtrade(){
	// var table = $.table.get("busiModemTable");
	// table.cleanRows();
    // document.getElementById('fmymembers').innerHTML="";
    $("#fmymembers").html("");
    // $("#fmymembers").innerHTML="";
	var modermtrade = $("#BUSI_MODEM_TRADE_TYPE").val();
	hideModermCheck();
	$("#busimodermapplyinfo").attr("class","");
	$("#busimodermInfo").attr("class","e_hide");
	$("#oldBusiModermId").attr("class","li e_hide");
	if(modermtrade == 1){
        memberTable.adjust();
		$("#TRADE_TYPE_CODE").val("6132");
	}else if(modermtrade == 2){
        busiModemTable.adjust();
		$("#TRADE_TYPE_CODE").val("6133");
		$("#busimodermapplyinfo").attr("class","e_hide");
		$("#busimodermInfo").attr("class","");
		$("#oldBusiModermId").attr("class","li");
		showModermCheck();
	}else if(modermtrade == 3){
        busiModemTable.adjust();
		$("#TRADE_TYPE_CODE").val("6134");
		$("#busimodermapplyinfo").attr("class","e_hide");
		$("#busimodermInfo").attr("class","");
	}else if(modermtrade == 4){
        busiModemTable.adjust();
		$("#TRADE_TYPE_CODE").val("6135");
		$("#busimodermapplyinfo").attr("class","e_hide");
		$("#busimodermInfo").attr("class","");
	}
}


/** 新增一条记录，对应表格新增按钮 */
function addItem() {
	/* 校验所有的输入框 */
	var kd_number=$("#KD_NUMBER").val();
	if(kd_number==""||kd_number==null){
		MessageBox.alert("请先录入宽带号码！");
		$("#KD_NUMBER").focus();
		return false;
	}
	var cust_id=$("#CUST_ID").val();
	//获取编辑区的数据
	var editData = $.ajax.buildJsonData("editPart");
	editData['KD_NUMBER']=kd_number;
	for(var i=0;i<memberTable.getData("KD_NUMBER").length;i++) {
        if(memberTable.getData("KD_NUMBER")[i].get("KD_NUMBER") == kd_number) {
            MessageBox.alert("该宽带号码已经存在列表中,请重新输入！");
            $("#KD_NUMBER").val("");
            $("#KD_NUMBER").focus();
            return false;
		}
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
				var editMap = $.parseJSON($.stringifyJSON(editData));
                memberTable.addRow(editMap);
                $.each($("#fmymembers1 tr"),function (index,data) {
                	if(index == $("#fmymembers1 tr").length-1) {
                        $(this).children("td:last-child").html("<button class='e_button-center e_button-blue e_button-form' str3 = "+kd_number+" jwcid='@Any' onclick='checkApplyBusiModem(this)'><i class='e_ico-bottom' /><span>光猫录入</span></button>");
                    }
                });
				// var newRow = $.table.get("memberTable").addRow(editData);
				// newRow.append({tag:"td",style:"class:e_center",html:"<button class='e_button-center e_button-form' str3 = '"+kd_number+"' jwcid='@Any' onclick='checkApplyBusiModem(this)'><i class='e_ico-bottom' /><span>光猫录入</span></button>"});
				$("#KD_NUMBER").val("");
			}else{
				MessageBox.alert(rtnData.get("RTNMSG"));
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

    checkBusiModermpopup.show();
	var serial_number = btn.getAttribute('str3');
	if(serial_number == ""){
		// $.closePopupDiv('checkBusiModermpopup');
        checkBusiModermpopup.hide();
		MessageBox.alert("光猫校验，宽带号码不能为空");
		return;
	}else{
		$("#check_MODEM_SERIAL_NUMBER").val(serial_number);
	}
}
/** 删除一条记录，对应表格删除按钮 */
function delItem() {
	// var rowData = $.table.get("memberTable").getRowData();
	if (memberTable.selected == undefined) {
		MessageBox.alert("请您选择记录后再进行删除操作！");
		return false;
	}
	if(confirm("您确认要删除宽带号码【"+ memberTable.getRowData(memberTable.selected).get("KD_NUMBER")+"】？")){
		memberTable.deleteRow(memberTable.selected);
	}

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
    // if(busiModemTable.selected == null){
    //     MessageBox.alert("一次只能操作一个光猫"+msg+"！");
    //     return false;
    // }
    if(busiModemTable.selected == null){
        MessageBox.alert("请选择需要"+msg+"的光猫！");
        return false;
    }
	var checkedData = busiModemTable.getRowData(busiModemTable.selected);//getCheckedTableData("busiModemTable",null,"monitorids");
	var modemStatus = checkedData.get('MODERM_STATUS');
	var statusMsg = "";
	if(modemStatus != "申领"){
		if(modemStatus == ""){
			modemStatus = "不是申领状态";
		}
		MessageBox.alert("该用户此光猫已"+modemStatus+"，不能再做"+msg+"操作");
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
		MessageBox.alert("请输入终端串码");
		return false;
	}
	$.beginPageLoading("正在校验光猫信息...");
	var serial_number = $("#check_MODEM_SERIAL_NUMBER").val();
	$.ajax.submit('','checkModermId',"&SERIAL_NUMBER="+serial_number+"&RES_ID="+modermId,'',function(data) {
		$.endPageLoading();
		// $.closePopupDiv('checkBusiModermpopup');
        checkBusiModermpopup.hide();
		$("#check_BUSI_MODEMID").val("");
			var modermInfo = data.get(0);
			var res_no = modermInfo.get("RES_NO");
			var res_kind_code = modermInfo.get("RES_KIND_CODE");
			var modermtrade = $("BUSI_MODEM_TRADE_TYPE").val();//$("#BUSI_MODEM_TRADE_TYPE option:selected").val();
			var table = null;
			if(modermtrade == 1){
				table = $.table.get("memberTable");
			}else if(modermtrade == 2){
				table = $.table.get("busiModemTable");
			}
			var json = table.getRowData();
			if(modermtrade == 1){
				json["KD_MODEM_ID"]=res_no;
				json["KD_MODEM_TYPE"]=res_kind_code;
			}else if(modermtrade == 2){
				json["NEW_MODERM_ID"]=res_no;
				json["NEW_MODERM_TYPE"]=res_kind_code;
			}
			var updaterow = table.updateRow(json);
	},function(e,i){
		$.endPageLoading();
		MessageBox.alert("提示",i,function(){});
		//$.closePopupDiv('checkModermpopup');
		checkModermpopup.hide();
		$("#check_MODERID").val("");
	});
}

function queryBusiModermInfo(){
	if(!checkNumber()){
		return;
	}
	var modermtrade = $("#BUSI_MODEM_TRADE_TYPE").val();//$("#BUSI_MODEM_TRADE_TYPE option:selected").val();
	var modermId = $("#cond_BUSI_MODERM_ID").val();
	var kdNumber = $("#cond_KD_NUMBER").val();
	if(kdNumber == ""){
		MessageBox.alert("请输入宽带号码!");
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
			MessageBox.alert("用户没有该光猫信息！");
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
		MessageBox.alert("光猫已"+statusMsg+",不能更换，录入新光猫！");
		return;
	}
	// popupDiv('checkBusiModermpopup','40%','光猫校验');
    checkBusiModermpopup.show();
	var oldModermId = btn.getAttribute('str1');
	$("#cond_BUSI_OLD_MODERM_ID").val(oldModermId);
	var serial_number = btn.getAttribute('str3');
	if(serial_number == ""){
		// $.closePopupDiv('checkBusiModermpopup');
        checkBusiModermpopup.hide();
		MessageBox.alert("光猫校验，宽带号码不能为空");
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
				$("#returnDate").attr("class","li e_hide");
				if(x_info == 0){
					applytype = $("#APPLYTYPE").val();
					//清理费用
					$.feeMgr.clearFeeList("9711","0");
					if(applyTpyeselect == 0){
						$("#PAYTYPE").html("押金金额：");
						// $("#MODERM_APPLY_TYPE option:nth-child(2)").attr("selected","selected");
						setValByIndex("MODERM_APPLY_TYPE","1")
						$("#returnDate").attr("class","li");
					}else if(applyTpyeselect == 2){
						$("#REMARK").val("光猫赠送，不收取光猫押金。");
						// $("#MODERM_APPLY_TYPE option:nth-child(4)").attr("selected","selected");
                        setValByIndex("MODERM_APPLY_TYPE","3")
					}else{
						$("#PAYTYPE").html("购买金额：");
						// $("#MODERM_APPLY_TYPE option:nth-child(3)").attr("selected","selected");
                        setValByIndex("MODERM_APPLY_TYPE","2")
						$.feeMgr.clearFeeList("9711","0");
						var feeData = $.DataMap();
						var fee = $("#DEPOSIT").val();
						feeData.put("MODE", "0"); //0:营业费  1：押金 2：预存
						feeData.put("CODE", "9205");
						feeData.put("FEE",  fee*100);
						feeData.put("PAY",  fee*100);
						feeData.put("TRADE_TYPE_CODE","9711");
						$.feeMgr.insertFee(feeData);
					}
				}else if(x_info == 1){
					MessageBox.alert("该用户已租赁了一个光猫，不能再次租赁!!");
					$("#REMARK").val("");
					$("#APPLYTYPE").val("");
					applytype = "";
					// $("#MODERM_APPLY_TYPE option:nth-child(1)").attr("selected","selected");
                    setValByIndex("MODERM_APPLY_TYPE","0")
				}else{
					$("#REMARK").val("");
					$("#APPLYTYPE").val("");
					applytype = "";
					// $("#MODERM_APPLY_TYPE option:nth-child(1)").attr("selected","selected");
                    setValByIndex("MODERM_APPLY_TYPE","0")
				}
		}else{
			$.endPageLoading();
			MessageBox.alert("程序出错，未找到数据！");
			return false;
		}
		}, function(error_code, error_info,detail) {
			$.endPageLoading();
			MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
		});
}

function checkNumber(){
	if(userInfo == "" || userInfo == null){
		MessageBox.alert("请校验服务号码！");
		return false;
	}
	var user_id = userInfo.get("USER_ID");
	var serial_number = userInfo.get("SERIAL_NUMBER");
	var serial_number1 = $("#AUTH_SERIAL_NUMBER").val();
	if(serial_number != serial_number1){
		MessageBox.alert("服务号码与校验号码不相同！");
		return false;
	}
	if(user_id == "" || serial_number == ""){
		MessageBox.alert("请校验服务号码！");
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
	var modermtrade = $("#MODERM_TRADE_TYPE").val();//$("#MODERM_TRADE_TYPE option:selected").val();
	var modermId = $("#cond_MODERM_ID").val();
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('','queryModermInfo',"&USER_ID="+user_id+"&SERIAL_NUMBER="+serial_number+"&MODERM_ID="+modermId, 'QueryListPart',function(rtnData) {
		if(rtnData!=null&&rtnData.length > 0){
			$.endPageLoading();
			if(modermtrade == 2||modermtrade == 5){
				showModermCheck();
			}
		}else{
			$.endPageLoading();
			if(modermtrade == 2||modermtrade == 5){
				showModermCheck();
			}
			MessageBox.alert("用户没有该光猫信息！");
			return false;
		}
	}, function(error_code, error_info,detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
	});
}


function changeApplyType(){
	if(userInfo != ""){
		var applyTpyeselect = $("#MODERM_APPLY_TYPE").val();
		if(applyTpyeselect == ""){
			$("#REMARK").val("");
			$("#APPLYTYPE").val("");
			$("#DEPOSIT").val("");
			$("#PAYTYPE").html("押金金额：");
			applytype = "";
		}else{
			queryApplyModermInfo(applyTpyeselect);
		}
	}

}

function showModermCheck(){
	$("th[hideId=insert]").attr("class","");
	$("td[hideId=insert]").attr("class","e_center");
}

function hideModermCheck(){
	$("th[hideId=insert]").attr("class","e_hide");
	$("td[hideId=insert]").attr("class","e_center e_hide");
}

function checkModermId(){
	if(!checkNumber()){
		return;
	}
	var user_id = userInfo.get("USER_ID");
	var modermtrade = $("#MODERM_TRADE_TYPE").val();//$("#MODERM_TRADE_TYPE option:selected").val();
	var modermId = $("#check_MODERID").val();
	if(modermId == ""){
		MessageBox.alert("请输入终端串码");
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
		// $.closePopupDiv('checkModermpopup');
		checkModermpopup.hide();
		$("#check_MODERID").val("");
		if(modermtrade == 2||modermtrade == 5){
			var modermInfo = data.get(0);
			var res_no = modermInfo.get("RES_NO");
			var res_kind_code = modermInfo.get("RES_KIND_CODE");
			//获取下标为0的行
			// var table = $.table.get("DataTable");
			var json = DataTable.getRowData(0);
			json["NEW_MODERM_ID"]=res_no;
			json["NEW_MODERM_TYPE"]=res_kind_code;
			var updaterow = DataTable.updateRow(json,0);
		}
	},function(e,i){
		$.endPageLoading();
		MessageBox.alert("提示",i,function(){});
		// $.closePopupDiv('checkModermpopup');
		checkModermpopup.hide();
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
		MessageBox.alert("光猫已"+statusMsg+",不能更换，录入新光猫！");
		return;
	}
	var modermtrade = $("#MODERM_TRADE_TYPE").val();
	var param = "&SERIAL_NUMBER="+userInfo.get("SERIAL_NUMBER")+"&USER_ID="+userInfo.get("USER_ID")+"&INST_ID="+btn.getAttribute('str20')
	+"&OPER_TYPE="+modermtrade+"&MODERM_ID="+btn.getAttribute('str1')+"&MODEM_TYPE="+btn.getAttribute('str6');
	if(modermtrade == 5){
		$.ajax.submit('', 'checkModermUp', param , '', function(rtnData) {
			if("1"!=rtnData.get("UP_TAG")){
				 MessageBox.alert("该光猫设备不符合，不能进行光猫升级！");
				 return;
			}
			checkModermpopup.show();
		}, function(error_code, error_info,detail) {
			$.endPageLoading();
			MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
		});
	}else{
		checkModermpopup.show();
	}
	
	//popupDiv('checkModermpopup','40%','光猫校验');
    //checkModermpopup.show();
	var oldModermId = btn.getAttribute('str1');
	$("#cond_OLD_MODERM_ID").val(oldModermId);
}


function onTradeSubmit()
{
    if(modemManageType == 1){//FTTH商务光猫管理
        var modermtrade = $("#BUSI_MODEM_TRADE_TYPE").val();//var modermtrade = $("#BUSI_MODEM_TRADE_TYPE option:selected").val();
        var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
        // param += '&TRADE_TYPE_CODE='+$('#TRADE_TYPE_CODE').val();
        if(modermtrade == 1){
            var submitData = $.DatasetList();
            var listTable=$.table.get("memberTable").getTableData(null,true);
            var changeNum =0;
            listTable.each(function(item,index,totalCount){
                if(item.get("tag")=="0"){
                    submitData.add(item);
                }
            });
            if(submitData.length ==0){
                MessageBox.alert("数据未发生变化，请勿提交！");
                $.endPageLoading();
                return false;
            }
            var isBlack = true;
            submitData.each(function(item,index,totalCount){
                if(item.get("KD_MODEM_ID")==""){
                    MessageBox.alert("请给宽带号码为"+item.get("KD_NUMBER")+"的用户录入光猫串号！");
                    isBlack = false;
                    return false;
                }
            });
            if(!isBlack){
                return;
            }
            param += "&FTTH_DATASET="+submitData.toString();
            param=param.replace(/%/g,"%25");
        }else if(modermtrade == 2||modermtrade == 5){
            var modemCheckStatus = checkBusiModemStatus(modermtrade);
            if(!modemCheckStatus){
                return;
            }
            // var checkedData = getCheckedTableData("busiModemTable",null,"monitorids");
            // var checkedDataMap = checkedData.get(0)
            var checkedDataMap = busiModemTable.getRowData(busiModemTable.selected);
            var inst_id = checkedDataMap.get('INST_ID');
            var new_moderm_id = checkedDataMap.get('NEW_MODERM_ID');
            var new_moderm_type = checkedDataMap.get('NEW_MODERM_TYPE');
            if(new_moderm_id == "" || $.format.undef(new_moderm_id) == ""){
            	if(modermtrade == 5){
            		 MessageBox.alert("请录入要升级的光猫串号信息！");
                     return;
            	}else{
            		 MessageBox.alert("请录入更换的光猫串号信息！");
                     return;
            	}
            }
            param += '&INST_ID='+inst_id;
            param += '&NEW_MODERM_ID='+new_moderm_id;
            param += '&NEW_MODERM_TYPE='+new_moderm_type;
        }else{
            var modemCheckStatus = checkBusiModemStatus(modermtrade);
            if(!modemCheckStatus){
                return;
            }
            // var checkedData = getCheckedTableData("busiModemTable",null,"monitorids");
            // var checkedDataMap = checkedData.get(0);
            var checkedDataMap = busiModemTable.getRowData(busiModemTable.selected);
            var inst_id = checkedDataMap.get('INST_ID');
            param += '&INST_ID='+inst_id;
        }
        $.cssubmit.addParam(param);
        return true;
    }else if(modemManageType == 2){//FTTH光猫管理
        var modermtrade = $("#MODERM_TRADE_TYPE").val();//var modermtrade = $("#MODERM_TRADE_TYPE option:selected").val();
        var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
        // param += '&TRADE_TYPE_CODE='+$('#TRADE_TYPE_CODE').val();
        if(modermtrade == 1){
            if(applytype == ""){
                MessageBox.alert("请选择申领模式!");
                return;
            }
            var apply_type = $("#MODERM_APPLY_TYPE").val();//var apply_type = $("#MODERM_APPLY_TYPE option:selected").val();
            if($('#cond_APPLYMODERMID').val() == ""){
                MessageBox.alert("请录入光猫串号！");
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
        }else if(modermtrade == 2){
            var modemCheckStatus = checkModemStatus(modermtrade);
            if(!modemCheckStatus){
                return;
            }
            // var checkedData = getCheckedTableData("DataTable",null,"monitorids");
            // var checkedDataMap = checkedData.get(0)
            var checkedDataMap = DataTable.getCheckedRowsData('monitorids')[0];
            var inst_id = checkedDataMap.get('INST_ID');
            var new_moderm_id = checkedDataMap.get('NEW_MODERM_ID');
            var new_moderm_type = checkedDataMap.get('NEW_MODERM_TYPE');
            if(new_moderm_id == "" || $.format.undef(new_moderm_id) == ""){
                MessageBox.alert("请录入更换的光猫串号信息！");
                return;
            }
            param += '&INST_ID='+inst_id;
            param += '&NEW_MODERM_ID='+new_moderm_id;
            param += '&NEW_MODERM_TYPE='+new_moderm_type;
        }else if(modermtrade == 5){
            var checkedDataMap = DataTable.getCheckedRowsData('monitorids')[0];
            var inst_id = checkedDataMap.get('INST_ID');
            var new_moderm_id = checkedDataMap.get('NEW_MODERM_ID');
            var new_moderm_type = checkedDataMap.get('NEW_MODERM_TYPE');
            if(new_moderm_id == "" || $.format.undef(new_moderm_id) == ""){
                MessageBox.alert("请录入升级的光猫串号信息！");
                return;
            }
            param += '&INST_ID='+inst_id;
            param += '&NEW_MODERM_ID='+new_moderm_id;
            param += '&NEW_MODERM_TYPE='+new_moderm_type;
        }else{
            var modemCheckStatus = checkModemStatus(modermtrade);
            if(!modemCheckStatus){
                return;
            }
            // var checkedData = getCheckedTableData("DataTable",null,"monitorids");
            // var checkedDataMap = checkedData.get(0);
            var checkedDataMap = DataTable.getCheckedRowsData('monitorids')[0];
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
                if(modemApplyType != "赠送"){
                	MessageBox.alert(modemApplyType+"的光猫不允许"+msg+"操作！");
                    return;
                }
                
            }
            var inst_id = checkedDataMap.get('INST_ID');
            param += '&INST_ID='+inst_id;
        }
        $.cssubmit.addParam(param);
        return true;
    }else{
        MessageBox.alert("请校验服务号码");
        return;
    }
}
// REQ201703230019__光猫归还提交前增加校验规则
function checkModermIdExist(obj) {
	var modermTradeType = "";
	if(modemManageType == 1){//FTTH商务光猫管理
		modermTradeType = $("#BUSI_MODEM_TRADE_TYPE").val();//$("#BUSI_MODEM_TRADE_TYPE option:selected").val();
	}else if(modemManageType == 2){
		modermTradeType = $("#MODERM_TRADE_TYPE").val();//$("#MODERM_TRADE_TYPE option:selected").val();
	}

	if (modermTradeType == '3') {//退还
		var moderId = obj.getAttribute("selectModermId");
		var paramcheck = "&SERIAL_NUMBER=" + $("#AUTH_SERIAL_NUMBER").val()
				+ "&RES_ID=" + moderId;
		//checkModenIDIsExistFlag = true;
		checkModenIDIsExistID = moderId;
		$.beginPageLoading("光猫串号终端信息校验中。。。");
		$.ajax.submit(null, 'checkModermId1', paramcheck, '', function(data) {
			$.endPageLoading();
			var modermInfo = data.get(0);
			var resultCode = modermInfo.get("X_RESULTCODE");
			var resultInfo = modermInfo.get("X_RESULTINFO");
			 // 1:无在库信息，无销售信息      2:在库，无销售信息       3:数据齐全        4:数据有误
			HWReturnCode = resultCode;
/*			if (resultCode == '1'||resultCode == '2') {
				//if (resultInfo.indexOf('不存在或已售出') != -1) {// 因华为接口没有返回特定的说明码代表不存在，仅能通过提示语内容判断。返回的原样说明内容：该终端串号：201512070006 // 不存在或已售出
					checkModenIDIsExistFlag = false;
				//}
			}else if (resultCode == '4') {//需要跟吴坚确认是否允许继续办理
				checkModenIDIsExistFlag = false;
			}*/
		}, function(e, i) {
			$.endPageLoading();
			MessageBox.alert("提示", i, function() {
			});
		});

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
    if(DataTable.getCheckedRowsData('monitorids') == null){
        MessageBox.alert("请选择需要"+msg+"的光猫！");
        return false;
    }
    if(DataTable.getCheckedRowsData('monitorids')){
    	if(DataTable.getCheckedRowsData('monitorids').length>1) {
            MessageBox.alert("一次只能操作一个光猫"+msg+"！");
            return false;
		}
    }
    // if(DataTable.selected > 0){
    //     MessageBox.alert("一次只能操作一个光猫"+msg+"！");
    //     return false;
    // }
	// var checkedData = getCheckedTableData("DataTable",null,"monitorids");
	// var listSize = checkedData.getCount();
	// if(listSize > 1){
	// 	MessageBox.alert("一次只能操作一个光猫"+msg+"！");
	// 	return false;
	// }
    //
	// var checkedDataMap = checkedData.get(0);
    var checkedDataMap = DataTable.getCheckedRowsData('monitorids')[0];
	var modemStatus = checkedDataMap.get('MODERM_STATUS');
	var statusMsg = "";
	if(modemStatus != "申领"){
		if(modemStatus == ""){
			modemStatus = "不是申领状态";
		}
		MessageBox.alert("该用户此光猫已"+modemStatus+"，不能再做"+msg+"操作");
		return false;
	}
	return true;
}

function changeModermtrade(){
	//暂时屏蔽
	// var table = $.table.get("DataTable");
	// table.cleanRows();
    // document.getElementById('tbdoyHide').innerHTML="";
	$("#tbdoyHide").html("");
	var modermtrade = $("#MODERM_TRADE_TYPE").val();
	$("#modermapplyinfo").attr("class","e_hide");
	$("#modermInfo").attr("class","e_hide");
	hideModermCheck();
	$("#oldModermId").attr("class","li e_hide");
	$("#selApplyType").attr("class","li");
	//清理费用
	$.feeMgr.clearFeeList("9711","0");
	//第一个元素被选到
    setValByIndex("MODERM_APPLY_TYPE","0");
	changeApplyType();
	if(modermtrade == 1){
		$("#TRADE_TYPE_CODE").val("6131");
		$("#modermapplyinfo").attr("class","");
        DataTable.adjust();
	}else if(modermtrade == 2){
		$("#TRADE_TYPE_CODE").val("7132");
		$("#modermInfo").attr("class","");
        DataTable.adjust();
		$("#oldModermId").attr("class","li");
		$("#selApplyType").attr("class","li e_hide");
		showModermCheck();
	}else if(modermtrade == 3){
		$("#TRADE_TYPE_CODE").val("7133");
		$("#modermInfo").attr("class","");
        DataTable.adjust();
		$("#selApplyType").attr("class","li e_hide");
	}else if(modermtrade == 4){
		$("#TRADE_TYPE_CODE").val("7134");
		$("#modermInfo").attr("class","");
        DataTable.adjust();
		$("#selApplyType").attr("class","li e_hide");
	}else if(modermtrade == 5){
		$("#TRADE_TYPE_CODE").val("7135");
		$("#modermInfo").attr("class","");
        DataTable.adjust();
		$("#oldModermId").attr("class","li");
		$("#selApplyType").attr("class","li e_hide");
		showModermCheck();
	}
	$.ajax.submit('', 'checkOperType', "&SERIAL_NUMBER="+userInfo.get("SERIAL_NUMBER")+"&OPER_TYPE="+modermtrade, '', function(rtnData) {

	}, function(error_code, error_info,detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
	});
	
	
}


function getCheckedTableData(tbName, g, chekboxName)
{
	var b = new $.DatasetList();
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
}
//segment根据下标设值
function setValByIndex(id,index) {
	//如果传入的下标为0，则默认都不选
	if(index == 0) {
		$("#"+id+"_span span").removeClass("e_segmentOn");
		$("#"+id).val("");
	}
	else if(index>0) {
        var actIndex=index-1;
        //判断传入的下标是否大于最大值
        var maxIndex = $("#"+id+"_span span:last").attr("idx");
        if(actIndex<=maxIndex) {
            //根据传入的下标获取到值
            var value = $("#"+id+"_span span[idx="+actIndex+"]").attr("val");
            $("#"+id).val(value);
        }
	}
}