package cn.com.aratek.demo.featuresrequest;

import java.io.Serializable;
import java.lang.String;
import com.google.gson.annotations.SerializedName;

public class DataForLogin implements Serializable {
  @SerializedName("password")
  private String password;

  @SerializedName("role")
  private String role;

  @SerializedName("name")
  private String name;

  @SerializedName("avatar")
  private String avatar;

  @SerializedName("email")
  private String email;

  public DataForLogin(String name, String password,String role,String avatar,String email) {
    this.name = name;
    this.password = password;
    this.role = role;
    this.avatar = avatar;
    this.email = email;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRole() {
    return this.role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAvatar() {
    return this.avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
