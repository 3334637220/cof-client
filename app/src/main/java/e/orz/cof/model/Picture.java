package e.orz.cof.model;

public class Picture {
	private int blogId;
	private String imageUrl;

	public Picture() {

	}

	public Picture(int blogId, String imageUrl) {
		this.blogId = blogId;
		this.imageUrl = imageUrl;
	}

	public int getBlogId() {
		return blogId;
	}

	public void setBlogId(int blogId) {
		this.blogId = blogId;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

}
