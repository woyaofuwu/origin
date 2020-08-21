function queryInfos(el) {

    if(isNull($("#cond_GROUP_ID").val()) && isNull($("#cond_IBSYSID").val())){
       MessageBox.error("请至少输入一个查询条件！");
       return false;
    }

    $.beginPageLoading('正在查询...');
    $.ajax.submit("QueryInfo", "queryInfos", "", "queryPart", function(data){
            $.endPageLoading();
            hidePopup(el);
        },
        function(error_code,error_info,derror){
            $.endPageLoading();
            showDetailErrorInfo(error_code,error_info,derror);
        }
    );
}

function goToWorkTask(el) {
    var todoUrl = $(el).attr("todoUrl");
    var newUrl = changeURL(todoUrl);
    
    var urlArr = newUrl.split("?");
    openNav('工单办理', encodeURI(urlArr[1].substring(13)), '', '', urlArr[0]);
}

function isNull(obj) {
    if(obj == null || obj == "" || obj == undefined){
        return true;
    }
    return false;
}

function changeURL(url) {  

	   
	   debugger;
	   //获取url中"?"符后的字串  
	   var str1 = "";
	   var str2 = "";
	   var newurl ="";
	   
//	   var theRequest = new Object(); 
	   var num =  url.indexOf("&");
	   var num1 =  url.indexOf("AuditFailedMinorec");
	   var num2 =  url.indexOf("AuditFailedChgMinorec");
	   var num3 =  url.indexOf("ComplexProcessChgMinorec");
	   
	   if((num1!=0&&num1!=-1)||(num2!=0&&num2!=-1)||(num3!=0&&num3!=-1)){
	       str1 = url.substr(0,num); 
	       str2 = url.substr(num);
	       newurl = str1+"ForPhone"+str2;
	   }else{
		   newurl = url;
	   }
	      
//	      strs = str.split("&");  
//	      
//	      for(var i = 0; i < strs.length; i ++) {  
//	         theRequest[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]);
//	         
//	      }  
	     
	  return newurl;
	   
	}  