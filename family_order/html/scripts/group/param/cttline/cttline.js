	function init(){
		var method =  $("#pam_NOTIN_METHOD_NAME").val();
		var state =  $("#pam_NOTIN_USER_DATELINE_STATE").val();

	}

	
	function initCrtUs() {
	
	}

	function createWideData(){
		
		//宽带号码
    	var acctId = $("#pam_NOTIN_WIDE_ACCT_ID").val();
    	//宽带时长
    	var month = $("#pam_NOTIN_WIDE_MONTH").val();
    	//带宽
    	var netLine = $("#pam_NOTIN_WIDE_NET_LINE").val();
    	
    	 //专线宽带
    	if (acctId == ""){
			alert ("请填写宽带号码！");
			return false;
		}
		//专线实例号
  	    if (month == ""){
			alert ("请填写宽带时长！");
			return false;
		}
		//业务标识
		if (netLine == ""){
			alert ("请填带宽！");
			return false;
		}
    	var pamAttrList = $.table.get("WideTable").getTableData(null,true);
		
		if("" != pamAttrList){
			for(var i=0; i<pamAttrList.length ;i++){
				var attrs = pamAttrList.get(i);
				var attrsAcctId = attrs.get("pam_NOTIN_WIDE_ACCT_ID");
				var tag = attrs.get("tag");
				if(acctId == attrsAcctId && tag != 1){
					alert ("该宽带号码已添加请重新选择！");
					return false;
				}
			}
		}
    	
		var visplineData = $.ajax.buildJsonData("widePart");
		$.table.get("WideTable").addRow(visplineData);
		
		$("#pam_NOTIN_WIDE_ACCT_ID").val("");
		$("#pam_NOTIN_WIDE_MONTH").val("");
		$("#pam_NOTIN_WIDE_NET_LINE").val("");
	}
	
	function deleteWideData(){
		$.table.get("WideTable").deleteRow();
	}
	
	function updateWideData(){
		//宽带号码
    	var acctId = $("#pam_NOTIN_WIDE_ACCT_ID").val();
    	//宽带时长
    	var month = $("#pam_NOTIN_WIDE_MONTH").val();
    	//带宽
    	var netLine = $("#pam_NOTIN_WIDE_NET_LINE").val();
    	
    	 //专线宽带
    	if (acctId == ""){
			alert ("请填写宽带号码！");
			return false;
		}
		//专线实例号
  	    if (month == ""){
			alert ("请填写宽带时长！");
			return false;
		}
		//业务标识
		if (netLine == ""){
			alert ("请填带宽！");
			return false;
		}
		var pamAttrList = $.table.get("WideTable").getTableData(null,true);
    
   		var rowData = $.table.get("WideTable").getRowData();
    
    	var rowAcctId = rowData.get("pam_NOTIN_WIDE_ACCT_ID");
    	
    	if("" != pamAttrList){
			for(var i=0; i<pamAttrList.length ;i++){
				var attrs = pamAttrList.get(i);
				var attrAcctId = attrs.get("pam_NOTIN_WIDE_ACCT_ID");
				var tag = attrs.get("tag");
				if(rowAcctId == acctId){
					break ;
				}
				if(acctId == attrAcctId && tag != 1){
					alert ("该专线已添加请重新选择！");
					return false;
				}
			}
		}
		
		var visplineData = $.ajax.buildJsonData("widePart");
		$.table.get("WideTable").updateRow(visplineData);
		
		$("#pam_NOTIN_WIDE_ACCT_ID").val("");
		$("#pam_NOTIN_WIDE_MONTH").val("");
		$("#pam_NOTIN_WIDE_NET_LINE").val("");
	
	}
	
	function tableRowClickWide(){
		//获取选择行的数据
		 var rowData = $.table.get("WideTable").getRowData();
		 $("#pam_NOTIN_WIDE_ACCT_ID").val(rowData.get("pam_NOTIN_WIDE_ACCT_ID"));
		 $("#pam_NOTIN_WIDE_MONTH").val(rowData.get("pam_NOTIN_WIDE_MONTH"));
		 $("#pam_NOTIN_WIDE_NET_LINE").val(rowData.get("pam_NOTIN_WIDE_NET_LINE"));
	}
	
	
	
	
	
	
	
	
	function createFixedData(){
		//固话号码
    	var phone = $("#pam_NOTIN_FIXED_PHONE").val();
    	//金额
    	var money = $("#pam_NOTIN_FIXED_MONEY").val();
    	
    	 //专线宽带
    	if (phone == ""){
			alert ("请填写固话号码！");
			return false;
		}
		//金额
  	    if (money == ""){
			alert ("请填写金额！");
			return false;
		}
    	
    	var pamAttrListFixed = $.table.get("FixedTable").getTableData(null,true);
		
		if("" != pamAttrListFixed){
			for(var i=0; i<pamAttrListFixed.length ;i++){
				var fixedlist = pamAttrListFixed.get(i);
				var attrPhone = fixedlist.get("pam_NOTIN_FIXED_PHONE");
				var tag = fixedlist.get("tag");
				if(phone == attrPhone && tag != 1){
					alert ("该固话号码已添加请重新选择！");
					return false;
				}
			}
		}
    	
		var fixedData = $.ajax.buildJsonData("fixedPart");
		$.table.get("FixedTable").addRow(fixedData);
		
		$("#pam_NOTIN_FIXED_PHONE").val("");
		$("#pam_NOTIN_FIXED_MONEY").val("");
	}
	
	function deleteFixedData(){
		$.table.get("FixedTable").deleteRow();
	}
	
	function updateFixedData(){
		//固话号码
    	var phone = $("#pam_NOTIN_FIXED_PHONE").val();
    	//金额
    	var money = $("#pam_NOTIN_FIXED_MONEY").val();
    	
    	 //专线宽带
    	if (phone == ""){
			alert ("请填写固话号码！");
			return false;
		}
		//金额
  	    if (money == ""){
			alert ("请填写金额！");
			return false;
		}
		
		
		var pamAttrList = $.table.get("FixedTable").getTableData(null,true);
    
   		var rowData = $.table.get("FixedTable").getRowData();
    
    	var rowAcctId = rowData.get("pam_NOTIN_FIXED_PHONE");
    	
    	if("" != pamAttrList){
			for(var i=0; i<pamAttrList.length ;i++){
				var attrs = pamAttrList.get(i);
				var attrAcctId = attrs.get("pam_NOTIN_FIXED_PHONE");
				var tag = attrs.get("tag");
				if(rowAcctId == phone){
					break ;
				}
				if(phone == attrAcctId && tag != 1){
					alert ("该固话号码已添加请重新选择！");
					return false;
				}
			}
		}
		
		var visplineData = $.ajax.buildJsonData("fixedPart");
		$.table.get("FixedTable").updateRow(visplineData);
		
		$("#pam_NOTIN_FIXED_PHONE").val("");
		$("#pam_NOTIN_FIXED_MONEY").val("");
	
	}
	
	
	function tableRowClickFixed(){
		//获取选择行的数据
		 var rowData = $.table.get("FixedTable").getRowData();
		 $("#pam_NOTIN_FIXED_PHONE").val(rowData.get("pam_NOTIN_FIXED_PHONE"));
		 $("#pam_NOTIN_FIXED_MONEY").val(rowData.get("pam_NOTIN_FIXED_MONEY"));
	}
	
	
	
	
	
	
	function createSerialData(){
		//固话号码
    	var phone = $("#pam_NOTIN_SERIAL_PHONE").val();
    	//金额
    	var money = $("#pam_NOTIN_SERIAL_MONEY").val();
    	
    	 //专线宽带
    	if (phone == ""){
			alert ("请填写固话号码！");
			return false;
		}
		//金额
  	    if (money == ""){
			alert ("请填写金额！");
			return false;
		}
    	
    	var pamAttrList = $.table.get("SerialTable").getTableData(null,true);
	
		if("" != pamAttrList){
			for(var i=0; i<pamAttrList.length ;i++){
				var attrs = pamAttrList.get(i);
				var attrsAcctId = attrs.get("pam_NOTIN_SERIAL_PHONE");
				var tag = attrs.get("tag");
				if(phone == attrsAcctId && tag != 1){
					alert ("该固话号码已添加请重新选择！");
					return false;
				}
			}
		}
    	
		var visplineData = $.ajax.buildJsonData("serialPart");
		$.table.get("SerialTable").addRow(visplineData);
		
		$("#pam_NOTIN_SERIAL_PHONE").val("");
		$("#pam_NOTIN_SERIAL_MONEY").val("");
	}
	
	function deleteSerialData(){
		$.table.get("SerialTable").deleteRow();
	}
	
	function updateSerialData(){
		//固话号码
    	var phone = $("#pam_NOTIN_SERIAL_PHONE").val();
    	//金额
    	var money = $("#pam_NOTIN_SERIAL_MONEY").val();
    	
    	 //专线宽带
    	if (phone == ""){
			alert ("请填写固话号码！");
			return false;
		}
		//金额
  	    if (money == ""){
			alert ("请填写金额！");
			return false;
		}
		
		
		var pamAttrListSerial = $.table.get("SerialTable").getTableData(null,true);
    
   		var rowData = $.table.get("SerialTable").getRowData();
    
    	var rowAcctId = rowData.get("pam_NOTIN_SERIAL_PHONE");
    	
    	if("" != pamAttrListSerial){
			for(var i=0; i<pamAttrListSerial.length ;i++){
				var attrsSerial = pamAttrListSerial.get(i);
				var attrAcctId = attrsSerial.get("pam_NOTIN_SERIAL_PHONE");
				var tag = attrsSerial.get("tag");
				if(rowAcctId == phone){
					break ;
				}
				if(phone == attrAcctId && tag != 1){
					alert ("该固话号码已添加请重新选择！");
					return false;
				}
			}
		}
		
		var visplineData = $.ajax.buildJsonData("serialPart");
		$.table.get("SerialTable").updateRow(visplineData);
		
		$("#pam_NOTIN_SERIAL_PHONE").val("");
		$("#pam_NOTIN_SERIAL_MONEY").val("");
	
	}
	
	
	function tableRowClickSerial(){
		//获取选择行的数据
		 var rowData = $.table.get("SerialTable").getRowData();
		 $("#pam_NOTIN_SERIAL_PHONE").val(rowData.get("pam_NOTIN_SERIAL_PHONE"));
		 $("#pam_NOTIN_SERIAL_MONEY").val(rowData.get("pam_NOTIN_SERIAL_MONEY"));
	}
	
	
	function showLayer(obj){
		var sbtitle = document.getElementById(obj);
		if(sbtitle){
	   		if(sbtitle.style.display=='block'){
	   			sbtitle.style.display='none';
	  		}else{
	   			sbtitle.style.display='block';
	   		}
		}
	}
	function hideLayer(obj){
		var sbtitle = document.getElementById(obj);
		if(sbtitle){
	   		if(sbtitle.style.display=='none'){
	   			sbtitle.style.display='block';
	  		}else{
	   			sbtitle.style.display='none';
	   		}
		}
	}
	
	
	
	
	function validateParamPage(methodName) {
		var wideAttr = $.table.get("WideTable").getTableData(null, true);
		var fixedAttr = $.table.get("FixedTable").getTableData(null, true);
		var serialAttr = $.table.get("SerialTable").getTableData(null, true);
		
		var oldWideAttr = $("#pam_NOTIN_OLD_WideData").val();
		var oldFixedAttr = $("#pam_NOTIN_OLD_FixedData").val();
		var oldSerialAttr = $("#pam_NOTIN_OLD_SerialData").val();
		
		
		var wideList = new Wade.DatasetList();
		var fixedList = new Wade.DatasetList();
		var serialList = new Wade.DatasetList();
		
		var wideList = compareDatasetWide(wideAttr,oldWideAttr);
		$("#pam_NOTIN_WideData").val(wideList);
		
		var fixedList = compareDatasetFixed(fixedAttr,oldFixedAttr);
		$("#pam_NOTIN_FixedData").val(fixedList);
		
		var serialList = compareDatasetSerial(serialAttr,oldSerialAttr);
		$("#pam_NOTIN_SerialData").val(serialList);
		
	    return true;
	}
	
	function compareDatasetWide(strNewValue,strOldValue){
		if (strOldValue == ""){
	        strOldValue="[]";
		}
	    if (strNewValue == ""){
	        strNewValue == "[]";
	    }
	    
	    var newValueSet = strNewValue;
	    var oldValueSet = new Wade.DatasetList(strOldValue);
	    var resultSet = new Wade.DatasetList();
	    
	    
	    for (var i=0;i<newValueSet.length;i++){
	    	var isfound = "false";
	    	var newValueColumn = newValueSet.get(i);
		    for (var j=0;j<oldValueSet.length;j++) {
		        var oldValueColumn = oldValueSet.get(j);
		        if(oldValueColumn.get('pam_NOTIN_WIDE_ACCT_ID') == newValueColumn.get('pam_NOTIN_WIDE_ACCT_ID')){
		        	if(newValueColumn.get('tag') == "2"){
		        		var isfound = "true";
						resultSet.add(newValueColumn);	        	
		        	}
		        }
		    }
		    if(isfound == "false"){
	       		resultSet.add(newValueColumn);
		    }
		}
		
		
		for (var i=0;i<oldValueSet.length;i++) {
	        var oldValueColumn = oldValueSet.get(i);
	        isfound = "false";
	        for (var j=0;j<newValueSet.length;j++) {
	            var newValueColumn = newValueSet.get(j);
	            if(oldValueColumn.get('pam_NOTIN_WIDE_ACCT_ID') == newValueColumn.get('pam_NOTIN_WIDE_ACCT_ID'))
	            {
	            	isfound = "true";
	            	break;
	            }
	        }
	        if (isfound == "false") {
	            oldValueColumn.put("tag","1");
	            resultSet.add(oldValueColumn);
	        }
	   }
	   
	  return resultSet.toString();
	
}
	
	function compareDatasetFixed(strNewValue,strOldValue){
		if (strOldValue == ""){
	        strOldValue="[]";
		}
	    if (strNewValue == ""){
	        strNewValue == "[]";
	    }
	    
	    var newValueSet = strNewValue;
	    var oldValueSet = new Wade.DatasetList(strOldValue);
	    var resultSet = new Wade.DatasetList();
	    
	    
	    for (var i=0;i<newValueSet.length;i++){
	    	var isfound = "false";
	    	var newValueColumn = newValueSet.get(i);
		    for (var j=0;j<oldValueSet.length;j++) {
		        var oldValueColumn = oldValueSet.get(j);
		        if(oldValueColumn.get('pam_NOTIN_FIXED_PHONE') == newValueColumn.get('pam_NOTIN_FIXED_PHONE')){
		        	if(newValueColumn.get('tag') == "2"){
		        		var isfound = "true";
						resultSet.add(newValueColumn);	        	
		        	}
		        }
		    }
		    if(isfound == "false"){
	       		resultSet.add(newValueColumn);
		    }
		}
		
		
		for (var i=0;i<oldValueSet.length;i++) {
	        var oldValueColumn = oldValueSet.get(i);
	        isfound = "false";
	        for (var j=0;j<newValueSet.length;j++) {
	            var newValueColumn = newValueSet.get(j);
	            if(oldValueColumn.get('pam_NOTIN_FIXED_PHONE') == newValueColumn.get('pam_NOTIN_FIXED_PHONE'))
	            {
	            	isfound = "true";
	            	break;
	            }
	        }
	        if (isfound == "false") {
	            oldValueColumn.put("tag","1");
	            resultSet.add(oldValueColumn);
	        }
	   }
	   
	  return resultSet.toString();
	
}
	
	
	
	function compareDatasetSerial(strNewValue,strOldValue){
		if (strOldValue == ""){
	        strOldValue="[]";
		}
	    if (strNewValue == ""){
	        strNewValue == "[]";
	    }
	    
	    var newValueSet = strNewValue;
	    var oldValueSet = new Wade.DatasetList(strOldValue);
	    var resultSet = new Wade.DatasetList();
	    
	    
	    for (var i=0;i<newValueSet.length;i++){
	    	var isfound = "false";
	    	var newValueColumn = newValueSet.get(i);
		    for (var j=0;j<oldValueSet.length;j++) {
		        var oldValueColumn = oldValueSet.get(j);
		        if(oldValueColumn.get('pam_NOTIN_SERIAL_PHONE') == newValueColumn.get('pam_NOTIN_SERIAL_PHONE')){
		        	if(newValueColumn.get('tag') == "2"){
		        		var isfound = "true";
						resultSet.add(newValueColumn);	        	
		        	}
		        }
		    }
		    if(isfound == "false"){
	       		resultSet.add(newValueColumn);
		    }
		}
		
		
		for (var i=0;i<oldValueSet.length;i++) {
	        var oldValueColumn = oldValueSet.get(i);
	        isfound = "false";
	        for (var j=0;j<newValueSet.length;j++) {
	            var newValueColumn = newValueSet.get(j);
	            if(oldValueColumn.get('pam_NOTIN_SERIAL_PHONE') == newValueColumn.get('pam_NOTIN_SERIAL_PHONE'))
	            {
	            	isfound = "true";
	            	break;
	            }
	        }
	        if (isfound == "false") {
	            oldValueColumn.put("tag","1");
	            resultSet.add(oldValueColumn);
	        }
	   }
	   
	  return resultSet.toString();
	
}
	