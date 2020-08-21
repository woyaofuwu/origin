/*$Id: ChooseIdlePhone.js,v 1.2 2013/04/26 07:10:32 chenzm3 Exp $*/

function querySerialMobile(){
		//查询条件校验
	if(!$.validate.verifyAll("QueryPhonePart")) {
		return false;
	}
	
	var psptId = $("#NETCHOOSE_PSPT_ID").val();
	var chooseType = $("#NETCHOOSE_TYPE").val();
	if(psptId==""||(psptId.length!=15&&psptId.length!=18))
	{
		alert("证件号码为空或者不符合长度！");
		return false;
	}
	$.ajax.submit('QueryPhonePart', 'queryIDlePhone', null, 'PhonePart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

//点击确定时间 sunxin
function setNewPhone(obj)
{
    $("#PhoneTableValues input[name=CHOOSE_PHONE]").each(function(){
      if(this.checked){
	     	this.click();
	        var rowIndex = this.parentNode.parentNode.rowIndex;
	   	    var table = $.table.get("NetPhoneTable");
            var json = table.getRowData(null, rowIndex);
            var phoneNumber = json.get("RES_NO");
            var psptidselected = json.get("RANDOM_NO");
            var str3 = json.get("RSRV_STR3");
            if(str3 != "kongbai"){
				var rsrvStr3 = str3.replace("%n","\n");
				//alert(rsrvStr3);
			}
			var srcWin = $.getSrcWindow();
	        $.setReturnValue(['INFO_TAG','1','SERIAL_NUMBER',obj.getAttribute('desc')],true);
	        //调用父页面js
	        srcWin.checkMphone(1);
	   }
	  });
}

//鼠标滑过变换颜色
function changeColor(e,flag)
{
	e.style.cursor="hand";
	if(flag==0)
	e.style.color="red";
	else
	e.style.color="black";
}
