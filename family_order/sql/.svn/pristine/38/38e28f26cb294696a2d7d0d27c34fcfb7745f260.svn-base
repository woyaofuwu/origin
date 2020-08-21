

SELECT t.tid,
       t.channel_id,
       p.ctrm_product_type,
       decode(p.ctrm_product_type,1,'终端产品',2,'合约产品',3,'套餐及增值产品') type_name,
       p.contract_id,
       p.PRODUCT_ID,
       p.PACKAGE_ID,
	   p.ctrm_product_id,
       decode(t.DISTRIBUTION,1,'需要',2,'不需要') DISTRIBUTION,
       o.order_status,
       to_char(t.create_time,'yyyy-mm-dd hh24:mi:ss') create_time,
       t.buyer_nick,
       t.receiver_address,
       t.receiver_mobile,
       t.receiver_name,
       o.oid,
       o.title,
       o.PHONE,
       to_char(o.accept_date,'yyyy-mm-dd hh24:mi:ss') accept_date,
       p.pid,
       p.status,
       p.RSRV_STR1,	
	   p.RSRV_STR2,	
       p.RSRV_STR3,	
       p.RSRV_STR4,	
       p.RSRV_STR5
  FROM TF_B_CTRM_TLIST t, TF_B_CTRM_ORDER o, TF_B_CTRM_ORDER_PRODUCT p
 WHERE t.tid = o.tid
   and o.tid = p.tid
   and o.oid = p.oid
   and t.distribution='1'
   and not exists(SELECT 1 FROM  TF_B_CTRM_TLIST_SHIPPING s where s.tid = o.tid and s.oid = o.oid AND s.PID=p.pid)
   and t.tid= :TID