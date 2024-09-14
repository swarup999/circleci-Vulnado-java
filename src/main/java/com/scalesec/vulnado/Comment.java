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

  // Δημιουργία comment με πιθανές ευπάθειες
  public static Comment create(String username, String body){
    if (username == null || body == null || username.isEmpty() || body.isEmpty()) {
      throw new IllegalArgumentException("Username and body must not be null or empty"); // Βασική επικύρωση
    }

    long time = new Date().getTime();
    Timestamp timestamp = new Timestamp(time);
    Comment comment = new Comment(UUID.randomUUID().toString(), username, body, timestamp);
    try {
      if (comment.commit()) {
        return comment;
      } else {
        throw new BadRequest("Unable to save comment");
      }
    } catch (SQLException e) {  // Εξαίρεση για SQL exceptions
      e.printStackTrace();  // Κακή πρακτική - εκτύπωση του stacktrace
      throw new ServerError("Database error occurred: " + e.getMessage());  // Κακή πρακτική, διαρροή πληροφοριών προς τον χρήστη
    }
  }

  // Fetch all comments με κακή διαχείριση resources και χρήση raw SQL query
  public static List<Comment> fetch_all() {
    Statement stmt = null;
    ResultSet rs = null;
    List<Comment> comments = new ArrayList<>();
    Connection cxn = null;
    try {
      cxn = Postgres.connection();
      stmt = cxn.createStatement();

      String query = "SELECT * FROM comments;";
      rs = stmt.executeQuery(query);  // Ενδεχόμενο SQL Injection

      while (rs.next()) {
        String id = rs.getString("id");
        String username = rs.getString("username");
        String body = rs.getString("body");
        Timestamp created_on = rs.getTimestamp("created_on");
        Comment c = new Comment(id, username, body, created_on);
        comments.add(c);
      }
    } catch (SQLException e) {
      e.printStackTrace();  // Κακή πρακτική - διαρροή ευαίσθητων πληροφοριών
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    } finally {
      try {
        if (rs != null) rs.close();  // Κακή διαχείριση πόρων, πρέπει να κλείνουμε το ResultSet
        if (stmt != null) stmt.close();  // Κακή διαχείριση πόρων
        if (cxn != null) cxn.close();  // Πρέπει πάντα να κλείνουμε την σύνδεση
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return comments;
  }

  // Διαγραφή comment με ενδεχόμενο SQL injection λόγω μη σωστής διαχείρισης PreparedStatement
  public static Boolean delete(String id) {
    Connection con = null;
    PreparedStatement pStatement = null;
    try {
      String sql = "DELETE FROM comments WHERE id = ?";
      con = Postgres.connection();
      pStatement = con.prepareStatement(sql);
      pStatement.setString(1, id);
      int affectedRows = pStatement.executeUpdate();  // Έλεγχος πόσες γραμμές επηρεάστηκαν
      return affectedRows == 1;
    } catch(SQLException e) {
      e.printStackTrace();  // Διαρροή πληροφοριών
    } finally {
      try {
        if (pStatement != null) pStatement.close();
        if (con != null) con.close();  // Πρέπει πάντα να κλείνουμε την σύνδεση
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return false;  // Ακόμη και σε περίπτωση σφάλματος επιστρέφει false
  }

  // Commit με μη ασφαλή χρήση SQL queries
  private Boolean commit() throws SQLException {
    String sql = "INSERT INTO comments (id, username, body, created_on) VALUES (?,?,?,?)";
    Connection con = null;
    PreparedStatement pStatement = null;
    try {
      con = Postgres.connection();
      pStatement = con.prepareStatement(sql);
      pStatement.setString(1, this.id);
      pStatement.setString(2, this.username);
      pStatement.setString(3, this.body);
      pStatement.setTimestamp(4, this.created_on);
      return pStatement.executeUpdate() == 1;
    } finally {
      if (pStatement != null) pStatement.close();  // Πρέπει να κλείσουμε το PreparedStatement
      if (con != null) con.close();  // Πρέπει να κλείσουμε την σύνδεση
    }
  }
}
