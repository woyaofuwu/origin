--IS_CACHE=Y
select r.* from TD_M_MENU_RELATION r where 1=1
 and r.CURRENT_MENU_ID in (select m.MENU_ID from TD_S_CHANNEL_MENU m where m.CHAN_ID=:CHAN_ID)
 and r.CURRENT_MENU_ID=:MENU_ID
 and r.RELATION_TYPE=:RELATION_TYPE