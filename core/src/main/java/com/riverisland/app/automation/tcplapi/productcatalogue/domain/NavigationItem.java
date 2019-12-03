package com.riverisland.app.automation.tcplapi.productcatalogue.domain;

import java.util.List;

public class NavigationItem {
	
	private int id;
	private int parentId;
	private int productListId;

	private String displayName;
	List<NavigationItem> children;
	
	public List<NavigationItem> getChildren() {
		return children;
	}
	public void setChildren(List<NavigationItem> children) {
		this.children = children;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public int getProductListId() {
		return productListId;
	}
	public void setProductListId(int productListId) {
		this.productListId = productListId;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public boolean hasChildren() {
		return null != this.children && !this.children.isEmpty();
	}
	@Override
	public String toString() {
		return "[id=" + id + ", "
				+ "parentId=" + parentId + ", "
				+ "productListId=" + productListId + ", "
				+ "displayName=" + displayName + ", "
				+ "children=" + children + "]\n";
	}
}
