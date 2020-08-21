function refreshPartAtferAuth(data){
    var userInfo = data.get("USER_INFO");
    var param = "&USER_ID="+userInfo.get("USER_ID")+"&EPARCHY_CODE="+userInfo.get("EPARCHY_CODE")+"&SERIAL_NUMBER="+userInfo.get("SERIAL_NUMBER");
	$.ajax.submit('', 'loadBaseTradeInfo', param, 'activatePart', 
	function(data){
		
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function selectActive(obj){
   var authData = $.auth.getAuthData();
   var userInfo = authData.get("USER_INFO");
   var serialNumber = userInfo.get("SERIAL_NUMBER");
   var selectedRowData = getSelectedData();
   debugger;
   var param = '&SERIAL_NUMBER='+serialNumber;
   param += '&ACTIVE_STATE='+selectedRowData.get(0).get("ACTIVE_STATE");
   param += '&END_DATE='+selectedRowData.get(0).get("END_DATE");
   param += "&EPARCHY_CODE="+userInfo.get("EPARCHY_CODE");
   $.beginPageLoading("转移活动校验中........");
   $.ajax.submit('', 'checkSourceUser', param, '', 
	 function(data){
	    $.endPageLoading();
		var tableObj = $.table.get("activeInfoTable");
        var tbodyObj = $("tbody", tableObj.getTable()[0]);
        var size = tableObj.tabHeadSize;
        if(tbodyObj){
           $("tr", tbodyObj[0]).each(function(index, item){
               var checked = $("input[name=saleactive]", item).attr("checked");
               if(!checked){
                  var rowData = tableObj.getRowData(null, index+size);
                  var tempIndex = rowData.get("ROWINDEX");
                  var transUserObj = $("#ACTIVE_"+tempIndex+"_TRANS_USER");
                  transUserObj.val("");
                  transUserObj.attr("disabled",true);
                  transUserObj.attr("isCheck",false);
               }
           });
        }
        var rowIndex = selectedRowData.get(0).get("ROWINDEX");
        var transUserObj = $("#ACTIVE_"+rowIndex+"_TRANS_USER");
        transUserObj.attr("disabled", false);
	 },
	 function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
		$(obj).attr("checked", false);
     }
   );
}

function checkTransUser(obj){
   var rowIndex = $(obj).attr("rowIndex");
   var authData = $.auth.getAuthData();
   var userInfo = authData.get("USER_INFO");
   var serialNumber = userInfo.get("SERIAL_NUMBER");
   debugger;
   var targetSn = $("#ACTIVE_"+rowIndex+"_TRANS_USER").val();
   if(targetSn==""){alert("转移号码不能为空！");return;};
   if(targetSn==serialNumber){alert("转移号码不能是业务受理号码！");return;}
   var selectedRowData = getSelectedData();
   var param = '&SERIAL_NUMBER='+serialNumber;
   param += "&TARGET_SERIAL_NUMBER="+targetSn;
   param += "&PRODUCT_ID="+selectedRowData.get(0).get("PRODUCT_ID");
   param += "&PACKAGE_ID="+selectedRowData.get(0).get("PACKAGE_ID");
   param += "&RELATION_TRADE_ID="+selectedRowData.get(0).get("RELATION_TRADE_ID");
   param += "&EPARCHY_CODE="+userInfo.get("EPARCHY_CODE");
   $.beginPageLoading("目标号码校验中........");
   $.ajax.submit('', 'checkTargetUser', param, '', 
	function(data){
	    $.endPageLoading();
		$("#ACTIVE_"+rowIndex+"_TRANS_USER").attr("isCheck",true);
        $("#ACTIVE_"+rowIndex+"_TRANS_USER").attr("disabled",true);
        alert("校验成功！");
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function getSelectedData(){
    var dataset = new $.DatasetList();
    var tableObj = $.table.get("activeInfoTable");
    var tbodyObj = $("tbody", tableObj.getTable()[0]);
    var size = tableObj.tabHeadSize;
    if(tbodyObj){
       $("tr", tbodyObj[0]).each(function(index, item){
           var checked = $("input[name=saleactive]", item).attr("checked");
           if(checked){
               var rowData = tableObj.getRowData(null, index+size);
               if(rowData){dataset.add(rowData);}
           }
       });
    }
    return dataset;
}

function checkSubmitBefore(){
   debugger;
   var selectedRowData = getSelectedData();
   if(selectedRowData.length===0){
      alert("请选择转移的活动后提交！");return false;
   }
   var rowIndex = selectedRowData.get(0).get("ROWINDEX");
   var isChecked = $("#ACTIVE_"+rowIndex+"_TRANS_USER").attr("isCheck");
   if(isChecked === "false"){
      alert("请校验转移号码后提交！");return false;
   }
   debugger;
   var  param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
   param += '&SOURCE_SN='+$("#AUTH_SERIAL_NUMBER").val();
   param += "&TARGET_SN="+$("#ACTIVE_"+rowIndex+"_TRANS_USER").val();
   param += "&PRODUCT_ID="+selectedRowData.get(0).get("PRODUCT_ID");
   param += "&PACKAGE_ID="+selectedRowData.get(0).get("PACKAGE_ID");
   param += "&RELATION_TRADE_ID="+selectedRowData.get(0).get("RELATION_TRADE_ID");
   $.cssubmit.addParam(param);
   return true;
}

$(document).ready(function(){
   $.table.fn.selectedRow = function (c, f){
        var e = $.table.fn.get(c);
	    var b = e.getTable().attr("selected");
	    if (b == null || b == "-1") {
		    //a(f).attr("class", "on");
	    } else {
		    var d = e.getSelected();
		    if (d && d.length) {
			    if (d.attr("raw_class")) {
				    d.attr("class", d.attr("raw_class"));
			    } else {
				    d.attr("class", "");
			    }
		    }
		    //a(f).attr("class", "on");
	    }
	    e.getTable().attr("selected", f.rowIndex);
   };
});