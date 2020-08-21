//中测 add
var check_ask_id="";//编号
var check_url="";   //路径（密码验证）
var check_ask_id_temp="";
var check_menu_name="";
function queryApplyTrade(){
	debugger;
    var start_Date = $("#cond_START_DATE").val();
	var end_Date = $("#cond_END_DATE").val();
	var menu_Id = $("#cond_MENU_ID").val();
	var state = $("#cond_APPLY_STATE").val();
	
	if(start_Date > end_Date){
		alert("开始时间不能大于结束时间！");
		return false;
	}
	
	//查询条件校验
	if(!$.validate.verifyAll("queryPart")) {//先校验已配置的校验属性
		return false;
	}
	
	var params = "&COND_MENU_ID="+menu_Id;
	params += "&COND_ASK_START_DATE="+start_Date;
	params += "&COND_ASK_END_DATE="+end_Date;
	params += "&COND_AWS_STATE="+state;
	$.ajax.submit('queryPart', 'queryApplyTradeList', params, 'detailPart',
			function(data) {
				$.endPageLoading();
				if (data.get('ALERT_INFO') != null
						&& data.get('ALERT_INFO') != '') {
					$.showWarnMessage(data.get('ALERT_INFO'));
				}
			}, function(error_code, error_info) {
				$.endPageLoading();
				alert(error_info);
			});
}



function delAuth(){
	if(getCheckedBoxNum("ASK_ID")==0){
		alert("请选择一条记录！");
		return false;
	}
	var ASK_ID = getCheckedBoxStr("ASK_ID");
	var delObj = getCheckObjForFA("ASK_ID");
	var checkList = delObj.parentNode.parentNode.childNodes;
	alert("ASK_ID:" + ASK_ID + ",delObj:" + delObj + ",checkList:" + checkList);
	if(getCheckListNode(checkList,'AWS_STATE').value == "0"){
		MessageBox.confirm('操作提醒',"该申请正处于待审核状态，确认要删除吗?",         
	           	function(btn){
				    if(btn=='ok'){
	           		delFuc(checkList);
           			}else{
           				return false;
           			}
           		},
           		{ok:"是",cancel:"否"}
        );
	}else if(getCheckListNode(checkList,'AWS_STATE').value == "2"){
		MessageBox.confirm('操作提醒',"该申请已被拒绝，确认要删除吗?",         
	           	function(btn){
				    if(btn=='ok'){
	           		   delFuc(checkList);
           			}else if(btn=='cancel'){
           				return false;
           			}else{
           				poupApplyDialog('reupd');
           			}
           		},
           		{ok:"是",cancel:"否",ext1:"重新申请"}
        );
	}else{
		MessageBox.confirm('操作提醒',"该申请已通过，可使用次数为:" + getCheckListNode(checkList,'ASK_NUM').value  + "次，确认要删除吗?",         
	           	function(btn){
				    if(btn=='ok'){
	           		delFuc(checkList);
           			}else{
           				return false;
           			}
           		},
           		{ok:"是",cancel:"否"}
        );
	}

}

function delFuc(checkList){
		var serMenuId =  $("#cond_MENU_ID").val();
		var serAwsState = $("#cond_Apply_State").val()==0?'':$("#cond_Apply_State").val();
		var serAwsStartDate = $("#cond_START_DATE").val();
		var serAwsEndDate = $("#cond_END_DATE").val();
		alert("serMenuId:" + serMenuId + ",serAwsState:" + serAwsState + ",serAwsStartDate:" + serAwsStartDate + ",serAwsEndDate:" + serAwsEndDate);
		MessageBox.confirm('操作提醒',"该操作不可逆，确认是否删除“" + getCheckListNode(checkList,'MENU_ID').innerText  + "”选项",         
	           	function(btn){
				    if(btn=='ok'){
				    	var params = "&COND_MENU_ID="+serMenuId;
						params += "&DEL_ASK_ID="+getCheckListNode(checkList,'ASKID').value;
						params += "&COND_ASK_START_DATE="+serAwsStartDate;
						params += "&COND_ASK_END_DATE="+serAwsEndDate;
						params += "&COND_AWS_STATE="+serAwsState;
						$.ajax.submit('queryPart', 'delAppAuthInfo', params, 'detailPart',
								function(data) {
									$.endPageLoading();
									if (data.get('ALERT_INFO') != null
											&& data.get('ALERT_INFO') != '') {
										$.showWarnMessage(data.get('ALERT_INFO'));
									}
									$("#QUERY_BTN").click();
									$.closePopupPage(true);
								    return true;
								}, function(error_code, error_info) {
									$.endPageLoading();
									alert(error_info);
								});
           			}else{
           				return false;
           			}
           		},
           		{ok:"是",cancel:"否"}
        );
}


