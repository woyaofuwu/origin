--IS_CACHE=Y
SELECT i.item_code,p.integrate_item,
	decode(i.item_type_tag,'0','明细账目','1','综合账目','其他') ITEM_TYPE_TAG,
	i.priority
  FROM td_a_integrateitem p,td_a_itemtopayitem i
 WHERE i.payitem_code=:PAYITEM_CODE
	AND i.eparchy_code=:EPARCHY_CODE
	AND i.item_code=p.integrate_item_code
 	AND p.eparchy_code=i.eparchy_code