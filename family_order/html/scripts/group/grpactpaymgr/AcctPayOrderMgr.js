function changeQueryType(){ 
 	var queryType = $('#cond_QUERY_TYPE').val(); 
 	if(queryType == '0' || queryType == '1'){
 		$('#GroupPart').css("display","none");
 		$('#GroupCustInfoPart').css("display","none");	
 		$('#PersonPart').css("display","block");
 	}else if(queryType == '2'){
 		$('#PersonPart').css("display","none");
 		$('#GroupPart').css("display","block");
 		$('#GroupCustInfoPart').css("display","block");
 	}
}
//集团资料查询成功后调用的方法
function selectGroupAfterAction(data){
	// 填充集团客户显示信息
	insertGroupCustInfo(data);
	getAcctInfoByGrpId();
}

//集团资料查询失败后调用的方法
function selectGroupErrorAfterAction(data){

	//清空填充的集团客户信息内容
    clearGroupCustInfo();
}
function getAcctInfoByGrpId(){	
	$.beginPageLoading();
	$.ajax.submit('GroupCustInfoPart','getAcctInfoByGrpId', '','AccTablePart', function(data){
		$.endPageLoading(); 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function getAcctInfoBySn(){
	var serialNumber = $('#ACCT_SERIAL_NUMBER').val();
	if(serialNumber == null || serialNumber == ""){
		alert("请输入查询条件！");
		return false;
	}
	ajaxSubmit('group.grpactpaymgr.AcctPayOrderMgr','getAcctInfoBySn','&cond_SERIAL_NUMBER='+serialNumber,'AccTablePart,AccRelTablePart');			
}

function getRelationAA() {
	var rowData = $.table.get("AccTable").getRowData();
	var accId = rowData.get("ACCT_ID");
	 if(accId==""){
      alert('账号为空！');
      return false;
    }
	var route = rowData.get("EPARCHY_CODE");
	var queryType = $('#cond_QUERY_TYPE').val(); 
	ajaxSubmit(this, 'getRelationAA','&cond_ACCT_ID_A='+accId+'&cond_QUERY_TYPE='+queryType+'&cond_EPARCHY_CODE='+route,'AccRelTablePart',null,false,null);
}

function dealRelationAAOrder(){
	var pam_oldrelay = $('#pam_oldrelay').val(); 
	var oldValueSet= $.DatasetList(pam_oldrelay);
	var newValueSet = $.DatasetList(pam_oldrelay);
    var resultSet =  $.DatasetList();

	for (var i=0; i < newValueSet.length; i++) {
		var ordernumvalue = $('#ordernum_'+i).val();
	    var instidvalue = $('#instid_'+i).val();
	    
	    if(ordernumvalue == "") {
			alert("\u4F18\u5148\u7EA7\u4E0D\u80FD\u4E3A\u7A7A\uFF01");
			$('#ordernum_'+i).focus();
			return false;
		}
	    //数字校验
	    var patrn=/^([+-]?)(\d+)$/;
		var isNumber1 =  patrn.test(ordernumvalue);
	    if(isNumber1 == false)
	    {
	      alert("\u4F18\u5148\u7EA7\u5FC5\u987B\u4E3A\u6574\u578B\uFF01"); 
	      $('#ordernum_'+i).val("");
	      $('#ordernum_'+i).focus();
	      return false;
	    }
	    
	    newValueSet.get(i).put('ORDERNO',ordernumvalue);
	    newValueSet.get(i).put('INST_ID',instidvalue);
	}
	//校检优先级是否重复
	for (var i=0; i < newValueSet.length; i++) {
		var ordernumvalue = $('#ordernum_'+i).val();
	    for (var j=i+1; j < newValueSet.length; j++) {
			var numvalue = $('#ordernum_'+j).val();
		     if(ordernumvalue == numvalue) {
				alert("\u4F18\u5148\u7EA7\u4E0D\u80FD\u91CD\u590D\uFF0C\u8BF7\u4FEE\u6539\u4E4B\u540E\u518D\u63D0\u4EA4\uFF01");
				$('#ordernum_'+j).val("");
				$('#ordernum_'+j).focus();
				return false;
			}
		}
	}
	for (var k=0; k < newValueSet.length; k++) {
		var newValueColumn = newValueSet.get(k);
		for (var j=0; j < oldValueSet.length; j++) {
			var oldValueColumn = oldValueSet.get(j);
			if (oldValueColumn.get('INST_ID') == newValueColumn.get('INST_ID')) {
				if (oldValueColumn.get('ORDERNO') != newValueColumn.get('ORDERNO')) {
					newValueColumn.put("STATE", "MODI");
					var remark = "优先级由["+oldValueColumn.get('ORDERNO')+"]变为["+newValueColumn.get('ORDERNO')+"]";
					newValueColumn.put("REMARK", remark);
					newValueColumn.put("RSRV_STR4", oldValueColumn.get('ORDERNO'));
					resultSet.add(newValueColumn);
				}
			}
		}
	}

	if(resultSet.length > 0){
	}else{
		alert("\u4F18\u5148\u7EA7\u672A\u53D1\u751F\u53D8\u52A8\uFF0C\u4E0D\u9700\u8981\u63D0\u4EA4\uFF01");
		return false;
	}
	$('#pam_relay').val(resultSet);
	return true;
}
