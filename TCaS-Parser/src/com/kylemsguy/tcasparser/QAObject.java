package com.kylemsguy.tcasparser;

public abstract class QAObject {
	private int id;
	private String content;
	
	public QAObject(int id, String content) {
		this.id = id;
		this.content = content;
	}

	public int getId() {
		return id;
	}

	public String getContent() {
		return content;
	}
}
