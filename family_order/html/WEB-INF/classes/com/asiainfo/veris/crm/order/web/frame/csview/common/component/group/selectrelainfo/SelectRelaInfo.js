function afterCheckGroupInfo() {
	var shortNum = this.ajaxDataset.get(0, "GROUPINFO_NUM");
	if(shortNum >'0') {
		popupDialog('group.destroygroupunifiedbill.QueryDestroyGroup', 'queryGroupInfo', '&cond_SERIAL_NUMBER='+getElement("cond_SERIAL_NUMBER").value, '集团查询', '600', '640');
	}
}

function getRelaGroupInfo(){
		ajaxDirect4CS(this, 'getGroupBaseInfo', '&cond_GROUP_ID='+getElement("cond_GROUP_ID").value, 'CustInfoPart,GroupProductPart',true,'');
}