function refreshPartAtferAuth(data)
{
	$.ajax.submit('', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString(), 'changeData,hidePart,dataInfoTablePart', function(){
		$("#comminfo_REMARK").attr('disabled',true);
		$("#comminfo_DONATE_DATA").attr('disabled',true);
		$("#objinfo_OBJ_SERIAL_NUMBER").bind("keydown",serialNumberKeydown);
		
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function serialNumberKeydown(event)
{
	event = event || window.event; 
	e = event.keyCode;
	
	if (e == 13 || e == 108)
	{
		checkSerialNumber();
	}
}
/** check serialNumber */
function checkSerialNumber() {
	var phonecode =$("#AUTH_SERIAL_NUMBER");
	var objectcode =$("#objinfo_OBJ_SERIAL_NUMBER");
	//var desc = phonecode.desc;

	if(!$.validate.verifyField($("#AUTH_SERIAL_NUMBER")[0]) || !$.validate.verifyField($("#objinfo_OBJ_SERIAL_NUMBER")[0]))
	{
		return false;
	} 

	//if(!checkMbphone(phonecode,desc)) return false;
	if( phonecode.val() == objectcode.val()){
		  MessageBox.alert("","转赠号码不能与用户号码相同!");
		  $("#SERIAL_NUMBER_INPUT").removeClass("e_elements-success");
		  $("#SERIAL_NUMBER_INPUT").addClass("e_elements-error");
		  return false;
	}
	
	$.beginPageLoading();
	$.ajax.submit('changeData', 'queryObjCustInfo', null, 'hidePart', function(){
		$("#comminfo_REMARK").attr('disabled',false);
		$("#comminfo_DONATE_DATA").attr('disabled',false);
		$("#objinfo_OBJ_SERIAL_NUMBER").bind("keydown",serialNumberKeydown);
		$("#SERIAL_NUMBER_INPUT").addClass("e_elements-success");
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$("#comminfo_DONATE_DATA").val('');
		$("#comminfo_DONATE_DATA").attr('disabled',true);
		$("#comminfo_REMARK").attr('disabled',true);
		$.endPageLoading();
		$("#SERIAL_NUMBER_INPUT").removeClass("e_elements-success");
		$("#SERIAL_NUMBER_INPUT").addClass("e_elements-error");
		showDetailErrorInfo(error_code,error_info,derror);
		
    });
 
}
 
/** check data */
function checkData() {
    var min = $("#MIN").val();
    var max = $("#MAX").val();
	var donate_data = $("#comminfo_DONATE_DATA");
	
	var isok = isChecked("dataInfoTable", null, "idList");
	if(!isok){
		alert('请先选择一条记录！');
		return false;
	}
	var batch = getCheckedTableData("dataInfoTable", null, "idList");
    if(batch == null || batch ==""){
        return false;
    }
	var balance = batch.get(0).get("BALANCE");
	if(!$.validate.verifyField($("#comminfo_DONATE_DATA")[0]))
	{
		$("#comminfo_DONATE_DATA").val('');
		return false;
	} 	

	if (parseInt(donate_data.val()) <= 0) {
		alert("转赠流量不能小于等于0!");
		$("#comminfo_DONATE_DATA").val('');
		return false;
	}
	
    if (parseInt(min) >0 && parseInt(balance) <= parseInt(min)) {
    	alert("用户流量小于"+min+"MB时不能办理转赠！");
    	$("#comminfo_DONATE_DATA").val('');
    	return false;
    }

    if ( (100*parseInt(donate_data.val())) > (parseInt(balance)* parseInt(max))) {
    	alert("转赠流量上限不能大于用户流量的"+max+"%!");
    	$("#comminfo_DONATE_DATA").val('');
    	return false;
    }

	if (parseInt(donate_data.val()) > parseInt(balance)) {
		alert("转赠流量不能大于用户流量!");
		$("#comminfo_DONATE_DATA").val('');
		return false;
	}

}

//业务提交
function onTradeSubmit()
{
	var newScore = $("#objinfo_NEWDATA").html();
	$.cssubmit.setParam("NEWDATA", newScore);
	$.cssubmit.submitTrade();
}

/** check submit  */
function checksubmit() {
	if(!$.validate.verifyField($("#objinfo_OBJ_SERIAL_NUMBER")[0]) || !verifyAll('changeData'))
	{
		return false;
	}

    var donate_data = $("#comminfo_DONATE_DATA");
    var min = $("#MIN").val();
    var max = $("#MAX").val();
    if (donate_data.val() == "" || parseInt(donate_data.val()) <= 0) {
    	alert("转赠流量不能小于0!");
    	return false;
    }

	var isok = isChecked("dataInfoTable", null, "idList");
	if(!isok){
		alert('请先选择一条记录！');
		return false;
	}
	var batch = getCheckedTableData("dataInfoTable", null, "idList");
    if(batch == null || batch ==""){
        return false;
    }
	var commId = batch.get(0).get("COMM_ID");
	var balance = batch.get(0).get("BALANCE");
	var dataType = batch.get(0).get("DATA_TYPE");
	var discntCode = batch.get(0).get("DISCNT_CODE");
	var effectiveDate = batch.get(0).get("EFFECTIVE_DATE");
	var expireDate = batch.get(0).get("EXPIRE_DATE");
    if (parseInt(min) >0 && parseInt(balance) <= parseInt(min)) {
    	alert("用户流量小于"+min+"KB时不能办理转赠！");
    	return false;
    }

    if ( (100*parseInt(donate_data.val())) > (parseInt(balance)* parseInt(max))) {
    	alert("转赠流量上限不能大于用户流量的"+max+"%!");
    	return false;
    }
    if ( parseInt(donate_data.val()) > parseInt(balance)) {
    	alert("转赠流量不能大于用户流量!");
    	return false;
    }
    var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
    param += "&COMM_ID="+commId+"&DATA_TYPE="+dataType+"&EFFECTIVE_DATE="+effectiveDate+"&EXPIRE_DATE="+expireDate+"&BALANCE="+balance+"&DISCNT_CODE="+discntCode;
	$.cssubmit.addParam(param);
	return true;
}

function getCheckedTableData(tbName, g, chekboxName)
{
	var b = new Wade.DatasetList();
	var d = Wade.table.get(tbName);
	var checkboxname = chekboxName;
	var c = Wade("tbody", d.getTable()[0]);
	var e = d.tabHeadSize;
	if (c) {
		Wade("tr", c[0]).each(
				function(h, i) {
					var isChecked = $('input[name=' + checkboxname + ']', this)
							.attr("checked");
					if (isChecked) {
						var j = d.getRowData(g, h + e);
						if (j) {
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

function isChecked(tbName, g, chekboxName)
{
	var isok = false;
	var b = new Wade.DatasetList();
	var d = Wade.table.get(tbName);
	var checkboxname = chekboxName;
	var c = Wade("tbody", d.getTable()[0]);
	var e = d.tabHeadSize;
	if (c) {
		Wade("tr", c[0]).each(
				function(h, i) {
					var isChecked = $('input[name=' + checkboxname + ']', this)
							.attr("checked");
					if (isChecked) {
						isok = true;
						return isok;
					}
					j = null;
				});
	}else{
		c = null;
		d = null;
		isok = false;
	}
	return isok;
}

function DealMark(chk){
	if (chk.attr("checked") == true) {
		chk.attr("rowIndex", $.table.get("dataInfoTable").getTable().attr("selected"));
	}
}
