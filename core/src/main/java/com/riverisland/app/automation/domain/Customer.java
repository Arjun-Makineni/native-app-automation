package com.riverisland.app.automation.domain;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 * Created by Prashant Ramcharan on 21/03/2017
 */
public class Customer {
	private String title;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String password;
    private String gender;
    private CustomerDateOfBirth dob;

    private static final String EMAIL_PATTERN = "riautotest+%s@gmail.com";

    public Customer(String title,
    		        String firstName,
                    String lastName,
                    String emailAddress,
                    String password,
                    String gender,
                    CustomerDateOfBirth dob) {
    	this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.password = password;
        this.gender = gender;
        this.dob = dob;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public CustomerDateOfBirth getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }
    
    public String getTitle() {
    	return title;
    }

    @Override
    public String toString() {
        return "{" +
        		"title='" + title + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public static class Builder {
    	private String title;
        private String firstName;
        private String lastName;
        private String emailAddress;
        private String password;
        private String gender;
        private CustomerDateOfBirth dob;

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public static Builder createDefaultBuilder() {
            return create()
            		.withTitle("Mr.")
                    .withFirstName("Test")
                    .withLastName("Tester")
                    .withRandomEmailAddress()
                    .withPassword("password")
                    .withDOB(new CustomerDateOfBirth("01", "Jan", "2000"))
                    .withGender("Male");
        }

        public Builder withTitle(String title) {
        	this.title = title;
        	return this;
        }
        public Builder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder withEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
            return this;
        }

        public Builder withRandomEmailAddress() {
            String emailWildcard = UUID.randomUUID().toString().substring(0, 33).replace("-", "");
            this.emailAddress = StringUtils.lowerCase(String.format(EMAIL_PATTERN, emailWildcard));
            return this;
        }

        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        public Customer build() {
            return new Customer(title, firstName, lastName, emailAddress, password, gender, dob);
        }

        public Customer buildNewCustomer() {
            return createDefaultBuilder().build();
        }

        public Builder withGender(String gender) {
            this.gender = gender;
            return this;
        }

        public Builder withDOB(CustomerDateOfBirth dob) {
            this.dob = dob;
            return this;
        }
    }
}