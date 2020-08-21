function conjuction(){
   var opercode = $('#OPERCODE').val();
   
   if(opercode==""){
   		alert("请选择操作类型");
    	return false;
    }
    
    var newd = $.DataMap();
    newd.put('OPERCODE',opercode);
    
 	parent.$('#POP_CODING_STR').val("操作类型:"+opercode);
	parent.$('#CODING_STR').val(newd);
 	
	parent.hiddenPopupPageGrp();
  }