function showDetailInfo(objevent){
	var buttonObj = $(objevent);
	//var buttonOffect = $(objevent).position();
	var buttonId = 	buttonObj.attr('id');
			
	var comboxName = buttonId.replace('_BUTTON','');
	var buttonOffect = $('#'+comboxName).offset();
	
	var topAdd = 0;
	var scroll =  $("div[class=m_wrapper]:first");
	if(scroll.length>0){
		topAdd = scroll.attr("scrollTop");
	}
	var top = buttonOffect.top+topAdd+20;
	var left = buttonOffect.left-40;
	showCombo(comboxName,top,left);
}


function comboxBodyClickAction(e){
	var td = null;
	if (e.target.tagName == 'TD') {
		td = e.target;
	} else {
		return ;
	}
	var tr = td.parentNode;
	var tbody = tr.parentNode;
	var tbodyId = tbody.id;
	var comboxName = tbodyId.replace('_TBODY','');
	
	hiddenCombo(comboxName);
	
	var trvalue =$(tr).attr("valueField");
	$("#"+comboxName).val(trvalue);
	$("#"+comboxName).click();
}


function showCombo(id,top,left){
	var comboxName = id+"_OPTION";
	$("#"+comboxName).css("display","");
	$("#"+comboxName).css("top", top + "px");
	$("#"+comboxName).css("left", (left) + "px");

}

function hiddenCombo(id){
	var comboxName = id+"_OPTION";
	$("#"+comboxName).css("display","none");

}

//生成comboBoxOption列表信息
function renderComboBoxHtml(comboBoxs,viewid){
	
	var optionId = viewid+"_OPTION";
	var optionIdObj = $('#'+optionId).length;
	if(optionIdObj ==0 ){
		renderComboBoxOptionHtml(comboBoxs,viewid);
	}else{
		renderComboBoxTBodyHtml(comboBoxs,viewid);
	}
}

//生成comboBoxTBODY列表信息
function renderComboBoxTBodyHtml(comboBoxs,viewid){
	
	var comboBoxsTBodyName = viewid+"_TBODY";
	comboBoxs.each(function(item,idx){
		
		$("#"+comboBoxsTBodyName).prepend(makeComboBoxTRHtml(item,viewid));
			
	});
	
}

function renderComboBoxOptionHtml(comboBoxs,viewid){
	var comboBoxsTBodyName =   $("div[class=m_wrapper2]:first");
	comboBoxsTBodyName.prepend(makeComboBoxOptionHtml(comboBoxs,viewid));
	addTableScript(viewid);
}

function makeComboBoxOptionHtml(comboBoxs,viewid){
	var opntionId = viewid+"_OPTION";
	var tbodyid = viewid+"_TBODY";
	var tableId = viewid+"_TABLE";
	var optionWidthName = opntionId+"_WIDTH";
	var filedHeadsName = viewid+"_SHOWHEADS_INPUT";
	var html ="";
	var optionWidth = $('#'+optionWidthName).val();
	html += ' <div class="c_option" style="position:absolute;top:0px; left:0px; width:'+optionWidth+'px; display:none;" id="'+opntionId+'"> ';
	html +='  <div class="c_optionContent"> ';
	html +='  <div class="c_scroll c_scroll-x c_scroll-table-5 " ><div class="c_table c_table-hover " > ';
	
	html +=' <table id="'+tableId+'" name="'+tableId+'"> ';
	html +='  <thead> ';
	html +='  <tr> ';
	var filedHeadStr = $('#'+filedHeadsName).val();
	var filedHeads = filedHeadStr.split(',');
	for(var k = 0; k < filedHeads.length; k++ ){
		html +='  <th> ' +filedHeads[k]+'</th>';
	}
	html +='  </tr> ';
	html +='  </thead> ';
	
	html +='  <tbody id="'+tbodyid+'"> ';
	comboBoxs.each(function(item,idx){
		
		html += makeComboBoxTRHtml(item,viewid);
			
	});
	html +='  </tbody> ';
	html +='  </table> ';
	html +=' </div> </div> ';
	html +=' </div> ';
	html +='  <div class="c_optionShadow"></div> ';
	html +=' </div> ' ;
	return html;
}
//生成comboBoxTBODY的TR行信息
function makeComboBoxTRHtml(item,viewid){

	var showFiledStr = $('#'+viewid+'_SHOWFILEDS_INPUT').val();
	var valueFiledKey = $('#'+viewid+'_VALUEFILED_INPUT').val();
	var valueFiledValue = item.get(valueFiledKey);
	var html="";
	html += '<tr valueField="'+valueFiledValue+'">';
	var showFiledS = showFiledStr.split(',');
	for( var i = 0; i< showFiledS.length; i++){
		var showFiledColumn = showFiledS[i];
		var showFiledValue = item.get(showFiledColumn);
		html += '<td >' + showFiledValue+ '</td>';
	}
	html += '</tr>';
	
	return html;
}

function cleanComboBoxTBodyPart(viewId){
	$("#"+viewId+"_TBODY").html('');
	
}

function addTableScript(viewId){

     $('#'+viewId+'_TABLE').click(function(e){
         comboxBodyClickAction(e);
   	 });
}