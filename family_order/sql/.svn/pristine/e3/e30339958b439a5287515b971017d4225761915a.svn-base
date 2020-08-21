--IS_CACHE=Y
SELECT a.eparchy_code,a.flag,a.note_item_code,a.note_item,a.default_tag,a.prevaluec1,a.prevaluec2,b.INTEGRATE_ITEM_CODE rsrv_num1,b.rsrv_num1  rsrv_num2
  FROM td_a_noteitem a,td_a_noteitem_det b 
 WHERE a.eparchy_code = b.eparchy_code(+) and a.flag = b.flag(+) and a.note_item_code = b.note_item_code(+) and a.FLAG=:FLAG
  order by a.eparchy_code,a.note_item_code,b.integrate_item_code