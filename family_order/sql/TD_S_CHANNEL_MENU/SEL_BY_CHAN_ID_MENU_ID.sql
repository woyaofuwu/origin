--IS_CACHE=Y
select m.*
	from TD_S_CHANNEL_MENU m where 1 = 1
	and m.CHAN_ID = :CHAN_ID
	and m.MENU_ID = :MENU_ID