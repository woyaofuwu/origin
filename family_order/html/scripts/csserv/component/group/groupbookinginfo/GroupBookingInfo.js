function renderGroupBookingInfo(productId){
	
	var busiType = $('#GROUPBOOKING_INFO_BUSI_TYPE').val();
	Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.groupbookinginfo.GroupBookingInfoHttpHandler','getGroupBookingTagInfo','&PRODUCT_ID='+productId+'&BUSI_TYPE='+busiType,
   	function(data){
   		if(data != null){
   			var bookingFlag = data.get("cond_Booking_Flag");
   			if(bookingFlag == "true"){
   				makeGroupBookingPart("false","false");
   			}else{
   				initHiddenGroupBookingPart();
   				
   			}
   		}
    	
   	},
	function(error_code,error_info,derror){
	
		showDetailErrorInfo(error_code,error_info,derror);
		
	});
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
	var checkduan = bookingChecked=="true"?' checked="true" ':''+bookingDisabled=="true"?' disabled="true" ':'';
	
	var bookingBoxAction = $('#GROUPBOOKING_INFO_BOOKING_BOX_ACTION').val();
	if(bookingBoxAction == ''){
		bookingBoxAction = 'clickBookingBox()';
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

function setBookingCheck(checkTag) {
	if(typeof($("#divEffectNow").val()) == 'undefined'){
		makeGroupBookingPart(checkTag,checkTag);
	}else{
		$("#divEffectNow").attr("checked",checkTag=='true'?true:false);
	}
}