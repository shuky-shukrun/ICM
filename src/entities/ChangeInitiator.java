package entities;

import java.io.Serializable;
import java.util.Objects;

public class ChangeInitiator implements Serializable {

	private Integer id;
	private String firstName;
	private String lastName;
	private Title title;
	private String email;
	private String phoneNumber;
	private CiDepartment department;
	private String password;
	private Position position;

	public enum Title {
		STUDENT,
		LECTURER,
		ADMINISTRATION,
		INFOENGINEER
	}


	/**
	 * Gets first name
	 * @return first name
	 */
	public String getFirstName() {
		return this.firstName;
	}
	/**
	 * Gets last name
	 * @return last name
	 */
	public String getLastName() {
		return this.lastName;
	}
	/**
	 * Gets title
	 * @return title
	 */
	public Title getTitle() {
		return this.title;
	}
	/**
	 * Gets email
	 * @return email
	 */
	public String getEmail() {
		return this.email;
	}
	/**
	 * Gets phone number
	 * @return  phone number
	 */
	public String getPhoneNumber() {
		return this.phoneNumber;
	}
	/**
	 * Gets department
	 * @return  department
	 */
	public CiDepartment getDepartment() {
		return this.department;
	}
	/**
	 * Gets password
	 * @return  password
	 */
	public String getPassword() {
		return this.password;
	}
	/**
	 * Gets id
	 * @return  id
	 */
	public Integer getId() {
		return this.id;
	}
	/**
	 * Sets first name
	 * @param firstName
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	/**
	 * Sets last name
	 * @param lastName
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	/**
	 * Sets title
	 * @param title
	 */
	public void setTitle(Title title) {
		this.title = title;
	}
	/**
	 * Sets email
	 * @param email
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * Sets phone number
	 * @param  phone number
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	/**
	 * Sets department
	 * @param  department
	 */
	public void setDepartment(CiDepartment department) {
		this.department = department;
	}
	/**
	 * sets password
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * sets id
	 * @param id
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * gets position
	 * @return position
	 */
	public Position getPosition() {
		return position;
	}
	/**
	 * sets position
	 * @param position
	 */
	public void setPosition(Position position) {
		this.position = position;
	}
	/**
	 * Checks if two object are the same
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ChangeInitiator initiator = (ChangeInitiator) o;
		return Objects.equals(id, initiator.id);
	}
	/**
	 * give to object hash code
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	/**
	 * Returns a string that describe the object
	 */
	@Override
	public String toString() {
		return firstName + " " + lastName + ", ID: " + id;
	}
}