function poupApplyDialog(flag){
	var startDate = "";
	var endDate = "";
	var TradeTypecode = "";
	var AskNum = "";
	var params = "&refresh=true";
	if(flag == "add"){
		params += "&ADD_OPRATE_FLAG="+flag;
		params += "&ADD_TRADE_TYPE_CODE="+TradeTypecode;
		params += "&ADD_ASK_START_DATE="+startDate;
		params += "&ADD_ASK_END_DATE="+endDate;
		params += "&ADD_ASK_NUM="+AskNum;
		popupPage('fastauth.fastauthapply.AddFastAuthApply','init', params, '\u7533\u8bf7\u6388\u6743\u4e1a\u52a1', '800', '240')
	
	}else if(flag == "upd"){
		var checkAuth = $.table.get("ResultTable").getCheckedRowDatas();// 获取选择中的数据		
		if (checkAuth == null || checkAuth.length == 0) {
			alert("请选择一条记录！");
			return false;
		}else{
			var ASK_ID = getCheckedBoxStr("ASK_ID");
			var checkObj = getCheckObjForFA("ASK_ID");
			var checkLis = checkObj.parentNode.parentNode.childNodes;
			var state = getCheckListNode(checkLis,"AWS_STATE").value;
			if(state == '1'){
				MessageBox.alert("操作提醒", "该申请已通过审核，不能修改!");
				return false;
			}
			if(state == '2'){
				MessageBox.alert("操作提醒", "该申请已被拒绝，不能修改，请重新申请!");
				return false;
			}
			startDate = getCheckListNode(checkLis,"ASK_START_DATE").value;
			endDate = getCheckListNode(checkLis,"ASK_END_DATE").value;
			TradeTypecode = getCheckListNode(checkLis,"MENU_ID").value;
			AskNum = getCheckListNode(checkLis,"ASK_NUM").value;
			params += "&ADD_ASK_ID="+ASK_ID;
			params += "&ADD_TRADE_TYPE_CODE="+TradeTypecode;
			params += "&ADD_ASK_START_DATE="+startDate;
			params += "&ADD_ASK_END_DATE="+endDate;
			params += "&ADD_ASK_NUM="+AskNum;
			params += "&ADD_OPRATE_FLAG="+flag;
			popupPage('fastauth.fastauthapply.AddFastAuthApply','init', params, '\u4fee\u6539\u6388\u6743\u4e1a\u52a1', '800', '335')
		
		}
		
	}else if(flag == "reupd"){
		if(getCheckedBoxNum("ASK_ID")==0){
			alert("请选择一条记录！");
			return false;
		}else{
			var ASK_ID = getCheckedBoxStr("ASK_ID");
			var checkObj = getCheckObjForFA("ASK_ID");
			var checkLis = checkObj.parentNode.parentNode.childNodes;
			
			var state = getCheckListNode(checkLis,"AWS_STATE").value;
			if(state == '1'){
				MessageBox.alert("操作提醒", "该申请已通过审核，不能修改!");
				return false;
			}
			startDate = getCheckListNode(checkLis,"ASK_START_DATE").value;
			endDate = getCheckListNode(checkLis,"ASK_END_DATE").value;
			TradeTypecode = getCheckListNode(checkLis,"MENU_ID").value;
			AskNum = getCheckListNode(checkLis,"ASK_NUM").value;
			params += "&ADD_ASK_ID="+ASK_ID;
			params += "&ADD_TRADE_TYPE_CODE="+TradeTypecode;
			params += "&ADD_ASK_START_DATE="+startDate;
			params += "&ADD_ASK_END_DATE="+endDate;
			params += "&ADD_ASK_NUM="+AskNum;
			params += "&ADD_OPRATE_FLAG="+flag;
			params += "&ADD_AWS_STATE="+0;
			popupPage('fastauth.fastauthapply.AddFastAuthApply','init', params, '\u4fee\u6539\u6388\u6743\u4e1a\u52a1', '800', '235')
		
		
		}
	}
	
}

