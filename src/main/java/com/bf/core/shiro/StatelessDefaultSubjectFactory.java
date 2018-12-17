package com.bf.core.shiro;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;

public class StatelessDefaultSubjectFactory extends DefaultWebSubjectFactory {
	
	@Override
	public Subject createSubject(SubjectContext context) {
		//不创建session  
		context.setSessionCreationEnabled(true);
		Subject ct = super.createSubject(context);
		ct.getSession(false);
		return ct;
	}
}
