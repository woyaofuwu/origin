--IS_CACHE=Y
select m.MENU_ID, m.MENU_NAME,m.MENU_EPARCHY, decode(a.MENU_ID,null,0,1) CHECKED_FLAG,decode(m.MENU_LEVEL,0,1,1,1,0) DISABLED_FLAG, r.RELATION_MENU_ID PARENT_MENU_ID, m.MENU_CODE || '|' || m.MENU_NAME MENU_TEXT, NODE_COUNT, EXPAND_NODE from TD_S_CHANNEL_MENU m
 , (select menu.CURRENT_MENU_ID, menu.RELATION_TYPE, menu.RELATION_MENU_ID, decode(relations.NODE_COUNT, null, 0, relations.NODE_COUNT) NODE_COUNT, decode(relations.NODE_COUNT, null, 0, 1) EXPAND_NODE from TD_M_MENU_RELATION menu
 , (select RELATION_MENU_ID, count(CURRENT_MENU_ID) node_count from TD_M_MENU_RELATION where RELATION_TYPE = 0 group by RELATION_MENU_ID) relations
  where menu.CURRENT_MENU_ID = relations.RELATION_MENU_ID(+)
  and menu.RELATION_MENU_ID = :MENU_ID
  order by EXPAND_NODE desc, menu.CURRENT_MENU_ID
 ) r, (select MENU_ID from TD_M_MENU_STAFF where STAFF_ROLE = :STAFF_ROLE) a
  where m.MENU_ID = r.CURRENT_MENU_ID
  and r.RELATION_TYPE = '0'
  and m.MENU_ID = a.MENU_ID(+)
  order by EXPAND_NODE desc, m.MENU_CODE