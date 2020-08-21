(function($){
   if(typeof($.SaleActiveActivate)=="undefined"){$.SaleActiveActivate={
      loadGroupActivateActives:function(){
          var startDate = $("#START_TIME").val();
          var endDate = $("#END_TIME").val();
          if(startDate==""||endDate==""){
             alert("开始时间和结束时间不能为空！"); return;
          }
          var serialNumber = $("#MOBILENO").val();
          var terminalType = $("#TERMINAL_TYPE").val();
          var imei = $("#IMEI").val();
          var packageCode = $("#PACKAGECODE").val();
          var dealFlag = $("#DEALFLAG").val();
          var param = "&START_DATE="+startDate+"&END_DATE="+endDate;
          param += "&TERMINAL_TYPE="+terminalType+"&SERIAL_NUMBER="+serialNumber;
          param += "&IMEI="+imei+"&PACKAGE_CODE="+packageCode+"&DEAL_FLAG="+dealFlag;
           $.beginPageLoading("信息查询中........");
          $.ajax.submit(null, 'queryActives', param, 'activatePart', 
	          function(data){$.endPageLoading();},
	          function(error_code,error_info){
		         $.endPageLoading();
		         alert(error_info);
		      }
          );
      },
      activateActives:function(){
         if(!$.SaleActiveActivate.checkIsSelected()){
             alert("请选择需要激活的活动！");return false;
         }
         debugger;
         var selectedDataset = $.SaleActiveActivate.getSelectedData();
         $.beginPageLoading("业务受理中........");
         var param = "&SELECTED_ACTIVES="+selectedDataset;
         $.ajax.submit(null, "activateActives", param, 'activatePart', function(data){
		    $.endPageLoading();
		    alert("激活成功！");
	     },
	     function(error_code,error_info){
		    $.endPageLoading();
		    alert(error_info);
	     });
      },
      checkIsSelected:function(){
          var tableObj = $.table.get("activeInfoTable");
          var tbodyObj = $("tbody", tableObj.getTable()[0]);
          var hasCheck = false;
          if(tbodyObj){
             $("tr", tbodyObj[0]).each(function(index, item){
                var checked = $("input[name=saleActiveCheckBox]", item).attr("checked");
			    if(checked){
			       hasCheck = true; return;
			    }		
             });
          }
          return hasCheck;
      },
      getSelectedData:function(){
          var dataset = new $.DatasetList();
          var tableObj = $.table.get("activeInfoTable");
          var tbodyObj = $("tbody", tableObj.getTable()[0]);
          var size = tableObj.tabHeadSize;
          if(tbodyObj){
             $("tr", tbodyObj[0]).each(function(index, item){
                 var checked = $("input[name=saleActiveCheckBox]", item).attr("checked");
                 if(checked){
                     var rowData = tableObj.getRowData(null, index+size);
                     if(rowData){dataset.add(rowData);}
                 }
             });
          }
          return dataset;
      }
   }}
})(Wade);