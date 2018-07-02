package e.orz.cof.model;

import java.util.ArrayList;

public class Blog {
	private int blogId;
	private String userName;
	private String text;
	private String faceUrl;
	private int upNum;
	private String time;
	private Boolean isLike;
	private Boolean isMine;
	private ArrayList<Comment> comments;
	private ArrayList<String> pictures;

	public Boolean getMine() {
		return isMine;
	}

	public void setMine(Boolean mine) {
		isMine = mine;
	}

	public Boolean getLike() {
		return isLike;
	}

	public void setLike(Boolean like) {
		isLike = like;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getUpNum() {
		return upNum;
	}

	public void setUpNum(int upNum) {
		this.upNum = upNum;
	}

	public String getFaceUrl() {
		return faceUrl;
	}

	public void setFaceUrl(String faceUrl) {
		this.faceUrl = faceUrl;
	}

	public ArrayList<Comment> getComments() {
		return comments;
	}

	public void setComments(ArrayList<Comment> comments) {
		this.comments = comments;
	}

	public ArrayList<String> getPictures() {
		return pictures;
	}

	public void setPictures(ArrayList<String> pictures) {
		this.pictures = pictures;
	}

	public Blog() {
	}

	public Blog(int blogId, String userName, String text) {
		this.blogId = blogId;
		this.userName = userName;
		this.text = text;
	}

	public Blog(String userName, String text) {
		this.userName = userName;
		this.text = text;
	}

	public int getBlogId() {
		return blogId;
	}

	public void setBlogId(int blogId) {
		this.blogId = blogId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
