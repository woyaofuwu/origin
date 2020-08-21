function refreshPartAtferAuth(data) {
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('AuthPart', 'loadChildInfo', '' ,
			'busiInfoPart', function() {
				
				$.endPageLoading();
			}, function(error_code, error_info,detail) {
				$.endPageLoading();
				MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
			});
}


function checkBeforeSubmit(){
	
	var checkedDatas = $.DatasetList();
	var size = $('#subNumTbody').children().length;
	if(size > 0){
		for(var i = 0 ; i < size ; i++){
			var index = i+1;
			if($('#subNumTbody tr:nth-child('+index+') td').children().attr('checked')){
				var data = new $.DataMap();
				data.put("SUB_USER_VALUE",$('#subNumTbody tr:nth-child('+index+') td').children().attr('subUserValue'));
				data.put("SUB_USER_TYPE",$('#subNumTbody tr:nth-child('+index+') td').children().attr('subUserType'));
				data.put("MAIN_USER_VALUE",$('#subNumTbody tr:nth-child('+index+') td').children().attr('mainUserValue'));
				checkedDatas.add(data);
			}
		}
	}
// TODO lijun17 input[name='CheckBox_VALUE'] 获取不到值
//	$.each($("input[name='CheckBox_VALUE']:checked"),
//			function getElements(){
//				var data = new $.DataMap();
//				data.put("SUB_USER_VALUE",this.subUserValue);
//				data.put("SUB_USER_TYPE",this.subUserType);
//				data.put("MAIN_USER_VALUE",this.mainUserValue);
//				checkedDatas.add(data);
//			}
//	);
	if(checkedDatas.length<1){
		alert("请勾选需要解约的记录后，再提交！");
		return false;
	}
	var param ="&CANCEL_DATAS="+checkedDatas;
	
	$.cssubmit.addParam(param);

 	return true; 
}