function getCheckedBoxStr(boxName, separator) {
	if (!(typeof (separator) == "string" && separator != "")) {
		separator = ",";
	}
	var boxStr = "", boxList = document.getElementsByName(boxName);
	if (boxList && boxList.length) {
		for ( var i = 0; i < boxList.length; i++) {
			if (boxList[i].checked) {
				boxStr += (boxStr == "" ? "" : separator)
						+ boxList[i].value;
			}
		}
	}
	boxList = null;
	separator = null;
	return boxStr;
}
//从checkLis中，获得name为checkName的DOM对象
function getCheckListNode(checkLis,checkName){
	if(checkLis.length <= 0)
		return;
	for(var i=0;i<checkLis.length;i++){
		if(checkLis[i].name == checkName){
			return checkLis[i];
		}
	}
}

//根据Dom对象的name属性获取选中的对象 仅限于单选radio
function getCheckObjForFA(boxName){
	boxList = document.getElementsByName(boxName);
	if (boxList && boxList.length) {
		for (var i = 0; i < boxList.length; i++) {
			if (boxList[i].checked) {
				return boxList[i];
			}
		}
	}
}

//打开页面后，进行可用次数删减
function delRealTime(ask_id){
	var params = "&ASK_ID="+ask_id;
	$.ajax.submit('queryPart', 'delAuthTime', params, 'detailPart',
			function(data) {
				
				$.endPageLoading();
				if (data.get('ALERT_INFO') != null
						&& data.get('ALERT_INFO') != '') {
					$.showWarnMessage(data.get('ALERT_INFO'));
				}
				$.closePopupPage(true);
			    return true;
			}, function(error_code, error_info) {
				$.endPageLoading();
				alert(error_info);
			});
	debugger;
	$("#QUERY_BTN").click();
}

function getSysDate(){
	var date = new Date();
	return date.getFullYear()+"-"+((date.getMonth()+1)>9 ? (date.getMonth()+1) : "0"+(date.getMonth()+1))+"-"+(date.getDate()>9 ? date.getDate() : "0"+date.getDate());
}

//通过快速授权打开业务页面
function openMenuByLink(obj){
	var checkList = obj.parentNode.parentNode.childNodes;
	
	if(!(checkList == '' || checkList.length<=0)){
		
		var Aws_State = getCheckListNode(checkList,'AWS_STATE').value;
		var start_date = getCheckListNode(checkList,'ASK_START_DATE').value;
		var end_date = getCheckListNode(checkList,'ASK_END_DATE').value;
		
		var now_date = getSysDate();
		var nowDates = now_date.split('-');
		var endDates = end_date.split('-');
		var startDates = start_date.split('-');
		
		var now = Date.parse(nowDates[1]+'-'+nowDates[2]+'-'+nowDates[0]+''+'00:00:00');
		var end = Date.parse(endDates[1]+'-'+endDates[2]+'-'+endDates[0]+''+'00:00:00');
		var start = Date.parse(startDates[1]+'-'+startDates[2]+'-'+startDates[0]+''+'00:00:00');
		
		var a = (end - now)/3600/1000;
		var b = (now - start)/3600/1000;
		
		if(Aws_State=='1'){
			if(getCheckListNode(checkList,'ASK_NUM').value > 0 && a>=0 && b>=0){
				
				var menutext = getCheckListNode(checkList,'MENU_ID').innerHTML;
				var askid = getCheckListNode(checkList,'ASKID').value;
				var textold = obj.childNodes[0].innerHTML;
				obj.childNodes[0].innerHTML = menutext;
				
				//打开菜单
				var url = obj.urlinfo;
				if(url == "NullData"){
					obj.childNodes[0].innerHTML = textold;
					alert("管理员已删除了该业务的快速使用权限!");
				}else{
					url+='&FAST_AUTH=true';
					//openmenu(url);
					obj.childNodes[0].innerHTML = textold;
					//中测  add
					check_ask_id=askid;
					
					showLayer('IncreateFrame');
					check_url = obj.urlinfo;
					//check_url=url;
					check_ask_id_temp = check_ask_id;
					check_menu_name = menutext;
					//openNav('用户资料变更',check_url,'','');
					//redirectTo('changeuserinfo.ModifyUserInfo', '', '', 'contentframe');
					//进行菜单可用次数删减
					//this.delRealTime(askid); 
				}
				
			}else if(a<0){
				MessageBox.alert("操作提醒", "该申请已到期!");
				return false;
			}else if(b<0){
				MessageBox.alert("操作提醒", "该申请还未开始生效!");
				return false;
			}else if(getCheckListNode(checkList,'ASK_NUM').value < 0){
				MessageBox.alert("操作提醒", "申请使用次数已使用完!");
				return false;
			}
			
		}else if(Aws_State=='0'){
			MessageBox.alert("操作提醒", "审核人还未通过该申请!");
			return false;
		}else {
			MessageBox.alert("操作提醒", "该申请审核被拒绝!");
			return false;
		}
	}
	
}

