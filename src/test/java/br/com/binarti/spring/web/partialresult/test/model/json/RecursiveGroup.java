package br.com.binarti.spring.web.partialresult.test.model.json;

import java.util.ArrayList;
import java.util.List;

public class RecursiveGroup extends Group {

	private List<Group> subgroups;
	
	public RecursiveGroup(Long id, String name) {
		super(id, name);
		this.subgroups = new ArrayList<Group>();
	}

	public RecursiveGroup() {
		this.subgroups = new ArrayList<Group>();
	}
	
	public List<Group> getSubgroups() {
		return subgroups;
	}
	
	public void setSubgroups(List<Group> subgroups) {
		this.subgroups = subgroups;
	}
	
}
