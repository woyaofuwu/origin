// 提交组件提交前校验
function onTradeSubmit() {
	MessageBox.confirm("提示信息","挂失业务为SIM卡丢失客户办理，办理报开需前往本省实体营业网点办理,确认挂失?", function (data) {
		if(data == "ok"){
            $.cssubmit.submitTrade();
		}
    });
	return false;
}
