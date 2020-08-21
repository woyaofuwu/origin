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
 

function verifyInfo(){
	if(!$.validate.verifyAll("APNDataPart"))
	{
		return false;
	}
	//var endDate=$("#END_DATE").val();
	var paraCode1=$("#PARA_CODE1").val();
	var remark=$("#REMARK").val();
	if( paraCode1 == null || paraCode1 == '' ){
		MessageBox.alert("告警提示","参数1不能为空！");
		return;
	}
	if( remark == null || remark == '' ){
		MessageBox.alert("告警提示","备注不能为空！");
		return;
	}
	$.beginPageLoading("校验paraCode1...");
	$.ajax.submit('', 'verifyInfo', "&PARA_CODE1="+paraCode1+"&REMARK="+remark, '', function(rtnData) { 
		$.endPageLoading();
		if(rtnData!=null&&rtnData.length > 0){
			if(rtnData.get("RESULT_CODE")=="1"){
				$("#CHECK_RESULT_TAG").val("1");
				$("#CHECK_RESULT").val(rtnData.get("RESULT_INFO")+" (PARA_CODE1="+rtnData.get("PARA_CODE1")+")");	
				MessageBox.success("成功提示","业务受理成功!");
			}else{
				$("#CHECK_RESULT_TAG").val("-1");
				$("#CHECK_RESULT").val(rtnData.get("RESULT_INFO")+" (PARA_CODE1="+rtnData.get("PARA_CODE1")+")");
				MessageBox.alert("告警提示","业务受理失败！");
			} 
		}
	}, function(error_code, error_info,detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
	}); 

	$.endPageLoading();
}
 