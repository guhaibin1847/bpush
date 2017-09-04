package com.github.guhaibin.utils.mail;

import java.util.Arrays;
import java.util.List;

public class Email {
	
	private List<String> to;
	private List<String> cc;
	private String subject;
	private String content;
	
	
	private Email(){}
	
	public static Email me(){
		Email e = new Email();
		return e;
	}
	
	public Email setTo(String ... tos){
		this.to = Arrays.asList(tos);
		return this;
	}

	public List<String> getCc() {
		return cc;
	}

	public Email setCc(String ... ccs) {
		this.cc = Arrays.asList(ccs);
		return this;
	}

	public String getSubject() {
		return subject;
	}

	public Email setSubject(String subject) {
		this.subject = subject;
		return this;
	}

	public String getContent() {
		return content;
	}

	public Email setContent(String content) {
		this.content = content;
		return this;
	}

	public List<String> getTo() {
		return to;
	}
	
}
