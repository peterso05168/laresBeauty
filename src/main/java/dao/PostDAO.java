package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import bean.Post;

public class PostDAO {

	JdbcTemplate template;

	public void setTemplate(JdbcTemplate template) {
		this.template = template;
	}

	public List<Post> getPosts(final String postStatus, final String postType) {
		String sqlStr = "SELECT * FROM post WHERE post_status IN ( ?, 'F' )";

		if (!postStatus.equals("F")) {
			sqlStr += " AND post_type = ? ";
		}

		List<Post> postList = template.query(sqlStr, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1, postStatus);
				if (!postStatus.equals("F")) {
					preparedStatement.setString(2, postType);
				}
			}
		}, new RowMapper<Post>() {
			public Post mapRow(ResultSet rs, int row) throws SQLException {
				Post e = setPost(rs);
				return e;
			}
		});
		return postList;
	}
	
	public int addPost(final String postTitle, final String postContent, final String postStatus,
			final String postType, final String postImgName) {
		String sqlStr = "INSERT INTO post (post_title, post_content, post_status, post_type, post_img) VALUES (?, ?, ?, ?, ?) ";

		int successFlag = template.update(sqlStr, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1, postTitle);
				preparedStatement.setString(2, postContent);
				preparedStatement.setString(3, postStatus);
				preparedStatement.setString(4, postType);
				preparedStatement.setString(5, postImgName);
			}
		});

		return successFlag;
	}
	
	public Post setPost(ResultSet rs) throws SQLException {
		Post post = new Post();
		post.setPostId(rs.getInt("post_id"));
		post.setPostTitle(rs.getString("post_title"));
		post.setPostContent(rs.getString("post_content"));
		post.setPostImg(rs.getString("post_img"));
		post.setPostType(rs.getString("post_type"));
		post.setPostStatus(rs.getString("post_status"));
		return post;
	}

}