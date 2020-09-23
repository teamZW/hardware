package com.hardware.sample.data;

public class CustomBean {
	/** 名称 */
	private String name;
	/** 说明 */
	private String info;
	/** url */
	private String activityUri;

	public CustomBean(String name, String info, String activityUri) {
		super();
		this.name = name;
		this.info = info;
		this.activityUri = activityUri;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getActivityUri() {
		return activityUri;
	}

	public void setActivityUri(String activityUri) {
		this.activityUri = activityUri;
	}

	@Override
	public String toString() {
		return "CustomBean [name=" + name + ", info=" + info + ", activityUri=" + activityUri + "]";
	}
}
