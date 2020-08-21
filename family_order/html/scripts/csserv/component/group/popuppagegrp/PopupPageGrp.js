function popupPageGrp(page, listener, params, title, width, height){
	var data ="";
	data+="&HIDDEN=false&IS_RENDER=true&FRAME_NAME="+page+"&FRAME_LISTENER="+listener+"&POPUP_TITLE="+title+"&POPUP_WIDTH="+width+"&POPUP_HEIGTH="+height+params;
	
	var oldFrameName = getFramePageName();
	if(oldFrameName != page){
		$.ajax.submit(null,null,data,$("#POPUPPAGEGRP_COMPONENT_ID").val(),'','');
	}else{
		displayPopupPageGrp();
	}
}


function hiddenPopupPageGrp(){
	$('#popupPageGrp').css('display','none');
}

function displayPopupPageGrp(){
	var framName = $('#POPUPPAGEGRP_FRAME_NAME').val();
	if(framName == ''){
		hiddenPopupPageGrp();
	}else{
		$('#popupPageGrp').css('display','');
	}
}

function cleanPopupPageGrp(){
	hiddenPopupPageGrp();
	$('#popupPageGrp').html('');
	$('#POPUPPAGEGRP_FRAME_NAME').val('');
}

function getFramePageName(){
	return $('#POPUPPAGEGRP_FRAME_NAME').val();
}