select b.* from tf_b_ewe_asyn a ,tf_b_ewe_asyn_attr b where a.busiform_id=:BUSIFORM_ID
and a.node_id=:NODE_ID
and a.asyn_id=b.asyn_id
Union all
select b.* from tf_bh_ewe_asyn a ,tf_bh_ewe_asyn_attr b where a.busiform_id=:BUSIFORM_ID
and a.node_id=:NODE_ID
and a.asyn_id=b.asyn_id