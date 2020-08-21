/*查询列表变更*/
function changeVpmnShortCode() {  
	choose=$('#cond_QueryType').val(); 
	if (choose=='0'){
	  $('#QueryTypeOne').css('display','');
	  $('#QueryTypeTwo').css('display','none');
	  $('#QueryTypeThree').css('display','none');
	  $('#QueryTypeFour').css('display','none');
 	  $('#cond_SERIAL_NUMBER').val('');
 	  $('#cond_GROUP_ID').val('');
 	  $('#cond_MSERIAL_NUMBER_A').val('');
	}else if (choose=='1'){
	  $('#QueryTypeOne').css('display','none');
	  $('#QueryTypeTwo').css('display','');
	  $('#QueryTypeThree').css('display','none');
	  $('#QueryTypeFour').css('display','none');
 	  $('#cond_SERIAL_NUMBER_A').val('');
 	  $('#cond_GROUP_ID').val('');
 	  $('#cond_MSERIAL_NUMBER_A').val('');
	}else if (choose=='2'){    //j2ee 移除
	  $('#QueryTypeOne').css('display','none');
	  $('#QueryTypeTwo').css('display','none');
	  $('#QueryTypeThree').css('display','');
	  $('#QueryTypeFour').css('display','none');
	  $('#cond_SERIAL_NUMBER').val('');
 	  $('#cond_SERIAL_NUMBER_A').val('');
 	  $('#cond_MSERIAL_NUMBER_A').val('');
	}else if (choose=='3'){
	  $('#QueryTypeOne').css('display','none');
	  $('#QueryTypeTwo').css('display','none');
	  $('#QueryTypeThree').css('display','none');
	  $('#QueryTypeFour').css('display','');
	  $('#cond_SERIAL_NUMBER').val('');
 	  $('#cond_SERIAL_NUMBER_A').val('');
 	 $('#cond_GROUP_ID').val('');
	}
	
}

function qryClick()
{	  
	if(!chkParam()){
		return false;
	} 
	$('#RefreshPart').html('');
	$.beginPageLoading("数据查询中......");
	$.ajax.submit('QueryCondPart', 'queryInfos', '', 'RefreshPart,refreshHintBar', function(data){
			$.endPageLoading();
		},
		function(error_code, error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code, error_info, derror);
		}
	);
}

function chkParam(){
	var serailnumerA =$('#cond_SERIAL_NUMBER_A').val();
 	var MserailnumerA =$('#cond_MSERIAL_NUMBER_A').val();
 	var serailnumer =$('#cond_SERIAL_NUMBER').val();
 	var groupid =$('#cond_GROUP_ID').val();
    var short_type =$('#cond_ShortType').val();  //短号位长 3456
	var short_length = $('#cond_SHORT_LENGTH').val();  //第二位起数字
	choose=$('#cond_QueryType').val();
	
	if(choose=='0'){
    	if(serailnumerA=='') {
	    	alert('请输入VPMN编码！');
	    	return false ;
    	}
    }
    
    if(choose=='1'){
    	if(serailnumer=='') {
	    	alert('请输入手机号码！');
	    	return false;
    	}
    }
    
    if(choose=='2'){
    	if(groupid=='') {
	    	alert('请输入集团编码！');
	    	return false;
    	}
    }
    
    if(choose=='3'){
    	if(MserailnumerA=='') {
	    	alert('请输入母VPMN编码！');
	    	return false;
    	}
    }
     
	if(short_type == 5 && short_length.length==0) {
		alert('请输入从第二位起1位或以上数字!');
		return false;
	}
	if(short_type == 6 && short_length.length<2) {
		alert('请输入从第二位起2位或以上数字!');
		return false;
	}
//	if(short_type == 7 && short_length.length<2) {
//		alert('请输入从第二位起2位或以上数字!');
//		return false;
//	}
//	if(short_type == 7 && short_length.length>=2) {
//		short_type = null;
//	}  
	return true;
}

function exportBeforeAction(domid) { 
//	alert("点击[导出]按钮时执行的JS方法exportBeforeAction,在这里可以用JS动态设置传到后台的参数"); 
//	$.Export.get(domid).setParams("&a=a&b=b"); 
	return chkParam(); 
}