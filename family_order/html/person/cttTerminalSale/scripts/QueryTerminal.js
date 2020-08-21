var chooseCttTerminal = function() {
	
	var isChooseTerminal=false;
	
	var tableObj = $.table.get("terminalInfo");
    var tbodyObj = $("tbody", tableObj.getTable()[0]);
    var size = tableObj.tabHeadSize;
    if(tbodyObj){
       $("tr", tbodyObj[0]).each(function(index, item){
           var checked = $("input[name=CHOOSE_TERMINAL]", item).attr("checked");
           if(checked){
        	   isChooseTerminal=true;
        	   var json = tableObj.getRowData(null, index+size);
	        	$.getSrcWindow().$("#TERMINAL_TYPE_NAME").attr('value',json.get("KIND_NAME"));
	        	$.getSrcWindow().$("#TERMINAL_TYPE").attr('value',json.get("RES_KIND_CODE"));
	        	$.getSrcWindow().$("#TERMINAL_CODE_NAME").attr('value',json.get("MODEL_DESC"));
	        	$.getSrcWindow().$("#TERMINAL_CODE").attr('value',json.get("MODEL_CODE"));
	        	$.getSrcWindow().$("#TERMINAL_PRICE").attr('value',json.get("SALEPRICE"));
	        	
	        	$.closePopupPage(true,null,null,null,null,true);   
	        	
	        	return false;
           }
       });
    }

	 if(!isChooseTerminal){
		 alert("请选择终端！");
		 return false;
	 }
	 
};


function queryCttTerminal(){
	if($.validate.verifyAll("cond")){
		$.beginPageLoading("查询中...");
		$.ajax.submit("cond","qryTerminalInfo",null,'terminalList', afterQuery);
	}
}

function afterQuery(data){
	$.endPageLoading();
	if(data&&data.length>0){
		$("#TipInfoPart").css("display","none");
	}
	else{
		$("#TipInfoPart").css("display","");
	}
}