function checkQueryCon(){
	
	if (!verifyAll(this)) {
		return false;
	}
	return true;
}

function getParams(){
	var startDate = $("#cond_START_DATE").val();
	var endDate = $("#cond_END_DATE").val();
	var applyState = $("#cond_APPLY_STATE").val();
	var menuId = $("#cond_MENU_ID").val();
	
	var params = "&COND_MENU_ID="+menuId;
	params += "&COND_AWS_STATE="+(applyState==0?'':applyState);
	params += "&COND_ASK_START_DATE="+startDate;
	params += "&COND_ASK_END_DATE="+endDate;
	return params;
}

function showLayer(optionID) {
	document.getElementById(optionID).style.display = "block";
}

function hideLayer(optionID) {
	document.getElementById(optionID).style.display = "none";
}


function selectStaff(){
	
	popupPage('fastauth.fastauthapply.SelectStaff','init','','\u7533\u8bf7\u6388\u6743\u4e1a\u52a1','580', '356');
}
//中测 add
//验证密码是否正确
function submitPwd(obj){
	var flag=false;
	var pwd= $("#condition_PWD").val();
	if(""==pwd){
		alert("密码不可为空");
	}else{
		$.ajax.submit('queryPart', 'checkPwd', "&cond_ASK_ID="+check_ask_id+"&cond_PWD="+pwd, 'detailPart',
				function(data) {
					var res = data.get('ALERT_INFO');
					if("正确"==res){	
						hideLayer('IncreateFrame');
						//$(check_ask_id).trigger("click");
						//document.getElementById(check_ask_id).trigger("click");
						//$("#ASK_ID").trigger("click");
						//$("#ASK_ID").click;
						//check_url+='&FAST_AUTH=true';
						//alert(check_url);
						//openmenu(check_url);
						//redirectToByUrl(check_url, 'contentfram', null, null, null);
						debugger;
						var url1 = "";
						var methods = "";
						var params = "";
						if (check_url.indexOf ('page/')> -1){
							url1 = check_url.substring(check_url.indexOf ('page/')+5,check_url.indexOf ('&',check_url.indexOf('page/')+5)); 
						}
						if (check_url.indexOf ('&listener=')> -1){
							methods = check_url.substring(check_url.indexOf ('&listener=')+10,check_url.indexOf ('&',check_url.indexOf('&listener=')+10)); 
							params = check_url.substring(check_url.indexOf ('&',check_url.indexOf('&listener=')+10)); 
						}
						
						openNav(check_menu_name,url1,methods,params);
						//进行菜单可用次数删减
						delRealTime(check_ask_id_temp); 
						
					}else{
						alert("密码错误");
					}
				}, function(error_code, error_info) {
					$.endPageLoading();
					alert(error_info);
		});
	}
}
//中测 add  打开菜单
function openMenu(obj){
	var checkList = obj.parentNode.parentNode.childNodes;
	if(!(checkList == '' || checkList.length<=0)){	
	var Aws_State = getCheckListNode(checkList,'AWS_STATE').value;
		if(Aws_State=='1'){
			if(getCheckListNode(checkList,'ASK_NUM').value > 0){
				var menutext = getCheckListNode(checkList,'MENU_ID').innerHTML;
				check_ask_id = getCheckListNode(checkList,'ASKID').value;
				var textold = obj.childNodes[0].innerHTML;
				obj.childNodes[0].innerHTML = menutext;			
				//打开菜单
				check_url = obj.urlinfo;
				check_url+='&FAST_AUTH=true';
				//alert(check_url);
				openmenu(check_url);
				
				obj.childNodes[0].innerHTML = textold;
				//进行菜单可用次数删减
				this.delRealTime(check_ask_id);
			}
		}
	}
}

function refresh(){
$.ajax.submit('queryPart', 'onInit', '', 'parent',
		function(data) {
			$.endPageLoading();
		}, function(error_code, error_info) {
			$.endPageLoading();
			alert(error_info);
});
}
/////////////////////other html page

