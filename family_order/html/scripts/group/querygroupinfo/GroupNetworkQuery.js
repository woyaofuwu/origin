 function changeNetQueryType() {
 	var choose = document.getElementById("cond_QueryType").value;
 	
	var qryOneType = document.getElementById("QueryTypeOne");
	var qryTwoType = document.getElementById("QueryTypeTwo");
	
	if(choose == "0"){
	  qryOneType.style.display='';
	  qryTwoType.style.display='none';
	  var choose = $("#cond_GROUP_ID").val("");
	   
	}else if (choose == "1"){ 
	  qryOneType.style.display='none';
	  qryTwoType.style.display='';
	  var choose = $("#cond_PRODUCT_ID").val("");
	}
 }
 
 
 function qryClick(){
	var groupId = $("#cond_GROUP_ID").val();
	var productId = $("#cond_PRODUCT_ID").val();
	
	$.beginPageLoading("查询中......");
    $.ajax.submit('QueryCondPart','queryInfos','','groupNetInfo,refreshHintBar', function(data){
		$.endPageLoading(); 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
 }
 
 function tableRowClick(){
  	var val = window.event.srcElement.parentElement;
    var userId = "";
	if(val.tagName=='TR'){
		var enuma = val.childNodes;
		userId = enuma[6].innerText;
	}
 	$.ajax.submit('','queryByUserid', '&USER_ID='+userId,'GroupProductPart,refreshHintBar', function(data){
		$.endPageLoading(); 
		setAttrProduct(data);
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
 }
 
 function setAttrProduct(data){
       for(a=0; data.length > a ; a++){
       	var elementData = data.get(0);
        var ATTR_VALUE = elementData.get("ATTR_VALUE");
        var ATTR_LABLE = elementData.get("ATTR_LABLE");
        if(null==ATTR_VALUE && ''==ATTR_VALUE && null==ATTR_VALUE && ''==ATTR_VALUE){
          return true;
        }else{
        if(a==0){
   	    document.getElementById('DBEdit1').innerHTML=ATTR_LABLE+': ';
   	    document.getElementById('cond_DBEdit1').value=ATTR_VALUE;
   	    }else if(a==1){
   	    document.getElementById('DBEdit2').innerHTML=ATTR_LABLE+': ';
   	    document.getElementById('cond_DBEdit2').value=ATTR_VALUE;}
   	    else if(a==2){
   	    document.getElementById('DBEdit3').innerHTML=ATTR_LABLE+': ';
   	    document.getElementById('cond_DBEdit3').value=ATTR_VALUE;
   	    }
   	    else if(a==3){
   	    document.getElementById('DBEdit4').innerHTML=ATTR_LABLE+': ';
   	    document.getElementById('cond_DBEdit4').value=ATTR_VALUE;}
   	    else if(a==4){
   	    document.getElementById('DBEdit5').innerHTML=ATTR_LABLE+': ';
   	    document.getElementById('cond_DBEdit5').value=ATTR_VALUE;}
   	    else if(a==5){
   	    document.getElementById('DBEdit6').innerHTML=ATTR_LABLE+': ';
   	    document.getElementById('cond_DBEdit6').value=ATTR_VALUE;}
   	     else if(a==6){  	   
   	    document.getElementById('DBEdit7').innerHTML=ATTR_LABLE+': ';
   	    document.getElementById('cond_DBEdit7').value=ATTR_VALUE;}
   	     else if(a==7){  	   
   	    document.getElementById('DBEdit8').innerHTML=ATTR_LABLE+': ';
   	    document.getElementById('cond_DBEdit8').value=ATTR_VALUE;}
   	   }
   	    
       } return true;
}
 
 