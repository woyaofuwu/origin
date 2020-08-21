
function initDiverTip(bookingTag){
	var diverTipName = $('#JUDGEMEBACCTDAY_DIVER_TIP_KEY').val();
  	$('#'+diverTipName).css('display','none');
	$('#'+diverTipName).html('');
  	if(bookingTag == 'true'){
  		$('#acctDayBox').css('display','');
  		makeGroupBookingPart('false','false');
  	}else{
	  	$('#acctDayBox').css('display','none');
	  	initHiddenGroupBookingPart();
	}	
	
}

function setDiverTip(userAcctDayDistribuion,productNatureTag,productImmediTag,fistDayNextAcct,payType,bookingTag){
	
	 initDiverTip(bookingTag);
	 var diverTip = getDiverTip(userAcctDayDistribuion,productNatureTag,productImmediTag,fistDayNextAcct,payType,bookingTag);
	 if(diverTip !=''){
	 	$('#acctDayBox').css('display','');
	 	var diverTipName = $('#JUDGEMEBACCTDAY_DIVER_TIP_KEY').val();
	 	$('#'+diverTipName).css('display','');
	 	$('#'+diverTipName).html('提示:'+diverTip);
	 }
}

function getDiverTip(userAcctDayDistribuion,productNatureTag,productImmediTag,fistDayNextAcct,payType,bookingTag){
	 
	 //自然月出账的用户不需要做任何改动
	 var diverTip ="";
	 if(userAcctDayDistribuion=='' || userAcctDayDistribuion=='0'){
	 	return '';
	 }
	 if(payType == 'undefined' || payType ==null || payType=='' ){
	 	payType='';
	 }
	 if(productNatureTag == 'undefined' || productNatureTag ==null || productNatureTag=='' ){
	 	return '';
	 }
	 
	 if(productImmediTag == 'undefined' || productImmediTag ==null || productImmediTag=='' ){
	 	return '';
	 }
	 
	 if(productNatureTag=='true' ){
	 		diverTip ='此产品不支持分散账期的用户';
		 	
		 	if(userAcctDayDistribuion=='2' || userAcctDayDistribuion=='3' ){
		 		diverTip += ',当前用户不容许办理业务!';
		 		return diverTip;
		 	}
		 	
		 	if(userAcctDayDistribuion=='4'){
	 			diverTip += ',当前用户必须将出账日改为1号才可办理业务!';
	 			return diverTip;
	 		}
	 		
	 		if(userAcctDayDistribuion=='1' && productImmediTag == 'true'){
	 			
	 			if(payType == ''){
	 				diverTip += ',当前用户选择个人付费,可以立即生效!选择集团付费,可以办理预约业务!';
	 			}else if(payType == 'P'){
	 				diverTip += ',当前用户选择个人付费,可以立即生效!';
	 			}else{
	 				diverTip += ',当前用户可办理预约业务!';
	 				setBookingCheckAndDisabled('true','true');
	 			}
	 			
	 			return diverTip;
	 		}
	 		
	 		if(userAcctDayDistribuion=='1' && productImmediTag == 'false'){
	 			diverTip += ',当前用户可办理预约业务!';
	 			setBookingCheckAndDisabled('true','true');
	 			return diverTip;
	 		}
	 		
	 		diverTip += diverTip+',当前用户账期信息错误,不能办理业务!';
	 		return diverTip;
	 		
	 }
	 
	 if(productNatureTag=='false' ){
	 	if(payType == 'P'){
	 		return diverTip;
	 	}
	 	
	 	if(payType == ''){
	 		diverTip += '集团付费不支持分散账期的用户';
	 		return diverTip;
	 	}
	 	
	 	if(payType == 'G' ){
			diverTip += '集团付费不支持分散账期的用户';
			if(userAcctDayDistribuion=='2' || userAcctDayDistribuion=='3' ){
				diverTip += ',当前用户不容许办理业务!';
		 		return diverTip;
			}
			
			if(userAcctDayDistribuion=='4'){
	 			diverTip += ',当前用户必须将出账日改为1号才可办理业务!';
	 			return diverTip;
	 		}
	 		
	 		if(userAcctDayDistribuion=='1'){
	 			diverTip += ',当前用户可办理预约业务!';
	 			setBookingCheckAndDisabled('true','true');
			 	return diverTip;
	 		}
	 		
		}
		
	 			
	 }
	 
	 
	 return diverTip;
	 
}

