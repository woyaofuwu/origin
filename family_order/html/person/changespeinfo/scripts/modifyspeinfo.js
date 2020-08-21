
function refreshPartAtferAuth(data)
{
	$.ajax.submit('', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString(), 'ChangeDataPart', function(data){
		
		$("#E_BRAND_CODE").attr('disabled',false);
		$("#USER_TYPE_CODE").attr('disabled',false);
		$("#SIM_CARD_NO").attr('disabled',false);
		$("#IMSI").attr('disabled',false);
		
		$("#DEVELOP_STAFF_ID").attr('disabled',false);
		//$("#IMEI").attr('disabled',false);
		$("#REMARK").attr('disabled',false);
		
		/*if(data.get('MODSPEINFO_TAG') == '1')
		{
			$("#CITY_CODE").attr('disabled',true);
		    $("#USER_STATE_CODESET").attr('disabled',true);
		}
		else
		{*/
			$("#CITY_CODE").attr('disabled',false);
		    $("#USER_STATE_CODESET").attr('disabled',false);
		//}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
		$.auth.reflushPage();
    });
}

function imsiCheck(){
	var imsi = $("#IMSI").val();
	if(imsi=="")return;
	$.ajax.submit('', 'imsiCheck', '&IMSI='+imsi, '', function(data){
		if(data.get("SIM_CARD_NO")!=""){
			$("#SIM_CARD_NO").val(data.get('SIM_CARD_NO'));
		}else{
			alert('根据录入的用户IMSI没有找到对应的用户卡号SIM_CARD_NO数据！');
			$("#IMSI").val('');
			$("#IMSI").focus();
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
		$("#IMSI").val('');
		$("#IMSI").focus();
		return false;
    });
	
}
function simCardNoCheck(){
	var simCardNo = $("#SIM_CARD_NO").val();
	if(simCardNo=="")return;
	$.ajax.submit('', 'simCardNoCheck', '&SIM_CARD_NO='+simCardNo, '', function(data){
		if(data.get("IMSI")!=""){
			$("#IMSI").val(data.get("IMSI"));
		}else{
			alert('根据录入的用户卡号没有找到对应的IMSI数据！');
			$("#SIM_CARD_NO").val('');
			$("#SIM_CARD_NO").focus();
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
		$("#SIM_CARD_NO").val('');
		$("#SIM_CARD_NO").focus();
		return false;
    });
}

function onTradeSubmit()
{
	if(!$.validate.verifyAll()) return false;
     return true;
}