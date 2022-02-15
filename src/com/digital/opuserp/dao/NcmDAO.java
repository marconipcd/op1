package com.digital.opuserp.dao;

import com.digital.opuserp.domain.Ncm;
import com.digital.opuserp.util.ConnUtil;

public class NcmDAO {

	public static Ncm getNcm(Integer id){
		return ConnUtil.getEntity().find(Ncm.class, id);
	}
}
