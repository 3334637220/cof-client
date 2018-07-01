package e.orz.cof.model;

public class Comment {
	private int blogId;
	private String userName;
	private String text;

	public Comment() {

	}

	public Comment(int blogId, String userName, String text) {
		this.blogId = blogId;
		this.userName = userName;
		this.text = text;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getBlogId() {
		return blogId;
	}

	public void setBlogId(int blogId) {
		this.blogId = blogId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
