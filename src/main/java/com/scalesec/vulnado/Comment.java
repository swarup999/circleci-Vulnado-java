package com.scalesec.vulnado;

import org.apache.catalina.Server;
import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

public class Comment {
  public String id, username, body;
  public Timestamp created_on;

  public Comment(String id, String username, String body, Timestamp created_on) {
    this.id = id;
    this.username = username;
    this.body = body;
    this.created_on = created_on;
  }
  //Linter unusedVariable inside ! Identified by SonarQube
  public void unusedVariableMethod() {
    int unused = 10;
    System.out.println("One useless comment!");
  }
  //Linter - Incorrect method name ! Identified by SonarQube
  public void FalseNameMethod() {
    System.out.println("There are many comments in the DB");
  }

  ////Linter - Same body methods ! Identified by SonarQube
  public void testMethod() {
    System.out.println("There are many comments in the DB");
  }

  ////Linter - Empty classes ! Identified by SonarQube
    class CommentImages
    {

    }
  ////Linter - Long line ! Identified by SonarQube
  public void longLineMethod() {
    System.out.println("The first patented printing mechanism for applying a marking medium to a recording medium or more particularly an electrostatic inking apparatus and a method for electrostatically depositing ink on controlled areas of a receiving medium, was in 1962 by C. R. Winston, Teletype Corporation, using continuous inkjet printing. The ink was a red stamp-pad ink manufactured by Phillips Process Company of Rochester, NY under the name Clear Print. This patent (US3060429) led to the Teletype Inktronic Printer product delivered to customers in late 1966.");
  }

  //SQL INJECTION HERE ! Identified by SonarQube
  public List<Comment> findUserComments(String username) {
    Statement statement = null;
    ResultSet rs = null;
    List<Comment> comments = new ArrayList<>();
    Connection con = null;

    try {
      con = Postgres.connection();
      statement = con.createStatement();

      // SQL INJECTION
      String query = "SELECT * FROM comments WHERE username = '" + username + "';";
      rs = statement.executeQuery(query);

      while (rs.next()) {
        String id = rs.getString("id");
        String body = rs.getString("body");
        Timestamp created_on = rs.getTimestamp("created_on");
        Comment c = new Comment(id, username, body, created_on);
        comments.add(c);
      }
    } catch (SQLException e) { //Not enough catch-Exception. Does not catch all the cases.
      e.printStackTrace();
    }
    return comments;
  }
  public void process(int value) {
    if (value > 0) {
      if (value < 10) {
        System.out.println("Small value");
      } else {
        System.out.println("Medium value");
      }
    } else {
      if (value == 0) {
        System.out.println("Zero");
      } else {
        System.out.println("Negative value");
      }
    }
  }



  //Try - Catch Should be here ! Identified by SonarQube
  private Boolean commit() throws SQLException {
    String sql = "INSERT INTO comments (id, username, body, created_on) VALUES (?,?,?,?)";
    Connection con = Postgres.connection();
    PreparedStatement pStatement = con.prepareStatement(sql);
    pStatement.setString(1, this.id);
    pStatement.setString(2, this.username);
    pStatement.setString(3, this.body);
    pStatement.setTimestamp(4, this.created_on);
    return 1 == pStatement.executeUpdate();
  }


  //finally must not have return ! Identified by SonarQube
  public static Boolean delete(String id) {
    try {
      String sql = "DELETE FROM comments where id = ?";
      Connection con = Postgres.connection();
      PreparedStatement pStatement = con.prepareStatement(sql);
      pStatement.setString(1, id);
      return 1 == pStatement.executeUpdate();
    } catch(Exception e) {
      e.printStackTrace();
    } finally {
      return false;
    }
  }

  public static Comment create(String username, String body){
    long time = new Date().getTime();
    Timestamp timestamp = new Timestamp(time);
    Comment comment = new Comment(UUID.randomUUID().toString(), username, body, timestamp);
    try {
      if (comment.commit()) {
        return comment;
      } else {
        throw new BadRequest("Unable to save comment");
      }
    } catch (Exception e) {
      throw new ServerError(e.getMessage());
    }
  }

  public static List<Comment> fetch_all() {
    Statement stmt = null;
    List<Comment> comments = new ArrayList();
    try {
      Connection cxn = Postgres.connection();
      stmt = cxn.createStatement();

      String query = "select * from comments;";
      ResultSet rs = stmt.executeQuery(query);
      while (rs.next()) {
        String id = rs.getString("id");
        String username = rs.getString("username");
        String body = rs.getString("body");
        Timestamp created_on = rs.getTimestamp("created_on");
        Comment c = new Comment(id, username, body, created_on);
        comments.add(c);
      }
      cxn.close();
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println(e.getClass().getName()+": "+e.getMessage());
    } finally {
      return comments;
    }
  }


}
