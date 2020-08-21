
function getUrlParams(param) { //param为要获取的参数名 注:获取不到是为null
    var currentUrl = window.location.href; //获取当前链接
    var arr = currentUrl.split("?");//分割域名和参数界限
    if (arr.length > 1) {
        arr = arr[1].split("&");//分割参数
        for (var i = 0; i < arr.length; i++) {
            var tem = arr[i].split("="); //分割参数名和参数内容
            if (tem[0] == param) {
                return tem[1];
            }
        }
        return null;
    } else {
        return null;
    }
}


function deleteData(){
	MessageBox.confirm("温馨提示","您确定要删除所有行吗？",function(re){
		if(re=="ok"){
			$("#DataList").empty();			
		}
	});
}

//单行数据删除
function deleteOneRowData()
{
	MessageBox.confirm("温馨提示","您确定要删除当前行数据吗？",function(re){
		if(re=="ok"){
			var rowIndex = LINE_INFOS.selected;
			LINE_INFOS.deleteRow(rowIndex, true);
		}
	});
}

function showAddPopup(){
    $("#BUTTONSTATE").val("add");
	showPopup('tradeTablepopup','tradeTablepopup_item',true);
}

function reset(){
	window.location.reload();
}

function synchronFiled(a,b){
	$("#"+a).val($("#"+b).val());
}


function changePayType(obj) {
    if(obj == '0'){
        $("[name='POST_PAYMENT_CYCLE']").each(function() {
            $(this).attr("checked", false);
            $(this).attr("disabled", true);
        });
        $("[name='PRE_PAYMENT_CYCLE']").each(function() {
            $(this).attr("disabled", false);
        });
        if($("#FEX_DAY")){
            $("#FEX_DAY").attr("disabled",true);
            $("#FEX_DAY").val("");
        }
    }else{
        $("[name='PRE_PAYMENT_CYCLE']").each(function() {
            $(this).attr("checked", false);
            $(this).attr("disabled", true);
        });
        $("[name='POST_PAYMENT_CYCLE']").each(function() {
            $(this).attr("disabled", false);
        });
        if($("#FEX_DAY")){
            $("#FEX_DAY").attr("disabled",false);
        }
    }
}

function checkRequiredFeild() {
	var payType = -1;
    var paymentCycle = -1;
    $("[name='PAYTYPE']").each(function() {
        if($(this).attr("checked")){
        	payType =$(this).val();
		}
    });
	if(payType==-1){
        MessageBox.alert("温馨提示","请您选择付费方式！");
        return false;
	}

    if(payType == '0'){
        $("[name='PRE_PAYMENT_CYCLE']").each(function() {
            if($(this).attr("checked")){
                paymentCycle =$(this).val();
            }
        });
        $("#FEX_DAY").attr("nullable","yes");
        if(paymentCycle==-1){
            MessageBox.alert("温馨提示","请您选择缴费周期！");
            return false;
        }
    }else if(payType == '1'){
        var PRE_PAYMENT_CYCLE = $("[name='POST_PAYMENT_CYCLE']").each(function() {
            if($(this).attr("checked")){
                paymentCycle =$(this).val();
            }
        });
        $("#FEX_DAY").attr("nullable","no");
        if(paymentCycle==-1){
            MessageBox.alert("温馨提示","请您选择缴费周期！");
            return false;
        }
    }

    return true;
}

function updateCol(a,b,c,d){
	if(a!=b){
		data={};
		data[d]=a;
		for(var i=0 ;i<c; i++){
			LINE_INFOS.updateRow(data,i);
		}
	}
}

/**
 * 协议中付费周期校验是否有选择（月、季、半年、年）
 * @returns
 */
function checkPayCycle() {
	var cnt = 0;
	var paymentCycle=-1;
	$("[name='PRE_PAYMENT_CYCLE']").each(function() {
		if ($(this).attr("checked")) {
			cnt++;
			paymentCycle = $(this).val();
		}
	});

	if (paymentCycle == -1 || cnt>1) {
		MessageBox.alert("温馨提示", "请您选择其中一种缴费周期！");
		return false;
	}
	return true;
}

function back(obj) {
    MessageBox.alert("温馨提示", "此协议将不生效！", function(btn){
        hidePopup(obj);
    });
}



