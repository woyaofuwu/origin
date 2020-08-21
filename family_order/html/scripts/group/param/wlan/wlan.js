
var number = 1;
function initCrtUs() {
	
}

function validateParamPage(methodName) {
    if(methodName == "CrtUs")
	{
	   var pamidc = $("#pam_GRP_WLAN").val();		
	    if(pamidc != "" && pamidc != null)
	    {
	   		alert ("请增加集团WLAN！");
		    return false;
	    }
	    
	    var data = $.table.get("OtherTable").getTableData();
	    
	    
   		
   		$("#pam_WLANINFOS").val(data);
	}
	
	if(methodName == "ChgUs")
	{
	    var data = $.table.get("OtherTable").getTableData();
	    
   		$("#pam_WLANINFOS").val(data);
	}

    return true;
}
	
function createData(){
    var grpwlan = $("#pam_GRP_WLAN_CODE option:selected").text();    
    var grpwlancode = $('#pam_GRP_WLAN_CODE').val();
    var netline = $('#pam_NET_LINE').val();
    var priceline = $("#pam_PRICE").val();
    var discount = $("#pam_DIS_DATA").val();
    var company = $("#pam_COMPANY_NAME_CODE option:selected").text();
    var companycode = $("#pam_COMPANY_NAME_CODE").val();
    var remark = $("#pam_REMARK").val();
    
    if(!verifySupTelListTable()) return false;
    
    var numberCode = Number(grpwlancode)+1;
 
    var pamAttrList = $.table.get("OtherTable").getTableData(null,true);
	
	if("" != pamAttrList){
		for(var i=0; i<pamAttrList.length ;i++){
			var attrs = pamAttrList.get(i);
			var attrsNumCode = attrs.get("pam_GRP_WLAN_CODE");
			var attrsNum = attrs.get("pam_GRP_WLAN");
			var tag = attrs.get("tag");
			if(numberCode == attrsNumCode && tag != 1){
				alert ("关键字段'集团WLAN'已经存在同样的值"+attrsNum);
				return false;
			}
		}
	}
	
	$("#pam_GRP_WLAN_CODE").val(numberCode);
	$("#pam_GRP_WLAN").val(grpwlan);
	$("#pam_NET_LINE").val(netline);
	$("#pam_PRICE").val(priceline);
	$("#pam_DISCOUNT").val(discount);
	$("#pam_COMPANY_NAME").val(company);
	$("#pam_COMPANY_NAME_CODE").val(companycode);
	$("#pam_REMARK").val(remark);
	
	var datalineData = $.ajax.buildJsonData("DiscntsPart");
	$.table.get("OtherTable").addRow(datalineData);
	
	$("#pam_GRP_WLAN_CODE").val("");
	$("#pam_GRP_WLAN").val("");
	$("#pam_NET_LINE").val("");
	$("#pam_PRICE").val("");
	$("#pam_DISCOUNT").val("");
	$("#pam_DIS_DATA").val("");
	$("#pam_COMPANY_NAME").val("");
	$("#pam_COMPANY_NAME_CODE").val("");
	$("#pam_REMARK").val("");
	number++;	
}

function updateData() {
    //效验表单
    if(!verifySupTelListTable()) return false;

    var grpwlan = $("#pam_GRP_WLAN_CODE option:selected").text();    
    var grpwlancode = $('#pam_GRP_WLAN_CODE').val();
    var netline = $('#pam_NET_LINE').val();
    var priceline = $("#pam_PRICE").val();
    var discount = $("#pam_DISCOUNT").val();
    var company = $("#pam_COMPANY_NAME_CODE option:selected").text();
    var companycode = $("#pam_COMPANY_NAME_CODE").val();
    var remark = $("#pam_REMARK").val();
    var instid = $("#INSTID").val();
    
    var maxnumberline = $("#MAX_NUMBER_LINE").value;
    
    var numberCode = Number(grpwlancode)+1;
    
	var pamAttrList = $.table.get("OtherTable").getTableData(null,true);
	
	var rowData = $.table.get("OtherTable").getRowData();
    
    var rowNumber = rowData.get("pam_GRP_WLAN_CODE");
    
	if("" != pamAttrList){
		for(var i=0; i<pamAttrList.length ;i++){
			var attrs = pamAttrList.get(i);
			var attrsNumCode = attrs.get("pam_GRP_WLAN_CODE");
			var tag = attrs.get("tag");
			if(rowNumber == numberCode){
				break ;
			}
			if(numberCode == attrsNumCode && tag != 1){
				alert ("该集团WLAN已存在不能重复");
				return false;
			}
		}
	}
	
	$("#pam_GRP_WLAN_CODE").val(numberCode);
	$("#pam_GRP_WLAN").val(grpwlan);
	$("#pam_NET_LINE").val(netline);
	$("#pam_PRICE").val(priceline);
	$("#pam_DISCOUNT").val(discount);
	$("#pam_COMPANY_NAME").val(company);
	$("#pam_COMPANY_NAME_CODE").val(companycode);
	$("#pam_REMARK").val(remark);
	$("#INSTID").val(instid);
	
	var discntData = $.ajax.buildJsonData("DiscntsPart");
	$.table.get("OtherTable").updateRow(discntData);
	
	$("#pam_GRP_WLAN_CODE").val("");
	$("#pam_GRP_WLAN").val("");
	$("#pam_NET_LINE").val("");
	$("#pam_PRICE").val("");
	$("#pam_DISCOUNT").val("");
	$("#pam_DIS_DATA").val("");
	$("#pam_COMPANY_NAME").val("");
	$("#pam_COMPANY_NAME_CODE").val("");
	$("#pam_REMARK").val("");
}