function judeAcctDays(userAcctDayDistribuion,productNatureTag,productImmediTag,fistDayNextAcct,payType){
	 
	 //自然月出账的用户不需要做任何改动
	 var diverTip ="";
	 if(userAcctDayDistribuion=='' || userAcctDayDistribuion=='0'){
	 	return true;
	 }
	 if(payType == 'undefined' || payType ==null || payType=='' ){
	 	payType='';
	 }
	 if(productNatureTag == 'undefined' || productNatureTag ==null || productNatureTag=='' ){
	 	return true;
	 }
	 
	 if(productImmediTag == 'undefined' || productImmediTag ==null || productImmediTag=='' ){
	 	return true;
	 }
	 
	 if(productNatureTag=='true' ){
	 		diverTip ='此产品不支持分散账期的用户';
		 	
		 	if(userAcctDayDistribuion=='2' || userAcctDayDistribuion=='3' ){
		 		diverTip += ',当前用户不容许办理业务!';
		 		alert(diverTip);
		 		return false;
		 	}
		 	
		 	if(userAcctDayDistribuion=='4'){
	 			diverTip += ',当前用户必须将出账日改为1号才可办理业务!';
	 			if(confirm(diverTip+'是否确定变更?')){
	 				makePopupChangeAcctDayPart();
  					$('#changeAcctDay').click();
  				}
	 			return false;
	 		}
	 		
	 		if(userAcctDayDistribuion=='1' && productImmediTag == 'true'){
	 			if(payType == 'G'){
	 				var bookingcheck = getBookingCheck();
 					if(bookingcheck== false ){
	 					   diverTip += ',当前用户可办理预约业务!';
					       alert(divTip);
						   return false;
				      }else{
				      	   alert("请注意：此业务为预约业务,生效时间是[" + fistDayNextAcct + "]!");;
						   return true;
				    }
	 			}
	 			return true;
	 		}
	 		
	 		if(userAcctDayDistribuion=='1' && productImmediTag == 'false'){
	 			var bookingcheck = getBookingCheck();
 				if(bookingcheck== false ){
	 				diverTip += ',当前用户可办理预约业务!';
			        alert(divTip);
				    return false;
	 			}else{
	 				alert("请注意：此业务为预约业务,生效时间是[" + fistDayNextAcct + "]!");;
					return true;
	 			}
	 		}
	 		
	 		return true;
	 }
	 
	 if(productNatureTag=='false' ){
	 	
	 	if(payType == 'P')
	 		return true;
	 	
	 	//集团付费情况下
		diverTip += '集团付费不支持分散账期的用户';
		if(userAcctDayDistribuion=='2' || userAcctDayDistribuion=='3' ){
			diverTip += ',当前用户不容许办理业务!';
			alert(diverTip);
	 		return false;
		}
			
		if(userAcctDayDistribuion=='4'){
 			diverTip += ',当前用户必须将出账日改为1号才可办理业务!';
 			if(confirm(diverTip+'是否确定变更?')){
 				makePopupChangeAcctDayPart();
  				$('#changeAcctDay').click();
  			}
 			return false;
 		}
	 		
 		if(userAcctDayDistribuion=='1'){
 			var bookingcheck = getBookingCheck();
 			if(bookingcheck== false ){
				   diverTip += ',当前用户可办理预约业务!';
			       alert(divTip);
				   return false;
			 }else{
		      	   alert("请注意：此业务为预约业务,生效时间是[" + fistDayNextAcct + "]!");;
				   return true;
			 }
 		}
	 		
	}
	 
	 
	 return true;
	 
}
function makePopupChangeAcctDayPart(){
	
	var html="";
	var popuHtml ="$.popupPageExternal('changeacctday.ChangeAcctDay','onInitTrade', '&AUTO_AUTH=true&DISABLED_AUTH=true&ISGRP=TRUE&SERIAL_NUMBER='+$('#cond_SERIAL_NUMBER').val() + '&REMARK='+encodeURIComponent('办理集团产品需要变更账期为自然月'),'调账期变更页面','820','650',null,null,subsys_cfg.personserv,false)";
	html += ' <a jwcid="@Redirect"  id="changeAcctDay" name="changeAcctDay" ';
	html += ' onclick="'+popuHtml+'"/> ';
	$('#popupPageDay').html('');
	$('#popupPageDay').prepend(html);
	;
}


function initSeeGroupBookingPart(){
	$("#bookingcheckbox").html('');
   	$("#bookingcheckbox").css("display", "");
}

function initHiddenGroupBookingPart(){
	$("#bookingcheckbox").html('');
   	$("#bookingcheckbox").css("display", "none");
}

function makeGroupBookingPart(bookingChecked,bookingDisabled){
	if(bookingChecked=='true')
		bookingChecked ='true'
	else 
		bookingChecked ='false';
		
	if(bookingDisabled=='true')
		bookingDisabled ='true'
	else 
		bookingDisabled ='false';
	initSeeGroupBookingPart();
	$("#bookingcheckbox").prepend(makeGroupBookingHtml(bookingChecked,bookingDisabled));

}
function makeGroupBookingHtml(bookingChecked,bookingDisabled){
	var checkduanchecked = bookingChecked=="true"?' checked="true" ':'';
	var checkduandisabled = bookingDisabled=="true"?' disabled="true" ':'';
	var checkduan = checkduanchecked+checkduandisabled;
	var bookingBoxAction = $('#JUDGEMEBACCTDAY_BOOKING_BOX_ACTION').val();
	if(bookingBoxAction==''){
		bookingBoxAction = 'clickBookingBox();';
	}
	
	var html="";
	html += '<ul class="ul">';
	html += '<li class="li">';
	html += '<span class="label" class="e_required" >是否下账期生效：</span>';
	html += '<span class="text" >';
	html += '<input type="checkbox" class="e_checkbox" name="divEffectNow" id="divEffectNow" '+checkduan+'  onclick="'+bookingBoxAction+'" />';
	html += '<input type="text" id="IF_BOOKING" name="IF_BOOKING" value="'+bookingChecked+'" style="display:none" />';
	html += '</span>';
	html += '</li>';
	html += '</ul>';
	
	return html;
}

function clickBookingBox() {
    var effectNow=$("#divEffectNow").attr("checked");
	if(effectNow) {
	    $("#IF_BOOKING").val('true');
	}
	else {
	   	$("#IF_BOOKING").val('false');;	    	
	}
}

function getBookingCheck() {
    var effectNow=$("#divEffectNow").attr("checked");
	return effectNow;
}

function setBookingCheckAndDisabled(checkTag,disabledTag) {
	$('#acctDayBox').css('display','');
	$('#bookingcheckbox').css('display','');
	if(typeof($("#divEffectNow").val()) == 'undefined'){
		makeGroupBookingPart(checkTag,disabledTag);
	}else{
		$("#divEffectNow").attr("checked",checkTag=='true'?true:false);
		$("#divEffectNow").attr("disabled",disabledTag=='true'?true:false);
		clickBookingBox();
	}
}
