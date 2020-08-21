function initPageParam_110000006200() {
    var methodName = $("#cond_OPER_TYPE").val();
    if(methodName=="ChgUs")
    {
        $("#pam_PASSWORD").closest('li').css("display","none");
        var grpSerialNumber = $("#cond_SERIAL_NUMBER").val();
        $("#VPMN_SERIAL_NUMBER").val(grpSerialNumber);
    }

}

function validateParamPage(methodName) {
    if (methodName=='CrtUs'||methodName=='ChgUs')
    {
        if(methodName=='CrtUs'){
            if(!checkPwd()){
                return false;
            }
        }
        var hasPriv = $('#HAS_FEE_PRIV').val();
        if(hasPriv == 'true'){
            var pay_money = $('#PAY_MONEY').val();
            return creatMoney(pay_money);
        }
    }
    return true;
}
function checkPwd()
{
    var pwdlen = $("#pam_PASSWORD").val().length;
    if(pwdlen<6){
        var message="初始密码长度不能低于6位！";
        $.validate.alerter.one($("#pam_PASSWORD")[0], message);
        return false;
    }
    var pwd = $("#pam_PASSWORD").val();
    //判断是否有数字
    var result = pwd.match(/^.*[0-9]+.*$/);
    if(result==null){
        var message="密码必须包含数字";
        $.validate.alerter.one($("#pam_PASSWORD")[0], message);
        return false;
    }

    //判断是否有字母
    result = pwd.match(/^.*[A-Z]+.*$/);
    if(result==null){
        var message="密码必须包含大写字母";
        $.validate.alerter.one($("#pam_PASSWORD")[0], message);
        return false;
    }

    result = pwd.match(/^.*[a-z]+.*$/);
    if(result==null){
        var message="密码必须包含小写字母";
        $.validate.alerter.one($("#pam_PASSWORD")[0], message);
        return false;
    }

    //判断是否有除数字,字母,下划线外的其他字符
    if(!/^[a-zA-Z0-9]+$/.test(pwd)){
        var message="不允许输入除数字,大小写字母外的其他字符";
        $.validate.alerter.one($("#pam_PASSWORD")[0], message);
        return false;
    }

    return true;
}

function creatMoney(pay_money){
    if(pay_money =="0" || pay_money =="100"){
        if($.feeMgr)
        {
            $.feeMgr.clearFee();
        }
        var tradeTypeCode= "2950";
        if(pay_money =="100"){
            //更新付费列表   开始
            var obj =$.DataMap();

            obj.put("TRADE_TYPE_CODE",tradeTypeCode);
            obj.put("MODE", "0");
            obj.put("CODE", "620");
            obj.put("FEE", pay_money*100);

            $.feeMgr.insertFee(obj);
            //更新付费列表  结束
        }else if(pay_money =="0"){
            var obj =$.DataMap();
            obj.put("TRADE_TYPE_CODE", tradeTypeCode);
            obj.put("MODE", "0");   //费用类型：押金、预存、赠送
            obj.put("CODE", "620"); //费用编码
            obj.put("FEE", pay_money*100);
            $.feeMgr.updateCacheFeeList(obj,'DEL');
        }
        var grpSn = "";
        if($("#cond_OPER_TYPE").val()=='CrtUs'){
            grpSn =$("#cond_SERIAL_NUMBER").val();
        }else if($("#cond_OPER_TYPE").val()=='ChgUs'){
            grpSn = $("#VPMN_SERIAL_NUMBER").val();
        }
        // 设置pos参数
        $.feeMgr.setPosParam(tradeTypeCode,grpSn,"898",null);

        return true;
    }else{
        $.validate.alerter.one($("#PAY_MONEY")[0], "彩铃制作费只能为0元或者100元！");
        return false;
    }
}

function checkPayMoney(obj){
    var e=Wade.event.fix(event);
    if(e.which==13){
        creatMoney($(obj).val());
        e.stop();
        return false;
    }
}
/*function checkpaysn()
{
	var PAY_SN = $("#pam_PAY_SN").val();
	if(PAY_SN==''){
	   	return;
	}
    var CheckAfterPAYSN=$("#CheckAfterPAYSN").val();
    if(CheckAfterPAYSN!=PAY_SN){
	    if(PAY_SN.length!=11){
	       请输入11位手机号码！
	       alert("\u8bf7\u8f93\u516511\u4f4d\u624b\u673a\u53f7\u7801\uff01");
	       return;
	    }
	    $.beginPageLoading();
	    Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.group.param.ProxyParam','productParamInvoker','&SERIAL_NUMBER='+PAY_SN+'&METHOD_NAME=check&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.colorring.UserParamInfo',afterCheck,errafterCheck);

	}else{
	    主付费号码没有修改，不需要验证！
	    alert("\u4e3b\u4ed8\u8d39\u53f7\u7801\u6ca1\u6709\u4fee\u6539\uff0c\u4e0d\u9700\u8981\u9a8c\u8bc1\uff01");
	}
} */

/* 验证主付费号码成功后 */
/*function afterCheck(data){
  $.endPageLoading();
  var result = data.get("RESULT");
  if(result==true||result=='true'){
 	 $.showSucMessage("效验成功");
  	 $("#CheckAfterPAYSN").val($("#pam_PAY_SN").val());
  }else{
     var message=data.get("ERROR_MESSAGE");
     $.showWarnMessage(message);
  }
}*/
/* 验证主付费号码失败后 */
/*function errafterCheck(e,i)
{
	$.endPageLoading();
    $.showErrorMessage(e+":"+i);
}*/



//提交
function checkSub(obj)
{
    var offerTpye = $("#cond_OPER_TYPE").val();
    if(!validateParamPage(offerTpye))
        return false;
	if($("#pam_MANAGER_NAME").val()!=''&&$("#pam_MANAGER_PHONE").val()!=''&&$("#pam_MANAGER_INFO").val()!=''&&$("#pam_PASSWORD").val()!=''&&$("#PAY_MONEY").val()!=''){
        $("#PAY_MONEY").val("");
        $("#PAY_MONEY").attr('nullable','yes');
        $("#HAS_FEE_PRIV").val("");
        $("#VPMN_SERIAL_NUMBER").val("");
	}

    if(!submitOfferCha())
        return false;
    backPopup(obj);
}