function applyTrade(){
	if (!verifyAll(this)) {
		return false;
	}
	if(document.getElementById("condition_STAFF_ID").value ==""){
		alert("请选择审核人!");
		return false;
	}
	
	var serialNumber = document.getElementById("condition_SERIAL_NUMBER").value;
	var partten = /^1[3,5,8,7]\d{9}$/;
	if(partten.test(serialNumber)){
		
	}else{
		alert("请输入正确的手机号码!");
		return false;
	}
	var flag = document.getElementById("OPRATE_FLAG").value;
	
	var startDate = $("#condition_ASK_START_DATE").val();
	var endDate = $("#condition_ASK_END_DATE").val();
	var menuId = $("#condition_MENU_ID").val();
	var selmenu =document.getElementById("condition_MENU_ID");
	var menutext =selmenu.options[selmenu.selectedIndex].text;
	menutext = menutext.substr(menutext.indexOf("]")+1);
	var askNum = $("#condition_ASK_NUM").val();
	var askId = $("#condition_ASK_ID").val();
	var aws_staff_id = $("#condition_STAFF_ID").val();
	var apply_remark = $("#condition_REMARK").val();
	var params ="";
	if(flag =="add"){
		params += "&ADD_MENU_TITLE="+menutext;
		params += "&ADD_ASK_SERIAL="+serialNumber;
		params += "&ADD_MENU_ID="+menuId;
		params += "&ADD_ASK_START_DATE="+startDate;
		params += "&ADD_ASK_END_DATE="+endDate;
		params += "&ADD_ASK_NUM="+askNum;
		params += "&ADD_AWS_STAFF_ID="+aws_staff_id;
		params += "&ADD_APPLY_REMARK="+apply_remark;
		$.ajax.submit('queryPart', 'applyAuthTrade', params, null,
				function(data) {
					if(data.get('ALERT_INFO') != '')
					{
					    alert("系统查询不到审核人手机号码，短信发送失败!请自行通知审核人!");
					}
					//refresh();
					parent.$("#QUERY_BTN").click();
					$.closePopupPage(true);
					$.endPageLoading();
				    return true;
				}, function(error_code, error_info) {
					$.endPageLoading();
					alert(error_info);
		});
	}else if(flag == "upd"){
		params += "&UPD_ASK_SERIAL="+serialNumber;
		params += "&UPD_ASK_ID="+askId;
		params += "&UPD_MENU_TITLE="+menutext;
		params += "&UPD_MENU_ID="+menuId;
		params += "&UPD_ASK_START_DATE="+startDate;
		params += "&UPD_ASK_END_DATE="+endDate;
		params += "&UPD_ASK_NUM="+askNum;
		params += "&UPD_AWS_STAFF_ID="+aws_staff_id;
		params += "&UPD_APPLY_REMARK="+apply_remark;
		$.ajax.submit('queryPart', 'updateAuthTrade', params, null,
				function(data) {
					$.endPageLoading();
					if (data.get('warnCode') != null
							&& data.get('warnCode') != '') {
						$.showWarnMessage("系统查询不到审核人手机号码，短信发送失败!请自行通知审核人!");
					}
					//refresh();
					parent.$("#QUERY_BTN").click();
					$.closePopupPage(true);
				    return true;
				}, function(error_code, error_info) {
					$.endPageLoading();
					alert(error_info);
		});

	}else if(flag == "reupd"){
		
		params += "&UPD_ASK_SERIAL="+serialNumber;
		params += "&UPD_ASK_ID="+askId;
		params += "&UPD_MENU_TITLE="+menutext;
		params += "&UPD_MENU_ID="+menuId;
		params += "&UPD_ASK_START_DATE="+startDate;
		params += "&UPD_ASK_END_DATE="+endDate;
		params += "&UPD_ASK_NUM="+askNum;
		params += "&UPD_AWS_STAFF_ID="+aws_staff_id;
		params += "&UPD_APPLY_REMARK="+apply_remark;
		params += "&UPD_AWS_STATE="+0;
		$.ajax.submit('queryPart', 'updateAuthTrade', params, null,
				function(data) {
					$.endPageLoading();
					if (data.get('warnCode') != null
							&& data.get('warnCode') != '') {
						$.showWarnMessage("系统查询不到审核人手机号码，短信发送失败!请自行通知审核人!");
					}
					//refresh();
					parent.$("#QUERY_BTN").click();
					$.closePopupPage(true);
				    return true;
				}, function(error_code, error_info) {
					$.endPageLoading();
					alert(error_info);
		});
		
	}
	
}