function refreshPartAtferAuth(data)
{
	//查询用户是否已经开户完成
	var serialNumber = data.get('USER_INFO').get('SERIAL_NUMBER');
	var userId = data.get('USER_INFO').get('USER_ID');
	$.ajax.submit(this, 'checkHYTUser', '&SERIAL_NUMBER=' + serialNumber+'&USER_ID='+userId, '',
			function(data) {

				$.cssubmit.disabledSubmitBtn(false);
				if (data.get("CODE") =='0000') {
					MessageBox.error("错误提示","加载业务数据!", $.auth.reflushPage, null, "用户已开通海洋通，请勿重复开通！", "用户已开通海洋通，请勿重复开通。");
				}
				$.endPageLoading();
			}, 
			function(error_code, error_info, derror) {
				$.endPageLoading();
				MessageBox.error("错误提示","加载业务数据!", $.auth.reflushPage, null, info, derror);
			}
		);
	
	
}

function checkValidDiscnt(){
	var isShipOwner = $("#IS_SHIP_OWNER").val();
	var shipId = $("#SHIP_ID").val();
	var discntCode = $("#DISCNT_CODE").val();
	
	var shipId = getShipId();
	$("#SHIP_ID").val(shipId);
	
	$.beginPageLoading("办理套餐校验中...");
	$.ajax.submit(this, 'checkValidDiscnt', '&IS_SHIP_OWNER=' + isShipOwner+'&SHIP_ID=' + shipId+'&DISCNT_CODE=' + discntCode, '',
		function(data) {


			if (data.get("CODE") !='0000') {
				MessageBox.alert("告警提示",data.get("MSG"));
				$.cssubmit.disabledSubmitBtn(true);
			}else{
				$("#IS_OWNER_DISCNT").val(data.get("IS_OWNER_DISCNT"));
				$("#DISCNT_NAME").val($("#DISCNT_CODE").find("option:selected").text());
				$.cssubmit.disabledSubmitBtn(false);
			}
			$.endPageLoading();
		}, 
		function(error_code, error_info, derror) {
			$.endPageLoading();
			
			showDetailErrorInfo(error_code,error_info,derror);
		}
	);
	
}

function onSubmit() {
	var discntCode = $("#DISCNT_CODE").val();
	if(discntCode==""){
		MessageBox.alert("告警提示","产品信息为必选参数，请选择后提交！");
		return false;
	}
	
	$("#SHIP_ID").val(getShipId());
	return true;
}
function getShipId(){
	var shipIdNum = $("#SHIP_ID_NUM").val();
	if(isNaN(shipIdNum)){
		MessageBox.alert("告警提示","船只编号必须为数字！");
		$("#SHIP_ID_NUM").focus();
		return false;
	}
	if(shipIdNum.length!=5){
		MessageBox.alert("告警提示","船只编号必须为五位数字！");
		$("#SHIP_ID_NUM").focus();
		return false;
	}
	var shipId = "琼"+ $("#SHIP_AREA").val()+"渔"+shipIdNum;
	return shipId;
}

