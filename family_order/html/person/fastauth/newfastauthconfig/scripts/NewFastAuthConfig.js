function queryFastAuth22(){
	var fastauthConfigState = $("#COND_FASTAUTH_CONFIG_STATE").val();
	var menuId = $("#COND_MENU_ID").val();
	var params = "";
	params += "&COND1_MENU_ID="+menuId;
	params += "&COND1_FASTAUTH_CONFIG_STATE="+fastauthConfigState;
	$.ajax.submit('queryInfoPart', 'queryHadTrade', params, 'detailPart',
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


//从checkLis中，获得name为checkName的DOM对象
function getCheckListNode(checkLis,checkName){
	if(checkLis.length <= 0)
		return;
	for(var i=0;i<checkLis.length;i++){
		if(checkLis[i].name == checkName){
			return checkLis[i];
		}
	}
	return null;
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


function operate22(){
	debugger;
	var menu_id = $("#condition_MENU_ID").val();
	var start_date = $("#condition_START_DATE").val();
	var end_date = $("#condition_END_DATE").val();
	
	if(menu_id=="" || menu_id == null){
		alert("请选择业务类型！");
		return false;
	}
	if(start_date == "" || start_date == null){
		alert("生效时间不能为空！");
		return false;
	}
	if(end_date == "" || end_date == null){
		alert("失效时间不能为空！");
		return false;
	}
	
	if(start_date > end_date){
		alert("开始时间不能大于结束时间！");
		return false;
	}
	
	if($("#OPERATE_FLAG").val() == 0){
		addAuth(menu_id,start_date,end_date);
	}else{
		updateAuth22(menu_id,start_date,end_date);
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

function showLayer(optionID) {
	document.getElementById(optionID).style.display = "block";
}

function hideLayer(optionID) {
	document.getElementById(optionID).style.display = "none";
}

function openUpdateAuth22(){
	debugger;
	if(!onclickTradeId()){
		alert("请选中一条记录！");
		return false;
	}
	showLayer('IncreateFrame');
	document.getElementById('IncreateFrameName').innerHTML='修改快速权限';
	var MENU_ID = getCheckedBoxStr('ACCEPT_TRADE_ID');
	var checkObj = getCheckObjForFA("ACCEPT_TRADE_ID");
	var checkLis = checkObj.parentNode.parentNode.childNodes;
	
	document.getElementById('condition_MENU_ID').value = MENU_ID;
	document.getElementById('condition_START_DATE').value = getCheckListNode(checkLis,"START_DATE").innerHTML;
	document.getElementById('condition_END_DATE').value = getCheckListNode(checkLis,"END_DATE").innerHTML;
	document.getElementById('condition_MENU_ID').disabled=true;
	document.getElementById('OPERATE_FLAG').value = 1;
}

function onclickTradeId(){
    var MENU_ID = getCheckedBoxStr("ACCEPT_TRADE_ID");
    return MENU_ID;
}

function updateAuth22(menu_id,start_date,end_date){
	
    var params = "&UPD_MENU_ID="+menu_id;
	params += "&UPD_START_DATE="+start_date;
	params += "&UPD_END_DATE="+end_date;
	$.ajax.submit('queryInfoPart', 'updateFastAuthTrade', params, 'detailPart',
			function(data) {
				$.endPageLoading();
				if (data.get('ALERT_INFO') != null
						&& data.get('ALERT_INFO') != '') {
					$.showWarnMessage(data.get('ALERT_INFO'));
				}
				$("#QUERY_BTN").click();
			}, function(error_code, error_info) {
				$.endPageLoading();
				alert(error_info);
			});
	
	hideLayer('IncreateFrame');
}

function deleteAuth22(){
	debugger;
	var MENU_ID = onclickTradeId();
	if(!MENU_ID){
		alert("请选中一条记录！");
		return false;
	}
	
	MessageBox.confirm('操作提醒',"该操作不可逆，确认是否删除",         
           	function(btn){
			    if(btn=='ok'){
			    	var params = "&DEL_MENU_ID="+MENU_ID;
			    	$.ajax.submit('queryInfoPart', 'delFastAuthTrade', params, 'detailPart',
			    			function(data) {
			    				$.endPageLoading();
			    				if (data.get('ALERT_INFO') != null
			    						&& data.get('ALERT_INFO') != '') {
			    					$.showWarnMessage(data.get('ALERT_INFO'));
			    				}
			    				$("#QUERY_BTN").click();
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

function getStaffAuth(){
	debugger;
	if (!verifyAll(this)) {
		return false;
	}
	var staffId = document.getElementById("cond_STAFF_ID").value;
	var staffName = document.getElementById("cond_STAFF_NAME").value;
	var params ="";
	
	params += "&GETSTAFF_STAFF_ID="+staffId;
	params += "&GETSTAFF_STAFF_NAME="+staffName;
	$.ajax.submit('search_condition', 'getStaffAuth', params, 'ManagerPart',
			function(data) {
				$.endPageLoading();
			}, function(error_code, error_info) {
				$.endPageLoading();
				alert(error_info);
	});
	
}

function sysChangeRange(){
	debugger;
	alert("subSysChildList:" + getElementValue("subSysChildList"));
	var subSysChildList = $.DatasetList(getElementValue("subSysChildList"));
	
	var chooseSys = document.getElementById("cond_TRADE_RANGE").value;
	
	var rangeList = RangeFuc(subSysChildList,"SUBSYS_CODE",chooseSys);
	initDragData("cond_TRADE_RANGE_CHILD",rangeList);
	document.getElementById("cond_TRADE_CHILD_CHILD").length = 0;
	var option = new Option("-----选择-----","");
	document.getElementById("cond_TRADE_CHILD_CHILD").options.add(option);
}

function getElementValue(name)
{
	var v;
	var element=document.getElementById(name);
	v=(element&&element.nodeType)?element.value:null;element=null;return v;
}

function sysChildChangeRange(){
	
	var sysTradeList = $.DatasetList(document.getElementById("subSysTradeList").value);
	
	var chooseSys = document.getElementById("cond_TRADE_RANGE_CHILD").value;
	
	var rangeList = RangeFuc(sysTradeList,"PARENT_MENU_ID",chooseSys);
	initDragData("cond_TRADE_CHILD_CHILD",rangeList);
	
}

//对指定List进行删选；根据列名为cloum，值为sysCode进行删选
function RangeFuc(list,cloum,sysCode){
	var rangeList = $.DatasetList();
	
	list.each(function(item,index,totalcount){
		if(item.get(cloum) == sysCode){
			rangeList.add(item);
		}
	});
	return rangeList;
}

//对下来菜单option进行赋值
function initDragData(objId,list){
	var ddObj = document.getElementById(objId);
	ddObj.length = 0;
	var option = new Option("-----选择-----","");
	ddObj.options.add(option);
	list.each(function(item,index,totalcount){
		var option = new Option(item.get("MENU_TITLE"),item.get("MENU_ID"));
		ddObj.options.add(option);
	});
}

//查询菜单树
function queryMenus(){
	var subSys = $("#cond_TRADE_RANGE").val();
	var tradeCode = $("#cond_TRADE_CHILD_CHILD").val();
	var tradeObj = document.getElementById("cond_TRADE_CHILD_CHILD");
	var tradeSelectText = tradeObj.options[tradeObj.selectedIndex].text;
	if(tradeCode == "" || tradeCode == "undefined"){
		alert("业务类型范围不能为空！");
		return false;
	}
	var param = '&menu_SUBSYS_CODE='+subSys+'&menu_PARENT_MENU_ID='+tradeCode+'&menu_PARENT_MENU_TEXT='+tradeSelectText;
	$.ajax.submit('queryInfoPart', 'qryMenus', param, 'treePart',
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

function deleteRow(r){
  var i=r.parentNode.parentNode.rowIndex;
  var tab = document.getElementById("menuConfigArea");
  tab.deleteRow(i-1);
}

function getMenuItem(flag,subSysCode){//flag: 0父节点,不处理，1菜单子节点，添加到右侧表格
	var obj = event.srcElement;
	if(flag == '0'){
		return false;
	}
	
	var tbody = document.getElementById("menuConfigArea");
	
	var tr = document.createElement("tr");
	tr.id = "tr_"+obj.id;
	tr.subsyscode = subSysCode;
	
	if(tbody.contains(document.getElementById('tr_'+obj.id))){
		alert("该菜单信息已添加!!");
		return false;
	}
	
	tbody.appendChild(tr);
	//删除操作cell
	var tdop = document.createElement("td");
	var aOp = document.createElement("a");
	aOp.className = "e_bLinkDeleteNB";
	aOp.href = "javascript:void(0);";
	aOp.onclick = function(){deleteRow(this);};
	tdop.appendChild(aOp);
	tr.appendChild(tdop);
	
	var tdname = document.createElement("td");
	tdname.innerHTML = obj.title;
	tr.appendChild(tdname);
	
	var tdvalue = document.createElement("td");
	tdvalue.innerHTML = obj.id;
	tr.appendChild(tdvalue);
	
	//开始时间控件 begin
	var tdstart = document.createElement("td");
	var divStart = document.createElement("div");
	
	System.onLoad(function(){Include('component/calendar.js', false);});
	
	tdstart.appendChild(divStart);
	var tabStart = document.createElement("table");
	divStart.appendChild(tabStart);
	
	//tbody
	var tbodySt = document.createElement("tbody");
	//tr
	var tbtr = document.createElement("tr");
	//td1
	var tbtrTd1 = document.createElement("td");
	var tbtrtdInput = document.createElement("input");
	tbtrtdInput.name = "cond_START_DATE_tr_"+obj.id;
	tbtrtdInput.className = "e_input";
	tbtrtdInput.id = "cond_START_DATE_tr_"+obj.id;
	tbtrtdInput.type = "text";
	tbtrtdInput.datatype = "date";
	tbtrtdInput.nullable = "no";
	tbtrtdInput.desc = "开始时间";
	tbtrtdInput.ctrlName = "WRAP_CAL_"+tbtrtdInput.name;
	tbtrtdInput.format = "yyyy-MM-dd";
	tbtrTd1.appendChild(tbtrtdInput);
	//td2
	var tbtrTd2 = document.createElement("td");
	tbtrTd2.className = "fct";
	tbtrTd2.id = "WRAP_CAL_"+tbtrtdInput.name;
	var tbtrtdA = document.createElement("a");
	tbtrtdA.className = "e_buttonDate";
	tbtrtdA.id = "IMG_CAL_"+tbtrtdInput.name;
	tbtrtdA.href = "javascript:void(0)";
	tbtrtdA.onclick = function(){Wade.component.CalendarManager.addCalendar('cond_START_DATE_tr_'+obj.id,false,true);};
	tbtrTd2.appendChild(tbtrtdA);
	
	tbtr.appendChild(tbtrTd1);
	tbtr.appendChild(tbtrTd2);
	//tr end
	tbodySt.appendChild(tbtr);
	tabStart.appendChild(tbodySt);
	//tbody end
	
	tr.appendChild(tdstart);
	//开始时间控件 end
	
	//结束时间控件 start
	var tdend = document.createElement("td");
	
	var divEnd = document.createElement("div");
	
	System.onLoad(function(){Include('component/calendar.js', false);});
	
	tdend.appendChild(divEnd);
	var tabEnd = document.createElement("table");
	divEnd.appendChild(tabEnd);
	
	//tbody
	var tbodySt = document.createElement("tbody");
	//tr
	var tbtr = document.createElement("tr");
	//td1
	var tbtrTd1 = document.createElement("td");
	var tbtrtdInput = document.createElement("input");
	tbtrtdInput.name = "cond_END_DATE_tr_"+obj.id;
	tbtrtdInput.className = "e_input";
	tbtrtdInput.id = "cond_END_DATE_tr_"+obj.id;
	tbtrtdInput.type = "text";
	tbtrtdInput.datatype = "date";
	tbtrtdInput.nullable = "no";
	tbtrtdInput.desc = "结束时间";
	tbtrtdInput.ctrlName = "WRAP_CAL_"+tbtrtdInput.name;
	tbtrtdInput.format = "yyyy-MM-dd";
	tbtrTd1.appendChild(tbtrtdInput);
	//td2
	var tbtrTd2 = document.createElement("td");
	tbtrTd2.className = "fct";
	tbtrTd2.id = "WRAP_CAL_"+tbtrtdInput.name;
	var tbtrtd2A = document.createElement("a");
	tbtrtd2A.className = "e_buttonDate";
	tbtrtd2A.id = "IMG_CAL_"+tbtrtdInput.name;
	tbtrtd2A.href = "javascript:void(0)";
	tbtrtd2A.onclick = function(){Wade.component.CalendarManager.addCalendar('cond_END_DATE_tr_'+obj.id,false,true);};
	tbtrTd2.appendChild(tbtrtd2A);
	
	tbtr.appendChild(tbtrTd1);
	tbtr.appendChild(tbtrTd2);
	//tr end
	tbodySt.appendChild(tbtr);
	tabEnd.appendChild(tbodySt);
	//tbody end
	tr.appendChild(tdend);
	//结束时间控件 end
}

//获取配置信息
function getConfigTab(){
	var tbody = document.all.menuConfigArea;
	var childsNode = tbody.childNodes;
	var confList = new Wade.DatasetList();
	var confMap = new Wade.DataMap();
	
	var opFlag = true;
	for(var i = 0; i<childsNode.length;i++){
		var trObj = childsNode[i];
		if(getTrObj(trObj) == false){
			opFlag = false;
			break;
		}
		confMap = getTrObj(trObj);
		confList.add(confMap);
	}
	if(opFlag == false){
		return false;
	}
	if(confList.length == 0){
		alert('未选择相应的权限信息!!');
		return false;
	}
	document.getElementById("menuesValue").value = confList;
	
	var param = "&MENUS_VALUE="+document.getElementById("menuesValue").value;
	var tt = document.createElement("tbody"); 
	tt.id = "menuConfigArea";
	getElement("menuConfigArea").replaceNode(tt);
	return opFlag;
	//Wade.ajax.ajaxDirect(this, "postMenus", param, "queryInfoPart,detailPart",false,function(){var tt = document.createElement("tbody"); tt.id = "menuConfigArea";getElement("menuConfigArea").replaceNode(tt);});
}

function getTrObj(trObj){
	
	var confMap = new Wade.DataMap();
	var trObjChilds = trObj.childNodes;
	
	if(trObjChilds.length < 5){
		alert("页面出错，请重新刷新页面!");
		return false;
	}else{
		confMap.put("MENU_ID",trObjChilds[2].innerHTML);
		confMap.put("MENU_TITLE",trObjChilds[1].innerHTML);
		
		var start_date = document.getElementById("cond_START_DATE_"+trObj.id).value;
		var end_date = document.getElementById("cond_END_DATE_"+trObj.id).value;
		
		if(start_date == ""){
			alert('请填写"'+trObjChilds[1].innerHTML+'"的开始时间!!');
			return false;
		}
		if(end_date == ""){
			alert('请填写"'+trObjChilds[1].innerHTML+'"的结束时间!!');
			return false;
		}
		if(start_date > end_date){
			alert('"'+trObjChilds[1].innerHTML+'"的开始时间不能大于结束时间!!');
			return false;
		}
		
		confMap.put("START_DATE",start_date);
		confMap.put("END_DATE",end_date);
		confMap.put("SUBSYS_CODE",trObj.subsyscode);
		return confMap;
	}
}

function setReturnValue(value, text, paramNames, paramValues) {
	var obj = new Object();
	obj.value = value;
	obj.text = text;
	obj.paramNames = paramNames;
	obj.paramValues = paramValues;
	//setReturnObj(obj);
}

function setReturnObj(obj) {
	var Win;
	if (typeof (window.parent) != "undefined"
			&& typeof (window.parent.window) != "undefined"
			&& (Win = window.parent.window) && Win.Wade
			&& Win.Wade.dialog.hasOpendDialog()) {
		Win = window.parent.window;
		Win.Wade.dialog.setDialogReturnObj(obj);
		Win.Wade.dialog.closeCurrentDialog();
		Win = null;
	} else {
		if (System.UserAgent.gecko) {
			if (typeof (top.window.opener) != "undefined"
					&& top.window.opener != null) {
				top.window.opener.returnValue = obj;
			}
		} else {
			window.returnValue = obj;
		}
		top.close();
	}
}

//中测新增(提交授权的时候，添加禁用员工)
	function selectStaff(){
		debugger;
		var staff = document.getElementsByName('STAFF_ID');
		var staff_id_checked="";
		var staff_name_checked="";
		//alert(strcheck[0].value);	
		var checkedStaff = new Array();
		
		for(var i=0 ; i<staff.length ; i++){
			var trObj = staff[i].parentNode.parentNode;
			var staff_id = getCheckListNode(trObj.childNodes,'STAFF_ID');
			var staff_name=getCheckListNode(trObj.childNodes,'STAFF_NAME');
			if(staff[i].checked) {
					checkedStaff.push(staff[i]);
					staff_id_checked+=staff_id.value+",";	
					staff_name_checked+=staff_name.value+",";
		 	}		
        }
        if(checkedStaff.length>10){
			alert("最多只能选10个员工！");
			return false;
		}else{		
       		setReturnValue(staff_id_checked.substring(0,staff_id_checked.length-1), staff_name_checked.substring(0,staff_name_checked.length-1));
        }
}
