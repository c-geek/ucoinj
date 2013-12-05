package fr.twiced.ucoinj.bean;

public abstract class UCoinEntity<E extends NaturalId> implements Jsonable {
	
	public abstract E getNaturalId();
}
