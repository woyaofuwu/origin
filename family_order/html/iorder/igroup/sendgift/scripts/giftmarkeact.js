/* 获得焦点事件后触发事件,将光标位置移到员工工号编码最后一位后 */
function moveLast(){
	var chnl_code_rng = document.getElementById('cond_SERIAL_NUMBER').createTextRange();
	chnl_code_rng.collapse(false);         
	chnl_code_rng.select();
}

function checkSubmitBefore() {
	if (!$.validate.verifyAll("sendGiftPart")) {
	    return false;
	}
	
	$.beginPageLoading("业务受理中...");
	$.ajax.submit('sendGiftPart', 'submitProcess', null, 'sendGiftPart', function(data)
	{
		if(data.get("result") == 'true'){
			MessageBox.alert("提示","赠送成功！",function(btn){
				if(btn=="ok"){
					window.location.href = window.location.href;
				}
			});
		}else{
			MessageBox.alert("提示","赠送失败！/n"+data.get("result"),function(btn){
				
			});
		}
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

//获取用户信息
function getCustInfo(){
	var serialNumber = $('#cond_SERIAL_NUMBER').val();
	if (serialNumber != null && serialNumber != ''){	
		$.beginPageLoading("正在查询客户信息...");
		$.ajax.submit("", "getCustInfo", '&SERIAL_NUMBER='+serialNumber, null, 
				function(data)
				{
					var success = data.get("success");/* 判断返回 */
					if(success == 'true'){
						$('#cond_CUST_NAME').val(data.get("CUST_NAME"));
						$('#cond_GROUP_NUM').val(data.get("GROUP_ID"));
						$('#cond_GROUP_NAME').val(data.get("GROUP_CUST_NAME"));
						$('#cond_CITY_CODE').val(data.get("CITY_CODE"));
						$('#cond_CUST_MANAGER_ID').val(data.get("CUST_MANAGER_ID"));
						$('#cond_PHONE').val(data.get("SERIAL_NUMBER"));
					}else{
						alert(data.get("msg"));
						$('#cond_CUST_NAME').val("");
						$('#cond_GROUP_NUM').val("");
						$('#cond_GROUP_NAME').val("");
						$('#cond_CITY_CODE').val("");
						$('#cond_CUST_MANAGER_ID').val("");
						$('#cond_PHONE').val("");
						document.getElementById('cond_SERIAL_NUMBER').focus();
					}
					$.endPageLoading();
				},
				function(error_code,error_info, derror)
				{
					$.endPageLoading();
					showDetailErrorInfo(error_code,error_info,derror);
				}
		    );
	}else{
		alert('请先输入客户手机号码！');
		document.getElementById('cond_SERIAL_NUMBER').focus();
	}
}

//赠送礼物
function checkAndSubmit()
{
	//查询条件校验
	if(!$.validate.verifyAll("sendGiftPart"))
	{
		return false;
	}
	
	$.beginPageLoading("业务受理中...");
	$.ajax.submit('sendGiftPart', 'submitProcess', null, 'sendGiftPart', function(data)
	{
		if(data.get("result") == 'true'){
			MessageBox.alert("提示","赠送成功！",function(btn){
				if(btn=="ok"){
					window.location.href = window.location.href;
				}
			});
		}else{
			MessageBox.alert("提示","赠送失败！/n"+data.get("result"),function(btn){
				
			});
		}
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

