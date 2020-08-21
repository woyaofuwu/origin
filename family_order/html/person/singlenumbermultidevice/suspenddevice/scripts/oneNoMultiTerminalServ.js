function tradeActionFun(data)
{
	$.ajax.submit('AuthPart', 'queryAuxDevInfos', '', 'QueryListPart', function(returnData){
		
	},	
	function(error_code,error_info){
		$("#CSSUBMIT_BUTTON").attr("disabled", true);
		$("#SubmitPart").attr("class", "e_dis");
		$.endPageLoading();
		alert(error_info);
    });
}


/*业务提交*/
function checkBeforeSubmit()
{	
	var num = 0;	//前台提示终止记录条数	
//	var auxcodes = "";//拼提交到后台的副设备记录
	var auxnicknames = "";//拼前台提示副设备昵称记录
	
	var checkedDatas = new Wade.DatasetList();//拼提交到后台的副设备记录
	$("input[name='ORDERNO']:checkbox:checked").each(function(){
	   if(this.checked) {
		   //拼提交到后台的副设备记录		   
		   var data =  new Wade.DataMap();
		   data.put("ORDERNO", $(this).val());
		   data.put("INST_ID",$(this).attr("inst_id"));
		   data.put("USER_ID_B", $(this).attr("user_id_b"));
		   data.put("AUX_NICK_NAME", $(this).attr("aux_nick_name"));
		   data.put("SERIAL_NUMBER_B", $(this).attr("serial_number_b"));
		   checkedDatas.add(data);
		   
//		   auxcodes = auxcodes + "["
//		   auxcodes = auxcodes + $(this).val();
//		   auxcodes = auxcodes + "::";
//		   auxcodes = auxcodes + $(this).attr("inst_id");
//		   auxcodes = auxcodes + "::";
//		   auxcodes = auxcodes + $(this).attr("user_id_b");
//		   auxcodes = auxcodes + "::";
//		   auxcodes = auxcodes + $(this).attr("aux_nick_name");
//		   auxcodes = auxcodes + "]"
//		   auxcodes = auxcodes + ",";
	    	
			//拼已选优惠提交后台的串
		   auxnicknames = auxnicknames + $(this).attr("aux_nick_name");
		   auxnicknames = auxnicknames + ",";
		   num = num+1;
	   }
	});
	if(auxnicknames == "") {
		alert("请先选择需要操作的副设备，再提交！");
		return false;
	}
//	auxcodes = auxcodes.substring(0, auxcodes.length-1);
	auxnicknames = auxnicknames.substring(0, auxnicknames.length-1);

	//前台提示
	if(!confirm( "您确认将以下" + num + "个副设备提交业务吗？\n" + auxnicknames)) {
		return false;
	}
	//提交参数
//	iData.toString()
	var param = "&AUXCODES=" + checkedDatas.toString() ;
//	alert(param);
	$.cssubmit.addParam(param);
	return true;
}