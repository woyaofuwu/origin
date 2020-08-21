function queryData(){
	var mos=/^1[3|4|5|8][0-9]\d{4,8}$/;
	var PHONENUMBER=$("#PHONENUMBER").val();
	var SRECONDATE=$("#SRECONDATE").val();
	var ERECONDATE=$("#ERECONDATE").val();
	var COMPRESULT=$("#COMPRESULT").val();
	var SYNRESULT=$("#SYNRESULT").val();
	//正则表达式验证手机号码的有效性
	if(PHONENUMBER !=""){
		if(!mos.test(PHONENUMBER)){
			MessageBox.alert("提示信息","请输入正确的手机号码",null,null,null);	
			return;
			
		}
	}
	
	if(SRECONDATE != "" && ERECONDATE != "" && SRECONDATE > ERECONDATE){
		MessageBox.alert("提示信息","开始时间不能大于结束时间！",null,null,null);	
		return;
	}
	
	if(PHONENUMBER=="" && SRECONDATE=="" && ERECONDATE=="" && COMPRESULT=="" && SYNRESULT=="" ){
		MessageBox.alert("提示信息","请输入查询条件中的一个或多个进行查询！",null,null,null);	
		return;
	}
	$.beginPageLoading("查询中...");
	$.ajax.submit("submitInfo","queryData",null,"OnecertificatefiveNoTablePart",function(data){
		$.endPageLoading();
		if(data.get("CODE")!=null){
			 MessageBox.alert("提示信息","未找到记录！",null,null,null);	
		}
	},
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
	
		
	
}

function deal(){	
   	var data = $.table.get("OnecertificatefiveNoTable").getCheckedRowDatas();//获取选择中的数据

	if(data == null || data.length == 0) {
		alert('请勾选选择需要处理的数据!');
		return false;
	}
	var PHONE_NUMBER = "";
	var CID_CATEGORY = "";
	var CID_CODE = "";
	var NAME_CODE = "";
	var COMP_RESULT = "";
	var SYN_RESULT = "";
	for(var i=0; i<data.length; i++) {
		if(i == 0) {
			if(data.get(i).get("SYN_RESULT") == "01" || data.get(i).get("SYN_RESULT") == "1"){
				alert('勾选的数据中包含已经处理成功的数据，请确认后重新选择!');
				return false;
			}else{
				PHONE_NUMBER = data.get(i).get("PHONE_NUMBER");
				CID_CATEGORY = data.get(i).get("CID_CATEGORY");
				CID_CODE = data.get(i).get("CID_CODE");
				NAME_CODE = data.get(i).get("NAME_CODE");
				COMP_RESULT = data.get(i).get("COMP_RESULT");
			}
		}
		if(i > 0) {
			if(SYN_RESULT == "01" || SYN_RESULT == "1"){
				alert('勾选的数据中包含已经处理成功的数据，请确认后重新选择!');
				return false;
			}else{
				PHONE_NUMBER = PHONE_NUMBER + "," + data.get(i).get("PHONE_NUMBER");
				CID_CATEGORY = CID_CATEGORY + "," + data.get(i).get("CID_CATEGORY");
				CID_CODE = CID_CODE + "," + data.get(i).get("CID_CODE");
				NAME_CODE = NAME_CODE + "," + data.get(i).get("NAME_CODE");
				COMP_RESULT = COMP_RESULT + "," + data.get(i).get("COMP_RESULT");						
			}
		}
	}
	
	//var param = "&PHONE_NUMBER=" + PHONE_NUMBER + "&CID_CATEGORY=" + CID_CATEGORY
				//+ "&CID_CODE=" + CID_CODE + "&NAME_CODE=" + NAME_CODE + "&COMP_RESULT=" + COMP_RESULT ;
	
	var param = "&TABLE_DATA=" + data;
	//alert(param);
	$.beginPageLoading("业务处理中..");
     $.ajax.submit('', 'dealOnecertificatefiveNoInfo', param, 'OnecertificatefiveNoTablePart', function(data){
		$.endPageLoading();
		$.showSucMessage("信息处理成功!");
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
     
     /**
      * 获取已选中的订购信息
      */
     function getUnionOrderCheckStr(){
     	var s = document.getElementsByName("SessionOrder");
     	var s2 = "";
     	for( var i = 0; i < s.length; i++ ){
     		if (s[i].checked){
     			s2 += s[i].value+";";
     		}
     	}
     	s2 = s2.substr(0,s2.length-1);
     	return s2;
     }
	    
}
