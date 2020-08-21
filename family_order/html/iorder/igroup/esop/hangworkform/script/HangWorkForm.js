/*window.onload = function () {
  //  $("#HANG_NODE").value() = "1";
    $("#HANG_NODE").val("1");
}*/
function LayoutWorkForm(){
	var ibsysId = $('#cond_IBSYSID').val();
	var groupId = $('#cond_GROUPID').val();
	var productNo = $('#cond_PRODUCTNO').val();
	var serialNo = $('#cond_SERIALNO').val();
	if (ibsysId=='' && groupId =='' && productNo == '' && serialNo == ''){
		//alert('请输入查询条件!');
		MessageBox.alert("请输入查询条件!");
		return false;
	}
	if(!$.validate.verifyAll('queryForm')) return false;

	$.beginPageLoading("数据查询中......");
	$.ajax.submit("queryForm", "layoutWorkForm", "", "refreshtable", function(data){
			$.endPageLoading();
			var resultCode = data.get("X_RESULTCODE");
			var resultInfo = data.get("X_RESULTINFO");
			if(resultCode =='0'){
				$('#queryMsg').css("display","");
				MessageBox.alert("没有符合条件的查询结果,请核对你要查询的内容！");
				return false;
			}
			$('#queryMsg').css("display","none");
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
	hidePopup('qryPopup');
}

function submit(){
	if(!$.validate.verifyAll('qryPopupItem')) {
		//alert('请输入查询条件!');
		MessageBox.alert("请输入所有必填项!");
		return false;
	}
	if(!$.validate.verifyAll("querySubmit")){
		return false;
	}
	var b = getCheckedTableData();
	if (b==''){
		//alert('请输入查询条件!');
		MessageBox.alert("请选择需要操作的专线！");
		return false;
	}
	var linkCode = $('#LINK_PHONE_CODE').val();
	if (linkCode==''){
		//alert('请输入查询条件!');
		MessageBox.alert("申请人联系电话不能为空！");
		return false;
	}
	var linkName = $('#LINK_NAME').val();
	if (linkName==''){
		//alert('请输入查询条件!');
		MessageBox.alert("申请人姓名不能为空！");
		return false;
	}
	var content = $('#CONTENT').val();
	if (CONTENT==''){
		//alert('请输入查询条件!');
		MessageBox.alert("申请原因不能为空不能为空！");
		return false;
	}
	if(!$.validate.verifyAll("querySubmit")){
		return false;
	}
	var param = "&SN="+b;
	$.beginPageLoading("数据提交中，请稍后...");
	$.ajax.submit("querySubmit,queryForm", "createWorkFormInfo", param, "refreshtable,querySubmit,queryForm", function(data){
		$.endPageLoading();
		/*if(resultCode =='0'){*/
			 MessageBox.success("操作成功！", "", function(btn){
					if("ok" == btn){
						closeNav();
					}
			});
		/* }*/
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			//MessageBox.alert("操作失败");
			showDetailErrorInfo(error_code,error_info,derror);
		}
	);
	
}

function getCheckedTableData()
{
	var chk = document.getElementsByName("monitorids");
	var result = "";
	if(chk) {
		for(var i = 0;i < chk.length; i++){
            if(chk[i].checked){
				result += chk[i].value;
                if(i == chk.length - 1){
                    
                }else{
                	result = result + "|";
                }
            }
       	}
		
	}
	return result;
}

	
function segment(obj){
	if(obj == '1'){
		$('#op1').attr("class", "e_segmentOn");
		$('#op2').attr("class", "");
		//$('#HANG_NODE').value() = "1";
		$("#HANG_NODE").val("1");
		$('#cond_HANG_DAT').css("display","");
		$('#cond_CONTENT').css("display","");
	}else{
		$('#op2').attr("class", "e_segmentOn");
		$('#op1').attr("class", "");
		//$('#HANG_NODE').value() = "2";
		$('#HANG_NODE').val("2");
		$('#cond_HANG_DAT').css("display","none");
		$('#cond_CONTENT').css("display","none");
	}
}