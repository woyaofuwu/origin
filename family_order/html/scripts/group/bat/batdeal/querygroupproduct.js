function getMenberGroupInfo(obj){
	parent.getElement("USER_ID").value = obj.getAttribute('userid');
	parent.getElement("PRODUCT_ID").value = obj.getAttribute('prodid');
	var parentButton = getElementValue('parentButton');
	if (parentButton == null || parentButton == "")
	    parent.getElement("button").click();
	else
	    parent.getElement(parentButton).click();
	cancel(true);
}