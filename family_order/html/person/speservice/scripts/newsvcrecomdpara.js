
//设置历史推荐信息
function refresSer(){
	$("#newID").attr("className", "");
	$("#hisID").attr("className", "on");
	
	$("#DiscntListPart").css("display","none");
	$("#ServiceListPart").css("display","");
}

//设置新业务推荐受理
function refresDic(){
	$("#newID").attr("className", "on");
	$("#hisID").attr("className", "");
	
	$("#DiscntListPart").css("display","");
	$("#ServiceListPart").css("display","none");
}



function init()
{
	$.beginPageLoading("数据查询中..");
	$.ajax.submit(null, 'loadChildInfo',  null,
	'RecomdInfoPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}


function getProduct()
{
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('RecomdInfoPart', 'getProductByBrand',  null,
	'ProductPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}


function getSvcAndDis()
{
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('RecomdInfoPart', 'getSVCAndDisByProduct',  null,
	'QueryInfoPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function tableDiscntClick()
{
	var rowData = $.table.get("discntTable").getRowData();
	var discntId = rowData.get("DISCNT_ID");
	var discntName = rowData.get("DISCNT_NAME");
	var discntContent = rowData.get("DISCNT_CONTENT"); 
	
	$("#DISCNT_ID").val(discntId);
	$("#DISCNT_NAME").val(discntName);
	$("#DISCNT_CONTENT").val(discntContent); 
}

function addDiscntInfo()
{  
	var discntEdit = new Array();
	discntEdit["DISCNT_ID"] =$("#DISCNT_ID").val();
	discntEdit["DISCNT_NAME"] = $("#DISCNT_NAME").val();
	discntEdit["DISCNT_CONTENT"] = $("#DISCNT_CONTENT").val();
	$.table.get("discntTable").addRow(discntEdit);
	 
}

function upDiscntInfo()
{  
	var tab = $.table.get("discntTable");
	var rowData = tab.getRowData();	
	
	if(rowData.length == 0)
	{
		alert("请您选择记录后再进行修改操作！");
		return false;
	}
	
	//修改表格内的该行数据
	$.table.get("discntTable").updateRow($.ajax.buildJsonData("discntInfoPart"));
	
}

function delDiscntInfo()
{  
	var tab = $.table.get("discntTable");
	tab.deleteRow();
}


function tableServiceClick()
{
	var rowData = $.table.get("serviceTable").getRowData();
	var serviceId = rowData.get("SERVICE_ID");
	var serviceName = rowData.get("SERVICE_NAME");
	var serviceContent = rowData.get("SERVICE_CONTENT"); 
	
	$("#SERVICE_ID").val(serviceId);
	$("#SERVICE_NAME").val(serviceName);
	$("#SERVICE_CONTENT").val(serviceContent); 
}

function addServiceInfo()
{  
	var serviceEdit = new Array();
	serviceEdit["SERVICE_ID"] =$("#SERVICE_ID").val();
	serviceEdit["SERVICE_NAME"] = $("#SERVICE_NAME").val();
	serviceEdit["SERVICE_CONTENT"] = $("#SERVICE_CONTENT").val();
	$.table.get("serviceTable").addRow(serviceEdit);
	 
}

function upServiceInfo()
{  
	var tab = $.table.get("serviceTable");
	var rowData = tab.getRowData();	
	
	if(rowData.length == 0)
	{
		alert("请您选择记录后再进行修改操作！");
		return false;
	}
	
	//修改表格内的该行数据
	$.table.get("serviceTable").updateRow($.ajax.buildJsonData("serviceInfoPart"));
	
}

function delServiceInfo()
{  
	var tab = $.table.get("serviceTable");
	tab.deleteRow();
}

function resetAll()
{  
	if(confirm('清空所有修改操作，是否清空'))
	{
		$.beginPageLoading("数据查询中..");
		$.ajax.submit('RecomdInfoPart', 'getSVCAndDisByProduct',  null,
		'QueryInfoPart', function(data){
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
	}
	 
}

function onTradeSubmit()
{
	var discntData = $.table.get("discntTable").getTableData();
	var serviceData = $.table.get("serviceTable").getTableData();
	
	if(serviceData.length==0  && discntData.length==0){
		alert("没有数据可以提交");
		return false;
	}
	
	var param = "&serviceData="+serviceData+"&discntData="+discntData;
	//$.cssubmit.addParam(param);	
   // return true;

	   $.ajax.submit('', 'onTradeSubmit', param, '',
			    function(ajaxData) {  
			        $.endPageLoading();
			        var msg = ajaxData.get("msg");

			        //后台操作成功
			        if(msg=!null){
			        	 MessageBox.success("成功提示", "操作成功！",
			        		        function() {}
			        	 );
			        }
			        			    
			                      });
	

}
    
   














