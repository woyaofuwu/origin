--IS_CACHE=Y
select a.subsys_code,a.param_attr,a.param_code,b.note_item ,b.note_item_code
from td_s_commpara a, TD_B_NOTEITEM b
where 
a.para_code1 = to_char(b.note_item_code)
and subsys_code = 'CGM'
and param_attr='1' 
and b.parent_item_code='-1'
and param_code =:PRODUCT_ID
and b.templet_code='50000001' and b.print_level=0