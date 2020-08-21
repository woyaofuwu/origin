
function refreshPartAtferAuth(data){
    //var userInfo = data.get("USER_INFO").toString();
    var param = "&USER_INFO="+data.get("USER_INFO").toString();
    		  /*+ "&ACCT_INFO="+data.get("ACCT_INFO").toString()
    		  + "&CUST_INFO="+data.get("CUST_INFO").toString();*/
	$.ajax.submit('', 'loadInfo', param, 'activatePart', 
	function(data){
		
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

function selectActive(obj){
	
	//alert("test");
	var selectedDataset = getSelectedData();
	var endDate = selectedDataset.get(0).get("END_DATE");
	var flag = selectedDataset.get(0).get("PRODT_FLAG");
	var daycount = selectedDataset.get(0).get("DAY_COUNT");
	
	var param = "&DAY_COUNT=" + daycount + "&END_DATE=" + endDate + "&PRODT_FLAG="+flag;
	$.beginPageLoading("活动校验中........");
	$.ajax.submit('', 'getSaleActiveExtend', param, 'userOperPart', 
		function(data){
			$.endPageLoading();
			//$("#cond_END_DATE").attr("value", endDate);
			//$("#cond_END_DATE").attr("disabled", true);
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
	/*if( flag!=null && flag == "0" ){ }else{ $.endPageLoading();$("#cond_END_DATE").attr("value", endDate); }*/
}

function checkSubmitBefore(data)
{
	var selectedActives = getSelectedData();
	if( selectedActives.length === 0 )
	{
		alert("请选择指定的活动后提交！");
		return false;
	}
	var endDate = selectedActives.get(0).get("END_DATE");
	var start=new Date(endDate.replace("-", "/").replace("-", "/"));
	var cond_END_DATE = $("#cond_END_DATE").val();
	var end=new Date(cond_END_DATE.replace("-", "/").replace("-", "/"));
	//alert(start + ',' + end);
	if( end < start ){
		alert("活动延长结束时间不能小于活动结束时间！");
		//alert(endDate + ',' + cond_END_DATE);
		return false;
	}
	if($("#REMARK").val()==null || $("#REMARK").val()==""){
		alert("请输入延长原因后再提交！");
		return false;
	}
	var authData = $.auth.getAuthData();
	var userInfo = authData.get("USER_INFO");
	var param = '&SERIAL_NUMBER='+userInfo.get("SERIAL_NUMBER");
	param += "&EPARCHY_CODE="+userInfo.get("EPARCHY_CODE");
	param += "&RELATION_TRADE_ID="+selectedActives.get(0).get("RELATION_TRADE_ID"); 
	param += "&END_DATE="+cond_END_DATE;
	var remark = $("#REMARK").val();
	param += "&REMARK="+ remark;
	
/*	var userId = data.get('USER_INFO').get('USER_ID');
	var serialNumber = data.get('USER_INFO').get('SERIAL_NUMBER');
	var param = '&USER_ID='+userId;
	param += '&SERIAL_NUMBER=' + serialNumber;
	param += '&EPARCHY_CODE=' + data.get('USER_INFO').get('EPARCHY_CODE');
	param += '&USER_PRODUCT_ID=' + data.get('USER_INFO').get('PRODUCT_ID');*/
	$.beginPageLoading("业务受理中...");
	$.ajax.submit(null, 'onTradeSubmit', param, null,loadInfoSuccess,loadInfoError);
}
function loadInfoSuccess(data){
	MessageBox.alert("提示","操作成功！",function(btn)
	{
		if(btn=="ok")
		{
			window.location.href = window.location.href;
		}
	});	
	$.endPageLoading();
}

function loadInfoError(code,info){
	$.endPageLoading();
	alert(info);
}
