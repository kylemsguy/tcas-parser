package com.kylemsguy.tcasparser;

public class Answer extends QAObject {
	
	private Question parent;

	public Answer(int id, String content, Question parent) {
		super(id, content);
		this.parent = parent;
	}

}