function deleteData(){
	$.table.get("OtherTable").deleteRow();
}

function tableRowClick() {
	var rowData = $.table.get("OtherTable").getRowData();
	$("#pam_GRP_WLAN_CODE").val(rowData.get("pam_GRP_WLAN_CODE")-1);
	$("#pam_GRP_WLAN").val(rowData.get("pam_GRP_WLAN"));
	$("#pam_NET_LINE").val(rowData.get("pam_NET_LINE"));
	$("#pam_PRICE").val(rowData.get("pam_PRICE"));
	$("#pam_DISCOUNT").val(rowData.get("pam_DISCOUNT"));
	$("#pam_DIS_DATA").val(rowData.get("pam_DIS_DATA"));
	$("#pam_COMPANY_NAME").val(rowData.get("pam_COMPANY_NAME"));
	$("#pam_COMPANY_NAME_CODE").val(rowData.get("pam_COMPANY_NAME_CODE"));
	$("#pam_REMARK").val(rowData.get("pam_REMARK"));
	$("#INSTID").val(rowData.get("INSTID"));
}

function countWlanFee(){
    var wlannet = $('#pam_NET_LINE').val();
    if(!(parseInt(wlannet) > 1 && parseInt(wlannet) < 2561))
    {
        alert("输入带宽在2M~2560M之间！");
        return false;
    }
       
	  var priceline = $("#pam_PRICE").val();
	  var strbandwidthdatas=$("#BANDWIDTHDATAS").val();
	  var pricedataset =new Wade.DatasetList(strbandwidthdatas);
	  var pricecount=pricedataset.length;
	  var wlanDataMin = 1,wlanFeeMin,wlanDataMax = 2561,wlanFeeMax;
	  var d,f ;
	  
	  for (i=0;i<pricecount;i++)
	  {
	     var wlanData = pricedataset.get(i,'PARA_CODE1');
	     var wlanFee =  pricedataset.get(i,'PARA_CODE2');
	     var bandwidthdatas = pricedataset.get(i);
	     
	     if(wlannet == wlanData)
	     {
	        $("#pam_PRICE").val(wlanFee);
	    	break; 
	      }
	      else{
	   			if( parseInt(wlanData) < parseInt(wlannet) && parseInt(wlanDataMin) < parseInt(wlanData))
	   			{
	   				wlanDataMin = wlanData;
	   				wlanFeeMin = wlanFee ;
	   			}
	   			if( parseInt(wlanData) > parseInt(wlannet) &&  parseInt(wlanData) < parseInt(wlanDataMax) )
	   			{
	   				wlanDataMax = wlanData;
	   				wlanFeeMax = wlanFee ;
	   			}
	   		    d=(parseInt(wlanDataMax)-parseInt(wlannet))/(parseInt(wlannet)-parseInt(wlanDataMin));
			    f=(parseInt(wlanFeeMax)+parseInt(wlanFeeMin)*d)/(d+1);
			    
			    if(!isNaN(f)){
			    	var tempf = f + "";
			    	if(tempf.indexOf(".") !=-1){
						var tempLength = tempf.indexOf(".");
						f = tempf.substring(0,tempLength);
					}
			    }
			    $("#pam_PRICE").val(f);
	          }          
	    }

		var strdiscountdata = $('#DISCOUNTDATA').val();
		var discountinfoset = new Wade.DatasetList(strdiscountdata);
		
		 var wlandisdata=  discountinfoset.get(0,'PARA_CODE2');
		   
		 var discountData =  "";
		 if (wlannet <= parseInt(wlandisdata)) 
		 {
			  discountData = discountinfoset.get(0,'PARA_CODE3'); 
	           
     	 }else
     	 {
			  discountData = discountinfoset.get(0,'PARA_CODE4'); 
         }
         
         if(discountData){
         	discountData.length = '0' ;
         }
         
		 for(var i = discountData;i <= 10; i++)
		 {
	       //jsAddItemToSelect($("#pam_DIS_DATA"), i, i);
	       //$("#pam_DIS_DATA").bindData({i:i});
	     } 
 }
	
function verifySupTelListTable()
{
	if(!checkFiled("pam_GRP_WLAN_CODE")) return false;
	if(!checkFiled("pam_NET_LINE")) return false;
	if(!checkFiled("pam_PRICE")) return false;
	if(!checkFiled("pam_COMPANY_NAME_CODE")) return false;

	return true;
}

function checkFiled(obj)
{
	var keyValue = $("#" + obj).val();	
	
	if(null == keyValue || "" == keyValue)
	{
	    alert($("#" + obj).attr("desc") + '不能为空');
	    $("#" + obj).focus()
	 	return false;
	}
	
	return $.validate.verifyField($("#" + obj));
	
	return true;
}

function jsAddItemToSelect(objSelect, objItemText, objItemValue) { 
       objSelect.append("<option selected value=\"" +objItemValue+"\">" +objItemText+ "</option>");
}


