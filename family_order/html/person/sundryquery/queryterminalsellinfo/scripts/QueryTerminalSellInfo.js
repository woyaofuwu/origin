	
function initBtn() { 
	$("#TERMINAL_SEARCH").attr("disabled",true).addClass("e_dis");
	$("#SALE_PRODUCT_ID").attr("disabled",true).addClass("e_dis");
	$("#SALE_CAMPN_TYPE").attr("disabled",true).addClass("e_dis");
	$("#TERMINAL_SEARCH1").attr("disabled",true).addClass("e_dis");	
	$("#SALE_CAMPN_TYPE").val("");
	$("#TERMINAL_SEARCH1").val("");
	
	$("#cond_PRICE_RANGE_START").attr("disabled",false).removeClass("e_dis");
	$("#cond_PRICE_RANGE_END").attr("disabled",false).removeClass("e_dis");
	$("#cond_TERMINAL_SRC_CODE").attr("disabled",false).removeClass("e_dis");
	$("#check_search").attr("disabled",false).removeClass("e_dis");
	$("#query1").attr("disabled",true).addClass("e_dis");
}

function queryPriceInfo() {
	
	var checked=$("#CampCheck").attr("checked");
	
	if(checked){
		var saleCampnType=$("#SALE_CAMPN_TYPE").val();
		var terminalSearch1=$("#TERMINAL_SEARCH1").val();
		
		if(!saleCampnType||saleCampnType==null||saleCampnType==""){
			alert("请选择活动类型");
			return false;
		}
		
		if(!terminalSearch1||terminalSearch1==null||terminalSearch1==""){
			alert("请选择终端型号");
			return false;
		}
		
	}
	
	
	
	
	$.beginPageLoading("数据查询中..");
	
	//var saleproductid = $("#SALE_PRODUCT_ID").val();
	//var salecampntype = $("#cond_SALE_CAMPN_TYPE").val();
	//var campcheck = $("#CampCheck").attr('checked'); 
	
	//var params = "&SALE_PRODUCT_ID="+ saleproductid + "&SALE_CAMPN_TYPE="+ salecampntype + "&CampCheck="+ campcheck ;
	
	$.ajax.submit('QueryCondPart', 'queryPriceInfo', null, 'results', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

function  checkStepByPhoneInfo(obj) {
			$("#TERMINAL_SEARCH").attr("disabled",false).removeClass("e_dis");
			$("#SALE_PRODUCT_ID").attr("disabled",false).removeClass("e_dis");
        	var price_start = $("#cond_PRICE_RANGE_START").val()*100;
        	var price_end = $("#cond_PRICE_RANGE_END").val()*100;
        	var terminal = $("#cond_TERMINAL_SRC_CODE").val();
        	var terminal_type = $("#TERMINAL_TYPE").val();
        	
        	if(price_start == "" && price_end=="" && terminal =="")
        	{
        		alert("终端厂商和价格区间必选一项！");
        		return false;
        	}
        	
        	if((price_start == "" && price_end !="") || (price_start != "" && price_end==""))
        	{
        		alert("价格区间开始和结束都必选！");
        		return false;
        	}
        	var params = "&STARTPRICE="+ price_start + "&ENDPRICE="+price_end ;
            params += "&FACTOR_CODE="+terminal ;
            params += "&TERMINAL_TYPE=" + terminal_type;
            
        	$.beginPageLoading("终端型号列表查询中。。。");
        	$.ajax.submit('','getTerminalByHW',params,'', function(d){
				    $("#TERMINAL_SEARCH").empty();
	        		$('#TerminalPart').css('display', '');
	        		$("#TerminalPart").css('width', '50%');
	        		$("#TerminalPart").css('width', '');
				    $("#TERMINAL_SEARCH").append("<option value=\"\">--请选择--</option>");	
				    d.each(function(item){ 
				    	//alert(item);
				    	var terminalValue = item.get("DEVICE_TYPE_CODE") + ',' +item.get("DEVICE_MODEL_CODE") + ',' + item.get("DEVICE_COST")+ ',' + item.get("SALE_PRICE");
				    	var deviceType = new Array();
				    	deviceType = item.get("GOODS_EXPLAIN").split('|');
				    	//alert(terminalValue);
					    $("#TERMINAL_SEARCH").append("<option title=\"" + item.get("GOODS_EXPLAIN") + "\"" + "value=\"" + terminalValue + "\">" +  deviceType[0]+deviceType[1] + "</option>");
				    });
				$.endPageLoading();
			},
			function(errorcode, errorinfo){
				$.endPageLoading();
				$.showErrorMessage('终端型号列表查询失败',errorinfo);
			});
}
        
function  toggleCheck()
{ 
       	var isStatus = document.getElementById('CampCheck').checked;	       	
       	if(isStatus){
       		//根据活动类型查询
       		$("#TERMINAL_SEARCH").attr("disabled",true).addClass("e_dis");
			$("#SALE_PRODUCT_ID").attr("disabled",true).addClass("e_dis");
			$("#TERMINAL_SEARCH").val("");
			$("#SALE_PRODUCT_ID").val("");
			
			$("#SALE_CAMPN_TYPE").attr("disabled",false).removeClass("e_dis");	
			$("#SALE_CAMPN_TYPE").val("");
			$("#TERMINAL_SEARCH1").attr("disabled",false).removeClass("e_dis");	
			$("#TERMINAL_SEARCH1").val("");			
			
			$("#cond_PRICE_RANGE_START").attr("disabled",true).addClass("e_dis");
			$("#cond_PRICE_RANGE_END").attr("disabled",true).addClass("e_dis");
			$("#cond_TERMINAL_SRC_CODE").attr("disabled",true).addClass("e_dis");
			$("#check_search").attr("disabled",true).addClass("e_dis");
			$("#cond_PRICE_RANGE_START").val("");
			$("#cond_PRICE_RANGE_END").val("");
			$("#cond_TERMINAL_SRC_CODE").val("");
			
			$("#TERMINAL_TYPE").attr("disabled",true).addClass("e_dis");
			$("#query").attr("disabled",true).addClass("e_dis");
			$("#query1").attr("disabled",false).removeClass("e_dis");
			
			CheckProductSearch();
			
       	}else{
       		//根据机型查询
       		$("#TERMINAL_SEARCH").attr("disabled",true).addClass("e_dis");
			$("#SALE_PRODUCT_ID").attr("disabled",true).addClass("e_dis");
			$("#SALE_CAMPN_TYPE").attr("disabled",true).addClass("e_dis");	
			$("#SALE_CAMPN_TYPE").val("");
			$("#TERMINAL_SEARCH1").attr("disabled",true).addClass("e_dis");	
			$("#TERMINAL_SEARCH1").val("");
			$("#cond_PRICE_RANGE_START").attr("disabled",false).removeClass("e_dis");
			$("#cond_PRICE_RANGE_END").attr("disabled",false).removeClass("e_dis");
			$("#cond_TERMINAL_SRC_CODE").attr("disabled",false).removeClass("e_dis");
			$("#check_search").attr("disabled",false).removeClass("e_dis");
			
			$("#TERMINAL_TYPE").attr("disabled",false).addClass("e_dis");
			$("#query1").attr("disabled",true).addClass("e_dis");
			$("#query").attr("disabled",false).removeClass("e_dis");
       	}
}
        
function  checkTerminalSearch(obj){
	
	obj = $(obj);     		     		
	
	var deviceModelCode=obj.val();
	
	if(!deviceModelCode||deviceModelCode==null||deviceModelCode==""){
		return false;
	}
	
	
	var param = '&DEVICE_MODEL_CODE='+deviceModelCode;
   	$.beginPageLoading("营销产品列表查询中。。。");
   	$.ajax.submit('','refreshProduct',param,'', function(d){
	    $("#SALE_PRODUCT_ID").empty();
	    $("#SALE_PRODUCT_ID").css('width', '50%');
		$("#SALE_PRODUCT_ID").css('width', '');
	    $("#SALE_PRODUCT_ID").append("<option value=\"\">--请选择--</option>");	
	    d.each(function(item){
		   var productDesc = item.get("PRODUCT_ID") + '|' + item.get("PRODUCT_NAME");
		   item.put('PRODUCT_NAME', productDesc);
		   $("#SALE_PRODUCT_ID").append("<option title=\"" + productDesc + "\"" + "value=\"" + item.get("PRODUCT_ID") + "\">" + productDesc + "</option>");
	    });
		$.endPageLoading();
		
	},
	function(errorcode, errorinfo){
		$.endPageLoading();
		$.showErrorMessage('营销产品列表查询失败',errorinfo);
	});
}


function getTerminalByID(obj){
		
		obj = $(obj);   
		var params = '&SALE_CAMPN_TYPE=' +obj.val();        
    	$.beginPageLoading("终端型号列表查询中。。。");
		ajaxSubmit('','getTerminalsByProductID',params,'', function(d){
		    $("#TERMINAL_SEARCH1").empty();
    		$("#TerminalPart1").css('width', '50%');
    		$("#TerminalPart1").css('width', '');
		    $("#TERMINAL_SEARCH1").append("<option value=\"\">--请选择--</option>");	
		    d.each(function(item){ 
		    	var terminalDesc = item.get("DEVICE_BRAND") + '|' +item.get("DEVICE_MODEL");// + '|' + item.get("DEVICE_COST_NAME")+ '|'+ item.get("SALE_PRICE_NAME");
		    	var terminalValue = item.get("TERMINAL_TYPE_CODE") + ',' +item.get("DEVICE_MODEL_CODE") + ',' + item.get("DEVICE_COST")+ ',' + item.get("SALE_PRICE");
		    	$("#TERMINAL_SEARCH1").append("<option title=\"" + terminalDesc + "\"" + "value=\"" + terminalValue + "\">" +  terminalDesc + "</option>");
		    });
			$.endPageLoading();
		},
		function(errorcode, errorinfo){
			$.endPageLoading();
			$.showErrorMessage('终端型号列表查询失败',errorinfo);
		});	
	}

function  CheckProductSearch(){
	
   	$.beginPageLoading("活动列表查询中。。。");
   	//alert("983038344");
   	$.ajax.submit('','refreshProduct1',null,'', function(d){
   		//alert("0595");
	    $("#SALE_CAMPN_TYPE").empty();
	    $("#SALE_CAMPN_TYPE").css('width', '50%');
		$("#SALE_CAMPN_TYPE").css('width', '');
	    $("#SALE_CAMPN_TYPE").append("<option value=\"\">--请选择--</option>");	
	    d.each(function(item){
		   var productDesc = item.get("PRODUCT_ID") + '|' + item.get("PRODUCT_NAME");
		   item.put('PRODUCT_NAME', productDesc);
		   $("#SALE_CAMPN_TYPE").append("<option title=\"" + productDesc + "\"" + "value=\"" + item.get("PRODUCT_ID") + "\">" + productDesc + "</option>");
	    });
		$.endPageLoading();
		
	},
	function(errorcode, errorinfo){
		$.endPageLoading();
		$.showErrorMessage('活动列表查询失败',errorinfo);
	});
}



