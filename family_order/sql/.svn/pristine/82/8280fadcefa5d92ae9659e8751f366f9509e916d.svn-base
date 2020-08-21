--IS_CACHE=Y
select a.note_item_code note_item_code,nvl(a.note_item,'') note_item, 
 b.integrate_item_code rsrv_num1,nvl(a.default_tag,'0') default_tag,
 a.eparchy_code eparchy_code,a.prevaluec1 prevaluec1,a.prevaluec2 prevaluec2,
 a.rsrv_num2 rsrv_num2,a.flag flag
 from td_a_noteitem a,td_a_noteitem_det b 
 where a.note_item_code = b.note_item_code(+)
 and a.flag = b.flag(+) and a.flag=:FLAG and a.eparchy_code=b.eparchy_code(+)
 group by a.eparchy_code,a.note_item_code,b.integrate_item_code,a.note_item,
 a.default_tag,a.prevaluec1,a.prevaluec2,a.rsrv_num2,a.flag