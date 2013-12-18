package fr.twiced.ucoinj.service.impl;

import fr.twiced.ucoinj.bean.Jsonable;

public class UCoinServiceImpl {

	protected Object jsonIt(Jsonable jsonable) {
		if (jsonable != null) {
			return jsonable.getJSON();
		}
		return null;
	}
}
