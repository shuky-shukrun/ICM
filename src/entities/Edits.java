package entities;

import java.time.LocalDate;

public class Edits {

	private String editorName;
	private LocalDate editDate;
	private String description;
	/**
	 * gets the editor name
	 * @return editor name
	 */
	public String getEditorName() {
		return this.editorName;
	}

	/**
	 * Sets the editor name
	 * @param editorName
	 */
	public void setEditorName(String editorName) {
		this.editorName = editorName;
	}
	/**
	 * gets the edit date
	 * @return edit date
	 */
	public LocalDate getEditDate() {
		return this.editDate;
	}

	/**
	 * 
	 * @param editDate
	 */
	public void setEditDate(LocalDate editDate) {
		this.editDate = editDate;
	}
	/**
	 * Gets the description
	 * @return description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

}