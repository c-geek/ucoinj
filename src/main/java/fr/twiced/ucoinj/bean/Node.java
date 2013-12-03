package fr.twiced.ucoinj.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = {
		@UniqueConstraint(columnNames = {"merkleId" , "line" , "position"})
})
public class Node implements Hashable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(unique = true, nullable = false)
	private Integer id;
	
	@Column(nullable = false)
	private Integer line;
	
	@Column(nullable = false)
	private Integer position;
	
	@Column(length = 40, nullable = false)
	private String hash;
	
	@OneToOne
	@JoinColumn(name = "merkleId", nullable = false)
	private Merkle<?> merkle;
	
	@Transient
	@Override
	public String getHash() {
		return hash;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getLine() {
		return line;
	}

	public void setLine(Integer line) {
		this.line = line;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public Merkle<?> getMerkle() {
		return merkle;
	}

	public void setMerkle(Merkle<?> merkle) {
		this.merkle = merkle;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

}
