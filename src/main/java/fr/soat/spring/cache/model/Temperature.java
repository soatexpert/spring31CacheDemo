package fr.soat.spring.cache.model;

public class Temperature {

	private Integer value;
	private long validAt;

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public long getValidAt() {
		return validAt;
	}

	public void setValidAt(long validAt) {
		this.validAt = validAt;
	}
}
