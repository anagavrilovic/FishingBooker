package com.backend.model;

import javax.persistence.*;
import java.util.*;

@Entity
public class RegistrationRequest {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name="reg_request_id", unique=true, nullable=false)
   private Integer id;

   @Column(unique=false, nullable=false)
   private String firstName;

   @Column(unique=false, nullable=false)
   private String lastName;

   @Column(unique=false, nullable=false)
   private String phoneNumber;

   @Column(unique=false, nullable=false)
   private String email;

   @Column(unique=false, nullable=false)
   private String password;

   @Column(unique=false, nullable=false)
   private Role role;

   @Column(unique=false, nullable=false)
   private String explanation;

   @OneToOne(fetch = FetchType.EAGER)
   @JoinColumn(name = "address_id")
   private Address address;

   public RegistrationRequest() { }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public String getFirstName() {
      return firstName;
   }

   public void setFirstName(String firstName) {
      this.firstName = firstName;
   }

   public String getLastName() {
      return lastName;
   }

   public void setLastName(String lastName) {
      this.lastName = lastName;
   }

   public String getPhoneNumber() {
      return phoneNumber;
   }

   public void setPhoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public Role getRole() {
      return role;
   }

   public void setRole(Role role) {
      this.role = role;
   }

   public String getExplanation() {
      return explanation;
   }

   public void setExplanation(String explanation) {
      this.explanation = explanation;
   }

   public Address getAddress() {
      return address;
   }

   public void setAddress(Address address) {
      this.address = address;
   }

   @Override
   public String toString() {
      return "RegistrationRequest{}";
   }
}