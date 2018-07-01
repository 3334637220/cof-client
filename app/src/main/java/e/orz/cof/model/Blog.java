package e.orz.cof.model;

public class Blog {
	private int blogId;
	private String userName;
	private String text;

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
